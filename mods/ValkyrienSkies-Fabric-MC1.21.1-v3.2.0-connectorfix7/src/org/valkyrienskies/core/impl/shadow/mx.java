package org.valkyrienskies.core.impl.shadow;

public final class mx extends mu<Double> {
   private static final double a = 1.772453850905516;
   private static final double b = 0.7511255444649425;
   private static final double c = 1.0622519320271968;

   @Override
   protected final Co<Double[], Double[]> c(int var1) {
      if (var1 == 1) {
         return new Co<>(new Double[]{0.0}, new Double[]{1.772453850905516});
      }

      int var2 = var1 - 1;
      Double[] var3 = this.b(var2).a;
      Double[] var4 = new Double[var1];
      Double[] var5 = new Double[var1];
      double var6 = BY.a((double)(2 * var2));
      double var8 = BY.a((double)(2 * var1));
      int var10 = var1 / 2;

      for (int var11 = 0; var11 < var10; var11++) {
         double var12 = var11 == 0 ? -var6 : var3[var11 - 1];
         double var14 = var10 == 1 ? -0.5 : var3[var11];
         double var16 = 0.7511255444649425;
         double var18 = var12 * 1.0622519320271968;

         for (int var20 = 1; var20 < var1; var20++) {
            double var21 = var20 + 1;
            double var23 = BY.a(2.0 / var21);
            double var25 = BY.a(var20 / var21);
            double var27 = var23 * var12 * var18 - var25 * var16;
            var16 = var18;
            var18 = var27;
         }

         double var39 = 0.5 * (var12 + var14);
         double var22 = 0.7511255444649425;
         boolean var26 = false;

         while (!var26) {
            var26 = var14 - var12 <= Math.ulp(var39);
            var22 = 0.7511255444649425;
            double var24 = var39 * 1.0622519320271968;

            for (int var40 = 1; var40 < var1; var40++) {
               double var28 = var40 + 1;
               double var30 = BY.a(2.0 / var28);
               double var32 = BY.a(var40 / var28);
               double var34 = var30 * var39 * var24 - var32 * var22;
               var22 = var24;
               var24 = var34;
            }

            if (!var26) {
               if (var18 * var24 < 0.0) {
                  var14 = var39;
               } else {
                  var12 = var39;
                  var18 = var24;
               }

               var39 = 0.5 * (var12 + var14);
            }
         }

         double var41 = var8 * var22;
         double var29 = 2.0 / (var41 * var41);
         var4[var11] = var39;
         var5[var11] = var29;
         int var31 = var2 - var11;
         var4[var31] = -var39;
         var5[var31] = var29;
      }

      if (var1 % 2 != 0) {
         double var36 = 0.7511255444649425;

         for (byte var13 = 1; var13 < var1; var13 += 2) {
            double var38 = var13 + 1;
            var36 = -BY.a(var13 / var38) * var36;
         }

         double var37 = var8 * var36;
         double var15 = 2.0 / (var37 * var37);
         var4[var10] = 0.0;
         var5[var10] = var15;
      }

      return new Co<>(var4, var5);
   }
}
