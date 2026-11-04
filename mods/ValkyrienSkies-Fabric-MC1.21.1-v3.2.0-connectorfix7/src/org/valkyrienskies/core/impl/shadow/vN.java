package org.valkyrienskies.core.impl.shadow;

final class vN<T extends kO<T>> extends wk<T> {
   vN(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }

   private static vN<T> b(kM<T> var0, boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uG<T> var7) {
      return new vN<>(var0, var1, (T[][])var2, var3, var4, var5, var6, var7);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      kO[] var5;
      kO[] var6;
      if (this.a() != null && var2.d() <= 0.5) {
         var5 = this.a((T[])(new kO[]{var3}));
         var6 = this.c((T[])(new kO[]{(kO)var1.c().b()}));
      } else {
         var5 = this.b((T[])(new kO[]{(kO)var4.a()}));
         var6 = this.c((T[])(new kO[]{(kO)var1.c().b()}));
      }

      return new uJ<>((T)var1, (T[])var5, (T[])var6);
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
      return new vN(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }
}
