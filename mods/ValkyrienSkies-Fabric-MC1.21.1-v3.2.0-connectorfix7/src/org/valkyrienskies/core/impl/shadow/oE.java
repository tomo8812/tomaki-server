package org.valkyrienskies.core.impl.shadow;

public final class oE extends oe {
   private static double b;
   private static final long c = 8589540077390120676L;
   private static final double d = BY.a(2.0);
   private final double e;
   private final double f;
   private final double g;
   private final double h;

   public oE() {
      this(0.0, 1.0);
   }

   private oE(double var1, double var3) {
      this(0.0, 1.0, 1.0E-9);
   }

   private oE(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   public oE(zF var1, double var2, double var4) {
      this(null, 0.0, 1.0, 1.0E-9);
   }

   public oE(zF var1, double var2, double var4, double var6) {
      super(var1);
      if (var4 <= 0.0) {
         throw new pk(px.STANDARD_DEVIATION, var4);
      }

      this.e = var2;
      this.f = var4;
      this.g = BY.k(var4) + 0.5 * BY.k(Math.PI * 2);
      this.h = var6;
   }

   private double j() {
      return this.e;
   }

   private double k() {
      return this.f;
   }

   @Override
   public final double d(double var1) {
      return BY.i(this.c(var1));
   }

   @Override
   public final double c(double var1) {
      double var3;
      double var5 = (var3 = var1 - this.e) / this.f;
      return var5 * -0.5 * var5 - this.g;
   }

   @Override
   public final double e(double var1) {
      double var3;
      if (BY.w(var3 = var1 - this.e) > 40.0 * this.f) {
         return var3 < 0.0 ? 0.0 : 1.0;
      } else {
         return 0.5 * zX.b(-var3 / (this.f * d));
      }
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         return this.e + this.f * d * zX.c(var1 * 2.0 - 1.0);
      } else {
         throw new po(var1, 0, 1);
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
      }

      double var5 = this.f * d;
      double var7 = (var1 - this.e) / var5;
      double var9 = (var3 - this.e) / var5;
      return 0.5 * zX.a(var7, var9);
   }

   @Override
   protected final double a() {
      return this.h;
   }

   @Override
   public final double c() {
      return this.e;
   }

   @Override
   public final double d() {
      double var1 = this.f;
      return this.f * var1;
   }

   @Override
   public final double e() {
      return Double.NEGATIVE_INFINITY;
   }

   @Override
   public final double f() {
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public final boolean g() {
      return false;
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
      return this.f * super.a.nextGaussian() + this.e;
   }
}
