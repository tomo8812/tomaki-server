package org.valkyrienskies.core.impl.shadow;

final class wg<T extends kO<T>> extends wk<T> {
   wg(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }

   private static wg<T> b(kM<T> var0, boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uG<T> var7) {
      return new wg<>(var0, var1, (T[][])var2, var3, var4, var5, var6, var7);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      kO var5 = (kO)var2.a(2);
      kO var6 = ((kO)var1.c().b()).b(var5);
      kO[] var9;
      kO[] var11;
      if (this.a() != null && var2.d() <= 0.5) {
         kO var12 = var2.c(var4);
         var2 = var2.c(var3);
         var9 = this.a((T[])(new kO[]{var12, var2}));
         var11 = this.c((T[])(new kO[]{var6, var5}));
      } else {
         kO var7 = var4.c(var2);
         var2 = (kO)var4.c(var2.a(1.0)).a();
         var9 = this.b((T[])(new kO[]{var7, var2}));
         var11 = this.c((T[])(new kO[]{var6, var5}));
      }

      return new uJ<>((T)var1, (T[])var9, (T[])var11);
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
      return new wg(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }
}
