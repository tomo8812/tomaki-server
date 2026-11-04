package org.valkyrienskies.core.impl.shadow;

public final class oo extends oe {
   private static double b;
   private static final long c = -8516354193418641566L;
   private final double d;
   private final double e;
   private final double f;
   private double g = Double.NaN;
   private boolean h = false;

   public oo(double var1, double var3) {
      this(var1, var3, 1.0E-9);
   }

   private oo(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   public oo(zF var1, double var2, double var4) {
      this(null, var2, var4, 1.0E-9);
   }

   public oo(zF var1, double var2, double var4, double var6) {
      super(var1);
      if (var2 <= 0.0) {
         throw new pk(px.DEGREES_OF_FREEDOM, var2);
      }

      if (var4 <= 0.0) {
         throw new pk(px.DEGREES_OF_FREEDOM, var4);
      }

      this.d = var2;
      this.e = var4;
      this.f = var6;
   }

   @Override
   public final double d(double var1) {
      return BY.i(this.c(var1));
   }

   @Override
   public final double c(double var1) {
      double var3 = this.d / 2.0;
      double var5 = this.e / 2.0;
      double var7 = BY.k(var1);
      double var9 = BY.k(this.d);
      double var11 = BY.k(this.e);
      double var13 = BY.k(this.d * var1 + this.e);
      return var3 * var9 + var3 * var7 - var7 + var5 * var11 - var3 * var13 - var5 * var13 - zW.a(var3, var5);
   }

   @Override
   public final double e(double var1) {
      double var3;
      if (var1 <= 0.0) {
         var3 = 0.0;
      } else {
         double var5 = this.d;
         double var7 = this.e;
         var3 = zW.a(var5 * var1 / (var7 + var5 * var1), var5 * 0.5, var7 * 0.5);
      }

      return var3;
   }

   private double j() {
      return this.d;
   }

   private double k() {
      return this.e;
   }

   @Override
   protected final double a() {
      return this.f;
   }

   @Override
   public final double c() {
      double var1 = this.e;
      return this.e > 2.0 ? var1 / (var1 - 2.0) : Double.NaN;
   }

   @Override
   public final double d() {
      if (!this.h) {
         oo var1 = this;
         double var2 = this.e;
         double var10001;
         if (this.e > 4.0) {
            double var4 = var1.d;
            double var6 = var2 - 2.0;
            var10001 = 2.0 * (var2 * var2) * (var4 + var2 - 2.0) / (var4 * (var6 * var6) * (var2 - 4.0));
         } else {
            var10001 = Double.NaN;
         }

         this.g = var10001;
         this.h = true;
      }

      return this.g;
   }

   private double l() {
      double var1 = this.e;
      if (this.e > 4.0) {
         double var3 = this.d;
         double var5 = var1 - 2.0;
         return 2.0 * (var1 * var1) * (var3 + var1 - 2.0) / (var3 * (var5 * var5) * (var1 - 4.0));
      } else {
         return Double.NaN;
      }
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
}
