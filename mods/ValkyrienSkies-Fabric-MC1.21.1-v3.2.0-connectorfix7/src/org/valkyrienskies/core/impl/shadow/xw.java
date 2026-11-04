package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class xw extends xp {
   private int c;
   private final boolean d;
   private final int e;
   private double[] f;
   private int g;
   private int h;
   boolean b = true;
   private final boolean i;
   private final int j;
   private final double k;
   private double l;
   private double m;
   private double n;
   private double o;
   private int p;
   private double q;
   private tq r;
   private double s;
   private double t;
   private double u;
   private double v;
   private double w;
   private double x;
   private double y;
   private double z;
   private double A;
   private double B;
   private tq C;
   private tq D;
   private tq E;
   private double F;
   private tq G;
   private tq H;
   private tq I;
   private tq J;
   private tq K;
   private tq L;
   private int M;
   private double[] N;
   private int O;
   private final zF P;
   private final List<Double> Q = new ArrayList<>();
   private final List<tq> R = new ArrayList<>();
   private final List<Double> S = new ArrayList<>();
   private final List<tq> T = new ArrayList<>();

   private xw(int var1, double var2, boolean var4, int var5, int var6, zF var7, boolean var8, wK<wQ> var9) {
      super(var9);
      this.j = var1;
      this.k = var2;
      this.d = var4;
      this.h = var5;
      this.e = var6;
      this.P = var7;
      this.i = var8;
   }

   private List<Double> b() {
      return this.Q;
   }

   private List<tq> d() {
      return this.R;
   }

   private List<Double> m() {
      return this.S;
   }

   private List<tq> o() {
      return this.T;
   }

   @Override
   public final wQ c(wO... var1) {
      return super.c(var1);
   }

   private wQ p() {
      this.b = this.n().equals(xi.MINIMIZE);
      xw.b var1 = new xw.b();
      double[] var2 = this.e();
      this.g = var2.length;
      double[] var32 = var2;
      xw var14 = this;
      if (this.c <= 0) {
         throw new pk(var14.c);
      }

      double[][] var33 = new double[var32.length][1];

      for (int var34 = 0; var34 < var32.length; var34++) {
         var33[var34][0] = var14.f[var34];
      }

      sy var130 = new sy(var33, false);
      var14.t = f(var130);
      var14.l = 1000.0 * f(var130);
      var14.m = 1.0E-11 * f(var130);
      var14.n = 1.0E-12;
      var14.o = 1.0E-13;
      var14.p = var14.c / 2;
      var14.q = BY.k(var14.p + 0.5);
      double var10002 = var14.p;
      double var46 = 1.0;
      double var44 = var10002;
      double var42 = 1.0;
      int var6;
      double[][] var9 = new double[var6 = (int)(var44 - 1.0 + 1.0)][1];
      double var50 = 1.0;

      for (int var12 = 0; var12 < var6; var12++) {
         var9[var12][0] = var50++;
      }

      sy var151;
      double[][] var43 = new double[(var151 = new sy(var9, false)).f()][var151.g()];

      for (int var152 = 0; var152 < var151.f(); var152++) {
         for (int var45 = 0; var45 < var151.g(); var45++) {
            var43[var152][var45] = BY.k(var151.b(var152, var45));
         }
      }

      var14.r = new sy(var43, false).b(-1.0).a(var14.q);
      double var35 = 0.0;
      double var37 = 0.0;

      for (int var11 = 0; var11 < var14.p; var11++) {
         double var40 = var14.r.b(var11, 0);
         var35 += var40;
         var37 += var40 * var40;
      }

      var14.r = var14.r.b(1.0 / var35);
      var14.s = var35 * var35 / var37;
      var14.u = (4.0 + var14.s / var14.g) / (var14.g + 4 + 2.0 * var14.s / var14.g);
      var14.v = (var14.s + 2.0) / (var14.g + var14.s + 3.0);
      var14.w = (1.0 + 2.0 * BY.e(0.0, BY.a((var14.s - 1.0) / (var14.g + 1)) - 1.0)) * BY.e(0.3, 1.0 - var14.g / (1.0E-6 + var14.j)) + var14.v;
      var14.x = 2.0 / ((var14.g + 1.3) * (var14.g + 1.3) + var14.s);
      var14.y = BY.d(1.0 - var14.x, 2.0 * (var14.s - 2.0 + 1.0 / var14.s) / ((var14.g + 2) * (var14.g + 2) + var14.s));
      var14.A = BY.d(1.0, var14.x * (var14.g + 1.5) / 3.0);
      var14.B = BY.d(1.0 - var14.x, var14.y * (var14.g + 1.5) / 3.0);
      var14.z = BY.a((double)var14.g) * (1.0 - 1.0 / (4.0 * var14.g) + 1.0 / (21.0 * var14.g * var14.g));
      var14.C = td.c(var32);
      var14.J = var130.b(1.0 / var14.t);
      var14.L = c(var14.J);
      var14.D = c(var14.g, 1);
      var14.E = c(var14.g, 1);
      var14.F = var14.E.c();
      var14.G = b(var14.g, var14.g);
      var14.H = a(var14.g, 1);
      var14.I = b(var14.G, a(var14.J.d(), var14.g, 1));
      var14.K = var14.G.c(e(c(var14.H)).c(var14.G.d()));
      var14.O = 10 + (int)((double)(30 * var14.g) / var14.c);
      var14.N = new double[var14.O];

      for (int var86 = 0; var86 < var14.O; var86++) {
         var14.N[var86] = Double.MAX_VALUE;
      }

      this.M = 0;
      xw.e var68;
      double var4 = (var68 = var1.a(var2)).a + var68.b;
      a(this.N, var4);
      wQ var69 = new wQ(this.e(), this.b ? var4 : -var4);
      wQ var3 = null;

      for (this.M = 1; this.M <= this.j; this.M++) {
         this.l();
         int var122 = this.c;
         int var112 = this.g;
         var14 = this;
         double[][] var131 = new double[var112][var122];

         for (int var140 = 0; var140 < var112; var140++) {
            for (int var36 = 0; var36 < var122; var36++) {
               var131[var140][var36] = var14.P.nextGaussian();
            }
         }

         sy var70 = new sy(var131, false);
         sy var7 = c(this.g, this.c);
         double[] var8 = new double[this.c];
         xw.e[] var78 = new xw.e[this.c];

         for (int var90 = 0; var90 < this.c; var90++) {
            tq var13 = null;

            for (int var10 = 0; var10 < this.e + 1; var10++) {
               if (this.h <= 0) {
                  var13 = this.C.a(this.I.c(var70.c(var90)).b(this.t));
               } else {
                  var13 = this.C.a(b(this.J, var70.c(var90)).b(this.t));
               }

               if (var10 >= this.e) {
                  break;
               }

               var32 = var13.g(0);
               xw.b var101 = var1;
               double[] var123 = var1.a.f();
               double[] var132 = var101.a.g();
               int var141 = 0;

               boolean var10000;
               while (true) {
                  if (var141 >= var32.length) {
                     var10000 = true;
                     break;
                  }

                  if (var32[var141] < var123[var141]) {
                     var10000 = false;
                     break;
                  }

                  if (var32[var141] > var132[var141]) {
                     var10000 = false;
                     break;
                  }

                  var141++;
               }

               if (var10000) {
                  break;
               }

               int var114 = this.g;
               var14 = this;
               double[] var124 = new double[var114];

               for (int var133 = 0; var133 < var114; var133++) {
                  var124[var133] = var14.P.nextGaussian();
               }

               var70.b(var90, var124);
            }

            int var134 = var90;
            sy var125 = var7;
            boolean var115 = false;
            tq var103 = var13;

            for (int var142 = 0; var142 < var103.f(); var142++) {
               var125.c(var142, var134, var103.b(var142, 0));
            }

            try {
               var78[var90] = var1.a(var7.g(var90));
            } catch (pp var67) {
               return var69;
            }
         }

         xw.e[] var104 = var78;
         double var126 = Double.NEGATIVE_INFINITY;
         var35 = Double.MAX_VALUE;
         xw.e[] var146 = var104;
         int var38 = var104.length;

         for (int var87 = 0; var87 < var38; var87++) {
            xw.e var148;
            if ((var148 = var146[var87]).a > var126) {
               var126 = var148.a;
            }

            if (var148.a < var35) {
               var35 = var148.a;
            }
         }

         double var91 = var126 - var35;

         for (int var84 = 0; var84 < var78.length; var84++) {
            xw.e var66;
            xw.e var164;
            var8[var84] = (var66 = var78[var84]).a + (var164 = var78[var84]).b * var91;
         }

         int[] var85 = c(var8);
         tq var79 = this.C;
         var7 = a(var7, Ch.a(var85, this.p));
         this.C = var7.c(this.r);
         sy var88;
         tq var92 = (var88 = a(var70, Ch.a(var85, this.p))).c(this.r);
         tq var127 = var79;
         tq var116 = var92;
         var14 = this;
         this.E = var14.E.b(1.0 - var14.v).a(var14.G.c(var116).b(BY.a(var14.v * (2.0 - var14.v) * var14.s)));
         var14.F = var14.E.c();
         boolean var135 = var14.F / BY.a(1.0 - BY.a(1.0 - var14.v, 2 * var14.M)) / var14.z < 1.4 + 2.0 / (var14.g + 1.0);
         var14.D = var14.D.b(1.0 - var14.u);
         if (var135) {
            var14.D = var14.D.a(var14.C.b(var127).b(BY.a(var14.u * (2.0 - var14.u) * var14.s) / var14.t));
         }

         boolean var93 = var135;
         if (this.h <= 0) {
            tq var145 = var79;
            int[] var144 = var85;
            var130 = var70;
            sy var128 = var7;
            boolean var117 = var93;
            var14 = this;
            var37 = 0.0;
            if (var14.x + var14.y > 0.0) {
               tq var89 = var128.b(a(var145, 1, var14.p)).b(1.0 / var14.t);
               tq var149 = var14.D.c(var14.D.d()).b(var14.x);
               double var41 = (var117 ? 0.0 : var14.x * var14.u * (2.0 - var14.u)) + (1.0 - var14.x - var14.y);
               if (!var14.d) {
                  var14.K = var14.K.b(var41).a(var149).a(var89.b(var14.y).c(b(a(var14.r, 1, var14.g), var89.d())));
               } else {
                  var37 = (1.0 - var14.y) * 0.25 * var14.s / (BY.a(var14.g + 2, 1.5) + 2.0 * var14.s);
                  int[] var47 = b(var144);
                  sy var71;
                  sy var94 = c(var71 = a(var130, Ch.a(var47, var14.p)));
                  double[][] var61 = new double[1][var94.g()];

                  for (int var62 = 0; var62 < var94.g(); var62++) {
                     double var63 = 0.0;

                     for (int var65 = 0; var65 < var94.f(); var65++) {
                        var63 += var94.b(var65, var62);
                     }

                     var61[0][var62] = var63;
                  }

                  sy var80;
                  int[] var153 = c((var80 = b(new sy(var61, false))).f(0));
                  sy var51 = a(var80, var153);
                  int[] var95 = b(var153);
                  sy var165 = a(var80, var95);
                  sy var154 = var51;
                  sy var96 = var165;
                  double[][] var157 = new double[var165.f()][var96.g()];

                  for (int var159 = 0; var159 < var96.f(); var159++) {
                     for (int var64 = 0; var64 < var96.g(); var64++) {
                        var157[var159][var64] = var96.b(var159, var64) / var154.b(var159, var64);
                     }
                  }

                  sy var81 = new sy(var157, false);
                  int[] var97 = var153;
                  int[] var155 = new int[var153.length];
                  int var158 = 0;

                  while (var158 < var97.length) {
                     var155[var97[var158]] = var158++;
                  }

                  int[] var98 = var155;
                  sy var82 = a(var81, var98);
                  double var56 = 0.33999999999999997 / c(var82).c(var14.r).b(0, 0);
                  if (var37 > var56) {
                     var37 = var56;
                  }

                  sy var72 = b(var71, a(var82, var14.g, 1));
                  tq var73;
                  tq var74 = (var73 = var14.I.c(var72)).c(e(var14.r)).c(var73.d());
                  var41 += var37 * 0.5;
                  var14.K = var14.K.b(var41).a(var149).a(var89.b(var14.y + var37 * 0.5).c(b(a(var14.r, 1, var14.g), var89.d()))).b(var74.b(var37));
               }
            }

            double var156 = var37;
            xw var99 = var14;
            if (var14.x + var99.y + var156 > 0.0 && var99.M % 1.0 / (var99.x + var99.y + var156) / var99.g / 10.0 < 1.0) {
               var99.K = a(var99.K, 0).a(a(var99.K, 1).d());
               sN var160 = new sN(var99.K);
               var99.G = var160.a();
               var99.H = var160.b();
               var99.J = e(var99.H);
               if (g(var99.J) <= 0.0) {
                  for (int var161 = 0; var161 < var99.g; var161++) {
                     if (var99.J.b(var161, 0) < 0.0) {
                        var99.J.c(var161, 0, 0.0);
                     }
                  }

                  double var162 = f(var99.J) / 1.0E14;
                  var99.K = var99.K.a(b(var99.g, var99.g).b(var162));
                  var99.J = var99.J.a(a(var99.g, 1).b(var162));
               }

               if (f(var99.J) > 1.0E14 * g(var99.J)) {
                  double var163 = f(var99.J) / 1.0E14 - g(var99.J);
                  var99.K = var99.K.a(b(var99.g, var99.g).b(var163));
                  var99.J = var99.J.a(a(var99.g, 1).b(var163));
               }

               var99.L = e(var99.K);
               var99.J = b(var99.J);
               var99.I = b(var99.G, a(var99.J.d(), var99.g, 1));
            }
         } else {
            sy var129 = var88;
            boolean var118 = var93;
            var14 = this;
            double var137 = (var118 ? 0.0 : var14.A * var14.u * (2.0 - var14.u)) + (1.0 - var14.A - var14.B);
            var14.L = var14.L.b(var137).a(c(var14.D).b(var14.A)).a(b(var14.L, c(var129).c(var14.r)).b(var14.B));
            var14.J = b(var14.L);
            if (var14.h > 1 && var14.M > var14.h) {
               var14.h = 0;
               var14.G = b(var14.g, var14.g);
               var14.I = e(var14.J);
               var14.K = e(var14.L);
            }
         }

         this.t = this.t * BY.i(BY.d(1.0, (this.F / this.z - 1.0) * this.v / this.w));
         double var20 = var8[var85[0]];
         double var22 = var8[var85[var85.length - 1]];
         if (var4 > var20) {
            var4 = var20;
            var3 = var69;
            var32 = var7.g(0);
            var69 = new wQ(var1.c(var32), this.b ? var20 : -var20);
            if (this.j() != null && this.j().a(this.M, var69, var3)) {
               break;
            }
         }

         if (this.k != 0.0 && var20 < (this.b ? this.k : -this.k)) {
            break;
         }

         double[] var75 = b(this.L).g(0);
         double[] var83 = this.D.g(0);

         for (int var26 = 0; var26 < this.g && !(this.t * BY.e(BY.w(var83[var26]), var75[var26]) > this.m); var26++) {
            if (var26 >= this.g - 1) {
               return var69;
            }
         }

         for (int var110 = 0; var110 < this.g; var110++) {
            if (this.t * var75[var110] > this.l) {
               return var69;
            }
         }

         double[] var108 = this.N;
         double var120 = Double.MAX_VALUE;

         for (int var138 = 0; var138 < var108.length; var138++) {
            if (var120 > var108[var138]) {
               var120 = var108[var138];
            }
         }

         double var111 = var120;
         double[] var109 = this.N;
         double var121 = -Double.MAX_VALUE;

         for (int var139 = 0; var139 < var109.length; var139++) {
            if (var121 < var109[var139]) {
               var121 = var109[var139];
            }
         }

         double var28 = var121;
         if (this.M > 2 && BY.e(var28, var22) - BY.d(var111, var20) < this.n
            || this.M > this.N.length && var28 - var111 < this.o
            || f(this.J) / g(this.J) > 1.0E7) {
            break;
         }

         if (this.j() != null) {
            wQ var76 = new wQ(var7.g(0), this.b ? var20 : -var20);
            if (var3 != null && this.j().a(this.M, var76, var3)) {
               break;
            }

            var3 = var76;
         }

         if (var4 == var8[var85[(int)(0.1 + this.c / 4.0)]]) {
            this.t = this.t * BY.i(0.2 + this.v / this.w);
         }

         if (this.M > 2 && BY.e(var28, var20) - BY.d(var111, var20) == 0.0) {
            this.t = this.t * BY.i(0.2 + this.v / this.w);
         }

         a(this.N, var20);
         if (this.i) {
            this.Q.add(this.t);
            this.S.add(var20);
            this.R.add(this.C.d());
            this.T.add(this.J.d().b(100000.0));
         }
      }

      return var69;
   }

   @Override
   public final void b(wO... var1) {
      super.b(var1);
      wO[] var6;
      int var2 = (var6 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var6[var3]) instanceof xw.d) {
            this.f = ((xw.d)var4).a();
         } else if (var4 instanceof xw.c) {
            xw.c var10;
            this.c = (var10 = (xw.c)var4).a;
         }
      }

      xw var11 = this;
      double[] var7 = this.e();
      double[] var8 = var11.f();
      double[] var9 = var11.g();
      if (var11.f != null) {
         if (var11.f.length != var7.length) {
            throw new oS(var11.f.length, var7.length);
         }

         for (int var5 = 0; var5 < var7.length; var5++) {
            if (var11.f[var5] > var9[var5] - var8[var5]) {
               throw new po(var11.f[var5], 0, var9[var5] - var8[var5]);
            }
         }
      }
   }

   private void q() {
      double[] var1 = this.e();
      double[] var2 = this.f();
      double[] var3 = this.g();
      if (this.f != null) {
         if (this.f.length != var1.length) {
            throw new oS(this.f.length, var1.length);
         }

         for (int var4 = 0; var4 < var1.length; var4++) {
            if (this.f[var4] > var3[var4] - var2[var4]) {
               throw new po(this.f[var4], 0, var3[var4] - var2[var4]);
            }
         }
      }
   }

   private void a(double[] var1) {
      if (this.c <= 0) {
         throw new pk(this.c);
      }

      double[][] var2 = new double[var1.length][1];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3][0] = this.f[var3];
      }

      sy var13 = new sy(var2, false);
      this.t = f(var13);
      this.l = 1000.0 * f(var13);
      this.m = 1.0E-11 * f(var13);
      this.n = 1.0E-12;
      this.o = 1.0E-13;
      this.p = this.c / 2;
      this.q = BY.k(this.p + 0.5);
      this.r = a(a(1.0, this.p, 1.0)).b(-1.0).a(this.q);
      double var4 = 0.0;
      double var6 = 0.0;

      for (int var11 = 0; var11 < this.p; var11++) {
         double var9 = this.r.b(var11, 0);
         var4 += var9;
         var6 += var9 * var9;
      }

      this.r = this.r.b(1.0 / var4);
      this.s = var4 * var4 / var6;
      this.u = (4.0 + this.s / this.g) / (this.g + 4 + 2.0 * this.s / this.g);
      this.v = (this.s + 2.0) / (this.g + this.s + 3.0);
      this.w = (1.0 + 2.0 * BY.e(0.0, BY.a((this.s - 1.0) / (this.g + 1)) - 1.0)) * BY.e(0.3, 1.0 - this.g / (1.0E-6 + this.j)) + this.v;
      this.x = 2.0 / ((this.g + 1.3) * (this.g + 1.3) + this.s);
      this.y = BY.d(1.0 - this.x, 2.0 * (this.s - 2.0 + 1.0 / this.s) / ((this.g + 2) * (this.g + 2) + this.s));
      this.A = BY.d(1.0, this.x * (this.g + 1.5) / 3.0);
      this.B = BY.d(1.0 - this.x, this.y * (this.g + 1.5) / 3.0);
      this.z = BY.a((double)this.g) * (1.0 - 1.0 / (4.0 * this.g) + 1.0 / (21.0 * this.g * this.g));
      this.C = td.c(var1);
      this.J = var13.b(1.0 / this.t);
      this.L = c(this.J);
      this.D = c(this.g, 1);
      this.E = c(this.g, 1);
      this.F = this.E.c();
      this.G = b(this.g, this.g);
      this.H = a(this.g, 1);
      this.I = b(this.G, a(this.J.d(), this.g, 1));
      this.K = this.G.c(e(c(this.H)).c(this.G.d()));
      this.O = 10 + (int)((double)(30 * this.g) / this.c);
      this.N = new double[this.O];

      for (int var12 = 0; var12 < this.O; var12++) {
         this.N[var12] = Double.MAX_VALUE;
      }
   }

   private boolean a(tq var1, tq var2) {
      this.E = this.E.b(1.0 - this.v).a(this.G.c(var1).b(BY.a(this.v * (2.0 - this.v) * this.s)));
      this.F = this.E.c();
      boolean var3 = this.F / BY.a(1.0 - BY.a(1.0 - this.v, 2 * this.M)) / this.z < 1.4 + 2.0 / (this.g + 1.0);
      this.D = this.D.b(1.0 - this.u);
      if (var3) {
         this.D = this.D.a(this.C.b(var2).b(BY.a(this.u * (2.0 - this.u) * this.s) / this.t));
      }

      return var3;
   }

   private void a(boolean var1, tq var2) {
      double var3 = (var1 ? 0.0 : this.A * this.u * (2.0 - this.u)) + (1.0 - this.A - this.B);
      this.L = this.L.b(var3).a(c(this.D).b(this.A)).a(b(this.L, c(var2).c(this.r)).b(this.B));
      this.J = b(this.L);
      if (this.h > 1 && this.M > this.h) {
         this.h = 0;
         this.G = b(this.g, this.g);
         this.I = e(this.J);
         this.K = e(this.L);
      }
   }

   private void a(boolean var1, tq var2, tq var3, int[] var4, tq var5) {
      double var6 = 0.0;
      if (this.x + this.y > 0.0) {
         var2 = var2.b(a(var5, 1, this.p)).b(1.0 / this.t);
         var5 = this.D.c(this.D.d()).b(this.x);
         double var10 = (var1 ? 0.0 : this.x * this.u * (2.0 - this.u)) + (1.0 - this.x - this.y);
         if (this.d) {
            var6 = (1.0 - this.y) * 0.25 * this.s / (BY.a(this.g + 2, 1.5) + 2.0 * this.s);
            int[] var23 = b(var4);
            sy var24;
            sy var29;
            var4 = c((var29 = b(d(c(var24 = a(var3, Ch.a(var23, this.p)))))).f(0));
            sy var8 = a(var29, var4);
            int[] var9 = b(var4);
            var3 = c(a(var29, var9), var8);
            var4 = a(var4);
            var3 = a(var3, var4);
            double var21 = 0.33999999999999997 / c(var3).c(this.r).b(0, 0);
            if (var6 > var21) {
               var6 = var21;
            }

            sy var25 = b(var24, a(var3, this.g, 1));
            tq var26;
            tq var27 = (var26 = this.I.c(var25)).c(e(this.r)).c(var26.d());
            var10 += var6 * 0.5;
            this.K = this.K.b(var10).a(var5).a(var2.b(this.y + var6 * 0.5).c(b(a(this.r, 1, this.g), var2.d()))).b(var27.b(var6));
         } else {
            this.K = this.K.b(var10).a(var5).a(var2.b(this.y).c(b(a(this.r, 1, this.g), var2.d())));
         }
      }

      this.a(var6);
   }

   private void a(double var1) {
      if (this.x + this.y + var1 > 0.0 && this.M % 1.0 / (this.x + this.y + var1) / this.g / 10.0 < 1.0) {
         this.K = a(this.K, 0).a(a(this.K, 1).d());
         sN var6 = new sN(this.K);
         this.G = var6.a();
         this.H = var6.b();
         this.J = e(this.H);
         if (g(this.J) <= 0.0) {
            for (int var4 = 0; var4 < this.g; var4++) {
               if (this.J.b(var4, 0) < 0.0) {
                  this.J.c(var4, 0, 0.0);
               }
            }

            double var7 = f(this.J) / 1.0E14;
            this.K = this.K.a(b(this.g, this.g).b(var7));
            this.J = this.J.a(a(this.g, 1).b(var7));
         }

         if (f(this.J) > 1.0E14 * g(this.J)) {
            double var8 = f(this.J) / 1.0E14 - g(this.J);
            this.K = this.K.a(b(this.g, this.g).b(var8));
            this.J = this.J.a(a(this.g, 1).b(var8));
         }

         this.L = e(this.K);
         this.J = b(this.J);
         this.I = b(this.G, a(this.J.d(), this.g, 1));
      }
   }

   private static void a(double[] var0, double var1) {
      for (int var3 = var0.length - 1; var3 > 0; var3--) {
         var0[var3] = var0[var3 - 1];
      }

      var0[0] = var1;
   }

   private static int[] c(double[] var0) {
      xw.a[] var1 = new xw.a[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = new xw.a(var0[var2], var2);
      }

      Arrays.sort(var1);
      int[] var4 = new int[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var4[var3] = var1[var3].b;
      }

      return var4;
   }

   private static double a(xw.e[] var0) {
      double var1 = Double.NEGATIVE_INFINITY;
      double var3 = Double.MAX_VALUE;
      xw.e[] var9;
      int var5 = (var9 = var0).length;

      for (int var6 = 0; var6 < var5; var6++) {
         xw.e var7;
         if ((var7 = var9[var6]).a > var1) {
            var1 = var7.a;
         }

         if (var7.a < var3) {
            var3 = var7.a;
         }
      }

      return var1 - var3;
   }

   private static sy a(tq var0) {
      double[][] var1 = new double[var0.f()][var0.g()];

      for (int var2 = 0; var2 < var0.f(); var2++) {
         for (int var3 = 0; var3 < var0.g(); var3++) {
            var1[var2][var3] = BY.k(var0.b(var2, var3));
         }
      }

      return new sy(var1, false);
   }

   private static sy b(tq var0) {
      double[][] var1 = new double[var0.f()][var0.g()];

      for (int var2 = 0; var2 < var0.f(); var2++) {
         for (int var3 = 0; var3 < var0.g(); var3++) {
            var1[var2][var3] = BY.a(var0.b(var2, var3));
         }
      }

      return new sy(var1, false);
   }

   private static sy c(tq var0) {
      double[][] var1 = new double[var0.f()][var0.g()];

      for (int var2 = 0; var2 < var0.f(); var2++) {
         for (int var3 = 0; var3 < var0.g(); var3++) {
            double var4 = var0.b(var2, var3);
            var1[var2][var3] = var4 * var4;
         }
      }

      return new sy(var1, false);
   }

   private static sy b(tq var0, tq var1) {
      double[][] var2 = new double[var0.f()][var0.g()];

      for (int var3 = 0; var3 < var0.f(); var3++) {
         for (int var4 = 0; var4 < var0.g(); var4++) {
            var2[var3][var4] = var0.b(var3, var4) * var1.b(var3, var4);
         }
      }

      return new sy(var2, false);
   }

   private static sy c(tq var0, tq var1) {
      double[][] var2 = new double[var0.f()][var0.g()];

      for (int var3 = 0; var3 < var0.f(); var3++) {
         for (int var4 = 0; var4 < var0.g(); var4++) {
            var2[var3][var4] = var0.b(var3, var4) / var1.b(var3, var4);
         }
      }

      return new sy(var2, false);
   }

   private static sy a(tq var0, int[] var1) {
      double[][] var2 = new double[var0.f()][var1.length];

      for (int var3 = 0; var3 < var0.f(); var3++) {
         for (int var4 = 0; var4 < var1.length; var4++) {
            var2[var3][var4] = var0.b(var3, var1[var4]);
         }
      }

      return new sy(var2, false);
   }

   private static sy a(tq var0, int var1) {
      double[][] var2 = new double[var0.f()][var0.g()];

      for (int var3 = 0; var3 < var0.f(); var3++) {
         for (int var4 = 0; var4 < var0.g(); var4++) {
            var2[var3][var4] = var3 <= var4 - var1 ? var0.b(var3, var4) : 0.0;
         }
      }

      return new sy(var2, false);
   }

   private static sy d(tq var0) {
      double[][] var1 = new double[1][var0.g()];

      for (int var2 = 0; var2 < var0.g(); var2++) {
         double var3 = 0.0;

         for (int var5 = 0; var5 < var0.f(); var5++) {
            var3 += var0.b(var5, var2);
         }

         var1[0][var2] = var3;
      }

      return new sy(var1, false);
   }

   private static sy e(tq var0) {
      if (var0.g() == 1) {
         double[][] var3 = new double[var0.f()][var0.f()];

         for (int var4 = 0; var4 < var0.f(); var4++) {
            var3[var4][var4] = var0.b(var4, 0);
         }

         return new sy(var3, false);
      } else {
         double[][] var1 = new double[var0.f()][1];

         for (int var2 = 0; var2 < var0.g(); var2++) {
            var1[var2][0] = var0.b(var2, var2);
         }

         return new sy(var1, false);
      }
   }

   private static void a(tq var0, int var1, tq var2, int var3) {
      for (int var4 = 0; var4 < var0.f(); var4++) {
         var2.c(var4, var3, var0.b(var4, 0));
      }
   }

   private static sy a(int var0, int var1) {
      double[][] var3 = new double[var0][1];

      for (int var2 = 0; var2 < var0; var2++) {
         Arrays.fill(var3[var2], 1.0);
      }

      return new sy(var3, false);
   }

   private static sy b(int var0, int var1) {
      double[][] var2 = new double[var0][var1];

      for (int var3 = 0; var3 < var0; var3++) {
         if (var3 < var1) {
            var2[var3][var3] = 1.0;
         }
      }

      return new sy(var2, false);
   }

   private static sy c(int var0, int var1) {
      return new sy(var0, var1);
   }

   private static sy a(tq var0, int var1, int var2) {
      int var3 = var0.f();
      int var4 = var0.g();
      double[][] var5 = new double[var1 * var3][var2 * var4];

      for (int var6 = 0; var6 < var1 * var3; var6++) {
         for (int var7 = 0; var7 < var2 * var4; var7++) {
            var5[var6][var7] = var0.b(var6 % var3, var7 % var4);
         }
      }

      return new sy(var5, false);
   }

   private static sy a(double var0, double var2, double var4) {
      int var10;
      double[][] var1 = new double[var10 = (int)(var2 - 1.0 + 1.0)][1];
      double var8 = 1.0;

      for (int var11 = 0; var11 < var10; var11++) {
         var1[var11][0] = var8++;
      }

      return new sy(var1, false);
   }

   private static double f(tq var0) {
      double var1 = -Double.MAX_VALUE;

      for (int var3 = 0; var3 < var0.f(); var3++) {
         for (int var4 = 0; var4 < var0.g(); var4++) {
            double var5 = var0.b(var3, var4);
            if (var1 < var5) {
               var1 = var5;
            }
         }
      }

      return var1;
   }

   private static double g(tq var0) {
      double var1 = Double.MAX_VALUE;

      for (int var3 = 0; var3 < var0.f(); var3++) {
         for (int var4 = 0; var4 < var0.g(); var4++) {
            double var5 = var0.b(var3, var4);
            if (var1 > var5) {
               var1 = var5;
            }
         }
      }

      return var1;
   }

   private static double d(double[] var0) {
      double var1 = -Double.MAX_VALUE;

      for (int var3 = 0; var3 < var0.length; var3++) {
         if (var1 < var0[var3]) {
            var1 = var0[var3];
         }
      }

      return var1;
   }

   private static double e(double[] var0) {
      double var1 = Double.MAX_VALUE;

      for (int var3 = 0; var3 < var0.length; var3++) {
         if (var1 > var0[var3]) {
            var1 = var0[var3];
         }
      }

      return var1;
   }

   private static int[] a(int[] var0) {
      int[] var1 = new int[var0.length];
      int var2 = 0;

      while (var2 < var0.length) {
         var1[var0[var2]] = var2++;
      }

      return var1;
   }

   private static int[] b(int[] var0) {
      int[] var1 = new int[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = var0[var0.length - var2 - 1];
      }

      return var1;
   }

   private double[] a(int var1) {
      double[] var2 = new double[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = this.P.nextGaussian();
      }

      return var2;
   }

   private sy d(int var1, int var2) {
      double[][] var3 = new double[var1][var2];

      for (int var4 = 0; var4 < var1; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            var3[var4][var5] = this.P.nextGaussian();
         }
      }

      return new sy(var3, false);
   }

   // $VF: synthetic method
   @Override
   public final Object a(wO[] var1) {
      wO[] var2 = var1;
      return super.c(var2);
   }

   // $VF: synthetic method
   @Override
   public final Object c() {
      return this.p();
   }

   // $VF: synthetic method
   static boolean a(xw var0) {
      return var0.b;
   }

   static class a implements Comparable<xw.a> {
      private final double a;
      private final int b;

      a(double var1, int var3) {
         this.a = var1;
         this.b = var3;
      }

      private int b(xw.a var1) {
         return Double.compare(this.a, var1.a);
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true;
         } else {
            return other instanceof xw.a ? Double.compare(this.a, ((xw.a)other).a) == 0 : false;
         }
      }

      @Override
      public final int hashCode() {
         long var1 = Double.doubleToLongBits(this.a);
         return (int)(1438542L ^ var1 >>> 32 ^ var1);
      }

      // $VF: synthetic method
      @Override
      public final int compareTo(Object var1) {
         xw.a var2 = (xw.a)var1;
         return Double.compare(this.a, var2.a);
      }
   }

   class b {
      private final boolean b = true;

      public final xw.e a(double[] var1) {
         double var4 = 0.0;
         double var2;
         if (this.b) {
            double[] var9 = this.c(var1);
            var2 = xw.this.b(var9);
            var4 = this.a(var1, var9);
         } else {
            var2 = xw.this.b(var1);
         }

         xw var6 = xw.this;
         var2 = xw.this.b ? var2 : -var2;
         xw var7 = xw.this;
         var4 = xw.this.b ? var4 : -var4;
         return new xw.e(var2, var4);
      }

      public final boolean b(double[] var1) {
         double[] var2 = xw.this.f();
         double[] var3 = xw.this.g();

         for (int var4 = 0; var4 < var1.length; var4++) {
            if (var1[var4] < var2[var4]) {
               return false;
            }

            if (var1[var4] > var3[var4]) {
               return false;
            }
         }

         return true;
      }

      double[] c(double[] var1) {
         double[] var2 = xw.this.f();
         double[] var3 = xw.this.g();
         double[] var4 = new double[var1.length];

         for (int var5 = 0; var5 < var1.length; var5++) {
            if (var1[var5] < var2[var5]) {
               var4[var5] = var2[var5];
            } else if (var1[var5] > var3[var5]) {
               var4[var5] = var3[var5];
            } else {
               var4[var5] = var1[var5];
            }
         }

         return var4;
      }

      private double a(double[] var1, double[] var2) {
         double var3 = 0.0;

         for (int var5 = 0; var5 < var1.length; var5++) {
            double var6 = BY.w(var1[var5] - var2[var5]);
            var3 += var6;
         }

         xw var8 = xw.this;
         return xw.this.b ? var3 : -var3;
      }

      // $VF: synthetic method
      static double[] a(xw.b var0, double[] var1) {
         return var0.c(var1);
      }
   }

   public static class c implements wO {
      final int a;

      private c(int var1) {
         if (var1 <= 0) {
            throw new pk(var1);
         }

         this.a = var1;
      }

      public final int a() {
         return this.a;
      }
   }

   public static class d implements wO {
      private final double[] a;

      private d(double[] var1) {
         for (int var2 = 0; var2 < var1.length; var2++) {
            if (var1[var2] < 0.0) {
               throw new pj(var1[var2]);
            }
         }

         this.a = (double[])var1.clone();
      }

      public final double[] a() {
         return (double[])this.a.clone();
      }
   }

   static class e {
      double a;
      double b;

      e(double var1, double var3) {
         this.a = var1;
         this.b = var3;
      }

      // $VF: synthetic method
      static double a(xw.e var0) {
         return var0.a;
      }

      // $VF: synthetic method
      static double b(xw.e var0) {
         return var0.b;
      }
   }
}
