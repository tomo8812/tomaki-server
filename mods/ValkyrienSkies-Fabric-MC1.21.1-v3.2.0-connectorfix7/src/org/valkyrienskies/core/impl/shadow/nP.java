package org.valkyrienskies.core.impl.shadow;

public final class nP {
   private nP() {
   }

   private static double c(lc var0, double var1, double var3) {
      if (var0 == null) {
         throw new pl(px.FUNCTION);
      } else {
         return new nz().a(Integer.MAX_VALUE, var0, var1, var3);
      }
   }

   public static double a(<unrepresentable> var0, double var1, double var3, double var5) {
      return new nz(var5).a(Integer.MAX_VALUE, var0, var1, var3);
   }

   public static double a(int var0, lc var1, nx<lc> var2, double var3, double var5, double var7, int var9) {
      if (var9 == nr.ANY_SIDE$37d0779) {
         return var3;
      }

      double var10 = BY.e(var2.g(), BY.w(var3 * var2.h()));
      double var12 = BY.e(var5, var3 - var10);
      double var14 = var1.a(var12);
      double var16 = BY.d(var7, var3 + var10);
      double var18 = var1.a(var16);
      int var20 = var0 - 2;

      while (var20 > 0) {
         if (var14 >= 0.0 && var18 <= 0.0 || var14 <= 0.0 && var18 >= 0.0) {
            return var2.a(var20, var1, var12, var16, var3, var9);
         }

         boolean var21 = false;
         boolean var22 = false;
         if (var14 < var18) {
            if (var14 >= 0.0) {
               var21 = true;
            } else {
               var22 = true;
            }
         } else if (var14 > var18) {
            if (var14 <= 0.0) {
               var21 = true;
            } else {
               var22 = true;
            }
         } else {
            var21 = true;
            var22 = true;
         }

         if (var21) {
            var12 = BY.e(var5, var12 - var10);
            var14 = var1.a(var12);
            var20--;
         }

         if (var22) {
            var16 = BY.d(var7, var16 + var10);
            var18 = var1.a(var16);
            var20--;
         }
      }

      throw new pe(px.FAILED_BRACKETING, var12, var16, var14, var18, var0 - var20, var0, var3, var5, var7);
   }

   private static double[] a(lc var0, double var1, double var3, double var5) {
      return a(var0, var1, var3, var5, 1.0, 1.0, Integer.MAX_VALUE);
   }

   private static double[] a(lc var0, double var1, double var3, double var5, int var7) {
      return a(var0, var1, var3, var5, 1.0, 1.0, var7);
   }

   private static double[] a(lc var0, double var1, double var3, double var5, double var7, double var9, int var11) {
      if (var0 == null) {
         throw new pl(px.FUNCTION);
      }

      if (var11 <= 0) {
         throw new pk(px.INVALID_MAX_ITERATIONS, var11);
      }

      b(var3, var1, var5);
      double var12 = var1;
      double var14 = var1;
      double var16 = Double.NaN;
      double var18 = Double.NaN;
      double var20 = 0.0;

      for (int var31 = 0; var31 < var11 && (var12 > var3 || var14 < var5); var31++) {
         double var23 = var12;
         double var25 = var16;
         double var27 = var14;
         double var29 = var18;
         var20 = var20 * 1.0 + 1.0;
         var12 = BY.e(var1 - var20, var3);
         var14 = BY.d(var1 + var20, var5);
         var16 = var0.a(var12);
         var18 = var0.a(var14);
         if (var31 == 0) {
            if (var16 * var18 <= 0.0) {
               return new double[]{var12, var14};
            }
         } else {
            if (var16 * var25 <= 0.0) {
               return new double[]{var12, var23};
            }

            if (var18 * var29 <= 0.0) {
               return new double[]{var27, var14};
            }
         }
      }

      throw new pe(var12, var14, var16, var18);
   }

   public static double a(double var0, double var2) {
      return (var0 + var2) * 0.5;
   }

   public static boolean a(lc var0, double var1, double var3) {
      if (var0 == null) {
         throw new pl(px.FUNCTION);
      }

      double var5 = var0.a(var1);
      double var7 = var0.a(var3);
      return var5 >= 0.0 && var7 <= 0.0 || var5 <= 0.0 && var7 >= 0.0;
   }

   public static boolean a(double var0, double var2, double var4) {
      return var0 < var2 && var2 < var4;
   }

   public static void b(double var0, double var2) {
      if (var0 >= var2) {
         throw new pm(px.ENDPOINTS_NOT_AN_INTERVAL, var0, var2, false);
      }
   }

   public static void b(double var0, double var2, double var4) {
      b(var0, var2);
      b(var2, var4);
   }

   public static void b(lc var0, double var1, double var3) {
      if (var0 == null) {
         throw new pl(px.FUNCTION);
      }

      b(var1, var3);
      if (!a(var0, var1, var3)) {
         throw new pe(var1, var3, var0.a(var1), var0.a(var3));
      }
   }
}
