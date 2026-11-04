package org.valkyrienskies.core.impl.shadow;

public final class oP extends oc {
   private static final long b = -140627372283420404L;
   private final int c;
   private final double d;
   private double e = Double.NaN;
   private boolean f = false;
   private double g = Double.NaN;
   private boolean h = false;
   private transient oP.a i;

   private oP(int var1, double var2) {
      this(new zR(), var1, var2);
   }

   public oP(zF var1, int var2, double var3) {
      super(var1);
      if (var2 <= 0) {
         throw new pk(px.DIMENSION, var2);
      }

      if (var3 <= 0.0) {
         throw new pk(px.EXPONENT, var3);
      }

      this.c = var2;
      this.d = var3;
   }

   private int g() {
      return this.c;
   }

   private double h() {
      return this.d;
   }

   @Override
   public final double c(int var1) {
      return var1 > 0 && var1 <= this.c ? 1.0 / BY.a(var1, this.d) / a(this.c, this.d) : 0.0;
   }

   @Override
   public final double b(int var1) {
      return var1 > 0 && var1 <= this.c ? -BY.k(var1) * this.d - BY.k(a(this.c, this.d)) : Double.NEGATIVE_INFINITY;
   }

   @Override
   public final double d(int var1) {
      if (var1 <= 0) {
         return 0.0;
      } else {
         return var1 >= this.c ? 1.0 : a(var1, this.d) / a(this.c, this.d);
      }
   }

   @Override
   public final double b() {
      if (!this.f) {
         oP var1 = this;
         int var2 = this.c;
         double var3 = var1.d;
         double var5 = a(var2, var3 - 1.0);
         double var7 = a(var2, var3);
         this.e = var5 / var7;
         this.f = true;
      }

      return this.e;
   }

   private double i() {
      int var1 = this.c;
      double var2 = this.d;
      double var4 = a(var1, var2 - 1.0);
      double var6 = a(var1, var2);
      return var4 / var6;
   }

   @Override
   public final double c() {
      if (!this.h) {
         oP var1 = this;
         int var2 = this.c;
         double var3 = var1.d;
         double var5 = a(var2, var3 - 2.0);
         double var7 = a(var2, var3 - 1.0);
         double var9 = a(var2, var3);
         this.g = var5 / var9 - var7 * var7 / (var9 * var9);
         this.h = true;
      }

      return this.g;
   }

   private double j() {
      int var1 = this.c;
      double var2 = this.d;
      double var4 = a(var1, var2 - 2.0);
      double var6 = a(var1, var2 - 1.0);
      double var8 = a(var1, var2);
      return var4 / var8 - var6 * var6 / (var8 * var8);
   }

   private static double a(int var0, double var1) {
      double var3 = 0.0;

      for (int var5 = var0; var5 > 0; var5--) {
         var3 += 1.0 / BY.a(var5, var1);
      }

      return var3;
   }

   @Override
   public final int d() {
      return 1;
   }

   @Override
   public final int e() {
      return this.c;
   }

   @Override
   public final boolean f() {
      return true;
   }

   @Override
   public final int a() {
      if (this.i == null) {
         this.i = new oP.a(this.c, this.d);
      }

      zF var2 = super.a;
      oP.a var1 = this.i;

      double var3;
      double var5;
      int var7;
      do {
         var3 = var1.c + var2.nextDouble() * (var1.b - var1.c);
         if ((var7 = (int)((var5 = var1.c(var3)) + 0.5)) <= 0) {
            var7 = 1;
         } else if (var7 > var1.a) {
            var7 = var1.a;
         }
      } while (!(var7 - var5 <= var1.d) && !(var3 >= var1.a(var7 + 0.5) - var1.b(var7)));

      return var7;
   }

   static final class a {
      private final double e;
      final int a;
      final double b;
      final double c;
      final double d;

      a(int var1, double var2) {
         this.e = var2;
         this.a = var1;
         this.b = this.a(1.5) - 1.0;
         this.c = this.a(var1 + 0.5);
         this.d = 2.0 - this.c(this.a(2.5) - this.b(2.0));
      }

      final int a(zF var1) {
         double var2;
         double var4;
         int var6;
         do {
            var2 = this.c + var1.nextDouble() * (this.b - this.c);
            if ((var6 = (int)((var4 = this.c(var2)) + 0.5)) <= 0) {
               var6 = 1;
            } else if (var6 > this.a) {
               var6 = this.a;
            }
         } while (!(var6 - var4 <= this.d) && !(var2 >= this.a(var6 + 0.5) - this.b(var6)));

         return var6;
      }

      double a(double var1) {
         double var3 = BY.k(var1);
         return e((1.0 - this.e) * var3) * var3;
      }

      double b(double var1) {
         return BY.i(-this.e * BY.k(var1));
      }

      double c(double var1) {
         double var3;
         if ((var3 = var1 * (1.0 - this.e)) < -1.0) {
            var3 = -1.0;
         }

         return BY.i(d(var3) * var1);
      }

      private static double d(double var0) {
         return BY.w(var0) > 1.0E-8 ? BY.l(var0) / var0 : 1.0 - var0 * (0.5 - var0 * (0.3333333333333333 - var0 * 0.25));
      }

      private static double e(double var0) {
         return BY.w(var0) > 1.0E-8 ? BY.j(var0) / var0 : 1.0 + var0 * 0.5 * (1.0 + var0 * 0.3333333333333333 * (1.0 + var0 * 0.25));
      }
   }
}
