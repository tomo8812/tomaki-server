package org.valkyrienskies.core.impl.shadow;

public abstract class wj<T extends kO<T>> extends uz<T> implements vQ<T> {
   private final T[] d = this.p();
   private final T[][] e = this.q();
   private final T[] f = this.r();
   private final T g;

   protected wj(kM<T> var1, String var2, T var3) {
      super(var1, var2);
      this.g = (T)var3.e();
   }

   protected final T a(int var1, int var2) {
      return this.a().a().a(var1).d((double)var2);
   }

   protected abstract wk<T> a(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5);

   @Override
   public final uJ<T> a(uH<T> var1, uI<T> var2, T var3) {
      this.a(var2, (T)var3);
      kO var4 = var2.a;
      kO[] var5 = var1.c.a(var2);
      this.a(this.a(var1, (T)var4, (T[])var5, (T)var3));
      boolean var13 = ((kO)var3.b(var2.a)).d() > 0.0;
      int var14 = this.d.length + 1;
      kO[][] var7 = Ch.a(this.a(), var14, -1);
      kO[] var8 = Ch.a(this.a(), var5.length);
      if (var13) {
         if (this.n().a.a(this.g).b((T)var3).d() >= 0.0) {
            this.a((T)var3.b(this.n().a));
         } else {
            this.a(this.g);
         }
      } else if (this.n().a.b(this.g).b((T)var3).d() <= 0.0) {
         this.a((T)var3.b(this.n().a));
      } else {
         this.a(this.g.a());
      }

      this.a(false);

      do {
         kO[] var6 = var1.c.a((uI<T>)this.n());
         var7[0] = var1.c.a(this.n());

         for (int var9 = 1; var9 < var14; var9++) {
            for (int var10 = 0; var10 < var5.length; var10++) {
               kO var11 = (kO)var7[0][var10].c(this.e[var9 - 1][0]);

               for (int var12 = 1; var12 < var9; var12++) {
                  var11 = (kO)var11.a(var7[var12][var10].c(this.e[var9 - 1][var12]));
               }

               var8[var10] = (kO)var6[var10].a(this.m().c((T)var11));
            }

            var7[var9] = this.a(this.n().a.a(this.m().c(this.d[var9 - 1])), (T[])var8);
         }

         for (int var15 = 0; var15 < var5.length; var15++) {
            kO var18 = (kO)var7[0][var15].c(this.f[0]);

            for (int var20 = 1; var20 < var14; var20++) {
               var18 = (kO)var18.a(var7[var20][var15].c(this.f[var20]));
            }

            var8[var15] = (kO)var6[var15].a(this.m().c((T)var18));
         }

         kO var16 = this.n().a.a(this.m());
         kO[] var19 = this.a((T)var16, (T[])var8);
         uJ var21 = new uJ((T)var16, (T[])var8, (T[])var19);
         System.arraycopy(var8, 0, var6, 0, var5.length);
         this.a(this.a(this.a(var13, (T[][])var7, this.n(), var21, var1.c), (T)var3));
         if (!this.o()) {
            kO var22 = this.n().a.a(this.m());
            if (var13 ? var22.b(var3).d() >= 0.0 : var22.b(var3).d() <= 0.0) {
               this.a((T)var3.b(this.n().a));
            }
         }
      } while (!this.o());

      uJ var17 = this.n();
      this.a((uJ<T>)null);
      this.a((T)null);
      return var17;
   }

   private T[] a(uN<T> var1, T var2, T[] var3, T var4) {
      kO[] var5 = (kO[])var3.clone();
      int var6 = this.d.length + 1;
      kO[][] var7 = Ch.a(this.a(), var6, -1);
      kO[] var8 = (kO[])var3.clone();
      var4 = var4.b(var2);
      var7[0] = var1.c();

      for (int var9 = 1; var9 < var6; var9++) {
         for (int var10 = 0; var10 < var3.length; var10++) {
            kO var11 = (kO)var7[0][var10].c(this.e[var9 - 1][0]);

            for (int var12 = 1; var12 < var9; var12++) {
               var11 = (kO)var11.a(var7[var12][var10].c(this.e[var9 - 1][var12]));
            }

            var8[var10] = var5[var10].a(var4.c(var11));
         }

         kO var10003 = (kO)var2.a(var4.c(this.d[var9 - 1]));
         var7[var9] = var1.c();
      }

      for (int var14 = 0; var14 < var3.length; var14++) {
         kO var15 = (kO)var7[0][var14].c(this.f[0]);

         for (int var16 = 1; var16 < var6; var16++) {
            var15 = (kO)var15.a(var7[var16][var14].c(this.f[var16]));
         }

         var5[var14] = var5[var14].a(var4.c(var15));
      }

      return (T[])var5;
   }
}
