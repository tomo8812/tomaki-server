package org.millenaire.world.sitescore;

import java.util.ArrayDeque;
import java.util.Locale;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

public record SiteMeasures(
   int samples,
   int minY,
   int maxY,
   int centreY,
   float bandFraction,
   float waterFraction,
   float lavaFraction,
   float gentle1Fraction,
   float gentle2Fraction,
   float gentle3Fraction,
   boolean centreUsable,
   float centreWeightedGentle2,
   float connectedGentle2,
   @Nullable SiteMeasures.LonePad lonePad,
   @Nullable TerrainFeatures features
) {
   public static final int BAND = 10;
   static volatile int seaLevel = 63;
   public static final float MIN_GENTLE2 = prop("millenaire.siteGentle", 0.7F);
   public static final float MIN_CENTRE_WEIGHTED = prop("millenaire.siteWeighted", 0.8F);
   public static final float MIN_CONNECTED = prop("millenaire.siteConnected", 0.6F);
   public static final float MAX_WATER = prop("millenaire.siteWater", 0.13671875F);
   public static final float MAX_LAVA = prop("millenaire.siteLava", 0.015625F);
   public static final float NEAR_MARGIN = prop("millenaire.siteNearMargin", 0.1F);

   private static float prop(String name, float fallback) {
      String v = System.getProperty(name);
      if (v != null && !v.isBlank()) {
         try {
            return Float.parseFloat(v.trim());
         } catch (NumberFormatException e) {
            return fallback;
         }
      } else {
         return fallback;
      }
   }

   public static String thresholds() {
      return String.format(
         Locale.ROOT,
         "gentle2>=%.2f centre_weighted>=%.2f connected>=%.2f water<%.3f lava<%.3f near_margin=%.2f lone_pad_usable>=%.2f lone_pad_range<=%.0f",
         MIN_GENTLE2,
         MIN_CENTRE_WEIGHTED,
         MIN_CONNECTED,
         MAX_WATER,
         MAX_LAVA,
         NEAR_MARGIN,
         SiteMeasures.LonePad.MIN_USABLE,
         SiteMeasures.LonePad.MAX_RANGE
      );
   }

   public int heightRange() {
      return this.maxY - this.minY;
   }

   public boolean gentleOk() {
      return this.gentle2Fraction >= MIN_GENTLE2;
   }

   public boolean centreWeightedOk() {
      return this.centreWeightedGentle2 >= MIN_CENTRE_WEIGHTED;
   }

   public boolean connectedOk() {
      return this.connectedGentle2 >= MIN_CONNECTED;
   }

   public boolean waterOk() {
      return this.waterFraction < MAX_WATER;
   }

   public boolean lavaOk() {
      return this.lavaFraction < MAX_LAVA;
   }

   public boolean passed() {
      return this.lonePad != null
         ? this.lonePad.passed()
         : this.centreUsable && this.gentleOk() && this.centreWeightedOk() && this.connectedOk() && this.waterOk() && this.lavaOk();
   }

   public boolean nearMiss() {
      return this.lonePad != null
         ? this.lonePad.nearMiss()
         : !this.passed()
            && this.centreUsable
            && this.waterOk()
            && this.lavaOk()
            && this.gentle2Fraction >= MIN_GENTLE2 - NEAR_MARGIN
            && this.centreWeightedGentle2 >= MIN_CENTRE_WEIGHTED - NEAR_MARGIN
            && this.connectedGentle2 >= MIN_CONNECTED - NEAR_MARGIN;
   }

   public String goodMeasures() {
      StringBuilder sb = new StringBuilder();
      if (this.lonePad != null) {
         if (this.lonePad.usableOk()) {
            sb.append("pad_usable;");
         }

         if (this.lonePad.rangeOk()) {
            sb.append("pad_range;");
         }

         return sb.isEmpty() ? "" : sb.substring(0, sb.length() - 1);
      } else {
         if (this.centreUsable) {
            sb.append("centre;");
         }

         if (this.gentleOk()) {
            sb.append("gentle2;");
         }

         if (this.centreWeightedOk()) {
            sb.append("centre_weighted;");
         }

         if (this.connectedOk()) {
            sb.append("connected;");
         }

         if (this.waterOk()) {
            sb.append("water;");
         }

         if (this.lavaOk()) {
            sb.append("lava;");
         }

         return sb.isEmpty() ? "" : sb.substring(0, sb.length() - 1);
      }
   }

   @Nullable
   public static SiteMeasures fromSummaries(BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int centreX, int centreZ, int windowChunks) {
      SiteMeasures.Grid g = grid(lookup, centreX, centreZ, windowChunks);
      return g == null ? null : of(g.h, g.water, g.lava, g.ci, g.cj).withFeatures(TerrainFeatures.of(g.h, g.water, g.lava, g.ci, g.cj));
   }

   static int cropHalf(int radius) {
      return Math.max(1, (radius - 2) / 4);
   }

   @Nullable
   public static SiteMeasures fromSummariesCropped(
      BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int centreX, int centreZ, int windowChunks, int radius
   ) {
      SiteMeasures.Grid g = grid(lookup, centreX, centreZ, windowChunks);
      if (g == null) {
         return null;
      }

      int rs = cropHalf(radius);
      SiteMeasures.Grid c = g.crop(rs);
      return of(c.h, c.water, c.lava, c.ci, c.cj).withFeatures(TerrainFeatures.of(c.h, c.water, c.lava, c.ci, c.cj));
   }

   @Nullable
   public static SiteMeasures fromSummariesCroppedFast(
      BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int centreX, int centreZ, int windowChunks, int radius
   ) {
      SiteMeasures.Grid g = grid(lookup, centreX, centreZ, windowChunks);
      if (g == null) {
         return null;
      }

      SiteMeasures.Grid c = g.crop(cropHalf(radius));
      return of(c.h, c.water, c.lava, c.ci, c.cj);
   }

   @Nullable
   public static SiteMeasures fromSummariesLone(
      BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int centreX, int centreZ, int windowChunks, int radius, int footprintW, int footprintD
   ) {
      SiteMeasures.Grid g = grid(lookup, centreX, centreZ, windowChunks);
      if (g == null) {
         return null;
      }

      int rs = cropHalf(radius);
      SiteMeasures.Grid crop = g.crop(rs);
      SiteMeasures area = of(crop.h, crop.water, crop.lava, crop.ci, crop.cj);
      int padW = Math.max(1, (int)Math.ceil(footprintW / 4.0F));
      int padD = Math.max(1, (int)Math.ceil(footprintD / 4.0F));
      SiteMeasures.LonePad pad = bestPad(crop.h, usableGrid(crop.h, crop.water, crop.lava), crop.ci, crop.cj, padW, padD, rs);
      return area.withLonePad(pad).withFeatures(TerrainFeatures.of(crop.h, crop.water, crop.lava, crop.ci, crop.cj));
   }

   SiteMeasures withFeatures(TerrainFeatures f) {
      return new SiteMeasures(
         this.samples,
         this.minY,
         this.maxY,
         this.centreY,
         this.bandFraction,
         this.waterFraction,
         this.lavaFraction,
         this.gentle1Fraction,
         this.gentle2Fraction,
         this.gentle3Fraction,
         this.centreUsable,
         this.centreWeightedGentle2,
         this.connectedGentle2,
         this.lonePad,
         f
      );
   }

   private SiteMeasures withLonePad(SiteMeasures.LonePad pad) {
      return new SiteMeasures(
         this.samples,
         this.minY,
         this.maxY,
         this.centreY,
         this.bandFraction,
         this.waterFraction,
         this.lavaFraction,
         this.gentle1Fraction,
         this.gentle2Fraction,
         this.gentle3Fraction,
         this.centreUsable,
         this.centreWeightedGentle2,
         this.connectedGentle2,
         pad,
         this.features
      );
   }

   static boolean[][] usableGrid(int[][] h, boolean[][] water, boolean[][] lava) {
      int n = h.length;
      boolean[][] usable = new boolean[n][n];

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            usable[i][j] = !water[i][j] && !lava[i][j] && localSlope(h, i, j) <= 2;
         }
      }

      return usable;
   }

   static SiteMeasures.LonePad bestPad(int[][] h, boolean[][] usable, int ci, int cj, int padW, int padD, int maxDist) {
      int n = h.length;
      SiteMeasures.LonePad best = SiteMeasures.LonePad.NONE;
      int[][] sizes = padW == padD ? new int[][]{{padW, padD}} : new int[][]{{padW, padD}, {padD, padW}};

      for (int[] size : sizes) {
         int pw = size[0];
         int pd = size[1];

         for (int i0 = 0; i0 + pw <= n; i0++) {
            for (int j0 = 0; j0 + pd <= n; j0++) {
               double pci = i0 + (pw - 1) / 2.0;
               double pcj = j0 + (pd - 1) / 2.0;
               double dist = Math.hypot(pci - ci, pcj - cj);
               if (!(dist > maxDist)) {
                  int good = 0;
                  int lo = Integer.MAX_VALUE;
                  int hi = Integer.MIN_VALUE;

                  for (int i = i0; i < i0 + pw; i++) {
                     for (int j = j0; j < j0 + pd; j++) {
                        if (usable[i][j]) {
                           good++;
                        }

                        lo = Math.min(lo, h[i][j]);
                        hi = Math.max(hi, h[i][j]);
                     }
                  }

                  SiteMeasures.LonePad pad = new SiteMeasures.LonePad(pw, pd, (float)good / (pw * pd), hi - lo, (int)Math.round(dist * 4.0));
                  if (better(pad, best)) {
                     best = pad;
                  }
               }
            }
         }
      }

      return best;
   }

   private static boolean better(SiteMeasures.LonePad a, SiteMeasures.LonePad b) {
      if (a.bestUsable() != b.bestUsable()) {
         return a.bestUsable() > b.bestUsable();
      } else {
         return a.range() != b.range() ? a.range() < b.range() : b.distance() < 0 || a.distance() < b.distance();
      }
   }

   @Nullable
   static SiteMeasures.Grid grid(BiFunction<Integer, Integer, ChunkTerrainSummary> lookup, int centreX, int centreZ, int windowChunks) {
      int ccx = centreX >> 4;
      int ccz = centreZ >> 4;
      int chunks = 2 * windowChunks + 1;
      int n = chunks * 4;
      int[][] h = new int[n][n];
      boolean[][] water = new boolean[n][n];
      boolean[][] lava = new boolean[n][n];

      for (int dz = 0; dz < chunks; dz++) {
         for (int dx = 0; dx < chunks; dx++) {
            ChunkTerrainSummary s = lookup.apply(ccx - windowChunks + dx, ccz - windowChunks + dz);
            if (s == null) {
               return null;
            }

            for (int j = 0; j < 4; j++) {
               for (int i = 0; i < 4; i++) {
                  int gi = dx * 4 + i;
                  int gj = dz * 4 + j;
                  h[gi][gj] = s.height(i, j);
                  water[gi][gj] = s.isWater(i, j);
                  lava[gi][gj] = s.isLava(i, j);
               }
            }
         }
      }

      int originX = ccx - windowChunks << 4;
      int originZ = ccz - windowChunks << 4;
      int ci = nearestSample(centreX - originX, n);
      int cj = nearestSample(centreZ - originZ, n);
      return new SiteMeasures.Grid(h, water, lava, ci, cj);
   }

   static int nearestSample(int localBlock, int n) {
      int idx = Math.round((localBlock - 2) / 4.0F);
      return Math.max(0, Math.min(n - 1, idx));
   }

   public static SiteMeasures of(int[][] h, boolean[][] water, boolean[][] lava, int ci, int cj) {
      int n = h.length;
      int total = n * n;
      int centreY = h[ci][cj];
      int bandMin = Math.max(centreY - 10, seaLevel);
      int bandMax = centreY + 10;
      int minY = Integer.MAX_VALUE;
      int maxY = Integer.MIN_VALUE;
      int band = 0;
      int wet = 0;
      int hot = 0;
      int g1 = 0;
      int g2 = 0;
      int g3 = 0;
      int[][] slope = new int[n][n];
      double weighted = 0.0;
      double weightSum = 0.0;
      double rim = n / 2.0 + 1.0;

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            int y = h[i][j];
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            boolean hazard = water[i][j] || lava[i][j];
            if (water[i][j]) {
               wet++;
            }

            if (lava[i][j]) {
               hot++;
            }

            if (!hazard && y >= bandMin && y <= bandMax) {
               band++;
            }

            int s = localSlope(h, i, j);
            slope[i][j] = s;
            if (!hazard && s <= 1) {
               g1++;
            }

            if (!hazard && s <= 2) {
               g2++;
            }

            if (!hazard && s <= 3) {
               g3++;
            }

            double w = Math.max(0.0, 1.0 - Math.hypot(i - ci, j - cj) / rim);
            weightSum += w;
            if (!hazard && s <= 2) {
               weighted += w;
            }
         }
      }

      boolean[][] usable = new boolean[n][n];

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            usable[i][j] = !water[i][j] && !lava[i][j] && slope[i][j] <= 2;
         }
      }

      int connected = connectedFrom(usable, ci, cj);
      return new SiteMeasures(
         total,
         minY,
         maxY,
         centreY,
         (float)band / total,
         (float)wet / total,
         (float)hot / total,
         (float)g1 / total,
         (float)g2 / total,
         (float)g3 / total,
         usable[ci][cj],
         weightSum > 0.0 ? (float)(weighted / weightSum) : 0.0F,
         (float)connected / total,
         null,
         null
      );
   }

   static int localSlope(int[][] h, int i, int j) {
      int n = h.length;
      int y = h[i][j];
      int s = 0;
      if (i > 0) {
         s = Math.max(s, Math.abs(y - h[i - 1][j]));
      }

      if (i < n - 1) {
         s = Math.max(s, Math.abs(y - h[i + 1][j]));
      }

      if (j > 0) {
         s = Math.max(s, Math.abs(y - h[i][j - 1]));
      }

      if (j < n - 1) {
         s = Math.max(s, Math.abs(y - h[i][j + 1]));
      }

      return s;
   }

   static int connectedFrom(boolean[][] usable, int ci, int cj) {
      int n = usable.length;
      int si = -1;
      int sj = -1;
      if (usable[ci][cj]) {
         si = ci;
         sj = cj;
      } else {
         double best = Double.MAX_VALUE;

         for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
               if (usable[i][j]) {
                  double d = Math.hypot(i - ci, j - cj);
                  if (d < best) {
                     best = d;
                     si = i;
                     sj = j;
                  }
               }
            }
         }
      }

      if (si < 0) {
         return 0;
      }

      boolean[][] seen = new boolean[n][n];
      ArrayDeque<int[]> queue = new ArrayDeque<>();
      queue.add(new int[]{si, sj});
      seen[si][sj] = true;
      int count = 0;
      int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

      while (!queue.isEmpty()) {
         int[] p = queue.poll();
         count++;

         for (int[] d : dirs) {
            int ni = p[0] + d[0];
            int nj = p[1] + d[1];
            if (ni >= 0 && nj >= 0 && ni < n && nj < n && !seen[ni][nj] && usable[ni][nj]) {
               seen[ni][nj] = true;
               queue.add(new int[]{ni, nj});
            }
         }
      }

      return count;
   }

   record Grid(int[][] h, boolean[][] water, boolean[][] lava, int ci, int cj) {
      SiteMeasures.Grid crop(int half) {
         int n = this.h.length;
         int hiI = Math.min(n - 1, this.ci + half);
         int hiJ = Math.min(n - 1, this.cj + half);
         int i0 = Math.max(0, this.ci - half);
         int j0 = Math.max(0, this.cj - half);
         int size = Math.min(hiI - i0, hiJ - j0) + 1;
         int[][] ch = new int[size][size];
         boolean[][] cw = new boolean[size][size];
         boolean[][] cl = new boolean[size][size];

         for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
               ch[i][j] = this.h[i0 + i][j0 + j];
               cw[i][j] = this.water[i0 + i][j0 + j];
               cl[i][j] = this.lava[i0 + i][j0 + j];
            }
         }

         return new SiteMeasures.Grid(ch, cw, cl, this.ci - i0, this.cj - j0);
      }
   }

   public record LonePad(int padW, int padD, float bestUsable, int range, int distance) {
      public static final float MIN_USABLE = SiteMeasures.prop("millenaire.siteLonePadUsable", 1.0F);
      public static final float MAX_RANGE = SiteMeasures.prop("millenaire.siteLonePadRange", 6.0F);
      static final int NEAR_EXTRA_RANGE = 3;
      static final SiteMeasures.LonePad NONE = new SiteMeasures.LonePad(0, 0, 0.0F, Integer.MAX_VALUE, -1);

      public boolean usableOk() {
         return this.bestUsable >= MIN_USABLE;
      }

      public boolean rangeOk() {
         return this.range <= MAX_RANGE;
      }

      public boolean passed() {
         return this.usableOk() && this.rangeOk();
      }

      public boolean nearMiss() {
         return !this.passed() && this.bestUsable >= MIN_USABLE - SiteMeasures.NEAR_MARGIN && this.range <= MAX_RANGE + 3.0F;
      }
   }
}
