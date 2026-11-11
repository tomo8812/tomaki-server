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

/** FULL cache compatibility inputs: terrain generation, not LOD/scheduling. */
public final class FingerprintPolicySelfTest {
    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("maki-full-compat-boundary");
        try {
            Path scheduler = temp.resolve("chunksmith.jar");
            writeModJar(scheduler, "chunksmith", "changed-scheduling", false);
            Path bridge = temp.resolve("millenaire.jar");
            writeModJar(bridge, "millenaire", "changed-village-queue", false);
            Path unknown = temp.resolve("utility.jar");
            writeModJar(unknown, "non_worldgen_utility", "ui-change", false);
            Path terrain = temp.resolve("worldgen.jar");
            writeModJar(terrain, "epicterrain", "terrain-change", false);
            Path dataWorldgen = temp.resolve("worldgen-data.jar");
            writeModJar(dataWorldgen, "new_terrain_provider", "terrain-change", true);

            FingerprintPolicy policy = FingerprintPolicy.forTesting(List.of());
            require(policy.classify("mods/chunksmith-4.8.1.jar", scheduler)
                == FingerprintManifest.Impact.IGNORED, "Chunksmith owns LOD/scheduling, not cached FULL validity");
            require(policy.classify("mods/millenaire-9.1.0.jar", bridge)
                == FingerprintManifest.Impact.IGNORED, "deferred village changes are not cached terrain changes");
            require(policy.classify("mods/utility.jar", unknown)
                == FingerprintManifest.Impact.IGNORED, "unrelated MOD changes must preserve FULLs");
            require(policy.classify("mods/worldgen.jar", terrain)
                == FingerprintManifest.Impact.RELEVANT, "known code-driven worldgen must remain relevant");
            require(policy.classify("mods/new-worldgen-data.jar", dataWorldgen)
                == FingerprintManifest.Impact.RELEVANT, "worldgen datapack resources must be relevant");
            require(policy.classify("config/jade-common.toml", unknown)
                == FingerprintManifest.Impact.IGNORED, "unrelated configuration is not a terrain input");
            require(policy.classify("config/epicterrain-options.toml", unknown)
                == FingerprintManifest.Impact.RELEVANT, "terrain config must stay relevant");
            require(FingerprintPolicy.isAlwaysExcluded("config/chunksmith/tasks/minecraft/overworld.properties"),
                "Chunksmith task progress never changes cached FULL compatibility");

            FingerprintPolicy manuallyConfigured = FingerprintPolicy.forTesting(
                Set.of("non_worldgen_utility"), List.of("config/custom-terrain.properties"), List.of());
            require(manuallyConfigured.classify("mods/utility.jar", unknown)
                == FingerprintManifest.Impact.RELEVANT,
                "code-only generator declared at installation must be a generation input");
            require(manuallyConfigured.classify("config/custom-terrain.properties", unknown)
                == FingerprintManifest.Impact.RELEVANT,
                "explicitly declared terrain option must be relevant");

            List<FingerprintManifest.Entry> old = new ArrayList<>(List.of(
                relevant("policy:compatibility", "old"),
                relevant("mods/chunksmith-4.6.0.jar", "old-scheduler"),
                relevant("mods/millenaire-9.0.3.jar", "old-village"),
                relevant("mods/other-utility.jar", "other"),
                relevant("config/jade-common.toml", "old-ui"),
                relevant("mods/epicterrain-0.3.0.jar", "terrain"),
                relevant("config/epicterrain-options.toml", "old-terrain-config"),
                relevant("level:seed", "42"),
                relevant("minecraft:data-version", "3955")
            ));
            List<FingerprintManifest.Entry> updated = new ArrayList<>(List.of(
                relevant("policy:compatibility", "new"),
                relevant("mods/chunksmith-4.8.1.jar", "new-scheduler"),
                relevant("mods/millenaire-9.1.0.jar", "new-village"),
                relevant("mods/another-utility.jar", "changed-ui"),
                relevant("config/jade-common.toml", "changed-ui"),
                relevant("mods/epicterrain-0.3.0.jar", "terrain"),
                relevant("config/epicterrain-options.toml", "old-terrain-config"),
                relevant("level:seed", "42"),
                relevant("minecraft:data-version", "3955")
            ));
            FingerprintManifest.Manifest source = manifest(old);
            FingerprintManifest.Manifest target = manifest(updated);
            require(FingerprintManifest.sameCompatibilityInputsExcept(
                source, target, Set.of("policy:compatibility")),
                "old overly broad manifests must adopt when only non-worldgen inputs changed");
            require(!FingerprintManifest.sameCompatibilityInputsExcept(
                source, manifest(replace(updated, "mods/epicterrain-0.3.0.jar", "changed-worldgen")),
                Set.of("policy:compatibility")), "real worldgen changes must not be adopted");
            require(!FingerprintManifest.sameCompatibilityInputsExcept(
                source, manifest(replace(updated, "minecraft:data-version", "4080")),
                Set.of("policy:compatibility")), "new Minecraft data version must block old FULL reuse");
            require(!FingerprintManifest.sameCompatibilityInputsExcept(
                source, manifest(replace(updated, "config/epicterrain-options.toml", "changed-terrain")),
                Set.of("policy:compatibility")), "terrain config change must block reuse");

            String encoded = FingerprintManifest.encode(source);
            FingerprintManifest.Manifest decoded = FingerprintManifest.decode(encoded);
            require(decoded.fingerprint().equals(source.fingerprint())
                && decoded.entries().size() == old.size(), "manifest round trip");
            boolean corruptRejected = false;
            try {
                FingerprintManifest.decode(encoded.replaceFirst("entry\\t[^\\t]+", "entry\t%%%"));
            } catch (IOException expected) { corruptRejected = true; }
            require(corruptRejected, "invalid manifest must not be accepted");

            System.out.println("Issue #40 FULL cache generation-input regression checks: PASS");
        } finally {
            try (var stream = Files.walk(temp)) {
                for (Path file : stream.sorted(java.util.Comparator.reverseOrder()).toList()) Files.deleteIfExists(file);
            }
        }
    }

    private static FingerprintManifest.Entry relevant(String key, String value) {
        return new FingerprintManifest.Entry(key, value.length(),
            CompatibilityFingerprint.sha256(value), FingerprintManifest.Impact.RELEVANT,
            "historical over-broad classification");
    }

    private static FingerprintManifest.Manifest manifest(List<FingerprintManifest.Entry> entries) {
        String fingerprint = CompatibilityFingerprint.compute(entries);
        return new FingerprintManifest.Manifest(fingerprint, 1L, Set.of(fingerprint), entries);
    }

    private static List<FingerprintManifest.Entry> replace(
        List<FingerprintManifest.Entry> entries, String key, String changed
    ) {
        List<FingerprintManifest.Entry> next = new ArrayList<>();
        for (FingerprintManifest.Entry entry : entries) {
            next.add(entry.key().equals(key) ? relevant(key, changed) : entry);
        }
        return next;
    }

    private static void writeModJar(Path path, String modId, String text,
                                    boolean worldgen) throws IOException {
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(path))) {
            out.putNextEntry(new JarEntry("META-INF/neoforge.mods.toml"));
            out.write(("modLoader=\"javafml\"\n[[mods]]\nmodId=\"" + modId
                + "\"\nversion=\"1\"\n").getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
            out.putNextEntry(new JarEntry(worldgen
                ? "data/fixture/worldgen/configured_feature/test.json" : "payload.txt"));
            out.write(text.getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
