package org.valkyrienskies.core.impl.shadow;

public final class oO extends oe {
   private static double b;
   private static final long c = 8589540077390120676L;
   private final double d;
   private final double e;
   private final double f;
   private double g = Double.NaN;
   private boolean h = false;
   private double i = Double.NaN;
   private boolean j = false;

   private oO(double var1, double var3) {
      this(var1, var3, 1.0E-9);
   }

   private oO(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   private oO(zF var1, double var2, double var4) {
      this(var1, var2, var4, 1.0E-9);
   }

   public oO(zF var1, double var2, double var4, double var6) {
      super(var1);
      if (var2 <= 0.0) {
         throw new pk(px.SHAPE, var2);
      }

      if (var4 <= 0.0) {
         throw new pk(px.SCALE, var4);
      }

      this.e = var4;
      this.d = var2;
      this.f = var6;
   }

   private double j() {
      return this.d;
   }

   private double k() {
      return this.e;
   }

   @Override
   public final double d(double var1) {
      if (var1 < 0.0) {
         return 0.0;
      }

      double var3;
      double var5;
      double var7 = (var5 = BY.a(var3 = var1 / this.e, this.d - 1.0)) * var3;
      return this.d / this.e * var5 * BY.i(-var7);
   }

   @Override
   public final double c(double var1) {
      if (var1 < 0.0) {
         return Double.NEGATIVE_INFINITY;
      }

      double var3;
      double var5;
      double var7 = BY.i(var5 = BY.k(var3 = var1 / this.e) * (this.d - 1.0)) * var3;
      return BY.k(this.d / this.e) + var5 - var7;
   }

   @Override
   public final double e(double var1) {
      double var3;
      if (var1 <= 0.0) {
         var3 = 0.0;
      } else {
         var3 = 1.0 - BY.i(-BY.a(var1 / this.e, this.d));
      }

      return var3;
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         double var3;
         if (var1 == 0.0) {
            var3 = 0.0;
         } else if (var1 == 1.0) {
            var3 = Double.POSITIVE_INFINITY;
         } else {
            var3 = this.e * BY.a(-BY.l(-var1), 1.0 / this.d);
         }

         return var3;
      } else {
         throw new po(var1, 0.0, 1.0);
      }
   }

   @Override
   protected final double a() {
      return this.f;
   }

   @Override
   public final double c() {
      if (!this.h) {
         oO var1 = this;
         double var2 = this.d;
         double var4;
         this.g = (var4 = var1.e) * BY.i(zY.a(1.0 + 1.0 / var2));
         this.h = true;
      }

      return this.g;
   }

   private double l() {
      double var1 = this.d;
      double var3;
      return (var3 = this.e) * BY.i(zY.a(1.0 + 1.0 / var1));
   }

   @Override
   public final double d() {
      if (!this.j) {
         oO var1 = this;
         double var2 = this.d;
         double var4 = var1.e;
         double var6 = var1.c();
         this.i = var4 * var4 * BY.i(zY.a(1.0 + 2.0 / var2)) - var6 * var6;
         this.j = true;
      }

      return this.i;
   }

   private double m() {
      double var1 = this.d;
      double var3 = this.e;
      double var5 = this.c();
      return var3 * var3 * BY.i(zY.a(1.0 + 2.0 / var1)) - var5 * var5;
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
}
