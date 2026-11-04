package org.valkyrienskies.core.impl.shadow;

public final class ov extends oe {
   private static final long b = 20141003L;
   private final double c;
   private final double d;

   private ov(double var1, double var3) {
      this(new zR(), var1, var3);
   }

   private ov(zR var1, double var2, double var4) {
      super(var1);
      if (var4 <= 0.0) {
         throw new pk(px.NOT_POSITIVE_SCALE, var4);
      }

      this.c = var2;
      this.d = var4;
   }

   private double j() {
      return this.c;
   }

   private double k() {
      return this.d;
   }

   @Override
   public final double d(double var1) {
      return BY.i(-BY.w(var1 - this.c) / this.d) / (2.0 * this.d);
   }

   @Override
   public final double e(double var1) {
      return var1 <= this.c ? BY.i((var1 - this.c) / this.d) / 2.0 : 1.0 - BY.i((this.c - var1) / this.d) / 2.0;
   }

   @Override
   public final double a(double var1) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0.0, 1.0);
      }

      if (var1 == 0.0) {
         return Double.NEGATIVE_INFINITY;
      }

      if (var1 == 1.0) {
         return Double.POSITIVE_INFINITY;
      }

      double var3 = var1 > 0.5 ? -Math.log(2.0 - var1 * 2.0) : Math.log(var1 * 2.0);
      return this.c + this.d * var3;
   }

   @Override
   public final double c() {
      return this.c;
   }

   @Override
   public final double d() {
      return 2.0 * this.d * this.d;
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
