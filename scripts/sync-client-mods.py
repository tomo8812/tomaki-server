#!/usr/bin/env python3
"""Synchronize shared client JARs and verify the client MOD folder."""
import argparse
import hashlib
import json
from pathlib import Path
import shutil
import tomllib
import zipfile

ROOT = Path(__file__).resolve().parents[1]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group(required=True)
    mode.add_argument('--sync', action='store_true', help='Copy shared server JARs into client-mods')
    mode.add_argument('--check', action='store_true', help='Verify without modifying files')
    args = parser.parse_args()
    client = ROOT / 'client-mods'
    names = [line.strip() for line in (client / 'shared-mods.txt').read_text().splitlines()
             if line.strip() and not line.lstrip().startswith('#')]
    if len(names) != len(set(names)):
        raise SystemExit('Duplicate filenames in shared-mods.txt')
    for name in names:
        if Path(name).name != name or not name.endswith('.jar'):
            raise SystemExit(f'Invalid shared filename: {name}')
        source, target = ROOT / 'mods' / name, client / name
        if not source.is_file():
            raise SystemExit(f'Missing shared source: {source}')
        if args.sync:
            shutil.copy2(source, target)
        if not target.is_file() or hashlib.sha256(source.read_bytes()).digest() != hashlib.sha256(target.read_bytes()).digest():
            raise SystemExit(f'Client/server JAR mismatch: {name}')
    listed = (ROOT / 'README.md').read_text().split('```text\n', 1)[1].split('```', 1)[0].splitlines()
    if set(listed) != set(names):
        raise SystemExit('README client list and shared-mods.txt differ')
    providers = {}
    jars = sorted(client.glob('*.jar'))
    for jar in jars:
        with zipfile.ZipFile(jar) as archive:
            if archive.testzip() is not None:
                raise SystemExit(f'Corrupt client JAR: {jar.name}')
            ids = []
            if 'META-INF/neoforge.mods.toml' in archive.namelist():
                metadata = tomllib.loads(archive.read('META-INF/neoforge.mods.toml').decode())
                ids = [mod['modId'] for mod in metadata.get('mods', [])]
            elif 'fabric.mod.json' in archive.namelist():
                ids = [json.loads(archive.read('fabric.mod.json'))['id']]
            for mod_id in ids:
                if mod_id in providers:
                    raise SystemExit(f'Duplicate client MOD id {mod_id}: {providers[mod_id]} / {jar.name}')
                providers[mod_id] = jar.name
    print(f'Client pack: {len(jars)} JARs, {len(names)} shared copies match; no duplicate top-level MOD IDs')


if __name__ == '__main__':
    main()
