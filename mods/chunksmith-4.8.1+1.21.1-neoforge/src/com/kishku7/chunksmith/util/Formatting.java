package com.kishku7.chunksmith.util;

import com.kishku7.chunksmith.Selection;
import java.text.DecimalFormat;

public final class Formatting {
   private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.##");
   private static final char[] BINARY_PREFIXES = new char[]{'K', 'M', 'G', 'T', 'P'};

   private Formatting() {
   }

   public static String bytes(long bytes) {
      long value = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
      if (value < 1024L) {
         return String.format("%d B", bytes);
      }

      int i = BINARY_PREFIXES.length - 1;

      long prefixValue;
      for (prefixValue = 1L << BINARY_PREFIXES.length * 10; i > 0 && value < prefixValue; i--) {
         prefixValue >>= 10;
      }

      return String.format("%.1f %cB", (double)bytes / prefixValue, BINARY_PREFIXES[i]);
   }

   public static String radius(Selection selection) {
      return !"rectangle".equals(selection.shape()) && !"ellipse".equals(selection.shape())
         ? String.format("%s", number(selection.radiusX()))
         : String.format("%s, %s", number(selection.radiusX()), number(selection.radiusZ()));
   }

   public static synchronized String number(double number) {
      return NUMBER_FORMAT.format(number);
   }
}
