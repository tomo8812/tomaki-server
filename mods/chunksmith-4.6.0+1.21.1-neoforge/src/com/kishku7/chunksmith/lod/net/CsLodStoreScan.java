package com.kishku7.chunksmith.lod.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class CsLodStoreScan {
   public static final String REGION_SUFFIX = ".cslod";
   public static final long SETTLE_MILLIS = 10000L;

   private CsLodStoreScan() {
   }

   public static boolean isSettled(Path file, long nowMillis) {
      try {
         return nowMillis - Files.getLastModifiedTime(file).toMillis() >= 10000L;
      } catch (IOException e) {
         return false;
      }
   }

   public static boolean hasData(Path dimensionDir, long nowMillis) {
      if (dimensionDir != null && Files.isDirectory(dimensionDir)) {
         try (Stream<Path> entries = Files.list(dimensionDir)) {
            return entries.anyMatch(file -> isRegionFile(file) && isSettled(file, nowMillis));
         } catch (IOException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static List<String> servable(List<Path> dimensionDirs, long nowMillis) {
      List<String> names = new ArrayList<>();
      if (dimensionDirs == null) {
         return names;
      }

      for (Path dir : dimensionDirs) {
         if (hasData(dir, nowMillis)) {
            names.add(dir.getFileName().toString());
         }
      }

      return names;
   }

   public static boolean isRegionFile(Path file) {
      return file.getFileName().toString().endsWith(".cslod") && Files.isRegularFile(file);
   }
}
