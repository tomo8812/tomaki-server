package org.valkyrienskies.core.impl.shadow;

public final class sN {
   private static final double d = 1.0E-12;
   private byte e = 30;
   private double[] f;
   private double[] g;
   private tI h;
   public double[] a;
   public double[] b;
   public sA[] c;
   private tq i;
   private tq j;
   private tq k;
   private final boolean l;

   public sN(tq var1) {
      double var2 = 10 * var1.f() * var1.g() * Cq.a;
      this.l = td.a(var1, var2);
      if (this.l) {
         tq var49 = var1;
         sN var42 = this;
         this.h = new tI(var49);
         tI var52 = var42.h;
         var42.f = var42.h.b;
         var52 = var42.h;
         var42.g = var42.h.c;
         tI var43 = this.h;
         if (this.h.d == null) {
            tI var50 = var43;
            if (var43.e == null) {
               int var54;
               double[][] var56 = new double[var54 = var50.a.length][var54];

               for (int var58 = var54 - 1; var58 > 0; var58--) {
                  double[] var64 = var50.a[var58 - 1];
                  var56[var58][var58] = 1.0;
                  if (var64[var58] != 0.0) {
                     double var11 = 1.0 / (var50.c[var58 - 1] * var64[var58]);
                     double var67 = 1.0 / var50.c[var58 - 1];
                     var56[var58][var58] = 1.0 + var67 * var64[var58];

                     for (int var15 = var58 + 1; var15 < var54; var15++) {
                        var56[var58][var15] = var67 * var64[var15];
                     }

                     for (int var71 = var58 + 1; var71 < var54; var71++) {
                        var67 = 0.0;

                        for (int var73 = var58 + 1; var73 < var54; var73++) {
                           var67 += var56[var71][var73] * var64[var73];
                        }

                        var67 *= var11;
                        var56[var71][var58] = var67 * var64[var58];

                        for (int var74 = var58 + 1; var74 < var54; var74++) {
                           var56[var71][var74] = var56[var71][var74] + var67 * var64[var74];
                        }
                     }
                  }
               }

               var56[0][0] = 1.0;
               var50.e = td.a(var56);
            }

            var43.d = var50.e.d();
         }

         this.a(var43.d.a());
      } else {
         tq var44 = var1;
         sN var39 = this;
         tz var3;
         double[][] var4 = (var3 = new tz(var44)).b().a();
         var39.a = new double[var4.length];
         var39.b = new double[var4.length];

         for (int var5 = 0; var5 < var39.a.length; var5++) {
            if (var5 != var39.a.length - 1 && !Cq.b(var4[var5 + 1][var5], 0.0, 1.0E-12)) {
               double var10 = var4[var5 + 1][var5 + 1];
               double var12;
               double var14 = BY.a(BY.w((var12 = 0.5 * (var4[var5][var5] - var10)) * var12 + var4[var5 + 1][var5] * var4[var5][var5 + 1]));
               var39.a[var5] = var10 + var12;
               var39.b[var5] = var14;
               var39.a[var5 + 1] = var10 + var12;
               var39.b[var5 + 1] = -var14;
               var5++;
            } else {
               var39.a[var5] = var4[var5][var5];
            }
         }

         tz var40 = var3;
         tz var45 = var40;
         sN var41 = this;
         double[][] var51 = var45.b().a();
         var4 = var45.a().a();
         int var57 = var51.length;
         double var63 = 0.0;

         for (int var65 = 0; var65 < var57; var65++) {
            for (int var13 = BY.b(var65 - 1, 0); var13 < var57; var13++) {
               var63 += BY.w(var51[var65][var13]);
            }
         }

         if (Cq.b(var63, 0.0, 1.0E-12)) {
            throw new oU(px.ZERO_NORM);
         }

         double var66 = 0.0;
         double var70 = 0.0;
         double var16 = 0.0;

         for (int var46 = var57 - 1; var46 >= 0; var46--) {
            double var19 = var41.a[var46];
            double var21;
            if (Cq.a(var21 = var41.b[var46], 0.0)) {
               int var59 = var46;
               var51[var46][var46] = 1.0;

               for (int var61 = var46 - 1; var61 >= 0; var61--) {
                  double var79 = var51[var61][var61] - var19;
                  var66 = 0.0;

                  for (int var80 = var59; var80 <= var46; var80++) {
                     var66 += var51[var61][var80] * var51[var80][var46];
                  }

                  if (Cq.a(var41.b[var61], 0.0, 1.0E-12) < 0) {
                     var16 = var79;
                     var70 = var66;
                  } else {
                     var59 = var61;
                     if (Cq.a(var41.b[var61], 0.0)) {
                        if (var79 != 0.0) {
                           var51[var61][var46] = -var66 / var79;
                        } else {
                           var51[var61][var46] = -var66 / (Cq.a * var63);
                        }
                     } else {
                        double var81 = var51[var61][var61 + 1];
                        double var84 = var51[var61 + 1][var61];
                        var21 = (var41.a[var61] - var19) * (var41.a[var61] - var19) + var41.b[var61] * var41.b[var61];
                        double var88 = (var81 * var70 - var16 * var66) / var21;
                        var51[var61][var46] = var88;
                        if (BY.w(var81) > BY.w(var16)) {
                           var51[var61 + 1][var46] = (-var66 - var79 * var88) / var81;
                        } else {
                           var51[var61 + 1][var46] = (-var70 - var84 * var88) / var16;
                        }
                     }

                     double var82 = BY.w(var51[var61][var46]);
                     if (Cq.a * var82 * var82 > 1.0) {
                        for (int var85 = var61; var85 <= var46; var85++) {
                           var51[var85][var46] = var51[var85][var46] / var82;
                        }
                     }
                  }
               }
            } else if (var21 < 0.0) {
               int var6 = var46 - 1;
               if (BY.w(var51[var46][var46 - 1]) > BY.w(var51[var46 - 1][var46])) {
                  var51[var46 - 1][var46 - 1] = var21 / var51[var46][var46 - 1];
                  var51[var46 - 1][var46] = -(var51[var46][var46] - var19) / var51[var46][var46 - 1];
               } else {
                  nQ var7 = a(0.0, -var51[var46 - 1][var46], var51[var46 - 1][var46 - 1] - var19, var21);
                  var51[var46 - 1][var46 - 1] = var7.d;
                  var51[var46 - 1][var46] = var7.c;
               }

               var51[var46][var46 - 1] = 0.0;
               var51[var46][var46] = 1.0;

               for (int var60 = var46 - 2; var60 >= 0; var60--) {
                  double var25 = 0.0;
                  double var27 = 0.0;

                  for (int var29 = var6; var29 <= var46; var29++) {
                     var25 += var51[var60][var29] * var51[var29][var46 - 1];
                     var27 += var51[var60][var29] * var51[var29][var46];
                  }

                  double var83 = var51[var60][var60] - var19;
                  if (Cq.a(var41.b[var60], 0.0, 1.0E-12) < 0) {
                     var16 = var83;
                     var66 = var25;
                     var70 = var27;
                  } else {
                     var6 = var60;
                     if (Cq.a(var41.b[var60], 0.0)) {
                        nQ var31 = a(-var25, -var27, var83, var21);
                        var51[var60][var46 - 1] = var31.d;
                        var51[var60][var46] = var31.c;
                     } else {
                        double var86 = var51[var60][var60 + 1];
                        double var33 = var51[var60 + 1][var60];
                        double var35 = (var41.a[var60] - var19) * (var41.a[var60] - var19) + var41.b[var60] * var41.b[var60] - var21 * var21;
                        double var37 = (var41.a[var60] - var19) * 2.0 * var21;
                        if (Cq.a(var35, 0.0) && Cq.a(var37, 0.0)) {
                           var35 = Cq.a * var63 * (BY.w(var83) + BY.w(var21) + BY.w(var86) + BY.w(var33) + BY.w(var16));
                        }

                        nQ var8 = a(var86 * var66 - var16 * var25 + var21 * var27, var86 * var70 - var16 * var27 - var21 * var25, var35, var37);
                        var51[var60][var46 - 1] = var8.d;
                        var51[var60][var46] = var8.c;
                        if (BY.w(var86) > BY.w(var16) + BY.w(var21)) {
                           var51[var60 + 1][var46 - 1] = (-var25 - var83 * var51[var60][var46 - 1] + var21 * var51[var60][var46]) / var86;
                           var51[var60 + 1][var46] = (-var27 - var83 * var51[var60][var46] - var21 * var51[var60][var46 - 1]) / var86;
                        } else {
                           var8 = a(-var66 - var33 * var51[var60][var46 - 1], -var70 - var33 * var51[var60][var46], var16, var21);
                           var51[var60 + 1][var46 - 1] = var8.d;
                           var51[var60 + 1][var46] = var8.c;
                        }
                     }

                     double var87 = BY.e(BY.w(var51[var60][var46 - 1]), BY.w(var51[var60][var46]));
                     if (Cq.a * var87 * var87 > 1.0) {
                        for (int var89 = var60; var89 <= var46; var89++) {
                           var51[var89][var46 - 1] = var51[var89][var46 - 1] / var87;
                           var51[var89][var46] = var51[var89][var46] / var87;
                        }
                     }
                  }
               }
            }
         }

         for (int var47 = var57 - 1; var47 >= 0; var47--) {
            for (int var75 = 0; var75 <= var57 - 1; var75++) {
               var16 = 0.0;

               for (int var20 = 0; var20 <= BY.a(var47, var57 - 1); var20++) {
                  var16 += var4[var75][var20] * var51[var20][var47];
               }

               var4[var75][var47] = var16;
            }
         }

         var41.c = new sA[var57];
         double[] var48 = new double[var57];

         for (int var76 = 0; var76 < var57; var76++) {
            for (int var77 = 0; var77 < var57; var77++) {
               var48[var77] = var4[var77][var76];
            }

            var41.c[var76] = new sA(var48);
         }
      }
   }

   @Deprecated
   private sN(tq var1, byte var2) {
      this(var1);
   }

   private sN(double[] var1, double[] var2) {
      this.l = true;
      this.f = (double[])var1.clone();
      this.g = (double[])var2.clone();
      this.h = null;
      int var4;
      double[][] var5 = new double[var4 = var1.length][var4];

      for (int var3 = 0; var3 < var4; var3++) {
         var5[var3][var3] = 1.0;
      }

      this.a(var5);
   }

   @Deprecated
   private sN(double[] var1, double[] var2, byte var3) {
      this(var1, var2);
   }

   public final tq a() {
      if (this.i == null) {
         int var1 = this.c.length;
         this.i = td.a(var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            this.i.b(var2, this.c[var2]);
         }
      }

      return this.i;
   }

   public final tq b() {
      if (this.j == null) {
         this.j = td.a(this.a);

         for (int var1 = 0; var1 < this.b.length; var1++) {
            if (Cq.a(this.b[var1], 0.0, 1.0E-12) > 0) {
               this.j.c(var1, var1 + 1, this.b[var1]);
            } else if (Cq.a(this.b[var1], 0.0, 1.0E-12) < 0) {
               this.j.c(var1, var1 - 1, this.b[var1]);
            }
         }
      }

      return this.j;
   }

   private tq g() {
      if (this.k == null) {
         int var1 = this.c.length;
         this.k = td.a(var1, var1);

         for (int var2 = 0; var2 < var1; var2++) {
            this.k.a(var2, this.c[var2]);
         }
      }

      return this.k;
   }

   private boolean h() {
      for (int var1 = 0; var1 < this.b.length; var1++) {
         if (!Cq.b(this.b[var1], 0.0, 1.0E-12)) {
            return true;
         }
      }

      return false;
   }

   public final double[] c() {
      return (double[])this.a.clone();
   }

   private double b(int var1) {
      return this.a[var1];
   }

   private double[] i() {
      return (double[])this.b.clone();
   }

   private double c(int var1) {
      return this.b[var1];
   }

   public final sA a(int var1) {
      return this.c[var1].a();
   }

   public final double d() {
      double var1 = 1.0;
      double[] var3 = this.a;
      int var4 = this.a.length;

      for (int var5 = 0; var5 < var4; var5++) {
         double var6 = var3[var5];
         var1 *= var6;
      }

      return var1;
   }

   public final tq e() {
      if (!this.l) {
         throw new pb();
      }

      double[] var1 = new double[this.a.length];

      for (int var2 = 0; var2 < this.a.length; var2++) {
         double var3;
         if ((var3 = this.a[var2]) <= 0.0) {
            throw new pb();
         }

         var1[var2] = BY.a(var3);
      }

      tq var5 = td.a(var1);
      tq var6 = this.a();
      tq var4 = this.g();
      return var6.c(var5).c(var4);
   }

   public final sN.a f() {
      if (this.h()) {
         throw new pb();
      } else {
         return new sN.a(this.a, this.b, this.c, (byte)0);
      }
   }

   private void a(tq var1) {
      this.h = new tI(var1);
      tI var2 = this.h;
      this.f = this.h.b;
      tI var3 = this.h;
      this.g = this.h.c;
   }

   private void a(double[][] var1) {
      var1 = (double[][])var1.clone();
      int var2 = this.f.length;
      this.a = new double[var2];
      this.b = new double[var2];
      double[] var3 = new double[var2];

      for (int var5 = 0; var5 < var2 - 1; var5++) {
         this.a[var5] = this.f[var5];
         var3[var5] = this.g[var5];
      }

      this.a[var2 - 1] = this.f[var2 - 1];
      var3[var2 - 1] = 0.0;
      double var32 = 0.0;

      for (int var4 = 0; var4 < var2; var4++) {
         if (BY.w(this.a[var4]) > var32) {
            var32 = BY.w(this.a[var4]);
         }

         if (BY.w(var3[var4]) > var32) {
            var32 = BY.w(var3[var4]);
         }
      }

      if (var32 != 0.0) {
         for (int var26 = 0; var26 < var2; var26++) {
            if (BY.w(this.a[var26]) <= Cq.a * var32) {
               this.a[var26] = 0.0;
            }

            if (BY.w(var3[var26]) <= Cq.a * var32) {
               var3[var26] = 0.0;
            }
         }
      }

      for (int var27 = 0; var27 < var2; var27++) {
         int var33 = 0;

         int var9;
         do {
            for (var9 = var27; var9 < var2 - 1; var9++) {
               double var10 = BY.w(this.a[var9]) + BY.w(this.a[var9 + 1]);
               if (BY.w(var3[var9]) + var10 == var10) {
                  break;
               }
            }

            if (var9 != var27) {
               if (var33 == this.e) {
                  throw new pc(px.CONVERGENCE_FAILED, this.e);
               }

               var33++;
               double var40 = (this.a[var27 + 1] - this.a[var27]) / (2.0 * var3[var27]);
               double var12 = BY.a(1.0 + var40 * var40);
               if (var40 < 0.0) {
                  var40 = this.a[var9] - this.a[var27] + var3[var27] / (var40 - var12);
               } else {
                  var40 = this.a[var9] - this.a[var27] + var3[var27] / (var40 + var12);
               }

               double var14 = 0.0;
               double var16 = 1.0;
               double var18 = 1.0;

               int var6;
               for (var6 = var9 - 1; var6 >= var27; var6--) {
                  double var21 = var16 * var3[var6];
                  double var23 = var18 * var3[var6];
                  if (BY.w(var21) >= BY.w(var40)) {
                     var12 = BY.a((var18 = var40 / var21) * var18 + 1.0);
                     var3[var6 + 1] = var21 * var12;
                     var16 = 1.0 / var12;
                     var18 *= var16;
                  } else {
                     var12 = BY.a((var16 = var21 / var40) * var16 + 1.0);
                     var3[var6 + 1] = var40 * var12;
                     var18 = 1.0 / var12;
                     var16 *= var18;
                  }

                  if (var3[var6 + 1] == 0.0) {
                     this.a[var6 + 1] = this.a[var6 + 1] - var14;
                     var3[var9] = 0.0;
                     break;
                  }

                  var40 = this.a[var6 + 1] - var14;
                  var12 = (this.a[var6] - var40) * var16 + var18 * 2.0 * var23;
                  var14 = var16 * var12;
                  this.a[var6 + 1] = var40 + var14;
                  var40 = var18 * var12 - var23;

                  for (int var7 = 0; var7 < var2; var7++) {
                     var21 = var1[var7][var6 + 1];
                     var1[var7][var6 + 1] = var16 * var1[var7][var6] + var18 * var21;
                     var1[var7][var6] = var18 * var1[var7][var6] - var16 * var21;
                  }
               }

               if (var12 != 0.0 || var6 < var27) {
                  this.a[var27] = this.a[var27] - var14;
                  var3[var27] = var40;
                  var3[var9] = 0.0;
               }
            }
         } while (var9 != var27);
      }

      for (int var28 = 0; var28 < var2; var28++) {
         int var34 = var28;
         double var37 = this.a[var28];

         for (int var11 = var28 + 1; var11 < var2; var11++) {
            if (this.a[var11] > var37) {
               var34 = var11;
               var37 = this.a[var11];
            }
         }

         if (var34 != var28) {
            this.a[var34] = this.a[var28];
            this.a[var28] = var37;

            for (int var43 = 0; var43 < var2; var43++) {
               var37 = var1[var43][var28];
               var1[var43][var28] = var1[var43][var34];
               var1[var43][var34] = var37;
            }
         }
      }

      var32 = 0.0;

      for (int var29 = 0; var29 < var2; var29++) {
         if (BY.w(this.a[var29]) > var32) {
            var32 = BY.w(this.a[var29]);
         }
      }

      if (var32 != 0.0) {
         for (int var30 = 0; var30 < var2; var30++) {
            if (BY.w(this.a[var30]) < Cq.a * var32) {
               this.a[var30] = 0.0;
            }
         }
      }

      this.c = new sA[var2];
      double[] var31 = new double[var2];

      for (int var36 = 0; var36 < var2; var36++) {
         for (int var39 = 0; var39 < var2; var39++) {
            var31[var39] = var1[var39][var36];
         }

         this.c[var36] = new sA(var31);
      }
   }

   private tz b(tq var1) {
      tz var11;
      double[][] var2 = (var11 = new tz(var1)).b().a();
      this.a = new double[var2.length];
      this.b = new double[var2.length];

      for (int var3 = 0; var3 < this.a.length; var3++) {
         if (var3 != this.a.length - 1 && !Cq.b(var2[var3 + 1][var3], 0.0, 1.0E-12)) {
            double var5 = var2[var3 + 1][var3 + 1];
            double var7;
            double var9 = BY.a(BY.w((var7 = 0.5 * (var2[var3][var3] - var5)) * var7 + var2[var3 + 1][var3] * var2[var3][var3 + 1]));
            this.a[var3] = var5 + var7;
            this.b[var3] = var9;
            this.a[var3 + 1] = var5 + var7;
            this.b[var3 + 1] = -var9;
            var3++;
         } else {
            this.a[var3] = var2[var3][var3];
         }
      }

      return var11;
   }

   private static nQ a(double var0, double var2, double var4, double var6) {
      return new nQ(var0, var2).b(new nQ(var4, var6));
   }

   private void a(tz var1) {
      double[][] var2 = var1.b().a();
      double[][] var34 = var1.a().a();
      int var3 = var2.length;
      double var5 = 0.0;

      for (int var7 = 0; var7 < var3; var7++) {
         for (int var8 = BY.b(var7 - 1, 0); var8 < var3; var8++) {
            var5 += BY.w(var2[var7][var8]);
         }
      }

      if (Cq.b(var5, 0.0, 1.0E-12)) {
         throw new oU(px.ZERO_NORM);
      }

      double var37 = 0.0;
      double var9 = 0.0;
      double var11 = 0.0;

      for (int var4 = var3 - 1; var4 >= 0; var4--) {
         double var14 = this.a[var4];
         double var16;
         if (Cq.a(var16 = this.b[var4], 0.0)) {
            int var39 = var4;
            var2[var4][var4] = 1.0;

            for (int var45 = var4 - 1; var45 >= 0; var45--) {
               double var47 = var2[var45][var45] - var14;
               var37 = 0.0;

               for (int var48 = var39; var48 <= var4; var48++) {
                  var37 += var2[var45][var48] * var2[var48][var4];
               }

               if (Cq.a(this.b[var45], 0.0, 1.0E-12) < 0) {
                  var11 = var47;
                  var9 = var37;
               } else {
                  var39 = var45;
                  if (Cq.a(this.b[var45], 0.0)) {
                     if (var47 != 0.0) {
                        var2[var45][var4] = -var37 / var47;
                     } else {
                        var2[var45][var4] = -var37 / (Cq.a * var5);
                     }
                  } else {
                     double var49 = var2[var45][var45 + 1];
                     double var52 = var2[var45 + 1][var45];
                     var16 = (this.a[var45] - var14) * (this.a[var45] - var14) + this.b[var45] * this.b[var45];
                     double var56 = (var49 * var9 - var11 * var37) / var16;
                     var2[var45][var4] = var56;
                     if (BY.w(var49) > BY.w(var11)) {
                        var2[var45 + 1][var4] = (-var37 - var47 * var56) / var49;
                     } else {
                        var2[var45 + 1][var4] = (-var9 - var52 * var56) / var11;
                     }
                  }

                  double var50 = BY.w(var2[var45][var4]);
                  if (Cq.a * var50 * var50 > 1.0) {
                     for (int var53 = var45; var53 <= var4; var53++) {
                        var2[var53][var4] = var2[var53][var4] / var50;
                     }
                  }
               }
            }
         } else if (var16 < 0.0) {
            int var13 = var4 - 1;
            if (BY.w(var2[var4][var4 - 1]) > BY.w(var2[var4 - 1][var4])) {
               var2[var4 - 1][var4 - 1] = var16 / var2[var4][var4 - 1];
               var2[var4 - 1][var4] = -(var2[var4][var4] - var14) / var2[var4][var4 - 1];
            } else {
               nQ var18 = a(0.0, -var2[var4 - 1][var4], var2[var4 - 1][var4 - 1] - var14, var16);
               var2[var4 - 1][var4 - 1] = var18.d;
               var2[var4 - 1][var4] = var18.c;
            }

            var2[var4][var4 - 1] = 0.0;
            var2[var4][var4] = 1.0;

            for (int var44 = var4 - 2; var44 >= 0; var44--) {
               double var20 = 0.0;
               double var22 = 0.0;

               for (int var24 = var13; var24 <= var4; var24++) {
                  var20 += var2[var44][var24] * var2[var24][var4 - 1];
                  var22 += var2[var44][var24] * var2[var24][var4];
               }

               double var51 = var2[var44][var44] - var14;
               if (Cq.a(this.b[var44], 0.0, 1.0E-12) < 0) {
                  var11 = var51;
                  var37 = var20;
                  var9 = var22;
               } else {
                  var13 = var44;
                  if (Cq.a(this.b[var44], 0.0)) {
                     nQ var26 = a(-var20, -var22, var51, var16);
                     var2[var44][var4 - 1] = var26.d;
                     var2[var44][var4] = var26.c;
                  } else {
                     double var54 = var2[var44][var44 + 1];
                     double var28 = var2[var44 + 1][var44];
                     double var30 = (this.a[var44] - var14) * (this.a[var44] - var14) + this.b[var44] * this.b[var44] - var16 * var16;
                     double var32 = (this.a[var44] - var14) * 2.0 * var16;
                     if (Cq.a(var30, 0.0) && Cq.a(var32, 0.0)) {
                        var30 = Cq.a * var5 * (BY.w(var51) + BY.w(var16) + BY.w(var54) + BY.w(var28) + BY.w(var11));
                     }

                     nQ var19 = a(var54 * var37 - var11 * var20 + var16 * var22, var54 * var9 - var11 * var22 - var16 * var20, var30, var32);
                     var2[var44][var4 - 1] = var19.d;
                     var2[var44][var4] = var19.c;
                     if (BY.w(var54) > BY.w(var11) + BY.w(var16)) {
                        var2[var44 + 1][var4 - 1] = (-var20 - var51 * var2[var44][var4 - 1] + var16 * var2[var44][var4]) / var54;
                        var2[var44 + 1][var4] = (-var22 - var51 * var2[var44][var4] - var16 * var2[var44][var4 - 1]) / var54;
                     } else {
                        var19 = a(-var37 - var28 * var2[var44][var4 - 1], -var9 - var28 * var2[var44][var4], var11, var16);
                        var2[var44 + 1][var4 - 1] = var19.d;
                        var2[var44 + 1][var4] = var19.c;
                     }
                  }

                  double var55 = BY.e(BY.w(var2[var44][var4 - 1]), BY.w(var2[var44][var4]));
                  if (Cq.a * var55 * var55 > 1.0) {
                     for (int var57 = var44; var57 <= var4; var57++) {
                        var2[var57][var4 - 1] = var2[var57][var4 - 1] / var55;
                        var2[var57][var4] = var2[var57][var4] / var55;
                     }
                  }
               }
            }
         }
      }

      for (int var35 = var3 - 1; var35 >= 0; var35--) {
         for (int var40 = 0; var40 <= var3 - 1; var40++) {
            var11 = 0.0;

            for (int var15 = 0; var15 <= BY.a(var35, var3 - 1); var15++) {
               var11 += var34[var40][var15] * var2[var15][var35];
            }

            var34[var40][var35] = var11;
         }
      }

      this.c = new sA[var3];
      double[] var36 = new double[var3];

      for (int var41 = 0; var41 < var3; var41++) {
         for (int var42 = 0; var42 < var3; var42++) {
            var36[var42] = var34[var42][var41];
         }

         this.c[var41] = new sA(var36);
      }
   }

   public static class a implements sG {
      private double[] a;
      private double[] b;
      private final sA[] c;

      private a(double[] var1, double[] var2, sA[] var3) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
      }

      @Override
      public final tu a(tu var1) {
         if (!this.a()) {
            throw new tA();
         }

         int var2 = this.a.length;
         if (var1.f() != var2) {
            throw new oS(var1.f(), var2);
         }

         double[] var3 = new double[var2];

         for (int var4 = 0; var4 < var2; var4++) {
            sA var5;
            double[] var6 = (var5 = this.c[var4]).a;
            double var7 = var5.b(var1) / this.a[var4];

            for (int var9 = 0; var9 < var2; var9++) {
               var3[var9] += var7 * var6[var9];
            }
         }

         return new sA(var3, false);
      }

      @Override
      public final tq a(tq var1) {
         if (!this.a()) {
            throw new tA();
         }

         int var2 = this.a.length;
         if (var1.f() != var2) {
            throw new oS(var1.f(), var2);
         }

         int var3 = var1.g();
         double[][] var4 = new double[var2][var3];
         double[] var5 = new double[var2];

         for (int var6 = 0; var6 < var3; var6++) {
            for (int var7 = 0; var7 < var2; var7++) {
               var5[var7] = var1.b(var7, var6);
               var4[var7][var6] = 0.0;
            }

            for (int var13 = 0; var13 < var2; var13++) {
               sA var8;
               double[] var9 = (var8 = this.c[var13]).a;
               double var10 = 0.0;

               for (int var12 = 0; var12 < var2; var12++) {
                  var10 += var8.a(var12) * var5[var12];
               }

               var10 /= this.a[var13];

               for (int var15 = 0; var15 < var2; var15++) {
                  var4[var15][var6] = var4[var15][var6] + var10 * var9[var15];
               }
            }
         }

         return new sy(var4, false);
      }

      @Override
      public final boolean a() {
         double var1 = 0.0;

         for (int var3 = 0; var3 < this.a.length; var3++) {
            var1 = BY.e(var1, this.a(var3));
         }

         if (var1 == 0.0) {
            return false;
         }

         for (int var4 = 0; var4 < this.a.length; var4++) {
            if (Cq.b(this.a(var4) / var1, 0.0, 1.0E-12)) {
               return false;
            }
         }

         return true;
      }

      private double a(int var1) {
         double var2 = this.a[var1];
         double var4 = this.b[var1];
         return BY.a(var2 * var2 + var4 * var4);
      }

      @Override
      public final tq b() {
         if (!this.a()) {
            throw new tA();
         }

         int var1;
         double[][] var2 = new double[var1 = this.a.length][var1];

         for (int var3 = 0; var3 < var1; var3++) {
            double[] var4 = var2[var3];

            for (int var5 = 0; var5 < var1; var5++) {
               double var6 = 0.0;

               for (int var8 = 0; var8 < var1; var8++) {
                  sA var9;
                  double[] var10 = (var9 = this.c[var8]).a;
                  var6 += var10[var3] * var10[var5] / this.a[var8];
               }

               var4[var5] = var6;
            }
         }

         return td.a(var2);
      }

      // $VF: synthetic method
      public a(double[] var1, double[] var2, sA[] var3, byte var4) {
         this(var1, var2, var3);
      }
   }
}
