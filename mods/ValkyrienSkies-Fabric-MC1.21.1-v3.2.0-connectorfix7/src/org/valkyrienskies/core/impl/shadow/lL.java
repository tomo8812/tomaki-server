package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class lL implements kS, ln {
   private final double a;
   private final double b;
   private final double c;
   private final double d;

   public lL(double var1, double var3, double var5) {
      if (var5 <= 0.0) {
         throw new pk(var5);
      }

      this.d = var1;
      this.a = var3;
      this.b = 1.0 / var5;
      this.c = 0.5 * this.b * this.b;
   }

   private lL(double var1, double var3) {
      this(1.0 / (1.0 * BY.a(Math.PI * 2)), 0.0, 1.0);
   }

   public lL() {
      this(0.0, 1.0);
   }

   @Override
   public final double a(double var1) {
      return a(var1 - this.a, this.d, this.c);
   }

   @Deprecated
   @Override
   public final lc a() {
      return kV.a(this).a();
   }

   static double a(double var0, double var2, double var4) {
      return var2 * BY.i(-var0 * var0 * var4);
   }

   @Override
   public final lg a(lg var1) {
      double var2 = this.b * (var1.b[0] - this.a);
      double[] var4;
      double[] var5;
      (var5 = new double[(var4 = new double[var1.D() + 1]).length])[0] = 1.0;
      double var6 = var2 * var2;
      double var8;
      if ((var8 = this.d * BY.i(var6 * -0.5)) <= Cq.b) {
         Arrays.fill(var4, 0.0);
      } else {
         var4[0] = var8;

         for (int var10 = 1; var10 < var4.length; var10++) {
            double var11 = 0.0;
            var5[var10] = -var5[var10 - 1];

            for (int var13 = var10; var13 >= 0; var13 -= 2) {
               var11 = var11 * var6 + var5[var13];
               if (var13 > 2) {
                  var5[var13 - 2] = (var13 - 1) * var5[var13 - 1] - var5[var13 - 3];
               } else if (var13 == 2) {
                  var5[0] = var5[1];
               }
            }

            if ((var10 & 1) == 1) {
               var11 *= var2;
            }

            var8 *= this.b;
            var4[var10] = var8 * var11;
         }
      }

      return var1.a(var4);
   }

   // $VF: synthetic method
   static double b(double var0, double var2, double var4) {
      return a(var0, var2, var4);
   }

   public static class a implements kZ {
      @Override
      public double a(double var1, double... var3) {
         a(var3);
         double var4 = var1 - var3[1];
         double var6 = 1.0 / (2.0 * var3[2] * var3[2]);
         double var10001 = var3[0];
         double var12 = var6;
         double var10 = var10001;
         return lL.a(var4, var10, var12);
      }

      @Override
      public double[] b(double var1, double... var3) {
         a(var3);
         double var4 = var3[0];
         double var6 = var1 - var3[1];
         double var8 = var3[2];
         double var10 = 1.0 / (var8 * 2.0 * var8);
         double var22 = var10;
         double var20 = 1.0;
         double var12 = lL.a(var6, var20, var22);
         double var14;
         double var16 = (var14 = var4 * var12 * 2.0 * var10 * var6) * var6 / var8;
         return new double[]{var12, var14, var16};
      }

      private static void a(double[] var0) {
         if (var0 == null) {
            throw new pl();
         }

         if (var0.length != 3) {
            throw new oS(var0.length, 3);
         }

         if (var0[2] <= 0.0) {
            throw new pk(var0[2]);
         }
      }
   }
}
