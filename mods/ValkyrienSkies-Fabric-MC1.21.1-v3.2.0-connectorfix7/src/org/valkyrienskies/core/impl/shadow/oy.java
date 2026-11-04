package org.valkyrienskies.core.impl.shadow;

public final class oy extends oe {
   private static final long b = 20141003L;
   private final double c;
   private final double d;

   private oy(double var1, double var3) {
      this(new zR(), var1, var3);
   }

   private oy(zR var1, double var2, double var4) {
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
      double var5 = BY.i(-((var1 - this.c) / this.d));
      return 1.0 / this.d * var5 / ((var5 + 1.0) * (var5 + 1.0));
   }

   @Override
   public final double e(double var1) {
      double var3 = 1.0 / this.d * (var1 - this.c);
      return 1.0 / (1.0 + BY.i(-var3));
   }

   @Override
   public final double a(double var1) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0.0, 1.0);
      } else if (var1 == 0.0) {
         return 0.0;
      } else {
         return var1 == 1.0 ? Double.POSITIVE_INFINITY : this.d * Math.log(var1 / (1.0 - var1)) + this.c;
      }
   }

   @Override
   public final double c() {
      return this.c;
   }

   @Override
   public final double d() {
      return 3.289868133696453 * (1.0 / (this.d * this.d));
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
