package org.valkyrienskies.core.impl.shadow;

final class mZ implements lb {
   private static final short a = 4;
   private final double[][][] b = new double[4][4][4];

   mZ(double[] var1) {
      for (int var2 = 0; var2 < 4; var2++) {
         for (int var3 = 0; var3 < 4; var3++) {
            for (int var4 = 0; var4 < 4; var4++) {
               this.b[var2][var3][var4] = var1[var2 + 4 * (var3 + 4 * var4)];
            }
         }
      }
   }

   @Override
   public final double a(double var1, double var3, double var5) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0, 1);
      }

      if (!(var3 < 0.0) && !(var3 > 1.0)) {
         if (!(var5 < 0.0) && !(var5 > 1.0)) {
            double var7;
            double var9 = (var7 = var1 * var1) * var1;
            double[] var24 = new double[]{1.0, var1, var7, var9};
            double var12;
            double var14 = (var12 = var3 * var3) * var3;
            double[] var2 = new double[]{1.0, var3, var12, var14};
            double var17;
            double var19 = (var17 = var5 * var5) * var5;
            double[] var25 = new double[]{1.0, var5, var17, var19};
            double var22 = 0.0;

            for (int var4 = 0; var4 < 4; var4++) {
               for (int var26 = 0; var26 < 4; var26++) {
                  for (int var6 = 0; var6 < 4; var6++) {
                     var22 += this.b[var4][var26][var6] * var24[var4] * var2[var26] * var25[var6];
                  }
               }
            }

            return var22;
         } else {
            throw new po(var5, 0, 1);
         }
      } else {
         throw new po(var3, 0, 1);
      }
   }
}
