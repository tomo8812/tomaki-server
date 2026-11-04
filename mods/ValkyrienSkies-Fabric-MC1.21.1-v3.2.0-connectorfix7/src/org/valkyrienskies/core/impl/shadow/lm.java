package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class lm implements Serializable, kO<lm> {
   private static final long a = 20131025L;
   private double b;
   private final Map<Integer, Double> c;

   private lm(double var1, Map<Integer, Double> var3) {
      this.b = var1;
      this.c = new HashMap<>();
      if (var3 != null) {
         this.c.putAll(var3);
      }
   }

   private lm(double var1, double var3, Map<Integer, Double> var5) {
      this.b = var1;
      this.c = new HashMap<>();
      if (var5 != null) {
         for (Entry var2 : var5.entrySet()) {
            this.c.put((Integer)var2.getKey(), var3 * (Double)var2.getValue());
         }
      }
   }

   public static lm h(double var0) {
      return new lm(var0, Collections.emptyMap());
   }

   private static lm a(int var0, double var1) {
      return new lm(var1, Collections.singletonMap(var0, 1.0));
   }

   private int C() {
      return this.c.size();
   }

   private double e(int var1) {
      Double var2;
      return (var2 = this.c.get(var1)) == null ? 0.0 : var2;
   }

   private double D() {
      return this.b;
   }

   @Override
   public final double d() {
      return this.b;
   }

   private lm a(lm var1) {
      lm var2 = new lm(this.b + var1.b, this.c);
      Iterator var6 = var1.c.entrySet().iterator();

      while (var6.hasNext()) {
         Entry var3;
         int var4 = (Integer)(var3 = (Entry)var6.next()).getKey();
         Double var5;
         if ((var5 = var2.c.get(var4)) == null) {
            var2.c.put(var4, (Double)var3.getValue());
         } else {
            var2.c.put(var4, var5 + (Double)var3.getValue());
         }
      }

      return var2;
   }

   private void b(lm var1) {
      this.b = this.b + var1.b;
      Iterator var5 = var1.c.entrySet().iterator();

      while (var5.hasNext()) {
         Entry var2;
         int var3 = (Integer)(var2 = (Entry)var5.next()).getKey();
         Double var4;
         if ((var4 = this.c.get(var3)) == null) {
            this.c.put(var3, (Double)var2.getValue());
         } else {
            this.c.put(var3, var4 + (Double)var2.getValue());
         }
      }
   }

   private lm i(double var1) {
      return new lm(this.b + var1, this.c);
   }

   private lm c(lm var1) {
      lm var2 = new lm(this.b - var1.b, this.c);
      Iterator var6 = var1.c.entrySet().iterator();

      while (var6.hasNext()) {
         Entry var3;
         int var4 = (Integer)(var3 = (Entry)var6.next()).getKey();
         Double var5;
         if ((var5 = var2.c.get(var4)) == null) {
            var2.c.put(var4, -(Double)var3.getValue());
         } else {
            var2.c.put(var4, var5 - (Double)var3.getValue());
         }
      }

      return var2;
   }

   private lm j(double var1) {
      return new lm(this.b - var1, this.c);
   }

   private lm d(lm var1) {
      lm var2 = new lm(this.b * var1.b, Collections.emptyMap());

      for (Entry var4 : this.c.entrySet()) {
         var2.c.put((Integer)var4.getKey(), var1.b * (Double)var4.getValue());
      }

      Iterator var7 = var1.c.entrySet().iterator();

      while (var7.hasNext()) {
         Entry var8;
         int var6 = (Integer)(var8 = (Entry)var7.next()).getKey();
         Double var5;
         if ((var5 = var2.c.get(var6)) == null) {
            var2.c.put(var6, this.b * (Double)var8.getValue());
         } else {
            var2.c.put(var6, var5 + this.b * (Double)var8.getValue());
         }
      }

      return var2;
   }

   private void e(lm var1) {
      for (Entry var3 : this.c.entrySet()) {
         this.c.put((Integer)var3.getKey(), var1.b * (Double)var3.getValue());
      }

      Iterator var6 = var1.c.entrySet().iterator();

      while (var6.hasNext()) {
         Entry var7;
         int var4 = (Integer)(var7 = (Entry)var6.next()).getKey();
         Double var5;
         if ((var5 = this.c.get(var4)) == null) {
            this.c.put(var4, this.b * (Double)var7.getValue());
         } else {
            this.c.put(var4, var5 + this.b * (Double)var7.getValue());
         }
      }

      this.b = this.b * var1.b;
   }

   private lm k(double var1) {
      return new lm(this.b * var1, var1, this.c);
   }

   private lm f(int var1) {
      return new lm(this.b * var1, var1, this.c);
   }

   private lm f(lm var1) {
      lm var2 = new lm(this.b / var1.b, Collections.emptyMap());

      for (Entry var4 : this.c.entrySet()) {
         var2.c.put((Integer)var4.getKey(), (Double)var4.getValue() / var1.b);
      }

      Iterator var7 = var1.c.entrySet().iterator();

      while (var7.hasNext()) {
         Entry var8;
         int var5 = (Integer)(var8 = (Entry)var7.next()).getKey();
         Double var6;
         if ((var6 = var2.c.get(var5)) == null) {
            var2.c.put(var5, -var2.b / var1.b * (Double)var8.getValue());
         } else {
            var2.c.put(var5, var6 - var2.b / var1.b * (Double)var8.getValue());
         }
      }

      return var2;
   }

   private lm l(double var1) {
      return new lm(this.b / var1, 1.0 / var1, this.c);
   }

   private lm E() {
      return new lm(-this.b, -1.0, this.c);
   }

   @Override
   public final kM<lm> c() {
      return new kM<lm>() {
         private static lm d() {
            return lm.h(0.0);
         }

         private static lm e() {
            return lm.h(1.0);
         }

         @Override
         public final Class<? extends kN<lm>> c() {
            return lm.class;
         }

         // $VF: synthetic method
         @Override
         public final kN b() {
            return lm.h(1.0);
         }

         // $VF: synthetic method
         @Override
         public final kN a() {
            return lm.h(0.0);
         }
      };
   }

   private lm m(double var1) {
      return new lm(BY.g(this.b, var1), this.c);
   }

   private lm g(lm var1) {
      double var2 = BY.g(this.b, var1.b);
      double var4 = BY.A((this.b - var2) / var1.b);
      return this.c(var1.k(var4));
   }

   private lm F() {
      return Double.doubleToLongBits(this.b) < 0L ? this.E() : this;
   }

   private lm G() {
      return h(BY.z(this.b));
   }

   private lm H() {
      return h(BY.y(this.b));
   }

   private lm I() {
      return h(BY.A(this.b));
   }

   @Override
   public final long i() {
      return BY.B(this.b);
   }

   private lm J() {
      return h(BY.h(this.b));
   }

   private lm h(lm var1) {
      long var2 = Double.doubleToLongBits(this.b);
      long var4 = Double.doubleToLongBits(var1.b);
      return (var2 < 0L || var4 < 0L) && (var2 >= 0L || var4 >= 0L) ? this.E() : this;
   }

   private lm n(double var1) {
      long var3 = Double.doubleToLongBits(this.b);
      long var5 = Double.doubleToLongBits(var1);
      return (var3 < 0L || var5 < 0L) && (var3 >= 0L || var5 >= 0L) ? this.E() : this;
   }

   private lm g(int var1) {
      lm var2 = new lm(BY.b(this.b, var1), Collections.emptyMap());

      for (Entry var4 : this.c.entrySet()) {
         var2.c.put((Integer)var4.getKey(), BY.b((Double)var4.getValue(), var1));
      }

      return var2;
   }

   private lm i(lm var1) {
      if (Double.isInfinite(this.b) || Double.isInfinite(var1.b)) {
         return h(Double.POSITIVE_INFINITY);
      }

      if (!Double.isNaN(this.b) && !Double.isNaN(var1.b)) {
         int var2 = BY.C(this.b);
         int var3 = BY.C(var1.b);
         if (var2 > var3 + 27) {
            return this.F();
         }

         if (var3 > var2 + 27) {
            return var1.F();
         }

         var2 = (var2 + var3) / 2;
         lm var6 = this.g(-var2);
         var1 = var1.g(-var2);
         return var6.d(var6).a(var1.d(var1)).L().g(var2);
      } else {
         return h(Double.NaN);
      }
   }

   private static lm a(lm var0, lm var1) {
      return var0.i(var1);
   }

   private lm K() {
      return new lm(1.0 / this.b, -1.0 / (this.b * this.b), this.c);
   }

   private lm L() {
      double var1 = BY.a(this.b);
      return new lm(var1, 0.5 / var1, this.c);
   }

   private lm M() {
      double var1 = BY.t(this.b);
      return new lm(var1, 1.0 / (var1 * 3.0 * var1), this.c);
   }

   private lm h(int var1) {
      if (var1 == 2) {
         return this.L();
      }

      if (var1 == 3) {
         return this.M();
      }

      double var2 = BY.a(this.b, 1.0 / var1);
      return new lm(var2, 1.0 / (var1 * BY.a(var2, var1 - 1)), this.c);
   }

   private lm o(double var1) {
      return new lm(BY.a(this.b, var1), var1 * BY.a(this.b, var1 - 1.0), this.c);
   }

   private lm i(int var1) {
      if (var1 == 0) {
         return this.c().b();
      }

      double var2 = BY.a(this.b, var1 - 1);
      return new lm(this.b * var2, var1 * var2, this.c);
   }

   private lm j(lm var1) {
      return this.P().d(var1).N();
   }

   private static lm a(double var0, lm var2) {
      if (var0 == 0.0) {
         if (var2.b == 0.0) {
            return var2.a(1.0, Double.NEGATIVE_INFINITY);
         } else {
            return var2.b < 0.0 ? var2.a(Double.NaN, Double.NaN) : var2.c().a();
         }
      } else {
         double var3 = BY.a(var0, var2.b);
         return new lm(var3, var3 * BY.k(var0), var2.c);
      }
   }

   private lm N() {
      double var1 = BY.i(this.b);
      return new lm(var1, var1, this.c);
   }

   private lm O() {
      return new lm(BY.j(this.b), BY.i(this.b), this.c);
   }

   private lm P() {
      return new lm(BY.k(this.b), 1.0 / this.b, this.c);
   }

   private lm Q() {
      return new lm(BY.m(this.b), 1.0 / (BY.k(10.0) * this.b), this.c);
   }

   private lm R() {
      return new lm(BY.l(this.b), 1.0 / (1.0 + this.b), this.c);
   }

   private lm S() {
      return new lm(BY.o(this.b), -BY.n(this.b), this.c);
   }

   private lm T() {
      return new lm(BY.n(this.b), BY.o(this.b), this.c);
   }

   private lm U() {
      double var1 = BY.p(this.b);
      return new lm(var1, 1.0 + var1 * var1, this.c);
   }

   private lm V() {
      return new lm(BY.s(this.b), -1.0 / BY.a(1.0 - this.b * this.b), this.c);
   }

   private lm W() {
      return new lm(BY.r(this.b), 1.0 / BY.a(1.0 - this.b * this.b), this.c);
   }

   private lm X() {
      return new lm(BY.q(this.b), 1.0 / (1.0 + this.b * this.b), this.c);
   }

   private lm k(lm var1) {
      lm var2 = this.d(this).a(var1.d(var1)).L();
      if (var1.b >= 0.0) {
         var2 = this.f(var2.a(var1)).X().f(2);
      } else {
         lm var4;
         var2 = (var4 = this.f(var2.c(var1)).X().f(-2)).i(var4.b <= 0.0 ? -Math.PI : Math.PI);
      }

      var2.b = BY.b(this.b, var1.b);
      return var2;
   }

   private static lm b(lm var0, lm var1) {
      return var0.k(var1);
   }

   private lm Y() {
      return new lm(BY.b(this.b), BY.c(this.b), this.c);
   }

   private lm Z() {
      return new lm(BY.c(this.b), BY.b(this.b), this.c);
   }

   private lm aa() {
      double var1 = BY.d(this.b);
      return new lm(var1, 1.0 - var1 * var1, this.c);
   }

   private lm ab() {
      return new lm(BY.e(this.b), 1.0 / BY.a(this.b * this.b - 1.0), this.c);
   }

   private lm ac() {
      return new lm(BY.f(this.b), 1.0 / BY.a(this.b * this.b + 1.0), this.c);
   }

   private lm ad() {
      return new lm(BY.g(this.b), 1.0 / (1.0 - this.b * this.b), this.c);
   }

   private lm ae() {
      return new lm(BY.v(this.b), BY.v(1.0), this.c);
   }

   private lm af() {
      return new lm(BY.u(this.b), BY.u(1.0), this.c);
   }

   private double a(double... var1) {
      double var2 = this.b;

      for (int var4 = 0; var4 < var1.length; var4++) {
         double var10001 = var1[var4];
         int var3 = var4;
         Double var5;
         var2 += var10001 * ((var5 = this.c.get(var3)) == null ? 0.0 : var5);
      }

      return var2;
   }

   private lm a(double var1, double var3) {
      return new lm(var1, var3, this.c);
   }

   private static lm a(lm[] var0, lm[] var1) {
      lm var2 = var0[0].c().a();

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2 = var2.a(var0[var3].d(var1[var3]));
      }

      double[] var7 = new double[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         lm var5;
         var7[var4] = (var5 = var0[var4]).b;
      }

      double[] var8 = new double[var1.length];

      for (int var6 = 0; var6 < var1.length; var6++) {
         lm var9;
         var8[var6] = (var9 = var1[var6]).b;
      }

      var2.b = Ch.h(var7, var8);
      return var2;
   }

   private static lm a(double[] var0, lm[] var1) {
      lm var2 = var1[0].c().a();

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2 = var2.a(var1[var3].k(var0[var3]));
      }

      double[] var6 = new double[var1.length];

      for (int var4 = 0; var4 < var1.length; var4++) {
         lm var5;
         var6[var4] = (var5 = var1[var4]).b;
      }

      var2.b = Ch.h(var0, var6);
      return var2;
   }

   private static lm a(lm var0, lm var1, lm var2, lm var3) {
      lm var4;
      (var4 = var0.d(var1).a(var2.d(var3))).b = Ch.a(var0.b, var1.b, var2.b, var3.b);
      return var4;
   }

   private static lm a(double var0, lm var2, double var3, lm var5) {
      lm var6;
      (var6 = var2.k(var0).a(var5.k(var3))).b = Ch.a(var0, var2.b, var3, var5.b);
      return var6;
   }

   private static lm a(lm var0, lm var1, lm var2, lm var3, lm var4, lm var5) {
      lm var6;
      (var6 = var0.d(var1).a(var2.d(var3)).a(var4.d(var5))).b = Ch.a(var0.b, var1.b, var2.b, var3.b, var4.b, var5.b);
      return var6;
   }

   private static lm a(double var0, lm var2, double var3, lm var5, double var6, lm var8) {
      lm var9;
      (var9 = var2.k(var0).a(var5.k(var3)).a(var8.k(var6))).b = Ch.a(var0, var2.b, var3, var5.b, var6, var8.b);
      return var9;
   }

   private static lm a(lm var0, lm var1, lm var2, lm var3, lm var4, lm var5, lm var6, lm var7) {
      lm var8;
      (var8 = var0.d(var1).a(var2.d(var3)).a(var4.d(var5)).a(var6.d(var7))).b = Ch.a(var0.b, var1.b, var2.b, var3.b, var4.b, var5.b, var6.b, var7.b);
      return var8;
   }

   private static lm a(double var0, lm var2, double var3, lm var5, double var6, lm var8, double var9, lm var11) {
      lm var12;
      (var12 = var2.k(var0).a(var5.k(var3)).a(var8.k(var6)).a(var11.k(var9))).b = Ch.a(var0, var2.b, var3, var5.b, var6, var8.b, var9, var11.b);
      return var12;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (other instanceof lm) {
         other = (lm)other;
         if (!Cq.b(this.b, ((lm)other).b, 1)) {
            return false;
         }

         if (this.c.size() != ((lm)other).c.size()) {
            return false;
         }

         for (Entry var3 : this.c.entrySet()) {
            if (!((lm)other).c.containsKey(var3.getKey())) {
               return false;
            }

            if (!Cq.b((Double)var3.getValue(), ((lm)other).c.get(var3.getKey()), 1)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 743 + 809 * Ci.a(this.b) + 167 * this.c.hashCode();
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6, double var7, kO var9, double var10, kO var12) {
      lm var10001 = var3;
      lm var10003 = (lm)var6;
      lm var10005 = (lm)var9;
      lm var26 = (lm)var12;
      double var22 = var10;
      var3 = var10005;
      double var19 = var7;
      lm var2 = var10003;
      double var16 = var4;
      lm var24 = var10001;
      double var13 = var1;
      lm var5;
      (var5 = var24.k(var13).a(var2.k(var16)).a(var3.k(var19)).a(var26.k(var22))).b = Ch.a(var13, var24.b, var16, var2.b, var19, var3.b, var22, var26.b);
      return var5;
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, kO var3, kO var4, kO var5, kO var6, kO var7, kO var8) {
      lm var10000 = var1;
      lm var10001 = var2;
      lm var10002 = var3;
      lm var10003 = var4;
      lm var10004 = var5;
      lm var10005 = var6;
      lm var10006 = var7;
      var8 = var8;
      var7 = var10006;
      var6 = var10005;
      var5 = var10004;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      var1 = var10000;
      lm var9;
      (var9 = var10000.d(var2).a(var3.d(var4)).a(var5.d(var6)).a(var7.d(var8))).b = Ch.a(var1.b, var2.b, var3.b, var4.b, var5.b, var6.b, var7.b, var8.b);
      return var9;
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6, double var7, kO var9) {
      lm var10001 = var3;
      lm var10003 = (lm)var6;
      var3 = (lm)var9;
      double var16 = var7;
      lm var2 = var10003;
      double var13 = var4;
      lm var18 = var10001;
      double var10 = var1;
      lm var20;
      (var20 = var18.k(var10).a(var2.k(var13)).a(var3.k(var16))).b = Ch.a(var10, var18.b, var13, var2.b, var16, var3.b);
      return var20;
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, kO var3, kO var4, kO var5, kO var6) {
      lm var10000 = var1;
      lm var10001 = var2;
      lm var10002 = var3;
      lm var10003 = var4;
      lm var10004 = var5;
      var6 = var6;
      var5 = var10004;
      var4 = var10003;
      var3 = var10002;
      var2 = var10001;
      var1 = var10000;
      lm var7;
      (var7 = var10000.d(var2).a(var3.d(var4)).a(var5.d(var6))).b = Ch.a(var1.b, var2.b, var3.b, var4.b, var5.b, var6.b);
      return var7;
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1, kO var3, double var4, kO var6) {
      lm var10001 = (lm)var3;
      lm var2 = (lm)var6;
      double var10 = var4;
      lm var12 = var10001;
      double var7 = var1;
      lm var13;
      (var13 = var12.k(var7).a(var2.k(var10))).b = Ch.a(var7, var12.b, var10, var2.b);
      return var13;
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1, kO var2, Object var3, kO var4) {
      lm var10000 = var1;
      lm var10001 = var2;
      lm var10002 = var3;
      var4 = var4;
      var3 = var10002;
      var2 = var10001;
      var1 = var10000;
      lm var5;
      (var5 = var10000.d(var2).a(var3.d(var4))).b = Ch.a(var1.b, var2.b, var3.b, var4.b);
      return var5;
   }

   // $VF: synthetic method
   @Override
   public final kO a(double[] var1, Object[] var2) {
      var2 = var2;
      var1 = var1;
      lm var3 = var2[0].c().a();

      for (int var4 = 0; var4 < var1.length; var4++) {
         var3 = var3.a(var2[var4].k(var1[var4]));
      }

      double[] var9 = new double[var2.length];

      for (int var5 = 0; var5 < var2.length; var5++) {
         lm var6;
         var9[var5] = (var6 = var2[var5]).b;
      }

      var3.b = Ch.h(var1, var9);
      return var3;
   }

   // $VF: synthetic method
   @Override
   public final kO a(Object[] var1, Object[] var2) {
      lm[] var10000 = var1;
      var2 = var2;
      var1 = var10000;
      lm var3 = var10000[0].c().a();

      for (int var4 = 0; var4 < var1.length; var4++) {
         var3 = var3.a(var1[var4].d(var2[var4]));
      }

      double[] var10 = new double[var1.length];

      for (int var5 = 0; var5 < var1.length; var5++) {
         lm var6;
         var10[var5] = (var6 = var1[var5]).b;
      }

      double[] var11 = new double[var2.length];

      for (int var8 = 0; var8 < var2.length; var8++) {
         lm var12;
         var11[var8] = (var12 = var2[var8]).b;
      }

      var3.b = Ch.h(var10, var11);
      return var3;
   }

   // $VF: synthetic method
   @Override
   public final kO B() {
      lm var1 = this;
      return new lm(BY.g(var1.b), 1.0 / (1.0 - var1.b * var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO A() {
      lm var1 = this;
      return new lm(BY.f(var1.b), 1.0 / BY.a(var1.b * var1.b + 1.0), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO z() {
      lm var1 = this;
      return new lm(BY.e(var1.b), 1.0 / BY.a(var1.b * var1.b - 1.0), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO y() {
      lm var1 = this;
      double var2 = BY.d(this.b);
      return new lm(var2, 1.0 - var2 * var2, var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO x() {
      lm var1 = this;
      return new lm(BY.c(var1.b), BY.b(var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO w() {
      lm var1 = this;
      return new lm(BY.b(var1.b), BY.c(var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO h(Object var1) {
      return this.k((lm)var1);
   }

   // $VF: synthetic method
   @Override
   public final kO v() {
      return this.X();
   }

   // $VF: synthetic method
   @Override
   public final kO u() {
      lm var1 = this;
      return new lm(BY.r(var1.b), 1.0 / BY.a(1.0 - var1.b * var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO t() {
      lm var1 = this;
      return new lm(BY.s(var1.b), -1.0 / BY.a(1.0 - var1.b * var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO s() {
      lm var1 = this;
      double var2 = BY.p(this.b);
      return new lm(var2, 1.0 + var2 * var2, var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO r() {
      lm var1 = this;
      return new lm(BY.n(var1.b), BY.o(var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO q() {
      lm var1 = this;
      return new lm(BY.o(var1.b), -BY.n(var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO p() {
      lm var1 = this;
      return new lm(BY.l(var1.b), 1.0 / (1.0 + var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO o() {
      return this.P();
   }

   // $VF: synthetic method
   @Override
   public final kO n() {
      lm var1 = this;
      return new lm(BY.j(var1.b), BY.i(var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO m() {
      return this.N();
   }

   // $VF: synthetic method
   @Override
   public final kO a(kO var1) {
      lm var2 = (lm)var1;
      return this.P().d(var2).N();
   }

   // $VF: synthetic method
   @Override
   public final kO d(int var1) {
      int var2 = var1;
      lm var6 = this;
      if (var2 == 0) {
         return var6.c().b();
      }

      double var4 = BY.a(var6.b, var2 - 1);
      return new lm(var6.b * var4, var2 * var4, var6.c);
   }

   // $VF: synthetic method
   @Override
   public final kO g(double var1) {
      double var4 = var1;
      lm var6 = this;
      return new lm(BY.a(var6.b, var4), var4 * BY.a(var6.b, var4 - 1.0), var6.c);
   }

   // $VF: synthetic method
   @Override
   public final kO c(int var1) {
      int var2 = var1;
      lm var6 = this;
      if (var2 == 2) {
         return var6.L();
      }

      if (var2 == 3) {
         return var6.M();
      }

      double var4 = BY.a(var6.b, 1.0 / var2);
      return new lm(var4, 1.0 / (var2 * BY.a(var4, var2 - 1)), var6.c);
   }

   // $VF: synthetic method
   @Override
   public final kO l() {
      return this.M();
   }

   // $VF: synthetic method
   @Override
   public final kO k() {
      return this.L();
   }

   // $VF: synthetic method
   @Override
   public final kN b() {
      lm var1 = this;
      return new lm(1.0 / var1.b, -1.0 / (var1.b * var1.b), var1.c);
   }

   // $VF: synthetic method
   @Override
   public final kO g(Object var1) {
      return this.i((lm)var1);
   }

   // $VF: synthetic method
   @Override
   public final kO b(int var1) {
      return this.g(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO f(double var1) {
      double var4 = var1;
      lm var10 = this;
      long var6 = Double.doubleToLongBits(this.b);
      long var8 = Double.doubleToLongBits(var4);
      return (var6 < 0L || var8 < 0L) && (var6 >= 0L || var8 >= 0L) ? var10.E() : var10;
   }

   // $VF: synthetic method
   @Override
   public final kO f(Object var1) {
      lm var2 = var1;
      var1 = this;
      long var4 = Double.doubleToLongBits(this.b);
      long var6 = Double.doubleToLongBits(var2.b);
      return (var4 < 0L || var6 < 0L) && (var4 >= 0L || var6 >= 0L) ? var1.E() : var1;
   }

   // $VF: synthetic method
   @Override
   public final kO j() {
      return h(BY.h(this.b));
   }

   // $VF: synthetic method
   @Override
   public final kO h() {
      return h(BY.A(this.b));
   }

   // $VF: synthetic method
   @Override
   public final kO g() {
      return h(BY.y(this.b));
   }

   // $VF: synthetic method
   @Override
   public final kO f() {
      return h(BY.z(this.b));
   }

   // $VF: synthetic method
   @Override
   public final kO e() {
      return this.F();
   }

   // $VF: synthetic method
   @Override
   public final kO e(Object var1) {
      lm var2 = var1;
      var1 = this;
      double var4 = BY.g(this.b, var2.b);
      double var6 = BY.A((var1.b - var4) / var2.b);
      return var1.c(var2.k(var6));
   }

   // $VF: synthetic method
   @Override
   public final kO e(double var1) {
      double var4 = var1;
      lm var6 = this;
      return new lm(BY.g(var6.b, var4), var6.c);
   }

   // $VF: synthetic method
   @Override
   public final kO d(double var1) {
      double var4 = var1;
      lm var6 = this;
      return new lm(var6.b / var4, 1.0 / var4, var6.c);
   }

   // $VF: synthetic method
   @Override
   public final kO c(double var1) {
      return this.k(var1);
   }

   // $VF: synthetic method
   @Override
   public final kO b(double var1) {
      double var4 = var1;
      lm var6 = this;
      return new lm(var6.b - var4, var6.c);
   }

   // $VF: synthetic method
   @Override
   public final kO a(double var1) {
      return this.i(var1);
   }

   // $VF: synthetic method
   @Override
   public final kN d(Object var1) {
      return this.f((lm)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN c(Object var1) {
      return this.d((lm)var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a(int var1) {
      return this.f(var1);
   }

   // $VF: synthetic method
   @Override
   public final kN a() {
      return this.E();
   }

   // $VF: synthetic method
   @Override
   public final kN b(Object var1) {
      return this.c((lm)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final kN a(Object var1) {
      return this.a((lm)var1);
   }
}
