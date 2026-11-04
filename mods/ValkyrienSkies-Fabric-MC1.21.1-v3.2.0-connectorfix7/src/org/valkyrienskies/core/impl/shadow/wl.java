package org.valkyrienskies.core.impl.shadow;

public abstract class wl extends uA {
   private final double[] f;
   private final double[][] g;
   private final double[] h;
   private final wm i;
   private final double j;

   protected wl(String var1, double[] var2, double[][] var3, double[] var4, wm var5, double var6) {
      super(var1);
      this.f = var2;
      this.g = var3;
      this.h = var4;
      this.i = var5;
      this.j = BY.w(var6);
   }

   @Override
   public final void a(uF var1, double var2) {
      this.b(var1, var2);
      this.a(var1);
      boolean var4 = var2 > var1.c;
      double[] var5;
      double[] var6 = (double[])(var5 = var1.g()).clone();
      int var7;
      double[][] var8 = new double[var7 = this.f.length + 1][];

      for (int var9 = 0; var9 < var7; var9++) {
         var8[var9] = new double[var5.length];
      }

      double[] var20 = (double[])var5.clone();
      double[] var10 = new double[var5.length];
      wm var11;
      (var11 = (wm)this.i.d()).a(this, var20, var8, var4, var1.b, var1.d());
      var11.a(var1.c);
      super.a = var1.c;
      if (var4) {
         if (super.a + this.j >= var2) {
            super.b = var2 - super.a;
         } else {
            super.b = this.j;
         }
      } else if (super.a - this.j <= var2) {
         super.b = var2 - super.a;
      } else {
         super.b = -this.j;
      }

      this.a(var1.c, var5, var2);
      super.c = false;

      do {
         var11.c();
         this.a(super.a, var6, var8[0]);

         for (int var12 = 1; var12 < var7; var12++) {
            for (int var13 = 0; var13 < var5.length; var13++) {
               double var14 = this.g[var12 - 1][0] * var8[0][var13];

               for (int var16 = 1; var16 < var12; var16++) {
                  var14 += this.g[var12 - 1][var16] * var8[var16][var13];
               }

               var20[var13] = var6[var13] + super.b * var14;
            }

            this.a(super.a + this.f[var12 - 1] * super.b, var20, var8[var12]);
         }

         for (int var21 = 0; var21 < var5.length; var21++) {
            double var23 = this.h[0] * var8[0][var21];

            for (int var15 = 1; var15 < var7; var15++) {
               var23 += this.h[var15] * var8[var15][var21];
            }

            var20[var21] = var6[var21] + super.b * var23;
         }

         var11.a(super.a + super.b);
         System.arraycopy(var20, 0, var6, 0, var5.length);
         System.arraycopy(var8[var7 - 1], 0, var10, 0, var5.length);
         super.a = this.a(var11, var6, var10, var2);
         if (!super.c) {
            var11.a(super.a);
            double var22 = super.a + super.b;
            if (var4 ? var22 >= var2 : var22 <= var2) {
               super.b = var2 - super.a;
            }
         }
      } while (!super.c);

      double var18 = super.a;
      var1.c = var18;
      var1.b(var6);
      super.a = Double.NaN;
      super.b = Double.NaN;
   }

   private double[] a(uM var1, double var2, double[] var4, double var5) {
      double[] var7 = (double[])var4.clone();
      int var8;
      double[][] var9 = new double[var8 = this.f.length + 1][];

      for (int var10 = 0; var10 < var8; var10++) {
         var9[var10] = new double[var4.length];
      }

      double[] var19 = (double[])var4.clone();
      double var11 = var5 - var2;
      var1.a(var2, var7, var9[0]);

      for (int var17 = 1; var17 < var8; var17++) {
         for (int var14 = 0; var14 < var4.length; var14++) {
            double var15 = this.g[var17 - 1][0] * var9[0][var14];

            for (int var6 = 1; var6 < var17; var6++) {
               var15 += this.g[var17 - 1][var6] * var9[var6][var14];
            }

            var19[var14] = var7[var14] + var11 * var15;
         }

         var1.a(var2 + this.f[var17 - 1] * var11, var19, var9[var17]);
      }

      for (int var18 = 0; var18 < var4.length; var18++) {
         double var20 = this.h[0] * var9[0][var18];

         for (int var16 = 1; var16 < var8; var16++) {
            var20 += this.h[var16] * var9[var16][var18];
         }

         var7[var18] += var11 * var20;
      }

      return var7;
   }
}
