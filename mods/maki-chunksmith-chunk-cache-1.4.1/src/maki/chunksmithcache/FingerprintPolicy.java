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
   static final String UNKNOWN_MOD_REASON = "unknown code-driven generation impact; fail-closed until audited";
   private static final String OWN_MOD_ID = "maki_chunksmith_chunk_cache";
   private static final Set<String> BUILTIN_GENERATION_NEUTRAL_MOD_IDS = Set.of(
      "maki_chunksmith_chunk_cache", "maki_lod_cpu", "maki_mods_observer", "maki_diorama_share"
   );
   private static final String DEFAULT_IGNORED_PATHS = "config/maki-mods-observer.properties,config/maki-lod-cpu.properties,config/maki-diorama-share-client.toml,config/chunksmith/config.json,config/millenaire-common.toml";
   private static final long DEFAULT_MAX_BYTES = 53687091200L;
   private static final Set<String> BUILTIN_GENERATION_SENSITIVE_MOD_IDS = Set.of(
      "chunksmith", "zfastnoise", "epicterrain", "millenaire", "worldgenfeaturefix", "maki_millenaire_lod", "terrain_slabs", "disabler"
   );
   private static final List<String> BUILTIN_GENERATION_SENSITIVE_JAR_PREFIXES = List.of(
      "chunksmith-", "zfastnoise-", "epicterrain-", "millenaire-", "worldgenfeaturefix-", "maki-millenaire-lod-compat-", "terrain_slabs-", "disabler-"
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
   private final Set<String> auditedNeutralArtifacts;
   private final int retentionDays;
   private final int maxGenerations;
   private final long maxBytes;
   private final int minProtectionHours;

   private FingerprintPolicy(
      Set<String> generationSensitiveModIds,
      List<String> generationSensitivePaths,
      List<String> ignoredPaths,
      Set<String> auditedNeutralArtifacts,
      int retentionDays,
      int maxGenerations,
      long maxBytes,
      int minProtectionHours
   ) {
      this.generationSensitiveModIds = Set.copyOf(generationSensitiveModIds);
      this.generationSensitivePaths = List.copyOf(generationSensitivePaths);
      this.ignoredPaths = List.copyOf(ignoredPaths);
      this.auditedNeutralArtifacts = Set.copyOf(auditedNeutralArtifacts);
      this.retentionDays = retentionDays;
      this.maxGenerations = maxGenerations;
      this.maxBytes = maxBytes;
      this.minProtectionHours = minProtectionHours;
   }

   static FingerprintPolicy load(Path serverDirectory) {
      Properties properties = new Properties();
      Path config = serverDirectory.resolve("config/maki-chunksmith-chunk-cache.properties");
      if (Files.isRegularFile(config)) {
         try (InputStream input = Files.newInputStream(config)) {
            properties.load(input);
         } catch (IOException var8) {
         }
      }

      return new FingerprintPolicy(
         lowerCsv(properties.getProperty("fingerprint.generation-sensitive-mod-ids", "")),
         new ArrayList<>(csv(properties.getProperty("fingerprint.generation-sensitive-paths", ""))),
         new ArrayList<>(
            csv(
               properties.getProperty(
                  "fingerprint.ignore-paths",
                  "config/maki-mods-observer.properties,config/maki-lod-cpu.properties,config/maki-diorama-share-client.toml,config/chunksmith/config.json,config/millenaire-common.toml"
               )
            )
         ),
         auditedNeutralPairs(properties.getProperty("fingerprint.audited-neutral-mod-sha256", "")),
         boundedInt(properties, "quarantine.retention-days", 30, 1, 3650),
         boundedInt(properties, "quarantine.max-generations", 8, 1, 1024),
         boundedLong(properties, "quarantine.max-bytes", 53687091200L, 1048576L, Long.MAX_VALUE),
         boundedInt(properties, "quarantine.min-protection-hours", 24, 0, 8760)
      );
   }

   static FingerprintPolicy forTesting(List<String> ignoredPaths) {
      return new FingerprintPolicy(Set.of(), List.of(), ignoredPaths, Set.of(), 30, 8, 53687091200L, 24);
   }

   static FingerprintPolicy forTesting(Set<String> sensitiveModIds, List<String> sensitivePaths, List<String> ignoredPaths) {
      LinkedHashSet<String> normalized = new LinkedHashSet<>();

      for (String id : sensitiveModIds) {
         normalized.add(id.toLowerCase(Locale.ROOT));
      }

      return new FingerprintPolicy(normalized, sensitivePaths, ignoredPaths, Set.of(), 30, 8, 53687091200L, 24);
   }

   static FingerprintPolicy forTesting(Set<String> sensitiveModIds, List<String> ignoredPaths) {
      return forTesting(sensitiveModIds, List.of(), ignoredPaths);
   }

   FingerprintManifest.Impact classify(String logicalPath, Path physicalPath) {
      String normalized = normalize(logicalPath);
      if (isAlwaysExcluded(normalized)) {
         return FingerprintManifest.Impact.IGNORED;
      }

      if (this.isGenerationSensitivePath(normalized)) {
         return FingerprintManifest.Impact.RELEVANT;
      }

      if (this.matchesIgnoredPath(normalized)) {
         return FingerprintManifest.Impact.IGNORED;
      }

      if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
         Set<String> ids = readTopLevelModIds(physicalPath);
         if (!ids.stream().anyMatch(this::isGenerationSensitiveModId)
            && !isGenerationSensitiveJarName(physicalPath.getFileName().toString())
            && !containsWorldgenResources(physicalPath)) {
            return ids.size() == 1 && BUILTIN_GENERATION_NEUTRAL_MOD_IDS.contains(ids.iterator().next())
               ? FingerprintManifest.Impact.IGNORED
               : FingerprintManifest.Impact.RELEVANT;
         } else {
            return FingerprintManifest.Impact.RELEVANT;
         }
      } else {
         return FingerprintManifest.Impact.RELEVANT;
      }
   }

   boolean allowsAuditedNeutral(String logicalPath, Path physicalPath, String rawSha256) {
      String normalized = normalize(logicalPath);
      if (normalized.startsWith("mods/") && normalized.endsWith(".jar") && rawSha256 != null && rawSha256.matches("[a-f0-9]{64}")) {
         Set<String> ids = readTopLevelModIds(physicalPath);
         if (ids.size() != 1) {
            return false;
         }

         String id = ids.iterator().next();
         return !this.isGenerationSensitiveModId(id)
               && !isGenerationSensitiveJarName(physicalPath.getFileName().toString())
               && !containsWorldgenResources(physicalPath)
               && !AuditedModCompatibility.declaresGenerationOverride(physicalPath)
            ? this.auditedNeutralArtifacts.contains(id + ":" + rawSha256)
            : false;
      } else {
         return false;
      }
   }

   String reason(String logicalPath, Path physicalPath, FingerprintManifest.Impact impact) {
      String normalized = normalize(logicalPath);
      if (normalized.startsWith("config/chunksmith/tasks/")) {
         return "chunksmith mutable task progress";
      }

      if (normalized.equals("config/maki-chunksmith-chunk-cache.properties")) {
         return "cache management policy; generated-result inputs are hashed separately";
      }

      if (this.isGenerationSensitivePath(normalized)) {
         return "declared/effective world-generation configuration input";
      }

      if (this.matchesIgnoredPath(normalized)) {
         return "explicit generation-neutral path policy";
      }

      if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
         Set<String> ids = readTopLevelModIds(physicalPath);
         if (impact == FingerprintManifest.Impact.RELEVANT) {
            if (ids.stream().anyMatch(this::isGenerationSensitiveModId)) {
               return "generation-sensitive MOD id=" + String.join(",", ids);
            } else if (isGenerationSensitiveJarName(physicalPath.getFileName().toString())) {
               return "known generation-sensitive MOD artifact";
            } else {
               return containsWorldgenResources(physicalPath)
                  ? "JAR contains worldgen/structure/dimension datapack resources"
                  : "unknown code-driven generation impact; fail-closed until audited";
            }
         } else {
            return ids.size() == 1 && BUILTIN_GENERATION_NEUTRAL_MOD_IDS.contains(ids.iterator().next())
               ? "owned scheduling/UI/diagnostic MOD role is generation-neutral"
               : "explicit generation-neutral MOD input";
         }
      } else if (isConfigurationPath(normalized)) {
         return "unknown configuration impact; fail-closed until explicitly classified";
      } else {
         return impact == FingerprintManifest.Impact.RELEVANT ? "generation compatibility input" : "generation-neutral input";
      }
   }

   String compatibilitySignature() {
      List<String> ignored = this.ignoredPaths.stream().map(FingerprintPolicy::normalize).sorted().toList();
      List<String> sensitivePaths = this.generationSensitivePaths.stream().map(FingerprintPolicy::normalize).sorted().toList();
      List<String> sensitiveMods = this.generationSensitiveModIds.stream().sorted().toList();
      return "policy-v9|self-mod-id=maki_chunksmith_chunk_cache|ignored-paths="
         + String.join(",", ignored)
         + "|generation-sensitive-mod-ids="
         + String.join(",", sensitiveMods)
         + "|generation-sensitive-paths="
         + String.join(",", sensitivePaths)
         + "|generation-neutral-owned-mod-ids="
         + BUILTIN_GENERATION_NEUTRAL_MOD_IDS.stream().sorted().toList()
         + "|default-mod-policy=unknown-fail-closed-v1|default-config-policy=unknown-fail-closed-v1|worldgen-resource-detection=v1|"
         + CompatibilityInput.policySignature();
   }

   int retentionDays() {
      return this.retentionDays;
   }

   int maxGenerations() {
      return this.maxGenerations;
   }

   long maxBytes() {
      return this.maxBytes;
   }

   int minProtectionHours() {
      return this.minProtectionHours;
   }

   static boolean isAlwaysExcluded(String logicalPath) {
      String normalized = normalize(logicalPath);
      return normalized.startsWith("config/chunksmith/tasks/") || normalized.equals("config/maki-chunksmith-chunk-cache.properties");
   }

   private boolean isGenerationSensitiveModId(String id) {
      String normalized = id.toLowerCase(Locale.ROOT);
      return BUILTIN_GENERATION_SENSITIVE_MOD_IDS.contains(normalized) || this.generationSensitiveModIds.contains(normalized);
   }

   private boolean isGenerationSensitivePath(String logicalPath) {
      if (logicalPath.startsWith("world/datapacks/")) {
         return true;
      }

      for (String rule : BUILTIN_GENERATION_SENSITIVE_PATHS) {
         if (matchesRule(logicalPath, rule)) {
            return true;
         }
      }

      for (String rule : this.generationSensitivePaths) {
         if (matchesRule(logicalPath, rule)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isConfigurationPath(String logicalPath) {
      return logicalPath.startsWith("config/") || logicalPath.startsWith("serverconfig/") || logicalPath.startsWith("world/serverconfig/");
   }

   private static boolean isGenerationSensitiveJarName(String fileName) {
      String lower = fileName.toLowerCase(Locale.ROOT);

      for (String prefix : BUILTIN_GENERATION_SENSITIVE_JAR_PREFIXES) {
         if (lower.startsWith(prefix)) {
            return true;
         }
      }

      return false;
   }

   private static boolean containsWorldgenResources(Path jarPath) {
      try (JarFile jar = new JarFile(jarPath.toFile())) {
         return jar.stream()
            .anyMatch(
               entry -> {
                  if (entry.isDirectory()) {
                     return false;
                  }

                  String name = entry.getName().toLowerCase(Locale.ROOT);
                  return !name.startsWith("data/")
                     ? false
                     : name.contains("/worldgen/")
                        || name.contains("/structure/")
                        || name.contains("/structures/")
                        || name.contains("/dimension/")
                        || name.contains("/dimension_type/")
                        || name.contains("/biome_modifier/")
                        || name.contains("/structure_modifier/");
               }
            );
      } catch (Throwable unreadable) {
         return true;
      }
   }

   private boolean matchesIgnoredPath(String logicalPath) {
      for (String rule : this.ignoredPaths) {
         if (matchesRule(logicalPath, rule)) {
            return true;
         }
      }

      return false;
   }

   private static boolean matchesRule(String logicalPath, String rule) {
      String normalizedRule = normalize(rule);
      if (!normalizedRule.endsWith("/**")) {
         if (normalizedRule.contains("*")) {
            String regex = normalizedRule.replace(".", "\\.").replace("*", "[^/]*");
            return logicalPath.matches(regex);
         } else {
            return logicalPath.equals(normalizedRule);
         }
      } else {
         String prefix = normalizedRule.substring(0, normalizedRule.length() - 3);
         return logicalPath.equals(prefix) || logicalPath.startsWith(prefix + "/");
      }
   }

   static Set<String> readTopLevelModIds(Path jarPath) {
      LinkedHashSet<String> ids = new LinkedHashSet<>();

      try (JarFile jar = new JarFile(jarPath.toFile())) {
         JarEntry metadata = jar.getJarEntry("META-INF/neoforge.mods.toml");
         if (metadata != null) {
            String text;
            try (InputStream input = jar.getInputStream(metadata)) {
               text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            }

            boolean inMods = false;

            for (String raw : text.split("\\R")) {
               String line = raw.trim();
               if (line.startsWith("[[") && line.endsWith("]]")) {
                  inMods = line.equals("[[mods]]");
               } else if (inMods && line.startsWith("modId")) {
                  int equals = line.indexOf(61);
                  if (equals >= 0) {
                     String value = line.substring(equals + 1).trim();
                     int comment = value.indexOf(35);
                     if (comment >= 0) {
                        value = value.substring(0, comment).trim();
                     }

                     if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        ids.add(value.substring(1, value.length() - 1).toLowerCase(Locale.ROOT));
                     }
                  }
               }
            }

            return Set.copyOf(ids);
         } else {
            return Set.of();
         }
      } catch (Throwable ignored) {
         return Set.of();
      }
   }

   private static Set<String> csv(String value) {
      LinkedHashSet<String> result = new LinkedHashSet<>();

      for (String item : value.split(",")) {
         String normalized = item.trim();
         if (!normalized.isEmpty()) {
            result.add(normalized);
         }
      }

      return result;
   }

   private static Set<String> auditedNeutralPairs(String value) {
      LinkedHashSet<String> result = new LinkedHashSet<>();

      for (String raw : csv(value)) {
         String entry = raw.toLowerCase(Locale.ROOT);
         if (entry.matches("[a-z0-9_.-]+:[a-f0-9]{64}")) {
            result.add(entry);
         }
      }

      return Set.copyOf(result);
   }

   private static Set<String> lowerCsv(String value) {
      LinkedHashSet<String> result = new LinkedHashSet<>();

      for (String item : csv(value)) {
         result.add(item.toLowerCase(Locale.ROOT));
      }

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
