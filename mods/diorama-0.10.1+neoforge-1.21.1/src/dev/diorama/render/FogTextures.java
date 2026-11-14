package dev.diorama.render;

import dev.diorama.render.gpu.MapTexture;
import java.util.Random;
import org.jspecify.annotations.Nullable;

final class FogTextures {
   static final int NOISE_SIZE = 256;
   private static @Nullable MapTexture noise;

   private FogTextures() {
   }

   static MapTexture noise() {
      if (noise == null) {
         noise = createNoise();
      }

      return noise;
   }

   private static MapTexture createNoise() {
      MapTexture texture = MapTexture.create("diorama fog noise", 256, 1);
      texture.write(0, 0, 0, 256, noisePixels());
      return texture;
   }

   static int[] noisePixels() {
      int size = 256;
      float[][] channels = new float[][]{fractal(size, 6, 1L), fractal(size, 12, 2L), fractal(size, 24, 3L), fractal(size, 48, 4L)};
      int[] pixels = new int[size * size];

      for (int i = 0; i < pixels.length; i++) {
         int r = Math.round(channels[0][i] * 255.0F);
         int g = Math.round(channels[1][i] * 255.0F);
         int b = Math.round(channels[2][i] * 255.0F);
         int a = Math.round(channels[3][i] * 255.0F);
         pixels[i] = a << 24 | b << 16 | g << 8 | r;
      }

      return pixels;
   }

   private static float[] fractal(int size, int cells, long seed) {
      float[] a = valueNoise(size, cells, seed);
      float[] b = valueNoise(size, cells * 2, seed * 31L + 7L);
      float[] out = new float[size * size];

      for (int i = 0; i < out.length; i++) {
         out[i] = a[i] * 0.7F + b[i] * 0.3F;
      }

      return out;
   }

   private static float[] valueNoise(int size, int cells, long seed) {
      Random random = new Random(seed);
      float[] lattice = new float[cells * cells];

      for (int i = 0; i < lattice.length; i++) {
         lattice[i] = random.nextFloat();
      }

      float[] out = new float[size * size];

      for (int y = 0; y < size; y++) {
         float fy = (float)y * cells / size;
         int y0 = (int)fy;
         float ty = smooth(fy - y0);

         for (int x = 0; x < size; x++) {
            float fx = (float)x * cells / size;
            int x0 = (int)fx;
            float tx = smooth(fx - x0);
            float v00 = lattice[y0 % cells * cells + x0 % cells];
            float v10 = lattice[y0 % cells * cells + (x0 + 1) % cells];
            float v01 = lattice[(y0 + 1) % cells * cells + x0 % cells];
            float v11 = lattice[(y0 + 1) % cells * cells + (x0 + 1) % cells];
            float top = v00 + (v10 - v00) * tx;
            float bottom = v01 + (v11 - v01) * tx;
            out[y * size + x] = top + (bottom - top) * ty;
         }
      }

      return out;
   }

   private static float smooth(float t) {
      return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
   }
}
