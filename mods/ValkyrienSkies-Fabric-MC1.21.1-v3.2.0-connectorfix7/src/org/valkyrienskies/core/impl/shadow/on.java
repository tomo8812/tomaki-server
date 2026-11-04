package org.valkyrienskies.core.impl.shadow;

public final class on extends oe {
   private static double b;
   private static final long c = 2401296428283614780L;
   private static final double[] d;
   private final double e;
   private final double f;
   private final double g;

   private on(double var1) {
      this(var1, 1.0E-9);
   }

   private on(double var1, double var3) {
      this(new zR(), var1, 1.0E-9);
   }

   private on(zF var1, double var2) {
      this(var1, var2, 1.0E-9);
   }

   public on(zF var1, double var2, double var4) {
      super(var1);
      if (var2 <= 0.0) {
         throw new pk(px.MEAN, var2);
      }

      this.e = var2;
      this.f = BY.k(var2);
      this.g = var4;
   }

   private double j() {
      return this.e;
   }

   @Override
   public final double d(double var1) {
      double var3;
      return (var3 = this.c(var1)) == Double.NEGATIVE_INFINITY ? 0.0 : BY.i(var3);
   }

   @Override
   public final double c(double var1) {
      return var1 < 0.0 ? Double.NEGATIVE_INFINITY : -var1 / this.e - this.f;
   }

   @Override
   public final double e(double var1) {
      double var3;
      if (var1 <= 0.0) {
         var3 = 0.0;
      } else {
         var3 = 1.0 - BY.i(-var1 / this.e);
      }

      return var3;
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         double var3;
         if (var1 == 1.0) {
            var3 = Double.POSITIVE_INFINITY;
         } else {
            var3 = -this.e * BY.k(1.0 - var1);
         }

         return var3;
      } else {
         throw new po(var1, 0.0, 1.0);
      }
   }

   @Override
   public final double b() {
      double var1 = 0.0;

      double var3;
      for (var3 = super.a.nextDouble(); var3 < 0.5; var3 *= 2.0) {
         var1 += d[0];
      }

      if ((var3 = var3 + (var3 - 1.0)) <= d[0]) {
         return this.e * (var1 + var3);
      }

      int var5 = 0;
      double var8 = super.a.nextDouble();

      do {
         var5++;
         double var6;
         if ((var6 = super.a.nextDouble()) < var8) {
            var8 = var6;
         }
      } while (var3 > d[var5]);

      return this.e * (var1 + var8 * d[0]);
   }

   @Override
   protected final double a() {
      return this.g;
   }

   @Override
   public final double c() {
      return this.e;
   }

   @Override
   public final double d() {
      double var1 = this.e;
      return this.e * var1;
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

   static {
      double var0 = BY.k(2.0);
      double var2 = 0.0;
      int var4 = 1;
      Cs var5 = new Cs(20);

      while (var2 < 1.0) {
         var2 += BY.a(var0, var4) / BR.a(var4);
         var5.a(var2);
         var4++;
      }

      d = var5.b();
   }
}
