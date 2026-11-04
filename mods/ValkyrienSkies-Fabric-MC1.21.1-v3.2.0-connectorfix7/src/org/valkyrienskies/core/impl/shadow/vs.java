package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class vs<T extends kO<T>> extends vp<T> {
   private static final String k = "Adams-Moulton";

   private vs(kM<T> var1, int var2, double var3, double var5, double var7, double var9) {
      super(var1, "Adams-Moulton", var2, var2 + 1, var3, var5, var7, var9);
   }

   private vs(kM<T> var1, int var2, double var3, double var5, double[] var7, double[] var8) {
      super(var1, "Adams-Moulton", var2, var2 + 1, var3, var5, var7, var8);
   }

   @Override
   public final uJ<T> a(uH<T> var1, uI<T> var2, T var3) {
      this.a(var2, (T)var3);
      kO var4 = var2.a;
      kO[] var5 = var1.c.a(var2);
      this.a(this.a(var1, (T)var4, (T[])var5, (T)var3));
      boolean var13 = ((kO)var3.b(var2.a)).d() > 0.0;
      this.a(var1, this.n(), (T)var3);
      uJ var14;
      uJ var15 = vq.a(var14 = this.n(), var14.a.a(this.m()), this.m(), super.d, super.e);
      this.a(false);

      do {
         kO[] var6 = null;
         kO[] var7 = Ch.a(this.a(), var5.length);
         sx var8 = null;
         kO var9 = this.a().a().a(10.0);

         while (var9.b(1.0).d() >= 0.0) {
            var6 = var15.c();
            kO[] var10 = this.a(var15.a, (T[])var6);

            for (int var11 = 0; var11 < var7.length; var11++) {
               var7[var11] = this.m().c((T)var10[var11]);
            }

            var8 = this.a(super.e);
            this.a(super.d, (T[])var7, var8);
            if ((var9 = var8.c(new vs.a((T[])var5, (T[])var7, (T[])var6))).b(1.0).d() >= 0.0) {
               kO var23 = this.c((T)var9);
               this.b(this.a(this.m().c((T)var23), var13, false));
               var15 = vq.a(this.n(), this.n().a.a(this.m()), this.m(), super.d, super.e);
            }
         }

         kO[] var22 = this.a(var15.a, (T[])var6);
         kO[] var24 = Ch.a(this.a(), var5.length);

         for (int var12 = 0; var12 < var24.length; var12++) {
            var24[var12] = this.m().c((T)var22[var12]);
         }

         this.a((T[])var7, (T[])var24, var8);
         var15 = new uJ(var15.a, (T[])var6, (T[])var22);
         this.a(this.a(new vq<>(this.m(), var15, (T[])var24, var8, var13, this.n(), var15, var1.c), (T)var3));
         super.d = (T[])var24;
         super.e = var8;
         if (!this.o()) {
            System.arraycopy(var6, 0, var5, 0, var5.length);
            if (this.l()) {
               this.a(var1, this.n(), (T)var3);
            }

            kO var25 = this.c((T)var9);
            var4 = this.m().c((T)var25);
            kO var18 = this.n().a.a((T)var4);
            boolean var19 = var13 ? var18.b(var3).d() >= 0.0 : var18.b(var3).d() <= 0.0;
            var4 = this.a((T)var4, var13, var19);
            kO var20 = this.n().a.a((T)var4);
            if (var13 ? var20.b(var3).d() >= 0.0 : var20.b(var3).d() <= 0.0) {
               var4 = (kO)var3.b(this.n().a);
            }

            this.b((T)var4);
            var15 = vq.a(this.n(), this.n().a.a(this.m()), this.m(), super.d, super.e);
         }
      } while (!this.o());

      uJ var21 = this.n();
      this.a((uJ<T>)null);
      this.a((T)null);
      return var21;
   }

   class a implements sS<T> {
      private final kO[] a;
      private final kO[] b;
      private final kO[] c;
      private final kO[] d;

      a(T[] var2, T[] var3, T[] var4) {
         this.a = var2;
         this.b = var3;
         this.d = var4;
         this.c = (kO[])var4.clone();
      }

      @Override
      public final void a(int var1, int var2, int var3, int var4) {
         vs var5 = vs.this;
         Arrays.fill(this.d, vs.super.c.a());
      }

      private void a(int var1, int var2, T var3) {
         if ((var1 & 1) == 0) {
            this.d[var2] = this.d[var2].b(var3);
         } else {
            this.d[var2] = this.d[var2].a(var3);
         }
      }

      private T b() {
         vs var1 = vs.this;
         kO var4 = vs.super.c.a();

         for (int var2 = 0; var2 < this.d.length; var2++) {
            this.d[var2] = this.d[var2].a(this.a[var2].a(this.b[var2]));
            if (var2 < vs.super.j) {
               kO var3 = Ci.a(this.a[var2].e(), this.d[var2].e());
               var3 = vs.super.h == null ? var3.c(vs.super.g).a(vs.super.f) : var3.c(vs.super.i[var2]).a(vs.super.h[var2]);
               var3 = this.d[var2].b(this.c[var2]).d(var3);
               var4 = var4.a(var3.c(var3));
            }
         }

         return (T)var4.d((double)vs.super.j).k();
      }

      // $VF: synthetic method
      @Override
      public final kN a() {
         vs.a var1 = this;
         vs var2 = vs.this;
         kO var5 = vs.super.c.a();

         for (int var3 = 0; var3 < var1.d.length; var3++) {
            var1.d[var3] = var1.d[var3].a(var1.a[var3].a(var1.b[var3]));
            if (var3 < vs.super.j) {
               kO var4 = Ci.a(var1.a[var3].e(), var1.d[var3].e());
               var4 = vs.super.h == null ? var4.c(vs.super.g).a(vs.super.f) : var4.c(vs.super.i[var3]).a(vs.super.h[var3]);
               var4 = var1.d[var3].b(var1.c[var3]).d(var4);
               var5 = var5.a(var4.c(var4));
            }
         }

         return var5.d((double)vs.super.j).k();
      }

      // $VF: synthetic method
      @Override
      public final void a(int var1, int var2, kN var3) {
         kO var4 = (kO)var3;
         int var7 = var2;
         var2 = var1;
         vs.a var5 = this;
         if ((var2 & 1) == 0) {
            var5.d[var7] = var5.d[var7].b(var4);
         } else {
            var5.d[var7] = var5.d[var7].a(var4);
         }
      }
   }
}
