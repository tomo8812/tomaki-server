package org.millenaire.world.sitescore;

import java.util.function.BiFunction;

public final class EdgeWater {
   public static final int RING = 10;
   public static final float FREE = 0.1F;

   private EdgeWater() {
   }

   public static float share(BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int centreX, int centreZ, int outer) {
      int inner = outer - 10;
      int water = 0;
      int total = 0;

      for (int cx = centreX - outer >> 4; cx <= centreX + outer >> 4; cx++) {
         for (int cz = centreZ - outer >> 4; cz <= centreZ + outer >> 4; cz++) {
            ChunkTerrainSummary s = lookup.apply(cx, cz);
            if (s != null) {
               for (int j = 0; j < 4; j++) {
                  for (int i = 0; i < 4; i++) {
                     int x = (cx << 4) + 2 + 4 * i;
                     int z = (cz << 4) + 2 + 4 * j;
                     int d = Math.max(Math.abs(x - centreX), Math.abs(z - centreZ));
                     if (d > inner && d <= outer) {
                        total++;
                        if (s.isWater(i, j)) {
                           water++;
                        }
                     }
                  }
               }
            }
         }
      }

      return total == 0 ? Float.NaN : (float)water / total;
   }

   public static float factor(float share) {
      return Float.isNaN(share) ? 1.0F : Math.max(0.0F, 1.0F - Math.max(0.0F, share - 0.1F));
   }
}
