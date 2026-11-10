#!/usr/bin/env python3
"""Check the installed MOD set and required Mixin injection points against shipped bytecode.

This is a static pre-merge gate, not a substitute for a NeoForge boot test.
Minecraft/NeoForge transformed classes and remapped Mixin targets are reported
as unverified rather than pretending that their runtime injection is proven.
"""
from __future__ import annotations

import argparse
from functools import lru_cache
import json
import os
from pathlib import Path
import re
import struct
import tomllib
import zipfile

from classfile_annotations import Reader, parse_class

INJECTOR_PREFIXES = (
    "Lorg/spongepowered/asm/mixin/injection/",
    "Lcom/llamalad7/mixinextras/injector/",
)
INVOKE_OPCODES = {0xb6, 0xb7, 0xb8, 0xb9}
TWO_BYTE = set(range(0x15, 0x1a)) | set(range(0x36, 0x3b)) | {0x10, 0x12, 0xa9, 0xbc}
THREE_BYTE = {0x11, 0x13, 0x14, 0x84, 0xbb, 0xbd, 0xc0, 0xc1} | set(range(0x99, 0xa9)) | set(range(0xb2, 0xb9)) | {0xc6, 0xc7}
FIVE_BYTE = {0xb9, 0xba, 0xc8, 0xc9}


def instruction_length(code: bytes, offset: int) -> int:
    opcode = code[offset]
    if opcode in TWO_BYTE:
        return 2
    if opcode in THREE_BYTE:
        return 3
    if opcode == 0xc5:
        return 4
    if opcode in FIVE_BYTE:
        return 5
    if opcode == 0xc4:
        if offset + 1 >= len(code):
            raise ValueError("truncated wide instruction")
        return 6 if code[offset + 1] == 0x84 else 4
    if opcode in (0xaa, 0xab):
        base = offset + 1 + ((4 - ((offset + 1) % 4)) % 4)
        if opcode == 0xaa:
            if base + 12 > len(code):
                raise ValueError("truncated tableswitch")
            lo, hi = struct.unpack_from(">ii", code, base + 4)
            if hi < lo or hi - lo > 65536:
                raise ValueError("invalid tableswitch bounds")
            return base + 12 + 4 * (hi - lo + 1) - offset
        if base + 8 > len(code):
            raise ValueError("truncated lookupswitch")
        entries = struct.unpack_from(">i", code, base + 4)[0]
        if entries < 0 or entries > 65536:
            raise ValueError("invalid lookupswitch size")
        return base + 8 + 8 * entries - offset
    return 1


def class_bytecode(raw: bytes):
    """Read only method descriptors, fields and actual invoke instructions."""
    r = Reader(raw)
    if r.u4() != 0xcafebabe:
        raise ValueError("bad classfile magic")
    r.u2()
    r.u2()
    cp = [None] * r.u2()
    index = 1
    while index < len(cp):
        tag = r.u1()
        if tag == 1:
            cp[index] = r.read(r.u2()).replace(b"\xc0\x80", b"\0").decode("utf-8", "surrogatepass")
        elif tag in (3, 4):
            r.read(4)
        elif tag in (5, 6):
            r.read(8)
            index += 1
        elif tag in (7, 8, 16, 19, 20):
            cp[index] = (tag, r.u2())
        elif tag in (9, 10, 11, 12, 17, 18):
            cp[index] = (tag, r.u2(), r.u2())
        elif tag == 15:
            cp[index] = (tag, r.u1(), r.u2())
        else:
            raise ValueError(f"invalid constant pool tag {tag}")
        index += 1

    def invoked(code: bytes):
        calls = []
        at = 0
        while at < len(code):
            opcode = code[at]
            if opcode in INVOKE_OPCODES:
                if at + 3 > len(code):
                    raise ValueError("truncated invoke")
                cp_index = struct.unpack_from(">H", code, at + 1)[0]
                item = cp[cp_index]
                if item is None or item[0] not in (10, 11):
                    raise ValueError("invalid invoke method reference")
                klass, nat = cp[item[1]], cp[item[2]]
                if klass[0] != 7 or nat[0] != 12:
                    raise ValueError("malformed invoke constant pool reference")
                calls.append((cp[klass[1]], cp[nat[1]], cp[nat[2]]))
            length = instruction_length(code, at)
            if at + length > len(code):
                raise ValueError("truncated bytecode instruction")
            at += length
        return calls

    r.u2()   # access
    r.u2()   # this_class
    r.u2()   # super_class
    for _ in range(r.u2()):
        r.u2()
    fields = []
    methods = []
    for kind in ("field", "method"):
        for _ in range(r.u2()):
            r.u2()  # access
            name, descriptor = cp[r.u2()], cp[r.u2()]
            calls = []
            for _ in range(r.u2()):
                attribute, length = cp[r.u2()], r.u4()
                raw_attr = r.read(length)
                if kind == "method" and attribute == "Code":
                    cr = Reader(raw_attr)
                    cr.u2()
                    cr.u2()
                    calls = invoked(cr.read(cr.u4()))
            record = {"name": name, "descriptor": descriptor, "calls": calls}
            (fields if kind == "field" else methods).append(record)
    return {"fields": fields, "methods": methods}


def scalar(value, default=None):
    if value is None:
        return default
    if value["kind"] == "[":
        return [scalar(v) for v in value["value"]]
    if value["kind"] == "@":
        return value["value"]
    return value.get("value", default)


def values(annotation):
    return {name: scalar(value) for name, value in annotation.get("values", {}).items()}


def selectors(value):
    if value is None:
        return []
    return value if isinstance(value, list) else [value]


def method_matches(selector: str, method: dict):
    if selector == "*":
        return True
    name, separator, desc = selector.partition("(")
    if name.endswith("*"):
        # Mixin's trailing '*' matches overloads of this named method.
        return method["name"] == name[:-1]
    if separator:
        return method["name"] == name and method["descriptor"] == "(" + desc
    return method["name"] == name


def invoke_target(target: str):
    match = re.fullmatch(r"L([^;]+);([^\(]+)(\(.*\).+)", target or "")
    return (match.group(1), match.group(2), match.group(3)) if match else None


def scan(mods_dir: Path):
    jars = sorted(mods_dir.glob("*.jar"))
    failures, warnings, checks = [], [], []
    if not jars:
        return [], ["No MOD JARs present"], [], []
    classes = {}
    metadata = {}
    providers = {}
    archives = {}
    try:
        for jar in jars:
            try:
                zf = zipfile.ZipFile(jar)
                if zf.testzip() is not None:
                    raise ValueError("CRC mismatch")
                archives[jar] = zf
                names = set(zf.namelist())
                for name in names:
                    if name.endswith(".class") and not name.startswith("META-INF/versions/"):
                        classes.setdefault(name.removesuffix(".class"), jar)
                if "META-INF/neoforge.mods.toml" in names:
                    meta = tomllib.loads(zf.read("META-INF/neoforge.mods.toml").decode("utf-8"))
                    metadata[jar] = meta
                    for mod in meta.get("mods", []):
                        mod_id = mod.get("modId")
                        if not mod_id:
                            continue
                        if mod_id in providers:
                            failures.append(f"Duplicate MOD id {mod_id}: {providers[mod_id].name} / {jar.name}")
                        else:
                            providers[mod_id] = jar
            except (OSError, zipfile.BadZipFile, ValueError, KeyError) as exc:
                failures.append(f"{jar.name}: cannot inspect JAR: {exc}")

        special = {"minecraft", "neoforge", "java", "forge"}
        for jar, meta in metadata.items():
            for owner, entries in meta.items():
                if not owner.startswith("dependencies.") or not isinstance(entries, list):
                    continue
                for entry in entries:
                    if entry.get("type") not in ("required", "mandatory"):
                        continue
                    if entry.get("side", "BOTH") == "CLIENT":
                        continue
                    dep = entry.get("modId")
                    if dep and dep not in providers and dep not in special:
                        # Libraries bundled through JarJar / Fabric Loader cannot
                        # always be resolved from top-level NeoForge manifests.
                        warnings.append(f"{jar.name}: required {dep} is not a top-level NeoForge provider (inspect nested/provided libraries)")

        @lru_cache(maxsize=None)
        def bytecode(owner: str):
            jar = classes[owner]
            return class_bytecode(archives[jar].read(owner + ".class"))

        for jar, meta in metadata.items():
            zf = archives[jar]
            for registered in meta.get("mixins", []):
                config_file = registered.get("config")
                if not config_file:
                    continue
                if config_file not in zf.namelist():
                    failures.append(f"{jar.name}: Mixin config missing: {config_file}")
                    continue
                try:
                    config = json.loads(zf.read(config_file).decode("utf-8"))
                except (ValueError, UnicodeError) as exc:
                    failures.append(f"{jar.name}:{config_file}: invalid Mixin config: {exc}")
                    continue
                prefix = config.get("package", "")
                required = int(config.get("injectors", {}).get("defaultRequire", 0))
                for key in ("mixins", "server"):
                    for class_name in config.get(key, []):
                        fqcn = prefix + "." + class_name if prefix else class_name
                        path = fqcn.replace(".", "/") + ".class"
                        if path not in zf.namelist():
                            failures.append(f"{jar.name}:{fqcn}: Mixin class missing")
                            continue
                        try:
                            parsed = parse_class(zf.read(path))
                        except (ValueError, KeyError) as exc:
                            failures.append(f"{jar.name}:{fqcn}: invalid Mixin class: {exc}")
                            continue
                        mixin = next((a for a in parsed["annotations"]
                                      if a["descriptor"] == "Lorg/spongepowered/asm/mixin/Mixin;"), None)
                        if not mixin:
                            failures.append(f"{jar.name}:{fqcn}: @Mixin annotation missing")
                            continue
                        mixin_args = values(mixin)
                        if mixin_args.get("remap", True):
                            warnings.append(f"{jar.name}:{fqcn}: remapped target needs runtime-mapped Minecraft check")
                            continue
                        targets = selectors(mixin_args.get("targets")) + selectors(mixin_args.get("value"))
                        for target in targets:
                            target = str(target).strip("L;").replace(".", "/")
                            if target not in classes:
                                warnings.append(f"{jar.name}:{fqcn}: target {target} not found in top-level MOD bytecode")
                                continue
                            try:
                                target_class = bytecode(target)
                            except (ValueError, KeyError) as exc:
                                failures.append(f"{jar.name}:{fqcn}: cannot inspect target {target}: {exc}")
                                continue
                            for handler in parsed["methods"]:
                                for ann in handler["annotations"]:
                                    if not ann["descriptor"].startswith(INJECTOR_PREFIXES):
                                        continue
                                    kind = ann["descriptor"].rsplit("/", 1)[-1].removesuffix(";")
                                    av = values(ann)
                                    minimum = int(av.get("require", required))
                                    if minimum < 1:
                                        continue
                                    methods = [m for m in target_class["methods"]
                                               if any(method_matches(selector, m) for selector in selectors(av.get("method")))]
                                    if not methods:
                                        failures.append(f"{jar.name}:{fqcn}.{handler['name']}: required {kind} method {av.get('method')} absent from {target}")
                                        continue
                                    at = av.get("at")
                                    if isinstance(at, list):
                                        points = at
                                    else:
                                        points = [at] if at is not None else []
                                    if not points:
                                        checks.append(f"{jar.name}:{fqcn}.{handler['name']} target method exists")
                                        continue
                                    for point in points:
                                        if not isinstance(point, dict):
                                            continue
                                        pv = values(point)
                                        if pv.get("value") not in ("INVOKE", "INVOKE_ASSIGN"):
                                            continue
                                        call = invoke_target(pv.get("target"))
                                        if call is None:
                                            continue
                                        ordinal = int(pv.get("ordinal", -1))
                                        counts = [(m["name"] + m["descriptor"], m["calls"].count(call)) for m in methods]
                                        matches = sum(int(count > ordinal) if ordinal >= 0 else count for _, count in counts)
                                        if matches < minimum:
                                            elsewhere = [
                                                m["name"] + m["descriptor"] for m in target_class["methods"]
                                                if m not in methods and call in m["calls"]
                                            ]
                                            failures.append(
                                                f"{jar.name}:{fqcn}.{handler['name']}: {kind} requires {minimum} "
                                                f"{call}, ordinal={ordinal}, matched={matches}, selected={counts}; "
                                                f"other methods containing this call={elsewhere}"
                                            )
                                        else:
                                            checks.append(f"{jar.name}:{fqcn}.{handler['name']}: {matches} verified {kind} injection(s)")
    finally:
        for zf in archives.values():
            zf.close()
    return jars, failures, warnings, checks


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--mods-dir", type=Path, default=Path("mods"))
    ap.add_argument("--json-output", type=Path)
    ap.add_argument("--github-actions", action="store_true")
    args = ap.parse_args()
    jars, failures, warnings, checks = scan(args.mods_dir)
    result = {"jars": len(jars), "verified_injections": checks, "warnings": warnings, "failures": failures}
    if args.json_output:
        args.json_output.parent.mkdir(parents=True, exist_ok=True)
        args.json_output.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"MOD interop: {len(jars)} JARs, {len(checks)} verified checks, {len(warnings)} unverified/warnings, {len(failures)} errors")
    for error in failures:
        print("FAIL:", error)
        if args.github_actions:
            print("::error title=Cross-MOD compatibility::" + error.replace("%", "%25").replace("\n", "%0A"))
    for warning in warnings[:30]:
        print("UNVERIFIED:", warning)
    if args.github_actions and os.environ.get("GITHUB_STEP_SUMMARY"):
        with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as out:
            out.write(f"\n## Cross-MOD bytecode compatibility\n\n"
                      f"- JAR files: {len(jars)}\n- Verified required injection points: {len(checks)}\n"
                      f"- Unverified items / metadata warnings: {len(warnings)}\n- Failures: {len(failures)}\n\n")
            for error in failures:
                out.write("- FAIL: " + error.replace("|", "\\|") + "\n")
            out.write("\nMinecraft remapping, runtime transformations and actual server startup "
                      "require a separate integration smoke test; this job checks raw shipped bytecode.\n")
    return 1 if failures else 0


if __name__ == "__main__":
    raise SystemExit(main())
