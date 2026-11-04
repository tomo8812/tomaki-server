package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class yy extends yA<kW> implements yn {
   private static int b;
   private static double c;
   private static double d;
   private static final double e = 0.0;
   private static final double f = 1.0;
   private static final double g = 2.0;
   private static final double h = 10.0;
   private static final double i = 16.0;
   private static final double j = 250.0;
   private static final double k = -1.0;
   private static final double l = 0.5;
   private static final double m = 0.25;
   private static final double n = 0.125;
   private static final double o = 0.1;
   private static final double p = 0.001;
   private final int q;
   private double r;
   private final double s;
   private boolean t;
   private sA u;
   private double[] v;
   private int w;
   private sy x;
   private sy y;
   private sy z;
   private sA A;
   private sA B;
   private sA C;
   private sA D;
   private sA E;
   private sA F;
   private sA G;
   private sA H;
   private sA I;
   private sA J;
   private sA K;
   private sA L;

   private yy(int var1) {
      this(var1, 10.0, 1.0E-8);
   }

   private yy(int var1, double var2, double var4) {
      super(null);
      this.q = var1;
      this.r = 10.0;
      this.s = 1.0E-8;
   }

   @Override
   protected final yp d() {
      double[] var1 = this.g();
      double[] var2 = this.h();
      double[] var5 = var2;
      double[] var4 = var1;
      yy var3 = this;
      int var7;
      if ((var7 = this.f().length) < 2) {
         throw new pn(var7, 2, true);
      }

      int[] var10 = new int[]{var7 + 2, (var7 + 2) * (var7 + 1) / 2};
      if (var3.q >= var10[0] && var3.q <= var10[1]) {
         var3.v = new double[var7];
         double var11 = 2.0 * var3.r;
         double var13 = Double.POSITIVE_INFINITY;

         for (int var15 = 0; var15 < var7; var15++) {
            var3.v[var15] = var5[var15] - var4[var15];
            var13 = BY.d(var13, var3.v[var15]);
         }

         if (var13 < var11) {
            var3.r = var13 / 3.0;
         }

         var3.x = new sy(var7 + var3.q, var7);
         var3.y = new sy(var3.q, var3.q - var7 - 1);
         var3.z = new sy(var3.q, var7);
         var3.A = new sA(var7);
         var3.B = new sA(var3.q);
         var3.C = new sA(var7);
         var3.D = new sA(var7);
         var3.E = new sA(var7);
         var3.F = new sA(var7);
         var3.G = new sA(var3.q);
         var3.H = new sA(var7);
         var3.I = new sA(var7);
         var3.J = new sA(var7);
         var3.K = new sA(var7 + var3.q);
         var3.L = new sA(var7 * (var7 + 1) / 2);
         this.t = this.e() == yf.MINIMIZE;
         this.u = new sA(this.f());
         var5 = var2;
         var4 = var1;
         var3 = this;
         sA var102 = this.u;
         int var6 = this.u.a.length;

         for (int var114 = 0; var114 < var6; var114++) {
            double var116 = var3.v[var114];
            var3.E.a(var114, var4[var114] - var3.u.a(var114));
            var3.F.a(var114, var5[var114] - var3.u.a(var114));
            if (var3.E.a(var114) >= -var3.r) {
               if (var3.E.a(var114) >= 0.0) {
                  var3.u.a(var114, var4[var114]);
                  var3.E.a(var114, 0.0);
                  var3.F.a(var114, var116);
               } else {
                  var3.u.a(var114, var4[var114] + var3.r);
                  var3.E.a(var114, -var3.r);
                  double var12 = var5[var114] - var3.u.a(var114);
                  var3.F.a(var114, BY.e(var12, var3.r));
               }
            } else if (var3.F.a(var114) <= var3.r) {
               if (var3.F.a(var114) <= 0.0) {
                  var3.u.a(var114, var5[var114]);
                  var3.E.a(var114, -var116);
                  var3.F.a(var114, 0.0);
               } else {
                  var3.u.a(var114, var5[var114] - var3.r);
                  double var119 = var4[var114] - var3.u.a(var114);
                  double var14 = -var3.r;
                  var3.E.a(var114, BY.d(var119, var14));
                  var3.F.a(var114, var3.r);
               }
            }
         }

         double[] var106 = var5;
         var2 = var4;
         yy var103 = var3;
         int var109 = var3.u.a.length;
         int var111 = var103.q;
         var6 = var109 + 1;
         var7 = var111 - var6;
         var6 = var109 * var6 / 2;
         sA var8 = new sA(var109);
         sA var9 = new sA(var111);
         sA var117 = new sA(var111);
         double var27 = Double.NaN;
         double var29 = Double.NaN;
         double var31 = Double.NaN;
         var103.w = 0;
         var103.c(var2, var106);
         double var35 = 0.0;

         for (int var37 = 0; var37 < var109; var37++) {
            var103.C.a(var37, var103.z.b(var103.w, var37));
            double var38 = var103.C.a(var37);
            var35 += var38 * var38;
         }

         double var123 = var103.B.a(0);
         int var118 = 0;
         int var120 = 0;
         int var121 = 0;
         int var122 = var103.b();
         double var44 = var103.r;
         double var46 = var103.r;
         double var48 = 0.0;
         double var50 = 0.0;
         double var52 = 0.0;
         double var54 = 0.0;
         double var56 = 0.0;
         double var58 = 0.0;
         double var60 = 0.0;
         double var62 = 0.0;
         double var64 = 0.0;
         double var70 = 0.0;
         short var72 = 20;

         label903:
         while (true) {
            label901:
            while (true) {
               label899:
               while (true) {
                  label897:
                  while (true) {
                     label894:
                     while (true) {
                        switch (var72) {
                           case 20:
                              if (var103.w != 0) {
                                 int var137 = 0;

                                 for (int var145 = 0; var145 < var109; var145++) {
                                    for (int var149 = 0; var149 <= var145; var149++) {
                                       if (var149 < var145) {
                                          var103.D.a(var145, var103.D.a(var145) + var103.L.a(var137) * var103.C.a(var149));
                                       }

                                       var103.D.a(var149, var103.D.a(var149) + var103.L.a(var137) * var103.C.a(var145));
                                       var137++;
                                    }
                                 }

                                 if (var103.b() > var111) {
                                    for (int var146 = 0; var146 < var111; var146++) {
                                       double var150 = 0.0;

                                       for (int var166 = 0; var166 < var109; var166++) {
                                          var150 += var103.z.b(var146, var166) * var103.C.a(var166);
                                       }

                                       var150 *= var103.G.a(var146);

                                       for (int var167 = 0; var167 < var109; var167++) {
                                          var103.D.a(var167, var103.D.a(var167) + var150 * var103.z.b(var146, var167));
                                       }
                                    }
                                 }
                              }
                           case 60:
                              break label897;
                           case 90:
                              break label901;
                           case 210:
                              double[] var73;
                              var29 = (var73 = var103.a(var121, var58))[0];
                              var27 = var73[1];

                              for (int var74 = 0; var74 < var109; var74++) {
                                 var103.J.a(var74, var103.H.a(var74) - var103.C.a(var74));
                              }
                           case 230:
                              for (int var128 = 0; var128 < var111; var128++) {
                                 double var140 = 0.0;
                                 double var76 = 0.0;
                                 double var78 = 0.0;

                                 for (int var80 = 0; var80 < var109; var80++) {
                                    var140 += var103.z.b(var128, var80) * var103.J.a(var80);
                                    var76 += var103.z.b(var128, var80) * var103.C.a(var80);
                                    var78 += var103.x.b(var128, var80) * var103.J.a(var80);
                                 }

                                 var117.a(var128, var140 * (var140 * 0.5 + var76));
                                 var103.K.a(var128, var78);
                                 var9.a(var128, var140);
                              }

                              var56 = 0.0;

                              for (int var129 = 0; var129 < var7; var129++) {
                                 double var141 = 0.0;

                                 for (int var154 = 0; var154 < var111; var154++) {
                                    var141 += var103.y.b(var154, var129) * var117.a(var154);
                                 }

                                 var56 -= var141 * var141;

                                 for (int var155 = 0; var155 < var111; var155++) {
                                    var103.K.a(var155, var103.K.a(var155) + var141 * var103.y.b(var155, var129));
                                 }
                              }

                              var31 = 0.0;
                              double var130 = 0.0;
                              double var75 = 0.0;

                              for (int var77 = 0; var77 < var109; var77++) {
                                 double var172 = var103.J.a(var77);
                                 var31 += var172 * var172;
                                 double var190 = 0.0;

                                 for (int var82 = 0; var82 < var111; var82++) {
                                    var190 += var117.a(var82) * var103.x.b(var82, var77);
                                 }

                                 var130 += var190 * var103.J.a(var77);
                                 int var201 = var111 + var77;

                                 for (int var83 = 0; var83 < var109; var83++) {
                                    var190 += var103.x.b(var201, var83) * var103.J.a(var83);
                                 }

                                 var103.K.a(var201, var190);
                                 var130 += var190 * var103.J.a(var77);
                                 var75 += var103.J.a(var77) * var103.C.a(var77);
                              }

                              var56 = var75 * var75 + var31 * (var35 + var75 + var75 + var31 * 0.5) + var56 - var130;
                              var103.K.a(var103.w, var103.K.a(var103.w) + 1.0);
                              if (var118 == 0) {
                                 double var160;
                                 if ((var60 = (var160 = var103.K.a(var121)) * var160 + var29 * var56) < var27 && var27 > 0.0) {
                                    for (int var79 = 0; var79 < var109; var79++) {
                                       var103.H.a(var79, var103.I.a(var79));
                                       var103.J.a(var79, var103.H.a(var79) - var103.C.a(var79));
                                    }

                                    var27 = 0.0;
                                    var72 = 230;
                                    continue;
                                 }
                              } else {
                                 double var161 = var46 * var46;
                                 double var66 = 0.0;
                                 double var68 = 0.0;
                                 var121 = 0;

                                 for (int var183 = 0; var183 < var111; var183++) {
                                    if (var183 != var103.w) {
                                       double var191 = 0.0;

                                       for (int var202 = 0; var202 < var7; var202++) {
                                          double var221 = var103.y.b(var183, var202);
                                          var191 += var221 * var221;
                                       }

                                       double var203 = var103.K.a(var183);
                                       double var84 = var56 * var191 + var203 * var203;
                                       var70 = 0.0;

                                       for (int var86 = 0; var86 < var109; var86++) {
                                          double var87 = var103.z.b(var183, var86) - var103.C.a(var86);
                                          var70 += var87 * var87;
                                       }

                                       double var252 = var70 / var161;
                                       double var88;
                                       if ((var88 = BY.e(1.0, var252 * var252)) * var84 > var66) {
                                          var66 = var88 * var84;
                                          var121 = var183;
                                          var60 = var84;
                                       }

                                       double var90 = var103.K.a(var183);
                                       var68 = BY.e(var68, var88 * (var90 * var90));
                                    }
                                 }
                              }
                           case 360:
                              break;
                           case 650:
                              break label894;
                           case 680:
                              break label899;
                           case 720:
                              break label903;
                           default:
                              throw new oX(px.SIMPLE_MESSAGE, "bobyqb");
                        }

                        for (int var132 = 0; var132 < var109; var132++) {
                           double var142 = var2[var132];
                           double var156 = var103.A.a(var132) + var103.H.a(var132);
                           double var173 = BY.e(var142, var156);
                           double var192 = var106[var132];
                           var103.u.a(var132, BY.d(var173, var192));
                           if (var103.H.a(var132) == var103.E.a(var132)) {
                              var103.u.a(var132, var2[var132]);
                           }

                           if (var103.H.a(var132) == var103.F.a(var132)) {
                              var103.u.a(var132, var106[var132]);
                           }
                        }

                        var54 = var103.a(var103.u.g());
                        if (!var103.t) {
                           var54 = -var54;
                        }

                        if (var118 == -1) {
                           var123 = var54;
                           var72 = 720;
                        } else {
                           double var133 = var103.B.a(var103.w);
                           double var148 = 0.0;
                           int var162 = 0;

                           for (int var174 = 0; var174 < var109; var174++) {
                              var148 += var103.J.a(var174) * var103.D.a(var174);

                              for (int var184 = 0; var184 <= var174; var184++) {
                                 double var193 = var103.J.a(var184) * var103.J.a(var174);
                                 if (var184 == var174) {
                                    var193 *= 0.5;
                                 }

                                 var148 += var103.L.a(var162) * var193;
                                 var162++;
                              }
                           }

                           for (int var175 = 0; var175 < var111; var175++) {
                              double var185;
                              double var81 = (var185 = var9.a(var175)) * var185;
                              var148 += 0.5 * var103.G.a(var175) * var81;
                           }

                           double var176 = var54 - var133 - var148;
                           var52 = var50;
                           var50 = var48;
                           var48 = BY.w(var176);
                           if (var64 > var44) {
                              var122 = var103.b();
                           }

                           if (var118 > 0) {
                              if (var148 >= 0.0) {
                                 throw new oX(px.TRUST_REGION_STEP_FAILED, var148);
                              }

                              var62 = (var54 - var133) / var148;
                              double var194 = var46 * 0.5;
                              if (var62 <= 0.1) {
                                 var46 = BY.d(var194, var64);
                              } else if (var62 <= 0.7) {
                                 var46 = BY.e(var194, var64);
                              } else {
                                 var46 = BY.e(var194, var64 * 2.0);
                              }

                              if (var46 <= var44 * 1.5) {
                                 var46 = var44;
                              }

                              if (var54 < var133) {
                                 int var204 = var121;
                                 double var222 = var60;
                                 double var85 = var46 * var46;
                                 double var126 = 0.0;
                                 double var127 = 0.0;
                                 var121 = 0;

                                 for (int var256 = 0; var256 < var111; var256++) {
                                    double var262 = 0.0;

                                    for (int var268 = 0; var268 < var7; var268++) {
                                       double var91 = var103.y.b(var256, var268);
                                       var262 += var91 * var91;
                                    }

                                    double var269 = var103.K.a(var256);
                                    double var92 = var56 * var262 + var269 * var269;
                                    var70 = 0.0;

                                    for (int var94 = 0; var94 < var109; var94++) {
                                       double var95 = var103.z.b(var256, var94) - var103.H.a(var94);
                                       var70 += var95 * var95;
                                    }

                                    double var275 = var70 / var85;
                                    double var96;
                                    if ((var96 = BY.e(1.0, var275 * var275)) * var92 > var126) {
                                       var126 = var96 * var92;
                                       var121 = var256;
                                       var60 = var92;
                                    }

                                    double var98 = var103.K.a(var256);
                                    double var100 = var96 * (var98 * var98);
                                    var127 = BY.e(var127, var100);
                                 }

                                 if (var126 <= var127 * 0.5) {
                                    var121 = var204;
                                    var60 = var222;
                                 }
                              }
                           }

                           var103.a(var56, var60, var121);
                           var162 = 0;
                           double var195 = var103.G.a(var121);
                           var103.G.a(var121, 0.0);

                           for (int var205 = 0; var205 < var109; var205++) {
                              double var223 = var195 * var103.z.b(var121, var205);

                              for (int var239 = 0; var239 <= var205; var239++) {
                                 var103.L.a(var162, var103.L.a(var162) + var223 * var103.z.b(var121, var239));
                                 var162++;
                              }
                           }

                           for (int var206 = 0; var206 < var7; var206++) {
                              double var224 = var176 * var103.y.b(var121, var206);

                              for (int var240 = 0; var240 < var111; var240++) {
                                 var103.G.a(var240, var103.G.a(var240) + var224 * var103.y.b(var240, var206));
                              }
                           }

                           var103.B.a(var121, var54);

                           for (int var207 = 0; var207 < var109; var207++) {
                              var103.z.c(var121, var207, var103.H.a(var207));
                              var8.a(var207, var103.x.b(var121, var207));
                           }

                           for (int var208 = 0; var208 < var111; var208++) {
                              double var225 = 0.0;

                              for (int var241 = 0; var241 < var7; var241++) {
                                 var225 += var103.y.b(var121, var241) * var103.y.b(var208, var241);
                              }

                              double var242 = 0.0;

                              for (int var257 = 0; var257 < var109; var257++) {
                                 var242 += var103.z.b(var208, var257) * var103.C.a(var257);
                              }

                              double var258 = var225 * var242;

                              for (int var89 = 0; var89 < var109; var89++) {
                                 var8.a(var89, var8.a(var89) + var258 * var103.z.b(var208, var89));
                              }
                           }

                           for (int var209 = 0; var209 < var109; var209++) {
                              var103.D.a(var209, var103.D.a(var209) + var176 * var8.a(var209));
                           }

                           if (var54 < var133) {
                              var103.w = var121;
                              var35 = 0.0;
                              var162 = 0;

                              for (int var210 = 0; var210 < var109; var210++) {
                                 var103.C.a(var210, var103.H.a(var210));
                                 double var226 = var103.C.a(var210);
                                 var35 += var226 * var226;

                                 for (int var243 = 0; var243 <= var210; var243++) {
                                    if (var243 < var210) {
                                       var103.D.a(var210, var103.D.a(var210) + var103.L.a(var162) * var103.J.a(var243));
                                    }

                                    var103.D.a(var243, var103.D.a(var243) + var103.L.a(var162) * var103.J.a(var210));
                                    var162++;
                                 }
                              }

                              for (int var211 = 0; var211 < var111; var211++) {
                                 double var227 = 0.0;

                                 for (int var244 = 0; var244 < var109; var244++) {
                                    var227 += var103.z.b(var211, var244) * var103.J.a(var244);
                                 }

                                 var227 *= var103.G.a(var211);

                                 for (int var245 = 0; var245 < var109; var245++) {
                                    var103.D.a(var245, var103.D.a(var245) + var227 * var103.z.b(var211, var245));
                                 }
                              }
                           }

                           if (var118 > 0) {
                              for (int var212 = 0; var212 < var111; var212++) {
                                 var103.K.a(var212, var103.B.a(var212) - var103.B.a(var103.w));
                                 var117.a(var212, 0.0);
                              }

                              for (int var213 = 0; var213 < var7; var213++) {
                                 double var229 = 0.0;

                                 for (int var246 = 0; var246 < var111; var246++) {
                                    var229 += var103.y.b(var246, var213) * var103.K.a(var246);
                                 }

                                 for (int var247 = 0; var247 < var111; var247++) {
                                    var117.a(var247, var117.a(var247) + var229 * var103.y.b(var247, var213));
                                 }
                              }

                              for (int var214 = 0; var214 < var111; var214++) {
                                 double var230 = 0.0;

                                 for (int var248 = 0; var248 < var109; var248++) {
                                    var230 += var103.z.b(var214, var248) * var103.C.a(var248);
                                 }

                                 var9.a(var214, var117.a(var214));
                                 var117.a(var214, var230 * var117.a(var214));
                              }

                              double var215 = 0.0;
                              double var235 = 0.0;

                              for (int var253 = 0; var253 < var109; var253++) {
                                 double var259 = 0.0;

                                 for (int var263 = 0; var263 < var111; var263++) {
                                    var259 += var103.x.b(var263, var253) * var103.K.a(var263) + var103.z.b(var263, var253) * var117.a(var263);
                                 }

                                 if (var103.C.a(var253) == var103.E.a(var253)) {
                                    double var264 = BY.d(0.0, var103.D.a(var253));
                                    var215 += var264 * var264;
                                    double var272 = BY.d(0.0, var259);
                                    var235 += var272 * var272;
                                 } else if (var103.C.a(var253) == var103.F.a(var253)) {
                                    double var265 = BY.e(0.0, var103.D.a(var253));
                                    var215 += var265 * var265;
                                    double var273 = BY.e(0.0, var259);
                                    var235 += var273 * var273;
                                 } else {
                                    double var266 = var103.D.a(var253);
                                    var215 += var266 * var266;
                                    var235 += var259 * var259;
                                 }

                                 var103.K.a(var111 + var253, var259);
                              }

                              var120++;
                              if (var215 < var235 * 10.0) {
                                 var120 = 0;
                              }

                              if (var120 >= 3) {
                                 int var254 = 0;

                                 for (int var260 = BY.b(var111, var6); var254 < var260; var254++) {
                                    if (var254 < var109) {
                                       var103.D.a(var254, var103.K.a(var111 + var254));
                                    }

                                    if (var254 < var111) {
                                       var103.G.a(var254, var9.a(var254));
                                    }

                                    if (var254 < var6) {
                                       var103.L.a(var254, 0.0);
                                    }

                                    var120 = 0;
                                 }
                              }
                           }

                           if (var118 == 0) {
                              var72 = 60;
                           } else {
                              if (!(var54 <= var133 + var148 * 0.1)) {
                                 double var216 = var46 * 2.0;
                                 double var236 = var44 * 10.0;
                                 var70 = BY.e(var216 * var216, var236 * var236);
                                 break;
                              }

                              var72 = 60;
                           }
                        }
                     }

                     var121 = -1;

                     for (int var134 = 0; var134 < var111; var134++) {
                        double var143 = 0.0;

                        for (int var157 = 0; var157 < var109; var157++) {
                           double var165 = var103.z.b(var134, var157) - var103.C.a(var157);
                           var143 += var165 * var165;
                        }

                        if (var143 > var70) {
                           var121 = var134;
                           var70 = var143;
                        }
                     }

                     if (var121 >= 0) {
                        double var136 = BY.a(var70);
                        if (var118 == -1 && (var46 = BY.d(var46 * 0.1, var136 * 0.5)) <= var44 * 1.5) {
                           var46 = var44;
                        }

                        var118 = 0;
                        var31 = (var58 = BY.e(BY.d(var136 * 0.1, var46), var44)) * var58;
                        var72 = 90;
                     } else if (var118 == -1) {
                        var72 = 680;
                     } else if (var62 > 0.0) {
                        var72 = 60;
                     } else {
                        if (BY.e(var46, var64) > var44) {
                           var72 = 60;
                           continue;
                        }
                        break label899;
                     }
                  }

                  sA var138 = new sA(var109);
                  sA var147 = new sA(var109);
                  sA var152 = new sA(var109);
                  sA var159 = new sA(var109);
                  sA var168 = new sA(var109);
                  double[] var178;
                  var31 = (var178 = var103.a(var46, var138, var147, var152, var159, var168))[0];
                  double var33 = var178[1];
                  double var186 = var46;
                  double var200 = BY.a(var31);
                  if ((var64 = BY.d(var186, var200)) < var44 * 0.5) {
                     var118 = -1;
                     var70 = (var186 = var44 * 10.0) * var186;
                     if (var103.b() <= var122 + 2) {
                        var72 = 650;
                        continue;
                     }

                     double var234 = BY.e(BY.e(var48, var50), var52);
                     double var251 = var44 * 0.125 * var44;
                     if (var33 > 0.0 && var234 > var251 * var33) {
                        var72 = 650;
                        continue;
                     }

                     double var261 = var234 / var44;

                     for (int var267 = 0; var267 < var109; var267++) {
                        double var270 = var261;
                        if (var103.H.a(var267) == var103.E.a(var267)) {
                           var270 = var8.a(var267);
                        }

                        if (var103.H.a(var267) == var103.F.a(var267)) {
                           var270 = -var8.a(var267);
                        }

                        if (var270 < var261) {
                           double var274 = var103.L.a((var267 + var267 * var267) / 2);

                           for (int var276 = 0; var276 < var111; var276++) {
                              double var277 = var103.z.b(var276, var267);
                              var274 += var103.G.a(var276) * (var277 * var277);
                           }

                           if ((var270 = var270 + var274 * 0.5 * var44) < var261) {
                              var72 = 680;
                              continue label899;
                           }
                        }
                     }
                  }

                  var118++;
                  break label901;
               }

               if (var44 > var103.s) {
                  var46 = var44 * 0.5;
                  if ((var62 = var44 / var103.s) <= 16.0) {
                     var44 = var103.s;
                  } else if (var62 <= 250.0) {
                     var44 = BY.a(var62) * var103.s;
                  } else {
                     var44 *= 0.1;
                  }

                  var46 = BY.e(var46, var44);
                  var118 = 0;
                  var122 = var103.b();
                  var72 = 60;
               } else {
                  if (var118 == -1) {
                     var72 = 360;
                     continue;
                  }
                  break label903;
               }
            }

            if (var31 <= var35 * 0.001) {
               double var139 = var35 * 0.25;
               double var153 = 0.0;

               for (int var169 = 0; var169 < var111; var169++) {
                  var153 += var103.G.a(var169);
                  double var179 = var35 * -0.5;

                  for (int var197 = 0; var197 < var109; var197++) {
                     var179 += var103.z.b(var169, var197) * var103.C.a(var197);
                  }

                  var9.a(var169, var179);
                  double var198 = var139 - var179 * 0.5;

                  for (int var217 = 0; var217 < var109; var217++) {
                     var8.a(var217, var103.x.b(var169, var217));
                     var103.K.a(var217, var179 * var103.z.b(var169, var217) + var198 * var103.C.a(var217));
                     int var231 = var111 + var217;

                     for (int var237 = 0; var237 <= var217; var237++) {
                        var103.x.c(var231, var237, var103.x.b(var231, var237) + var8.a(var217) * var103.K.a(var237) + var103.K.a(var217) * var8.a(var237));
                     }
                  }
               }

               for (int var170 = 0; var170 < var7; var170++) {
                  double var180 = 0.0;
                  double var199 = 0.0;

                  for (int var218 = 0; var218 < var111; var218++) {
                     var180 += var103.y.b(var218, var170);
                     var103.K.a(var218, var9.a(var218) * var103.y.b(var218, var170));
                     var199 += var103.K.a(var218);
                  }

                  for (int var219 = 0; var219 < var109; var219++) {
                     double var232 = (var139 * var180 - var199 * 0.5) * var103.C.a(var219);

                     for (int var249 = 0; var249 < var111; var249++) {
                        var232 += var103.K.a(var249) * var103.z.b(var249, var219);
                     }

                     var8.a(var219, var232);

                     for (int var250 = 0; var250 < var111; var250++) {
                        var103.x.c(var250, var219, var103.x.b(var250, var219) + var232 * var103.y.b(var250, var170));
                     }
                  }

                  for (int var220 = 0; var220 < var109; var220++) {
                     int var233 = var220 + var111;
                     double var238 = var8.a(var220);

                     for (int var255 = 0; var255 <= var220; var255++) {
                        var103.x.c(var233, var255, var103.x.b(var233, var255) + var238 * var8.a(var255));
                     }
                  }
               }

               int var171 = 0;

               for (int var181 = 0; var181 < var109; var181++) {
                  var8.a(var181, var153 * -0.5 * var103.C.a(var181));

                  for (int var187 = 0; var187 < var111; var187++) {
                     var8.a(var181, var8.a(var181) + var103.G.a(var187) * var103.z.b(var187, var181));
                     var103.z.c(var187, var181, var103.z.b(var187, var181) - var103.C.a(var181));
                  }

                  for (int var188 = 0; var188 <= var181; var188++) {
                     var103.L.a(var171, var103.L.a(var171) + var8.a(var188) * var103.C.a(var181) + var103.C.a(var188) * var8.a(var181));
                     var103.x.c(var111 + var188, var181, var103.x.b(var111 + var181, var188));
                     var171++;
                  }
               }

               for (int var182 = 0; var182 < var109; var182++) {
                  var103.A.a(var182, var103.A.a(var182) + var103.C.a(var182));
                  var103.H.a(var182, var103.H.a(var182) - var103.C.a(var182));
                  var103.E.a(var182, var103.E.a(var182) - var103.C.a(var182));
                  var103.F.a(var182, var103.F.a(var182) - var103.C.a(var182));
                  var103.C.a(var182, 0.0);
               }

               var35 = 0.0;
            }

            if (var118 == 0) {
               var72 = 210;
            } else {
               var72 = 230;
            }
         }

         if (var103.B.a(var103.w) <= var123) {
            for (int var135 = 0; var135 < var109; var135++) {
               double var144 = var2[var135];
               double var158 = var103.A.a(var135) + var103.C.a(var135);
               double var177 = BY.e(var144, var158);
               double var196 = var106[var135];
               var103.u.a(var135, BY.d(var177, var196));
               if (var103.C.a(var135) == var103.E.a(var135)) {
                  var103.u.a(var135, var2[var135]);
               }

               if (var103.C.a(var135) == var103.F.a(var135)) {
                  var103.u.a(var135, var106[var135]);
               }
            }

            var54 = var103.B.a(var103.w);
         }

         double var107 = var54;
         var102 = this.u;
         return new yp(this.u.a, this.t ? var107 : -var107);
      } else {
         throw new po(px.NUMBER_OF_INTERPOLATION_POINTS, var3.q, var10[0], var10[1]);
      }
   }

   private double a(double[] var1, double[] var2) {
      sA var3 = this.u;
      int var11 = this.u.a.length;

      for (int var4 = 0; var4 < var11; var4++) {
         double var5 = this.v[var4];
         this.E.a(var4, var1[var4] - this.u.a(var4));
         this.F.a(var4, var2[var4] - this.u.a(var4));
         if (this.E.a(var4) >= -this.r) {
            if (this.E.a(var4) >= 0.0) {
               this.u.a(var4, var1[var4]);
               this.E.a(var4, 0.0);
               this.F.a(var4, var5);
            } else {
               this.u.a(var4, var1[var4] + this.r);
               this.E.a(var4, -this.r);
               double var7 = var2[var4] - this.u.a(var4);
               this.F.a(var4, BY.e(var7, this.r));
            }
         } else if (this.F.a(var4) <= this.r) {
            if (this.F.a(var4) <= 0.0) {
               this.u.a(var4, var2[var4]);
               this.E.a(var4, -var5);
               this.F.a(var4, 0.0);
            } else {
               this.u.a(var4, var2[var4] - this.r);
               double var12 = var1[var4] - this.u.a(var4);
               double var9 = -this.r;
               this.E.a(var4, BY.d(var12, var9));
               this.F.a(var4, this.r);
            }
         }
      }

      return this.b(var1, var2);
   }

   private double b(double[] var1, double[] var2) {
      sA var86 = this.u;
      int var3 = this.u.a.length;
      int var4 = this.q;
      int var5 = var3 + 1;
      int var6 = var4 - var5;
      var5 = var3 * var5 / 2;
      sA var7 = new sA(var3);
      sA var8 = new sA(var4);
      sA var9 = new sA(var4);
      double var11 = Double.NaN;
      double var13 = Double.NaN;
      double var15 = Double.NaN;
      this.w = 0;
      this.c(var1, var2);
      double var19 = 0.0;

      for (int var21 = 0; var21 < var3; var21++) {
         this.C.a(var21, this.z.b(this.w, var21));
         double var22 = this.C.a(var21);
         var19 += var22 * var22;
      }

      double var89 = this.B.a(0);
      int var10 = 0;
      int var23 = 0;
      int var24 = 0;
      int var25 = this.b();
      double var28 = this.r;
      double var30 = this.r;
      double var32 = 0.0;
      double var34 = 0.0;
      double var36 = 0.0;
      double var38 = 0.0;
      double var40 = 0.0;
      double var42 = 0.0;
      double var44 = 0.0;
      double var46 = 0.0;
      double var48 = 0.0;
      double var54 = 0.0;
      short var17 = 20;

      label863:
      while (true) {
         label861:
         while (true) {
            label859:
            while (true) {
               label857:
               while (true) {
                  label854:
                  while (true) {
                     switch (var17) {
                        case 20:
                           if (this.w != 0) {
                              int var103 = 0;

                              for (int var111 = 0; var111 < var3; var111++) {
                                 for (int var115 = 0; var115 <= var111; var115++) {
                                    if (var115 < var111) {
                                       this.D.a(var111, this.D.a(var111) + this.L.a(var103) * this.C.a(var115));
                                    }

                                    this.D.a(var115, this.D.a(var115) + this.L.a(var103) * this.C.a(var111));
                                    var103++;
                                 }
                              }

                              if (this.b() > var4) {
                                 for (int var112 = 0; var112 < var4; var112++) {
                                    double var116 = 0.0;

                                    for (int var132 = 0; var132 < var3; var132++) {
                                       var116 += this.z.b(var112, var132) * this.C.a(var132);
                                    }

                                    var116 *= this.G.a(var112);

                                    for (int var133 = 0; var133 < var3; var133++) {
                                       this.D.a(var133, this.D.a(var133) + var116 * this.z.b(var112, var133));
                                    }
                                 }
                              }
                           }
                        case 60:
                           break label857;
                        case 90:
                           break label861;
                        case 210:
                           double[] var57;
                           var13 = (var57 = this.a(var24, var42))[0];
                           var11 = var57[1];

                           for (int var58 = 0; var58 < var3; var58++) {
                              this.J.a(var58, this.H.a(var58) - this.C.a(var58));
                           }
                        case 230:
                           for (int var94 = 0; var94 < var4; var94++) {
                              double var106 = 0.0;
                              double var60 = 0.0;
                              double var62 = 0.0;

                              for (int var64 = 0; var64 < var3; var64++) {
                                 var106 += this.z.b(var94, var64) * this.J.a(var64);
                                 var60 += this.z.b(var94, var64) * this.C.a(var64);
                                 var62 += this.x.b(var94, var64) * this.J.a(var64);
                              }

                              var9.a(var94, var106 * (var106 * 0.5 + var60));
                              this.K.a(var94, var62);
                              var8.a(var94, var106);
                           }

                           var40 = 0.0;

                           for (int var95 = 0; var95 < var6; var95++) {
                              double var107 = 0.0;

                              for (int var120 = 0; var120 < var4; var120++) {
                                 var107 += this.y.b(var120, var95) * var9.a(var120);
                              }

                              var40 -= var107 * var107;

                              for (int var121 = 0; var121 < var4; var121++) {
                                 this.K.a(var121, this.K.a(var121) + var107 * this.y.b(var121, var95));
                              }
                           }

                           var15 = 0.0;
                           double var96 = 0.0;
                           double var59 = 0.0;

                           for (int var61 = 0; var61 < var3; var61++) {
                              double var138 = this.J.a(var61);
                              var15 += var138 * var138;
                              double var156 = 0.0;

                              for (int var66 = 0; var66 < var4; var66++) {
                                 var156 += var9.a(var66) * this.x.b(var66, var61);
                              }

                              var96 += var156 * this.J.a(var61);
                              int var167 = var4 + var61;

                              for (int var67 = 0; var67 < var3; var67++) {
                                 var156 += this.x.b(var167, var67) * this.J.a(var67);
                              }

                              this.K.a(var167, var156);
                              var96 += var156 * this.J.a(var61);
                              var59 += this.J.a(var61) * this.C.a(var61);
                           }

                           var40 = var59 * var59 + var15 * (var19 + var59 + var59 + var15 * 0.5) + var40 - var96;
                           this.K.a(this.w, this.K.a(this.w) + 1.0);
                           if (var10 == 0) {
                              double var126;
                              if ((var44 = (var126 = this.K.a(var24)) * var126 + var13 * var40) < var11 && var11 > 0.0) {
                                 for (int var63 = 0; var63 < var3; var63++) {
                                    this.H.a(var63, this.I.a(var63));
                                    this.J.a(var63, this.H.a(var63) - this.C.a(var63));
                                 }

                                 var11 = 0.0;
                                 var17 = 230;
                                 continue;
                              }
                           } else {
                              double var127 = var30 * var30;
                              double var50 = 0.0;
                              double var52 = 0.0;
                              var24 = 0;

                              for (int var149 = 0; var149 < var4; var149++) {
                                 if (var149 != this.w) {
                                    double var157 = 0.0;

                                    for (int var168 = 0; var168 < var6; var168++) {
                                       double var187 = this.y.b(var149, var168);
                                       var157 += var187 * var187;
                                    }

                                    double var169 = this.K.a(var149);
                                    double var68 = var40 * var157 + var169 * var169;
                                    var54 = 0.0;

                                    for (int var70 = 0; var70 < var3; var70++) {
                                       double var71 = this.z.b(var149, var70) - this.C.a(var70);
                                       var54 += var71 * var71;
                                    }

                                    double var218 = var54 / var127;
                                    double var72;
                                    if ((var72 = BY.e(1.0, var218 * var218)) * var68 > var50) {
                                       var50 = var72 * var68;
                                       var24 = var149;
                                       var44 = var68;
                                    }

                                    double var74 = this.K.a(var149);
                                    var52 = BY.e(var52, var72 * (var74 * var74));
                                 }
                              }
                           }
                        case 360:
                           break;
                        case 650:
                           break label854;
                        case 680:
                           break label859;
                        case 720:
                           break label863;
                        default:
                           throw new oX(px.SIMPLE_MESSAGE, "bobyqb");
                     }

                     for (int var98 = 0; var98 < var3; var98++) {
                        double var108 = var1[var98];
                        double var122 = this.A.a(var98) + this.H.a(var98);
                        double var139 = BY.e(var108, var122);
                        double var158 = var2[var98];
                        this.u.a(var98, BY.d(var139, var158));
                        if (this.H.a(var98) == this.E.a(var98)) {
                           this.u.a(var98, var1[var98]);
                        }

                        if (this.H.a(var98) == this.F.a(var98)) {
                           this.u.a(var98, var2[var98]);
                        }
                     }

                     var38 = this.a(this.u.g());
                     if (!this.t) {
                        var38 = -var38;
                     }

                     if (var10 == -1) {
                        var89 = var38;
                        var17 = 720;
                     } else {
                        double var99 = this.B.a(this.w);
                        double var114 = 0.0;
                        int var128 = 0;

                        for (int var140 = 0; var140 < var3; var140++) {
                           var114 += this.J.a(var140) * this.D.a(var140);

                           for (int var150 = 0; var150 <= var140; var150++) {
                              double var159 = this.J.a(var150) * this.J.a(var140);
                              if (var150 == var140) {
                                 var159 *= 0.5;
                              }

                              var114 += this.L.a(var128) * var159;
                              var128++;
                           }
                        }

                        for (int var141 = 0; var141 < var4; var141++) {
                           double var151;
                           double var65 = (var151 = var8.a(var141)) * var151;
                           var114 += 0.5 * this.G.a(var141) * var65;
                        }

                        double var142 = var38 - var99 - var114;
                        var36 = var34;
                        var34 = var32;
                        var32 = BY.w(var142);
                        if (var48 > var28) {
                           var25 = this.b();
                        }

                        if (var10 > 0) {
                           if (var114 >= 0.0) {
                              throw new oX(px.TRUST_REGION_STEP_FAILED, var114);
                           }

                           var46 = (var38 - var99) / var114;
                           double var160 = var30 * 0.5;
                           if (var46 <= 0.1) {
                              var30 = BY.d(var160, var48);
                           } else if (var46 <= 0.7) {
                              var30 = BY.e(var160, var48);
                           } else {
                              var30 = BY.e(var160, var48 * 2.0);
                           }

                           if (var30 <= var28 * 1.5) {
                              var30 = var28;
                           }

                           if (var38 < var99) {
                              int var170 = var24;
                              double var188 = var44;
                              double var69 = var30 * var30;
                              double var92 = 0.0;
                              double var93 = 0.0;
                              var24 = 0;

                              for (int var222 = 0; var222 < var4; var222++) {
                                 double var228 = 0.0;

                                 for (int var234 = 0; var234 < var6; var234++) {
                                    double var75 = this.y.b(var222, var234);
                                    var228 += var75 * var75;
                                 }

                                 double var235 = this.K.a(var222);
                                 double var76 = var40 * var228 + var235 * var235;
                                 var54 = 0.0;

                                 for (int var78 = 0; var78 < var3; var78++) {
                                    double var79 = this.z.b(var222, var78) - this.H.a(var78);
                                    var54 += var79 * var79;
                                 }

                                 double var241 = var54 / var69;
                                 double var80;
                                 if ((var80 = BY.e(1.0, var241 * var241)) * var76 > var92) {
                                    var92 = var80 * var76;
                                    var24 = var222;
                                    var44 = var76;
                                 }

                                 double var82 = this.K.a(var222);
                                 double var84 = var80 * (var82 * var82);
                                 var93 = BY.e(var93, var84);
                              }

                              if (var92 <= var93 * 0.5) {
                                 var24 = var170;
                                 var44 = var188;
                              }
                           }
                        }

                        this.a(var40, var44, var24);
                        var128 = 0;
                        double var161 = this.G.a(var24);
                        this.G.a(var24, 0.0);

                        for (int var171 = 0; var171 < var3; var171++) {
                           double var189 = var161 * this.z.b(var24, var171);

                           for (int var205 = 0; var205 <= var171; var205++) {
                              this.L.a(var128, this.L.a(var128) + var189 * this.z.b(var24, var205));
                              var128++;
                           }
                        }

                        for (int var172 = 0; var172 < var6; var172++) {
                           double var190 = var142 * this.y.b(var24, var172);

                           for (int var206 = 0; var206 < var4; var206++) {
                              this.G.a(var206, this.G.a(var206) + var190 * this.y.b(var206, var172));
                           }
                        }

                        this.B.a(var24, var38);

                        for (int var173 = 0; var173 < var3; var173++) {
                           this.z.c(var24, var173, this.H.a(var173));
                           var7.a(var173, this.x.b(var24, var173));
                        }

                        for (int var174 = 0; var174 < var4; var174++) {
                           double var191 = 0.0;

                           for (int var207 = 0; var207 < var6; var207++) {
                              var191 += this.y.b(var24, var207) * this.y.b(var174, var207);
                           }

                           double var208 = 0.0;

                           for (int var223 = 0; var223 < var3; var223++) {
                              var208 += this.z.b(var174, var223) * this.C.a(var223);
                           }

                           double var224 = var191 * var208;

                           for (int var73 = 0; var73 < var3; var73++) {
                              var7.a(var73, var7.a(var73) + var224 * this.z.b(var174, var73));
                           }
                        }

                        for (int var175 = 0; var175 < var3; var175++) {
                           this.D.a(var175, this.D.a(var175) + var142 * var7.a(var175));
                        }

                        if (var38 < var99) {
                           this.w = var24;
                           var19 = 0.0;
                           var128 = 0;

                           for (int var176 = 0; var176 < var3; var176++) {
                              this.C.a(var176, this.H.a(var176));
                              double var192 = this.C.a(var176);
                              var19 += var192 * var192;

                              for (int var209 = 0; var209 <= var176; var209++) {
                                 if (var209 < var176) {
                                    this.D.a(var176, this.D.a(var176) + this.L.a(var128) * this.J.a(var209));
                                 }

                                 this.D.a(var209, this.D.a(var209) + this.L.a(var128) * this.J.a(var176));
                                 var128++;
                              }
                           }

                           for (int var177 = 0; var177 < var4; var177++) {
                              double var193 = 0.0;

                              for (int var210 = 0; var210 < var3; var210++) {
                                 var193 += this.z.b(var177, var210) * this.J.a(var210);
                              }

                              var193 *= this.G.a(var177);

                              for (int var211 = 0; var211 < var3; var211++) {
                                 this.D.a(var211, this.D.a(var211) + var193 * this.z.b(var177, var211));
                              }
                           }
                        }

                        if (var10 > 0) {
                           for (int var178 = 0; var178 < var4; var178++) {
                              this.K.a(var178, this.B.a(var178) - this.B.a(this.w));
                              var9.a(var178, 0.0);
                           }

                           for (int var179 = 0; var179 < var6; var179++) {
                              double var195 = 0.0;

                              for (int var212 = 0; var212 < var4; var212++) {
                                 var195 += this.y.b(var212, var179) * this.K.a(var212);
                              }

                              for (int var213 = 0; var213 < var4; var213++) {
                                 var9.a(var213, var9.a(var213) + var195 * this.y.b(var213, var179));
                              }
                           }

                           for (int var180 = 0; var180 < var4; var180++) {
                              double var196 = 0.0;

                              for (int var214 = 0; var214 < var3; var214++) {
                                 var196 += this.z.b(var180, var214) * this.C.a(var214);
                              }

                              var8.a(var180, var9.a(var180));
                              var9.a(var180, var196 * var9.a(var180));
                           }

                           double var181 = 0.0;
                           double var201 = 0.0;

                           for (int var219 = 0; var219 < var3; var219++) {
                              double var225 = 0.0;

                              for (int var229 = 0; var229 < var4; var229++) {
                                 var225 += this.x.b(var229, var219) * this.K.a(var229) + this.z.b(var229, var219) * var9.a(var229);
                              }

                              if (this.C.a(var219) == this.E.a(var219)) {
                                 double var230 = BY.d(0.0, this.D.a(var219));
                                 var181 += var230 * var230;
                                 double var238 = BY.d(0.0, var225);
                                 var201 += var238 * var238;
                              } else if (this.C.a(var219) == this.F.a(var219)) {
                                 double var231 = BY.e(0.0, this.D.a(var219));
                                 var181 += var231 * var231;
                                 double var239 = BY.e(0.0, var225);
                                 var201 += var239 * var239;
                              } else {
                                 double var232 = this.D.a(var219);
                                 var181 += var232 * var232;
                                 var201 += var225 * var225;
                              }

                              this.K.a(var4 + var219, var225);
                           }

                           var23++;
                           if (var181 < var201 * 10.0) {
                              var23 = 0;
                           }

                           if (var23 >= 3) {
                              int var220 = 0;

                              for (int var226 = BY.b(var4, var5); var220 < var226; var220++) {
                                 if (var220 < var3) {
                                    this.D.a(var220, this.K.a(var4 + var220));
                                 }

                                 if (var220 < var4) {
                                    this.G.a(var220, var8.a(var220));
                                 }

                                 if (var220 < var5) {
                                    this.L.a(var220, 0.0);
                                 }

                                 var23 = 0;
                              }
                           }
                        }

                        if (var10 == 0) {
                           var17 = 60;
                        } else {
                           if (!(var38 <= var99 + var114 * 0.1)) {
                              double var182 = var30 * 2.0;
                              double var202 = var28 * 10.0;
                              var54 = BY.e(var182 * var182, var202 * var202);
                              break;
                           }

                           var17 = 60;
                        }
                     }
                  }

                  var24 = -1;

                  for (int var100 = 0; var100 < var4; var100++) {
                     double var109 = 0.0;

                     for (int var123 = 0; var123 < var3; var123++) {
                        double var131 = this.z.b(var100, var123) - this.C.a(var123);
                        var109 += var131 * var131;
                     }

                     if (var109 > var54) {
                        var24 = var100;
                        var54 = var109;
                     }
                  }

                  if (var24 >= 0) {
                     double var102 = BY.a(var54);
                     if (var10 == -1 && (var30 = BY.d(var30 * 0.1, var102 * 0.5)) <= var28 * 1.5) {
                        var30 = var28;
                     }

                     var10 = 0;
                     var15 = (var42 = BY.e(BY.d(var102 * 0.1, var30), var28)) * var42;
                     var17 = 90;
                  } else if (var10 == -1) {
                     var17 = 680;
                  } else if (var46 > 0.0) {
                     var17 = 60;
                  } else {
                     if (BY.e(var30, var48) > var28) {
                        var17 = 60;
                        continue;
                     }
                     break label859;
                  }
               }

               sA var104 = new sA(var3);
               sA var113 = new sA(var3);
               sA var118 = new sA(var3);
               sA var125 = new sA(var3);
               sA var134 = new sA(var3);
               double[] var144;
               var15 = (var144 = this.a(var30, var104, var113, var118, var125, var134))[0];
               double var88 = var144[1];
               double var152 = var30;
               double var166 = BY.a(var15);
               if ((var48 = BY.d(var152, var166)) < var28 * 0.5) {
                  var10 = -1;
                  var54 = (var152 = var28 * 10.0) * var152;
                  if (this.b() <= var25 + 2) {
                     var17 = 650;
                     continue;
                  }

                  double var200 = BY.e(BY.e(var32, var34), var36);
                  double var217 = var28 * 0.125 * var28;
                  if (var88 > 0.0 && var200 > var217 * var88) {
                     var17 = 650;
                     continue;
                  }

                  double var227 = var200 / var28;

                  for (int var233 = 0; var233 < var3; var233++) {
                     double var236 = var227;
                     if (this.H.a(var233) == this.E.a(var233)) {
                        var236 = var7.a(var233);
                     }

                     if (this.H.a(var233) == this.F.a(var233)) {
                        var236 = -var7.a(var233);
                     }

                     if (var236 < var227) {
                        double var240 = this.L.a((var233 + var233 * var233) / 2);

                        for (int var242 = 0; var242 < var4; var242++) {
                           double var243 = this.z.b(var242, var233);
                           var240 += this.G.a(var242) * (var243 * var243);
                        }

                        if ((var236 = var236 + var240 * 0.5 * var28) < var227) {
                           var17 = 680;
                           continue label859;
                        }
                     }
                  }
               }

               var10++;
               break label861;
            }

            if (var28 > this.s) {
               var30 = var28 * 0.5;
               if ((var46 = var28 / this.s) <= 16.0) {
                  var28 = this.s;
               } else if (var46 <= 250.0) {
                  var28 = BY.a(var46) * this.s;
               } else {
                  var28 *= 0.1;
               }

               var30 = BY.e(var30, var28);
               var10 = 0;
               var25 = this.b();
               var17 = 60;
            } else {
               if (var10 == -1) {
                  var17 = 360;
                  continue;
               }
               break label863;
            }
         }

         if (var15 <= var19 * 0.001) {
            double var105 = var19 * 0.25;
            double var119 = 0.0;

            for (int var135 = 0; var135 < var4; var135++) {
               var119 += this.G.a(var135);
               double var145 = var19 * -0.5;

               for (int var163 = 0; var163 < var3; var163++) {
                  var145 += this.z.b(var135, var163) * this.C.a(var163);
               }

               var8.a(var135, var145);
               double var164 = var105 - var145 * 0.5;

               for (int var183 = 0; var183 < var3; var183++) {
                  var7.a(var183, this.x.b(var135, var183));
                  this.K.a(var183, var145 * this.z.b(var135, var183) + var164 * this.C.a(var183));
                  int var197 = var4 + var183;

                  for (int var203 = 0; var203 <= var183; var203++) {
                     this.x.c(var197, var203, this.x.b(var197, var203) + var7.a(var183) * this.K.a(var203) + this.K.a(var183) * var7.a(var203));
                  }
               }
            }

            for (int var136 = 0; var136 < var6; var136++) {
               double var146 = 0.0;
               double var165 = 0.0;

               for (int var184 = 0; var184 < var4; var184++) {
                  var146 += this.y.b(var184, var136);
                  this.K.a(var184, var8.a(var184) * this.y.b(var184, var136));
                  var165 += this.K.a(var184);
               }

               for (int var185 = 0; var185 < var3; var185++) {
                  double var198 = (var105 * var146 - var165 * 0.5) * this.C.a(var185);

                  for (int var215 = 0; var215 < var4; var215++) {
                     var198 += this.K.a(var215) * this.z.b(var215, var185);
                  }

                  var7.a(var185, var198);

                  for (int var216 = 0; var216 < var4; var216++) {
                     this.x.c(var216, var185, this.x.b(var216, var185) + var198 * this.y.b(var216, var136));
                  }
               }

               for (int var186 = 0; var186 < var3; var186++) {
                  int var199 = var186 + var4;
                  double var204 = var7.a(var186);

                  for (int var221 = 0; var221 <= var186; var221++) {
                     this.x.c(var199, var221, this.x.b(var199, var221) + var204 * var7.a(var221));
                  }
               }
            }

            int var137 = 0;

            for (int var147 = 0; var147 < var3; var147++) {
               var7.a(var147, var119 * -0.5 * this.C.a(var147));

               for (int var153 = 0; var153 < var4; var153++) {
                  var7.a(var147, var7.a(var147) + this.G.a(var153) * this.z.b(var153, var147));
                  this.z.c(var153, var147, this.z.b(var153, var147) - this.C.a(var147));
               }

               for (int var154 = 0; var154 <= var147; var154++) {
                  this.L.a(var137, this.L.a(var137) + var7.a(var154) * this.C.a(var147) + this.C.a(var154) * var7.a(var147));
                  this.x.c(var4 + var154, var147, this.x.b(var4 + var147, var154));
                  var137++;
               }
            }

            for (int var148 = 0; var148 < var3; var148++) {
               this.A.a(var148, this.A.a(var148) + this.C.a(var148));
               this.H.a(var148, this.H.a(var148) - this.C.a(var148));
               this.E.a(var148, this.E.a(var148) - this.C.a(var148));
               this.F.a(var148, this.F.a(var148) - this.C.a(var148));
               this.C.a(var148, 0.0);
            }

            var19 = 0.0;
         }

         if (var10 == 0) {
            var17 = 210;
         } else {
            var17 = 230;
         }
      }

      if (this.B.a(this.w) <= var89) {
         for (int var101 = 0; var101 < var3; var101++) {
            double var110 = var1[var101];
            double var124 = this.A.a(var101) + this.C.a(var101);
            double var143 = BY.e(var110, var124);
            double var162 = var2[var101];
            this.u.a(var101, BY.d(var143, var162));
            if (this.C.a(var101) == this.E.a(var101)) {
               this.u.a(var101, var1[var101]);
            }

            if (this.C.a(var101) == this.F.a(var101)) {
               this.u.a(var101, var2[var101]);
            }
         }

         var38 = this.B.a(this.w);
      }

      return var38;
   }

   private double[] a(int var1, double var2) {
      sA var4 = this.u;
      int var50 = this.u.a.length;
      int var5 = this.q;
      sA var6 = new sA(var50);
      sA var7 = new sA(var5);
      sA var8 = new sA(var50);
      sA var9 = new sA(var50);

      for (int var10 = 0; var10 < var5; var10++) {
         var7.a(var10, 0.0);
      }

      int var51 = 0;

      for (int var11 = var5 - var50 - 1; var51 < var11; var51++) {
         double var12 = this.y.b(var1, var51);

         for (int var14 = 0; var14 < var5; var14++) {
            var7.a(var14, var7.a(var14) + var12 * this.y.b(var14, var51));
         }
      }

      double var52 = var7.a(var1);
      double var53 = var52 * 0.5;

      for (int var54 = 0; var54 < var50; var54++) {
         var6.a(var54, this.x.b(var1, var54));
      }

      for (int var55 = 0; var55 < var5; var55++) {
         double var15 = 0.0;

         for (int var17 = 0; var17 < var50; var17++) {
            var15 += this.z.b(var55, var17) * this.C.a(var17);
         }

         var15 *= var7.a(var55);

         for (int var58 = 0; var58 < var50; var58++) {
            var6.a(var58, var6.a(var58) + var15 * this.z.b(var55, var58));
         }
      }

      double var56 = 0.0;
      double var16 = Double.NaN;
      int var18 = 0;
      int var19 = 0;
      double var20 = 0.0;

      for (int var22 = 0; var22 < var5; var22++) {
         if (var22 != this.w) {
            double var23 = 0.0;
            double var25 = 0.0;

            for (int var27 = 0; var27 < var50; var27++) {
               double var28 = this.z.b(var22, var27) - this.C.a(var27);
               var23 += var6.a(var27) * var28;
               var25 += var28 * var28;
            }

            double var63;
            double var29 = -(var63 = var2 / BY.a(var25));
            int var31 = 0;
            int var32 = 0;
            double var33 = BY.d(1.0, var63);

            for (int var35 = 0; var35 < var50; var35++) {
               double var36;
               if ((var36 = this.z.b(var22, var35) - this.C.a(var35)) > 0.0) {
                  if (var29 * var36 < this.E.a(var35) - this.C.a(var35)) {
                     var29 = (this.E.a(var35) - this.C.a(var35)) / var36;
                     var31 = -var35 - 1;
                  }

                  if (var63 * var36 > this.F.a(var35) - this.C.a(var35)) {
                     var63 = BY.e(var33, (this.F.a(var35) - this.C.a(var35)) / var36);
                     var32 = var35 + 1;
                  }
               } else if (var36 < 0.0) {
                  if (var29 * var36 > this.F.a(var35) - this.C.a(var35)) {
                     var29 = (this.F.a(var35) - this.C.a(var35)) / var36;
                     var31 = var35 + 1;
                  }

                  if (var63 * var36 < this.E.a(var35) - this.C.a(var35)) {
                     var63 = BY.e(var33, (this.E.a(var35) - this.C.a(var35)) / var36);
                     var32 = -var35 - 1;
                  }
               }
            }

            var16 = var29;
            int var69 = var31;
            double var72;
            if (var22 == var1) {
               double var38 = var23 - 1.0;
               var72 = var29 * (var23 - var29 * var38);
               double var40;
               if (BY.w(var40 = var63 * (var23 - var63 * var38)) > BY.w(var72)) {
                  var16 = var63;
                  var72 = var40;
                  var69 = var32;
               }

               double var42;
               double var44 = (var42 = var23 * 0.5) - var38 * var29;
               double var46 = var42 - var38 * var63;
               double var48;
               if (var44 * var46 < 0.0 && BY.w(var48 = var42 * var42 / var38) > BY.w(var72)) {
                  var16 = var42 / var38;
                  var72 = var48;
                  var69 = 0;
               }
            } else {
               var72 = var29 * (1.0 - var29);
               double var76;
               if (BY.w(var76 = var63 * (1.0 - var63)) > BY.w(var72)) {
                  var16 = var63;
                  var72 = var76;
                  var69 = var32;
               }

               if (var63 > 0.5 && BY.w(var72) < 0.25) {
                  var16 = 0.5;
                  var72 = 0.25;
                  var69 = 0;
               }

               var72 *= var23;
            }

            double var77 = var16 * (1.0 - var16) * var25;
            double var82;
            if ((var82 = var72 * var72 * (var72 * var72 + var53 * var77 * var77)) > var56) {
               var56 = var82;
               var18 = var22;
               var20 = var16;
               var19 = var69;
            }
         }
      }

      for (int var59 = 0; var59 < var50; var59++) {
         double var61 = this.C.a(var59) + var20 * (this.z.b(var18, var59) - this.C.a(var59));
         this.H.a(var59, BY.e(this.E.a(var59), BY.d(this.F.a(var59), var61)));
      }

      if (var19 < 0) {
         this.H.a(-var19 - 1, this.E.a(-var19 - 1));
      }

      if (var19 > 0) {
         this.H.a(var19 - 1, this.F.a(var19 - 1));
      }

      double var60 = var2 + var2;
      boolean var24 = false;
      double var64 = 0.0;

      while (true) {
         double var66 = 0.0;

         for (int var67 = 0; var67 < var50; var67++) {
            double var34 = var6.a(var67);
            var8.a(var67, 0.0);
            if (BY.d(this.C.a(var67) - this.E.a(var67), var34) > 0.0 || BY.e(this.C.a(var67) - this.F.a(var67), var34) < 0.0) {
               var8.a(var67, var60);
               var66 += var34 * var34;
            }
         }

         if (var66 == 0.0) {
            return new double[]{var52, 0.0};
         }

         double var68;
         if ((var68 = var2 * var2) > 0.0) {
            var16 = BY.a(var68 / var66);

            for (int var70 = 0; var70 < var50; var70++) {
               if (var8.a(var70) == var60) {
                  double var74;
                  if ((var74 = this.C.a(var70) - var16 * var6.a(var70)) <= this.E.a(var70)) {
                     var8.a(var70, this.E.a(var70) - this.C.a(var70));
                     var8.a(var70);
                  } else if (var74 >= this.F.a(var70)) {
                     var8.a(var70, this.F.a(var70) - this.C.a(var70));
                     var8.a(var70);
                  } else {
                     var6.a(var70);
                  }
               }
            }
         }

         double var71 = 0.0;

         for (int var37 = 0; var37 < var50; var37++) {
            double var78 = var6.a(var37);
            if (var8.a(var37) == var60) {
               var8.a(var37, -var16 * var78);
               double var83 = BY.d(this.F.a(var37), this.C.a(var37) + var8.a(var37));
               this.I.a(var37, BY.e(this.E.a(var37), var83));
            } else if (var8.a(var37) == 0.0) {
               this.I.a(var37, this.C.a(var37));
            } else if (var78 > 0.0) {
               this.I.a(var37, this.E.a(var37));
            } else {
               this.I.a(var37, this.F.a(var37));
            }

            var71 += var78 * var8.a(var37);
         }

         double var75 = 0.0;

         for (int var39 = 0; var39 < var5; var39++) {
            double var84 = 0.0;

            for (int var86 = 0; var86 < var50; var86++) {
               var84 += this.z.b(var39, var86) * var8.a(var86);
            }

            var75 += var7.a(var39) * var84 * var84;
         }

         if (var24) {
            var75 = -var75;
         }

         double var62;
         if (var75 > -var71 && var75 < -var71 * (1.0 + BY.a(2.0))) {
            double var80 = -var71 / var75;

            for (int var41 = 0; var41 < var50; var41++) {
               double var87 = this.C.a(var41) + var80 * var8.a(var41);
               this.I.a(var41, BY.e(this.E.a(var41), BY.d(this.F.a(var41), var87)));
            }

            double var85;
            var62 = (var85 = var71 * 0.5 * var80) * var85;
         } else {
            double var79;
            var62 = (var79 = var71 + var75 * 0.5) * var79;
         }

         if (var24) {
            if (var64 > var62) {
               for (int var65 = 0; var65 < var50; var65++) {
                  this.I.a(var65, var9.a(var65));
               }

               var62 = var64;
            }

            return new double[]{var52, var62};
         }

         for (int var81 = 0; var81 < var50; var81++) {
            var6.a(var81, -var6.a(var81));
            var9.a(var81, this.I.a(var81));
         }

         var64 = var62;
         var24 = true;
      }
   }

   private void c(double[] var1, double[] var2) {
      sA var3 = this.u;
      int var33 = this.u.a.length;
      int var4 = this.q;
      int var5 = this.x.f();
      double var6 = this.r * this.r;
      double var8 = 1.0 / var6;
      int var10 = var33 + 1;

      for (int var11 = 0; var11 < var33; var11++) {
         this.A.a(var11, this.u.a(var11));

         for (int var12 = 0; var12 < var4; var12++) {
            this.z.c(var12, var11, 0.0);
         }

         for (int var38 = 0; var38 < var5; var38++) {
            this.x.c(var38, var11, 0.0);
         }
      }

      int var35 = 0;

      for (int var39 = var33 * var10 / 2; var35 < var39; var35++) {
         this.L.a(var35, 0.0);
      }

      for (int var36 = 0; var36 < var4; var36++) {
         this.G.a(var36, 0.0);
         int var40 = 0;

         for (int var13 = var4 - var10; var40 < var13; var40++) {
            this.y.c(var36, var40, 0.0);
         }
      }

      var35 = 0;
      int var41 = 0;
      double var42 = Double.NaN;

      do {
         int var15 = (var5 = this.b()) - var33;
         int var16 = var5 - 1;
         int var17 = var15 - 1;
         double var19 = 0.0;
         double var21 = 0.0;
         if (var5 <= 2 * var33) {
            if (var5 > 0 && var5 <= var33) {
               var19 = this.r;
               if (this.F.a(var16) == 0.0) {
                  var19 = -var19;
               }

               this.z.c(var5, var16, var19);
            } else if (var5 > var33) {
               var19 = this.z.b(var15, var17);
               var21 = -this.r;
               if (this.E.a(var17) == 0.0) {
                  var21 = BY.d(2.0 * this.r, this.F.a(var17));
               }

               if (this.F.a(var17) == 0.0) {
                  var21 = BY.e(-2.0 * this.r, this.E.a(var17));
               }

               this.z.c(var5, var17, var21);
            }
         } else {
            int var23 = (var5 - var10) / var33;
            if ((var35 = (var41 = var5 - var23 * var33 - var33) + var23) > var33) {
               int var24 = var41;
               var41 = var35 - var33;
               var35 = var24;
            }

            int var45 = var35 - 1;
            int var25 = var41 - 1;
            this.z.c(var5, var45, this.z.b(var35, var45));
            this.z.c(var5, var25, this.z.b(var41, var25));
         }

         for (int var43 = 0; var43 < var33; var43++) {
            this.u.a(var43, BY.d(BY.e(var1[var43], this.A.a(var43) + this.z.b(var5, var43)), var2[var43]));
            if (this.z.b(var5, var43) == this.E.a(var43)) {
               this.u.a(var43, var1[var43]);
            }

            if (this.z.b(var5, var43) == this.F.a(var43)) {
               this.u.a(var43, var2[var43]);
            }
         }

         double var44 = this.a(this.u.g());
         double var46 = this.t ? var44 : -var44;
         int var18 = this.b();
         this.B.a(var5, var46);
         if (var18 == 1) {
            var42 = var46;
            this.w = 0;
         } else if (var46 < this.B.a(this.w)) {
            this.w = var5;
         }

         if (var18 <= 2 * var33 + 1) {
            if (var18 >= 2 && var18 <= var33 + 1) {
               this.D.a(var16, (var46 - var42) / var19);
               if (var4 < var18 + var33) {
                  double var47 = 1.0 / var19;
                  this.x.c(0, var16, -var47);
                  this.x.c(var5, var16, var47);
                  this.x.c(var4 + var16, var16, var6 * -0.5);
               }
            } else if (var18 >= var33 + 2) {
               int var28 = var15 * (var15 + 1) / 2 - 1;
               double var29 = (var46 - var42) / var21;
               double var31 = var21 - var19;
               this.L.a(var28, 2.0 * (var29 - this.D.a(var17)) / var31);
               this.D.a(var17, (this.D.a(var17) * var21 - var29 * var19) / var31);
               if (var19 * var21 < 0.0 && var46 < this.B.a(var5 - var33)) {
                  this.B.a(var5, this.B.a(var5 - var33));
                  this.B.a(var5 - var33, var46);
                  if (this.w == var5) {
                     this.w = var5 - var33;
                  }

                  this.z.c(var5 - var33, var17, var21);
                  this.z.c(var5, var17, var19);
               }

               this.x.c(0, var17, -(var19 + var21) / (var19 * var21));
               this.x.c(var5, var17, -0.5 / this.z.b(var5 - var33, var17));
               this.x.c(var5 - var33, var17, -this.x.b(0, var17) - this.x.b(var5, var17));
               this.y.c(0, var17, BY.a(2.0) / (var19 * var21));
               this.y.c(var5, var17, BY.a(0.5) / var6);
               this.y.c(var5 - var33, var17, -this.y.b(0, var17) - this.y.b(var5, var17));
            }
         } else {
            this.y.c(0, var17, var8);
            this.y.c(var5, var17, var8);
            this.y.c(var35, var17, -var8);
            this.y.c(var41, var17, -var8);
            int var48 = var35 * (var35 - 1) / 2 + var41 - 1;
            double var49 = this.z.b(var5, var35 - 1) * this.z.b(var5, var41 - 1);
            this.L.a(var48, (var42 - this.B.a(var35) - this.B.a(var41) + var46) / var49);
         }
      } while (this.b() < var4);
   }

   private double[] a(double var1, sA var3, sA var4, sA var5, sA var6, sA var7) {
      sA var91 = this.u;
      int var8 = this.u.a.length;
      int var9 = this.q;
      double var29 = 0.0;
      int var10 = -1;
      double var37 = 0.0;
      double var44 = 0.0;
      double var48 = 0.0;
      double var50 = 0.0;
      double var52 = 0.0;
      double var59 = 0.0;
      double var67 = 0.0;
      double var71 = 0.0;
      int var75 = 0;
      double var76 = 0.0;
      double var78 = 0.0;
      double var82 = 0.0;
      int var84 = 0;
      int var16 = 0;
      int var11 = 0;

      for (int var85 = 0; var85 < var8; var85++) {
         var4.a(var85, 0.0);
         if (this.C.a(var85) <= this.E.a(var85)) {
            if (this.D.a(var85) >= 0.0) {
               var4.a(var85, -1.0);
            }
         } else if (this.C.a(var85) >= this.F.a(var85) && this.D.a(var85) <= 0.0) {
            var4.a(var85, 1.0);
         }

         if (var4.a(var85) != 0.0) {
            var11++;
         }

         this.J.a(var85, 0.0);
         var3.a(var85, this.D.a(var85));
      }

      double var57 = var1 * var1;
      double var39 = 0.0;
      double var12 = -1.0;
      short var108 = 20;

      label372:
      while (true) {
         label368:
         while (true) {
            label366:
            while (true) {
               switch (var108) {
                  case 20:
                     var29 = 0.0;
                  case 30:
                     break;
                  case 50:
                     double var55 = var57;
                     double var14 = 0.0;
                     double var94 = 0.0;

                     for (int var115 = 0; var115 < var8; var115++) {
                        if (var4.a(var115) == 0.0) {
                           double var128 = this.J.a(var115);
                           var55 -= var128 * var128;
                           var14 += var5.a(var115) * this.J.a(var115);
                           var94 += var5.a(var115) * var6.a(var115);
                        }
                     }

                     if (var55 <= 0.0) {
                        var108 = 90;
                        continue;
                     } else {
                        double var105 = BY.a(var82 * var55 + var14 * var14);
                        double var33;
                        if (var14 < 0.0) {
                           var33 = (var105 - var14) / var82;
                        } else {
                           var33 = var55 / (var105 + var14);
                        }

                        double var80 = var33;
                        if (var94 > 0.0) {
                           var80 = BY.d(var33, var71 / var94);
                        }

                        var10 = -1;

                        for (int var116 = 0; var116 < var8; var116++) {
                           if (var5.a(var116) != 0.0) {
                              double var46 = this.C.a(var116) + this.J.a(var116);
                              if (var5.a(var116) > 0.0) {
                                 var105 = (this.F.a(var116) - var46) / var5.a(var116);
                              } else {
                                 var105 = (this.E.a(var116) - var46) / var5.a(var116);
                              }

                              if (var105 < var80) {
                                 var80 = var105;
                                 var10 = var116;
                              }
                           }
                        }

                        double var96 = 0.0;
                        if (var80 > 0.0) {
                           var16++;
                           var105 = var94 / var82;
                           if (var10 == -1 && var105 > 0.0 && (var12 = BY.d(var12, var105)) == -1.0) {
                              var12 = var105;
                           }

                           var59 = var71;
                           var71 = 0.0;

                           for (int var117 = 0; var117 < var8; var117++) {
                              var3.a(var117, var3.a(var117) + var80 * var6.a(var117));
                              if (var4.a(var117) == 0.0) {
                                 double var129 = var3.a(var117);
                                 var71 += var129 * var129;
                              }

                              this.J.a(var117, this.J.a(var117) + var80 * var5.a(var117));
                           }

                           double var118;
                           var96 = BY.e(var118 = var80 * (var59 - var80 * 0.5 * var94), 0.0);
                           var39 += var96;
                        }

                        if (var10 >= 0) {
                           var11++;
                           var4.a(var10, 1.0);
                           if (var5.a(var10) < 0.0) {
                              var4.a(var10, -1.0);
                           }

                           double var120 = this.J.a(var10);
                           if ((var57 -= var120 * var120) <= 0.0) {
                              var108 = 190;
                           } else {
                              var108 = 20;
                           }
                           continue;
                        } else if (var80 < var33) {
                           if (var16 == var84) {
                              var108 = 190;
                           } else if (var96 <= var39 * 0.01) {
                              var108 = 190;
                           } else {
                              var29 = var71 / var59;
                              var108 = 30;
                           }
                           continue;
                        }
                     }
                  case 90:
                     var12 = 0.0;
                  case 100:
                     break label366;
                  case 120:
                     var16++;
                     double var99;
                     if ((var99 = var71 * var67 - var50 * var50) <= var39 * 1.0E-4 * var39) {
                        var108 = 190;
                        continue;
                     } else {
                        var99 = BY.a(var99);

                        for (int var113 = 0; var113 < var8; var113++) {
                           if (var4.a(var113) == 0.0) {
                              var5.a(var113, (var50 * this.J.a(var113) - var67 * var3.a(var113)) / var99);
                           } else {
                              var5.a(var113, 0.0);
                           }
                        }

                        var52 = -var99;
                        var48 = 1.0;
                        var10 = -1;
                        int var114 = 0;

                        while (var114 < var8) {
                           label308: {
                              if (var4.a(var114) == 0.0) {
                                 double var61 = this.C.a(var114) + this.J.a(var114) - this.E.a(var114);
                                 double var63 = this.F.a(var114) - this.C.a(var114) - this.J.a(var114);
                                 if (var61 <= 0.0) {
                                    var11++;
                                    var4.a(var114, -1.0);
                                    break label308;
                                 }

                                 if (var63 <= 0.0) {
                                    var11++;
                                    var4.a(var114, 1.0);
                                    break label308;
                                 }

                                 double var125 = this.J.a(var114);
                                 double var136 = var5.a(var114);
                                 double var27 = var125 * var125 + var136 * var136;
                                 var125 = this.C.a(var114) - this.E.a(var114);
                                 if ((var99 = var27 - var125 * var125) > 0.0) {
                                    var99 = BY.a(var99) - var5.a(var114);
                                    if (var48 * var99 > var61) {
                                       var48 = var61 / var99;
                                       var10 = var114;
                                       var44 = -1.0;
                                    }
                                 }

                                 var125 = this.F.a(var114) - this.C.a(var114);
                                 if ((var99 = var27 - var125 * var125) > 0.0) {
                                    var99 = BY.a(var99) + var5.a(var114);
                                    if (var48 * var99 > var63) {
                                       var48 = var63 / var99;
                                       var10 = var114;
                                       var44 = 1.0;
                                    }
                                 }
                              }

                              var114++;
                              continue;
                           }

                           var108 = 210;
                           continue label366;
                        }
                        break label368;
                     }
                  case 150:
                     double var23 = 0.0;
                     double var19 = 0.0;
                     double var17 = 0.0;

                     for (int var109 = 0; var109 < var8; var109++) {
                        if (var4.a(var109) == 0.0) {
                           var23 += var5.a(var109) * var6.a(var109);
                           var19 += this.J.a(var109) * var6.a(var109);
                           var17 += this.J.a(var109) * var7.a(var109);
                        }
                     }

                     double var65 = 0.0;
                     int var2 = -1;
                     double var69 = 0.0;
                     int var92 = (int)(var48 * 17.0 + 3.1);

                     for (int var110 = 0; var110 < var92; var110++) {
                        double var25 = ((var37 = var48 * var110 / var92) + var37) / (1.0 + var37 * var37);
                        double var42 = var23 + var37 * (var37 * var17 - var19 - var19);
                        double var73;
                        if ((var73 = var25 * (var37 * var50 - var52 - var25 * 0.5 * var42)) > var65) {
                           var65 = var73;
                           var2 = var110;
                           var76 = var69;
                        } else if (var110 == var2 + 1) {
                           var78 = var73;
                        }

                        var69 = var73;
                     }

                     if (var2 < 0) {
                        var108 = 190;
                        continue;
                     } else {
                        if (var2 < var92) {
                           double var97 = (var78 - var76) / (var65 + var65 - var76 - var78);
                           var37 = var48 * (var2 + var97 * 0.5) / var92;
                        }

                        double var21 = (1.0 - var37 * var37) / (1.0 + var37 * var37);
                        double var95 = (var37 + var37) / (1.0 + var37 * var37);
                        double var98 = var23 + var37 * (var37 * var17 - var19 - var19);
                        double var31;
                        if ((var31 = var95 * (var37 * var50 - var52 - var95 * 0.5 * var98)) <= 0.0) {
                           var108 = 190;
                           continue;
                        } else {
                           var50 = 0.0;
                           var71 = 0.0;

                           for (int var111 = 0; var111 < var8; var111++) {
                              var3.a(var111, var3.a(var111) + (var21 - 1.0) * var7.a(var111) + var95 * var6.a(var111));
                              if (var4.a(var111) == 0.0) {
                                 this.J.a(var111, var21 * this.J.a(var111) + var95 * var5.a(var111));
                                 var50 += this.J.a(var111) * var3.a(var111);
                                 double var123 = var3.a(var111);
                                 var71 += var123 * var123;
                              }

                              var7.a(var111, var21 * var7.a(var111) + var95 * var6.a(var111));
                           }

                           var39 += var31;
                           if (var10 >= 0 && var2 == var92) {
                              var11++;
                              var4.a(var10, var44);
                              var108 = 100;
                              continue;
                           } else if (var31 > var39 * 0.01) {
                              var108 = 120;
                              continue;
                           }
                        }
                     }
                  case 190:
                     break label372;
                  case 210:
                     break label368;
                  default:
                     throw new oX(px.SIMPLE_MESSAGE, "trsbox");
               }

               var82 = 0.0;

               for (int var121 = 0; var121 < var8; var121++) {
                  if (var4.a(var121) != 0.0) {
                     var5.a(var121, 0.0);
                  } else if (var29 == 0.0) {
                     var5.a(var121, -var3.a(var121));
                  } else {
                     var5.a(var121, var29 * var5.a(var121) - var3.a(var121));
                  }

                  double var132 = var5.a(var121);
                  var82 += var132 * var132;
               }

               if (var82 == 0.0) {
                  var108 = 190;
               } else {
                  if (var29 == 0.0) {
                     var71 = var82;
                     var84 = var16 + var8 - var11;
                  }

                  if (var71 * var57 <= var39 * 1.0E-4 * var39) {
                     var108 = 190;
                  } else {
                     var108 = 210;
                  }
               }
            }

            if (var11 >= var8 - 1) {
               var108 = 190;
            } else {
               var67 = 0.0;
               var50 = 0.0;
               var71 = 0.0;

               for (int var119 = 0; var119 < var8; var119++) {
                  if (var4.a(var119) == 0.0) {
                     double var130 = this.J.a(var119);
                     var67 += var130 * var130;
                     var50 += this.J.a(var119) * var3.a(var119);
                     var130 = var3.a(var119);
                     var71 += var130 * var130;
                     var5.a(var119, this.J.a(var119));
                  } else {
                     var5.a(var119, 0.0);
                  }
               }

               var75 = var16;
               var108 = 210;
            }
         }

         int var86 = 0;

         for (int var87 = 0; var87 < var8; var87++) {
            var6.a(var87, 0.0);

            for (int var88 = 0; var88 <= var87; var88++) {
               if (var88 < var87) {
                  var6.a(var87, var6.a(var87) + this.L.a(var86) * var5.a(var88));
               }

               var6.a(var88, var6.a(var88) + this.L.a(var86) * var5.a(var87));
               var86++;
            }
         }

         tu var122 = this.z.a(var5).i(this.G);

         for (int var133 = 0; var133 < var9; var133++) {
            if (this.G.a(var133) != 0.0) {
               for (int var89 = 0; var89 < var8; var89++) {
                  var6.a(var89, var6.a(var89) + var122.a(var133) * this.z.b(var133, var89));
               }
            }
         }

         if (var12 != 0.0) {
            var108 = 50;
         } else if (var16 > var75) {
            var108 = 150;
         } else {
            for (int var134 = 0; var134 < var8; var134++) {
               var7.a(var134, var6.a(var134));
            }

            var108 = 120;
         }
      }

      double var93 = 0.0;

      for (int var112 = 0; var112 < var8; var112++) {
         double var124 = BY.d(this.C.a(var112) + this.J.a(var112), this.F.a(var112));
         this.H.a(var112, BY.e(var124, this.E.a(var112)));
         if (var4.a(var112) == -1.0) {
            this.H.a(var112, this.E.a(var112));
         }

         if (var4.a(var112) == 1.0) {
            this.H.a(var112, this.F.a(var112));
         }

         this.J.a(var112, this.H.a(var112) - this.C.a(var112));
         double var135 = this.J.a(var112);
         var93 += var135 * var135;
      }

      return new double[]{var93, var12};
   }

   private void a(double var1, double var3, int var5) {
      sA var6 = this.u;
      int var28 = this.u.a.length;
      int var7 = this.q;
      int var8 = this.q - var28 - 1;
      sA var9 = new sA(var7 + var28);
      double var10 = 0.0;

      for (int var12 = 0; var12 < var7; var12++) {
         for (int var13 = 0; var13 < var8; var13++) {
            var10 = BY.e(var10, BY.w(this.y.b(var12, var13)));
         }
      }

      var10 *= 1.0E-20;

      for (int var31 = 1; var31 < var8; var31++) {
         if (BY.w(this.y.b(var5, var31)) > var10) {
            double var15 = this.y.b(var5, 0);
            double var17 = this.y.b(var5, var31);
            double var19 = BY.a(var15 * var15 + var17 * var17);
            double var21 = this.y.b(var5, 0) / var19;
            double var23 = this.y.b(var5, var31) / var19;

            for (int var25 = 0; var25 < var7; var25++) {
               double var26 = var21 * this.y.b(var25, 0) + var23 * this.y.b(var25, var31);
               this.y.c(var25, var31, var21 * this.y.b(var25, var31) - var23 * this.y.b(var25, 0));
               this.y.c(var25, 0, var26);
            }
         }

         this.y.c(var5, var31, 0.0);
      }

      for (int var32 = 0; var32 < var7; var32++) {
         var9.a(var32, this.y.b(var5, 0) * this.y.b(var32, 0));
      }

      double var33 = var9.a(var5);
      double var14 = this.K.a(var5);
      this.K.a(var5, this.K.a(var5) - 1.0);
      double var16 = BY.a(var3);
      double var18 = var14 / var16;
      double var20 = this.y.b(var5, 0) / var16;

      for (int var22 = 0; var22 < var7; var22++) {
         this.y.c(var22, 0, var18 * this.y.b(var22, 0) - var20 * this.K.a(var22));
      }

      for (int var34 = 0; var34 < var28; var34++) {
         int var35 = var7 + var34;
         var9.a(var35, this.x.b(var5, var34));
         double var24 = (var33 * this.K.a(var35) - var14 * var9.a(var35)) / var3;
         double var36 = (-var1 * var9.a(var35) - var14 * this.K.a(var35)) / var3;

         for (int var29 = 0; var29 <= var35; var29++) {
            this.x.c(var29, var34, this.x.b(var29, var34) + var24 * this.K.a(var29) + var36 * var9.a(var29));
            if (var29 >= var7) {
               this.x.c(var35, var29 - var7, this.x.b(var29, var34));
            }
         }
      }
   }

   private void d(double[] var1, double[] var2) {
      int var3;
      if ((var3 = this.f().length) < 2) {
         throw new pn(var3, 2, true);
      }

      int[] var4 = new int[]{var3 + 2, (var3 + 2) * (var3 + 1) / 2};
      if (this.q >= var4[0] && this.q <= var4[1]) {
         this.v = new double[var3];
         double var6 = 2.0 * this.r;
         double var8 = Double.POSITIVE_INFINITY;

         for (int var10 = 0; var10 < var3; var10++) {
            this.v[var10] = var2[var10] - var1[var10];
            var8 = BY.d(var8, this.v[var10]);
         }

         if (var8 < var6) {
            this.r = var8 / 3.0;
         }

         this.x = new sy(var3 + this.q, var3);
         this.y = new sy(this.q, this.q - var3 - 1);
         this.z = new sy(this.q, var3);
         this.A = new sA(var3);
         this.B = new sA(this.q);
         this.C = new sA(var3);
         this.D = new sA(var3);
         this.E = new sA(var3);
         this.F = new sA(var3);
         this.G = new sA(this.q);
         this.H = new sA(var3);
         this.I = new sA(var3);
         this.J = new sA(var3);
         this.K = new sA(var3 + this.q);
         this.L = new sA(var3 * (var3 + 1) / 2);
      } else {
         throw new po(px.NUMBER_OF_INTERPOLATION_POINTS, this.q, var4[0], var4[1]);
      }
   }

   private static String b(int var0) {
      StackTraceElement var2 = new Throwable().getStackTrace()[var0];
      return var2.getMethodName() + " (at line " + var2.getLineNumber() + ")";
   }

   private static void i() {
   }

   private static void j() {
   }

   // $VF: synthetic method
   static String a(int var0) {
      byte var2 = 3;
      StackTraceElement var3 = new Throwable().getStackTrace()[var2];
      return var3.getMethodName() + " (at line " + var3.getLineNumber() + ")";
   }

   static class a extends RuntimeException {
      private static final long a = 745350979634801853L;
      private static final String b = "If this exception is thrown, just remove it from the code";

      a() {
         StringBuilder var10001 = new StringBuilder("If this exception is thrown, just remove it from the code ");
         byte var1 = 3;
         var1 = 3;
         StackTraceElement var4 = new Throwable().getStackTrace()[var1];
         super(var10001.append(var4.getMethodName() + " (at line " + var4.getLineNumber() + ")").toString());
      }
   }
}
