package org.valkyrienskies.core.impl.shadow;

final class sB {
   private final double[][] a;
   private final double[] b;
   private final double[] c;
   private tq d;
   private tq e;
   private tq f;

   private sB(tq var1) {
      int var2 = var1.f();
      int var3 = var1.g();
      int var4 = BY.a(var2, var3);
      this.a = var1.a();
      this.b = new double[var4];
      this.c = new double[var4 - 1];
      this.d = null;
      this.e = null;
      this.f = null;
      if (var2 >= var3) {
         sB var21 = this;
         var2 = this.a.length;
         var3 = var21.a[0].length;

         for (int var27 = 0; var27 < var3; var27++) {
            double var32 = 0.0;

            for (int var11 = var27; var11 < var2; var11++) {
               double var37 = var21.a[var11][var27];
               var32 += var37 * var37;
            }

            double[] var35;
            double var38 = (var35 = var21.a[var27])[var27] > 0.0 ? -BY.a(var32) : BY.a(var32);
            var21.b[var27] = var38;
            if (var38 != 0.0) {
               var35[var27] -= var38;

               for (int var40 = var27 + 1; var40 < var3; var40++) {
                  double var45 = 0.0;

                  for (int var51 = var27; var51 < var2; var51++) {
                     double[] var57 = var21.a[var51];
                     var45 -= var57[var40] * var57[var27];
                  }

                  var45 /= var38 * var21.a[var27][var27];

                  for (int var52 = var27; var52 < var2; var52++) {
                     double[] var58;
                     (var58 = var21.a[var52])[var40] -= var45 * var58[var27];
                  }
               }
            }

            if (var27 < var3 - 1) {
               var32 = 0.0;

               for (int var41 = var27 + 1; var41 < var3; var41++) {
                  double var47 = var35[var41];
                  var32 += var47 * var47;
               }

               double var42 = var35[var27 + 1] > 0.0 ? -BY.a(var32) : BY.a(var32);
               var21.c[var27] = var42;
               if (var42 != 0.0) {
                  var35[var27 + 1] = var35[var27 + 1] - var42;

                  for (int var50 = var27 + 1; var50 < var2; var50++) {
                     double[] var53 = var21.a[var50];
                     double var59 = 0.0;

                     for (int var29 = var27 + 1; var29 < var3; var29++) {
                        var59 -= var53[var29] * var35[var29];
                     }

                     var59 /= var42 * var35[var27 + 1];

                     for (int var30 = var27 + 1; var30 < var3; var30++) {
                        var53[var30] -= var59 * var35[var30];
                     }
                  }
               }
            }
         }
      } else {
         sB var20 = this;
         var2 = this.a.length;
         var3 = var20.a[0].length;

         for (int var26 = 0; var26 < var2; var26++) {
            double[] var9 = var20.a[var26];
            double var10 = 0.0;

            for (int var12 = var26; var12 < var3; var12++) {
               double var13 = var9[var12];
               var10 += var13 * var13;
            }

            double var36 = var9[var26] > 0.0 ? -BY.a(var10) : BY.a(var10);
            var20.b[var26] = var36;
            if (var36 != 0.0) {
               var9[var26] -= var36;

               for (int var14 = var26 + 1; var14 < var2; var14++) {
                  double[] var15 = var20.a[var14];
                  double var16 = 0.0;

                  for (int var18 = var26; var18 < var3; var18++) {
                     var16 -= var15[var18] * var9[var18];
                  }

                  var16 /= var36 * var20.a[var26][var26];

                  for (int var54 = var26; var54 < var3; var54++) {
                     var15[var54] -= var16 * var9[var54];
                  }
               }
            }

            if (var26 < var2 - 1) {
               double[] var39 = var20.a[var26 + 1];
               var10 = 0.0;

               for (int var43 = var26 + 1; var43 < var2; var43++) {
                  double var49 = var20.a[var43][var26];
                  var10 += var49 * var49;
               }

               double var44 = var39[var26] > 0.0 ? -BY.a(var10) : BY.a(var10);
               var20.c[var26] = var44;
               if (var44 != 0.0) {
                  var39[var26] -= var44;

                  for (int var17 = var26 + 1; var17 < var3; var17++) {
                     double var55 = 0.0;

                     for (int var5 = var26 + 1; var5 < var2; var5++) {
                        double[] var6 = var20.a[var5];
                        var55 -= var6[var17] * var6[var26];
                     }

                     var55 /= var44 * var39[var26];

                     for (int var28 = var26 + 1; var28 < var2; var28++) {
                        double[] var31;
                        (var31 = var20.a[var28])[var17] -= var55 * var31[var26];
                     }
                  }
               }
            }
         }
      }
   }

   private tq a() {
      if (this.d == null) {
         int var1 = this.a.length;
         int var2 = this.a[0].length;
         int var3 = this.b.length;
         int var4 = var1 >= var2 ? 0 : 1;
         double[] var12 = var1 >= var2 ? this.b : this.c;
         double[][] var5 = new double[var1][var1];

         for (int var6 = var1 - 1; var6 >= var3; var6--) {
            var5[var6][var6] = 1.0;
         }

         for (int var14 = var3 - 1; var14 >= var4; var14--) {
            double[] var13 = this.a[var14];
            var5[var14][var14] = 1.0;
            if (var13[var14 - var4] != 0.0) {
               for (int var7 = var14; var7 < var1; var7++) {
                  double var10 = 0.0;

                  for (int var8 = var14; var8 < var1; var8++) {
                     var10 -= var5[var8][var7] * this.a[var8][var14 - var4];
                  }

                  var10 /= var12[var14 - var4] * var13[var14 - var4];

                  for (int var15 = var14; var15 < var1; var15++) {
                     var5[var15][var7] = var5[var15][var7] + -var10 * this.a[var15][var14 - var4];
                  }
               }
            }
         }

         if (var4 > 0) {
            var5[0][0] = 1.0;
         }

         this.d = td.a(var5);
      }

      return this.d;
   }

   private tq b() {
      if (this.e == null) {
         int var1 = this.a.length;
         int var2 = this.a[0].length;
         double[][] var3 = new double[var1][var2];

         for (int var4 = 0; var4 < this.b.length; var4++) {
            var3[var4][var4] = this.b[var4];
            if (var1 < var2) {
               if (var4 > 0) {
                  var3[var4][var4 - 1] = this.c[var4 - 1];
               }
            } else if (var4 < this.b.length - 1) {
               var3[var4][var4 + 1] = this.c[var4];
            }
         }

         this.e = td.a(var3);
      }

      return this.e;
   }

   private tq c() {
      if (this.f == null) {
         int var1 = this.a.length;
         int var2 = this.a[0].length;
         int var3 = this.b.length;
         int var4 = var1 >= var2 ? 1 : 0;
         double[] var12 = var1 >= var2 ? this.c : this.b;
         double[][] var5 = new double[var2][var2];

         for (int var6 = var2 - 1; var6 >= var3; var6--) {
            var5[var6][var6] = 1.0;
         }

         for (int var14 = var3 - 1; var14 >= var4; var14--) {
            double[] var13 = this.a[var14 - var4];
            var5[var14][var14] = 1.0;
            if (var13[var14] != 0.0) {
               for (int var7 = var14; var7 < var2; var7++) {
                  double var10 = 0.0;

                  for (int var8 = var14; var8 < var2; var8++) {
                     var10 -= var5[var8][var7] * var13[var8];
                  }

                  var10 /= var12[var14 - var4] * var13[var14];

                  for (int var15 = var14; var15 < var2; var15++) {
                     var5[var15][var7] = var5[var15][var7] + -var10 * var13[var15];
                  }
               }
            }
         }

         if (var4 > 0) {
            var5[0][0] = 1.0;
         }

         this.f = td.a(var5);
      }

      return this.f;
   }

   private double[][] d() {
      return this.a;
   }

   private double[] e() {
      return this.b;
   }

   private double[] f() {
      return this.c;
   }

   private boolean g() {
      return this.a.length >= this.a[0].length;
   }

   private void h() {
      int var1 = this.a.length;
      int var2 = this.a[0].length;

      for (int var3 = 0; var3 < var2; var3++) {
         double var4 = 0.0;

         for (int var6 = var3; var6 < var1; var6++) {
            double var7 = this.a[var6][var3];
            var4 += var7 * var7;
         }

         double[] var20;
         double var21 = (var20 = this.a[var3])[var3] > 0.0 ? -BY.a(var4) : BY.a(var4);
         this.b[var3] = var21;
         if (var21 != 0.0) {
            var20[var3] -= var21;

            for (int var9 = var3 + 1; var9 < var2; var9++) {
               double var10 = 0.0;

               for (int var15 = var3; var15 < var1; var15++) {
                  double[] var13 = this.a[var15];
                  var10 -= var13[var9] * var13[var3];
               }

               var10 /= var21 * this.a[var3][var3];

               for (int var16 = var3; var16 < var1; var16++) {
                  double[] var26;
                  (var26 = this.a[var16])[var9] -= var10 * var26[var3];
               }
            }
         }

         if (var3 < var2 - 1) {
            var4 = 0.0;

            for (int var22 = var3 + 1; var22 < var2; var22++) {
               double var25 = var20[var22];
               var4 += var25 * var25;
            }

            double var23 = var20[var3 + 1] > 0.0 ? -BY.a(var4) : BY.a(var4);
            this.c[var3] = var23;
            if (var23 != 0.0) {
               var20[var3 + 1] = var20[var3 + 1] - var23;

               for (int var11 = var3 + 1; var11 < var1; var11++) {
                  double[] var18 = this.a[var11];
                  double var27 = 0.0;

                  for (int var5 = var3 + 1; var5 < var2; var5++) {
                     var27 -= var18[var5] * var20[var5];
                  }

                  var27 /= var23 * var20[var3 + 1];

                  for (int var19 = var3 + 1; var19 < var2; var19++) {
                     var18[var19] -= var27 * var20[var19];
                  }
               }
            }
         }
      }
   }

   private void i() {
      int var1 = this.a.length;
      int var2 = this.a[0].length;

      for (int var3 = 0; var3 < var1; var3++) {
         double[] var4 = this.a[var3];
         double var5 = 0.0;

         for (int var7 = var3; var7 < var2; var7++) {
            double var8 = var4[var7];
            var5 += var8 * var8;
         }

         double var20 = var4[var3] > 0.0 ? -BY.a(var5) : BY.a(var5);
         this.b[var3] = var20;
         if (var20 != 0.0) {
            var4[var3] -= var20;

            for (int var9 = var3 + 1; var9 < var1; var9++) {
               double[] var10 = this.a[var9];
               double var11 = 0.0;

               for (int var13 = var3; var13 < var2; var13++) {
                  var11 -= var10[var13] * var4[var13];
               }

               var11 /= var20 * this.a[var3][var3];

               for (int var26 = var3; var26 < var2; var26++) {
                  var10[var26] -= var11 * var4[var26];
               }
            }
         }

         if (var3 < var1 - 1) {
            double[] var21 = this.a[var3 + 1];
            var5 = 0.0;

            for (int var22 = var3 + 1; var22 < var1; var22++) {
               double var25 = this.a[var22][var3];
               var5 += var25 * var25;
            }

            double var23 = var21[var3] > 0.0 ? -BY.a(var5) : BY.a(var5);
            this.c[var3] = var23;
            if (var23 != 0.0) {
               var21[var3] -= var23;

               for (int var12 = var3 + 1; var12 < var2; var12++) {
                  double var27 = 0.0;

                  for (int var15 = var3 + 1; var15 < var1; var15++) {
                     double[] var18 = this.a[var15];
                     var27 -= var18[var12] * var18[var3];
                  }

                  var27 /= var23 * var21[var3];

                  for (int var16 = var3 + 1; var16 < var1; var16++) {
                     double[] var19;
                     (var19 = this.a[var16])[var12] -= var27 * var19[var3];
                  }
               }
            }
         }
      }
   }
}
