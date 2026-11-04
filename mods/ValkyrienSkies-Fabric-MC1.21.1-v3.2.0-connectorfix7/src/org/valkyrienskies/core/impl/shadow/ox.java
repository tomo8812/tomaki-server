package org.valkyrienskies.core.impl.shadow;

public final class ox extends oe {
   private static double b;
   private static final long c = 20120112L;
   private static final double d = BY.a(Math.PI * 2);
   private static final double e = BY.a(2.0);
   private final double f;
   private final double g;
   private final double h;
   private final double i;

   public ox() {
      this(0.0, 1.0);
   }

   private ox(double var1, double var3) {
      this(0.0, 1.0, 1.0E-9);
   }

   private ox(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   private ox(zF var1, double var2, double var4) {
      this(var1, var2, var4, 1.0E-9);
   }

   private ox(zF var1, double var2, double var4, double var6) {
      super(var1);
      if (var4 <= 0.0) {
         throw new pk(px.SHAPE, var4);
      }

      this.f = var2;
      this.g = var4;
      this.h = BY.k(var4) + 0.5 * BY.k(Math.PI * 2);
      this.i = var6;
   }

   private double j() {
      return this.f;
   }

   private double k() {
      return this.g;
   }

   @Override
   public final double d(double var1) {
      if (var1 <= 0.0) {
         return 0.0;
      }

      double var5 = (BY.k(var1) - this.f) / this.g;
      return BY.i(var5 * -0.5 * var5) / (this.g * d * var1);
   }

   @Override
   public final double c(double var1) {
      if (var1 <= 0.0) {
         return Double.NEGATIVE_INFINITY;
      }

      double var3;
      double var7 = ((var3 = BY.k(var1)) - this.f) / this.g;
      return var7 * -0.5 * var7 - (this.h + var3);
   }

   @Override
   public final double e(double var1) {
      if (var1 <= 0.0) {
         return 0.0;
      } else {
         double var3;
         if (BY.w(var3 = BY.k(var1) - this.f) > 40.0 * this.g) {
            return var3 < 0.0 ? 0.0 : 1.0;
         } else {
            return 0.5 + 0.5 * zX.a(var3 / (this.g * e));
         }
      }
   }

   @Deprecated
   @Override
   public final double a(double var1, double var3) {
      return this.b(var1, var3);
   }

   @Override
   public final double b(double var1, double var3) {
      if (var1 > var3) {
         throw new pm(px.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, var1, var3, true);
      } else if (!(var1 <= 0.0) && !(var3 <= 0.0)) {
         double var5 = this.g * e;
         double var7 = (BY.k(var1) - this.f) / var5;
         double var9 = (BY.k(var3) - this.f) / var5;
         return 0.5 * zX.a(var7, var9);
      } else {
         return super.b(var1, var3);
      }
   }

   @Override
   protected final double a() {
      return this.i;
   }

   @Override
   public final double c() {
      double var1 = this.g;
      return BY.i(this.f + var1 * var1 / 2.0);
   }

   @Override
   public final double d() {
      double var1 = this.g;
      double var3;
      return BY.j(var3 = this.g * var1) * BY.i(2.0 * this.f + var3);
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
      double var1 = super.a.nextGaussian();
      return BY.i(this.f + this.g * var1);
   }
}
