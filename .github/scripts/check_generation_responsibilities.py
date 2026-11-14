"""Check normal world persistence and the separation of generation and map display."""
from pathlib import Path
import json
root=Path(__file__).resolve().parents[2]
projects=root/'custom-mods'
controller=projects/'maki-chunksmith-controller'
for name in ('maki-chunksmith-chunk-cache','maki-millenaire-lod-compat'):
    assert not (projects/name).exists(), f'Retired pregen project remains: {name}'
for source in (controller/'src/main/java').rglob('*.java'):
    text=source.read_text()
    for forbidden in ('maki.chunksmithcache','PregenBorrowLedger','ChunkSerializer','Backfill','FingerprintManifest','import com.kishku7.chunksmith.lod.CsLodRegionStore','new CsLodRegionStore','SavedLodPresence','markLod(', 'LodUpdateQueue', 'ExploredLodRefresh', 'reserveBackfill', 'deferCandidate', 'incompleteInRange', 'ExternalTaskMigration', 'TaskMigrationStore'):
        assert forbidden not in text, f'Generation controller crosses persistence boundary: {source.name}: {forbidden}'
assert 'NativeLodTelemetryMixin' not in (controller/'src/main/resources/maki-chunksmith-controller.mixins.json').read_text(), 'LOD writes belong to the fix MOD'
for name in ('AdaptivePolicy.java','PregenFlowController.java'):
    assert 'Files.delete' not in (controller/'src/main/java/maki/chunksmithcontroller'/name).read_text()
center=(controller/'src/main/java/maki/chunksmithcontroller/PlayerCenter.java').read_text()
assert '.horizon(0)' in center and '.horizon(1)' not in center, 'Follow task must keep ordinary FULL persistence'
idle=projects/'maki-idle-pregen'
registered=json.loads((idle/'src/main/resources/maki-idle-pregen.mixins.json').read_text())['server']
assert {'VillageManagerMixin', 'MillVillagerMixin', 'MinecraftServerMixin'} <= set(registered), 'Idle village and villager activity pause must remain'
village_tick=(idle/'src/main/java/maki/idlepregen/mixin/VillageManagerMixin.java').read_text()
assert 'IdlePregenApi.isActive()' in village_tick and 'ci.cancel()' in village_tick
assert 'VillageSpawnQueueMixin' not in registered, 'Idle mode must not cancel native village creation'
assert 'VillageSpawnClockMixin' not in registered, 'Native spawn clocks must remain unchanged'
for source in (idle/'src/main/java').rglob('*.java'):
    assert 'setFrozen(' not in source.read_text(), 'Idle pregen must not freeze or unfreeze the world'
fixes=projects/'maki-chunksmith-fixes'
presence=(fixes/'src/main/java/maki/chunksmithfixes/mixin/PresenceIndexMixin.java').read_text()
assert 'method = "hasLod"' not in presence, 'Pending updates must not override native presence or create missing-LOD work'
guard=(fixes/'src/main/java/maki/chunksmithfixes/mixin/ExploredLodGuardMixin.java').read_text()
assert 'ExploredLodRefresh.capture' not in guard, 'Native first-time LOD capture must not create update receipts'
for source in (fixes/'src/main/java').rglob('*.java'):
    text=source.read_text()
    for forbidden in ('maki.chunksmithcontroller','org.millenaire','maki.chunksmithcache','GenerationTask(', 'FullSaveOrder', 'net.minecraft.world.level.chunk.storage.IOWorker', 'net.minecraft.world.level.chunk.storage.ChunkStorage', 'net.minecraft.world.level.chunk.storage.RegionFileStorage'):
        assert forbidden not in text, f'Chunksmith fixes cross scheduling or village boundary: {source.name}: {forbidden}'
assert not (root/'config/maki-chunksmith-chunk-cache.properties').exists()
print('Generation responsibilities: PASS (normal FULL persistence, native village creation, village/ship pause, normal world time, no temporary cache)')
