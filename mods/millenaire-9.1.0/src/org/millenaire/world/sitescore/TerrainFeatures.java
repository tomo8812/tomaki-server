package org.millenaire.world.sitescore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public record TerrainFeatures(
   float bigWaterFraction,
   boolean bigWaterAtEdge,
   int waterBodies,
   int smallWaterSamples,
   float bigLavaFraction,
   int largeRange,
   int largeMaxSlope,
   float largeSteepFraction,
   int deepestPit,
   int biggestPitSamples,
   boolean ravine,
   int hillCutBlocks,
   int coverBlocks,
   float blockerFraction,
   float connectedUnblocked
) {
   static final int SMOOTH_RADIUS = 4;
   static final int BIG_BODY_SAMPLES = 36;
   static final int EDGE_BODY_SAMPLES = 8;
   static final int PIT_DEPTH = 3;
   static final int BIG_PIT_SAMPLES = 24;
   static final int DEEP_PIT = 16;
   static final int HILL_TOLERANCE = 2;
   static final int STEEP_LARGE_SLOPE = 3;
   private static final int BLOCKS_PER_SAMPLE = 16;

   public static TerrainFeatures of(int[][] h, boolean[][] water, boolean[][] lava, int ci, int cj) {
      int n = h.length;
      int total = n * n;
      boolean[][] blocker = new boolean[n][n];
      TerrainFeatures.Bodies wet = bodies(water, 36, 8, blocker);
      TerrainFeatures.Bodies hot = bodies(lava, 36, Integer.MAX_VALUE, blocker);
      double[][] smooth = smooth(h, 4);
      double sMin = Double.MAX_VALUE;
      double sMax = -Double.MAX_VALUE;
      int maxStep = 0;
      int steep = 0;

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            sMin = Math.min(sMin, smooth[i][j]);
            sMax = Math.max(sMax, smooth[i][j]);
            int step = (int)Math.round(largeStep(smooth, i, j));
            maxStep = Math.max(maxStep, step);
            if (step > 3) {
               steep++;
               blocker[i][j] = true;
            }
         }
      }

      boolean[][] pit = new boolean[n][n];
      int deepest = 0;
      int hillCut = 0;

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            if (!water[i][j] && !lava[i][j]) {
               boolean interior = i >= 4 && j >= 4 && i < n - 4 && j < n - 4;
               if (interior) {
                  double r = h[i][j] - smooth[i][j];
                  if (r <= -3.0) {
                     pit[i][j] = true;
                     deepest = Math.max(deepest, (int)Math.round(-r));
                  } else if (r > 2.0) {
                     hillCut += (int)Math.round((r - 2.0) * 16.0);
                  }
               }
            }
         }
      }

      boolean[][] pitBlocker = new boolean[n][n];
      int biggestPit = 0;
      int fixablePitSamples = 0;
      boolean ravine = false;
      boolean[][] seen = new boolean[n][n];

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            if (pit[i][j] && !seen[i][j]) {
               List<int[]> comp = component(pit, seen, i, j);
               int depth = 0;
               boolean byWater = false;

               for (int[] p : comp) {
                  depth = Math.max(depth, (int)Math.round(smooth[p[0]][p[1]] - h[p[0]][p[1]]));
                  if (!byWater) {
                     byWater = touches(water, p[0], p[1]);
                  }
               }

               biggestPit = Math.max(biggestPit, comp.size());
               if (comp.size() >= 24 && depth >= 16 && !byWater) {
                  ravine = true;

                  for (int[] p : comp) {
                     pitBlocker[p[0]][p[1]] = true;
                  }
               } else {
                  fixablePitSamples += comp.size();
               }
            }
         }
      }

      int blockers = 0;

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            if (pitBlocker[i][j]) {
               blocker[i][j] = true;
            }

            if (blocker[i][j]) {
               blockers++;
            }
         }
      }

      boolean[][] open = new boolean[n][n];

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            open[i][j] = !blocker[i][j];
         }
      }

      int connected = SiteMeasures.connectedFrom(open, ci, cj);
      int cover = (fixablePitSamples + wet.smallSamples + hot.smallSamples) * 16;
      return new TerrainFeatures(
         (float)wet.bigSamples / total,
         wet.bigAtEdge,
         wet.count,
         wet.smallSamples,
         (float)hot.bigSamples / total,
         (int)Math.round(sMax - sMin),
         maxStep,
         (float)steep / total,
         deepest,
         biggestPit,
         ravine,
         hillCut,
         cover,
         (float)blockers / total,
         (float)connected / total
      );
   }

   private static TerrainFeatures.Bodies bodies(boolean[][] mask, int bigMin, int edgeMin, boolean[][] blocker) {
      int n = mask.length;
      boolean[][] seen = new boolean[n][n];
      int count = 0;
      int big = 0;
      int small = 0;
      boolean bigAtEdge = false;

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            if (mask[i][j] && !seen[i][j]) {
               List<int[]> comp = component(mask, seen, i, j);
               count++;
               boolean edge = false;

               for (int[] p : comp) {
                  if (p[0] == 0 || p[1] == 0 || p[0] == n - 1 || p[1] == n - 1) {
                     edge = true;
                     break;
                  }
               }

               if (comp.size() >= bigMin || edge && comp.size() >= edgeMin) {
                  big += comp.size();
                  if (edge) {
                     bigAtEdge = true;
                  }

                  for (int[] p : comp) {
                     blocker[p[0]][p[1]] = true;
                  }
               } else {
                  small += comp.size();
               }
            }
         }
      }

      return new TerrainFeatures.Bodies(count, big, small, bigAtEdge);
   }

   private static List<int[]> component(boolean[][] mask, boolean[][] seen, int i, int j) {
      int n = mask.length;
      List<int[]> out = new ArrayList<>();
      ArrayDeque<int[]> queue = new ArrayDeque<>();
      queue.add(new int[]{i, j});
      seen[i][j] = true;
      int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

      while (!queue.isEmpty()) {
         int[] p = queue.poll();
         out.add(p);

         for (int[] d : dirs) {
            int a = p[0] + d[0];
            int b = p[1] + d[1];
            if (a >= 0 && b >= 0 && a < n && b < n && !seen[a][b] && mask[a][b]) {
               seen[a][b] = true;
               queue.add(new int[]{a, b});
            }
         }
      }

      return out;
   }

   private static boolean touches(boolean[][] mask, int i, int j) {
      int n = mask.length;
      return i > 0 && mask[i - 1][j] || i < n - 1 && mask[i + 1][j] || j > 0 && mask[i][j - 1] || j < n - 1 && mask[i][j + 1];
   }

   static double[][] smooth(int[][] h, int radius) {
      int n = h.length;
      double[][] out = new double[n][n];

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            double sum = 0.0;
            int count = 0;

            for (int a = Math.max(0, i - radius); a <= Math.min(n - 1, i + radius); a++) {
               for (int b = Math.max(0, j - radius); b <= Math.min(n - 1, j + radius); b++) {
                  sum += h[a][b];
                  count++;
               }
            }

            out[i][j] = sum / count;
         }
      }

      return out;
   }

   private static double largeStep(double[][] s, int i, int j) {
      int n = s.length;
      double v = s[i][j];
      double step = 0.0;
      if (i > 0) {
         step = Math.max(step, Math.abs(v - s[i - 1][j]));
      }

      if (i < n - 1) {
         step = Math.max(step, Math.abs(v - s[i + 1][j]));
      }

      if (j > 0) {
         step = Math.max(step, Math.abs(v - s[i][j - 1]));
      }

      if (j < n - 1) {
         step = Math.max(step, Math.abs(v - s[i][j + 1]));
      }

      return step;
   }

   private record Bodies(int count, int bigSamples, int smallSamples, boolean bigAtEdge) {
   }
}
