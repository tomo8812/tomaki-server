package org.valkyrienskies.core.impl.shadow;

final class sW {
   final double[][] a;
   private final double[] c;
   private tq d;
   private tq e;
   tq b;

   sW(tq var1) {
      if (!var1.e()) {
         throw new th(var1.f(), var1.g());
      }

      int var2 = var1.f();
      this.a = var1.a();
      this.c = new double[var2];
      this.d = null;
      this.e = null;
      this.b = null;
      sW var16 = this;
      int var3 = (var2 = this.a.length) - 1;

      for (int var4 = 1; var4 <= var3 - 1; var4++) {
         double var7 = 0.0;

         for (int var9 = var4; var9 <= var3; var9++) {
            var7 += BY.w(var16.a[var9][var4 - 1]);
         }

         if (!Cq.a(var7, 0.0)) {
            double var22 = 0.0;

            for (int var11 = var3; var11 >= var4; var11--) {
               var16.c[var11] = var16.a[var11][var4 - 1] / var7;
               var22 += var16.c[var11] * var16.c[var11];
            }

            double var24 = var16.c[var4] > 0.0 ? -BY.a(var22) : BY.a(var22);
            var22 -= var16.c[var4] * var24;
            var16.c[var4] = var16.c[var4] - var24;

            for (int var5 = var4; var5 < var2; var5++) {
               double var14 = 0.0;

               for (int var6 = var3; var6 >= var4; var6--) {
                  var14 += var16.c[var6] * var16.a[var6][var5];
               }

               var14 /= var22;

               for (int var19 = var4; var19 <= var3; var19++) {
                  var16.a[var19][var5] = var16.a[var19][var5] - var14 * var16.c[var19];
               }
            }

            for (int var18 = 0; var18 <= var3; var18++) {
               double var26 = 0.0;

               for (int var20 = var3; var20 >= var4; var20--) {
                  var26 += var16.c[var20] * var16.a[var18][var20];
               }

               var26 /= var22;

               for (int var21 = var4; var21 <= var3; var21++) {
                  var16.a[var18][var21] = var16.a[var18][var21] - var26 * var16.c[var21];
               }
            }

            var16.c[var4] = var7 * var16.c[var4];
            var16.a[var4][var4 - 1] = var7 * var24;
         }
      }
   }

   public final tq a() {
      if (this.d == null) {
         int var1;
         int var2 = (var1 = this.a.length) - 1;
         double[][] var3 = new double[var1][var1];

         for (int var4 = 0; var4 < var1; var4++) {
            for (int var5 = 0; var5 < var1; var5++) {
               var3[var4][var5] = var4 == var5 ? 1.0 : 0.0;
            }
         }

         for (int var10 = var2 - 1; var10 > 0; var10--) {
            if (this.a[var10][var10 - 1] != 0.0) {
               for (int var11 = var10 + 1; var11 <= var2; var11++) {
                  this.c[var11] = this.a[var11][var10 - 1];
               }

               for (int var12 = var10; var12 <= var2; var12++) {
                  double var6 = 0.0;

                  for (int var8 = var10; var8 <= var2; var8++) {
                     var6 += this.c[var8] * var3[var8][var12];
                  }

                  var6 = var6 / this.c[var10] / this.a[var10][var10 - 1];

                  for (int var9 = var10; var9 <= var2; var9++) {
                     var3[var9][var12] = var3[var9][var12] + var6 * this.c[var9];
                  }
               }
            }
         }

         this.d = td.a(var3);
      }

      return this.d;
   }

   private tq c() {
      if (this.e == null) {
         this.e = this.a().d();
      }

      return this.e;
   }

   public final tq b() {
      if (this.b == null) {
         int var1;
         double[][] var2 = new double[var1 = this.a.length][var1];

         for (int var3 = 0; var3 < var1; var3++) {
            if (var3 > 0) {
               var2[var3][var3 - 1] = this.a[var3][var3 - 1];
            }

            for (int var4 = var3; var4 < var1; var4++) {
               var2[var3][var4] = this.a[var3][var4];
            }
         }

         this.b = td.a(var2);
      }

      return this.b;
   }

   private double[][] d() {
      return this.a;
   }

   private void e() {
      int var1;
      int var2 = (var1 = this.a.length) - 1;

      for (int var3 = 1; var3 <= var2 - 1; var3++) {
         double var4 = 0.0;

         for (int var6 = var3; var6 <= var2; var6++) {
            var4 += BY.w(this.a[var6][var3 - 1]);
         }

         if (!Cq.a(var4, 0.0)) {
            double var14 = 0.0;

            for (int var8 = var2; var8 >= var3; var8--) {
               this.c[var8] = this.a[var8][var3 - 1] / var4;
               var14 += this.c[var8] * this.c[var8];
            }

            double var16 = this.c[var3] > 0.0 ? -BY.a(var14) : BY.a(var14);
            var14 -= this.c[var3] * var16;
            this.c[var3] = this.c[var3] - var16;

            for (int var10 = var3; var10 < var1; var10++) {
               double var11 = 0.0;

               for (int var13 = var2; var13 >= var3; var13--) {
                  var11 += this.c[var13] * this.a[var13][var10];
               }

               var11 /= var14;

               for (int var21 = var3; var21 <= var2; var21++) {
                  this.a[var21][var10] = this.a[var21][var10] - var11 * this.c[var21];
               }
            }

            for (int var17 = 0; var17 <= var2; var17++) {
               double var19 = 0.0;

               for (int var22 = var2; var22 >= var3; var22--) {
                  var19 += this.c[var22] * this.a[var17][var22];
               }

               var19 /= var14;

               for (int var23 = var3; var23 <= var2; var23++) {
                  this.a[var17][var23] = this.a[var17][var23] - var19 * this.c[var23];
               }
            }

            this.c[var3] = var4 * this.c[var3];
            this.a[var3][var3 - 1] = var4 * var16;
         }
      }
   }
}
