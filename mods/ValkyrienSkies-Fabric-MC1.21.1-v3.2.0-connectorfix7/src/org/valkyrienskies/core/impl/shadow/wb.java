package org.valkyrienskies.core.impl.shadow;

public final class wb<T extends kO<T>> extends wj<T> {
   private wb(kM<T> var1, T var2) {
      super(var1, "Luther", (T)var2);
   }

   @Override
   public final T[] p() {
      kO var1 = this.a().a().a(21.0).k();
      kO[] var2;
      (var2 = Ch.a(this.a(), 6))[0] = this.a().b();
      var2[1] = this.a(1, 2);
      var2[2] = this.a(2, 3);
      var2[3] = var1.b(7.0).d(-14.0);
      var2[4] = var1.a(7.0).d(14.0);
      var2[5] = this.a().b();
      return (T[])var2;
   }

   @Override
   public final T[][] q() {
      kO var1 = this.a().a().a(21.0).k();
      kO[][] var2 = Ch.a(this.a(), 6, -1);

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = Ch.a(this.a(), var3 + 1);
      }

      var2[0][0] = this.a().b();
      var2[1][0] = this.a(3, 8);
      var2[1][1] = this.a(1, 8);
      var2[2][0] = this.a(8, 27);
      var2[2][1] = this.a(2, 27);
      var2[2][2] = var2[2][0];
      var2[3][0] = ((kO)var1.a(9)).a(-21.0).d(392.0);
      var2[3][1] = ((kO)var1.a(8)).a(-56.0).d(392.0);
      var2[3][2] = ((kO)var1.a(-48)).a(336.0).d(392.0);
      var2[3][3] = ((kO)var1.a(3)).a(-63.0).d(392.0);
      var2[4][0] = ((kO)var1.a(-255)).a(-1155.0).d(1960.0);
      var2[4][1] = ((kO)var1.a(-40)).a(-280.0).d(1960.0);
      var2[4][2] = ((kO)var1.a(-320)).d(1960.0);
      var2[4][3] = ((kO)var1.a(363)).a(63.0).d(1960.0);
      var2[4][4] = ((kO)var1.a(392)).a(2352.0).d(1960.0);
      var2[5][0] = ((kO)var1.a(105)).a(330.0).d(180.0);
      var2[5][1] = this.a(2, 3);
      var2[5][2] = ((kO)var1.a(280)).a(-200.0).d(180.0);
      var2[5][3] = ((kO)var1.a(-189)).a(126.0).d(180.0);
      var2[5][4] = ((kO)var1.a(-126)).a(-686.0).d(180.0);
      var2[5][5] = ((kO)var1.a(-70)).a(490.0).d(180.0);
      return (T[][])var2;
   }

   @Override
   public final T[] r() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 7))[0] = this.a(1, 20);
      var1[1] = this.a().a();
      var1[2] = this.a(16, 45);
      var1[3] = this.a().a();
      var1[4] = this.a(49, 180);
      var1[5] = var1[4];
      var1[6] = var1[0];
      return (T[])var1;
   }

   private wc<T> b(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5) {
      return new wc<>(this.a(), var1, (T[][])var2, var3, var4, var3, var4, var5);
   }

   // $VF: synthetic method
   @Override
   protected final wk a(boolean var1, kO[][] var2, uJ var3, uJ var4, uG var5) {
      uG var6 = var5;
      uJ var11 = var4;
      var4 = var3;
      kO[][] var9 = var2;
      boolean var8 = var1;
      wb var7 = this;
      return new wc(var7.a(), var8, (T[][])var9, var4, var11, var4, var11, var6);
   }
}
