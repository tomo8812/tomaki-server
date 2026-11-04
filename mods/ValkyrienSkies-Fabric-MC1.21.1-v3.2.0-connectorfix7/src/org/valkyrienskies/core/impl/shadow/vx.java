package org.valkyrienskies.core.impl.shadow;

public abstract class vx extends uA {
   protected double h;
   protected double i;
   protected double[] j;
   protected double[] k;
   protected int l;
   private double f;
   private double g;
   private double m;

   public vx(String var1, double var2, double var4, double var6, double var8) {
      super(var1);
      double var17 = var8;
      double var15 = var6;
      double var13 = var4;
      double var11 = var2;
      vx var19 = this;
      this.g = BY.w(var11);
      var19.m = BY.w(var13);
      var19.f = -1.0;
      var19.h = var15;
      var19.i = var17;
      var19.j = null;
      var19.k = null;
      this.l();
   }

   public vx(String var1, double var2, double var4, double[] var6, double[] var7) {
      super(var1);
      double[] var3 = var7;
      double[] var14 = var6;
      double var11 = var4;
      double var9 = var2;
      vx var13 = this;
      this.g = BY.w(var9);
      var13.m = BY.w(var11);
      var13.f = -1.0;
      var13.h = 0.0;
      var13.i = 0.0;
      var13.j = (double[])var14.clone();
      var13.k = (double[])var3.clone();
      this.l();
   }

   private void a(double var1, double var3, double var5, double var7) {
      this.g = BY.w(var1);
      this.m = BY.w(var3);
      this.f = -1.0;
      this.h = var5;
      this.i = var7;
      this.j = null;
      this.k = null;
   }

   private void a(double var1, double var3, double[] var5, double[] var6) {
      this.g = BY.w(var1);
      this.m = BY.w(var3);
      this.f = -1.0;
      this.h = 0.0;
      this.i = 0.0;
      this.j = (double[])var5.clone();
      this.k = (double[])var6.clone();
   }

   private void a(double var1) {
      if (!(var1 < this.g) && !(var1 > this.m)) {
         this.f = var1;
      } else {
         this.f = -1.0;
      }
   }

   @Override
   public final void b(uF var1, double var2) {
      super.b(var1, var2);
      this.l = var1.b.b;
      if (this.j != null && this.j.length != this.l) {
         throw new oS(this.l, this.j.length);
      }

      if (this.k != null && this.k.length != this.l) {
         throw new oS(this.l, this.k.length);
      }
   }

   public final double a(boolean var1, int var2, double[] var3, double var4, double[] var6, double[] var7, double[] var8, double[] var9) {
      if (this.f > 0.0) {
         return var1 ? this.f : -this.f;
      }

      double var12 = 0.0;
      double var14 = 0.0;

      for (int var16 = 0; var16 < var3.length; var16++) {
         double var10 = var6[var16] / var3[var16];
         var12 += var10 * var10;
         var10 = var7[var16] / var3[var16];
         var14 += var10 * var10;
      }

      double var26 = !(var12 < 1.0E-10) && !(var14 < 1.0E-10) ? 0.01 * BY.a(var12 / var14) : 1.0E-6;
      if (!var1) {
         var26 = -var26;
      }

      for (int var18 = 0; var18 < var6.length; var18++) {
         var8[var18] = var6[var18] + var26 * var7[var18];
      }

      this.a(var4 + var26, var8, var9);
      double var28 = 0.0;

      for (int var20 = 0; var20 < var3.length; var20++) {
         double var25 = (var9[var20] - var7[var20]) / var3[var20];
         var28 += var25 * var25;
      }

      var28 = BY.a(var28) / var26;
      double var30;
      double var22 = (var30 = BY.e(BY.a(var14), var28)) < 1.0E-15 ? BY.e(1.0E-6, 0.001 * BY.w(var26)) : BY.a(0.01 / var30, 1.0 / var2);
      if ((var26 = BY.e(BY.d(100.0 * BY.w(var26), var22), 1.0E-12 * BY.w(var4))) < this.g) {
         var26 = this.g;
      }

      if (var26 > this.m) {
         var26 = this.m;
      }

      if (!var1) {
         var26 = -var26;
      }

      return var26;
   }

   protected final double a(double var1, boolean var3, boolean var4) {
      double var5 = var1;
      if (BY.w(var1) < this.g) {
         if (!var4) {
            throw new pn(px.MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION, BY.w(var1), this.g, true);
         }

         var5 = var3 ? this.g : -this.g;
      }

      if (var5 > this.m) {
         var5 = this.m;
      } else if (var5 < -this.m) {
         var5 = -this.m;
      }

      return var5;
   }

   @Override
   public abstract void a(uF var1, double var2);

   @Override
   public final double f() {
      return super.a;
   }

   protected final void l() {
      super.a = Double.NaN;
      super.b = BY.a(this.g * this.m);
   }

   private double n() {
      return this.g;
   }

   public final double m() {
      return this.m;
   }
}
