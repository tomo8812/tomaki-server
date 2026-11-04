package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

@Deprecated
public final class xK extends xI {
   private static final double a = 2.0 * Cq.a;
   private int b;
   private double[] c;
   private double[] d;
   private double[] e;
   private int[] f;
   private int g;
   private double h;
   private double[] i;
   private final double j = 100.0;
   private final double k;
   private final double l;
   private final double m;
   private final double n;
   private double[] o;
   private double[][] p;

   public xK() {
      this(100.0, 1.0E-10, 1.0E-10, 1.0E-10, Cq.b);
   }

   private xK(wK<wR> var1) {
      this(100.0, var1, 1.0E-10, 1.0E-10, 1.0E-10, Cq.b);
   }

   private xK(double var1, wK<wR> var3, double var4, double var6, double var8, double var10) {
      super(var3);
      this.k = 1.0E-10;
      this.l = 1.0E-10;
      this.m = 1.0E-10;
      this.n = var10;
   }

   private xK(double var1, double var3, double var5) {
      this(100.0, var1, var3, var5, Cq.b);
   }

   private xK(double var1, double var3, double var5, double var7, double var9) {
      super(null);
      this.k = var3;
      this.l = var5;
      this.m = var7;
      this.n = var9;
   }

   private wR o() {
      xK var27 = this;
      if (this.f() == null && var27.g() == null) {
         int var1 = this.d().length;
         double[] var2;
         int var3 = (var2 = this.e()).length;
         this.b = BY.a(var1, var3);
         this.c = new double[var3];
         this.d = new double[var3];
         this.e = new double[var3];
         this.f = new int[var3];
         this.i = new double[var3];
         double var4 = 0.0;
         double var6 = 0.0;
         double[] var8 = new double[var3];
         double[] var9 = new double[var3];
         double[] var10 = new double[var1];
         double[] var12 = new double[var1];
         double[] var13 = new double[var3];
         double[] var14 = new double[var3];
         double[] var15 = new double[var3];
         tq var16 = this.n();
         double[] var17 = this.b(var2);
         double[] var18 = this.e(var17);
         wR var19 = new wR(var2, var17);
         double var20 = this.d(var18);
         this.h = 0.0;
         boolean var22 = true;
         wK var23 = this.j();

         while (true) {
            this.l();
            wR var24 = var19;
            tq var28 = this.c(var2);
            var27 = this;
            this.p = var28.b(-1.0).a();
            int var51 = var27.p.length;
            int var52 = var27.p[0].length;

            for (int var31 = 0; var31 < var52; var31++) {
               var27.f[var31] = var31;
               double var54 = 0.0;

               for (int var56 = 0; var56 < var51; var56++) {
                  double var57 = var27.p[var56][var31];
                  var54 += var57 * var57;
               }

               var27.d[var31] = BY.a(var54);
            }

            int var124 = 0;

            while (true) {
               if (var124 >= var52) {
                  var27.g = var27.b;
                  break;
               }

               int var145 = -1;
               double var55 = Double.NEGATIVE_INFINITY;

               for (int var152 = var124; var152 < var52; var152++) {
                  double var58 = 0.0;

                  for (int var60 = var124; var60 < var51; var60++) {
                     double var61 = var27.p[var60][var27.f[var152]];
                     var58 += var61 * var61;
                  }

                  if (Double.isInfinite(var58) || Double.isNaN(var58)) {
                     throw new oR(px.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, var51, var52);
                  }

                  if (var58 > var55) {
                     var145 = var152;
                     var55 = var58;
                  }
               }

               if (var55 <= var27.n) {
                  var27.g = var124;
                  break;
               }

               int var153 = var27.f[var145];
               var27.f[var145] = var27.f[var124];
               var27.f[var124] = var153;
               double var157;
               double var165 = (var157 = var27.p[var124][var153]) > 0.0 ? -BY.a(var55) : BY.a(var55);
               double var62 = 1.0 / (var55 - var157 * var165);
               var27.e[var153] = var62;
               var27.c[var153] = var165;
               var27.p[var124][var153] = var27.p[var124][var153] - var165;

               for (int var64 = var52 - 1 - var124; var64 > 0; var64--) {
                  double var65 = 0.0;

                  for (int var67 = var124; var67 < var51; var67++) {
                     var65 += var27.p[var67][var153] * var27.p[var67][var27.f[var124 + var64]];
                  }

                  var65 *= var62;

                  for (int var180 = var124; var180 < var51; var180++) {
                     var27.p[var180][var27.f[var124 + var64]] = var27.p[var180][var27.f[var124 + var64]] - var65 * var27.p[var180][var153];
                  }
               }

               var124++;
            }

            this.o = var16.a(var18);

            for (int var25 = 0; var25 < var1; var25++) {
               var12[var25] = this.o[var25];
            }

            double[] var118 = var12;
            var27 = this;
            var51 = this.p.length;
            var52 = var27.p[0].length;

            for (int var125 = 0; var125 < var52; var125++) {
               int var146 = var27.f[var125];
               double var148 = 0.0;

               for (int var154 = var125; var154 < var51; var154++) {
                  var148 += var27.p[var154][var146] * var118[var154];
               }

               var148 *= var27.e[var146];

               for (int var155 = var125; var155 < var51; var155++) {
                  var118[var155] -= var148 * var27.p[var155][var146];
               }
            }

            for (int var108 = 0; var108 < this.b; var108++) {
               int var26 = this.f[var108];
               this.p[var108][var26] = this.c[var26];
            }

            if (var22) {
               var6 = 0.0;

               for (int var109 = 0; var109 < var3; var109++) {
                  double var111;
                  if ((var111 = this.d[var109]) == 0.0) {
                     var111 = 1.0;
                  }

                  double var119 = var111 * var2[var109];
                  var6 += var119 * var119;
                  var8[var109] = var111;
               }

               var4 = (var6 = BY.a(var6)) == 0.0 ? this.j : this.j * var6;
            }

            double var110 = 0.0;
            if (var20 != 0.0) {
               for (int var114 = 0; var114 < this.b; var114++) {
                  int var120 = this.f[var114];
                  double var29;
                  if ((var29 = this.d[var120]) != 0.0) {
                     double var126 = 0.0;

                     for (int var33 = 0; var33 <= var114; var33++) {
                        var126 += this.p[var33][var120] * var12[var33];
                     }

                     var110 = BY.e(var110, BY.w(var126) / (var29 * var20));
                  }
               }
            }

            if (var110 <= this.m) {
               this.a(var20);
               return var19;
            }

            for (int var115 = 0; var115 < var3; var115++) {
               var8[var115] = BY.e(var8[var115], this.d[var115]);
            }

            double var116 = 0.0;

            while (var116 < 1.0E-4) {
               for (int var122 = 0; var122 < this.b; var122++) {
                  int var30 = this.f[var122];
                  var9[var30] = var2[var30];
               }

               double var123 = var20;
               double[] var127 = this.o;
               this.o = var10;
               var10 = var127;
               double[] var11 = var17;
               double[] var151 = var15;
               double[] var150 = var14;
               double[] var147 = var13;
               double[] var128 = var8;
               double var143 = var4;
               double[] var121 = var12;
               var27 = this;
               int var156 = this.p[0].length;

               for (int var158 = 0; var158 < var27.g; var158++) {
                  var27.i[var27.f[var158]] = var121[var158];
               }

               for (int var159 = var27.g; var159 < var156; var159++) {
                  var27.i[var27.f[var159]] = 0.0;
               }

               for (int var160 = var27.g - 1; var160 >= 0; var160--) {
                  int var59 = var27.f[var160];
                  double var166 = var27.i[var59] / var27.c[var59];

                  for (int var170 = 0; var170 < var160; var170++) {
                     var27.i[var27.f[var170]] = var27.i[var27.f[var170]] - var166 * var27.p[var170][var59];
                  }

                  var27.i[var59] = var166;
               }

               double var161 = 0.0;

               for (int var167 = 0; var167 < var27.b; var167++) {
                  int var169 = var27.f[var167];
                  double var171 = var128[var169] * var27.i[var169];
                  var147[var169] = var171;
                  var161 += var171 * var171;
               }

               double var168;
               if ((var168 = (var161 = BY.a(var161)) - var143) <= var143 * 0.1) {
                  var27.h = 0.0;
               } else {
                  double var175 = 0.0;
                  if (var27.g == var27.b) {
                     for (int var66 = 0; var66 < var27.b; var66++) {
                        int var181 = var27.f[var66];
                        var147[var181] *= var128[var181] / var161;
                     }

                     double var172 = 0.0;

                     for (int var177 = 0; var177 < var27.b; var177++) {
                        int var182 = var27.f[var177];
                        double var68 = 0.0;

                        for (int var70 = 0; var70 < var177; var70++) {
                           var68 += var27.p[var70][var182] * var147[var27.f[var70]];
                        }

                        double var187 = (var147[var182] - var68) / var27.c[var182];
                        var147[var182] = var187;
                        var172 += var187 * var187;
                     }

                     var175 = var168 / (var143 * var172);
                  }

                  double var173 = 0.0;

                  for (int var178 = 0; var178 < var27.b; var178++) {
                     int var183 = var27.f[var178];
                     double var184 = 0.0;

                     for (int var188 = 0; var188 <= var178; var188++) {
                        var184 += var27.p[var188][var183] * var121[var188];
                     }

                     var184 /= var128[var183];
                     var173 += var184 * var184;
                  }

                  double var179;
                  double var186;
                  if ((var186 = (var179 = BY.a(var173)) / var143) == 0.0) {
                     var186 = Cq.b / BY.d(var143, 0.1);
                  }

                  var27.h = BY.d(var186, BY.e(var27.h, var175));
                  if (var27.h == 0.0) {
                     var27.h = var179 / var161;
                  }

                  for (int var189 = 10; var189 >= 0; var189--) {
                     if (var27.h == 0.0) {
                        var27.h = BY.e(Cq.b, var186 * 0.001);
                     }

                     double var71 = BY.a(var27.h);

                     for (int var73 = 0; var73 < var27.b; var73++) {
                        int var74 = var27.f[var73];
                        var147[var74] = var71 * var128[var74];
                     }

                     double[] var84 = var151;
                     double[] var83 = var150;
                     double[] var82 = var147;
                     double[] var81 = var121;
                     xK var80 = var27;

                     for (int var85 = 0; var85 < var80.b; var85++) {
                        int var86 = var80.f[var85];

                        for (int var87 = var85 + 1; var87 < var80.b; var87++) {
                           var80.p[var87][var86] = var80.p[var85][var80.f[var87]];
                        }

                        var80.i[var85] = var80.c[var86];
                        var84[var85] = var81[var85];
                     }

                     for (int var199 = 0; var199 < var80.b; var199++) {
                        int var201 = var80.f[var199];
                        double var205;
                        if ((var205 = var82[var201]) != 0.0) {
                           Arrays.fill(var83, var199 + 1, var83.length, 0.0);
                        }

                        var83[var199] = var205;
                        double var89 = 0.0;

                        for (int var91 = var199; var91 < var80.b; var91++) {
                           int var92 = var80.f[var91];
                           if (var83[var91] != 0.0) {
                              double var93;
                              double var95;
                              double var97;
                              if (BY.w(var97 = var80.p[var91][var92]) < BY.w(var83[var91])) {
                                 double var99 = var97 / var83[var91];
                                 var95 = (var93 = 1.0 / BY.a(1.0 + var99 * var99)) * var99;
                              } else {
                                 double var207 = var83[var91] / var97;
                                 var93 = (var95 = 1.0 / BY.a(1.0 + var207 * var207)) * var207;
                              }

                              var80.p[var91][var92] = var95 * var97 + var93 * var83[var91];
                              double var208 = var95 * var84[var91] + var93 * var89;
                              var89 = -var93 * var84[var91] + var95 * var89;
                              var84[var91] = var208;

                              for (int var101 = var91 + 1; var101 < var80.b; var101++) {
                                 double var102 = var80.p[var101][var92];
                                 double var104 = var95 * var102 + var93 * var83[var101];
                                 var83[var101] = -var93 * var102 + var95 * var83[var101];
                                 var80.p[var101][var92] = var104;
                              }
                           }
                        }

                        var83[var199] = var80.p[var199][var80.f[var199]];
                        var80.p[var199][var80.f[var199]] = var80.i[var199];
                     }

                     int var200 = var80.b;

                     for (int var202 = 0; var202 < var80.b; var202++) {
                        if (var83[var202] == 0.0 && var200 == var80.b) {
                           var200 = var202;
                        }

                        if (var200 < var80.b) {
                           var84[var202] = 0.0;
                        }
                     }

                     if (var200 > 0) {
                        for (int var203 = var200 - 1; var203 >= 0; var203--) {
                           int var206 = var80.f[var203];
                           double var88 = 0.0;

                           for (int var90 = var203 + 1; var90 < var200; var90++) {
                              var88 += var80.p[var90][var206] * var84[var90];
                           }

                           var84[var203] = (var84[var203] - var88) / var83[var203];
                        }
                     }

                     for (int var204 = 0; var204 < var80.i.length; var204++) {
                        var80.i[var80.f[var204]] = var84[var204];
                     }

                     var161 = 0.0;

                     for (int var190 = 0; var190 < var27.b; var190++) {
                        int var192 = var27.f[var190];
                        double var75 = var128[var192] * var27.i[var192];
                        var151[var192] = var75;
                        var161 += var75 * var75;
                     }

                     var161 = BY.a(var161);
                     double var191 = var168;
                     if (BY.w(var168 = var161 - var143) <= var143 * 0.1 || var175 == 0.0 && var168 <= var191 && var191 < 0.0) {
                        break;
                     }

                     for (int var193 = 0; var193 < var27.b; var193++) {
                        int var76 = var27.f[var193];
                        var147[var76] = var151[var76] * var128[var76] / var161;
                     }

                     for (int var194 = 0; var194 < var27.b; var194++) {
                        int var197 = var27.f[var194];
                        var147[var197] /= var150[var194];
                        double var77 = var147[var197];

                        for (int var79 = var194 + 1; var79 < var27.b; var79++) {
                           var147[var27.f[var79]] = var147[var27.f[var79]] - var27.p[var79][var197] * var77;
                        }
                     }

                     var173 = 0.0;

                     for (int var195 = 0; var195 < var27.b; var195++) {
                        double var198 = var147[var27.f[var195]];
                        var173 += var198 * var198;
                     }

                     double var196 = var168 / (var143 * var173);
                     if (var168 > 0.0) {
                        var175 = BY.e(var175, var27.h);
                     } else if (var168 < 0.0) {
                        var186 = BY.d(var186, var27.h);
                     }

                     var27.h = BY.e(var175, var27.h + var196);
                  }
               }

               double var32 = 0.0;

               for (int var34 = 0; var34 < this.b; var34++) {
                  int var35 = this.f[var34];
                  this.i[var35] = -this.i[var35];
                  var2[var35] = var9[var35] + this.i[var35];
                  double var36 = var8[var35] * this.i[var35];
                  var32 += var36 * var36;
               }

               var32 = BY.a(var32);
               if (var22) {
                  var4 = BY.d(var4, var32);
               }

               var17 = this.b(var2);
               var18 = this.e(var17);
               var19 = new wR(var2, var17);
               var20 = this.d(var18);
               double var131 = -1.0;
               if (var20 * 0.1 < var123) {
                  double var132 = var20 / var123;
                  var131 = 1.0 - var132 * var132;
               }

               for (int var133 = 0; var133 < this.b; var133++) {
                  int var37 = this.f[var133];
                  double var38 = this.i[var37];
                  var13[var133] = 0.0;

                  for (int var40 = 0; var40 <= var133; var40++) {
                     var13[var40] += this.p[var40][var37] * var38;
                  }
               }

               double var134 = 0.0;

               for (int var136 = 0; var136 < this.b; var136++) {
                  var134 += var13[var136] * var13[var136];
               }

               double var137 = var123 * var123;
               var134 /= var137;
               double var138 = this.h * var32 * var32 / var137;
               double var42 = var134 + var138 * 2.0;
               double var44 = -(var134 + var138);
               double var10000 = var42 == 0.0 ? 0.0 : var131 / var42;
               var116 = var42 == 0.0 ? 0.0 : var131 / var42;
               if (var10000 <= 0.25) {
                  double var46 = var131 < 0.0 ? var44 * 0.5 / (var44 + var131 * 0.5) : 0.5;
                  if (var20 * 0.1 >= var123 || var46 < 0.1) {
                     var46 = 0.1;
                  }

                  var4 = var46 * BY.d(var4, var32 * 10.0);
                  this.h /= var46;
               } else if (this.h == 0.0 || var116 >= 0.75) {
                  var4 = var32 * 2.0;
                  this.h *= 0.5;
               }

               if (var116 >= 1.0E-4) {
                  var22 = false;
                  var6 = 0.0;

                  for (int var140 = 0; var140 < var3; var140++) {
                     double var141 = var8[var140] * var2[var140];
                     var6 += var141 * var141;
                  }

                  var6 = BY.a(var6);
                  if (var23 != null && var23.a(this.i(), var24, var19)) {
                     this.a(var20);
                     return var19;
                  }
               } else {
                  var20 = var123;

                  for (int var139 = 0; var139 < this.b; var139++) {
                     int var47 = this.f[var139];
                     var2[var47] = var9[var47];
                  }

                  double[] var129 = this.o;
                  this.o = var10;
                  var10 = var129;
                  var17 = var11;
                  var19 = new wR(var2, var17);
               }

               if (BY.w(var131) <= this.k && var42 <= this.k && var116 <= 2.0 || var4 <= this.l * var6) {
                  this.a(var20);
                  return var19;
               }

               if (BY.w(var131) <= a && var42 <= a && var116 <= 2.0) {
                  throw new oR(px.TOO_SMALL_COST_RELATIVE_TOLERANCE, this.k);
               }

               if (var4 <= a * var6) {
                  throw new oR(px.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, this.l);
               }

               if (var110 <= a) {
                  throw new oR(px.TOO_SMALL_ORTHOGONALITY_TOLERANCE, this.m);
               }
            }
         }
      } else {
         throw new pb(px.CONSTRAINT);
      }
   }

   private void a(double[] var1, double var2, double[] var4, double[] var5, double[] var6, double[] var7) {
      int var8 = this.p[0].length;

      for (int var9 = 0; var9 < this.g; var9++) {
         this.i[this.f[var9]] = var1[var9];
      }

      for (int var31 = this.g; var31 < var8; var31++) {
         this.i[this.f[var31]] = 0.0;
      }

      for (int var32 = this.g - 1; var32 >= 0; var32--) {
         int var10 = this.f[var32];
         double var11 = this.i[var10] / this.c[var10];

         for (int var13 = 0; var13 < var32; var13++) {
            this.i[this.f[var13]] = this.i[this.f[var13]] - var11 * this.p[var13][var10];
         }

         this.i[var10] = var11;
      }

      double var33 = 0.0;

      for (int var37 = 0; var37 < this.b; var37++) {
         int var12 = this.f[var37];
         double var39 = var4[var12] * this.i[var12];
         var5[var12] = var39;
         var33 += var39 * var39;
      }

      double var38;
      if ((var38 = (var33 = BY.a(var33)) - var2) <= var2 * 0.1) {
         this.h = 0.0;
      } else {
         double var15 = 0.0;
         if (this.g == this.b) {
            for (int var17 = 0; var17 < this.b; var17++) {
               int var18 = this.f[var17];
               var5[var18] *= var4[var18] / var33;
            }

            double var40 = 0.0;

            for (int var43 = 0; var43 < this.b; var43++) {
               int var46 = this.f[var43];
               double var19 = 0.0;

               for (int var21 = 0; var21 < var43; var21++) {
                  var19 += this.p[var21][var46] * var5[this.f[var21]];
               }

               double var51 = (var5[var46] - var19) / this.c[var46];
               var5[var46] = var51;
               var40 += var51 * var51;
            }

            var15 = var38 / (var2 * var40);
         }

         double var41 = 0.0;

         for (int var44 = 0; var44 < this.b; var44++) {
            int var47 = this.f[var44];
            double var48 = 0.0;

            for (int var52 = 0; var52 <= var44; var52++) {
               var48 += this.p[var52][var47] * var1[var52];
            }

            var48 /= var4[var47];
            var41 += var48 * var48;
         }

         double var45;
         double var50;
         if ((var50 = (var45 = BY.a(var41)) / var2) == 0.0) {
            var50 = Cq.b / BY.d(var2, 0.1);
         }

         this.h = BY.d(var50, BY.e(this.h, var15));
         if (this.h == 0.0) {
            this.h = var45 / var33;
         }

         for (int var53 = 10; var53 >= 0; var53--) {
            if (this.h == 0.0) {
               this.h = BY.e(Cq.b, var50 * 0.001);
            }

            double var22 = BY.a(this.h);

            for (int var24 = 0; var24 < this.b; var24++) {
               int var25 = this.f[var24];
               var5[var25] = var22 * var4[var25];
            }

            this.a(var1, var5, var6, var7);
            var33 = 0.0;

            for (int var54 = 0; var54 < this.b; var54++) {
               int var56 = this.f[var54];
               double var26 = var4[var56] * this.i[var56];
               var7[var56] = var26;
               var33 += var26 * var26;
            }

            var33 = BY.a(var33);
            double var55 = var38;
            if (BY.w(var38 = var33 - var2) <= var2 * 0.1 || var15 == 0.0 && var38 <= var55 && var55 < 0.0) {
               return;
            }

            for (int var57 = 0; var57 < this.b; var57++) {
               int var27 = this.f[var57];
               var5[var27] = var7[var27] * var4[var27] / var33;
            }

            for (int var58 = 0; var58 < this.b; var58++) {
               int var61 = this.f[var58];
               var5[var61] /= var6[var58];
               double var28 = var5[var61];

               for (int var30 = var58 + 1; var30 < this.b; var30++) {
                  var5[this.f[var30]] = var5[this.f[var30]] - this.p[var30][var61] * var28;
               }
            }

            var41 = 0.0;

            for (int var59 = 0; var59 < this.b; var59++) {
               double var62 = var5[this.f[var59]];
               var41 += var62 * var62;
            }

            double var60 = var38 / (var2 * var41);
            if (var38 > 0.0) {
               var15 = BY.e(var15, this.h);
            } else if (var38 < 0.0) {
               var50 = BY.d(var50, this.h);
            }

            this.h = BY.e(var15, this.h + var60);
         }
      }
   }

   private void a(double[] var1, double[] var2, double[] var3, double[] var4) {
      for (int var5 = 0; var5 < this.b; var5++) {
         int var6 = this.f[var5];

         for (int var7 = var5 + 1; var7 < this.b; var7++) {
            this.p[var7][var6] = this.p[var5][this.f[var7]];
         }

         this.i[var5] = this.c[var6];
         var4[var5] = var1[var5];
      }

      for (int var27 = 0; var27 < this.b; var27++) {
         int var29 = this.f[var27];
         double var34;
         if ((var34 = var2[var29]) != 0.0) {
            Arrays.fill(var3, var27 + 1, var3.length, 0.0);
         }

         var3[var27] = var34;
         double var9 = 0.0;

         for (int var26 = var27; var26 < this.b; var26++) {
            var29 = this.f[var26];
            if (var3[var26] != 0.0) {
               double var13;
               double var15;
               double var17;
               if (BY.w(var17 = this.p[var26][var29]) < BY.w(var3[var26])) {
                  double var19 = var17 / var3[var26];
                  var15 = (var13 = 1.0 / BY.a(1.0 + var19 * var19)) * var19;
               } else {
                  double var37 = var3[var26] / var17;
                  var13 = (var15 = 1.0 / BY.a(1.0 + var37 * var37)) * var37;
               }

               this.p[var26][var29] = var15 * var17 + var13 * var3[var26];
               double var38 = var15 * var4[var26] + var13 * var9;
               var9 = -var13 * var4[var26] + var15 * var9;
               var4[var26] = var38;

               for (int var35 = var26 + 1; var35 < this.b; var35++) {
                  double var22 = this.p[var35][var29];
                  double var24 = var15 * var22 + var13 * var3[var35];
                  var3[var35] = -var13 * var22 + var15 * var3[var35];
                  this.p[var35][var29] = var24;
               }
            }
         }

         var3[var27] = this.p[var27][this.f[var27]];
         this.p[var27][this.f[var27]] = this.i[var27];
      }

      int var28 = this.b;

      for (int var31 = 0; var31 < this.b; var31++) {
         if (var3[var31] == 0.0 && var28 == this.b) {
            var28 = var31;
         }

         if (var28 < this.b) {
            var4[var31] = 0.0;
         }
      }

      if (var28 > 0) {
         for (int var32 = var28 - 1; var32 >= 0; var32--) {
            int var36 = this.f[var32];
            double var8 = 0.0;

            for (int var10 = var32 + 1; var10 < var28; var10++) {
               var8 += this.p[var10][var36] * var4[var10];
            }

            var4[var32] = (var4[var32] - var8) / var3[var32];
         }
      }

      for (int var33 = 0; var33 < this.i.length; var33++) {
         this.i[this.f[var33]] = var4[var33];
      }
   }

   private void a(tq var1) {
      this.p = var1.b(-1.0).a();
      int var18 = this.p.length;
      int var2 = this.p[0].length;

      for (int var3 = 0; var3 < var2; var3++) {
         this.f[var3] = var3;
         double var5 = 0.0;

         for (int var7 = 0; var7 < var18; var7++) {
            double var8 = this.p[var7][var3];
            var5 += var8 * var8;
         }

         this.d[var3] = BY.a(var5);
      }

      for (int var19 = 0; var19 < var2; var19++) {
         int var20 = -1;
         double var6 = Double.NEGATIVE_INFINITY;

         for (int var23 = var19; var23 < var2; var23++) {
            double var9 = 0.0;

            for (int var11 = var19; var11 < var18; var11++) {
               double var12 = this.p[var11][this.f[var23]];
               var9 += var12 * var12;
            }

            if (Double.isInfinite(var9) || Double.isNaN(var9)) {
               throw new oR(px.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, var18, var2);
            }

            if (var9 > var6) {
               var20 = var23;
               var6 = var9;
            }
         }

         if (var6 <= this.n) {
            this.g = var19;
            return;
         }

         int var24 = this.f[var20];
         this.f[var20] = this.f[var19];
         this.f[var19] = var24;
         double var25;
         double var26 = (var25 = this.p[var19][var24]) > 0.0 ? -BY.a(var6) : BY.a(var6);
         double var13 = 1.0 / (var6 - var25 * var26);
         this.e[var24] = var13;
         this.c[var24] = var26;
         this.p[var19][var24] = this.p[var19][var24] - var26;

         for (int var4 = var2 - 1 - var19; var4 > 0; var4--) {
            double var16 = 0.0;

            for (int var21 = var19; var21 < var18; var21++) {
               var16 += this.p[var21][var24] * this.p[var21][this.f[var19 + var4]];
            }

            var16 *= var13;

            for (int var22 = var19; var22 < var18; var22++) {
               this.p[var22][this.f[var19 + var4]] = this.p[var22][this.f[var19 + var4]] - var16 * this.p[var22][var24];
            }
         }
      }

      this.g = this.b;
   }

   private void f(double[] var1) {
      int var2 = this.p.length;
      int var3 = this.p[0].length;

      for (int var4 = 0; var4 < var3; var4++) {
         int var5 = this.f[var4];
         double var6 = 0.0;

         for (int var8 = var4; var8 < var2; var8++) {
            var6 += this.p[var8][var5] * var1[var8];
         }

         var6 *= this.e[var5];

         for (int var10 = var4; var10 < var2; var10++) {
            var1[var10] -= var6 * this.p[var10][var5];
         }
      }
   }

   private void p() {
      if (this.f() != null || this.g() != null) {
         throw new pb(px.CONSTRAINT);
      }
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      xK var1 = this;
      xK var28 = this;
      if (this.f() == null && var28.g() == null) {
         int var2 = var1.d().length;
         double[] var3;
         int var4 = (var3 = var1.e()).length;
         var1.b = BY.a(var2, var4);
         var1.c = new double[var4];
         var1.d = new double[var4];
         var1.e = new double[var4];
         var1.f = new int[var4];
         var1.i = new double[var4];
         double var5 = 0.0;
         double var7 = 0.0;
         double[] var9 = new double[var4];
         double[] var10 = new double[var4];
         double[] var11 = new double[var2];
         double[] var13 = new double[var2];
         double[] var14 = new double[var4];
         double[] var15 = new double[var4];
         double[] var16 = new double[var4];
         tq var17 = var1.n();
         double[] var18 = var1.b(var3);
         double[] var19 = var1.e(var18);
         wR var20 = new wR(var3, var18);
         double var21 = var1.d(var19);
         var1.h = 0.0;
         boolean var23 = true;
         wK var24 = var1.j();

         while (true) {
            var1.l();
            wR var25 = var20;
            tq var29 = var1.c(var3);
            var28 = var1;
            var1.p = var29.b(-1.0).a();
            int var52 = var28.p.length;
            int var53 = var28.p[0].length;

            for (int var32 = 0; var32 < var53; var32++) {
               var28.f[var32] = var32;
               double var55 = 0.0;

               for (int var57 = 0; var57 < var52; var57++) {
                  double var58 = var28.p[var57][var32];
                  var55 += var58 * var58;
               }

               var28.d[var32] = BY.a(var55);
            }

            int var125 = 0;

            while (true) {
               if (var125 >= var53) {
                  var28.g = var28.b;
                  break;
               }

               int var146 = -1;
               double var56 = Double.NEGATIVE_INFINITY;

               for (int var153 = var125; var153 < var53; var153++) {
                  double var59 = 0.0;

                  for (int var61 = var125; var61 < var52; var61++) {
                     double var62 = var28.p[var61][var28.f[var153]];
                     var59 += var62 * var62;
                  }

                  if (Double.isInfinite(var59) || Double.isNaN(var59)) {
                     throw new oR(px.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, var52, var53);
                  }

                  if (var59 > var56) {
                     var146 = var153;
                     var56 = var59;
                  }
               }

               if (var56 <= var28.n) {
                  var28.g = var125;
                  break;
               }

               int var154 = var28.f[var146];
               var28.f[var146] = var28.f[var125];
               var28.f[var125] = var154;
               double var158;
               double var166 = (var158 = var28.p[var125][var154]) > 0.0 ? -BY.a(var56) : BY.a(var56);
               double var63 = 1.0 / (var56 - var158 * var166);
               var28.e[var154] = var63;
               var28.c[var154] = var166;
               var28.p[var125][var154] = var28.p[var125][var154] - var166;

               for (int var65 = var53 - 1 - var125; var65 > 0; var65--) {
                  double var66 = 0.0;

                  for (int var68 = var125; var68 < var52; var68++) {
                     var66 += var28.p[var68][var154] * var28.p[var68][var28.f[var125 + var65]];
                  }

                  var66 *= var63;

                  for (int var181 = var125; var181 < var52; var181++) {
                     var28.p[var181][var28.f[var125 + var65]] = var28.p[var181][var28.f[var125 + var65]] - var66 * var28.p[var181][var154];
                  }
               }

               var125++;
            }

            var1.o = var17.a(var19);

            for (int var26 = 0; var26 < var2; var26++) {
               var13[var26] = var1.o[var26];
            }

            double[] var119 = var13;
            var28 = var1;
            var52 = var1.p.length;
            var53 = var28.p[0].length;

            for (int var126 = 0; var126 < var53; var126++) {
               int var147 = var28.f[var126];
               double var149 = 0.0;

               for (int var155 = var126; var155 < var52; var155++) {
                  var149 += var28.p[var155][var147] * var119[var155];
               }

               var149 *= var28.e[var147];

               for (int var156 = var126; var156 < var52; var156++) {
                  var119[var156] -= var149 * var28.p[var156][var147];
               }
            }

            for (int var109 = 0; var109 < var1.b; var109++) {
               int var27 = var1.f[var109];
               var1.p[var109][var27] = var1.c[var27];
            }

            if (var23) {
               var7 = 0.0;

               for (int var110 = 0; var110 < var4; var110++) {
                  double var112;
                  if ((var112 = var1.d[var110]) == 0.0) {
                     var112 = 1.0;
                  }

                  double var120 = var112 * var3[var110];
                  var7 += var120 * var120;
                  var9[var110] = var112;
               }

               var5 = (var7 = BY.a(var7)) == 0.0 ? var1.j : var1.j * var7;
            }

            double var111 = 0.0;
            if (var21 != 0.0) {
               for (int var115 = 0; var115 < var1.b; var115++) {
                  int var121 = var1.f[var115];
                  double var30;
                  if ((var30 = var1.d[var121]) != 0.0) {
                     double var127 = 0.0;

                     for (int var34 = 0; var34 <= var115; var34++) {
                        var127 += var1.p[var34][var121] * var13[var34];
                     }

                     var111 = BY.e(var111, BY.w(var127) / (var30 * var21));
                  }
               }
            }

            if (var111 <= var1.m) {
               var1.a(var21);
               return var20;
            }

            for (int var116 = 0; var116 < var4; var116++) {
               var9[var116] = BY.e(var9[var116], var1.d[var116]);
            }

            double var117 = 0.0;

            while (var117 < 1.0E-4) {
               for (int var123 = 0; var123 < var1.b; var123++) {
                  int var31 = var1.f[var123];
                  var10[var31] = var3[var31];
               }

               double var124 = var21;
               double[] var128 = var1.o;
               var1.o = var11;
               var11 = var128;
               double[] var12 = var18;
               double[] var152 = var16;
               double[] var151 = var15;
               double[] var148 = var14;
               double[] var129 = var9;
               double var144 = var5;
               double[] var122 = var13;
               var28 = var1;
               int var157 = var1.p[0].length;

               for (int var159 = 0; var159 < var28.g; var159++) {
                  var28.i[var28.f[var159]] = var122[var159];
               }

               for (int var160 = var28.g; var160 < var157; var160++) {
                  var28.i[var28.f[var160]] = 0.0;
               }

               for (int var161 = var28.g - 1; var161 >= 0; var161--) {
                  int var60 = var28.f[var161];
                  double var167 = var28.i[var60] / var28.c[var60];

                  for (int var171 = 0; var171 < var161; var171++) {
                     var28.i[var28.f[var171]] = var28.i[var28.f[var171]] - var167 * var28.p[var171][var60];
                  }

                  var28.i[var60] = var167;
               }

               double var162 = 0.0;

               for (int var168 = 0; var168 < var28.b; var168++) {
                  int var170 = var28.f[var168];
                  double var172 = var129[var170] * var28.i[var170];
                  var148[var170] = var172;
                  var162 += var172 * var172;
               }

               double var169;
               if ((var169 = (var162 = BY.a(var162)) - var144) <= var144 * 0.1) {
                  var28.h = 0.0;
               } else {
                  double var176 = 0.0;
                  if (var28.g == var28.b) {
                     for (int var67 = 0; var67 < var28.b; var67++) {
                        int var182 = var28.f[var67];
                        var148[var182] *= var129[var182] / var162;
                     }

                     double var173 = 0.0;

                     for (int var178 = 0; var178 < var28.b; var178++) {
                        int var183 = var28.f[var178];
                        double var69 = 0.0;

                        for (int var71 = 0; var71 < var178; var71++) {
                           var69 += var28.p[var71][var183] * var148[var28.f[var71]];
                        }

                        double var188 = (var148[var183] - var69) / var28.c[var183];
                        var148[var183] = var188;
                        var173 += var188 * var188;
                     }

                     var176 = var169 / (var144 * var173);
                  }

                  double var174 = 0.0;

                  for (int var179 = 0; var179 < var28.b; var179++) {
                     int var184 = var28.f[var179];
                     double var185 = 0.0;

                     for (int var189 = 0; var189 <= var179; var189++) {
                        var185 += var28.p[var189][var184] * var122[var189];
                     }

                     var185 /= var129[var184];
                     var174 += var185 * var185;
                  }

                  double var180;
                  double var187;
                  if ((var187 = (var180 = BY.a(var174)) / var144) == 0.0) {
                     var187 = Cq.b / BY.d(var144, 0.1);
                  }

                  var28.h = BY.d(var187, BY.e(var28.h, var176));
                  if (var28.h == 0.0) {
                     var28.h = var180 / var162;
                  }

                  for (int var190 = 10; var190 >= 0; var190--) {
                     if (var28.h == 0.0) {
                        var28.h = BY.e(Cq.b, var187 * 0.001);
                     }

                     double var72 = BY.a(var28.h);

                     for (int var74 = 0; var74 < var28.b; var74++) {
                        int var75 = var28.f[var74];
                        var148[var75] = var72 * var129[var75];
                     }

                     double[] var85 = var152;
                     double[] var84 = var151;
                     double[] var83 = var148;
                     double[] var82 = var122;
                     xK var81 = var28;

                     for (int var86 = 0; var86 < var81.b; var86++) {
                        int var87 = var81.f[var86];

                        for (int var88 = var86 + 1; var88 < var81.b; var88++) {
                           var81.p[var88][var87] = var81.p[var86][var81.f[var88]];
                        }

                        var81.i[var86] = var81.c[var87];
                        var85[var86] = var82[var86];
                     }

                     for (int var200 = 0; var200 < var81.b; var200++) {
                        int var202 = var81.f[var200];
                        double var206;
                        if ((var206 = var83[var202]) != 0.0) {
                           Arrays.fill(var84, var200 + 1, var84.length, 0.0);
                        }

                        var84[var200] = var206;
                        double var90 = 0.0;

                        for (int var92 = var200; var92 < var81.b; var92++) {
                           int var93 = var81.f[var92];
                           if (var84[var92] != 0.0) {
                              double var94;
                              double var96;
                              double var98;
                              if (BY.w(var98 = var81.p[var92][var93]) < BY.w(var84[var92])) {
                                 double var100 = var98 / var84[var92];
                                 var96 = (var94 = 1.0 / BY.a(1.0 + var100 * var100)) * var100;
                              } else {
                                 double var208 = var84[var92] / var98;
                                 var94 = (var96 = 1.0 / BY.a(1.0 + var208 * var208)) * var208;
                              }

                              var81.p[var92][var93] = var96 * var98 + var94 * var84[var92];
                              double var209 = var96 * var85[var92] + var94 * var90;
                              var90 = -var94 * var85[var92] + var96 * var90;
                              var85[var92] = var209;

                              for (int var102 = var92 + 1; var102 < var81.b; var102++) {
                                 double var103 = var81.p[var102][var93];
                                 double var105 = var96 * var103 + var94 * var84[var102];
                                 var84[var102] = -var94 * var103 + var96 * var84[var102];
                                 var81.p[var102][var93] = var105;
                              }
                           }
                        }

                        var84[var200] = var81.p[var200][var81.f[var200]];
                        var81.p[var200][var81.f[var200]] = var81.i[var200];
                     }

                     int var201 = var81.b;

                     for (int var203 = 0; var203 < var81.b; var203++) {
                        if (var84[var203] == 0.0 && var201 == var81.b) {
                           var201 = var203;
                        }

                        if (var201 < var81.b) {
                           var85[var203] = 0.0;
                        }
                     }

                     if (var201 > 0) {
                        for (int var204 = var201 - 1; var204 >= 0; var204--) {
                           int var207 = var81.f[var204];
                           double var89 = 0.0;

                           for (int var91 = var204 + 1; var91 < var201; var91++) {
                              var89 += var81.p[var91][var207] * var85[var91];
                           }

                           var85[var204] = (var85[var204] - var89) / var84[var204];
                        }
                     }

                     for (int var205 = 0; var205 < var81.i.length; var205++) {
                        var81.i[var81.f[var205]] = var85[var205];
                     }

                     var162 = 0.0;

                     for (int var191 = 0; var191 < var28.b; var191++) {
                        int var193 = var28.f[var191];
                        double var76 = var129[var193] * var28.i[var193];
                        var152[var193] = var76;
                        var162 += var76 * var76;
                     }

                     var162 = BY.a(var162);
                     double var192 = var169;
                     if (BY.w(var169 = var162 - var144) <= var144 * 0.1 || var176 == 0.0 && var169 <= var192 && var192 < 0.0) {
                        break;
                     }

                     for (int var194 = 0; var194 < var28.b; var194++) {
                        int var77 = var28.f[var194];
                        var148[var77] = var152[var77] * var129[var77] / var162;
                     }

                     for (int var195 = 0; var195 < var28.b; var195++) {
                        int var198 = var28.f[var195];
                        var148[var198] /= var151[var195];
                        double var78 = var148[var198];

                        for (int var80 = var195 + 1; var80 < var28.b; var80++) {
                           var148[var28.f[var80]] = var148[var28.f[var80]] - var28.p[var80][var198] * var78;
                        }
                     }

                     var174 = 0.0;

                     for (int var196 = 0; var196 < var28.b; var196++) {
                        double var199 = var148[var28.f[var196]];
                        var174 += var199 * var199;
                     }

                     double var197 = var169 / (var144 * var174);
                     if (var169 > 0.0) {
                        var176 = BY.e(var176, var28.h);
                     } else if (var169 < 0.0) {
                        var187 = BY.d(var187, var28.h);
                     }

                     var28.h = BY.e(var176, var28.h + var197);
                  }
               }

               double var33 = 0.0;

               for (int var35 = 0; var35 < var1.b; var35++) {
                  int var36 = var1.f[var35];
                  var1.i[var36] = -var1.i[var36];
                  var3[var36] = var10[var36] + var1.i[var36];
                  double var37 = var9[var36] * var1.i[var36];
                  var33 += var37 * var37;
               }

               var33 = BY.a(var33);
               if (var23) {
                  var5 = BY.d(var5, var33);
               }

               var18 = var1.b(var3);
               var19 = var1.e(var18);
               var20 = new wR(var3, var18);
               var21 = var1.d(var19);
               double var132 = -1.0;
               if (var21 * 0.1 < var124) {
                  double var133 = var21 / var124;
                  var132 = 1.0 - var133 * var133;
               }

               for (int var134 = 0; var134 < var1.b; var134++) {
                  int var38 = var1.f[var134];
                  double var39 = var1.i[var38];
                  var14[var134] = 0.0;

                  for (int var41 = 0; var41 <= var134; var41++) {
                     var14[var41] += var1.p[var41][var38] * var39;
                  }
               }

               double var135 = 0.0;

               for (int var137 = 0; var137 < var1.b; var137++) {
                  var135 += var14[var137] * var14[var137];
               }

               double var138 = var124 * var124;
               var135 /= var138;
               double var139 = var1.h * var33 * var33 / var138;
               double var43 = var135 + var139 * 2.0;
               double var45 = -(var135 + var139);
               double var10000 = var43 == 0.0 ? 0.0 : var132 / var43;
               var117 = var43 == 0.0 ? 0.0 : var132 / var43;
               if (var10000 <= 0.25) {
                  double var47 = var132 < 0.0 ? var45 * 0.5 / (var45 + var132 * 0.5) : 0.5;
                  if (var21 * 0.1 >= var124 || var47 < 0.1) {
                     var47 = 0.1;
                  }

                  var5 = var47 * BY.d(var5, var33 * 10.0);
                  var1.h /= var47;
               } else if (var1.h == 0.0 || var117 >= 0.75) {
                  var5 = var33 * 2.0;
                  var1.h *= 0.5;
               }

               if (var117 >= 1.0E-4) {
                  var23 = false;
                  var7 = 0.0;

                  for (int var141 = 0; var141 < var4; var141++) {
                     double var142 = var9[var141] * var3[var141];
                     var7 += var142 * var142;
                  }

                  var7 = BY.a(var7);
                  if (var24 != null && var24.a(var1.i(), var25, var20)) {
                     var1.a(var21);
                     return var20;
                  }
               } else {
                  var21 = var124;

                  for (int var140 = 0; var140 < var1.b; var140++) {
                     int var48 = var1.f[var140];
                     var3[var48] = var10[var48];
                  }

                  double[] var130 = var1.o;
                  var1.o = var11;
                  var11 = var130;
                  var18 = var12;
                  var20 = new wR(var3, var18);
               }

               if (BY.w(var132) <= var1.k && var43 <= var1.k && var117 <= 2.0 || var5 <= var1.l * var7) {
                  var1.a(var21);
                  return var20;
               }

               if (BY.w(var132) <= a && var43 <= a && var117 <= 2.0) {
                  throw new oR(px.TOO_SMALL_COST_RELATIVE_TOLERANCE, var1.k);
               }

               if (var5 <= a * var7) {
                  throw new oR(px.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, var1.l);
               }

               if (var111 <= a) {
                  throw new oR(px.TOO_SMALL_ORTHOGONALITY_TOLERANCE, var1.m);
               }
            }
         }
      } else {
         throw new pb(px.CONSTRAINT);
      }
   }
}
