#!/usr/bin/env python3
"""Conservative source reachability inventory for custom NeoForge MODs.

A source-only scan cannot prove that bytecode has no runtime callers.
This script reports candidates and protects NeoForge/Mixin entrypoints.
"""
import argparse
import json
import re
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CUSTOM = ROOT / "custom-mods"
ENTRY = re.compile(
    r"@(?:Mod|Mixin|EventBusSubscriber|SubscribeEvent|Mod\.EventBusSubscriber|AutoService)\b"
)
INJECTOR = re.compile(
    r"@(?:Inject|Redirect|Overwrite|Modify\w*|Wrap\w*|Accessor|Invoker|SubscribeEvent|"
    r"EventHandler|EventListener|Override|Shadow|Unique|OnEvent)\b"
)
PRIVATE_METHOD = re.compile(
    r"(?m)^\s*private\s+(?:(?:static|final|synchronized|native|strictfp)\s+)*"
    r"(?:<[^;{}()]+>\s*)?[\w.$<>?,\[\] ]+\s+([A-Za-z_$][\w$]*)\s*\("
)
IDENTIFIER = r"(?<![A-Za-z0-9_$]){}(?![A-Za-z0-9_$])"


def masked(source):
    # Mask comments and quoted literals without altering offsets.
    pattern = re.compile(r'//[^\n]*|/\*[\s\S]*?\*/|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'')
    return pattern.sub(lambda m: re.sub(r"[^\n]", " ", m.group()), source)


def audit():
    java = sorted(CUSTOM.glob("*/src/main/java/**/*.java"))
    tests = sorted(CUSTOM.glob("*/src/test/**/*.java"))
    resources = sorted(p for p in CUSTOM.rglob("*")
                       if p.is_file() and p.suffix in {".json", ".toml", ".gradle", ".properties", ".md", ".xml", ".txt"})
    raw = {p: p.read_text("utf-8", errors="replace") for p in java + tests + resources}
    code = {p: masked(raw[p]) for p in java + tests}
    production = {p: code[p] for p in java}
    entrypoint_text = "\n".join(raw[p] for p in resources)
    classes, methods = [], []
    for path in java:
        src = code[path]
        name = path.stem
        mod = path.relative_to(CUSTOM).parts[0]
        other_prod = [p for p, text in production.items()
                      if p != path and re.search(IDENTIFIER.format(re.escape(name)), text)]
        referenced_by_resource = bool(re.search(IDENTIFIER.format(re.escape(name)), entrypoint_text))
        registered = bool(ENTRY.search(raw[path])) or referenced_by_resource
        # A Mixin class or injected class might be discovered through metadata or the loader.
        if not other_prod and not registered:
            test_refs = sum(bool(re.search(IDENTIFIER.format(re.escape(name)), code[p])) for p in tests)
            classes.append({
                "mod": mod, "file": str(path.relative_to(ROOT)),
                "class": name, "test_references": test_refs,
                "basis": "no references in other production Java files or known loader resources"
            })
        # This is an inventory, not an automatic deletion rule.
        for match in PRIVATE_METHOD.finditer(src):
            method = match.group(1)
            if method == name:
                continue
            before = raw[path][:match.start()]
            # A Mixin callback is invoked by bytecode injection rather than Java calls.
            last_body_end = max(before.rfind("\n    }"), before.rfind("\n   }"), before.rfind("\n}"))
            annotation_area = before[last_body_end + 1:]
            if INJECTOR.search(annotation_area):
                continue
            # Method references (Thread(...::watchLoop), callbacks) are real callers.
            if re.search(r"::\s*" + re.escape(method) + r"\b", src):
                continue
            invocation = re.compile(IDENTIFIER.format(re.escape(method)) + r"\s*\(")
            uses = len(invocation.findall(src))
            if uses == 1:
                production_users = [str(p.relative_to(ROOT)) for p, text in production.items()
                                    if p != path and invocation.search(text)]
                if production_users:
                    continue
                methods.append({
                    "mod": mod, "file": str(path.relative_to(ROOT)),
                    "method": method, "line": src.count("\n", 0, match.start()) + 1,
                    "basis": "single declaration-like occurrence in production Java"
                })
    return {
        "scanned_java_files": len(java),
        "scanned_tests": len(tests),
        "scanned_resource_files": len(resources),
        "unreferenced_class_candidates": classes,
        "unreferenced_private_method_candidates": methods,
        "candidate_counts_by_mod": dict(Counter(x["mod"] for x in classes + methods)),
        "note": "Candidates require manual review of callbacks, generated lookups, reflection and test/build references."
    }


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--json-output", type=Path)
    args = parser.parse_args()
    result = audit()
    if args.json_output:
        args.json_output.parent.mkdir(parents=True, exist_ok=True)
        args.json_output.write_text(json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
