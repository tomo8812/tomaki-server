package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class mP implements Serializable, ng {
   private static double a;
   private static int b;
   private static double c;
   private static final long d = 5204927143605193821L;
   private final double e;
   private final int f;
   private final double g;

   public mP() {
      this.e = 0.3;
      this.f = 2;
      this.g = 1.0E-12;
   }

   private mP(double var1, int var3) {
      this(var1, var3, 1.0E-12);
   }

   private mP(double var1, int var3, double var4) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         this.e = var1;
         if (var3 < 0) {
            throw new pj(px.ROBUSTNESS_ITERATIONS, var3);
         }

         this.f = var3;
         this.g = 1.0E-12;
      } else {
         throw new po(px.BANDWIDTH, var1, 0, 1);
      }
   }

   private nl a(double[] var1, double[] var2) {
      new mY();
      double[] var3 = var2;
      var2 = var1;
      mP var55 = this;
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      double[] var4;
      Arrays.fill(var4 = new double[var2.length], 1.0);
      var4 = var4;
      var3 = var3;
      var2 = var2;
      mP var56 = var55;
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      int var5;
      if ((var5 = var2.length) == 0) {
         throw new pf();
      }

      a(var2);
      a(var3);
      a(var4);
      Ch.a(var2);
      double[] var10001;
      if (var5 == 1) {
         var10001 = new double[]{var3[0]};
      } else if (var5 == 2) {
         var10001 = new double[]{var3[0], var3[1]};
      } else {
         int var6;
         if ((var6 = (int)(var56.e * var5)) < 2) {
            throw new pn(px.BANDWIDTH, var6, 2, true);
         }

         double[] var7 = new double[var5];
         double[] var8 = new double[var5];
         double[] var9 = new double[var5];
         double[] var10;
         Arrays.fill(var10 = new double[var5], 1.0);

         for (int var11 = 0; var11 <= var56.f; var11++) {
            int[] var12 = new int[]{0, var6 - 1};

            for (int var19 = 0; var19 < var5; var19++) {
               double var20 = var2[var19];
               if (var19 > 0) {
                  int[] var52 = var12;
                  int var51 = var19;
                  double[] var50 = var4;
                  double[] var49 = var2;
                  int var53 = var52[0];
                  int var54 = var52[1];
                  int var13;
                  if ((var13 = a(var50, var54)) < var49.length && var49[var13] - var49[var51] < var49[var51] - var49[var53]) {
                     int var14 = a(var50, var52[0]);
                     var52[0] = var14;
                     var52[1] = var13;
                  }
               }

               int var22 = var12[0];
               int var23 = var12[1];
               int var24;
               if (var2[var19] - var2[var22] > var2[var23] - var2[var19]) {
                  var24 = var22;
               } else {
                  var24 = var23;
               }

               double var25 = 0.0;
               double var27 = 0.0;
               double var29 = 0.0;
               double var31 = 0.0;
               double var33 = 0.0;
               double var35 = BY.w(1.0 / (var2[var24] - var20));

               for (int var37 = var22; var37 <= var23; var37++) {
                  double var38 = var2[var37];
                  double var40 = var3[var37];
                  double var65;
                  double var66;
                  double var44 = (
                        (var65 = BY.w((var37 < var19 ? var20 - var38 : var38 - var20) * var35)) >= 1.0
                           ? 0.0
                           : (var66 = 1.0 - var65 * var65 * var65) * var66 * var66
                     )
                     * var10[var37]
                     * var4[var37];
                  double var46 = var38 * var44;
                  var25 += var44;
                  var27 += var46;
                  var29 += var38 * var46;
                  var31 += var40 * var44;
                  var33 += var40 * var46;
               }

               double var64 = var27 / var25;
               double var39 = var31 / var25;
               double var41 = var33 / var25;
               double var43;
               double var45;
               if (BY.a(BY.w((var43 = var29 / var25) - var64 * var64)) < var56.g) {
                  var45 = 0.0;
               } else {
                  var45 = (var41 - var64 * var39) / (var43 - var64 * var64);
               }

               double var47 = var39 - var45 * var64;
               var7[var19] = var45 * var20 + var47;
               var8[var19] = BY.w(var3[var19] - var7[var19]);
            }

            if (var11 == var56.f) {
               break;
            }

            System.arraycopy(var8, 0, var9, 0, var5);
            Arrays.sort(var9);
            double var61;
            if (BY.w(var61 = var9[var5 / 2]) < var56.g) {
               break;
            }

            for (int var21 = 0; var21 < var5; var21++) {
               double var62;
               if ((var62 = var8[var21] / (var61 * 6.0)) >= 1.0) {
                  var10[var21] = 0.0;
               } else {
                  double var63 = 1.0 - var62 * var62;
                  var10[var21] = var63 * var63;
               }
            }
         }

         var10001 = var7;
      }

      return mY.a(var1, var10001);
   }

   private double[] a(double[] var1, double[] var2, double[] var3) {
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      int var4;
      if ((var4 = var1.length) == 0) {
         throw new pf();
      }

      a(var1);
      a(var2);
      a(var3);
      Ch.a(var1);
      if (var4 == 1) {
         return new double[]{var2[0]};
      }

      if (var4 == 2) {
         return new double[]{var2[0], var2[1]};
      }

      int var5;
      if ((var5 = (int)(this.e * var4)) < 2) {
         throw new pn(px.BANDWIDTH, var5, 2, true);
      }

      double[] var6 = new double[var4];
      double[] var7 = new double[var4];
      double[] var8 = new double[var4];
      double[] var9;
      Arrays.fill(var9 = new double[var4], 1.0);

      for (int var10 = 0; var10 <= this.f; var10++) {
         int[] var11 = new int[]{0, var5 - 1};

         for (int var12 = 0; var12 < var4; var12++) {
            double var13 = var1[var12];
            if (var12 > 0) {
               a(var1, var3, var12, var11);
            }

            int var15 = var11[0];
            int var16 = var11[1];
            int var17;
            if (var1[var12] - var1[var15] > var1[var16] - var1[var12]) {
               var17 = var15;
            } else {
               var17 = var16;
            }

            double var18 = 0.0;
            double var20 = 0.0;
            double var22 = 0.0;
            double var24 = 0.0;
            double var26 = 0.0;
            double var28 = BY.w(1.0 / (var1[var17] - var13));

            for (int var30 = var15; var30 <= var16; var30++) {
               double var31 = var1[var30];
               double var33 = var2[var30];
               double var37 = a((var30 < var12 ? var13 - var31 : var31 - var13) * var28) * var9[var30] * var3[var30];
               double var39 = var31 * var37;
               var18 += var37;
               var20 += var39;
               var22 += var31 * var39;
               var24 += var33 * var37;
               var26 += var33 * var39;
            }

            double var45 = var20 / var18;
            double var32 = var24 / var18;
            double var34 = var26 / var18;
            double var36;
            double var38;
            if (BY.a(BY.w((var36 = var22 / var18) - var45 * var45)) < this.g) {
               var38 = 0.0;
            } else {
               var38 = (var34 - var45 * var32) / (var36 - var45 * var45);
            }

            double var40 = var32 - var38 * var45;
            var6[var12] = var38 * var13 + var40;
            var7[var12] = BY.w(var2[var12] - var6[var12]);
         }

         if (var10 == this.f) {
            break;
         }

         System.arraycopy(var7, 0, var8, 0, var4);
         Arrays.sort(var8);
         double var42;
         if (BY.w(var42 = var8[var4 / 2]) < this.g) {
            break;
         }

         for (int var14 = 0; var14 < var4; var14++) {
            double var43;
            if ((var43 = var7[var14] / (var42 * 6.0)) >= 1.0) {
               var9[var14] = 0.0;
            } else {
               double var44 = 1.0 - var43 * var43;
               var9[var14] = var44 * var44;
            }
         }
      }

      return var6;
   }

   private double[] c(double[] var1, double[] var2) {
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      double[] var3;
      Arrays.fill(var3 = new double[var1.length], 1.0);
      return this.a(var1, var2, var3);
   }

   private static void a(double[] var0, double[] var1, int var2, int[] var3) {
      int var4 = var3[0];
      int var5 = var3[1];
      if ((var5 = a(var1, var5)) < var0.length && var0[var5] - var0[var2] < var0[var2] - var0[var4]) {
         int var6 = a(var1, var3[0]);
         var3[0] = var6;
         var3[1] = var5;
      }
   }

   private static int a(double[] var0, int var1) {
      var1++;

      while (var1 < var0.length && var0[var1] == 0.0) {
         var1++;
      }

      return var1;
   }

   private static double a(double var0) {
      double var2;
      double var4;
      return (var2 = BY.w(var0)) >= 1.0 ? 0.0 : (var4 = 1.0 - var2 * var2 * var2) * var4 * var4;
   }

   private static void a(double[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         Ci.b(var0[var1]);
      }
   }

   // $VF: synthetic method
   @Override
   public final lc b(double[] var1, double[] var2) {
      double[] var3 = var2;
      var2 = var1;
      mP var58 = this;
      new mY();
      var3 = var3;
      var2 = var2;
      mP var59 = var58;
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      double[] var4;
      Arrays.fill(var4 = new double[var2.length], 1.0);
      var4 = var4;
      var3 = var3;
      var2 = var2;
      mP var60 = var59;
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      int var5;
      if ((var5 = var2.length) == 0) {
         throw new pf();
      }

      a(var2);
      a(var3);
      a(var4);
      Ch.a(var2);
      double[] var10001;
      if (var5 == 1) {
         var10001 = new double[]{var3[0]};
      } else if (var5 == 2) {
         var10001 = new double[]{var3[0], var3[1]};
      } else {
         int var6;
         if ((var6 = (int)(var60.e * var5)) < 2) {
            throw new pn(px.BANDWIDTH, var6, 2, true);
         }

         double[] var7 = new double[var5];
         double[] var8 = new double[var5];
         double[] var9 = new double[var5];
         double[] var10;
         Arrays.fill(var10 = new double[var5], 1.0);

         for (int var11 = 0; var11 <= var60.f; var11++) {
            int[] var12 = new int[]{0, var6 - 1};

            for (int var22 = 0; var22 < var5; var22++) {
               double var23 = var2[var22];
               if (var22 > 0) {
                  int[] var55 = var12;
                  int var54 = var22;
                  double[] var53 = var4;
                  double[] var52 = var2;
                  int var56 = var55[0];
                  int var57 = var55[1];
                  int var13;
                  if ((var13 = a(var53, var57)) < var52.length && var52[var13] - var52[var54] < var52[var54] - var52[var56]) {
                     int var14 = a(var53, var55[0]);
                     var55[0] = var14;
                     var55[1] = var13;
                  }
               }

               int var25 = var12[0];
               int var26 = var12[1];
               int var27;
               if (var2[var22] - var2[var25] > var2[var26] - var2[var22]) {
                  var27 = var25;
               } else {
                  var27 = var26;
               }

               double var28 = 0.0;
               double var30 = 0.0;
               double var32 = 0.0;
               double var34 = 0.0;
               double var36 = 0.0;
               double var38 = BY.w(1.0 / (var2[var27] - var23));

               for (int var40 = var25; var40 <= var26; var40++) {
                  double var41 = var2[var40];
                  double var43 = var3[var40];
                  double var71;
                  double var72;
                  double var47 = (
                        (var71 = BY.w((var40 < var22 ? var23 - var41 : var41 - var23) * var38)) >= 1.0
                           ? 0.0
                           : (var72 = 1.0 - var71 * var71 * var71) * var72 * var72
                     )
                     * var10[var40]
                     * var4[var40];
                  double var49 = var41 * var47;
                  var28 += var47;
                  var30 += var49;
                  var32 += var41 * var49;
                  var34 += var43 * var47;
                  var36 += var43 * var49;
               }

               double var70 = var30 / var28;
               double var42 = var34 / var28;
               double var44 = var36 / var28;
               double var46;
               double var48;
               if (BY.a(BY.w((var46 = var32 / var28) - var70 * var70)) < var60.g) {
                  var48 = 0.0;
               } else {
                  var48 = (var44 - var70 * var42) / (var46 - var70 * var70);
               }

               double var50 = var42 - var48 * var70;
               var7[var22] = var48 * var23 + var50;
               var8[var22] = BY.w(var3[var22] - var7[var22]);
            }

            if (var11 == var60.f) {
               break;
            }

            System.arraycopy(var8, 0, var9, 0, var5);
            Arrays.sort(var9);
            double var67;
            if (BY.w(var67 = var9[var5 / 2]) < var60.g) {
               break;
            }

            for (int var24 = 0; var24 < var5; var24++) {
               double var68;
               if ((var68 = var8[var24] / (var67 * 6.0)) >= 1.0) {
                  var10[var24] = 0.0;
               } else {
                  double var69 = 1.0 - var68 * var68;
                  var10[var24] = var69 * var69;
               }
            }
         }

         var10001 = var7;
      }

      return mY.a(var2, var10001);
   }
}
