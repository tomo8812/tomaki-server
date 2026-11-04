package org.valkyrienskies.core.impl.shadow;

public final class nb implements nf {
   private <unrepresentable> b(double[] var1, double[] var2, double[] var3, double[][][] var4) {
      if (var1.length != 0 && var2.length != 0 && var3.length != 0 && var4.length != 0) {
         if (var1.length != var4.length) {
            throw new oS(var1.length, var4.length);
         }

         Ch.a(var1);
         Ch.a(var2);
         Ch.a(var3);
         int var5 = var1.length;
         int var6 = var2.length;
         int var7 = var3.length;
         double[][][] var8 = new double[var5][var6][var7];
         double[][][] var9 = new double[var5][var6][var7];
         double[][][] var10 = new double[var5][var6][var7];
         double[][][] var11 = new double[var5][var6][var7];
         double[][][] var12 = new double[var5][var6][var7];
         double[][][] var13 = new double[var5][var6][var7];
         double[][][] var14 = new double[var5][var6][var7];

         for (int var15 = 1; var15 < var5 - 1; var15++) {
            if (var2.length != var4[var15].length) {
               throw new oS(var2.length, var4[var15].length);
            }

            int var16 = var15 + 1;
            int var17 = var15 - 1;
            double var18 = var1[var16];
            double var20 = var1[var17];
            double var22 = var18 - var20;

            for (int var50 = 1; var50 < var6 - 1; var50++) {
               if (var3.length != var4[var15][var50].length) {
                  throw new oS(var3.length, var4[var15][var50].length);
               }

               int var19 = var50 + 1;
               int var51 = var50 - 1;
               double var27 = var2[var19];
               double var29 = var2[var51];
               double var31 = var27 - var29;
               double var33 = var22 * var31;

               for (int var21 = 1; var21 < var7 - 1; var21++) {
                  int var24 = var21 + 1;
                  int var25 = var21 - 1;
                  double var38 = var3[var24];
                  double var40 = var3[var25];
                  double var42 = var38 - var40;
                  var8[var15][var50][var21] = (var4[var16][var50][var21] - var4[var17][var50][var21]) / var22;
                  var9[var15][var50][var21] = (var4[var15][var19][var21] - var4[var15][var51][var21]) / var31;
                  var10[var15][var50][var21] = (var4[var15][var50][var24] - var4[var15][var50][var25]) / var42;
                  double var44 = var22 * var42;
                  double var46 = var31 * var42;
                  var11[var15][var50][var21] = (var4[var16][var19][var21] - var4[var16][var51][var21] - var4[var17][var19][var21] + var4[var17][var51][var21])
                     / var33;
                  var12[var15][var50][var21] = (var4[var16][var50][var24] - var4[var16][var50][var25] - var4[var17][var50][var24] + var4[var17][var50][var25])
                     / var44;
                  var13[var15][var50][var21] = (var4[var15][var19][var24] - var4[var15][var19][var25] - var4[var15][var51][var24] + var4[var15][var51][var25])
                     / var46;
                  double var48 = var33 * var42;
                  var14[var15][var50][var21] = (
                        var4[var16][var19][var24]
                           - var4[var16][var51][var24]
                           - var4[var17][var19][var24]
                           + var4[var17][var51][var24]
                           - var4[var16][var19][var25]
                           + var4[var16][var51][var25]
                           + var4[var17][var19][var25]
                           - var4[var17][var51][var25]
                     )
                     / var48;
               }
            }
         }

         return new na(this, var1, var2, var3, var4, var8, var9, var10, var11, var12, var13, var14, var1, var2, var3) {
            // $VF: synthetic field
            private double[] a;
            // $VF: synthetic field
            private double[] b;
            // $VF: synthetic field
            private double[] c;
            // $VF: synthetic field
            private nb d;

            {
               this.d = var1;
               this.a = var13x;
               this.b = var14x;
               this.c = var15x;
            }

            @Override
            public final boolean b(double var1, double var3x, double var5x) {
               return !(var1 < this.a[1])
                  && !(var1 > this.a[this.a.length - 2])
                  && !(var3x < this.b[1])
                  && !(var3x > this.b[this.b.length - 2])
                  && !(var5x < this.c[1])
                  && !(var5x > this.c[this.c.length - 2]);
            }
         };
      } else {
         throw new pf();
      }
   }

   // $VF: synthetic method
   @Override
   public final lb a(double[] var1, double[] var2, double[] var3, double[][][] var4) {
      double[][][] var5 = var4;
      double[] var58 = var3;
      var3 = var2;
      var2 = var1;
      nb var55 = this;
      if (var2.length != 0 && var3.length != 0 && var58.length != 0 && var5.length != 0) {
         if (var2.length != var5.length) {
            throw new oS(var2.length, var5.length);
         }

         Ch.a(var2);
         Ch.a(var3);
         Ch.a(var58);
         int var6 = var2.length;
         int var7 = var3.length;
         int var8 = var58.length;
         double[][][] var9 = new double[var6][var7][var8];
         double[][][] var10 = new double[var6][var7][var8];
         double[][][] var11 = new double[var6][var7][var8];
         double[][][] var12 = new double[var6][var7][var8];
         double[][][] var13 = new double[var6][var7][var8];
         double[][][] var14 = new double[var6][var7][var8];
         double[][][] var15 = new double[var6][var7][var8];

         for (int var16 = 1; var16 < var6 - 1; var16++) {
            if (var3.length != var5[var16].length) {
               throw new oS(var3.length, var5[var16].length);
            }

            int var17 = var16 + 1;
            int var18 = var16 - 1;
            double var23 = var2[var17];
            double var25 = var2[var18];
            double var27 = var23 - var25;

            for (int var19 = 1; var19 < var7 - 1; var19++) {
               if (var58.length != var5[var16][var19].length) {
                  throw new oS(var58.length, var5[var16][var19].length);
               }

               int var20 = var19 + 1;
               int var21 = var19 - 1;
               double var32 = var3[var20];
               double var34 = var3[var21];
               double var36 = var32 - var34;
               double var38 = var27 * var36;

               for (int var22 = 1; var22 < var8 - 1; var22++) {
                  int var59 = var22 + 1;
                  int var24 = var22 - 1;
                  double var43 = var58[var59];
                  double var45 = var58[var24];
                  double var47 = var43 - var45;
                  var9[var16][var19][var22] = (var5[var17][var19][var22] - var5[var18][var19][var22]) / var27;
                  var10[var16][var19][var22] = (var5[var16][var20][var22] - var5[var16][var21][var22]) / var36;
                  var11[var16][var19][var22] = (var5[var16][var19][var59] - var5[var16][var19][var24]) / var47;
                  double var49 = var27 * var47;
                  double var51 = var36 * var47;
                  var12[var16][var19][var22] = (var5[var17][var20][var22] - var5[var17][var21][var22] - var5[var18][var20][var22] + var5[var18][var21][var22])
                     / var38;
                  var13[var16][var19][var22] = (var5[var17][var19][var59] - var5[var17][var19][var24] - var5[var18][var19][var59] + var5[var18][var19][var24])
                     / var49;
                  var14[var16][var19][var22] = (var5[var16][var20][var59] - var5[var16][var20][var24] - var5[var16][var21][var59] + var5[var16][var21][var24])
                     / var51;
                  double var53 = var38 * var47;
                  var15[var16][var19][var22] = (
                        var5[var17][var20][var59]
                           - var5[var17][var21][var59]
                           - var5[var18][var20][var59]
                           + var5[var18][var21][var59]
                           - var5[var17][var20][var24]
                           + var5[var17][var21][var24]
                           + var5[var18][var20][var24]
                           - var5[var18][var21][var24]
                     )
                     / var53;
               }
            }
         }

         return new na(var55, var2, var3, var58, var5, var9, var10, var11, var12, var13, var14, var15, var2, var3, var58) {
            // $VF: synthetic field
            private double[] a;
            // $VF: synthetic field
            private double[] b;
            // $VF: synthetic field
            private double[] c;
            // $VF: synthetic field
            private nb d;

            {
               this.d = var1;
               this.a = var13x;
               this.b = var14x;
               this.c = var15x;
            }

            @Override
            public final boolean b(double var1, double var3x, double var5x) {
               return !(var1 < this.a[1])
                  && !(var1 > this.a[this.a.length - 2])
                  && !(var3x < this.b[1])
                  && !(var3x > this.b[this.b.length - 2])
                  && !(var5x < this.c[1])
                  && !(var5x > this.c[this.c.length - 2]);
            }
         };
      } else {
         throw new pf();
      }
   }
}
