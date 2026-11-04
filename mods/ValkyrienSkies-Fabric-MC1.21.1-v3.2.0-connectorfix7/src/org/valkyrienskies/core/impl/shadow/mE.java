package org.valkyrienskies.core.impl.shadow;

public final class mE implements mI {
   private <unrepresentable> b(double[] var1, double[] var2, double[][] var3) {
      if (var1.length != 0 && var2.length != 0 && var3.length != 0) {
         if (var1.length != var3.length) {
            throw new oS(var1.length, var3.length);
         }

         Ch.a(var1);
         Ch.a(var2);
         int var4 = var1.length;
         int var5 = var2.length;
         double[][] var6 = new double[var4][var5];
         double[][] var7 = new double[var4][var5];
         double[][] var8 = new double[var4][var5];

         for (int var9 = 1; var9 < var4 - 1; var9++) {
            int var10 = var9 + 1;
            int var11 = var9 - 1;
            double var12 = var1[var10];
            double var14 = var1[var11];
            double var16 = var12 - var14;

            for (int var29 = 1; var29 < var5 - 1; var29++) {
               int var13 = var29 + 1;
               int var30 = var29 - 1;
               double var21 = var2[var13];
               double var23 = var2[var30];
               double var25 = var21 - var23;
               var6[var9][var29] = (var3[var10][var29] - var3[var11][var29]) / var16;
               var7[var9][var29] = (var3[var9][var13] - var3[var9][var30]) / var25;
               double var27 = var16 * var25;
               var8[var9][var29] = (var3[var10][var13] - var3[var10][var30] - var3[var11][var13] + var3[var11][var30]) / var27;
            }
         }

         return new mD(this, var1, var2, var3, var6, var7, var8, var1, var2) {
            // $VF: synthetic field
            private double[] a;
            // $VF: synthetic field
            private double[] b;
            // $VF: synthetic field
            private mE c;

            {
               this.c = var1;
               this.a = var8x;
               this.b = var9x;
            }

            @Override
            public final boolean b(double var1, double var3x) {
               return !(var1 < this.a[1]) && !(var1 > this.a[this.a.length - 2]) && !(var3x < this.b[1]) && !(var3x > this.b[this.b.length - 2]);
            }
         };
      } else {
         throw new pf();
      }
   }

   // $VF: synthetic method
   @Override
   public final kP a(double[] var1, double[] var2, double[][] var3) {
      double[][] var4 = var3;
      double[] var35 = var2;
      var2 = var1;
      mE var33 = this;
      if (var2.length != 0 && var35.length != 0 && var4.length != 0) {
         if (var2.length != var4.length) {
            throw new oS(var2.length, var4.length);
         }

         Ch.a(var2);
         Ch.a(var35);
         int var5 = var2.length;
         int var6 = var35.length;
         double[][] var7 = new double[var5][var6];
         double[][] var8 = new double[var5][var6];
         double[][] var9 = new double[var5][var6];

         for (int var10 = 1; var10 < var5 - 1; var10++) {
            int var11 = var10 + 1;
            int var12 = var10 - 1;
            double var16 = var2[var11];
            double var18 = var2[var12];
            double var20 = var16 - var18;

            for (int var13 = 1; var13 < var6 - 1; var13++) {
               int var14 = var13 + 1;
               int var15 = var13 - 1;
               double var25 = var35[var14];
               double var27 = var35[var15];
               double var29 = var25 - var27;
               var7[var10][var13] = (var4[var11][var13] - var4[var12][var13]) / var20;
               var8[var10][var13] = (var4[var10][var14] - var4[var10][var15]) / var29;
               double var31 = var20 * var29;
               var9[var10][var13] = (var4[var11][var14] - var4[var11][var15] - var4[var12][var14] + var4[var12][var15]) / var31;
            }
         }

         return new mD(var33, var2, var35, var4, var7, var8, var9, var2, var35) {
            // $VF: synthetic field
            private double[] a;
            // $VF: synthetic field
            private double[] b;
            // $VF: synthetic field
            private mE c;

            {
               this.c = var1;
               this.a = var8x;
               this.b = var9x;
            }

            @Override
            public final boolean b(double var1, double var3x) {
               return !(var1 < this.a[1]) && !(var1 > this.a[this.a.length - 2]) && !(var3x < this.b[1]) && !(var3x > this.b[this.b.length - 2]);
            }
         };
      } else {
         throw new pf();
      }
   }
}
