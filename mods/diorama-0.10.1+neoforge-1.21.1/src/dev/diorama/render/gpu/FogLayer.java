package dev.diorama.render.gpu;

import org.jspecify.annotations.Nullable;

public final class FogLayer implements AutoCloseable {
   private @Nullable MapTexture target;
   private int size;
   private static final int MAX_SIZE = 4096;

   public @Nullable MapTexture texture() {
      return this.target;
   }

   public int size() {
      return this.size;
   }

   public boolean render(
      MapCanvas canvas, MapTexture mask, MapTexture noise, MapTexture detail, int width, int height, float u0, float v0, float u1, float v1, int params
   ) {
      int need = (Math.max(width, height) + 255) / 256 * 256;
      if (need > 4096) {
         return false;
      }

      if (this.target == null || this.size != need) {
         if (this.target != null) {
            this.target.close();
         }

         this.target = MapTexture.createTarget("diorama fog layer", need);
         this.size = need;
      }

      return ((ImmediateCanvas)canvas).fogLayer(this.target, this.size, mask, noise, detail, width, height, u0, v0, u1, v1, params);
   }

   @Override
   public void close() {
      if (this.target != null) {
         this.target.close();
         this.target = null;
      }
   }
}
