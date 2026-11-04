package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class nQ implements Serializable, kN<nQ> {
   private static nQ e = new nQ(0.0, 1.0);
   private static nQ f = new nQ(Double.NaN, Double.NaN);
   private static nQ g = new nQ(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
   public static final nQ a = new nQ(1.0, 0.0);
   public static final nQ b = new nQ(0.0, 0.0);
   private static final long h = -6195664516687396620L;
   public final double c;
   public final double d;
   private final transient boolean i;
   private final transient boolean j;

   private nQ(double var1) {
      this(var1, 0.0);
   }

   public nQ(double var1, double var3) {
      this.d = var1;
      this.c = var3;
      this.i = Double.isNaN(var1) || Double.isNaN(var3);
      this.j = !this.i && (Double.isInfinite(var1) || Double.isInfinite(var3));
   }

   public final double d() {
      if (this.i) {
         return Double.NaN;
      }

      if (this.j) {
         return Double.POSITIVE_INFINITY;
      }

      if (BY.w(this.d) < BY.w(this.c)) {
         if (this.c == 0.0) {
            return BY.w(this.d);
         }

         double var3 = this.d / this.c;
         return BY.w(this.c) * BY.a(1.0 + var3 * var3);
      } else {
         if (this.d == 0.0) {
            return BY.w(this.c);
         }

         double var1 = this.c / this.d;
         return BY.w(this.d) * BY.a(1.0 + var1 * var1);
      }
   }

   public final nQ a(nQ var1) {
      Ci.a(var1);
      return !this.i && !var1.i ? a(this.d + var1.d, this.c + var1.c) : f;
   }

   private nQ a(double var1) {
      return !this.i && !Double.isNaN(var1) ? a(this.d + var1, this.c) : f;
   }

   private nQ h() {
      return this.i ? f : a(this.d, -this.c);
   }

   public final nQ b(nQ var1) {
      Ci.a(var1);
      if (!this.i && !var1.i) {
         double var2 = var1.d;
         double var4 = var1.c;
         if (var2 == 0.0 && var4 == 0.0) {
            return f;
         } else if (var1.j && !this.j) {
            return b;
         } else if (BY.w(var2) < BY.w(var4)) {
            double var10 = var2 / var4;
            double var11 = var2 * var10 + var4;
            return a((this.d * var10 + this.c) / var11, (this.c * var10 - this.d) / var11);
         } else {
            double var6 = var4 / var2;
            double var8 = var4 * var6 + var2;
            return a((this.c * var6 + this.d) / var8, (this.c - this.d * var6) / var8);
         }
      } else {
         return f;
      }
   }

   private nQ b(double var1) {
      if (this.i || Double.isNaN(var1)) {
         return f;
      } else if (var1 == 0.0) {
         return f;
      } else if (!Double.isInfinite(var1)) {
         return a(this.d / var1, this.c / var1);
      } else {
         return !this.j ? b : f;
      }
   }

   private nQ i() {
      if (this.i) {
         return f;
      } else if (this.d == 0.0 && this.c == 0.0) {
         return g;
      } else if (this.j) {
         return b;
      } else if (BY.w(this.d) < BY.w(this.c)) {
         double var5 = this.d / this.c;
         double var6;
         return a((var6 = 1.0 / (this.d * var5 + this.c)) * var5, -var6);
      } else {
         double var1 = this.c / this.d;
         double var3;
         return a(var3 = 1.0 / (this.c * var1 + this.d), -var3 * var1);
      }
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof nQ) {
         nQ var2;
         return (var2 = (nQ)other).i ? this.i : Ci.a(this.d, var2.d) && Ci.a(this.c, var2.c);
      } else {
         return false;
      }
   }

   private static boolean a(nQ var0, nQ var1, int var2) {
      return Cq.b(var0.d, var1.d, 1) && Cq.b(var0.c, var1.c, 1);
   }

   private static boolean a(nQ var0, nQ var1) {
      boolean var2 = true;
      var1 = var1;
      nQ var3 = var0;
      return Cq.b(var0.d, var1.d, 1) && Cq.b(var3.c, var1.c, 1);
   }

   private static boolean a(nQ var0, nQ var1, double var2) {
      return Cq.b(var0.d, var1.d, var2) && Cq.b(var0.c, var1.c, var2);
   }

   private static boolean b(nQ var0, nQ var1, double var2) {
      return Cq.c(var0.d, var1.d, var2) && Cq.c(var0.c, var1.c, var2);
   }

   @Override
   public int hashCode() {
      return this.i ? 7 : 37 * (17 * Ci.a(this.c) + Ci.a(this.d));
   }

   public final double e() {
      return this.c;
   }

   public final double f() {
      return this.d;
   }

   private boolean j() {
      return this.i;
   }

   private boolean k() {
      return this.j;
   }

   public final nQ c(nQ var1) {
      Ci.a(var1);
      if (this.i || var1.i) {
         return f;
      } else {
         return !Double.isInfinite(this.d) && !Double.isInfinite(this.c) && !Double.isInfinite(var1.d) && !Double.isInfinite(var1.c)
            ? a(this.d * var1.d - this.c * var1.c, this.d * var1.c + this.c * var1.d)
            : g;
      }
   }

   private nQ b(int var1) {
      if (this.i) {
         return f;
      } else {
         return !Double.isInfinite(this.d) && !Double.isInfinite(this.c) ? a(this.d * var1, this.c * var1) : g;
      }
   }

   private nQ c(double var1) {
      if (this.i || Double.isNaN(var1)) {
         return f;
      } else {
         return !Double.isInfinite(this.d) && !Double.isInfinite(this.c) && !Double.isInfinite(var1) ? a(this.d * var1, this.c * var1) : g;
      }
   }

   private nQ l() {
      return this.i ? f : a(-this.d, -this.c);
   }

   public final nQ d(nQ var1) {
      Ci.a(var1);
      return !this.i && !var1.i ? a(this.d - var1.d, this.c - var1.c) : f;
   }

   private nQ d(double var1) {
      return !this.i && !Double.isNaN(var1) ? a(this.d - var1, this.c) : f;
   }

   private nQ m() {
      return this.i ? f : this.a(this.v().c(e)).s().c(e.l());
   }

   private nQ n() {
      return this.i ? f : this.v().a(this.c(e)).s().c(e.l());
   }

   private nQ o() {
      return this.i ? f : this.a(e).b(e.d(this)).s().c(e.b(a(2.0, 0.0)));
   }

   private nQ p() {
      return this.i ? f : a(BY.o(this.d) * BY.b(this.c), -BY.n(this.d) * BY.c(this.c));
   }

   private nQ q() {
      return this.i ? f : a(BY.b(this.d) * BY.o(this.c), BY.c(this.d) * BY.n(this.c));
   }

   private nQ r() {
      double var1;
      return this.i ? f : a((var1 = BY.i(this.d)) * BY.o(this.c), var1 * BY.n(this.c));
   }

   private nQ s() {
      return this.i ? f : a(BY.k(this.d()), BY.b(this.c, this.d));
   }

   private nQ e(nQ var1) {
      Ci.a(var1);
      return this.s().c(var1).r();
   }

   private nQ e(double var1) {
      nQ var10000 = this.s();
      double var4 = var1;
      nQ var6 = var10000;
      return (var10000.i || Double.isNaN(var4)
            ? f
            : (!Double.isInfinite(var6.d) && !Double.isInfinite(var6.c) && !Double.isInfinite(var4) ? a(var6.d * var4, var6.c * var4) : g))
         .r();
   }

   private nQ t() {
      return this.i ? f : a(BY.n(this.d) * BY.b(this.c), BY.o(this.d) * BY.c(this.c));
   }

   private nQ u() {
      return this.i ? f : a(BY.c(this.d) * BY.o(this.c), BY.b(this.d) * BY.n(this.c));
   }

   public final nQ g() {
      if (this.i) {
         return f;
      }

      if (this.d == 0.0 && this.c == 0.0) {
         return a(0.0, 0.0);
      }

      double var1 = BY.a((BY.w(this.d) + this.d()) / 2.0);
      return this.d >= 0.0 ? a(var1, this.c / (var1 * 2.0)) : a(BY.w(this.c) / (var1 * 2.0), BY.h(1.0, this.c) * var1);
   }

   private nQ v() {
      return a(1.0, 0.0).d(this.c(this)).g();
   }

   private nQ w() {
      if (this.i || Double.isInfinite(this.d)) {
         return f;
      }

      if (this.c > 20.0) {
         return a(0.0, 1.0);
      }

      if (this.c < -20.0) {
         return a(0.0, -1.0);
      }

      double var1 = 2.0 * this.d;
      double var3 = 2.0 * this.c;
      double var5 = BY.o(var1) + BY.b(var3);
      return a(BY.n(var1) / var5, BY.c(var3) / var5);
   }

   private nQ x() {
      if (this.i || Double.isInfinite(this.c)) {
         return f;
      }

      if (this.d > 20.0) {
         return a(1.0, 0.0);
      }

      if (this.d < -20.0) {
         return a(-1.0, 0.0);
      }

      double var1 = 2.0 * this.d;
      double var3 = 2.0 * this.c;
      double var5 = BY.b(var1) + BY.o(var3);
      return a(BY.c(var1) / var5, BY.n(var3) / var5);
   }

   private double y() {
      return BY.b(this.c, this.d);
   }

   private List<nQ> c(int var1) {
      if (var1 <= 0) {
         throw new pj(px.CANNOT_COMPUTE_NTH_ROOT_FOR_NEGATIVE_N, var1);
      }

      ArrayList var2 = new ArrayList();
      if (this.i) {
         var2.add(f);
         return var2;
      }

      if (this.j) {
         var2.add(g);
         return var2;
      }

      double var3 = BY.a(this.d(), 1.0 / var1);
      nQ var5 = this;
      double var16 = BY.b(this.c, var5.d) / var1;
      double var7 = (Math.PI * 2) / var1;
      double var9 = var16;

      for (int var17 = 0; var17 < var1; var17++) {
         double var12 = var3 * BY.o(var9);
         double var14 = var3 * BY.n(var9);
         var2.add(a(var12, var14));
         var9 += var7;
      }

      return var2;
   }

   private static nQ a(double var0, double var2) {
      return new nQ(var0, var2);
   }

   private static nQ b(double var0, double var2) {
      return !Double.isNaN(var0) && !Double.isNaN(var2) ? new nQ(var0, var2) : f;
   }

   private static nQ f(double var0) {
      return Double.isNaN(var0) ? f : new nQ(var0);
   }

   private nQ z() {
      return a(this.d, this.c);
   }

   private static nR A() {
      return nR.a.a();
   }

   @Override
   public String toString() {
      return "(" + this.d + ", " + this.c + ")";
   }

   // $VF: synthetic method
   @Override
   public final kM c() {
      return nR.a.a();
   }

   // $VF: synthetic method
   @Override
   public final kN b() {
      nQ var1 = this;
      if (this.i) {
         return f;
      } else if (var1.d == 0.0 && var1.c == 0.0) {
         return g;
      } else if (var1.j) {
         return b;
      } else if (BY.w(var1.d) < BY.w(var1.c)) {
         double var6 = var1.d / var1.c;
         double var7;
         return a((var7 = 1.0 / (var1.d * var6 + var1.c)) * var6, -var7);
      } else {
         double var2 = var1.c / var1.d;
         double var4;
         return a(var4 = 1.0 / (var1.c * var2 + var1.d), -var4 * var2);
      }
   }

   // $VF: synthetic method
   @Override
   public final kN d(Object var1) {
      return this.b((nQ)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN c(Object var1) {
      return this.c((nQ)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a(int var1) {
      int var2 = var1;
      nQ var3 = this;
      if (this.i) {
         return f;
      } else {
         return !Double.isInfinite(var3.d) && !Double.isInfinite(var3.c) ? a(var3.d * var2, var3.c * var2) : g;
      }
   }

   // $VF: synthetic method
   @Override
   public final kN a() {
      return this.l();
   }

   // $VF: synthetic method
   @Override
   public final kN b(Object var1) {
      return this.d((nQ)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN a(Object var1) {
      return this.a((nQ)var1);
   }
}
