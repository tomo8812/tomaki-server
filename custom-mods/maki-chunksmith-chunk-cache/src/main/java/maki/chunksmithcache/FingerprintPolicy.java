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
    private static final String DEFAULT_IGNORED_PATHS = "config/maki-mods-observer.properties";
    private static final long DEFAULT_WARN_BYTES = 50L * 1024L * 1024L * 1024L;
    private static final Set<String> GENERATION_SENSITIVE_MOD_IDS = Set.of(
        "chunksmith", "zfastnoise", "epicterrain", "millenaire", "worldgenfeaturefix", "maki_millenaire_lod"
    );

    private final Set<String> generationNeutralModIds;
    private final List<String> ignoredPaths;
    private final int warnGenerations;
    private final long warnBytes;

    private FingerprintPolicy(Set<String> generationNeutralModIds, List<String> ignoredPaths, int warnGenerations, long warnBytes) {
        this.generationNeutralModIds = Set.copyOf(generationNeutralModIds);
        this.ignoredPaths = List.copyOf(ignoredPaths);
        this.warnGenerations = warnGenerations;
        this.warnBytes = warnBytes;
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
        Set<String> neutral = lowerCsv(properties.getProperty("fingerprint.generation-neutral-mod-ids", ""));
        List<String> paths = new ArrayList<>(csv(properties.getProperty("fingerprint.ignore-paths", DEFAULT_IGNORED_PATHS)));
        return new FingerprintPolicy(
            neutral,
            paths,
            boundedInt(properties, "quarantine.warn-generations", 8, 1, 1024),
            boundedLong(properties, "quarantine.warn-bytes", DEFAULT_WARN_BYTES, 1024L * 1024L, Long.MAX_VALUE)
        );
    }

    static FingerprintPolicy forTesting(List<String> ignoredPaths) {
        return new FingerprintPolicy(Set.of(), ignoredPaths, 8, DEFAULT_WARN_BYTES);
    }

    static FingerprintPolicy forTesting(Set<String> ignoredModIds, List<String> ignoredPaths) {
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String id : ignoredModIds) normalized.add(id.toLowerCase(Locale.ROOT));
        return new FingerprintPolicy(normalized, ignoredPaths, 8, DEFAULT_WARN_BYTES);
    }

    FingerprintManifest.Impact classify(String logicalPath, Path physicalPath) {
        String normalized = normalize(logicalPath);
        if (isAlwaysExcluded(normalized)) return FingerprintManifest.Impact.IGNORED;
        if (matchesIgnoredPath(normalized)) return FingerprintManifest.Impact.IGNORED;
        if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
            Set<String> ids = readTopLevelModIds(physicalPath);
            if (ids.size() == 1 && ids.contains(OWN_MOD_ID)) return FingerprintManifest.Impact.IGNORED;
            if (ids.size() == 1) {
                String id = ids.iterator().next();
                if (!GENERATION_SENSITIVE_MOD_IDS.contains(id) && generationNeutralModIds.contains(id)) {
                    return FingerprintManifest.Impact.IGNORED;
                }
            }
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
            if (impact == FingerprintManifest.Impact.IGNORED && ids.contains(OWN_MOD_ID)) {
                return "chunk-cache implementation itself; cache serialization schema is validated per entry";
            }
            if (impact == FingerprintManifest.Impact.IGNORED && ids.size() == 1) {
                return "operator-declared generation-neutral MOD id=" + ids.iterator().next() + "; raw bytes retained for audit";
            }
            return ids.isEmpty()
                ? "unknown mod metadata; fail-closed relevant"
                : "mod ids=" + String.join(",", ids) + "; no generation-neutral/semantic contract; fail-closed relevant";
        }
        return "worldgen/serialization input by default";
    }

    String compatibilitySignature() {
        List<String> paths = ignoredPaths.stream().map(FingerprintPolicy::normalize).sorted().toList();
        // The explicit neutral-id set is intentionally not itself fingerprinted. A newly added,
        // explicitly classified QoL MOD contributes only an ignored audit entry, so its addition
        // does not rotate otherwise-identical world-generation data. Removing the classification
        // makes the JAR relevant again and therefore changes the fingerprint through that JAR.
        return "policy-v5|self-mod-id=" + OWN_MOD_ID
            + "|ignored-paths=" + String.join(",", paths)
            + "|neutral-mod-policy=explicit-v1"
            + "|" + CompatibilityInput.policySignature();
    }

    int warnGenerations() { return warnGenerations; }
    long warnBytes() { return warnBytes; }

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
                int comment = value.indexOf('#');
                if (comment >= 0) value = value.substring(0, comment).trim();
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
            String normalized = item.trim();
            if (!normalized.isEmpty()) result.add(normalized);
        }
        return result;
    }

    private static Set<String> lowerCsv(String value) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String item : csv(value)) result.add(item.toLowerCase(Locale.ROOT));
        return Set.copyOf(result);
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
