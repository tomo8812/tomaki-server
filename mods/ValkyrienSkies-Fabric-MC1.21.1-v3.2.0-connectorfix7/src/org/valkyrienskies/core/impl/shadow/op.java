package org.valkyrienskies.core.impl.shadow;

public final class op extends oe {
   private static double c;
   private static final long d = 20120524L;
   final double b;
   private final double e;
   private final double f;
   private final double g;
   private final double h;
   private final double i;
   private final double j;
   private final double k;
   private final double l;
   private final double m;

   public op(double var1, double var3) {
      this(var1, 2.0, 1.0E-9);
   }

   private op(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   private op(zF var1, double var2, double var4) {
      this(var1, var2, var4, 1.0E-9);
   }

   public op(zF var1, double var2, double var4, double var6) {
      super(var1);
      if (var2 <= 0.0) {
         throw new pk(px.SHAPE, var2);
      }

      if (var4 <= 0.0) {
         throw new pk(px.SCALE, var4);
      }

      this.b = var2;
      this.e = var4;
      this.m = var6;
      this.f = var2 + 4.7421875 + 0.5;
      double var8 = Math.E / ((Math.PI * 2) * this.f);
      this.i = var2 * BY.a(var8) / zY.b(var2);
      this.j = BY.k(var2) + 0.5 * BY.k(var8) - BY.k(zY.b(var2));
      this.g = this.i / var4 * BY.a(this.f, -var2) * BY.i(var2 + 4.7421875);
      this.h = this.j - BY.k(var4) - BY.k(this.f) * var2 + var2 + 4.7421875;
      this.k = var2 + 4.7421875 - BY.k(Double.MAX_VALUE);
      this.l = BY.k(Double.MAX_VALUE) / (var2 - 1.0);
   }

   @Deprecated
   private double k() {
      return this.b;
   }

   public final double j() {
      return this.b;
   }

   @Deprecated
   private double l() {
      return this.e;
   }

   private double m() {
      return this.e;
   }

   @Override
   public final double d(double var1) {
      if (var1 < 0.0) {
         return 0.0;
      }

      double var3;
      if (!((var3 = var1 / this.e) <= this.k) && !(BY.k(var3) >= this.l)) {
         return this.g * BY.i(-var3) * BY.a(var3, this.b - 1.0);
      }

      double var5 = (var3 - this.f) / this.f;
      double var7 = this.b * (BY.l(var5) - var5);
      double var9 = -var3 * 5.2421875 / this.f + 4.7421875 + var7;
      return this.i / var1 * BY.i(var9);
   }

   @Override
   public final double c(double var1) {
      if (var1 < 0.0) {
         return Double.NEGATIVE_INFINITY;
      }

      double var3;
      if (!((var3 = var1 / this.e) <= this.k) && !(BY.k(var3) >= this.l)) {
         return this.h - var3 + BY.k(var3) * (this.b - 1.0);
      }

      double var5 = (var3 - this.f) / this.f;
      double var7 = this.b * (BY.l(var5) - var5);
      double var9 = -var3 * 5.2421875 / this.f + 4.7421875 + var7;
      return this.j - BY.k(var1) + var9;
   }

   @Override
   public final double e(double var1) {
      double var3;
      if (var1 <= 0.0) {
         var3 = 0.0;
      } else {
         var3 = zY.a(this.b, var1 / this.e);
      }

      return var3;
   }

   @Override
   protected final double a() {
      return this.m;
   }

   @Override
   public final double c() {
      return this.b * this.e;
   }

   @Override
   public final double d() {
      return this.b * this.e * this.e;
   }

   @Override
   public final double e() {
      return 0.0;
   }

   @Override
   public final double f() {
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public final boolean g() {
      return true;
   }

   @Override
   public final boolean h() {
      return false;
   }

   @Override
   public final boolean i() {
      return true;
   }

   @Override
   public final double b() {
      if (this.b < 1.0) {
         while (true) {
            double var13 = super.a.nextDouble();
            double var14;
            double var15;
            if ((var15 = (var14 = 1.0 + this.b / Math.E) * var13) <= 1.0) {
               double var17 = BY.a(var15, 1.0 / this.b);
               if (!(super.a.nextDouble() > BY.i(-var17))) {
                  return this.e * var17;
               }
            } else {
               double var16 = -1.0 * BY.k((var14 - var15) / this.b);
               if (!(super.a.nextDouble() > BY.a(var16, this.b - 1.0))) {
                  return this.e * var16;
               }
            }
         }
      } else {
         double var1 = this.b - 0.3333333333333333;
         double var3 = 1.0 / (3.0 * BY.a(var1));

         while (true) {
            double var5 = super.a.nextGaussian();
            double var7;
            if (!((var7 = (1.0 + var3 * var5) * (1.0 + var3 * var5) * (1.0 + var3 * var5)) <= 0.0)) {
               double var9 = var5 * var5;
               double var11;
               if ((var11 = super.a.nextDouble()) < 1.0 - var9 * 0.0331 * var9) {
                  return this.e * var1 * var7;
               }

               if (BY.k(var11) < var9 * 0.5 + var1 * (1.0 - var7 + BY.k(var7))) {
                  return this.e * var1 * var7;
               }
            }
         }
      }
   }
}
