package org.valkyrienskies.core.impl.shadow;

final class mC implements kP {
   private static final short a = 4;
   private final double[][] b = new double[4][4];

   mC(double[] var1) {
      for (int var2 = 0; var2 < 4; var2++) {
         double[] var3 = this.b[var2];

         for (int var4 = 0; var4 < 4; var4++) {
            var3[var4] = var1[(var4 << 2) + var2];
         }
      }
   }

   @Override
   public final double a(double var1, double var3) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0, 1);
      }

      if (!(var3 < 0.0) && !(var3 > 1.0)) {
         double var5;
         double var7 = (var5 = var1 * var1) * var1;
         double[] var23 = new double[]{1.0, var1, var5, var7};
         double var10;
         double var12 = (var10 = var3 * var3) * var3;
         double[] var2 = new double[]{1.0, var3, var10, var12};
         double[][] var26 = this.b;
         var2 = var2;
         double[] var24 = var23;
         double var18 = 0.0;

         for (int var4 = 0; var4 < 4; var4++) {
            double var21 = Ch.h(var26[var4], var2);
            var18 += var21 * var24[var4];
         }

         return var18;
      } else {
         throw new po(var3, 0, 1);
      }
   }

   private static double a(double[] var0, double[] var1, double[][] var2) {
      double var3 = 0.0;

      for (int var5 = 0; var5 < 4; var5++) {
         double var6 = Ch.h(var2[var5], var1);
         var3 += var6 * var0[var5];
      }

      return var3;
   }
}
