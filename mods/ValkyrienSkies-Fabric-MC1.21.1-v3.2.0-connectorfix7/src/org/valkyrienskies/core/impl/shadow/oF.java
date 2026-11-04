package org.valkyrienskies.core.impl.shadow;

public final class oF extends oe {
   private static double b;
   private static final long c = 20130424L;
   private final double d;
   private final double e;
   private final double f;

   public oF() {
      this(1.0, 1.0);
   }

   private oF(double var1, double var3) {
      this(1.0, 1.0, 1.0E-9);
   }

   private oF(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   private oF(zF var1, double var2, double var4) {
      this(var1, var2, var4, 1.0E-9);
   }

   private oF(zF var1, double var2, double var4, double var6) {
      super(var1);
      if (var2 <= 0.0) {
         throw new pk(px.SCALE, var2);
      }

      if (var4 <= 0.0) {
         throw new pk(px.SHAPE, var4);
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
   public final double d(double var1) {
      return var1 < this.d ? 0.0 : BY.a(this.d, this.e) / BY.a(var1, this.e + 1.0) * this.e;
   }

   @Override
   public final double c(double var1) {
      return var1 < this.d ? Double.NEGATIVE_INFINITY : BY.k(this.d) * this.e - BY.k(var1) * (this.e + 1.0) + BY.k(this.e);
   }

   @Override
   public final double e(double var1) {
      return var1 <= this.d ? 0.0 : 1.0 - BY.a(this.d / var1, this.e);
   }

   @Deprecated
   @Override
   public final double a(double var1, double var3) {
      return this.b(var1, var3);
   }

   @Override
   protected final double a() {
      return this.f;
   }

   @Override
   public final double c() {
      return this.e <= 1.0 ? Double.POSITIVE_INFINITY : this.e * this.d / (this.e - 1.0);
   }

   @Override
   public final double d() {
      if (this.e <= 2.0) {
         return Double.POSITIVE_INFINITY;
      }

      double var1 = this.e - 1.0;
      return this.d * this.d * this.e / (var1 * var1) / (this.e - 2.0);
   }

   @Override
   public final double e() {
      return this.d;
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
      double var1 = super.a.nextDouble();
      return this.d / BY.a(var1, 1.0 / this.e);
   }
}
