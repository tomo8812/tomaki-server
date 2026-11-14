package dev.diorama.bake;

import org.jspecify.annotations.Nullable;

public final class BlockLight {
   public static final int SCALE = 4;
   public static final int EDGE = 128;

   private BlockLight() {
   }

   static int encode(int sum, int known) {
      return known == 0 ? 0 : (sum * 17 + known / 2) / known;
   }

   static byte[] shrink(byte[] pixelLight, int[] pixels, int @Nullable [] saved, byte @Nullable [] savedLight, int px0, int py0, int edge) {
      int size = 512;
      int out = edge / 4;
      byte[] light = new byte[out * out];

      for (int ly = 0; ly < out; ly++) {
         for (int lx = 0; lx < out; lx++) {
            int sum = 0;
            int weight = 0;

            for (int dy = 0; dy < 4; dy++) {
               for (int dx = 0; dx < 4; dx++) {
                  int x = lx * 4 + dx;
                  int y = ly * 4 + dy;
                  int i = y * edge + x;
                  int alpha = pixels[i] >>> 24;
                  int value = pixelLight[i] & 255;
                  if (saved != null) {
                     int s = (py0 + y) * size + px0 + x;
                     int savedAlpha = saved[s] >>> 24;
                     if (savedAlpha > alpha) {
                        alpha = savedAlpha;
                        value = savedLight == null ? 0 : savedLight[(py0 + y) / 4 * 128 + (px0 + x) / 4] & 0xFF;
                     }
                  }

                  sum += value * alpha;
                  weight += alpha;
               }
            }

            light[ly * out + lx] = (byte)(weight == 0 ? 0 : (sum + weight / 2) / weight);
         }
      }

      return light;
   }

   public static byte @Nullable [] overview(byte @Nullable [][] children, int @Nullable [][] childPixels) {
      byte[] out = null;
      int half = 64;
      int size = 512;

      for (int c = 0; c < 4; c++) {
         byte[] l = children[c];
         int[] p = childPixels[c];
         if (l != null && p != null) {
            if (out == null) {
               out = new byte[16384];
            }

            int ox = (c & 1) * half;
            int oy = (c >> 1) * half;

            for (int y = 0; y < half; y++) {
               for (int x = 0; x < half; x++) {
                  int sum = 0;
                  int weight = 0;

                  for (int d = 0; d < 4; d++) {
                     int lx = 2 * x + (d & 1);
                     int ly = 2 * y + (d >> 1);
                     int w = known(p, lx, ly, size);
                     sum += (l[ly * 128 + lx] & 255) * w;
                     weight += w;
                  }

                  out[(oy + y) * 128 + ox + x] = (byte)(weight == 0 ? 0 : (sum + weight / 2) / weight);
               }
            }
         }
      }

      return out;
   }

   private static int known(int[] pixels, int lx, int ly, int size) {
      int sum = 0;

      for (int dy = 0; dy < 4; dy++) {
         int row = (ly * 4 + dy) * size + lx * 4;

         for (int dx = 0; dx < 4; dx++) {
            sum += pixels[row + dx] >>> 24;
         }
      }

      return sum;
   }

   public static byte[][] mips(byte[] base, int levels) {
      byte[][] out = new byte[levels][];
      out[0] = base;

      for (int level = 1; level < levels; level++) {
         int src = 128 >> level - 1;
         int dst = src >> 1;
         byte[] from = out[level - 1];
         byte[] to = new byte[dst * dst];

         for (int y = 0; y < dst; y++) {
            for (int x = 0; x < dst; x++) {
               int i = y * 2 * src + x * 2;
               to[y * dst + x] = (byte)(((from[i] & 255) + (from[i + 1] & 255) + (from[i + src] & 255) + (from[i + src + 1] & 255) + 2) / 4);
            }
         }

         out[level] = to;
      }

      return out;
   }

   public static boolean any(byte @Nullable [] light) {
      if (light == null) {
         return false;
      }

      for (byte b : light) {
         if (b != 0) {
            return true;
         }
      }

      return false;
   }
}
