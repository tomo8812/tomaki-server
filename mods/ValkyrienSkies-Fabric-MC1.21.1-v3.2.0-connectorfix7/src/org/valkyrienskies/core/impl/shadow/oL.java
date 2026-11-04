package org.valkyrienskies.core.impl.shadow;

public final class oL extends oe {
   private static final long b = 20120112L;
   private final double c;
   private final double d;
   private final double e;
   private final double f;

   private oL(double var1, double var3, double var5) {
      this(new zR(), var1, var3, var5);
   }

   private oL(zR var1, double var2, double var4, double var6) {
      super(var1);
      if (var2 >= var6) {
         throw new pm(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var2, var6, false);
      }

      if (var4 < var2) {
         throw new pn(px.NUMBER_TOO_SMALL, var4, var2, true);
      }

      if (var4 > var6) {
         throw new pm(px.NUMBER_TOO_LARGE, var4, var6, true);
      }

      this.c = var2;
      this.e = var4;
      this.d = var6;
      this.f = BY.e(BY.x(var2), BY.x(var6));
   }

   private double j() {
      return this.e;
   }

   @Override
   protected final double a() {
      return this.f;
   }

   @Override
   public final double d(double var1) {
      if (var1 < this.c) {
         return 0.0;
      } else if (this.c <= var1 && var1 < this.e) {
         double var7 = 2.0 * (var1 - this.c);
         double var8 = (this.d - this.c) * (this.e - this.c);
         return var7 / var8;
      } else if (var1 == this.e) {
         return 2.0 / (this.d - this.c);
      } else if (this.e < var1 && var1 <= this.d) {
         double var3 = 2.0 * (this.d - var1);
         double var5 = (this.d - this.c) * (this.d - this.e);
         return var3 / var5;
      } else {
         return 0.0;
      }
   }

   @Override
   public final double e(double var1) {
      if (var1 < this.c) {
         return 0.0;
      } else if (this.c <= var1 && var1 < this.e) {
         double var7 = (var1 - this.c) * (var1 - this.c);
         double var8 = (this.d - this.c) * (this.e - this.c);
         return var7 / var8;
      } else if (var1 == this.e) {
         return (this.e - this.c) / (this.d - this.c);
      } else if (this.e < var1 && var1 <= this.d) {
         double var3 = (this.d - var1) * (this.d - var1);
         double var5 = (this.d - this.c) * (this.d - this.e);
         return 1.0 - var3 / var5;
      } else {
         return 1.0;
      }
   }

   @Override
   public final double c() {
      return (this.c + this.d + this.e) / 3.0;
   }

   @Override
   public final double d() {
      return (this.c * this.c + this.d * this.d + this.e * this.e - this.c * this.d - this.c * this.e - this.d * this.e) / 18.0;
   }

   @Override
   public final double e() {
      return this.c;
   }

   @Override
   public final double f() {
      return this.d;
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
   public final double a(double var1) {
      if (var1 < 0.0 || var1 > 1.0) {
         throw new po(var1, 0, 1);
      } else if (var1 == 0.0) {
         return this.c;
      } else if (var1 == 1.0) {
         return this.d;
      } else {
         return var1 < (this.e - this.c) / (this.d - this.c)
            ? this.c + BY.a(var1 * (this.d - this.c) * (this.e - this.c))
            : this.d - BY.a((1.0 - var1) * (this.d - this.c) * (this.d - this.e));
      }
   }
}
