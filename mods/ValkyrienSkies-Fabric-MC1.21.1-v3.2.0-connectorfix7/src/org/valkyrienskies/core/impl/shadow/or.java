package org.valkyrienskies.core.impl.shadow;

public final class or extends oe {
   private static final long b = 20141003L;
   private static final double c = 0.5778636748954609;
   private final double d;
   private final double e;

   private or(double var1, double var3) {
      this(new zR(), var1, var3);
   }

   private or(zR var1, double var2, double var4) {
      super(var1);
      if (var4 <= 0.0) {
         throw new pk(px.SCALE, var4);
      }

      this.e = var4;
      this.d = var2;
   }

   private double j() {
      return this.d;
   }

   private double k() {
      return this.e;
   }

   @Override
   public final double d(double var1) {
      double var3;
      double var5 = BY.i(-(var3 = (var1 - this.d) / this.e));
      return BY.i(-var3 - var5) / this.e;
   }

   @Override
   public final double e(double var1) {
      return BY.i(-BY.i(-((var1 - this.d) / this.e)));
   }

   @Override
   public final double a(double var1) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0.0, 1.0);
      } else if (var1 == 0.0) {
         return Double.NEGATIVE_INFINITY;
      } else {
         return var1 == 1.0 ? Double.POSITIVE_INFINITY : this.d - BY.k(-BY.k(var1)) * this.e;
      }
   }

   @Override
   public final double c() {
      return this.d + 0.5778636748954609 * this.e;
   }

   @Override
   public final double d() {
      return 1.6449340668482264 * (this.e * this.e);
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
