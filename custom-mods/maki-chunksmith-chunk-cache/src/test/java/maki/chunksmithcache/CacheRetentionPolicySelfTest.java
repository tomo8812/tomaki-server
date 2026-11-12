package maki.chunksmithcache;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/** Storage-retention settings only; no world or MOD compatibility monitoring. */
public final class CacheRetentionPolicySelfTest {
    private CacheRetentionPolicySelfTest() {}

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("cache-retention-test");
        try {
            CacheRetentionPolicy defaults = CacheRetentionPolicy.load(root);
            require(defaults.retentionDays() == 30, "retention default");
            require(defaults.maxGenerations() == 8, "max stored quarantines default");
            require(defaults.minProtectionHours() == 24, "protection default");

            Path config = root.resolve(CacheRetentionPolicy.CONFIG_RELATIVE);
            Files.createDirectories(config.getParent());
            Files.writeString(config,
                "quarantine.retention-days=14\n"
                + "quarantine.max-generations=5\n"
                + "quarantine.max-bytes=8388608\n"
                + "quarantine.min-protection-hours=48\n");
            CacheRetentionPolicy modified = CacheRetentionPolicy.load(root);
            require(modified.retentionDays() == 14, "explicit retention days");
            require(modified.maxGenerations() == 5, "explicit max quarantines");
            require(modified.maxBytes() == 8388608L, "explicit max bytes");
            require(modified.minProtectionHours() == 48, "explicit protection hours");
            require(Fingerprint.CACHE_ID.matches("[0-9a-f]{24}"),
                "fixed cache namespace must not depend on settings");

            Files.writeString(config, "quarantine.retention-days=invalid\n");
            require(CacheRetentionPolicy.load(root).retentionDays() == 30,
                "invalid retention setting falls back safely");
            System.out.println("Cache retention settings and stable FULL storage identity: PASS");
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(p);
            }
        }
    }

    private static void require(boolean success, String description) {
        if (!success) throw new AssertionError(description);
    }
}
