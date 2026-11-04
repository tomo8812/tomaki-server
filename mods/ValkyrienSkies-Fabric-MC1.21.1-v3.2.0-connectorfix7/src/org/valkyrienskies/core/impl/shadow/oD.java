package org.valkyrienskies.core.impl.shadow;

public final class oD extends oe {
   private static double b;
   private static final long c = 20141003L;
   private final double d;
   private final double e;
   private final double f;

   private oD(double var1, double var3) {
      this(var1, var3, 1.0E-9);
   }

   private oD(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   private oD(zR var1, double var2, double var4, double var6) {
      super(var1);
      if (var2 < 0.5) {
         throw new pn(var2, 0.5, true);
      }

      if (var4 <= 0.0) {
         throw new pk(px.NOT_POSITIVE_SCALE, var4);
      }

      this.d = var2;
      this.e = var4;
      this.f = var6;
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
   public final double d(double var1) {
      return var1 <= 0.0
         ? 0.0
         : 2.0 * BY.a(this.d, this.d) / (zY.d(this.d) * BY.a(this.e, this.d)) * BY.a(var1, 2.0 * this.d - 1.0) * BY.i(-this.d * var1 * var1 / this.e);
   }

   @Override
   public final double e(double var1) {
      return zY.a(this.d, this.d * var1 * var1 / this.e);
   }

   @Override
   public final double c() {
      return zY.d(this.d + 0.5) / zY.d(this.d) * BY.a(this.e / this.d);
   }

   @Override
   public final double d() {
      double var1 = zY.d(this.d + 0.5) / zY.d(this.d);
      return this.e * (1.0 - 1.0 / this.d * var1 * var1);
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
