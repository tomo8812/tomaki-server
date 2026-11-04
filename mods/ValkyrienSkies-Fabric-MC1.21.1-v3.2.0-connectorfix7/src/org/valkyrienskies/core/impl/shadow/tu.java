package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class tu {
   public abstract int f();

   public abstract double a(int var1);

   public abstract void a(int var1, double var2);

   public void b(int var1, double var2) {
      this.a(var1, this.a(var1) + var2);
   }

   public abstract tu g(tu var1);

   public abstract tu e(double var1);

   public abstract tu a(int var1, int var2);

   public abstract void a(int var1, tu var2);

   public abstract boolean h();

   public abstract boolean i();

   protected void h(tu var1) {
      this.b(var1.f());
   }

   protected void b(int var1) {
      int var2;
      if ((var2 = this.f()) != var1) {
         throw new oS(var2, var1);
      }
   }

   protected final void c(int var1) {
      if (var1 < 0 || var1 >= this.f()) {
         throw new po(px.INDEX, var1, 0, this.f() - 1);
      }
   }

   protected final void b(int var1, int var2) {
      int var3 = this.f();
      if (var1 < 0 || var1 >= var3) {
         throw new po(px.INDEX, var1, 0, var3 - 1);
      }

      if (var2 < 0 || var2 >= var3) {
         throw new po(px.INDEX, var2, 0, var3 - 1);
      }

      if (var2 < var1) {
         throw new pn(px.INITIAL_ROW_AFTER_FINAL_ROW, var2, var1, false);
      }
   }

   public tu l(tu var1) {
      this.h(var1);
      var1 = var1.j();
      Iterator var2 = this.l();

      while (var2.hasNext()) {
         tu.a var3;
         int var4 = (var3 = (tu.a)var2.next()).b();
         var1.a(var4, var3.a() + var1.a(var4));
      }

      return var1;
   }

   public tu k(tu var1) {
      this.h(var1);
      var1 = var1.h(-1.0);
      Iterator var2 = this.l();

      while (var2.hasNext()) {
         tu.a var3;
         int var4 = (var3 = (tu.a)var2.next()).b();
         var1.a(var4, var3.a() + var1.a(var4));
      }

      return var1;
   }

   public tu g(double var1) {
      return this.j().a(var1);
   }

   public tu a(double var1) {
      return var1 != 0.0 ? this.b(kV.a(new lw(), var1)) : this;
   }

   public abstract tu j();

   public double b(tu var1) {
      this.h(var1);
      double var2 = 0.0;
      int var4 = this.f();

      for (int var5 = 0; var5 < var4; var5++) {
         var2 += this.a(var5) * var1.a(var5);
      }

      return var2;
   }

   public double m(tu var1) {
      double var2 = this.c();
      double var4 = var1.c();
      if (var2 != 0.0 && var4 != 0.0) {
         return this.b(var1) / (var2 * var4);
      } else {
         throw new oU(px.ZERO_NORM);
      }
   }

   public abstract tu j(tu var1);

   public abstract tu i(tu var1);

   public double c(tu var1) {
      this.h(var1);
      double var2 = 0.0;
      Iterator var4 = this.l();

      while (var4.hasNext()) {
         tu.a var5;
         double var6 = (var5 = (tu.a)var4.next()).a() - var1.a(var5.b());
         var2 += var6 * var6;
      }

      return BY.a(var2);
   }

   public double c() {
      double var1 = 0.0;
      Iterator var3 = this.l();

      while (var3.hasNext()) {
         double var5 = ((tu.a)var3.next()).a();
         var1 += var5 * var5;
      }

      return BY.a(var1);
   }

   public double d() {
      double var1 = 0.0;
      Iterator var3 = this.l();

      while (var3.hasNext()) {
         tu.a var4 = (tu.a)var3.next();
         var1 += BY.w(var4.a());
      }

      return var1;
   }

   public double e() {
      double var1 = 0.0;
      Iterator var3 = this.l();

      while (var3.hasNext()) {
         tu.a var4 = (tu.a)var3.next();
         var1 = BY.e(var1, BY.w(var4.a()));
      }

      return var1;
   }

   public double d(tu var1) {
      this.h(var1);
      double var2 = 0.0;
      Iterator var4 = this.l();

      while (var4.hasNext()) {
         tu.a var5 = (tu.a)var4.next();
         var2 += BY.w(var5.a() - var1.a(var5.b()));
      }

      return var2;
   }

   public double e(tu var1) {
      this.h(var1);
      double var2 = 0.0;
      Iterator var4 = this.l();

      while (var4.hasNext()) {
         tu.a var5;
         var2 = BY.e(BY.w((var5 = (tu.a)var4.next()).a() - var1.a(var5.b())), var2);
      }

      return var2;
   }

   private int m() {
      int var1 = -1;
      double var2 = Double.POSITIVE_INFINITY;
      Iterator var4 = this.l();

      while (var4.hasNext()) {
         tu.a var5;
         if ((var5 = (tu.a)var4.next()).a() <= var2) {
            var1 = var5.b();
            var2 = var5.a();
         }
      }

      return var1;
   }

   private double n() {
      tu var1 = this;
      int var2 = -1;
      double var4 = Double.POSITIVE_INFINITY;
      Iterator var6 = var1.l();

      while (var6.hasNext()) {
         tu.a var3;
         if ((var3 = (tu.a)var6.next()).a() <= var4) {
            var2 = var3.b();
            var4 = var3.a();
         }
      }

      int var7 = var2;
      return var2 < 0 ? Double.NaN : this.a(var7);
   }

   private int o() {
      int var1 = -1;
      double var2 = Double.NEGATIVE_INFINITY;
      Iterator var4 = this.l();

      while (var4.hasNext()) {
         tu.a var5;
         if ((var5 = (tu.a)var4.next()).a() >= var2) {
            var1 = var5.b();
            var2 = var5.a();
         }
      }

      return var1;
   }

   private double p() {
      tu var1 = this;
      int var2 = -1;
      double var4 = Double.NEGATIVE_INFINITY;
      Iterator var6 = var1.l();

      while (var6.hasNext()) {
         tu.a var3;
         if ((var3 = (tu.a)var6.next()).a() >= var4) {
            var2 = var3.b();
            var4 = var3.a();
         }
      }

      int var7 = var2;
      return var2 < 0 ? Double.NaN : this.a(var7);
   }

   public tu h(double var1) {
      return this.j().c(var1);
   }

   public tu c(double var1) {
      return this.b(kV.a(new lX(), var1));
   }

   public tu i(double var1) {
      return this.j().b(var1);
   }

   public tu b(double var1) {
      return this.a(-var1);
   }

   public tu j(double var1) {
      return this.j().d(var1);
   }

   public tu d(double var1) {
      return this.b(kV.a(new lH(), var1));
   }

   public tq f(tu var1) {
      int var2 = this.f();
      int var3 = var1.f();
      sv var4;
      if (!(var1 instanceof tG) && !(this instanceof tG)) {
         var4 = new sy(var2, var3);
      } else {
         var4 = new tk(var2, var3);
      }

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var4.c(var5, var6, this.a(var5) * var1.a(var6));
         }
      }

      return var4;
   }

   private tu a(tu var1) {
      if (var1.b(var1) == 0.0) {
         throw new oU(px.ZERO_NORM);
      } else {
         return var1.h(this.b(var1) / var1.b(var1));
      }
   }

   public void f(double var1) {
      Iterator var3 = this.l();

      while (var3.hasNext()) {
         ((tu.a)var3.next()).a(0.0);
      }
   }

   public double[] g() {
      int var1;
      double[] var2 = new double[var1 = this.f()];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = this.a(var3);
      }

      return var2;
   }

   public tu k() {
      double var1;
      if ((var1 = this.c()) == 0.0) {
         throw new oU(px.ZERO_NORM);
      } else {
         return this.j(var1);
      }
   }

   public void d_() {
      if (this.c() == 0.0) {
         throw new oU(px.ZERO_NORM);
      }

      this.d(this.c());
   }

   public Iterator<tu.a> e_() {
      return new tu.b();
   }

   public Iterator<tu.a> l() {
      final int var1 = this.f();
      return new Iterator<tu.a>() {
         private int a = 0;
         private tu.a b = tu.this.new a();

         @Override
         public final boolean hasNext() {
            return this.a < var1;
         }

         private tu.a a() {
            if (this.a < var1) {
               tu.a var10000 = this.b;
               int var2 = this.a++;
               var10000.a = var2;
               return this.b;
            } else {
               throw new NoSuchElementException();
            }
         }

         @Override
         public final void remove() {
            throw new pb();
         }

         // $VF: synthetic method
         @Override
         public final Object next() {
            var var1x = this;
            if (this.a < var1) {
               tu.a var10000 = var1x.b;
               int var3 = var1x.a++;
               var10000.a = var3;
               return var1x.b;
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   public tu c(lc var1) {
      return this.j().b(var1);
   }

   public tu b(lc var1) {
      Iterator var2 = this.l();

      while (var2.hasNext()) {
         tu.a var3;
         (var3 = (tu.a)var2.next()).a(var1.a(var3.a()));
      }

      return this;
   }

   public tu b(double var1, double var3, tu var5) {
      return this.j().a(var1, var3, var5);
   }

   public tu a(double var1, double var3, tu var5) {
      this.h(var5);

      for (int var6 = 0; var6 < this.f(); var6++) {
         double var7 = this.a(var6);
         double var9 = var5.a(var6);
         this.a(var6, var1 * var7 + var3 * var9);
      }

      return this;
   }

   public double a(tx var1) {
      int var2 = this.f();

      for (int var3 = 0; var3 < var2; var3++) {
         this.a(var3);
      }

      return var1.c();
   }

   public double a(tx var1, int var2, int var3) {
      this.b(var2, var3);
      this.f();

      for (int var4 = var2; var4 <= var3; var4++) {
         this.a(var4);
      }

      return var1.c();
   }

   public double b(tx var1) {
      return this.a(var1);
   }

   public double b(tx var1, int var2, int var3) {
      return this.a(var1, var2, var3);
   }

   public double a(tv var1) {
      int var2 = this.f();

      for (int var3 = 0; var3 < var2; var3++) {
         this.a(var3);
         this.a(var3, var1.b());
      }

      return var1.c();
   }

   public double a(tv var1, int var2, int var3) {
      this.b(var2, var3);
      this.f();

      for (int var4 = var2; var4 <= var3; var4++) {
         this.a(var4);
         this.a(var4, var1.b());
      }

      return var1.c();
   }

   public double b(tv var1) {
      return this.a(var1);
   }

   public double b(tv var1, int var2, int var3) {
      return this.a(var1, var2, var3);
   }

   @Override
   public boolean equals(Object other) {
      throw new pb();
   }

   @Override
   public int hashCode() {
      throw new pb();
   }

   public static <unrepresentable> n(final tu var0) {
      return new tu() {
         @Override
         public final tu b(lc var1) {
            throw new pb();
         }

         @Override
         public final tu c(lc var1) {
            return var0.c(var1);
         }

         @Override
         public final Iterator<tu.a> l() {
            final Iterator var1 = var0.l();
            return new Iterator<tu.a>() {
               private final a a = <VAR_NAMELESS_ENCLOSURE>.new a();

               @Override
               public final boolean hasNext() {
                  return var1.hasNext();
               }

               private a a() {
                  a var10000 = this.a;
                  int var2 = ((tu.a)var1.next()).b();
                  var10000.a = var2;
                  return this.a;
               }

               @Override
               public final void remove() {
                  throw new pb();
               }

               // $VF: synthetic method
               @Override
               public final Object next() {
                  var var1x = this;
                  a var10000 = this.a;
                  int var3 = ((tu.a)var1.next()).b();
                  var10000.a = var3;
                  return var1x.a;
               }
            };
         }

         @Override
         public final Iterator<tu.a> e_() {
            final Iterator var1 = var0.e_();
            return new Iterator<tu.a>() {
               private final a a = <VAR_NAMELESS_ENCLOSURE>.new a();

               @Override
               public final boolean hasNext() {
                  return var1.hasNext();
               }

               private a a() {
                  a var10000 = this.a;
                  int var2 = ((tu.a)var1.next()).b();
                  var10000.a = var2;
                  return this.a;
               }

               @Override
               public final void remove() {
                  throw new pb();
               }

               // $VF: synthetic method
               @Override
               public final Object next() {
                  var var1x = this;
                  a var10000 = this.a;
                  int var3 = ((tu.a)var1.next()).b();
                  var10000.a = var3;
                  return var1x.a;
               }
            };
         }

         @Override
         public final tu j() {
            return var0.j();
         }

         @Override
         public final tu l(tu var1) {
            return var0.l(var1);
         }

         @Override
         public final tu k(tu var1) {
            return var0.k(var1);
         }

         @Override
         public final tu g(double var1) {
            return var0.g(var1);
         }

         @Override
         public final tu a(double var1) {
            throw new pb();
         }

         @Override
         public final tu i(double var1) {
            return var0.i(var1);
         }

         @Override
         public final tu b(double var1) {
            throw new pb();
         }

         @Override
         public final tu h(double var1) {
            return var0.h(var1);
         }

         @Override
         public final tu c(double var1) {
            throw new pb();
         }

         @Override
         public final tu j(double var1) {
            return var0.j(var1);
         }

         @Override
         public final tu d(double var1) {
            throw new pb();
         }

         @Override
         public final tu i(tu var1) {
            return var0.i(var1);
         }

         @Override
         public final tu j(tu var1) {
            return var0.j(var1);
         }

         @Override
         public final double b(tu var1) {
            return var0.b(var1);
         }

         @Override
         public final double m(tu var1) {
            return var0.m(var1);
         }

         @Override
         public final double c() {
            return var0.c();
         }

         @Override
         public final double d() {
            return var0.d();
         }

         @Override
         public final double e() {
            return var0.e();
         }

         @Override
         public final double c(tu var1) {
            return var0.c(var1);
         }

         @Override
         public final double d(tu var1) {
            return var0.d(var1);
         }

         @Override
         public final double e(tu var1) {
            return var0.e(var1);
         }

         @Override
         public final tu k() {
            return var0.k();
         }

         @Override
         public final void d_() {
            throw new pb();
         }

         @Override
         public final tq f(tu var1) {
            return var0.f(var1);
         }

         @Override
         public final double a(int var1) {
            return var0.a(var1);
         }

         @Override
         public final void a(int var1, double var2) {
            throw new pb();
         }

         @Override
         public final void b(int var1, double var2) {
            throw new pb();
         }

         @Override
         public final int f() {
            return var0.f();
         }

         @Override
         public final tu g(tu var1) {
            return var0.g(var1);
         }

         @Override
         public final tu e(double var1) {
            return var0.e(var1);
         }

         @Override
         public final tu a(int var1, int var2) {
            return var0.a(var1, var2);
         }

         @Override
         public final void a(int var1, tu var2) {
            throw new pb();
         }

         @Override
         public final void f(double var1) {
            throw new pb();
         }

         @Override
         public final double[] g() {
            return var0.g();
         }

         @Override
         public final boolean h() {
            return var0.h();
         }

         @Override
         public final boolean i() {
            return var0.i();
         }

         @Override
         public final tu b(double var1, double var3, tu var5) {
            return var0.b(var1, var3, var5);
         }

         @Override
         public final tu a(double var1, double var3, tu var5) {
            throw new pb();
         }

         class a extends tu.a {
            @Override
            public final double a() {
               return var0.a(this.b());
            }

            @Override
            public final void a(double var1) {
               throw new pb();
            }
         }
      };
   }

   protected class a {
      int a;

      public a() {
         byte var2 = 0;
         this.a = var2;
      }

      public double a() {
         return tu.this.a(this.b());
      }

      public void a(double var1) {
         tu.this.a(this.b(), var1);
      }

      public int b() {
         return this.a;
      }

      public final void a(int var1) {
         this.a = var1;
      }
   }

   protected class b implements Iterator<tu.a> {
      private final int a = tu.this.f();
      private tu.a b = tu.this.new a();
      private tu.a c = tu.this.new a();

      protected b() {
         if (this.c.a() == 0.0) {
            this.a(this.c);
         }
      }

      private void a(tu.a var1) {
         if (var1 != null) {
            do {
               int var3 = var1.b() + 1;
               var1.a = var3;
            } while (var1.b() < this.a && var1.a() == 0.0);

            if (var1.b() >= this.a) {
               byte var4 = -1;
               var1.a = var4;
            }
         }
      }

      @Override
      public final boolean hasNext() {
         return this.c.b() >= 0;
      }

      private tu.a a() {
         int var1;
         if ((var1 = this.c.b()) < 0) {
            throw new NoSuchElementException();
         }

         int var2 = var1;
         tu.a var3 = this.b;
         this.b.a = var2;
         this.a(this.c);
         return this.b;
      }

      @Override
      public final void remove() {
         throw new pb();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         tu.b var1 = this;
         int var2;
         if ((var2 = this.c.b()) < 0) {
            throw new NoSuchElementException();
         }

         int var3 = var2;
         tu.a var4 = var1.b;
         var1.b.a = var3;
         var1.a(var1.c);
         return var1.b;
      }
   }
}
