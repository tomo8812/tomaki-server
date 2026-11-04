package org.valkyrienskies.core.impl.shadow;

public abstract class vr extends uT {
   private final vv m;

   public vr(String var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      super(var1, var2, var3, var4, var6, var8, var10);
      this.m = vv.a(var2);
   }

   public vr(String var1, int var2, int var3, double var4, double var6, double[] var8, double[] var9) {
      super(var1, var2, var3, var4, var6, var8, var9);
      this.m = vv.a(var2);
   }

   @Override
   public abstract void a(uF var1, double var2);

   @Override
   public final sy a(double var1, double[] var3, double[][] var4, double[][] var5) {
      double[][] var26 = var5;
      double[][] var25 = var4;
      double[] var2 = var3;
      double var7 = var1;
      vv var23 = this.m;
      var5 = new double[this.m.b.length + 1][var23.b.length + 1];
      double[][] var24 = new double[var23.b.length + 1][var25[0].length];
      double[] var6 = var25[0];
      double[] var9 = var26[0];

      for (int var10 = 1; var10 < var25.length; var10++) {
         double var17;
         double var19 = (var17 = var2[var10] - var2[0]) / var7;
         double var21 = 1.0 / var7;
         double[] var11 = var5[2 * var10 - 2];
         double[] var12 = 2 * var10 - 1 < var5.length ? var5[2 * var10 - 1] : null;

         for (int var13 = 0; var13 < var11.length; var13++) {
            var21 *= var19;
            var11[var13] = var17 * var21;
            if (var12 != null) {
               var12[var13] = (var13 + 2) * var21;
            }
         }

         double[] var30 = var25[var10];
         var11 = var26[var10];
         var12 = var24[2 * var10 - 2];
         double[] var14 = 2 * var10 - 1 < var24.length ? var24[2 * var10 - 1] : null;

         for (int var15 = 0; var15 < var30.length; var15++) {
            var12[var15] = var30[var15] - var6[var15] - var17 * var9[var15];
            if (var14 != null) {
               var14[var15] = var11[var15] - var9[var15];
            }
         }
      }

      tq var31 = new tn(new sy(var5, false)).c().a(new sy(var24, false));
      sy var18 = new sy(var31.f() - 1, var31.g());

      for (int var32 = 0; var32 < var18.f(); var32++) {
         for (int var20 = 0; var20 < var18.g(); var20++) {
            var18.c(var32, var20, var31.b(var32, var20));
         }
      }

      return var18;
   }

   public final sy a(sy var1) {
      sy var2 = var1;
      vv var17 = this.m;
      var2 = var2;
      var1 = this.m.a;
      td.b(this.m.a, var2);
      int var3 = var1.f();
      int var4 = var2.g();
      int var5 = var1.g();
      double[][] var6 = new double[var3][var4];
      double[] var7 = new double[var5];
      double[][] var20 = var2.a;

      for (int var8 = 0; var8 < var4; var8++) {
         for (int var9 = 0; var9 < var5; var9++) {
            var7[var9] = var20[var9][var8];
         }

         for (int var21 = 0; var21 < var3; var21++) {
            double[] var10 = var1.a[var21];
            double var15 = 0.0;

            for (int var11 = 0; var11 < var5; var11++) {
               var15 += var10[var11] * var7[var11];
            }

            var6[var21][var8] = var15;
         }
      }

      return new sy(var6, false);
   }

   public final void a(double[] var1, double[] var2, sy var3) {
      sy var4 = var3;
      double[] var15 = var2;
      var2 = var1;
      vv var13 = this.m;
      double[][] var16 = var4.a;

      for (int var5 = 0; var5 < var16.length; var5++) {
         double[] var6 = var16[var5];
         double var11 = var13.b[var5];

         for (int var7 = 0; var7 < var6.length; var7++) {
            var6[var7] += var11 * (var2[var7] - var15[var7]);
         }
      }
   }
}
