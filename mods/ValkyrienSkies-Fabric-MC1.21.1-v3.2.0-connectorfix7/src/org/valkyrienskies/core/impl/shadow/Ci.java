package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class Ci {
   private static double a;
   private static double b;

   private Ci() {
   }

   public static int a(double var0) {
      return Double.valueOf(var0).hashCode();
   }

   public static boolean a(double var0, double var2) {
      return Double.valueOf(var0).equals(var2);
   }

   public static int a(double[] var0) {
      return Arrays.hashCode(var0);
   }

   public static double b(double var0, double var2) {
      return var0 - (Math.PI * 2) * BY.y((var0 + Math.PI - var2) / (Math.PI * 2));
   }

   public static <T extends kO<T>> T a(T var0, T var1) {
      return (T)(var0.b(var1).d() >= 0.0 ? var0 : var1);
   }

   public static <T extends kO<T>> T b(T var0, T var1) {
      return (T)(var0.b(var1).d() >= 0.0 ? var1 : var0);
   }

   public static double a(double var0, double var2, double var4) {
      double var6 = BY.w(var2);
      return var0 - var6 * BY.y((var0 - var4) / var6) - var4;
   }

   private static byte a(byte var0, byte var1) {
      if ((var0 < 0 || var1 < 0) && (var0 >= 0 || var1 >= 0)) {
         if (var1 >= 0 && var0 == -128) {
            throw new oU(px.OVERFLOW);
         } else {
            return (byte)(-var0);
         }
      } else {
         return var0;
      }
   }

   private static short a(short var0, short var1) {
      if ((var0 < 0 || var1 < 0) && (var0 >= 0 || var1 >= 0)) {
         if (var1 >= 0 && var0 == -32768) {
            throw new oU(px.OVERFLOW);
         } else {
            return (short)(-var0);
         }
      } else {
         return var0;
      }
   }

   public static int a(int var0, int var1) {
      return var1 >= 0 ? 1 : -1;
   }

   private static long a(long var0, long var2) {
      if ((var0 < 0L || var2 < 0L) && (var0 >= 0L || var2 >= 0L)) {
         if (var2 >= 0L && var0 == Long.MIN_VALUE) {
            throw new oU(px.OVERFLOW);
         } else {
            return -var0;
         }
      } else {
         return var0;
      }
   }

   public static void b(double var0) {
      if (Double.isInfinite(var0) || Double.isNaN(var0)) {
         throw new pi(var0);
      }
   }

   private static void b(double[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         double var2;
         if (Double.isInfinite(var2 = var0[var1]) || Double.isNaN(var2)) {
            throw new pi(px.ARRAY_ELEMENT, var2, var1);
         }
      }
   }

   public static void a(Object var0, px var1, Object... var2) {
      if (var0 == null) {
         throw new pl(var1, var2);
      }
   }

   public static void a(Object var0) {
      if (var0 == null) {
         throw new pl();
      }
   }
}
