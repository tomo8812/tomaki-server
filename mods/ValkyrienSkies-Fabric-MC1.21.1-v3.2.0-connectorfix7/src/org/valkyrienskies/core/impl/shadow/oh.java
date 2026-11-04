package org.valkyrienskies.core.impl.shadow;

public final class oh extends oe {
   private static double b;
   private static final long c = 8589540077390120676L;
   private final double d;
   private final double e;
   private final double f;

   public oh() {
      this(0.0, 1.0);
   }

   private oh(double var1, double var3) {
      this(0.0, 1.0, 1.0E-9);
   }

   private oh(double var1, double var3, double var5) {
      this(new zR(), var1, var3, 1.0E-9);
   }

   private oh(zF var1, double var2, double var4) {
      this(var1, var2, var4, 1.0E-9);
   }

   public oh(zF var1, double var2, double var4, double var6) {
      super(var1);
      if (var4 <= 0.0) {
         throw new pk(px.SCALE, var4);
      }

      this.e = var4;
      this.d = var2;
      this.f = var6;
   }

   @Override
   public final double e(double var1) {
      return 0.5 + BY.q((var1 - this.d) / this.e) / Math.PI;
   }

   private double j() {
      return this.d;
   }

   private double k() {
      return this.e;
   }

   @Override
   public final double d(double var1) {
      double var3 = var1 - this.d;
      return 0.3183098861837907 * (this.e / (var3 * var3 + this.e * this.e));
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         double var3;
         if (var1 == 0.0) {
            var3 = Double.NEGATIVE_INFINITY;
         } else if (var1 == 1.0) {
            var3 = Double.POSITIVE_INFINITY;
         } else {
            var3 = this.d + this.e * BY.p(Math.PI * (var1 - 0.5));
         }

         return var3;
      } else {
         throw new po(var1, 0, 1);
      }
   }

   @Override
   protected final double a() {
      return this.f;
   }

   @Override
   public final double c() {
      return Double.NaN;
   }

   @Override
   public final double d() {
      return Double.NaN;
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
