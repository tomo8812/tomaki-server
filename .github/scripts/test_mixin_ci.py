"""Exercise CI failure reporting with real shipped JARs, without a server."""
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
import zipfile


class MixinCITest(unittest.TestCase):
    root = Path(__file__).resolve().parents[2]

    def run_audit(self, *directories):
        primary = directories[0]
        report = primary / 'reports' / 'audit.json'
        summary = primary / 'summary.md'
        cmd = [sys.executable, str(self.root / '.github/scripts/audit_mixin_annotations.py')]
        for directory in directories:
            cmd += ['--mods-dir', str(directory)]
        cmd += ['--json-output', str(report), '--github-actions']
        result = subprocess.run(
            cmd,
            env=dict(os.environ, GITHUB_STEP_SUMMARY=str(summary)),
            capture_output=True, text=True, encoding='utf-8')
        return result, json.loads(report.read_text()), summary.read_text(encoding='utf-8')

    def test_missing_class_is_failed_check_with_report(self):
        source = next((self.root / 'mods').glob('maki-chunksmith-controller-*.jar'))
        with tempfile.TemporaryDirectory() as tmp:
            directory = Path(tmp)
            with zipfile.ZipFile(source) as src, zipfile.ZipFile(directory / source.name, 'w') as dst:
                for entry in src.infolist():
                    if not entry.filename.endswith('/GenerationTaskMixin.class'):
                        dst.writestr(entry, src.read(entry))
            result, report, summary = self.run_audit(directory)
            self.assertEqual(result.returncode, 1, result.stderr)
            self.assertTrue(any('GenerationTaskMixin.class is missing' in f for f in report['failures']))
            self.assertIn('::error title=Mixin static audit::', result.stdout)
            self.assertIn('**FAIL**', summary)

    def test_empty_directory_cannot_pass(self):
        with tempfile.TemporaryDirectory() as tmp:
            result, report, summary = self.run_audit(Path(tmp))
            self.assertEqual(result.returncode, 2, result.stderr)
            self.assertEqual(report['jars'], [])
            self.assertIn('No maki-* JARs found', report['failures'][0])
            self.assertIn('**FAIL**', summary)

    def test_corrupt_jar_does_not_hide_valid_jar_results(self):
        source = next((self.root / 'mods').glob('maki-chunksmith-controller-*.jar'))
        with tempfile.TemporaryDirectory() as tmp:
            directory = Path(tmp)
            (directory / source.name).write_bytes(source.read_bytes())
            (directory / 'maki-broken.jar').write_bytes(b'not a zip')
            result, report, summary = self.run_audit(directory)
            self.assertEqual(result.returncode, 1, result.stderr)
            self.assertGreaterEqual(len(report['classes']), 1)
            self.assertTrue(any('maki-broken.jar' in f for f in report['failures']))
            self.assertIn(source.name, summary)

    def test_multiple_mods_dirs_deduplicate_shared_and_reject_mismatch(self):
        shared = next((self.root / 'mods').glob('maki-diorama-share-*.jar'))
        camera = next((self.root / 'client-mods').glob('maki-diorama-camera-*.jar'))
        with tempfile.TemporaryDirectory() as tmp:
            server_dir = Path(tmp) / 'mods'
            client_dir = Path(tmp) / 'client-mods'
            server_dir.mkdir()
            client_dir.mkdir()
            (server_dir / shared.name).write_bytes(shared.read_bytes())
            (client_dir / shared.name).write_bytes(shared.read_bytes())
            (client_dir / camera.name).write_bytes(camera.read_bytes())
            result, report, _ = self.run_audit(server_dir, client_dir)
            self.assertEqual(result.returncode, 0, result.stderr)
            self.assertEqual(report['jars'], [shared.name, camera.name])
            (client_dir / shared.name).write_bytes(b'mismatched')
            mismatch, bad_report, _ = self.run_audit(server_dir, client_dir)
            self.assertEqual(mismatch.returncode, 1)
            self.assertTrue(any('differs from' in f for f in bad_report['failures']))


if __name__ == '__main__':
    unittest.main()
