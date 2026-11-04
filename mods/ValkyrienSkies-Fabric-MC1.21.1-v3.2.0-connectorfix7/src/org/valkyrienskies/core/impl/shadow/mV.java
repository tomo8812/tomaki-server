package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class mV implements kP {
   private static final int a = 5;
   private final double[] b;
   private final double[] c;
   private final double[][] d;

   public mV(double[] var1, double[] var2, double[][] var3) {
      if (var1 != null && var2 != null && var3 != null && var3[0] != null) {
         int var4 = var1.length;
         int var5 = var2.length;
         if (var4 != 0 && var5 != 0 && var3.length != 0 && var3[0].length != 0) {
            if (var4 < 5 || var5 < 5 || var3.length < 5 || var3[0].length < 5) {
               throw new oT();
            }

            if (var4 != var3.length) {
               throw new oS(var4, var3.length);
            }

            if (var5 != var3[0].length) {
               throw new oS(var5, var3[0].length);
            }

            Ch.a(var1);
            Ch.a(var2);
            this.b = (double[])var1.clone();
            this.c = (double[])var2.clone();
            this.d = (double[][])var3.clone();
         } else {
            throw new pf();
         }
      } else {
         throw new pl();
      }
   }

   @Override
   public final double a(double var1, double var3) {
      mB var5 = new mB();
      int var6 = a(var1, this.b, 2, 5);
      int var7 = a(var3, this.c, 2, 5);
      double[] var8 = new double[5];
      double[] var9 = new double[5];
      double[] var10 = new double[5];
      double[] var11 = new double[5];

      for (int var12 = 0; var12 < 5; var12++) {
         var8[var12] = this.b[var6 + var12];
         var9[var12] = this.c[var7 + var12];
      }

      for (int var15 = 0; var15 < 5; var15++) {
         for (int var13 = 0; var13 < 5; var13++) {
            var10[var13] = this.d[var6 + var13][var7 + var15];
         }

         nl var16 = var5.a(var8, var10);
         var11[var15] = var16.a(var1);
      }

      return var5.a(var9, var11).a(var3);
   }

   private boolean b(double var1, double var3) {
      return !(var1 < this.b[0]) && !(var1 > this.b[this.b.length - 1]) && !(var3 < this.c[0]) && !(var3 > this.c[this.c.length - 1]);
   }

   private static int a(double var0, double[] var2, int var3, int var4) {
      if ((var3 = Arrays.binarySearch(var2, var0)) != -1 && var3 != -var2.length - 1) {
         if (var3 < 0) {
            var3 = -var3 - 2 - 1;
         } else {
            var3 -= 2;
         }

         if (var3 < 0) {
            var3 = 0;
         }

         if (var3 + 5 >= var2.length) {
            var3 = var2.length - 5;
         }

         return var3;
      } else {
         throw new po(var0, var2[0], var2[var2.length - 1]);
      }
   }
}
