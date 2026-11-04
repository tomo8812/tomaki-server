package org.valkyrienskies.core.impl.shadow;

final class vH<T extends kO<T>> extends wk<T> {
   private final T[][] d;

   vH(kM<T> var1, boolean var2, T[][] var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uJ<T> var7, uG<T> var8) {
      super(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
      this.d = (T[][])Ch.a(var1, 7, 16);
      this.d[0][0] = (T)a(var1, 104257.0, 1920240.0);
      this.d[0][1] = (T)var1.a();
      this.d[0][2] = (T)var1.a();
      this.d[0][3] = (T)var1.a();
      this.d[0][4] = (T)var1.a();
      this.d[0][5] = (T)a(var1, 3399327.0, 763840.0);
      this.d[0][6] = (T)a(var1, 6.657843E7F, 3.5198415E7);
      this.d[0][7] = (T)a(var1, -1.674902723E9, 2.887164E8);
      this.d[0][8] = (T)a(var1, 5.4980371265625E13, 1.76692375811392E14);
      this.d[0][9] = (T)a(var1, -734375.0, 4826304.0);
      this.d[0][10] = (T)a(var1, 1.71414593E8, 8.512614E8);
      this.d[0][11] = (T)a(var1, 137909.0, 3084480.0);
      this.d[0][12] = (T)var1.a();
      this.d[0][13] = (T)var1.a();
      this.d[0][14] = (T)var1.a();
      this.d[0][15] = (T)var1.a();
      this.d[1][0] = this.d[0][0].a().a(1.0);
      this.d[1][1] = this.d[0][1].a();
      this.d[1][2] = this.d[0][2].a();
      this.d[1][3] = this.d[0][3].a();
      this.d[1][4] = this.d[0][4].a();
      this.d[1][5] = this.d[0][5].a();
      this.d[1][6] = this.d[0][6].a();
      this.d[1][7] = this.d[0][7].a();
      this.d[1][8] = this.d[0][8].a();
      this.d[1][9] = this.d[0][9].a();
      this.d[1][10] = this.d[0][10].a();
      this.d[1][11] = this.d[0][11].a();
      this.d[1][12] = this.d[0][12].a();
      this.d[1][13] = this.d[0][13].a();
      this.d[1][14] = this.d[0][14].a();
      this.d[1][15] = this.d[0][15].a();
      this.d[2][0] = this.d[0][0].a(2).b(1.0);
      this.d[2][1] = this.d[0][1].a(2);
      this.d[2][2] = this.d[0][2].a(2);
      this.d[2][3] = this.d[0][3].a(2);
      this.d[2][4] = this.d[0][4].a(2);
      this.d[2][5] = this.d[0][5].a(2);
      this.d[2][6] = this.d[0][6].a(2);
      this.d[2][7] = this.d[0][7].a(2);
      this.d[2][8] = this.d[0][8].a(2);
      this.d[2][9] = this.d[0][9].a(2);
      this.d[2][10] = this.d[0][10].a(2);
      this.d[2][11] = this.d[0][11].a(2);
      this.d[2][12] = this.d[0][12].a(2).b(1.0);
      this.d[2][13] = this.d[0][13].a(2);
      this.d[2][14] = this.d[0][14].a(2);
      this.d[2][15] = this.d[0][15].a(2);
      this.d[3][0] = (T)a(var1, -1.7751989329E10, 2.10607656E9);
      this.d[3][1] = (T)var1.a();
      this.d[3][2] = (T)var1.a();
      this.d[3][3] = (T)var1.a();
      this.d[3][4] = (T)var1.a();
      this.d[3][5] = (T)a(var1, 4.272954039E9, 7.53986464E9);
      this.d[3][6] = (T)a(var1, -1.18476319744E11, 3.8604839385E10);
      this.d[3][7] = (T)a(var1, 7.55123450731E11, 3.166577316E11);
      this.d[3][8] = (T)a(var1, 3.6923844612348283E18, 1.7441304416342505E18);
      this.d[3][9] = (T)a(var1, -4.612609375E9, 5.293382976E9);
      this.d[3][10] = (T)a(var1, 2.091772278379E12, 9.336445866E11);
      this.d[3][11] = (T)a(var1, 2.136624137E9, 3.38298912E9);
      this.d[3][12] = (T)a(var1, -126493.0, 1421424.0);
      this.d[3][13] = (T)a(var1, 9.835E7, 5419179.0);
      this.d[3][14] = (T)a(var1, -1.8878125E7, 2053168.0);
      this.d[3][15] = (T)a(var1, -1.944542619E9, 4.38351368E8);
      this.d[4][0] = (T)a(var1, 3.2941697297E10, 3.15911484E9);
      this.d[4][1] = (T)var1.a();
      this.d[4][2] = (T)var1.a();
      this.d[4][3] = (T)var1.a();
      this.d[4][4] = (T)var1.a();
      this.d[4][5] = (T)a(var1, 4.56696183123E11, 1.88496616E9);
      this.d[4][6] = (T)a(var1, 1.9132610714624E13, 1.15814518155E11);
      this.d[4][7] = (T)a(var1, -1.77904688592943E14, 4.749865974E11);
      this.d[4][8] = (T)a(var1, -4.821139941836765E18, 2.180163052042813E17);
      this.d[4][9] = (T)a(var1, 3.0702015625E10, 3.970037232E9);
      this.d[4][10] = (T)a(var1, -8.5916079474274E13, 2.8009337598E12);
      this.d[4][11] = (T)a(var1, -5.919468007E9, 6.3431046E8);
      this.d[4][12] = (T)a(var1, 2479159.0, 157936.0);
      this.d[4][13] = (T)a(var1, -1.875E7, 602131.0);
      this.d[4][14] = (T)a(var1, -1.9203125E7, 2053168.0);
      this.d[4][15] = (T)a(var1, 1.5700361463E10, 4.38351368E8);
      this.d[5][0] = (T)a(var1, 1.2627015655E10, 6.31822968E8);
      this.d[5][1] = (T)var1.a();
      this.d[5][2] = (T)var1.a();
      this.d[5][3] = (T)var1.a();
      this.d[5][4] = (T)var1.a();
      this.d[5][5] = (T)a(var1, -7.2955222965E10, 1.88496616E8);
      this.d[5][6] = (T)a(var1, -1.314574495232E13, 6.9488710893E10);
      this.d[5][7] = (T)a(var1, 3.0084216194513E13, 5.6998391688E10);
      this.d[5][8] = (T)a(var1, -2.9685876100664064E17, 2.5648977082856624E16);
      this.d[5][9] = (T)a(var1, 5.69140625E8, 8.2709109E7);
      this.d[5][10] = (T)a(var1, -1.8684190637E10, 1.8672891732E10);
      this.d[5][11] = (T)a(var1, 6.9644045E7, 8.954971E7F);
      this.d[5][12] = (T)a(var1, -1.1847025E7, 4264272.0);
      this.d[5][13] = (T)a(var1, -9.7865E8, 1.6257537E7);
      this.d[5][14] = (T)a(var1, 5.19371875E8, 6159504.0);
      this.d[5][15] = (T)a(var1, 5.256837225E9, 4.38351368E8);
      this.d[6][0] = (T)a(var1, -4.50944925E8, 1.7550638E7);
      this.d[6][1] = (T)var1.a();
      this.d[6][2] = (T)var1.a();
      this.d[6][3] = (T)var1.a();
      this.d[6][4] = (T)var1.a();
      this.d[6][5] = (T)a(var1, -1.4532122925E10, 9.4248308E7);
      this.d[6][6] = (T)a(var1, -5.958769664E11, 2.573655959E9);
      this.d[6][7] = (T)a(var1, 1.88748653015E11, 5.27762886E8);
      this.d[6][8] = (T)a(var1, 2.5454854581152343E18, 2.7252038150535164E16);
      this.d[6][9] = (T)a(var1, -1.376953125E9, 3.6759604E7);
      this.d[6][10] = (T)a(var1, 5.3995596795E10, 5.18691437E8);
      this.d[6][11] = (T)a(var1, 2.10311225E8, 7047894.0);
      this.d[6][12] = (T)a(var1, -1718875.0, 39484.0);
      this.d[6][13] = (T)a(var1, 5.8E7, 602131.0);
      this.d[6][14] = (T)a(var1, -1546875.0, 39484.0);
      this.d[6][15] = (T)a(var1, -1.262172375E9, 8429834.0);
   }

   private static vH<T> b(kM<T> var0, boolean var1, T[][] var2, uJ<T> var3, uJ<T> var4, uJ<T> var5, uJ<T> var6, uG<T> var7) {
      return new vH<>(var0, var1, (T[][])var2, var3, var4, var5, var6, var7);
   }

   private static T a(kM<T> var0, double var1, double var3) {
      return (T)((kO)var0.a()).a(var1).d(var3);
   }

   @Override
   public final uJ<T> a(T var1, T var2, T var3, T var4) {
      kO var5;
      kO var6 = (var5 = (kO)var1.c().b()).b(var2);
      kO var7 = (kO)var2.a(2);
      kO var8 = var2.c(var2);
      var5 = var5.b(var7);
      kO var9 = var2.c(((kO)var2.a(-3)).a(2.0));
      var7 = var7.c(var2.c(var7.b(3.0)).a(1.0));
      kO var10 = var8.c(var2.c(((kO)var2.a(5)).b(8.0)).a(3.0));
      kO var11 = var8.c(var2.c(var2.c(((kO)var2.a(-6)).a(15.0)).b(12.0)).a(3.0));
      var8 = var8.c(var2.c(var2.c(var2.c(((kO)var2.a(-7)).a(18.0)).b(15.0)).a(4.0)));
      kO[] var19;
      kO[] var22;
      if (this.a() != null && var2.d() <= 0.5) {
         kO var23;
         kO var28;
         kO var31;
         kO var32;
         kO var33;
         var2 = (var28 = (var33 = (var32 = (var31 = (var4 = (var23 = var3).c(var6)).c(var2)).c(var6)).c(var2)).c(var6)).c(var2);
         kO[] var34 = Ch.a(var1.c(), 16);
         kO[] var35 = Ch.a(var1.c(), 16);

         for (int var36 = 0; var36 < var34.length; var36++) {
            var34[var36] = (kO)((kO)((kO)((kO)((kO)((kO)((kO)var23.c(this.d[0][var36])).a(var4.c(this.d[1][var36]))).a(var31.c(this.d[2][var36])))
                        .a(var32.c(this.d[3][var36])))
                     .a(var33.c(this.d[4][var36])))
                  .a(var28.c(this.d[5][var36])))
               .a(var2.c(this.d[6][var36]));
            var35[var36] = this.d[0][var36]
               .a((T)var5.c(this.d[1][var36]))
               .a((T)var9.c(this.d[2][var36]))
               .a((T)var7.c(this.d[3][var36]))
               .a((T)var10.c(this.d[4][var36]))
               .a((T)var11.c(this.d[5][var36]))
               .a((T)var8.c(this.d[6][var36]));
         }

         var19 = this.a(
            (T[])(new kO[]{
               var34[0],
               var34[1],
               var34[2],
               var34[3],
               var34[4],
               var34[5],
               var34[6],
               var34[7],
               var34[8],
               var34[9],
               var34[10],
               var34[11],
               var34[12],
               var34[13],
               var34[14],
               var34[15]
            })
         );
         var22 = this.c(
            (T[])(new kO[]{
               var35[0],
               var35[1],
               var35[2],
               var35[3],
               var35[4],
               var35[5],
               var35[6],
               var35[7],
               var35[8],
               var35[9],
               var35[10],
               var35[11],
               var35[12],
               var35[13],
               var35[14],
               var35[15]
            })
         );
      } else {
         kO var12;
         kO var13;
         kO var14;
         kO var24;
         kO var27;
         var2 = (var27 = (var14 = (var13 = (var12 = (var24 = (kO)(var3 = (kO)var4.a()).c(var2).a()).c(var2)).c(var6)).c(var2)).c(var6)).c(var2);
         kO[] var15 = Ch.a(var1.c(), 16);
         kO[] var16 = Ch.a(var1.c(), 16);

         for (int var17 = 0; var17 < var15.length; var17++) {
            var15[var17] = (kO)((kO)((kO)((kO)((kO)((kO)((kO)var3.c(this.d[0][var17])).a(var24.c(this.d[1][var17]))).a(var12.c(this.d[2][var17])))
                        .a(var13.c(this.d[3][var17])))
                     .a(var14.c(this.d[4][var17])))
                  .a(var27.c(this.d[5][var17])))
               .a(var2.c(this.d[6][var17]));
            var16[var17] = this.d[0][var17]
               .a((T)var5.c(this.d[1][var17]))
               .a((T)var9.c(this.d[2][var17]))
               .a((T)var7.c(this.d[3][var17]))
               .a((T)var10.c(this.d[4][var17]))
               .a((T)var11.c(this.d[5][var17]))
               .a((T)var8.c(this.d[6][var17]));
         }

         var19 = this.b(
            (T[])(new kO[]{
               var15[0],
               var15[1],
               var15[2],
               var15[3],
               var15[4],
               var15[5],
               var15[6],
               var15[7],
               var15[8],
               var15[9],
               var15[10],
               var15[11],
               var15[12],
               var15[13],
               var15[14],
               var15[15]
            })
         );
         var22 = this.c(
            (T[])(new kO[]{
               var16[0],
               var16[1],
               var16[2],
               var16[3],
               var16[4],
               var16[5],
               var16[6],
               var16[7],
               var16[8],
               var16[9],
               var16[10],
               var16[11],
               var16[12],
               var16[13],
               var16[14],
               var16[15]
            })
         );
      }

      return new uJ<>((T)var1, (T[])var19, (T[])var22);
   }

   // $VF: synthetic method
   @Override
   protected final wk a(kM var1, boolean var2, kO[][] var3, uJ var4, uJ var5, uJ var6, uJ var7, uG var8) {
      var8 = var8;
      var7 = var7;
      var6 = var6;
      var5 = var5;
      var4 = var4;
      var3 = var3;
      var2 = var2;
      var1 = var1;
      return new vH(var1, var2, (T[][])var3, var4, var5, var6, var7, var8);
   }
}
