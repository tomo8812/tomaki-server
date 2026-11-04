package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class BI implements Serializable, BJ {
   private static long a;
   private final int b;

   private BI(int var1) {
      this.b = var1;
   }

   @Override
   public final double[] a(double[] var1, int var2) {
      if (this.b == BE.ORTHOGONAL_DST_I$553793ab) {
         double var5 = BY.a(2.0 / var1.length);
         return BL.a(a(var1), var5);
      }

      if (var2 == BK.FORWARD$4b20d4ed) {
         return a(var1);
      }

      double var3 = 2.0 / var1.length;
      return BL.a(a(var1), var3);
   }

   @Override
   public final double[] a(lc var1, double var2, double var4, int var6, int var7) {
      double[] var8;
      (var8 = kV.a(var1, var2, var4, var6))[0] = 0.0;
      return this.a(var8, var7);
   }

   private static double[] a(double[] var0) {
      double[] var1 = new double[var0.length];
      if (!BM.a(var0.length)) {
         throw new oV(px.NOT_POWER_OF_TWO_CONSIDER_PADDING, var0.length);
      }

      if (var0[0] != 0.0) {
         throw new oV(px.FIRST_ELEMENT_NOT_ZERO, var0[0]);
      }

      int var2;
      if ((var2 = var0.length) == 1) {
         var1[0] = 0.0;
         return var1;
      }

      double[] var3;
      (var3 = new double[var2])[0] = 0.0;
      var3[var2 >> 1] = 2.0 * var0[var2 >> 1];

      for (int var4 = 1; var4 < var2 >> 1; var4++) {
         double var5 = BY.n(var4 * Math.PI / var2) * (var0[var4] + var0[var2 - var4]);
         double var7 = 0.5 * (var0[var4] - var0[var2 - var4]);
         var3[var4] = var5 + var7;
         var3[var2 - var4] = var5 - var7;
      }

      nQ[] var12 = new BG(BD.STANDARD$1f30b0a8).a(var3, BK.FORWARD$4b20d4ed);
      var1[0] = 0.0;
      nQ var9;
      var1[1] = 0.5 * (var9 = var12[0]).d;

      for (int var6 = 1; var6 < var2 >> 1; var6++) {
         nQ var10;
         var1[2 * var6] = -(var10 = var12[var6]).c;
         nQ var11;
         var1[2 * var6 + 1] = (var11 = var12[var6]).d + var1[2 * var6 - 1];
      }

      return var1;
   }
}
