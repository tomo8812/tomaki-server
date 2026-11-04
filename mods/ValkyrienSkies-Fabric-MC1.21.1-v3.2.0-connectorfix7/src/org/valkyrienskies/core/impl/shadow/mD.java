package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public class mD implements kP {
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
   private final mC[][] e;

   public mD(double[] var1, double[] var2, double[][] var3, double[][] var4, double[][] var5, double[][] var6) {
      int var7 = var1.length;
      int var8 = var2.length;
      if (var7 != 0 && var8 != 0 && var3.length != 0 && var3[0].length != 0) {
         if (var7 != var3.length) {
            throw new oS(var7, var3.length);
         }

         if (var7 != var4.length) {
            throw new oS(var7, var4.length);
         }

         if (var7 != var5.length) {
            throw new oS(var7, var5.length);
         }

         if (var7 != var6.length) {
            throw new oS(var7, var6.length);
         }

         Ch.a(var1);
         Ch.a(var2);
         this.c = (double[])var1.clone();
         this.d = (double[])var2.clone();
         int var21 = var7 - 1;
         int var22 = var8 - 1;
         this.e = new mC[var21][var22];

         for (int var23 = 0; var23 < var21; var23++) {
            if (var3[var23].length != var8) {
               throw new oS(var3[var23].length, var8);
            }

            if (var4[var23].length != var8) {
               throw new oS(var4[var23].length, var8);
            }

            if (var5[var23].length != var8) {
               throw new oS(var5[var23].length, var8);
            }

            if (var6[var23].length != var8) {
               throw new oS(var6[var23].length, var8);
            }

            int var9 = var23 + 1;
            double var13 = this.c[var9] - this.c[var23];

            for (int var10 = 0; var10 < var22; var10++) {
               int var11 = var10 + 1;
               double var17 = this.d[var11] - this.d[var10];
               double var19 = var13 * var17;
               double[] var24 = new double[]{
                  var3[var23][var10],
                  var3[var9][var10],
                  var3[var23][var11],
                  var3[var9][var11],
                  var4[var23][var10] * var13,
                  var4[var9][var10] * var13,
                  var4[var23][var11] * var13,
                  var4[var9][var11] * var13,
                  var5[var23][var10] * var17,
                  var5[var9][var10] * var17,
                  var5[var23][var11] * var17,
                  var5[var9][var11] * var17,
                  var6[var23][var10] * var19,
                  var6[var9][var10] * var19,
                  var6[var23][var11] * var19,
                  var6[var9][var11] * var19
               };
               this.e[var23][var10] = new mC(a(var24));
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

   public boolean b(double var1, double var3) {
      return !(var1 < this.c[0]) && !(var1 > this.c[this.c.length - 1]) && !(var3 < this.d[0]) && !(var3 > this.d[this.d.length - 1]);
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
