package org.valkyrienskies.core.impl.shadow;

public final class Aj {
   final tq a;
   private final int b;

   public Aj() {
      this.a = null;
      this.b = 0;
   }

   private Aj(double[][] var1) {
      this(new sD(var1));
   }

   public Aj(tq var1) {
      this.b = var1.f();
      this.a = this.a(var1);
   }

   private Aj(Ah var1) {
      tq var2;
      if ((var2 = var1.a()) == null) {
         throw new pl(px.COVARIANCE_MATRIX);
      }

      this.b = var1.b();
      this.a = b(var2);
   }

   private Aj(tq var1, int var2) {
      this.b = var2;
      this.a = b(var1);
   }

   public final tq a() {
      return this.a;
   }

   private sD b() {
      int var1;
      double[][] var2 = new double[var1 = this.a.g()][var1];

      for (int var3 = 0; var3 < var1; var3++) {
         for (int var4 = 0; var4 < var1; var4++) {
            double var5 = this.a.b(var3, var4);
            var2[var3][var4] = BY.a((1.0 - var5 * var5) / (this.b - 2));
         }
      }

      return new sD(var2);
   }

   private sD c() {
      oK var1 = new oK(this.b - 2);
      int var2;
      double[][] var3 = new double[var2 = this.a.g()][var2];

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            if (var4 == var5) {
               var3[var4][var5] = 0.0;
            } else {
               double var6;
               double var8 = BY.w((var6 = this.a.b(var4, var5)) * BY.a((this.b - 2) / (1.0 - var6 * var6)));
               var3[var4][var5] = 2.0 * var1.e(-var8);
            }
         }
      }

      return new sD(var3);
   }

   public final sD a(tq var1) {
      c(var1);
      int var2 = var1.g();
      sD var3 = new sD(var2, var2);

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var4; var5++) {
            double var6 = a(var1.g(var4), var1.g(var5));
            var3.c(var4, var5, var6);
            var3.c(var5, var4, var6);
         }

         var3.c(var4, var4, 1.0);
      }

      return var3;
   }

   private tq a(double[][] var1) {
      return this.a(new sD(var1));
   }

   public static double a(double[] var0, double[] var1) {
      BA var2 = new BA();
      if (var0.length != var1.length) {
         throw new oS(var0.length, var1.length);
      }

      if (var0.length < 2) {
         throw new oV(px.INSUFFICIENT_DIMENSION, var0.length, 2);
      }

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2.a(var0[var3], var1[var3]);
      }

      return var2.e();
   }

   private static sD b(tq var0) {
      int var1 = var0.g();
      sD var2 = new sD(var1, var1);

      for (int var3 = 0; var3 < var1; var3++) {
         double var4 = BY.a(var0.b(var3, var3));
         var2.c(var3, var3, 1.0);

         for (int var6 = 0; var6 < var3; var6++) {
            double var7 = var0.b(var3, var6) / (var4 * BY.a(var0.b(var6, var6)));
            var2.c(var3, var6, var7);
            var2.c(var6, var3, var7);
         }
      }

      return var2;
   }

   private static void c(tq var0) {
      int var1 = var0.f();
      int var2 = var0.g();
      if (var1 < 2 || var2 < 2) {
         throw new oV(px.INSUFFICIENT_ROWS_AND_COLUMNS, var1, var2);
      }
   }
}
