package dev.diorama.bake;

import org.jspecify.annotations.Nullable;

final class RockRays {
   static final int MAX = 4;
   static final int ALL = -1;
   static final int MAX_PIECES = 5;
   private static final double EPS = 1.0E-9;

   private RockRays() {
   }

   static int enter(double[] out, double @Nullable [] above, int aboveN, double @Nullable [] south, int southN, int y, int z, double cot) {
      if (aboveN == -1 && southN == -1) {
         return -1;
      }

      double low = z - (y + 1) * cot;
      double middle = z + 1 - (y + 1) * cot;
      double high = z + 1 - y * cot;
      int n = clip(out, 0, above, aboveN, low, middle);
      if (n > 4) {
         return n;
      }

      n = clip(out, n, south, southN, middle, high);
      return n == 1 && out[0] <= low + 1.0E-9 && out[1] >= high - 1.0E-9 ? -1 : n;
   }

   private static int clip(double[] out, int n, double @Nullable [] span, int spanN, double a, double b) {
      if (spanN == -1) {
         return add(out, n, a, b);
      }

      for (int k = 0; k < spanN && n <= 4; k++) {
         double lo = Math.max(span[k * 2], a);
         double hi = Math.min(span[k * 2 + 1], b);
         if (hi - lo > 1.0E-9) {
            n = add(out, n, lo, hi);
         }
      }

      return n;
   }

   private static int add(double[] out, int n, double lo, double hi) {
      if (n > 0 && out[n * 2 - 1] >= lo - 1.0E-9) {
         out[n * 2 - 1] = Math.max(out[n * 2 - 1], hi);
         return n;
      }

      if (n == 4) {
         return 5;
      }

      out[n * 2] = lo;
      out[n * 2 + 1] = hi;
      return n + 1;
   }

   static int pieces(int[] out, int at, boolean top, int lo, int hi, double[] rock, int rockN, int y, int z, double cot, boolean inside) {
      if (rockN != 0 && rockN != -1) {
         double unit = 128.0;
         int a = top ? lo >> 16 & 0xFF : lo >> 8 & 0xFF;
         int b = top ? hi >> 16 & 0xFF : hi >> 8 & 0xFF;
         double plane = (top ? hi >> 8 & 0xFF : hi >> 16 & 0xFF) / unit;
         double ua = top ? z + a / unit - (y + plane) * cot : z + plane - (y + b / unit) * cot;
         double ub = top ? z + b / unit - (y + plane) * cot : z + plane - (y + a / unit) * cot;
         boolean touched = false;

         for (int k = 0; k < rockN; k++) {
            if (Math.min(rock[k * 2 + 1], ub) - Math.max(rock[k * 2], ua) > 1.0E-9) {
               touched = true;
               break;
            }
         }

         if (!touched) {
            if (!inside) {
               out[at] = lo;
               out[at + 1] = hi;
               return at + 2;
            } else {
               return at;
            }
         } else {
            double cursor = ua;

            for (int k = 0; k < rockN; k++) {
               double r0 = Math.max(ua, rock[k * 2]);
               double r1 = Math.min(ub, rock[k * 2 + 1]);
               if (!(r1 - r0 <= 1.0E-9)) {
                  at = inside ? emit(out, at, top, lo, hi, r0, r1, y, z, plane, cot) : emit(out, at, top, lo, hi, cursor, r0, y, z, plane, cot);
                  cursor = Math.max(cursor, r1);
               }
            }

            return inside ? at : emit(out, at, top, lo, hi, cursor, ub, y, z, plane, cot);
         }
      } else if (inside == (rockN == -1)) {
         out[at] = lo;
         out[at + 1] = hi;
         return at + 2;
      } else {
         return at;
      }
   }

   private static int emit(int[] out, int at, boolean top, int lo, int hi, double u0, double u1, int y, int z, double plane, double cot) {
      if (u1 - u0 <= 1.0E-9) {
         return at;
      }

      double unit = 128.0;
      if (top) {
         int z0 = clamp((int)Math.round((u0 - z + (y + plane) * cot) * unit), lo >> 16 & 0xFF, hi >> 16 & 0xFF);
         int z1 = clamp((int)Math.round((u1 - z + (y + plane) * cot) * unit), lo >> 16 & 0xFF, hi >> 16 & 0xFF);
         if (z1 <= z0) {
            return at;
         }

         out[at] = lo & -16711681 | z0 << 16;
         out[at + 1] = hi & -16711681 | z1 << 16;
      } else {
         int y0 = clamp((int)Math.round(((z + plane - u1) / cot - y) * unit), lo >> 8 & 0xFF, hi >> 8 & 0xFF);
         int y1 = clamp((int)Math.round(((z + plane - u0) / cot - y) * unit), lo >> 8 & 0xFF, hi >> 8 & 0xFF);
         if (y1 <= y0) {
            return at;
         }

         out[at] = lo & -65281 | y0 << 8;
         out[at + 1] = hi & -65281 | y1 << 8;
      }

      return at + 2;
   }

   private static int clamp(int v, int lo, int hi) {
      return Math.max(lo, Math.min(hi, v));
   }
}
