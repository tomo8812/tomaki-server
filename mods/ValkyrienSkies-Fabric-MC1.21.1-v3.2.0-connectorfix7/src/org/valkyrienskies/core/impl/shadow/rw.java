package org.valkyrienskies.core.impl.shadow;

import java.awt.geom.AffineTransform;

public final class rw implements rU<rv, qU>, rV<rv> {
   private static final double e = 1.0E-10;
   private double f;
   double a;
   double b;
   double c;
   final double d;
   private rw g;

   public rw(rB var1, rB var2, double var3) {
      rB var5 = var2;
      var2 = var1;
      rw var14 = this;
      this.j();
      double var8 = var5.c - var2.c;
      double var10 = var5.d - var2.d;
      double var12;
      if ((var12 = BY.f(var8, var10)) == 0.0) {
         var14.f = 0.0;
         var14.a = 1.0;
         var14.b = 0.0;
         var14.c = var2.d;
      } else {
         var14.f = Math.PI + BY.b(-var10, -var8);
         var14.a = var8 / var12;
         var14.b = var10 / var12;
         var14.c = Ch.a(var5.c, var2.d, -var2.c, var5.d) / var12;
      }

      this.d = var3;
   }

   public rw(rB var1, double var2, double var4) {
      double var8 = var2;
      rB var11 = var1;
      rw var10 = this;
      this.j();
      var10.f = Ci.b(var8, Math.PI);
      var10.a = BY.o(var10.f);
      var10.b = BY.n(var10.f);
      var10.c = Ch.a(var10.a, var11.d, -var10.b, var11.c);
      this.d = var4;
   }

   private rw(double var1, double var3, double var5, double var7, double var9) {
      this.f = var1;
      this.a = var3;
      this.b = var5;
      this.c = var7;
      this.d = var9;
      this.g = null;
   }

   @Deprecated
   private rw(rB var1, rB var2) {
      this(var1, var2, 1.0E-10);
   }

   @Deprecated
   private rw(rB var1, double var2) {
      this(var1, var2, 1.0E-10);
   }

   private rw(rw var1) {
      this.f = Ci.b(var1.f, Math.PI);
      this.a = var1.a;
      this.b = var1.b;
      this.c = var1.c;
      this.d = var1.d;
      this.g = null;
   }

   public final rw a() {
      return new rw(this);
   }

   private void a(rB var1, rB var2) {
      this.j();
      double var3 = var2.c - var1.c;
      double var5 = var2.d - var1.d;
      double var7;
      if ((var7 = BY.f(var3, var5)) == 0.0) {
         this.f = 0.0;
         this.a = 1.0;
         this.b = 0.0;
         this.c = var1.d;
      } else {
         this.f = Math.PI + BY.b(-var5, -var3);
         this.a = var3 / var7;
         this.b = var5 / var7;
         this.c = Ch.a(var2.c, var1.d, -var1.c, var2.d) / var7;
      }
   }

   private void a(rB var1, double var2) {
      this.j();
      this.f = Ci.b(var2, Math.PI);
      this.a = BY.o(this.f);
      this.b = BY.n(this.f);
      this.c = Ch.a(this.a, var1.d, -this.b, var1.c);
   }

   private void i() {
      this.j();
      if (this.f < Math.PI) {
         this.f += Math.PI;
      } else {
         this.f -= Math.PI;
      }

      this.a = -this.a;
      this.b = -this.b;
      this.c = -this.c;
   }

   private void j() {
      if (this.g != null) {
         this.g.g = null;
      }

      this.g = null;
   }

   public final rw c() {
      if (this.g == null) {
         this.g = new rw(this.f < Math.PI ? this.f + Math.PI : this.f - Math.PI, -this.a, -this.b, -this.c, this.d);
         this.g.g = this;
      }

      return this.g;
   }

   public final qZ a(qO<rv> var1) {
      return this.e((qM<rv>)var1);
   }

   public final rB a(qO<qU> var1) {
      return this.f(var1);
   }

   public final qZ e(qM<rv> var1) {
      var1 = var1;
      return new qZ(Ch.a(this.a, var1.c, this.b, var1.d));
   }

   public final rB f(qM<qU> var1) {
      qZ var4;
      double var2 = (var4 = (qZ)var1).d;
      return new rB(Ch.a(var2, this.a, -this.c, this.b), Ch.a(var2, this.b, this.c, this.a));
   }

   public final rB a(rw var1) {
      double var2;
      return BY.w(var2 = Ch.a(this.b, var1.a, -var1.b, this.a)) < this.d
         ? null
         : new rB(Ch.a(this.a, var1.c, -var1.a, this.c) / var2, Ch.a(this.b, var1.c, -var1.b, this.c) / var2);
   }

   @Override
   public final qM<rv> b(qM<rv> var1) {
      qZ var2 = this.e(var1);
      return this.f(var2);
   }

   @Override
   public final double b() {
      return this.d;
   }

   public final rA d() {
      return new rA(this, new qW(this.d));
   }

   private ry k() {
      return new ry(this.d);
   }

   public final double b(rw var1) {
      return this.c + (Ch.a(this.a, var1.a, this.b, var1.b) > 0.0 ? -var1.c : var1.c);
   }

   public final double b(qO<rv> var1) {
      return this.a((qM<rv>)var1);
   }

   @Override
   public final double a(qM<rv> var1) {
      var1 = var1;
      return Ch.a(this.b, var1.c, -this.a, var1.d, 1.0, this.c);
   }

   @Override
   public final boolean a(rV<rv> var1) {
      var1 = var1;
      return Ch.a(this.b, var1.b, this.a, var1.a) >= 0.0;
   }

   private rB a(qZ var1, double var2) {
      double var4 = var1.d;
      double var6 = var2 - this.c;
      return new rB(Ch.a(var4, this.a, var6, this.b), Ch.a(var4, this.b, -var6, this.a));
   }

   private boolean c(rB var1) {
      rB var2 = var1;
      return BY.w(this.a((qM<rv>)var2)) < this.d;
   }

   private double d(rB var1) {
      rB var2 = var1;
      return BY.w(this.a((qM<rv>)var2));
   }

   private boolean g(rw var1) {
      return BY.w(Ch.a(this.b, var1.a, -this.a, var1.b)) < this.d;
   }

   private void e(rB var1) {
      this.c = Ch.a(this.a, var1.d, -this.b, var1.c);
   }

   public final double h() {
      return Ci.b(this.f, Math.PI);
   }

   private void a(double var1) {
      this.j();
      this.f = Ci.b(var1, Math.PI);
      this.a = BY.o(this.f);
      this.b = BY.n(this.f);
   }

   private double l() {
      return this.c;
   }

   private void b(double var1) {
      this.j();
      this.c = var1;
   }

   @Deprecated
   private static sc<rv, qU> a(AffineTransform var0) {
      double[] var1 = new double[6];
      var0.getMatrix(var1);
      return new rw.a(var1[0], var1[1], var1[2], var1[3], var1[4], var1[5]);
   }

   public static sc<rv, qU> a(double var0, double var2, double var4, double var6, double var8, double var10) {
      return new rw.a(var0, var2, var4, var6, var8, var10);
   }

   // $VF: synthetic method
   @Override
   public final rK e() {
      rw var1 = this;
      return new ry(var1.d);
   }

   // $VF: synthetic method
   @Override
   public final rL f() {
      return this.d();
   }

   // $VF: synthetic method
   @Override
   public final rV g() {
      return this.a();
   }

   // $VF: synthetic method
   @Override
   public final qM c(qM var1) {
      return this.f(var1);
   }

   // $VF: synthetic method
   @Override
   public final qM d(qM var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   static double c(rw var0) {
      return var0.a;
   }

   // $VF: synthetic method
   static double d(rw var0) {
      return var0.b;
   }

   // $VF: synthetic method
   static double e(rw var0) {
      return var0.c;
   }

   // $VF: synthetic method
   static double f(rw var0) {
      return var0.d;
   }

   // $VF: synthetic method
   rw(double var1, double var3, double var5, double var7, double var9, byte var11) {
      this(var1, var3, var5, var7, var9);
   }

   static class a implements sc<rv, qU> {
      private double a;
      private double b;
      private double c;
      private double d;
      private double e;
      private double f;
      private double g;
      private double h;
      private double i;

      a(double var1, double var3, double var5, double var7, double var9, double var11) {
         this.a = var1;
         this.b = var3;
         this.c = var5;
         this.d = var7;
         this.e = var9;
         this.f = var11;
         this.g = Ch.a(var5, var11, -var7, var9);
         this.h = Ch.a(var1, var11, -var3, var9);
         this.i = Ch.a(var1, var7, -var3, var5);
         if (BY.w(this.i) < 1.0E-20) {
            throw new oV(px.NON_INVERTIBLE_TRANSFORM);
         }
      }

      private rB b(qM<rv> var1) {
         rB var7;
         double var3 = (var7 = (rB)var1).c;
         double var5 = var7.d;
         return new rB(Ch.a(this.a, var3, this.c, var5, this.e, 1.0), Ch.a(this.b, var3, this.d, var5, this.f, 1.0));
      }

      private rw b(rV<rv> var1) {
         var1 = var1;
         double var3 = Ch.a(this.h, var1.a, this.g, var1.b, this.i, var1.c);
         double var5 = Ch.a(this.a, var1.a, this.c, var1.b);
         double var7 = Ch.a(this.b, var1.a, this.d, var1.b);
         double var9 = 1.0 / BY.a(var7 * var7 + var5 * var5);
         return new rw(Math.PI + BY.b(-var7, -var5), var9 * var5, var9 * var7, var9 * var3, var1.d, (byte)0);
      }

      @Override
      public final sb<qU> a(sb<qU> var1, rV<rv> var2, rV<rv> var3) {
         qX var5 = (qX)var1.c();
         var2 = var2;
         rw var7;
         rw var10000 = var7 = (rw)var3;
         qZ var4 = var5.a;
         rB var9 = this.b(var2.f(var4));
         qZ var8 = var10000.e(var9);
         return new qX(var8, var5.b, var2.d).a();
      }

      // $VF: synthetic method
      @Override
      public final rV a(rV var1) {
         rw var2 = var1;
         rw.a var13 = this;
         var2 = var2;
         double var5 = Ch.a(var13.h, var2.a, var13.g, var2.b, var13.i, var2.c);
         double var7 = Ch.a(var13.a, var2.a, var13.c, var2.b);
         double var9 = Ch.a(var13.b, var2.a, var13.d, var2.b);
         double var11 = 1.0 / BY.a(var9 * var9 + var7 * var7);
         return new rw(Math.PI + BY.b(-var9, -var7), var11 * var7, var11 * var9, var11 * var5, var2.d, (byte)0);
      }

      // $VF: synthetic method
      @Override
      public final qM a(qM var1) {
         return this.b(var1);
      }
   }
}
