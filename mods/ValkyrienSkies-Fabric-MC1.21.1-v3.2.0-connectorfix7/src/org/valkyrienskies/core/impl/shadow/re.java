package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public final class re<T extends kO<T>> implements Serializable {
   private static final long d = 20130224L;
   final T a;
   final T b;
   final T c;

   public re(T var1, T var2, T var3) {
      this.a = (T)var1;
      this.b = (T)var2;
      this.c = (T)var3;
   }

   private re(T[] var1) {
      if (var1.length != 3) {
         throw new oS(var1.length, 3);
      }

      this.a = (T)var1[0];
      this.b = (T)var1[1];
      this.c = (T)var1[2];
   }

   private re(T var1, T var2) {
      kO var3 = var2.q();
      this.a = (T)var1.q().c(var3);
      this.b = (T)var1.r().c(var3);
      this.c = (T)var2.r();
   }

   private re(T var1, re<T> var2) {
      this.a = (T)var1.c(var2.a);
      this.b = (T)var1.c(var2.b);
      this.c = (T)var1.c(var2.c);
   }

   public re(T var1, rs var2) {
      this.a = (T)var1.c(var2.i);
      this.b = (T)var1.c(var2.j);
      this.c = (T)var1.c(var2.k);
   }

   private re(double var1, re<T> var3) {
      this.a = var3.a.c(var1);
      this.b = var3.b.c(var1);
      this.c = var3.c.c(var1);
   }

   private re(T var1, re<T> var2, T var3, re<T> var4) {
      kO var5 = var1;
      this.a = (T)var5.a(var1, var2.a, var3, var4.a);
      this.b = (T)var5.a(var1, var2.b, var3, var4.b);
      this.c = (T)var5.a(var1, var2.c, var3, var4.c);
   }

   private re(T var1, rs var2, T var3, rs var4) {
      kO var5 = var1;
      this.a = (T)var5.a(var2.i, var1, var4.i, var3);
      this.b = (T)var5.a(var2.j, var1, var4.j, var3);
      this.c = (T)var5.a(var2.k, var1, var4.k, var3);
   }

   private re(double var1, re<T> var3, double var4, re<T> var6) {
      kO var7 = var3.a;
      this.a = (T)var7.a(1.0, var3.a, var4, var6.a);
      this.b = (T)var7.a(1.0, var3.b, var4, var6.b);
      this.c = (T)var7.a(1.0, var3.c, var4, var6.c);
   }

   private re(T var1, re<T> var2, T var3, re<T> var4, T var5, re<T> var6) {
      kO var7 = var1;
      this.a = (T)var7.a(var1, var2.a, var3, var4.a, var5, var6.a);
      this.b = (T)var7.a(var1, var2.b, var3, var4.b, var5, var6.b);
      this.c = (T)var7.a(var1, var2.c, var3, var4.c, var5, var6.c);
   }

   private re(T var1, rs var2, T var3, rs var4, T var5, rs var6) {
      kO var7 = var1;
      this.a = (T)var7.a(var2.i, var1, var4.i, var3, var6.i, var5);
      this.b = (T)var7.a(var2.j, var1, var4.j, var3, var6.j, var5);
      this.c = (T)var7.a(var2.k, var1, var4.k, var3, var6.k, var5);
   }

   private re(double var1, re<T> var3, double var4, re<T> var6, double var7, re<T> var9) {
      kO var10 = var3.a;
      this.a = (T)var10.a(var1, var3.a, var4, var6.a, var7, var9.a);
      this.b = (T)var10.a(var1, var3.b, var4, var6.b, var7, var9.b);
      this.c = (T)var10.a(var1, var3.c, var4, var6.c, var7, var9.c);
   }

   private re(T var1, re<T> var2, T var3, re<T> var4, T var5, re<T> var6, T var7, re<T> var8) {
      kO var9 = var1;
      this.a = (T)var9.a(var1, var2.a, var3, var4.a, var5, var6.a, var7, var8.a);
      this.b = (T)var9.a(var1, var2.b, var3, var4.b, var5, var6.b, var7, var8.b);
      this.c = (T)var9.a(var1, var2.c, var3, var4.c, var5, var6.c, var7, var8.c);
   }

   private re(T var1, rs var2, T var3, rs var4, T var5, rs var6, T var7, rs var8) {
      kO var9 = var1;
      this.a = (T)var9.a(var2.i, var1, var4.i, var3, var6.i, var5, var8.i, var7);
      this.b = (T)var9.a(var2.j, var1, var4.j, var3, var6.j, var5, var8.j, var7);
      this.c = (T)var9.a(var2.k, var1, var4.k, var3, var6.k, var5, var8.k, var7);
   }

   private re(double var1, re<T> var3, double var4, re<T> var6, double var7, re<T> var9, double var10, re<T> var12) {
      kO var13 = var3.a;
      this.a = (T)var13.a(var1, var3.a, var4, var6.a, var7, var9.a, var10, var12.a);
      this.b = (T)var13.a(var1, var3.b, var4, var6.b, var7, var9.b, var10, var12.b);
      this.c = (T)var13.a(var1, var3.c, var4, var6.c, var7, var9.c, var10, var12.c);
   }

   public final T a() {
      return this.a;
   }

   public final T b() {
      return this.b;
   }

   public final T c() {
      return this.c;
   }

   private T[] g() {
      kO[] var1;
      (var1 = Ch.a(this.a.c(), 3))[0] = this.a;
      var1[1] = this.b;
      var1[2] = this.c;
      return (T[])var1;
   }

   private rs h() {
      return new rs(this.a.d(), this.b.d(), this.c.d());
   }

   private T i() {
      return this.a.e().a(this.b.e()).a(this.c.e());
   }

   public final T d() {
      return this.a.c(this.a).a(this.b.c(this.b)).a(this.c.c(this.c)).k();
   }

   private T j() {
      return this.a.c(this.a).a(this.b.c(this.b)).a(this.c.c(this.c));
   }

   private T k() {
      kO var1 = this.a.e();
      kO var2 = this.b.e();
      kO var3 = this.c.e();
      if (var1.d() <= var2.d()) {
         return (T)(var2.d() <= var3.d() ? var3 : var2);
      } else {
         return (T)(var1.d() <= var3.d() ? var3 : var1);
      }
   }

   private T l() {
      return this.b.h(this.a);
   }

   private T m() {
      return this.c.d(this.d()).u();
   }

   private re<T> c(re<T> var1) {
      return new re<>(this.a.a(var1.a), this.b.a(var1.b), this.c.a(var1.c));
   }

   private re<T> a(rs var1) {
      return new re<>(this.a.a(var1.i), this.b.a(var1.j), this.c.a(var1.k));
   }

   private re<T> a(T var1, re<T> var2) {
      return new re<>(this.a.c().b(), this, (T)var1, var2);
   }

   private re<T> a(T var1, rs var2) {
      return new re<>(this.a.a((T)var1.c(var2.i)), this.b.a((T)var1.c(var2.j)), this.c.a((T)var1.c(var2.k)));
   }

   private re<T> a(double var1, re<T> var3) {
      return new re<>(1.0, this, var1, var3);
   }

   private re<T> a(double var1, rs var3) {
      return new re<>(this.a.a(var1 * var3.i), this.b.a(var1 * var3.j), this.c.a(var1 * var3.k));
   }

   private re<T> d(re<T> var1) {
      return new re<>(this.a.b(var1.a), this.b.b(var1.b), this.c.b(var1.c));
   }

   private re<T> b(rs var1) {
      return new re<>(this.a.b(var1.i), this.b.b(var1.j), this.c.b(var1.k));
   }

   private re<T> b(T var1, re<T> var2) {
      return new re<>(this.a.c().b(), this, (T)var1.a(), var2);
   }

   private re<T> b(T var1, rs var2) {
      return new re<>(this.a.b((T)var1.c(var2.i)), this.b.b((T)var1.c(var2.j)), this.c.b((T)var1.c(var2.k)));
   }

   private re<T> b(double var1, re<T> var3) {
      return new re<>(1.0, this, -var1, var3);
   }

   private re<T> b(double var1, rs var3) {
      return new re<>(this.a.b(var1 * var3.i), this.b.b(var1 * var3.j), this.c.b(var1 * var3.k));
   }

   public final re<T> e() {
      kO var1;
      if ((var1 = this.d()).d() == 0.0) {
         throw new oU(px.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
      } else {
         return this.a((T)var1.b());
      }
   }

   public final re<T> f() {
      double var1;
      if ((var1 = 0.6 * this.d().d()) == 0.0) {
         throw new oU(px.ZERO_NORM);
      } else if (BY.w(this.a.d()) <= var1) {
         kO var5 = this.b.c(this.b).a(this.c.c(this.c)).k().b();
         return new re<>((T)var5.c().a(), (T)var5.c(this.c), (T)((kO)var5.c(this.b)).a());
      } else if (BY.w(this.b.d()) <= var1) {
         kO var4 = this.a.c(this.a).a(this.c.c(this.c)).k().b();
         return new re<>((T)((kO)var4.c(this.c)).a(), (T)var4.c().a(), (T)var4.c(this.a));
      } else {
         kO var3 = this.a.c(this.a).a(this.b.c(this.b)).k().b();
         return new re<>((T)var3.c(this.b), (T)((kO)var3.c(this.a)).a(), (T)var3.c().a());
      }
   }

   private static <T extends kO<T>> T c(re<T> var0, re<T> var1) {
      kO var2;
      if ((var2 = var0.d().c(var1.d())).d() == 0.0) {
         throw new oU(px.ZERO_NORM);
      }

      re var5 = var1;
      kO var3 = var0.a(var5);
      double var4 = var2.d() * 0.9999;
      if (!(var3.d() < -var4) && !(var3.d() > var4)) {
         return (T)var3.d(var2).t();
      }

      var5 = var1;
      var0 = var0.b(var5);
      return (T)(var3.d() >= 0.0 ? var0.d().d(var2).u() : var0.d().d(var2).u().b(Math.PI).a());
   }

   private static <T extends kO<T>> T a(re<T> var0, rs var1) {
      kO var2;
      if ((var2 = var0.d().c(var1.e())).d() == 0.0) {
         throw new oU(px.ZERO_NORM);
      }

      rs var5 = var1;
      kO var3 = var0.c(var5);
      double var4 = var2.d() * 0.9999;
      if (!(var3.d() < -var4) && !(var3.d() > var4)) {
         return (T)var3.d(var2).t();
      }

      var5 = var1;
      var1 = var5;
      var0 = var0;
      var0 = new re(var0.a.a(var1.k, var0.b, -var1.j, var0.c), var0.b.a(var1.i, var0.c, -var1.k, var0.a), var0.c.a(var1.j, var0.a, -var1.i, var0.b));
      return (T)(var3.d() >= 0.0 ? var0.d().d(var2).u() : var0.d().d(var2).u().b(Math.PI).a());
   }

   private static <T extends kO<T>> T a(rs var0, re<T> var1) {
      rs var11 = var0;
      re var8 = var1;
      kO var2;
      if ((var2 = var1.d().c(var11.e())).d() == 0.0) {
         throw new oU(px.ZERO_NORM);
      }

      rs var5 = var11;
      kO var3 = var8.c(var5);
      double var6 = var2.d() * 0.9999;
      if (!(var3.d() < -var6) && !(var3.d() > var6)) {
         return (T)var3.d(var2).t();
      }

      var5 = var11;
      rs var12 = var5;
      re var9 = var8;
      re var10 = new re(var9.a.a(var12.k, var9.b, -var12.j, var9.c), var9.b.a(var12.i, var9.c, -var12.k, var9.a), var9.c.a(var12.j, var9.a, -var12.i, var9.b));
      return (T)(var3.d() >= 0.0 ? var10.d().d(var2).u() : var10.d().d(var2).u().b(Math.PI).a());
   }

   private re<T> n() {
      return new re<>(this.a.a(), this.b.a(), this.c.a());
   }

   private re<T> a(T var1) {
      return new re<>(this.a.c((T)var1), this.b.c((T)var1), this.c.c((T)var1));
   }

   private re<T> a(double var1) {
      return new re<>(this.a.c(var1), this.b.c(var1), this.c.c(var1));
   }

   private boolean o() {
      return Double.isNaN(this.a.d()) || Double.isNaN(this.b.d()) || Double.isNaN(this.c.d());
   }

   private boolean p() {
      return !this.o() && (Double.isInfinite(this.a.d()) || Double.isInfinite(this.b.d()) || Double.isInfinite(this.c.d()));
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof re) {
         re var2;
         return (var2 = (re)other).o() ? this.o() : this.a.equals(var2.a) && this.b.equals(var2.b) && this.c.equals(var2.c);
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return this.o() ? 409 : 311 * (107 * this.a.hashCode() + 83 * this.b.hashCode() + this.c.hashCode());
   }

   public final T a(re<T> var1) {
      return this.a.a(this.a, var1.a, this.b, var1.b, this.c, var1.c);
   }

   private T c(rs var1) {
      return this.a.a(var1.i, this.a, var1.j, this.b, var1.k, this.c);
   }

   public final re<T> b(re<T> var1) {
      return new re<>(this.a.a(this.b, var1.c, this.c.a(), var1.b), this.b.a(this.c, var1.a, this.a.a(), var1.c), this.c.a(this.a, var1.b, this.b.a(), var1.a));
   }

   private re<T> d(rs var1) {
      return new re<>(this.a.a(var1.k, this.b, -var1.j, this.c), this.b.a(var1.i, this.c, -var1.k, this.a), this.c.a(var1.j, this.a, -var1.i, this.b));
   }

   private T e(re<T> var1) {
      kO var2 = var1.a.b(this.a).e();
      kO var3 = var1.b.b(this.b).e();
      kO var4 = var1.c.b(this.c).e();
      return (T)var2.a(var3).a(var4);
   }

   private T e(rs var1) {
      kO var2 = this.a.b(var1.i).e();
      kO var3 = this.b.b(var1.j).e();
      kO var5 = this.c.b(var1.k).e();
      return (T)var2.a(var3).a(var5);
   }

   private T f(re<T> var1) {
      kO var2 = var1.a.b(this.a);
      kO var3 = var1.b.b(this.b);
      kO var4 = var1.c.b(this.c);
      return (T)var2.c(var2).a(var3.c(var3)).a(var4.c(var4)).k();
   }

   private T f(rs var1) {
      kO var2 = this.a.b(var1.i);
      kO var3 = this.b.b(var1.j);
      kO var5 = this.c.b(var1.k);
      return (T)var2.c(var2).a(var3.c(var3)).a(var5.c(var5)).k();
   }

   private T g(re<T> var1) {
      kO var2 = var1.a.b(this.a).e();
      kO var3 = var1.b.b(this.b).e();
      kO var4 = var1.c.b(this.c).e();
      if (var2.d() <= var3.d()) {
         return (T)(var3.d() <= var4.d() ? var4 : var3);
      } else {
         return (T)(var2.d() <= var4.d() ? var4 : var2);
      }
   }

   private T g(rs var1) {
      kO var2 = this.a.b(var1.i).e();
      kO var3 = this.b.b(var1.j).e();
      kO var5 = this.c.b(var1.k).e();
      if (var2.d() <= var3.d()) {
         return (T)(var3.d() <= var5.d() ? var5 : var3);
      } else {
         return (T)(var2.d() <= var5.d() ? var5 : var2);
      }
   }

   private T h(re<T> var1) {
      kO var2 = var1.a.b(this.a);
      kO var3 = var1.b.b(this.b);
      kO var4 = var1.c.b(this.c);
      return (T)var2.c(var2).a(var3.c(var3)).a(var4.c(var4));
   }

   private T h(rs var1) {
      kO var2 = this.a.b(var1.i);
      kO var3 = this.b.b(var1.j);
      kO var5 = this.c.b(var1.k);
      return (T)var2.c(var2).a(var3.c(var3)).a(var5.c(var5));
   }

   public static <T extends kO<T>> T a(re<T> var0, re<T> var1) {
      return (T)var0.a(var1);
   }

   private static <T extends kO<T>> T b(re<T> var0, rs var1) {
      return (T)var0.c(var1);
   }

   private static <T extends kO<T>> T b(rs var0, re<T> var1) {
      return (T)var1.c(var0);
   }

   public static <T extends kO<T>> re<T> b(re<T> var0, re<T> var1) {
      return var0.b(var1);
   }

   private static <T extends kO<T>> re<T> c(re<T> var0, rs var1) {
      return var0.d(var1);
   }

   private static <T extends kO<T>> re<T> c(rs var0, re<T> var1) {
      return new re<>(var1.a.a(var0.j, var1.c, -var0.k, var1.b), var1.b.a(var0.k, var1.a, -var0.i, var1.c), var1.c.a(var0.i, var1.b, -var0.j, var1.a));
   }

   private static <T extends kO<T>> T d(re<T> var0, re<T> var1) {
      var1 = var1;
      var0 = var0;
      kO var2 = var1.a.b(var0.a).e();
      kO var3 = var1.b.b(var0.b).e();
      kO var5 = var1.c.b(var0.c).e();
      return (T)var2.a(var3).a(var5);
   }

   private static <T extends kO<T>> T d(re<T> var0, rs var1) {
      return (T)var0.e(var1);
   }

   private static <T extends kO<T>> T d(rs var0, re<T> var1) {
      return (T)var1.e(var0);
   }

   private static <T extends kO<T>> T e(re<T> var0, re<T> var1) {
      var1 = var1;
      var0 = var0;
      kO var2 = var1.a.b(var0.a);
      kO var3 = var1.b.b(var0.b);
      kO var5 = var1.c.b(var0.c);
      return (T)var2.c(var2).a(var3.c(var3)).a(var5.c(var5)).k();
   }

   private static <T extends kO<T>> T e(re<T> var0, rs var1) {
      return (T)var0.f(var1);
   }

   private static <T extends kO<T>> T e(rs var0, re<T> var1) {
      return (T)var1.f(var0);
   }

   private static <T extends kO<T>> T f(re<T> var0, re<T> var1) {
      var1 = var1;
      var0 = var0;
      kO var2 = var1.a.b(var0.a).e();
      kO var3 = var1.b.b(var0.b).e();
      kO var5 = var1.c.b(var0.c).e();
      if (var2.d() <= var3.d()) {
         return (T)(var3.d() <= var5.d() ? var5 : var3);
      } else {
         return (T)(var2.d() <= var5.d() ? var5 : var2);
      }
   }

   private static <T extends kO<T>> T f(re<T> var0, rs var1) {
      return (T)var0.g(var1);
   }

   private static <T extends kO<T>> T f(rs var0, re<T> var1) {
      return (T)var1.g(var0);
   }

   private static <T extends kO<T>> T g(re<T> var0, re<T> var1) {
      var1 = var1;
      var0 = var0;
      kO var2 = var1.a.b(var0.a);
      kO var3 = var1.b.b(var0.b);
      kO var5 = var1.c.b(var0.c);
      return (T)var2.c(var2).a(var3.c(var3)).a(var5.c(var5));
   }

   private static <T extends kO<T>> T g(re<T> var0, rs var1) {
      return (T)var0.h(var1);
   }

   private static <T extends kO<T>> T g(rs var0, re<T> var1) {
      return (T)var1.h(var0);
   }

   @Override
   public final String toString() {
      return rt.a(Locale.getDefault()).a(this.h());
   }

   private String a(NumberFormat var1) {
      return new rt(var1).a(this.h());
   }
}
