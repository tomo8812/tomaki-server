package org.valkyrienskies.core.impl.shadow;

public final class vM<T extends kO<T>> extends wj<T> {
   private vM(kM<T> var1, T var2) {
      super(var1, "Euler", (T)var2);
   }

   @Override
   public final T[] p() {
      return Ch.a(this.a(), 0);
   }

   @Override
   public final T[][] q() {
      return Ch.a(this.a(), 0, 0);
   }

   @Override
   public final T[] r() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 1))[0] = this.a().b();
      return (T[])var1;
   }

   private vN<T> b(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5) {
      return new vN<>(this.a(), var1, (T[][])var2, var3, var4, var3, var4, var5);
   }

   // $VF: synthetic method
   @Override
   protected final wk a(boolean var1, kO[][] var2, uJ var3, uJ var4, uG var5) {
      uG var6 = var5;
      uJ var11 = var4;
      var4 = var3;
      kO[][] var9 = var2;
      boolean var8 = var1;
      vM var7 = this;
      return new vN(var7.a(), var8, (T[][])var9, var4, var11, var4, var11, var6);
   }
}
