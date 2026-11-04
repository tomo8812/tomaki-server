package org.valkyrienskies.core.impl.shadow;

public final class mY implements ng {
   public static nl a(double[] var0, double[] var1) {
      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      if (var0.length < 3) {
         throw new pn(px.NUMBER_OF_POINTS, var0.length, 3, true);
      }

      int var2 = var0.length - 1;
      Ch.a(var0);
      double[] var3 = new double[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = var0[var4 + 1] - var0[var4];
      }

      double[] var11 = new double[var2];
      double[] var5 = new double[var2 + 1];
      var11[0] = 0.0;
      var5[0] = 0.0;

      for (int var8 = 1; var8 < var2; var8++) {
         double var6 = 2.0 * (var0[var8 + 1] - var0[var8 - 1]) - var3[var8 - 1] * var11[var8 - 1];
         var11[var8] = var3[var8] / var6;
         var5[var8] = (
               3.0
                     * (var1[var8 + 1] * var3[var8 - 1] - var1[var8] * (var0[var8 + 1] - var0[var8 - 1]) + var1[var8 - 1] * var3[var8])
                     / (var3[var8 - 1] * var3[var8])
                  - var3[var8 - 1] * var5[var8 - 1]
            )
            / var6;
      }

      double[] var14 = new double[var2];
      double[] var13 = new double[var2 + 1];
      double[] var7 = new double[var2];
      var5[var2] = 0.0;
      var13[var2] = 0.0;

      for (int var9 = var2 - 1; var9 >= 0; var9--) {
         var13[var9] = var5[var9] - var11[var9] * var13[var9 + 1];
         var14[var9] = (var1[var9 + 1] - var1[var9]) / var3[var9] - var3[var9] * (var13[var9 + 1] + 2.0 * var13[var9]) / 3.0;
         var7[var9] = (var13[var9 + 1] - var13[var9]) / (3.0 * var3[var9]);
      }

      ni[] var15 = new ni[var2];
      var3 = new double[4];

      for (int var12 = 0; var12 < var2; var12++) {
         var3[0] = var1[var12];
         var3[1] = var14[var12];
         var3[2] = var13[var12];
         var3[3] = var7[var12];
         var15[var12] = new ni(var3);
      }

      return new nl(var0, var15);
   }

   // $VF: synthetic method
   @Override
   public final lc b(double[] var1, double[] var2) {
      return a(var1, var2);
   }
}
