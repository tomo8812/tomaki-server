package org.sinytra.connector.locator;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.mojang.logging.LogUtils;
import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.LambdaExceptionUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.neoforged.fml.loading.ClasspathLocatorUtils;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LogMarkers;
import net.neoforged.fml.loading.ModDirTransformerDiscoverer;
import net.neoforged.fml.loading.StringUtils;
import org.slf4j.Logger;

public final class FabricModsDiscoverer {
   private static final String SUFFIX = ".jar";
   private static final String ADDITIONAL_MODS_PROPERTY = "connector.additionalModLocations";
   private static final Logger LOGGER = LogUtils.getLogger();

   public static Stream<Path> scanFabricMods() {
      List<Path> excluded = ModDirTransformerDiscoverer.allExcluded();
      return Stream.of(scanModsDir(excluded), scanClasspath(), scanFromArguments(excluded)).flatMap(Function.identity());
   }

   private static Stream<Path> scanModsDir(List<Path> excluded) {
      return filterPaths((Stream<Path>)LambdaExceptionUtils.uncheck(() -> Files.list(FMLPaths.MODSDIR.get())), excluded);
   }

   private static Stream<Path> filterPaths(Stream<Path> stream, List<Path> excluded) {
      return stream.filter(p -> !excluded.contains(p) && StringUtils.toLowerCase(p.getFileName().toString()).endsWith(".jar"))
         .sorted(Comparator.comparing(path -> StringUtils.toLowerCase(path.getFileName().toString())))
         .filter(FabricModsDiscoverer::isFabricModJar);
   }

   private static Stream<Path> scanClasspath() {
      if (FMLEnvironment.production) {
         return Stream.of();
      }

      try {
         List<Path> claimed = new ArrayList<>(
            Arrays.stream(System.getProperty("legacyClassPath", "").split(File.pathSeparator)).map(x$0 -> Path.of(x$0)).toList()
         );
         Builder<Path> ret = Stream.builder();
         Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources("fabric.mod.json");

         while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Path path = ClasspathLocatorUtils.findJarPathFor("fabric.mod.json", "fabric.mod.json", url);
            if (claimed.stream().noneMatch(path::equals) && Files.exists(path) && !Files.isDirectory(path) && isFabricModJar(path)) {
               ret.add(path);
            }
         }

         return ret.build();
      } catch (IOException e) {
         LOGGER.error(LogMarkers.SCAN, "Error trying to find resources", e);
         throw new RuntimeException(e);
      }
   }

   private static Stream<Path> scanFromArguments(List<Path> excluded) {
      String[] paths = System.getProperty("connector.additionalModLocations", "").split(",");
      if (paths.length == 0) {
         return Stream.of();
      }

      Builder<Path> files = Stream.builder();
      Arrays.stream(paths).filter(s -> !s.isBlank()).map(x$0 -> Path.of(x$0)).forEach(path -> {
         if (Files.isDirectory(path)) {
            ((Stream)LambdaExceptionUtils.uncheck(() -> Files.list(path))).forEach(files::add);
         } else {
            files.add(path);
         }
      });
      return filterPaths(files.build(), excluded);
   }

   private static boolean isFabricModJar(Path path) {
      SecureJar secureJar = SecureJar.from(new Path[]{path});
      String name = secureJar.name();
      Path modsToml = secureJar.getPath("META-INF/neoforge.mods.toml", new String[0]);
      if (Files.exists(modsToml) && !containsPlaceholder(modsToml)) {
         LOGGER.debug(LogMarkers.SCAN, "Skipping jar {} as it contains a mods.toml file", path);
         return false;
      } else if (secureJar.moduleDataProvider().findFile("fabric.mod.json").isPresent()) {
         LOGGER.debug(LogMarkers.SCAN, "Found {} mod: {}", "fabric.mod.json", path);
         return true;
      } else {
         LOGGER.info(LogMarkers.SCAN, "Fabric mod metadata not found in jar {}, ignoring", name);
         return false;
      }
   }

   private static boolean containsPlaceholder(Path modsTomlPath) {
      try {
         FileConfig fileConfig = FileConfig.of(modsTomlPath);
         fileConfig.load();
         fileConfig.close();
         return fileConfig.getOptional("properties").map(map -> map.contains("connector:placeholder")).orElse(false);
      } catch (Throwable t) {
         LOGGER.error("Error reading placeholder information from {}", modsTomlPath, t);
         return false;
      }
   }
}
