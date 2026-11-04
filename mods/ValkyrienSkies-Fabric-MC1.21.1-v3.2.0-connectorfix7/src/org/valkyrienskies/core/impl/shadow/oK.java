package org.valkyrienskies.core.impl.shadow;

public final class oK extends oe {
   private static double b;
   private static final long c = -5852615386664158222L;
   private final double d;
   private final double e;
   private final double f;

   public oK(double var1) {
      this(var1, 1.0E-9);
   }

   private oK(double var1, double var3) {
      this(new zR(), var1, 1.0E-9);
   }

   public oK(zF var1, double var2) {
      this(null, var2, 1.0E-9);
   }

   public oK(zF var1, double var2, double var4) {
      super(var1);
      if (var2 <= 0.0) {
         throw new pk(px.DEGREES_OF_FREEDOM, var2);
      }

      this.d = var2;
      this.e = var4;
      double var6 = var2;
      double var8 = (var2 + 1.0) / 2.0;
      this.f = zY.a(var8) - 0.5 * (BY.k(Math.PI) + BY.k(var6)) - zY.a(var6 / 2.0);
   }

   private double j() {
      return this.d;
   }

   @Override
   public final double d(double var1) {
      return BY.i(this.c(var1));
   }

   @Override
   public final double c(double var1) {
      double var3 = this.d;
      double var5 = (this.d + 1.0) / 2.0;
      return this.f - var5 * BY.k(1.0 + var1 * var1 / var3);
   }

   @Override
   public final double e(double var1) {
      double var3;
      if (var1 == 0.0) {
         var3 = 0.5;
      } else {
         double var5 = zW.a(this.d / (this.d + var1 * var1), 0.5 * this.d, 0.5);
         if (var1 < 0.0) {
            var3 = var5 * 0.5;
         } else {
            var3 = 1.0 - var5 * 0.5;
         }
      }

      return var3;
   }

   @Override
   protected final double a() {
      return this.e;
   }

   @Override
   public final double c() {
      double var1 = this.d;
      return this.d > 1.0 ? 0.0 : Double.NaN;
   }

   @Override
   public final double d() {
      double var1 = this.d;
      if (this.d > 2.0) {
         return var1 / (var1 - 2.0);
      } else {
         return var1 > 1.0 && var1 <= 2.0 ? Double.POSITIVE_INFINITY : Double.NaN;
      }
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
}
