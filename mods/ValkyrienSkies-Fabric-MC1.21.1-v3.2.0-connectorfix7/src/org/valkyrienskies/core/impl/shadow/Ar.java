package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public class Ar implements Serializable, As {
   private static final long a = 2271900808994826718L;
   private int b;
   private long c = 0L;
   private Av[] d;
   private Av[] e;
   private Av[] f;
   private Av[] g;
   private Av[] h;
   private Av[] i;
   private Av[] j;
   private AN k;

   public Ar(int var1, boolean var2) {
      this.b = var1;
      this.d = new Av[var1];
      this.e = new Av[var1];
      this.f = new Av[var1];
      this.g = new Av[var1];
      this.h = new Av[var1];
      this.i = new Av[var1];
      this.j = new Av[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         this.d[var3] = new AV();
         this.e[var3] = new AX();
         this.f[var3] = new AR();
         this.g[var3] = new AP();
         this.h[var3] = new AW();
         this.i[var3] = new AE();
         this.j[var3] = new AG();
      }

      this.k = new AN(var1, var2);
   }

   public void a(double[] var1) {
      this.a(var1.length);

      for (int var2 = 0; var2 < this.b; var2++) {
         double var3 = var1[var2];
         this.d[var2].a(var3);
         this.e[var2].a(var3);
         this.f[var2].a(var3);
         this.g[var2].a(var3);
         this.h[var2].a(var3);
         this.i[var2].a(var3);
         this.j[var2].a(var3);
      }

      double[] var7 = var1;
      AN var6 = this.k;
      if (var7.length != var6.a.length) {
         throw new oS(var7.length, var6.a.length);
      }

      int var8 = 0;

      for (int var4 = 0; var4 < var7.length; var4++) {
         var6.a[var4] = var6.a[var4] + var7[var4];

         for (int var5 = 0; var5 <= var4; var5++) {
            int var10001 = var8++;
            var6.b[var10001] = var6.b[var10001] + var7[var4] * var7[var5];
         }
      }

      var6.c++;
      this.c++;
   }

   @Override
   public int a() {
      return this.b;
   }

   @Override
   public long b() {
      return this.c;
   }

   private static double[] h(Av[] var0) {
      double[] var1 = new double[var0.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0[var2].c();
      }

      return var1;
   }

   @Override
   public double[] c() {
      return h(this.d);
   }

   @Override
   public double[] d() {
      return h(this.e);
   }

   @Override
   public double[] e() {
      return h(this.h);
   }

   @Override
   public double[] f() {
      return h(this.j);
   }

   @Override
   public double[] g() {
      double[] var1 = new double[this.b];
      if (this.b() < 1L) {
         Arrays.fill(var1, Double.NaN);
      } else if (this.b() < 2L) {
         Arrays.fill(var1, 0.0);
      } else {
         tq var2 = this.k.a();

         for (int var3 = 0; var3 < this.b; var3++) {
            var1[var3] = BY.a(var2.b(var3, var3));
         }
      }

      return var1;
   }

   @Override
   public tq h() {
      return this.k.a();
   }

   @Override
   public double[] i() {
      return h(this.g);
   }

   @Override
   public double[] j() {
      return h(this.f);
   }

   @Override
   public double[] k() {
      return h(this.i);
   }

   @Override
   public String toString() {
      String var1 = System.getProperty("line.separator");
      StringBuilder var2;
      (var2 = new StringBuilder()).append("MultivariateSummaryStatistics:" + var1);
      var2.append("n: " + this.b() + var1);
      a(var2, this.j(), "min: ", ", ", var1);
      a(var2, this.i(), "max: ", ", ", var1);
      a(var2, this.f(), "mean: ", ", ", var1);
      a(var2, this.k(), "geometric mean: ", ", ", var1);
      a(var2, this.d(), "sum of squares: ", ", ", var1);
      a(var2, this.e(), "sum of logarithms: ", ", ", var1);
      a(var2, this.g(), "standard deviation: ", ", ", var1);
      var2.append("covariance: " + this.h().toString() + var1);
      return var2.toString();
   }

   private static void a(StringBuilder var0, double[] var1, String var2, String var3, String var4) {
      var0.append(var2);

      for (int var5 = 0; var5 < var1.length; var5++) {
         if (var5 > 0) {
            var0.append(var3);
         }

         var0.append(var1[var5]);
      }

      var0.append(var4);
   }

   public void l() {
      this.c = 0L;

      for (int var1 = 0; var1 < this.b; var1++) {
         this.f[var1].b();
         this.g[var1].b();
         this.d[var1].b();
         this.h[var1].b();
         this.e[var1].b();
         this.i[var1].b();
         this.j[var1].b();
      }

      AN var2 = this.k;
      this.k.c = 0L;
      Arrays.fill(var2.a, 0.0);
      Arrays.fill(var2.b, 0.0);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      Ar var2;
      return !(object instanceof Ar)
         ? false
         : Ch.j((var2 = (Ar)object).k(), this.k())
            && Ch.j(var2.i(), this.i())
            && Ch.j(var2.f(), this.f())
            && Ch.j(var2.j(), this.j())
            && Cq.b((float)var2.b(), (float)this.b())
            && Ch.j(var2.c(), this.c())
            && Ch.j(var2.d(), this.d())
            && Ch.j(var2.e(), this.e())
            && var2.h().equals(this.h());
   }

   @Override
   public int hashCode() {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     ((31 + Arrays.hashCode(this.k())) * 31 + Arrays.hashCode(this.k())) * 31
                                                                        + Arrays.hashCode(this.i())
                                                                  )
                                                                  * 31
                                                               + Arrays.hashCode(this.f())
                                                         )
                                                         * 31
                                                      + Arrays.hashCode(this.j())
                                                )
                                                * 31
                                             + Ci.a(this.b())
                                       )
                                       * 31
                                    + Arrays.hashCode(this.c())
                              )
                              * 31
                           + Arrays.hashCode(this.d())
                     )
                     * 31
                  + Arrays.hashCode(this.e())
            )
            * 31
         + this.h().hashCode();
   }

   private void a(Av[] var1, Av[] var2) {
      Ar var3 = this;
      if (this.c > 0L) {
         throw new oX(px.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, var3.c);
      }

      this.a(var1.length);
      System.arraycopy(var1, 0, var2, 0, var1.length);
   }

   public Av[] m() {
      return (Av[])this.d.clone();
   }

   public void a(Av[] var1) {
      this.a(var1, this.d);
   }

   public Av[] n() {
      return (Av[])this.e.clone();
   }

   public void b(Av[] var1) {
      this.a(var1, this.e);
   }

   public Av[] o() {
      return (Av[])this.f.clone();
   }

   public void c(Av[] var1) {
      this.a(var1, this.f);
   }

   public Av[] p() {
      return (Av[])this.g.clone();
   }

   public void d(Av[] var1) {
      this.a(var1, this.g);
   }

   public Av[] q() {
      return (Av[])this.h.clone();
   }

   public void e(Av[] var1) {
      this.a(var1, this.h);
   }

   public Av[] r() {
      return (Av[])this.i.clone();
   }

   public void f(Av[] var1) {
      this.a(var1, this.i);
   }

   public Av[] s() {
      return (Av[])this.j.clone();
   }

   public void g(Av[] var1) {
      this.a(var1, this.j);
   }

   private void t() {
      if (this.c > 0L) {
         throw new oX(px.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, this.c);
      }
   }

   private void a(int var1) {
      if (var1 != this.b) {
         throw new oS(var1, this.b);
      }
   }
}
