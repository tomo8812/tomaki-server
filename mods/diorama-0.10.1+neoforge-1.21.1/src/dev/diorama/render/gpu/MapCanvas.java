package dev.diorama.render.gpu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public interface MapCanvas {
   static MapCanvas of(GuiGraphics graphics) {
      return new ImmediateCanvas(graphics);
   }

   void quad(MapTexture var1, Sampling var2, float[] var3, float[] var4, int var5);

   default void rect(MapTexture texture, Sampling sampling, float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, int argb) {
      this.quad(texture, sampling, new float[]{x0, y0, x0, y1, x1, y1, x1, y0}, new float[]{u0, v0, u0, v1, u1, v1, u1, v0}, argb);
   }

   void nightTile(MapTexture var1, Sampling var2, MapTexture var3, float[] var4, float[] var5, int var6);

   void solid(float[] var1, int var2);

   void blit(ResourceLocation var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   void solids(float[] var1, int[] var2, int var3);

   void blits(ResourceLocation var1, float[] var2, float[] var3, int[] var4, int var5);

   void rects(MapTexture var1, Sampling var2, float[] var3, float[] var4, int[] var5, int var6);

   void playerFace(AbstractClientPlayer var1, int var2, int var3, int var4, int var5);

   default void fog(
      MapTexture mask, MapTexture noise, MapTexture detail, int x, int y, int width, int height, float u0, float v0, float u1, float v1, int params
   ) {
      this.fog(
         mask,
         noise,
         detail,
         new float[]{x, y, x, y + height, x + width, y + height, x + width, y},
         new float[]{u0, v0, u0, v1, u1, v1, u1, v0},
         x,
         y,
         width,
         height,
         params
      );
   }

   void fog(MapTexture var1, MapTexture var2, MapTexture var3, float[] var4, float[] var5, int var6, int var7, int var8, int var9, int var10);
}
