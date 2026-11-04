package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class oQ {
   private static final int a = 1000;
   private static final double b = 1.0E-5;
   private final double[][] c;
   private oz d;
   private double e = 0.0;

   private oQ(double[][] var1) {
      if (var1.length <= 0) {
         throw new pk(var1.length);
      }

      this.c = new double[var1.length][var1[0].length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         if (var1[var2].length != var1[0].length) {
            throw new oS(var1[var2].length, var1[0].length);
         }

         if (var1[var2].length < 2) {
            throw new pn(px.NUMBER_TOO_SMALL, var1[var2].length, 2, true);
         }

         this.c[var2] = Ch.a(var1[var2], var1[var2].length);
      }
   }

   private void a(oz var1, int var2, double var3) {
      var2 = this.c.length;
      int var26 = this.c[0].length;
      int var4 = var1.c().size();
      int var5;
      if ((var5 = ((oB)((Co)var1.c().get(0)).b).c().length) != var26) {
         throw new oS(var5, var26);
      }

      var5 = 0;
      double var10 = 0.0;
      this.e = Double.NEGATIVE_INFINITY;
      this.d = new oz(var1.c());

      while (var5++ <= 1000 && BY.w(var10 - this.e) > 1.0E-5) {
         var10 = this.e;
         double var12 = 0.0;
         List var23 = this.d.c();
         double[] var6 = new double[var4];
         oB[] var7 = new oB[var4];

         for (int var8 = 0; var8 < var4; var8++) {
            var6[var8] = (Double)((Co)var23.get(var8)).a;
            var7[var8] = (oB)((Co)var23.get(var8)).b;
         }

         double[][] var30 = new double[var2][var4];
         double[] var24 = new double[var4];
         double[][] var9 = new double[var4][var26];

         for (int var14 = 0; var14 < var2; var14++) {
            double var21 = this.d.a(this.c[var14]);
            var12 += BY.k(var21);

            for (int var15 = 0; var15 < var4; var15++) {
               var30[var14][var15] = var6[var15] * var7[var15].a(this.c[var14]) / var21;
               var24[var15] += var30[var14][var15];

               for (int var16 = 0; var16 < var26; var16++) {
                  var9[var15][var16] = var9[var15][var16] + var30[var14][var15] * this.c[var14][var16];
               }
            }
         }

         this.e = var12 / var2;
         double[] var31 = new double[var4];
         double[][] var38 = new double[var4][var26];

         for (int var22 = 0; var22 < var4; var22++) {
            var31[var22] = var24[var22] / var2;

            for (int var32 = 0; var32 < var26; var32++) {
               var38[var22][var32] = var9[var22][var32] / var24[var22];
            }
         }

         tq[] var39 = new tq[var4];

         for (int var33 = 0; var33 < var4; var33++) {
            var39[var33] = new sy(var26, var26);
         }

         for (int var34 = 0; var34 < var2; var34++) {
            for (int var36 = 0; var36 < var4; var36++) {
               sy var28;
               tq var29 = (var28 = new sy(Ch.a(this.c[var34], var38[var36]))).c(var28.d()).b(var30[var34][var36]);
               var39[var36] = var39[var36].a(var29);
            }
         }

         double[][][] var35 = new double[var4][var26][var26];

         for (int var37 = 0; var37 < var4; var37++) {
            var39[var37] = var39[var37].b(1.0 / var24[var37]);
            var35[var37] = var39[var37].a();
         }

         this.d = new oz(var31, var38, var35);
      }

      if (BY.w(var10 - this.e) > 1.0E-5) {
         throw new oR();
      }
   }

   private void a(oz var1) {
      double var5 = 1.0E-5;
      short var25 = 1000;
      oz var2 = var1;
      oQ var26 = this;
      int var3 = this.c.length;
      int var4 = var26.c[0].length;
      int var29 = var2.c().size();
      int var6;
      if ((var6 = ((oB)((Co)var2.c().get(0)).b).c().length) != var4) {
         throw new oS(var6, var4);
      }

      var6 = 0;
      double var12 = 0.0;
      var26.e = Double.NEGATIVE_INFINITY;
      var26.d = new oz(var2.c());

      while (var6++ <= 1000 && BY.w(var12 - var26.e) > 1.0E-5) {
         var12 = var26.e;
         double var14 = 0.0;
         List var27 = var26.d.c();
         double[] var7 = new double[var29];
         oB[] var8 = new oB[var29];

         for (int var9 = 0; var9 < var29; var9++) {
            var7[var9] = (Double)((Co)var27.get(var9)).a;
            var8[var9] = (oB)((Co)var27.get(var9)).b;
         }

         double[][] var33 = new double[var3][var29];
         double[] var28 = new double[var29];
         double[][] var10 = new double[var29][var4];

         for (int var11 = 0; var11 < var3; var11++) {
            double var23 = var26.d.a(var26.c[var11]);
            var14 += BY.k(var23);

            for (int var16 = 0; var16 < var29; var16++) {
               var33[var11][var16] = var7[var16] * var8[var16].a(var26.c[var11]) / var23;
               var28[var16] += var33[var11][var16];

               for (int var17 = 0; var17 < var4; var17++) {
                  var10[var16][var17] = var10[var16][var17] + var33[var11][var16] * var26.c[var11][var17];
               }
            }
         }

         var26.e = var14 / var3;
         double[] var34 = new double[var29];
         double[][] var41 = new double[var29][var4];

         for (int var24 = 0; var24 < var29; var24++) {
            var34[var24] = var28[var24] / var3;

            for (int var35 = 0; var35 < var4; var35++) {
               var41[var24][var35] = var10[var24][var35] / var28[var24];
            }
         }

         tq[] var42 = new tq[var29];

         for (int var36 = 0; var36 < var29; var36++) {
            var42[var36] = new sy(var4, var4);
         }

         for (int var37 = 0; var37 < var3; var37++) {
            for (int var39 = 0; var39 < var29; var39++) {
               sy var31;
               tq var32 = (var31 = new sy(Ch.a(var26.c[var37], var41[var39]))).c(var31.d()).b(var33[var37][var39]);
               var42[var39] = var42[var39].a(var32);
            }
         }

         double[][][] var38 = new double[var29][var4][var4];

         for (int var40 = 0; var40 < var29; var40++) {
            var42[var40] = var42[var40].b(1.0 / var28[var40]);
            var38[var40] = var42[var40].a();
         }

         var26.d = new oz(var34, var41, var38);
      }

      if (BY.w(var12 - var26.e) > 1.0E-5) {
         throw new oR();
      }
   }

   private static oz a(double[][] var0, int var1) {
      if (var0.length < 2) {
         throw new pk(var0.length);
      }

      if (var1 < 2) {
         throw new pn(var1, 2, true);
      }

      if (var1 > var0.length) {
         throw new pm(var1, var0.length, true);
      }

      int var2 = var0.length;
      int var3 = var0[0].length;
      oQ.a[] var4 = new oQ.a[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = new oQ.a(var0[var5]);
      }

      Arrays.sort(var4);
      double var20 = 1.0 / var1;
      ArrayList var19 = new ArrayList(var1);

      for (int var7 = 0; var7 < var1; var7++) {
         int var8 = var7 * var2 / var1;
         int var9;
         int var10;
         double[][] var11 = new double[var10 = (var9 = (var7 + 1) * var2 / var1) - var8][var3];
         double[] var12 = new double[var3];
         var8 = var8;

         for (int var13 = 0; var8 < var9; var13++) {
            for (int var14 = 0; var14 < var3; var14++) {
               oQ.a var15;
               double var17 = (var15 = var4[var8]).a[var14];
               var12[var14] += var17;
               var11[var13][var14] = var17;
            }

            var8++;
         }

         Ch.a(1.0 / var10, var12);
         double[][] var22 = new Ah(var11).a().a();
         oB var23 = new oB(var12, var22);
         var19.add(new Co<>(var20, var23));
      }

      return new oz(var19);
   }

   private double a() {
      return this.e;
   }

   private oz b() {
      return new oz(this.d.c());
   }

   static class a implements Comparable<oQ.a> {
      final double[] a;
      private Double b;

      a(double[] var1) {
         this.a = var1;
         this.b = 0.0;

         for (int var2 = 0; var2 < var1.length; var2++) {
            this.b = this.b + var1[var2];
         }

         this.b = this.b / var1.length;
      }

      private int a(oQ.a var1) {
         return this.b.compareTo(var1.b);
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true;
         } else {
            return other instanceof oQ.a ? Ch.i(this.a, ((oQ.a)other).a) : false;
         }
      }

      @Override
      public final int hashCode() {
         return Arrays.hashCode(this.a);
      }

      public final double[] a() {
         return this.a;
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public final int compareTo(Object var1) {
         oQ.a var2 = (oQ.a)var1;
         return this.b.compareTo(var2.b);
      }
   }
}
