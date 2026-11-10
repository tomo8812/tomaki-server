"""Conservative source-reference audit for all maintained custom MODs.

A finding is a review candidate, not proof of runtime unreachability. NeoForge,
Mixin, services, reflection, registries, events and external APIs are special.
"""
import argparse
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
IDENT = re.compile(r"\b[A-Za-z_$][A-Za-z_0-9$]*\b")
JAVA_TOKEN = re.compile(r'"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'|//[^\n]*|/\*.*?\*/', re.S)
PRIV_METHOD = re.compile(
    r"\bprivate\s+(?:(?:static|final|synchronized|native)\s+)*"
    r"[\w$<>,.?\[\] \t]+\s+([A-Za-z_$][\w$]*)\s*"
    r"\((?:[^()]|\([^()]*\))*\)\s*(?:throws\s+[\w$., \t]+)?\s*\{",
    re.M,
)
PRIV_FIELD = re.compile(
    r"\bprivate\s+(?:(?:static|final|volatile|transient)\s+)*"
    r"[\w$<>,.?\[\] \t]+\s+([A-Za-z_$][\w$]*)\s*(?:=[^;{}]*)?;",
    re.M,
)
RUNTIME_ANNOTATIONS = (
    "@Mod", "@Mixin", "@SubscribeEvent", "@EventBusSubscriber", "@Inject",
    "@Redirect", "@Modify", "@Accessor", "@Invoker", "@Shadow", "@Unique",
    "@Overwrite", "@Override", "@Subscribe", "@AutoRegister",
)


def strip_comments(source):
    def replace(match):
        part = match.group()
        if part.startswith("//") or part.startswith("/*"):
            return re.sub(r"[^\n]", " ", part)
        return part
    return JAVA_TOKEN.sub(replace, source)


def previous_annotations(source, offset):
    # Avoid methods registered by annotations. Inspect the declaration preamble.
    prefix = source[max(0, offset - 600):offset]
    cut = max(prefix.rfind("}"), prefix.rfind(";"), prefix.rfind("{"))
    return prefix[cut + 1:]


def audit():
    roots = sorted(p for p in (ROOT / "custom-mods").iterdir()
                   if p.is_dir() and (p / "build.gradle").is_file())
    files = []
    resources = []
    for mod in roots:
        for path in sorted((mod / "src").rglob("*.java")):
            files.append((mod.name, path, strip_comments(path.read_text(encoding="utf-8"))))
        for path in sorted((mod / "src").rglob("*")):
            if path.is_file() and path.suffix in (".json", ".toml", ".properties"):
                resources.append(path.read_text(encoding="utf-8"))
    for path in (ROOT / "config").rglob("*"):
        if path.is_file() and path.suffix in (".json", ".toml", ".properties"):
            resources.append(path.read_text(encoding="utf-8", errors="replace"))
    resources_text = "\n".join(resources)
    findings = []
    for mod, path, source in files:
        if "/src/main/java/" not in path.as_posix():
            continue  # Test-only code and compile-only connector API are not runtime code.
        relative = path.relative_to(ROOT).as_posix()
        name = path.stem
        line = lambda at: source.count("\n", 0, at) + 1
        # Cross-file identifier references include code, imported types, and string
        # literals (to avoid losing trivially discoverable reflection references).
        other_refs = any(
            re.search(r"\b" + re.escape(name) + r"\b", other_source)
            for _, other_path, other_source in files if other_path != path
        )
        resource_ref = re.search(r"\b" + re.escape(name) + r"\b", resources_text) is not None
        runtime_root = any(a in source for a in ("@Mod(", "@Mixin(", "@EventBusSubscriber"))
        if not other_refs and not resource_ref and not runtime_root:
            findings.append(dict(kind="unreferenced-type", mod=mod, path=relative,
                                 line=1, symbol=name, confidence="review"))
        if "/mixin/" in relative or "@Mixin(" in source:
            continue  # Injection callbacks are invoked by Mixin, outside Java calls.
        for pattern, kind in ((PRIV_METHOD, "private-method"), (PRIV_FIELD, "private-field")):
            for match in pattern.finditer(source):
                symbol = match.group(1)
                preamble = previous_annotations(source, match.start())
                if any(tag in preamble for tag in RUNTIME_ANNOTATIONS):
                    continue
                if kind == "private-method":
                    # Ignore constructors and special names; a single declaration
                    # with no textual call is only a candidate.
                    if symbol == name or len(re.findall(
                        r"\b" + re.escape(symbol) + r"\b", source
                    )) != 1:
                        continue
                elif len(re.findall(r"\b" + re.escape(symbol) + r"\b", source)) != 1:
                    continue
                findings.append(dict(kind=kind, mod=mod, path=relative,
                                     line=line(match.start()), symbol=symbol,
                                     confidence="review"))
    return dict(projects=len(roots), java_files=len(files),
                candidates=sorted(findings, key=lambda x: (
                    x["mod"], x["path"], x["line"], x["symbol"])))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    report = audit()
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n",
                               encoding="utf-8")
    print("Audited {projects} custom MODs, {java_files} Java sources; "
          "{count} review candidates".format(count=len(report["candidates"]), **report))
    for item in report["candidates"]:
        print("{path}:{line}: {kind} {symbol}".format(**item))


if __name__ == "__main__":
    main()
