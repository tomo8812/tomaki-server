import importlib.util
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
import fcntl

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / 'scripts/remove-temporary-pregen-data.py'
spec = importlib.util.spec_from_file_location('remove_pregen_data', SCRIPT)
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


class CleanupTest(unittest.TestCase):
    def test_only_retired_data_is_deleted(self):
        with tempfile.TemporaryDirectory() as tmp:
            world = Path(tmp)
            kept = ['level.dat', 'region/r.0.0.mca', 'entities/r.0.0.mca', 'poi/r.0.0.mca',
                    'chunksmith/lod/minecraft_overworld/r.0.0.cslod', 'data/millenaire_villages.dat']
            removed = ['chunksmith_chunk_cache/old/full.nbt.gz',
                       'chunksmith_chunk_cache/.promotions/r.0.0/c.0.0.nbt.gz.in-use',
                       'data/maki_millenaire_lod_pending.dat', 'DIM-1/data/maki_millenaire_lod_pending.dat']
            for name in kept + removed:
                file = world / name
                file.parent.mkdir(parents=True, exist_ok=True)
                file.write_bytes(b'fixture')
            module.remove_data(world)
            for name in kept:
                self.assertEqual((world / name).read_bytes(), b'fixture', name)
            for name in removed:
                self.assertFalse((world / name).exists(), name)
            self.assertEqual(module.remove_data(world), [])

    def test_running_world_is_rejected(self):
        with tempfile.TemporaryDirectory() as tmp:
            world = Path(tmp)
            (world / 'level.dat').touch()
            with (world / 'session.lock').open('a+b') as lock:
                fcntl.lockf(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
                result = subprocess.run([sys.executable, str(SCRIPT), str(world)], capture_output=True)
                self.assertNotEqual(result.returncode, 0)
                self.assertIn(b'Stop the Minecraft server', result.stderr)


if __name__ == '__main__':
    unittest.main()
