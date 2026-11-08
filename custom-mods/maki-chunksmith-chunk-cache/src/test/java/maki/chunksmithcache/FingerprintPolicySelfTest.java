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
            Path qolJar = temp.resolve("diagnostic-probe.jar");
            writeModJar(qolJar, "diagnostic_probe", "payload-a", false);
            Path chunksmithJar = temp.resolve("chunksmith-test.jar");
            writeModJar(chunksmithJar, "chunksmith", "payload-a", false);
            Path resourceWorldgenJar = temp.resolve("resource-worldgen.jar");
            writeModJar(resourceWorldgenJar, "resource_worldgen", "payload-a", true);
            Path customWorldgenJar = temp.resolve("custom-worldgen.jar");
            writeModJar(customWorldgenJar, "custom_worldgen", "payload-a", false);
            Path disablerJar = temp.resolve("disabler-neoforge-1.21.1-1.3.jar");
            writeModJar(disablerJar, "disabler", "payload-a", false);

            FingerprintPolicy policy = FingerprintPolicy.forTesting(List.of("config/diagnostic-probe.properties"));
            require(policy.classify("mods/diagnostic-probe.jar", qolJar) == FingerprintManifest.Impact.IGNORED,
                "unknown QoL/runtime MODs must not rotate generation compatibility by default");
            require(policy.classify("mods/chunksmith-test.jar", chunksmithJar) == FingerprintManifest.Impact.RELEVANT,
                "Chunksmith code remains generation-sensitive");
            require(policy.classify("mods/resource-worldgen.jar", resourceWorldgenJar) == FingerprintManifest.Impact.RELEVANT,
                "worldgen datapack resources inside a JAR must make it generation-sensitive");
            require(policy.classify("mods/disabler-neoforge-1.21.1-1.3.jar", disablerJar) == FingerprintManifest.Impact.RELEVANT,
                "Disabler code is generation-sensitive because it controls structure availability");
            require(policy.classify("config/disabler-server.json", temp.resolve("unused")) == FingerprintManifest.Impact.RELEVANT,
                "Disabler structure blocking config must rotate generated-result compatibility");
            require(policy.classify("config/random-qol.properties", temp.resolve("unused")) == FingerprintManifest.Impact.IGNORED,
                "ordinary config must not rotate generation compatibility");
            require(policy.classify("config/chunksmith/config.json", temp.resolve("unused")) == FingerprintManifest.Impact.IGNORED,
                "current Chunksmith scheduling/LOD task config must not rotate generated-result compatibility");
            require(policy.classify("config/millenaire-common.toml", temp.resolve("unused")) == FingerprintManifest.Impact.IGNORED,
                "Millenaire common logging config must not rotate generated-result compatibility");
            require(policy.classify("world/serverconfig/millenaire-server.toml", temp.resolve("unused")) == FingerprintManifest.Impact.RELEVANT,
                "Millenaire server generation config remains generation-sensitive");
            require(policy.classify("world/datapacks/test.zip", temp.resolve("unused")) == FingerprintManifest.Impact.RELEVANT,
                "world datapacks remain generation-sensitive");
            require(FingerprintPolicy.isAlwaysExcluded("config/chunksmith/tasks/minecraft/overworld.properties"),
                "Chunksmith task progress regression");

            FingerprintPolicy operatorPolicy = FingerprintPolicy.forTesting(
                Set.of("custom_worldgen"),
                List.of("config/custom-worldgen.properties"),
                List.of("world/serverconfig/millenaire-server.toml")
            );
            require(operatorPolicy.classify("mods/custom-worldgen.jar", customWorldgenJar) == FingerprintManifest.Impact.RELEVANT,
                "operator-declared code-driven worldgen MOD must be relevant");
            require(operatorPolicy.classify("config/custom-worldgen.properties", temp.resolve("unused")) == FingerprintManifest.Impact.RELEVANT,
                "operator-declared generation config must be relevant");
            require(operatorPolicy.classify("world/serverconfig/millenaire-server.toml", temp.resolve("unused")) == FingerprintManifest.Impact.RELEVANT,
                "built-in Millenaire generation config cannot be neutralized by ignore-paths");
            require(operatorPolicy.classify("config/disabler-server.json", temp.resolve("unused")) == FingerprintManifest.Impact.RELEVANT,
                "built-in Disabler structure policy cannot be neutralized by ignore-paths");
            require(operatorPolicy.classify("config/chunksmith/config.json", temp.resolve("unused")) == FingerprintManifest.Impact.IGNORED,
                "current Chunksmith operational config remains generation-neutral");

            List<FingerprintManifest.Entry> baseline = new ArrayList<>();
            baseline.add(entry("policy:compatibility", "policy", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("mods/chunksmith.jar", "wg-a", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("mods/diagnostic-probe.jar", "diag-a", FingerprintManifest.Impact.IGNORED));
            baseline.add(entry("config/diagnostic-probe.properties", "cfg-a", FingerprintManifest.Impact.IGNORED));
            baseline.add(entry("world/datapacks/test.zip", "data-a", FingerprintManifest.Impact.RELEVANT));
            baseline.add(entry("level:seed", "123", FingerprintManifest.Impact.RELEVANT));

            String base = CompatibilityFingerprint.compute(baseline);
            require(base.equals(CompatibilityFingerprint.compute(replace(baseline, "mods/diagnostic-probe.jar", "diag-b"))),
                "generation-neutral JAR change must not invalidate");
            require(base.equals(CompatibilityFingerprint.compute(replace(baseline, "config/diagnostic-probe.properties", "cfg-b"))),
                "generation-neutral config change must not invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "mods/chunksmith.jar", "wg-b"))),
                "worldgen MOD change must invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "world/datapacks/test.zip", "data-b"))),
                "datapack change must invalidate");
            require(!base.equals(CompatibilityFingerprint.compute(replace(baseline, "level:seed", "124"))),
                "seed change must invalidate");

            List<FingerprintManifest.Entry> oldPolicyEntries = new ArrayList<>();
            oldPolicyEntries.add(entry("policy:compatibility", "policy-v5", FingerprintManifest.Impact.RELEVANT));
            oldPolicyEntries.add(entry("mods/chunksmith.jar", "wg-a", FingerprintManifest.Impact.RELEVANT));
            oldPolicyEntries.add(entry("mods/diagnostic-probe.jar", "diag-old", FingerprintManifest.Impact.RELEVANT));
            oldPolicyEntries.add(entry("config/diagnostic-probe.properties", "cfg-old", FingerprintManifest.Impact.RELEVANT));
            oldPolicyEntries.add(entry("level:seed", "123", FingerprintManifest.Impact.RELEVANT));

            List<FingerprintManifest.Entry> newPolicyEntries = new ArrayList<>();
            newPolicyEntries.add(entry("policy:compatibility", "policy-v6", FingerprintManifest.Impact.RELEVANT));
            newPolicyEntries.add(entry("mods/chunksmith.jar", "wg-a", FingerprintManifest.Impact.RELEVANT));
            newPolicyEntries.add(entry("mods/diagnostic-probe.jar", "diag-new", FingerprintManifest.Impact.IGNORED));
            newPolicyEntries.add(entry("config/diagnostic-probe.properties", "cfg-new", FingerprintManifest.Impact.IGNORED));
            newPolicyEntries.add(entry("level:seed", "123", FingerprintManifest.Impact.RELEVANT));

            FingerprintManifest.Manifest oldPolicyManifest = manifest(oldPolicyEntries, 1L);
            FingerprintManifest.Manifest newPolicyManifest = manifest(newPolicyEntries, 2L);
            require(
                FingerprintManifest.sameCompatibilityInputsExcept(
                    oldPolicyManifest, newPolicyManifest, Set.of("policy:compatibility")
                ),
                "policy-v6 must re-adopt unchanged generation inputs when old QoL/config inputs are reclassified neutral"
            );
            List<FingerprintManifest.Entry> oldDisablerPolicy = new ArrayList<>(oldPolicyEntries);
            oldDisablerPolicy.add(entry("config/disabler-server.json", "villages-off", FingerprintManifest.Impact.IGNORED));
            List<FingerprintManifest.Entry> newDisablerPolicy = new ArrayList<>(newPolicyEntries);
            newDisablerPolicy.add(entry("config/disabler-server.json", "villages-off", FingerprintManifest.Impact.RELEVANT));
            require(
                FingerprintManifest.sameCompatibilityInputsExcept(
                    manifest(oldDisablerPolicy, 3L), manifest(newDisablerPolicy, 4L), Set.of("policy:compatibility")
                ),
                "unchanged historical Disabler config may migrate across its neutral-to-relevant policy correction"
            );
            require(
                !FingerprintManifest.sameCompatibilityInputsExcept(
                    manifest(oldDisablerPolicy, 5L),
                    manifest(replace(newDisablerPolicy, "config/disabler-server.json", "villages-on"), 6L),
                    Set.of("policy:compatibility")
                ),
                "changed Disabler structure policy must invalidate retained generation compatibility"
            );

            List<FingerprintManifest.Entry> changedWorldgen = replace(newPolicyEntries, "mods/chunksmith.jar", "wg-b");
            require(
                !FingerprintManifest.sameCompatibilityInputsExcept(
                    oldPolicyManifest, manifest(changedWorldgen, 3L), Set.of("policy:compatibility")
                ),
                "worldgen change must not be adopted as policy-equivalent"
            );

            FingerprintManifest.Manifest manifest = manifest(baseline, 4L);
            String encoded = FingerprintManifest.encode(manifest);
            FingerprintManifest.Manifest decoded = FingerprintManifest.decode(encoded);
            require(decoded.fingerprint().equals(manifest.fingerprint()) && decoded.entries().size() == baseline.size(),
                "manifest round-trip");
            boolean corruptRejected = false;
            try {
                FingerprintManifest.decode(encoded.replaceFirst("entry\\t[^\\t]+", "entry\t%%%"));
            } catch (IOException expected) {
                corruptRejected = true;
            }
            require(corruptRejected, "corrupt manifest must fail closed");

            writeModJar(qolJar, "diagnostic_probe", "payload-b", false);
            require(policy.classify("mods/diagnostic-probe.jar", qolJar) == FingerprintManifest.Impact.IGNORED,
                "ordinary QoL/runtime updates must remain generation-neutral");
            System.out.println("Issue #40/#79 fingerprint compatibility regression checks: PASS");
        } finally {
            deleteTree(temp);
        }
    }

    private static FingerprintManifest.Manifest manifest(List<FingerprintManifest.Entry> entries, long created) {
        String fingerprint = CompatibilityFingerprint.compute(entries);
        return new FingerprintManifest.Manifest(fingerprint, created, Set.of(fingerprint), entries);
    }

    private static FingerprintManifest.Entry entry(String key, String value, FingerprintManifest.Impact impact) {
        return new FingerprintManifest.Entry(
            key,
            value.length(),
            CompatibilityFingerprint.sha256(value),
            impact,
            impact == FingerprintManifest.Impact.RELEVANT ? "test relevant" : "test ignored"
        );
    }

    private static List<FingerprintManifest.Entry> replace(List<FingerprintManifest.Entry> source, String key, String value) {
        List<FingerprintManifest.Entry> result = new ArrayList<>();
        for (FingerprintManifest.Entry entry : source) {
            result.add(entry.key().equals(key) ? entry(key, value, entry.impact()) : entry);
        }
        return result;
    }

    private static void writeModJar(Path path, String modId, String payload, boolean worldgenResource) throws IOException {
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(path))) {
            jar.putNextEntry(new JarEntry("META-INF/neoforge.mods.toml"));
            jar.write(("modLoader=\"javafml\"\n[[mods]]\nmodId=\"" + modId
                + "\"\nversion=\"1\"\n[[dependencies." + modId + "]]\nmodId=\"minecraft\"\n")
                .getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
            jar.putNextEntry(new JarEntry("payload.txt"));
            jar.write(payload.getBytes(StandardCharsets.UTF_8));
            jar.closeEntry();
            if (worldgenResource) {
                jar.putNextEntry(new JarEntry("data/" + modId + "/worldgen/configured_feature/test.json"));
                jar.write("{}".getBytes(StandardCharsets.UTF_8));
                jar.closeEntry();
            }
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
