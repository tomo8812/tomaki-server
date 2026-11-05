#!/usr/bin/env python3
"""Audit classfile annotation ABI for every registered Mixin in maki-* JARs."""

from __future__ import annotations

import argparse
import json
import html
import os
import zipfile
import tomllib
from pathlib import Path
from classfile_annotations import parse_class, walk_annotations

RUNTIME_VISIBLE = {
    "org.spongepowered.asm.mixin.Shadow",
    "org.spongepowered.asm.mixin.Unique",
    "org.spongepowered.asm.mixin.Final",
    "org.spongepowered.asm.mixin.Mutable",
    "org.spongepowered.asm.mixin.Overwrite",
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


def validate_api(cls, api):
    failures = []
    def matches(value, desc):
        kind = value['kind']
        if desc.startswith('['):
            return kind == '[' and all(matches(v, desc[1:]) for v in value['value'])
        if desc == 'Ljava/lang/String;':
            return kind == 's'
        if desc == 'Ljava/lang/Class;':
            return kind == 'c'
        if len(desc) == 1:
            return kind == desc
        return ((kind == 'e' and value['type'] == desc) or
                (kind == '@' and value['value']['descriptor'] == desc))
    for location, members in [('TYPE', [cls]), ('FIELD', cls['fields']), ('METHOD', cls['methods'])]:
        for member in members:
            for annotation in walk_annotations(member['annotations']):
                desc = annotation['descriptor']
                if not desc.startswith(('Lorg/spongepowered/asm/mixin/', 'Lcom/llamalad7/mixinextras/')):
                    continue
                definition = api.get(desc)
                if definition is None:
                    failures.append(f'{desc}: missing from supplied annotation API')
                    continue
                methods = {m['name']: m['descriptor'][2:] for m in definition['methods']}
                for name, value in annotation['values'].items():
                    if name not in methods or not matches(value, methods[name]):
                        failures.append(f'{desc}.{name}: value encoding disagrees with API')
                for meta in definition['annotations']:
                    if meta['descriptor'] == 'Ljava/lang/annotation/Retention;':
                        visible = meta['values']['value']['value'] == 'RUNTIME'
                        if visible != annotation['visible']:
                            failures.append(f'{desc}: retention disagrees with API')
                # Nested annotations such as @At have no declaration target;
                # check @Target only for direct declarations.
                if annotation in member['annotations']:
                    for meta in definition['annotations']:
                        if meta['descriptor'] == 'Ljava/lang/annotation/Target;':
                            allowed = {v['value'] for v in meta['values']['value']['value']}
                            if location not in allowed:
                                failures.append(f'{desc}: invalid declaration target {location}')
    return failures


def audit_jar(jar: Path, api=None) -> tuple[list[dict], list[str]]:
    rows: list[dict] = []
    failures: list[str] = []
    with zipfile.ZipFile(jar) as zf:
        configs = sorted(
            name for name in zf.namelist()
            if name.lower().endswith(".json") and "mixin" in Path(name).name.lower()
        )
        registered: dict[str, set[str]] = {}
        parsed = {name: parse_class(zf.read(name)) for name in zf.namelist() if name.endswith('.class')}
        for path in parsed:
            if path.startswith(('org/spongepowered/asm/', 'com/llamalad7/mixinextras/')):
                failures.append(f'{jar.name}: bundled Mixin API/stub class: {path}')
        metadata = tomllib.loads(zf.read('META-INF/neoforge.mods.toml').decode('utf-8'))
        declared = {entry['config'] for entry in metadata.get('mixins', [])}
        for missing in declared - set(configs):
            failures.append(f'{jar.name}: declared config {missing} is missing')
        for config_name in configs:
            try:
                config = json.loads(zf.read(config_name).decode("utf-8"))
                classes = mixin_classes(config)
                if config_name not in declared:
                    failures.append(f'{jar.name}:{config_name}: not registered in neoforge.mods.toml')
                package = config.get('package', '').replace('.', '/').strip('/')
                if not package:
                    raise ValueError('Mixin package must not be empty')
                entries = {name.replace('.', '/') + '.class' for name in classes}
                for path in parsed:
                    if path.startswith(package + '/') and path not in entries:
                        # Inner implementation classes belong to their registered
                        # mixin; unrelated runtime classes must never live here.
                        outer = path.split('$', 1)[0] + '.class'
                        if '$' not in path or outer not in entries:
                            failures.append(f'{jar.name}:{config_name}: ordinary/unregistered class in reserved Mixin package: {path}')
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

            cls = parsed[entry]
            if api is not None:
                failures.extend(f'{jar.name}:{class_name}: {f}' for f in validate_api(cls, api))
            sections = {'visible': set(), 'invisible': set()}
            for member in [cls, *cls['fields'], *cls['methods']]:
                for annotation in walk_annotations(member['annotations']):
                    sections['visible' if annotation['visible'] else 'invisible'].add(
                        annotation['descriptor'][1:-1].replace('/', '.'))
            mixin_annotations = [a for a in cls['annotations'] if a['descriptor'] == 'Lorg/spongepowered/asm/mixin/Mixin;']
            mixin_visible = any(a['visible'] for a in mixin_annotations)
            mixin_invisible = any(not a['visible'] for a in mixin_annotations)
            if not mixin_invisible or mixin_visible:
                failures.append(
                    f"{jar.name}:{class_name}: @Mixin visibility invalid "
                    f"(visible={mixin_visible}, invisible={mixin_invisible})"
                )

            injector_bad = sorted(RUNTIME_VISIBLE & sections["invisible"])
            if injector_bad:
                failures.append(
                    f"{jar.name}:{class_name}: runtime annotation(s) emitted invisible: "
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


def write_reports(rows, failures, jars, json_output=None, github_actions=False):
    report = {'jars': [jar.name for jar in jars], 'classes': rows, 'failures': failures}
    if json_output:
        output = Path(json_output)
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
    if github_actions:
        for failure in failures:
            escaped = failure.replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
            print(f'::error title=Mixin static audit::{escaped}')
        summary_path = os.environ.get('GITHUB_STEP_SUMMARY')
        if summary_path:
            def code(value):
                return '<code>' + html.escape(str(value)).replace('|', '&#124;').replace('\n', '&#10;').replace('\r', '&#13;') + '</code>'
            lines = ['## Mixin static audit', '',
                     f"**{'FAIL' if failures else 'PASS'}** — {len(jars)} JARs, "
                     f'{len(rows)} registered classes, {len(failures)} failures.', '',
                     'Checks: annotation retention/ABI, config registration, class presence, '
                     'reserved packages and bundled API stubs. No server or client is started; '
                     'runtime injection-point compatibility is not established by this check.', '']
            if failures:
                lines += ['### Failures', ''] + ['- ' + code(f) for f in failures] + ['']
            lines += ['### Inventory', '', '| JAR | Registered classes |', '| --- | ---: |']
            for jar in jars:
                count = sum(row['jar'] == jar.name for row in rows)
                lines.append(f'| {code(jar.name)} | {count} |')
            lines += ['', 'Detailed class results are available in the JSON workflow artifact.', '']
            with Path(summary_path).open('a', encoding='utf-8') as stream:
                stream.write('\n'.join(lines))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--mods-dir", default="mods")
    parser.add_argument("--json-output")
    parser.add_argument("--github-actions", action='store_true',
                        help='Emit error annotations and append a GitHub job summary')
    parser.add_argument("--annotation-api", action='append', default=[],
                        help='Actual Sponge Mixin / MixinExtras JAR; repeat for multiple APIs')
    args = parser.parse_args()
    api = None
    if args.annotation_api:
        api = {}
        for path in args.annotation_api:
            with zipfile.ZipFile(path) as zf:
                for name in zf.namelist():
                    if name.endswith('.class') and name.startswith(('org/spongepowered/asm/mixin/', 'com/llamalad7/mixinextras/')):
                        cls = parse_class(zf.read(name))
                        if cls['access'] & 0x2000:
                            api['L' + cls['name'] + ';'] = cls

    jars = sorted(Path(args.mods_dir).glob("maki-*.jar"))
    all_rows: list[dict] = []
    failures: list[str] = [] if jars else [f'No maki-* JARs found in {args.mods_dir}.']
    for jar in jars:
        try:
            rows, jar_failures = audit_jar(jar, api)
        except (zipfile.BadZipFile, ValueError, KeyError, OSError) as exc:
            rows, jar_failures = [], [f"{jar.name}: invalid JAR/config/classfile: {exc}"]
        all_rows.extend(rows)
        failures.extend(jar_failures)

    audited_jars = sorted({row["jar"] for row in all_rows})
    print(f"Audited {len(all_rows)} registered Mixin classes in {len(audited_jars)} maki-* JARs.")
    for row in all_rows:
        visible = ", ".join(a.rsplit(".", 1)[-1] for a in row["runtime_annotations"]) or "-"
        print(f"OK? {row['jar']} :: {row['class']} :: @Mixin={row['mixin']} :: runtime={visible}")

    write_reports(all_rows, failures, jars, args.json_output, args.github_actions)

    if failures:
        print("\nMixin annotation ABI failures:")
        for failure in failures:
            print(f"FAIL: {failure}")
        return 1 if jars else 2

    print("\nAll registered Mixin classes passed annotation ABI checks.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
