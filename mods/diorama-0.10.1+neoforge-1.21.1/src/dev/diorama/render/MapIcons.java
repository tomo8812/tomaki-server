package dev.diorama.render;

import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import org.jspecify.annotations.Nullable;

public final class MapIcons {
   private static final int SIZE = 32;
   private static @Nullable MapTexture dot;
   private static @Nullable MapTexture caret;

   private MapIcons() {
   }

   public static void dot(IconBatch icons, float x, float y, float size, int argb) {
      if (dot == null) {
         dot = create("diorama dot", MapIcons::circle);
      }

      draw(icons, dot, x - size / 2.0F, y - size / 2.0F, size, argb, false);
   }

   public static void caret(IconBatch icons, float x, float y, float size, int argb, boolean down) {
      if (caret == null) {
         caret = create("diorama caret", MapIcons::triangle);
      }

      draw(icons, caret, x - size / 2.0F, y - size / 2.0F, size, argb, down);
   }

   private static void draw(IconBatch icons, MapTexture texture, float x, float y, float size, int argb, boolean flip) {
      icons.rect(texture, Sampling.CLAMP_LINEAR, x, y, x + size, y + size, 0.0F, flip ? 1.0F : 0.0F, 1.0F, flip ? 0.0F : 1.0F, argb);
   }

   private static float circle(float px, float py) {
      float d = (float)Math.sqrt(px * px + py * py);
      return Math.max(0.0F, Math.min(1.0F, (0.94F - d) * 32.0F / 2.0F));
   }

   private static float triangle(float px, float py) {
      float halfWidth = (py + 0.8F) * 0.62F;
      return !(py < -0.8F) && !(py > 0.75F) ? Math.max(0.0F, Math.min(1.0F, (halfWidth - Math.abs(px)) * 32.0F / 2.0F)) : 0.0F;
   }

   private static MapTexture create(String label, MapIcons.Shape shape) {
      int[] pixels = new int[1024];

      for (int y = 0; y < 32; y++) {
         for (int x = 0; x < 32; x++) {
            float px = (x + 0.5F) / 32.0F * 2.0F - 1.0F;
            float py = (y + 0.5F) / 32.0F * 2.0F - 1.0F;
            int a = Math.round(shape.coverage(px, py) * 255.0F);
            pixels[y * 32 + x] = a << 24 | 16777215;
         }
      }

      MapTexture texture = MapTexture.create(label, 32, 1);
      texture.write(0, 0, 0, 32, pixels);
      return texture;
   }

   private interface Shape {
      float coverage(float var1, float var2);
   }
}
