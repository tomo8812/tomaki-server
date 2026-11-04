package com.fasterxml.jackson.core.io.doubleparser;

public class FastFloatParser {
   private FastFloatParser() {
   }

   public static float parseFloat(CharSequence str) throws NumberFormatException {
      return parseFloat(str, 0, str.length());
   }

   public static float parseFloat(CharSequence str, int offset, int length) throws NumberFormatException {
      long bitPattern = new FloatBitsFromCharSequence().parseFloatingPointLiteral(str, offset, length);
      if (bitPattern == -1L) {
         throw new NumberFormatException("Illegal input");
      } else {
         return Float.intBitsToFloat((int)bitPattern);
      }
   }

   public static float parseFloat(char[] str) throws NumberFormatException {
      return parseFloat(str, 0, str.length);
   }

   public static float parseFloat(char[] str, int offset, int length) throws NumberFormatException {
      long bitPattern = new FloatBitsFromCharArray().parseFloatingPointLiteral(str, offset, length);
      if (bitPattern == -1L) {
         throw new NumberFormatException("Illegal input");
      } else {
         return Float.intBitsToFloat((int)bitPattern);
      }
   }

   public static long parseFloatBits(CharSequence str, int offset, int length) {
      return new FloatBitsFromCharSequence().parseFloatingPointLiteral(str, offset, length);
   }

   public static long parseFloatBits(char[] str, int offset, int length) {
      return new FloatBitsFromCharArray().parseFloatingPointLiteral(str, offset, length);
   }
}
