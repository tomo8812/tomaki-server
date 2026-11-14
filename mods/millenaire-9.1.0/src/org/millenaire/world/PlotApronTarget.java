package org.millenaire.world;

public final class PlotApronTarget {
   private static final int COARSE_CELL = 6;
   private static final int FINE_CELL = 2;
   private static final double ONE_BLOCK = 0.45;
   private static final double TWO_BLOCKS = 0.68;
   static final double ROUGH_SHARE = 0.15;
   static final int FREE = Integer.MIN_VALUE;

   private PlotApronTarget() {
   }

   static int[][] compute(int[][] natural, int[][] tolerance, boolean[][] inside, boolean[][] rough, int baseY) {
      return compute(natural, tolerance, inside, rough, null, baseY);
   }

   static int[][] compute(int[][] natural, int[][] tolerance, boolean[][] inside, boolean[][] rough, int[][] pinned, int baseY) {
      int w = natural.length;
      int d = natural[0].length;
      int[][] h = new int[w][d];

      for (int i = 0; i < w; i++) {
         for (int j = 0; j < d; j++) {
            h[i][j] = inside[i][j]
               ? baseY
               : (isPinned(pinned, i, j) ? pinned[i][j] : Math.max(baseY - tolerance[i][j], Math.min(baseY + tolerance[i][j], natural[i][j])));
         }
      }

      boolean changed = true;

      while (changed) {
         changed = false;

         for (int i = 0; i < w; i++) {
            for (int j = 0; j < d; j++) {
               if (!inside[i][j] && !isPinned(pinned, i, j)) {
                  int v = h[i][j];
                  int ceiling = Integer.MAX_VALUE;
                  int floor = Integer.MIN_VALUE;
                  if (i > 0) {
                     ceiling = Math.min(ceiling, h[i - 1][j] + limit(rough, i, j, i - 1, j));
                     floor = Math.max(floor, h[i - 1][j] - limit(rough, i, j, i - 1, j));
                  }

                  if (i < w - 1) {
                     ceiling = Math.min(ceiling, h[i + 1][j] + limit(rough, i, j, i + 1, j));
                     floor = Math.max(floor, h[i + 1][j] - limit(rough, i, j, i + 1, j));
                  }

                  if (j > 0) {
                     ceiling = Math.min(ceiling, h[i][j - 1] + limit(rough, i, j, i, j - 1));
                     floor = Math.max(floor, h[i][j - 1] - limit(rough, i, j, i, j - 1));
                  }

                  if (j < d - 1) {
                     ceiling = Math.min(ceiling, h[i][j + 1] + limit(rough, i, j, i, j + 1));
                     floor = Math.max(floor, h[i][j + 1] - limit(rough, i, j, i, j + 1));
                  }

                  if (v > baseY && v > ceiling) {
                     v = Math.max(baseY, ceiling);
                  } else if (v < baseY && v < floor) {
                     v = Math.min(baseY, floor);
                  }

                  if (v != h[i][j]) {
                     h[i][j] = v;
                     changed = true;
                  }
               }
            }
         }
      }

      return h;
   }

   private static boolean isPinned(int[][] pinned, int i, int j) {
      return pinned != null && pinned[i][j] != Integer.MIN_VALUE;
   }

   private static int limit(boolean[][] rough, int i, int j, int ni, int nj) {
      return rough == null || !rough[i][j] && !rough[ni][nj] ? 1 : 2;
   }

   public static int noise(int x, int z) {
      double v = 0.6 * valueNoise(x, z, 6, 0) + 0.4 * valueNoise(x, z, 2, 1);
      return v > 0.68 ? 2 : (v > 0.45 ? 1 : 0);
   }

   public static boolean rough(int x, int z) {
      double own = hash01(x, z, 2);
      if (own >= 0.15) {
         return false;
      }

      for (int dx = -1; dx <= 1; dx++) {
         for (int dz = -1; dz <= 1; dz++) {
            if (dx != 0 || dz != 0) {
               double other = hash01(x + dx, z + dz, 2);
               if (other < 0.15 && other <= own) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private static double valueNoise(int x, int z, int cell, int salt) {
      int x0 = Math.floorDiv(x, cell);
      int z0 = Math.floorDiv(z, cell);
      double fx = (double)(x - x0 * cell) / cell;
      double fz = (double)(z - z0 * cell) / cell;
      double top = lerp(hash01(x0, z0, salt), hash01(x0 + 1, z0, salt), fx);
      double bottom = lerp(hash01(x0, z0 + 1, salt), hash01(x0 + 1, z0 + 1, salt), fx);
      return lerp(top, bottom, fz);
   }

   private static double lerp(double a, double b, double t) {
      return a + (b - a) * t;
   }

   private static double hash01(int x, int z, int salt) {
      long h = x * -7046029254386353131L ^ z * -4417276706812531889L ^ salt * 1609587929392839161L;
      h = (h ^ h >>> 30) * -4658895280553007687L;
      h = (h ^ h >>> 27) * -7723592293110705685L;
      h ^= h >>> 31;
      return (h >>> 11) * 1.110223E-16F;
   }
}
