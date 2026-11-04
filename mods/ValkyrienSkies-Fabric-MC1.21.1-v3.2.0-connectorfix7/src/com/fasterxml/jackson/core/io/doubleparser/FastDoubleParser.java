package com.fasterxml.jackson.core.io.doubleparser;

public class FastDoubleParser {
   private FastDoubleParser() {
   }

   public static double parseDouble(CharSequence str) throws NumberFormatException {
      return parseDouble(str, 0, str.length());
   }

   public static double parseDouble(CharSequence str, int offset, int length) throws NumberFormatException {
      long bitPattern = new DoubleBitsFromCharSequence().parseFloatingPointLiteral(str, offset, length);
      if (bitPattern == -1L) {
         throw new NumberFormatException("Illegal input");
      } else {
         return Double.longBitsToDouble(bitPattern);
      }
   }

   public static double parseDouble(char[] str) throws NumberFormatException {
      return parseDouble(str, 0, str.length);
   }

   public static double parseDouble(char[] str, int offset, int length) throws NumberFormatException {
      long bitPattern = new DoubleBitsFromCharArray().parseFloatingPointLiteral(str, offset, length);
      if (bitPattern == -1L) {
         throw new NumberFormatException("Illegal input");
      } else {
         return Double.longBitsToDouble(bitPattern);
      }
   }

   public static long parseDoubleBits(CharSequence str, int offset, int length) {
      return new DoubleBitsFromCharSequence().parseFloatingPointLiteral(str, offset, length);
   }

   public static long parseDoubleBits(char[] str, int offset, int length) {
      return new DoubleBitsFromCharArray().parseFloatingPointLiteral(str, offset, length);
   }
}
