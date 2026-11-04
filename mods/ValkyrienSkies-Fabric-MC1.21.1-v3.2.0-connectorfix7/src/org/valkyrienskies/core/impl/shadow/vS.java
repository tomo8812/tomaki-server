package org.valkyrienskies.core.impl.shadow;

final class vS<T extends kO<T>> extends wk<T> {
   private final T d;
   private final T e;

   vS(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
      kO var9 = ((kO)var1.a()).a(0.5).k();
      this.d = (T)((kO)var1.b()).b(var9);
      this.e = (T)((kO)var1.b()).a(var9);
   }

   private static vS<T> b(kM<T> var0, boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uG<T> var7) {
      return new vS<>(var0, var1, (T[][])var2, var3, var4, var5, var6, var7);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      kO var5 = (kO)var1.c().b();
      kO var6;
      kO var7 = (var6 = (kO)var2.a(2)).c(var6);
      kO var8 = var2.c(var6.b(3.0)).a(1.0);
      kO var22;
      kO var9 = (kO)(var22 = var6.c(var5.b(var2))).c(this.d);
      var5 = (kO)var22.c(this.e);
      kO var10 = var2.c(var6.b(1.0));
      kO[] var13;
      kO[] var16;
      if (this.a() != null && var2.d() <= 0.5) {
         kO var17;
         var4 = (var17 = var3.d(6.0)).c(((kO)var2.a(6)).b(var7));
         var6 = var17.c(((kO)var7.b(var2.a(9))).a(6.0));
         kO var26 = (kO)var4.c(this.d);
         var4 = (kO)var4.c(this.e);
         var2 = (kO)var17.c(var7.b(var2.a(3)));
         var13 = this.a((T[])(new kO[]{var6, var26, var4, var2}));
         var16 = this.c((T[])(new kO[]{var8, var9, var5, var10}));
      } else {
         var4 = (var3 = var4.d(-6.0)).c(var6.a(2.0).b(var7));
         var6 = var3.c(((kO)var7.b(var2.a(5))).a(1.0));
         kO var11 = (kO)var4.c(this.d);
         var4 = (kO)var4.c(this.e);
         var2 = var3.c(var7.a(var2).a(1.0));
         var13 = this.b((T[])(new kO[]{var6, var11, var4, var2}));
         var16 = this.c((T[])(new kO[]{var8, var9, var5, var10}));
      }

      return new uJ<>((T)var1, (T[])var13, (T[])var16);
   }

   // $VF: synthetic method
   @Override
   protected final wk a(kM var1, boolean var2, kO[][] var3, uJ var4, uJ var5, uJ var6, uJ var7, uG var8) {
      var8 = var8;
      var7 = var7;
      var6 = var6;
      var5 = var5;
      var4 = var4;
      var3 = var3;
      var2 = var2;
      var1 = var1;
      return new vS(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }
}
