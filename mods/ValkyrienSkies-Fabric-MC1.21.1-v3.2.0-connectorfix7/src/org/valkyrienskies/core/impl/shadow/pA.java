package org.valkyrienskies.core.impl.shadow;

public final class pA {
   private final pC a;
   private final pB b;
   private tq c;
   private tq d;
   private tq e;
   private tq f;
   private tq g;
   private tu h;
   private tq i;

   private pA(pC var1, pB var2) {
      Ci.a(var1);
      Ci.a(var2);
      this.a = var1;
      this.b = var2;
      this.c = this.a.a();
      Ci.a(this.c);
      this.d = this.c.d();
      if (this.a.b() == null) {
         this.e = new sy();
      } else {
         this.e = this.a.b();
      }

      this.f = this.b.a();
      Ci.a(this.f);
      this.g = this.f.d();
      tq var3;
      Ci.a(var3 = this.a.c());
      tq var4;
      Ci.a(var4 = this.b.b());
      if (this.a.d() == null) {
         this.h = new sA(this.c.g());
      } else {
         this.h = this.a.d();
      }

      if (this.c.g() != this.h.f()) {
         throw new oS(this.c.g(), this.h.f());
      }

      if (this.a.e() == null) {
         this.i = var3.i();
      } else {
         this.i = this.a.e();
      }

      if (!this.c.e()) {
         throw new th(this.c.f(), this.c.g());
      }

      if (this.e != null && this.e.f() > 0 && this.e.g() > 0 && this.e.f() != this.c.f()) {
         throw new tc(this.e.f(), this.e.g(), this.c.f(), this.e.g());
      }

      td.a(this.c, var3);
      if (this.f.g() != this.c.f()) {
         throw new tc(this.f.f(), this.f.g(), this.f.f(), this.c.f());
      }

      if (var4.f() != this.f.f()) {
         throw new tc(var4.f(), var4.g(), this.f.f(), var4.g());
      }
   }

   private int a() {
      return this.h.f();
   }

   private int b() {
      return this.f.f();
   }

   private double[] c() {
      return this.h.g();
   }

   private tu d() {
      return this.h.j();
   }

   private double[][] e() {
      return this.i.a();
   }

   private tq f() {
      return this.i.i();
   }

   private void g() {
      this.a((tu)null);
   }

   private void a(double[] var1) {
      this.a((tu)(new sA(var1, false)));
   }

   private void a(tu var1) {
      if (var1 != null && var1.f() != this.e.g()) {
         throw new oS(var1.f(), this.e.g());
      }

      this.h = this.c.a(this.h);
      if (var1 != null) {
         this.h = this.h.l(this.e.a(var1));
      }

      this.i = this.c.c(this.i).c(this.d).a(this.a.c());
   }

   private void b(double[] var1) {
      sA var2 = new sA(var1, false);
      pA var4 = this;
      Ci.a(var2);
      if (var2.f() != var4.f.f()) {
         throw new oS(var2.f(), var4.f.f());
      }

      tq var3 = var4.f.c(var4.i).c(var4.g).a(var4.b.b());
      tu var5 = var2.k(var4.f.a(var4.h));
      var3 = new sE(var3).a().a(var4.f.c(var4.i.d())).d();
      var4.h = var4.h.l(var3.a(var5));
      tq var6 = td.a(var3.f());
      var4.i = var6.b(var3.c(var4.f)).c(var4.i);
   }

   private void a(sA var1) {
      Ci.a(var1);
      if (var1.f() != this.f.f()) {
         throw new oS(var1.f(), this.f.f());
      }

      tq var2 = this.f.c(this.i).c(this.g).a(this.b.b());
      tu var3 = var1.k(this.f.a(this.h));
      var2 = new sE(var2).a().a(this.f.c(this.i.d())).d();
      this.h = this.h.l(var2.a(var3));
      tq var4 = td.a(var2.f());
      this.i = var4.b(var2.c(this.f)).c(this.i);
   }
}
