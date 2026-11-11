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

/**
 * Only the deployed server's known terrain MODs are compatibility inputs.
 * Newly added generators are declared by changing this policy at install time.
 */
public final class FingerprintPolicySelfTest {
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("maki-full-cache-current-mods");
        try {
            Path jar = root.resolve("fixture.jar");
            writeModJar(jar, "arbitrary_future_mod", true);
            FingerprintPolicy policy = FingerprintPolicy.forTesting(List.of());

            require(policy.classify("mods/new-unknown-generator.jar", jar)
                == FingerprintManifest.Impact.IGNORED,
                "unknown or future MODs are not preemptively treated as FULL terrain inputs");
            require(policy.classify("mods/chunksmith-4.8.1+1.21.1-neoforge.jar", jar)
                == FingerprintManifest.Impact.IGNORED,
                "native LOD/Chunksmith compatibility belongs to Chunksmith");
            require(policy.classify("mods/millenaire-9.1.0.jar", jar)
                == FingerprintManifest.Impact.IGNORED,
                "deferred village scheduling does not invalidate stored FULL terrain");
            require(policy.classify("mods/epicterrain-0.3.0-1.21.1.jar", jar)
                == FingerprintManifest.Impact.RELEVANT, "installed EpicTerrain is terrain input");
            require(policy.classify("mods/letsdo-farm_and_charm-neoforge-1.1.29.jar", jar)
                == FingerprintManifest.Impact.RELEVANT, "installed Farm and Charm contributes worldgen");
            require(policy.classify("mods/architectury-13.0.11-neoforge.jar", jar)
                == FingerprintManifest.Impact.IGNORED, "Architectury is an API library, not a terrain generator");
            require(policy.classify("config/jade-common.toml", jar)
                == FingerprintManifest.Impact.IGNORED, "ordinary config changes must preserve FULL");
            require(policy.classify("config/epicterrain-config.toml", jar)
                == FingerprintManifest.Impact.RELEVANT, "terrain generator config change must invalidate");
            require(policy.classify("config/zfastnoise.mixin.properties", jar)
                == FingerprintManifest.Impact.RELEVANT, "effective terrain noise settings must be monitored");
            require(policy.classify("world/datapacks/current/pack.mcmeta", jar)
                == FingerprintManifest.Impact.RELEVANT, "world datapacks must be monitored");
            require(FingerprintPolicy.isAlwaysExcluded("config/chunksmith/tasks/minecraft/overworld.properties"),
                "Chunksmith task progress must never invalidate FULL");

            List<FingerprintManifest.Entry> oldEntries = baseline();
            List<FingerprintManifest.Entry> updatedEntries = new ArrayList<>(oldEntries);
            updatedEntries.set(0, relevant("mods/epicterrain-0.3.0-1.21.1.jar", "changed-terrain"));
            require(!FingerprintManifest.sameCompatibilityInputsExcept(
                manifest(oldEntries), manifest(updatedEntries), Set.of("policy:compatibility")),
                "terrain generator change must not be adopted");
            updatedEntries = new ArrayList<>(oldEntries);
            updatedEntries.add(relevant("mods/letsdo-candlelight-neoforge-2.1.15.jar", "new-generation-input"));
            require(!FingerprintManifest.sameCompatibilityInputsExcept(
                manifest(oldEntries), manifest(updatedEntries), Set.of("policy:compatibility")),
                "installing a currently recognized terrain generator must change compatibility");

            String encoded = FingerprintManifest.encode(manifest(oldEntries));
            FingerprintManifest.Manifest decoded = FingerprintManifest.decode(encoded);
            require(decoded.entries().size() == oldEntries.size(), "manifest serialization");
            boolean corruptRejected = false;
            try {
                FingerprintManifest.decode(encoded.replaceFirst("entry\\t[^\\t]+", "entry\t%%%"));
            } catch (IOException expected) { corruptRejected = true; }
            require(corruptRejected, "malformed manifest must reject");
            System.out.println("Issue #40 current terrain-MOD boundary and manifest tests: PASS");
        } finally {
            try (var stream = Files.walk(root)) {
                for (Path path : stream.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static List<FingerprintManifest.Entry> baseline() {
        return List.of(
            relevant("mods/epicterrain-0.3.0-1.21.1.jar", "original-terrain"),
            relevant("minecraft:data-version", "3955"),
            relevant("level:seed", "123"),
            relevant("world/datapacks/current/pack.mcmeta", "old-datapack")
        );
    }

    private static FingerprintManifest.Entry relevant(String key, String value) {
        return new FingerprintManifest.Entry(key, value.length(),
            CompatibilityFingerprint.sha256(value), FingerprintManifest.Impact.RELEVANT,
            "known FULL terrain generation input");
    }

    private static FingerprintManifest.Manifest manifest(List<FingerprintManifest.Entry> entries) {
        String fingerprint = CompatibilityFingerprint.compute(entries);
        return new FingerprintManifest.Manifest(fingerprint, 1L, Set.of(fingerprint), entries);
    }

    private static void writeModJar(Path path, String id, boolean withWorldgenData) throws IOException {
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(path))) {
            out.putNextEntry(new JarEntry("META-INF/neoforge.mods.toml"));
            out.write(("modLoader=\"javafml\"\n[[mods]]\nmodId=\"" + id + "\"\n")
                .getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
            if (withWorldgenData) {
                out.putNextEntry(new JarEntry("data/fixture/worldgen/configured_feature/feature.json"));
                out.write("{}".getBytes(StandardCharsets.UTF_8));
                out.closeEntry();
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
