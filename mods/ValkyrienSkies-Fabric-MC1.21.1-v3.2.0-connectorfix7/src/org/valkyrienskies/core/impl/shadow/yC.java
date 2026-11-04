package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
public final class yC extends yA<kW> implements yn {
   private static int b;
   private static double c;
   private static boolean d;
   private static int e;
   private static int f;
   private static zz g = new zz();
   private int h;
   private boolean i;
   private int j;
   private double[] k;
   private int l;
   private int m = 0;
   private boolean n = true;
   private boolean o = false;
   private int p;
   private double q;
   private double r;
   private double s;
   private double t;
   private double u;
   private int v;
   private double w;
   private tq x;
   private double y;
   private double z;
   private double A;
   private double B;
   private double C;
   private double D;
   private double E;
   private double F;
   private double G;
   private double H;
   private tq I;
   private tq J;
   private tq K;
   private double L;
   private tq M;
   private tq N;
   private tq O;
   private tq P;
   private tq Q;
   private tq R;
   private int S;
   private double[] T;
   private int U;
   private zF V;
   private List<Double> W = new ArrayList<>();
   private List<tq> X = new ArrayList<>();
   private List<Double> Y = new ArrayList<>();
   private List<tq> Z = new ArrayList<>();

   @Deprecated
   public yC() {
      this(0);
   }

   @Deprecated
   private yC(int var1) {
      this(0, null, 30000, 0.0, true, 0, 0, g, false, null);
   }

   @Deprecated
   private yC(int var1, double[] var2) {
      this(var1, var2, 30000, 0.0, true, 0, 0, g, false);
   }

   @Deprecated
   private yC(int var1, double[] var2, int var3, double var4, boolean var6, int var7, int var8, zF var9, boolean var10) {
      this(var1, var2, 30000, 0.0, true, 0, 0, var9, false, new yt());
   }

   @Deprecated
   private yC(int var1, double[] var2, int var3, double var4, boolean var6, int var7, int var8, zF var9, boolean var10, ya<yp> var11) {
      super(var11);
      this.h = var1;
      this.k = var2 == null ? null : (double[])var2.clone();
      this.p = var3;
      this.q = var4;
      this.i = var6;
      this.m = var7;
      this.j = var8;
      this.V = var9;
      this.o = var10;
   }

   private yC(int var1, double var2, boolean var4, int var5, int var6, zF var7, boolean var8, ya<yp> var9) {
      super(var9);
      this.p = var1;
      this.q = var2;
      this.i = var4;
      this.m = var5;
      this.j = var6;
      this.V = var7;
      this.o = var8;
   }

   private List<Double> i() {
      return this.W;
   }

   private List<tq> j() {
      return this.X;
   }

   private List<Double> k() {
      return this.Y;
   }

   private List<tq> l() {
      return this.Z;
   }

   @Override
   protected final yp a(int var1, kW var2, yf var3, yo... var4) {
      yo[] var6 = var4;
      yC var5 = this;
      yo[] var10;
      int var7 = (var10 = var6).length;

      for (int var8 = 0; var8 < var7; var8++) {
         yo var9;
         if ((var9 = var10[var8]) instanceof yC.d) {
            var5.k = ((yC.d)var9).a();
         } else if (var9 instanceof yC.c) {
            yC.c var11;
            var5.h = (var11 = (yC.c)var9).a;
         }
      }

      return super.a(var1, var2, var3, var4);
   }

   @Override
   protected final yp d() {
      yC var12 = this;
      double[] var28 = this.f();
      double[] var29 = var12.g();
      double[] var30 = var12.h();
      if (var12.k != null) {
         if (var12.k.length != var28.length) {
            throw new oS(var12.k.length, var28.length);
         }

         for (int var31 = 0; var31 < var28.length; var31++) {
            if (var12.k[var31] < 0.0) {
               throw new pj(var12.k[var31]);
            }

            if (var12.k[var31] > var30[var31] - var29[var31]) {
               throw new po(var12.k[var31], 0, var30[var31] - var29[var31]);
            }
         }
      }

      this.n = this.e().equals(yf.MINIMIZE);
      yC.b var1 = new yC.b();
      double[] var2 = this.f();
      this.l = var2.length;
      var28 = var2;
      var12 = this;
      if (this.h <= 0) {
         var12.h = 4 + (int)(3.0 * BY.k(var12.l));
      }

      double[][] var115 = new double[var28.length][1];

      for (int var123 = 0; var123 < var28.length; var123++) {
         var115[var123][0] = var12.k == null ? 0.3 : var12.k[var123];
      }

      sy var124 = new sy(var115, false);
      var12.z = f(var124);
      var12.r = 1000.0 * f(var124);
      var12.s = 1.0E-11 * f(var124);
      var12.t = 1.0E-12;
      var12.u = 1.0E-13;
      var12.v = var12.h / 2;
      var12.w = BY.k(var12.v + 0.5);
      double var10002 = var12.v;
      double var42 = 1.0;
      double var40 = var10002;
      double var38 = 1.0;
      int var7;
      double[][] var10 = new double[var7 = (int)(var40 - 1.0 + 1.0)][1];
      double var46 = 1.0;

      for (int var13 = 0; var13 < var7; var13++) {
         var10[var13][0] = var46++;
      }

      sy var143;
      double[][] var39 = new double[(var143 = new sy(var10, false)).f()][var143.g()];

      for (int var144 = 0; var144 < var143.f(); var144++) {
         for (int var41 = 0; var41 < var143.g(); var41++) {
            var39[var144][var41] = BY.k(var143.b(var144, var41));
         }
      }

      var12.x = new sy(var39, false).b(-1.0).a(var12.w);
      double var134 = 0.0;
      double var33 = 0.0;

      for (int var6 = 0; var6 < var12.v; var6++) {
         double var36 = var12.x.b(var6, 0);
         var134 += var36;
         var33 += var36 * var36;
      }

      var12.x = var12.x.b(1.0 / var134);
      var12.y = var134 * var134 / var33;
      var12.A = (4.0 + var12.y / var12.l) / (var12.l + 4 + 2.0 * var12.y / var12.l);
      var12.B = (var12.y + 2.0) / (var12.l + var12.y + 3.0);
      var12.C = (1.0 + 2.0 * BY.e(0.0, BY.a((var12.y - 1.0) / (var12.l + 1)) - 1.0)) * BY.e(0.3, 1.0 - var12.l / (1.0E-6 + var12.p)) + var12.B;
      var12.D = 2.0 / ((var12.l + 1.3) * (var12.l + 1.3) + var12.y);
      var12.E = BY.d(1.0 - var12.D, 2.0 * (var12.y - 2.0 + 1.0 / var12.y) / ((var12.l + 2) * (var12.l + 2) + var12.y));
      var12.G = BY.d(1.0, var12.D * (var12.l + 1.5) / 3.0);
      var12.H = BY.d(1.0 - var12.D, var12.E * (var12.l + 1.5) / 3.0);
      var12.F = BY.a((double)var12.l) * (1.0 - 1.0 / (4.0 * var12.l) + 1.0 / (21.0 * var12.l * var12.l));
      var12.I = td.c(var28);
      var12.P = var124.b(1.0 / var12.z);
      var12.R = c(var12.P);
      var12.J = c(var12.l, 1);
      var12.K = c(var12.l, 1);
      var12.L = var12.K.c();
      var12.M = b(var12.l, var12.l);
      var12.N = a(var12.l, 1);
      var12.O = b(var12.M, a(var12.P.d(), var12.l, 1));
      var12.Q = var12.M.c(e(c(var12.N)).c(var12.M.d()));
      var12.U = 10 + (int)((double)(30 * var12.l) / var12.h);
      var12.T = new double[var12.U];

      for (int var64 = 0; var64 < var12.U; var64++) {
         var12.T[var64] = Double.MAX_VALUE;
      }

      this.S = 0;
      double var3 = var1.a(var2);
      a(this.T, var3);
      yp var63 = new yp(this.f(), this.n ? var3 : -var3);
      yp var5 = null;

      for (this.S = 1; this.S <= this.p; this.S++) {
         int var116 = this.h;
         int var104 = this.l;
         var12 = this;
         double[][] var125 = new double[var104][var116];

         for (int var135 = 0; var135 < var104; var135++) {
            for (int var32 = 0; var32 < var116; var32++) {
               var125[var135][var32] = var12.V.nextGaussian();
            }
         }

         sy var65 = new sy(var125, false);
         sy var69 = c(this.l, this.h);
         double[] var8 = new double[this.h];

         for (int var9 = 0; var9 < this.h; var9++) {
            tq var77 = null;

            for (int var11 = 0; var11 < this.j + 1; var11++) {
               if (this.m <= 0) {
                  var77 = this.I.a(this.O.c(var65.c(var9)).b(this.z));
               } else {
                  var77 = this.I.a(b(this.P, var65.c(var9)).b(this.z));
               }

               if (var11 >= this.j) {
                  break;
               }

               var28 = var77.g(0);
               yC.b var85 = var1;
               var29 = var1.b.g();
               var30 = var85.b.h();
               int var136 = 0;

               boolean var10000;
               while (true) {
                  if (var136 >= var28.length) {
                     var10000 = true;
                     break;
                  }

                  if (var28[var136] < var29[var136]) {
                     var10000 = false;
                     break;
                  }

                  if (var28[var136] > var30[var136]) {
                     var10000 = false;
                     break;
                  }

                  var136++;
               }

               if (var10000) {
                  break;
               }

               int var106 = this.l;
               var12 = this;
               var29 = new double[var106];

               for (int var127 = 0; var127 < var106; var127++) {
                  var29[var127] = var12.V.nextGaussian();
               }

               var65.b(var9, var29);
            }

            int var128 = var9;
            sy var119 = var69;
            boolean var107 = false;
            tq var87 = var77;

            for (int var137 = 0; var137 < var87.f(); var137++) {
               var119.c(var137, var128, var87.b(var137, 0));
            }

            try {
               var8[var9] = var1.a(var69.g(var9));
            } catch (pp var62) {
               return var63;
            }
         }

         int[] var76 = c(var8);
         tq var78 = this.I;
         sy var82 = a(var69, Ch.a(var76, this.v));
         this.I = var82.c(this.x);
         sy var70;
         tq var88 = (var70 = a(var65, Ch.a(var76, this.v))).c(this.x);
         tq var120 = var78;
         tq var108 = var88;
         var12 = this;
         this.K = var12.K.b(1.0 - var12.B).a(var12.M.c(var108).b(BY.a(var12.B * (2.0 - var12.B) * var12.y)));
         var12.L = var12.K.c();
         boolean var129 = var12.L / BY.a(1.0 - BY.a(1.0 - var12.B, 2 * var12.S)) / var12.F < 1.4 + 2.0 / (var12.l + 1.0);
         var12.J = var12.J.b(1.0 - var12.A);
         if (var129) {
            var12.J = var12.J.a(var12.I.b(var120).b(BY.a(var12.A * (2.0 - var12.A) * var12.y) / var12.z));
         }

         boolean var90 = var129;
         if (this.m <= 0) {
            tq var139 = var78;
            int[] var138 = var76;
            sy var130 = var65;
            sy var121 = var82;
            boolean var109 = var90;
            var12 = this;
            var33 = 0.0;
            if (var12.D + var12.E > 0.0) {
               tq var66 = var121.b(a(var139, 1, var12.v)).b(1.0 / var12.z);
               tq var141 = var12.J.c(var12.J.d()).b(var12.D);
               double var37 = (var109 ? 0.0 : var12.D * var12.A * (2.0 - var12.A)) + (1.0 - var12.D - var12.E);
               if (!var12.i) {
                  var12.Q = var12.Q.b(var37).a(var141).a(var66.b(var12.E).c(b(a(var12.x, 1, var12.l), var66.d())));
               } else {
                  var33 = (1.0 - var12.E) * 0.25 * var12.y / (BY.a(var12.l + 2, 1.5) + 2.0 * var12.y);
                  int[] var43 = b(var138);
                  sy var71;
                  sy var95 = c(var71 = a(var130, Ch.a(var43, var12.v)));
                  double[][] var57 = new double[1][var95.g()];

                  for (int var58 = 0; var58 < var95.g(); var58++) {
                     double var59 = 0.0;

                     for (int var61 = 0; var61 < var95.f(); var61++) {
                        var59 += var95.b(var61, var58);
                     }

                     var57[0][var58] = var59;
                  }

                  sy var79;
                  int[] var145 = c((var79 = b(new sy(var57, false))).f(0));
                  sy var47 = a(var79, var145);
                  int[] var96 = b(var145);
                  sy var156 = a(var79, var96);
                  sy var146 = var47;
                  var95 = var156;
                  double[][] var149 = new double[var156.f()][var95.g()];

                  for (int var151 = 0; var151 < var95.f(); var151++) {
                     for (int var60 = 0; var60 < var95.g(); var60++) {
                        var149[var151][var60] = var95.b(var151, var60) / var146.b(var151, var60);
                     }
                  }

                  sy var80 = new sy(var149, false);
                  int[] var98 = var145;
                  int[] var147 = new int[var145.length];
                  int var150 = 0;

                  while (var150 < var98.length) {
                     var147[var98[var150]] = var150++;
                  }

                  int[] var99 = var147;
                  sy var81 = a(var80, var99);
                  double var52 = 0.33999999999999997 / c(var81).c(var12.x).b(0, 0);
                  if (var33 > var52) {
                     var33 = var52;
                  }

                  sy var72 = b(var71, a(var81, var12.l, 1));
                  tq var73;
                  tq var74 = (var73 = var12.O.c(var72)).c(e(var12.x)).c(var73.d());
                  var37 += var33 * 0.5;
                  var12.Q = var12.Q.b(var37).a(var141).a(var66.b(var12.E + var33 * 0.5).c(b(a(var12.x, 1, var12.l), var66.d()))).b(var74.b(var33));
               }
            }

            double var148 = var33;
            yC var100 = var12;
            if (var12.D + var100.E + var148 > 0.0 && var100.S % 1.0 / (var100.D + var100.E + var148) / var100.l / 10.0 < 1.0) {
               var100.Q = a(var100.Q, 0).a(a(var100.Q, 1).d());
               sN var152 = new sN(var100.Q);
               var100.M = var152.a();
               var100.N = var152.b();
               var100.P = e(var100.N);
               if (g(var100.P) <= 0.0) {
                  for (int var153 = 0; var153 < var100.l; var153++) {
                     if (var100.P.b(var153, 0) < 0.0) {
                        var100.P.c(var153, 0, 0.0);
                     }
                  }

                  double var154 = f(var100.P) / 1.0E14;
                  var100.Q = var100.Q.a(b(var100.l, var100.l).b(var154));
                  var100.P = var100.P.a(a(var100.l, 1).b(var154));
               }

               if (f(var100.P) > 1.0E14 * g(var100.P)) {
                  double var155 = f(var100.P) / 1.0E14 - g(var100.P);
                  var100.Q = var100.Q.a(b(var100.l, var100.l).b(var155));
                  var100.P = var100.P.a(a(var100.l, 1).b(var155));
               }

               var100.R = e(var100.Q);
               var100.P = b(var100.P);
               var100.O = b(var100.M, a(var100.P.d(), var100.l, 1));
            }
         } else {
            sy var122 = var70;
            boolean var110 = var90;
            var12 = this;
            double var131 = (var110 ? 0.0 : var12.G * var12.A * (2.0 - var12.A)) + (1.0 - var12.G - var12.H);
            var12.R = var12.R.b(var131).a(c(var12.J).b(var12.G)).a(b(var12.R, c(var122).c(var12.x)).b(var12.H));
            var12.P = b(var12.R);
            if (var12.m > 1 && var12.S > var12.m) {
               var12.m = 0;
               var12.M = b(var12.l, var12.l);
               var12.O = e(var12.P);
               var12.Q = e(var12.R);
            }
         }

         this.z = this.z * BY.i(BY.d(1.0, (this.L / this.F - 1.0) * this.B / this.C));
         double var16 = var8[var76[0]];
         double var18 = var8[var76[var76.length - 1]];
         if (var3 > var16) {
            var3 = var16;
            var5 = var63;
            var28 = var82.g(0);
            var63 = new yp(var1.c(var28), this.n ? var16 : -var16);
            if (this.c() != null && this.c().a(this.S, var63, var5)) {
               break;
            }
         }

         if (this.q != 0.0 && var16 < (this.n ? this.q : -this.q)) {
            break;
         }

         double[] var67 = b(this.R).g(0);
         double[] var75 = this.J.g(0);

         for (int var22 = 0; var22 < this.l && !(this.z * BY.e(BY.w(var75[var22]), var67[var22]) > this.s); var22++) {
            if (var22 >= this.l - 1) {
               return var63;
            }
         }

         for (int var101 = 0; var101 < this.l; var101++) {
            if (this.z * var67[var101] > this.r) {
               return var63;
            }
         }

         double[] var93 = this.T;
         double var112 = Double.MAX_VALUE;

         for (int var132 = 0; var132 < var93.length; var132++) {
            if (var112 > var93[var132]) {
               var112 = var93[var132];
            }
         }

         double var102 = var112;
         double[] var94 = this.T;
         double var113 = -Double.MAX_VALUE;

         for (int var133 = 0; var133 < var94.length; var133++) {
            if (var113 < var94[var133]) {
               var113 = var94[var133];
            }
         }

         double var24 = var113;
         if (this.S > 2 && BY.e(var24, var18) - BY.d(var102, var16) < this.t
            || this.S > this.T.length && var24 - var102 < this.u
            || f(this.P) / g(this.P) > 1.0E7) {
            break;
         }

         if (this.c() != null) {
            yp var68 = new yp(var82.g(0), this.n ? var16 : -var16);
            if (var5 != null && this.c().a(this.S, var68, var5)) {
               break;
            }

            var5 = var68;
         }

         if (var3 == var8[var76[(int)(0.1 + this.h / 4.0)]]) {
            this.z = this.z * BY.i(0.2 + this.B / this.C);
         }

         if (this.S > 2 && BY.e(var24, var16) - BY.d(var102, var16) == 0.0) {
            this.z = this.z * BY.i(0.2 + this.B / this.C);
         }

         a(this.T, var16);
         double var114 = var18 - var16;
         var1.a = var114;
         if (this.o) {
            this.W.add(this.z);
            this.Y.add(var16);
            this.X.add(this.I.d());
            this.Z.add(this.P.d().b(100000.0));
         }
      }

      return var63;
   }

   private void a(yo... var1) {
      yo[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         yo var4;
         if ((var4 = var5[var3]) instanceof yC.d) {
            this.k = ((yC.d)var4).a();
         } else if (var4 instanceof yC.c) {
            yC.c var6;
            this.h = (var6 = (yC.c)var4).a;
         }
      }
   }

   private void m() {
      double[] var1 = this.f();
      double[] var2 = this.g();
      double[] var3 = this.h();
      if (this.k != null) {
         if (this.k.length != var1.length) {
            throw new oS(this.k.length, var1.length);
         }

         for (int var4 = 0; var4 < var1.length; var4++) {
            if (this.k[var4] < 0.0) {
               throw new pj(this.k[var4]);
            }

            if (this.k[var4] > var3[var4] - var2[var4]) {
               throw new po(this.k[var4], 0, var3[var4] - var2[var4]);
            }
         }
      }
   }

   private void b(double[] var1) {
      if (this.h <= 0) {
         this.h = 4 + (int)(3.0 * BY.k(this.l));
      }

      double[][] var2 = new double[var1.length][1];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3][0] = this.k == null ? 0.3 : this.k[var3];
      }

      sy var13 = new sy(var2, false);
      this.z = f(var13);
      this.r = 1000.0 * f(var13);
      this.s = 1.0E-11 * f(var13);
      this.t = 1.0E-12;
      this.u = 1.0E-13;
      this.v = this.h / 2;
      this.w = BY.k(this.v + 0.5);
      this.x = a(a(1.0, this.v, 1.0)).b(-1.0).a(this.w);
      double var4 = 0.0;
      double var6 = 0.0;

      for (int var11 = 0; var11 < this.v; var11++) {
         double var9 = this.x.b(var11, 0);
         var4 += var9;
         var6 += var9 * var9;
      }

      this.x = this.x.b(1.0 / var4);
      this.y = var4 * var4 / var6;
      this.A = (4.0 + this.y / this.l) / (this.l + 4 + 2.0 * this.y / this.l);
      this.B = (this.y + 2.0) / (this.l + this.y + 3.0);
      this.C = (1.0 + 2.0 * BY.e(0.0, BY.a((this.y - 1.0) / (this.l + 1)) - 1.0)) * BY.e(0.3, 1.0 - this.l / (1.0E-6 + this.p)) + this.B;
      this.D = 2.0 / ((this.l + 1.3) * (this.l + 1.3) + this.y);
      this.E = BY.d(1.0 - this.D, 2.0 * (this.y - 2.0 + 1.0 / this.y) / ((this.l + 2) * (this.l + 2) + this.y));
      this.G = BY.d(1.0, this.D * (this.l + 1.5) / 3.0);
      this.H = BY.d(1.0 - this.D, this.E * (this.l + 1.5) / 3.0);
      this.F = BY.a((double)this.l) * (1.0 - 1.0 / (4.0 * this.l) + 1.0 / (21.0 * this.l * this.l));
      this.I = td.c(var1);
      this.P = var13.b(1.0 / this.z);
      this.R = c(this.P);
      this.J = c(this.l, 1);
      this.K = c(this.l, 1);
      this.L = this.K.c();
      this.M = b(this.l, this.l);
      this.N = a(this.l, 1);
      this.O = b(this.M, a(this.P.d(), this.l, 1));
      this.Q = this.M.c(e(c(this.N)).c(this.M.d()));
      this.U = 10 + (int)((double)(30 * this.l) / this.h);
      this.T = new double[this.U];

      for (int var12 = 0; var12 < this.U; var12++) {
         this.T[var12] = Double.MAX_VALUE;
      }
   }

   private boolean a(tq var1, tq var2) {
      this.K = this.K.b(1.0 - this.B).a(this.M.c(var1).b(BY.a(this.B * (2.0 - this.B) * this.y)));
      this.L = this.K.c();
      boolean var3 = this.L / BY.a(1.0 - BY.a(1.0 - this.B, 2 * this.S)) / this.F < 1.4 + 2.0 / (this.l + 1.0);
      this.J = this.J.b(1.0 - this.A);
      if (var3) {
         this.J = this.J.a(this.I.b(var2).b(BY.a(this.A * (2.0 - this.A) * this.y) / this.z));
      }

      return var3;
   }

   private void a(boolean var1, tq var2) {
      double var3 = (var1 ? 0.0 : this.G * this.A * (2.0 - this.A)) + (1.0 - this.G - this.H);
      this.R = this.R.b(var3).a(c(this.J).b(this.G)).a(b(this.R, c(var2).c(this.x)).b(this.H));
      this.P = b(this.R);
      if (this.m > 1 && this.S > this.m) {
         this.m = 0;
         this.M = b(this.l, this.l);
         this.O = e(this.P);
         this.Q = e(this.R);
      }
   }

   private void a(boolean var1, tq var2, tq var3, int[] var4, tq var5) {
      double var6 = 0.0;
      if (this.D + this.E > 0.0) {
         var2 = var2.b(a(var5, 1, this.v)).b(1.0 / this.z);
         var5 = this.J.c(this.J.d()).b(this.D);
         double var10 = (var1 ? 0.0 : this.D * this.A * (2.0 - this.A)) + (1.0 - this.D - this.E);
         if (this.i) {
            var6 = (1.0 - this.E) * 0.25 * this.y / (BY.a(this.l + 2, 1.5) + 2.0 * this.y);
            int[] var23 = b(var4);
            sy var24;
            sy var29;
            var4 = c((var29 = b(d(c(var24 = a(var3, Ch.a(var23, this.v)))))).f(0));
            sy var8 = a(var29, var4);
            int[] var9 = b(var4);
            var3 = c(a(var29, var9), var8);
            var4 = a(var4);
            var3 = a(var3, var4);
            double var21 = 0.33999999999999997 / c(var3).c(this.x).b(0, 0);
            if (var6 > var21) {
               var6 = var21;
            }

            sy var25 = b(var24, a(var3, this.l, 1));
            tq var26;
            tq var27 = (var26 = this.O.c(var25)).c(e(this.x)).c(var26.d());
            var10 += var6 * 0.5;
            this.Q = this.Q.b(var10).a(var5).a(var2.b(this.E + var6 * 0.5).c(b(a(this.x, 1, this.l), var2.d()))).b(var27.b(var6));
         } else {
            this.Q = this.Q.b(var10).a(var5).a(var2.b(this.E).c(b(a(this.x, 1, this.l), var2.d())));
         }
      }

      this.a(var6);
   }

   private void a(double var1) {
      if (this.D + this.E + var1 > 0.0 && this.S % 1.0 / (this.D + this.E + var1) / this.l / 10.0 < 1.0) {
         this.Q = a(this.Q, 0).a(a(this.Q, 1).d());
         sN var6 = new sN(this.Q);
         this.M = var6.a();
         this.N = var6.b();
         this.P = e(this.N);
         if (g(this.P) <= 0.0) {
            for (int var4 = 0; var4 < this.l; var4++) {
               if (this.P.b(var4, 0) < 0.0) {
                  this.P.c(var4, 0, 0.0);
               }
            }

            double var7 = f(this.P) / 1.0E14;
            this.Q = this.Q.a(b(this.l, this.l).b(var7));
            this.P = this.P.a(a(this.l, 1).b(var7));
         }

         if (f(this.P) > 1.0E14 * g(this.P)) {
            double var8 = f(this.P) / 1.0E14 - g(this.P);
            this.Q = this.Q.a(b(this.l, this.l).b(var8));
            this.P = this.P.a(a(this.l, 1).b(var8));
         }

         this.R = e(this.Q);
         this.P = b(this.P);
         this.O = b(this.M, a(this.P.d(), this.l, 1));
      }
   }

   private static void a(double[] var0, double var1) {
      for (int var3 = var0.length - 1; var3 > 0; var3--) {
         var0[var3] = var0[var3 - 1];
      }

      var0[0] = var1;
   }

   private static int[] c(double[] var0) {
      yC.a[] var1 = new yC.a[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = new yC.a(var0[var2], var2);
      }

      Arrays.sort(var1);
      int[] var4 = new int[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var4[var3] = var1[var3].b;
      }

      return var4;
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
         var2[var3] = this.V.nextGaussian();
      }

      return var2;
   }

   private sy d(int var1, int var2) {
      double[][] var3 = new double[var1][var2];

      for (int var4 = 0; var4 < var1; var4++) {
         for (int var5 = 0; var5 < var2; var5++) {
            var3[var4][var5] = this.V.nextGaussian();
         }
      }

      return new sy(var3, false);
   }

   static class a implements Comparable<yC.a> {
      private final double a;
      private final int b;

      a(double var1, int var3) {
         this.a = var1;
         this.b = var3;
      }

      private int b(yC.a var1) {
         return Double.compare(this.a, var1.a);
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true;
         } else {
            return other instanceof yC.a ? Double.compare(this.a, ((yC.a)other).a) == 0 : false;
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
         yC.a var2 = (yC.a)var1;
         return Double.compare(this.a, var2.a);
      }
   }

   class b {
      double a = 1.0;
      private final boolean c = true;

      public final double a(double[] var1) {
         double var15;
         if (this.c) {
            double[] var2 = this.c(var1);
            double var10000 = yC.this.a(var2);
            double[] var3 = var2;
            double[] var14 = var1;
            yC.b var13 = this;
            double var8 = 0.0;

            for (int var4 = 0; var4 < var14.length; var4++) {
               double var11 = BY.w(var14[var4] - var3[var4]);
               var8 += var11 * var13.a;
            }

            var15 = var10000 + (yC.this.n ? var8 : -var8);
         } else {
            var15 = yC.this.a(var1);
         }

         return yC.this.n ? var15 : -var15;
      }

      public final boolean b(double[] var1) {
         double[] var2 = yC.this.g();
         double[] var3 = yC.this.h();

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

      public final void a(double var1) {
         this.a = var1;
      }

      double[] c(double[] var1) {
         double[] var2 = yC.this.g();
         double[] var3 = yC.this.h();
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
            var3 += var6 * this.a;
         }

         return yC.this.n ? var3 : -var3;
      }

      // $VF: synthetic method
      static double[] a(yC.b var0, double[] var1) {
         return var0.c(var1);
      }
   }

   public static class c implements yo {
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

   public static class d implements yo {
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
}
