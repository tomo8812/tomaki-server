package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

final class xf implements Serializable {
   private static final String d = "x-";
   private static final long e = -1369660067587938365L;
   private final wY f;
   private final List<wW> g;
   private final boolean h;
   private final List<String> i = new ArrayList<>();
   transient sy a;
   private final int j;
   private final int k;
   int b;
   private final double l;
   private final int m;
   private int[] n;
   int[] c;

   private xf(wY var1, Collection<wW> var2, xi var3, boolean var4, double var5) {
      this(var1, var2, var3, var4, var5, 10);
   }

   xf(wY var1, Collection<wW> var2, xi var3, boolean var4, double var5, int var7) {
      this.f = var1;
      Collection var8 = var2;
      ArrayList var9 = new ArrayList(var8.size());

      for (wW var11 : var8) {
         wW var12 = var11;
         wW var10002;
         if (var11.c < 0.0) {
            var10002 = new wW;
            tu var10004 = var12.a.h(-1.0);
            xd var13 = var12.b;
            xd var10005;
            switch (var13) {
               case LEQ:
                  var10005 = xd.GEQ;
                  break;
               case GEQ:
                  var10005 = xd.LEQ;
                  break;
               default:
                  var10005 = xd.EQ;
            }

            var10002./* $VF: Unable to resugar constructor */<init>(var10004, var10005, -1.0 * var12.c);
         } else {
            var10002 = new wW(var12.a, var12.b, var12.c);
         }

         var9.add(var10002);
      }

      this.g = var9;
      this.h = var4;
      this.l = var5;
      this.m = var7;
      this.j = var1.a.f() + (var4 ? 0 : 1);
      this.k = this.a(xd.LEQ) + this.a(xd.GEQ);
      this.b = this.a(xd.EQ) + this.a(xd.GEQ);
      boolean var21 = var3 == xi.MAXIMIZE;
      xf var16 = this;
      int var25 = this.j + var16.k + var16.b + var16.a() + 1;
      int var26 = var16.g.size() + var16.a();
      sy var27 = new sy(var26, var25);
      if (var16.a() == 2) {
         var27.c(0, 0, -1.0);
      }

      int var28 = var16.a() == 1 ? 0 : 1;
      var27.c(var28, var28, var21 ? 1.0 : -1.0);
      tu var10001;
      if (var21) {
         wY var14 = var16.f;
         var10001 = var16.f.a.h(-1.0);
      } else {
         wY var30 = var16.f;
         var10001 = var16.f.a;
      }

      tu var29 = var10001;
      var16.a(var29.g(), var27.a[var28]);
      int var10003 = var25 - 1;
      double var36;
      if (var21) {
         wY var31 = var16.f;
         var36 = var16.f.b;
      } else {
         wY var32 = var16.f;
         var36 = -1.0 * var16.f.b;
      }

      var27.c(var28, var10003, var36);
      if (!var16.h) {
         var27.c(var28, var16.k() - 1, a(var29));
      }

      int var15 = 0;
      int var18 = 0;

      for (int var19 = 0; var19 < var16.g.size(); var19++) {
         wW var20 = var16.g.get(var19);
         int var6 = var16.a() + var19;
         var16.a(var20.a.g(), var27.a[var6]);
         if (!var16.h) {
            var27.c(var6, var16.k() - 1, a(var20.a));
         }

         var27.c(var6, var25 - 1, var20.c);
         if (var20.b == xd.LEQ) {
            var27.c(var6, var16.k() + var15++, 1.0);
         } else if (var20.b == xd.GEQ) {
            var27.c(var6, var16.k() + var15++, -1.0);
         }

         if (var20.b == xd.EQ || var20.b == xd.GEQ) {
            var27.c(0, var16.g() + var18, 1.0);
            var27.c(var6, var16.g() + var18++, 1.0);
            var27.a(0, var27.d(0).k(var27.d(var6)));
         }
      }

      this.a = var27;
      this.c(this.k());
      xf var17 = this;
      if (this.a() == 2) {
         var17.i.add("W");
      }

      var17.i.add("Z");

      for (int var22 = 0; var22 < var17.m(); var22++) {
         var17.i.add("x" + var22);
      }

      if (!var17.h) {
         var17.i.add("x-");
      }

      for (int var23 = 0; var23 < var17.k; var23++) {
         var17.i.add("s" + var23);
      }

      for (int var24 = 0; var24 < var17.b; var24++) {
         var17.i.add("a" + var24);
      }

      var17.i.add("RHS");
   }

   private void j() {
      if (this.a() == 2) {
         this.i.add("W");
      }

      this.i.add("Z");

      for (int var1 = 0; var1 < this.m(); var1++) {
         this.i.add("x" + var1);
      }

      if (!this.h) {
         this.i.add("x-");
      }

      for (int var3 = 0; var3 < this.k; var3++) {
         this.i.add("s" + var3);
      }

      for (int var4 = 0; var4 < this.b; var4++) {
         this.i.add("a" + var4);
      }

      this.i.add("RHS");
   }

   private sy a(boolean var1) {
      int var2 = this.j + this.k + this.b + this.a() + 1;
      int var3 = this.g.size() + this.a();
      sy var10 = new sy(var3, var2);
      if (this.a() == 2) {
         var10.c(0, 0, -1.0);
      }

      int var4 = this.a() == 1 ? 0 : 1;
      var10.c(var4, var4, var1 ? 1.0 : -1.0);
      tu var10000;
      if (var1) {
         wY var8 = this.f;
         var10000 = this.f.a.h(-1.0);
      } else {
         wY var13 = this.f;
         var10000 = this.f.a;
      }

      tu var5 = var10000;
      this.a(var5.g(), var10.a[var4]);
      int var10002 = var2 - 1;
      double var10003;
      if (var1) {
         wY var14 = this.f;
         var10003 = this.f.b;
      } else {
         wY var15 = this.f;
         var10003 = -1.0 * this.f.b;
      }

      var10.c(var4, var10002, var10003);
      if (!this.h) {
         var10.c(var4, this.k() - 1, a(var5));
      }

      var1 = 0;
      var4 = 0;

      for (int var12 = 0; var12 < this.g.size(); var12++) {
         wW var6 = this.g.get(var12);
         int var7 = this.a() + var12;
         this.a(var6.a.g(), var10.a[var7]);
         if (!this.h) {
            var10.c(var7, this.k() - 1, a(var6.a));
         }

         var10.c(var7, var2 - 1, var6.c);
         if (var6.b == xd.LEQ) {
            var10.c(var7, this.k() + var1++, 1.0);
         } else if (var6.b == xd.GEQ) {
            var10.c(var7, this.k() + var1++, -1.0);
         }

         if (var6.b == xd.EQ || var6.b == xd.GEQ) {
            var10.c(0, this.g() + var4, 1.0);
            var10.c(var7, this.g() + var4++, 1.0);
            var10.a(0, var10.d(0).k(var10.d(var7)));
         }
      }

      return var10;
   }

   private List<wW> a(Collection<wW> var1) {
      ArrayList var2 = new ArrayList(var1.size());

      for (wW var3 : var1) {
         var2.add(a(var3));
      }

      return var2;
   }

   private static wW a(wW var0) {
      return var0.c < 0.0 ? new wW(var0.a.h(-1.0), var0.b.a(), -1.0 * var0.c) : new wW(var0.a, var0.b, var0.c);
   }

   protected final int a() {
      return this.b > 0 ? 2 : 1;
   }

   private int a(xd var1) {
      int var2 = 0;
      Iterator var3 = this.g.iterator();

      while (var3.hasNext()) {
         if (((wW)var3.next()).b == var1) {
            var2++;
         }
      }

      return var2;
   }

   private static double a(tu var0) {
      double var1 = 0.0;
      double[] var8;
      int var3 = (var8 = var0.g()).length;

      for (int var4 = 0; var4 < var3; var4++) {
         double var6 = var8[var4];
         var1 -= var6;
      }

      return var1;
   }

   protected final Integer a(int var1) {
      int var2;
      return (var2 = this.n[var1]) == -1 ? null : var2;
   }

   protected final int b(int var1) {
      return this.c[var1];
   }

   private void c(int var1) {
      this.n = new int[this.a.g() - 1];
      this.c = new int[this.a.f()];
      Arrays.fill(this.n, -1);

      for (int var3 = var1; var3 < this.a.g() - 1; var3++) {
         Integer var2;
         if ((var2 = this.d(var3)) != null) {
            this.n[var3] = var2;
            this.c[var2] = var3;
         }
      }
   }

   private Integer d(int var1) {
      Integer var2 = null;

      for (int var3 = 0; var3 < this.a.f(); var3++) {
         double var4;
         if (Cq.b(var4 = this.b(var3, var1), 1.0, this.m) && var2 == null) {
            var2 = var3;
         } else if (!Cq.b(var4, 0.0, this.m)) {
            return null;
         }
      }

      return var2;
   }

   protected final void b() {
      if (this.a() != 1) {
         TreeSet var1;
         (var1 = new TreeSet()).add(0);

         for (int var2 = this.a(); var2 < this.g(); var2++) {
            if (Cq.a(this.b(0, var2), 0.0, this.l) > 0) {
               var1.add(var2);
            }
         }

         for (int var7 = 0; var7 < this.b; var7++) {
            int var3 = var7 + this.g();
            if (this.a(var3) == null) {
               var1.add(var3);
            }
         }

         double[][] var8 = new double[this.a.f() - 1][this.a.g() - var1.size()];

         for (int var9 = 1; var9 < this.a.f(); var9++) {
            int var4 = 0;

            for (int var5 = 0; var5 < this.a.g(); var5++) {
               if (!var1.contains(var5)) {
                  var8[var9 - 1][var4++] = this.b(var9, var5);
               }
            }
         }

         Integer[] var10;
         for (int var11 = (var10 = var1.toArray(new Integer[var1.size()])).length - 1; var11 >= 0; var11--) {
            this.i.remove(var10[var11].intValue());
         }

         this.a = new sy(var8);
         this.b = 0;
         this.c(this.a());
      }
   }

   private void a(double[] var1, double[] var2) {
      System.arraycopy(var1, 0, var2, this.a(), var1.length);
   }

   final boolean c() {
      double[] var1 = this.e(0);
      int var2 = this.a.g() - 1;

      for (int var3 = this.a(); var3 < var2; var3++) {
         double var4;
         if (Cq.a(var4 = var1[var3], 0.0, this.l) < 0) {
            return false;
         }
      }

      return true;
   }

   protected final wQ d() {
      int var1;
      Integer var7;
      double var3 = (var7 = (var1 = this.i.indexOf("x-")) > 0 ? this.a(var1) : null) == null ? 0.0 : this.b(var7, this.a.g() - 1);
      HashSet var8 = new HashSet();
      double[] var2 = new double[this.m()];

      for (int var5 = 0; var5 < var2.length; var5++) {
         int var6;
         if ((var6 = this.i.indexOf("x" + var5)) < 0) {
            var2[var5] = 0.0;
         } else {
            Integer var9;
            if ((var9 = this.a(var6)) != null && var9 == 0) {
               var2[var5] = 0.0;
            } else if (var8.contains(var9)) {
               var2[var5] = 0.0 - (this.h ? 0.0 : var3);
            } else {
               var8.add(var9);
               var2[var5] = (var9 == null ? 0.0 : this.b(var9, this.a.g() - 1)) - (this.h ? 0.0 : var3);
            }
         }
      }

      return new wQ(var2, this.f.a(var2));
   }

   protected final void a(int var1, int var2) {
      double var3 = this.b(var2, var1);
      double var10 = var3;
      int var5 = var2;
      xf var4 = this;
      double[] var12 = this.e(var5);

      for (int var17 = 0; var17 < var4.a.g(); var17++) {
         var12[var17] /= var10;
      }

      for (int var13 = 0; var13 < this.a.f(); var13++) {
         double var6;
         if (var13 != var2 && (var6 = this.b(var13, var1)) != 0.0) {
            double var11 = var6;
            int var21 = var2;
            var5 = var13;
            var4 = this;
            double[] var19 = this.e(var5);
            double[] var20 = var4.e(var21);

            for (int var7 = 0; var7 < var4.a.g(); var7++) {
               var19[var7] -= var20[var7] * var11;
            }
         }
      }

      int var14 = var2;
      int var15 = this.c[var14];
      this.n[var15] = -1;
      this.n[var1] = var2;
      this.c[var2] = var1;
   }

   private void a(int var1, double var2) {
      double[] var6 = this.e(var1);

      for (int var4 = 0; var4 < this.a.g(); var4++) {
         var6[var4] /= var2;
      }
   }

   private void a(int var1, int var2, double var3) {
      double[] var7 = this.e(var1);
      double[] var8 = this.e(var2);

      for (int var5 = 0; var5 < this.a.g(); var5++) {
         var7[var5] -= var8[var5] * var3;
      }
   }

   protected final int e() {
      return this.a.g();
   }

   protected final int f() {
      return this.a.f();
   }

   protected final double b(int var1, int var2) {
      return this.a.b(var1, var2);
   }

   private void b(int var1, int var2, double var3) {
      this.a.c(var1, var2, var3);
   }

   private int k() {
      return this.a() + this.j;
   }

   protected final int g() {
      return this.a() + this.j + this.k;
   }

   protected final int h() {
      return this.a.g() - 1;
   }

   private int l() {
      return this.j;
   }

   private int m() {
      wY var1 = this.f;
      return this.f.a.f();
   }

   private int n() {
      return this.k;
   }

   protected final int i() {
      return this.b;
   }

   private double[] e(int var1) {
      sy var2 = this.a;
      return this.a.a[var1];
   }

   private double[][] o() {
      sy var1 = this.a;
      return this.a.k();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof xf) {
         other = (xf)other;
         return this.h == ((xf)other).h
            && this.j == ((xf)other).j
            && this.k == ((xf)other).k
            && this.b == ((xf)other).b
            && this.l == ((xf)other).l
            && this.m == ((xf)other).m
            && this.f.equals(((xf)other).f)
            && this.g.equals(((xf)other).g)
            && this.a.equals(((xf)other).a);
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return Boolean.valueOf(this.h).hashCode()
         ^ this.j
         ^ this.k
         ^ this.b
         ^ Double.valueOf(this.l).hashCode()
         ^ this.m
         ^ this.f.hashCode()
         ^ this.g.hashCode()
         ^ this.a.hashCode();
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      td.a(this.a, var1);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      td.b(this, "tableau", var1);
   }
}
