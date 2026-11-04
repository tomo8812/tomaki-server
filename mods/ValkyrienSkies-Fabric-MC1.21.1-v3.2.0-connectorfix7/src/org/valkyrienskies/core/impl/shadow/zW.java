package org.valkyrienskies.core.impl.shadow;

public final class zW {
   private static final double a = 1.0E-14;
   private static final double b = 0.9189385332046727;
   private static final double[] c = new double[]{
      0.08333333333333333,
      -2.777777777777778E-5,
      7.936507936507937E-8,
      -5.952380952380953E-10,
      8.417508417508329E-12,
      -1.917526917518546E-13,
      6.410256405103255E-15,
      -2.955065141253382E-16,
      1.7964371635940225E-17,
      -1.3922896466162779E-18,
      1.338028550140209E-19,
      -1.542460098679661E-20,
      1.9770199298095743E-21,
      -2.3406566479399704E-22,
      1.713480149663986E-23
   };

   private zW() {
   }

   public static double a(double var0, double var2, double var4) {
      return a(var0, var2, var4, 1.0E-14, Integer.MAX_VALUE);
   }

   private static double a(double var0, double var2, double var4, double var6) {
      return a(var0, var2, var4, var6, Integer.MAX_VALUE);
   }

   private static double a(double var0, double var2, double var4, int var6) {
      return a(var0, var2, var4, 1.0E-14, var6);
   }

   private static double a(double var0, final double var2, final double var4, double var6, int var8) {
      double var9;
      if (!Double.isNaN(var0) && !Double.isNaN(var2) && !Double.isNaN(var4) && !(var0 < 0.0) && !(var0 > 1.0) && !(var2 <= 0.0) && !(var4 <= 0.0)) {
         if (var0 > (var2 + 1.0) / (var4 + 2.0 + var2) && 1.0 - var0 <= (var4 + 1.0) / (var4 + 2.0 + var2)) {
            var9 = 1.0 - a(1.0 - var0, var4, var2, var6, var8);
         } else {
            BT var11 = new BT() {
               @Override
               public final double a(int var1, double var2x) {
                  double var4x;
                  if (var1 % 2 == 0) {
                     double var6x;
                     var4x = (var6x = var1 / 2.0) * (var4 - var6x) * var2x / ((var2 + var6x * 2.0 - 1.0) * (var2 + var6x * 2.0));
                  } else {
                     double var8x = (var1 - 1.0) / 2.0;
                     var4x = -((var2 + var8x) * (var2 + var4 + var8x) * var2x) / ((var2 + var8x * 2.0) * (var2 + var8x * 2.0 + 1.0));
                  }

                  return var4x;
               }

               @Override
               public final double b(int var1, double var2x) {
                  return 1.0;
               }
            };
            var9 = BY.i(var2 * BY.k(var0) + var4 * BY.l(-var0) - BY.k(var2) - a(var2, var4)) / var11.a(var0, var6, var8);
         }
      } else {
         var9 = Double.NaN;
      }

      return var9;
   }

   @Deprecated
   private static double b(double var0, double var2) {
      return a(var0, var2);
   }

   private static double c(double var0, double var2) {
      if (var0 < 1.0 || var0 > 2.0) {
         throw new po(var0, 1.0, 2.0);
      }

      if (!(var2 < 1.0) && !(var2 > 2.0)) {
         double var4;
         if ((var4 = var0 - 1.0 + (var2 - 1.0)) <= 0.5) {
            return zY.c(var4 + 1.0);
         } else {
            return var4 <= 1.5 ? zY.c(var4) + BY.l(var4) : zY.c(var4 - 1.0) + BY.k(var4 * (var4 + 1.0));
         }
      } else {
         throw new po(var2, 1.0, 2.0);
      }
   }

   private static double d(double var0, double var2) {
      if (var0 < 0.0) {
         throw new pn(var0, 0.0, true);
      }

      if (var2 < 10.0) {
         throw new pn(var2, 10.0, true);
      }

      double var4;
      double var6;
      if (var0 <= var2) {
         var4 = var2 + (var0 - 0.5);
         var6 = e(var0, var2);
      } else {
         var4 = var0 + (var2 - 0.5);
         var6 = e(var2, var0);
      }

      double var8 = var4 * BY.l(var0 / var2);
      double var10 = var0 * (BY.k(var2) - 1.0);
      return var8 <= var10 ? var6 - var8 - var10 : var6 - var10 - var8;
   }

   private static double e(double var0, double var2) {
      if (var0 < 0.0 || var0 > var2) {
         throw new po(var0, 0, var2);
      }

      if (var2 < 10.0) {
         throw new pn(var2, 10, true);
      }

      double var4;
      double var6 = (var4 = var0 / var2) / (var4 + 1.0);
      double var8;
      double var10 = (var8 = 1.0 / (var4 + 1.0)) * var8;
      double[] var19;
      (var19 = new double[c.length])[0] = 1.0;

      for (int var13 = 1; var13 < var19.length; var13++) {
         var19[var13] = 1.0 + (var8 + var10 * var19[var13 - 1]);
      }

      double var20;
      double var15 = (var20 = 10.0 / var2) * var20;
      double var17 = c[c.length - 1] * var19[var19.length - 1];

      for (int var1 = c.length - 2; var1 >= 0; var1--) {
         var17 = var15 * var17 + c[var1] * var19[var1];
      }

      return var17 * var6 / var2;
   }

   private static double f(double var0, double var2) {
      if (var0 < 10.0) {
         throw new pn(var0, 10.0, true);
      }

      if (var2 < 10.0) {
         throw new pn(var2, 10.0, true);
      }

      double var4 = BY.d(var0, var2);
      double var6 = BY.e(var0, var2);
      double var8;
      double var10 = (var8 = 10.0 / var4) * var8;
      double var12 = c[c.length - 1];

      for (int var14 = c.length - 2; var14 >= 0; var14--) {
         var12 = var10 * var12 + c[var14];
      }

      return var12 / var4 + e(var4, var6);
   }

   public static double a(double var0, double var2) {
      if (!Double.isNaN(var0) && !Double.isNaN(var2) && !(var0 <= 0.0) && !(var2 <= 0.0)) {
         double var4 = BY.d(var0, var2);
         double var6 = BY.e(var0, var2);
         if (var4 >= 10.0) {
            double var20 = f(var4, var6);
            double var22;
            double var24 = (var22 = var4 / var6) / (var22 + 1.0);
            double var25 = -(var4 - 0.5) * BY.k(var24);
            double var16 = var6 * BY.l(var22);
            return var25 <= var16
               ? -0.5 * BY.k(var6) + 0.9189385332046727 + var20 - var25 - var16
               : -0.5 * BY.k(var6) + 0.9189385332046727 + var20 - var16 - var25;
         }

         if (var4 > 2.0) {
            if (var6 > 1000.0) {
               int var19 = (int)BY.y(var4 - 1.0);
               double var9 = 1.0;
               double var11 = var4;

               for (int var13 = 0; var13 < var19; var13++) {
                  var11--;
                  var9 *= var11 / (1.0 + var11 / var6);
               }

               return BY.k(var9) - var19 * BY.k(var6) + (zY.a(var11) + d(var11, var6));
            } else {
               double var18 = 1.0;
               double var21 = var4;

               while (var21 > 2.0) {
                  double var12 = --var21 / var6;
                  var18 *= var12 / (var12 + 1.0);
               }

               if (!(var6 < 10.0)) {
                  return BY.k(var18) + zY.a(var21) + d(var21, var6);
               }

               double var23 = 1.0;

               double var14;
               for (var14 = var6; var14 > 2.0; var23 *= var14 / (var21 + var14)) {
                  var14--;
               }

               return BY.k(var18) + BY.k(var23) + (zY.a(var21) + (zY.a(var14) - c(var21, var14)));
            }
         } else {
            if (!(var4 >= 1.0)) {
               return var6 >= 10.0 ? zY.a(var4) + d(var4, var6) : BY.k(zY.d(var4) * zY.d(var6) / zY.d(var4 + var6));
            }

            if (!(var6 > 2.0)) {
               return zY.a(var4) + zY.a(var6) - c(var4, var6);
            }

            if (!(var6 < 10.0)) {
               return zY.a(var4) + d(var4, var6);
            }

            double var8 = 1.0;

            double var10;
            for (var10 = var6; var10 > 2.0; var8 *= var10 / (var4 + var10)) {
               var10--;
            }

            return BY.k(var8) + (zY.a(var4) + (zY.a(var10) - c(var4, var10)));
         }
      } else {
         return Double.NaN;
      }
   }
}
