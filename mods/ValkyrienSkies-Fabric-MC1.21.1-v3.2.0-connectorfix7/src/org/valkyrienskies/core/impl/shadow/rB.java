package org.valkyrienskies.core.impl.shadow;

import java.text.NumberFormat;
import java.util.Locale;

public class rB implements qO<rv> {
   public static final rB a = new rB(0.0, 0.0);
   public static final rB b = new rB(Double.NaN, Double.NaN);
   private static rB e;
   private static rB f;
   private static final long g = 266938651998679754L;
   public final double c;
   public final double d;

   public rB(double var1, double var3) {
      this.c = var1;
      this.d = var3;
   }

   private rB(double[] var1) {
      if (var1.length != 2) {
         throw new oS(var1.length, 2);
      }

      this.c = var1[0];
      this.d = var1[1];
   }

   private rB(double var1, rB var3) {
      this.c = var1 * var3.c;
      this.d = var1 * var3.d;
   }

   public rB(double var1, rB var3, double var4, rB var6) {
      this.c = 0.5 * var3.c + 0.5 * var6.c;
      this.d = 0.5 * var3.d + 0.5 * var6.d;
   }

   private rB(double var1, rB var3, double var4, rB var6, double var7, rB var9) {
      this.c = var1 * var3.c + var4 * var6.c + var7 * var9.c;
      this.d = var1 * var3.d + var4 * var6.d + var7 * var9.d;
   }

   private rB(double var1, rB var3, double var4, rB var6, double var7, rB var9, double var10, rB var12) {
      this.c = var1 * var3.c + var4 * var6.c + var7 * var9.c + var10 * var12.c;
      this.d = var1 * var3.d + var4 * var6.d + var7 * var9.d + var10 * var12.d;
   }

   public final double k() {
      return this.c;
   }

   public final double l() {
      return this.d;
   }

   private double[] m() {
      return new double[]{this.c, this.d};
   }

   @Override
   public final qN a() {
      return rv.a.a();
   }

   private static rB n() {
      return a;
   }

   @Override
   public final double d() {
      return BY.w(this.c) + BY.w(this.d);
   }

   @Override
   public final double e() {
      return BY.a(this.c * this.c + this.d * this.d);
   }

   @Override
   public final double f() {
      return this.c * this.c + this.d * this.d;
   }

   @Override
   public final double g() {
      return BY.e(BY.w(this.c), BY.w(this.d));
   }

   private rB h(qO<rv> var1) {
      var1 = var1;
      return new rB(this.c + var1.c, this.d + var1.d);
   }

   private rB c(double var1, qO<rv> var3) {
      var3 = var3;
      return new rB(this.c + var1 * var3.c, this.d + var1 * var3.d);
   }

   public final rB g(qO<rv> var1) {
      var1 = var1;
      return new rB(this.c - var1.c, this.d - var1.d);
   }

   private rB d(double var1, qO<rv> var3) {
      var3 = var3;
      return new rB(this.c - var1 * var3.c, this.d - var1 * var3.d);
   }

   private rB o() {
      double var1;
      if ((var1 = this.e()) == 0.0) {
         throw new oU(px.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
      } else {
         return this.b(1.0 / var1);
      }
   }

   public static double a(rB var0, rB var1) {
      double var2;
      if ((var2 = var0.e() * var1.e()) == 0.0) {
         throw new oU(px.ZERO_NORM);
      }

      double var4 = var0.f(var1);
      double var6 = var2 * 0.9999;
      if (!(var4 < -var6) && !(var4 > var6)) {
         return BY.s(var4 / var2);
      }

      double var8 = BY.w(Ch.a(var0.c, var1.d, -var0.d, var1.c));
      return var4 >= 0.0 ? BY.r(var8 / var2) : Math.PI - BY.r(var8 / var2);
   }

   private rB p() {
      return new rB(-this.c, -this.d);
   }

   private rB b(double var1) {
      return new rB(var1 * this.c, var1 * this.d);
   }

   @Override
   public final boolean b() {
      return Double.isNaN(this.c) || Double.isNaN(this.d);
   }

   @Override
   public final boolean j() {
      return !this.b() && (Double.isInfinite(this.c) || Double.isInfinite(this.d));
   }

   @Override
   public final double a(qO<rv> var1) {
      rB var7;
      double var3 = BY.w((var7 = (rB)var1).c - this.c);
      double var5 = BY.w(var7.d - this.d);
      return var3 + var5;
   }

   @Override
   public final double c(qO<rv> var1) {
      return this.a((qM<rv>)var1);
   }

   @Override
   public final double a(qM<rv> var1) {
      rB var7;
      double var3 = (var7 = (rB)var1).c - this.c;
      double var5 = var7.d - this.d;
      return BY.a(var3 * var3 + var5 * var5);
   }

   @Override
   public final double d(qO<rv> var1) {
      rB var7;
      double var3 = BY.w((var7 = (rB)var1).c - this.c);
      double var5 = BY.w(var7.d - this.d);
      return BY.e(var3, var5);
   }

   @Override
   public final double e(qO<rv> var1) {
      rB var7;
      double var3 = (var7 = (rB)var1).c - this.c;
      double var5 = var7.d - this.d;
      return var3 * var3 + var5 * var5;
   }

   @Override
   public final double f(qO<rv> var1) {
      var1 = var1;
      return Ch.a(this.c, var1.c, this.d, var1.d);
   }

   public final double b(rB var1, rB var2) {
      double var3 = var2.c - var1.c;
      double var5 = this.d - var1.d;
      double var7 = this.c - var1.c;
      double var9 = var2.d - var1.d;
      return Ch.a(var3, var5, -var7, var9);
   }

   public static double c(rB var0, rB var1) {
      var1 = var1;
      return var0.a(var1);
   }

   private static double d(rB var0, rB var1) {
      return var0.d(var1);
   }

   private static double e(rB var0, rB var1) {
      return var0.e(var1);
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof rB) {
         rB var2;
         return (var2 = (rB)other).b() ? this.b() : this.c == var2.c && this.d == var2.d;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.b() ? 542 : 122 * (76 * Ci.a(this.c) + Ci.a(this.d));
   }

   @Override
   public String toString() {
      Locale var1 = Locale.getDefault();
      return new rC(BS.a(var1)).a(this);
   }

   @Override
   public final String a(NumberFormat var1) {
      return new rC(var1).a(this);
   }

   // $VF: synthetic method
   @Override
   public final qO a(double var1) {
      return this.b(var1);
   }

   // $VF: synthetic method
   @Override
   public final qO i() {
      rB var1 = this;
      double var2;
      if ((var2 = this.e()) == 0.0) {
         throw new oU(px.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
      } else {
         return var1.b(1.0 / var2);
      }
   }

   // $VF: synthetic method
   @Override
   public final qO h() {
      rB var1 = this;
      return new rB(-var1.c, -var1.d);
   }

   // $VF: synthetic method
   @Override
   public final qO b(double var1, qO var3) {
      rB var2 = var3;
      double var5 = var1;
      rB var7 = this;
      var2 = var2;
      return new rB(var7.c - var5 * var2.c, var7.d - var5 * var2.d);
   }

   // $VF: synthetic method
   @Override
   public final qO b(qO var1) {
      return this.g(var1);
   }

   // $VF: synthetic method
   @Override
   public final qO a(double var1, qO var3) {
      rB var2 = var3;
      double var5 = var1;
      rB var7 = this;
      var2 = var2;
      return new rB(var7.c + var5 * var2.c, var7.d + var5 * var2.d);
   }

   // $VF: synthetic method
   @Override
   public final qO a(qO var1) {
      rB var2 = var1;
      var1 = this;
      var2 = var2;
      return new rB(var1.c + var2.c, var1.d + var2.d);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final qO c() {
      return a;
   }

   static {
      new rB(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
      new rB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
   }
}
