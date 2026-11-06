"""Reject duplicate custom MOD IDs and stale embedded maintained Java sources."""
from pathlib import Path
import tomllib
import zipfile

root = Path(__file__).resolve().parents[2]
seen = {}
checked = 0
for jar in sorted((root / "mods").glob("maki-*.jar")):
    with zipfile.ZipFile(jar) as archive:
        assert archive.testzip() is None, f"Corrupt archive: {jar.name}"
        metadata = tomllib.loads(archive.read("META-INF/neoforge.mods.toml").decode())
        for mod in metadata["mods"]:
            mod_id = mod["modId"]
            assert mod_id not in seen, f"Duplicate {mod_id}: {seen.get(mod_id)} / {jar.name}"
            seen[mod_id] = jar.name
        project = root / "custom-mods" / jar.name.rsplit("-", 1)[0]
        if not project.is_dir():
            continue
        names = set(archive.namelist())
        for source in (project / "src/main/java").rglob("*.java"):
            embedded = "META-INF/sources/" + source.relative_to(project / "src/main/java").as_posix()
            # Older maintained projects may not embed sources. Require all embedded
            # projects to cover every maintained source, including added hooks.
            if any(name.startswith("META-INF/sources/") for name in names):
                assert embedded in names, f"Missing source {embedded} in {jar.name}"
                actual = archive.read(embedded).decode().replace("\r\n", "\n")
                expected = source.read_text(encoding="utf-8")
                assert actual == expected, f"Stale source {embedded} in {jar.name}"
                checked += 1
print(f"Deployment checks: {len(seen)} unique custom MODs; {checked} maintained embedded sources match")
