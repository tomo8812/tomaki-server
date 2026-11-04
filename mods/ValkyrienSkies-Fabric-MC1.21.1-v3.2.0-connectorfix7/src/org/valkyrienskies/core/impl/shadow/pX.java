package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class pX implements pV {
   private static final double a = 2.0 * Cq.a;
   private final double b;
   private final double c;
   private final double d;
   private final double e;
   private final double f;

   public pX() {
      this(100.0, 1.0E-10, 1.0E-10, 1.0E-10, Cq.b);
   }

   private pX(double var1, double var3, double var5, double var7, double var9) {
      this.b = var1;
      this.c = var3;
      this.d = var5;
      this.e = var7;
      this.f = var9;
   }

   private pX a(double var1) {
      return new pX(var1, this.c, this.d, this.e, this.f);
   }

   private pX b(double var1) {
      return new pX(this.b, var1, this.d, this.e, this.f);
   }

   private pX c(double var1) {
      return new pX(this.b, this.c, var1, this.e, this.f);
   }

   private pX d(double var1) {
      return new pX(this.b, this.c, this.d, var1, this.f);
   }

   private pX e(double var1) {
      return new pX(this.b, this.c, this.d, this.e, var1);
   }

   private double a() {
      return this.b;
   }

   private double b() {
      return this.c;
   }

   private double c() {
      return this.d;
   }

   private double d() {
      return this.e;
   }

   private double e() {
      return this.f;
   }

   @Override
   public final pZ a(pW var1) {
      int var2 = var1.b();
      int var3 = var1.c();
      Cb var4 = var1.e();
      Cb var5 = var1.d();
      wK var6 = var1.f();
      int var7 = BY.a(var2, var3);
      double[] var8 = new double[var3];
      double var9 = 0.0;
      double var11 = 0.0;
      double var13 = 0.0;
      double[] var15 = new double[var3];
      double[] var16 = new double[var3];
      double[] var17 = new double[var2];
      double[] var18 = new double[var3];
      double[] var19 = new double[var3];
      double[] var20 = new double[var3];
      var5.c();
      pW.a var21;
      double[] var22 = (var21 = var1.a(var1.a())).d().g();
      double var23 = var21.b();
      double[] var25 = var21.e().g();
      boolean var26 = true;

      while (true) {
         var4.c();
         pW.a var27 = var21;
         tq var10001 = var21.c();
         int var60 = var7;
         tq var36 = var10001;
         pX var33 = this;
         double[][] var61;
         int var37 = (var61 = var36.b(-1.0).a()).length;
         int var40;
         int[] var64 = new int[var40 = var61[0].length];
         double[] var65 = new double[var40];
         double[] var66 = new double[var40];
         double[] var67 = new double[var40];

         for (int var68 = 0; var68 < var40; var68++) {
            var64[var68] = var68;
            double var69 = 0.0;

            for (int var71 = 0; var71 < var37; var71++) {
               double var72 = var61[var71][var68];
               var69 += var72 * var72;
            }

            var66[var68] = BY.a(var69);
         }

         int var174 = 0;

         pX.a var10000;
         while (true) {
            if (var174 >= var40) {
               var10000 = new pX.a(var61, var64, var60, var65, var66, var67);
               break;
            }

            int var178 = -1;
            double var70 = Double.NEGATIVE_INFINITY;

            for (int var184 = var174; var184 < var40; var184++) {
               double var73 = 0.0;

               for (int var75 = var174; var75 < var37; var75++) {
                  double var76 = var61[var75][var64[var184]];
                  var73 += var76 * var76;
               }

               if (Double.isInfinite(var73) || Double.isNaN(var73)) {
                  throw new oR(px.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, var37, var40);
               }

               if (var73 > var70) {
                  var178 = var184;
                  var70 = var73;
               }
            }

            if (var70 <= var33.f) {
               var10000 = new pX.a(var61, var64, var174, var65, var66, var67);
               break;
            }

            int var185 = var64[var178];
            var64[var178] = var64[var174];
            var64[var174] = var185;
            double var187;
            double var189 = (var187 = var61[var174][var185]) > 0.0 ? -BY.a(var70) : BY.a(var70);
            double var77 = 1.0 / (var70 - var187 * var189);
            var67[var185] = var77;
            var65[var185] = var189;
            var61[var174][var185] = var61[var174][var185] - var189;

            for (int var79 = var40 - 1 - var174; var79 > 0; var79--) {
               double var80 = 0.0;

               for (int var82 = var174; var82 < var37; var82++) {
                  var80 += var61[var82][var185] * var61[var82][var64[var174 + var79]];
               }

               var80 *= var77;

               for (int var208 = var174; var208 < var37; var208++) {
                  var61[var208][var64[var174 + var79]] = var61[var208][var64[var174 + var79]] - var80 * var61[var208][var185];
               }
            }

            var174++;
         }

         pX.a var28 = var10000;
         double[][] var29 = var10000.a;
         int[] var30 = var28.b;
         double[] var31 = var28.d;
         double[] var32 = var28.e;
         double[] var132 = var22;

         for (int var34 = 0; var34 < var2; var34++) {
            var17[var34] = var132[var34];
         }

         pX.a var138 = var28;
         double[] var133 = var17;
         var61 = var138.a;
         int[] var143 = var138.b;
         double[] var149 = var138.f;
         int var166 = var61.length;
         int var168 = var61[0].length;

         for (int var170 = 0; var170 < var168; var170++) {
            int var172 = var143[var170];
            double var175 = 0.0;

            for (int var181 = var170; var181 < var166; var181++) {
               var175 += var61[var181][var172] * var133[var181];
            }

            double var176 = var175 * var149[var172];

            for (int var182 = var170; var182 < var166; var182++) {
               var133[var182] -= var176 * var61[var182][var172];
            }
         }

         for (int var134 = 0; var134 < var7; var134++) {
            int var35 = var30[var134];
            var29[var134][var35] = var31[var35];
         }

         if (var26) {
            var13 = 0.0;

            for (int var135 = 0; var135 < var3; var135++) {
               double var137;
               if ((var137 = var32[var135]) == 0.0) {
                  var137 = 1.0;
               }

               double var144 = var137 * var25[var135];
               var13 += var144 * var144;
               var15[var135] = var137;
            }

            var11 = (var13 = BY.a(var13)) == 0.0 ? this.b : this.b * var13;
         }

         double var136 = 0.0;
         if (var23 != 0.0) {
            for (int var139 = 0; var139 < var7; var139++) {
               var37 = var30[var139];
               double var38;
               if ((var38 = var32[var37]) != 0.0) {
                  double var150 = 0.0;

                  for (int var42 = 0; var42 <= var139; var42++) {
                     var150 += var29[var42][var37] * var17[var42];
                  }

                  var136 = BY.e(var136, BY.w(var150) / (var38 * var23));
               }
            }
         }

         if (var136 <= this.e) {
            return new pZ(var21, var5.b, var4.b);
         }

         for (int var140 = 0; var140 < var3; var140++) {
            var15[var140] = BY.e(var15[var140], var32[var140]);
         }

         double var141 = 0.0;

         while (var141 < 1.0E-4) {
            for (int var147 = 0; var147 < var7; var147++) {
               int var39 = var30[var147];
               var16[var39] = var25[var39];
            }

            double var148 = var23;
            double var179 = var9;
            double[] var177 = var8;
            var67 = var20;
            var66 = var19;
            var65 = var18;
            int var167 = var7;
            pX.a var151 = var28;
            double[] var146 = var15;
            double var164 = var11;
            double[] var142 = var17;
            double[][] var183 = var151.a;
            int[] var186 = var151.b;
            int var188 = var151.c;
            double[] var74 = var151.d;
            int var190 = var183[0].length;

            for (int var191 = 0; var191 < var188; var191++) {
               var177[var186[var191]] = var142[var191];
            }

            for (int var192 = var188; var192 < var190; var192++) {
               var177[var186[var192]] = 0.0;
            }

            for (int var193 = var188 - 1; var193 >= 0; var193--) {
               int var198 = var186[var193];
               double var78 = var177[var198] / var74[var198];

               for (int var203 = 0; var203 < var193; var203++) {
                  var177[var186[var203]] = var177[var186[var203]] - var78 * var183[var203][var198];
               }

               var177[var198] = var78;
            }

            double var194 = 0.0;

            for (int var199 = 0; var199 < var167; var199++) {
               int var201 = var186[var199];
               double var204 = var146[var201] * var177[var201];
               var65[var201] = var204;
               var194 += var204 * var204;
            }

            double var200;
            double var240;
            if ((var200 = (var194 = BY.a(var194)) - var164) <= var164 * 0.1) {
               var240 = 0.0;
            } else {
               double var209 = 0.0;
               if (var188 == var167) {
                  for (int var84 = 0; var84 < var167; var84++) {
                     int var85 = var186[var84];
                     var65[var85] *= var146[var85] / var194;
                  }

                  double var205 = 0.0;

                  for (int var210 = 0; var210 < var167; var210++) {
                     int var213 = var186[var210];
                     double var86 = 0.0;

                     for (int var88 = 0; var88 < var210; var88++) {
                        var86 += var183[var88][var213] * var65[var186[var88]];
                     }

                     double var218 = (var65[var213] - var86) / var74[var213];
                     var65[var213] = var218;
                     var205 += var218 * var218;
                  }

                  var209 = var200 / (var164 * var205);
               }

               double var206 = 0.0;

               for (int var211 = 0; var211 < var167; var211++) {
                  int var214 = var186[var211];
                  double var215 = 0.0;

                  for (int var219 = 0; var219 <= var211; var219++) {
                     var215 += var183[var219][var214] * var142[var219];
                  }

                  var215 /= var146[var214];
                  var206 += var215 * var215;
               }

               double var212;
               double var217;
               if ((var217 = (var212 = BY.a(var206)) / var164) == 0.0) {
                  var217 = Cq.b / BY.d(var164, 0.1);
               }

               if ((var179 = BY.d(var217, BY.e(var179, var209))) == 0.0) {
                  var179 = var212 / var194;
               }

               int var220 = 10;

               while (true) {
                  if (var220 < 0) {
                     var240 = var179;
                     break;
                  }

                  if (var179 == 0.0) {
                     var179 = BY.e(Cq.b, var217 * 0.001);
                  }

                  double var89 = BY.a(var179);

                  for (int var91 = 0; var91 < var167; var91++) {
                     int var92 = var186[var91];
                     var65[var92] = var89 * var146[var92];
                  }

                  double[] var104 = var177;
                  double[] var103 = var67;
                  int var102 = var167;
                  pX.a var101 = var151;
                  double[] var100 = var66;
                  double[] var99 = var65;
                  double[] var98 = var142;
                  int[] var105 = var101.b;
                  double[][] var106 = var101.a;
                  double[] var107 = var101.d;

                  for (int var108 = 0; var108 < var102; var108++) {
                     int var109 = var105[var108];

                     for (int var110 = var108 + 1; var110 < var102; var110++) {
                        var106[var110][var109] = var106[var108][var105[var110]];
                     }

                     var104[var108] = var107[var109];
                     var103[var108] = var98[var108];
                  }

                  for (int var230 = 0; var230 < var102; var230++) {
                     int var232 = var105[var230];
                     double var236;
                     if ((var236 = var99[var232]) != 0.0) {
                        Arrays.fill(var100, var230 + 1, var100.length, 0.0);
                     }

                     var100[var230] = var236;
                     double var112 = 0.0;

                     for (int var114 = var230; var114 < var102; var114++) {
                        int var115 = var105[var114];
                        if (var100[var114] != 0.0) {
                           double var116;
                           double var118;
                           double var120;
                           if (BY.w(var120 = var106[var114][var115]) < BY.w(var100[var114])) {
                              double var122 = var120 / var100[var114];
                              var118 = (var116 = 1.0 / BY.a(1.0 + var122 * var122)) * var122;
                           } else {
                              double var238 = var100[var114] / var120;
                              var116 = (var118 = 1.0 / BY.a(1.0 + var238 * var238)) * var238;
                           }

                           var106[var114][var115] = var118 * var120 + var116 * var100[var114];
                           double var239 = var118 * var103[var114] + var116 * var112;
                           var112 = -var116 * var103[var114] + var118 * var112;
                           var103[var114] = var239;

                           for (int var124 = var114 + 1; var124 < var102; var124++) {
                              double var125 = var106[var124][var115];
                              double var127 = var118 * var125 + var116 * var100[var124];
                              var100[var124] = -var116 * var125 + var118 * var100[var124];
                              var106[var124][var115] = var127;
                           }
                        }
                     }

                     var100[var230] = var106[var230][var105[var230]];
                     var106[var230][var105[var230]] = var104[var230];
                  }

                  int var231 = var102;

                  for (int var233 = 0; var233 < var102; var233++) {
                     if (var100[var233] == 0.0 && var231 == var102) {
                        var231 = var233;
                     }

                     if (var231 < var102) {
                        var103[var233] = 0.0;
                     }
                  }

                  if (var231 > 0) {
                     for (int var234 = var231 - 1; var234 >= 0; var234--) {
                        int var237 = var105[var234];
                        double var111 = 0.0;

                        for (int var113 = var234 + 1; var113 < var231; var113++) {
                           var111 += var106[var113][var237] * var103[var113];
                        }

                        var103[var234] = (var103[var234] - var111) / var100[var234];
                     }
                  }

                  for (int var235 = 0; var235 < var104.length; var235++) {
                     var104[var105[var235]] = var103[var235];
                  }

                  var194 = 0.0;

                  for (int var221 = 0; var221 < var167; var221++) {
                     int var223 = var186[var221];
                     double var93 = var146[var223] * var177[var223];
                     var67[var223] = var93;
                     var194 += var93 * var93;
                  }

                  var194 = BY.a(var194);
                  double var222 = var200;
                  if (BY.w(var200 = var194 - var164) <= var164 * 0.1 || var209 == 0.0 && var200 <= var222 && var222 < 0.0) {
                     var240 = var179;
                     break;
                  }

                  for (int var224 = 0; var224 < var167; var224++) {
                     int var94 = var186[var224];
                     var65[var94] = var67[var94] * var146[var94] / var194;
                  }

                  for (int var225 = 0; var225 < var167; var225++) {
                     int var228 = var186[var225];
                     var65[var228] /= var66[var225];
                     double var95 = var65[var228];

                     for (int var97 = var225 + 1; var97 < var167; var97++) {
                        var65[var186[var97]] = var65[var186[var97]] - var183[var97][var228] * var95;
                     }
                  }

                  var206 = 0.0;

                  for (int var226 = 0; var226 < var167; var226++) {
                     double var229 = var65[var186[var226]];
                     var206 += var229 * var229;
                  }

                  double var227 = var200 / (var164 * var206);
                  if (var200 > 0.0) {
                     var209 = BY.e(var209, var179);
                  } else if (var200 < 0.0) {
                     var217 = BY.d(var217, var179);
                  }

                  var179 = BY.e(var209, var179 + var227);
                  var220--;
               }
            }

            var9 = var240;
            double var41 = 0.0;

            for (int var43 = 0; var43 < var7; var43++) {
               int var44 = var30[var43];
               var8[var44] = -var8[var44];
               var25[var44] = var16[var44] + var8[var44];
               double var45 = var15[var44] * var8[var44];
               var41 += var45 * var45;
            }

            var41 = BY.a(var41);
            if (var26) {
               var11 = BY.d(var11, var41);
            }

            var5.c();
            var22 = (var21 = var1.a(new sA(var25))).d().g();
            var23 = var21.b();
            var25 = var21.e().g();
            double var153 = -1.0;
            if (var23 * 0.1 < var148) {
               double var154 = var23 / var148;
               var153 = 1.0 - var154 * var154;
            }

            for (int var155 = 0; var155 < var7; var155++) {
               int var46 = var30[var155];
               double var47 = var8[var46];
               var18[var155] = 0.0;

               for (int var49 = 0; var49 <= var155; var49++) {
                  var18[var49] += var29[var49][var46] * var47;
               }
            }

            double var156 = 0.0;

            for (int var158 = 0; var158 < var7; var158++) {
               var156 += var18[var158] * var18[var158];
            }

            double var159 = var148 * var148;
            var156 /= var159;
            double var160 = var9 * var41 * var41 / var159;
            double var51 = var156 + var160 * 2.0;
            double var53 = -(var156 + var160);
            double var241 = var51 == 0.0 ? 0.0 : var153 / var51;
            var141 = var51 == 0.0 ? 0.0 : var153 / var51;
            if (var241 <= 0.25) {
               double var55 = var153 < 0.0 ? var53 * 0.5 / (var53 + var153 * 0.5) : 0.5;
               if (var23 * 0.1 >= var148 || var55 < 0.1) {
                  var55 = 0.1;
               }

               var11 = var55 * BY.d(var11, var41 * 10.0);
               var9 /= var55;
            } else if (var9 == 0.0 || var141 >= 0.75) {
               var11 = var41 * 2.0;
               var9 *= 0.5;
            }

            if (var141 >= 1.0E-4) {
               var26 = false;
               var13 = 0.0;

               for (int var162 = 0; var162 < var3; var162++) {
                  double var163 = var15[var162] * var25[var162];
                  var13 += var163 * var163;
               }

               var13 = BY.a(var13);
               if (var6 != null && var6.a(var4.b, var27, var21)) {
                  return new pZ(var21, var5.b, var4.b);
               }
            } else {
               var23 = var148;

               for (int var161 = 0; var161 < var7; var161++) {
                  int var56 = var30[var161];
                  var25[var56] = var16[var56];
               }

               var21 = var27;
            }

            if (BY.w(var153) <= this.c && var51 <= this.c && var141 <= 2.0 || var11 <= this.d * var13) {
               return new pZ(var21, var5.b, var4.b);
            }

            if (BY.w(var153) <= a && var51 <= a && var141 <= 2.0) {
               throw new oR(px.TOO_SMALL_COST_RELATIVE_TOLERANCE, this.c);
            }

            if (var11 <= a * var13) {
               throw new oR(px.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, this.d);
            }

            if (var136 <= a) {
               throw new oR(px.TOO_SMALL_ORTHOGONALITY_TOLERANCE, this.e);
            }
         }
      }
   }

   private double a(double[] var1, double var2, double[] var4, pX.a var5, int var6, double[] var7, double[] var8, double[] var9, double[] var10, double var11) {
      double[][] var13 = var5.a;
      int[] var14 = var5.b;
      int var15 = var5.c;
      double[] var16 = var5.d;
      int var17 = var13[0].length;

      for (int var18 = 0; var18 < var15; var18++) {
         var10[var14[var18]] = var1[var18];
      }

      for (int var41 = var15; var41 < var17; var41++) {
         var10[var14[var41]] = 0.0;
      }

      for (int var42 = var15 - 1; var42 >= 0; var42--) {
         int var19 = var14[var42];
         double var20 = var10[var19] / var16[var19];

         for (int var22 = 0; var22 < var42; var22++) {
            var10[var14[var22]] = var10[var14[var22]] - var20 * var13[var22][var19];
         }

         var10[var19] = var20;
      }

      double var43 = 0.0;

      for (int var47 = 0; var47 < var6; var47++) {
         int var21 = var14[var47];
         double var49 = var4[var21] * var10[var21];
         var7[var21] = var49;
         var43 += var49 * var49;
      }

      double var48;
      if ((var48 = (var43 = BY.a(var43)) - var2) <= var2 * 0.1) {
         return 0.0;
      }

      double var24 = 0.0;
      if (var15 == var6) {
         for (int var26 = 0; var26 < var6; var26++) {
            int var27 = var14[var26];
            var7[var27] *= var4[var27] / var43;
         }

         double var50 = 0.0;

         for (int var53 = 0; var53 < var6; var53++) {
            int var56 = var14[var53];
            double var28 = 0.0;

            for (int var30 = 0; var30 < var53; var30++) {
               var28 += var13[var30][var56] * var7[var14[var30]];
            }

            double var61 = (var7[var56] - var28) / var16[var56];
            var7[var56] = var61;
            var50 += var61 * var61;
         }

         var24 = var48 / (var2 * var50);
      }

      double var51 = 0.0;

      for (int var54 = 0; var54 < var6; var54++) {
         int var57 = var14[var54];
         double var58 = 0.0;

         for (int var62 = 0; var62 <= var54; var62++) {
            var58 += var13[var62][var57] * var1[var62];
         }

         var58 /= var4[var57];
         var51 += var58 * var58;
      }

      double var55;
      double var60;
      if ((var60 = (var55 = BY.a(var51)) / var2) == 0.0) {
         var60 = Cq.b / BY.d(var2, 0.1);
      }

      if ((var11 = BY.d(var60, BY.e(var11, var24))) == 0.0) {
         var11 = var55 / var43;
      }

      for (int var63 = 10; var63 >= 0; var63--) {
         if (var11 == 0.0) {
            var11 = BY.e(Cq.b, var60 * 0.001);
         }

         double var31 = BY.a(var11);

         for (int var33 = 0; var33 < var6; var33++) {
            int var34 = var14[var33];
            var7[var34] = var31 * var4[var34];
         }

         a(var1, var7, var8, var5, var6, var9, var10);
         var43 = 0.0;

         for (int var64 = 0; var64 < var6; var64++) {
            int var66 = var14[var64];
            double var35 = var4[var66] * var10[var66];
            var9[var66] = var35;
            var43 += var35 * var35;
         }

         var43 = BY.a(var43);
         double var65 = var48;
         if (BY.w(var48 = var43 - var2) <= var2 * 0.1 || var24 == 0.0 && var48 <= var65 && var65 < 0.0) {
            return var11;
         }

         for (int var67 = 0; var67 < var6; var67++) {
            int var36 = var14[var67];
            var7[var36] = var9[var36] * var4[var36] / var43;
         }

         for (int var68 = 0; var68 < var6; var68++) {
            int var71 = var14[var68];
            var7[var71] /= var8[var68];
            double var37 = var7[var71];

            for (int var40 = var68 + 1; var40 < var6; var40++) {
               var7[var14[var40]] = var7[var14[var40]] - var13[var40][var71] * var37;
            }
         }

         var51 = 0.0;

         for (int var69 = 0; var69 < var6; var69++) {
            double var72 = var7[var14[var69]];
            var51 += var72 * var72;
         }

         double var70 = var48 / (var2 * var51);
         if (var48 > 0.0) {
            var24 = BY.e(var24, var11);
         } else if (var48 < 0.0) {
            var60 = BY.d(var60, var11);
         }

         var11 = BY.e(var24, var11 + var70);
      }

      return var11;
   }

   private static void a(double[] var0, double[] var1, double[] var2, pX.a var3, int var4, double[] var5, double[] var6) {
      int[] var7 = var3.b;
      double[][] var8 = var3.a;
      double[] var32 = var3.d;

      for (int var9 = 0; var9 < var4; var9++) {
         int var10 = var7[var9];

         for (int var12 = var9 + 1; var12 < var4; var12++) {
            var8[var12][var10] = var8[var9][var7[var12]];
         }

         var6[var9] = var32[var10];
         var5[var9] = var0[var9];
      }

      for (int var34 = 0; var34 < var4; var34++) {
         int var36 = var7[var34];
         double var41;
         if ((var41 = var1[var36]) != 0.0) {
            Arrays.fill(var2, var34 + 1, var2.length, 0.0);
         }

         var2[var34] = var41;
         double var14 = 0.0;

         for (int var31 = var34; var31 < var4; var31++) {
            int var33 = var7[var31];
            if (var2[var31] != 0.0) {
               double var18;
               double var20;
               double var22;
               if (BY.w(var22 = var8[var31][var33]) < BY.w(var2[var31])) {
                  double var24 = var22 / var2[var31];
                  var20 = (var18 = 1.0 / BY.a(1.0 + var24 * var24)) * var24;
               } else {
                  double var43 = var2[var31] / var22;
                  var18 = (var20 = 1.0 / BY.a(1.0 + var43 * var43)) * var43;
               }

               var8[var31][var33] = var20 * var22 + var18 * var2[var31];
               double var44 = var20 * var5[var31] + var18 * var14;
               var14 = -var18 * var5[var31] + var20 * var14;
               var5[var31] = var44;

               for (int var37 = var31 + 1; var37 < var4; var37++) {
                  double var27 = var8[var37][var33];
                  double var29 = var20 * var27 + var18 * var2[var37];
                  var2[var37] = -var18 * var27 + var20 * var2[var37];
                  var8[var37][var33] = var29;
               }
            }
         }

         var2[var34] = var8[var34][var7[var34]];
         var8[var34][var7[var34]] = var6[var34];
      }

      int var35 = var4;

      for (int var38 = 0; var38 < var4; var38++) {
         if (var2[var38] == 0.0 && var35 == var4) {
            var35 = var38;
         }

         if (var35 < var4) {
            var5[var38] = 0.0;
         }
      }

      if (var35 > 0) {
         for (int var39 = var35 - 1; var39 >= 0; var39--) {
            int var42 = var7[var39];
            double var13 = 0.0;

            for (int var15 = var39 + 1; var15 < var35; var15++) {
               var13 += var8[var15][var42] * var5[var15];
            }

            var5[var39] = (var5[var39] - var13) / var2[var39];
         }
      }

      for (int var40 = 0; var40 < var6.length; var40++) {
         var6[var7[var40]] = var5[var40];
      }
   }

   private pX.a a(tq var1, int var2) {
      double[][] var24;
      int var3 = (var24 = var1.b(-1.0).a()).length;
      int var4;
      int[] var5 = new int[var4 = var24[0].length];
      double[] var6 = new double[var4];
      double[] var7 = new double[var4];
      double[] var8 = new double[var4];

      for (int var9 = 0; var9 < var4; var9++) {
         var5[var9] = var9;
         double var11 = 0.0;

         for (int var13 = 0; var13 < var3; var13++) {
            double var14 = var24[var13][var9];
            var11 += var14 * var14;
         }

         var7[var9] = BY.a(var11);
      }

      for (int var25 = 0; var25 < var4; var25++) {
         int var26 = -1;
         double var12 = Double.NEGATIVE_INFINITY;

         for (int var29 = var25; var29 < var4; var29++) {
            double var15 = 0.0;

            for (int var17 = var25; var17 < var3; var17++) {
               double var18 = var24[var17][var5[var29]];
               var15 += var18 * var18;
            }

            if (Double.isInfinite(var15) || Double.isNaN(var15)) {
               throw new oR(px.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, var3, var4);
            }

            if (var15 > var12) {
               var26 = var29;
               var12 = var15;
            }
         }

         if (var12 <= this.f) {
            return new pX.a(var24, var5, var25, var6, var7, var8);
         }

         int var30 = var5[var26];
         var5[var26] = var5[var25];
         var5[var25] = var30;
         double var31;
         double var32 = (var31 = var24[var25][var30]) > 0.0 ? -BY.a(var12) : BY.a(var12);
         double var19 = 1.0 / (var12 - var31 * var32);
         var8[var30] = var19;
         var6[var30] = var32;
         var24[var25][var30] = var24[var25][var30] - var32;

         for (int var10 = var4 - 1 - var25; var10 > 0; var10--) {
            double var22 = 0.0;

            for (int var27 = var25; var27 < var3; var27++) {
               var22 += var24[var27][var30] * var24[var27][var5[var25 + var10]];
            }

            var22 *= var19;

            for (int var28 = var25; var28 < var3; var28++) {
               var24[var28][var5[var25 + var10]] = var24[var28][var5[var25 + var10]] - var22 * var24[var28][var30];
            }
         }
      }

      return new pX.a(var24, var5, var2, var6, var7, var8);
   }

   private static void a(double[] var0, pX.a var1) {
      double[][] var2 = var1.a;
      int[] var3 = var1.b;
      double[] var11 = var1.f;
      int var4 = var2.length;
      int var5 = var2[0].length;

      for (int var6 = 0; var6 < var5; var6++) {
         int var7 = var3[var6];
         double var9 = 0.0;

         for (int var8 = var6; var8 < var4; var8++) {
            var9 += var2[var8][var7] * var0[var8];
         }

         var9 *= var11[var7];

         for (int var12 = var6; var12 < var4; var12++) {
            var0[var12] -= var9 * var2[var12][var7];
         }
      }
   }

   static class a {
      final double[][] a;
      final int[] b;
      final int c;
      final double[] d;
      final double[] e;
      final double[] f;

      a(double[][] var1, int[] var2, int var3, double[] var4, double[] var5, double[] var6) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = var4;
         this.e = var5;
         this.f = var6;
      }

      // $VF: synthetic method
      static double[][] a(pX.a var0) {
         return var0.a;
      }

      // $VF: synthetic method
      static int[] b(pX.a var0) {
         return var0.b;
      }

      // $VF: synthetic method
      static double[] c(pX.a var0) {
         return var0.d;
      }

      // $VF: synthetic method
      static double[] d(pX.a var0) {
         return var0.e;
      }

      // $VF: synthetic method
      static int e(pX.a var0) {
         return var0.c;
      }

      // $VF: synthetic method
      static double[] f(pX.a var0) {
         return var0.f;
      }
   }
}
