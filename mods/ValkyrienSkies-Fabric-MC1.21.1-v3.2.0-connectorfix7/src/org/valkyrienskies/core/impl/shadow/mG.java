package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

@Deprecated
public final class mG implements kP {
   private static final int a = 16;
   private static final double[][] b = new double[][]{
      {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
      {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
      {-3.0, 3.0, 0.0, 0.0, -2.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
      {2.0, -2.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
      {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
      {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0},
      {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -3.0, 3.0, 0.0, 0.0, -2.0, -1.0, 0.0, 0.0},
      {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, -2.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0},
      {-3.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, -2.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0},
      {0.0, 0.0, 0.0, 0.0, -3.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, -2.0, 0.0, -1.0, 0.0},
      {9.0, -9.0, -9.0, 9.0, 6.0, 3.0, -6.0, -3.0, 6.0, -6.0, 3.0, -3.0, 4.0, 2.0, 2.0, 1.0},
      {-6.0, 6.0, 6.0, -6.0, -3.0, -3.0, 3.0, 3.0, -4.0, 4.0, -2.0, 2.0, -2.0, -2.0, -1.0, -1.0},
      {2.0, 0.0, -2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0},
      {0.0, 0.0, 0.0, 0.0, 2.0, 0.0, -2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0},
      {-6.0, 6.0, 6.0, -6.0, -4.0, -2.0, 4.0, 2.0, -3.0, 3.0, -3.0, 3.0, -2.0, -1.0, -2.0, -1.0},
      {4.0, -4.0, -4.0, 4.0, 2.0, 2.0, -2.0, -2.0, 2.0, -2.0, 2.0, -2.0, 1.0, 1.0, 1.0, 1.0}
   };
   private final double[] c;
   private final double[] d;
   private final mF[][] e;
   private final kP[][][] f;

   private mG(double[] var1, double[] var2, double[][] var3, double[][] var4, double[][] var5, double[][] var6) {
      this(var1, var2, var3, var4, var5, var6, false);
   }

   public mG(double[] var1, double[] var2, double[][] var3, double[][] var4, double[][] var5, double[][] var6, boolean var7) {
      int var8 = var1.length;
      int var9 = var2.length;
      if (var8 != 0 && var9 != 0 && var3.length != 0 && var3[0].length != 0) {
         if (var8 != var3.length) {
            throw new oS(var8, var3.length);
         }

         if (var8 != var4.length) {
            throw new oS(var8, var4.length);
         }

         if (var8 != var5.length) {
            throw new oS(var8, var5.length);
         }

         if (var8 != var6.length) {
            throw new oS(var8, var6.length);
         }

         Ch.a(var1);
         Ch.a(var2);
         this.c = (double[])var1.clone();
         this.d = (double[])var2.clone();
         int var13 = var8 - 1;
         int var14 = var9 - 1;
         this.e = new mF[var13][var14];

         for (int var15 = 0; var15 < var13; var15++) {
            if (var3[var15].length != var9) {
               throw new oS(var3[var15].length, var9);
            }

            if (var4[var15].length != var9) {
               throw new oS(var4[var15].length, var9);
            }

            if (var5[var15].length != var9) {
               throw new oS(var5[var15].length, var9);
            }

            if (var6[var15].length != var9) {
               throw new oS(var6[var15].length, var9);
            }

            int var10 = var15 + 1;

            for (int var11 = 0; var11 < var14; var11++) {
               int var12 = var11 + 1;
               double[] var19 = new double[]{
                  var3[var15][var11],
                  var3[var10][var11],
                  var3[var15][var12],
                  var3[var10][var12],
                  var4[var15][var11],
                  var4[var10][var11],
                  var4[var15][var12],
                  var4[var10][var12],
                  var5[var15][var11],
                  var5[var10][var11],
                  var5[var15][var12],
                  var5[var10][var12],
                  var6[var15][var11],
                  var6[var10][var11],
                  var6[var15][var12],
                  var6[var10][var12]
               };
               this.e[var15][var11] = new mF(a(var19), var7);
            }
         }

         if (!var7) {
            this.f = null;
         } else {
            this.f = new kP[5][var13][var14];

            for (int var16 = 0; var16 < var13; var16++) {
               for (int var17 = 0; var17 < var14; var17++) {
                  mF var18 = this.e[var16][var17];
                  this.f[0][var16][var17] = var18.a;
                  this.f[1][var16][var17] = var18.b;
                  this.f[2][var16][var17] = var18.c;
                  this.f[3][var16][var17] = var18.d;
                  this.f[4][var16][var17] = var18.e;
               }
            }
         }
      } else {
         throw new pf();
      }
   }

   @Override
   public final double a(double var1, double var3) {
      int var5 = a(var1, this.c);
      int var6 = a(var3, this.d);
      double var7 = (var1 - this.c[var5]) / (this.c[var5 + 1] - this.c[var5]);
      double var9 = (var3 - this.d[var6]) / (this.d[var6 + 1] - this.d[var6]);
      return this.e[var5][var6].a(var7, var9);
   }

   private boolean e(double var1, double var3) {
      return !(var1 < this.c[0]) && !(var1 > this.c[this.c.length - 1]) && !(var3 < this.d[0]) && !(var3 > this.d[this.d.length - 1]);
   }

   public final double b(double var1, double var3) {
      return this.a(0, var1, var3);
   }

   public final double c(double var1, double var3) {
      return this.a(1, var1, var3);
   }

   private double f(double var1, double var3) {
      return this.a(2, var1, var3);
   }

   private double g(double var1, double var3) {
      return this.a(3, var1, var3);
   }

   public final double d(double var1, double var3) {
      return this.a(4, var1, var3);
   }

   double a(int var1, double var2, double var4) {
      int var6 = a(var2, this.c);
      int var7 = a(var4, this.d);
      double var8 = (var2 - this.c[var6]) / (this.c[var6 + 1] - this.c[var6]);
      double var10 = (var4 - this.d[var7]) / (this.d[var7 + 1] - this.d[var7]);
      return this.f[var1][var6][var7].a(var8, var10);
   }

   private static int a(double var0, double[] var2) {
      int var3;
      if ((var3 = Arrays.binarySearch(var2, var0)) == -1 || var3 == -var2.length - 1) {
         throw new po(var0, var2[0], var2[var2.length - 1]);
      }

      if (var3 < 0) {
         return -var3 - 2;
      }

      int var4 = var2.length - 1;
      return var3 == var4 ? var4 - 1 : var3;
   }

   private static double[] a(double[] var0) {
      double[] var1 = new double[16];

      for (int var2 = 0; var2 < 16; var2++) {
         double var3 = 0.0;
         double[] var5 = b[var2];

         for (int var6 = 0; var6 < 16; var6++) {
            var3 += var5[var6] * var0[var6];
         }

         var1[var2] = var3;
      }

      return var1;
   }
}
