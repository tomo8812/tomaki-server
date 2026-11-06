import tempfile
from pathlib import Path
import unittest

from plan_mixin_ci import plan, projects_and_inputs


class BuildSelectionTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = Path(self.tmp.name)
        self.project('maki-api')
        self.project('maki-worker', "file('../maki-api/build/libs/maki-api-1.jar')\nfile('../../mods/api-1.jar')")
        self.project('maki-addon', "file('../maki-worker/build/libs/maki-worker-1.jar')")
        self.project('maki-independent')

    def project(self, name, text=''):
        path = self.root / 'custom-mods' / name / 'build.gradle'
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(text)

    def test_docs_only_skip_audit_tests_and_all_builds(self):
        result = plan(self.root, ['README.md', 'custom-mods/maki-worker/README.md', 'docs/guide.md'])
        self.assertEqual(result, {'projects': [], 'audit': False, 'tests': False, 'full': False})

    def test_independent_source_selects_only_owner(self):
        result = plan(self.root, ['custom-mods/maki-independent/src/main/java/Example.java'])
        self.assertEqual(result['projects'], ['maki-independent'])
        self.assertTrue(result['audit'])
        self.assertFalse(result['tests'])

    def test_changed_upstream_selects_transitive_consumers_in_order(self):
        result = plan(self.root, ['custom-mods/maki-api/src/main/java/Api.java'])
        self.assertEqual(result['projects'], ['maki-api', 'maki-worker', 'maki-addon'])

    def test_changed_binary_selects_its_consumers(self):
        self.assertEqual(plan(self.root, ['mods/api-1.jar'])['projects'], ['maki-worker', 'maki-addon'])
        self.assertEqual(plan(self.root, ['mods/unrelated.jar'])['projects'], [])
        self.assertTrue(plan(self.root, ['mods/unrelated.jar'])['audit'])

    def test_resources_and_tests_are_build_inputs(self):
        for path in ['src/main/resources/help.txt', 'src/main/resources/readme.md',
                     'src/test/java/Test.java', 'gradle.properties', 'build.gradle']:
            with self.subTest(path=path):
                self.assertEqual(plan(self.root, ['custom-mods/maki-independent/' + path])['projects'],
                                 ['maki-independent'])

    def test_new_project_is_discovered_automatically(self):
        self.project('maki-new')
        result = plan(self.root, ['custom-mods/maki-new/build.gradle'])
        self.assertEqual(result['projects'], ['maki-new'])

    def test_local_gradle_cache_is_not_read_as_a_build_file(self):
        (self.root / 'custom-mods/maki-api/.gradle').mkdir()
        self.assertEqual(len(projects_and_inputs(self.root)), 4)

    def test_shared_config_and_manual_dispatch_build_all(self):
        for paths, full in [(['.github/workflows/mixin-annotation-audit.yml'], False),
                            (['.github/scripts/plan_mixin_ci.py'], False), ([], True)]:
            result = plan(self.root, paths, full)
            self.assertEqual(set(result['projects']), set(projects_and_inputs(self.root)))
            self.assertTrue(result['audit'])
            self.assertTrue(result['tests'])

    def test_auditor_change_runs_checks_without_recompiling_mods(self):
        result = plan(self.root, ['.github/scripts/classfile_annotations.py'])
        self.assertEqual(result['projects'], [])
        self.assertTrue(result['audit'])
        self.assertTrue(result['tests'])

    def test_both_paths_of_rename_select_both_projects(self):
        # --no-renames returns deleted and added paths, so neither owner is lost.
        result = plan(self.root, ['custom-mods/maki-api/src/Old.java',
                                  'custom-mods/maki-independent/src/New.java'])
        self.assertEqual(set(result['projects']), set(projects_and_inputs(self.root)))

    def test_dependency_cycle_fails_instead_of_silently_skipping(self):
        self.project('maki-api', "file('../maki-addon/build/libs/maki-addon-1.jar')")
        with self.assertRaises(ValueError):
            plan(self.root, [], full=True)


if __name__ == '__main__':
    unittest.main()
