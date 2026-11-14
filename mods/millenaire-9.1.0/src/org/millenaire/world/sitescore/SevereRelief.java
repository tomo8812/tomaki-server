package org.millenaire.world.sitescore;

import java.util.Arrays;
import java.util.function.BiFunction;

public final class SevereRelief {
   public static final int RADIUS = 50;
   public static final int DEVIATION = 20;
   public static final int MAX_SAMPLES = 5;

   private SevereRelief() {
   }

   public static boolean ok(int severeSamples) {
      return severeSamples <= 5;
   }

   public static int count(BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int x, int z) {
      return count(lookup, x, z, 50, 20);
   }

   public static int count(BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int x, int z, int radius, int deviation) {
      int n = 0;
      int[] heights = new int[64];
      int centre = 0;
      double nearest = Double.MAX_VALUE;

      for (int cx = x - radius >> 4; cx <= x + radius >> 4; cx++) {
         for (int cz = z - radius >> 4; cz <= z + radius >> 4; cz++) {
            ChunkTerrainSummary s = lookup.apply(cx, cz);
            if (s != null) {
               for (int j = 0; j < 4; j++) {
                  for (int i = 0; i < 4; i++) {
                     if (!s.isWater(i, j)) {
                        int sx = (cx << 4) + 2 + 4 * i;
                        int sz = (cz << 4) + 2 + 4 * j;
                        double d = Math.hypot(sx - x, sz - z);
                        if (!(d > radius)) {
                           if (n == heights.length) {
                              heights = Arrays.copyOf(heights, n * 2);
                           }

                           heights[n++] = s.height(i, j);
                           if (d < nearest) {
                              nearest = d;
                              centre = s.height(i, j);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      int severe = 0;

      for (int k = 0; k < n; k++) {
         if (Math.abs(heights[k] - centre) > deviation) {
            severe++;
         }
      }

      return severe;
   }
}
