package org.valkyrienskies.core.impl.shadow;

public final class oN extends oe {
   @Deprecated
   private static double b;
   private static final long c = 20120109L;
   private final double d;
   private final double e;

   public oN() {
      this(0.0, 1.0);
   }

   public oN(double var1, double var3) {
      this(new zR(), var1, var3);
   }

   @Deprecated
   private oN(double var1, double var3, byte var5) {
      this(new zR(), var1, var3);
   }

   @Deprecated
   private oN(zF var1, double var2, double var4, byte var6) {
      this(var1, var2, var4);
   }

   public oN(zF var1, double var2, double var4) {
      super(var1);
      if (var2 >= var4) {
         throw new pm(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var2, var4, false);
      }

      this.d = var2;
      this.e = var4;
   }

   @Override
   public final double d(double var1) {
      return !(var1 < this.d) && !(var1 > this.e) ? 1.0 / (this.e - this.d) : 0.0;
   }

   @Override
   public final double e(double var1) {
      if (var1 <= this.d) {
         return 0.0;
      } else {
         return var1 >= this.e ? 1.0 : (var1 - this.d) / (this.e - this.d);
      }
   }

   @Override
   public final double a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         return var1 * (this.e - this.d) + this.d;
      } else {
         throw new po(var1, 0, 1);
      }
   }

   @Override
   public final double c() {
      return 0.5 * (this.d + this.e);
   }

   @Override
   public final double d() {
      double var1;
      return (var1 = this.e - this.d) * var1 / 12.0;
   }

   @Override
   public final double e() {
      return this.d;
   }

   @Override
   public final double f() {
      return this.e;
   }

   @Override
   public final boolean g() {
      return true;
   }

   @Override
   public final boolean h() {
      return true;
   }

   @Override
   public final boolean i() {
      return true;
   }

   @Override
   public final double b() {
      double var1;
      return (var1 = super.a.nextDouble()) * this.e + (1.0 - var1) * this.d;
   }
}
