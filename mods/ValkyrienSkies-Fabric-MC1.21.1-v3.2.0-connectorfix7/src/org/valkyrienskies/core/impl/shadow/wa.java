package org.valkyrienskies.core.impl.shadow;

final class wa extends wm {
   private static final long n = 20111120L;

   public wa() {
   }

   private wa(wa var1) {
      super(var1);
   }

   @Override
   public final ws a() {
      return new wa(this);
   }

   @Override
   public final void a(double var1, double var3) {
      double var5 = 1.0 + var1 * (-7.5 + var1 * (16.0 - var1 * 10.0));
      double var7 = var1 * (28.6875 + var1 * (-91.125 + var1 * 67.5));
      double var9 = var1 * (-44.0 + var1 * (152.0 - var1 * 120.0));
      double var11 = var1 * (23.4375 + var1 * (-78.125 + var1 * 62.5));
      double var13 = var1 * 5.0 / 8.0 * (var1 * 2.0 - 1.0);
      if (super.a != null && var1 <= 0.5) {
         double var40;
         double var41 = (var40 = super.d * var1) * (1.0 + var1 * (-3.75 + var1 * (5.333333333333333 - var1 * 2.5)));
         double var42 = var40 * (var1 * (14.34375 + var1 * (-30.375 + var1 * 135.0 / 8.0)));
         double var43 = var40 * (var1 * (-22.0 + var1 * (50.666666666666664 + var1 * -30.0)));
         double var44 = var40 * (var1 * (11.71875 + var1 * (-26.041666666666668 + var1 * 125.0 / 8.0)));
         double var45 = var40 * (var1 * (-0.3125 + var1 * 5.0 / 12.0));

         for (int var39 = 0; var39 < super.g.length; var39++) {
            double var46 = super.b[0][var39];
            double var47 = super.b[2][var39];
            double var48 = super.b[3][var39];
            double var49 = super.b[4][var39];
            double var50 = super.b[5][var39];
            super.g[var39] = super.a[var39] + var41 * var46 + var42 * var47 + var43 * var48 + var44 * var49 + var45 * var50;
            super.h[var39] = var5 * var46 + var7 * var47 + var9 * var48 + var11 * var49 + var13 * var50;
         }
      } else {
         double var15 = var1 * var1;
         double var17 = super.d * (-0.08333333333333333 + var1 * (1.0 + var1 * (-3.75 + var1 * (5.333333333333333 + var1 * -5.0 / 2.0))));
         double var19 = super.d * (-0.84375 + var15 * (14.34375 + var1 * (-30.375 + var1 * 135.0 / 8.0)));
         double var21 = super.d * (1.3333333333333333 + var15 * (-22.0 + var1 * (50.666666666666664 + var1 * -30.0)));
         double var23 = super.d * (-1.3020833333333333 + var15 * (11.71875 + var1 * (-26.041666666666668 + var1 * 125.0 / 8.0)));
         double var25 = super.d * (-0.10416666666666667 + var15 * (-0.3125 + var1 * 5.0 / 12.0));

         for (int var38 = 0; var38 < super.g.length; var38++) {
            double var28 = super.b[0][var38];
            double var30 = super.b[2][var38];
            double var32 = super.b[3][var38];
            double var34 = super.b[4][var38];
            double var36 = super.b[5][var38];
            super.g[var38] = super.e[var38] + var17 * var28 + var19 * var30 + var21 * var32 + var23 * var34 + var25 * var36;
            super.h[var38] = var5 * var28 + var7 * var30 + var9 * var32 + var11 * var34 + var13 * var36;
         }
      }
   }
}
