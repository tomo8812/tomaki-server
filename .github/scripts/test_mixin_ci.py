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

    def run_audit(self, directory):
        report = directory / 'reports' / 'audit.json'
        summary = directory / 'summary.md'
        result = subprocess.run(
            [sys.executable, str(self.root / '.github/scripts/audit_mixin_annotations.py'),
             '--mods-dir', str(directory), '--json-output', str(report), '--github-actions'],
            env=dict(os.environ, GITHUB_STEP_SUMMARY=str(summary)),
            capture_output=True, text=True, encoding='utf-8')
        return result, json.loads(report.read_text()), summary.read_text(encoding='utf-8')

    def test_missing_class_is_failed_check_with_report(self):
        source = next((self.root / 'mods').glob('maki-lod-cpu-1.4.1.jar'))
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
        source = next((self.root / 'mods').glob('maki-lod-cpu-1.4.1.jar'))
        with tempfile.TemporaryDirectory() as tmp:
            directory = Path(tmp)
            (directory / source.name).write_bytes(source.read_bytes())
            (directory / 'maki-broken.jar').write_bytes(b'not a zip')
            result, report, summary = self.run_audit(directory)
            self.assertEqual(result.returncode, 1, result.stderr)
            self.assertGreaterEqual(len(report['classes']), 1)
            self.assertTrue(any('maki-broken.jar' in f for f in report['failures']))
            self.assertIn(source.name, summary)


if __name__ == '__main__':
    unittest.main()
