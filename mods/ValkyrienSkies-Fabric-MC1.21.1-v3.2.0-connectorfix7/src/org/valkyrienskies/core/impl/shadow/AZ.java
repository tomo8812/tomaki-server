package org.valkyrienskies.core.impl.shadow;

public final class AZ {
   private boolean a(int var1, int var2, double var3, AY var5, double var6) {
      AY var27 = var5;
      double var12 = var3;
      var2 = var2;
      var1 = var1;
      if (var1 < 0) {
         throw new pj(var1);
      }

      if (var2 < 0) {
         throw new pj(var2);
      }

      if (var12 < 0.0 || var12 > 1.0) {
         throw new po(var12, 0, 1);
      }

      if (var1 < var2) {
         throw new oV(px.BINOMIAL_INVALID_PARAMETERS_ORDER, var1, var2);
      }

      if (var27 == null) {
         throw new pl();
      }

      og var4 = new og(null, var1, var12);
      double var29;
      switch (var27) {
         case GREATER_THAN:
            var29 = 1.0 - var4.d(var2 - 1);
            break;
         case LESS_THAN:
            var29 = var4.d(var2);
            break;
         case TWO_SIDED:
            int var28 = 0;
            var1 = var1;
            double var18 = 0.0;

            do {
               double var20 = var4.c(var28);
               double var22 = var4.c(var1);
               if (var20 == var22) {
                  var18 += var20 * 2.0;
                  var28++;
                  var1--;
               } else if (var20 < var22) {
                  var18 += var20;
                  var28++;
               } else {
                  var18 += var22;
                  var1--;
               }
            } while (var28 <= var2 && var1 >= var2);

            var29 = var18;
            break;
         default:
            throw new oY(px.OUT_OF_RANGE_SIMPLE, var27, AY.TWO_SIDED, AY.LESS_THAN);
      }

      return var29 < var6;
   }

   private static double a(int var0, int var1, double var2, AY var4) {
      if (var0 < 0) {
         throw new pj(var0);
      }

      if (var1 < 0) {
         throw new pj(var1);
      }

      if (var2 < 0.0 || var2 > 1.0) {
         throw new po(var2, 0, 1);
      }

      if (var0 < var1) {
         throw new oV(px.BINOMIAL_INVALID_PARAMETERS_ORDER, var0, var1);
      }

      if (var4 == null) {
         throw new pl();
      }

      og var15 = new og(null, var0, var2);
      switch (var4) {
         case GREATER_THAN:
            return 1.0 - var15.d(var1 - 1);
         case LESS_THAN:
            return var15.d(var1);
         case TWO_SIDED:
            int var3 = 0;
            var0 = var0;
            double var8 = 0.0;

            do {
               double var10 = var15.c(var3);
               double var12 = var15.c(var0);
               if (var10 == var12) {
                  var8 += var10 * 2.0;
                  var3++;
                  var0--;
               } else if (var10 < var12) {
                  var8 += var10;
                  var3++;
               } else {
                  var8 += var12;
                  var0--;
               }
            } while (var3 <= var1 && var0 >= var1);

            return var8;
         default:
            throw new oY(px.OUT_OF_RANGE_SIMPLE, var4, AY.TWO_SIDED, AY.LESS_THAN);
      }
   }
}
