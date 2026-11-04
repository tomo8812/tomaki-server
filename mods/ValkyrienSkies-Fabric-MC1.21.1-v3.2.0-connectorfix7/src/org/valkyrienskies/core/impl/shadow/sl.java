package org.valkyrienskies.core.impl.shadow;

public final class sl implements rU<sq, sj>, rV<sq> {
   rs a;
   rs b;
   rs c;
   final double d;

   public sl(rs var1, double var2) {
      this.b(var1);
      this.d = var2;
   }

   public sl(sp var1, sp var2, double var3) {
      this.b(var1.c.b(var2.c));
      this.d = var3;
   }

   private sl(rs var1, rs var2, rs var3, double var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   private sl(sl var1) {
      this(var1.a, var1.b, var1.c, var1.d);
   }

   public final sl a() {
      return new sl(this);
   }

   private void b(rs var1) {
      this.a = var1.o();
      this.b = var1.p();
      this.c = rs.c(var1, this.b).o();
   }

   private void i() {
      this.c = this.c.q();
      this.a = this.a.q();
   }

   public final sl c() {
      return new sl(this.a.q(), this.b, this.c.q(), this.d);
   }

   @Override
   public final qM<sq> b(qM<sq> var1) {
      return this.e(this.f(var1));
   }

   @Override
   public final double b() {
      return this.d;
   }

   private si f(qM<sq> var1) {
      sp var2;
      return new si(this.a((var2 = (sp)var1).c));
   }

   public final double a(rs var1) {
      return Math.PI + BY.b(-var1.f(this.c), -var1.f(this.b));
   }

   public final sp e(qM<sj> var1) {
      si var2;
      return new sp(this.a((var2 = (si)var1).b));
   }

   public final rs a(double var1) {
      return new rs(BY.o(var1), this.b, BY.n(var1), this.c);
   }

   private rs j() {
      return this.b;
   }

   private rs k() {
      return this.c;
   }

   public final rs d() {
      return this.a;
   }

   public final sf a(sl var1) {
      double var2 = this.a(var1.a);
      return new sf(var2 - (Math.PI / 2), var2 + (Math.PI / 2), this.d);
   }

   public final ss h() {
      return new ss(this, new sg(this.d));
   }

   private sr l() {
      return new sr(this.d);
   }

   @Override
   public final double a(qM<sq> var1) {
      sp var3;
      rs var2 = (var3 = (sp)var1).c;
      return rs.a(this.a, var2) - (Math.PI / 2);
   }

   private double c(rs var1) {
      return rs.a(this.a, var1) - (Math.PI / 2);
   }

   @Override
   public final boolean a(rV<sq> var1) {
      var1 = var1;
      return rs.b(this.a, var1.a) >= 0.0;
   }

   private static sc<sq, sj> a(rk var0) {
      return new sl.a(var0);
   }

   // $VF: synthetic method
   @Override
   public final rK e() {
      sl var1 = this;
      return new sr(var1.d);
   }

   // $VF: synthetic method
   @Override
   public final rL f() {
      return this.h();
   }

   // $VF: synthetic method
   @Override
   public final rV g() {
      return this.a();
   }

   // $VF: synthetic method
   @Override
   public final qM c(qM var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   @Override
   public final qM d(qM var1) {
      return this.f(var1);
   }

   // $VF: synthetic method
   static rs b(sl var0) {
      return var0.a;
   }

   // $VF: synthetic method
   static rs c(sl var0) {
      return var0.b;
   }

   // $VF: synthetic method
   static rs d(sl var0) {
      return var0.c;
   }

   // $VF: synthetic method
   static double e(sl var0) {
      return var0.d;
   }

   // $VF: synthetic method
   sl(rs var1, rs var2, rs var3, double var4, byte var6) {
      this(var1, var2, var3, var4);
   }

   static class a implements sc<sq, sj> {
      private final rk a;

      a(rk var1) {
         this.a = var1;
      }

      private sp b(qM<sq> var1) {
         sp var2;
         return new sp(this.a.a((var2 = (sp)var1).c));
      }

      private sl b(rV<sq> var1) {
         var1 = var1;
         return new sl(this.a.a(var1.a), this.a.a(var1.b), this.a.a(var1.c), var1.d, (byte)0);
      }

      @Override
      public final sb<sj> a(sb<sj> var1, rV<sq> var2, rV<sq> var3) {
         return var1;
      }

      // $VF: synthetic method
      @Override
      public final rV a(rV var1) {
         sl var2 = var1;
         sl.a var4 = this;
         var2 = var2;
         return new sl(var4.a.a(var2.a), var4.a.a(var2.b), var4.a.a(var2.c), var2.d, (byte)0);
      }

      // $VF: synthetic method
      @Override
      public final qM a(qM var1) {
         qM var2 = var1;
         sl.a var3 = this;
         return new sp(var3.a.a((var1 = (sp)var2).c));
      }
   }
}
