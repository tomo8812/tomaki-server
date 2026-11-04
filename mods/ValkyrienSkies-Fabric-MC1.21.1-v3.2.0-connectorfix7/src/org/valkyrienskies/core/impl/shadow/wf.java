package org.valkyrienskies.core.impl.shadow;

public final class wf<T extends kO<T>> extends wj<T> {
   private wf(kM<T> var1, T var2) {
      super(var1, "midpoint", (T)var2);
   }

   @Override
   public final T[] p() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 1))[0] = this.a().b().c(0.5);
      return (T[])var1;
   }

   @Override
   public final T[][] q() {
      kO[][] var1;
      (var1 = Ch.a(this.a(), 1, 1))[0][0] = this.a(1, 2);
      return (T[][])var1;
   }

   @Override
   public final T[] r() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 2))[0] = this.a().a();
      var1[1] = this.a().b();
      return (T[])var1;
   }

   private wg<T> b(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5) {
      return new wg<>(this.a(), var1, (T[][])var2, var3, var4, var3, var4, var5);
   }

   // $VF: synthetic method
   @Override
   protected final wk a(boolean var1, kO[][] var2, uJ var3, uJ var4, uG var5) {
      uG var6 = var5;
      uJ var11 = var4;
      var4 = var3;
      kO[][] var9 = var2;
      boolean var8 = var1;
      wf var7 = this;
      return new wg(var7.a(), var8, (T[][])var9, var4, var11, var4, var11, var6);
   }
}
