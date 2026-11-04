package org.valkyrienskies.core.impl.shadow;

public final class vV extends vx {
   private static final String f = "Gragg-Bulirsch-Stoer";
   private int g;
   private int[] m;
   private int[] n;
   private double[] o;
   private double[] p;
   private double[][] q;
   private boolean r;
   private int s;
   private int t;
   private double u;
   private double v;
   private double w;
   private double x;
   private double y;
   private double z;
   private double A;
   private boolean B;
   private int C;

   private vV(double var1, double var3, double var5, double var7) {
      super("Gragg-Bulirsch-Stoer", var1, var3, var5, var7);
      this.a(true, -1, -1, -1.0);
      this.a(-1.0, -1.0, -1.0, -1.0);
      this.a(-1, -1.0, -1.0);
      this.a(true, -1);
   }

   private vV(double var1, double var3, double[] var5, double[] var6) {
      super("Gragg-Bulirsch-Stoer", var1, var3, var5, var6);
      this.a(true, -1, -1, -1.0);
      this.a(-1.0, -1.0, -1.0, -1.0);
      this.a(-1, -1.0, -1.0);
      this.a(true, -1);
   }

   private void a(boolean var1, int var2, int var3, double var4) {
      this.r = true;
      this.t = 2;
      this.s = 1;
      this.u = 0.5;
   }

   private void a(double var1, double var3, double var5, double var7) {
      this.v = 0.65;
      this.w = 0.94;
      this.x = 0.02;
      this.y = 4.0;
   }

   private void a(int var1, double var2, double var4) {
      this.g = 18;
      this.z = 0.8;
      this.A = 0.9;
      this.n();
   }

   @Override
   public final void a(wA var1) {
      super.a(var1);
      this.n();
   }

   @Override
   public final void a(vh var1, double var2, double var4, int var6, nO var7) {
      super.a(var1, var2, var4, var6, var7);
      this.n();
   }

   private void n() {
      int var1 = this.g / 2;
      if (this.m == null || this.m.length != var1) {
         this.m = new int[var1];
         this.n = new int[var1];
         this.q = new double[var1][];
         this.o = new double[var1];
         this.p = new double[var1];
      }

      for (int var2 = 0; var2 < var1; var2++) {
         this.m[var2] = 4 * var2 + 2;
      }

      this.n[0] = this.m[0] + 1;

      for (int var6 = 1; var6 < var1; var6++) {
         this.n[var6] = this.n[var6 - 1] + this.m[var6];
      }

      for (int var7 = 0; var7 < var1; var7++) {
         this.q[var7] = var7 > 0 ? new double[var7] : null;

         for (int var3 = 0; var3 < var7; var3++) {
            double var4 = (double)this.m[var7] / this.m[var7 - var3 - 1];
            this.q[var7][var3] = 1.0 / (var4 * var4 - 1.0);
         }
      }
   }

   private void a(boolean var1, int var2) {
      this.B = true;
      this.C = 4;
   }

   private void a(double[] var1, double[] var2, double[] var3) {
      if (super.j == null) {
         for (int var7 = 0; var7 < var3.length; var7++) {
            double var8 = BY.e(BY.w(var1[var7]), BY.w(var2[var7]));
            var3[var7] = super.h + super.i * var8;
         }
      } else {
         for (int var4 = 0; var4 < var3.length; var4++) {
            double var5 = BY.e(BY.w(var1[var4]), BY.w(var2[var4]));
            var3[var4] = super.j[var4] + super.k[var4] * var5;
         }
      }
   }

   private boolean a(double var1, double[] var3, double var4, int var6, double[] var7, double[][] var8, double[] var9, double[] var10, double[] var11) {
      int var12 = this.m[var6];
      double var13 = var4 / var12;
      double var15 = var13 * 2.0;
      double var17 = var1 + var13;

      for (int var27 = 0; var27 < var3.length; var27++) {
         var11[var27] = var3[var27];
         var10[var27] = var3[var27] + var13 * var8[0][var27];
      }

      this.a(var17, var10, var8[1]);

      for (int var28 = 1; var28 < var12; var28++) {
         if (2 * var28 == var12) {
            System.arraycopy(var10, 0, var9, 0, var3.length);
         }

         var17 += var13;

         for (int var20 = 0; var20 < var3.length; var20++) {
            double var21 = var10[var20];
            var10[var20] = var11[var20] + var15 * var8[var28][var20];
            var11[var20] = var21;
         }

         this.a(var17, var10, var8[var28 + 1]);
         if (this.r && var28 <= this.s && var6 < this.t) {
            double var30 = 0.0;

            for (int var22 = 0; var22 < var7.length; var22++) {
               double var23 = var8[0][var22] / var7[var22];
               var30 += var23 * var23;
            }

            double var31 = 0.0;

            for (int var24 = 0; var24 < var7.length; var24++) {
               double var25 = (var8[var28 + 1][var24] - var8[0][var24]) / var7[var24];
               var31 += var25 * var25;
            }

            if (var31 > 4.0 * BY.e(1.0E-15, var30)) {
               return false;
            }
         }
      }

      for (int var29 = 0; var29 < var3.length; var29++) {
         var10[var29] = 0.5 * (var11[var29] + var10[var29] + var13 * var8[var12][var29]);
      }

      return true;
   }

   private void a(int var1, int var2, double[][] var3, double[] var4) {
      for (int var5 = 1; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var4.length; var6++) {
            var3[var2 - var5 - 1][var6] = var3[var2 - var5][var6] + this.q[var2 + var1][var5 - 1] * (var3[var2 - var5][var6] - var3[var2 - var5 - 1][var6]);
         }
      }

      for (int var7 = 0; var7 < var4.length; var7++) {
         var4[var7] = var3[0][var7] + this.q[var2 + var1][var2 - 1] * (var3[0][var7] - var4[var7]);
      }
   }

   @Override
   public final void a(uF var1, double var2) {
      this.b(var1, var2);
      this.a(var1);
      boolean var4 = var2 > var1.c;
      double[] var5;
      double[] var6;
      double[] var7 = new double[(var6 = (double[])(var5 = var1.g()).clone()).length];
      double[] var8 = new double[var6.length];
      double[] var9 = new double[var6.length];
      double[] var10 = new double[var6.length];
      double[][] var11 = new double[this.m.length - 1][];
      double[][] var12 = new double[this.m.length - 1][];

      for (int var13 = 0; var13 < this.m.length - 1; var13++) {
         var11[var13] = new double[var6.length];
         var12[var13] = new double[var6.length];
      }

      double[][][] var78 = new double[this.m.length][][];

      for (int var14 = 0; var14 < this.m.length; var14++) {
         var78[var14] = new double[this.m[var14] + 1][];
         var78[var14][0] = var7;

         for (int var15 = 0; var15 < this.m[var14]; var15++) {
            var78[var14][var15 + 1] = new double[var5.length];
         }
      }

      if (var6 != var5) {
         System.arraycopy(var5, 0, var6, 0, var5.length);
      }

      double[] var79 = new double[var5.length];
      double[][] var80 = new double[1 + 2 * this.m.length][var5.length];
      double[] var16 = new double[super.l];
      this.a(var6, var6, var16);
      double var17 = super.k == null ? super.i : super.k[0];
      double var19 = BY.m(BY.e(1.0E-10, var17));
      int var81 = BY.b(1, BY.a(this.m.length - 2, (int)BY.y(0.5 - var19 * 0.6)));
      vW var18;
      (var18 = new vW(var6, var7, var8, var79, var80, var4, var1.b, var1.d())).a(var1.c);
      super.a = var1.c;
      double var23 = 0.0;
      double var25 = Double.MAX_VALUE;
      boolean var82 = false;
      boolean var20 = true;
      boolean var21 = true;
      boolean var22 = false;
      this.a(var1.c, var5, var2);
      this.o[0] = 0.0;
      super.c = false;

      do {
         boolean var27 = false;
         if (var21) {
            var18.c();
            if (!var22) {
               this.a(super.a, var6, var7);
            }

            if (var20) {
               var23 = this.a(var4, 2 * var81 + 1, var16, super.a, var6, var7, var9, var10);
            }

            var21 = false;
         }

         super.b = var23;
         if (var4 && super.a + super.b > var2 || !var4 && super.a + super.b < var2) {
            super.b = var2 - super.a;
         }

         double var34 = super.a + super.b;
         super.c = var4 ? var34 >= var2 : var34 <= var2;
         int var28 = -1;
         boolean var37 = true;

         while (var37) {
            double[][] var10006 = var78[++var28];
            double[] var10007 = var28 == 0 ? var80[0] : var11[var28 - 1];
            double[] var10008 = var28 == 0 ? var8 : var12[var28 - 1];
            double[] var89 = var9;
            double[] var33 = var10008;
            double[] var32 = var10007;
            double[][] var31 = var10006;
            double[] var30 = var16;
            int var54 = var28;
            double var52 = super.b;
            double[] var51 = var6;
            double var49 = super.a;
            vV var29 = this;
            int var35 = this.m[var54];
            double var61 = var52 / var35;
            double var63 = var61 * 2.0;
            double var65 = var49 + var61;

            for (int var67 = 0; var67 < var51.length; var67++) {
               var89[var67] = var51[var67];
               var33[var67] = var51[var67] + var61 * var31[0][var67];
            }

            var29.a(var65, var33, var31[1]);
            int var109 = 1;

            boolean var10000;
            while (true) {
               if (var109 >= var35) {
                  for (int var110 = 0; var110 < var51.length; var110++) {
                     var33[var110] = 0.5 * (var89[var110] + var33[var110] + var61 * var31[var35][var110]);
                  }

                  var10000 = true;
                  break;
               }

               if (2 * var109 == var35) {
                  System.arraycopy(var33, 0, var32, 0, var51.length);
               }

               var65 += var61;

               for (int var68 = 0; var68 < var51.length; var68++) {
                  double var69 = var33[var68];
                  var33[var68] = var89[var68] + var63 * var31[var109][var68];
                  var89[var68] = var69;
               }

               var29.a(var65, var33, var31[var109 + 1]);
               if (var29.r && var109 <= var29.s && var54 < var29.t) {
                  double var111 = 0.0;

                  for (int var70 = 0; var70 < var30.length; var70++) {
                     double var71 = var31[0][var70] / var30[var70];
                     var111 += var71 * var71;
                  }

                  double var112 = 0.0;

                  for (int var72 = 0; var72 < var30.length; var72++) {
                     double var73 = (var31[var109 + 1][var72] - var31[0][var72]) / var30[var72];
                     var112 += var73 * var73;
                  }

                  if (var112 > 4.0 * BY.e(1.0E-15, var111)) {
                     var10000 = false;
                     break;
                  }
               }

               var109++;
            }

            if (!var10000) {
               var23 = BY.w(this.a(super.b * this.u, var4, false));
               var27 = true;
               var37 = false;
            } else if (var28 > 0) {
               this.a(0, var28, var12, var8);
               this.a(var6, var8, var16);
               double var87 = 0.0;

               for (int var38 = 0; var38 < super.l; var38++) {
                  double var39 = BY.w(var8[var38] - var12[0][var38]) / var16[var38];
                  var87 += var39 * var39;
               }

               double var88;
               if (!((var88 = BY.a(var87 / super.l)) > 1.0E15) && (var28 <= 1 || !(var88 > var25))) {
                  var25 = BY.e(var88 * 4.0, 1.0);
                  double var91 = 1.0 / (2 * var28 + 1);
                  double var40 = this.w / BY.a(var88 / this.v, var91);
                  double var42;
                  var40 = BY.e((var42 = BY.a(this.x, var91)) / this.y, BY.d(1.0 / var42, var40));
                  this.p[var28] = BY.w(this.a(super.b * var40, var4, true));
                  this.o[var28] = this.n[var28] / this.p[var28];
                  switch (var28 - var81) {
                     case -1:
                        if (var81 > 1 && !var82) {
                           if (var88 <= 1.0) {
                              var37 = false;
                           } else {
                              double var101 = (double)this.m[var81] * this.m[var81 + 1] / (this.m[0] * this.m[0]);
                              if (var88 > var101 * var101) {
                                 var27 = true;
                                 var37 = false;
                                 var81 = var28;
                                 if (var28 > 1 && this.o[var81 - 1] < this.z * this.o[var81]) {
                                    var81--;
                                 }

                                 var23 = this.p[var81];
                              }
                           }
                        }
                        break;
                     case 0:
                        if (var88 <= 1.0) {
                           var37 = false;
                        } else {
                           double var44 = (double)this.m[var28 + 1] / this.m[0];
                           if (var88 > var44 * var44) {
                              var27 = true;
                              var37 = false;
                              if (var81 > 1 && this.o[var81 - 1] < this.z * this.o[var81]) {
                                 var81--;
                              }

                              var23 = this.p[var81];
                           }
                        }
                        break;
                     case 1:
                        if (var88 > 1.0) {
                           var27 = true;
                           if (var81 > 1 && this.o[var81 - 1] < this.z * this.o[var81]) {
                              var81--;
                           }

                           var23 = this.p[var81];
                        }

                        var37 = false;
                        break;
                     default:
                        if ((var20 || super.c) && var88 <= 1.0) {
                           var37 = false;
                        }
                  }
               } else {
                  var23 = BY.w(this.a(super.b * this.u, var4, false));
                  var27 = true;
                  var37 = false;
               }
            }
         }

         if (!var27) {
            this.a(super.a + super.b, var8, var79);
         }

         double var90 = this.m();
         if (!var27) {
            for (int var92 = 1; var92 <= var28; var92++) {
               this.a(0, var92, var11, var80[0]);
            }

            int var93 = 2 * var28 - this.C + 3;

            for (int var96 = 0; var96 < var93; var96++) {
               int var41 = var96 / 2;
               double var99 = BY.a(0.5 * this.m[var41], var96);
               int var102 = var78[var41].length / 2;

               for (int var45 = 0; var45 < var5.length; var45++) {
                  var80[var96 + 1][var45] = var99 * var78[var41][var102 + var96][var45];
               }

               for (int var104 = 1; var104 <= var28 - var41; var104++) {
                  var99 = BY.a(0.5 * this.m[var104 + var41], var96);
                  var102 = var78[var41 + var104].length / 2;

                  for (int var83 = 0; var83 < var5.length; var83++) {
                     var11[var104 - 1][var83] = var99 * var78[var41 + var104][var102 + var96][var83];
                  }

                  this.a(var41, var104, var11, var80[var96 + 1]);
               }

               for (int var105 = 0; var105 < var5.length; var105++) {
                  var80[var96 + 1][var105] = var80[var96 + 1][var105] * super.b;
               }

               for (int var106 = (var96 + 1) / 2; var106 <= var28; var106++) {
                  for (int var84 = var78[var106].length - 1; var84 >= 2 * (var96 + 1); var84--) {
                     for (int var85 = 0; var85 < var5.length; var85++) {
                        var78[var106][var84][var85] = var78[var106][var84][var85] - var78[var106][var84 - 2][var85];
                     }
                  }
               }
            }

            if (var93 >= 0) {
               vW var97;
               (var97 = var18).a(var93, super.b);
               if (this.B) {
                  double[] var107 = var16;
                  vW var86 = var97;
                  double var50 = 0.0;
                  if (var86.c >= 5) {
                     for (int var108 = 0; var108 < var107.length; var108++) {
                        double var53 = var86.a[var86.c][var108] / var107[var108];
                        var50 += var53 * var53;
                     }

                     var50 = BY.a(var50 / var107.length) * var86.b[var86.c - 5];
                  }

                  double var98 = var50;
                  var90 = BY.w(super.b / BY.e(BY.a(var98, 1.0 / (var93 + 4)), 0.01));
                  if (var98 > 10.0) {
                     var23 = var90;
                     var27 = true;
                  }
               }
            }
         }

         if (!var27) {
            var18.a(super.a + super.b);
            super.a = this.a(var18, var8, var79, var2);
            var18.a(super.a);
            System.arraycopy(var8, 0, var6, 0, var5.length);
            System.arraycopy(var79, 0, var7, 0, var5.length);
            var22 = true;
            int var94;
            if (var28 == 1) {
               var94 = 2;
               if (var82) {
                  var94 = 1;
               }
            } else if (var28 <= var81) {
               var94 = var28;
               if (this.o[var28 - 1] < this.z * this.o[var28]) {
                  var94 = var28 - 1;
               } else if (this.o[var28] < this.A * this.o[var28 - 1]) {
                  var94 = BY.a(var28 + 1, this.m.length - 2);
               }
            } else {
               var94 = var28 - 1;
               if (var28 > 2 && this.o[var28 - 2] < this.z * this.o[var28 - 1]) {
                  var94 = var28 - 2;
               }

               if (this.o[var28] < this.A * this.o[var94]) {
                  var94 = BY.a(var28, this.m.length - 2);
               }
            }

            if (var82) {
               var81 = BY.a(var94, var28);
               var23 = BY.d(BY.w(super.b), this.p[var81]);
            } else {
               if (var94 <= var28) {
                  var23 = this.p[var94];
               } else if (var28 < var81 && this.o[var28] < this.A * this.o[var28 - 1]) {
                  var23 = this.a(this.p[var28] * this.n[var94 + 1] / this.n[var28], var4, false);
               } else {
                  var23 = this.a(this.p[var28] * this.n[var94] / this.n[var28], var4, false);
               }

               var81 = var94;
            }

            var21 = true;
         }

         var23 = BY.d(var23, var90);
         if (!var4) {
            var23 = -var23;
         }

         var20 = false;
         if (var27) {
            super.c = false;
            var82 = true;
         } else {
            var82 = false;
         }
      } while (!super.c);

      double var76 = super.a;
      var1.c = var76;
      var1.b(var6);
      this.l();
   }
}
