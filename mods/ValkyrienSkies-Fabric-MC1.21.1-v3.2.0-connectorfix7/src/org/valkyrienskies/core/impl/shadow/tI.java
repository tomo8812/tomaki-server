package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

final class tI {
   final double[][] a;
   final double[] b;
   final double[] c;
   tq d;
   tq e;
   private tq f;

   tI(tq var1) {
      if (!var1.e()) {
         throw new th(var1.f(), var1.g());
      }

      int var2 = var1.f();
      this.a = var1.a();
      this.b = new double[var2];
      this.c = new double[var2 - 1];
      this.d = null;
      this.e = null;
      this.f = null;
      tI var23 = this;
      double[] var3 = new double[var2 = this.a.length];

      for (int var4 = 0; var4 < var2 - 1; var4++) {
         double[] var5 = var23.a[var4];
         var23.b[var4] = var5[var4];
         double var8 = 0.0;

         for (int var10 = var4 + 1; var10 < var2; var10++) {
            double var11 = var5[var10];
            var8 += var11 * var11;
         }

         double var25 = var5[var4 + 1] > 0.0 ? -BY.a(var8) : BY.a(var8);
         var23.c[var4] = var25;
         if (var25 != 0.0) {
            var5[var4 + 1] = var5[var4 + 1] - var25;
            double var12 = -1.0 / (var25 * var5[var4 + 1]);
            Arrays.fill(var3, var4 + 1, var2, 0.0);

            for (int var14 = var4 + 1; var14 < var2; var14++) {
               double[] var15 = var23.a[var14];
               double var16 = var5[var14];
               double var18 = var15[var14] * var16;

               for (int var6 = var14 + 1; var6 < var2; var6++) {
                  double var21 = var15[var6];
                  var18 += var21 * var5[var6];
                  var3[var6] += var21 * var16;
               }

               var3[var14] = var12 * (var3[var14] + var18);
            }

            double var26 = 0.0;

            for (int var28 = var4 + 1; var28 < var2; var28++) {
               var26 += var3[var28] * var5[var28];
            }

            var26 *= var12 / 2.0;

            for (int var29 = var4 + 1; var29 < var2; var29++) {
               var3[var29] -= var26 * var5[var29];
            }

            for (int var30 = var4 + 1; var30 < var2; var30++) {
               double[] var17 = var23.a[var30];

               for (int var31 = var30; var31 < var2; var31++) {
                  var17[var31] -= var5[var30] * var3[var31] + var3[var30] * var5[var31];
               }
            }
         }
      }

      var23.b[var2 - 1] = var23.a[var2 - 1][var2 - 1];
   }

   public final tq a() {
      if (this.d == null) {
         this.d = this.d().d();
      }

      return this.d;
   }

   private tq d() {
      if (this.e == null) {
         int var1;
         double[][] var2 = new double[var1 = this.a.length][var1];

         for (int var3 = var1 - 1; var3 > 0; var3--) {
            double[] var4 = this.a[var3 - 1];
            var2[var3][var3] = 1.0;
            if (var4[var3] != 0.0) {
               double var5 = 1.0 / (this.c[var3 - 1] * var4[var3]);
               double var7 = 1.0 / this.c[var3 - 1];
               var2[var3][var3] = 1.0 + var7 * var4[var3];

               for (int var9 = var3 + 1; var9 < var1; var9++) {
                  var2[var3][var9] = var7 * var4[var9];
               }

               for (int var13 = var3 + 1; var13 < var1; var13++) {
                  var7 = 0.0;

                  for (int var10 = var3 + 1; var10 < var1; var10++) {
                     var7 += var2[var13][var10] * var4[var10];
                  }

                  var7 *= var5;
                  var2[var13][var3] = var7 * var4[var3];

                  for (int var14 = var3 + 1; var14 < var1; var14++) {
                     var2[var13][var14] = var2[var13][var14] + var7 * var4[var14];
                  }
               }
            }
         }

         var2[0][0] = 1.0;
         this.e = td.a(var2);
      }

      return this.e;
   }

   private tq e() {
      if (this.f == null) {
         int var1;
         double[][] var2 = new double[var1 = this.b.length][var1];

         for (int var3 = 0; var3 < var1; var3++) {
            var2[var3][var3] = this.b[var3];
            if (var3 > 0) {
               var2[var3][var3 - 1] = this.c[var3 - 1];
            }

            if (var3 < this.b.length - 1) {
               var2[var3][var3 + 1] = this.c[var3];
            }
         }

         this.f = td.a(var2);
      }

      return this.f;
   }

   private double[][] f() {
      return this.a;
   }

   final double[] b() {
      return this.b;
   }

   final double[] c() {
      return this.c;
   }

   private void g() {
      int var1;
      double[] var2 = new double[var1 = this.a.length];

      for (int var3 = 0; var3 < var1 - 1; var3++) {
         double[] var4 = this.a[var3];
         this.b[var3] = var4[var3];
         double var5 = 0.0;

         for (int var7 = var3 + 1; var7 < var1; var7++) {
            double var8 = var4[var7];
            var5 += var8 * var8;
         }

         double var21 = var4[var3 + 1] > 0.0 ? -BY.a(var5) : BY.a(var5);
         this.c[var3] = var21;
         if (var21 != 0.0) {
            var4[var3 + 1] = var4[var3 + 1] - var21;
            double var9 = -1.0 / (var21 * var4[var3 + 1]);
            Arrays.fill(var2, var3 + 1, var1, 0.0);

            for (int var11 = var3 + 1; var11 < var1; var11++) {
               double[] var12 = this.a[var11];
               double var13 = var4[var11];
               double var15 = var12[var11] * var13;

               for (int var20 = var11 + 1; var20 < var1; var20++) {
                  double var18 = var12[var20];
                  var15 += var18 * var4[var20];
                  var2[var20] += var18 * var13;
               }

               var2[var11] = var9 * (var2[var11] + var15);
            }

            double var22 = 0.0;

            for (int var24 = var3 + 1; var24 < var1; var24++) {
               var22 += var2[var24] * var4[var24];
            }

            var22 *= var9 / 2.0;

            for (int var25 = var3 + 1; var25 < var1; var25++) {
               var2[var25] -= var22 * var4[var25];
            }

            for (int var26 = var3 + 1; var26 < var1; var26++) {
               double[] var14 = this.a[var26];

               for (int var27 = var26; var27 < var1; var27++) {
                  var14[var27] -= var4[var26] * var2[var27] + var2[var26] * var4[var27];
               }
            }
         }
      }

      this.b[var1 - 1] = this.a[var1 - 1][var1 - 1];
   }
}
