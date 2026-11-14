#!/usr/bin/env python3
"""Remove retired temporary FULL/candidate data from a stopped Minecraft world."""
import argparse
import fcntl
from pathlib import Path
import shutil


def remove_data(world: Path) -> list[Path]:
    world = world.resolve(strict=True)
    if not (world / 'level.dat').is_file():
        raise ValueError(f'Not a Minecraft world: {world}')
    removed = []
    # Java's world session lock uses the same POSIX record lock. Hold it across
    # deletion so a live server cannot recreate cache entries during the switch.
    with (world / 'session.lock').open('a+b') as lock:
        try:
            fcntl.lockf(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError as error:
            raise RuntimeError('Stop the Minecraft server before removing temporary data') from error
        cache = world / 'chunksmith_chunk_cache'
        if cache.exists():
            shutil.rmtree(cache)
            removed.append(cache)
        for file in world.rglob('maki_millenaire_lod_pending.dat'):
            if file.parent.name == 'data':
                file.unlink()
                removed.append(file)
    return removed


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('world', type=Path, help='World directory containing level.dat; server must be stopped')
    args = parser.parse_args()
    for file in remove_data(args.world):
        print(f'Removed {file}')
    print('Temporary pregen data cleanup complete. Normal chunks, villages and Chunksmith LOD were preserved.')


if __name__ == '__main__':
    main()
