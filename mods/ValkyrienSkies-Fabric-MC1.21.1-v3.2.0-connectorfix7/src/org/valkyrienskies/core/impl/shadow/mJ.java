package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class mJ implements Serializable, ng {
   private static final long a = 107049519551235069L;

   private static nk a(double[] var0, double[] var1) {
      nj.a(var0, var1, true);
      double[] var2 = new double[var0.length - 1];
      System.arraycopy(var0, 0, var2, 0, var2.length);
      var1 = var1;
      double[] var13 = var0;
      nj.a(var0, var1, true);
      var1 = (double[])var1.clone();
      int var3;
      double[] var4;
      (var4 = new double[var3 = var13.length])[0] = var1[0];

      for (int var5 = 1; var5 < var3; var5++) {
         for (int var6 = 0; var6 < var3 - var5; var6++) {
            double var11 = var13[var6 + var5] - var13[var6];
            var1[var6] = (var1[var6 + 1] - var1[var6]) / var11;
         }

         var4[var5] = var1[0];
      }

      var0 = var4;
      return new nk(var0, var2);
   }

   private static double[] c(double[] var0, double[] var1) {
      nj.a(var0, var1, true);
      var1 = (double[])var1.clone();
      int var2;
      double[] var3;
      (var3 = new double[var2 = var0.length])[0] = var1[0];

      for (int var4 = 1; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var2 - var4; var5++) {
            double var7 = var0[var5 + var4] - var0[var5];
            var1[var5] = (var1[var5 + 1] - var1[var5]) / var7;
         }

         var3[var4] = var1[0];
      }

      return var3;
   }

   // $VF: synthetic method
   @Override
   public final lc b(double[] var1, double[] var2) {
      var2 = var2;
      double[] var16 = var1;
      nj.a(var1, var2, true);
      double[] var3 = new double[var16.length - 1];
      System.arraycopy(var16, 0, var3, 0, var3.length);
      var2 = var2;
      var1 = var16;
      nj.a(var16, var2, true);
      var2 = (double[])var2.clone();
      int var4;
      double[] var5;
      (var5 = new double[var4 = var1.length])[0] = var2[0];

      for (int var6 = 1; var6 < var4; var6++) {
         for (int var7 = 0; var7 < var4 - var6; var7++) {
            double var14 = var1[var7 + var6] - var1[var7];
            var2[var7] = (var2[var7 + 1] - var2[var7]) / var14;
         }

         var5[var6] = var2[0];
      }

      var1 = var5;
      return new nk(var1, var3);
   }
}
