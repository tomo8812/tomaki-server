package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public abstract class uA implements uP {
   private Collection<wA> f;
   protected double a;
   protected double b;
   protected boolean c;
   protected boolean d;
   private Collection<vi> g;
   private boolean h;
   private final String i;
   private Cc.a j;
   transient uF e;

   public uA(String var1) {
      this.i = var1;
      this.f = new ArrayList<>();
      this.a = Double.NaN;
      this.b = Double.NaN;
      this.g = new ArrayList<>();
      this.h = false;
      this.j = Cc.a.a().b(Integer.MAX_VALUE);
   }

   protected uA() {
      this(null);
   }

   @Override
   public final String a() {
      return this.i;
   }

   @Override
   public void a(wA var1) {
      this.f.add(var1);
   }

   @Override
   public final Collection<wA> b() {
      return Collections.unmodifiableCollection(this.f);
   }

   @Override
   public final void c() {
      this.f.clear();
   }

   @Override
   public final void a(vh var1, double var2, double var4, int var6) {
      this.a(var1, var2, var4, var6, new ny(var4, 5));
   }

   @Override
   public void a(vh var1, double var2, double var4, int var6, nO var7) {
      this.g.add(new vi(var1, var2, var4, var6, var7));
   }

   @Override
   public final Collection<vh> d() {
      ArrayList var1 = new ArrayList(this.g.size());

      for (vi var3 : this.g) {
         var1.add(var3.a);
      }

      return Collections.unmodifiableCollection(var1);
   }

   @Override
   public final void e() {
      this.g.clear();
   }

   @Override
   public double f() {
      return this.a;
   }

   @Override
   public final double g() {
      return this.b;
   }

   @Override
   public final void a(int var1) {
      this.j = this.j.b(var1 < 0 ? Integer.MAX_VALUE : var1);
   }

   @Override
   public final int h() {
      Cc.a var1 = this.j;
      return this.j.b;
   }

   @Override
   public final int i() {
      Cc.a var1 = this.j;
      return this.j.e;
   }

   protected final void a(double var1, double[] var3, double var4) {
      this.j = this.j.a(0);
      Iterator var6 = this.g.iterator();

      while (var6.hasNext()) {
         vi var7;
         vi var10000 = var7 = (vi)var6.next();
         uF var9 = this.e;
         var10000.c = var9;
         var7.a.a(var1, var3, var4);
      }

      var6 = this.f.iterator();

      while (var6.hasNext()) {
         ((wA)var6.next()).a(var1, var3, var4);
      }

      boolean var11 = false;
      this.h = false;
   }

   protected final void a(uF var1) {
      this.e = var1;
   }

   protected final uF j() {
      return this.e;
   }

   @Deprecated
   private Cb l() {
      return Cb.a(this.j);
   }

   protected final Cc.a k() {
      return this.j;
   }

   @Override
   public final double a(<unrepresentable> var1, double var2, double[] var4, double var5, double[] var7) {
      if (var4.length != var1.a()) {
         throw new oS(var4.length, var1.a());
      }

      if (var7.length != var1.a()) {
         throw new oS(var7.length, var1.a());
      }

      uF var14;
      uF var10000 = var14 = new uF(var1);
      double var12 = var2;
      var10000.c = var12;
      double[] var3 = var4;
      uF var15 = var14;
      if (var3.length != var15.d.length) {
         throw new oS(var3.length, var15.d.length);
      }

      System.arraycopy(var3, 0, var15.d, 0, var3.length);
      this.a(var14, var5);
      System.arraycopy(var14.f(), 0, var7, 0, var7.length);
      return var14.c;
   }

   public abstract void a(uF var1, double var2);

   public final void a(double var1, double[] var3, double[] var4) {
      Cc.a var5 = this.j;
      this.j.d(1);
      this.e.a(var1, var3, var4);
   }

   private void a(boolean var1) {
      this.h = false;
   }

   protected final double a(ws var1, double[] var2, double[] var3, double var4) {
      double var6 = var1.i;
      double var8 = var1.j;
      if (!this.h) {
         for (vi var10000 : this.g) {
            ws var23 = var1;
            vi var13 = var10000;
            var10000.d = var23.g();
            var23.d(var13.d);
            var13.e = var13.a.a(var13.d, var13.a((wB)var23));
            if (var13.e == 0.0) {
               double var24 = BY.e(var13.j.g(), BY.w(var13.j.h() * var13.d));
               double var26 = var13.d + var24 * 0.5;
               var23.d(var26);
               var13.e = var13.a.a(var26, var13.a((wB)var23));
            }

            var13.f = var13.e >= 0.0;
         }

         this.h = true;
      }

      final int var34 = var1.m ? 1 : -1;
      TreeSet var11 = new TreeSet<>(new Comparator<vi>() {
         private int a(vi var1, vi var2x) {
            return var34 * Double.compare(var1.b(), var2x.b());
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            vi var10001 = (vi)var1;
            vi var3 = var2;
            var2 = var10001;
            return var34 * Double.compare(var2.b(), var3.b());
         }
      });
      Iterator var35 = this.g.iterator();

      while (var35.hasNext()) {
         vi var12;
         if ((var12 = (vi)var35.next()).b(var1)) {
            var11.add(var12);
         }
      }

      while (!var11.isEmpty()) {
         Iterator var36;
         vi var41 = (vi)(var36 = var11.iterator()).next();
         var36.remove();
         double var14 = var41.b();
         double var29 = var6;
         var1.k = var29;
         var29 = var14;
         var1.l = var29;
         var1.d(var14);
         double[] var31 = new double[var2.length];
         uF var43 = this.e;
         this.e.b.b(var1.k(), var31);
         int var7 = 0;
         uE[] var37;
         int var44 = (var37 = this.e.d()).length;

         for (int var16 = 0; var16 < var44; var16++) {
            uE var17;
            (var17 = var37[var16]).b(var1.a(var7++), var31);
         }

         Iterator var38 = this.g.iterator();

         while (var38.hasNext()) {
            vi var45;
            (var45 = (vi)var38.next()).a(var14, var31);
            this.c = this.c || var45.c();
         }

         Iterator var39 = this.f.iterator();

         while (var39.hasNext()) {
            ((wA)var39.next()).a(var1, this.c);
         }

         if (this.c) {
            System.arraycopy(var31, 0, var2, 0, var2.length);
            return var14;
         }

         this.d = false;
         double var52 = var14;
         vi var46 = var41;
         boolean var57;
         if (var41.g && BY.w(var46.h - var52) <= var46.b) {
            if (var46.i == vh.a.RESET_STATE$534a8113) {
               ;
            }

            var46.g = false;
            var46.h = Double.NaN;
            var57 = var46.i == vh.a.RESET_STATE$534a8113 || var46.i == vh.a.RESET_DERIVATIVES$534a8113;
         } else {
            var57 = false;
         }

         if (var57) {
            var1.d(var14);
            System.arraycopy(var31, 0, var2, 0, var2.length);
            this.a(var14, var2, var3);
            this.d = true;
            return var14;
         }

         var6 = var14;
         var29 = var14;
         var1.k = var29;
         var29 = var8;
         var1.l = var29;
         if (var41.b(var1)) {
            var11.add(var41);
         }
      }

      var1.d(var8);
      double[] var40 = new double[var2.length];
      uF var47 = this.e;
      this.e.b.b(var1.k(), var40);
      int var42 = 0;
      uE[] var48;
      int var15 = (var48 = this.e.d()).length;

      for (int var32 = 0; var32 < var15; var32++) {
         uE var33;
         (var33 = var48[var32]).b(var1.a(var42++), var40);
      }

      Iterator var49 = this.g.iterator();

      while (var49.hasNext()) {
         vi var51;
         (var51 = (vi)var49.next()).a(var8, var40);
         this.c = this.c || var51.c();
      }

      this.c = this.c || Cq.b(var8, var4, 1);
      Iterator var50 = this.f.iterator();

      while (var50.hasNext()) {
         ((wA)var50.next()).a(var1, this.c);
      }

      return var8;
   }

   public void b(uF var1, double var2) {
      double var4 = 1000.0 * BY.x(BY.e(BY.w(var1.c), BY.w(var2)));
      double var6;
      if ((var6 = BY.w(var1.c - var2)) <= var4) {
         throw new pn(px.TOO_SMALL_INTEGRATION_INTERVAL, var6, var4, false);
      }
   }
}
