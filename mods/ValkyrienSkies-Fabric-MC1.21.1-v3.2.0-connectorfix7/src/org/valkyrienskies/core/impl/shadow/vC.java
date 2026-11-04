package org.valkyrienskies.core.impl.shadow;

public final class vC<T extends kO<T>> extends vK<T> {
   private static final String d = "Dormand-Prince 5(4)";
   private final T e = this.a(71, 57600);
   private final T k = this.a(-71, 16695);
   private final T l = this.a(71, 1920);
   private final T m = this.a(-17253, 339200);
   private final T n = this.a(22, 525);
   private final T o = this.a(-1, 40);

   private vC(kM<T> var1, double var2, double var4, double var6, double var8) {
      super(var1, "Dormand-Prince 5(4)", 6, var2, var4, var6, var8);
   }

   private vC(kM<T> var1, double var2, double var4, double[] var6, double[] var7) {
      super(var1, "Dormand-Prince 5(4)", 6, var2, var4, var6, var7);
   }

   @Override
   public final T[] p() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 6))[0] = this.a(1, 5);
      var1[1] = this.a(3, 10);
      var1[2] = this.a(4, 5);
      var1[3] = this.a(8, 9);
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

      var1[0][0] = this.a(1, 5);
      var1[1][0] = this.a(3, 40);
      var1[1][1] = this.a(9, 40);
      var1[2][0] = this.a(44, 45);
      var1[2][1] = this.a(-56, 15);
      var1[2][2] = this.a(32, 9);
      var1[3][0] = this.a(19372, 6561);
      var1[3][1] = this.a(-25360, 2187);
      var1[3][2] = this.a(64448, 6561);
      var1[3][3] = this.a(-212, 729);
      var1[4][0] = this.a(9017, 3168);
      var1[4][1] = this.a(-355, 33);
      var1[4][2] = this.a(46732, 5247);
      var1[4][3] = this.a(49, 176);
      var1[4][4] = this.a(-5103, 18656);
      var1[5][0] = this.a(35, 384);
      var1[5][1] = this.a().a();
      var1[5][2] = this.a(500, 1113);
      var1[5][3] = this.a(125, 192);
      var1[5][4] = this.a(-2187, 6784);
      var1[5][5] = this.a(11, 84);
      return (T[][])var1;
   }

   @Override
   public final T[] r() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 7))[0] = this.a(35, 384);
      var1[1] = this.a().a();
      var1[2] = this.a(500, 1113);
      var1[3] = this.a(125, 192);
      var1[4] = this.a(-2187, 6784);
      var1[5] = this.a(11, 84);
      var1[6] = this.a().a();
      return (T[])var1;
   }

   private vD<T> b(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5) {
      return new vD<>(this.a(), var1, (T[][])var2, var3, var4, var3, var4, var5);
   }

   @Override
   public final int s() {
      return 5;
   }

   @Override
   protected final T a(T[][] var1, T[] var2, T[] var3, T var4) {
      kO var5 = this.a().a();

      for (int var6 = 0; var6 < super.j; var6++) {
         kO var7 = (kO)((kO)((kO)((kO)((kO)((kO)var1[0][var6].c(this.e)).a(var1[2][var6].c(this.k))).a(var1[3][var6].c(this.l))).a(var1[4][var6].c(this.m)))
               .a(var1[5][var6].c(this.n)))
            .a(var1[6][var6].c(this.o));
         kO var8 = Ci.a(var2[var6].e(), var3[var6].e());
         var8 = super.h == null ? var8.c(super.g).a(super.f) : var8.c(super.i[var6]).a(super.h[var6]);
         var7 = var4.c(var7).d(var8);
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
      vC var7 = this;
      return new vD(var7.a(), var8, (T[][])var9, var4, var11, var4, var11, var6);
   }
}
