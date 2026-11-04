package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Aq implements Serializable, At {
   private static int a;
   private static final long b = 4133067267405273064L;
   private static final String c = "setQuantile";
   private int d = -1;
   private Cs e = new Cs();
   private AA f = new AG();
   private AA g = new AE();
   private AA h = new AF();
   private AA i = new AP();
   private AA j = new AR();
   private AA k = new AT();
   private AA l = new AJ();
   private AA m = new AM();
   private AA n = new AX();
   private AA o = new AV();

   public Aq() {
   }

   public Aq(int var1) {
      this.a(var1);
   }

   private Aq(double[] var1) {
      if (var1 != null) {
         this.e = new Cs(var1);
      }
   }

   private Aq(Aq var1) {
      a(var1, this);
   }

   public void a(double var1) {
      if (this.d != -1) {
         if (this.d() == this.d) {
            this.e.b(var1);
            return;
         }

         if (this.d() >= this.d) {
            return;
         }
      }

      this.e.a(var1);
   }

   private void m() {
      try {
         this.e.c(1);
      } catch (oV var1) {
         throw new oX(px.NO_DATA);
      }
   }

   private double b(double var1) {
      return this.e.c(var1);
   }

   @Override
   public final double b() {
      return this.a(this.f);
   }

   private double n() {
      return this.a(this.g);
   }

   @Override
   public final double g() {
      return this.a(this.m);
   }

   private double o() {
      return this.a(new AM(false));
   }

   @Override
   public double e() {
      double var1 = Double.NaN;
      if (this.d() > 0L) {
         if (this.d() > 1L) {
            var1 = BY.a(this.g());
         } else {
            var1 = 0.0;
         }
      }

      return var1;
   }

   public double h() {
      long var1;
      if ((var1 = this.d()) > 0L) {
         Aq var3 = this;
         return BY.a(this.a(var3.n) / var1);
      } else {
         return Double.NaN;
      }
   }

   private double p() {
      return this.a(this.l);
   }

   private double q() {
      return this.a(this.h);
   }

   @Override
   public final double a() {
      return this.a(this.i);
   }

   @Override
   public final double c() {
      return this.a(this.j);
   }

   @Override
   public long d() {
      return this.e.a();
   }

   @Override
   public final double f() {
      return this.a(this.o);
   }

   private double r() {
      return this.a(this.n);
   }

   public void i() {
      this.e.c();
   }

   public int j() {
      return this.d;
   }

   public void a(int var1) {
      if (var1 <= 0 && var1 != -1) {
         throw new oV(px.NOT_POSITIVE_WINDOW_SIZE, var1);
      }

      this.d = var1;
      if (var1 != -1 && var1 < this.e.a()) {
         this.e.b(this.e.a() - var1);
      }
   }

   public double[] k() {
      return this.e.b();
   }

   private double[] s() {
      double[] var1;
      Arrays.sort(var1 = this.k());
      return var1;
   }

   public double b(int var1) {
      return this.e.a(var1);
   }

   private double c(double var1) {
      if (this.k instanceof AT) {
         ((AT)this.k).a(50.0);
      } else {
         try {
            this.k.getClass().getMethod("setQuantile", double.class).invoke(this.k, 50.0);
         } catch (NoSuchMethodException var3) {
            throw new oX(px.PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD, this.k.getClass().getName(), "setQuantile");
         } catch (IllegalAccessException var4) {
            throw new oX(px.PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD, "setQuantile", this.k.getClass().getName());
         } catch (InvocationTargetException var5) {
            throw new IllegalStateException(var5.getCause());
         }
      }

      return this.a(this.k);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = "\n";
      var1.append("DescriptiveStatistics:").append(var2);
      var1.append("n: ").append(this.d()).append(var2);
      var1.append("min: ").append(this.c()).append(var2);
      var1.append("max: ").append(this.a()).append(var2);
      var1.append("mean: ").append(this.b()).append(var2);
      var1.append("std dev: ").append(this.e()).append(var2);

      try {
         var1.append("median: ").append(this.c(50.0)).append(var2);
      } catch (oX var4) {
         var1.append("median: unavailable").append(var2);
      }

      StringBuilder var10000 = var1.append("skewness: ");
      Aq var3 = this;
      var10000.append(this.a(var3.l)).append(var2);
      var10000 = var1.append("kurtosis: ");
      var3 = this;
      var10000.append(this.a(var3.h)).append(var2);
      return var1.toString();
   }

   public double a(AA var1) {
      return this.e.a(var1);
   }

   private synchronized AA t() {
      return this.f;
   }

   private synchronized void b(AA var1) {
      this.f = var1;
   }

   private synchronized AA u() {
      return this.g;
   }

   private synchronized void c(AA var1) {
      this.g = var1;
   }

   private synchronized AA v() {
      return this.h;
   }

   private synchronized void d(AA var1) {
      this.h = var1;
   }

   private synchronized AA w() {
      return this.i;
   }

   private synchronized void e(AA var1) {
      this.i = var1;
   }

   private synchronized AA x() {
      return this.j;
   }

   private synchronized void f(AA var1) {
      this.j = var1;
   }

   private synchronized AA y() {
      return this.k;
   }

   private synchronized void g(AA var1) {
      try {
         var1.getClass().getMethod("setQuantile", double.class).invoke(var1, 50.0);
      } catch (NoSuchMethodException var2) {
         throw new oV(px.PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD, var1.getClass().getName(), "setQuantile");
      } catch (IllegalAccessException var3) {
         throw new oV(px.PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD, "setQuantile", var1.getClass().getName());
      } catch (InvocationTargetException var4) {
         throw new IllegalArgumentException(var4.getCause());
      }

      this.k = var1;
   }

   private synchronized AA z() {
      return this.l;
   }

   private synchronized void h(AA var1) {
      this.l = var1;
   }

   private synchronized AA A() {
      return this.m;
   }

   private synchronized void i(AA var1) {
      this.m = var1;
   }

   private synchronized AA B() {
      return this.n;
   }

   private synchronized void j(AA var1) {
      this.n = var1;
   }

   private synchronized AA C() {
      return this.o;
   }

   private synchronized void k(AA var1) {
      this.o = var1;
   }

   public Aq l() {
      Aq var1 = new Aq();
      a(this, var1);
      return var1;
   }

   public static void a(Aq var0, Aq var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.e = var0.e.d();
      var1.d = var0.d;
      var1.i = var0.i.d();
      var1.f = var0.f.d();
      var1.j = var0.j.d();
      var1.o = var0.o.d();
      var1.m = var0.m.d();
      var1.n = var0.n.d();
      var1.g = var0.g.d();
      var1.h = var0.h;
      var1.l = var0.l;
      var1.k = var0.k;
   }
}
