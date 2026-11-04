package org.valkyrienskies.core.impl.shadow;

public abstract class vL extends vx {
   private final boolean f;
   private final double[] g;
   private final double[][] m;
   private final double[] n;
   private final wm o;
   private final double p;
   private double q;
   private double r;
   private double s;

   protected vL(String var1, boolean var2, double[] var3, double[][] var4, double[] var5, wm var6, double var7, double var9, double var11, double var13) {
      super(var1, var7, var9, var11, var13);
      this.f = var2;
      this.g = var3;
      this.m = var4;
      this.n = var5;
      this.o = var6;
      this.p = -1.0 / this.n();
      double var16 = 0.9;
      this.q = 0.9;
      var16 = 0.2;
      this.r = 0.2;
      var16 = 10.0;
      this.s = 10.0;
   }

   protected vL(String var1, boolean var2, double[] var3, double[][] var4, double[] var5, wm var6, double var7, double var9, double[] var11, double[] var12) {
      super(var1, var7, var9, var11, var12);
      this.f = var2;
      this.g = var3;
      this.m = var4;
      this.n = var5;
      this.o = var6;
      this.p = -1.0 / this.n();
      double var14 = 0.9;
      this.q = 0.9;
      var14 = 0.2;
      this.r = 0.2;
      var14 = 10.0;
      this.s = 10.0;
   }

   public abstract int n();

   private double o() {
      return this.q;
   }

   private void a(double var1) {
      this.q = 0.9;
   }

   @Override
   public final void a(uF var1, double var2) {
      this.b(var1, var2);
      this.a(var1);
      boolean var4 = var2 > var1.c;
      double[] var5;
      double[] var6 = (double[])(var5 = var1.g()).clone();
      int var7;
      double[][] var8 = new double[var7 = this.g.length + 1][var6.length];
      double[] var9 = (double[])var5.clone();
      double[] var10 = new double[var6.length];
      wm var11;
      (var11 = (wm)this.o.d()).a(this, var9, var8, var4, var1.b, var1.d());
      var11.a(var1.c);
      super.a = var1.c;
      double var12 = 0.0;
      boolean var14 = true;
      this.a(var1.c, var5, var2);
      super.c = false;

      do {
         var11.c();
         double var15 = 10.0;

         while (var15 >= 1.0) {
            if (var14 || !this.f) {
               this.a(super.a, var6, var8[0]);
            }

            if (var14) {
               double[] var17 = new double[super.l];
               if (super.j == null) {
                  for (int var18 = 0; var18 < var17.length; var18++) {
                     var17[var18] = super.h + super.i * BY.w(var6[var18]);
                  }
               } else {
                  for (int var35 = 0; var35 < var17.length; var35++) {
                     var17[var35] = super.j[var35] + super.k[var35] * BY.w(var6[var35]);
                  }
               }

               var12 = this.a(var4, this.n(), var17, super.a, var6, var8[0], var9, var8[1]);
               var14 = false;
            }

            super.b = var12;
            if (var4) {
               if (super.a + super.b >= var2) {
                  super.b = var2 - super.a;
               }
            } else if (super.a + super.b <= var2) {
               super.b = var2 - super.a;
            }

            for (int var31 = 1; var31 < var7; var31++) {
               for (int var36 = 0; var36 < var5.length; var36++) {
                  double var19 = this.m[var31 - 1][0] * var8[0][var36];

                  for (int var21 = 1; var21 < var31; var21++) {
                     var19 += this.m[var31 - 1][var21] * var8[var21][var36];
                  }

                  var9[var36] = var6[var36] + super.b * var19;
               }

               this.a(super.a + this.g[var31 - 1] * super.b, var9, var8[var31]);
            }

            for (int var32 = 0; var32 < var5.length; var32++) {
               double var37 = this.n[0] * var8[0][var32];

               for (int var20 = 1; var20 < var7; var20++) {
                  var37 += this.n[var20] * var8[var20][var32];
               }

               var9[var32] = var6[var32] + super.b * var37;
            }

            if ((var15 = this.a(var8, var6, var9, super.b)) >= 1.0) {
               double var33 = BY.d(this.s, BY.e(this.r, this.q * BY.a(var15, this.p)));
               var12 = this.a(super.b * var33, var4, false);
            }
         }

         var11.a(super.a + super.b);
         System.arraycopy(var9, 0, var6, 0, var5.length);
         System.arraycopy(var8[var7 - 1], 0, var10, 0, var5.length);
         super.a = this.a(var11, var6, var10, var2);
         System.arraycopy(var6, 0, var9, 0, var6.length);
         if (!super.c) {
            var11.a(super.a);
            if (this.f) {
               System.arraycopy(var10, 0, var8[0], 0, var5.length);
            }

            double var34 = BY.d(this.s, BY.e(this.r, this.q * BY.a(var15, this.p)));
            double var38 = super.b * var34;
            double var39 = super.a + var38;
            boolean var30 = var4 ? var39 >= var2 : var39 <= var2;
            var12 = this.a(var38, var4, var30);
            double var24 = super.a + var12;
            if (var4 ? var24 >= var2 : var24 <= var2) {
               var12 = var2 - super.a;
            }
         }
      } while (!super.c);

      double var28 = super.a;
      var1.c = var28;
      var1.b(var6);
      this.l();
   }

   private double p() {
      return this.r;
   }

   private void b(double var1) {
      this.r = 0.2;
   }

   private double q() {
      return this.s;
   }

   private void c(double var1) {
      this.s = 10.0;
   }

   protected abstract double a(double[][] var1, double[] var2, double[] var3, double var4);
}
