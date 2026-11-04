package org.valkyrienskies.core.impl.shadow;

public final class vR<T extends kO<T>> extends wj<T> {
   private vR(kM<T> var1, T var2) {
      super(var1, "Gill", (T)var2);
   }

   @Override
   public final T[] p() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 3))[0] = this.a(1, 2);
      var1[1] = var1[0];
      var1[2] = this.a().b();
      return (T[])var1;
   }

   @Override
   public final T[][] q() {
      kO var1 = this.a().a().a(2.0).k();
      kO[][] var2 = Ch.a(this.a(), 3, -1);

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = Ch.a(this.a(), var3 + 1);
      }

      var2[0][0] = this.a(1, 2);
      var2[1][0] = var1.b(1.0).c(0.5);
      var2[1][1] = var1.b(2.0).c(-0.5);
      var2[2][0] = this.a().a();
      var2[2][1] = var1.c(-0.5);
      var2[2][2] = var1.a(2.0).c(0.5);
      return (T[][])var2;
   }

   @Override
   public final T[] r() {
      kO var1 = this.a().a().a(2.0).k();
      kO[] var2;
      (var2 = Ch.a(this.a(), 4))[0] = this.a(1, 6);
      var2[1] = var1.b(2.0).d(-6.0);
      var2[2] = var1.a(2.0).d(6.0);
      var2[3] = var2[0];
      return (T[])var2;
   }

   private vS<T> b(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5) {
      return new vS<>(this.a(), var1, (T[][])var2, var3, var4, var3, var4, var5);
   }

   // $VF: synthetic method
   @Override
   protected final wk a(boolean var1, kO[][] var2, uJ var3, uJ var4, uG var5) {
      uG var6 = var5;
      uJ var11 = var4;
      var4 = var3;
      kO[][] var9 = var2;
      boolean var8 = var1;
      vR var7 = this;
      return new vS(var7.a(), var8, (T[][])var9, var4, var11, var4, var11, var6);
   }
}
