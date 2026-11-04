package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Iterator;

public final class tl extends tG implements Serializable {
   private static double a;
   private static final long b = 8772222695580707260L;
   private final Cm c;
   private final int d;
   private final double e;

   public tl() {
      this(0, 1.0E-12);
   }

   private tl(int var1) {
      this(var1, 1.0E-12);
   }

   private tl(int var1, double var2) {
      this.d = var1;
      this.c = new Cm(0.0);
      this.e = 1.0E-12;
   }

   private tl(tl var1, int var2) {
      this.d = var1.d + var2;
      this.c = new Cm(var1.c);
      this.e = var1.e;
   }

   private tl(int var1, int var2) {
      this(var1, var2, 1.0E-12);
   }

   private tl(int var1, int var2, double var3) {
      this.d = var1;
      this.c = new Cm(var2, 0.0);
      this.e = 1.0E-12;
   }

   private tl(double[] var1) {
      this(var1, 1.0E-12);
   }

   private tl(double[] var1, double var2) {
      this.d = var1.length;
      this.c = new Cm(0.0);
      this.e = 1.0E-12;

      for (int var7 = 0; var7 < var1.length; var7++) {
         double var5 = var1[var7];
         if (!this.k(var5)) {
            this.c.a(var7, var5);
         }
      }
   }

   private tl(Double[] var1) {
      this(var1, 1.0E-12);
   }

   private tl(Double[] var1, double var2) {
      this.d = var1.length;
      this.c = new Cm(0.0);
      this.e = 1.0E-12;

      for (int var7 = 0; var7 < var1.length; var7++) {
         double var5 = var1[var7];
         if (!this.k(var5)) {
            this.c.a(var7, var5);
         }
      }
   }

   private tl(tl var1) {
      this.d = var1.d;
      this.c = new Cm(var1.c);
      this.e = var1.e;
   }

   private tl(tu var1) {
      this.d = var1.f();
      this.c = new Cm(0.0);
      this.e = 1.0E-12;

      for (int var2 = 0; var2 < this.d; var2++) {
         double var3 = var1.a(var2);
         if (!this.k(var3)) {
            this.c.a(var2, var3);
         }
      }
   }

   private Cm m() {
      return this.c;
   }

   private boolean k(double var1) {
      return BY.w(var1) < this.e;
   }

   @Override
   public final tu l(tu var1) {
      this.b(var1.f());
      if (var1 instanceof tl) {
         tl var2 = var1;
         var1 = this;
         this.b(var2.d);
         Cm var3 = var1.c;
         var3 = var2.c;
         boolean var10;
         tl var4 = (var10 = var1.c.a > var2.c.a) ? var1.n() : var2.n();
         Cm.a var5 = var10 ? var2.c.a() : var1.c.a();
         Cm var7 = var10 ? var1.c : var2.c;

         while (var5.a()) {
            var5.d();
            int var8 = var5.b();
            if (var7.b(var8)) {
               var4.a(var8, var7.a(var8) + var5.c());
            } else {
               var4.a(var8, var5.c());
            }
         }

         return var4;
      } else {
         return super.l(var1);
      }
   }

   private tl b(tl var1) {
      this.b(var1.d);
      Cm var2 = this.c;
      var2 = var1.c;
      boolean var7;
      tl var3 = (var7 = this.c.a > var1.c.a) ? this.n() : var1.n();
      Cm.a var4 = var7 ? var1.c.a() : this.c.a();
      Cm var5 = var7 ? this.c : var1.c;

      while (var4.a()) {
         var4.d();
         int var8 = var4.b();
         if (var5.b(var8)) {
            var3.a(var8, var5.a(var8) + var4.c());
         } else {
            var3.a(var8, var4.c());
         }
      }

      return var3;
   }

   private tl c(tl var1) {
      tl var2 = new tl(this, var1.d);
      Cm.a var3 = var1.c.a();

      while (var3.a()) {
         var3.d();
         var2.a(var3.b() + this.d, var3.c());
      }

      return var2;
   }

   private tl a(tu var1) {
      if (var1 instanceof tl) {
         return this.c((tl)var1);
      }

      tl var2 = new tl(this, var1.f());

      for (int var3 = 0; var3 < var1.f(); var3++) {
         var2.a(var3 + this.d, var1.a(var3));
      }

      return var2;
   }

   private tl l(double var1) {
      tl var3;
      (var3 = new tl(this, 1)).a(this.d, var1);
      return var3;
   }

   private tl n() {
      return new tl(this);
   }

   @Deprecated
   private double d(tl var1) {
      return this.b(var1);
   }

   private tl o(tu var1) {
      this.b(var1.f());
      tl var2 = new tl(this);
      int var3 = this.d;

      for (int var4 = 0; var4 < var3; var4++) {
         var2.a(var4, this.a(var4) / var1.a(var4));
      }

      return var2;
   }

   private tl p(tu var1) {
      this.b(var1.f());
      tl var2 = new tl(this);
      Cm.a var3 = this.c.a();

      while (var3.a()) {
         var3.d();
         var2.a(var3.b(), var3.c() * var1.a(var3.b()));
      }

      return var2;
   }

   private tl c(int var1, int var2) {
      this.c(var1);
      if (var2 < 0) {
         throw new pj(px.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, var2);
      }

      this.c(var1 + var2 - 1);
      tl var3 = new tl(var2);
      var2 = var1 + var2;
      Cm.a var4 = this.c.a();

      while (var4.a()) {
         var4.d();
         int var5;
         if ((var5 = var4.b()) >= var1 && var5 < var2) {
            var3.a(var5 - var1, var4.c());
         }
      }

      return var3;
   }

   @Override
   public final int f() {
      return this.d;
   }

   private double e(tl var1) {
      this.b(var1.d);
      Cm.a var2 = this.c.a();
      double var3 = 0.0;

      while (var2.a()) {
         var2.d();
         int var5 = var2.b();
         double var6 = var2.c() - var1.a(var5);
         var3 += var6 * var6;
      }

      var2 = var1.c.a();

      while (var2.a()) {
         var2.d();
         int var9 = var2.b();
         if (!this.c.b(var9)) {
            double var10 = var2.c();
            var3 += var10 * var10;
         }
      }

      return BY.a(var3);
   }

   @Override
   public final double c(tu var1) {
      this.b(var1.f());
      return var1 instanceof tl ? this.e((tl)var1) : super.c(var1);
   }

   @Override
   public final double a(int var1) {
      this.c(var1);
      return this.c.a(var1);
   }

   private double f(tl var1) {
      this.b(var1.d);
      double var2 = 0.0;
      Cm.a var4 = this.c.a();

      while (var4.a()) {
         var4.d();
         double var5 = BY.w(var4.c() - var1.a(var4.b()));
         var2 += var5;
      }

      var4 = var1.c.a();

      while (var4.a()) {
         var4.d();
         int var9 = var4.b();
         if (!this.c.b(var9)) {
            double var6 = BY.w(var4.c());
            var2 += BY.w(var6);
         }
      }

      return var2;
   }

   @Override
   public final double d(tu var1) {
      this.b(var1.f());
      return var1 instanceof tl ? this.f((tl)var1) : super.d(var1);
   }

   private double g(tl var1) {
      this.b(var1.d);
      double var2 = 0.0;
      Cm.a var4 = this.c.a();

      while (var4.a()) {
         var4.d();
         double var5;
         if ((var5 = BY.w(var4.c() - var1.a(var4.b()))) > var2) {
            var2 = var5;
         }
      }

      var4 = var1.c.a();

      while (var4.a()) {
         var4.d();
         int var8 = var4.b();
         if (!this.c.b(var8) && var4.c() > var2) {
            var2 = var4.c();
         }
      }

      return var2;
   }

   @Override
   public final double e(tu var1) {
      this.b(var1.f());
      return var1 instanceof tl ? this.g((tl)var1) : super.e(var1);
   }

   @Override
   public final boolean i() {
      boolean var1 = false;
      Cm.a var2 = this.c.a();

      while (var2.a()) {
         var2.d();
         double var3;
         if (Double.isNaN(var3 = var2.c())) {
            return false;
         }

         if (Double.isInfinite(var3)) {
            var1 = true;
         }
      }

      return var1;
   }

   @Override
   public final boolean h() {
      Cm.a var1 = this.c.a();

      while (var1.a()) {
         var1.d();
         if (Double.isNaN(var1.c())) {
            return true;
         }
      }

      return false;
   }

   private tl m(double var1) {
      return this.n().n(var1);
   }

   private tl n(double var1) {
      for (int var3 = 0; var3 < this.d; var3++) {
         this.a(var3, this.a(var3) + var1);
      }

      return this;
   }

   @Override
   public final void a(int var1, double var2) {
      this.c(var1);
      if (!this.k(var2)) {
         this.c.a(var1, var2);
      } else {
         if (this.c.b(var1)) {
            this.c.c(var1);
         }
      }
   }

   @Override
   public final void a(int var1, tu var2) {
      this.c(0);
      this.c(0 + var2.f() - 1);

      for (int var3 = 0; var3 < var2.f(); var3++) {
         this.a(var3, var2.a(var3));
      }
   }

   @Override
   public final void f(double var1) {
      for (int var3 = 0; var3 < this.d; var3++) {
         this.a(var3, 0.0);
      }
   }

   private tl h(tl var1) {
      this.b(var1.d);
      tl var2 = this.n();
      Cm.a var4 = var1.c.a();

      while (var4.a()) {
         var4.d();
         int var3 = var4.b();
         if (this.c.b(var3)) {
            var2.a(var3, this.c.a(var3) - var4.c());
         } else {
            var2.a(var3, -var4.c());
         }
      }

      return var2;
   }

   @Override
   public final tu k(tu var1) {
      this.b(var1.f());
      return var1 instanceof tl ? this.h((tl)var1) : super.k(var1);
   }

   private tl o() {
      tl var1;
      tl var2;
      double var3 = (var2 = var1 = this.n()).c();
      if (var2.k(var3)) {
         throw new oU(px.ZERO_NORM);
      }

      Cm.a var5 = var2.c.a();

      while (var5.a()) {
         var5.d();
         var2.c.a(var5.b(), var5.c() / var3);
      }

      return var1;
   }

   @Override
   public final void d_() {
      double var1 = this.c();
      if (this.k(var1)) {
         throw new oU(px.ZERO_NORM);
      }

      Cm.a var3 = this.c.a();

      while (var3.a()) {
         var3.d();
         this.c.a(var3.b(), var3.c() / var1);
      }
   }

   @Override
   public final double[] g() {
      double[] var1 = new double[this.d];

      for (Cm.a var2 = this.c.a(); var2.a(); var1[var2.b()] = var2.c()) {
         var2.d();
      }

      return var1;
   }

   @Override
   public final int hashCode() {
      long var2 = Double.doubleToLongBits(this.e);
      int var1 = 31 + (int)(var2 ^ var2 >>> 32);
      var1 = var1 * 31 + this.d;
      Cm.a var4 = this.c.a();

      while (var4.a()) {
         var4.d();
         var2 = Double.doubleToLongBits(var4.c());
         var1 = var1 * 31 + (int)(var2 ^ var2 >> 32);
      }

      return var1;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof tl)) {
         return false;
      }

      obj = (tl)obj;
      if (this.d != ((tl)obj).d) {
         return false;
      }

      if (Double.doubleToLongBits(this.e) != Double.doubleToLongBits(((tl)obj).e)) {
         return false;
      }

      Cm.a var2 = this.c.a();

      while (var2.a()) {
         var2.d();
         if (Double.doubleToLongBits(((tu)obj).a(var2.b())) != Double.doubleToLongBits(var2.c())) {
            return false;
         }
      }

      var2 = ((tl)obj).c.a();

      while (var2.a()) {
         var2.d();
         if (Double.doubleToLongBits(var2.c()) != Double.doubleToLongBits(this.a(var2.b()))) {
            return false;
         }
      }

      return true;
   }

   private double p() {
      Cm var1 = this.c;
      return (double)this.c.a / this.d;
   }

   @Override
   public final Iterator<tu.a> e_() {
      return new tl.b();
   }

   // $VF: synthetic method
   @Override
   public final tu k() {
      tl var1;
      tl var2;
      double var4 = (var2 = var1 = this.n()).c();
      if (var2.k(var4)) {
         throw new oU(px.ZERO_NORM);
      }

      Cm.a var3 = var2.c.a();

      while (var3.a()) {
         var3.d();
         var2.c.a(var3.b(), var3.c() / var4);
      }

      return var1;
   }

   // $VF: synthetic method
   @Override
   public final tu i(tu var1) {
      return this.p(var1);
   }

   // $VF: synthetic method
   @Override
   public final tu j(tu var1) {
      return this.o(var1);
   }

   // $VF: synthetic method
   @Override
   public final tu j() {
      return this.n();
   }

   // $VF: synthetic method
   @Override
   public final tu a(double var1) {
      return this.n(var1);
   }

   // $VF: synthetic method
   @Override
   public final tu g(double var1) {
      double var4 = var1;
      return this.n().n(var4);
   }

   // $VF: synthetic method
   @Override
   public final tu a(int var1, int var2) {
      return this.c(var1, var2);
   }

   // $VF: synthetic method
   @Override
   public final tu e(double var1) {
      return this.l(var1);
   }

   // $VF: synthetic method
   @Override
   public final tu g(tu var1) {
      return this.a(var1);
   }

   protected class a extends tu.a {
      private final Cm.a b;

      protected a(Cm.a var2) {
         this.b = var2;
      }

      @Override
      public final double a() {
         return this.b.c();
      }

      @Override
      public final void a(double var1) {
         tl.this.c.a(this.b.b(), var1);
      }

      @Override
      public final int b() {
         return this.b.b();
      }
   }

   protected class b implements Iterator<tu.a> {
      private final Cm.a a = tl.this.c.a();
      private final tl.a b = tl.this.new a(this.a);

      @Override
      public final boolean hasNext() {
         return this.a.a();
      }

      private tu.a a() {
         this.a.d();
         return this.b;
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException("Not supported");
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         return this.a();
      }
   }
}
