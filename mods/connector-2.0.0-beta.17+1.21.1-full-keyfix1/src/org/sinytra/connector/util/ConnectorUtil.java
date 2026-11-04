package org.sinytra.connector.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import cpw.mods.modlauncher.api.ServiceRunner;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.impl.metadata.EntrypointMetadata;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;
import org.sinytra.connector.locator.EmbeddedDependencies;
import sun.misc.Unsafe;

public final class ConnectorUtil {
   public static final Unsafe UNSAFE = (Unsafe)LambdaExceptionUtils.uncheck(() -> {
      Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      return (Unsafe)theUnsafe.get(null);
   });
   public static final Lookup TRUSTED_LOOKUP = (Lookup)LambdaExceptionUtils.uncheck(() -> {
      Field hackfield = Lookup.class.getDeclaredField("IMPL_LOOKUP");
      return (Lookup)UNSAFE.getObject(UNSAFE.staticFieldBase(hackfield), UNSAFE.staticFieldOffset(hackfield));
   });
   public static final String FABRIC_MOD_JSON = "fabric.mod.json";
   public static final String MODS_TOML = "META-INF/neoforge.mods.toml";
   public static final String CONNECTOR_MARKER = "connector_transformed";
   public static final Path CONNECTOR_FOLDER = FMLPaths.GAMEDIR.get().resolve(".cache/connector");
   public static final String CONNECTOR_MODID = "connector";
   public static final String CONNECTOR_ISSUE_TRACKER_URL = "https://github.com/Sinytra/Connector/issues";
   public static final Collection<String> DISABLED_MODS = Set.of("fabric_api", "mixinextras");
   private static final String MIXINEXTRAS_MODID = "com_github_llamalad7_mixinextras";
   private static final Version MIXINEXTRAS_ENTRYPOINT_VERSION = (Version)LambdaExceptionUtils.uncheck(() -> Version.parse("0.2.0-beta.6"));
   public static final Collection<String> DISABLED_MIXINEXTRAS_ENTRYPOINTS = Set.of(
      "com.llamalad7.mixinextras.MixinExtrasBootstrap", "com.llamalad7.mixinextras.MixinExtrasBootstrap::init"
   );
   private static final Set<String> RESERVED = Set.of(
      "abstract",
      "assert",
      "boolean",
      "break",
      "byte",
      "case",
      "catch",
      "char",
      "class",
      "const",
      "continue",
      "default",
      "do",
      "double",
      "else",
      "enum",
      "extends",
      "final",
      "finally",
      "float",
      "for",
      "goto",
      "if",
      "implements",
      "import",
      "instanceof",
      "int",
      "interface",
      "long",
      "native",
      "new",
      "package",
      "private",
      "protected",
      "public",
      "return",
      "short",
      "static",
      "strictfp",
      "super",
      "switch",
      "synchronized",
      "this",
      "throw",
      "throws",
      "transient",
      "try",
      "void",
      "volatile",
      "while",
      "true",
      "false",
      "null",
      "_"
   );
   public static final Multimap<String, String> DEFAULT_GLOBAL_MOD_ALIASES = ImmutableMultimap.of(
      "cloth_config", "cloth-config2", "playeranimator", "player-animator"
   );
   private static final boolean CACHE_ENABLED;

   public static ConnectorUtil.CacheFile getCached(@Nullable Path input, Path output) {
      if (CACHE_ENABLED) {
         Path inputCache = output.getParent().resolve(output.getFileName() + ".input");

         try {
            String hash = EmbeddedDependencies.getJarCacheVersion();
            if (input != null) {
               byte[] bytes = Files.readAllBytes(input);
               hash = hash + "," + Hashing.sha256().hashBytes(bytes);
            }

            if (Files.exists(inputCache)) {
               if (Files.exists(output)) {
                  String cached = Files.readString(inputCache);
                  if (cached.equals(hash)) {
                     return new ConnectorUtil.CacheFile(inputCache, hash, true);
                  }

                  Files.delete(output);
                  Files.delete(inputCache);
               }
            } else {
               Files.deleteIfExists(output);
            }

            return new ConnectorUtil.CacheFile(inputCache, hash, false);
         } catch (Throwable t) {
            throw new RuntimeException(t);
         }
      } else {
         return new ConnectorUtil.CacheFile(null, null, false);
      }
   }

   public static void cache(@Nullable Path input, Path output, ServiceRunner action) {
      ConnectorUtil.CacheFile cacheFile = getCached(input, output);
      if (!cacheFile.isUpToDate()) {
         try {
            Files.deleteIfExists(output);
            action.run();
            cacheFile.save();
         } catch (Throwable t) {
            throw new RuntimeException(t);
         }
      }
   }

   public static boolean isJavaReservedKeyword(String str) {
      return RESERVED.contains(str);
   }

   public static List<EntrypointMetadata> filterMixinExtrasEntrypoints(List<EntrypointMetadata> entrypoints) {
      return FabricLoader.getInstance()
         .getModContainer("com_github_llamalad7_mixinextras")
         .filter(mod -> mod.getMetadata().getVersion().compareTo(MIXINEXTRAS_ENTRYPOINT_VERSION) >= 0)
         .map(mod -> entrypoints.stream().filter(metadata -> !DISABLED_MIXINEXTRAS_ENTRYPOINTS.contains(metadata.getValue())).toList())
         .orElse(entrypoints);
   }

   public static boolean isValidURL(String str) {
      if (str == null) {
         return false;
      }

      try {
         new URL(str);
         return true;
      } catch (MalformedURLException e) {
         return false;
      }
   }

   private ConnectorUtil() {
   }

   static {
      String prop = System.getProperty("connector.cache.enabled");
      CACHE_ENABLED = prop == null || prop.equals("true");
   }

   public static class CacheFile {
      private final Path inputCache;
      private final String inputChecksum;
      private boolean isUpToDate;

      public CacheFile(Path inputCache, String inputChecksum, boolean isUpToDate) {
         this.inputCache = inputCache;
         this.inputChecksum = inputChecksum;
         this.isUpToDate = isUpToDate;
      }

      public boolean isUpToDate() {
         return this.isUpToDate;
      }

      public void save() {
         if (this.inputCache != null) {
            try {
               Files.writeString(this.inputCache, this.inputChecksum);
               this.isUpToDate = true;
            } catch (IOException e) {
               throw new UncheckedIOException(e);
            }
         }
      }
   }
}
