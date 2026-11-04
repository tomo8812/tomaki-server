package org.valkyrienskies.core.impl.shadow;

final class vz<T extends kO<T>> extends wk<T> {
   vz(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }

   private static vz<T> b(kM<T> var0, boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uG<T> var7) {
      return new vz<>(var0, var1, (T[][])var2, var3, var4, var5, var6, var7);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      kO var5;
      kO var6 = (var5 = (kO)var1.c().b()).b(var2);
      var5 = (kO)var5.b(var2.a(2));
      kO var7 = var6.c(var5);
      var6 = (kO)var2.c(var6).a(2);
      var5 = (kO)var2.c(var5).a();
      kO[] var11;
      kO[] var14;
      if (this.a() != null && var2.d() <= 0.5) {
         kO var21 = (kO)var2.c(var2).a(4);
         kO var15;
         var4 = (var15 = var3.d(6.0)).c(((kO)var21.b(var2.a(9))).a(6.0));
         kO var22 = var15.c(((kO)var2.a(6)).b(var21));
         var2 = (kO)var15.c(var21.b(var2.a(3)));
         var11 = this.a((T[])(new kO[]{var4, var22, var22, var2}));
         var14 = this.c((T[])(new kO[]{var7, var6, var6, var5}));
      } else {
         kO var8 = (kO)var2.a(4);
         var4 = (var3 = var4.d(6.0)).c(var2.c(((kO)var8.a()).a(5.0)).b(1.0));
         kO var9 = var3.c(var2.c(var8.b(2.0)).b(2.0));
         var2 = var3.c(var2.c(((kO)var8.a()).b(1.0)).b(1.0));
         var11 = this.b((T[])(new kO[]{var4, var9, var9, var2}));
         var14 = this.c((T[])(new kO[]{var7, var6, var6, var5}));
      }

      return new uJ<>((T)var1, (T[])var11, (T[])var14);
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
      return new vz(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }
}
