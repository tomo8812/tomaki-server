package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class pH extends pD {
   private static final lM.a a = new lM.a();
   private final double[] b;
   private final int c;

   private pH(double[] var1, int var2) {
      this.b = var1;
      this.c = var2;
   }

   private static pH a() {
      return new pH(null, Integer.MAX_VALUE);
   }

   private pH a(double[] var1) {
      return new pH((double[])var1.clone(), this.c);
   }

   private pH a(int var1) {
      return new pH(this.b, var1);
   }

   @Override
   protected final pW a(Collection<pM> var1) {
      int var2;
      double[] var3 = new double[var2 = var1.size()];
      double[] var12 = new double[var2];
      int var4 = 0;

      for (pM var6 : var1) {
         var3[var4] = var6.c;
         var12[var4] = var6.a;
         var4++;
      }

      pD.a var13 = new pD.a(a, var1);
      double[] var10000;
      if (this.b != null) {
         var10000 = this.b;
      } else {
         pH.a var8 = new pH.a(var1);
         var10000 = new double[]{var8.a, var8.b, var8.c};
      }

      double[] var14 = var10000;
      pT var17 = new pT();
      int var9 = Integer.MAX_VALUE;
      pT var7 = var17;
      var17.a = Integer.MAX_VALUE;
      int var10 = this.c;
      pT var15 = var7;
      var7.b = var10;
      pT var19 = var15.b(var14).a(var3);
      sM var11 = new sM(var12);
      var7 = var19;
      var19.c = var11;
      return var7.a(var13.a(), var13.b()).a();
   }

   public static class a {
      final double a;
      final double b;
      final double c;

      public a(Collection<pM> var1) {
         if (var1.size() < 4) {
            throw new pn(px.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, var1.size(), 4, true);
         }

         Collection var2 = var1;
         ArrayList var6;
         pM var7 = (pM)(var6 = new ArrayList(var2)).get(0);
         int var8 = var6.size();

         for (int var9 = 1; var9 < var8; var9++) {
            pM var10 = var7;
            if ((var7 = (pM)var6.get(var9)).b < var10.b) {
               int var11 = var9 - 1;
               pM var12 = (pM)var6.get(var11);

               while (var11 >= 0 && var7.b < var12.b) {
                  var6.set(var11 + 1, var12);
                  if (var11-- != 0) {
                     var12 = (pM)var6.get(var11);
                  }
               }

               var6.set(var11 + 1, var7);
               var7 = (pM)var6.get(var9);
            }
         }

         pM[] var43;
         pM[] var45 = var43 = var6.toArray(new pM[0]);
         double[] var58 = new double[2];
         double var60 = 0.0;
         double var62 = 0.0;
         double var64 = 0.0;
         double var13 = 0.0;
         double var15 = 0.0;
         pM var3;
         double var17 = (var3 = var45[0]).b;
         double var19 = (var3 = var45[0]).c;
         double var21 = 0.0;
         double var23 = 0.0;
         double var25 = var17;

         for (int var27 = 1; var27 < var45.length; var27++) {
            double var28 = var17;
            double var30 = var19;
            var17 = (var3 = var45[var27]).b;
            var19 = (var3 = var45[var27]).c;
            double var32 = var17 - var28;
            double var34 = var19 - var30;
            double var36 = var32 * (var30 * var30 + var30 * var19 + var19 * var19) / 3.0;
            double var38 = var34 * var34 / var32;
            double var40 = var17 - var25;
            var21 += var36;
            var23 += var38;
            var60 += var40 * var40;
            var62 += var21 * var21;
            var64 += var40 * var21;
            var13 += var40 * var23;
            var15 += var21 * var23;
         }

         double var72 = var62 * var13 - var64 * var15;
         double var29 = var64 * var13 - var60 * var15;
         double var31 = var60 * var62 - var64 * var64;
         if (!(var72 / var29 < 0.0) && !(var29 / var31 < 0.0)) {
            if (var29 == 0.0) {
               throw new oX(px.ZERO_DENOMINATOR);
            }

            var58[0] = BY.a(var72 / var29);
            var58[1] = BY.a(var29 / var31);
         } else {
            int var33 = var45.length - 1;
            double var73;
            if ((var73 = (var3 = var45[var33]).b - (var3 = var45[0]).b) == 0.0) {
               throw new pr();
            }

            var58[1] = (Math.PI * 2) / var73;
            double var74 = Double.POSITIVE_INFINITY;
            double var75 = Double.NEGATIVE_INFINITY;

            for (int var76 = 1; var76 < var45.length; var76++) {
               double var41;
               if ((var41 = (var3 = var45[var76]).c) < var74) {
                  var74 = var41;
               }

               if (var41 > var75) {
                  var75 = var41;
               }
            }

            var58[0] = 0.5 * (var75 - var74);
         }

         double[] var46 = var58;
         this.a = var46[0];
         this.b = var46[1];
         pM[] var44 = var43;
         pH.a var47 = this;
         double var59 = 0.0;
         double var61 = 0.0;
         double var63 = (var3 = var44[0]).b;
         double var65 = (var3 = var44[0]).c;

         for (int var14 = 1; var14 < var44.length; var14++) {
            var15 = var63;
            var17 = var65;
            var63 = (var3 = var44[var14]).b;
            var19 = ((var65 = (var3 = var44[var14]).c) - var17) / (var63 - var15);
            var23 = BY.o(var21 = var47.b * var63);
            var25 = BY.n(var21);
            var59 += var47.b * var65 * var23 - var19 * var25;
            var61 += var47.b * var65 * var25 + var19 * var23;
         }

         this.c = BY.b(-var61, var59);
      }

      public final double[] a() {
         return new double[]{this.a, this.b, this.c};
      }

      private static List<pM> a(Collection<pM> var0) {
         ArrayList var7;
         pM var1 = (pM)(var7 = new ArrayList(var0)).get(0);
         int var2 = var7.size();

         for (int var3 = 1; var3 < var2; var3++) {
            pM var4 = var1;
            if ((var1 = (pM)var7.get(var3)).b < var4.b) {
               int var8 = var3 - 1;
               pM var5 = (pM)var7.get(var8);

               while (var8 >= 0 && var1.b < var5.b) {
                  var7.set(var8 + 1, var5);
                  if (var8-- != 0) {
                     var5 = (pM)var7.get(var8);
                  }
               }

               var7.set(var8 + 1, var1);
               var1 = (pM)var7.get(var3);
            }
         }

         return var7;
      }

      private static double[] a(pM[] var0) {
         double[] var1 = new double[2];
         double var2 = 0.0;
         double var4 = 0.0;
         double var6 = 0.0;
         double var8 = 0.0;
         double var10 = 0.0;
         pM var14;
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

      private double b(pM[] var1) {
         double var2 = 0.0;
         double var4 = 0.0;
         pM var8;
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
