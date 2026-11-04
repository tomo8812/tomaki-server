package org.valkyrienskies.core.impl.shadow;

public final class ow extends oe {
   private static final long b = 20130314L;
   private final double c;
   private final double d;
   private final double e;

   private ow(double var1, double var3) {
      this(new zR(), var1, var3);
   }

   private ow(zR var1, double var2, double var4) {
      super(var1);
      this.c = var2;
      this.d = var4;
      this.e = var4 * 0.5;
   }

   @Override
   public final double d(double var1) {
      if (var1 < this.c) {
         return Double.NaN;
      }

      double var3 = var1 - this.c;
      double var5;
      return BY.a((var5 = this.e / var3) / Math.PI) * BY.i(-var5) / var3;
   }

   @Override
   public final double c(double var1) {
      if (var1 < this.c) {
         return Double.NaN;
      }

      double var3 = var1 - this.c;
      double var5 = this.e / var3;
      return 0.5 * BY.k(var5 / Math.PI) - var5 - BY.k(var3);
   }

   @Override
   public final double e(double var1) {
      return var1 < this.c ? Double.NaN : zX.b(BY.a(this.e / (var1 - this.c)));
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         double var5 = var1;
         double var3 = zX.c(1.0 - var5);
         return this.c + this.e / (var3 * var3);
      } else {
         throw new po(var1, 0, 1);
      }
   }

   private double j() {
      return this.d;
   }

   private double k() {
      return this.c;
   }

   @Override
   public final double c() {
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public final double d() {
      return Double.POSITIVE_INFINITY;
   }

   @Override
   public final double e() {
      return this.c;
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
