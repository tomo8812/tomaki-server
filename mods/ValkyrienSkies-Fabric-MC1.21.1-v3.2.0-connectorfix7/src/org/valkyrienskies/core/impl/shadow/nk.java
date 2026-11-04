package org.valkyrienskies.core.impl.shadow;

public final class nk implements ln {
   private double[] a;
   private final double[] b;
   private final double[] c;
   private boolean d;

   public nk(double[] var1, double[] var2) {
      a(var1, var2);
      this.c = new double[var1.length];
      this.b = new double[var2.length];
      System.arraycopy(var1, 0, this.c, 0, var1.length);
      System.arraycopy(var2, 0, this.b, 0, var2.length);
      this.d = false;
   }

   @Override
   public final double a(double var1) {
      double var5 = var1;
      double[] var2 = this.b;
      double[] var10 = this.c;
      a(this.c, var2);
      int var3 = var2.length;
      double var8 = var10[var3];
      var3--;

      while (var3 >= 0) {
         var8 = var10[var3] + (var5 - var2[var3]) * var8;
         var3--;
      }

      return var8;
   }

   @Override
   public final lg a(lg var1) {
      a(this.c, this.b);
      int var2 = this.b.length;
      lg var3 = new lg(var1.C(), var1.D(), this.c[var2]);
      var2--;

      while (var2 >= 0) {
         double var6 = this.b[var2];
         var3 = var1.h(-var6).b(var3).h(this.c[var2]);
         var2--;
      }

      return var3;
   }

   private int a() {
      return this.b.length;
   }

   private double[] b() {
      double[] var1 = new double[this.c.length];
      System.arraycopy(this.c, 0, var1, 0, this.c.length);
      return var1;
   }

   private double[] c() {
      double[] var1 = new double[this.b.length];
      System.arraycopy(this.b, 0, var1, 0, this.b.length);
      return var1;
   }

   private double[] d() {
      if (!this.d) {
         nk var1 = this;
         int var2 = this.b.length;
         var1.a = new double[var2 + 1];

         for (int var3 = 0; var3 <= var2; var3++) {
            var1.a[var3] = 0.0;
         }

         var1.a[0] = var1.c[var2];

         for (int var6 = var2 - 1; var6 >= 0; var6--) {
            for (int var4 = var2 - var6; var4 > 0; var4--) {
               var1.a[var4] = var1.a[var4 - 1] - var1.b[var6] * var1.a[var4];
            }

            var1.a[0] = var1.c[var6] - var1.b[var6] * var1.a[0];
         }

         var1.d = true;
      }

      double[] var5 = new double[this.a.length];
      System.arraycopy(this.a, 0, var5, 0, this.a.length);
      return var5;
   }

   private static double a(double[] var0, double[] var1, double var2) {
      a(var0, var1);
      int var4 = var1.length;
      double var5 = var0[var4];
      var4--;

      while (var4 >= 0) {
         var5 = var0[var4] + (var2 - var1[var4]) * var5;
         var4--;
      }

      return var5;
   }

   private void e() {
      int var1 = this.b.length;
      this.a = new double[var1 + 1];

      for (int var2 = 0; var2 <= var1; var2++) {
         this.a[var2] = 0.0;
      }

      this.a[0] = this.c[var1];

      for (int var4 = var1 - 1; var4 >= 0; var4--) {
         for (int var3 = var1 - var4; var3 > 0; var3--) {
            this.a[var3] = this.a[var3 - 1] - this.b[var4] * this.a[var3];
         }

         this.a[0] = this.c[var4] - this.b[var4] * this.a[0];
      }

      this.d = true;
   }

   private static void a(double[] var0, double[] var1) {
      Ci.a(var0);
      Ci.a(var1);
      if (var0.length != 0 && var1.length != 0) {
         if (var0.length != var1.length + 1) {
            throw new oS(px.ARRAY_SIZES_SHOULD_HAVE_DIFFERENCE_1, var0.length, var1.length);
         }
      } else {
         throw new pf(px.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
      }
   }
}
