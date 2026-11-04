package org.valkyrienskies.core.impl.shadow;

public final class vG<T extends kO<T>> extends vK<T> {
   private static final String d = "Dormand-Prince 8 (5, 3)";
   private final T e = this.a(1.16092271E8, 8.848466E9F);
   private final T k = this.a(-1871647.0, 1527680.0);
   private final T l = this.a(-6.9799717E7, 1.4079366E8);
   private final T m = this.a(1.230164450203E12, 7.39113984E11);
   private final T n = this.a(-1.980813971228885E15, 5.654156025964544E15);
   private final T o = this.a(4.64500805E8, 1.3899756E9F);
   private final T p = this.a(1.606764981773E12, 1.9613062656E13);
   private final T q = this.a(-137909.0, 6168960.0);
   private final T r = this.a(-364463.0, 1920240.0);
   private final T s = this.a(3399327.0, 763840.0);
   private final T t = this.a(6.657843E7F, 3.5198415E7);
   private final T u = this.a(-1.674902723E9, 2.887164E8);
   private final T v = this.a(-7.4684743568175E13, 1.76692375811392E14);
   private final T w = this.a(-734375.0, 4826304.0);
   private final T x = this.a(1.71414593E8, 8.512614E8);
   private final T y = this.a(69869.0, 3084480.0);

   public vG(kM<T> var1, double var2, double var4, double var6, double var8) {
      super(var1, "Dormand-Prince 8 (5, 3)", 12, var2, var4, var6, var8);
   }

   public vG(kM<T> var1, double var2, double var4, double[] var6, double[] var7) {
      super(var1, "Dormand-Prince 8 (5, 3)", 12, var2, var4, var6, var7);
   }

   @Override
   public final T[] p() {
      kO var1 = this.a().b().a(6).k();
      kO[] var2;
      (var2 = Ch.a(this.a(), 15))[0] = var1.a(-6.0).d(-67.5);
      var2[1] = var1.a(-6.0).d(-45.0);
      var2[2] = var1.a(-6.0).d(-30.0);
      var2[3] = var1.a(6.0).d(30.0);
      var2[4] = this.a(1, 3);
      var2[5] = this.a(1, 4);
      var2[6] = this.a(4, 13);
      var2[7] = this.a(127, 195);
      var2[8] = this.a(3, 5);
      var2[9] = this.a(6, 7);
      var2[10] = this.a().b();
      var2[11] = this.a().b();
      var2[12] = this.a(1.0, 10.0);
      var2[13] = this.a(1.0, 5.0);
      var2[14] = this.a(7.0, 9.0);
      return (T[])var2;
   }

   @Override
   public final T[][] q() {
      kO var1 = this.a().b().a(6).k();
      kO[][] var2 = Ch.a(this.a(), 15, -1);

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = Ch.a(this.a(), var3 + 1);
      }

      var2[0][0] = var1.a(-6.0).d(-67.5);
      var2[1][0] = var1.a(-6.0).d(-180.0);
      var2[1][1] = var1.a(-6.0).d(-60.0);
      var2[2][0] = var1.a(-6.0).d(-120.0);
      var2[2][1] = this.a().a();
      var2[2][2] = var1.a(-6.0).d(-40.0);
      var2[3][0] = ((kO)var1.a(107)).a(462.0).d(3000.0);
      var2[3][1] = this.a().a();
      var2[3][2] = ((kO)var1.a(197)).a(402.0).d(-1000.0);
      var2[3][3] = ((kO)var1.a(73)).a(168.0).d(375.0);
      var2[4][0] = this.a(1, 27);
      var2[4][1] = this.a().a();
      var2[4][2] = this.a().a();
      var2[4][3] = var1.a(16.0).d(108.0);
      var2[4][4] = var1.a(-16.0).d(-108.0);
      var2[5][0] = this.a(19, 512);
      var2[5][1] = this.a().a();
      var2[5][2] = this.a().a();
      var2[5][3] = ((kO)var1.a(23)).a(118.0).d(1024.0);
      var2[5][4] = ((kO)var1.a(-23)).a(118.0).d(1024.0);
      var2[5][5] = this.a(-9, 512);
      var2[6][0] = this.a(13772, 371293);
      var2[6][1] = this.a().a();
      var2[6][2] = this.a().a();
      var2[6][3] = ((kO)var1.a(4784)).a(51544.0).d(371293.0);
      var2[6][4] = ((kO)var1.a(-4784)).a(51544.0).d(371293.0);
      var2[6][5] = this.a(-5688, 371293);
      var2[6][6] = this.a(3072, 371293);
      var2[7][0] = this.a(5.8656157643E10, 9.3983540625E10);
      var2[7][1] = this.a().a();
      var2[7][2] = this.a().a();
      var2[7][3] = var1.c(-3.18801444819E11).a(-1.324889724104E12).d(6.265569375E11);
      var2[7][4] = var1.c(3.18801444819E11).a(-1.324889724104E12).d(6.265569375E11);
      var2[7][5] = this.a(9.6044563816E10, 3.480871875E9);
      var2[7][6] = this.a(5.682451879168E12, 2.81950621875E11);
      var2[7][7] = this.a(-1.65125654E8, 3796875.0);
      var2[8][0] = this.a(8909899.0, 1.8653125E7);
      var2[8][1] = this.a().a();
      var2[8][2] = this.a().a();
      var2[8][3] = var1.c(-1137963.0).a(-4521408.0).d(2937500.0);
      var2[8][4] = var1.c(1137963.0).a(-4521408.0).d(2937500.0);
      var2[8][5] = this.a(9.6663078E7, 4553125.0);
      var2[8][6] = this.a(2.107245E9F, 1.37915625E8);
      var2[8][7] = this.a(-4.913652016E9, 1.47609375E8);
      var2[8][8] = this.a(-7.889427E7, 3.880452869E9);
      var2[9][0] = this.a(-2.0401265806E10, 2.1769653311E10);
      var2[9][1] = this.a().a();
      var2[9][2] = this.a().a();
      var2[9][3] = var1.c(94326.0).a(354216.0).d(112847.0);
      var2[9][4] = var1.c(-94326.0).a(354216.0).d(112847.0);
      var2[9][5] = this.a(-4.3306765128E10, 5.313852383E9);
      var2[9][6] = this.a(-2.0866708358144E13, 1.126708119789E12);
      var2[9][7] = this.a(1.488600343802E13, 6.54632330667E11);
      var2[9][8] = this.a(3.5290686222309376E16, 1.4152473387134412E16);
      var2[9][9] = this.a(-1.477884375E9, 4.85066827E8);
      var2[10][0] = this.a(3.9815761E7, 1.7514443E7);
      var2[10][1] = this.a().a();
      var2[10][2] = this.a().a();
      var2[10][3] = var1.c(-960905.0).a(-3457480.0).d(551636.0);
      var2[10][4] = var1.c(960905.0).a(-3457480.0).d(551636.0);
      var2[10][5] = this.a(-8.44554132E8, 4.7026969E7);
      var2[10][6] = this.a(8.444996352E9, 3.02158619E8);
      var2[10][7] = this.a(-2.509602342E9, 8.77790785E8);
      var2[10][8] = this.a(-2.838879529799625E16, 3.199510091356783E15);
      var2[10][9] = this.a(2.2671625E8, 1.8341897E7);
      var2[10][10] = this.a(1.371316744E9, 2.131383595E9);
      var2[11][0] = this.a(104257.0, 1920240.0);
      var2[11][1] = this.a().a();
      var2[11][2] = this.a().a();
      var2[11][3] = this.a().a();
      var2[11][4] = this.a().a();
      var2[11][5] = this.a(3399327.0, 763840.0);
      var2[11][6] = this.a(6.657843E7F, 3.5198415E7);
      var2[11][7] = this.a(-1.674902723E9, 2.887164E8);
      var2[11][8] = this.a(5.4980371265625E13, 1.76692375811392E14);
      var2[11][9] = this.a(-734375.0, 4826304.0);
      var2[11][10] = this.a(1.71414593E8, 8.512614E8);
      var2[11][11] = this.a(137909.0, 3084480.0);
      var2[12][0] = this.a(1.3481885573E10, 2.4003E11);
      var2[12][1] = this.a().a();
      var2[12][2] = this.a().a();
      var2[12][3] = this.a().a();
      var2[12][4] = this.a().a();
      var2[12][5] = this.a().a();
      var2[12][6] = this.a(1.39418837528E11, 5.49975234375E11);
      var2[12][7] = this.a(-1.1108320068443E13, 4.51119375E13);
      var2[12][8] = this.a(-1.769651421925959E15, 1.424938514608E16);
      var2[12][9] = this.a(5.7799439E7, 3.77055E8);
      var2[12][10] = this.a(7.93322643029E11, 9.673425E13);
      var2[12][11] = this.a(1.458939311E9, 1.9278E11);
      var2[12][12] = this.a(-4149.0, 500000.0);
      var2[13][0] = this.a(1.595561272731E12, 5.01202735E13);
      var2[13][1] = this.a().a();
      var2[13][2] = this.a().a();
      var2[13][3] = this.a().a();
      var2[13][4] = this.a().a();
      var2[13][5] = this.a(9.75183916491E11, 3.445768803125E13);
      var2[13][6] = this.a(3.8492013932672E13, 7.18912673015625E14);
      var2[13][7] = this.a(-1.114881286517557E15, 2.02987107675E16);
      var2[13][8] = this.a().a();
      var2[13][9] = this.a().a();
      var2[13][10] = this.a(-2.538710946863E12, 2.343122786125E16);
      var2[13][11] = this.a(8.824659001E9, 2.306671678125E13);
      var2[13][12] = this.a(-1.1518334563E10, 3.38311846125E13);
      var2[13][13] = this.a(1.912306948E9, 1.3532473845E10);
      var2[14][0] = this.a(-1.3613986967E10, 3.1741908048E10);
      var2[14][1] = this.a().a();
      var2[14][2] = this.a().a();
      var2[14][3] = this.a().a();
      var2[14][4] = this.a().a();
      var2[14][5] = this.a(-4.755612631E9, 1.012344804E9);
      var2[14][6] = this.a(4.2939257944576E13, 5.588559685701E12);
      var2[14][7] = this.a(7.7881972900277E13, 1.9140370552944E13);
      var2[14][8] = this.a(2.2719829234375E13, 6.3689648654052E13);
      var2[14][9] = this.a().a();
      var2[14][10] = this.a().a();
      var2[14][11] = this.a().a();
      var2[14][12] = this.a(-1.199007803E9, 8.57031517296E11);
      var2[14][13] = this.a(1.57882067E11, 5.3564469831E10);
      var2[14][14] = this.a(-2.90468882375E11, 3.1741908048E10);
      return (T[][])var2;
   }

   @Override
   public final T[] r() {
      kO[] var1;
      (var1 = Ch.a(this.a(), 16))[0] = this.a(104257, 1920240);
      var1[1] = this.a().a();
      var1[2] = this.a().a();
      var1[3] = this.a().a();
      var1[4] = this.a().a();
      var1[5] = this.a(3399327.0, 763840.0);
      var1[6] = this.a(6.657843E7F, 3.5198415E7);
      var1[7] = this.a(-1.674902723E9, 2.887164E8);
      var1[8] = this.a(5.4980371265625E13, 1.76692375811392E14);
      var1[9] = this.a(-734375.0, 4826304.0);
      var1[10] = this.a(1.71414593E8, 8.512614E8);
      var1[11] = this.a(137909.0, 3084480.0);
      var1[12] = this.a().a();
      var1[13] = this.a().a();
      var1[14] = this.a().a();
      var1[15] = this.a().a();
      return (T[])var1;
   }

   private vH<T> b(boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uG<T> var5) {
      return new vH<>(this.a(), var1, (T[][])var2, var3, var4, var3, var4, var5);
   }

   @Override
   public final int s() {
      return 8;
   }

   @Override
   protected final T a(T[][] var1, T[] var2, T[] var3, T var4) {
      kO var5 = (kO)var4.c().a();
      kO var6 = (kO)var4.c().a();

      for (int var7 = 0; var7 < super.j; var7++) {
         kO var8 = (kO)((kO)((kO)((kO)((kO)((kO)((kO)((kO)var1[0][var7].c(this.e)).a(var1[5][var7].c(this.k))).a(var1[6][var7].c(this.l)))
                        .a(var1[7][var7].c(this.m)))
                     .a(var1[8][var7].c(this.n)))
                  .a(var1[9][var7].c(this.o)))
               .a(var1[10][var7].c(this.p)))
            .a(var1[11][var7].c(this.q));
         kO var9 = (kO)((kO)((kO)((kO)((kO)((kO)((kO)((kO)var1[0][var7].c(this.r)).a(var1[5][var7].c(this.s))).a(var1[6][var7].c(this.t)))
                        .a(var1[7][var7].c(this.u)))
                     .a(var1[8][var7].c(this.v)))
                  .a(var1[9][var7].c(this.w)))
               .a(var1[10][var7].c(this.x)))
            .a(var1[11][var7].c(this.y));
         kO var10 = Ci.a(var2[var7].e(), var3[var7].e());
         var10 = super.h == null ? var10.c(super.g).a(super.f) : var10.c(super.i[var7]).a(super.h[var7]);
         var8 = var8.d(var10);
         var5 = var5.a(var8.c(var8));
         var8 = var9.d(var10);
         var6 = var6.a(var8.c(var8));
      }

      kO var11;
      if ((var11 = var5.a(var6.c(0.01))).d() <= 0.0) {
         var11 = (kO)var4.c().b();
      }

      return (T)var4.e().c(var5).d(((kO)var11.a(super.j)).k());
   }

   // $VF: synthetic method
   @Override
   protected final wk a(boolean var1, kO[][] var2, uJ var3, uJ var4, uG var5) {
      uG var6 = var5;
      uJ var11 = var4;
      var4 = var3;
      kO[][] var9 = var2;
      boolean var8 = var1;
      vG var7 = this;
      return new vH(var7.a(), var8, (T[][])var9, var4, var11, var4, var11, var6);
   }
}
