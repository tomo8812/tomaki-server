package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public class Aw implements Serializable, At {
   private static final long b = -2021321786743555871L;
   private long c = 0L;
   AH a = new AH();
   private AV d = new AV();
   private AX e = new AX();
   private AR f = new AR();
   private AP g = new AP();
   private AW h = new AW();
   private AE i = new AE(this.h);
   private AG j = new AG(this.a);
   private AM k = new AM(this.a);
   private Av l = this.d;
   private Av m = this.e;
   private Av n = this.f;
   private Av o = this.g;
   private Av p = this.h;
   private Av q = this.i;
   private Av r = this.j;
   private Av s = this.k;

   public Aw() {
   }

   public Aw(Aw var1) {
      a(var1, this);
   }

   public At h() {
      return new Au(this.b(), this.g(), this.d(), this.a(), this.c(), this.f());
   }

   public void a(double var1) {
      this.l.a(var1);
      this.m.a(var1);
      this.n.a(var1);
      this.o.a(var1);
      this.p.a(var1);
      this.a.a(var1);
      if (this.r != this.j) {
         this.r.a(var1);
      }

      if (this.s != this.k) {
         this.s.a(var1);
      }

      if (this.q != this.i) {
         this.q.a(var1);
      }

      this.c++;
   }

   @Override
   public long d() {
      return this.c;
   }

   @Override
   public double f() {
      return this.l.c();
   }

   public double i() {
      return this.m.c();
   }

   @Override
   public double b() {
      return this.r.c();
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

   public double j() {
      long var1;
      return (var1 = this.d()) > 0L ? BY.a(this.i() / var1) : Double.NaN;
   }

   @Override
   public double g() {
      return this.s.c();
   }

   public double k() {
      AM var1;
      AM var10000 = var1 = new AM(this.a);
      boolean var3 = false;
      var10000.b = var3;
      return var1.c();
   }

   @Override
   public double a() {
      return this.o.c();
   }

   @Override
   public double c() {
      return this.n.c();
   }

   public double l() {
      return this.q.c();
   }

   public final double m() {
      return this.p.c();
   }

   public final double n() {
      return this.a.c();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = "\n";
      var1.append("SummaryStatistics:").append(var2);
      var1.append("n: ").append(this.d()).append(var2);
      var1.append("min: ").append(this.c()).append(var2);
      var1.append("max: ").append(this.a()).append(var2);
      var1.append("sum: ").append(this.f()).append(var2);
      var1.append("mean: ").append(this.b()).append(var2);
      var1.append("geometric mean: ").append(this.l()).append(var2);
      var1.append("variance: ").append(this.g()).append(var2);
      var1.append("population variance: ").append(this.k()).append(var2);
      var1.append("second moment: ").append(this.a.c()).append(var2);
      var1.append("sum of squares: ").append(this.i()).append(var2);
      var1.append("standard deviation: ").append(this.e()).append(var2);
      var1.append("sum of logs: ").append(this.m()).append(var2);
      return var1.toString();
   }

   public void o() {
      this.c = 0L;
      this.n.b();
      this.o.b();
      this.l.b();
      this.p.b();
      this.m.b();
      this.q.b();
      this.a.b();
      if (this.r != this.j) {
         this.r.b();
      }

      if (this.s != this.k) {
         this.s.b();
      }
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      }

      Aw var2;
      return !(object instanceof Aw)
         ? false
         : Cq.b((var2 = (Aw)object).l(), this.l())
            && Cq.b(var2.a(), this.a())
            && Cq.b(var2.b(), this.b())
            && Cq.b(var2.c(), this.c())
            && Cq.b((float)var2.d(), (float)this.d())
            && Cq.b(var2.f(), this.f())
            && Cq.b(var2.i(), this.i())
            && Cq.b(var2.g(), this.g());
   }

   @Override
   public int hashCode() {
      return (
               (
                        (
                                 (((((31 + Ci.a(this.l())) * 31 + Ci.a(this.l())) * 31 + Ci.a(this.a())) * 31 + Ci.a(this.b())) * 31 + Ci.a(this.c())) * 31
                                    + Ci.a(this.d())
                              )
                              * 31
                           + Ci.a(this.f())
                     )
                     * 31
                  + Ci.a(this.i())
            )
            * 31
         + Ci.a(this.g());
   }

   public Av p() {
      return this.l;
   }

   public void a(Av var1) {
      this.y();
      this.l = var1;
   }

   public Av q() {
      return this.m;
   }

   public void b(Av var1) {
      this.y();
      this.m = var1;
   }

   public Av r() {
      return this.n;
   }

   public void c(Av var1) {
      this.y();
      this.n = var1;
   }

   public Av s() {
      return this.o;
   }

   public void d(Av var1) {
      this.y();
      this.o = var1;
   }

   public Av t() {
      return this.p;
   }

   public void e(Av var1) {
      this.y();
      this.p = var1;
      Av var2 = var1;
      var1 = this.i;
      AE var3 = this.i;
      if (this.i.g_() > 0L) {
         throw new oX(px.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, var3.g_());
      }

      var1.a = var2;
   }

   public Av u() {
      return this.q;
   }

   public void f(Av var1) {
      this.y();
      this.q = var1;
   }

   public Av v() {
      return this.r;
   }

   public void g(Av var1) {
      this.y();
      this.r = var1;
   }

   public Av w() {
      return this.s;
   }

   public void h(Av var1) {
      this.y();
      this.s = var1;
   }

   private void y() {
      if (this.c > 0L) {
         throw new oX(px.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, this.c);
      }
   }

   public Aw x() {
      Aw var1 = new Aw();
      a(this, var1);
      return var1;
   }

   public static void a(Aw var0, Aw var1) {
      Ci.a(var0);
      Ci.a(var1);
      var1.o = var0.o.a();
      var1.n = var0.n.a();
      var1.l = var0.l.a();
      var1.p = var0.p.a();
      var1.m = var0.m.a();
      var1.a = var0.a.i();
      var1.c = var0.c;
      if (var0.w() instanceof AM) {
         var1.s = new AM(var1.a);
      } else {
         var1.s = var0.s.a();
      }

      if (var0.r instanceof AG) {
         var1.r = new AG(var1.a);
      } else {
         var1.r = var0.r.a();
      }

      if (var0.u() instanceof AE) {
         var1.q = new AE((AW)var1.p);
      } else {
         var1.q = var0.q.a();
      }

      if (var0.i == var0.q) {
         var1.i = (AE)var1.q;
      } else {
         AE.a(var0.i, var1.i);
      }

      if (var0.g == var0.o) {
         var1.g = (AP)var1.o;
      } else {
         AP.a(var0.g, var1.g);
      }

      if (var0.j == var0.r) {
         var1.j = (AG)var1.r;
      } else {
         AG.a(var0.j, var1.j);
      }

      if (var0.f == var0.n) {
         var1.f = (AR)var1.n;
      } else {
         AR.a(var0.f, var1.f);
      }

      if (var0.d == var0.l) {
         var1.d = (AV)var1.l;
      } else {
         AV.a(var0.d, var1.d);
      }

      if (var0.k == var0.s) {
         var1.k = (AM)var1.s;
      } else {
         AM.a(var0.k, var1.k);
      }

      if (var0.h == var0.p) {
         var1.h = (AW)var1.p;
      } else {
         AW.a(var0.h, var1.h);
      }

      if (var0.e == var0.m) {
         var1.e = (AX)var1.m;
      } else {
         AX.a(var0.e, var1.e);
      }
   }
}
