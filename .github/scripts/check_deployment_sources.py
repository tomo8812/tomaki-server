"""Verify the deployable custom MOD set is source-owned, unique, and current."""
from pathlib import Path
import tomllib
import zipfile

root = Path(__file__).resolve().parents[2]
mods_dir = root / "mods"
projects_dir = root / "custom-mods"

projects = sorted(path for path in projects_dir.iterdir() if path.is_dir() and (path / "build.gradle").is_file())
project_names = {path.name for path in projects}
expected_projects = {
    "maki-airship-compat",
    "maki-airship-foundation-compat",
    "maki-assembly-tracking",
    "maki-chunksmith-chunk-cache",
    "maki-diorama-share",
    "maki-idle-pregen",
    "maki-lod-cpu",
    "maki-millenaire-lod-compat",
    "maki-mods-observer",
    "maki-ship-anchor-fix",
    "maki-ship-edge",
    "maki-ship-footsteps",
    "maki-ship-save",
}
assert project_names == expected_projects, (
    f"Unexpected custom MOD source projects: missing={sorted(expected_projects-project_names)} "
    f"extra={sorted(project_names-expected_projects)}"
)

for forbidden in ("maki-status-io", "maki-lag-observer", "maki-lod-cpu-online-fix"):
    assert not (projects_dir / forbidden).exists(), f"Retired source project present: {forbidden}"
    assert not list(mods_dir.glob(forbidden + "-*.jar")), f"Retired deployable JAR present: {forbidden}"

assert not (root / "config/maki-lag-observer.properties").exists(), "Retired observer config is still deployed"
fingerprint_config = (root / "config/maki-chunksmith-chunk-cache.properties").read_text(encoding="utf-8")
assert "maki_lag_observer" not in fingerprint_config, "Retired observer MOD ID remains in active fingerprint config"
assert "maki-lag-observer.properties" not in fingerprint_config, "Retired observer config path remains in active fingerprint config"
assert "fingerprint.observation-only-mod-ids" not in fingerprint_config, "MOD-id-only compatibility exemptions must not be deployed"
assert "fingerprint.audited-neutral-mod-sha256" not in fingerprint_config, "Obsolete future-MOD audit config must be removed"
assert "fingerprint.generation-sensitive-mod-ids" not in fingerprint_config, "Hypothetical future-MOD policy must not be configured"
cache_policy = (
    root / "custom-mods/maki-chunksmith-chunk-cache/src/main/java/maki/chunksmithcache/FingerprintPolicy.java"
).read_text(encoding="utf-8")
assert '"epicterrain-"' in cache_policy and '"zfastnoise-"' in cache_policy, "Known terrain generators are not tracked"
assert "fingerprint.audited-neutral-mod-sha256" not in cache_policy, "Future-MOD SHA audit remains in FULL policy"
assert "maki-mods-observer-" not in cache_policy, "Observer role must not need a hardcoded compatibility exception"

seen_ids = {}
checked_sources = 0
matched_projects = set()
for jar in sorted(mods_dir.glob("maki-*.jar")):
    project_name = next(
        (name for name in sorted(expected_projects, key=len, reverse=True) if jar.name.startswith(name + "-")),
        None,
    )
    assert project_name is not None, f"No maintained source project owns {jar.name}"
    assert project_name not in matched_projects, f"Multiple deployed JARs for {project_name}"
    matched_projects.add(project_name)
    project = projects_dir / project_name

    with zipfile.ZipFile(jar) as archive:
        assert archive.testzip() is None, f"Corrupt archive: {jar.name}"
        metadata_text = archive.read("META-INF/neoforge.mods.toml").decode()
        assert "maki_status_io" not in metadata_text, f"Retired status-io dependency in deployed metadata: {jar.name}"
        assert "maki_lag_observer" not in metadata_text, f"Retired observer MOD ID in deployed metadata: {jar.name}"
        metadata = tomllib.loads(metadata_text)
        for mod in metadata["mods"]:
            mod_id = mod["modId"]
            assert mod_id not in seen_ids, f"Duplicate {mod_id}: {seen_ids.get(mod_id)} / {jar.name}"
            seen_ids[mod_id] = jar.name

        names = set(archive.namelist())
        embedded_java = {name for name in names if name.startswith("META-INF/sources/") and name.endswith(".java")}
        maintained_java = {
            "META-INF/sources/" + source.relative_to(project / "src/main/java").as_posix()
            for source in (project / "src/main/java").rglob("*.java")
        }
        assert embedded_java == maintained_java, (
            f"Embedded source set differs for {jar.name}: "
            f"missing={sorted(maintained_java-embedded_java)} extra={sorted(embedded_java-maintained_java)}"
        )
        for embedded in sorted(maintained_java):
            source = project / "src/main/java" / embedded.removeprefix("META-INF/sources/")
            actual = archive.read(embedded).decode().replace("\r\n", "\n")
            expected = source.read_text(encoding="utf-8").replace("\r\n", "\n")
            assert actual == expected, f"Stale source {embedded} in {jar.name}"
            checked_sources += 1

assert matched_projects == expected_projects, (
    f"Deployable custom MOD set incomplete: missing={sorted(expected_projects-matched_projects)}"
)

for path in projects_dir.rglob("*"):
    if not path.is_file():
        continue
    # StatusIo must disappear from both runtime code and build dependencies.
    if path.suffix in {".java", ".gradle", ".toml", ".json", ".properties"}:
        text = path.read_text(encoding="utf-8", errors="ignore")
        assert "maki.statusio" not in text, f"Retired status-io reference in {path.relative_to(root)}"
    # Old status-file names are allowed only in build/test regression assertions and docs,
    # never in runtime Java/resources/config where they could be emitted again.
    if path.suffix in {".java", ".toml", ".json", ".properties"}:
        text = path.read_text(encoding="utf-8", errors="ignore")
        assert "maki-lod-cpu-status.json" not in text, f"Retired status JSON reference in {path.relative_to(root)}"
        assert "maki-lod-follow-status.json" not in text, f"Retired status JSON reference in {path.relative_to(root)}"
        assert "maki-chunk-cache-backfill-status.json" not in text, f"Retired status JSON reference in {path.relative_to(root)}"


active_runtime_roots = [projects_dir, root / "config", root / ".github/workflows"]
for active_root in active_runtime_roots:
    for path in active_root.rglob("*"):
        if not path.is_file() or path.suffix not in {".java", ".gradle", ".toml", ".json", ".properties", ".yml", ".yaml"}:
            continue
        text = path.read_text(encoding="utf-8", errors="ignore")
        assert "maki_lag_observer" not in text, f"Retired observer MOD ID in active runtime file {path.relative_to(root)}"
        assert "maki_status_io" not in text, f"Retired status-io MOD ID in active runtime file {path.relative_to(root)}"
        assert "maki-status-io" not in text, f"Retired status-io artifact reference in active runtime file {path.relative_to(root)}"
        assert "maki-lag-observer.properties" not in text, f"Retired observer config name in active runtime file {path.relative_to(root)}"

print(
    f"Deployment checks: {len(seen_ids)} unique custom MOD IDs; "
    f"{len(matched_projects)} source-owned JARs; {checked_sources} embedded maintained sources match"
)
