package dev.diorama.bake;

import dev.diorama.Projection;
import java.util.Arrays;
import org.jspecify.annotations.Nullable;

public final class TileChunks {
   public static final byte NONE = -128;
   private static final int REFERENCE_Y = 64;
   private static final int SIZE = 512;

   private TileChunks() {
   }

   public static int chunkX(TileKey key, int px) {
      return Math.floorDiv((int)Math.floor(key.minU() + (px + 0.5) * key.units() / 512.0), 16);
   }

   public static int chunkZ(TileKey key, int py, byte value) {
      return base(key, py) + value;
   }

   public static int base(TileKey key, int py) {
      double v = key.minV() + (py + 0.5) * key.units() / 512.0;
      return Math.floorDiv((int)Math.floor(Projection.zAt(v, 64.0)), 16);
   }

   private static byte relative(int chunkZ, int base) {
      int d = chunkZ - base;
      return d <= 127 && d > -128 ? (byte)d : -128;
   }

   public static byte @Nullable [] fromSpots(TileKey key, int @Nullable [] spots) {
      if (spots == null) {
         return null;
      }

      byte[] out = new byte[262144];
      int perColumn = 4;

      for (int py = 0; py < 512; py++) {
         int base = base(key, py);

         for (int column = 0; column < 128; column++) {
            int z = TileSpots.z(key, spots, column, py);
            byte value = z == Integer.MIN_VALUE ? -128 : relative(Math.floorDiv(z, 16), base);
            int i = py * 512 + column * perColumn;
            Arrays.fill(out, i, i + perColumn, value);
         }
      }

      return out;
   }

   public static byte @Nullable [] shrink(TileKey key, byte @Nullable [][] children) {
      int half = 256;
      byte[] out = null;

      for (int dy = 0; dy < 2; dy++) {
         for (int dx = 0; dx < 2; dx++) {
            byte[] child = children[dy * 2 + dx];
            if (child != null) {
               if (out == null) {
                  out = new byte[262144];
                  Arrays.fill(out, (byte)-128);
               }

               TileKey childKey = new TileKey(key.layer(), key.tileU() * 2 + dx, key.tileV() * 2 + dy, key.lod() - 1);

               for (int y = 0; y < half; y++) {
                  int childRow = 2 * y + 1;
                  int childBase = base(childKey, childRow);
                  int row = dy * half + y;
                  int base = base(key, row);

                  for (int x = 0; x < half; x++) {
                     byte value = child[childRow * 512 + 2 * x + 1];
                     out[row * 512 + dx * half + x] = value == -128 ? -128 : relative(childBase + value, base);
                  }
               }
            }
         }
      }

      return out;
   }
}
