package org.valkyrienskies.core.impl.shadow;

public final class mz extends mu<Double> {
   @Override
   protected final Co<Double[], Double[]> c(int var1) {
      if (var1 == 1) {
         return new Co<>(new Double[]{0.0}, new Double[]{2.0});
      }

      Double[] var2 = this.b(var1 - 1).a;
      Double[] var3 = new Double[var1];
      Double[] var4 = new Double[var1];
      int var5 = var1 / 2;

      for (int var6 = 0; var6 < var5; var6++) {
         double var7 = var6 == 0 ? -1.0 : var2[var6 - 1];
         double var9 = var5 == 1 ? 1.0 : var2[var6];
         double var11 = 1.0;
         double var13 = var7;
         double var15 = 1.0;
         double var17 = var9;

         for (int var19 = 1; var19 < var1; var19++) {
            int var20 = 2 * var19 + 1;
            int var21 = var19 + 1;
            double var22 = (var20 * var7 * var13 - var19 * var11) / var21;
            double var24 = (var20 * var9 * var17 - var19 * var15) / var21;
            var11 = var13;
            var13 = var22;
            var15 = var17;
            var17 = var24;
         }

         double var33 = 0.5 * (var7 + var9);
         double var34 = 1.0;
         double var23 = var33;
         boolean var25 = false;

         while (!var25) {
            var25 = var9 - var7 <= Math.ulp(var33);
            var34 = 1.0;
            var23 = var33;

            for (int var26 = 1; var26 < var1; var26++) {
               double var27 = ((2 * var26 + 1) * var33 * var23 - var26 * var34) / (var26 + 1);
               var34 = var23;
               var23 = var27;
            }

            if (!var25) {
               if (var13 * var23 <= 0.0) {
                  var9 = var33;
               } else {
                  var7 = var33;
                  var13 = var23;
               }

               var33 = 0.5 * (var7 + var9);
            }
         }

         double var35 = var1 * (var34 - var33 * var23);
         double var28 = 2.0 * (1.0 - var33 * var33) / (var35 * var35);
         var3[var6] = var33;
         var4[var6] = var28;
         int var31 = var1 - var6 - 1;
         var3[var31] = -var33;
         var4[var31] = var28;
      }

      if (var1 % 2 != 0) {
         double var30 = 1.0;

         for (byte var8 = 1; var8 < var1; var8 += 2) {
            var30 = -var8 * var30 / (var8 + 1);
         }

         double var32 = var1 * var30;
         double var10 = 2.0 / (var32 * var32);
         var3[var5] = 0.0;
         var4[var5] = var10;
      }

      return new Co<>(var3, var4);
   }
}
