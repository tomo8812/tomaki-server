#!/usr/bin/env python3
from __future__ import annotations

import argparse
import hashlib
import json
import os
import shutil
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path, PurePosixPath

SCHEMA_VERSION = 1
PIPELINE_REVISION = 1
VINEFLOWER_VERSION = "1.12.0"
VINEFLOWER_SHA256 = "1dfcfe974395734fa467ce620661c7623d05ba83670de0529b1fbd63ff548b9d"

VINEFLOWER_OUTPUT_OPTIONS = [
    "--folder",
    "--skip-extra-files=1",
    "--remove-synthetic=0",
    "--remove-bridge=0",
    "--log-level=WARN",
]

TEXT_EXTENSIONS = {
    ".accesswidener", ".at", ".cfg", ".conf", ".csv", ".json", ".json5",
    ".lang", ".license", ".list", ".mcmeta", ".md", ".mf", ".properties",
    ".snbt", ".toml", ".tsv", ".txt", ".xml", ".yaml", ".yml",
}
TEXT_BASENAMES = {
    "license", "notice", "copying", "credits", "authors", "changelog",
}


def pipeline_fingerprint() -> str:
    payload = {
        "schema": SCHEMA_VERSION,
        "pipeline_revision": PIPELINE_REVISION,
        "vineflower_version": VINEFLOWER_VERSION,
        "vineflower_sha256": VINEFLOWER_SHA256,
        "vineflower_output_options": VINEFLOWER_OUTPUT_OPTIONS,
        "text_extensions": sorted(TEXT_EXTENSIONS),
        "text_basenames": sorted(TEXT_BASENAMES),
    }
    raw = json.dumps(payload, sort_keys=True, separators=(",", ":")).encode()
    return hashlib.sha256(raw).hexdigest()


def run(cmd: list[str], *, cwd: Path | None = None) -> None:
    subprocess.run(cmd, cwd=cwd, check=True)


def git_tracked_jars(source_root: Path) -> dict[str, str]:
    proc = subprocess.run(
        ["git", "ls-files", "-s", "-z", "--", "mods"],
        cwd=source_root,
        check=True,
        stdout=subprocess.PIPE,
    )
    result: dict[str, str] = {}
    for record in proc.stdout.split(b"\0"):
        if not record:
            continue
        meta, path_raw = record.split(b"\t", 1)
        parts = meta.split()
        if len(parts) != 3:
            continue
        path = path_raw.decode("utf-8", "surrogateescape")
        if not path.lower().endswith(".jar"):
            continue
        result[path] = parts[1].decode("ascii")
    return dict(sorted(result.items()))


def output_path_for(jar_path: str) -> str:
    p = PurePosixPath(jar_path)
    try:
        rel = p.relative_to("mods")
    except ValueError as exc:
        raise ValueError(f"JAR is outside mods/: {jar_path}") from exc
    return str(PurePosixPath("mods") / rel.with_suffix(""))


def git_index_has_prefix(generated_root: Path, path: str) -> bool:
    proc = subprocess.run(
        ["git", "-C", str(generated_root), "ls-files", "-z", "--", path],
        check=True,
        stdout=subprocess.PIPE,
    )
    return bool(proc.stdout)


def load_manifest(generated_root: Path) -> dict:
    path = generated_root / ".decompile-manifest.json"
    if not path.exists():
        return {}
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return {}


def write_github_outputs(values: dict[str, object]) -> None:
    output = os.environ.get("GITHUB_OUTPUT")
    if not output:
        return
    with open(output, "a", encoding="utf-8") as f:
        for key, value in values.items():
            if isinstance(value, bool):
                value = "true" if value else "false"
            f.write(f"{key}={value}\n")


def make_plan(source_root: Path, generated_root: Path, force_all: bool) -> dict:
    current = git_tracked_jars(source_root)
    outputs = [output_path_for(path) for path in current]
    if len(outputs) != len(set(outputs)):
        raise RuntimeError("Two JAR paths resolve to the same generated output directory")

    manifest = load_manifest(generated_root)
    old_entries = manifest.get("jars", {}) if isinstance(manifest.get("jars", {}), dict) else {}
    expected_fingerprint = pipeline_fingerprint()
    pipeline_changed = manifest.get("pipeline_fingerprint") != expected_fingerprint
    rebuild_all = force_all or pipeline_changed

    decompile: list[dict] = []
    reuse: list[dict] = []
    unchanged: list[str] = []
    removed: list[dict] = []

    reusable_by_oid: dict[str, list[tuple[str, dict]]] = {}
    if not rebuild_all:
        for old_path, entry in old_entries.items():
            if not isinstance(entry, dict):
                continue
            oid = entry.get("blob_oid")
            out = entry.get("output")
            if oid and out and ((generated_root / out).is_dir() or git_index_has_prefix(generated_root, out)):
                reusable_by_oid.setdefault(oid, []).append((old_path, entry))

    for jar_path, oid in current.items():
        output = output_path_for(jar_path)
        old = old_entries.get(jar_path)
        if (
            not rebuild_all
            and isinstance(old, dict)
            and old.get("blob_oid") == oid
            and old.get("output") == output
            and ((generated_root / output).is_dir() or git_index_has_prefix(generated_root, output))
        ):
            unchanged.append(jar_path)
            continue

        reused = False
        if not rebuild_all:
            for source_path, source_entry in reusable_by_oid.get(oid, []):
                source_output = source_entry.get("output")
                if source_output and source_output != output and ((generated_root / source_output).is_dir() or git_index_has_prefix(generated_root, source_output)):
                    reuse.append({
                        "jar": jar_path,
                        "blob_oid": oid,
                        "output": output,
                        "reuse_from_jar": source_path,
                        "reuse_from_output": source_output,
                        "sha256": source_entry.get("sha256"),
                    })
                    reused = True
                    break
        if not reused:
            decompile.append({"jar": jar_path, "blob_oid": oid, "output": output})

    current_paths = set(current)
    for old_path, entry in old_entries.items():
        if old_path not in current_paths and isinstance(entry, dict) and entry.get("output"):
            removed.append({"jar": old_path, "output": entry["output"]})

    needs_work = bool(decompile or reuse or removed or rebuild_all or not manifest)
    return {
        "schema": SCHEMA_VERSION,
        "pipeline_fingerprint": expected_fingerprint,
        "pipeline_changed": pipeline_changed,
        "force_all": force_all,
        "rebuild_all": rebuild_all,
        "current": current,
        "decompile": decompile,
        "reuse": reuse,
        "unchanged": unchanged,
        "removed": removed,
        "needs_work": needs_work,
        "needs_decompiler": bool(decompile),
    }


def sha256_file(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def is_text_resource(name: str) -> bool:
    p = PurePosixPath(name)
    if name.endswith("/") or p.suffix.lower() == ".class":
        return False
    if p.suffix.lower() in TEXT_EXTENSIONS or p.name.lower() in TEXT_BASENAMES:
        return True
    return name.startswith("META-INF/services/")


def safe_zip_member(name: str) -> PurePosixPath | None:
    p = PurePosixPath(name)
    if p.is_absolute() or ".." in p.parts:
        return None
    return p


def extract_text_resources(jar: Path, target_root: Path) -> int:
    count = 0
    with zipfile.ZipFile(jar) as zf:
        for info in zf.infolist():
            if info.is_dir() or not is_text_resource(info.filename):
                continue
            member = safe_zip_member(info.filename)
            if member is None:
                continue
            data = zf.read(info)
            if b"\x00" in data[:8192]:
                continue
            target = target_root.joinpath(*member.parts)
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(data)
            count += 1
    return count


def jar_info_text(jar_path: str, blob_oid: str, sha256: str, resource_count: int | None = None) -> str:
    lines = [
        f"# {PurePosixPath(jar_path).name}",
        "",
        f"- Source: `{jar_path}`",
        f"- Git blob OID: `{blob_oid}`",
        f"- SHA-256: `{sha256}`",
        f"- Decompiler: Vineflower `{VINEFLOWER_VERSION}`",
    ]
    if resource_count is not None:
        lines.append(f"- Extracted text resources: `{resource_count}`")
    lines += [
        "",
        "`src/` is decompiled code. `resources/` contains only text-like non-class files from the original JAR.",
        "",
    ]
    return "\n".join(lines)


def decompile_one(source_root: Path, generated_root: Path, vineflower: Path, item: dict) -> tuple[str, int]:
    jar_path = item["jar"]
    jar = source_root / jar_path
    output = generated_root / item["output"]
    output.parent.mkdir(parents=True, exist_ok=True)

    temp_parent = generated_root / ".tmp-decompile"
    temp_parent.mkdir(parents=True, exist_ok=True)
    temp_dir = Path(tempfile.mkdtemp(prefix="jar-", dir=temp_parent))
    materialized_jar: Path | None = None
    try:
        # Main is blobless/sparse. Pull only this changed JAR blob on demand.
        if not jar.is_file():
            fd, temp_name = tempfile.mkstemp(prefix="input-", suffix=".jar", dir=temp_parent)
            os.close(fd)
            materialized_jar = Path(temp_name)
            with materialized_jar.open("wb") as out:
                subprocess.run(
                    ["git", "show", f"HEAD:{jar_path}"],
                    cwd=source_root,
                    check=True,
                    stdout=out,
                )
            jar = materialized_jar

        src_dir = temp_dir / "src"
        resources_dir = temp_dir / "resources"
        src_dir.mkdir(parents=True, exist_ok=True)
        threads = max(1, min(os.cpu_count() or 2, 4))
        cmd = [
            "java",
            "-XX:MaxRAMPercentage=65.0",
            "-jar",
            str(vineflower),
            *VINEFLOWER_OUTPUT_OPTIONS,
            f"--thread-count={threads}",
            str(jar),
            str(src_dir),
        ]
        print(f"Decompiling {jar_path} -> {item['output']} ({threads} threads)", flush=True)
        run(cmd)
        resource_count = extract_text_resources(jar, resources_dir)
        sha256 = sha256_file(jar)
        (temp_dir / "JAR_INFO.md").write_text(
            jar_info_text(jar_path, item["blob_oid"], sha256, resource_count),
            encoding="utf-8",
        )
        if output.exists():
            shutil.rmtree(output)
        shutil.move(str(temp_dir), str(output))
        return sha256, resource_count
    except Exception:
        shutil.rmtree(temp_dir, ignore_errors=True)
        raise
    finally:
        if materialized_jar is not None:
            materialized_jar.unlink(missing_ok=True)


def copytree_hardlink(src: Path, dst: Path) -> None:
    def link_or_copy(source: str, target: str) -> str:
        try:
            os.link(source, target)
            return target
        except OSError:
            return shutil.copy2(source, target)

    shutil.copytree(src, dst, copy_function=link_or_copy)


def resource_count_from_info(output: Path) -> int | None:
    info = output / "JAR_INFO.md"
    if not info.exists():
        return None
    for line in info.read_text(encoding="utf-8", errors="replace").splitlines():
        prefix = "- Extracted text resources: `"
        if line.startswith(prefix) and line.endswith("`"):
            try:
                return int(line[len(prefix):-1])
            except ValueError:
                return None
    return None


def apply_plan(source_root: Path, generated_root: Path, plan: dict, vineflower: Path | None) -> None:
    old_manifest = load_manifest(generated_root)
    old_entries = old_manifest.get("jars", {}) if isinstance(old_manifest.get("jars", {}), dict) else {}
    final_entries: dict[str, dict] = {}

    for jar_path in plan["unchanged"]:
        final_entries[jar_path] = old_entries[jar_path]

    for item in plan["reuse"]:
        src = generated_root / item["reuse_from_output"]
        dst = generated_root / item["output"]
        tmp_parent = generated_root / ".tmp-decompile"
        tmp_parent.mkdir(parents=True, exist_ok=True)
        tmp = Path(tempfile.mkdtemp(prefix="reuse-", dir=tmp_parent))
        shutil.rmtree(tmp)
        copytree_hardlink(src, tmp)
        sha256 = item.get("sha256") or old_entries.get(item["reuse_from_jar"], {}).get("sha256")
        if not sha256:
            sha256 = sha256_file(source_root / item["jar"])
        count = resource_count_from_info(tmp)
        (tmp / "JAR_INFO.md").write_text(
            jar_info_text(item["jar"], item["blob_oid"], sha256, count),
            encoding="utf-8",
        )
        if dst.exists():
            shutil.rmtree(dst)
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.move(str(tmp), str(dst))
        final_entries[item["jar"]] = {
            "blob_oid": item["blob_oid"],
            "sha256": sha256,
            "output": item["output"],
        }
        print(f"Reused {item['reuse_from_jar']} for {item['jar']}")

    if plan["decompile"] and vineflower is None:
        raise RuntimeError("Vineflower path is required for decompilation")

    for item in plan["decompile"]:
        sha256, _ = decompile_one(source_root, generated_root, vineflower, item)
        final_entries[item["jar"]] = {
            "blob_oid": item["blob_oid"],
            "sha256": sha256,
            "output": item["output"],
        }

    current_outputs = {entry["output"] for entry in final_entries.values()}
    for item in plan["removed"]:
        if item["output"] in current_outputs:
            continue
        target = generated_root / item["output"]
        if target.exists():
            shutil.rmtree(target)
            print(f"Removed stale output for {item['jar']}")

    for jar_path, oid in plan["current"].items():
        if jar_path in final_entries:
            continue
        old = old_entries.get(jar_path, {})
        output = output_path_for(jar_path)
        if old.get("blob_oid") == oid and (generated_root / output).is_dir():
            final_entries[jar_path] = old
        else:
            raise RuntimeError(f"No generated output for current JAR: {jar_path}")

    manifest = {
        "schema": SCHEMA_VERSION,
        "pipeline_fingerprint": plan["pipeline_fingerprint"],
        "generator": {
            "name": "Vineflower",
            "version": VINEFLOWER_VERSION,
            "sha256": VINEFLOWER_SHA256,
        },
        "jars": dict(sorted(final_entries.items())),
    }
    (generated_root / ".decompile-manifest.json").write_text(
        json.dumps(manifest, indent=2, sort_keys=True) + "\n",
        encoding="utf-8",
    )

    readme = [
        "# Decompiled Minecraft mods",
        "",
        "This branch is generated automatically from `main:mods/*.jar`. Do not edit generated files manually.",
        "",
        f"Decompiler: Vineflower {VINEFLOWER_VERSION}",
        "",
        "| JAR | Decompiled output | Git blob |",
        "|---|---|---|",
    ]
    for jar_path, entry in sorted(final_entries.items()):
        display = PurePosixPath(jar_path).name.replace("|", "\\|")
        link = entry["output"].replace(" ", "%20")
        readme.append(f"| `{display}` | [`{entry['output']}/`]({link}) | `{entry['blob_oid'][:12]}` |")
    readme += [
        "",
        "Only code and text-like resources are retained; binary assets such as textures and audio stay in the original JARs on `main`.",
        "",
    ]
    (generated_root / "README.md").write_text("\n".join(readme), encoding="utf-8")

    shutil.rmtree(generated_root / ".tmp-decompile", ignore_errors=True)

    replacement_paths = {item["output"] for item in plan["decompile"]}
    replacement_paths.update(item["output"] for item in plan["reuse"])
    removed_paths = {item["output"] for item in plan["removed"]}

    # Sparse checkout leaves untouched generated trees absent from disk. Remove only the
    # affected prefixes from the index, then add back replacements. This preserves every
    # unchanged JAR without downloading its generated source tree.
    for path in sorted(replacement_paths | removed_paths):
        subprocess.run(
            ["git", "-C", str(generated_root), "rm", "-r", "--cached", "--sparse", "--ignore-unmatch", "--", path],
            check=True,
        )
    for path in sorted(replacement_paths):
        run(["git", "-C", str(generated_root), "add", "--sparse", "-A", "--", path])
    run(["git", "-C", str(generated_root), "add", "--sparse", "-A", "--", ".decompile-manifest.json", "README.md"])


def main() -> int:
    parser = argparse.ArgumentParser()
    sub = parser.add_subparsers(dest="command", required=True)

    plan_p = sub.add_parser("plan")
    plan_p.add_argument("--source-root", type=Path, default=Path("."))
    plan_p.add_argument("--generated-root", type=Path, required=True)
    plan_p.add_argument("--plan-file", type=Path, required=True)
    plan_p.add_argument("--force-all", action="store_true")

    apply_p = sub.add_parser("apply")
    apply_p.add_argument("--source-root", type=Path, default=Path("."))
    apply_p.add_argument("--generated-root", type=Path, required=True)
    apply_p.add_argument("--plan-file", type=Path, required=True)
    apply_p.add_argument("--vineflower", type=Path)

    args = parser.parse_args()
    source_root = args.source_root.resolve()
    generated_root = args.generated_root.resolve()

    if args.command == "plan":
        plan = make_plan(source_root, generated_root, args.force_all)
        args.plan_file.parent.mkdir(parents=True, exist_ok=True)
        args.plan_file.write_text(json.dumps(plan, indent=2, sort_keys=True) + "\n", encoding="utf-8")
        write_github_outputs({
            "needs_work": plan["needs_work"],
            "needs_decompiler": plan["needs_decompiler"],
            "decompile_count": len(plan["decompile"]),
            "reuse_count": len(plan["reuse"]),
            "removed_count": len(plan["removed"]),
            "unchanged_count": len(plan["unchanged"]),
            "rebuild_all": plan["rebuild_all"],
        })
        print(
            f"Plan: decompile={len(plan['decompile'])}, reuse={len(plan['reuse'])}, "
            f"removed={len(plan['removed'])}, unchanged={len(plan['unchanged'])}, "
            f"rebuild_all={plan['rebuild_all']}"
        )
        return 0

    plan = json.loads(args.plan_file.read_text(encoding="utf-8"))
    apply_plan(source_root, generated_root, plan, args.vineflower.resolve() if args.vineflower else None)
    return 0


if __name__ == "__main__":
    sys.exit(main())
