package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class qW extends rK<qU, qU> implements Iterable<double[]> {
   private static final double b = 1.0E-10;

   public qW(double var1) {
      super(var1);
   }

   public qW(double var1, double var3, double var5) {
      double var11 = var5;
      double var9 = var3;
      double var7 = var1;
      rM var10001;
      if (!Double.isInfinite(var1) || !(var7 < 0.0)) {
         qY var14 = new qX(new qZ(var7), false, var11).a();
         if (Double.isInfinite(var9) && var9 > 0.0) {
            var10001 = new rM<>(var14, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null);
         } else {
            qY var2 = new qX(new qZ(var9), true, var11).a();
            var10001 = new rM<>(var14, new rM<>(Boolean.FALSE), new rM<>(var2, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null), null);
         }
      } else if (Double.isInfinite(var9) && var9 > 0.0) {
         var10001 = new rM(Boolean.TRUE);
      } else {
         qY var13 = new qX(new qZ(var9), true, var11).a();
         var10001 = new rM<>(var13, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null);
      }

      super(var10001, var5);
   }

   public qW(rM<qU> var1, double var2) {
      super(var1, var2);
   }

   private qW(Collection<sb<qU>> var1, double var2) {
      super(var1, 1.0E-10);
   }

   @Deprecated
   public qW() {
      this(1.0E-10);
   }

   @Deprecated
   private qW(double var1, double var3) {
      this(var1, var3, 1.0E-10);
   }

   @Deprecated
   private qW(rM<qU> var1) {
      this(var1, 1.0E-10);
   }

   @Deprecated
   private qW(Collection<sb<qU>> var1) {
      this(var1, 1.0E-10);
   }

   private static rM<qU> a(double var0, double var2, double var4) {
      if (!Double.isInfinite(var0) || !(var0 < 0.0)) {
         qY var7 = new qX(new qZ(var0), false, var4).a();
         if (Double.isInfinite(var2) && var2 > 0.0) {
            return new rM<>(var7, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null);
         }

         qY var1 = new qX(new qZ(var2), true, var4).a();
         return new rM<>(var7, new rM<>(Boolean.FALSE), new rM<>(var1, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null), null);
      } else {
         if (Double.isInfinite(var2) && var2 > 0.0) {
            return new rM<>(Boolean.TRUE);
         }

         qY var6 = new qX(new qZ(var2), true, var4).a();
         return new rM<>(var6, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null);
      }
   }

   private qW e(rM<qU> var1) {
      return new qW(var1, this.c());
   }

   @Override
   public final void a() {
      if (this.a(false).a == null) {
         this.c(qZ.c);
         this.a((Boolean)this.a(false).e ? Double.POSITIVE_INFINITY : 0.0);
      } else {
         double var1 = 0.0;
         double var3 = 0.0;

         for (qV var6 : this.b()) {
            var1 += var6.c();
            var3 += var6.c() * var6.d();
         }

         this.a(var1);
         if (Double.isInfinite(var1)) {
            this.c(qZ.c);
         } else if (var1 >= Cq.b) {
            this.c(new qZ(var3 / var1));
         } else {
            this.c(((qX)this.a(false).a.c()).a);
         }
      }
   }

   private double j() {
      rM var1 = this.a(false);
      double var2 = Double.POSITIVE_INFINITY;

      while (var1.a != null) {
         qX var4;
         var2 = (var4 = (qX)var1.a.c()).a.d;
         var1 = var4.b ? var1.c : var1.b;
      }

      return (Boolean)var1.e ? Double.NEGATIVE_INFINITY : var2;
   }

   private double k() {
      rM var1 = this.a(false);
      double var2 = Double.NEGATIVE_INFINITY;

      while (var1.a != null) {
         qX var4;
         var2 = (var4 = (qX)var1.a.c()).a.d;
         var1 = var4.b ? var1.b : var1.c;
      }

      return (Boolean)var1.e ? Double.POSITIVE_INFINITY : var2;
   }

   @Override
   public final rQ<qU> a(qM<qU> var1) {
      qZ var2;
      double var12 = (var2 = (qZ)var1).d;
      double var4 = Double.NEGATIVE_INFINITY;

      for (double[] var7 : this) {
         if (var12 < var7[0]) {
            double var13 = var12 - var4;
            double var14 = var7[0] - var12;
            if (var13 < var14) {
               return new rQ<>(var1, b(var4), var13);
            }

            return new rQ<>(var1, b(var7[0]), var14);
         }

         if (var12 <= var7[1]) {
            double var8 = var7[0] - var12;
            double var10 = var12 - var7[1];
            if (var8 < var10) {
               return new rQ<>(var1, b(var7[1]), var10);
            }

            return new rQ<>(var1, b(var7[0]), var8);
         }

         var4 = var7[1];
      }

      return new rQ<>(var1, b(var4), var12 - var4);
   }

   private static qZ b(double var0) {
      return Double.isInfinite(var0) ? null : new qZ(var0);
   }

   public final List<qV> b() {
      ArrayList var1 = new ArrayList();

      for (double[] var3 : this) {
         var1.add(new qV(var3[0], var3[1]));
      }

      return var1;
   }

   private rM<qU> f(rM<qU> var1) {
      if (var1.a == null) {
         return var1;
      }

      rM var2 = null;
      var1 = var1;

      while (var1 != null) {
         var2 = var1;
         rM var3 = var1;
         qW var8 = this;
         rM var10000;
         if (this.o(var3).a != null) {
            var10000 = var8.k(var3).d;
         } else {
            while (true) {
               rM var5 = var3;
               qW var4 = var8;
               rM var6 = var5.d;
               if (var5.d == null || var5 != var4.o(var6)) {
                  var10000 = var3.d;
                  break;
               }

               var3 = var3.d;
            }
         }

         var1 = var10000;
      }

      return this.k(var2);
   }

   private rM<qU> l() {
      rM var1;
      if ((var1 = this.a(false)).a == null) {
         return null;
      }

      var1 = this.f(var1).d;

      while (var1 != null && !this.g(var1) && !this.h(var1)) {
         var1 = this.i(var1);
      }

      return var1;
   }

   private boolean g(rM<qU> var1) {
      return (Boolean)this.k(var1).e ? false : (Boolean)this.l(var1).e;
   }

   private boolean h(rM<qU> var1) {
      return !(Boolean)this.k(var1).e ? false : !(Boolean)this.l(var1).e;
   }

   private rM<qU> i(rM<qU> var1) {
      if (this.p(var1).a != null) {
         return this.l(var1).d;
      }

      while (this.n(var1)) {
         var1 = var1.d;
      }

      return var1.d;
   }

   private rM<qU> j(rM<qU> var1) {
      if (this.o(var1).a != null) {
         return this.k(var1).d;
      }

      while (this.m(var1)) {
         var1 = var1.d;
      }

      return var1.d;
   }

   private rM<qU> k(rM<qU> var1) {
      var1 = this.o(var1);

      while (var1.a != null) {
         var1 = this.p(var1);
      }

      return var1;
   }

   private rM<qU> l(rM<qU> var1) {
      var1 = this.p(var1);

      while (var1.a != null) {
         var1 = this.o(var1);
      }

      return var1;
   }

   private boolean m(rM<qU> var1) {
      rM var2 = var1.d;
      return var1.d == null ? false : var1 == this.o(var2);
   }

   private boolean n(rM<qU> var1) {
      rM var2 = var1.d;
      return var1.d == null ? false : var1 == this.p(var2);
   }

   private rM<qU> o(rM<qU> var1) {
      return q(var1) ? var1.c : var1.b;
   }

   private rM<qU> p(rM<qU> var1) {
      return q(var1) ? var1.b : var1.c;
   }

   private static boolean q(rM<qU> var0) {
      return ((qX)var0.a.c()).b;
   }

   private static double r(rM<qU> var0) {
      return ((qX)var0.a.c()).a.d;
   }

   @Override
   public final Iterator<double[]> iterator() {
      return new qW.a();
   }

   // $VF: synthetic method
   @Override
   public final rK a(rM var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   @Override
   public final rK b(rM var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   static rM a(qW var0) {
      rM var1;
      if ((var1 = (var0 = var0).a(false)).a == null) {
         return null;
      }

      var1 = var0.f(var1).d;

      while (var1 != null && !var0.g(var1) && !var0.h(var1)) {
         var1 = var0.i(var1);
      }

      return var1;
   }

   // $VF: synthetic method
   static double c(qW var0, rM var1) {
      return ((qX)var1.a.c()).a.d;
   }

   class a implements Iterator<double[]> {
      private rM<qU> a = qW.a(qW.this);
      private double[] b;

      a() {
         if (this.a == null) {
            if ((Boolean)qW.this.f(qW.this.a(false)).e) {
               this.b = new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
            } else {
               this.b = null;
            }
         } else if (qW.this.h(this.a)) {
            this.b = new double[]{Double.NEGATIVE_INFINITY, qW.c(qW.this, this.a)};
         } else {
            this.a();
         }
      }

      private void a() {
         rM var1 = this.a;

         while (var1 != null && !qW.this.g(var1)) {
            var1 = qW.this.i(var1);
         }

         if (var1 == null) {
            this.a = null;
            this.b = null;
         } else {
            rM var2 = var1;

            while (var2 != null && !qW.this.h(var2)) {
               var2 = qW.this.i(var2);
            }

            if (var2 != null) {
               this.b = new double[]{qW.c(qW.this, var1), qW.c(qW.this, var2)};
               this.a = var2;
            } else {
               this.b = new double[]{qW.c(qW.this, var1), Double.POSITIVE_INFINITY};
               this.a = null;
            }
         }
      }

      @Override
      public final boolean hasNext() {
         return this.b != null;
      }

      private double[] b() {
         if (this.b == null) {
            throw new NoSuchElementException();
         }

         double[] var1 = this.b;
         this.a();
         return var1;
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         qW.a var1 = this;
         if (this.b == null) {
            throw new NoSuchElementException();
         }

         double[] var2 = var1.b;
         var1.a();
         return var2;
      }
   }
}
