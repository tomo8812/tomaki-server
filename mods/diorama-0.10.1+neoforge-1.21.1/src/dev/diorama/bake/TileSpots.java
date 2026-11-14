package dev.diorama.bake;

import dev.diorama.Projection;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public final class TileSpots {
   public static final int COLUMNS = 128;
   public static final int ROWS = 512;
   public static final int UNKNOWN = 0;
   public static final int NOTHING = 16777216;
   private static final int SPOT = 33554432;
   public static final int PATCH_COLUMNS = 8;

   private TileSpots() {
   }

   public static int @Nullable [] pick(TileImage image, ChunkSource source, BlockLooks looks, boolean fogOfWar, int @Nullable [] saved) {
      if (!image.isFull() && image.patches().isEmpty()) {
         return null;
      }

      TileKey key = image.key();
      MapPick pick = new MapPick(source, looks, key, fogOfWar).passWhereSaved(saved);
      int[] spots = new int[65536];
      boolean known = false;
      if (image.isFull()) {
         known = pick(key, pick, spots, saved, 0, 0, 128, 512);
      } else {
         for (TileImage.Patch patch : image.patches()) {
            known |= pick(key, pick, spots, saved, patchColumn(patch), patch.blockY() * 32, 8, 32);
         }
      }

      return known ? spots : null;
   }

   public static int patchColumn(TileImage.Patch patch) {
      return patch.blockX() * 8;
   }

   private static boolean pick(TileKey key, MapPick pick, int[] spots, int @Nullable [] saved, int column0, int row0, int columns, int rows) {
      boolean known = false;

      for (int row = row0; row < row0 + rows; row++) {
         double v = v(key, row);

         for (int column = column0; column < column0 + columns; column++) {
            int i = row * 128 + column;

            spots[i] = switch (pick.pick(key.minU() + column + 0.5, v)) {
               case 0 -> encode(pick.y, pick.z, v);
               case 1 -> 16777216;
               default -> saved == null ? 0 : saved[i];
            };
            known |= spots[i] != 0;
         }
      }

      return known;
   }

   public static @Nullable BlockPos spot(TileKey key, int @Nullable [] spots, double u, double v) {
      if (spots == null) {
         return null;
      }

      int i = index(key, u, v);
      int value = spots[i];
      if ((value & 0xFF000000) != 33554432) {
         return null;
      }

      int column = i % 128;
      int row = i / 128;
      int y = (short)value;
      int z = (int)Math.floor(Projection.zAt(v(key, row), y)) - (value >>> 16 & 0xFF);
      return new BlockPos((int)Math.floor(key.minU()) + column, y, z);
   }

   public static boolean known(TileKey key, int @Nullable [] spots, double u, double v) {
      return spots != null && spots[index(key, u, v)] != 0;
   }

   public static int z(TileKey key, int[] spots, int column, int row) {
      int value = spots[row * 128 + column];
      return (value & 0xFF000000) != 33554432 ? Integer.MIN_VALUE : (int)Math.floor(Projection.zAt(v(key, row), (short)value)) - (value >>> 16 & 0xFF);
   }

   private static int index(TileKey key, double u, double v) {
      int column = (int)Math.max(0L, Math.min(127L, (long)Math.floor(u - key.minU())));
      int row = (int)Math.max(0L, Math.min(511L, (long)Math.floor((v - key.minV()) * 4.0)));
      return row * 128 + column;
   }

   private static double v(TileKey key, int row) {
      return key.minV() + (row + 0.5) / 4.0;
   }

   public static double rowMiddle(double v) {
      return (Math.floor(v * 4.0) + 0.5) / 4.0;
   }

   private static boolean passedWhenSaved(TileKey key, int @Nullable [] spots, int x, int y, double v) {
      if (spots == null) {
         return false;
      } else {
         long column = x - (long)Math.floor(key.minU());
         long row = (long)Math.floor((v - key.minV()) * 4.0);
         if (column >= 0L && column < 128L && row >= 0L && row < 512L) {
            int value = spots[(int)row * 128 + (int)column];
            return (value & 0xFF000000) == 33554432 && y > (short)value;
         } else {
            return false;
         }
      }
   }

   public static boolean passedWhenSaved(TileKey key, int @Nullable [] spots, int x, int y, int z) {
      if (spots == null) {
         return false;
      }

      double sin = Projection.sin();
      double cos = Projection.cos();
      double first = Math.max(key.minV(), z * sin - (y + 1) * cos);
      double last = Math.min(key.minV() + 128.0, (z + 1) * sin - y * cos);
      if (first >= last) {
         return false;
      }

      double step = 0.25;
      boolean crossed = false;

      for (double v = rowMiddle(first); v - step / 2.0 < last; v += step) {
         if (!passedWhenSaved(key, spots, x, y, v)) {
            return false;
         }

         crossed |= v >= first && v < last;
      }

      return crossed;
   }

   private static int encode(int y, int z, double v) {
      int back = (int)Math.floor(Projection.zAt(v, y)) - z;
      return back >= 0 && back <= 255 && y == (short)y ? 33554432 | back << 16 | y & 65535 : 0;
   }
}
