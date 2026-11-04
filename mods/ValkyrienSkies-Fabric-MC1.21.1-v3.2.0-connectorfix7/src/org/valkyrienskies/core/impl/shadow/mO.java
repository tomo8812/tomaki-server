package org.valkyrienskies.core.impl.shadow;

public final class mO implements ng {
   private static nl a(double[] var0, double[] var1) {
      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      if (var0.length < 2) {
         throw new pn(px.NUMBER_OF_POINTS, var0.length, 2, true);
      }

      int var2 = var0.length - 1;
      Ch.a(var0);
      double[] var3 = new double[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = (var1[var4 + 1] - var1[var4]) / (var0[var4 + 1] - var0[var4]);
      }

      ni[] var7 = new ni[var2];
      double[] var5 = new double[2];

      for (int var6 = 0; var6 < var2; var6++) {
         var5[0] = var1[var6];
         var5[1] = var3[var6];
         var7[var6] = new ni(var5);
      }

      return new nl(var0, var7);
   }

   // $VF: synthetic method
   @Override
   public final lc b(double[] var1, double[] var2) {
      var2 = var2;
      var1 = var1;
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      if (var1.length < 2) {
         throw new pn(px.NUMBER_OF_POINTS, var1.length, 2, true);
      }

      int var3 = var1.length - 1;
      Ch.a(var1);
      double[] var4 = new double[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         var4[var5] = (var2[var5 + 1] - var2[var5]) / (var1[var5 + 1] - var1[var5]);
      }

      ni[] var10 = new ni[var3];
      double[] var6 = new double[2];

      for (int var7 = 0; var7 < var3; var7++) {
         var6[0] = var2[var7];
         var6[1] = var4[var7];
         var10[var7] = new ni(var6);
      }

      return new nl(var1, var10);
   }
}
