package net.fabricmc.loader.impl.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class LoaderUtil {
   public static final String RUNTIME_MAPPING = "mojang";
   private static final ConcurrentMap<Path, Path> pathNormalizationCache = new ConcurrentHashMap<>();

   public static String getClassFileName(String className) {
      return className.replace('.', '/').concat(".class");
   }

   public static Path normalizePath(Path path) {
      return Files.exists(path) ? normalizeExistingPath(path) : path.toAbsolutePath().normalize();
   }

   public static Path normalizeExistingPath(Path path) {
      return pathNormalizationCache.computeIfAbsent(path, LoaderUtil::normalizeExistingPath0);
   }

   private static Path normalizeExistingPath0(Path path) {
      try {
         return path.toRealPath();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public static boolean hasMacOs() {
      return System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac");
   }

   public static boolean hasAwtSupport() {
      if (hasMacOs()) {
         for (String key : System.getenv().keySet()) {
            if (key.startsWith("JAVA_STARTED_ON_FIRST_THREAD_")) {
               return false;
            }
         }
      }

      return true;
   }
}
