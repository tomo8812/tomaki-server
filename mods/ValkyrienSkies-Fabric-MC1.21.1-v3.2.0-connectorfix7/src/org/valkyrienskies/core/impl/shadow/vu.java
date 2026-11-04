package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class vu<T extends kO<T>> {
   private static final Map<Integer, Map<kM<? extends kO<?>>, vu<? extends kO<?>>>> d = new HashMap<>();
   final kM<T> a;
   final sx<T> b;
   final T[] c;

   private vu(kM<T> var1, int var2) {
      this.a = var1;
      int var4 = --var2;
      vu var3 = this;
      kO[][] var13 = Ch.a(this.a, var4, var4);

      for (int var5 = 1; var5 <= var13.length; var5++) {
         kO[] var6 = var13[var5 - 1];
         int var7 = -var5;
         kO var8 = var3.a.a().a(var7);

         for (int var9 = 1; var9 <= var6.length; var9++) {
            var6[var9 - 1] = (kO)var8.a(var9 + 1);
            var8 = (kO)var8.a(var7);
         }
      }

      sx var11 = new sx<>(var13, false);
      sP.a var14 = new sP<>(var11).a();
      kO[] var15;
      Arrays.fill(var15 = Ch.a(var1, var2), var1.b());
      this.c = (T[])var14.a(new sz<>(var15, false)).h();

      for (int var16 = (var12 = (kO[][])var11.c()).length - 1; var16 > 0; var16--) {
         var12[var16] = var12[var16 - 1];
      }

      var12[0] = Ch.a(var1, var2);
      Arrays.fill(var12[0], var1.a());
      this.b = new sx<>((T[][])var14.a(new sx<>(var12, false)).c());
   }

   public static <T extends kO<T>> vu<T> a(kM<T> var0, int var1) {
      synchronized (d) {
         Map var3;
         if ((var3 = d.get(var1)) == null) {
            var3 = new HashMap();
            d.put(var1, var3);
         }

         vu var4;
         if ((var4 = (vu)var3.get(var0)) == null) {
            var4 = new vu(var0, var1);
            var3.put(var0, var4);
         }

         return var4;
      }
   }

   private sQ<T> a(int var1) {
      kO[][] var7 = Ch.a(this.a, var1, var1);

      for (int var2 = 1; var2 <= var7.length; var2++) {
         kO[] var3 = var7[var2 - 1];
         int var4 = -var2;
         kO var5 = this.a.a().a(var4);

         for (int var6 = 1; var6 <= var3.length; var6++) {
            var3[var6 - 1] = (kO)var5.a(var6 + 1);
            var5 = (kO)var5.a(var4);
         }
      }

      return new sx<>((T[][])var7, false);
   }

   public final sx<T> a(T var1, T[] var2, T[][] var3, T[][] var4) {
      kO[][] var5 = Ch.a(this.a, this.c.length + 1, this.c.length + 1);
      kO[][] var6 = Ch.a(this.a, this.c.length + 1, var3[0].length);
      kO[] var7 = var3[0];
      kO[] var8 = var4[0];

      for (int var9 = 1; var9 < var3.length; var9++) {
         kO var10;
         kO var11 = (var10 = var2[var9].b(var2[0])).d(var1);
         kO var12 = (kO)var1.b();
         kO[] var13 = var5[2 * var9 - 2];
         kO[] var14 = 2 * var9 - 1 < var5.length ? var5[2 * var9 - 1] : null;

         for (int var15 = 0; var15 < var13.length; var15++) {
            var12 = var12.c(var11);
            var13[var15] = var10.c(var12);
            if (var14 != null) {
               var14[var15] = (kO)var12.a(var15 + 2);
            }
         }

         kO[] var24 = var3[var9];
         kO[] var17 = var4[var9];
         kO[] var19 = var6[2 * var9 - 2];
         var13 = 2 * var9 - 1 < var6.length ? var6[2 * var9 - 1] : null;

         for (int var23 = 0; var23 < var24.length; var23++) {
            var19[var23] = var24[var23].b(var7[var23]).b(var10.c(var8[var23]));
            if (var13 != null) {
               var13[var23] = var17[var23].b(var8[var23]);
            }
         }
      }

      sx var16 = new sP<>(new sx<>((T[][])var5, false)).a().a(new sx<>((T[][])var6, false));
      sx var18 = new sx<>(this.a, var16.f() - 1, var16.g());

      for (int var20 = 0; var20 < var18.f(); var20++) {
         for (int var22 = 0; var22 < var18.g(); var22++) {
            var18.a(var20, var22, (T)var16.b(var20, var22));
         }
      }

      return var18;
   }

   public final sx<T> a(sx<T> var1) {
      return this.b.a(var1);
   }

   public final void a(T[] var1, T[] var2, sx<T> var3) {
      kO[][] var8 = var3.a;

      for (int var4 = 0; var4 < var8.length; var4++) {
         kO[] var5 = var8[var4];
         kO var6 = this.c[var4];

         for (int var7 = 0; var7 < var5.length; var7++) {
            var5[var7] = var5[var7].a(var6.c(var1[var7].b(var2[var7])));
         }
      }
   }
}
