package org.valkyrienskies.core.impl.shadow;

public final class og extends oc {
   private static final long b = 6751309484392813623L;
   private final int c;
   private final double d;

   private og(int var1, double var2) {
      this(new zR(), var1, var2);
   }

   public og(zF var1, int var2, double var3) {
      super(var1);
      if (var2 < 0) {
         throw new pj(px.NUMBER_OF_TRIALS, var2);
      }

      if (!(var3 < 0.0) && !(var3 > 1.0)) {
         this.d = var3;
         this.c = var2;
      } else {
         throw new po(var3, 0, 1);
      }
   }

   private int g() {
      return this.c;
   }

   private double h() {
      return this.d;
   }

   @Override
   public final double c(int var1) {
      double var2;
      return (var2 = this.b(var1)) == Double.NEGATIVE_INFINITY ? 0.0 : BY.i(var2);
   }

   @Override
   public final double b(int var1) {
      if (this.c == 0) {
         return var1 == 0 ? 0.0 : Double.NEGATIVE_INFINITY;
      }

      double var2;
      if (var1 >= 0 && var1 <= this.c) {
         var2 = oJ.a(var1, this.c, this.d, 1.0 - this.d);
      } else {
         var2 = Double.NEGATIVE_INFINITY;
      }

      return var2;
   }

   @Override
   public final double d(int var1) {
      double var2;
      if (var1 < 0) {
         var2 = 0.0;
      } else if (var1 >= this.c) {
         var2 = 1.0;
      } else {
         var2 = 1.0 - zW.a(this.d, var1 + 1.0, this.c - var1);
      }

      return var2;
   }

   @Override
   public final double b() {
      return this.c * this.d;
   }

   @Override
   public final double c() {
      double var1 = this.d;
      return this.c * var1 * (1.0 - var1);
   }

   @Override
   public final int d() {
      return this.d < 1.0 ? 0 : this.c;
   }

   @Override
   public final int e() {
      return this.d > 0.0 ? this.c : 0;
   }

   @Override
   public final boolean f() {
      return true;
   }
}
