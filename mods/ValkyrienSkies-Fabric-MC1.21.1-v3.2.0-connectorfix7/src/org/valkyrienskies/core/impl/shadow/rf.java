package org.valkyrienskies.core.impl.shadow;

public final class rf implements rU<rc, qU> {
   private static final double d = 1.0E-10;
   rs a;
   rs b;
   final double c;

   public rf(rs var1, rs var2, double var3) {
      rs var5 = var2;
      var2 = var1;
      rf var11 = this;
      double var9;
      if ((var9 = (var5 = var5.h(var2)).f()) == 0.0) {
         throw new oV(px.ZERO_NORM);
      }

      var11.a = new rs(1.0 / BY.a(var9), var5);
      var11.b = new rs(1.0, var2, -var2.f(var5) / var9, var5);
      this.c = var3;
   }

   private rf(rf var1) {
      this.a = var1.a;
      this.b = var1.b;
      this.c = var1.c;
   }

   @Deprecated
   private rf(rs var1, rs var2) {
      this(var1, var2, 1.0E-10);
   }

   private void a(rs var1, rs var2) {
      double var4;
      if ((var4 = (var2 = var2.h(var1)).f()) == 0.0) {
         throw new oV(px.ZERO_NORM);
      }

      this.a = new rs(1.0 / BY.a(var4), var2);
      this.b = new rs(1.0, var1, -var1.f(var2) / var4, var2);
   }

   public final double a() {
      return this.c;
   }

   private rf c() {
      rf var1;
      (var1 = new rf(this)).a = var1.a.q();
      return var1;
   }

   public final rs b() {
      return this.a;
   }

   private rs d() {
      return this.b;
   }

   public final double a(rs var1) {
      return var1.h(this.b).f(this.a);
   }

   private rs a(double var1) {
      return new rs(1.0, this.b, var1, this.a);
   }

   private qZ a(qO<rc> var1) {
      return this.a((qM<rc>)var1);
   }

   private rs b(qO<qU> var1) {
      return this.b((qM<qU>)var1);
   }

   public final qZ a(qM<rc> var1) {
      return new qZ(this.a((rs)var1));
   }

   public final rs b(qM<qU> var1) {
      qZ var2;
      return this.a((var2 = (qZ)var1).d);
   }

   private boolean b(rf var1) {
      double var2;
      return ((var2 = rs.a(this.a, var1.a)) < this.c || var2 > Math.PI - this.c) && this.b(var1.b);
   }

   public final boolean b(rs var1) {
      return this.c(var1) < this.c;
   }

   private double c(rs var1) {
      var1 = var1.h(this.b);
      return new rs(1.0, var1, -var1.f(this.a), this.a).e();
   }

   private double c(rf var1) {
      rs var2;
      double var3;
      return (var3 = (var2 = rs.c(this.a, var1.a)).e()) < Cq.b ? this.c(var1.b) : BY.w(var1.b.h(this.b).f(var2) / var3);
   }

   private rs d(rf var1) {
      double var2 = this.a.f(var1.a);
      double var4;
      if ((var4 = 1.0 - var2 * var2) < Cq.a) {
         return this.b;
      }

      rs var6;
      double var7 = (var6 = var1.b.h(this.b)).f(this.a);
      double var9 = var6.f(var1.a);
      return new rs(1.0, this.b, (var7 - var9 * var2) / var4, this.a);
   }

   public final rs a(rf var1) {
      rf var3 = var1;
      rf var2 = this;
      double var5 = this.a.f(var3.a);
      double var7;
      rs var10000;
      if ((var7 = 1.0 - var5 * var5) < Cq.a) {
         var10000 = var2.b;
      } else {
         rs var4;
         double var10 = (var4 = var3.b.h(var2.b)).f(var2.a);
         double var12 = var4.f(var3.a);
         var10000 = new rs(1.0, var2.b, (var10 - var12 * var5) / var7, var2.a);
      }

      rs var14 = var10000;
      return var1.b(var14) ? var14 : null;
   }

   private rq e() {
      return new rq(this, new qW(this.c));
   }

   // $VF: synthetic method
   @Override
   public final qM c(qM var1) {
      return this.b(var1);
   }

   // $VF: synthetic method
   @Override
   public final qM d(qM var1) {
      return this.a(var1);
   }
}
