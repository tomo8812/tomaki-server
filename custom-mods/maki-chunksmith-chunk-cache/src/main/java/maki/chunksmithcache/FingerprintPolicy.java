package maki.chunksmithcache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

final class FingerprintPolicy {
    static final String CONFIG_RELATIVE = "config/maki-chunksmith-chunk-cache.properties";
    private static final long DEFAULT_MAX_BYTES = 50L * 1024L * 1024L * 1024L;

    // Current deployment's generation-affecting MODs. When
    // installing another generation MOD, update this code as part of that change.
    private static final List<String> BUILTIN_GENERATION_SENSITIVE_JAR_PREFIXES = List.of(
        "zfastnoise-", "epicterrain-", "worldgenfeaturefix-", "terrain_slabs-", "disabler-",
        "letsdo-bakery-", "letsdo-brewery-", "letsdo-candlelight-",
        "letsdo-farm_and_charm-", "letsdo-herbalbrews-"
    );
    private static final List<String> BUILTIN_GENERATION_SENSITIVE_PATHS = List.of(
        "config/zfastnoise.mixin.properties",
        "config/disabler-server.json",
        "config/epicterrain*",
        "serverconfig/epicterrain*",
        "world/serverconfig/epicterrain*",
        "config/terrain_slabs*",
        "serverconfig/terrain_slabs*",
        "world/serverconfig/terrain_slabs*"
    );

    private final int retentionDays;
    private final int maxGenerations;
    private final long maxBytes;
    private final int minProtectionHours;

    private FingerprintPolicy(
        int retentionDays,
        int maxGenerations,
        long maxBytes,
        int minProtectionHours
    ) {
        this.retentionDays = retentionDays;
        this.maxGenerations = maxGenerations;
        this.maxBytes = maxBytes;
        this.minProtectionHours = minProtectionHours;
    }

    static FingerprintPolicy load(Path serverDirectory) {
        Properties properties = new Properties();
        Path config = serverDirectory.resolve(CONFIG_RELATIVE);
        if (Files.isRegularFile(config)) {
            try (InputStream input = Files.newInputStream(config)) {
                properties.load(input);
            } catch (IOException ignored) {
            }
        }
        return new FingerprintPolicy(
            boundedInt(properties, "quarantine.retention-days", 30, 1, 3650),
            boundedInt(properties, "quarantine.max-generations", 8, 1, 1024),
            boundedLong(properties, "quarantine.max-bytes", DEFAULT_MAX_BYTES, 1024L * 1024L, Long.MAX_VALUE),
            boundedInt(properties, "quarantine.min-protection-hours", 24, 0, 24 * 365)
        );
    }

    static FingerprintPolicy forTesting(List<String> ignoredPaths) {
        return new FingerprintPolicy(30, 8, DEFAULT_MAX_BYTES, 24);
    }

    FingerprintManifest.Impact classify(String logicalPath, Path physicalPath) {
        String normalized = normalize(logicalPath);
        if (isAlwaysExcluded(normalized)) return FingerprintManifest.Impact.IGNORED;
        if (isGenerationSensitivePath(normalized)) return FingerprintManifest.Impact.RELEVANT;

        if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
            if (isGenerationSensitiveJarName(physicalPath.getFileName().toString())) {
                return FingerprintManifest.Impact.RELEVANT;
            }
            // All other installed MODs are currently outside FULL generation
            // ownership. New generation MODs require a deliberate code update.
            return FingerprintManifest.Impact.IGNORED;
        }
        return FingerprintManifest.Impact.IGNORED;
    }

    String reason(String logicalPath, Path physicalPath, FingerprintManifest.Impact impact) {
        String key = normalize(logicalPath);
        if (isAlwaysExcluded(key)) return "cache management or Chunksmith task progress";
        if (isGenerationSensitivePath(key)) return "known terrain generation configuration input";
        if (key.startsWith("mods/") && key.endsWith(".jar")) {
            return impact == FingerprintManifest.Impact.RELEVANT
                ? "known terrain generation MOD" : "does not own FULL terrain generation";
        }
        return impact == FingerprintManifest.Impact.RELEVANT
            ? "current FULL generation input" : "not a FULL generation input";
    }

    String compatibilitySignature() {
        return "full-worldgen-current-v2"
            + "|known-generators=" + String.join(",", BUILTIN_GENERATION_SENSITIVE_JAR_PREFIXES)
            + "|known-configs=" + String.join(",", BUILTIN_GENERATION_SENSITIVE_PATHS)
            + "|" + CompatibilityInput.policySignature();
    }

    int retentionDays() { return retentionDays; }
    int maxGenerations() { return maxGenerations; }
    long maxBytes() { return maxBytes; }
    int minProtectionHours() { return minProtectionHours; }

    static boolean isAlwaysExcluded(String logicalPath) {
        String normalized = normalize(logicalPath);
        return normalized.startsWith("config/chunksmith/tasks/") || normalized.equals(CONFIG_RELATIVE);
    }

    private boolean isGenerationSensitivePath(String logicalPath) {
        if (logicalPath.startsWith("world/datapacks/")) return true;
        for (String rule : BUILTIN_GENERATION_SENSITIVE_PATHS) if (matchesRule(logicalPath, rule)) return true;
        return false;
    }

    private static boolean isConfigurationPath(String logicalPath) {
        return logicalPath.startsWith("config/")
            || logicalPath.startsWith("serverconfig/")
            || logicalPath.startsWith("world/serverconfig/");
    }

    private static boolean isGenerationSensitiveJarName(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        for (String prefix : BUILTIN_GENERATION_SENSITIVE_JAR_PREFIXES) if (lower.startsWith(prefix)) return true;
        return false;
    }

    /**
     * Compatibility is the responsibility of the FULL cache. Existing manifests
     * from older policy revisions may call unrelated files RELEVANT; those
     * historical classifications must not reject otherwise valid terrain.
     * Actual generation inputs still compare by path, size and content SHA.
     */
    static boolean affectsCachedFull(FingerprintManifest.Entry entry) {
        String key = normalize(entry.key());
        if (key.equals("policy:compatibility") || key.startsWith("audit:")
            || key.startsWith("compat:mod:")) return false;
        if (key.startsWith("mods/") && key.endsWith(".jar")) {
            String filename = key.substring(key.lastIndexOf('/') + 1);
            return isGenerationSensitiveJarName(filename);
        }
        if (isConfigurationPath(key)) {
            if (key.startsWith("world/datapacks/")) return true;
            for (String rule : BUILTIN_GENERATION_SENSITIVE_PATHS) {
                if (matchesRule(key, rule)) return true;
            }
            return false;
        }
        return entry.impact() == FingerprintManifest.Impact.RELEVANT;
    }

    private static boolean matchesRule(String logicalPath, String rule) {
        String normalizedRule = normalize(rule);
        if (normalizedRule.endsWith("/**")) {
            String prefix = normalizedRule.substring(0, normalizedRule.length() - 3);
            return logicalPath.equals(prefix) || logicalPath.startsWith(prefix + "/");
        }
        if (normalizedRule.contains("*")) {
            String regex = normalizedRule.replace(".", "\\.").replace("*", "[^/]*");
            return logicalPath.matches(regex);
        }
        return logicalPath.equals(normalizedRule);
    }

    private static int boundedInt(Properties properties, String key, int fallback, int min, int max) {
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(properties.getProperty(key, Integer.toString(fallback)).trim())));
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static long boundedLong(Properties properties, String key, long fallback, long min, long max) {
        try {
            return Math.max(min, Math.min(max, Long.parseLong(properties.getProperty(key, Long.toString(fallback)).trim())));
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static String normalize(String value) {
        return value.replace('\\', '/');
    }
}
