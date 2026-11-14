package org.millenaire.world.sitescore;

import java.util.Arrays;
import java.util.function.BiFunction;

public final class CoreSpread {
   public static final int RADIUS = 40;

   private CoreSpread() {
   }

   public static int of(BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int x, int z) {
      int[] land = new int[128];
      int n = 0;

      for (int cx = x - 40 >> 4; cx <= x + 40 >> 4; cx++) {
         for (int cz = z - 40 >> 4; cz <= z + 40 >> 4; cz++) {
            ChunkTerrainSummary s = lookup.apply(cx, cz);
            if (s != null) {
               for (int j = 0; j < 4; j++) {
                  for (int i = 0; i < 4; i++) {
                     if (!s.isWater(i, j) && !s.isLava(i, j)) {
                        int sx = (cx << 4) + 2 + 4 * i;
                        int sz = (cz << 4) + 2 + 4 * j;
                        if (!(Math.hypot(sx - x, sz - z) > 40.0)) {
                           if (n == land.length) {
                              land = Arrays.copyOf(land, n * 2);
                           }

                           land[n++] = s.height(i, j);
                        }
                     }
                  }
               }
            }
         }
      }

      if (n == 0) {
         return 0;
      }

      Arrays.sort(land, 0, n);
      return land[(int)(0.9 * (n - 1))] - land[(int)(0.1 * (n - 1))];
   }
}
