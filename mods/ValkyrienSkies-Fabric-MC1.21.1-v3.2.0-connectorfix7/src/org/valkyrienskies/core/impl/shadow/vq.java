package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

final class vq<T extends kO<T>> extends wr<T> {
   private T d;
   private final uJ<T> e;
   private final T[] f;
   private final sx<T> g;

   vq(T var1, uJ<T> var2, T[] var3, sx<T> var4, boolean var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      this((T)var1, var2, (T[])var3, var4, var5, var6, var7, var6, var7, var8);
   }

   private vq(T var1, uJ<T> var2, T[] var3, sx<T> var4, boolean var5, uJ<T> var6, uJ<T> var7, uJ<T> var8, uJ<T> var9, uG<T> var10) {
      super(var5, var6, var7, var8, var9, var10);
      this.d = (T)var1;
      this.e = var2;
      this.f = (T[])((kO[])var3.clone());
      this.g = new sx<>((T[][])var4.c(), false);
   }

   private vq<T> b(boolean var1, uJ<T> var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uG<T> var6) {
      return new vq<>(this.d, this.e, this.f, this.g, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      return a(this.e, (T)var1, this.d, this.f, this.g);
   }

   public static <S extends kO<S>> uJ<S> a(uJ<S> var0, S var1, S var2, S[] var3, sx<S> var4) {
      kO var5;
      var2 = (var5 = (kO)var1.b(var0.a)).d(var2);
      kO[] var6;
      Arrays.fill(var6 = Ch.a(var1.c(), var3.length), var1.c().a());
      kO[] var7;
      Arrays.fill(var7 = Ch.a(var1.c(), var3.length), var1.c().a());

      for (int var8 = (var15 = var4.a).length - 1; var8 >= 0; var8--) {
         int var9 = var8 + 2;
         kO[] var10 = var15[var8];
         kO var11 = var2.d(var9);

         for (int var12 = 0; var12 < var10.length; var12++) {
            kO var13 = var10[var12].c(var11);
            var6[var12] = var6[var12].a(var13);
            var7[var12] = (kO)var7[var12].a(var13.a(var9));
         }
      }

      kO[] var16 = var0.c();

      for (int var17 = 0; var17 < var6.length; var17++) {
         var6[var17] = var6[var17].a(var3[var17].c(var2));
         var16[var17] = var16[var17].a(var6[var17]);
         var7[var17] = var7[var17].a(var3[var17].c(var2)).d(var5);
      }

      return new uJ<>((S)var1, (S[])var16, (S[])var7);
   }

   // $VF: synthetic method
   @Override
   public final wr a(boolean var1, uJ var2, uJ var3, uJ var4, uJ var5, uG var6) {
      uG var7 = var6;
      uJ var13 = var5;
      var5 = var4;
      var4 = var3;
      var3 = var2;
      boolean var9 = var1;
      vq var8 = this;
      return new vq<>(var8.d, var8.e, var8.f, var8.g, var9, var3, var4, var5, var13, var7);
   }
}
