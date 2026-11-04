package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class ne implements nf {
   private nd b(double[] var1, double[] var2, double[] var3, double[][][] var4) {
      if (var1.length != 0 && var2.length != 0 && var3.length != 0 && var4.length != 0) {
         if (var1.length != var4.length) {
            throw new oS(var1.length, var4.length);
         }

         Ch.a(var1);
         Ch.a(var2);
         Ch.a(var3);
         int var5 = var1.length;
         int var6 = var2.length;
         int var7;
         double[][][] var8 = new double[var7 = var3.length][var5][var6];
         double[][][] var9 = new double[var6][var7][var5];

         for (int var10 = 0; var10 < var5; var10++) {
            if (var4[var10].length != var6) {
               throw new oS(var4[var10].length, var6);
            }

            for (int var11 = 0; var11 < var6; var11++) {
               if (var4[var10][var11].length != var7) {
                  throw new oS(var4[var10][var11].length, var7);
               }

               for (int var12 = 0; var12 < var7; var12++) {
                  double var13 = var4[var10][var11][var12];
                  var8[var12][var10][var11] = var13;
                  var9[var11][var12][var10] = var13;
               }
            }
         }

         mH var42 = new mH(true);
         mG[] var45 = new mG[var5];

         for (int var47 = 0; var47 < var5; var47++) {
            var45[var47] = var42.b(var2, var3, var4[var47]);
         }

         mG[] var48 = new mG[var6];

         for (int var50 = 0; var50 < var6; var50++) {
            var48[var50] = var42.b(var3, var1, var9[var50]);
         }

         mG[] var51 = new mG[var7];

         for (int var14 = 0; var14 < var7; var14++) {
            var51[var14] = var42.b(var1, var2, var8[var14]);
         }

         double[][][] var52 = new double[var5][var6][var7];
         var8 = new double[var5][var6][var7];
         var9 = new double[var5][var6][var7];

         for (int var43 = 0; var43 < var7; var43++) {
            mG var15 = var51[var43];

            for (int var16 = 0; var16 < var5; var16++) {
               double var20 = var1[var16];

               for (int var22 = 0; var22 < var6; var22++) {
                  double var23 = var2[var22];
                  double[] var10000 = var52[var16][var22];
                  double var33 = var23;
                  double var31 = var20;
                  var10000[var43] = var15.a(0, var31, var33);
                  var10000 = var8[var16][var22];
                  double var38 = var23;
                  double var36 = var20;
                  var10000[var43] = var15.a(1, var36, var38);
                  var10000 = var9[var16][var22];
                  var38 = var23;
                  var36 = var20;
                  var10000[var43] = var15.a(4, var36, var38);
               }
            }
         }

         double[][][] var44 = new double[var5][var6][var7];
         double[][][] var53 = new double[var5][var6][var7];

         for (int var54 = 0; var54 < var5; var54++) {
            mG var56 = var45[var54];

            for (int var21 = 0; var21 < var6; var21++) {
               double var61 = var2[var21];

               for (int var24 = 0; var24 < var7; var24++) {
                  double var25 = var3[var24];
                  double[] var80 = var44[var54][var21];
                  double var75 = var25;
                  double var71 = var61;
                  var80[var24] = var56.a(1, var71, var75);
                  var80 = var53[var54][var21];
                  var75 = var25;
                  var71 = var61;
                  var80[var24] = var56.a(4, var71, var75);
               }
            }
         }

         double[][][] var55 = new double[var5][var6][var7];

         for (int var57 = 0; var57 < var6; var57++) {
            mG var59 = var48[var57];

            for (int var62 = 0; var62 < var7; var62++) {
               double var64 = var3[var62];

               for (int var67 = 0; var67 < var5; var67++) {
                  double var26 = var1[var67];
                  double[] var82 = var55[var67][var57];
                  double var77 = var26;
                  double var73 = var64;
                  var82[var62] = var59.a(4, var73, var77);
               }
            }
         }

         double[][][] var58 = new double[var5][var6][var7];

         for (int var60 = 0; var60 < var5; var60++) {
            int var63 = a(var60, var5);
            int var65 = a(var60);

            for (int var66 = 0; var66 < var6; var66++) {
               int var68 = a(var66, var6);
               int var69 = a(var66);

               for (int var27 = 0; var27 < var7; var27++) {
                  int var46 = a(var27, var7);
                  int var49 = a(var27);
                  var58[var60][var66][var27] = (
                        var4[var63][var68][var46]
                           - var4[var63][var69][var46]
                           - var4[var65][var68][var46]
                           + var4[var65][var69][var46]
                           - var4[var63][var68][var49]
                           + var4[var63][var69][var49]
                           + var4[var65][var68][var49]
                           - var4[var65][var69][var49]
                     )
                     / ((var1[var63] - var1[var65]) * (var2[var68] - var2[var69]) * (var3[var46] - var3[var49]));
               }
            }
         }

         return new nd(var1, var2, var3, var4, var52, var8, var44, var9, var55, var53, var58);
      } else {
         throw new pf();
      }
   }

   private static int a(int var0, int var1) {
      return ++var0 < var1 ? var0 : var0 - 1;
   }

   private static int a(int var0) {
      return --var0 >= 0 ? var0 : 0;
   }

   // $VF: synthetic method
   @Override
   public final lb a(double[] var1, double[] var2, double[] var3, double[][][] var4) {
      double[][][] var5 = var4;
      double[] var48 = var3;
      var3 = var2;
      var2 = var1;
      if (var2.length != 0 && var3.length != 0 && var48.length != 0 && var5.length != 0) {
         if (var2.length != var5.length) {
            throw new oS(var2.length, var5.length);
         }

         Ch.a(var2);
         Ch.a(var3);
         Ch.a(var48);
         int var45 = var2.length;
         int var6 = var3.length;
         int var7;
         double[][][] var8 = new double[var7 = var48.length][var45][var6];
         double[][][] var9 = new double[var6][var7][var45];

         for (int var10 = 0; var10 < var45; var10++) {
            if (var5[var10].length != var6) {
               throw new oS(var5[var10].length, var6);
            }

            for (int var11 = 0; var11 < var6; var11++) {
               if (var5[var10][var11].length != var7) {
                  throw new oS(var5[var10][var11].length, var7);
               }

               for (int var12 = 0; var12 < var7; var12++) {
                  double var18 = var5[var10][var11][var12];
                  var8[var12][var10][var11] = var18;
                  var9[var11][var12][var10] = var18;
               }
            }
         }

         mH var51 = new mH(true);
         mG[] var54 = new mG[var45];

         for (int var56 = 0; var56 < var45; var56++) {
            var54[var56] = var51.b(var3, var48, var5[var56]);
         }

         mG[] var57 = new mG[var6];

         for (int var62 = 0; var62 < var6; var62++) {
            var57[var62] = var51.b(var48, var2, var9[var62]);
         }

         mG[] var63 = new mG[var7];

         for (int var19 = 0; var19 < var7; var19++) {
            var63[var19] = var51.b(var2, var3, var8[var19]);
         }

         double[][][] var64 = new double[var45][var6][var7];
         var8 = new double[var45][var6][var7];
         var9 = new double[var45][var6][var7];

         for (int var52 = 0; var52 < var7; var52++) {
            mG var13 = var63[var52];

            for (int var14 = 0; var14 < var45; var14++) {
               double var25 = var2[var14];

               for (int var27 = 0; var27 < var6; var27++) {
                  double var28 = var3[var27];
                  double[] var10000 = var64[var14][var27];
                  double var38 = var28;
                  double var36 = var25;
                  var10000[var52] = var13.a(0, var36, var38);
                  var10000 = var8[var14][var27];
                  double var43 = var28;
                  double var41 = var25;
                  var10000[var52] = var13.a(1, var41, var43);
                  var10000 = var9[var14][var27];
                  var43 = var28;
                  var41 = var25;
                  var10000[var52] = var13.a(4, var41, var43);
               }
            }
         }

         double[][][] var53 = new double[var45][var6][var7];
         double[][][] var59 = new double[var45][var6][var7];

         for (int var60 = 0; var60 < var45; var60++) {
            mG var65 = var54[var60];

            for (int var26 = 0; var26 < var6; var26++) {
               double var70 = var3[var26];

               for (int var29 = 0; var29 < var7; var29++) {
                  double var30 = var48[var29];
                  double[] var89 = var53[var60][var26];
                  double var84 = var30;
                  double var80 = var70;
                  var89[var29] = var65.a(1, var80, var84);
                  var89 = var59[var60][var26];
                  var84 = var30;
                  var80 = var70;
                  var89[var29] = var65.a(4, var80, var84);
               }
            }
         }

         double[][][] var61 = new double[var45][var6][var7];

         for (int var66 = 0; var66 < var6; var66++) {
            mG var68 = var57[var66];

            for (int var71 = 0; var71 < var7; var71++) {
               double var73 = var48[var71];

               for (int var76 = 0; var76 < var45; var76++) {
                  double var31 = var2[var76];
                  double[] var91 = var61[var76][var66];
                  double var86 = var31;
                  double var82 = var73;
                  var91[var71] = var68.a(4, var82, var86);
               }
            }
         }

         double[][][] var67 = new double[var45][var6][var7];

         for (int var69 = 0; var69 < var45; var69++) {
            int var72 = a(var69, var45);
            int var74 = a(var69);

            for (int var75 = 0; var75 < var6; var75++) {
               int var77 = a(var75, var6);
               int var78 = a(var75);

               for (int var32 = 0; var32 < var7; var32++) {
                  int var55 = a(var32, var7);
                  int var58 = a(var32);
                  var67[var69][var75][var32] = (
                        var5[var72][var77][var55]
                           - var5[var72][var78][var55]
                           - var5[var74][var77][var55]
                           + var5[var74][var78][var55]
                           - var5[var72][var77][var58]
                           + var5[var72][var78][var58]
                           + var5[var74][var77][var58]
                           - var5[var74][var78][var58]
                     )
                     / ((var2[var72] - var2[var74]) * (var3[var77] - var3[var78]) * (var48[var55] - var48[var58]));
               }
            }
         }

         return new nd(var2, var3, var48, var5, var64, var8, var53, var9, var61, var59, var67);
      } else {
         throw new pf();
      }
   }
}
