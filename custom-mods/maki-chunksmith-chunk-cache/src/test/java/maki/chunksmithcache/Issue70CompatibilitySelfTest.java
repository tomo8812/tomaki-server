package maki.chunksmithcache;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public final class Issue70CompatibilitySelfTest {
    private static final String CURRENT = "ff8a956818bf2099bd3d800b";
    private static final String OLD_5688 = "6934f9fb309d88c5b58107b4";
    private static final String OLD_921 = "c7d4f67f58c119cf434fc0b7";
    private static final String RAW_5688 = "71c1fb9c36aaca148288a2049014bdd73222e031ca720410d5c87a8162325db5";
    private static final String RAW_921 = "e075ece367195f951c10ee54e87af5cf074fc43ef3b48e0d06d97c85acc66aa6";
    private static final FingerprintPolicy POLICY = FingerprintPolicy.forTesting(List.of());

    private Issue70CompatibilitySelfTest() {}

    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("maki-cache-issue70-test");
        try {
            testProductionLegacyAttestations(temp);
            testSourceBodyEvidenceMigration(temp);
            testFailClosedDiagnostics(temp);
            testProductionScaleNonDestructiveMerge(temp);
            System.out.println("Issue #70 legacy compatibility migration regression checks: PASS");
        } finally {
            deleteTree(temp);
        }
    }

    private static void testProductionLegacyAttestations(Path temp) throws Exception {
        String raw5688 = "#\n#Fri Nov 06 10:05:37 UTC 2026\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        String raw921 = "#\n#Fri Nov 06 13:18:17 UTC 2026\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        byte[] bytes5688 = raw5688.getBytes(StandardCharsets.UTF_8);
        byte[] bytes921 = raw921.getBytes(StandardCharsets.UTF_8);

        require(bytes5688.length == 76 && RAW_5688.equals(sha256(bytes5688)),
            "5,688-entry production source must reproduce the manifest digest exactly");
        require(bytes921.length == 76 && RAW_921.equals(sha256(bytes921)),
            "921-entry production source must reproduce the manifest digest exactly");

        List<FingerprintManifest.Entry> currentEntries = List.of(
            CompatibilityInput.zfastnoiseCanonicalEntry(true, true),
            entry("mods/worldgen.jar", "same-worldgen")
        );
        FingerprintManifest.Manifest current = manifest(CURRENT, currentEntries);

        FingerprintManifest.Manifest old5688 = manifest(OLD_5688, List.of(
            rawZfastnoise(RAW_5688),
            entry("mods/worldgen.jar", "same-worldgen")
        ));
        FingerprintManifest.Manifest old921 = manifest(OLD_921, List.of(
            rawZfastnoise(RAW_921),
            entry("mods/worldgen.jar", "same-worldgen")
        ));

        require(FingerprintManifest.sameCompatibilityInputsExcept(old5688, current, Set.of()),
            "production 5,688-entry RAW manifest must normalize to true/true");
        require(FingerprintManifest.sameCompatibilityInputsExcept(old921, current, Set.of()),
            "production 921-entry RAW manifest must normalize to true/true");

        List<String> proof5688 = FingerprintManifest.compatibilityProofs(old5688, current, Set.of(), 8, null);
        List<String> proof921 = FingerprintManifest.compatibilityProofs(old921, current, Set.of(), 8, null);
        require(proof5688.stream().anyMatch(value -> value.contains("Issue #70 production source")),
            "5,688-entry migration must expose its verified production proof");
        require(proof921.stream().anyMatch(value -> value.contains("Issue #70 production source")),
            "921-entry migration must expose its verified production proof");

        List<FingerprintManifest.Entry> first = configEntries(temp, "prod-a.properties", raw5688);
        List<FingerprintManifest.Entry> second = configEntries(temp, "prod-b.properties", raw921);
        require(CompatibilityFingerprint.compute(first).equals(CompatibilityFingerprint.compute(second)),
            "production timestamp-only sources must compute the same compatibility fingerprint");
    }

    private static void testSourceBodyEvidenceMigration(Path temp) throws Exception {
        Path server = temp.resolve("future-server");
        Path generation = temp.resolve("future-generation");
        Files.createDirectories(server.resolve("config"));
        String source = "#\n#Sat Nov 07 02:03:04 UTC 2026\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        Path config = server.resolve(CompatibilityInput.ZFASTNOISE_PATH);
        Files.writeString(config, source, StandardCharsets.UTF_8);
        byte[] bytes = Files.readAllBytes(config);
        String digest = sha256(bytes);

        List<FingerprintManifest.Entry> observed = CompatibilityInput.entries(
            CompatibilityInput.ZFASTNOISE_PATH, config, bytes.length, digest, POLICY
        );
        CompatibilityInput.persistHistoricalEvidence(generation, server, observed);
        Path evidence = CompatibilityInput.evidencePath(generation, digest);
        require(Files.isRegularFile(evidence) && Files.readString(evidence).equals(source),
            "current raw source must be retained as exact digest-keyed migration evidence");

        FingerprintManifest.Manifest old = manifest("111111111111111111111111", List.of(
            new FingerprintManifest.Entry(
                CompatibilityInput.ZFASTNOISE_PATH,
                bytes.length,
                digest,
                FingerprintManifest.Impact.RELEVANT,
                "future legacy raw zfastnoise"
            )
        ));
        FingerprintManifest.Manifest current = manifest(CURRENT, List.of(
            CompatibilityInput.zfastnoiseCanonicalEntry(true, true)
        ));

        require(!FingerprintManifest.sameCompatibilityInputsExcept(old, current, Set.of()),
            "an unknown hash with no source-body evidence must remain fail-closed");
        require(FingerprintManifest.sameCompatibilityInputsExcept(old, current, Set.of(), generation),
            "an exact source-body proof must allow semantic migration without hard-coding its digest");
        require(FingerprintManifest.compatibilityProofs(old, current, Set.of(), 8, generation)
                .stream().anyMatch(value -> value.contains("verified source-body evidence")),
            "source-body migration must expose its proof type");

        Files.writeString(evidence, "tampered", StandardCharsets.UTF_8);
        require(!FingerprintManifest.sameCompatibilityInputsExcept(old, current, Set.of(), generation),
            "evidence whose bytes no longer match the stored manifest digest must be rejected");
    }

    private static void testFailClosedDiagnostics(Path temp) throws Exception {
        String unknown = "#\n#Sat Nov 07 05:06:07 UTC 2026\nmixin.perf.biome=true\nmixin.perf.noise=true\n";
        byte[] unknownBytes = unknown.getBytes(StandardCharsets.UTF_8);
        FingerprintManifest.Manifest unknownOld = manifest("222222222222222222222222", List.of(
            new FingerprintManifest.Entry(
                CompatibilityInput.ZFASTNOISE_PATH,
                unknownBytes.length,
                sha256(unknownBytes),
                FingerprintManifest.Impact.RELEVANT,
                "unknown legacy raw zfastnoise"
            )
        ));
        FingerprintManifest.Manifest current = manifest(CURRENT, List.of(
            CompatibilityInput.zfastnoiseCanonicalEntry(true, true)
        ));
        List<String> unknownChanged = FingerprintManifest.changedCompatibilityInputs(
            unknownOld, current, Set.of(), 8, null
        );
        require(unknownChanged.stream().anyMatch(value -> value.contains("migration proof missing")),
            "unknown legacy RAW must diagnose missing migration proof rather than an effective-value change");

        Path falseGeneration = temp.resolve("false-evidence");
        String falseSource = "#\n#Sat Nov 07 05:06:08 UTC 2026\nmixin.perf.biome=false\nmixin.perf.noise=true\n";
        byte[] falseBytes = falseSource.getBytes(StandardCharsets.UTF_8);
        String falseDigest = sha256(falseBytes);
        Path falseEvidence = CompatibilityInput.evidencePath(falseGeneration, falseDigest);
        Files.createDirectories(falseEvidence.getParent());
        Files.write(falseEvidence, falseBytes);

        FingerprintManifest.Manifest falseOld = manifest("333333333333333333333333", List.of(
            new FingerprintManifest.Entry(
                CompatibilityInput.ZFASTNOISE_PATH,
                falseBytes.length,
                falseDigest,
                FingerprintManifest.Impact.RELEVANT,
                "verified but different legacy zfastnoise"
            )
        ));
        require(!FingerprintManifest.sameCompatibilityInputsExcept(falseOld, current, Set.of(), falseGeneration),
            "verified legacy evidence with different effective values must remain incompatible");
        List<String> falseChanged = FingerprintManifest.changedCompatibilityInputs(
            falseOld, current, Set.of(), 8, falseGeneration
        );
        require(falseChanged.stream().anyMatch(value -> value.contains("different effective values")),
            "verified effective-value changes must be diagnosed separately from missing proof");

        FingerprintManifest.Manifest changedWorldgen = manifest(CURRENT, List.of(
            CompatibilityInput.zfastnoiseCanonicalEntry(true, true),
            entry("mods/worldgen.jar", "changed-worldgen")
        ));
        FingerprintManifest.Manifest sameLegacy = manifest(OLD_5688, List.of(
            rawZfastnoise(RAW_5688),
            entry("mods/worldgen.jar", "old-worldgen")
        ));
        require(!FingerprintManifest.sameCompatibilityInputsExcept(sameLegacy, changedWorldgen, Set.of()),
            "a verified zfastnoise migration must never hide a real worldgen change");
    }

    private static void testProductionScaleNonDestructiveMerge(Path temp) throws Exception {
        Path mergeRoot = temp.resolve("production-scale");
        Path active = mergeRoot.resolve(CURRENT);
        Path newer = mergeRoot.resolve("quarantine-newer");
        Path older = mergeRoot.resolve("quarantine-older");
        Files.createDirectories(active);
        Files.createDirectories(newer);
        Files.createDirectories(older);

        writeRange(active, 0, 234, "active");
        writeRange(newer, 200, 1120, "newer");
        writeRange(older, 881, 6568, "older");

        CacheDirectoryMerge.MergeStats newerStats = CacheDirectoryMerge.copyCacheEntriesPreservingSource(newer, active);
        CacheDirectoryMerge.MergeStats olderStats = CacheDirectoryMerge.copyCacheEntriesPreservingSource(older, active);

        require(newerStats.sourceEntries() == 921L && newerStats.adoptedEntries() == 886L
                && newerStats.duplicateEntries() == 35L,
            "921-entry generation must measure 886 adoptions and 35 collisions");
        require(olderStats.sourceEntries() == 5_688L && olderStats.adoptedEntries() == 5_448L
                && olderStats.duplicateEntries() == 240L,
            "5,688-entry generation must measure 5,448 adoptions and 240 collisions");
        require(countCaches(active) == 6_569L,
            "235 current + 6,609 historical files must produce 6,569 unique active coordinates");
        require(newerStats.adoptedEntries() + olderStats.adoptedEntries() == 6_334L,
            "historical re-adoption must add exactly 6,334 unique coordinates");
        require(newerStats.duplicateEntries() + olderStats.duplicateEntries() == 275L,
            "historical merge must report all 275 duplicate coordinate occurrences");
        require(countCaches(newer) == 921L && countCaches(older) == 5_688L,
            "automatic recovery must preserve both quarantine sources");
        require(Files.readString(cache(active, 200)).equals("active-200"),
            "current active cache must win collisions against historical generations");
        require(Files.readString(cache(active, 900)).equals("newer-900"),
            "newer historical generation must win collisions against the older generation");

        FingerprintManifest.Manifest activeManifest = new FingerprintManifest.Manifest(
            CURRENT,
            1L,
            Set.of(CURRENT, OLD_5688, OLD_921),
            List.of(CompatibilityInput.zfastnoiseCanonicalEntry(true, true))
        );
        FingerprintManifest.writeAtomic(active.resolve(FingerprintManifest.FILE_NAME), activeManifest);
        require(StoredFingerprintAcceptance.accepts(active, CURRENT, OLD_5688),
            "old 5,688-generation NBT fingerprint must be accepted after manifest adoption");
        require(StoredFingerprintAcceptance.accepts(active, CURRENT, OLD_921),
            "old 921-generation NBT fingerprint must be accepted after manifest adoption");
        require(!StoredFingerprintAcceptance.accepts(active, CURRENT, "444444444444444444444444"),
            "unadopted stored fingerprints must remain rejected");

        Path missingManifest = mergeRoot.resolve("missing-manifest");
        Files.createDirectories(missingManifest);
        require(!StoredFingerprintAcceptance.accepts(missingManifest, CURRENT, OLD_5688),
            "manifest-missing data must not gain stored-fingerprint trust");
    }

    private static List<FingerprintManifest.Entry> configEntries(Path temp, String name, String content) throws Exception {
        Path file = temp.resolve(name);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        byte[] bytes = Files.readAllBytes(file);
        return CompatibilityInput.entries(
            CompatibilityInput.ZFASTNOISE_PATH, file, bytes.length, sha256(bytes), POLICY
        );
    }

    private static FingerprintManifest.Entry rawZfastnoise(String digest) {
        return new FingerprintManifest.Entry(
            CompatibilityInput.ZFASTNOISE_PATH,
            76L,
            digest,
            FingerprintManifest.Impact.RELEVANT,
            "legacy raw zfastnoise"
        );
    }

    private static FingerprintManifest.Entry entry(String key, String value) {
        return new FingerprintManifest.Entry(
            key,
            value.length(),
            CompatibilityFingerprint.sha256(value),
            FingerprintManifest.Impact.RELEVANT,
            "test"
        );
    }

    private static FingerprintManifest.Manifest manifest(
        String fingerprint,
        List<FingerprintManifest.Entry> entries
    ) {
        return new FingerprintManifest.Manifest(fingerprint, 1L, Set.of(fingerprint), entries);
    }

    private static void writeRange(Path directory, int first, int last, String prefix) throws Exception {
        for (int i = first; i <= last; i++) {
            Path file = cache(directory, i);
            Files.createDirectories(file.getParent());
            Files.writeString(file, prefix + "-" + i, StandardCharsets.UTF_8);
        }
    }

    private static Path cache(Path directory, int x) {
        return directory.resolve("r.0.0").resolve("c." + x + ".0.nbt.gz");
    }

    private static long countCaches(Path directory) throws Exception {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".nbt.gz"))
                .count();
        }
    }

    private static String sha256(byte[] bytes) {
        MessageDigest digest = CompatibilityFingerprint.digest();
        digest.update(bytes);
        return CompatibilityFingerprint.hex(digest.digest());
    }

    private static void deleteTree(Path root) throws Exception {
        if (!Files.exists(root)) return;
        try (Stream<Path> stream = Files.walk(root)) {
            for (Path path : stream.sorted(java.util.Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
