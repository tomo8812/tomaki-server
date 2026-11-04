package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public class lg implements Serializable, kO<lg> {
   private static final long c = 20120730L;
   public transient lf a;
   public final double[] b;

   public lg(lf var1) {
      this.a = var1;
      this.b = new double[var1.c()];
   }

   private lg(int var1, int var2) {
      this(lf.a(var1, var2));
   }

   public lg(int var1, int var2, double var3) {
      this(var1, var2);
      this.b[0] = var3;
   }

   public lg(int var1, int var2, int var3, double var4) {
      this(var1, 1, var4);
      if (var3 >= var1) {
         throw new pm(var3, var1, false);
      }

      this.b[lf.a(var3, 1).c()] = 1.0;
   }

   private lg(double var1, lg var3, double var4, lg var6) {
      this(var3.a);
      this.a.a(var6.a);
      boolean var14 = false;
      double[] var19 = this.b;
      boolean var15 = false;
      double[] var18 = var6.b;
      double var12 = var4;
      boolean var16 = false;
      double[] var2 = var3.b;
      double var8 = var1;
      lf var17 = this.a;

      for (int var5 = 0; var5 < var17.c(); var5++) {
         var19[var5 + 0] = Ch.a(var8, var2[var5 + 0], var12, var18[var5 + 0]);
      }
   }

   private lg(double var1, lg var3, double var4, lg var6, double var7, lg var9) {
      this(var3.a);
      this.a.a(var6.a);
      this.a.a(var9.a);
      boolean var21 = false;
      double[] var5 = this.b;
      boolean var22 = false;
      double[] var27 = var9.b;
      double var19 = var7;
      boolean var23 = false;
      double[] var26 = var6.b;
      double var15 = var4;
      boolean var24 = false;
      double[] var2 = var3.b;
      double var11 = var1;
      lf var25 = this.a;

      for (int var28 = 0; var28 < var25.c(); var28++) {
         var5[var28 + 0] = Ch.a(var11, var2[var28 + 0], var15, var26[var28 + 0], var19, var27[var28 + 0]);
      }
   }

   private lg(double var1, lg var3, double var4, lg var6, double var7, lg var9, double var10, lg var12) {
      this(var3.a);
      this.a.a(var6.a);
      this.a.a(var9.a);
      this.a.a(var12.a);
      boolean var28 = false;
      double[] var36 = this.b;
      boolean var29 = false;
      double[] var5 = var12.b;
      double var26 = var10;
      boolean var30 = false;
      double[] var35 = var9.b;
      double var22 = var7;
      boolean var31 = false;
      double[] var34 = var6.b;
      double var18 = var4;
      boolean var32 = false;
      double[] var2 = var3.b;
      double var14 = var1;
      lf var33 = this.a;

      for (int var37 = 0; var37 < var33.c(); var37++) {
         var36[var37 + 0] = Ch.a(var14, var2[var37 + 0], var18, var34[var37 + 0], var22, var35[var37 + 0], var26, var5[var37 + 0]);
      }
   }

   public lg(int var1, int var2, double... var3) {
      this(var1, var2);
      if (var3.length != this.b.length) {
         throw new oS(var3.length, this.b.length);
      }

      System.arraycopy(var3, 0, this.b, 0, this.b.length);
   }

   private lg(lg var1) {
      this.a = var1.a;
      this.b = (double[])var1.b.clone();
   }

   public final int C() {
      lf var1 = this.a;
      return this.a.a;
   }

   public final int D() {
      lf var1 = this.a;
      return this.a.b;
   }

   private lg l(double var1) {
      return new lg(this.C(), this.D(), var1);
   }

   @Override
   public final double d() {
      return this.b[0];
   }

   public final double E() {
      return this.b[0];
   }

   public final double a(int... var1) {
      return this.b[this.a.a(var1)];
   }

   public final double[] F() {
      return (double[])this.b.clone();
   }

   public final lg h(double var1) {
      lg var3;
      (var3 = new lg(this)).b[0] += var1;
      return var3;
   }

   public final lg a(lg var1) {
      this.a.a(var1.a);
      lg var2 = new lg(this);
      this.a.a(this.b, 0, var1.b, 0, var2.b, 0);
      return var2;
   }

   public final lg i(double var1) {
      return this.h(-var1);
   }

   private lg d(lg var1) {
      this.a.a(var1.a);
      lg var2 = new lg(this);
      this.a.b(this.b, 0, var1.b, 0, var2.b, 0);
      return var2;
   }

   private lg f(int var1) {
      return this.j(var1);
   }

   public final lg j(double var1) {
      lg var3 = new lg(this);

      for (int var4 = 0; var4 < var3.b.length; var4++) {
         var3.b[var4] = var3.b[var4] * var1;
      }

      return var3;
   }

   public final lg b(lg var1) {
      this.a.a(var1.a);
      lg var2 = new lg(this.a);
      this.a.c(this.b, 0, var1.b, 0, var2.b, 0);
      return var2;
   }

   private lg m(double var1) {
      lg var3 = new lg(this);

      for (int var4 = 0; var4 < var3.b.length; var4++) {
         var3.b[var4] = var3.b[var4] / var1;
      }

      return var3;
   }

   private lg e(lg var1) {
      this.a.a(var1.a);
      lg var2 = new lg(this.a);
      this.a.a(this.b, 0, var1.b, var2.b, 0);
      return var2;
   }

   private lg n(double var1) {
      lg var3;
      (var3 = new lg(this)).b[0] = BY.g(var3.b[0], var1);
      return var3;
   }

   private lg f(lg var1) {
      this.a.a(var1.a);
      lg var2 = new lg(this.a);
      boolean var14 = false;
      double[] var5 = var2.b;
      boolean var15 = false;
      double[] var4 = var1.b;
      boolean var16 = false;
      double[] var3 = this.b;
      lf var17 = this.a;
      double var10 = BY.g(var3[0], var4[0]);
      double var12 = BY.A((var3[0] - var10) / var4[0]);
      var5[0] = var10;

      for (int var6 = 1; var6 < var17.c(); var6++) {
         var5[var6 + 0] = var3[var6 + 0] - var12 * var4[var6 + 0];
      }

      return var2;
   }

   public final lg G() {
      lg var1 = new lg(this.a);

      for (int var2 = 0; var2 < var1.b.length; var2++) {
         var1.b[var2] = -this.b[var2];
      }

      return var1;
   }

   private lg ab() {
      return Double.doubleToLongBits(this.b[0]) < 0L ? this.G() : this;
   }

   private lg ac() {
      lf var1 = this.a;
      var1 = this.a;
      return new lg(this.a.a, this.a.b, BY.z(this.b[0]));
   }

   private lg ad() {
      lf var1 = this.a;
      var1 = this.a;
      return new lg(this.a.a, this.a.b, BY.y(this.b[0]));
   }

   private lg ae() {
      lf var1 = this.a;
      var1 = this.a;
      return new lg(this.a.a, this.a.b, BY.A(this.b[0]));
   }

   @Override
   public final long i() {
      return BY.B(this.b[0]);
   }

   private lg af() {
      lf var1 = this.a;
      var1 = this.a;
      return new lg(this.a.a, this.a.b, BY.h(this.b[0]));
   }

   private lg g(lg var1) {
      long var2 = Double.doubleToLongBits(this.b[0]);
      long var4 = Double.doubleToLongBits(var1.b[0]);
      return (var2 < 0L || var4 < 0L) && (var2 >= 0L || var4 >= 0L) ? this.G() : this;
   }

   private lg o(double var1) {
      long var3 = Double.doubleToLongBits(this.b[0]);
      long var5 = Double.doubleToLongBits(var1);
      return (var3 < 0L || var5 < 0L) && (var3 >= 0L || var5 >= 0L) ? this.G() : this;
   }

   private int ag() {
      return BY.C(this.b[0]);
   }

   private lg g(int var1) {
      lg var2 = new lg(this.a);

      for (int var3 = 0; var3 < var2.b.length; var3++) {
         var2.b[var3] = BY.b(this.b[var3], var1);
      }

      return var2;
   }

   private lg h(lg var1) {
      this.a.a(var1.a);
      if (Double.isInfinite(this.b[0]) || Double.isInfinite(var1.b[0])) {
         lf var7 = this.a;
         lf var8 = this.a;
         return new lg(this.a.a, this.a.a, Double.POSITIVE_INFINITY);
      }

      if (!Double.isNaN(this.b[0]) && !Double.isNaN(var1.b[0])) {
         int var2 = this.ag();
         int var3 = var1.ag();
         if (var2 > var3 + 27) {
            return this.ab();
         }

         if (var3 > var2 + 27) {
            return var1.ab();
         }

         var2 = (var2 + var3) / 2;
         lg var10 = this.g(-var2);
         var1 = var1.g(-var2);
         return var10.b(var10).a(var1.b(var1)).e(2).g(var2);
      } else {
         lf var4 = this.a;
         lf var5 = this.a;
         return new lg(this.a.a, this.a.a, Double.NaN);
      }
   }

   private static lg a(lg var0, lg var1) {
      return var0.h(var1);
   }

   public final lg a(double... var1) {
      if (var1.length != this.D() + 1) {
         throw new oS(var1.length, this.D() + 1);
      }

      lg var2 = new lg(this.a);
      this.a.b(this.b, 0, var1, var2.b, 0);
      return var2;
   }

   public final lg H() {
      lg var1 = new lg(this.a);
      this.a.a(this.b, 0, -1, var1.b, 0);
      return var1;
   }

   public final lg I() {
      return this.e(2);
   }

   public final lg J() {
      return this.e(3);
   }

   public final lg e(int var1) {
      lg var2 = new lg(this.a);
      this.a.b(this.b, 0, var1, var2.b, 0);
      return var2;
   }

   @Override
   public final kM<lg> c() {
      return new kM<lg>() {
         private lg d() {
            return new lg(lg.this.a.a, lg.this.a.b, 0.0);
         }

         private lg e() {
            return new lg(lg.this.a.a, lg.this.a.b, 1.0);
         }

         @Override
         public final Class<? extends kN<lg>> c() {
            return lg.class;
         }

         // $VF: synthetic method
         @Override
         public final kN b() {
            var var1 = this;
            return new lg(lg.this.a.a, lg.this.a.b, 1.0);
         }

         // $VF: synthetic method
         @Override
         public final kN a() {
            var var1 = this;
            return new lg(lg.this.a.a, lg.this.a.b, 0.0);
         }
      };
   }

   private static lg a(double var0, lg var2) {
      lg var3 = new lg(var2.a);
      boolean var14 = false;
      double[] var17 = var3.b;
      boolean var15 = false;
      double[] var1 = var2.b;
      double var5 = var0;
      lf var16 = var2.a;
      double[] var4 = new double[1 + var16.b];
      if (var5 == 0.0) {
         if (var1[0] == 0.0) {
            var4[0] = 1.0;
            double var12 = Double.POSITIVE_INFINITY;

            for (int var18 = 1; var18 < var4.length; var18++) {
               var12 = -var12;
               var4[var18] = var12;
            }
         } else if (var1[0] < 0.0) {
            Arrays.fill(var4, Double.NaN);
         }
      } else {
         var4[0] = BY.a(var5, var1[0]);
         double var20 = BY.k(var5);

         for (int var19 = 1; var19 < var4.length; var19++) {
            var4[var19] = var20 * var4[var19 - 1];
         }
      }

      var16.b(var1, 0, var4, var17, 0);
      return var3;
   }

   public final lg k(double var1) {
      lg var3 = new lg(this.a);
      boolean var16 = false;
      double[] var4 = var3.b;
      double var7 = var1;
      boolean var17 = false;
      double[] var2 = this.b;
      lf var18 = this.a;
      double[] var5 = new double[1 + var18.b];
      double var12 = BY.a(var2[0], var7 - var18.b);

      for (int var14 = var18.b; var14 > 0; var14--) {
         var5[var14] = var12;
         var12 *= var2[0];
      }

      var5[0] = var12;
      double var19 = var7;

      for (int var6 = 1; var6 <= var18.b; var6++) {
         var5[var6] *= var19;
         var19 *= var7 - var6;
      }

      var18.b(var2, 0, var5, var4, 0);
      return var3;
   }

   private lg h(int var1) {
      lg var2 = new lg(this.a);
      this.a.a(this.b, 0, var1, var2.b, 0);
      return var2;
   }

   private lg i(lg var1) {
      this.a.a(var1.a);
      lg var2 = new lg(this.a);
      boolean var7 = false;
      double[] var5 = var2.b;
      boolean var8 = false;
      double[] var4 = var1.b;
      boolean var9 = false;
      double[] var3 = this.b;
      lf var10 = this.a;
      double[] var6 = new double[this.a.c()];
      var10.c(var3, 0, var6, 0);
      var3 = new double[var10.c()];
      var10.c(var6, 0, var4, 0, var3, 0);
      var10.a(var3, 0, var5, 0);
      return var2;
   }

   public final lg K() {
      lg var1 = new lg(this.a);
      this.a.a(this.b, 0, var1.b, 0);
      return var1;
   }

   public final lg L() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var7 = this.a;
      double[] var5;
      (var5 = new double[1 + var7.b])[0] = BY.j(var3[0]);
      Arrays.fill(var5, 1, 1 + var7.b, BY.i(var3[0]));
      var7.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg M() {
      lg var1 = new lg(this.a);
      this.a.c(this.b, 0, var1.b, 0);
      return var1;
   }

   public final lg N() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var13 = this.a;
      double[] var5;
      (var5 = new double[1 + var13.b])[0] = BY.l(var3[0]);
      if (var13.b > 0) {
         double var8;
         double var10 = var8 = 1.0 / (1.0 + var3[0]);

         for (int var6 = 1; var6 <= var13.b; var6++) {
            var5[var6] = var10;
            var10 *= -var6 * var8;
         }
      }

      var13.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg O() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var13 = this.a;
      double[] var5;
      (var5 = new double[1 + var13.b])[0] = BY.m(var3[0]);
      if (var13.b > 0) {
         double var8;
         double var10 = (var8 = 1.0 / var3[0]) / BY.k(10.0);

         for (int var6 = 1; var6 <= var13.b; var6++) {
            var5[var6] = var10;
            var10 *= -var6 * var8;
         }
      }

      var13.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg P() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var8 = this.a;
      double[] var5;
      (var5 = new double[1 + var8.b])[0] = BY.o(var3[0]);
      if (var8.b > 0) {
         var5[1] = -BY.n(var3[0]);

         for (int var6 = 2; var6 <= var8.b; var6++) {
            var5[var6] = -var5[var6 - 2];
         }
      }

      var8.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg Q() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var8 = this.a;
      double[] var5;
      (var5 = new double[1 + var8.b])[0] = BY.n(var3[0]);
      if (var8.b > 0) {
         var5[1] = BY.o(var3[0]);

         for (int var6 = 2; var6 <= var8.b; var6++) {
            var5[var6] = -var5[var6 - 2];
         }
      }

      var8.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg R() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var17 = this.a;
      double[] var5 = new double[1 + var17.b];
      double var8 = BY.p(var3[0]);
      var5[0] = var8;
      if (var17.b > 0) {
         double[] var6;
         (var6 = new double[var17.b + 2])[1] = 1.0;
         double var11 = var8 * var8;

         for (int var7 = 1; var7 <= var17.b; var7++) {
            double var14 = 0.0;
            var6[var7 + 1] = var7 * var6[var7];

            for (int var10 = var7 + 1; var10 >= 0; var10 -= 2) {
               var14 = var14 * var11 + var6[var10];
               if (var10 > 2) {
                  var6[var10 - 2] = (var10 - 1) * var6[var10 - 1] + (var10 - 3) * var6[var10 - 3];
               } else if (var10 == 2) {
                  var6[0] = var6[1];
               }
            }

            if ((var7 & 1) == 0) {
               var14 *= var8;
            }

            var5[var7] = var14;
         }
      }

      var17.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg S() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var21 = this.a;
      double[] var5 = new double[1 + var21.b];
      double var8 = var3[0];
      var5[0] = BY.s(var8);
      if (var21.b > 0) {
         double[] var6;
         (var6 = new double[var21.b])[0] = -1.0;
         double var11 = var8 * var8;
         double var13;
         double var15 = BY.a(var13 = 1.0 / (1.0 - var11));
         var5[1] = var15 * var6[0];

         for (int var7 = 2; var7 <= var21.b; var7++) {
            double var18 = 0.0;
            var6[var7 - 1] = (var7 - 1) * var6[var7 - 2];

            for (int var10 = var7 - 1; var10 >= 0; var10 -= 2) {
               var18 = var18 * var11 + var6[var10];
               if (var10 > 2) {
                  var6[var10 - 2] = (var10 - 1) * var6[var10 - 1] + (2 * var7 - var10) * var6[var10 - 3];
               } else if (var10 == 2) {
                  var6[0] = var6[1];
               }
            }

            if ((var7 & 1) == 0) {
               var18 *= var8;
            }

            var15 *= var13;
            var5[var7] = var15 * var18;
         }
      }

      var21.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg T() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var21 = this.a;
      double[] var5 = new double[1 + var21.b];
      double var8 = var3[0];
      var5[0] = BY.r(var8);
      if (var21.b > 0) {
         double[] var6;
         (var6 = new double[var21.b])[0] = 1.0;
         double var11 = var8 * var8;
         double var13;
         double var15 = BY.a(var13 = 1.0 / (1.0 - var11));
         var5[1] = var15 * var6[0];

         for (int var7 = 2; var7 <= var21.b; var7++) {
            double var18 = 0.0;
            var6[var7 - 1] = (var7 - 1) * var6[var7 - 2];

            for (int var10 = var7 - 1; var10 >= 0; var10 -= 2) {
               var18 = var18 * var11 + var6[var10];
               if (var10 > 2) {
                  var6[var10 - 2] = (var10 - 1) * var6[var10 - 1] + (2 * var7 - var10) * var6[var10 - 3];
               } else if (var10 == 2) {
                  var6[0] = var6[1];
               }
            }

            if ((var7 & 1) == 0) {
               var18 *= var8;
            }

            var15 *= var13;
            var5[var7] = var15 * var18;
         }
      }

      var21.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg U() {
      lg var1 = new lg(this.a);
      this.a.k(this.b, 0, var1.b, 0);
      return var1;
   }

   private lg j(lg var1) {
      this.a.a(var1.a);
      lg var2 = new lg(this.a);
      boolean var8 = false;
      double[] var5 = var2.b;
      boolean var9 = false;
      double[] var4 = var1.b;
      boolean var10 = false;
      double[] var3 = this.b;
      lf var11 = this.a;
      double[] var6 = new double[this.a.c()];
      var11.c(var4, 0, var4, 0, var6, 0);
      double[] var7 = new double[var11.c()];
      var11.c(var3, 0, var3, 0, var7, 0);
      var11.a(var6, 0, var7, 0, var7, 0);
      var11.b(var7, 0, 2, var6, 0);
      if (var4[0] >= 0.0) {
         var11.a(var6, 0, var4, 0, var7, 0);
         var11.a(var3, 0, var7, var6, 0);
         var11.k(var6, 0, var7, 0);

         for (int var12 = 0; var12 < var7.length; var12++) {
            var5[var12 + 0] = 2.0 * var7[var12];
         }
      } else {
         var11.b(var6, 0, var4, 0, var7, 0);
         var11.a(var3, 0, var7, var6, 0);
         var11.k(var6, 0, var7, 0);
         var5[0] = (var7[0] <= 0.0 ? -Math.PI : Math.PI) - 2.0 * var7[0];

         for (int var13 = 1; var13 < var7.length; var13++) {
            var5[var13 + 0] = -2.0 * var7[var13];
         }
      }

      var5[0] = BY.b(var3[0], var4[0]);
      return var2;
   }

   private static lg b(lg var0, lg var1) {
      return var0.j(var1);
   }

   public final lg V() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var8 = this.a;
      double[] var5;
      (var5 = new double[1 + var8.b])[0] = BY.b(var3[0]);
      if (var8.b > 0) {
         var5[1] = BY.c(var3[0]);

         for (int var6 = 2; var6 <= var8.b; var6++) {
            var5[var6] = var5[var6 - 2];
         }
      }

      var8.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg W() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var8 = this.a;
      double[] var5;
      (var5 = new double[1 + var8.b])[0] = BY.c(var3[0]);
      if (var8.b > 0) {
         var5[1] = BY.b(var3[0]);

         for (int var6 = 2; var6 <= var8.b; var6++) {
            var5[var6] = var5[var6 - 2];
         }
      }

      var8.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg X() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var17 = this.a;
      double[] var5 = new double[1 + var17.b];
      double var8 = BY.d(var3[0]);
      var5[0] = var8;
      if (var17.b > 0) {
         double[] var6;
         (var6 = new double[var17.b + 2])[1] = 1.0;
         double var11 = var8 * var8;

         for (int var7 = 1; var7 <= var17.b; var7++) {
            double var14 = 0.0;
            var6[var7 + 1] = -var7 * var6[var7];

            for (int var10 = var7 + 1; var10 >= 0; var10 -= 2) {
               var14 = var14 * var11 + var6[var10];
               if (var10 > 2) {
                  var6[var10 - 2] = (var10 - 1) * var6[var10 - 1] - (var10 - 3) * var6[var10 - 3];
               } else if (var10 == 2) {
                  var6[0] = var6[1];
               }
            }

            if ((var7 & 1) == 0) {
               var14 *= var8;
            }

            var5[var7] = var14;
         }
      }

      var17.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg Y() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var21 = this.a;
      double[] var5 = new double[1 + var21.b];
      double var8 = var3[0];
      var5[0] = BY.e(var8);
      if (var21.b > 0) {
         double[] var6;
         (var6 = new double[var21.b])[0] = 1.0;
         double var11 = var8 * var8;
         double var13;
         double var15 = BY.a(var13 = 1.0 / (var11 - 1.0));
         var5[1] = var15 * var6[0];

         for (int var7 = 2; var7 <= var21.b; var7++) {
            double var18 = 0.0;
            var6[var7 - 1] = (1 - var7) * var6[var7 - 2];

            for (int var10 = var7 - 1; var10 >= 0; var10 -= 2) {
               var18 = var18 * var11 + var6[var10];
               if (var10 > 2) {
                  var6[var10 - 2] = (1 - var10) * var6[var10 - 1] + (var10 - 2 * var7) * var6[var10 - 3];
               } else if (var10 == 2) {
                  var6[0] = -var6[1];
               }
            }

            if ((var7 & 1) == 0) {
               var18 *= var8;
            }

            var15 *= var13;
            var5[var7] = var15 * var18;
         }
      }

      var21.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg Z() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var21 = this.a;
      double[] var5 = new double[1 + var21.b];
      double var8 = var3[0];
      var5[0] = BY.f(var8);
      if (var21.b > 0) {
         double[] var6;
         (var6 = new double[var21.b])[0] = 1.0;
         double var11 = var8 * var8;
         double var13;
         double var15 = BY.a(var13 = 1.0 / (var11 + 1.0));
         var5[1] = var15 * var6[0];

         for (int var7 = 2; var7 <= var21.b; var7++) {
            double var18 = 0.0;
            var6[var7 - 1] = (1 - var7) * var6[var7 - 2];

            for (int var10 = var7 - 1; var10 >= 0; var10 -= 2) {
               var18 = var18 * var11 + var6[var10];
               if (var10 > 2) {
                  var6[var10 - 2] = (var10 - 1) * var6[var10 - 1] + (var10 - 2 * var7) * var6[var10 - 3];
               } else if (var10 == 2) {
                  var6[0] = var6[1];
               }
            }

            if ((var7 & 1) == 0) {
               var18 *= var8;
            }

            var15 *= var13;
            var5[var7] = var15 * var18;
         }
      }

      var21.b(var3, 0, var5, var4, 0);
      return var1;
   }

   public final lg aa() {
      lg var1 = new lg(this.a);
      boolean var2 = false;
      double[] var4 = var1.b;
      var2 = false;
      double[] var3 = this.b;
      lf var21 = this.a;
      double[] var5 = new double[1 + var21.b];
      double var8 = var3[0];
      var5[0] = BY.g(var8);
      if (var21.b > 0) {
         double[] var6;
         (var6 = new double[var21.b])[0] = 1.0;
         double var11 = var8 * var8;
         double var13;
         double var15 = var13 = 1.0 / (1.0 - var11);
         var5[1] = var15 * var6[0];

         for (int var7 = 2; var7 <= var21.b; var7++) {
            double var18 = 0.0;
            var6[var7 - 1] = var7 * var6[var7 - 2];

            for (int var10 = var7 - 1; var10 >= 0; var10 -= 2) {
               var18 = var18 * var11 + var6[var10];
               if (var10 > 2) {
                  var6[var10 - 2] = (var10 - 1) * var6[var10 - 1] + (2 * var7 - var10 + 1) * var6[var10 - 3];
               } else if (var10 == 2) {
                  var6[0] = var6[1];
               }
            }

            if ((var7 & 1) == 0) {
               var18 *= var8;
            }

            var15 *= var13;
            var5[var7] = var15 * var18;
         }
      }

      var21.b(var3, 0, var5, var4, 0);
      return var1;
   }

   private lg ah() {
      lg var1 = new lg(this.a);

      for (int var2 = 0; var2 < var1.b.length; var2++) {
         var1.b[var2] = BY.v(this.b[var2]);
      }

      return var1;
   }

   private lg ai() {
      lg var1 = new lg(this.a);

      for (int var2 = 0; var2 < var1.b.length; var2++) {
         var1.b[var2] = BY.u(this.b[var2]);
      }

      return var1;
   }

   private double b(double... var1) {
      return this.a.a(this.b, 0, var1);
   }

   private static lg a(lg[] var0, lg[] var1) {
      double[] var2 = new double[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         lg var5;
         var2[var3] = (var5 = var0[var3]).b[0];
      }

      double[] var7 = new double[var1.length];

      for (int var4 = 0; var4 < var1.length; var4++) {
         lg var11;
         var7[var4] = (var11 = var1[var4]).b[0];
      }

      double var10 = Ch.h(var2, var7);
      lg var6 = var0[0].c().a();

      for (int var8 = 0; var8 < var0.length; var8++) {
         var6 = var6.a(var0[var8].b(var1[var8]));
      }

      (var7 = var6.F())[0] = var10;
      return new lg(var6.C(), var6.D(), var7);
   }

   private static lg a(double[] var0, lg[] var1) {
      double[] var2 = new double[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         lg var4;
         var2[var3] = (var4 = var1[var3]).b[0];
      }

      double var7 = Ch.h(var0, var2);
      lg var6 = var1[0].c().a();

      for (int var5 = 0; var5 < var0.length; var5++) {
         var6 = var6.a(var1[var5].j(var0[var5]));
      }

      double[] var8;
      (var8 = var6.F())[0] = var7;
      return new lg(var6.C(), var6.D(), var8);
   }

   private lg a(lg var1, lg var2, lg var3, lg var4) {
      double var5 = Ch.a(var1.b[0], var2.b[0], var3.b[0], var4.b[0]);
      double[] var7;
      (var7 = var1.b(var2).a(var3.b(var4)).F())[0] = var5;
      return new lg(this.C(), this.D(), var7);
   }

   private lg a(double var1, lg var3, double var4, lg var6) {
      double var7 = Ch.a(var1, var3.b[0], var4, var6.b[0]);
      double[] var9;
      (var9 = var3.j(var1).a(var6.j(var4)).F())[0] = var7;
      return new lg(this.C(), this.D(), var9);
   }

   private lg a(lg var1, lg var2, lg var3, lg var4, lg var5, lg var6) {
      double var7 = Ch.a(var1.b[0], var2.b[0], var3.b[0], var4.b[0], var5.b[0], var6.b[0]);
      double[] var9;
      (var9 = var1.b(var2).a(var3.b(var4)).a(var5.b(var6)).F())[0] = var7;
      return new lg(this.C(), this.D(), var9);
   }

   private lg a(double var1, lg var3, double var4, lg var6, double var7, lg var9) {
      double var10 = Ch.a(var1, var3.b[0], var4, var6.b[0], var7, var9.b[0]);
      double[] var12;
      (var12 = var3.j(var1).a(var6.j(var4)).a(var9.j(var7)).F())[0] = var10;
      return new lg(this.C(), this.D(), var12);
   }

   private lg a(lg var1, lg var2, lg var3, lg var4, lg var5, lg var6, lg var7, lg var8) {
      double var9 = Ch.a(var1.b[0], var2.b[0], var3.b[0], var4.b[0], var5.b[0], var6.b[0], var7.b[0], var8.b[0]);
      double[] var11;
      (var11 = var1.b(var2).a(var3.b(var4)).a(var5.b(var6)).a(var7.b(var8)).F())[0] = var9;
      return new lg(this.C(), this.D(), var11);
   }

   private lg a(double var1, lg var3, double var4, lg var6, double var7, lg var9, double var10, lg var12) {
      double var13 = Ch.a(var1, var3.b[0], var4, var6.b[0], var7, var9.b[0], var10, var12.b[0]);
      double[] var15;
      (var15 = var3.j(var1).a(var6.j(var4)).a(var9.j(var7)).a(var12.j(var10)).F())[0] = var13;
      return new lg(this.C(), this.D(), var15);
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof lg) {
         other = (lg)other;
         return this.C() == ((lg)other).C() && this.D() == ((lg)other).D() && Ch.i(this.b, ((lg)other).b);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var10000 = 227 + 229 * this.C() + 233 * this.D();
      double[] var1 = this.b;
      return var10000 + 239 * Arrays.hashCode(this.b);
   }

   private lg.a aj() {
      lf var1 = this.a;
      var1 = this.a;
      return new lg.a(this.a.a, this.a.b, this.b);
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6, double var7, kO var9, double var10, kO var12) {
      lg var10002 = var3;
      lg var10004 = (lg)var6;
      lg var10006 = (lg)var9;
      lg var5 = (lg)var12;
      double var23 = var10;
      lg var31 = var10006;
      double var20 = var7;
      var3 = var10004;
      double var17 = var4;
      lg var2 = var10002;
      double var14 = var1;
      lg var28 = this;
      double var26 = Ch.a(var14, var2.b[0], var17, var3.b[0], var20, var31.b[0], var23, var5.b[0]);
      double[] var29;
      (var29 = var2.j(var14).a(var3.j(var17)).a(var31.j(var20)).a(var5.j(var23)).F())[0] = var26;
      return new lg(var28.C(), var28.D(), var29);
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, kO var3, kO var4, kO var5, kO var6, kO var7, kO var8) {
      lg var10001 = var1;
      lg var10002 = var2;
      lg var10003 = var3;
      lg var10004 = var4;
      lg var10005 = var5;
      lg var10006 = var6;
      lg var10007 = var7;
      lg var9 = var8;
      var8 = var10007;
      var7 = var10006;
      var6 = var10005;
      var5 = var10004;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      var1 = this;
      double var18 = Ch.a(var2.b[0], var3.b[0], var4.b[0], var5.b[0], var6.b[0], var7.b[0], var8.b[0], var9.b[0]);
      double[] var22;
      (var22 = var2.b(var3).a(var4.b(var5)).a(var6.b(var7)).a(var8.b(var9)).F())[0] = var18;
      return new lg(var1.C(), var1.D(), var22);
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6, double var7, kO var9) {
      lg var10002 = var3;
      lg var10004 = (lg)var6;
      lg var25 = (lg)var9;
      double var17 = var7;
      var3 = var10004;
      double var14 = var4;
      lg var2 = var10002;
      double var11 = var1;
      lg var22 = this;
      double var20 = Ch.a(var11, var2.b[0], var14, var3.b[0], var17, var25.b[0]);
      double[] var23;
      (var23 = var2.j(var11).a(var3.j(var14)).a(var25.j(var17)).F())[0] = var20;
      return new lg(var22.C(), var22.D(), var23);
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, kO var3, kO var4, kO var5, kO var6) {
      lg var10001 = var1;
      lg var10002 = var2;
      lg var10003 = var3;
      lg var10004 = var4;
      lg var10005 = var5;
      lg var7 = var6;
      var6 = var10005;
      var5 = var10004;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      var1 = this;
      double var14 = Ch.a(var2.b[0], var3.b[0], var4.b[0], var5.b[0], var6.b[0], var7.b[0]);
      double[] var18;
      (var18 = var2.b(var3).a(var4.b(var5)).a(var6.b(var7)).F())[0] = var14;
      return new lg(var1.C(), var1.D(), var18);
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6) {
      lg var10002 = var3;
      var3 = (lg)var6;
      double var11 = var4;
      lg var2 = var10002;
      double var8 = var1;
      lg var16 = this;
      double var14 = Ch.a(var8, var2.b[0], var11, var3.b[0]);
      double[] var17;
      (var17 = var2.j(var8).a(var3.j(var11)).F())[0] = var14;
      return new lg(var16.C(), var16.D(), var17);
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, Object var3, kO var4) {
      lg var10001 = var1;
      lg var10002 = var2;
      lg var10003 = var3;
      lg var5 = var4;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      var1 = this;
      double var10 = Ch.a(var2.b[0], var3.b[0], var4.b[0], var5.b[0]);
      double[] var14;
      (var14 = var2.b(var3).a(var4.b(var5)).F())[0] = var10;
      return new lg(var1.C(), var1.D(), var14);
   }

   // $VF: synthetic method
   @Override
   public final kO a(double[] var1, Object[] var2) {
      var2 = var2;
      var1 = var1;
      double[] var3 = new double[var2.length];

      for (int var6 = 0; var6 < var2.length; var6++) {
         lg var4;
         var3[var6] = (var4 = var2[var6]).b[0];
      }

      double var13 = Ch.h(var1, var3);
      lg var10 = var2[0].c().a();

      for (int var11 = 0; var11 < var1.length; var11++) {
         var10 = var10.a(var2[var11].j(var1[var11]));
      }

      double[] var12;
      (var12 = var10.F())[0] = var13;
      return new lg(var10.C(), var10.D(), var12);
   }

   // $VF: synthetic method
   @Override
   public final kO a(Object[] var1, Object[] var2) {
      lg[] var10000 = var1;
      var2 = var2;
      var1 = var10000;
      double[] var3 = new double[var10000.length];

      for (int var4 = 0; var4 < var1.length; var4++) {
         lg var5;
         var3[var4] = (var5 = var1[var4]).b[0];
      }

      double[] var12 = new double[var2.length];

      for (int var7 = 0; var7 < var2.length; var7++) {
         lg var15;
         var12[var7] = (var15 = var2[var7]).b[0];
      }

      double var16 = Ch.h(var3, var12);
      lg var11 = var1[0].c().a();

      for (int var13 = 0; var13 < var1.length; var13++) {
         var11 = var11.a(var1[var13].b(var2[var13]));
      }

      (var12 = var11.F())[0] = var16;
      return new lg(var11.C(), var11.D(), var12);
   }

   // $VF: synthetic method
   @Override
   public final kO B() {
      return this.aa();
   }

   // $VF: synthetic method
   @Override
   public final kO A() {
      return this.Z();
   }

   // $VF: synthetic method
   @Override
   public final kO z() {
      return this.Y();
   }

   // $VF: synthetic method
   @Override
   public final kO y() {
      return this.X();
   }

   // $VF: synthetic method
   @Override
   public final kO x() {
      return this.W();
   }

   // $VF: synthetic method
   @Override
   public final kO w() {
      return this.V();
   }

   // $VF: synthetic method
   @Override
   public final kO h(Object var1) {
      return this.j((lg)var1);
   }

   // $VF: synthetic method
   @Override
   public final kO v() {
      return this.U();
   }

   // $VF: synthetic method
   @Override
   public final kO u() {
      return this.T();
   }

   // $VF: synthetic method
   @Override
   public final kO t() {
      return this.S();
   }

   // $VF: synthetic method
   @Override
   public final kO s() {
      return this.R();
   }

   // $VF: synthetic method
   @Override
   public final kO r() {
      return this.Q();
   }

   // $VF: synthetic method
   @Override
   public final kO q() {
      return this.P();
   }

   // $VF: synthetic method
   @Override
   public final kO p() {
      return this.N();
   }

   // $VF: synthetic method
   @Override
   public final kO o() {
      return this.M();
   }

   // $VF: synthetic method
   @Override
   public final kO n() {
      return this.L();
   }

   // $VF: synthetic method
   @Override
   public final kO m() {
      return this.K();
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1) {
      lg var2 = var1;
      var1 = this;
      this.a.a(var2.a);
      lg var3 = new lg(var1.a);
      boolean var8 = false;
      double[] var5 = var3.b;
      boolean var9 = false;
      double[] var4 = var2.b;
      boolean var10 = false;
      double[] var12 = var1.b;
      lf var11 = var1.a;
      double[] var6 = new double[var1.a.c()];
      var11.c(var12, 0, var6, 0);
      double[] var13 = new double[var11.c()];
      var11.c(var6, 0, var4, 0, var13, 0);
      var11.a(var13, 0, var5, 0);
      return var3;
   }

   // $VF: synthetic method
   @Override
   public final kO d(int var1) {
      int var2 = var1;
      lg var4 = this;
      lg var3 = new lg(var4.a);
      var4.a.a(var4.b, 0, var2, var3.b, 0);
      return var3;
   }

   // $VF: synthetic method
   @Override
   public final kO g(double var1) {
      return this.k(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO c(int var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO l() {
      return this.e(3);
   }

   // $VF: synthetic method
   @Override
   public final kO k() {
      return this.e(2);
   }

   // $VF: synthetic method
   @Override
   public final kN b() {
      return this.H();
   }

   // $VF: synthetic method
   @Override
   public final kO g(Object var1) {
      return this.h((lg)var1);
   }

   // $VF: synthetic method
   @Override
   public final kO b(int var1) {
      return this.g(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO f(double var1) {
      double var4 = var1;
      lg var10 = this;
      long var6 = Double.doubleToLongBits(this.b[0]);
      long var8 = Double.doubleToLongBits(var4);
      return (var6 < 0L || var8 < 0L) && (var6 >= 0L || var8 >= 0L) ? var10.G() : var10;
   }

   // $VF: synthetic method
   @Override
   public final kO f(Object var1) {
      lg var2 = var1;
      var1 = this;
      long var4 = Double.doubleToLongBits(this.b[0]);
      long var6 = Double.doubleToLongBits(var2.b[0]);
      return (var4 < 0L || var6 < 0L) && (var4 >= 0L || var6 >= 0L) ? var1.G() : var1;
   }

   // $VF: synthetic method
   @Override
   public final kO j() {
      lg var1 = this;
      lf var2 = var1.a;
      var2 = var1.a;
      return new lg(var1.a.a, var1.a.b, BY.h(var1.b[0]));
   }

   // $VF: synthetic method
   @Override
   public final kO h() {
      lg var1 = this;
      lf var2 = var1.a;
      var2 = var1.a;
      return new lg(var1.a.a, var1.a.b, BY.A(var1.b[0]));
   }

   // $VF: synthetic method
   @Override
   public final kO g() {
      lg var1 = this;
      lf var2 = var1.a;
      var2 = var1.a;
      return new lg(var1.a.a, var1.a.b, BY.y(var1.b[0]));
   }

   // $VF: synthetic method
   @Override
   public final kO f() {
      lg var1 = this;
      lf var2 = var1.a;
      var2 = var1.a;
      return new lg(var1.a.a, var1.a.b, BY.z(var1.b[0]));
   }

   // $VF: synthetic method
   @Override
   public final kO e() {
      return this.ab();
   }

   // $VF: synthetic method
   @Override
   public final kO e(Object var1) {
      lg var2 = var1;
      var1 = this;
      this.a.a(var2.a);
      lg var3 = new lg(var1.a);
      boolean var17 = false;
      double[] var5 = var3.b;
      boolean var18 = false;
      double[] var4 = var2.b;
      boolean var19 = false;
      double[] var21 = var1.b;
      lf var20 = var1.a;
      double var12 = BY.g(var21[0], var4[0]);
      double var14 = BY.A((var21[0] - var12) / var4[0]);
      var5[0] = var12;

      for (int var6 = 1; var6 < var20.c(); var6++) {
         var5[var6 + 0] = var21[var6 + 0] - var14 * var4[var6 + 0];
      }

      return var3;
   }

   // $VF: synthetic method
   @Override
   public final kO e(double var1) {
      double var4 = var1;
      lg var6 = this;
      lg var7;
      (var7 = new lg(var6)).b[0] = BY.g(var7.b[0], var4);
      return var7;
   }

   // $VF: synthetic method
   @Override
   public final kO d(double var1) {
      double var4 = var1;
      lg var6 = this;
      lg var7 = new lg(var6);

      for (int var2 = 0; var2 < var7.b.length; var2++) {
         var7.b[var2] = var7.b[var2] / var4;
      }

      return var7;
   }

   // $VF: synthetic method
   @Override
   public final kO c(double var1) {
      return this.j(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO b(double var1) {
      double var4 = var1;
      return this.h(-var4);
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1) {
      return this.h(var1);
   }

   // $VF: synthetic method
   @Override
   public final kN d(Object var1) {
      lg var2 = var1;
      var1 = this;
      this.a.a(var2.a);
      lg var3 = new lg(var1.a);
      var1.a.a(var1.b, 0, var2.b, var3.b, 0);
      return var3;
   }

   // $VF: synthetic method
   @Override
   public final kN c(Object var1) {
      return this.b((lg)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a(int var1) {
      int var2 = var1;
      return this.j(var2);
   }

   // $VF: synthetic method
   @Override
   public final kN a() {
      return this.G();
   }

   // $VF: synthetic method
   @Override
   public final kN b(Object var1) {
      lg var2 = var1;
      var1 = this;
      this.a.a(var2.a);
      lg var3 = new lg(var1);
      var1.a.b(var1.b, 0, var2.b, 0, var3.b, 0);
      return var3;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN a(Object var1) {
      return this.a((lg)var1);
   }

   static class a implements Serializable {
      private static final long a = 20120730L;
      private final int b;
      private final int c;
      private final double[] d;

      a(int var1, int var2, double[] var3) {
         this.b = var1;
         this.c = var2;
         this.d = var3;
      }

      private lg a() {
         return new lg(this.b, this.c, this.d);
      }
   }
}
