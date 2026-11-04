package org.valkyrienskies.core.impl.shadow;

public final class vn<T extends kO<T>> extends vp<T> {
   private static final String k = "Adams-Bashforth";

   private vn(kM<T> var1, int var2, double var3, double var5, double var7, double var9) {
      super(var1, "Adams-Bashforth", var2, var2, var3, var5, var7, var9);
   }

   private vn(kM<T> var1, int var2, double var3, double var5, double[] var7, double[] var8) {
      super(var1, "Adams-Bashforth", var2, var2, var3, var5, var7, var8);
   }

   private T a(T[] var1, T[] var2, T[] var3, sQ<T> var4) {
      kO var5 = this.a().a();

      for (int var6 = 0; var6 < super.j; var6++) {
         kO var7 = var2[var6].e();
         var7 = super.h == null ? var7.c(super.g).a(super.f) : var7.c(super.i[var6]).a(super.h[var6]);
         kO var8 = this.a().a();
         int var9 = var4.f() % 2 == 0 ? -1 : 1;

         for (int var10 = var4.f() - 1; var10 >= 0; var10--) {
            var8 = (kO)var8.a(((kO)var4.b(var10, var6)).a(var9));
            var9 = -var9;
         }

         var8 = var8.b(var3[var6]);
         kO var13 = var2[var6].b(var1[var6]).a(var8).d(var7);
         var5 = var5.a(var13.c(var13));
      }

      return (T)var5.d((double)super.j).k();
   }

   @Override
   public final uJ<T> a(uH<T> var1, uI<T> var2, T var3) {
      this.a(var2, (T)var3);
      kO var4 = var2.a;
      kO[] var5 = var1.c.a(var2);
      this.a(this.a(var1, (T)var4, (T[])var5, (T)var3));
      boolean var20 = ((kO)var3.b(var2.a)).d() > 0.0;
      this.a(var1, this.n(), (T)var3);
      uJ var21;
      uJ var22 = vq.a(var21 = this.n(), var21.a.a(this.m()), this.m(), super.d, super.e);
      this.a(false);

      do {
         kO[] var6 = null;
         kO[] var7 = Ch.a(this.a(), var5.length);
         sx var8 = null;
         kO var9 = this.a().a().a(10.0);

         while (var9.b(1.0).d() >= 0.0) {
            var6 = var22.c();
            kO[] var28 = this.a(var22.a, (T[])var6);

            for (int var10 = 0; var10 < var7.length; var10++) {
               var7[var10] = this.m().c((T)var28[var10]);
            }

            var8 = this.a(super.e);
            this.a(super.d, (T[])var7, var8);
            sx var13 = var8;
            kO[] var12 = var7;
            kO[] var11 = var6;
            kO[] var31 = var5;
            vn var30 = this;
            kO var14 = this.a().a();

            for (int var15 = 0; var15 < var30.j; var15++) {
               kO var16 = var11[var15].e();
               var16 = var30.h == null ? var16.c(var30.g).a(var30.f) : var16.c(var30.i[var15]).a(var30.h[var15]);
               kO var17 = (kO)var30.a().a();
               int var18 = var13.f() % 2 == 0 ? -1 : 1;

               for (int var19 = var13.f() - 1; var19 >= 0; var19--) {
                  var17 = (kO)var17.a(((kO)var13.b(var19, var15)).a(var18));
                  var18 = -var18;
               }

               var17 = var17.b(var12[var15]);
               kO var36 = var11[var15].b(var31[var15]).a(var17).d(var16);
               var14 = var14.a(var36.c(var36));
            }

            if ((var9 = var14.d((double)var30.j).k()).b(1.0).d() >= 0.0) {
               kO var32 = this.c((T)var9);
               this.b(this.a(this.m().c((T)var32), var20, false));
               var22 = vq.a(this.n(), this.n().a.a(this.m()), this.m(), super.d, super.e);
            }
         }

         this.a(this.a(new vq<>(this.m(), var22, (T[])var7, var8, var20, this.n(), var22, var1.c), (T)var3));
         super.d = (T[])var7;
         super.e = var8;
         if (!this.o()) {
            System.arraycopy(var6, 0, var5, 0, var5.length);
            if (this.l()) {
               this.a(var1, this.n(), (T)var3);
            }

            kO var29 = this.c((T)var9);
            kO var33 = this.m().c((T)var29);
            var4 = this.n().a.a((T)var33);
            boolean var24 = var20 ? var4.b(var3).d() >= 0.0 : var4.b(var3).d() <= 0.0;
            var4 = this.a((T)var33, var20, var24);
            kO var26 = this.n().a.a((T)var4);
            if (var20 ? var26.b(var3).d() >= 0.0 : var26.b(var3).d() <= 0.0) {
               var4 = (kO)var3.b(this.n().a);
            }

            this.b((T)var4);
            var22 = vq.a(this.n(), this.n().a.a(this.m()), this.m(), super.d, super.e);
         }
      } while (!this.o());

      uJ var27 = this.n();
      this.a((uJ<T>)null);
      this.a((T)null);
      return var27;
   }
}
