package org.valkyrienskies.core.impl.shadow;

public abstract class vK<T extends kO<T>> extends vw<T> implements vQ<T> {
   private final int d;
   private final T[] e;
   private final T[][] k;
   private final T[] l;
   private final T m;
   private T n;
   private T o;
   private T p;

   protected vK(kM<T> var1, String var2, int var3, double var4, double var6, double var8, double var10) {
      super(var1, var2, var4, var6, var8, var10);
      this.d = var3;
      this.e = this.p();
      this.k = this.q();
      this.l = this.r();
      this.m = (T)((kO)var1.b()).d((double)(-this.s()));
      kO var12 = ((kO)var1.a()).a(0.9);
      this.n = (T)var12;
      kO var13 = ((kO)var1.a()).a(0.2);
      this.o = (T)var13;
      kO var14 = ((kO)var1.a()).a(10.0);
      this.p = (T)var14;
   }

   protected vK(kM<T> var1, String var2, int var3, double var4, double var6, double[] var8, double[] var9) {
      super(var1, var2, var4, var6, var8, var9);
      this.d = var3;
      this.e = this.p();
      this.k = this.q();
      this.l = this.r();
      this.m = (T)((kO)var1.b()).d((double)(-this.s()));
      kO var10 = ((kO)var1.a()).a(0.9);
      this.n = (T)var10;
      kO var11 = ((kO)var1.a()).a(0.2);
      this.o = (T)var11;
      kO var12 = ((kO)var1.a()).a(10.0);
      this.p = (T)var12;
   }

   protected final T a(int var1, int var2) {
      return this.a().b().a(var1).d((double)var2);
   }

   protected final T a(double var1, double var3) {
      return this.a().b().c(var1).d(var3);
   }

   protected abstract wk<T> a(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5);

   public abstract int s();

   private T t() {
      return this.n;
   }

   private void b(T var1) {
      this.n = (T)var1;
   }

   @Override
   public final uJ<T> a(uH<T> var1, uI<T> var2, T var3) {
      this.a(var2, (T)var3);
      kO var4 = var2.a;
      kO[] var5 = var1.c.a(var2);
      this.a(this.a(var1, (T)var4, (T[])var5, (T)var3));
      boolean var15 = ((kO)var3.b(var2.a)).d() > 0.0;
      int var16 = this.e.length + 1;
      kO[] var6 = var5;
      kO[][] var7 = Ch.a(this.a(), var16, -1);
      kO[] var8 = Ch.a(this.a(), var5.length);
      kO var9 = this.a().a();
      boolean var10 = true;
      this.a(false);

      do {
         kO var11 = this.a().a().a(10.0);

         while (var11.b(1.0).d() >= 0.0) {
            var6 = var1.c.a((uI<T>)this.n());
            var7[0] = var1.c.a(this.n());
            if (var10) {
               kO[] var12 = Ch.a(this.a(), super.j);
               if (super.h == null) {
                  for (int var13 = 0; var13 < var12.length; var13++) {
                     var12[var13] = var6[var13].e().c(super.g).a(super.f);
                  }
               } else {
                  for (int var28 = 0; var28 < var12.length; var28++) {
                     var12[var28] = var6[var28].e().c(super.i[var28]).a(super.h[var28]);
                  }
               }

               var9 = this.a(var15, this.s(), (T[])var12, this.n(), var1.c);
               var10 = false;
            }

            this.a((T)var9);
            if (var15) {
               if (this.n().a.a(this.m()).b((T)var3).d() >= 0.0) {
                  this.a((T)var3.b(this.n().a));
               }
            } else if (this.n().a.a(this.m()).b((T)var3).d() <= 0.0) {
               this.a((T)var3.b(this.n().a));
            }

            for (int var24 = 1; var24 < var16; var24++) {
               for (int var29 = 0; var29 < var5.length; var29++) {
                  kO var14 = (kO)var7[0][var29].c(this.k[var24 - 1][0]);

                  for (int var18 = 1; var18 < var24; var18++) {
                     var14 = (kO)var14.a(var7[var18][var29].c(this.k[var24 - 1][var18]));
                  }

                  var8[var29] = (kO)var6[var29].a(this.m().c((T)var14));
               }

               var7[var24] = this.a(this.n().a.a(this.m().c(this.e[var24 - 1])), (T[])var8);
            }

            for (int var25 = 0; var25 < var5.length; var25++) {
               kO var30 = (kO)var7[0][var25].c(this.l[0]);

               for (int var32 = 1; var32 < var16; var32++) {
                  var30 = (kO)var30.a(var7[var32][var25].c(this.l[var32]));
               }

               var8[var25] = (kO)var6[var25].a(this.m().c((T)var30));
            }

            if ((var11 = this.a((T[][])var7, (T[])var6, (T[])var8, this.m())).b(1.0).d() >= 0.0) {
               kO var26 = Ci.b(this.p, Ci.a(this.o, this.n.c((T)var11.a(this.m))));
               var9 = this.a(this.m().c((T)var26), var15, false);
            }
         }

         kO var27 = this.n().a.a(this.m());
         kO[] var31 = (kO[])(this.d >= 0 ? var7[this.d] : this.a((T)var27, (T[])var8));
         uJ var33 = new uJ((T)var27, (T[])var8, (T[])var31);
         System.arraycopy(var8, 0, var6, 0, var5.length);
         this.a(this.a(this.a(var15, (T[][])var7, this.n(), var33, var1.c), (T)var3));
         if (!this.o()) {
            var11 = Ci.b(this.p, Ci.a(this.o, this.n.c((T)var11.a(this.m))));
            kO var17 = this.m().c((T)var11);
            var11 = this.n().a.a((T)var17);
            boolean var21 = var15 ? var11.b(var3).d() >= 0.0 : var11.b(var3).d() <= 0.0;
            var9 = this.a((T)var17, var15, var21);
            var11 = this.n().a.a((T)var9);
            if (var15 ? var11.b(var3).d() >= 0.0 : var11.b(var3).d() <= 0.0) {
               var9 = (kO)var3.b(this.n().a);
            }
         }
      } while (!this.o());

      uJ var23 = this.n();
      this.f_();
      return var23;
   }

   private T u() {
      return this.o;
   }

   private void c(T var1) {
      this.o = (T)var1;
   }

   private T v() {
      return this.p;
   }

   private void d(T var1) {
      this.p = (T)var1;
   }

   protected abstract T a(T[][] var1, T[] var2, T[] var3, T var4);
}
