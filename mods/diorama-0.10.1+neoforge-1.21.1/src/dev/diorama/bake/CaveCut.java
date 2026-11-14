package dev.diorama.bake;

public final class CaveCut {
   public static final int LIFT = 8;
   private static final int REACH = 14;
   private static final int CLEARANCE = 4;
   private static final int NONE = Integer.MIN_VALUE;
   private final int base;
   private final int x0;
   private final int z0;
   private final int width;
   private final int depth;
   private final int[] cut;

   CaveCut(VoxelReader reader, int base, int x0, int z0, int x1, int z1) {
      this.base = base;
      this.x0 = x0 - 14;
      this.z0 = z0 - 14;
      this.width = x1 - x0 + 1 + 28;
      this.depth = z1 - z0 + 1 + 28;
      int n = this.width * this.depth;
      int[] ground = new int[n];
      int lowest = base - 4 - 8;
      int highest = base - 4 + 8;

      for (int j = 0; j < this.depth; j++) {
         for (int i = 0; i < this.width; i++) {
            int x = this.x0 + i;
            int z = this.z0 + j;
            int g = Integer.MIN_VALUE;

            for (int y = highest; y > lowest && g == Integer.MIN_VALUE; y--) {
               if (reader.solid(x, y - 1, z) > 0 && reader.solid(x, y, z) == 0 && reader.solid(x, y + 1, z) == 0) {
                  g = y;
               }
            }

            ground[j * this.width + i] = g;
         }
      }

      boolean[] reached = new boolean[n];

      for (int c = 0; c < n; c++) {
         reached[c] = ground[c] != Integer.MIN_VALUE && ground[c] <= base - 4;
      }

      boolean[] next = new boolean[n];

      for (int step = 0; step < 14; step++) {
         for (int j = 0; j < this.depth; j++) {
            for (int i = 0; i < this.width; i++) {
               int c = j * this.width + i;
               int g = ground[c];
               next[c] = reached[c]
                  || g != Integer.MIN_VALUE
                     && (
                        i > 0 && climbs(reached, ground, c - 1, g)
                           || i < this.width - 1 && climbs(reached, ground, c + 1, g)
                           || j > 0 && climbs(reached, ground, c - this.width, g)
                           || j < this.depth - 1 && climbs(reached, ground, c + this.width, g)
                     );
            }
         }

         boolean[] t = reached;
         reached = next;
         next = t;
      }

      this.cut = new int[n];

      for (int c = 0; c < n; c++) {
         this.cut[c] = reached[c] ? Math.max(base, ground[c] + 4) : base;
      }
   }

   boolean raises() {
      for (int j = 14; j < this.depth - 14; j++) {
         for (int i = 14; i < this.width - 14; i++) {
            if (this.cut[j * this.width + i] != this.base) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean climbs(boolean[] reached, int[] ground, int from, int g) {
      return reached[from] && ground[from] != Integer.MIN_VALUE && ground[from] >= g - 1;
   }

   int at(int x, int z) {
      int i = x - this.x0;
      int j = z - this.z0;
      return i >= 0 && j >= 0 && i < this.width && j < this.depth ? this.cut[j * this.width + i] : this.base;
   }
}
