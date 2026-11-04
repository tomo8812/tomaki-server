package org.valkyrienskies.core.impl.shadow;

final class wo<T extends kO<T>> extends wk<T> {
   wo(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }

   private static wo<T> b(kM<T> var0, boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uG<T> var7) {
      return new wo<>(var0, var1, (T[][])var2, var3, var4, var5, var6, var7);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      kO var5;
      kO var6 = (var5 = var2.c(0.75)).c(((kO)var2.a(4)).b(5.0)).a(1.0);
      kO var7 = var5.c(((kO)var2.a(-6)).a(5.0));
      kO var8 = var5.c(((kO)var2.a(2)).b(1.0));
      kO[] var13;
      kO[] var17;
      if (this.a() != null && var2.d() <= 0.5) {
         var3 = var3.d(8.0);
         var4 = (kO)var2.c(var2).a(4);
         kO var21 = var3.c(((kO)((kO)var4.a(2)).b(var2.a(15))).a(8.0));
         kO var22 = (kO)var3.c(((kO)var2.a(5)).b(var4)).a(3);
         kO var23 = (kO)var3.c(var2).a(3);
         var2 = (kO)var3.c(var4.b(var2.a(3)));
         var13 = this.a((T[])(new kO[]{var21, var22, var23, var2}));
         var17 = this.c((T[])(new kO[]{var6, var7, var5, var8}));
      } else {
         var3 = var4.d(-8.0);
         var4 = (kO)var2.c(var2).a(4);
         kO var9 = var2.a(1.0);
         kO var10 = var3.c(((kO)((kO)var4.a(2)).b(var2.a(7))).a(1.0));
         kO var11 = (kO)var3.c(var9.b(var4)).a(3);
         var2 = (kO)var3.c(var9).a(3);
         var3 = var3.c(var9.a(var4));
         var13 = this.b((T[])(new kO[]{var10, var11, var2, var3}));
         var17 = this.c((T[])(new kO[]{var6, var7, var5, var8}));
      }

      return new uJ<>((T)var1, (T[])var13, (T[])var17);
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
      return new wo(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }
}
