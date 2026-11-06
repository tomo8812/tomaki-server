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
   private static final long DEFAULT_MAX_BYTES = 53687091200L;
   private final List<String> ignoredPaths;
   private final int retentionDays;
   private final int maxGenerations;
   private final long maxBytes;
   private final int minProtectionHours;

   private FingerprintPolicy(List<String> ignoredPaths, int retentionDays, int maxGenerations, long maxBytes, int minProtectionHours) {
      this.ignoredPaths = List.copyOf(ignoredPaths);
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

      List<String> paths = new ArrayList<>(csv(properties.getProperty("fingerprint.ignore-paths", "config/maki-mods-observer.properties")));
      return new FingerprintPolicy(
         paths,
         boundedInt(properties, "quarantine.retention-days", 30, 1, 3650),
         boundedInt(properties, "quarantine.max-generations", 8, 1, 1024),
         boundedLong(properties, "quarantine.max-bytes", 53687091200L, 1048576L, Long.MAX_VALUE),
         boundedInt(properties, "quarantine.min-protection-hours", 24, 0, 8760)
      );
   }

   static FingerprintPolicy forTesting(List<String> ignoredPaths) {
      return new FingerprintPolicy(ignoredPaths, 30, 8, 53687091200L, 24);
   }

   static FingerprintPolicy forTesting(Set<String> ignoredModIds, List<String> ignoredPaths) {
      return forTesting(ignoredPaths);
   }

   FingerprintManifest.Impact classify(String logicalPath, Path physicalPath) {
      String normalized = normalize(logicalPath);
      if (isAlwaysExcluded(normalized)) {
         return FingerprintManifest.Impact.IGNORED;
      }

      if (this.matchesIgnoredPath(normalized)) {
         return FingerprintManifest.Impact.IGNORED;
      }

      if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
         Set<String> ids = readTopLevelModIds(physicalPath);
         if (ids.size() == 1 && ids.contains("maki_chunksmith_chunk_cache")) {
            return FingerprintManifest.Impact.IGNORED;
         }
      }

      return FingerprintManifest.Impact.RELEVANT;
   }

   String reason(String logicalPath, Path physicalPath, FingerprintManifest.Impact impact) {
      String normalized = normalize(logicalPath);
      if (normalized.startsWith("config/chunksmith/tasks/")) {
         return "chunksmith mutable task progress";
      }

      if (normalized.equals("config/maki-chunksmith-chunk-cache.properties")) {
         return "cache management policy; compatibility fields are hashed separately";
      }

      if (this.matchesIgnoredPath(normalized)) {
         return "explicit non-worldgen path policy";
      }

      if (normalized.startsWith("mods/") && normalized.endsWith(".jar")) {
         Set<String> ids = readTopLevelModIds(physicalPath);
         if (impact == FingerprintManifest.Impact.IGNORED && ids.contains("maki_chunksmith_chunk_cache")) {
            return "chunk-cache implementation itself; compatibility schema/policy is hashed separately";
         } else {
            return ids.isEmpty()
               ? "unknown mod metadata; fail-closed relevant"
               : "mod ids=" + String.join(",", ids) + "; not an exact audited generation-neutral artifact; fail-closed relevant";
         }
      } else {
         return "worldgen/serialization input by default";
      }
   }

   String compatibilitySignature() {
      List<String> paths = this.ignoredPaths.stream().map(FingerprintPolicy::normalize).sorted().toList();
      return "policy-v4|self-mod-id=maki_chunksmith_chunk_cache|ignored-paths=" + String.join(",", paths) + "|" + CompatibilityInput.policySignature();
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

   private boolean matchesIgnoredPath(String logicalPath) {
      for (String rule : this.ignoredPaths) {
         String normalizedRule = normalize(rule);
         if (normalizedRule.endsWith("/**")) {
            String prefix = normalizedRule.substring(0, normalizedRule.length() - 3);
            if (logicalPath.equals(prefix) || logicalPath.startsWith(prefix + "/")) {
               return true;
            }
         } else if (normalizedRule.contains("*")) {
            String regex = normalizedRule.replace(".", "\\.").replace("*", "[^/]*");
            if (logicalPath.matches(regex)) {
               return true;
            }
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
