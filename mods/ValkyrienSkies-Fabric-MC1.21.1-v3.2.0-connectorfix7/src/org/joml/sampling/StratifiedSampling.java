package org.joml.sampling;

import org.joml.Random;

public class StratifiedSampling {
   private final Random rnd;

   public StratifiedSampling(long seed) {
      this.rnd = new Random(seed);
   }

   public void generateRandom(int n, Callback2d callback) {
      for (int y = 0; y < n; y++) {
         for (int x = 0; x < n; x++) {
            float sampleX = (this.rnd.nextFloat() / n + (float)x / n) * 2.0F - 1.0F;
            float sampleY = (this.rnd.nextFloat() / n + (float)y / n) * 2.0F - 1.0F;
            callback.onNewSample(sampleX, sampleY);
         }
      }
   }

   public void generateCentered(int n, float centering, Callback2d callback) {
      float start = centering * 0.5F;
      float end = 1.0F - centering;

      for (int y = 0; y < n; y++) {
         for (int x = 0; x < n; x++) {
            float sampleX = ((start + this.rnd.nextFloat() * end) / n + (float)x / n) * 2.0F - 1.0F;
            float sampleY = ((start + this.rnd.nextFloat() * end) / n + (float)y / n) * 2.0F - 1.0F;
            callback.onNewSample(sampleX, sampleY);
         }
      }
   }
}
