package com.kishku7.chunksmith.util;

import com.kishku7.chunksmith.nbt.StringTag;
import com.kishku7.chunksmith.nbt.util.Chunk;
import com.kishku7.chunksmith.nbt.util.ChunkFilter;
import com.kishku7.chunksmith.nbt.util.RegionFile;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GeneratedChunkScan {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");

   private GeneratedChunkScan() {
   }

   public static long seed(Path regionDirectory, RegionCache.WorldState state, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
      File dir = regionDirectory.toFile();
      File[] files = dir.listFiles((d, name) -> name.startsWith("r.") && name.endsWith(".mca"));
      if (files != null && files.length != 0) {
         int minRegionX = minChunkX >> 5;
         int minRegionZ = minChunkZ >> 5;
         int maxRegionX = maxChunkX >> 5;
         int maxRegionZ = maxChunkZ >> 5;
         long seeded = 0L;
         int scanned = 0;
         int unreadable = 0;

         for (File file : files) {
            int[] coords = regionCoordinates(file.getName());
            if (coords != null && coords[0] >= minRegionX && coords[0] <= maxRegionX && coords[1] >= minRegionZ && coords[1] <= maxRegionZ) {
               try {
                  RegionFile region = new RegionFile(file, ChunkFilter.of((byte)8, "Status"));
                  scanned++;

                  for (Chunk chunk : region.getChunks()) {
                     int cx = chunk.getX();
                     int cz = chunk.getZ();
                     if (cx >= minChunkX && cx <= maxChunkX && cz >= minChunkZ && cz <= maxChunkZ && isFull(chunk)) {
                        state.setGenerated(cx, cz);
                        seeded++;
                     }
                  }
               } catch (Throwable t) {
                  unreadable++;
               }
            }
         }

         if (unreadable > 0) {
            LOGGER.info(
               "Chunksmith: pre-scan read " + scanned + " region file(s), " + unreadable + " unreadable (those chunks will be checked individually as before)"
            );
         }

         return seeded;
      } else {
         return 0L;
      }
   }

   private static int[] regionCoordinates(String name) {
      int end = name.indexOf(".mca");
      if (end < 2) {
         return null;
      }

      String middle = name.substring(2, end);
      int dot = middle.indexOf(46);
      if (dot < 1) {
         return null;
      }

      try {
         return new int[]{Integer.parseInt(middle.substring(0, dot)), Integer.parseInt(middle.substring(dot + 1))};
      } catch (NumberFormatException e) {
         return null;
      }
   }

   private static boolean isFull(Chunk chunk) {
      return Optional.ofNullable(chunk.getData())
         .filter(StringTag.class::isInstance)
         .map(StringTag.class::cast)
         .map(StringTag::value)
         .map(status -> "minecraft:full".equals(status) || "full".equals(status))
         .orElse(false);
   }
}
