package maki.chunksmithcache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class FingerprintPolicy {
    static final String CONFIG_RELATIVE = "config/maki-chunksmith-chunk-cache.properties";
    private static final String OWN_MOD_ID = "maki_chunksmith_chunk_cache";
    private static final String DEFAULT_OBSERVATION_MOD_IDS = "maki_mods_observer";
    private static final String DEFAULT_IGNORED_PATHS = "config/maki-mods-observer.properties";
    private static final long DEFAULT_MAX_BYTES = 50L * 1024L * 1024L * 1024L;

    private final Set<String> observationOnlyModIds;
    private final List<String> ignoredPaths;
    private final int retentionDays;
    private final int maxGenerations;
    private final long maxBytes;
    private final int minProtectionHours;

    private FingerprintPolicy(Set<String> observationOnlyModIds, List<String> ignoredPaths, int retentionDays, int maxGenerations, long maxBytes, int minProtectionHours) {
        LinkedHashSet<String> ids = new LinkedHashSet<>(observationOnlyModIds);
        ids.add(OWN_MOD_ID);
        this.observationOnlyModIds = Set.copyOf(ids);
        this.ignoredPaths = List.copyOf(ignoredPaths);
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
        Set<String> ids = csv(properties.getProperty("fingerprint.observation-only-mod-ids", DEFAULT_OBSERVATION_MOD_IDS));
        List<String> paths = new ArrayList<>(csv(properties.getProperty("fingerprint.ignore-paths", DEFAULT_IGNORED_PATHS)));
        return new FingerprintPolicy(
            ids,
            paths,
            boundedInt(properties, "quarantine.retention-days", 30, 1, 3650),
            boundedInt(properties, "quarantine.max-generations", 8, 1, 1024),
            boundedLong(properties, "quarantine.max-bytes", DEFAULT_MAX_BYTES, 1024L * 1024L, Long.MAX_VALUE),
            boundedInt(properties, "quarantine.min-protection-hours", 24, 0, 24 * 365)
        );
    }

    static FingerprintPolicy forTesting(Set<String> observationOnlyModIds, List<String> ignoredPaths) {
        return new FingerprintPolicy(observationOnlyModIds, ignoredPaths, 30, 8, DEFAULT_MAX_BYTES, 24);
    }

    FingerprintManifest.Impact classify(String logicalPath, Path physicalPath) {
        String normalized = normalize(logicalPath);
        if (isAlwaysExcluded(normalized)) return FingerprintManifest.Impact.IGNORED;
        if (matchesIgnoredPath(normalized)) return FingerprintManifest.Impact.IGNORED;
        if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
            Set<String> ids = readTopLevelModIds(physicalPath);
            if (!ids.isEmpty() && ids.stream().allMatch(observationOnlyModIds::contains)) return FingerprintManifest.Impact.IGNORED;
        }
        return FingerprintManifest.Impact.RELEVANT;
    }

    String reason(String logicalPath, Path physicalPath, FingerprintManifest.Impact impact) {
        String normalized = normalize(logicalPath);
        if (normalized.startsWith("config/chunksmith/tasks/")) return "chunksmith mutable task progress";
        if (normalized.equals(CONFIG_RELATIVE)) return "cache management policy; compatibility fields are hashed separately";
        if (matchesIgnoredPath(normalized)) return "explicit non-worldgen path policy";
        if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
            Set<String> ids = readTopLevelModIds(physicalPath);
            if (impact == FingerprintManifest.Impact.IGNORED) return "observation-only mod ids=" + String.join(",", ids);
            return ids.isEmpty() ? "unknown mod metadata; fail-closed relevant" : "mod ids=" + String.join(",", ids) + "; fail-closed relevant";
        }
        return "worldgen/serialization input by default";
    }

    String compatibilitySignature() {
        List<String> ids = observationOnlyModIds.stream().sorted().toList();
        List<String> paths = ignoredPaths.stream().map(FingerprintPolicy::normalize).sorted().toList();
        return "policy-v3|ignored-mod-ids=" + String.join(",", ids)
            + "|ignored-paths=" + String.join(",", paths)
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

    private boolean matchesIgnoredPath(String logicalPath) {
        for (String rule : ignoredPaths) {
            String normalizedRule = normalize(rule);
            if (normalizedRule.endsWith("/**")) {
                String prefix = normalizedRule.substring(0, normalizedRule.length() - 3);
                if (logicalPath.equals(prefix) || logicalPath.startsWith(prefix + "/")) return true;
            } else if (normalizedRule.contains("*")) {
                String regex = normalizedRule.replace(".", "\\.").replace("*", "[^/]*");
                if (logicalPath.matches(regex)) return true;
            } else if (logicalPath.equals(normalizedRule)) {
                return true;
            }
        }
        return false;
    }

    static Set<String> readTopLevelModIds(Path jarPath) {
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            JarEntry metadata = jar.getJarEntry("META-INF/neoforge.mods.toml");
            if (metadata == null) return Set.of();
            String text;
            try (InputStream input = jar.getInputStream(metadata)) {
                text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }
            boolean inMods = false;
            for (String raw : text.split("\\R")) {
                String line = raw.trim();
                if (line.startsWith("[[") && line.endsWith("]]")) {
                    inMods = line.equals("[[mods]]");
                    continue;
                }
                if (!inMods || !line.startsWith("modId")) continue;
                int equals = line.indexOf('=');
                if (equals < 0) continue;
                String value = line.substring(equals + 1).trim();
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                    ids.add(value.substring(1, value.length() - 1).toLowerCase(Locale.ROOT));
                }
            }
        } catch (Throwable ignored) {
            return Set.of();
        }
        return Set.copyOf(ids);
    }

    private static Set<String> csv(String value) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String item : value.split(",")) {
            String normalized = item.trim().toLowerCase(Locale.ROOT);
            if (!normalized.isEmpty()) result.add(normalized);
        }
        return result;
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

    private static String normalize(String path) {
        String normalized = path.replace('\\', '/');
        while (normalized.startsWith("./")) normalized = normalized.substring(2);
        return normalized;
    }
}
