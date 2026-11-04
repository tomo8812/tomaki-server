package org.valkyrienskies.core.impl.shadow;

public final class oi extends oe {
   private static double b;
   private static final long c = -8352658048349159782L;
   private final op d;
   private final double e;

   public oi(double var1) {
      this(var1, 1.0E-9);
   }

   private oi(double var1, double var3) {
      this(new zR(), var1, 1.0E-9);
   }

   public oi(zF var1, double var2) {
      this(null, var2, 1.0E-9);
   }

   public oi(zF var1, double var2, double var4) {
      super(var1);
      this.d = new op(var2 / 2.0, 2.0);
      this.e = var4;
   }

   private double j() {
      op var1 = this.d;
      return this.d.b * 2.0;
   }

   @Override
   public final double d(double var1) {
      return this.d.d(var1);
   }

   @Override
   public final double c(double var1) {
      return this.d.c(var1);
   }

   @Override
   public final double e(double var1) {
      return this.d.e(var1);
   }

   @Override
   protected final double a() {
      return this.e;
   }

   @Override
   public final double c() {
      return this.j();
   }

   @Override
   public final double d() {
      return 2.0 * this.j();
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
