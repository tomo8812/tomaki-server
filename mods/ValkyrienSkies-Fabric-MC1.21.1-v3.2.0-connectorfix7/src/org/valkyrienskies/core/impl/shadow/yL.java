package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class yL extends yJ<lM.a> {
   private yL(ye var1) {
      super(var1);
   }

   private double[] a(double[] var1) {
      return this.a(new lM.a(), var1);
   }

   private double[] b() {
      yL.a var1 = new yL.a(this.a());
      double[] var2 = new double[]{var1.a, var1.b, var1.c};
      return this.a(new lM.a(), var2);
   }

   public static class a {
      final double a;
      final double b;
      final double c;

      public a(yN[] var1) {
         if (var1.length < 4) {
            throw new pn(px.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, var1.length, 4, true);
         }

         yN[] var6;
         yN var7 = (var6 = (yN[])var1.clone())[0];

         for (int var8 = 1; var8 < var6.length; var8++) {
            yN var9 = var7;
            if ((var7 = var6[var8]).b < var9.b) {
               int var10 = var8 - 1;
               yN var11 = var6[var10];

               while (var10 >= 0 && var7.b < var11.b) {
                  var6[var10 + 1] = var11;
                  if (var10-- != 0) {
                     var11 = var6[var10];
                  }
               }

               var6[var10 + 1] = var7;
               var7 = var6[var8];
            }
         }

         var1 = var6;
         yN[] var2 = var6;
         double[] var57 = new double[2];
         double var59 = 0.0;
         double var61 = 0.0;
         double var63 = 0.0;
         double var13 = 0.0;
         double var15 = 0.0;
         yN var3;
         double var17 = (var3 = var2[0]).b;
         double var19 = (var3 = var2[0]).c;
         double var21 = 0.0;
         double var23 = 0.0;
         double var25 = var17;

         for (int var27 = 1; var27 < var2.length; var27++) {
            double var28 = var17;
            double var30 = var19;
            var17 = (var3 = var2[var27]).b;
            var19 = (var3 = var2[var27]).c;
            double var32 = var17 - var28;
            double var34 = var19 - var30;
            double var36 = var32 * (var30 * var30 + var30 * var19 + var19 * var19) / 3.0;
            double var38 = var34 * var34 / var32;
            double var40 = var17 - var25;
            var21 += var36;
            var23 += var38;
            var59 += var40 * var40;
            var61 += var21 * var21;
            var63 += var40 * var21;
            var13 += var40 * var23;
            var15 += var21 * var23;
         }

         double var70 = var61 * var13 - var63 * var15;
         double var29 = var63 * var13 - var59 * var15;
         double var31 = var59 * var61 - var63 * var63;
         if (!(var70 / var29 < 0.0) && !(var29 / var31 < 0.0)) {
            if (var29 == 0.0) {
               throw new oX(px.ZERO_DENOMINATOR);
            }

            var57[0] = BY.a(var70 / var29);
            var57[1] = BY.a(var29 / var31);
         } else {
            int var33 = var2.length - 1;
            double var71;
            if ((var71 = (var3 = var2[var33]).b - (var3 = var2[0]).b) == 0.0) {
               throw new pr();
            }

            var57[1] = (Math.PI * 2) / var71;
            double var72 = Double.POSITIVE_INFINITY;
            double var73 = Double.NEGATIVE_INFINITY;

            for (int var74 = 1; var74 < var2.length; var74++) {
               double var41;
               if ((var41 = (var3 = var2[var74]).c) < var72) {
                  var72 = var41;
               }

               if (var41 > var73) {
                  var73 = var41;
               }
            }

            var57[0] = 0.5 * (var73 - var72);
         }

         double[] var45 = var57;
         this.a = var45[0];
         this.b = var45[1];
         var1 = var1;
         yL.a var46 = this;
         double var58 = 0.0;
         double var60 = 0.0;
         double var62 = (var3 = var1[0]).b;
         double var12 = (var3 = var1[0]).c;

         for (int var14 = 1; var14 < var1.length; var14++) {
            var15 = var62;
            var17 = var12;
            var62 = (var3 = var1[var14]).b;
            var19 = ((var12 = (var3 = var1[var14]).c) - var17) / (var62 - var15);
            var23 = BY.o(var21 = var46.b * var62);
            var25 = BY.n(var21);
            var58 += var46.b * var12 * var23 - var19 * var25;
            var60 += var46.b * var12 * var25 + var19 * var23;
         }

         this.c = BY.b(-var60, var58);
      }

      public final double[] a() {
         return new double[]{this.a, this.b, this.c};
      }

      private static yN[] a(yN[] var0) {
         yN[] var6;
         yN var1 = (var6 = (yN[])var0.clone())[0];

         for (int var2 = 1; var2 < var6.length; var2++) {
            yN var3 = var1;
            if ((var1 = var6[var2]).b < var3.b) {
               int var7 = var2 - 1;
               yN var4 = var6[var7];

               while (var7 >= 0 && var1.b < var4.b) {
                  var6[var7 + 1] = var4;
                  if (var7-- != 0) {
                     var4 = var6[var7];
                  }
               }

               var6[var7 + 1] = var1;
               var1 = var6[var2];
            }
         }

         return var6;
      }

      private static double[] b(yN[] var0) {
         double[] var1 = new double[2];
         double var2 = 0.0;
         double var4 = 0.0;
         double var6 = 0.0;
         double var8 = 0.0;
         double var10 = 0.0;
         yN var14;
         double var12 = (var14 = var0[0]).b;
         double var39 = (var14 = var0[0]).c;
         double var16 = 0.0;
         double var18 = 0.0;
         double var20 = var12;

         for (int var22 = 1; var22 < var0.length; var22++) {
            double var23 = var12;
            double var25 = var39;
            var12 = (var14 = var0[var22]).b;
            var39 = (var14 = var0[var22]).c;
            double var27 = var12 - var23;
            double var29 = var39 - var25;
            double var31 = var27 * (var25 * var25 + var25 * var39 + var39 * var39) / 3.0;
            double var33 = var29 * var29 / var27;
            double var35 = var12 - var20;
            var16 += var31;
            var18 += var33;
            var2 += var35 * var35;
            var4 += var16 * var16;
            var6 += var35 * var16;
            var8 += var35 * var18;
            var10 += var16 * var18;
         }

         double var45 = var4 * var8 - var6 * var10;
         double var24 = var6 * var8 - var2 * var10;
         double var26 = var2 * var4 - var6 * var6;
         if (!(var45 / var24 < 0.0) && !(var24 / var26 < 0.0)) {
            if (var24 == 0.0) {
               throw new oX(px.ZERO_DENOMINATOR);
            }

            var1[0] = BY.a(var45 / var24);
            var1[1] = BY.a(var24 / var26);
         } else {
            int var28 = var0.length - 1;
            double var46;
            if ((var46 = (var14 = var0[var28]).b - (var14 = var0[0]).b) == 0.0) {
               throw new pr();
            }

            var1[1] = (Math.PI * 2) / var46;
            double var47 = Double.POSITIVE_INFINITY;
            double var48 = Double.NEGATIVE_INFINITY;

            for (int var49 = 1; var49 < var0.length; var49++) {
               double var36;
               if ((var36 = (var14 = var0[var49]).c) < var47) {
                  var47 = var36;
               }

               if (var36 > var48) {
                  var48 = var36;
               }
            }

            var1[0] = 0.5 * (var48 - var47);
         }

         return var1;
      }

      private double c(yN[] var1) {
         double var2 = 0.0;
         double var4 = 0.0;
         yN var8;
         double var6 = (var8 = var1[0]).b;
         double var24 = (var8 = var1[0]).c;

         for (int var10 = 1; var10 < var1.length; var10++) {
            double var11 = var6;
            double var13 = var24;
            var6 = (var8 = var1[var10]).b;
            double var15 = ((var24 = (var8 = var1[var10]).c) - var13) / (var6 - var11);
            double var17;
            double var19 = BY.o(var17 = this.b * var6);
            double var21 = BY.n(var17);
            var2 += this.b * var24 * var19 - var15 * var21;
            var4 += this.b * var24 * var21 + var15 * var19;
         }

         return BY.b(-var4, var2);
      }
   }
}
