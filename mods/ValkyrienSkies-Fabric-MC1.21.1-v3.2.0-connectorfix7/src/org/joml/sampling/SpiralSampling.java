package org.joml.sampling;

import org.joml.Random;

public class SpiralSampling {
   private final Random rnd;

   public SpiralSampling(long seed) {
      this.rnd = new Random(seed);
   }

   public void createEquiAngle(float radius, int numRotations, int numSamples, Callback2d callback) {
      for (int sample = 0; sample < numSamples; sample++) {
         float angle = (float) (java.lang.Math.PI * 2) * (sample * numRotations) / numSamples;
         float r = radius * sample / (numSamples - 1);
         float x = (float)Math.sin_roquen_9(angle + (float) (java.lang.Math.PI / 2)) * r;
         float y = (float)Math.sin_roquen_9(angle) * r;
         callback.onNewSample(x, y);
      }
   }

   public void createEquiAngle(float radius, int numRotations, int numSamples, float jitter, Callback2d callback) {
      float spacing = radius / numRotations;

      for (int sample = 0; sample < numSamples; sample++) {
         float angle = (float) (java.lang.Math.PI * 2) * (sample * numRotations) / numSamples;
         float r = radius * sample / (numSamples - 1) + (this.rnd.nextFloat() * 2.0F - 1.0F) * spacing * jitter;
         float x = (float)Math.sin_roquen_9(angle + (float) (java.lang.Math.PI / 2)) * r;
         float y = (float)Math.sin_roquen_9(angle) * r;
         callback.onNewSample(x, y);
      }
   }
}
