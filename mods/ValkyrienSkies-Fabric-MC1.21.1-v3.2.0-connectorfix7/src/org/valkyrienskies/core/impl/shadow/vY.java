package org.valkyrienskies.core.impl.shadow;

final class vY<T extends kO<T>> extends wk<T> {
   vY(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }

   private static vY<T> b(kM<T> var0, boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uG<T> var7) {
      return new vY<>(var0, var1, (T[][])var2, var3, var4, var5, var6, var7);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      var4 = var2.c(var2.c(var2.c(-10.0).a(16.0)).a(-7.5)).a(1.0);
      kO var5 = (kO)var1.c().a();
      kO var6 = var2.c(var2.c(var2.c(67.5).a(-91.125)).a(28.6875));
      kO var7 = var2.c(var2.c(var2.c(-120.0).a(152.0)).a(-44.0));
      kO var8 = var2.c(var2.c(var2.c(62.5).a(-78.125)).a(23.4375));
      kO var9 = var2.c(0.625).c(((kO)var2.a(2)).b(1.0));
      kO[] var17;
      kO[] var19;
      if (this.a() != null && var2.d() <= 0.5) {
         kO var22 = var3.c(var2.c(var2.c(var2.c(-2.5).a(5.333333333333333)).a(-3.75)).a(1.0));
         kO var23 = (kO)var1.c().a();
         kO var24 = var3.c(var2.c(var2.c(var2.c(16.875).a(-30.375)).a(14.34375)));
         kO var25 = var3.c(var2.c(var2.c(var2.c(-30.0).a(50.666666666666664)).a(-22.0)));
         kO var26 = var3.c(var2.c(var2.c(var2.c(15.625).a(-26.041666666666668)).a(11.71875)));
         var3 = var3.c(var2.c(var2.c(0.4166666666666667).a(-0.3125)));
         var17 = this.a((T[])(new kO[]{var22, var23, var24, var25, var26, var3}));
         var19 = this.c((T[])(new kO[]{var4, var5, var6, var7, var8, var9}));
      } else {
         kO var10 = var2.c(var2);
         kO var11;
         kO var12 = (var11 = var3.d(var2)).c(var2.c(var2.c(var2.c(var2.c(-2.5).a(5.333333333333333)).a(-3.75)).a(1.0)).a(-0.08333333333333333));
         kO var13 = (kO)var1.c().a();
         kO var14 = var11.c(var10.c(var2.c(var2.c(16.875).a(-30.375)).a(14.34375)).a(-0.84375));
         var3 = var11.c(var10.c(var2.c(var2.c(-30.0).a(50.666666666666664)).a(-22.0)).a(1.3333333333333333));
         kO var15 = var11.c(var10.c(var2.c(var2.c(15.625).a(-26.041666666666668)).a(11.71875)).a(-1.3020833333333333));
         var2 = var11.c(var10.c(var2.c(0.4166666666666667).a(-0.3125)).a(-0.10416666666666667));
         var17 = this.b((T[])(new kO[]{var12, var13, var14, var3, var15, var2}));
         var19 = this.c((T[])(new kO[]{var4, var5, var6, var7, var8, var9}));
      }

      return new uJ<>((T)var1, (T[])var17, (T[])var19);
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
      return new vY(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }
}
