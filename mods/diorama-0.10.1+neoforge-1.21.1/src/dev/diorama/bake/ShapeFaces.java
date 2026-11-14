package dev.diorama.bake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

final class ShapeFaces {
   static final int UNIT = 128;
   static final int FULL_LO = 0;
   static final int FULL_HI = pack(128, 128, 128);
   private static final Map<float[], ShapeFaces> CACHE = Collections.synchronizedMap(new WeakHashMap<>());
   final int[] top;
   final int[] south;
   final int[] boxes;
   final int[] wetTop;
   final int[] wetSouth;
   final boolean wetCut;
   private final int[][] boxBounds;
   private final Map<ShapeFaces.Pitch, int[][]> seen = new ConcurrentHashMap<>();

   static int pack(int x, int y, int z) {
      return x | y << 8 | z << 16;
   }

   static boolean inset(int hi, boolean top) {
      return (top ? hi >> 8 & 0xFF : hi >> 16 & 0xFF) < 128;
   }

   static ShapeFaces of(float[] boxes) {
      return CACHE.computeIfAbsent(boxes, ShapeFaces::new);
   }

   int[][] seen(double sin, double cos) {
      return this.seen
         .computeIfAbsent(new ShapeFaces.Pitch(sin, cos), k -> new int[][]{this.unhidden(this.top, true, sin, cos), this.unhidden(this.south, false, sin, cos)});
   }

   private int[] unhidden(int[] pieces, boolean top, double sin, double cos) {
      List<int[]> out = new ArrayList<>();

      for (int p = 0; p < pieces.length; p += 2) {
         int lo = pieces[p];
         int hi = pieces[p + 1];
         int plane = top ? hi >> 8 & 0xFF : hi >> 16 & 0xFF;
         List<int[]> left = new ArrayList<>(
            List.of(top ? new int[]{lo & 0xFF, hi & 0xFF, lo >> 16 & 0xFF, hi >> 16 & 0xFF} : new int[]{lo & 0xFF, hi & 0xFF, lo >> 8 & 0xFF, hi >> 8 & 0xFF})
         );

         for (int[] b : this.boxBounds) {
            if (top) {
               if (b[4] > plane) {
                  double t0 = Math.max(0.0, (b[1] - plane) / sin);
                  double t1 = (b[4] - plane) / sin;
                  subtract(left, b[0], b[3], (int)Math.floor(b[2] - t1 * cos), (int)Math.ceil(b[5] - t0 * cos));
               }
            } else if (b[5] > plane) {
               double t0 = Math.max(0.0, (b[2] - plane) / cos);
               double t1 = (b[5] - plane) / cos;
               subtract(left, b[0], b[3], (int)Math.floor(b[1] - t1 * sin), (int)Math.ceil(b[4] - t0 * sin));
            }
         }

         for (int[] r : left) {
            out.add(top ? new int[]{pack(r[0], plane, r[2]), pack(r[1], plane, r[3])} : new int[]{pack(r[0], r[2], plane), pack(r[1], r[3], plane)});
         }
      }

      return flatten(out);
   }

   private ShapeFaces(float[] shape) {
      int n = shape.length / 6;
      int[][] b = new int[n][6];

      for (int i = 0; i < n; i++) {
         for (int k = 0; k < 6; k++) {
            b[i][k] = Math.max(0, Math.min(128, Math.round(shape[i * 6 + k] * 128.0F)));
         }
      }

      this.boxes = new int[n * 2];
      this.boxBounds = b;
      List<int[]> top = new ArrayList<>();
      List<int[]> south = new ArrayList<>();

      for (int i = 0; i < n; i++) {
         int[] a = b[i];
         this.boxes[i * 2] = pack(a[0], a[1], a[2]);
         this.boxes[i * 2 + 1] = pack(a[3], a[4], a[5]);
         List<int[]> pieces = new ArrayList<>(List.of(new int[]{a[0], a[3], a[2], a[5]}));

         for (int j = 0; j < n; j++) {
            if (j != i && b[j][1] <= a[4] && b[j][4] > a[4]) {
               subtract(pieces, b[j][0], b[j][3], b[j][2], b[j][5]);
            }
         }

         for (int[] r : pieces) {
            top.add(new int[]{pack(r[0], a[4], r[2]), pack(r[1], a[4], r[3])});
         }

         pieces = new ArrayList<>(List.of(new int[]{a[0], a[3], a[1], a[4]}));

         for (int j = 0; j < n; j++) {
            if (j != i && b[j][2] <= a[5] && b[j][5] > a[5]) {
               subtract(pieces, b[j][0], b[j][3], b[j][1], b[j][4]);
            }
         }

         for (int[] r : pieces) {
            south.add(new int[]{pack(r[0], r[2], a[5]), pack(r[1], r[3], a[5])});
         }
      }

      this.top = flatten(top);
      this.south = flatten(south);
      List<int[]> wet = new ArrayList<>(List.of(new int[]{0, 128, 0, 128}));

      for (int[] a : b) {
         if (a[4] == 128) {
            subtract(wet, a[0], a[3], a[2], a[5]);
         }
      }

      List<int[]> wetTop = new ArrayList<>();

      for (int[] r : wet) {
         wetTop.add(new int[]{pack(r[0], 128, r[2]), pack(r[1], 128, r[3])});
      }

      this.wetTop = flatten(wetTop);
      wet = new ArrayList<>(List.of(new int[]{0, 128, 0, 128}));

      for (int[] a : b) {
         if (a[5] == 128) {
            subtract(wet, a[0], a[3], a[1], a[4]);
         }
      }

      List<int[]> wetSouth = new ArrayList<>();

      for (int[] r : wet) {
         wetSouth.add(new int[]{pack(r[0], r[2], 128), pack(r[1], r[3], 128)});
      }

      this.wetSouth = flatten(wetSouth);
      this.wetCut = this.wetTop.length != 2
         || this.wetTop[0] != pack(0, 128, 0)
         || this.wetTop[1] != FULL_HI
         || this.wetSouth.length != 2
         || this.wetSouth[0] != pack(0, 0, 128)
         || this.wetSouth[1] != FULL_HI;
   }

   private static void subtract(List<int[]> pieces, int s0, int s1, int t0, int t1) {
      List<int[]> left = new ArrayList<>();

      for (int[] r : pieces) {
         if (s1 > r[0] && s0 < r[1] && t1 > r[2] && t0 < r[3]) {
            if (r[0] < s0) {
               left.add(new int[]{r[0], s0, r[2], r[3]});
            }

            if (s1 < r[1]) {
               left.add(new int[]{s1, r[1], r[2], r[3]});
            }

            int m0 = Math.max(r[0], s0);
            int m1 = Math.min(r[1], s1);
            if (r[2] < t0) {
               left.add(new int[]{m0, m1, r[2], t0});
            }

            if (t1 < r[3]) {
               left.add(new int[]{m0, m1, t1, r[3]});
            }
         } else {
            left.add(r);
         }
      }

      pieces.clear();
      pieces.addAll(left);
   }

   private static int[] flatten(List<int[]> pairs) {
      int[] out = new int[pairs.size() * 2];

      for (int i = 0; i < pairs.size(); i++) {
         out[i * 2] = pairs.get(i)[0];
         out[i * 2 + 1] = pairs.get(i)[1];
      }

      return out;
   }

   private record Pitch(double sin, double cos) {
   }
}
