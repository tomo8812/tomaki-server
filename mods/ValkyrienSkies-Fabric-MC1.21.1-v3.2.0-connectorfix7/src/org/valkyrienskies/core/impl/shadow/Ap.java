package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public final class Ap implements Serializable, At {
   private static final long a = -8207112444016386906L;
   private final Aw b;
   private final Aw c;

   public Ap() {
      this(new Aw());
   }

   private Ap(Aw var1) {
      this(var1, new Aw(var1));
   }

   private Ap(Aw var1, Aw var2) {
      this.b = var1 == null ? new Aw() : var1;
      this.c = var2 == null ? new Aw() : var2;
   }

   @Override
   public final double a() {
      synchronized (this.c) {
         return this.c.a();
      }
   }

   @Override
   public final double b() {
      synchronized (this.c) {
         return this.c.b();
      }
   }

   @Override
   public final double c() {
      synchronized (this.c) {
         return this.c.c();
      }
   }

   @Override
   public final long d() {
      synchronized (this.c) {
         return this.c.d();
      }
   }

   @Override
   public final double e() {
      synchronized (this.c) {
         return this.c.e();
      }
   }

   @Override
   public final double f() {
      synchronized (this.c) {
         return this.c.f();
      }
   }

   @Override
   public final double g() {
      synchronized (this.c) {
         return this.c.g();
      }
   }

   private double h() {
      synchronized (this.c) {
         return this.c.m();
      }
   }

   private double i() {
      synchronized (this.c) {
         return this.c.l();
      }
   }

   private double j() {
      synchronized (this.c) {
         return this.c.i();
      }
   }

   private double k() {
      synchronized (this.c) {
         Aw var2 = this.c;
         return this.c.a.c();
      }
   }

   private Au l() {
      synchronized (this.c) {
         return new Au(this.b(), this.g(), this.d(), this.a(), this.c(), this.f());
      }
   }

   private Ap.a m() {
      Ap.a var1 = new Ap.a(this.c);
      Aw.a(this.b, var1);
      return var1;
   }

   private static Au a(Collection<? extends At> var0) {
      if (var0 == null) {
         return null;
      }

      Iterator var25;
      if (!(var25 = var0.iterator()).hasNext()) {
         return null;
      }

      At var1;
      long var3 = (var1 = (At)var25.next()).d();
      double var5 = var1.c();
      double var7 = var1.f();
      double var9 = var1.a();
      double var13 = var1.g() * (var3 - 1.0);
      double var15 = var1.b();

      while (var25.hasNext()) {
         if ((var1 = (At)var25.next()).c() < var5 || Double.isNaN(var5)) {
            var5 = var1.c();
         }

         if (var1.a() > var9 || Double.isNaN(var9)) {
            var9 = var1.a();
         }

         var7 += var1.f();
         double var17 = var3;
         double var19 = var1.d();
         var3 = (long)(var3 + var19);
         double var21 = var1.b() - var15;
         var15 = var7 / var3;
         double var23 = var1.g() * (var19 - 1.0);
         var13 = var13 + var23 + var21 * var21 * var17 * var19 / var3;
      }

      double var27;
      if (var3 == 0L) {
         var27 = Double.NaN;
      } else if (var3 == 1L) {
         var27 = 0.0;
      } else {
         var27 = var13 / (var3 - 1L);
      }

      return new Au(var15, var27, var3, var9, var5, var7);
   }

   static class a extends Aw {
      private static final long b = 1L;
      private final Aw c;

      a(Aw var1) {
         this.c = var1;
      }

      @Override
      public final void a(double var1) {
         super.a(var1);
         synchronized (this.c) {
            this.c.a(var1);
         }
      }

      @Override
      public final boolean equals(Object object) {
         if (object == this) {
            return true;
         }

         if (!(object instanceof Ap.a)) {
            return false;
         }

         Ap.a var2 = (Ap.a)object;
         return super.equals(var2) && this.c.equals(var2.c);
      }

      @Override
      public final int hashCode() {
         return 123 + super.hashCode() + this.c.hashCode();
      }
   }
}
