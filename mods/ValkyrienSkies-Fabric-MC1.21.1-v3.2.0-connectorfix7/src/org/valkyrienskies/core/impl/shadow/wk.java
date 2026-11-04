package org.valkyrienskies.core.impl.shadow;

abstract class wk<T extends kO<T>> extends wr<T> {
   private final kM<T> d;
   private final T[][] e;

   protected wk(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var2, var4, var5, var6, var7, var8);
      this.d = var1;
      this.e = (T[][])Ch.a(var1, var3.length, -1);

      for (int var9 = 0; var9 < var3.length; var9++) {
         this.e[var9] = (T[])((kO[])var3[var9].clone());
      }
   }

   private wk<T> b(boolean var1, uJ<T> var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uG<T> var6) {
      return this.a(this.d, var1, this.e, var2, var3, var4, var5, var6);
   }

   protected abstract wk<T> a(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8);

   protected final T[] a(T... var1) {
      return this.a(this.c().c(), (T[])var1);
   }

   protected final T[] b(T... var1) {
      return this.a(this.d().c(), (T[])var1);
   }

   protected final T[] c(T... var1) {
      return this.a(Ch.a(this.d, this.e[0].length), (T[])var1);
   }

   private T[] a(T[] var1, T... var2) {
      for (int var3 = 0; var3 < var1.length; var3++) {
         for (int var4 = 0; var4 < var2.length; var4++) {
            var1[var3] = (kO)var1[var3].a(var2[var4].c(this.e[var4][var3]));
         }
      }

      return (T[])var1;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final wr a(boolean var1, uJ var2, uJ var3, uJ var4, uJ var5, uG var6) {
      uG var7 = var6;
      uJ var13 = var5;
      var5 = var4;
      var4 = var3;
      var3 = var2;
      boolean var9 = var1;
      wk var8 = this;
      return this.a(var8.d, var9, var8.e, var3, var4, var5, var13, var7);
   }
}
