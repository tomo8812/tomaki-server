package org.valkyrienskies.core.impl.shadow;

public final class vX<T extends kO<T>> extends vK<T> {
   private static final String d = "Higham-Hall 5(4)";
   private final T[] e;

   private vX(kM<T> var1, double var2, double var4, double var6, double var8) {
      super(var1, "Higham-Hall 5(4)", -1, var2, var4, var6, var8);
      this.e = (T[])Ch.a(var1, 7);
      this.e[0] = this.a(-1, 20);
      this.e[1] = (T)var1.a();
      this.e[2] = this.a(81, 160);
      this.e[3] = this.a(-6, 5);
      this.e[4] = this.a(25, 32);
      this.e[5] = this.a(1, 16);
      this.e[6] = this.a(-1, 10);
   }

   private vX(kM<T> var1, double var2, double var4, double[] var6, double[] var7) {
      super(var1, "Higham-Hall 5(4)", -1, var2, var4, var6, var7);
      this.e = (T[])Ch.a(var1, 7);
      this.e[0] = this.a(-1, 20);
      this.e[1] = (T)var1.a();
      this.e[2] = this.a(81, 160);
      this.e[3] = this.a(-6, 5);
      this.e[4] = this.a(25, 32);
      this.e[5] = this.a(1, 16);
      this.e[6] = this.a(-1, 10);
   }

   @Override
   public final T[] p() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 6))[0] = this.a(2, 9);
      var1[1] = this.a(1, 3);
      var1[2] = this.a(1, 2);
      var1[3] = this.a(3, 5);
      var1[4] = this.a().b();
      var1[5] = this.a().b();
      return (T[])var1;
   }

   @Override
   public final T[][] q() {
      kO[][] var1 = Ch.a(this.a(), 6, -1);

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = Ch.a(this.a(), var2 + 1);
      }

      var1[0][0] = this.a(2, 9);
      var1[1][0] = this.a(1, 12);
      var1[1][1] = this.a(1, 4);
      var1[2][0] = this.a(1, 8);
      var1[2][1] = this.a().a();
      var1[2][2] = this.a(3, 8);
      var1[3][0] = this.a(91, 500);
      var1[3][1] = this.a(-27, 100);
      var1[3][2] = this.a(78, 125);
      var1[3][3] = this.a(8, 125);
      var1[4][0] = this.a(-11, 20);
      var1[4][1] = this.a(27, 20);
      var1[4][2] = this.a(12, 5);
      var1[4][3] = this.a(-36, 5);
      var1[4][4] = this.a(5, 1);
      var1[5][0] = this.a(1, 12);
      var1[5][1] = this.a().a();
      var1[5][2] = this.a(27, 32);
      var1[5][3] = this.a(-4, 3);
      var1[5][4] = this.a(125, 96);
      var1[5][5] = this.a(5, 48);
      return (T[][])var1;
   }

   @Override
   public final T[] r() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 7))[0] = this.a(1, 12);
      var1[1] = this.a().a();
      var1[2] = this.a(27, 32);
      var1[3] = this.a(-4, 3);
      var1[4] = this.a(125, 96);
      var1[5] = this.a(5, 48);
      var1[6] = this.a().a();
      return (T[])var1;
   }

   private vY<T> b(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5) {
      return new vY<>(this.a(), var1, (T[][])var2, var3, var4, var3, var4, var5);
   }

   @Override
   public final int s() {
      return 5;
   }

   @Override
   protected final T a(T[][] var1, T[] var2, T[] var3, T var4) {
      kO var5 = this.a().a();

      for (int var6 = 0; var6 < super.j; var6++) {
         kO var7 = (kO)var1[0][var6].c(this.e[0]);

         for (int var8 = 1; var8 < this.e.length; var8++) {
            var7 = (kO)var7.a(var1[var8][var6].c(this.e[var8]));
         }

         kO var10 = Ci.a(var2[var6].e(), var3[var6].e());
         var10 = super.h == null ? var10.c(super.g).a(super.f) : var10.c(super.i[var6]).a(super.h[var6]);
         var7 = var4.c(var7).d(var10);
         var5 = var5.a(var7.c(var7));
      }

      return (T)var5.d((double)super.j).k();
   }

   // $VF: synthetic method
   @Override
   protected final wk a(boolean var1, kO[][] var2, uJ var3, uJ var4, uG var5) {
      uG var6 = var5;
      uJ var11 = var4;
      var4 = var3;
      kO[][] var9 = var2;
      boolean var8 = var1;
      vX var7 = this;
      return new vY(var7.a(), var8, (T[][])var9, var4, var11, var4, var11, var6);
   }
}
