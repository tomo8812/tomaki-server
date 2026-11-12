"""Check the architectural boundary of generation control, FULL storage and village compatibility."""
from pathlib import Path
import tomllib

root = Path(__file__).resolve().parents[2]
projects = root / 'custom-mods'
cache = projects / 'maki-chunksmith-chunk-cache'
village = projects / 'maki-millenaire-lod-compat'
controller = projects / 'maki-chunksmith-controller'

for source in (cache / 'src/main/java').rglob('*.java'):
    text = source.read_text()
    for forbidden in ('com.kishku7.chunksmith.lod.', '.cslod', 'hasLod(', 'markLod(',
                      'new GenerationTask(', 'reserveCandidate(', 'beginActual(', 'isChunkGenerated(',
                      'protectChunksmithGeneratedResult(', 'observeGenerated('):
        assert forbidden not in text, f'{source.relative_to(root)} crosses FULL storage boundary: {forbidden}'

for source in (village / 'src/main/java').rglob('*.java'):
    text = source.read_text()
    for forbidden in ('LodSupport', 'CsLod', 'LodRefreshQueue', 'setUnsaved(',
                      'getChunkAtAsync(', 'pregenPersistenceSuppressed(', 'BackfillGuard'):
        assert forbidden not in text, f'{source.relative_to(root)} crosses village compatibility boundary: {forbidden}'

for project in (cache, village):
    for source in (project / 'src/main/java').rglob('*.java'):
        text = source.read_text()
        assert '@Mixin(targets="com.kishku7.chunksmith.GenerationTask"' not in text, (
            f'GenerationTask hook must be owned by Controller: {source.relative_to(root)}')

for name in ('AdaptivePolicy.java', 'PregenFlowController.java'):
    text = (controller / 'src/main/java/maki/chunksmithcontroller' / name).read_text()
    for forbidden in ('NbtIo', 'CsLodRegionStore', 'Files.delete', 'PromotionClaim', 'FingerprintManifest'):
        assert forbidden not in text, f'Controller must not own cache/LOD storage: {name}: {forbidden}'


# These installed mods are a single supported configuration, not optional providers.
for project in (cache, controller, village):
    metadata = tomllib.loads((project / 'src/main/resources/META-INF/neoforge.mods.toml').read_text())
    for entries in metadata.get('dependencies', {}).values():
        assert all(entry['type'] != 'optional' for entry in entries), f'Optional dependency in {project.name}'

for project, names in ((cache, ('FullTelemetryBridge.java', 'FollowCaptureOwnership.java')),
                       (controller, ('CpuGovernor.java',))):
    for name in names:
        files = list((project / 'src/main/java').rglob(name))
        assert len(files) == 1
        text = files[0].read_text()
        assert 'Class.forName' not in text and 'ModList' not in text, f'Optional API bridge remains: {name}'

replay = (village / 'src/main/java/maki/millenairelod/ReplayController.java').read_text()
for forbidden in ('java.lang.reflect', 'ManagementFactory', 'ChunksmithSignals', 'csDispatchRatio', 'sinkQueue'):
    assert forbidden not in replay, f'Village replay must not control generation resources: {forbidden}'
for source in (controller / 'src/main/java').rglob('*.java'):
    assert 'backfill' not in source.read_text().lower(), f'Retired Backfill scheduling remains: {source.name}'

print('Generation responsibilities: PASS (direct dependencies, native decisions, FULL-only cache, village queues)')
