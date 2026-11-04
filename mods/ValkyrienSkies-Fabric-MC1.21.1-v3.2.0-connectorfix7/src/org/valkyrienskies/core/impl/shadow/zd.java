package org.valkyrienskies.core.impl.shadow;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

@Deprecated
final class zd implements Serializable {
   private static final String b = "x-";
   private static final int c = 10;
   private static final double d = 1.0E-12;
   private static final long e = -1369660067587938365L;
   private final yY f;
   private final List<yX> g;
   private final boolean h;
   private final List<String> i = new ArrayList<>();
   private transient tq j;
   private final int k;
   private final int l;
   int a;
   private final double m;
   private final int n;

   private zd(yY var1, Collection<yX> var2, yf var3, boolean var4, double var5) {
      this(var1, var2, var3, var4, var5, 10);
   }

   zd(yY var1, Collection<yX> var2, yf var3, boolean var4, double var5, int var7) {
      this.f = var1;
      Collection var8 = var2;
      ArrayList var9 = new ArrayList(var8.size());

      for (yX var11 : var8) {
         yX var12 = var11;
         yX var10002;
         if (var11.c < 0.0) {
            var10002 = new yX;
            tu var10004 = var12.a.h(-1.0);
            zb var13 = var12.b;
            zb var10005;
            switch (var13) {
               case LEQ:
                  var10005 = zb.GEQ;
                  break;
               case GEQ:
                  var10005 = zb.LEQ;
                  break;
               default:
                  var10005 = zb.EQ;
            }

            var10002./* $VF: Unable to resugar constructor */<init>(var10004, var10005, -1.0 * var12.c);
         } else {
            var10002 = new yX(var12.a, var12.b, var12.c);
         }

         var9.add(var10002);
      }

      this.g = var9;
      this.h = var4;
      this.m = var5;
      this.n = var7;
      this.k = var1.a.f() + (var4 ? 0 : 1);
      this.l = this.a(zb.LEQ) + this.a(zb.GEQ);
      this.a = this.a(zb.EQ) + this.a(zb.GEQ);
      boolean var21 = var3 == yf.MAXIMIZE;
      zd var16 = this;
      int var25 = this.k + var16.l + var16.a + var16.a() + 1;
      int var26 = var16.g.size() + var16.a();
      sy var27 = new sy(var26, var25);
      if (var16.a() == 2) {
         var27.c(0, 0, -1.0);
      }

      int var28 = var16.a() == 1 ? 0 : 1;
      var27.c(var28, var28, var21 ? 1.0 : -1.0);
      tu var10001;
      if (var21) {
         yY var14 = var16.f;
         var10001 = var16.f.a.h(-1.0);
      } else {
         yY var30 = var16.f;
         var10001 = var16.f.a;
      }

      tu var29 = var10001;
      var16.a(var29.g(), var27.a[var28]);
      int var10003 = var25 - 1;
      double var36;
      if (var21) {
         yY var31 = var16.f;
         var36 = var16.f.b;
      } else {
         yY var32 = var16.f;
         var36 = -1.0 * var16.f.b;
      }

      var27.c(var28, var10003, var36);
      if (!var16.h) {
         var27.c(var28, var16.k() - 1, a(var29));
      }

      int var15 = 0;
      int var18 = 0;

      for (int var19 = 0; var19 < var16.g.size(); var19++) {
         yX var20 = var16.g.get(var19);
         int var6 = var16.a() + var19;
         var16.a(var20.a.g(), var27.a[var6]);
         if (!var16.h) {
            var27.c(var6, var16.k() - 1, a(var20.a));
         }

         var27.c(var6, var25 - 1, var20.c);
         if (var20.b == zb.LEQ) {
            var27.c(var6, var16.k() + var15++, 1.0);
         } else if (var20.b == zb.GEQ) {
            var27.c(var6, var16.k() + var15++, -1.0);
         }

         if (var20.b == zb.EQ || var20.b == zb.GEQ) {
            var27.c(0, var16.g() + var18, 1.0);
            var27.c(var6, var16.g() + var18++, 1.0);
            var27.a(0, var27.d(0).k(var27.d(var6)));
         }
      }

      this.j = var27;
      zd var17 = this;
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

      for (int var23 = 0; var23 < var17.l; var23++) {
         var17.i.add("s" + var23);
      }

      for (int var24 = 0; var24 < var17.a; var24++) {
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

      for (int var3 = 0; var3 < this.l; var3++) {
         this.i.add("s" + var3);
      }

      for (int var4 = 0; var4 < this.a; var4++) {
         this.i.add("a" + var4);
      }

      this.i.add("RHS");
   }

   private sy a(boolean var1) {
      int var2 = this.k + this.l + this.a + this.a() + 1;
      int var3 = this.g.size() + this.a();
      sy var10 = new sy(var3, var2);
      if (this.a() == 2) {
         var10.c(0, 0, -1.0);
      }

      int var4 = this.a() == 1 ? 0 : 1;
      var10.c(var4, var4, var1 ? 1.0 : -1.0);
      tu var10000;
      if (var1) {
         yY var8 = this.f;
         var10000 = this.f.a.h(-1.0);
      } else {
         yY var13 = this.f;
         var10000 = this.f.a;
      }

      tu var5 = var10000;
      this.a(var5.g(), var10.a[var4]);
      int var10002 = var2 - 1;
      double var10003;
      if (var1) {
         yY var14 = this.f;
         var10003 = this.f.b;
      } else {
         yY var15 = this.f;
         var10003 = -1.0 * this.f.b;
      }

      var10.c(var4, var10002, var10003);
      if (!this.h) {
         var10.c(var4, this.k() - 1, a(var5));
      }

      var1 = 0;
      var4 = 0;

      for (int var12 = 0; var12 < this.g.size(); var12++) {
         yX var6 = this.g.get(var12);
         int var7 = this.a() + var12;
         this.a(var6.a.g(), var10.a[var7]);
         if (!this.h) {
            var10.c(var7, this.k() - 1, a(var6.a));
         }

         var10.c(var7, var2 - 1, var6.c);
         if (var6.b == zb.LEQ) {
            var10.c(var7, this.k() + var1++, 1.0);
         } else if (var6.b == zb.GEQ) {
            var10.c(var7, this.k() + var1++, -1.0);
         }

         if (var6.b == zb.EQ || var6.b == zb.GEQ) {
            var10.c(0, this.g() + var4, 1.0);
            var10.c(var7, this.g() + var4++, 1.0);
            var10.a(0, var10.d(0).k(var10.d(var7)));
         }
      }

      return var10;
   }

   private List<yX> a(Collection<yX> var1) {
      ArrayList var2 = new ArrayList(var1.size());

      for (yX var3 : var1) {
         var2.add(a(var3));
      }

      return var2;
   }

   private static yX a(yX var0) {
      return var0.c < 0.0 ? new yX(var0.a.h(-1.0), var0.b.a(), -1.0 * var0.c) : new yX(var0.a, var0.b, var0.c);
   }

   protected final int a() {
      return this.a > 0 ? 2 : 1;
   }

   private int a(zb var1) {
      int var2 = 0;
      Iterator var3 = this.g.iterator();

      while (var3.hasNext()) {
         if (((yX)var3.next()).b == var1) {
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
      Integer var2 = null;

      for (int var3 = 0; var3 < this.f(); var3++) {
         double var4;
         if (Cq.b(var4 = this.a(var3, var1), 1.0, this.n) && var2 == null) {
            var2 = var3;
         } else if (!Cq.b(var4, 0.0, this.n)) {
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
            if (Cq.a(this.j.b(0, var2), 0.0, this.m) > 0) {
               var1.add(var2);
            }
         }

         for (int var6 = 0; var6 < this.a; var6++) {
            int var3 = var6 + this.g();
            if (this.a(var3) == null) {
               var1.add(var3);
            }
         }

         double[][] var7 = new double[this.f() - 1][this.e() - var1.size()];

         for (int var8 = 1; var8 < this.f(); var8++) {
            int var4 = 0;

            for (int var5 = 0; var5 < this.e(); var5++) {
               if (!var1.contains(var5)) {
                  var7[var8 - 1][var4++] = this.j.b(var8, var5);
               }
            }
         }

         Integer[] var9;
         for (int var10 = (var9 = var1.toArray(new Integer[var1.size()])).length - 1; var10 >= 0; var10--) {
            this.i.remove(var9[var10].intValue());
         }

         this.j = new sy(var7);
         this.a = 0;
      }
   }

   private void a(double[] var1, double[] var2) {
      System.arraycopy(var1, 0, var2, this.a(), var1.length);
   }

   final boolean c() {
      for (int var1 = this.a(); var1 < this.e() - 1; var1++) {
         if (Cq.a(this.j.b(0, var1), 0.0, this.m) < 0) {
            return false;
         }
      }

      return true;
   }

   protected final yp d() {
      int var1;
      Integer var7;
      double var3 = (var7 = (var1 = this.i.indexOf("x-")) > 0 ? this.a(var1) : null) == null ? 0.0 : this.a(var7, this.e() - 1);
      HashSet var8 = new HashSet();
      double[] var2 = new double[this.m()];

      for (int var5 = 0; var5 < var2.length; var5++) {
         int var6;
         if ((var6 = this.i.indexOf("x" + var5)) < 0) {
            var2[var5] = 0.0;
         } else {
            Integer var11;
            if ((var11 = this.a(var6)) != null && var11 == 0) {
               var2[var5] = 0.0;
            } else if (var8.contains(var11)) {
               var2[var5] = 0.0 - (this.h ? 0.0 : var3);
            } else {
               var8.add(var11);
               var2[var5] = (var11 == null ? 0.0 : this.a(var11, this.e() - 1)) - (this.h ? 0.0 : var3);
            }
         }
      }

      double[] var10;
      double[] var10002 = var10 = var2;
      yY var9 = this.f;
      return new yp(var10002, this.f.a.b(new sA(var10, false)) + var9.b);
   }

   protected final void a(int var1, double var2) {
      for (int var4 = 0; var4 < this.e(); var4++) {
         this.j.c(var1, var4, this.j.b(var1, var4) / var2);
      }
   }

   protected final void a(int var1, int var2, double var3) {
      for (int var5 = 0; var5 < this.e(); var5++) {
         double var6;
         if (BY.w(var6 = this.j.b(var1, var5) - this.j.b(var2, var5) * var3) < 1.0E-12) {
            var6 = 0.0;
         }

         this.j.c(var1, var5, var6);
      }
   }

   protected final int e() {
      return this.j.g();
   }

   protected final int f() {
      return this.j.f();
   }

   protected final double a(int var1, int var2) {
      return this.j.b(var1, var2);
   }

   private void b(int var1, int var2, double var3) {
      this.j.c(var1, var2, var3);
   }

   private int k() {
      return this.a() + this.k;
   }

   protected final int g() {
      return this.a() + this.k + this.l;
   }

   protected final int h() {
      return this.e() - 1;
   }

   private int l() {
      return this.k;
   }

   private int m() {
      yY var1 = this.f;
      return this.f.a.f();
   }

   private int n() {
      return this.l;
   }

   protected final int i() {
      return this.a;
   }

   private double[][] o() {
      return this.j.a();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof zd) {
         other = (zd)other;
         return this.h == ((zd)other).h
            && this.k == ((zd)other).k
            && this.l == ((zd)other).l
            && this.a == ((zd)other).a
            && this.m == ((zd)other).m
            && this.n == ((zd)other).n
            && this.f.equals(((zd)other).f)
            && this.g.equals(((zd)other).g)
            && this.j.equals(((zd)other).j);
      } else {
         return false;
      }
   }

   @Override
   public final int hashCode() {
      return Boolean.valueOf(this.h).hashCode()
         ^ this.k
         ^ this.l
         ^ this.a
         ^ Double.valueOf(this.m).hashCode()
         ^ this.n
         ^ this.f.hashCode()
         ^ this.g.hashCode()
         ^ this.j.hashCode();
   }

   private void a(ObjectOutputStream var1) {
      var1.defaultWriteObject();
      td.a(this.j, var1);
   }

   private void a(ObjectInputStream var1) {
      var1.defaultReadObject();
      td.b(this, "tableau", var1);
   }
}
