package org.valkyrienskies.core.impl.shadow;

public final class Ba {
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
         if (var8) {
            double var12 = var1[var14] - var15 * var0[var14];
            var9 += var12 * var12 / (var15 * var0[var14]);
         } else {
            double var16 = var1[var14] - var0[var14];
            var9 += var16 * var16 / var0[var14];
         }
      }

      return var9;
   }

   public final double b(double[] var1, long[] var2) {
      oi var3 = new oi(null, var1.length - 1.0);
      return 1.0 - var3.e(a(var1, var2));
   }

   public final boolean a(double[] var1, long[] var2, double var3) {
      if (!(var3 <= 0.0) && !(var3 > 0.5)) {
         return this.b(var1, var2) < var3;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var3, 0, 0.5);
      }
   }

   public final double a(long[][] var1) {
      c(var1);
      int var2 = var1.length;
      int var3 = var1[0].length;
      double[] var4 = new double[var2];
      double[] var5 = new double[var3];
      double var6 = 0.0;

      for (int var8 = 0; var8 < var2; var8++) {
         for (int var9 = 0; var9 < var3; var9++) {
            var4[var8] += var1[var8][var9];
            var5[var9] += var1[var8][var9];
            var6 += var1[var8][var9];
         }
      }

      double var14 = 0.0;

      for (int var12 = 0; var12 < var2; var12++) {
         for (int var13 = 0; var13 < var3; var13++) {
            double var10 = var4[var12] * var5[var13] / var6;
            var14 += (var1[var12][var13] - var10) * (var1[var12][var13] - var10) / var10;
         }
      }

      return var14;
   }

   public final double b(long[][] var1) {
      c(var1);
      double var2 = (var1.length - 1.0) * (var1[0].length - 1.0);
      oi var4 = new oi(var2);
      return 1.0 - var4.e(this.a(var1));
   }

   public final boolean a(long[][] var1, double var2) {
      if (!(var2 <= 0.0) && !(var2 > 0.5)) {
         return this.b(var1) < var2;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var2, 0, 0.5);
      }
   }

   public static double a(long[] var0, long[] var1) {
      if (var0.length < 2) {
         throw new oS(var0.length, 2);
      }

      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      Ch.a(var0);
      Ch.a(var1);
      long var2 = 0L;
      long var4 = 0L;
      double var7 = 0.0;

      for (int var9 = 0; var9 < var0.length; var9++) {
         var2 += var0[var9];
         var4 += var1[var9];
      }

      if (var2 != 0L && var4 != 0L) {
         boolean var6;
         if (var6 = var2 != var4) {
            var7 = BY.a((double)var2 / var4);
         }

         double var18 = 0.0;

         for (int var17 = 0; var17 < var0.length; var17++) {
            if (var0[var17] == 0L && var1[var17] == 0L) {
               throw new pr(px.OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY, var17);
            }

            double var13 = var0[var17];
            double var15 = var1[var17];
            double var11;
            if (var6) {
               var11 = var13 / var7 - var15 * var7;
            } else {
               var11 = var13 - var15;
            }

            var18 += var11 * var11 / (var13 + var15);
         }

         return var18;
      } else {
         throw new pr();
      }
   }

   public final double b(long[] var1, long[] var2) {
      oi var3 = new oi(null, var1.length - 1.0);
      return 1.0 - var3.e(a(var1, var2));
   }

   public final boolean a(long[] var1, long[] var2, double var3) {
      if (!(var3 <= 0.0) && !(var3 > 0.5)) {
         return this.b(var1, var2) < var3;
      } else {
         throw new po(px.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, var3, 0, 0.5);
      }
   }

   private static void c(long[][] var0) {
      if (var0.length < 2) {
         throw new oS(var0.length, 2);
      }

      if (var0[0].length < 2) {
         throw new oS(var0[0].length, 2);
      }

      Ch.a(var0);
      Ch.b(var0);
   }
}
