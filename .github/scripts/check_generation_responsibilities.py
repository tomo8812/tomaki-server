"""Check the architectural boundary of generation control, FULL storage and village compatibility."""
from pathlib import Path
import json

root = Path(__file__).resolve().parents[2]
projects = root / 'custom-mods'
cache = projects / 'maki-chunksmith-chunk-cache'
village = projects / 'maki-millenaire-lod-compat'
controller = projects / 'maki-chunksmith-controller'

for source in (cache / 'src/main/java').rglob('*.java'):
    text = source.read_text()
    for forbidden in ('com.kishku7.chunksmith.lod.', '.cslod', 'hasLod(', 'markLod(',
                      'new GenerationTask(', 'reserveCandidate(', 'beginActual('):
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

print('Generation responsibilities: PASS (Controller admission, FULL-only cache, village deferral)')
