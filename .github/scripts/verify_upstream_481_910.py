#!/usr/bin/env python3
"""Verify the exact upstream bytecode callsites required by custom Mixin hooks.

Static ABI gate for the pinned Chunksmith 4.8.1 and Millenaire 9.1.0 jars.
The NeoForge runtime Mixin transformation still needs a real server boot.
"""
import argparse
from pathlib import Path
import re
import subprocess

METHOD = re.compile(r'^  [^\n]*(?:\([^\n]*\);)\s*$', re.MULTILINE)


def read(jar: Path, klass: str) -> str:
    result = subprocess.run(
        ['javap', '-classpath', str(jar), '-p', '-c', klass],
        text=True, capture_output=True, check=False,
    )
    if result.returncode:
        raise AssertionError(f'{klass}: javap failed: {result.stderr or result.stdout}')
    return result.stdout


def method(text: str, marker: str) -> str:
    matches = list(METHOD.finditer(text))
    matching = [i for i, item in enumerate(matches) if marker in item.group()]
    if len(matching) != 1:
        raise AssertionError(f'{marker}: expected one method, found {len(matching)}')
    i = matching[0]
    return text[matches[i].start():matches[i+1].start() if i+1 < len(matches) else len(text)]


def check(text: str, marker: str, target: str, expected: int) -> None:
    body = method(text, marker)
    actual = sum(target in line and '// ' in line for line in body.splitlines())
    if actual != expected:
        raise AssertionError(f'{marker}: {target}: expected {expected}, got {actual}')


def main():
    p = argparse.ArgumentParser()
    p.add_argument('--chunksmith', type=Path, required=True)
    p.add_argument('--millenaire', type=Path, required=True)
    args = p.parse_args()
    task = read(args.chunksmith, 'com.kishku7.chunksmith.GenerationTask')
    for name, target, count in [
        ('run()', 'RegionCache$WorldState.isGenerated:', 1),
        ('run()', 'CsLodPresenceIndex.hasLod:', 2),
        ('run()', 'World.isChunkGenerated:', 1),
        ('run()', 'ChunkIterator.next:', 1),
        ('run()', 'adjustFromLodQueue:', 1),
        ('run()', 'LodHorizon.arm:', 1),
        ('lambda$run$1(', 'CsLodPresenceIndex.hasLod:', 1),
        ('lambda$run$1(', 'World.getChunkAtAsync:', 2),
        ('lambda$run$1(', 'CsLodPresenceIndex.markLod:', 2),
    ]:
        check(task, name, target, count)

    world = read(args.chunksmith, 'com.kishku7.chunksmith.platform.NeoForgeWorld')
    for name, target, count in [
        ('getChunkAtAsync(int, int)', 'ticketSafePoint:', 2),
        ('getChunkAtAsync(int, int)', 'ServerChunkCacheMixin.invokeGetChunkFutureMainThread:', 1),
        ('lambda$getChunkAtAsync$8(', 'LodSupport.offer:', 1),
        ('lambda$getChunkAtAsync$10(', 'ServerChunkCache.removeRegionTicket:', 1),
        ('lambda$getChunkAtAsync$9(', 'ServerChunkCache.removeRegionTicket:', 1),
    ]:
        check(world, name, target, count)

    sink = read(args.chunksmith, 'com.kishku7.chunksmith.lod.CsLodStoreSink')
    check(sink, 'persist(com.kishku7.chunksmith.lod.CsLodChunk)', 'CsLodRegionStore.write:', 1)
    explored = read(args.chunksmith, 'com.kishku7.chunksmith.lod.ExploredLodGuard')
    check(explored, 'take(net.minecraft.server.level.ServerLevel', 'CsLodPresenceIndex.markLod:', 1)
    check(explored, 'onSave(net.minecraft.server.level.ServerLevel', 'LevelChunk.isUnsaved:', 1)
    method(world, 'lambda$getChunkAtAsync$11(net.minecraft.server.level.ChunkResult)')
    manager = read(args.millenaire, 'org.millenaire.village.VillageManager')
    method(manager, 'public void tick(net.minecraft.server.level.ServerLevel)')
    villager = read(args.millenaire, 'org.millenaire.entity.MillVillager')
    method(villager, 'public void tick()')
    rehydrator = read(args.millenaire, 'org.millenaire.village.VillageRehydrator')
    check(rehydrator, 'rehydrateAll(net.minecraft.server.level.ServerLevel)', 'Village.rebuildWaypointGraph:', 1)
    print('Upstream Mixin target bytecode: PASS (Chunksmith 4.8.1 / Millenaire 9.1.0)')


if __name__ == '__main__':
    main()
