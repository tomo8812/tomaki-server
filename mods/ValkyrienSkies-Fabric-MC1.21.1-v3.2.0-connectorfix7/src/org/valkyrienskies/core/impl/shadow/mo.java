package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class mo extends mm {
   private static final double[] a = new double[]{-1.0 / BY.a(3.0), 1.0 / BY.a(3.0)};
   private static final double[] b = new double[]{1.0, 1.0};
   private static final double[] c = new double[]{-BY.a(0.6), 0.0, BY.a(0.6)};
   private static final double[] d = new double[]{0.5555555555555556, 0.8888888888888888, 0.5555555555555556};
   private static final double[] e = new double[]{
      -BY.a((15.0 + 2.0 * BY.a(30.0)) / 35.0),
      -BY.a((15.0 - 2.0 * BY.a(30.0)) / 35.0),
      BY.a((15.0 - 2.0 * BY.a(30.0)) / 35.0),
      BY.a((15.0 + 2.0 * BY.a(30.0)) / 35.0)
   };
   private static final double[] f = new double[]{
      (90.0 - 5.0 * BY.a(30.0)) / 180.0, (90.0 + 5.0 * BY.a(30.0)) / 180.0, (90.0 + 5.0 * BY.a(30.0)) / 180.0, (90.0 - 5.0 * BY.a(30.0)) / 180.0
   };
   private static final double[] g = new double[]{
      -BY.a((35.0 + 2.0 * BY.a(70.0)) / 63.0),
      -BY.a((35.0 - 2.0 * BY.a(70.0)) / 63.0),
      0.0,
      BY.a((35.0 - 2.0 * BY.a(70.0)) / 63.0),
      BY.a((35.0 + 2.0 * BY.a(70.0)) / 63.0)
   };
   private static final double[] h = new double[]{
      (322.0 - 13.0 * BY.a(70.0)) / 900.0,
      (322.0 + 13.0 * BY.a(70.0)) / 900.0,
      0.5688888888888889,
      (322.0 + 13.0 * BY.a(70.0)) / 900.0,
      (322.0 - 13.0 * BY.a(70.0)) / 900.0
   };
   private final double[] i;
   private final double[] j;

   private mo(int var1, double var2, double var4, int var6, int var7) {
      super(var2, var4, var6, var7);
      switch (var1) {
         case 2:
            this.i = a;
            this.j = b;
            return;
         case 3:
            this.i = c;
            this.j = d;
            return;
         case 4:
            this.i = e;
            this.j = f;
            return;
         case 5:
            this.i = g;
            this.j = h;
            return;
         default:
            throw new oV(px.N_POINTS_GAUSS_LEGENDRE_INTEGRATOR_NOT_SUPPORTED, var1, 2, 5);
      }
   }

   private mo(int var1, double var2, double var4) {
      this(var1, var2, var4, 3, Integer.MAX_VALUE);
   }

   private mo(int var1, int var2, int var3) {
      this(var1, 1.0E-6, 1.0E-15, var2, var3);
   }

   @Override
   protected final double j() {
      double var1 = this.a(1);
      int var3 = 2;

      while (true) {
         double var4;
         double var6 = BY.w((var4 = this.a(var3)) - var1);
         double var8 = BY.e(this.b(), this.a() * (BY.w(var1) + BY.w(var4)) * 0.5);
         if (this.f() + 1 >= this.c() && var6 <= var8) {
            return var4;
         }

         var3 = BY.b((int)(BY.d(4.0, BY.a(var6 / var8, 0.5 / this.i.length)) * var3), var3 + 1);
         var1 = var4;
         this.g();
      }
   }

   private double a(int var1) {
      double var2;
      double var4 = (var2 = (this.i() - this.h()) / var1) / 2.0;
      double var6 = this.h() + var4;
      double var8 = 0.0;

      for (int var10 = 0; var10 < var1; var10++) {
         for (int var11 = 0; var11 < this.i.length; var11++) {
            var8 += this.j[var11] * this.a(var6 + var4 * this.i[var11]);
         }

         var6 += var2;
      }

      return var4 * var8;
   }
}
