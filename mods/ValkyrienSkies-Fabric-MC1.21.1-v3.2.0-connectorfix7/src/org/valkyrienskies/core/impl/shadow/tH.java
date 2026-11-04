package org.valkyrienskies.core.impl.shadow;

public final class tH extends tm {
   private static final String a = "operator";
   private static final String b = "threshold";
   private static final String c = "vector";
   private static final String d = "vector1";
   private static final String e = "vector2";
   private final boolean f;
   private final double g;

   private tH(int var1, double var2, boolean var4) {
      super(var1);
      this.g = var2;
      this.f = var4;
   }

   private tH(Cf var1, double var2, boolean var4) {
      super(var1);
      this.g = var2;
      this.f = var4;
   }

   private boolean b() {
      return this.f;
   }

   @Override
   public final tu a(tp var1, tp var2, tu var3) {
      Ci.a(var1);
      sA var4 = new sA(var1.g());
      return this.a(var1, var2, var3, var4, false, 0.0);
   }

   private tu a(tp var1, tp var2, tu var3, boolean var4, double var5) {
      Ci.a(var1);
      sA var7 = new sA(var1.g());
      return this.a(var1, var2, var3, var7, var4, var5);
   }

   @Override
   public final tu b(tp var1, tp var2, tu var3, tu var4) {
      Ci.a(var4);
      return this.a(var1, var2, var3, var4.j(), false, 0.0);
   }

   @Override
   public final tu a(tp var1, tu var2) {
      Ci.a(var1);
      sA var3;
      (var3 = new sA(var1.g())).f(0.0);
      return this.a(var1, null, var2, var3, false, 0.0);
   }

   private tu a(tp var1, tu var2, boolean var3, double var4) {
      Ci.a(var1);
      sA var6 = new sA(var1.g());
      return this.a(var1, null, var2, var6, var3, var4);
   }

   @Override
   public final tu b(tp var1, tu var2, tu var3) {
      Ci.a(var3);
      return this.a(var1, null, var2, var3.j(), false, 0.0);
   }

   @Override
   public final tu a(tp var1, tp var2, tu var3, tu var4) {
      return this.a(var1, var2, var3, var4, false, 0.0);
   }

   private tu a(tp var1, tp var2, tu var3, tu var4, boolean var5, double var6) {
      c(var1, var2, var3, var4);
      Cf var8;
      (var8 = this.a()).c();
      Cc.a var9 = var8.a;
      var8.a.d(1);
      tH.a var10;
      (var10 = new tH.a(var1, var2, var3, var5, var6, this.g, this.f)).a();
      var10.a(var4);
      var9 = var8.a;
      sJ var11 = new sJ(this, var8.a.e, var4, var3, var10.b);
      if (var10.c) {
         var8.d(var11);
         return var4;
      }

      var5 = var10.e() || var10.a;
      var8.a(var11);
      if (!var5) {
         do {
            var9 = var8.a;
            var8.a.d(1);
            var9 = var8.a;
            sJ var12 = new sJ(this, var8.a.e, var4, var3, var10.b);
            var8.c(var12);
            var10.b();
            var10.a(var4);
            var9 = var8.a;
            sJ var13 = new sJ(this, var8.a.e, var4, var3, var10.b);
            var8.b(var13);
         } while (!var10.a);
      }

      var9 = var8.a;
      sJ var14 = new sJ(this, var8.a.e, var4, var3, var10.b);
      var8.d(var14);
      return var4;
   }

   @Override
   public final tu c(tp var1, tu var2, tu var3) {
      return this.a(var1, null, var2, var3, false, 0.0);
   }

   static class a {
      private static double d = BY.t(tH.a.e = BY.x(1.0));
      private static double e;
      private final tp f;
      private final tu g;
      private final boolean h;
      private final double i;
      private double j;
      private double k;
      private double l;
      private double m;
      private double n;
      private double o;
      private double p;
      private double q;
      private double r;
      private final boolean s;
      boolean a;
      private double t;
      private final tp u;
      private double v;
      private final tu w;
      private double x;
      private tu y;
      private tu z;
      double b;
      private final double A;
      private double B;
      private double C;
      private tu D;
      private final sA E;
      private tu F;
      private double G;
      boolean c;

      a(tp var1, tp var2, tu var3, boolean var4, double var5, double var7, boolean var9) {
         this.f = var1;
         this.u = var2;
         this.g = var3;
         this.E = new sA(var3.f());
         this.s = var4;
         this.A = var5;
         this.w = var2 == null ? var3 : var2.a(var3);
         this.a = false;
         this.h = var9;
         this.i = var7;
      }

      private static void a(tp var0, tu var1, tu var2, tu var3) {
         double var4 = var2.b(var2);
         double var6 = var1.b(var3);
         double var8 = (var4 + e) * d;
         if (BY.w(var4 - var6) > var8) {
            tg var10;
            pu var11;
            (var11 = (var10 = new tg()).a).a("operator", var0);
            var11.a("vector1", var1);
            var11.a("vector2", var2);
            var11.a("threshold", var8);
            throw var10;
         }
      }

      private static void a(tp var0, tu var1) {
         tf var2;
         pu var3;
         (var3 = (var2 = new tf()).a).a("operator", var0);
         var3.a("vector", var1);
         throw var2;
      }

      private static void a(double var0, tu var2, tu var3) {
         int var4 = var2.f();

         for (int var5 = 0; var5 < var4; var5++) {
            var3.a(var5, var0 * var2.a(var5) + var3.a(var5));
         }
      }

      private static void a(double var0, tu var2, double var3, tu var5, tu var6) {
         int var7 = var6.f();

         for (int var8 = 0; var8 < var7; var8++) {
            double var9 = var0 * var2.a(var8) + var3 * var5.a(var8) + var6.a(var8);
            var6.a(var8, var9);
         }
      }

      final void a(tu var1) {
         int var2 = this.E.f();
         if (this.t < this.m) {
            if (!this.s) {
               var1.a(0, this.E);
            } else {
               double var20 = this.l / this.k;

               for (int var21 = 0; var21 < var2; var21++) {
                  double var6 = this.w.a(var21);
                  double var8 = this.E.a(var21);
                  var1.a(var21, var8 + var20 * var6);
               }
            }
         } else {
            double var3 = BY.a(this.C);
            double var5 = this.p == 0.0 ? var3 * e : this.p;
            double var7 = this.o / var5;
            double var9 = (this.l + this.B * var7) / this.k;
            if (!this.s) {
               for (int var19 = 0; var19 < var2; var19++) {
                  double var22 = this.E.a(var19);
                  double var23 = this.D.a(var19);
                  var1.a(var19, var22 + var7 * var23);
               }
            } else {
               for (int var18 = 0; var18 < var2; var18++) {
                  double var12 = this.E.a(var18);
                  double var14 = this.D.a(var18);
                  double var16 = this.w.a(var18);
                  var1.a(var18, var12 + var7 * var14 + var9 * var16);
               }
            }
         }
      }

      final void a() {
         this.E.f(0.0);
         this.y = this.g.j();
         this.F = this.u == null ? this.g.j() : this.u.a(this.y);
         if (this.u != null && this.h) {
            a(this.u, this.y, this.F, this.u.a(this.F));
         }

         this.k = this.y.b(this.F);
         if (this.k < 0.0) {
            a(this.u, this.F);
         }

         if (this.k == 0.0) {
            this.c = true;
         } else {
            this.c = false;
            this.k = BY.a(this.k);
            tu var1 = this.F.h(1.0 / this.k);
            this.F = this.f.a(var1);
            if (this.h) {
               a(this.f, var1, this.F, this.f.a(this.F));
            }

            a(-this.A, var1, this.F);
            double var2;
            a(-(var2 = var1.b(this.F)) / this.k, this.y, this.F);
            double var4 = var1.b(this.F);
            double var6 = var1.b(var1);
            a(-var4 / var6, var1, this.F);
            this.z = this.F.j();
            if (this.u != null) {
               this.F = this.u.a(this.z);
            }

            this.x = this.k;
            this.j = this.z.b(this.F);
            if (this.j < 0.0) {
               a(this.u, this.F);
            }

            this.j = BY.a(this.j);
            this.m = this.k;
            this.p = var2;
            this.n = this.j;
            this.o = this.k;
            this.v = 0.0;
            this.l = 0.0;
            this.B = 1.0;
            this.C = var2 * var2 + this.j * this.j;
            this.G = 0.0;
            this.q = BY.w(var2) + e;
            this.r = this.q;
            if (this.s) {
               this.D = new sA(this.f.f());
               this.D.f(0.0);
            } else {
               this.D = var1;
            }

            this.g();
         }
      }

      final void b() {
         tu var1 = this.F.h(1.0 / this.j);
         this.F = this.f.a(var1);
         a(-this.A, var1, -this.j / this.x, this.y, this.F);
         double var2;
         a(-(var2 = var1.b(this.F)) / this.j, this.z, this.F);
         this.y = this.z;
         this.z = this.F;
         if (this.u != null) {
            this.F = this.u.a(this.z);
         }

         this.x = this.j;
         this.j = this.z.b(this.F);
         if (this.j < 0.0) {
            a(this.u, this.F);
         }

         this.j = BY.a(this.j);
         this.C = this.C + (var2 * var2 + this.x * this.x + this.j * this.j);
         double var4 = BY.a(this.p * this.p + this.x * this.x);
         double var6 = this.p / var4;
         double var8 = this.x / var4;
         double var10 = var6 * this.n + var8 * var2;
         this.p = var8 * this.n - var6 * var2;
         double var12 = var8 * this.j;
         this.n = -var6 * this.j;
         double var14;
         double var16 = (var14 = this.o / var4) * var6;
         double var18 = var14 * var8;
         int var28 = this.E.f();

         for (int var3 = 0; var3 < var28; var3++) {
            double var22 = this.E.a(var3);
            double var24 = var1.a(var3);
            double var26 = this.D.a(var3);
            this.E.a(var3, var22 + var26 * var16 + var24 * var18);
            this.D.a(var3, var26 * var8 - var24 * var6);
         }

         this.l = this.l + this.B * var6 * var14;
         this.B *= var8;
         this.q = BY.e(this.q, var4);
         this.r = BY.d(this.r, var4);
         this.G += var14 * var14;
         this.o = this.v - var10 * var14;
         this.v = -var12 * var14;
         this.g();
      }

      private void g() {
         double var1 = BY.a(this.C);
         double var3 = BY.a(this.G);
         double var5 = var1 * e;
         double var7 = var1 * var3 * e;
         double var9 = var1 * var3 * this.i;
         double var11 = this.p == 0.0 ? var5 : this.p;
         this.t = BY.a(this.o * this.o + this.v * this.v);
         double var13 = this.B * this.k;
         this.m = var13 * this.j / BY.w(var11);
         double var15;
         if (this.t <= this.m) {
            var15 = this.q / this.r;
         } else {
            var15 = this.q / BY.d(this.r, BY.w(var11));
         }

         if (var15 * e >= 0.1) {
            throw new sX(var15);
         }

         if (this.k <= var7) {
            throw new tB();
         }

         this.b = BY.d(this.m, this.t);
         this.a = this.m <= var7 || this.m <= var9;
      }

      final boolean c() {
         return this.a;
      }

      final boolean d() {
         return this.c;
      }

      final boolean e() {
         return this.j < e;
      }

      final double f() {
         return this.b;
      }
   }
}
