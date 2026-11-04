package org.valkyrienskies.core.impl.shadow;

public final class oq extends oc {
   private static final long b = 20130507L;
   private final double c;
   private final double d;
   private final double e;

   private oq(double var1) {
      this(new zR(), var1);
   }

   private oq(zR var1, double var2) {
      super(var1);
      if (!(var2 <= 0.0) && !(var2 > 1.0)) {
         this.c = var2;
         this.d = BY.k(var2);
         this.e = BY.l(-var2);
      } else {
         throw new po(px.OUT_OF_RANGE_LEFT, var2, 0, 1);
      }
   }

   private double g() {
      return this.c;
   }

   @Override
   public final double c(int var1) {
      return var1 < 0 ? 0.0 : BY.i(this.e * var1) * this.c;
   }

   @Override
   public final double b(int var1) {
      return var1 < 0 ? Double.NEGATIVE_INFINITY : var1 * this.e + this.d;
   }

   @Override
   public final double d(int var1) {
      return var1 < 0 ? 0.0 : -BY.j(this.e * (var1 + 1));
   }

   @Override
   public final double b() {
      return (1.0 - this.c) / this.c;
   }

   @Override
   public final double c() {
      return (1.0 - this.c) / (this.c * this.c);
   }

   @Override
   public final int d() {
      return 0;
   }

   @Override
   public final int e() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final boolean f() {
      return true;
   }

   @Override
   public final int a(double var1) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0, 1);
      } else if (var1 == 1.0) {
         return Integer.MAX_VALUE;
      } else {
         return var1 == 0.0 ? 0 : Math.max(0, (int)Math.ceil(BY.l(-var1) / this.e - 1.0));
      }
   }
}
