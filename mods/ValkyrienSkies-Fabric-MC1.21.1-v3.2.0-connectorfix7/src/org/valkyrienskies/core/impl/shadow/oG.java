package org.valkyrienskies.core.impl.shadow;

public final class oG extends oc {
   private static final long b = 6751309484392813623L;
   private final int c;
   private final double d;
   private final double e;
   private final double f;

   private oG(int var1, double var2) {
      this(new zR(), var1, var2);
   }

   public oG(zF var1, int var2, double var3) {
      super(var1);
      if (var2 <= 0) {
         throw new pk(px.NUMBER_OF_SUCCESSES, var2);
      }

      if (!(var3 < 0.0) && !(var3 > 1.0)) {
         this.c = var2;
         this.d = var3;
         this.e = BY.k(var3);
         this.f = BY.l(-var3);
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
      if (var1 < 0) {
         var2 = 0.0;
      } else {
         var2 = BR.b(var1 + this.c - 1, this.c - 1) * BY.a(this.d, this.c) * BY.a(1.0 - this.d, var1);
      }

      return var2;
   }

   @Override
   public final double b(int var1) {
      double var2;
      if (var1 < 0) {
         var2 = Double.NEGATIVE_INFINITY;
      } else {
         var2 = BR.c(var1 + this.c - 1, this.c - 1) + this.e * this.c + this.f * var1;
      }

      return var2;
   }

   @Override
   public final double d(int var1) {
      double var2;
      if (var1 < 0) {
         var2 = 0.0;
      } else {
         var2 = zW.a(this.d, this.c, var1 + 1.0);
      }

      return var2;
   }

   @Override
   public final double b() {
      double var1 = this.d;
      double var3;
      return (var3 = this.c) * (1.0 - var1) / var1;
   }

   @Override
   public final double c() {
      double var1 = this.d;
      double var3;
      return (var3 = this.c) * (1.0 - var1) / (var1 * var1);
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
}
