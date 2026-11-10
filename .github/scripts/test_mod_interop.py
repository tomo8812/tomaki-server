"""Regression fixtures for raw bytecode Mixin target validation."""
from pathlib import Path
import subprocess
import tempfile
import unittest

from check_mod_interop import class_bytecode, method_matches


class ModInteropTest(unittest.TestCase):
    def test_run_and_async_lambda_are_different_bytecode_methods(self):
        source = """
            package fixture;
            import java.util.concurrent.CompletableFuture;
            public final class PairTask {
                static final class Index {
                    boolean hasLod(int x, int z) { return x == z; }
                }
                private final Index index = new Index();
                public boolean run() {
                    return index.hasLod(1, 2) || index.hasLod(3, 4);
                }
                public CompletableFuture<Boolean> async() {
                    return CompletableFuture.completedFuture(Boolean.TRUE)
                        .thenApply(value -> index.hasLod(5, 6));
                }
                public String modifiedUtf8() {
                    return "\\u0000";
                }
                public int branching(int value) {
                    switch(value) {
                        case 1: return 7;
                        case 2: return 8;
                        default: return 9;
                    }
                }
            }
        """
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            folder = root / "fixture"
            folder.mkdir()
            file = folder / "PairTask.java"
            file.write_text(source, encoding="utf-8")
            subprocess.run(["javac", "-d", str(root), str(file)], check=True, capture_output=True)
            parsed = class_bytecode((folder / "PairTask.class").read_bytes())

        call = ("fixture/PairTask$Index", "hasLod", "(II)Z")
        run = next(m for m in parsed["methods"] if m["name"] == "run")
        self.assertEqual(run["calls"].count(call), 2)
        # The original failure was ordinal=2 in run(), though the third
        # invocation belonged to a compiler-generated lambda method.
        self.assertFalse(run["calls"].count(call) > 2)
        lambdas = [m for m in parsed["methods"] if m["name"].startswith("lambda$async$")]
        self.assertEqual(sum(m["calls"].count(call) for m in lambdas), 1)
        self.assertFalse(any(method_matches("lambda$async$0", m) for m in parsed["methods"]
                             if m["name"] == "run"))
        self.assertTrue(any(method_matches("run()Z", m) for m in parsed["methods"]))
        self.assertFalse(any(method_matches("run()V", m) for m in parsed["methods"]))

    def test_missing_methods_are_not_silently_matched(self):
        methods = [{"name": "run", "descriptor": "()V"}]
        self.assertTrue(method_matches("run()V", methods[0]))
        self.assertFalse(method_matches("run(I)V", methods[0]))
        self.assertFalse(method_matches("lambda$run$1", methods[0]))


if __name__ == "__main__":
    unittest.main()
