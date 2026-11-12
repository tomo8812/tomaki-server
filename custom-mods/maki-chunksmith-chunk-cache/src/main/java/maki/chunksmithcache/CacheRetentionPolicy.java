package maki.chunksmithcache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Retention controls for corrupt/incomplete historical cache files. */
final class CacheRetentionPolicy {
    static final String CONFIG_RELATIVE = "config/maki-chunksmith-chunk-cache.properties";
    private static final long DEFAULT_MAX_BYTES = 50L * 1024L * 1024L * 1024L;

    private final int retentionDays;
    private final int maxGenerations;
    private final long maxBytes;
    private final int minProtectionHours;

    private CacheRetentionPolicy(int retentionDays, int maxGenerations,
                                 long maxBytes, int minProtectionHours) {
        this.retentionDays = retentionDays;
        this.maxGenerations = maxGenerations;
        this.maxBytes = maxBytes;
        this.minProtectionHours = minProtectionHours;
    }

    static CacheRetentionPolicy load(Path serverDirectory) {
        Properties properties = new Properties();
        Path config = serverDirectory.resolve(CONFIG_RELATIVE);
        if (Files.isRegularFile(config)) {
            try (InputStream input = Files.newInputStream(config)) {
                properties.load(input);
            } catch (IOException ignored) {
                // Keep retention defaults; never block FULL cache access.
            }
        }
        return new CacheRetentionPolicy(
            boundedInt(properties, "quarantine.retention-days", 30, 1, 3650),
            boundedInt(properties, "quarantine.max-generations", 8, 1, 1024),
            boundedLong(properties, "quarantine.max-bytes", DEFAULT_MAX_BYTES, 1024L * 1024L, Long.MAX_VALUE),
            boundedInt(properties, "quarantine.min-protection-hours", 24, 0, 24 * 365)
        );
    }

    int retentionDays() { return retentionDays; }
    int maxGenerations() { return maxGenerations; }
    long maxBytes() { return maxBytes; }
    int minProtectionHours() { return minProtectionHours; }

    private static int boundedInt(Properties properties, String key, int fallback, int min, int max) {
        try {
            return Math.max(min, Math.min(max,
                Integer.parseInt(properties.getProperty(key, Integer.toString(fallback)).trim())));
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static long boundedLong(Properties properties, String key, long fallback, long min, long max) {
        try {
            return Math.max(min, Math.min(max,
                Long.parseLong(properties.getProperty(key, Long.toString(fallback)).trim())));
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }
}
