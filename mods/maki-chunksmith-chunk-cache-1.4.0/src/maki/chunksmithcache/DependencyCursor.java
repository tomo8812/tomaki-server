package maki.chunksmithcache;

final class DependencyCursor {
   private final int minZ;
   private final int maxX;
   private final int maxZ;
   private int x;
   private int z;
   private boolean done;

   DependencyCursor(int centerX, int centerZ, int radius) {
      if (radius < 0) {
         throw new IllegalArgumentException("negative dependency radius");
      }

      this.x = centerX - radius;
      this.minZ = centerZ - radius;
      this.z = this.minZ;
      this.maxX = centerX + radius;
      this.maxZ = centerZ + radius;
   }

   boolean hasNext() {
      return !this.done;
   }

   int x() {
      return this.x;
   }

   int z() {
      return this.z;
   }

   void advance() {
      if (this.done) {
         throw new IllegalStateException("dependency scan exhausted");
      }

      if (this.z == this.maxZ) {
         this.z = this.minZ;
         if (this.x == this.maxX) {
            this.done = true;
         } else {
            this.x++;
         }
      } else {
         this.z++;
      }
   }
}
