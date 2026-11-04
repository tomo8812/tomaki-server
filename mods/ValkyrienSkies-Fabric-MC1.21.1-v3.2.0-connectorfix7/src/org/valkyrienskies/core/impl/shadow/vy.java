package org.valkyrienskies.core.impl.shadow;

public final class vy<T extends kO<T>> extends wj<T> {
   private vy(kM<T> var1, T var2) {
      super(var1, "classical Runge-Kutta", (T)var2);
   }

   @Override
   public final T[] p() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 3))[0] = this.a().b().c(0.5);
      var1[1] = var1[0];
      var1[2] = this.a().b();
      return (T[])var1;
   }

   @Override
   public final T[][] q() {
      kO[][] var1 = Ch.a(this.a(), 3, -1);

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = Ch.a(this.a(), var2 + 1);
      }

      var1[0][0] = this.a(1, 2);
      var1[1][0] = this.a().a();
      var1[1][1] = var1[0][0];
      var1[2][0] = this.a().a();
      var1[2][1] = this.a().a();
      var1[2][2] = this.a().b();
      return (T[][])var1;
   }

   @Override
   public final T[] r() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 4))[0] = this.a(1, 6);
      var1[1] = this.a(1, 3);
      var1[2] = var1[1];
      var1[3] = var1[0];
      return (T[])var1;
   }

   private vz<T> b(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5) {
      return new vz<>(this.a(), var1, (T[][])var2, var3, var4, var3, var4, var5);
   }

   // $VF: synthetic method
   @Override
   protected final wk a(boolean var1, kO[][] var2, uJ var3, uJ var4, uG var5) {
      uG var6 = var5;
      uJ var11 = var4;
      var4 = var3;
      kO[][] var9 = var2;
      boolean var8 = var1;
      vy var7 = this;
      return new vz(var7.a(), var8, (T[][])var9, var4, var11, var4, var11, var6);
   }
}
