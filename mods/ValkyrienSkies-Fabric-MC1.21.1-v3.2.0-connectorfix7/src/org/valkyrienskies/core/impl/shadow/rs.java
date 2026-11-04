package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class rs implements Serializable, qO<rc> {
   public static final rs a = new rs(0.0, 0.0, 0.0);
   public static final rs b = new rs(1.0, 0.0, 0.0);
   public static final rs c = new rs(-1.0, 0.0, 0.0);
   public static final rs d = new rs(0.0, 1.0, 0.0);
   public static final rs e = new rs(0.0, -1.0, 0.0);
   public static final rs f = new rs(0.0, 0.0, 1.0);
   public static final rs g = new rs(0.0, 0.0, -1.0);
   public static final rs h = new rs(Double.NaN, Double.NaN, Double.NaN);
   private static rs l;
   private static rs m;
   private static final long n = 1313493323784566947L;
   public final double i;
   public final double j;
   final double k;

   public rs(double var1, double var3, double var5) {
      this.i = var1;
      this.j = var3;
      this.k = var5;
   }

   private rs(double[] var1) {
      if (var1.length != 3) {
         throw new oS(var1.length, 3);
      }

      this.i = var1[0];
      this.j = var1[1];
      this.k = var1[2];
   }

   private rs(double var1, double var3) {
      double var5 = BY.o(var3);
      this.i = BY.o(var1) * var5;
      this.j = BY.n(var1) * var5;
      this.k = BY.n(var3);
   }

   public rs(double var1, rs var3) {
      this.i = var1 * var3.i;
      this.j = var1 * var3.j;
      this.k = var1 * var3.k;
   }

   public rs(double var1, rs var3, double var4, rs var6) {
      this.i = Ch.a(var1, var3.i, var4, var6.i);
      this.j = Ch.a(var1, var3.j, var4, var6.j);
      this.k = Ch.a(var1, var3.k, var4, var6.k);
   }

   public rs(double var1, rs var3, double var4, rs var6, double var7, rs var9) {
      this.i = Ch.a(var1, var3.i, var4, var6.i, var7, var9.i);
      this.j = Ch.a(var1, var3.j, var4, var6.j, var7, var9.j);
      this.k = Ch.a(var1, var3.k, var4, var6.k, var7, var9.k);
   }

   private rs(double var1, rs var3, double var4, rs var6, double var7, rs var9, double var10, rs var12) {
      this.i = Ch.a(var1, var3.i, var4, var6.i, var7, var9.i, var10, var12.i);
      this.j = Ch.a(var1, var3.j, var4, var6.j, var7, var9.j, var10, var12.j);
      this.k = Ch.a(var1, var3.k, var4, var6.k, var7, var9.k, var10, var12.k);
   }

   public final double k() {
      return this.i;
   }

   public final double l() {
      return this.j;
   }

   public final double m() {
      return this.k;
   }

   private double[] r() {
      return new double[]{this.i, this.j, this.k};
   }

   @Override
   public final qN a() {
      return rc.a.a();
   }

   private static rs s() {
      return a;
   }

   @Override
   public final double d() {
      return BY.w(this.i) + BY.w(this.j) + BY.w(this.k);
   }

   @Override
   public final double e() {
      return BY.a(this.i * this.i + this.j * this.j + this.k * this.k);
   }

   @Override
   public final double f() {
      return this.i * this.i + this.j * this.j + this.k * this.k;
   }

   @Override
   public final double g() {
      return BY.e(BY.e(BY.w(this.i), BY.w(this.j)), BY.w(this.k));
   }

   public final double n() {
      return BY.b(this.j, this.i);
   }

   private double t() {
      return BY.r(this.k / this.e());
   }

   public final rs g(qO<rc> var1) {
      var1 = var1;
      return new rs(this.i + var1.i, this.j + var1.j, this.k + var1.k);
   }

   private rs c(double var1, qO<rc> var3) {
      return new rs(1.0, this, var1, (rs)var3);
   }

   public final rs h(qO<rc> var1) {
      var1 = var1;
      return new rs(this.i - var1.i, this.j - var1.j, this.k - var1.k);
   }

   private rs d(double var1, qO<rc> var3) {
      return new rs(1.0, this, -var1, (rs)var3);
   }

   public final rs o() {
      double var1;
      if ((var1 = this.e()) == 0.0) {
         throw new oU(px.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
      } else {
         return this.b(1.0 / var1);
      }
   }

   public final rs p() {
      double var1;
      if ((var1 = 0.6 * this.e()) == 0.0) {
         throw new oU(px.ZERO_NORM);
      } else if (BY.w(this.i) <= var1) {
         double var6 = 1.0 / BY.a(this.j * this.j + this.k * this.k);
         return new rs(0.0, var6 * this.k, -var6 * this.j);
      } else if (BY.w(this.j) <= var1) {
         double var5 = 1.0 / BY.a(this.i * this.i + this.k * this.k);
         return new rs(-var5 * this.k, 0.0, var5 * this.i);
      } else {
         double var3 = 1.0 / BY.a(this.i * this.i + this.j * this.j);
         return new rs(var3 * this.j, -var3 * this.i, 0.0);
      }
   }

   public static double a(rs var0, rs var1) {
      double var2;
      if ((var2 = var0.e() * var1.e()) == 0.0) {
         throw new oU(px.ZERO_NORM);
      }

      double var4 = var0.f(var1);
      double var6 = var2 * 0.9999;
      if (!(var4 < -var6) && !(var4 > var6)) {
         return BY.s(var4 / var2);
      }

      var1 = var1;
      var0 = var0.b(var1);
      return var4 >= 0.0 ? BY.r(var0.e() / var2) : Math.PI - BY.r(var0.e() / var2);
   }

   public final rs q() {
      return new rs(-this.i, -this.j, -this.k);
   }

   private rs b(double var1) {
      return new rs(var1 * this.i, var1 * this.j, var1 * this.k);
   }

   @Override
   public final boolean b() {
      return Double.isNaN(this.i) || Double.isNaN(this.j) || Double.isNaN(this.k);
   }

   @Override
   public final boolean j() {
      return !this.b() && (Double.isInfinite(this.i) || Double.isInfinite(this.j) || Double.isInfinite(this.k));
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof rs) {
         rs var2;
         return (var2 = (rs)other).b() ? this.b() : this.i == var2.i && this.j == var2.j && this.k == var2.k;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.b() ? 642 : 643 * (164 * Ci.a(this.i) + 3 * Ci.a(this.j) + Ci.a(this.k));
   }

   @Override
   public final double f(qO<rc> var1) {
      var1 = var1;
      return Ch.a(this.i, var1.i, this.j, var1.j, this.k, var1.k);
   }

   public final rs b(qO<rc> var1) {
      var1 = var1;
      return new rs(Ch.a(this.j, var1.k, -this.k, var1.j), Ch.a(this.k, var1.i, -this.i, var1.k), Ch.a(this.i, var1.j, -this.j, var1.i));
   }

   @Override
   public final double a(qO<rc> var1) {
      rs var9;
      double var3 = BY.w((var9 = var1).i - this.i);
      double var5 = BY.w(var9.j - this.j);
      double var7 = BY.w(var9.k - this.k);
      return var3 + var5 + var7;
   }

   @Override
   public final double c(qO<rc> var1) {
      return this.a((qM<rc>)var1);
   }

   @Override
   public final double a(qM<rc> var1) {
      rs var9;
      double var3 = (var9 = (rs)var1).i - this.i;
      double var5 = var9.j - this.j;
      double var7 = var9.k - this.k;
      return BY.a(var3 * var3 + var5 * var5 + var7 * var7);
   }

   @Override
   public final double d(qO<rc> var1) {
      rs var9;
      double var3 = BY.w((var9 = (rs)var1).i - this.i);
      double var5 = BY.w(var9.j - this.j);
      double var7 = BY.w(var9.k - this.k);
      return BY.e(BY.e(var3, var5), var7);
   }

   @Override
   public final double e(qO<rc> var1) {
      rs var9;
      double var3 = (var9 = (rs)var1).i - this.i;
      double var5 = var9.j - this.j;
      double var7 = var9.k - this.k;
      return var3 * var3 + var5 * var5 + var7 * var7;
   }

   public static double b(rs var0, rs var1) {
      return var0.f(var1);
   }

   public static rs c(rs var0, rs var1) {
      return var0.b(var1);
   }

   private static double e(rs var0, rs var1) {
      var1 = var1;
      var0 = var0;
      rs var13;
      double var5 = BY.w((var13 = var1).i - var0.i);
      double var7 = BY.w(var13.j - var0.j);
      double var9 = BY.w(var13.k - var0.k);
      return var5 + var7 + var9;
   }

   public static double d(rs var0, rs var1) {
      var1 = var1;
      return var0.a((qM<rc>)var1);
   }

   private static double f(rs var0, rs var1) {
      return var0.d(var1);
   }

   private static double g(rs var0, rs var1) {
      return var0.e(var1);
   }

   @Override
   public String toString() {
      return rt.a(Locale.getDefault()).a(this);
   }

   @Override
   public final String a(NumberFormat var1) {
      return new rt(var1).a(this);
   }

   // $VF: synthetic method
   @Override
   public final qO a(double var1) {
      return this.b(var1);
   }

   // $VF: synthetic method
   @Override
   public final qO i() {
      return this.o();
   }

   // $VF: synthetic method
   @Override
   public final qO h() {
      return this.q();
   }

   // $VF: synthetic method
   @Override
   public final qO b(double var1, qO var3) {
      qO var2 = var3;
      double var5 = var1;
      rs var7 = this;
      return new rs(1.0, var7, -var5, (rs)var2);
   }

   // $VF: synthetic method
   @Override
   public final qO b(qO var1) {
      return this.h(var1);
   }

   // $VF: synthetic method
   @Override
   public final qO a(double var1, qO var3) {
      qO var2 = var3;
      double var5 = var1;
      rs var7 = this;
      return new rs(1.0, var7, var5, (rs)var2);
   }

   // $VF: synthetic method
   @Override
   public final qO a(qO var1) {
      return this.g(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final qO c() {
      return a;
   }

   static {
      new rs(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
      new rs(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
   }
}
