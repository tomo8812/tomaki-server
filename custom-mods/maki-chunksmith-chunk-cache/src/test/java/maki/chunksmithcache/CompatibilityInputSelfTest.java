package maki.chunksmithcache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Effective current terrain config and compatibility with already stored FULLs. */
public final class CompatibilityInputSelfTest {
    private static final FingerprintPolicy POLICY = FingerprintPolicy.forTesting(List.of());

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("maki-full-terrain-config");
        try {
            testZfastnoise(root);
            testHistoricalZfastnoise(root);
            testConflictSafeMerge(root);
            System.out.println("FULL cache effective terrain configuration checks: PASS");
        } finally {
            try (var stream = Files.walk(root)) {
                for (Path file : stream.sorted(java.util.Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(file);
                }
            }
        }
    }

    private static void testZfastnoise(Path root) throws Exception {
        String original = "# test\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        String equivalent = "# updated formatting\nmixin.perf.noise = true\n"
            + "unrelated.key=x\nmixin.perf.biome : true\n";
        String changed = "mixin.perf.biome=false\nmixin.perf.noise=true\n";
        List<FingerprintManifest.Entry> old = configEntries(root, "a", CompatibilityInput.ZFASTNOISE_PATH, original);
        List<FingerprintManifest.Entry> same = configEntries(root, "b", CompatibilityInput.ZFASTNOISE_PATH, equivalent);
        List<FingerprintManifest.Entry> different = configEntries(root, "c", CompatibilityInput.ZFASTNOISE_PATH, changed);
        require(CompatibilityFingerprint.compute(old).equals(CompatibilityFingerprint.compute(same)),
            "comments and ordering must not invalidate unchanged effective terrain settings");
        require(!CompatibilityFingerprint.compute(old).equals(CompatibilityFingerprint.compute(different)),
            "real zfastnoise terrain setting change must invalidate FULLs");
        require(FingerprintManifest.sameCompatibilityInputsExcept(
            manifest(old), manifest(same), Set.of()), "equivalent effective configuration must be reused");
        require(!FingerprintManifest.sameCompatibilityInputsExcept(
            manifest(old), manifest(different), Set.of()), "changed terrain setting must be rejected");

        List<FingerprintManifest.Entry> uiBefore = configEntries(
            root, "ui-before", "config/unknown-ui.properties", "display=true\n");
        List<FingerprintManifest.Entry> uiAfter = configEntries(
            root, "ui-after", "config/unknown-ui.properties", "display=false\n");
        require(CompatibilityFingerprint.compute(uiBefore).equals(CompatibilityFingerprint.compute(uiAfter)),
            "general non-generation configuration must not rotate FULL cache");
    }

    private static void testHistoricalZfastnoise(Path root) throws Exception {
        String oldSource = "#\n#Fri Nov 06 10:05:37 UTC 2026\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        byte[] bytes = oldSource.getBytes(StandardCharsets.UTF_8);
        String digest = CompatibilityFingerprint.sha256(bytes);
        FingerprintManifest.Entry old = new FingerprintManifest.Entry(
            CompatibilityInput.ZFASTNOISE_PATH, bytes.length, digest,
            FingerprintManifest.Impact.RELEVANT, "old raw zfastnoise properties");
        FingerprintManifest.Manifest historical = manifest(List.of(old));
        FingerprintManifest.Manifest current = manifest(List.of(
            CompatibilityInput.zfastnoiseCanonicalEntry(true, true)));
        require(FingerprintManifest.sameCompatibilityInputsExcept(historical, current, Set.of()),
            "already generated FULL with a known equivalent raw config must remain reusable");

        Path archive = root.resolve("historic-generation");
        Path evidence = CompatibilityInput.evidencePath(archive, digest);
        Files.createDirectories(evidence.getParent());
        Files.write(evidence, bytes);
        require(FingerprintManifest.sameCompatibilityInputsExcept(
            historical, current, Set.of(), archive),
            "historical source bytes are sufficient to establish equivalent terrain settings");
    }

    private static void testConflictSafeMerge(Path root) throws Exception {
        Path source = root.resolve("old-generation");
        Path active = root.resolve("new-generation");
        Files.createDirectories(source);
        Files.createDirectories(active);
        Files.writeString(source.resolve("existing.nbt.gz"), "historical");
        Files.writeString(active.resolve("existing.nbt.gz"), "current");
        Files.writeString(source.resolve("unique.nbt.gz"), "reusable");

        CacheDirectoryMerge.merge(source, active);
        require("current".equals(Files.readString(active.resolve("existing.nbt.gz"))),
            "historical conflict must not overwrite existing FULL");
        require("reusable".equals(Files.readString(active.resolve("unique.nbt.gz"))),
            "non-conflicting FULL should be adopted");
    }

    private static List<FingerprintManifest.Entry> configEntries(
        Path root, String filename, String key, String content
    ) throws Exception {
        Path path = root.resolve(filename);
        Files.writeString(path, content, StandardCharsets.UTF_8);
        byte[] bytes = Files.readAllBytes(path);
        return CompatibilityInput.entries(key, path, bytes.length,
            CompatibilityFingerprint.sha256(bytes), POLICY);
    }

    private static FingerprintManifest.Manifest manifest(List<FingerprintManifest.Entry> entries) {
        String fingerprint = CompatibilityFingerprint.compute(entries);
        return new FingerprintManifest.Manifest(fingerprint, 1L, Set.of(fingerprint), entries);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
