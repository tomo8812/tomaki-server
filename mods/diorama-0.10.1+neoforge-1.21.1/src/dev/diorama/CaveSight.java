package dev.diorama;

import dev.diorama.bake.BlockLooks;
import dev.diorama.data.SeenBlocks;
import java.util.Arrays;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class CaveSight {
   static final int RADIUS = 24;
   static final int MAX_BLOCKS = 60000;
   private static final int SIZE = 49;
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final byte UNKNOWN = 0;
   private static final byte OPEN = 1;
   private static final byte SOLID = 2;
   private static final byte KIND = 3;
   private static final byte VISITED = 4;
   private final ClientLevel level;
   private final byte[] cells = new byte[117649];
   private final int[] queue = new int[60000 + DIRECTIONS.length];
   private final MutableBlockPos pos = new MutableBlockPos();
   private BlockLooks looks;
   private int eyeX;
   private int eyeY;
   private int eyeZ;
   private int visited;

   CaveSight(ClientLevel level) {
      this.level = level;
   }

   int visited() {
      return this.visited;
   }

   int[] spread(BlockLooks looks, Vec3 eyes, SeenBlocks seen) {
      this.looks = looks;
      this.eyeX = Mth.floor(eyes.x);
      this.eyeY = Mth.floor(eyes.y);
      this.eyeZ = Mth.floor(eyes.z);
      Arrays.fill(this.cells, (byte)0);
      int[] box = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      int minY = this.level.getMinBuildHeight();
      int maxY = this.level.getMaxBuildHeight() - 1;
      int r2 = 576;
      int head = 0;
      int tail = 0;
      int start = index(0, 0, 0);
      this.queue[tail++] = start;
      this.cells[start] = (byte)(this.cells[start] | 4);
      this.visited = 1;

      while (head < tail && this.visited < 60000) {
         int cell = this.queue[head++];
         int x = this.eyeX + cell % 49 - 24;
         int z = this.eyeZ + cell / 49 % 49 - 24;
         int y = this.eyeY + cell / 2401 - 24;
         reveal(seen, x, y, z, box);

         for (Direction d : DIRECTIONS) {
            int nx = x + d.getStepX();
            int ny = y + d.getStepY();
            int nz = z + d.getStepZ();
            int dx = nx - this.eyeX;
            int dy = ny - this.eyeY;
            int dz = nz - this.eyeZ;
            if (dx * dx + dy * dy + dz * dz <= r2 && ny >= minY && ny <= maxY) {
               int next = index(dx, dy, dz);
               if ((this.cells[next] & 4) == 0) {
                  this.cells[next] = (byte)(this.cells[next] | 4);
                  this.visited++;
                  if (this.inSight(eyes, nx, ny, nz)) {
                     if (this.solid(next, nx, ny, nz)) {
                        reveal(seen, nx, ny, nz, box);
                     } else {
                        this.queue[tail++] = next;
                     }
                  }
               }
            }
         }
      }

      return box;
   }

   private static int index(int dx, int dy, int dz) {
      return ((dy + 24) * 49 + dz + 24) * 49 + dx + 24;
   }

   private boolean inSight(Vec3 eyes, int tx, int ty, int tz) {
      double dx = tx + 0.5 - eyes.x;
      double dy = ty + 0.5 - eyes.y;
      double dz = tz + 0.5 - eyes.z;
      int x = this.eyeX;
      int y = this.eyeY;
      int z = this.eyeZ;
      int stepX = dx > 0.0 ? 1 : -1;
      int stepY = dy > 0.0 ? 1 : -1;
      int stepZ = dz > 0.0 ? 1 : -1;
      double deltaX = dx == 0.0 ? Double.MAX_VALUE : Math.abs(1.0 / dx);
      double deltaY = dy == 0.0 ? Double.MAX_VALUE : Math.abs(1.0 / dy);
      double deltaZ = dz == 0.0 ? Double.MAX_VALUE : Math.abs(1.0 / dz);
      double maxX = dx == 0.0 ? Double.MAX_VALUE : (dx > 0.0 ? x + 1 - eyes.x : eyes.x - x) * deltaX;
      double maxY = dy == 0.0 ? Double.MAX_VALUE : (dy > 0.0 ? y + 1 - eyes.y : eyes.y - y) * deltaY;
      double maxZ = dz == 0.0 ? Double.MAX_VALUE : (dz > 0.0 ? z + 1 - eyes.z : eyes.z - z) * deltaZ;
      int cell = index(0, 0, 0);
      int target = index(tx - this.eyeX, ty - this.eyeY, tz - this.eyeZ);
      int strideY = stepY * 49 * 49;
      int strideZ = stepZ * 49;
      int x0 = this.eyeX - 24;
      int y0 = this.eyeY - 24;
      int z0 = this.eyeZ - 24;

      for (int i = 0; i < 96; i++) {
         if (maxX < maxY && maxX < maxZ) {
            x += stepX;
            cell += stepX;
            maxX += deltaX;
         } else if (maxY < maxZ) {
            y += stepY;
            cell += strideY;
            maxY += deltaY;
         } else {
            z += stepZ;
            cell += strideZ;
            maxZ += deltaZ;
         }

         if (Integer.compareUnsigned(x - x0, 49) < 0 && Integer.compareUnsigned(y - y0, 49) < 0 && Integer.compareUnsigned(z - z0, 49) < 0) {
            if (cell == target) {
               return true;
            }

            if (this.solid(cell, x, y, z)) {
               return false;
            }
         } else if (this.read(x, y, z) == 2) {
            return false;
         }
      }

      return false;
   }

   private boolean solid(int cell, int x, int y, int z) {
      int kind = this.cells[cell] & 3;
      if (kind == 0) {
         kind = this.read(x, y, z);
         this.cells[cell] = (byte)(this.cells[cell] | (byte)kind);
      }

      return kind == 2;
   }

   private byte read(int x, int y, int z) {
      int id = this.looks.lookId(this.level.getBlockState(this.pos.set(x, y, z)));
      return (byte)(id < this.looks.kind.length && this.looks.kind[id] == 1 ? 2 : 1);
   }

   private static void reveal(SeenBlocks seen, int x, int y, int z, int[] box) {
      if (seen.markSeen(x, y, z)) {
         box[0] = Math.min(box[0], x);
         box[1] = Math.min(box[1], y);
         box[2] = Math.min(box[2], z);
         box[3] = Math.max(box[3], x);
         box[4] = Math.max(box[4], y);
         box[5] = Math.max(box[5], z);
      }
   }
}
