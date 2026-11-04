package com.archiangel.quadhotbar.client;

final class QuadHotbarFade {
   private static final long FADE_DURATION_NANOS = 1000000000L;

   private QuadHotbarFade() {
   }

   static int color(long untilNanos, int rgb) {
      if (untilNanos == 0L) {
         return 0;
      }

      long remaining = untilNanos - System.nanoTime();
      if (remaining <= 0L) {
         return 0;
      }

      int alpha = remaining >= 1000000000L ? 255 : Math.max(4, (int)(255L * remaining / 1000000000L));
      return alpha << 24 | rgb & 16777215;
   }
}
