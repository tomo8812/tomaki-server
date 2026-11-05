import copy
import json
import subprocess
import tempfile
import unittest
import zipfile
from pathlib import Path

from audit_mixin_annotations import audit_jar, validate_api
from classfile_annotations import parse_class, walk_annotations


class MixinAuditTest(unittest.TestCase):
    def test_reserved_package_regression(self):
        root = Path(__file__).resolve().parents[2]
        source = next((root / 'mods').glob('maki-lod-cpu-online-fix-*.jar'))
        with tempfile.TemporaryDirectory() as tmp:
            target = Path(tmp) / source.name
            with zipfile.ZipFile(source) as src, zipfile.ZipFile(target, 'w') as dst:
                for entry in src.infolist():
                    data = src.read(entry)
                    if entry.filename == 'maki-lod-cpu-online-fix.mixins.json':
                        config = json.loads(data)
                        config['package'] = 'maki.lodcpu.onlinefix'
                        config['server'] = ['mixin.AdaptivePolicyMixin']
                        data = json.dumps(config).encode()
                    dst.writestr(entry, data)
            _, errors = audit_jar(target)
            self.assertTrue(any('MakiLodCpuOnlineFix.class' in e and 'reserved' in e for e in errors), errors)

    def test_classfile_annotations_unicode_nested_and_retention(self):
        # Compile a real fixture so constant-pool and nested attribute parsing
        # are checked independently of this reader's implementation.
        source = '''
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME) @interface At { String value(); }
@Retention(RetentionPolicy.RUNTIME) @interface Inject { At[] at(); String[] method(); }
@Retention(RetentionPolicy.CLASS) @interface Mixin { Class<?>[] value(); }
@Mixin({String.class}) class Fixture {
  static final String TEXT = "死亡履歴\\u0000😀";
  @Inject(at={@At("HEAD")}, method={"tick()V"}) void tick() {}
}
'''
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / 'Fixture.java'
            path.write_text(source, encoding='utf-8')
            subprocess.run(['javac', '-encoding', 'UTF-8', '-d', tmp, str(path)], check=True,
                           stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            cls = parse_class((Path(tmp) / 'Fixture.class').read_bytes())
            self.assertEqual(cls['annotations'][0]['descriptor'], 'LMixin;')
            self.assertFalse(cls['annotations'][0]['visible'])
            method = next(m for m in cls['methods'] if m['name'] == 'tick')
            annotations = list(walk_annotations(method['annotations']))
            self.assertEqual([a['descriptor'] for a in annotations], ['LInject;', 'LAt;'])
            self.assertTrue(all(a['visible'] for a in annotations))

    def test_abi_rejects_scalar_instead_of_array(self):
        desc = 'Lorg/spongepowered/asm/mixin/injection/Inject;'
        cls = {'annotations': [], 'fields': [], 'methods': [{'annotations': [
            {'descriptor': desc, 'visible': True,
             'values': {'method': {'kind': 's', 'value': 'tick()V'}}}]}]}
        api = {desc: {'annotations': [], 'methods': [
            {'name': 'method', 'descriptor': '()[Ljava/lang/String;'}]}}
        self.assertTrue(validate_api(cls, api))
        valid = copy.deepcopy(cls)
        valid['methods'][0]['annotations'][0]['values']['method'] = {
            'kind': '[', 'value': [{'kind': 's', 'value': 'tick()V'}]}
        self.assertEqual(validate_api(valid, api), [])


if __name__ == '__main__':
    unittest.main()
