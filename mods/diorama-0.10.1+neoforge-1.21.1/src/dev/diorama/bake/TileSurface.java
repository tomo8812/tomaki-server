package dev.diorama.bake;

import java.util.Arrays;

public record TileSurface(
   int revision, int x0, int y0, int width, int height, float[] y, float[] near, boolean[] top, byte[] sun, int[] albedo, int[] light, float[] outline
) {
   static final int WATER = 268435456;
   static final int SLATE = 536870912;

   int index(int px, int py) {
      int x = px - this.x0;
      int y = py - this.y0;
      return x >= 0 && y >= 0 && x < this.width && y < this.height ? y * this.width + x : -1;
   }

   public boolean covers(int x0, int y0, int x1, int y1) {
      return x0 >= this.x0 && y0 >= this.y0 && x1 <= this.x0 + this.width && y1 <= this.y0 + this.height;
   }

   public boolean touches(int x0, int y0, int x1, int y1) {
      return x0 < this.x0 + this.width && this.x0 < x1 && y0 < this.y0 + this.height && this.y0 < y1;
   }

   static int caveLight(int blockLight, int skyLight, float ao, float fade, int emission) {
      return blockLight | skyLight << 4 | emission << 8 | Math.round(ao * 255.0F) << 12 | Math.round(fade * 255.0F) << 20;
   }

   static float fog(int packed) {
      return (packed & 268435456) == 0 && (packed & 536870912) != 0 ? 1.0F - (packed >> 20 & 0xFF) / 255.0F : 0.0F;
   }

   void caveLight(int i, float blockLight, float[] out) {
      caveLightOf(this.light[i], blockLight, out);
   }

   static void caveLightOf(int packed, float blockLight, float[] out) {
      if ((packed & 268435456) != 0) {
         Arrays.fill(out, 0, 3, Lighting.caveWater(blockLight, packed >> 4 & 15) * (packed >> 20 & 0xFF) / 255.0F);
      } else {
         Lighting.cave(blockLight, packed >> 4 & 15, (packed >> 12 & 0xFF) / 255.0F, (packed >> 20 & 0xFF) / 255.0F, (packed >> 8 & 15) / 15.0F, 1.0F, out);
      }
   }
}
