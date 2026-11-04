package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

@Deprecated
public final class yT extends yP {
   private int c;
   private double[] d;
   private double[] e;
   private double[] f;
   private int[] g;
   private int h;
   private double i;
   private double[] j;
   private final double k = 100.0;
   private final double l;
   private final double m;
   private final double n;
   private final double o;
   private double[] p;
   private double[][] q;

   public yT() {
      this(100.0, 1.0E-10, 1.0E-10, 1.0E-10, Cq.b);
   }

   private yT(ya<yq> var1) {
      this(100.0, var1, 1.0E-10, 1.0E-10, 1.0E-10, Cq.b);
   }

   private yT(double var1, ya<yq> var3, double var4, double var6, double var8, double var10) {
      super(var3);
      this.l = 1.0E-10;
      this.m = 1.0E-10;
      this.n = 1.0E-10;
      this.o = var10;
   }

   private yT(double var1, double var3, double var5) {
      this(100.0, var1, var3, var5, Cq.b);
   }

   private yT(double var1, double var3, double var5, double var7, double var9) {
      super(null);
      this.l = var3;
      this.m = var5;
      this.n = var7;
      this.o = var9;
   }

   @Override
   public final yq h() {
      int var1 = this.f().length;
      double[] var2;
      int var3 = (var2 = this.d()).length;
      this.c = BY.a(var1, var3);
      this.d = new double[var3];
      this.e = new double[var3];
      this.f = new double[var3];
      this.g = new int[var3];
      this.j = new double[var3];
      double var4 = 0.0;
      double var6 = 0.0;
      double[] var8 = new double[var3];
      double[] var9 = new double[var3];
      double[] var10 = new double[var1];
      double[] var12 = new double[var1];
      double[] var13 = new double[var3];
      double[] var14 = new double[var3];
      double[] var15 = new double[var3];
      tq var16 = this.j();
      double[] var17 = this.a(var2);
      double[] var18 = this.d(var17);
      yq var19 = new yq(var2, var17);
      double var20 = this.c(var18);
      this.i = 0.0;
      boolean var22 = true;
      int var23 = 0;
      ya var24 = this.c();

      while (true) {
         var23++;
         yq var25 = var19;
         tq var29 = this.b(var2);
         yT var28 = this;
         this.q = var29.b(-1.0).a();
         int var52 = var28.q.length;
         int var53 = var28.q[0].length;

         for (int var32 = 0; var32 < var53; var32++) {
            var28.g[var32] = var32;
            double var55 = 0.0;

            for (int var57 = 0; var57 < var52; var57++) {
               double var58 = var28.q[var57][var32];
               var55 += var58 * var58;
            }

            var28.e[var32] = BY.a(var55);
         }

         int var124 = 0;

         while (true) {
            if (var124 >= var53) {
               var28.h = var28.c;
               break;
            }

            int var145 = -1;
            double var56 = Double.NEGATIVE_INFINITY;

            for (int var152 = var124; var152 < var53; var152++) {
               double var59 = 0.0;

               for (int var61 = var124; var61 < var52; var61++) {
                  double var62 = var28.q[var61][var28.g[var152]];
                  var59 += var62 * var62;
               }

               if (Double.isInfinite(var59) || Double.isNaN(var59)) {
                  throw new oR(px.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, var52, var53);
               }

               if (var59 > var56) {
                  var145 = var152;
                  var56 = var59;
               }
            }

            if (var56 <= var28.o) {
               var28.h = var124;
               break;
            }

            int var153 = var28.g[var145];
            var28.g[var145] = var28.g[var124];
            var28.g[var124] = var153;
            double var157;
            double var165 = (var157 = var28.q[var124][var153]) > 0.0 ? -BY.a(var56) : BY.a(var56);
            double var63 = 1.0 / (var56 - var157 * var165);
            var28.f[var153] = var63;
            var28.d[var153] = var165;
            var28.q[var124][var153] = var28.q[var124][var153] - var165;

            for (int var65 = var53 - 1 - var124; var65 > 0; var65--) {
               double var66 = 0.0;

               for (int var68 = var124; var68 < var52; var68++) {
                  var66 += var28.q[var68][var153] * var28.q[var68][var28.g[var124 + var65]];
               }

               var66 *= var63;

               for (int var180 = var124; var180 < var52; var180++) {
                  var28.q[var180][var28.g[var124 + var65]] = var28.q[var180][var28.g[var124 + var65]] - var66 * var28.q[var180][var153];
               }
            }

            var124++;
         }

         this.p = var16.a(var18);

         for (int var26 = 0; var26 < var1; var26++) {
            var12[var26] = this.p[var26];
         }

         double[] var118 = var12;
         var28 = this;
         var52 = this.q.length;
         var53 = var28.q[0].length;

         for (int var125 = 0; var125 < var53; var125++) {
            int var146 = var28.g[var125];
            double var148 = 0.0;

            for (int var154 = var125; var154 < var52; var154++) {
               var148 += var28.q[var154][var146] * var118[var154];
            }

            var148 *= var28.f[var146];

            for (int var155 = var125; var155 < var52; var155++) {
               var118[var155] -= var148 * var28.q[var155][var146];
            }
         }

         for (int var109 = 0; var109 < this.c; var109++) {
            int var27 = this.g[var109];
            this.q[var109][var27] = this.d[var27];
         }

         if (var22) {
            var6 = 0.0;

            for (int var110 = 0; var110 < var3; var110++) {
               double var112;
               if ((var112 = this.e[var110]) == 0.0) {
                  var112 = 1.0;
               }

               double var119 = var112 * var2[var110];
               var6 += var119 * var119;
               var8[var110] = var112;
            }

            var4 = (var6 = BY.a(var6)) == 0.0 ? this.k : this.k * var6;
         }

         double var111 = 0.0;
         if (var20 != 0.0) {
            for (int var114 = 0; var114 < this.c; var114++) {
               int var120 = this.g[var114];
               double var30;
               if ((var30 = this.e[var120]) != 0.0) {
                  double var126 = 0.0;

                  for (int var34 = 0; var34 <= var114; var34++) {
                     var126 += this.q[var34][var120] * var12[var34];
                  }

                  var111 = BY.e(var111, BY.w(var126) / (var30 * var20));
               }
            }
         }

         if (var111 <= this.n) {
            this.a(var20);
            super.a = var19.a();
            return var19;
         }

         for (int var115 = 0; var115 < var3; var115++) {
            var8[var115] = BY.e(var8[var115], this.e[var115]);
         }

         double var116 = 0.0;

         while (var116 < 1.0E-4) {
            for (int var122 = 0; var122 < this.c; var122++) {
               int var31 = this.g[var122];
               var9[var31] = var2[var31];
            }

            double var123 = var20;
            double[] var127 = this.p;
            this.p = var10;
            var10 = var127;
            double[] var11 = var17;
            double[] var151 = var15;
            double[] var150 = var14;
            double[] var147 = var13;
            double[] var128 = var8;
            double var143 = var4;
            double[] var121 = var12;
            var28 = this;
            int var156 = this.q[0].length;

            for (int var158 = 0; var158 < var28.h; var158++) {
               var28.j[var28.g[var158]] = var121[var158];
            }

            for (int var159 = var28.h; var159 < var156; var159++) {
               var28.j[var28.g[var159]] = 0.0;
            }

            for (int var160 = var28.h - 1; var160 >= 0; var160--) {
               int var60 = var28.g[var160];
               double var166 = var28.j[var60] / var28.d[var60];

               for (int var170 = 0; var170 < var160; var170++) {
                  var28.j[var28.g[var170]] = var28.j[var28.g[var170]] - var166 * var28.q[var170][var60];
               }

               var28.j[var60] = var166;
            }

            double var161 = 0.0;

            for (int var167 = 0; var167 < var28.c; var167++) {
               int var169 = var28.g[var167];
               double var171 = var128[var169] * var28.j[var169];
               var147[var169] = var171;
               var161 += var171 * var171;
            }

            double var168;
            if ((var168 = (var161 = BY.a(var161)) - var143) <= var143 * 0.1) {
               var28.i = 0.0;
            } else {
               double var175 = 0.0;
               if (var28.h == var28.c) {
                  for (int var67 = 0; var67 < var28.c; var67++) {
                     int var181 = var28.g[var67];
                     var147[var181] *= var128[var181] / var161;
                  }

                  double var172 = 0.0;

                  for (int var177 = 0; var177 < var28.c; var177++) {
                     int var182 = var28.g[var177];
                     double var69 = 0.0;

                     for (int var71 = 0; var71 < var177; var71++) {
                        var69 += var28.q[var71][var182] * var147[var28.g[var71]];
                     }

                     double var187 = (var147[var182] - var69) / var28.d[var182];
                     var147[var182] = var187;
                     var172 += var187 * var187;
                  }

                  var175 = var168 / (var143 * var172);
               }

               double var173 = 0.0;

               for (int var178 = 0; var178 < var28.c; var178++) {
                  int var183 = var28.g[var178];
                  double var184 = 0.0;

                  for (int var188 = 0; var188 <= var178; var188++) {
                     var184 += var28.q[var188][var183] * var121[var188];
                  }

                  var184 /= var128[var183];
                  var173 += var184 * var184;
               }

               double var179;
               double var186;
               if ((var186 = (var179 = BY.a(var173)) / var143) == 0.0) {
                  var186 = 2.2251E-308 / BY.d(var143, 0.1);
               }

               var28.i = BY.d(var186, BY.e(var28.i, var175));
               if (var28.i == 0.0) {
                  var28.i = var179 / var161;
               }

               for (int var189 = 10; var189 >= 0; var189--) {
                  if (var28.i == 0.0) {
                     var28.i = BY.e(2.2251E-308, var186 * 0.001);
                  }

                  double var72 = BY.a(var28.i);

                  for (int var74 = 0; var74 < var28.c; var74++) {
                     int var75 = var28.g[var74];
                     var147[var75] = var72 * var128[var75];
                  }

                  double[] var85 = var151;
                  double[] var84 = var150;
                  double[] var83 = var147;
                  double[] var82 = var121;
                  yT var81 = var28;

                  for (int var86 = 0; var86 < var81.c; var86++) {
                     int var87 = var81.g[var86];

                     for (int var88 = var86 + 1; var88 < var81.c; var88++) {
                        var81.q[var88][var87] = var81.q[var86][var81.g[var88]];
                     }

                     var81.j[var86] = var81.d[var87];
                     var85[var86] = var82[var86];
                  }

                  for (int var199 = 0; var199 < var81.c; var199++) {
                     int var201 = var81.g[var199];
                     double var205;
                     if ((var205 = var83[var201]) != 0.0) {
                        Arrays.fill(var84, var199 + 1, var84.length, 0.0);
                     }

                     var84[var199] = var205;
                     double var90 = 0.0;

                     for (int var92 = var199; var92 < var81.c; var92++) {
                        int var93 = var81.g[var92];
                        if (var84[var92] != 0.0) {
                           double var94;
                           double var96;
                           double var98;
                           if (BY.w(var98 = var81.q[var92][var93]) < BY.w(var84[var92])) {
                              double var100 = var98 / var84[var92];
                              var96 = (var94 = 1.0 / BY.a(1.0 + var100 * var100)) * var100;
                           } else {
                              double var207 = var84[var92] / var98;
                              var94 = (var96 = 1.0 / BY.a(1.0 + var207 * var207)) * var207;
                           }

                           var81.q[var92][var93] = var96 * var98 + var94 * var84[var92];
                           double var208 = var96 * var85[var92] + var94 * var90;
                           var90 = -var94 * var85[var92] + var96 * var90;
                           var85[var92] = var208;

                           for (int var102 = var92 + 1; var102 < var81.c; var102++) {
                              double var103 = var81.q[var102][var93];
                              double var105 = var96 * var103 + var94 * var84[var102];
                              var84[var102] = -var94 * var103 + var96 * var84[var102];
                              var81.q[var102][var93] = var105;
                           }
                        }
                     }

                     var84[var199] = var81.q[var199][var81.g[var199]];
                     var81.q[var199][var81.g[var199]] = var81.j[var199];
                  }

                  int var200 = var81.c;

                  for (int var202 = 0; var202 < var81.c; var202++) {
                     if (var84[var202] == 0.0 && var200 == var81.c) {
                        var200 = var202;
                     }

                     if (var200 < var81.c) {
                        var85[var202] = 0.0;
                     }
                  }

                  if (var200 > 0) {
                     for (int var203 = var200 - 1; var203 >= 0; var203--) {
                        int var206 = var81.g[var203];
                        double var89 = 0.0;

                        for (int var91 = var203 + 1; var91 < var200; var91++) {
                           var89 += var81.q[var91][var206] * var85[var91];
                        }

                        var85[var203] = (var85[var203] - var89) / var84[var203];
                     }
                  }

                  for (int var204 = 0; var204 < var81.j.length; var204++) {
                     var81.j[var81.g[var204]] = var85[var204];
                  }

                  var161 = 0.0;

                  for (int var190 = 0; var190 < var28.c; var190++) {
                     int var192 = var28.g[var190];
                     double var76 = var128[var192] * var28.j[var192];
                     var151[var192] = var76;
                     var161 += var76 * var76;
                  }

                  var161 = BY.a(var161);
                  double var191 = var168;
                  if (BY.w(var168 = var161 - var143) <= var143 * 0.1 || var175 == 0.0 && var168 <= var191 && var191 < 0.0) {
                     break;
                  }

                  for (int var193 = 0; var193 < var28.c; var193++) {
                     int var77 = var28.g[var193];
                     var147[var77] = var151[var77] * var128[var77] / var161;
                  }

                  for (int var194 = 0; var194 < var28.c; var194++) {
                     int var197 = var28.g[var194];
                     var147[var197] /= var150[var194];
                     double var78 = var147[var197];

                     for (int var80 = var194 + 1; var80 < var28.c; var80++) {
                        var147[var28.g[var80]] = var147[var28.g[var80]] - var28.q[var80][var197] * var78;
                     }
                  }

                  var173 = 0.0;

                  for (int var195 = 0; var195 < var28.c; var195++) {
                     double var198 = var147[var28.g[var195]];
                     var173 += var198 * var198;
                  }

                  double var196 = var168 / (var143 * var173);
                  if (var168 > 0.0) {
                     var175 = BY.e(var175, var28.i);
                  } else if (var168 < 0.0) {
                     var186 = BY.d(var186, var28.i);
                  }

                  var28.i = BY.e(var175, var28.i + var196);
               }
            }

            double var33 = 0.0;

            for (int var35 = 0; var35 < this.c; var35++) {
               int var36 = this.g[var35];
               this.j[var36] = -this.j[var36];
               var2[var36] = var9[var36] + this.j[var36];
               double var37 = var8[var36] * this.j[var36];
               var33 += var37 * var37;
            }

            var33 = BY.a(var33);
            if (var22) {
               var4 = BY.d(var4, var33);
            }

            var17 = this.a(var2);
            var18 = this.d(var17);
            var19 = new yq(var2, var17);
            var20 = this.c(var18);
            double var131 = -1.0;
            if (var20 * 0.1 < var123) {
               double var132 = var20 / var123;
               var131 = 1.0 - var132 * var132;
            }

            for (int var133 = 0; var133 < this.c; var133++) {
               int var38 = this.g[var133];
               double var39 = this.j[var38];
               var13[var133] = 0.0;

               for (int var41 = 0; var41 <= var133; var41++) {
                  var13[var41] += this.q[var41][var38] * var39;
               }
            }

            double var134 = 0.0;

            for (int var136 = 0; var136 < this.c; var136++) {
               var134 += var13[var136] * var13[var136];
            }

            double var137 = var123 * var123;
            var134 /= var137;
            double var138 = this.i * var33 * var33 / var137;
            double var43 = var134 + var138 * 2.0;
            double var45 = -(var134 + var138);
            double var10000 = var43 == 0.0 ? 0.0 : var131 / var43;
            var116 = var43 == 0.0 ? 0.0 : var131 / var43;
            if (var10000 <= 0.25) {
               double var47 = var131 < 0.0 ? var45 * 0.5 / (var45 + var131 * 0.5) : 0.5;
               if (var20 * 0.1 >= var123 || var47 < 0.1) {
                  var47 = 0.1;
               }

               var4 = var47 * BY.d(var4, var33 * 10.0);
               this.i /= var47;
            } else if (this.i == 0.0 || var116 >= 0.75) {
               var4 = var33 * 2.0;
               this.i *= 0.5;
            }

            if (var116 >= 1.0E-4) {
               var22 = false;
               var6 = 0.0;

               for (int var140 = 0; var140 < var3; var140++) {
                  double var141 = var8[var140] * var2[var140];
                  var6 += var141 * var141;
               }

               var6 = BY.a(var6);
               if (var24 != null && var24.a(var23, var25, var19)) {
                  this.a(var20);
                  super.a = var19.a();
                  return var19;
               }
            } else {
               var20 = var123;

               for (int var139 = 0; var139 < this.c; var139++) {
                  int var48 = this.g[var139];
                  var2[var48] = var9[var48];
               }

               double[] var129 = this.p;
               this.p = var10;
               var10 = var129;
               var17 = var11;
               var19 = new yq(var2, var17);
            }

            if (BY.w(var131) <= this.l && var43 <= this.l && var116 <= 2.0 || var4 <= this.m * var6) {
               this.a(var20);
               super.a = var19.a();
               return var19;
            }

            if (BY.w(var131) <= 2.2204E-16 && var43 <= 2.2204E-16 && var116 <= 2.0) {
               throw new oR(px.TOO_SMALL_COST_RELATIVE_TOLERANCE, this.l);
            }

            if (var4 <= var6 * 2.2204E-16) {
               throw new oR(px.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, this.m);
            }

            if (var111 <= 2.2204E-16) {
               throw new oR(px.TOO_SMALL_ORTHOGONALITY_TOLERANCE, this.n);
            }
         }
      }
   }

   private void a(double[] var1, double var2, double[] var4, double[] var5, double[] var6, double[] var7) {
      int var8 = this.q[0].length;

      for (int var9 = 0; var9 < this.h; var9++) {
         this.j[this.g[var9]] = var1[var9];
      }

      for (int var31 = this.h; var31 < var8; var31++) {
         this.j[this.g[var31]] = 0.0;
      }

      for (int var32 = this.h - 1; var32 >= 0; var32--) {
         int var10 = this.g[var32];
         double var11 = this.j[var10] / this.d[var10];

         for (int var13 = 0; var13 < var32; var13++) {
            this.j[this.g[var13]] = this.j[this.g[var13]] - var11 * this.q[var13][var10];
         }

         this.j[var10] = var11;
      }

      double var33 = 0.0;

      for (int var37 = 0; var37 < this.c; var37++) {
         int var12 = this.g[var37];
         double var39 = var4[var12] * this.j[var12];
         var5[var12] = var39;
         var33 += var39 * var39;
      }

      double var38;
      if ((var38 = (var33 = BY.a(var33)) - var2) <= var2 * 0.1) {
         this.i = 0.0;
      } else {
         double var15 = 0.0;
         if (this.h == this.c) {
            for (int var17 = 0; var17 < this.c; var17++) {
               int var18 = this.g[var17];
               var5[var18] *= var4[var18] / var33;
            }

            double var40 = 0.0;

            for (int var43 = 0; var43 < this.c; var43++) {
               int var46 = this.g[var43];
               double var19 = 0.0;

               for (int var21 = 0; var21 < var43; var21++) {
                  var19 += this.q[var21][var46] * var5[this.g[var21]];
               }

               double var51 = (var5[var46] - var19) / this.d[var46];
               var5[var46] = var51;
               var40 += var51 * var51;
            }

            var15 = var38 / (var2 * var40);
         }

         double var41 = 0.0;

         for (int var44 = 0; var44 < this.c; var44++) {
            int var47 = this.g[var44];
            double var48 = 0.0;

            for (int var52 = 0; var52 <= var44; var52++) {
               var48 += this.q[var52][var47] * var1[var52];
            }

            var48 /= var4[var47];
            var41 += var48 * var48;
         }

         double var45;
         double var50;
         if ((var50 = (var45 = BY.a(var41)) / var2) == 0.0) {
            var50 = 2.2251E-308 / BY.d(var2, 0.1);
         }

         this.i = BY.d(var50, BY.e(this.i, var15));
         if (this.i == 0.0) {
            this.i = var45 / var33;
         }

         for (int var53 = 10; var53 >= 0; var53--) {
            if (this.i == 0.0) {
               this.i = BY.e(2.2251E-308, var50 * 0.001);
            }

            double var22 = BY.a(this.i);

            for (int var24 = 0; var24 < this.c; var24++) {
               int var25 = this.g[var24];
               var5[var25] = var22 * var4[var25];
            }

            this.a(var1, var5, var6, var7);
            var33 = 0.0;

            for (int var54 = 0; var54 < this.c; var54++) {
               int var56 = this.g[var54];
               double var26 = var4[var56] * this.j[var56];
               var7[var56] = var26;
               var33 += var26 * var26;
            }

            var33 = BY.a(var33);
            double var55 = var38;
            if (BY.w(var38 = var33 - var2) <= var2 * 0.1 || var15 == 0.0 && var38 <= var55 && var55 < 0.0) {
               return;
            }

            for (int var57 = 0; var57 < this.c; var57++) {
               int var27 = this.g[var57];
               var5[var27] = var7[var27] * var4[var27] / var33;
            }

            for (int var58 = 0; var58 < this.c; var58++) {
               int var61 = this.g[var58];
               var5[var61] /= var6[var58];
               double var28 = var5[var61];

               for (int var30 = var58 + 1; var30 < this.c; var30++) {
                  var5[this.g[var30]] = var5[this.g[var30]] - this.q[var30][var61] * var28;
               }
            }

            var41 = 0.0;

            for (int var59 = 0; var59 < this.c; var59++) {
               double var62 = var5[this.g[var59]];
               var41 += var62 * var62;
            }

            double var60 = var38 / (var2 * var41);
            if (var38 > 0.0) {
               var15 = BY.e(var15, this.i);
            } else if (var38 < 0.0) {
               var50 = BY.d(var50, this.i);
            }

            this.i = BY.e(var15, this.i + var60);
         }
      }
   }

   private void a(double[] var1, double[] var2, double[] var3, double[] var4) {
      for (int var5 = 0; var5 < this.c; var5++) {
         int var6 = this.g[var5];

         for (int var7 = var5 + 1; var7 < this.c; var7++) {
            this.q[var7][var6] = this.q[var5][this.g[var7]];
         }

         this.j[var5] = this.d[var6];
         var4[var5] = var1[var5];
      }

      for (int var27 = 0; var27 < this.c; var27++) {
         int var29 = this.g[var27];
         double var34;
         if ((var34 = var2[var29]) != 0.0) {
            Arrays.fill(var3, var27 + 1, var3.length, 0.0);
         }

         var3[var27] = var34;
         double var9 = 0.0;

         for (int var26 = var27; var26 < this.c; var26++) {
            var29 = this.g[var26];
            if (var3[var26] != 0.0) {
               double var13;
               double var15;
               double var17;
               if (BY.w(var17 = this.q[var26][var29]) < BY.w(var3[var26])) {
                  double var19 = var17 / var3[var26];
                  var15 = (var13 = 1.0 / BY.a(1.0 + var19 * var19)) * var19;
               } else {
                  double var37 = var3[var26] / var17;
                  var13 = (var15 = 1.0 / BY.a(1.0 + var37 * var37)) * var37;
               }

               this.q[var26][var29] = var15 * var17 + var13 * var3[var26];
               double var38 = var15 * var4[var26] + var13 * var9;
               var9 = -var13 * var4[var26] + var15 * var9;
               var4[var26] = var38;

               for (int var35 = var26 + 1; var35 < this.c; var35++) {
                  double var22 = this.q[var35][var29];
                  double var24 = var15 * var22 + var13 * var3[var35];
                  var3[var35] = -var13 * var22 + var15 * var3[var35];
                  this.q[var35][var29] = var24;
               }
            }
         }

         var3[var27] = this.q[var27][this.g[var27]];
         this.q[var27][this.g[var27]] = this.j[var27];
      }

      int var28 = this.c;

      for (int var31 = 0; var31 < this.c; var31++) {
         if (var3[var31] == 0.0 && var28 == this.c) {
            var28 = var31;
         }

         if (var28 < this.c) {
            var4[var31] = 0.0;
         }
      }

      if (var28 > 0) {
         for (int var32 = var28 - 1; var32 >= 0; var32--) {
            int var36 = this.g[var32];
            double var8 = 0.0;

            for (int var10 = var32 + 1; var10 < var28; var10++) {
               var8 += this.q[var10][var36] * var4[var10];
            }

            var4[var32] = (var4[var32] - var8) / var3[var32];
         }
      }

      for (int var33 = 0; var33 < this.j.length; var33++) {
         this.j[this.g[var33]] = var4[var33];
      }
   }

   private void a(tq var1) {
      this.q = var1.b(-1.0).a();
      int var18 = this.q.length;
      int var2 = this.q[0].length;

      for (int var3 = 0; var3 < var2; var3++) {
         this.g[var3] = var3;
         double var5 = 0.0;

         for (int var7 = 0; var7 < var18; var7++) {
            double var8 = this.q[var7][var3];
            var5 += var8 * var8;
         }

         this.e[var3] = BY.a(var5);
      }

      for (int var19 = 0; var19 < var2; var19++) {
         int var20 = -1;
         double var6 = Double.NEGATIVE_INFINITY;

         for (int var23 = var19; var23 < var2; var23++) {
            double var9 = 0.0;

            for (int var11 = var19; var11 < var18; var11++) {
               double var12 = this.q[var11][this.g[var23]];
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

         if (var6 <= this.o) {
            this.h = var19;
            return;
         }

         int var24 = this.g[var20];
         this.g[var20] = this.g[var19];
         this.g[var19] = var24;
         double var25;
         double var26 = (var25 = this.q[var19][var24]) > 0.0 ? -BY.a(var6) : BY.a(var6);
         double var13 = 1.0 / (var6 - var25 * var26);
         this.f[var24] = var13;
         this.d[var24] = var26;
         this.q[var19][var24] = this.q[var19][var24] - var26;

         for (int var4 = var2 - 1 - var19; var4 > 0; var4--) {
            double var16 = 0.0;

            for (int var21 = var19; var21 < var18; var21++) {
               var16 += this.q[var21][var24] * this.q[var21][this.g[var19 + var4]];
            }

            var16 *= var13;

            for (int var22 = var19; var22 < var18; var22++) {
               this.q[var22][this.g[var19 + var4]] = this.q[var22][this.g[var19 + var4]] - var16 * this.q[var22][var24];
            }
         }
      }

      this.h = this.c;
   }

   private void e(double[] var1) {
      int var2 = this.q.length;
      int var3 = this.q[0].length;

      for (int var4 = 0; var4 < var3; var4++) {
         int var5 = this.g[var4];
         double var6 = 0.0;

         for (int var8 = var4; var8 < var2; var8++) {
            var6 += this.q[var8][var5] * var1[var8];
         }

         var6 *= this.f[var5];

         for (int var10 = var4; var10 < var2; var10++) {
            var1[var10] -= var6 * this.q[var10][var5];
         }
      }
   }
}
