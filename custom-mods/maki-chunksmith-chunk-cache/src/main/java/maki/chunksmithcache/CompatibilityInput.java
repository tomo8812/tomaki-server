package maki.chunksmithcache;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

final class CompatibilityInput {
    static final String ZFASTNOISE_PATH = "config/zfastnoise.mixin.properties";
    static final String RAW_AUDIT_PREFIX = "audit:raw:";

    private static final List<String> ZFASTNOISE_KEYS = List.of("mixin.perf.biome", "mixin.perf.noise");

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
            List<FingerprintManifest.Entry> audited = AuditedModCompatibility.entries(file, rawSize, rawSha256);
            if (audited != null) return audited;
        }

        FingerprintManifest.Impact impact = policy.classify(key, file);
        return List.of(new FingerprintManifest.Entry(key, rawSize, rawSha256, impact, policy.reason(key, file, impact)));
    }

    static String policySignature() {
        return "semantic-v2"
            + "|zfastnoise=" + String.join(",", ZFASTNOISE_KEYS)
            + "|" + AuditedModCompatibility.policySignature();
    }

    static FingerprintManifest.Entry normalizeHistoricalEntry(FingerprintManifest.Entry entry) {
        String key = entry.key();
        if (key.startsWith(RAW_AUDIT_PREFIX)) return null;

        FingerprintManifest.Entry audited = AuditedModCompatibility.normalizeHistoricalEntry(entry);
        if (audited != entry) return audited;

        if (ZFASTNOISE_PATH.equals(key) && isKnownLegacyZfastnoiseTrueTrue(entry)) {
            return zfastnoiseCanonicalEntry(true, true);
        }

        return entry;
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

    private static List<FingerprintManifest.Entry> zfastnoiseEntries(String key, Path file, long rawSize, String rawSha256) {
        try {
            Properties properties = new Properties();
            try (InputStream input = Files.newInputStream(file)) {
                properties.load(input);
            }
            boolean biome = Boolean.parseBoolean(properties.getProperty("mixin.perf.biome", "true"));
            boolean noise = Boolean.parseBoolean(properties.getProperty("mixin.perf.noise", "true"));
            return List.of(
                zfastnoiseCanonicalEntry(biome, noise),
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

    private static boolean isKnownLegacyZfastnoiseTrueTrue(FingerprintManifest.Entry entry) {
        if (entry.size() != 76L) return false;
        return entry.sha256().equals("802908edb8bb4c295a71796823e68320dc0e0b56f52f4d8db482eacc31bc6d3c")
            || entry.sha256().equals("fae3abedad69cc66a78764bb1ad837adc6d730c258af3604b40f4f68e0874fa6");
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
}
