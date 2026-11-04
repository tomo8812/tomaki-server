package org.valkyrienskies.core.impl.shadow;

public abstract class vp<T extends kO<T>> extends uS<T> {
   private final vu<T> k;

   public vp(kM<T> var1, String var2, int var3, int var4, double var5, double var7, double var9, double var11) {
      super(var1, var2, var3, var4, var5, var7, var9, var11);
      this.k = vu.a(var1, var3);
   }

   public vp(kM<T> var1, String var2, int var3, int var4, double var5, double var7, double[] var9, double[] var10) {
      super(var1, var2, var3, var4, var5, var7, var9, var10);
      this.k = vu.a(var1, var3);
   }

   @Override
   public abstract uJ<T> a(uH<T> var1, uI<T> var2, T var3);

   @Override
   public final sx<T> a(T var1, T[] var2, T[][] var3, T[][] var4) {
      kO[][] var5 = var4;
      var4 = var3;
      kO[] var19 = var2;
      kO var18 = var1;
      vu var17 = this.k;
      kO[][] var6 = Ch.a(this.k.a, var17.c.length + 1, var17.c.length + 1);
      kO[][] var7 = Ch.a(var17.a, var17.c.length + 1, var4[0].length);
      kO[] var8 = var4[0];
      kO[] var9 = var5[0];

      for (int var10 = 1; var10 < var4.length; var10++) {
         kO var11;
         kO var12 = (var11 = var19[var10].b(var19[0])).d(var18);
         kO var13 = (kO)var18.b();
         kO[] var14 = var6[2 * var10 - 2];
         kO[] var15 = 2 * var10 - 1 < var6.length ? var6[2 * var10 - 1] : null;

         for (int var16 = 0; var16 < var14.length; var16++) {
            var13 = var13.c(var12);
            var14[var16] = var11.c(var13);
            if (var15 != null) {
               var15[var16] = (kO)var13.a(var16 + 2);
            }
         }

         kO[] var29 = var4[var10];
         kO[] var22 = var5[var10];
         kO[] var24 = var7[2 * var10 - 2];
         var14 = 2 * var10 - 1 < var7.length ? var7[2 * var10 - 1] : null;

         for (int var28 = 0; var28 < var29.length; var28++) {
            var24[var28] = var29[var28].b(var8[var28]).b(var11.c(var9[var28]));
            if (var14 != null) {
               var14[var28] = var22[var28].b(var9[var28]);
            }
         }
      }

      sx var21 = new sP<>(new sx<>((T[][])var6, false)).a().a(new sx<>((T[][])var7, false));
      sx var23 = new sx<>(var17.a, var21.f() - 1, var21.g());

      for (int var25 = 0; var25 < var23.f(); var25++) {
         for (int var27 = 0; var27 < var23.g(); var27++) {
            var23.a(var25, var27, (T)var21.b(var25, var27));
         }
      }

      return var23;
   }

   public final sx<T> a(sx<T> var1) {
      sx var2 = var1;
      vu var3 = this.k;
      return this.k.b.a(var2);
   }

   public final void a(T[] var1, T[] var2, sx<T> var3) {
      sx var4 = var3;
      kO[] var11 = var2;
      var2 = var1;
      vu var9 = this.k;
      kO[][] var12 = var4.a;

      for (int var5 = 0; var5 < var12.length; var5++) {
         kO[] var6 = var12[var5];
         kO var7 = var9.c[var5];

         for (int var8 = 0; var8 < var6.length; var8++) {
            var6[var8] = var6[var8].a(var7.c(var2[var8].b(var11[var8])));
         }
      }
   }
}
