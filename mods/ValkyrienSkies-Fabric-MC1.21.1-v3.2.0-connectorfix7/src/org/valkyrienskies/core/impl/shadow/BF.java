package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class BF implements Serializable, BJ {
   private static long a;
   private final int b;

   private BF(int var1) {
      this.b = var1;
   }

   @Override
   public final double[] a(double[] var1, int var2) {
      if (var2 == BK.FORWARD$4b20d4ed) {
         if (this.b == BC.ORTHOGONAL_DCT_I$3c19b5bb) {
            double var7 = BY.a(2.0 / (var1.length - 1));
            return BL.a(a(var1), var7);
         } else {
            return a(var1);
         }
      } else {
         double var3 = 2.0 / (var1.length - 1);
         double var5;
         if (this.b == BC.ORTHOGONAL_DCT_I$3c19b5bb) {
            var5 = BY.a(var3);
         } else {
            var5 = var3;
         }

         return BL.a(a(var1), var5);
      }
   }

   @Override
   public final double[] a(lc var1, double var2, double var4, int var6, int var7) {
      double[] var8 = kV.a(var1, var2, var4, var6);
      return this.a(var8, var7);
   }

   private static double[] a(double[] var0) {
      double[] var1 = new double[var0.length];
      int var2;
      if (!BM.a(var2 = var0.length - 1)) {
         throw new oV(px.NOT_POWER_OF_TWO_PLUS_ONE, var0.length);
      }

      if (var2 == 1) {
         var1[0] = 0.5 * (var0[0] + var0[1]);
         var1[1] = 0.5 * (var0[0] - var0[1]);
         return var1;
      }

      double[] var3;
      (var3 = new double[var2])[0] = 0.5 * (var0[0] + var0[var2]);
      var3[var2 >> 1] = var0[var2 >> 1];
      double var4 = 0.5 * (var0[0] - var0[var2]);

      for (int var6 = 1; var6 < var2 >> 1; var6++) {
         double var7 = 0.5 * (var0[var6] + var0[var2 - var6]);
         double var9 = BY.n(var6 * Math.PI / var2) * (var0[var6] - var0[var2 - var6]);
         double var11 = BY.o(var6 * Math.PI / var2) * (var0[var6] - var0[var2 - var6]);
         var3[var6] = var7 - var9;
         var3[var2 - var6] = var7 + var9;
         var4 += var11;
      }

      nQ[] var17 = new BG(BD.STANDARD$1f30b0a8).a(var3, BK.FORWARD$4b20d4ed);
      nQ var13;
      var1[0] = (var13 = var17[0]).d;
      var1[1] = var4;

      for (int var8 = 1; var8 < var2 >> 1; var8++) {
         nQ var14;
         var1[2 * var8] = (var14 = var17[var8]).d;
         nQ var15;
         var1[2 * var8 + 1] = var1[2 * var8 - 1] - (var15 = var17[var8]).c;
      }

      nQ var16;
      var1[var2] = (var16 = var17[var2 >> 1]).d;
      return var1;
   }
}
