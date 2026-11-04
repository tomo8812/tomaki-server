package org.valkyrienskies.core.impl.shadow;

public final class nj implements lc {
   private double[] a;
   private final double[] b;
   private final double[] c;
   private boolean d;

   public nj(double[] var1, double[] var2) {
      this.b = new double[var1.length];
      this.c = new double[var2.length];
      System.arraycopy(var1, 0, this.b, 0, var1.length);
      System.arraycopy(var2, 0, this.c, 0, var2.length);
      this.d = false;
      if (!a(var1, var2, false)) {
         Ch.a(this.b, new double[][]{this.c});
         a(this.b, this.c, true);
      }
   }

   @Override
   public final double a(double var1) {
      return b(this.b, this.c, var1);
   }

   private int a() {
      return this.b.length - 1;
   }

   private double[] b() {
      double[] var1 = new double[this.b.length];
      System.arraycopy(this.b, 0, var1, 0, this.b.length);
      return var1;
   }

   private double[] c() {
      double[] var1 = new double[this.c.length];
      System.arraycopy(this.c, 0, var1, 0, this.c.length);
      return var1;
   }

   private double[] d() {
      if (!this.d) {
         nj var1 = this;
         int var2 = this.b.length - 1 + 1;
         var1.a = new double[var2];

         for (int var3 = 0; var3 < var2; var3++) {
            var1.a[var3] = 0.0;
         }

         double[] var12;
         (var12 = new double[var2 + 1])[0] = 1.0;

         for (int var4 = 0; var4 < var2; var4++) {
            for (int var5 = var4; var5 > 0; var5--) {
               var12[var5] = var12[var5 - 1] - var12[var5] * var1.b[var4];
            }

            var12[0] *= -var1.b[var4];
            var12[var4 + 1] = 1.0;
         }

         double[] var13 = new double[var2];

         for (int var14 = 0; var14 < var2; var14++) {
            double var7 = 1.0;

            for (int var9 = 0; var9 < var2; var9++) {
               if (var14 != var9) {
                  var7 *= var1.b[var14] - var1.b[var9];
               }
            }

            double var15 = var1.c[var14] / var7;
            var13[var2 - 1] = var12[var2];
            var1.a[var2 - 1] = var1.a[var2 - 1] + var15 * var13[var2 - 1];

            for (int var6 = var2 - 2; var6 >= 0; var6--) {
               var13[var6] = var12[var6 + 1] + var13[var6 + 1] * var1.b[var14];
               var1.a[var6] = var1.a[var6] + var15 * var13[var6];
            }
         }

         var1.d = true;
      }

      double[] var11 = new double[this.a.length];
      System.arraycopy(this.a, 0, var11, 0, this.a.length);
      return var11;
   }

   private static double a(double[] var0, double[] var1, double var2) {
      if (a(var0, var1, false)) {
         return b(var0, var1, var2);
      }

      double[] var4 = new double[var0.length];
      double[] var5 = new double[var1.length];
      System.arraycopy(var0, 0, var4, 0, var0.length);
      System.arraycopy(var1, 0, var5, 0, var1.length);
      Ch.a(var4, new double[][]{var5});
      a(var4, var5, true);
      return b(var4, var5, var2);
   }

   private static double b(double[] var0, double[] var1, double var2) {
      int var4 = 0;
      int var5;
      double[] var6 = new double[var5 = var0.length];
      double[] var7 = new double[var5];
      double var8 = Double.POSITIVE_INFINITY;

      for (int var10 = 0; var10 < var5; var10++) {
         var6[var10] = var1[var10];
         var7[var10] = var1[var10];
         double var11;
         if ((var11 = BY.w(var2 - var0[var10])) < var8) {
            var4 = var10;
            var8 = var11;
         }
      }

      double var23 = var1[var4];

      for (int var12 = 1; var12 < var5; var12++) {
         for (int var22 = 0; var22 < var5 - var12; var22++) {
            double var14 = var0[var22] - var2;
            double var16 = var0[var12 + var22] - var2;
            double var18 = var0[var22] - var0[var12 + var22];
            double var20 = (var6[var22 + 1] - var7[var22]) / var18;
            var6[var22] = var14 * var20;
            var7[var22] = var16 * var20;
         }

         if (var4 < 0.5 * (var5 - var12 + 1)) {
            var23 += var6[var4];
         } else {
            var23 += var7[--var4];
         }
      }

      return var23;
   }

   private void e() {
      int var1 = this.b.length - 1 + 1;
      this.a = new double[var1];

      for (int var2 = 0; var2 < var1; var2++) {
         this.a[var2] = 0.0;
      }

      double[] var9;
      (var9 = new double[var1 + 1])[0] = 1.0;

      for (int var3 = 0; var3 < var1; var3++) {
         for (int var4 = var3; var4 > 0; var4--) {
            var9[var4] = var9[var4 - 1] - var9[var4] * this.b[var3];
         }

         var9[0] *= -this.b[var3];
         var9[var3 + 1] = 1.0;
      }

      double[] var10 = new double[var1];

      for (int var11 = 0; var11 < var1; var11++) {
         double var5 = 1.0;

         for (int var7 = 0; var7 < var1; var7++) {
            if (var11 != var7) {
               var5 *= this.b[var11] - this.b[var7];
            }
         }

         double var13 = this.c[var11] / var5;
         var10[var1 - 1] = var9[var1];
         this.a[var1 - 1] = this.a[var1 - 1] + var13 * var10[var1 - 1];

         for (int var12 = var1 - 2; var12 >= 0; var12--) {
            var10[var12] = var9[var12 + 1] + var10[var12 + 1] * this.b[var11];
            this.a[var12] = this.a[var12] + var13 * var10[var12];
         }
      }

      this.d = true;
   }

   public static boolean a(double[] var0, double[] var1, boolean var2) {
      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      } else if (var0.length < 2) {
         throw new pn(px.WRONG_NUMBER_OF_POINTS, 2, var0.length, true);
      } else {
         return Ch.a(var0, Ch.b.INCREASING$37efe540, true, var2);
      }
   }
}
