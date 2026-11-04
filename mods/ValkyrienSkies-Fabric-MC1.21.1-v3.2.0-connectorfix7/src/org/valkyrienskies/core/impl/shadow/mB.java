package org.valkyrienskies.core.impl.shadow;

public final class mB implements ng {
   private static final int a = 5;

   public final nl a(double[] var1, double[] var2) {
      if (var1 != null && var2 != null) {
         if (var1.length != var2.length) {
            throw new oS(var1.length, var2.length);
         }

         if (var1.length < 5) {
            throw new pn(px.NUMBER_OF_POINTS, var1.length, 5, true);
         }

         Ch.a(var1);
         int var3;
         double[] var4 = new double[var3 = var1.length - 1];
         double[] var39 = new double[var3];

         for (int var5 = 0; var5 < var4.length; var5++) {
            var4[var5] = (var2[var5 + 1] - var2[var5]) / (var1[var5 + 1] - var1[var5]);
         }

         for (int var42 = 1; var42 < var39.length; var42++) {
            var39[var42] = BY.w(var4[var42] - var4[var42 - 1]);
         }

         double[] var43 = new double[var1.length];

         for (int var6 = 2; var6 < var43.length - 2; var6++) {
            double var8 = var39[var6 + 1];
            double var10 = var39[var6 - 1];
            if (Cq.a(var8, 0.0) && Cq.a(var10, 0.0)) {
               double var12 = var1[var6];
               double var14 = var1[var6 + 1];
               double var16 = var1[var6 - 1];
               var43[var6] = ((var14 - var12) * var4[var6 - 1] + (var12 - var16) * var4[var6]) / (var14 - var16);
            } else {
               var43[var6] = (var8 * var4[var6 - 1] + var10 * var4[var6]) / (var8 + var10);
            }
         }

         var43[0] = a(var1, var2, 0, 0, 1, 2);
         var43[1] = a(var1, var2, 1, 0, 1, 2);
         var43[var1.length - 2] = a(var1, var2, var1.length - 2, var1.length - 3, var1.length - 2, var1.length - 1);
         var43[var1.length - 1] = a(var1, var2, var1.length - 1, var1.length - 3, var1.length - 2, var1.length - 1);
         double[] var40 = var43;
         var2 = var2;
         var1 = var1;
         if (var1.length != var2.length) {
            throw new oS(var1.length, var2.length);
         }

         if (var1.length != var40.length) {
            throw new oS(var1.length, var40.length);
         }

         if (var1.length < 2) {
            throw new pn(px.NUMBER_OF_POINTS, var1.length, 2, true);
         }

         ni[] var41 = new ni[var1.length - 1];
         var43 = new double[4];

         for (int var45 = 0; var45 < var41.length; var45++) {
            double var25;
            double var27 = (var25 = var1[var45 + 1] - var1[var45]) * var25;
            double var29 = var2[var45];
            double var31 = var2[var45 + 1];
            double var33 = var40[var45];
            double var35 = var40[var45 + 1];
            var43[0] = var29;
            var43[1] = var40[var45];
            var43[2] = (3.0 * (var31 - var29) / var25 - var33 * 2.0 - var35) / var25;
            var43[3] = (2.0 * (var29 - var31) / var25 + var33 + var35) / var27;
            var41[var45] = new ni(var43);
         }

         return new nl(var1, var41);
      } else {
         throw new pl();
      }
   }

   private static double a(double[] var0, double[] var1, int var2, int var3, int var4, int var5) {
      double var6 = var1[var3];
      double var8 = var1[var4];
      double var10 = var1[var5];
      double var12 = var0[var2] - var0[var3];
      double var14 = var0[var4] - var0[var3];
      double var16 = var0[var5] - var0[var3];
      double var18 = (var10 - var6 - var16 / var14 * (var8 - var6)) / (var16 * var16 - var14 * var16);
      double var20 = (var8 - var6 - var18 * var14 * var14) / var14;
      return var18 * 2.0 * var12 + var20;
   }

   private static nl a(double[] var0, double[] var1, double[] var2) {
      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      if (var0.length != var2.length) {
         throw new oS(var0.length, var2.length);
      }

      if (var0.length < 2) {
         throw new pn(px.NUMBER_OF_POINTS, var0.length, 2, true);
      }

      ni[] var3 = new ni[var0.length - 1];
      double[] var4 = new double[4];

      for (int var5 = 0; var5 < var3.length; var5++) {
         double var7;
         double var9 = (var7 = var0[var5 + 1] - var0[var5]) * var7;
         double var11 = var1[var5];
         double var13 = var1[var5 + 1];
         double var15 = var2[var5];
         double var17 = var2[var5 + 1];
         var4[0] = var11;
         var4[1] = var2[var5];
         var4[2] = (3.0 * (var13 - var11) / var7 - var15 * 2.0 - var17) / var7;
         var4[3] = (2.0 * (var11 - var13) / var7 + var15 + var17) / var9;
         var3[var5] = new ni(var4);
      }

      return new nl(var0, var3);
   }

   // $VF: synthetic method
   @Override
   public final lc b(double[] var1, double[] var2) {
      return this.a(var1, var2);
   }
}
