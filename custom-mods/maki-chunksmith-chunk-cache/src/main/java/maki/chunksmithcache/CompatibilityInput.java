package maki.chunksmithcache;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

final class CompatibilityInput {
    static final String ZFASTNOISE_PATH = "config/zfastnoise.mixin.properties";
    static final String RAW_AUDIT_PREFIX = "audit:raw:";
    static final String HISTORICAL_EVIDENCE_DIR = ".compatibility-evidence";

    private static final List<String> ZFASTNOISE_KEYS = List.of("mixin.perf.biome", "mixin.perf.noise");
    private static final List<LegacyZfastnoiseAttestation> LEGACY_ZFASTNOISE = List.of(
        new LegacyZfastnoiseAttestation(
            76L,
            "802908edb8bb4c295a71796823e68320dc0e0b56f52f4d8db482eacc31bc6d3c",
            true,
            true,
            "Issue #68 verified legacy source"
        ),
        new LegacyZfastnoiseAttestation(
            76L,
            "fae3abedad69cc66a78764bb1ad837adc6d730c258af3604b40f4f68e0874fa6",
            true,
            true,
            "Issue #68 verified legacy source"
        ),
        new LegacyZfastnoiseAttestation(
            76L,
            "71c1fb9c36aaca148288a2049014bdd73222e031ca720410d5c87a8162325db5",
            true,
            true,
            "Issue #70 production source 2026-11-06T10:05:37Z"
        ),
        new LegacyZfastnoiseAttestation(
            76L,
            "e075ece367195f951c10ee54e87af5cf074fc43ef3b48e0d06d97c85acc66aa6",
            true,
            true,
            "Issue #70 production source 2026-11-06T13:18:17Z"
        )
    );

    private CompatibilityInput() {}

    static List<FingerprintManifest.Entry> entries(
        String key,
        Path file,
        long rawSize,
        String rawSha256,
        FingerprintPolicy policy
    ) {
        if (ZFASTNOISE_PATH.equals(key)) {
            return zfastnoiseEntries(key, file, rawSize, rawSha256);
        }

        if (key.startsWith("mods/") && key.endsWith(".jar")) {
            if (AuditedModCompatibility.declaresGenerationOverride(file)) {
                return List.of(new FingerprintManifest.Entry(
                    key,
                    rawSize,
                    rawSha256,
                    FingerprintManifest.Impact.RELEVANT,
                    "MOD declares an indirect zfastnoise generation override or its metadata cannot be safely inspected; fail-closed relevant"
                ));
            }
            FingerprintManifest.Impact classified = policy.classify(key, file);
            if (classified == FingerprintManifest.Impact.IGNORED) {
                // Keep the logical path in the manifest even though its bytes do not
                // contribute to the fingerprint. This lets a policy transition prove
                // that a formerly-relevant input is now intentionally neutral without
                // losing the source identity needed for migration/audit.
                return List.of(new FingerprintManifest.Entry(
                    key,
                    rawSize,
                    rawSha256,
                    FingerprintManifest.Impact.IGNORED,
                    policy.reason(key, file, classified)
                ));
            }
            List<FingerprintManifest.Entry> audited = AuditedModCompatibility.entries(file, rawSize, rawSha256, policy);
            if (audited != null) return audited;
        }

        FingerprintManifest.Impact impact = policy.classify(key, file);
        return List.of(new FingerprintManifest.Entry(key, rawSize, rawSha256, impact, policy.reason(key, file, impact)));
    }

    static String policySignature() {
        return "semantic-v3"
            + "|zfastnoise=" + String.join(",", ZFASTNOISE_KEYS)
            + "|" + AuditedModCompatibility.policySignature();
    }

    static FingerprintManifest.Entry normalizeHistoricalEntry(FingerprintManifest.Entry entry) {
        return normalizeHistoricalEntry(entry, null);
    }

    static FingerprintManifest.Entry normalizeHistoricalEntry(
        FingerprintManifest.Entry entry,
        Path historicalGeneration
    ) {
        String key = entry.key();
        if (key.startsWith(RAW_AUDIT_PREFIX)) return null;

        FingerprintManifest.Entry audited = AuditedModCompatibility.normalizeHistoricalEntry(entry);
        if (audited != entry) return audited;

        if (ZFASTNOISE_PATH.equals(key)) {
            LegacyZfastnoiseAttestation attestation = legacyZfastnoiseAttestation(entry);
            if (attestation != null) {
                return zfastnoiseCanonicalEntry(attestation.biome(), attestation.noise());
            }
            FingerprintManifest.Entry evidenced = normalizeZfastnoiseEvidence(entry, historicalGeneration);
            if (evidenced != null) return evidenced;
        }

        return entry;
    }

    static String historicalNormalizationNote(
        FingerprintManifest.Entry entry,
        Path historicalGeneration
    ) {
        FingerprintManifest.Entry audited = AuditedModCompatibility.normalizeHistoricalEntry(entry);
        if (audited != entry) {
            return entry.key() + "(verified legacy raw JAR -> audited compatibility contract " + audited.key() + ")";
        }
        if (!ZFASTNOISE_PATH.equals(entry.key())) return null;

        LegacyZfastnoiseAttestation attestation = legacyZfastnoiseAttestation(entry);
        if (attestation != null) {
            return entry.key() + "(verified legacy RAW -> effective values; " + attestation.evidence() + ")";
        }
        if (normalizeZfastnoiseEvidence(entry, historicalGeneration) != null) {
            return entry.key() + "(verified source-body evidence -> effective values)";
        }
        return null;
    }

    static String zfastnoiseDifferenceReason(
        FingerprintManifest.Entry historical,
        FingerprintManifest.Entry current,
        Path historicalGeneration
    ) {
        if (historical == null || current == null) return "(added/removed)";
        if (isCanonicalZfastnoiseEntry(historical) && isCanonicalZfastnoiseEntry(current)) {
            return "(effective values changed)";
        }
        FingerprintManifest.Entry normalized = normalizeHistoricalEntry(historical, historicalGeneration);
        if (normalized != historical) {
            return "(verified legacy raw evidence maps to different effective values)";
        }
        return "(legacy raw format; migration proof missing)";
    }

    static FingerprintManifest.Entry zfastnoiseCanonicalEntry(boolean biome, boolean noise) {
        String value = "mixin.perf.biome=" + biome + "\n"
            + "mixin.perf.noise=" + noise + "\n";
        return valueEntry(
            ZFASTNOISE_PATH,
            value,
            FingerprintManifest.Impact.RELEVANT,
            "zfastnoise effective properties; comments/order/whitespace and ignored keys do not affect compatibility"
        );
    }

    static boolean isCanonicalZfastnoiseEntry(FingerprintManifest.Entry entry) {
        if (entry == null || !ZFASTNOISE_PATH.equals(entry.key())) return false;
        for (boolean biome : List.of(false, true)) {
            for (boolean noise : List.of(false, true)) {
                FingerprintManifest.Entry candidate = zfastnoiseCanonicalEntry(biome, noise);
                if (sameIdentity(entry, candidate)) return true;
            }
        }
        return false;
    }

    static Path evidencePath(Path generationDirectory, String sha256) {
        return generationDirectory
            .resolve(HISTORICAL_EVIDENCE_DIR)
            .resolve("zfastnoise-" + sha256 + ".properties");
    }

    static void persistHistoricalEvidence(
        Path generationDirectory,
        Path serverDirectory,
        List<FingerprintManifest.Entry> entries
    ) throws Exception {
        FingerprintManifest.Entry raw = null;
        String rawKey = RAW_AUDIT_PREFIX + ZFASTNOISE_PATH;
        for (FingerprintManifest.Entry entry : entries) {
            if (rawKey.equals(entry.key())) {
                raw = entry;
                break;
            }
        }
        if (raw == null) return;

        Path source = serverDirectory.resolve(ZFASTNOISE_PATH);
        if (!Files.isRegularFile(source)) return;
        byte[] bytes = Files.readAllBytes(source);
        if (!matchesRaw(raw, bytes)) return;

        Path target = evidencePath(generationDirectory, raw.sha256());
        Files.createDirectories(target.getParent());
        if (Files.isRegularFile(target)) {
            byte[] existing = Files.readAllBytes(target);
            if (!matchesRaw(raw, existing)) {
                throw new IllegalStateException("historical zfastnoise evidence digest collision");
            }
            return;
        }
        try {
            Files.write(target, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (FileAlreadyExistsException ignored) {
            byte[] existing = Files.readAllBytes(target);
            if (!matchesRaw(raw, existing)) {
                throw new IllegalStateException("historical zfastnoise evidence changed concurrently");
            }
        }
    }

    private static List<FingerprintManifest.Entry> zfastnoiseEntries(String key, Path file, long rawSize, String rawSha256) {
        try (InputStream input = Files.newInputStream(file)) {
            FingerprintManifest.Entry canonical = parseZfastnoise(input);
            return List.of(
                canonical,
                new FingerprintManifest.Entry(
                    RAW_AUDIT_PREFIX + key,
                    rawSize,
                    rawSha256,
                    FingerprintManifest.Impact.IGNORED,
                    "zfastnoise raw source bytes for audit; compatibility uses effective boolean values"
                )
            );
        } catch (Throwable error) {
            return List.of(new FingerprintManifest.Entry(
                key,
                rawSize,
                rawSha256,
                FingerprintManifest.Impact.RELEVANT,
                "zfastnoise parse failed; raw bytes kept fail-closed (" + error.getClass().getSimpleName() + ")"
            ));
        }
    }

    private static FingerprintManifest.Entry parseZfastnoise(InputStream input) throws Exception {
        Properties properties = new Properties();
        properties.load(input);
        boolean biome = Boolean.parseBoolean(properties.getProperty("mixin.perf.biome", "true"));
        boolean noise = Boolean.parseBoolean(properties.getProperty("mixin.perf.noise", "true"));
        return zfastnoiseCanonicalEntry(biome, noise);
    }

    private static FingerprintManifest.Entry normalizeZfastnoiseEvidence(
        FingerprintManifest.Entry entry,
        Path historicalGeneration
    ) {
        if (historicalGeneration == null || !ZFASTNOISE_PATH.equals(entry.key())) return null;
        try {
            Path evidence = evidencePath(historicalGeneration, entry.sha256());
            if (!Files.isRegularFile(evidence)) return null;
            byte[] bytes = Files.readAllBytes(evidence);
            if (!matchesRaw(entry, bytes)) return null;
            try (InputStream input = new ByteArrayInputStream(bytes)) {
                return parseZfastnoise(input);
            }
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static LegacyZfastnoiseAttestation legacyZfastnoiseAttestation(FingerprintManifest.Entry entry) {
        if (!ZFASTNOISE_PATH.equals(entry.key())) return null;
        for (LegacyZfastnoiseAttestation attestation : LEGACY_ZFASTNOISE) {
            if (entry.size() == attestation.size() && entry.sha256().equals(attestation.sha256())) {
                return attestation;
            }
        }
        return null;
    }

    private static boolean matchesRaw(FingerprintManifest.Entry entry, byte[] bytes) {
        return entry.size() == bytes.length && entry.sha256().equals(sha256(bytes));
    }

    private static String sha256(byte[] bytes) {
        var digest = CompatibilityFingerprint.digest();
        digest.update(bytes);
        return CompatibilityFingerprint.hex(digest.digest());
    }

    private static boolean sameIdentity(FingerprintManifest.Entry a, FingerprintManifest.Entry b) {
        return a.key().equals(b.key())
            && a.size() == b.size()
            && a.sha256().equals(b.sha256())
            && a.impact() == b.impact();
    }

    private static FingerprintManifest.Entry valueEntry(
        String key,
        String value,
        FingerprintManifest.Impact impact,
        String reason
    ) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return new FingerprintManifest.Entry(key, bytes.length, CompatibilityFingerprint.sha256(value), impact, reason);
    }

    private record LegacyZfastnoiseAttestation(
        long size,
        String sha256,
        boolean biome,
        boolean noise,
        String evidence
    ) {}
}
