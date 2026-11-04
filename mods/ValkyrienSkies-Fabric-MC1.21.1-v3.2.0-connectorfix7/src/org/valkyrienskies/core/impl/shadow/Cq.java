package org.valkyrienskies.core.impl.shadow;

import java.math.BigDecimal;

public final class Cq {
   public static final double a = Double.longBitsToDouble(4368491638549381120L);
   public static final double b = Double.longBitsToDouble(4503599627370496L);
   private static final long c = 1023L;
   private static final long d = Long.MIN_VALUE;
   private static final int e = Integer.MIN_VALUE;
   private static final double f = 0.0;
   private static final long g = Double.doubleToRawLongBits(0.0);
   private static final long h = Double.doubleToRawLongBits(-0.0);
   private static final int i = Float.floatToRawIntBits(0.0F);
   private static final int j = Float.floatToRawIntBits(-0.0F);

   private Cq() {
   }

   public static int a(double var0, double var2, double var4) {
      if (b(var0, var2, var4)) {
         return 0;
      } else {
         return var0 < var2 ? -1 : 1;
      }
   }

   public static int a(double var0, double var2, int var4) {
      if (b(var0, 0.0, var4)) {
         return 0;
      } else {
         return var0 < 0.0 ? -1 : 1;
      }
   }

   public static boolean a(float var0, float var1) {
      return a(var0, var1, 1);
   }

   public static boolean b(float var0, float var1) {
      return var0 == var0 && var1 == var1 ? a(var0, var1, 1) : !(var0 != var0 ^ var1 != var1);
   }

   private static boolean a(float var0, float var1, float var2) {
      return a(var0, var1, 1) || BY.a(var1 - var0) <= var2;
   }

   private static boolean b(float var0, float var1, float var2) {
      return b(var0, var1) || BY.a(var1 - var0) <= var2;
   }

   private static boolean a(float var0, float var1, int var2) {
      int var3 = Float.floatToRawIntBits(var0);
      int var4 = Float.floatToRawIntBits(var1);
      boolean var6;
      if (((var3 ^ var4) & -2147483648) == 0) {
         var6 = BY.a(var3 - var4) <= var2;
      } else {
         int var5;
         if (var3 < var4) {
            var5 = var4 - i;
            var3 -= j;
         } else {
            var5 = var3 - i;
            var3 = var4 - j;
         }

         var6 = var5 <= var2 && var3 <= var2 - var5;
      }

      return var6 && !Float.isNaN(var0) && !Float.isNaN(var1);
   }

   private static boolean b(float var0, float var1, int var2) {
      return var0 == var0 && var1 == var1 ? a(var0, var1, var2) : !(var0 != var0 ^ var1 != var1);
   }

   public static boolean a(double var0, double var2) {
      return b(var0, var2, 1);
   }

   public static boolean b(double var0, double var2) {
      return var0 == var0 && var2 == var2 ? b(var0, var2, 1) : !(var0 != var0 ^ var2 != var2);
   }

   public static boolean b(double var0, double var2, double var4) {
      return b(var0, var2, 1) || BY.w(var2 - var0) <= var4;
   }

   public static boolean c(double var0, double var2, double var4) {
      if (b(var0, var2, 1)) {
         return true;
      }

      double var6 = BY.e(BY.w(var0), BY.w(var2));
      return BY.w((var0 - var2) / var6) <= var4;
   }

   private static boolean d(double var0, double var2, double var4) {
      return b(var0, var2) || BY.w(var2 - var0) <= var4;
   }

   public static boolean b(double var0, double var2, int var4) {
      long var5 = Double.doubleToRawLongBits(var0);
      long var7 = Double.doubleToRawLongBits(var2);
      boolean var14;
      if (((var5 ^ var7) & Long.MIN_VALUE) == 0L) {
         var14 = BY.a(var5 - var7) <= var4;
      } else {
         long var10;
         long var12;
         if (var5 < var7) {
            var10 = var7 - g;
            var12 = var5 - h;
         } else {
            var10 = var5 - g;
            var12 = var7 - h;
         }

         var14 = var10 <= var4 && var12 <= var4 - var10;
      }

      return var14 && !Double.isNaN(var0) && !Double.isNaN(var2);
   }

   private static boolean c(double var0, double var2, int var4) {
      return var0 == var0 && var2 == var2 ? b(var0, var2, var4) : !(var0 != var0 ^ var2 != var2);
   }

   public static double a(double var0, int var2) {
      return a(var0, var2, 4);
   }

   private static double a(double var0, int var2, int var3) {
      try {
         double var4;
         return (var4 = new BigDecimal(Double.toString(var0)).setScale(var2, 4).doubleValue()) == 0.0 ? var0 * 0.0 : var4;
      } catch (NumberFormatException var6) {
         return Double.isInfinite(var0) ? var0 : Double.NaN;
      }
   }

   private static float a(float var0, int var1) {
      byte var14 = 4;
      var1 = var1;
      var0 = var0;
      float var2 = BY.a(1.0F, var0);
      float var18 = (float)BY.a(10.0, var1) * var2;
      double var21 = var0 * var18;
      double var10001 = var2;
      byte var16 = 4;
      double var9 = var10001;
      double var7 = var21;
      switch (var16) {
         case 0:
            if (var7 != BY.y(var7)) {
               var7 = BY.z(BY.c(var7, Double.POSITIVE_INFINITY));
            }
            break;
         case 1:
            var7 = BY.y(BY.c(var7, Double.NEGATIVE_INFINITY));
            break;
         case 2:
            if (var9 == -1.0) {
               var7 = BY.y(BY.c(var7, Double.NEGATIVE_INFINITY));
            } else {
               var7 = BY.z(BY.c(var7, Double.POSITIVE_INFINITY));
            }
            break;
         case 3:
            if (var9 == -1.0) {
               var7 = BY.z(BY.c(var7, Double.POSITIVE_INFINITY));
            } else {
               var7 = BY.y(BY.c(var7, Double.NEGATIVE_INFINITY));
            }
            break;
         case 4:
            if ((var7 = BY.c(var7, Double.POSITIVE_INFINITY)) - BY.y(var7) >= 0.5) {
               var7 = BY.z(var7);
            } else {
               var7 = BY.y(var7);
            }
            break;
         case 5:
            if ((var7 = BY.c(var7, Double.NEGATIVE_INFINITY)) - BY.y(var7) > 0.5) {
               var7 = BY.z(var7);
            } else {
               var7 = BY.y(var7);
            }
            break;
         case 6:
            double var12;
            if ((var12 = var7 - BY.y(var7)) > 0.5) {
               var7 = BY.z(var7);
            } else if (var12 < 0.5) {
               var7 = BY.y(var7);
            } else if (BY.y(var7) / 2.0 == BY.y(BY.y(var7) / 2.0)) {
               var7 = BY.y(var7);
            } else {
               var7 = BY.z(var7);
            }
            break;
         case 7:
            if (var7 != BY.y(var7)) {
               throw new oU();
            }
            break;
         default:
            throw new oV(
               px.INVALID_ROUNDING_METHOD,
               Integer.valueOf(var16),
               "ROUND_CEILING",
               2,
               "ROUND_DOWN",
               1,
               "ROUND_FLOOR",
               3,
               "ROUND_HALF_DOWN",
               5,
               "ROUND_HALF_EVEN",
               6,
               "ROUND_HALF_UP",
               4,
               "ROUND_UNNECESSARY",
               7,
               "ROUND_UP",
               0
            );
      }

      return (float)var7 / var18;
   }

   private static float a(float var0, int var1, int var2) {
      float var4 = BY.a(1.0F, var0);
      float var3 = (float)BY.a(10.0, var1) * var4;
      return (float)d(var0 * var3, var4, 4) / var3;
   }

   private static double d(double var0, double var2, int var4) {
      switch (var4) {
         case 0:
            if (var0 != BY.y(var0)) {
               var0 = BY.z(BY.c(var0, Double.POSITIVE_INFINITY));
            }
            break;
         case 1:
            var0 = BY.y(BY.c(var0, Double.NEGATIVE_INFINITY));
            break;
         case 2:
            if (var2 == -1.0) {
               var0 = BY.y(BY.c(var0, Double.NEGATIVE_INFINITY));
            } else {
               var0 = BY.z(BY.c(var0, Double.POSITIVE_INFINITY));
            }
            break;
         case 3:
            if (var2 == -1.0) {
               var0 = BY.z(BY.c(var0, Double.POSITIVE_INFINITY));
            } else {
               var0 = BY.y(BY.c(var0, Double.NEGATIVE_INFINITY));
            }
            break;
         case 4:
            if ((var0 = BY.c(var0, Double.POSITIVE_INFINITY)) - BY.y(var0) >= 0.5) {
               var0 = BY.z(var0);
            } else {
               var0 = BY.y(var0);
            }
            break;
         case 5:
            if ((var0 = BY.c(var0, Double.NEGATIVE_INFINITY)) - BY.y(var0) > 0.5) {
               var0 = BY.z(var0);
            } else {
               var0 = BY.y(var0);
            }
            break;
         case 6:
            double var5;
            if ((var5 = var0 - BY.y(var0)) > 0.5) {
               var0 = BY.z(var0);
            } else if (var5 < 0.5) {
               var0 = BY.y(var0);
            } else if (BY.y(var0) / 2.0 == BY.y(BY.y(var0) / 2.0)) {
               var0 = BY.y(var0);
            } else {
               var0 = BY.z(var0);
            }
            break;
         case 7:
            if (var0 != BY.y(var0)) {
               throw new oU();
            }
            break;
         default:
            throw new oV(
               px.INVALID_ROUNDING_METHOD,
               var4,
               "ROUND_CEILING",
               2,
               "ROUND_DOWN",
               1,
               "ROUND_FLOOR",
               3,
               "ROUND_HALF_DOWN",
               5,
               "ROUND_HALF_EVEN",
               6,
               "ROUND_HALF_UP",
               4,
               "ROUND_UNNECESSARY",
               7,
               "ROUND_UP",
               0
            );
      }

      return var0;
   }

   private static double c(double var0, double var2) {
      return var0 + var2 - var0;
   }
}
