package maki.chunksmithcache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public final class FingerprintPolicySelfTest {
    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("maki-cache-issue40-test");
        try {
            Path observerJar = temp.resolve("diagnostic-probe.jar");
            writeModJar(observerJar, "diagnostic_probe", "payload-a");
            Path worldgenJar = temp.resolve("worldgen.jar");
            writeModJar(worldgenJar, "worldgen_mod", "payload-a");

            FingerprintPolicy policy = FingerprintPolicy.forTesting(List.of("config/diagnostic-probe.properties"));
            require(policy.classify("mods/diagnostic-probe.jar", observerJar) == FingerprintManifest.Impact.RELEVANT,
                "a mod id alone must not bypass compatibility checks; audited artifacts are normalized before policy fallback");
            require(policy.classify("mods/worldgen.jar", worldgenJar) == FingerprintManifest.Impact.RELEVANT, "unknown/worldgen mod must remain relevant");
            require(FingerprintPolicy.isAlwaysExcluded("config/chunksmith/tasks/minecraft/overworld.properties"), "Chunksmith task progress regression");

            List<FingerprintManifest.Entry> baseline = new ArrayList<>();
            baseline.add(entry("policy:compatibility", "policy", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("mods/worldgen.jar", "wg-a", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("mods/diagnostic-probe.jar", "diag-a", FingerprintManifest.Impact.IGNORED));
            baseline.add(entry("config/diagnostic-probe.properties", "cfg-a", FingerprintManifest.Impact.IGNORED));
            baseline.add(entry("world/datapacks/test.zip", "data-a", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("level:seed", "123", FingerprintManifest.Impact.RELEVANT));

            String base = CompatibilityFingerprint.compute(baseline);
            require(base.equals(CompatibilityFingerprint.compute(replace(baseline, "mods/diagnostic-probe.jar", "diag-b"))), "observation-only JAR change must not invalidate");
            require(base.equals(CompatibilityFingerprint.compute(replace(baseline, "config/diagnostic-probe.properties", "cfg-b"))), "explicit non-worldgen config change must not invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "mods/worldgen.jar", "wg-b"))), "worldgen mod change must invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "world/datapacks/test.zip", "data-b"))), "datapack change must invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "level:seed", "124"))), "seed change must invalidate");

            List<FingerprintManifest.Entry> renamedObserver = new ArrayList<>();
            renamedObserver.add(entry("policy:compatibility", "policy-renamed-observer", FingerprintManifest.Impact.RELEVANT));
            renamedObserver.add(entry("mods/worldgen.jar", "wg-a", FingerprintManifest.Impact.RELEVANT));
            renamedObserver.add(entry("mods/maki-mods-observer.jar", "diag-b", FingerprintManifest.Impact.IGNORED));
            renamedObserver.add(entry("config/maki-mods-observer.properties", "cfg-b", FingerprintManifest.Impact.IGNORED));
            renamedObserver.add(entry("world/datapacks/test.zip", "data-a", FingerprintManifest.Impact.RELEVANT));
            renamedObserver.add(entry("level:seed", "123", FingerprintManifest.Impact.RELEVANT));
            FingerprintManifest.Manifest oldPolicyManifest = new FingerprintManifest.Manifest(base, 1L, Set.of(base), baseline);
            FingerprintManifest.Manifest renamedPolicyManifest = new FingerprintManifest.Manifest(
                CompatibilityFingerprint.compute(renamedObserver), 2L, Set.of(), renamedObserver);
            require(
                FingerprintManifest.sameRelevantInputsExcept(oldPolicyManifest, renamedPolicyManifest, Set.of("policy:compatibility")),
                "diagnostic observer rename must preserve relevant cache inputs"
            );
            List<FingerprintManifest.Entry> changedWorldgen = replace(renamedObserver, "mods/worldgen.jar", "wg-b");
            require(
                !FingerprintManifest.sameRelevantInputsExcept(
                    oldPolicyManifest,
                    new FingerprintManifest.Manifest(CompatibilityFingerprint.compute(changedWorldgen), 3L, Set.of(), changedWorldgen),
                    Set.of("policy:compatibility")
                ),
                "worldgen change must not be adopted as policy-equivalent"
            );

            FingerprintManifest.Manifest manifest = new FingerprintManifest.Manifest(base, 1L, Set.of(base), baseline);
            String encoded = FingerprintManifest.encode(manifest);
            FingerprintManifest.Manifest decoded = FingerprintManifest.decode(encoded);
            require(decoded.fingerprint().equals(base) && decoded.entries().size() == baseline.size(), "manifest round-trip");
            boolean corruptRejected = false;
            try {
                FingerprintManifest.decode(encoded.replaceFirst("entry\\t[^\\t]+", "entry\t%%%"));
            } catch (IOException expected) {
                corruptRejected = true;
            }
            require(corruptRejected, "corrupt manifest must fail closed");

            writeModJar(observerJar, "diagnostic_probe", "payload-b");
            require(policy.classify("mods/diagnostic-probe.jar", observerJar) == FingerprintManifest.Impact.RELEVANT,
                "unknown changed bytes must remain relevant even when the mod id is unchanged");
            System.out.println("Issue #40 fingerprint/quarantine regression checks: PASS");
        } finally {
            deleteTree(temp);
        }
    }

    private static FingerprintManifest.Entry entry(String key, String value, FingerprintManifest.Impact impact) {
        return new FingerprintManifest.Entry(key, value.length(), CompatibilityFingerprint.sha256(value), impact, impact == FingerprintManifest.Impact.RELEVANT ? "test relevant" : "test ignored");
    }

    private static List<FingerprintManifest.Entry> replace(List<FingerprintManifest.Entry> source, String key, String value) {
        List<FingerprintManifest.Entry> result = new ArrayList<>();
        for (FingerprintManifest.Entry entry : source) {
            result.add(entry.key().equals(key) ? entry(key, value, entry.impact()) : entry);
        }
        return result;
    }

    private static void writeModJar(Path path, String modId, String payload) throws IOException {
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(path))) {
            jar.putNextEntry(new JarEntry("META-INF/neoforge.mods.toml"));
            jar.write(("modLoader=\"javafml\"\n[[mods]]\nmodId=\"" + modId + "\"\nversion=\"1\"\n[[dependencies." + modId + "]]\nmodId=\"minecraft\"\n").getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
            jar.putNextEntry(new JarEntry("payload.txt"));
            jar.write(payload.getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
        }
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) return;
        try (var stream = Files.walk(root)) {
            for (Path path : stream.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
