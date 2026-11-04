package org.valkyrienskies.core.impl.shadow;

public final class Bb {
   public static double a(double[] var0, long[] var1) {
      if (var0.length < 2) {
         throw new oS(var0.length, 2);
      }

      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      Ch.b(var0);
      Ch.a(var1);
      double var2 = 0.0;
      double var4 = 0.0;

      for (int var6 = 0; var6 < var1.length; var6++) {
         var2 += var0[var6];
         var4 += var1[var6];
      }

      double var15 = 1.0;
      boolean var8 = false;
      if (BY.w(var2 - var4) > 1.0E-5) {
         var15 = var4 / var2;
         var8 = true;
      }

      double var9 = 0.0;

      for (int var14 = 0; var14 < var1.length; var14++) {
         double var12 = var8 ? BY.k(var1[var14] / (var15 * var0[var14])) : BY.k(var1[var14] / var0[var14]);
         var9 += var1[var14] * var12;
      }

      return var9 * 2.0;
   }

   public final double b(double[] var1, long[] var2) {
      oi var3 = new oi(null, var1.length - 1.0);
      return 1.0 - var3.e(a(var1, var2));
   }

   public final double c(double[] var1, long[] var2) {
      oi var3 = new oi(null, var1.length - 2.0);
      return 1.0 - var3.e(a(var1, var2));
   }

   public final boolean a(double[] var1, long[] var2, double var3) {
      if (!(var3 <= 0.0) && !(var3 > 0.5)) {
         return this.b(var1, var2) < var3;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var3, 0, 0.5);
      }
   }

   private static double a(long[][] var0) {
      double var1 = 0.0;
      double var3 = 0.0;

      for (int var5 = 0; var5 < var0.length; var5++) {
         for (int var6 = 0; var6 < var0[var5].length; var6++) {
            var3 += var0[var5][var6];
         }
      }

      for (int var9 = 0; var9 < var0.length; var9++) {
         for (int var10 = 0; var10 < var0[var9].length; var10++) {
            if (var0[var9][var10] != 0L) {
               double var7 = var0[var9][var10] / var3;
               var1 += var7 * BY.k(var7);
            }
         }
      }

      return -var1;
   }

   private static double a(long[] var0) {
      double var1 = 0.0;
      double var3 = 0.0;

      for (int var5 = 0; var5 < var0.length; var5++) {
         var3 += var0[var5];
      }

      for (int var8 = 0; var8 < var0.length; var8++) {
         if (var0[var8] != 0L) {
            double var6 = var0[var8] / var3;
            var1 += var6 * BY.k(var6);
         }
      }

      return -var1;
   }

   public final double a(long[] var1, long[] var2) {
      if (var1.length < 2) {
         throw new oS(var1.length, 2);
      }

      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      Ch.a(var1);
      Ch.a(var2);
      long var3 = 0L;
      long var5 = 0L;
      long[] var7 = new long[var1.length];
      long[][] var8 = new long[2][var1.length];

      for (int var9 = 0; var9 < var1.length; var9++) {
         if (var1[var9] == 0L && var2[var9] == 0L) {
            throw new pr(px.OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY, var9);
         }

         var3 += var1[var9];
         var5 += var2[var9];
         var7[var9] = var1[var9] + var2[var9];
         var8[0][var9] = var1[var9];
         var8[1][var9] = var2[var9];
      }

      if (var3 != 0L && var5 != 0L) {
         long[] var26 = new long[]{var3, var5};
         double var10 = (double)var3 + var5;
         double var10000 = var10 * 2.0;
         double var10001 = a(var26) + a(var7);
         long[][] var21 = var8;
         double var13 = 0.0;
         double var15 = 0.0;

         for (int var22 = 0; var22 < var21.length; var22++) {
            for (int var24 = 0; var24 < var21[var22].length; var24++) {
               var15 += var21[var22][var24];
            }
         }

         for (int var23 = 0; var23 < var21.length; var23++) {
            for (int var25 = 0; var25 < var21[var23].length; var25++) {
               if (var21[var23][var25] != 0L) {
                  double var19 = var21[var23][var25] / var15;
                  var13 += var19 * BY.k(var19);
               }
            }
         }

         return var10000 * (var10001 - -var13);
      } else {
         throw new pr();
      }
   }

   public final double a(long var1, long var3, long var5, long var7) {
      double var11 = BY.a(this.a(new long[]{var1, var3}, new long[]{var5, var7}));
      if ((double)var1 / (var1 + var3) < (double)var5 / (var5 + var7)) {
         var11 = -var11;
      }

      return var11;
   }

   public final double b(long[] var1, long[] var2) {
      oi var3 = new oi(null, var1.length - 1.0);
      return 1.0 - var3.e(this.a(var1, var2));
   }

   public final boolean a(long[] var1, long[] var2, double var3) {
      if (!(var3 <= 0.0) && !(var3 > 0.5)) {
         return this.b(var1, var2) < var3;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var3, 0, 0.5);
      }
   }
}
