package org.valkyrienskies.core.impl.shadow;

public final class ri implements rU<rc, rv>, rV<rc> {
   private static final double g = 1.0E-10;
   double a;
   rs b;
   rs c;
   rs d;
   rs e;
   final double f;

   public ri(rs var1, double var2) {
      this.d(var1);
      this.f = var2;
      this.a = 0.0;
      this.h();
   }

   public ri(rs var1, rs var2, double var3) {
      this.d(var2);
      this.f = var3;
      this.a = -var1.f(this.e);
      this.h();
   }

   public ri(rs var1, rs var2, rs var3, double var4) {
      this(var1, var2.h(var1).b(var3.h(var1)), var4);
   }

   @Deprecated
   private ri(rs var1) {
      this(var1, 1.0E-10);
   }

   @Deprecated
   private ri(rs var1, rs var2) {
      this(var1, var2, 1.0E-10);
   }

   @Deprecated
   private ri(rs var1, rs var2, rs var3) {
      this(var1, var2, var3, 1.0E-10);
   }

   private ri(ri var1) {
      this.a = var1.a;
      this.b = var1.b;
      this.c = var1.c;
      this.d = var1.d;
      this.e = var1.e;
      this.f = var1.f;
   }

   public final ri a() {
      return new ri(this);
   }

   private void a(rs var1, rs var2) {
      this.d(var2);
      this.a = -var1.f(this.e);
      this.h();
   }

   private void c(ri var1) {
      this.a = var1.a;
      this.b = var1.b;
      this.c = var1.c;
      this.d = var1.d;
      this.e = var1.e;
   }

   private void d(rs var1) {
      double var2;
      if ((var2 = var1.e()) < 1.0E-10) {
         throw new oU(px.ZERO_NORM);
      }

      this.e = new rs(1.0 / var2, var1);
   }

   private void h() {
      this.b = new rs(-this.a, this.e);
      this.c = this.e.p();
      this.d = rs.c(this.e, this.c);
   }

   public final rs c() {
      return this.b;
   }

   public final rs d() {
      return this.e;
   }

   private rs i() {
      return this.c;
   }

   private rs j() {
      return this.d;
   }

   @Override
   public final qM<rc> b(qM<rc> var1) {
      rB var2 = this.e(var1);
      return this.f(var2);
   }

   @Override
   public final double b() {
      return this.f;
   }

   private void k() {
      rs var1 = this.c;
      this.c = this.d;
      this.d = var1;
      this.e = this.e.q();
      this.a = -this.a;
   }

   public final rB a(qO<rc> var1) {
      return this.e((qM<rc>)var1);
   }

   public final rs a(qO<rv> var1) {
      return this.f(var1);
   }

   public final rB e(qM<rc> var1) {
      var1 = var1;
      return new rB(var1.f(this.c), var1.f(this.d));
   }

   public final rs f(qM<rv> var1) {
      var1 = var1;
      return new rs(var1.c, this.c, var1.d, this.d, -this.a, this.e);
   }

   private rs a(rB var1, double var2) {
      return new rs(var1.c, this.c, var1.d, this.d, var2 - this.a, this.e);
   }

   private boolean d(ri var1) {
      double var2;
      return (var2 = rs.a(this.e, var1.e)) < 1.0E-10 && BY.w(this.a - var1.a) < this.f || var2 > 3.141592653489793 && BY.w(this.a + var1.a) < this.f;
   }

   public final ri a(rs var1, rk var2) {
      rs var3 = this.b.h(var1);
      ri var4;
      (var4 = new ri(var1.g(var2.a(var3)), var2.a(this.e), this.f)).c = var2.a(this.c);
      var4.d = var2.a(this.d);
      return var4;
   }

   public final ri b(rs var1) {
      ri var2;
      (var2 = new ri(this.b.g(var1), this.e, this.f)).c = this.c;
      var2.d = this.d;
      return var2;
   }

   public final rs a(rf var1) {
      rs var2 = var1.a;
      double var3;
      if (BY.w(var3 = this.e.f(var2)) < 1.0E-10) {
         return null;
      }

      rs var8 = var1.b(qZ.a);
      double var6 = -(this.a + this.e.f(var8)) / var3;
      return new rs(1.0, var8, var6, var2);
   }

   public final rf a(ri var1) {
      rs var2;
      if ((var2 = rs.c(this.e, var1.e)).e() < this.f) {
         return null;
      }

      ri var4 = new ri(var2, this.f);
      ri var3 = var1;
      var1 = this;
      rs var5 = this.e;
      double var7 = this.e.i;
      var5 = var1.e;
      double var9 = var1.e.j;
      var5 = var1.e;
      double var11 = var1.e.k;
      double var13 = var1.a;
      var5 = var3.e;
      double var15 = var3.e.i;
      var5 = var3.e;
      double var17 = var3.e.j;
      var5 = var3.e;
      double var19 = var3.e.k;
      double var21 = var3.a;
      var5 = var4.e;
      double var23 = var4.e.i;
      var5 = var4.e;
      double var25 = var4.e.j;
      var5 = var4.e;
      double var27 = var4.e.k;
      double var29 = var4.a;
      double var31 = var17 * var27 - var25 * var19;
      double var33 = var19 * var23 - var27 * var15;
      double var35 = var15 * var25 - var23 * var17;
      double var37;
      rs var10000;
      if (BY.w(var37 = var7 * var31 + var9 * var33 + var11 * var35) < 1.0E-10) {
         var10000 = null;
      } else {
         double var39 = 1.0 / var37;
         var10000 = new rs(
            (-var31 * var13 - (var11 * var25 - var27 * var9) * var21 - (var19 * var9 - var11 * var17) * var29) * var39,
            (-var33 * var13 - (var27 * var7 - var11 * var23) * var21 - (var11 * var15 - var19 * var7) * var29) * var39,
            (-var35 * var13 - (var9 * var23 - var25 * var7) * var21 - (var17 * var7 - var9 * var15) * var29) * var39
         );
      }

      rs var42 = var10000;
      return new rf(var42, var42.g(var2), this.f);
   }

   private static rs a(ri var0, ri var1, ri var2) {
      rs var23 = var0.e;
      double var3 = var0.e.i;
      var23 = var0.e;
      double var5 = var0.e.j;
      var23 = var0.e;
      double var7 = var0.e.k;
      double var9 = var0.a;
      var23 = var1.e;
      double var11 = var1.e.i;
      var23 = var1.e;
      double var13 = var1.e.j;
      var23 = var1.e;
      double var15 = var1.e.k;
      double var17 = var1.a;
      var23 = var2.e;
      double var19 = var2.e.i;
      var23 = var2.e;
      double var21 = var2.e.j;
      var23 = var2.e;
      double var45 = var2.e.k;
      double var25 = var2.a;
      double var27 = var13 * var45 - var21 * var15;
      double var29 = var15 * var19 - var45 * var11;
      double var31 = var11 * var21 - var19 * var13;
      double var33;
      if (BY.w(var33 = var3 * var27 + var5 * var29 + var7 * var31) < 1.0E-10) {
         return null;
      }

      double var35 = 1.0 / var33;
      return new rs(
         (-var27 * var9 - (var7 * var21 - var45 * var5) * var17 - (var15 * var5 - var7 * var13) * var25) * var35,
         (-var29 * var9 - (var45 * var3 - var7 * var19) * var17 - (var7 * var11 - var15 * var3) * var25) * var35,
         (-var31 * var9 - (var5 * var19 - var21 * var3) * var17 - (var13 * var3 - var5 * var11) * var25) * var35
      );
   }

   private rr l() {
      return new rr(this, new ry(this.f));
   }

   private rj m() {
      return new rj(this.f);
   }

   public final boolean c(rs var1) {
      rs var2 = var1;
      return BY.w(this.a((qM<rc>)var2)) < this.f;
   }

   public final double b(ri var1) {
      return this.a + (this.a((rV<rc>)var1) ? -var1.a : var1.a);
   }

   private double e(qO<rc> var1) {
      return this.a((qM<rc>)var1);
   }

   @Override
   public final double a(qM<rc> var1) {
      return ((rs)var1).f(this.e) + this.a;
   }

   @Override
   public final boolean a(rV<rc> var1) {
      return ((ri)var1).e.f(this.e) > 0.0;
   }

   // $VF: synthetic method
   @Override
   public final rK e() {
      ri var1 = this;
      return new rj(var1.f);
   }

   // $VF: synthetic method
   @Override
   public final rL f() {
      ri var1 = this;
      return new rr(var1, new ry(var1.f));
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
}
