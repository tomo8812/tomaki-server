package org.valkyrienskies.core.impl.shadow;

public final class tC {
   private static final double b = 2.220446E-16F;
   private static final double c = 1.6033346880071782E-291;
   final double[] a;
   private final int d;
   private final int e;
   private final boolean f;
   private final tq g;
   private tq h;
   private tq i;
   private final tq j;
   private tq k;
   private final double l;

   public tC(tq var1) {
      double[][] var2;
      if (var1.f() < var1.g()) {
         this.f = true;
         var2 = var1.d().a();
         this.d = var1.g();
         this.e = var1.f();
      } else {
         this.f = false;
         var2 = var1.a();
         this.d = var1.f();
         this.e = var1.g();
      }

      this.a = new double[this.e];
      double[][] var44 = new double[this.d][this.e];
      double[][] var3 = new double[this.e][this.e];
      double[] var4 = new double[this.e];
      double[] var5 = new double[this.d];
      int var6 = BY.a(this.d - 1, this.e);
      int var7 = BY.b(0, this.e - 2);

      for (int var8 = 0; var8 < BY.b(var6, var7); var8++) {
         if (var8 < var6) {
            this.a[var8] = 0.0;

            for (int var9 = var8; var9 < this.d; var9++) {
               this.a[var8] = BY.f(this.a[var8], var2[var9][var8]);
            }

            if (this.a[var8] != 0.0) {
               if (var2[var8][var8] < 0.0) {
                  this.a[var8] = -this.a[var8];
               }

               for (int var49 = var8; var49 < this.d; var49++) {
                  var2[var49][var8] = var2[var49][var8] / this.a[var8];
               }

               var2[var8][var8]++;
            }

            this.a[var8] = -this.a[var8];
         }

         for (int var50 = var8 + 1; var50 < this.e; var50++) {
            if (var8 < var6 && this.a[var8] != 0.0) {
               double var11 = 0.0;

               for (int var13 = var8; var13 < this.d; var13++) {
                  var11 += var2[var13][var8] * var2[var13][var50];
               }

               var11 = -var11 / var2[var8][var8];

               for (int var78 = var8; var78 < this.d; var78++) {
                  var2[var78][var50] = var2[var78][var50] + var11 * var2[var78][var8];
               }
            }

            var4[var50] = var2[var8][var50];
         }

         if (var8 < var6) {
            for (int var51 = var8; var51 < this.d; var51++) {
               var44[var51][var8] = var2[var51][var8];
            }
         }

         if (var8 < var7) {
            var4[var8] = 0.0;

            for (int var52 = var8 + 1; var52 < this.e; var52++) {
               var4[var8] = BY.f(var4[var8], var4[var52]);
            }

            if (var4[var8] != 0.0) {
               if (var4[var8 + 1] < 0.0) {
                  var4[var8] = -var4[var8];
               }

               for (int var53 = var8 + 1; var53 < this.e; var53++) {
                  var4[var53] /= var4[var8];
               }

               var4[var8 + 1]++;
            }

            var4[var8] = -var4[var8];
            if (var8 + 1 < this.d && var4[var8] != 0.0) {
               for (int var54 = var8 + 1; var54 < this.d; var54++) {
                  var5[var54] = 0.0;
               }

               for (int var55 = var8 + 1; var55 < this.e; var55++) {
                  for (int var63 = var8 + 1; var63 < this.d; var63++) {
                     var5[var63] += var4[var55] * var2[var63][var55];
                  }
               }

               for (int var56 = var8 + 1; var56 < this.e; var56++) {
                  double var64 = -var4[var56] / var4[var8 + 1];

                  for (int var79 = var8 + 1; var79 < this.d; var79++) {
                     var2[var79][var56] = var2[var79][var56] + var64 * var5[var79];
                  }
               }
            }

            for (int var57 = var8 + 1; var57 < this.e; var57++) {
               var3[var57][var8] = var4[var57];
            }
         }
      }

      int var48 = this.e;
      if (var6 < this.e) {
         this.a[var6] = var2[var6][var6];
      }

      if (this.d < var48) {
         this.a[var48 - 1] = 0.0;
      }

      if (var7 + 1 < var48) {
         var4[var7] = var2[var7][var48 - 1];
      }

      var4[var48 - 1] = 0.0;

      for (int var58 = var6; var58 < this.e; var58++) {
         for (int var65 = 0; var65 < this.d; var65++) {
            var44[var65][var58] = 0.0;
         }

         var44[var58][var58] = 1.0;
      }

      for (int var59 = var6 - 1; var59 >= 0; var59--) {
         if (this.a[var59] != 0.0) {
            for (int var67 = var59 + 1; var67 < this.e; var67++) {
               double var12 = 0.0;

               for (int var14 = var59; var14 < this.d; var14++) {
                  var12 += var44[var14][var59] * var44[var14][var67];
               }

               var12 = -var12 / var44[var59][var59];

               for (int var88 = var59; var88 < this.d; var88++) {
                  var44[var88][var67] = var44[var88][var67] + var12 * var44[var88][var59];
               }
            }

            for (int var68 = var59; var68 < this.d; var68++) {
               var44[var68][var59] = -var44[var68][var59];
            }

            var44[var59][var59]++;

            for (int var69 = 0; var69 < var59 - 1; var69++) {
               var44[var69][var59] = 0.0;
            }
         } else {
            for (int var66 = 0; var66 < this.d; var66++) {
               var44[var66][var59] = 0.0;
            }

            var44[var59][var59] = 1.0;
         }
      }

      for (int var60 = this.e - 1; var60 >= 0; var60--) {
         if (var60 < var7 && var4[var60] != 0.0) {
            for (int var70 = var60 + 1; var70 < this.e; var70++) {
               double var75 = 0.0;

               for (int var89 = var60 + 1; var89 < this.e; var89++) {
                  var75 += var3[var89][var60] * var3[var89][var70];
               }

               var75 = -var75 / var3[var60 + 1][var60];

               for (int var90 = var60 + 1; var90 < this.e; var90++) {
                  var3[var90][var70] = var3[var90][var70] + var75 * var3[var90][var60];
               }
            }
         }

         for (int var71 = 0; var71 < this.e; var71++) {
            var3[var71][var60] = 0.0;
         }

         var3[var60][var60] = 1.0;
      }

      int var61 = var48 - 1;

      label334:
      while (var48 > 0) {
         int var72;
         for (var72 = var48 - 2; var72 >= 0; var72--) {
            double var80 = 1.6033346880071782E-291 + 2.220446E-16F * (BY.w(this.a[var72]) + BY.w(this.a[var72 + 1]));
            if (!(BY.w(var4[var72]) > var80)) {
               var4[var72] = 0.0;
               break;
            }
         }

         byte var77;
         if (var72 == var48 - 2) {
            var77 = 4;
         } else {
            int var81;
            for (var81 = var48 - 1; var81 >= var72 && var81 != var72; var81--) {
               double var91 = (var81 != var48 ? BY.w(var4[var81]) : 0.0) + (var81 != var72 + 1 ? BY.w(var4[var81 - 1]) : 0.0);
               if (BY.w(this.a[var81]) <= 1.6033346880071782E-291 + var91 * 2.220446E-16F) {
                  this.a[var81] = 0.0;
                  break;
               }
            }

            if (var81 == var72) {
               var77 = 3;
            } else if (var81 == var48 - 1) {
               var77 = 1;
            } else {
               var77 = 2;
               var72 = var81;
            }
         }

         var72++;
         switch (var77) {
            case 1:
               double var87 = var4[var48 - 2];
               var4[var48 - 2] = 0.0;
               int var95 = var48 - 2;

               while (true) {
                  if (var95 < var72) {
                     continue label334;
                  }

                  double var97 = BY.f(this.a[var95], var87);
                  double var99 = this.a[var95] / var97;
                  double var100 = var87 / var97;
                  this.a[var95] = var97;
                  if (var95 != var72) {
                     var87 = -var100 * var4[var95 - 1];
                     var4[var95 - 1] = var99 * var4[var95 - 1];
                  }

                  for (int var101 = 0; var101 < this.e; var101++) {
                     var97 = var99 * var3[var101][var95] + var100 * var3[var101][var48 - 1];
                     var3[var101][var48 - 1] = -var100 * var3[var101][var95] + var99 * var3[var101][var48 - 1];
                     var3[var101][var95] = var97;
                  }

                  var95--;
               }
            case 2:
               double var86 = var4[var72 - 1];
               var4[var72 - 1] = 0.0;
               int var94 = var72;

               while (true) {
                  if (var94 >= var48) {
                     continue label334;
                  }

                  double var16 = BY.f(this.a[var94], var86);
                  double var18 = this.a[var94] / var16;
                  double var20 = var86 / var16;
                  this.a[var94] = var16;
                  var86 = -var20 * var4[var94];
                  var4[var94] = var18 * var4[var94];

                  for (int var22 = 0; var22 < this.d; var22++) {
                     var16 = var18 * var44[var22][var94] + var20 * var44[var22][var72 - 1];
                     var44[var22][var72 - 1] = -var20 * var44[var22][var94] + var18 * var44[var22][var72 - 1];
                     var44[var22][var94] = var16;
                  }

                  var94++;
               }
            case 3:
               double var93 = BY.e(
                  BY.e(BY.e(BY.e(BY.w(this.a[var48 - 1]), BY.w(this.a[var48 - 2])), BY.w(var4[var48 - 2])), BY.w(this.a[var72])), BY.w(var4[var72])
               );
               double var17 = this.a[var48 - 1] / var93;
               double var19 = this.a[var48 - 2] / var93;
               double var21 = var4[var48 - 2] / var93;
               double var23 = this.a[var72] / var93;
               double var25 = var4[var72] / var93;
               double var27 = ((var19 + var17) * (var19 - var17) + var21 * var21) / 2.0;
               double var29 = var17 * var21 * (var17 * var21);
               double var31 = 0.0;
               if (var27 != 0.0 || var29 != 0.0) {
                  var31 = BY.a(var27 * var27 + var29);
                  if (var27 < 0.0) {
                     var31 = -var31;
                  }

                  var31 = var29 / (var27 + var31);
               }

               double var33 = (var23 + var17) * (var23 - var17) + var31;
               double var35 = var23 * var25;

               for (int var45 = var72; var45 < var48 - 1; var45++) {
                  double var38 = BY.f(var33, var35);
                  double var40 = var33 / var38;
                  double var42 = var35 / var38;
                  if (var45 != var72) {
                     var4[var45 - 1] = var38;
                  }

                  var33 = var40 * this.a[var45] + var42 * var4[var45];
                  var4[var45] = var40 * var4[var45] - var42 * this.a[var45];
                  var35 = var42 * this.a[var45 + 1];
                  this.a[var45 + 1] = var40 * this.a[var45 + 1];

                  for (int var46 = 0; var46 < this.e; var46++) {
                     var38 = var40 * var3[var46][var45] + var42 * var3[var46][var45 + 1];
                     var3[var46][var45 + 1] = -var42 * var3[var46][var45] + var40 * var3[var46][var45 + 1];
                     var3[var46][var45] = var38;
                  }

                  var38 = BY.f(var33, var35);
                  var40 = var33 / var38;
                  var42 = var35 / var38;
                  this.a[var45] = var38;
                  var33 = var40 * var4[var45] + var42 * this.a[var45 + 1];
                  this.a[var45 + 1] = -var42 * var4[var45] + var40 * this.a[var45 + 1];
                  var35 = var42 * var4[var45 + 1];
                  var4[var45 + 1] = var40 * var4[var45 + 1];
                  if (var45 < this.d - 1) {
                     for (int var47 = 0; var47 < this.d; var47++) {
                        var38 = var40 * var44[var47][var45] + var42 * var44[var47][var45 + 1];
                        var44[var47][var45 + 1] = -var42 * var44[var47][var45] + var40 * var44[var47][var45 + 1];
                        var44[var47][var45] = var38;
                     }
                  }
               }

               var4[var48 - 2] = var33;
               continue;
         }

         if (this.a[var72] <= 0.0) {
            this.a[var72] = this.a[var72] < 0.0 ? -this.a[var72] : 0.0;

            for (int var82 = 0; var82 <= var61; var82++) {
               var3[var82][var72] = -var3[var82][var72];
            }
         }

         for (; var72 < var61 && !(this.a[var72] >= this.a[var72 + 1]); var72++) {
            double var83 = this.a[var72];
            this.a[var72] = this.a[var72 + 1];
            this.a[var72 + 1] = var83;
            if (var72 < this.e - 1) {
               for (int var15 = 0; var15 < this.e; var15++) {
                  var83 = var3[var15][var72 + 1];
                  var3[var15][var72 + 1] = var3[var15][var72];
                  var3[var15][var72] = var83;
               }
            }

            if (var72 < this.d - 1) {
               for (int var92 = 0; var92 < this.d; var92++) {
                  var83 = var44[var92][var72 + 1];
                  var44[var92][var72 + 1] = var44[var92][var72];
                  var44[var92][var72] = var83;
               }
            }
         }

         var48--;
      }

      this.l = BY.e(this.d * this.a[0] * 2.220446E-16F, BY.a(Cq.b));
      if (!this.f) {
         this.g = td.a(var44);
         this.j = td.a(var3);
      } else {
         this.g = td.a(var3);
         this.j = td.a(var44);
      }
   }

   private tq b() {
      return this.g;
   }

   private tq c() {
      if (this.h == null) {
         this.h = this.g.d();
      }

      return this.h;
   }

   private tq d() {
      if (this.i == null) {
         this.i = td.a(this.a);
      }

      return this.i;
   }

   private double[] e() {
      return (double[])this.a.clone();
   }

   private tq f() {
      return this.j;
   }

   private tq g() {
      if (this.k == null) {
         this.k = this.j.d();
      }

      return this.k;
   }

   private tq a(double var1) {
      int var3 = this.a.length;
      int var4 = 0;

      while (var4 < var3 && this.a[var4] >= var1) {
         var4++;
      }

      if (var4 == 0) {
         throw new pm(px.TOO_LARGE_CUTOFF_SINGULAR_VALUE, var1, this.a[0], true);
      }

      final double[][] var6 = new double[var4][var3];
      tC var2 = this;
      if (this.k == null) {
         var2.k = var2.j.d();
      }

      var2.k.a(new sL() {
         @Override
         public final void a(int var1, int var2x, double var3x) {
            double[] var10000 = var6[var1];
            tC var5 = tC.this;
            var10000[var2x] = var3x / tC.this.a[var1];
         }
      }, 0, var4 - 1, 0, var3 - 1);
      sy var7;
      return (var7 = new sy(var6, false)).d().c(var7);
   }

   private double h() {
      return this.a[0];
   }

   private double i() {
      return this.a[0] / this.a[this.e - 1];
   }

   private double j() {
      return this.a[this.e - 1] / this.a[0];
   }

   private int k() {
      int var1 = 0;

      for (int var2 = 0; var2 < this.a.length; var2++) {
         if (this.a[var2] > this.l) {
            var1++;
         }
      }

      return var1;
   }

   public final tC.a a() {
      return new tC.a(this.a, this.c(), this.j, this.k() == this.d, this.l, (byte)0);
   }

   // $VF: synthetic method
   static double[] a(tC var0) {
      return var0.a;
   }

   static class a implements sG {
      private final tq a;
      private boolean b;

      private a(double[] var1, tq var2, tq var3, boolean var4, double var5) {
         double[][] var12 = var2.a();

         for (int var7 = 0; var7 < var1.length; var7++) {
            double var9;
            if (var1[var7] > var5) {
               var9 = 1.0 / var1[var7];
            } else {
               var9 = 0.0;
            }

            double[] var8 = var12[var7];

            for (int var11 = 0; var11 < var8.length; var11++) {
               var8[var11] *= var9;
            }
         }

         this.a = var3.c(new sy(var12, false));
         this.b = var4;
      }

      @Override
      public final tu a(tu var1) {
         return this.a.a(var1);
      }

      @Override
      public final tq a(tq var1) {
         return this.a.c(var1);
      }

      @Override
      public final boolean a() {
         return this.b;
      }

      @Override
      public final tq b() {
         return this.a;
      }

      // $VF: synthetic method
      a(double[] var1, tq var2, tq var3, boolean var4, double var5, byte var7) {
         this(var1, var2, var3, var4, var5);
      }
   }
}
