package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class BH implements Serializable, BJ {
   private static long a;

   @Override
   public final double[] a(double[] var1, int var2) {
      return var2 == BK.FORWARD$4b20d4ed ? a(var1) : BL.a(a(var1), 1.0 / var1.length);
   }

   @Override
   public final double[] a(lc var1, double var2, double var4, int var6, int var7) {
      return this.a(kV.a(var1, var2, var4, var6), var7);
   }

   private int[] a(int[] var1) {
      int var2;
      int[] var8;
      int var3 = (var2 = (var8 = var1).length) / 2;
      if (!BM.a(var2)) {
         throw new oV(px.NOT_POWER_OF_TWO, var2);
      }

      int[] var4 = new int[var2];
      var1 = (int[])var8.clone();

      for (byte var5 = 1; var5 < var2; var5 <<= 1) {
         int[] var6 = var1;
         var1 = var4;
         var4 = var6;

         for (int var10 = 0; var10 < var3; var10++) {
            int var7 = 2 * var10;
            var1[var10] = var4[var7] + var4[var7 + 1];
         }

         for (int var11 = var3; var11 < var2; var11++) {
            int var12 = 2 * var11;
            var1[var11] = var4[var12 - var2] - var4[var12 - var2 + 1];
         }
      }

      return var1;
   }

   private static double[] a(double[] var0) {
      int var1;
      int var2 = (var1 = var0.length) / 2;
      if (!BM.a(var1)) {
         throw new oV(px.NOT_POWER_OF_TWO, var1);
      }

      double[] var3 = new double[var1];
      var0 = (double[])var0.clone();

      for (byte var4 = 1; var4 < var1; var4 <<= 1) {
         double[] var5 = var0;
         var0 = var3;
         var3 = var5;

         for (int var8 = 0; var8 < var2; var8++) {
            int var6 = 2 * var8;
            var0[var8] = var3[var6] + var3[var6 + 1];
         }

         for (int var9 = var2; var9 < var1; var9++) {
            int var10 = 2 * var9;
            var0[var9] = var3[var10 - var1] - var3[var10 - var1 + 1];
         }
      }

      return var0;
   }

   private static int[] b(int[] var0) {
      int var1;
      int var2 = (var1 = var0.length) / 2;
      if (!BM.a(var1)) {
         throw new oV(px.NOT_POWER_OF_TWO, var1);
      }

      int[] var3 = new int[var1];
      var0 = (int[])var0.clone();

      for (byte var4 = 1; var4 < var1; var4 <<= 1) {
         int[] var5 = var0;
         var0 = var3;
         var3 = var5;

         for (int var8 = 0; var8 < var2; var8++) {
            int var6 = 2 * var8;
            var0[var8] = var3[var6] + var3[var6 + 1];
         }

         for (int var9 = var2; var9 < var1; var9++) {
            int var10 = 2 * var9;
            var0[var9] = var3[var10 - var1] - var3[var10 - var1 + 1];
         }
      }

      return var0;
   }
}
