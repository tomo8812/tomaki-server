package org.valkyrienskies.core.impl.shadow;

public final class ty {
   public final tq a;
   public int b;

   private ty(tq var1) {
      this(var1, 0.0);
   }

   public ty(tq var1, double var2) {
      int var4 = var1.f();
      double[][] var26 = var1.a();
      double[][] var5 = new double[var4][var4];
      int[] var6 = new int[var4];
      int var7 = 0;

      while (var7 < var4) {
         var6[var7] = var7++;
      }

      var7 = 0;
      int var8 = 1;

      while (var8) {
         int var9 = var7;

         for (int var28 = var7 + 1; var28 < var4; var28++) {
            int var12 = var6[var28];
            int var13 = var6[var9];
            if (var26[var12][var12] > var26[var13][var13]) {
               var9 = var28;
            }
         }

         if (var9 != var7) {
            var8 = var6[var7];
            var6[var7] = var6[var9];
            var6[var9] = var8;
            double[] var34 = var5[var7];
            var5[var7] = var5[var9];
            var5[var9] = var34;
         }

         var8 = var6[var7];
         boolean var10000;
         if (var26[var8][var8] <= var2) {
            if (var7 == 0) {
               throw new te(var26[var8][var8], var8, var2);
            }

            for (int var37 = var7; var37 < var4; var37++) {
               if (var26[var6[var37]][var6[var37]] < -var2) {
                  throw new te(var26[var6[var37]][var6[var37]], var37, var2);
               }
            }

            var10000 = false;
         } else {
            double var35 = BY.a(var26[var8][var8]);
            var5[var7][var7] = var35;
            double var14 = 1.0 / var35;
            double var16 = 1.0 / var26[var8][var8];

            for (int var32 = var7 + 1; var32 < var4; var32++) {
               int var10 = var6[var32];
               double var20 = var14 * var26[var10][var8];
               var5[var32][var7] = var20;
               var26[var10][var10] = var26[var10][var10] - var26[var10][var8] * var26[var10][var8] * var16;

               for (int var11 = var7 + 1; var11 < var32; var11++) {
                  int var36 = var6[var11];
                  double var24 = var26[var10][var36] - var20 * var5[var11][var7];
                  var26[var10][var36] = var24;
                  var26[var36][var10] = var24;
               }
            }

            var7++;
            var10000 = var7 < var4;
         }

         var8 = var10000;
      }

      this.b = var7;
      this.a = td.a(var4, var7);

      for (int var31 = 0; var31 < var4; var31++) {
         for (int var33 = 0; var33 < var7; var33++) {
            this.a.c(var6[var31], var33, var5[var31][var33]);
         }
      }
   }

   public final tq a() {
      return this.a;
   }

   public final int b() {
      return this.b;
   }
}
