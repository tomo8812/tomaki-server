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
    private static final Set<String> BUILTIN_GENERATION_NEUTRAL_MOD_IDS = Set.of(
        OWN_MOD_ID, "maki_lod_cpu", "maki_lod_cpu_online_fix", "maki_mods_observer", "maki_diorama_share"
    );
    private static final String DEFAULT_IGNORED_PATHS =
        "config/maki-mods-observer.properties,config/maki-lod-cpu.properties,config/maki-diorama-share-client.toml,"
        + "config/chunksmith/config.json,config/millenaire-common.toml";
    private static final long DEFAULT_MAX_BYTES = 50L * 1024L * 1024L * 1024L;

    // Code-driven generation inputs that are known to affect generated results even
    // when their JAR contains no datapack worldgen resources.
    private static final Set<String> BUILTIN_GENERATION_SENSITIVE_MOD_IDS = Set.of(
        "chunksmith", "zfastnoise", "epicterrain", "millenaire", "worldgenfeaturefix",
        "maki_millenaire_lod", "terrain_slabs", "disabler"
    );
    private static final List<String> BUILTIN_GENERATION_SENSITIVE_JAR_PREFIXES = List.of(
        "chunksmith-", "zfastnoise-", "epicterrain-", "millenaire-", "worldgenfeaturefix-",
        "maki-millenaire-lod-compat-", "terrain_slabs-", "disabler-"
    );
    private static final List<String> BUILTIN_GENERATION_SENSITIVE_PATHS = List.of(
        "config/zfastnoise.mixin.properties",
        "config/disabler-server.json",
        "serverconfig/millenaire-*.toml",
        "world/serverconfig/millenaire-*.toml",
        "config/epicterrain*",
        "serverconfig/epicterrain*",
        "world/serverconfig/epicterrain*",
        "config/terrain_slabs*",
        "serverconfig/terrain_slabs*",
        "world/serverconfig/terrain_slabs*"
    );

    private final Set<String> generationSensitiveModIds;
    private final List<String> generationSensitivePaths;
    private final List<String> ignoredPaths;
    private final int retentionDays;
    private final int maxGenerations;
    private final long maxBytes;
    private final int minProtectionHours;

    private FingerprintPolicy(
        Set<String> generationSensitiveModIds,
        List<String> generationSensitivePaths,
        List<String> ignoredPaths,
        int retentionDays,
        int maxGenerations,
        long maxBytes,
        int minProtectionHours
    ) {
        this.generationSensitiveModIds = Set.copyOf(generationSensitiveModIds);
        this.generationSensitivePaths = List.copyOf(generationSensitivePaths);
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
        return new FingerprintPolicy(
            lowerCsv(properties.getProperty("fingerprint.generation-sensitive-mod-ids", "")),
            new ArrayList<>(csv(properties.getProperty("fingerprint.generation-sensitive-paths", ""))),
            new ArrayList<>(csv(properties.getProperty("fingerprint.ignore-paths", DEFAULT_IGNORED_PATHS))),
            boundedInt(properties, "quarantine.retention-days", 30, 1, 3650),
            boundedInt(properties, "quarantine.max-generations", 8, 1, 1024),
            boundedLong(properties, "quarantine.max-bytes", DEFAULT_MAX_BYTES, 1024L * 1024L, Long.MAX_VALUE),
            boundedInt(properties, "quarantine.min-protection-hours", 24, 0, 24 * 365)
        );
    }

    static FingerprintPolicy forTesting(List<String> ignoredPaths) {
        return new FingerprintPolicy(Set.of(), List.of(), ignoredPaths, 30, 8, DEFAULT_MAX_BYTES, 24);
    }

    static FingerprintPolicy forTesting(Set<String> sensitiveModIds, List<String> sensitivePaths, List<String> ignoredPaths) {
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String id : sensitiveModIds) normalized.add(id.toLowerCase(Locale.ROOT));
        return new FingerprintPolicy(normalized, sensitivePaths, ignoredPaths, 30, 8, DEFAULT_MAX_BYTES, 24);
    }

    // Legacy test/source compatibility: the first set is now explicitly generation-sensitive.
    static FingerprintPolicy forTesting(Set<String> sensitiveModIds, List<String> ignoredPaths) {
        return forTesting(sensitiveModIds, List.of(), ignoredPaths);
    }

    FingerprintManifest.Impact classify(String logicalPath, Path physicalPath) {
        String normalized = normalize(logicalPath);
        if (isAlwaysExcluded(normalized)) return FingerprintManifest.Impact.IGNORED;
        if (isGenerationSensitivePath(normalized)) return FingerprintManifest.Impact.RELEVANT;
        if (matchesIgnoredPath(normalized)) return FingerprintManifest.Impact.IGNORED;

        if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
            Set<String> ids = readTopLevelModIds(physicalPath);
            if (ids.stream().anyMatch(this::isGenerationSensitiveModId)
                || isGenerationSensitiveJarName(physicalPath.getFileName().toString())
                || containsWorldgenResources(physicalPath)) {
                return FingerprintManifest.Impact.RELEVANT;
            }
            if (ids.size() == 1 && BUILTIN_GENERATION_NEUTRAL_MOD_IDS.contains(ids.iterator().next())) {
                return FingerprintManifest.Impact.IGNORED;
            }
            // Absence of recognizable worldgen resources is not proof that code cannot
            // alter generation. Unknown/new MODs therefore stay fail-closed; exact
            // audited neutral artifacts are normalized later by CompatibilityInput.
            return FingerprintManifest.Impact.RELEVANT;
        }

        // Unknown configuration can drive code-based generation too. Only explicit
        // ignored paths and known semantic generation inputs are classified specially.
        return FingerprintManifest.Impact.RELEVANT;
    }

    String reason(String logicalPath, Path physicalPath, FingerprintManifest.Impact impact) {
        String normalized = normalize(logicalPath);
        if (normalized.startsWith("config/chunksmith/tasks/")) return "chunksmith mutable task progress";
        if (normalized.equals(CONFIG_RELATIVE)) return "cache management policy; generated-result inputs are hashed separately";
        if (isGenerationSensitivePath(normalized)) return "declared/effective world-generation configuration input";
        if (matchesIgnoredPath(normalized)) return "explicit generation-neutral path policy";
        if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
            Set<String> ids = readTopLevelModIds(physicalPath);
            if (impact == FingerprintManifest.Impact.RELEVANT) {
                if (ids.stream().anyMatch(this::isGenerationSensitiveModId)) {
                    return "generation-sensitive MOD id=" + String.join(",", ids);
                }
                if (isGenerationSensitiveJarName(physicalPath.getFileName().toString())) {
                    return "known generation-sensitive MOD artifact";
                }
                if (containsWorldgenResources(physicalPath)) {
                    return "JAR contains worldgen/structure/dimension datapack resources";
                }
                return "unknown code-driven generation impact; fail-closed until audited";
            }
            if (ids.size() == 1 && BUILTIN_GENERATION_NEUTRAL_MOD_IDS.contains(ids.iterator().next())) {
                return "owned scheduling/UI/diagnostic MOD role is generation-neutral";
            }
            return "explicit generation-neutral MOD input";
        }
        if (isConfigurationPath(normalized)) return "unknown configuration impact; fail-closed until explicitly classified";
        return impact == FingerprintManifest.Impact.RELEVANT ? "generation compatibility input" : "generation-neutral input";
    }

    String compatibilitySignature() {
        List<String> ignored = ignoredPaths.stream().map(FingerprintPolicy::normalize).sorted().toList();
        List<String> sensitivePaths = generationSensitivePaths.stream().map(FingerprintPolicy::normalize).sorted().toList();
        List<String> sensitiveMods = generationSensitiveModIds.stream().sorted().toList();
        return "policy-v9|self-mod-id=" + OWN_MOD_ID
            + "|ignored-paths=" + String.join(",", ignored)
            + "|generation-sensitive-mod-ids=" + String.join(",", sensitiveMods)
            + "|generation-sensitive-paths=" + String.join(",", sensitivePaths)
            + "|generation-neutral-owned-mod-ids=" + BUILTIN_GENERATION_NEUTRAL_MOD_IDS.stream().sorted().toList()
            + "|default-mod-policy=unknown-fail-closed-v1"
            + "|default-config-policy=unknown-fail-closed-v1"
            + "|worldgen-resource-detection=v1"
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

    private boolean isGenerationSensitiveModId(String id) {
        String normalized = id.toLowerCase(Locale.ROOT);
        return BUILTIN_GENERATION_SENSITIVE_MOD_IDS.contains(normalized) || generationSensitiveModIds.contains(normalized);
    }

    private boolean isGenerationSensitivePath(String logicalPath) {
        if (logicalPath.startsWith("world/datapacks/")) return true;
        for (String rule : BUILTIN_GENERATION_SENSITIVE_PATHS) if (matchesRule(logicalPath, rule)) return true;
        for (String rule : generationSensitivePaths) if (matchesRule(logicalPath, rule)) return true;
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

    private static boolean containsWorldgenResources(Path jarPath) {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            return jar.stream().anyMatch(entry -> {
                if (entry.isDirectory()) return false;
                String name = entry.getName().toLowerCase(Locale.ROOT);
                if (!name.startsWith("data/")) return false;
                return name.contains("/worldgen/")
                    || name.contains("/structure/")
                    || name.contains("/structures/")
                    || name.contains("/dimension/")
                    || name.contains("/dimension_type/")
                    || name.contains("/biome_modifier/")
                    || name.contains("/structure_modifier/");
            });
        } catch (Throwable unreadable) {
            // Unreadable/corrupt JARs never become generation-neutral.
            return true;
        }
    }

    private boolean matchesIgnoredPath(String logicalPath) {
        for (String rule : ignoredPaths) if (matchesRule(logicalPath, rule)) return true;
        return false;
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
