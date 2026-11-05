#!/usr/bin/env python3
"""Audit classfile annotation ABI for every registered Mixin in maki-* JARs."""

from __future__ import annotations

import argparse
import json
import re
import subprocess
import tempfile
import zipfile
from pathlib import Path

RUNTIME_VISIBLE = {
    "org.spongepowered.asm.mixin.injection.Inject",
    "org.spongepowered.asm.mixin.injection.Redirect",
    "org.spongepowered.asm.mixin.injection.ModifyArg",
    "org.spongepowered.asm.mixin.injection.ModifyArgs",
    "org.spongepowered.asm.mixin.injection.ModifyVariable",
    "org.spongepowered.asm.mixin.injection.ModifyConstant",
    "org.spongepowered.asm.mixin.injection.At",
    "org.spongepowered.asm.mixin.injection.Slice",
    "org.spongepowered.asm.mixin.gen.Accessor",
    "org.spongepowered.asm.mixin.gen.Invoker",
    "com.llamalad7.mixinextras.injector.ModifyExpressionValue",
    "com.llamalad7.mixinextras.injector.ModifyReturnValue",
    "com.llamalad7.mixinextras.injector.WrapWithCondition",
    "com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation",
}

SECTION_NAMES = {"RuntimeVisibleAnnotations:", "RuntimeInvisibleAnnotations:"}


def annotation_sections(javap_text: str) -> dict[str, set[str]]:
    found = {"visible": set(), "invisible": set()}
    current: str | None = None
    for raw in javap_text.splitlines():
        stripped = raw.strip()
        if stripped in SECTION_NAMES:
            current = "visible" if stripped.startswith("RuntimeVisible") else "invisible"
            continue
        if current is None:
            continue
        # Attribute headings end the current annotation section. Annotation payload
        # lines contain '=', '(', ')', commas or fqcn text, not a bare heading.
        if stripped.endswith(":") and re.fullmatch(r"[A-Za-z][A-Za-z0-9_$ ]*:", stripped):
            current = None
            continue
        for annotation in {"org.spongepowered.asm.mixin.Mixin", *RUNTIME_VISIBLE}:
            if annotation in stripped:
                found[current].add(annotation)
    return found


def mixin_classes(config: dict) -> list[str]:
    package = config.get("package", "").strip(".")
    names: list[str] = []
    for key in ("mixins", "client", "server"):
        value = config.get(key, [])
        if not isinstance(value, list):
            raise ValueError(f"{key} must be a list")
        for name in value:
            if not isinstance(name, str):
                raise ValueError(f"{key} entry must be a string")
            names.append(f"{package}.{name}" if package else name)
    return names


def run_javap(class_file: Path) -> str:
    proc = subprocess.run(
        ["javap", "-p", "-v", str(class_file)],
        check=False,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        encoding="utf-8",
    )
    if proc.returncode:
        raise RuntimeError(proc.stdout.strip())
    return proc.stdout


def audit_jar(jar: Path) -> tuple[list[dict], list[str]]:
    rows: list[dict] = []
    failures: list[str] = []
    with zipfile.ZipFile(jar) as zf, tempfile.TemporaryDirectory() as temp_dir:
        configs = sorted(
            name for name in zf.namelist()
            if name.lower().endswith(".json") and "mixin" in Path(name).name.lower()
        )
        if not configs:
            return rows, failures

        registered: dict[str, set[str]] = {}
        for config_name in configs:
            try:
                config = json.loads(zf.read(config_name).decode("utf-8"))
                classes = mixin_classes(config)
            except Exception as exc:
                failures.append(f"{jar.name}:{config_name}: invalid Mixin config: {exc}")
                continue
            for class_name in classes:
                registered.setdefault(class_name, set()).add(config_name)

        for class_name, owners in sorted(registered.items()):
            entry = class_name.replace(".", "/") + ".class"
            if entry not in zf.namelist():
                failures.append(
                    f"{jar.name}:{class_name}: registered by {sorted(owners)} but {entry} is missing"
                )
                continue

            target = Path(temp_dir) / entry
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(zf.read(entry))
            try:
                sections = annotation_sections(run_javap(target))
            except Exception as exc:
                failures.append(f"{jar.name}:{class_name}: javap failed: {exc}")
                continue

            mixin_visible = "org.spongepowered.asm.mixin.Mixin" in sections["visible"]
            mixin_invisible = "org.spongepowered.asm.mixin.Mixin" in sections["invisible"]
            if not mixin_invisible or mixin_visible:
                failures.append(
                    f"{jar.name}:{class_name}: @Mixin visibility invalid "
                    f"(visible={mixin_visible}, invisible={mixin_invisible})"
                )

            injector_bad = sorted(RUNTIME_VISIBLE & sections["invisible"])
            if injector_bad:
                failures.append(
                    f"{jar.name}:{class_name}: runtime injector annotation(s) emitted invisible: "
                    + ", ".join(injector_bad)
                )

            rows.append(
                {
                    "jar": jar.name,
                    "class": class_name,
                    "configs": sorted(owners),
                    "mixin": "invisible" if mixin_invisible and not mixin_visible else "INVALID",
                    "runtime_annotations": sorted(RUNTIME_VISIBLE & sections["visible"]),
                    "bad_runtime_annotations": injector_bad,
                }
            )
    return rows, failures


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--mods-dir", default="mods")
    parser.add_argument("--json-output")
    args = parser.parse_args()

    jars = sorted(Path(args.mods_dir).glob("maki-*.jar"))
    if not jars:
        print("No maki-* JARs found.")
        return 2

    all_rows: list[dict] = []
    failures: list[str] = []
    for jar in jars:
        try:
            rows, jar_failures = audit_jar(jar)
        except zipfile.BadZipFile as exc:
            rows, jar_failures = [], [f"{jar.name}: invalid JAR: {exc}"]
        all_rows.extend(rows)
        failures.extend(jar_failures)

    audited_jars = sorted({row["jar"] for row in all_rows})
    print(f"Audited {len(all_rows)} registered Mixin classes in {len(audited_jars)} maki-* JARs.")
    for row in all_rows:
        visible = ", ".join(a.rsplit(".", 1)[-1] for a in row["runtime_annotations"]) or "-"
        print(f"OK? {row['jar']} :: {row['class']} :: @Mixin={row['mixin']} :: runtime={visible}")

    if args.json_output:
        Path(args.json_output).write_text(
            json.dumps({"classes": all_rows, "failures": failures}, indent=2) + "\n",
            encoding="utf-8",
        )

    if failures:
        print("\nMixin annotation ABI failures:")
        for failure in failures:
            print(f"FAIL: {failure}")
        return 1

    print("\nAll registered Mixin classes passed annotation ABI checks.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
