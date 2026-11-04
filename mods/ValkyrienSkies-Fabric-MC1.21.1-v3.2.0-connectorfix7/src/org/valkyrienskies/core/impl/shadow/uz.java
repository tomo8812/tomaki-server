package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public abstract class uz<T extends kO<T>> implements uO<T> {
   private static final double d = 1.0E-14;
   private static final double e = 1.0E-15;
   private Collection<wv<T>> f;
   uJ<T> a;
   T b;
   private boolean g;
   private boolean h;
   public final kM<T> c;
   private Collection<vk<T>> i;
   private boolean j;
   private final String k;
   private Cc.a l;
   private transient uH<T> m;

   public uz(kM<T> var1, String var2) {
      this.c = var1;
      this.k = var2;
      this.f = new ArrayList<>();
      this.a = null;
      this.b = null;
      this.i = new ArrayList<>();
      this.j = false;
      this.l = Cc.a.a().b(Integer.MAX_VALUE);
   }

   public final kM<T> a() {
      return this.c;
   }

   @Override
   public final String b() {
      return this.k;
   }

   @Override
   public final void a(wv<T> var1) {
      this.f.add(var1);
   }

   @Override
   public final Collection<wv<T>> c() {
      return Collections.unmodifiableCollection(this.f);
   }

   @Override
   public final void d() {
      this.f.clear();
   }

   @Override
   public final void a(vj<T> var1, double var2, double var4, int var6) {
      nB var15 = new nB(this.c.a().a(1.0E-14), this.c.a().a(var4), this.c.a().a(1.0E-15), 5);
      int var3 = var6;
      double var11 = var4;
      double var9 = var2;
      vj var14 = var1;
      uz var13 = this;
      this.i.add(new vk<>(var14, var9, var13.c.a().a(var11), var3, var15));
   }

   @Override
   public final void a(vj<T> var1, double var2, double var4, int var6, nw<T> var7) {
      this.i.add(new vk<>(var1, var2, this.c.a().a(var4), var6, var7));
   }

   @Override
   public final Collection<vj<T>> e() {
      ArrayList var1 = new ArrayList(this.i.size());

      for (vk var3 : this.i) {
         var1.add(var3.a);
      }

      return Collections.unmodifiableCollection(var1);
   }

   @Override
   public final void f() {
      this.i.clear();
   }

   @Override
   public final uJ<T> g() {
      return this.a;
   }

   @Override
   public final T h() {
      return this.b;
   }

   @Override
   public final void a(int var1) {
      this.l = this.l.b(var1 < 0 ? Integer.MAX_VALUE : var1);
   }

   @Override
   public final int i() {
      Cc.a var1 = this.l;
      return this.l.b;
   }

   @Override
   public final int j() {
      Cc.a var1 = this.l;
      return this.l.e;
   }

   protected final uJ<T> a(uH<T> var1, T var2, T[] var3, T var4) {
      this.m = var1;
      this.l = this.l.a(0);
      kO[] var8 = var3;
      uH var5 = var1;
      int var6 = 0;
      var5.c.a(0, (T[])var8);

      while (true) {
         var6++;
         uG var7 = var5.c;
         if (var6 >= var5.c.a.length - 1) {
            kO[] var9 = this.a((T)var2, (T[])var3);
            uJ var10 = new uJ((T)var2, (T[])var3, (T[])var9);

            for (vk var15 : this.i) {
               ;
            }

            Iterator var12 = this.f.iterator();

            while (var12.hasNext()) {
               ((wv)var12.next()).a(var10, (T)var4);
            }

            boolean var13 = false;
            this.j = false;
            return var10;
         }

         var5.c.a(var6, (T[])var8);
         uK var14 = var5.b.get(var6 - 1);
      }
   }

   private uH<T> s() {
      return this.m;
   }

   protected final Cc.a k() {
      return this.l;
   }

   public final T[] a(T var1, T[] var2) {
      Cc.a var3 = this.l;
      this.l.d(1);
      return this.m.a((T)var1, (T[])var2);
   }

   private void b(boolean var1) {
      this.j = false;
   }

   protected final uJ<T> a(wr<T> var1, T var2) {
      uJ var3 = var1.a;
      uJ var4 = var1.b;
      if (!this.j) {
         for (vk var10000 : this.i) {
            wr var10 = var1;
            vk var9 = var10000;
            uJ var11 = var10.c();
            var9.c = var11.a;
            var9.d = var9.a.b();
            if (var9.d.d() == 0.0) {
               double var19 = BY.e(var9.i.c().d(), BY.w(var9.i.d().c(var9.c).d()));
               kO var6 = var9.c.a(var19 * 0.5);
               vj var10001 = var9.a;
               var10.a((T)var6);
               var9.d = (T)var10001.b();
            }

            var9.e = var9.d.d() >= 0.0;
         }

         this.j = true;
      }

      final int var32 = var1.c ? 1 : -1;
      TreeSet var36 = new TreeSet<>(new Comparator<vk<T>>() {
         private int a(vk<T> var1, vk<T> var2x) {
            return var32 * Double.compare(var1.b().d(), var2x.b().d());
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            vk var10001 = (vk)var1;
            vk var3 = var2;
            var2 = var10001;
            return var32 * Double.compare(var2.b().d(), var3.b().d());
         }
      });
      Iterator var33 = this.i.iterator();

      while (var33.hasNext()) {
         vk var7;
         if ((var7 = (vk)var33.next()).b(var1)) {
            var36.add(var7);
         }
      }

      wr var34 = var1;

      while (!var36.isEmpty()) {
         Iterator var38;
         vk var21 = (vk)(var38 = var36.iterator()).next();
         var38.remove();
         uJ var39 = var34.a((T)var21.b());
         wr var35 = var34.a(var3, var39);
         Iterator var27 = this.i.iterator();

         while (var27.hasNext()) {
            vk var8;
            (var8 = (vk)var27.next()).a(var39);
            this.g = this.g || var8.c();
         }

         Iterator var28 = this.f.iterator();

         while (var28.hasNext()) {
            ((wv)var28.next()).a(var35, this.g);
         }

         if (this.g) {
            return var39;
         }

         this.h = false;

         for (vk var51 : this.i) {
            uJ var45 = var39;
            vk var43 = var51;
            uI var52;
            if (var51.f && var43.g.b(var45.a).e().b(var43.b).d() <= 0.0) {
               uI var47;
               if (var43.h == vf.RESET_STATE$3522afc3) {
                  var47 = var43.a.d();
               } else if (var43.h == vf.RESET_DERIVATIVES$3522afc3) {
                  var47 = var45;
               } else {
                  var47 = null;
               }

               var43.f = false;
               var43.g = null;
               var52 = var47;
            } else {
               var52 = null;
            }

            uI var29 = var52;
            if (var52 != null) {
               uH var12 = this.m;
               kO[] var22 = this.m.c.a(var29);
               kO[] var25 = this.a(var29.a, (T[])var22);
               this.h = true;
               var12 = this.m;
               kO[] var50 = var25;
               kO[] var48 = var22;
               kO var46 = var29.a;
               uG var44 = this.m.c;
               if (var48.length != var44.b()) {
                  throw new oS(var48.length, var44.b());
               }

               if (var50.length != var44.b()) {
                  throw new oS(var50.length, var44.b());
               }

               int var20 = var44.a.length - 1;
               int var37 = 0;
               kO[] var23 = var44.a(0, (T[])var48);
               kO[] var26 = var44.a(0, (T[])var50);
               if (var20 < 2) {
                  return new uJ<>((T)var46, (T[])var23, (T[])var26);
               }

               kO[][] var30 = Ch.a(var46.c(), var20 - 1, -1);

               for (var31 = Ch.a(var46.c(), var20 - 1, -1); ++var37 < var44.a.length - 1; var31[var37 - 1] = var44.a(var37, (T[])var50)) {
                  var30[var37 - 1] = var44.a(var37, (T[])var48);
               }

               return new uJ<>((T)var46, (T[])var23, (T[])var26, (T[][])var30, (T[][])var31);
            }
         }

         var3 = var39;
         var34 = var35.a(var39, var4);
         if (var21.b(var34)) {
            var36.add(var21);
         }
      }

      Iterator var40 = this.i.iterator();

      while (var40.hasNext()) {
         vk var24;
         (var24 = (vk)var40.next()).a(var4);
         this.g = this.g || var24.c();
      }

      this.g = this.g || var4.a.b((T)var2).e().d() <= BY.x(var2.d());
      var40 = this.f.iterator();

      while (var40.hasNext()) {
         ((wv)var40.next()).a(var34, this.g);
      }

      return var4;
   }

   public void a(uI<T> var1, T var2) {
      double var3 = 1000.0 * BY.x(BY.e(BY.w(var1.a.d()), BY.w(var2.d())));
      double var5;
      if ((var5 = var1.a.b((T)var2).e().d()) <= var3) {
         throw new pn(px.TOO_SMALL_INTEGRATION_INTERVAL, var5, var3, false);
      }
   }

   protected final boolean l() {
      return this.h;
   }

   protected final void a(T var1) {
      this.b = (T)var1;
   }

   protected final T m() {
      return this.b;
   }

   protected final void a(uJ<T> var1) {
      this.a = var1;
   }

   protected final uJ<T> n() {
      return this.a;
   }

   protected final void a(boolean var1) {
      this.g = false;
   }

   protected final boolean o() {
      return this.g;
   }
}
