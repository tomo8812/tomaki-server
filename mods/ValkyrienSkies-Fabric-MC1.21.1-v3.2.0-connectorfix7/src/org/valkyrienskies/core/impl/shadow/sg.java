package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class sg extends rK<sj, sj> implements Iterable<double[]> {
   public sg(double var1) {
      super(var1);
   }

   private sg(double var1, double var3, double var5) {
      double var11 = var5;
      double var9 = var3;
      double var7 = var1;
      rM var10001;
      if (!Cq.b(var1, var9, 0) && !(var9 - var7 >= Math.PI * 2)) {
         if (var7 > var9) {
            throw new pm(px.ENDPOINTS_NOT_AN_INTERVAL, var7, var9, true);
         }

         double var13;
         double var15 = (var13 = Ci.b(var7, Math.PI)) + (var9 - var7);
         sk var17 = new sh(new si(var13), false, var11).c();
         if (var15 <= Math.PI * 2) {
            sk var2 = new sh(new si(var15), true, var11).c();
            var10001 = new rM<>(var17, new rM<>(Boolean.FALSE), new rM<>(var2, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null), null);
         } else {
            sk var18 = new sh(new si(var15 - (Math.PI * 2)), true, var11).c();
            var10001 = new rM<>(var17, new rM<>(var18, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null), new rM<>(Boolean.TRUE), null);
         }
      } else {
         var10001 = new rM(Boolean.TRUE);
      }

      super(var10001, var5);
   }

   private sg(rM<sj> var1, double var2) {
      super(var1, var2);
      this.j();
   }

   private sg(Collection<sb<sj>> var1, double var2) {
      super(var1, var2);
      this.j();
   }

   private static rM<sj> a(double var0, double var2, double var4) {
      if (Cq.b(var0, var2, 0) || var2 - var0 >= Math.PI * 2) {
         return new rM<>(Boolean.TRUE);
      } else if (var0 > var2) {
         throw new pm(px.ENDPOINTS_NOT_AN_INTERVAL, var0, var2, true);
      } else {
         double var6;
         double var8 = (var6 = Ci.b(var0, Math.PI)) + (var2 - var0);
         sk var10 = new sh(new si(var6), false, var4).c();
         if (var8 <= Math.PI * 2) {
            sk var11 = new sh(new si(var8), true, var4).c();
            return new rM<>(var10, new rM<>(Boolean.FALSE), new rM<>(var11, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null), null);
         } else {
            sk var1 = new sh(new si(var8 - (Math.PI * 2)), true, var4).c();
            return new rM<>(var10, new rM<>(var1, new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null), new rM<>(Boolean.TRUE), null);
         }
      }
   }

   private void j() {
      rM var1;
      if ((var1 = this.a(false)).a != null) {
         Boolean var2 = (Boolean)this.e(var1).e;
         Boolean var4 = (Boolean)this.f(var1).e;
         if (var2 ^ var4) {
            throw new sg.a();
         }
      }
   }

   private rM<sj> e(rM<sj> var1) {
      if (var1.a == null) {
         return var1;
      }

      rM var2 = null;
      var1 = var1;

      while (var1 != null) {
         var2 = var1;
         var1 = this.j(var1);
      }

      return this.k(var2);
   }

   private rM<sj> f(rM<sj> var1) {
      if (var1.a == null) {
         return var1;
      }

      rM var2 = null;
      var1 = var1;

      while (var1 != null) {
         var2 = var1;
         var1 = this.i(var1);
      }

      return this.l(var2);
   }

   private rM<sj> k() {
      rM var1;
      if ((var1 = this.a(false)).a == null) {
         return null;
      }

      var1 = this.e(var1).d;

      while (var1 != null && !this.g(var1)) {
         var1 = this.i(var1);
      }

      return var1;
   }

   private boolean g(rM<sj> var1) {
      return (Boolean)this.k(var1).e ? false : (Boolean)this.l(var1).e;
   }

   private boolean h(rM<sj> var1) {
      return !(Boolean)this.k(var1).e ? false : !(Boolean)this.l(var1).e;
   }

   private rM<sj> i(rM<sj> var1) {
      if (this.p(var1).a != null) {
         return this.l(var1).d;
      }

      while (this.n(var1)) {
         var1 = var1.d;
      }

      return var1.d;
   }

   private rM<sj> j(rM<sj> var1) {
      if (this.o(var1).a != null) {
         return this.k(var1).d;
      }

      while (this.m(var1)) {
         var1 = var1.d;
      }

      return var1.d;
   }

   private rM<sj> k(rM<sj> var1) {
      var1 = this.o(var1);

      while (var1.a != null) {
         var1 = this.p(var1);
      }

      return var1;
   }

   private rM<sj> l(rM<sj> var1) {
      var1 = this.p(var1);

      while (var1.a != null) {
         var1 = this.o(var1);
      }

      return var1;
   }

   private boolean m(rM<sj> var1) {
      rM var2 = var1.d;
      return var1.d == null ? false : var1 == this.o(var2);
   }

   private boolean n(rM<sj> var1) {
      rM var2 = var1.d;
      return var1.d == null ? false : var1 == this.p(var2);
   }

   private rM<sj> o(rM<sj> var1) {
      return q(var1) ? var1.c : var1.b;
   }

   private rM<sj> p(rM<sj> var1) {
      return q(var1) ? var1.b : var1.c;
   }

   private static boolean q(rM<sj> var0) {
      return ((sh)var0.a.c()).b;
   }

   private static double r(rM<sj> var0) {
      return ((sh)var0.a.c()).a.b;
   }

   private sg s(rM<sj> var1) {
      return new sg(var1, this.c());
   }

   @Override
   public final void a() {
      if (this.a(false).a == null) {
         this.c(si.a);
         this.a((Boolean)this.a(false).e ? Math.PI * 2 : 0.0);
      } else {
         double var1 = 0.0;
         double var3 = 0.0;
         Iterator var5 = this.iterator();

         while (var5.hasNext()) {
            double[] var6;
            double var7 = (var6 = (double[])var5.next())[1] - var6[0];
            var1 += var7;
            var3 += var7 * (var6[0] + var6[1]);
         }

         this.a(var1);
         if (Cq.b(var1, Math.PI * 2, 0)) {
            this.c(si.a);
         } else if (var1 >= Cq.b) {
            this.c(new si(var3 / (var1 * 2.0)));
         } else {
            sh var9 = (sh)this.a(false).a.c();
            this.c(var9.a);
         }
      }
   }

   @Override
   public final rQ<sj> a(qM<sj> var1) {
      si var2;
      double var15 = (var2 = (si)var1).b;
      boolean var4 = false;
      double var5 = Double.NaN;
      double var7 = Double.NaN;

      for (double[] var10 : this) {
         if (Double.isNaN(var5)) {
            var5 = var10[0];
         }

         if (!var4) {
            if (var15 < var10[0]) {
               if (!Double.isNaN(var7)) {
                  double var11 = var15 - var7;
                  double var13 = var10[0] - var15;
                  if (var11 < var13) {
                     return new rQ<>(var1, new si(var7), var11);
                  }

                  return new rQ<>(var1, new si(var10[0]), var13);
               }

               var4 = true;
            } else if (var15 <= var10[1]) {
               double var18 = var10[0] - var15;
               double var21 = var15 - var10[1];
               if (var18 < var21) {
                  return new rQ<>(var1, new si(var10[1]), var21);
               }

               return new rQ<>(var1, new si(var10[0]), var18);
            }
         }

         var7 = var10[1];
      }

      if (Double.isNaN(var7)) {
         return new rQ<>(var1, null, Math.PI * 2);
      } else if (var4) {
         double var17 = var15 - (var7 - (Math.PI * 2));
         double var20 = var5 - var15;
         return var17 < var20 ? new rQ<>(var1, new si(var7), var17) : new rQ<>(var1, new si(var5), var20);
      } else {
         double var16 = var15 - var7;
         double var19 = var5 + (Math.PI * 2) - var15;
         return var16 < var19 ? new rQ<>(var1, new si(var7), var16) : new rQ<>(var1, new si(var5), var19);
      }
   }

   public final List<sf> b() {
      ArrayList var1 = new ArrayList();

      for (double[] var3 : this) {
         var1.add(new sf(var3[0], var3[1], this.c()));
      }

      return var1;
   }

   @Override
   public final Iterator<double[]> iterator() {
      return new sg.c();
   }

   @Deprecated
   private int b(sf var1) {
      sg.b var2;
      if ((var2 = this.a(var1)).a != null) {
         return var2.b != null ? sa.BOTH$7c70dd6b : sa.PLUS$7c70dd6b;
      } else {
         return var2.b != null ? sa.MINUS$7c70dd6b : sa.HYPER$7c70dd6b;
      }
   }

   public final sg.b a(sf var1) {
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      double var4 = Math.PI + var1.a;
      double var6 = var1.b - var1.a;
      Iterator var8 = this.iterator();

      while (var8.hasNext()) {
         double[] var9;
         double var10 = Ci.b((var9 = (double[])var8.next())[0], var4) - var1.a;
         double var12 = var9[0] - var10;
         double var14 = var9[1] - var12;
         if (var10 < var6) {
            var2.add(var9[0]);
            if (var14 > var6) {
               double var16 = var6 + var12;
               var2.add(var16);
               var3.add(var16);
               if (var14 > Math.PI * 2) {
                  double var18 = var12 + (Math.PI * 2);
                  var3.add(var18);
                  var2.add(var18);
                  var2.add(var9[1]);
               } else {
                  var3.add(var9[1]);
               }
            } else {
               var2.add(var9[1]);
            }
         } else {
            var3.add(var9[0]);
            if (var14 > Math.PI * 2) {
               double var20 = var12 + (Math.PI * 2);
               var3.add(var20);
               var2.add(var20);
               if (var14 > var6 + (Math.PI * 2)) {
                  double var21 = var6 + (Math.PI * 2) + var12;
                  var2.add(var21);
                  var3.add(var21);
                  var3.add(var9[1]);
               } else {
                  var2.add(var9[1]);
               }
            } else {
               var3.add(var9[1]);
            }
         }
      }

      return new sg.b(this.a(var3), this.a(var2), (byte)0);
   }

   private void a(rM<sj> var1, double var2, boolean var4) {
      sh var6 = new sh(new si(var2), !var4, this.c());
      if ((var1 = var1.a(var6.a, this.c())).a != null) {
         throw new oY();
      }

      var1.a(var6);
      Object var7 = null;
      var1.e = var7;
      var7 = Boolean.FALSE;
      rM var3 = var1.b;
      var1.b.e = var7;
      var7 = Boolean.TRUE;
      var3 = var1.c;
      var1.c.e = var7;
   }

   private sg a(List<Double> var1) {
      if (var1.isEmpty()) {
         return null;
      }

      for (int var2 = 0; var2 < var1.size(); var2++) {
         int var3 = (var2 + 1) % var1.size();
         double var4 = (Double)var1.get(var2);
         if (BY.w(Ci.b((Double)var1.get(var3), var4) - var4) <= this.c()) {
            if (var3 > 0) {
               var1.remove(var3);
               var1.remove(var2);
               var2--;
            } else {
               double var8 = (Double)var1.remove(var1.size() - 1);
               double var10 = (Double)var1.remove(0);
               if (var1.isEmpty()) {
                  if (var8 - var10 > Math.PI) {
                     return new sg(new rM<>(Boolean.TRUE), this.c());
                  }

                  return null;
               }

               var1.add((Double)var1.remove(0) + (Math.PI * 2));
            }
         }
      }

      rM var12 = new rM(Boolean.FALSE);

      for (byte var13 = 0; var13 < var1.size() - 1; var13 += 2) {
         this.a(var12, (Double)var1.get(var13), true);
         this.a(var12, (Double)var1.get(var13 + 1), false);
      }

      return var12.a == null ? null : new sg(var12, this.c());
   }

   // $VF: synthetic method
   @Override
   public final rK a(rM var1) {
      return this.s(var1);
   }

   // $VF: synthetic method
   @Override
   public final rK b(rM var1) {
      return this.s(var1);
   }

   // $VF: synthetic method
   static rM a(sg var0) {
      rM var1;
      if ((var1 = (var0 = var0).a(false)).a == null) {
         return null;
      }

      var1 = var0.e(var1).d;

      while (var1 != null && !var0.g(var1)) {
         var1 = var0.i(var1);
      }

      return var1;
   }

   // $VF: synthetic method
   static boolean d(sg var0, rM var1) {
      var1 = var1;
      sg var3 = var0;
      return !(Boolean)var0.k(var1).e ? false : !(Boolean)var3.l(var1).e;
   }

   // $VF: synthetic method
   static double e(sg var0, rM var1) {
      return ((sh)var1.a.c()).a.b;
   }

   public static class a extends oV {
      private static final long b = 20140107L;

      public a() {
         super(px.INCONSISTENT_STATE_AT_2_PI_WRAPPING);
      }
   }

   public static class b {
      public final sg a;
      public final sg b;

      private b(sg var1, sg var2) {
         this.a = var1;
         this.b = var2;
      }

      public final sg a() {
         return this.a;
      }

      public final sg b() {
         return this.b;
      }

      public final int c() {
         if (this.a != null) {
            return this.b != null ? sa.BOTH$7c70dd6b : sa.PLUS$7c70dd6b;
         } else {
            return this.b != null ? sa.MINUS$7c70dd6b : sa.HYPER$7c70dd6b;
         }
      }

      // $VF: synthetic method
      b(sg var1, sg var2, byte var3) {
         this(var1, var2);
      }
   }

   class c implements Iterator<double[]> {
      private final rM<sj> a = sg.a(sg.this);
      private rM<sj> b = this.a;
      private double[] c;

      c() {
         if (this.a == null) {
            if ((Boolean)sg.this.e(sg.this.a(false)).e) {
               this.c = new double[]{0.0, Math.PI * 2};
            } else {
               this.c = null;
            }
         } else {
            this.a();
         }
      }

      private void a() {
         rM var1 = this.b;

         while (var1 != null && !sg.this.g(var1)) {
            var1 = sg.this.i(var1);
         }

         if (var1 == null) {
            this.b = null;
            this.c = null;
         } else {
            rM var2 = var1;

            while (var2 != null && !sg.d(sg.this, var2)) {
               var2 = sg.this.i(var2);
            }

            if (var2 != null) {
               this.c = new double[]{sg.e(sg.this, var1), sg.e(sg.this, var2)};
               this.b = var2;
            } else {
               var2 = this.a;

               while (var2 != null && !sg.d(sg.this, var2)) {
                  var2 = sg.this.j(var2);
               }

               if (var2 == null) {
                  throw new oY();
               }

               this.c = new double[]{sg.e(sg.this, var1), sg.e(sg.this, var2) + (Math.PI * 2)};
               this.b = null;
            }
         }
      }

      @Override
      public final boolean hasNext() {
         return this.c != null;
      }

      private double[] b() {
         if (this.c == null) {
            throw new NoSuchElementException();
         }

         double[] var1 = this.c;
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
         sg.c var1 = this;
         if (this.c == null) {
            throw new NoSuchElementException();
         }

         double[] var2 = var1.c;
         var1.a();
         return var2;
      }
   }
}
