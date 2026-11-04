package org.valkyrienskies.core.impl.shadow;

public final class Am extends Ah {
   private Al[] a;
   private int b;

   private Am(int var1) {
      this(var1, true);
   }

   private Am(int var1, boolean var2) {
      this.b = var1;
      this.a = new Al[this.b * (this.b + 1) / 2];
      var2 = true;
      Am var9 = this;

      for (int var3 = 0; var3 < var9.b; var3++) {
         for (int var4 = 0; var4 < var9.b; var4++) {
            Al var8 = new Al(var2);
            int var7 = var4;
            int var6 = var3;
            var9.a[a(var6, var7)] = var8;
         }
      }
   }

   private void a(boolean var1) {
      for (int var2 = 0; var2 < this.b; var2++) {
         for (int var3 = 0; var3 < this.b; var3++) {
            this.a(var2, var3, new Al(var1));
         }
      }
   }

   private static int a(int var0, int var1) {
      return var1 < var0 ? var0 * (var0 + 1) / 2 + var1 : var1 * (var1 + 1) / 2 + var0;
   }

   private Al b(int var1, int var2) {
      return this.a[a(var1, var2)];
   }

   private void a(int var1, int var2, Al var3) {
      this.a[a(var1, var2)] = var3;
   }

   private double c(int var1, int var2) {
      return this.b(var1, var2).a();
   }

   private void a(double[] var1) {
      int var2;
      if ((var2 = var1.length) != this.b) {
         throw new oS(var2, this.b);
      }

      for (int var3 = 0; var3 < var2; var3++) {
         for (int var4 = var3; var4 < var2; var4++) {
            Al var10000 = this.b(var3, var4);
            double var10001 = var1[var3];
            double var8 = var1[var4];
            double var6 = var10001;
            Al var5 = var10000;
            var10000.c++;
            double var10 = var6 - var5.a;
            double var12 = var8 - var5.b;
            var5.a = var5.a + var10 / var5.c;
            var5.b = var5.b + var12 / var5.c;
            var5.d = var5.d + (var5.c - 1.0) / var5.c * var10 * var12;
         }
      }
   }

   private void a(Am var1) {
      if (var1.b != this.b) {
         throw new oS(var1.b, this.b);
      }

      for (int var2 = 0; var2 < this.b; var2++) {
         for (int var3 = var2; var3 < this.b; var3++) {
            Al var10000 = this.b(var2, var3);
            Al var5 = var1.b(var2, var3);
            Al var4 = var10000;
            double var6 = var10000.c;
            var4.c = var4.c + var5.c;
            double var8 = var5.a - var4.a;
            double var10 = var5.b - var4.b;
            var4.a = var4.a + var8 * var5.c / var4.c;
            var4.b = var4.b + var10 * var5.c / var4.c;
            var4.d = var4.d + (var5.d + var6 * var5.c / var4.c * var8 * var10);
         }
      }
   }

   @Override
   public final tq a() {
      Am var1 = this;
      double[][] var2 = new double[this.b][var1.b];

      for (int var3 = 0; var3 < var1.b; var3++) {
         for (int var4 = 0; var4 < var1.b; var4++) {
            var2[var3][var4] = var1.b(var3, var4).a();
         }
      }

      return td.a(var2);
   }

   private double[][] c() {
      double[][] var1 = new double[this.b][this.b];

      for (int var2 = 0; var2 < this.b; var2++) {
         for (int var3 = 0; var3 < this.b; var3++) {
            var1[var2][var3] = this.b(var2, var3).a();
         }
      }

      return var1;
   }

   @Override
   public final int b() {
      throw new pb();
   }
}
