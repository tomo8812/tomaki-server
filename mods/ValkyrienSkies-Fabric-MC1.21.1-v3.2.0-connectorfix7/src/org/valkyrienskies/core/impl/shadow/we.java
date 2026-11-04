package org.valkyrienskies.core.impl.shadow;

final class we extends wm {
   private static final long n = 20140416L;
   private static final double o = BY.a(21.0);

   public we() {
   }

   private we(we var1) {
      super(var1);
   }

   @Override
   public final ws a() {
      return new we(this);
   }

   @Override
   public final void a(double var1, double var3) {
      double var5 = 1.0 + var1 * (-10.8 + var1 * (36.0 + var1 * (-47.0 + var1 * 21.0)));
      double var7 = var1 * (-13.866666666666667 + var1 * (106.66666666666667 + var1 * (-202.66666666666666 + var1 * 112.0)));
      double var9 = var1 * (12.96 + var1 * (-97.2 + var1 * (194.4 + var1 * -567.0 / 5.0)));
      double var11 = var1
         * ((833.0 + 343.0 * o) / 150.0 + var1 * ((-637.0 - 357.0 * o) / 30.0 + var1 * ((392.0 + 287.0 * o) / 15.0 + var1 * (-49.0 - 49.0 * o) / 5.0)));
      double var13 = var1
         * ((833.0 - 343.0 * o) / 150.0 + var1 * ((-637.0 + 357.0 * o) / 30.0 + var1 * ((392.0 - 287.0 * o) / 15.0 + var1 * (-49.0 + 49.0 * o) / 5.0)));
      double var15 = var1 * (0.6 + var1 * (-3.0 + var1 * 3.0));
      if (super.a != null && var1 <= 0.5) {
         double var44 = 1.0 + var1 * (-5.4 + var1 * (12.0 + var1 * (-11.75 + var1 * 21.0 / 5.0)));
         double var45 = var1 * (-6.933333333333334 + var1 * (35.55555555555556 + var1 * (-50.666666666666664 + var1 * 112.0 / 5.0)));
         double var46 = var1 * (6.48 + var1 * (-32.4 + var1 * (48.6 + var1 * -567.0 / 25.0)));
         double var47 = var1
            * ((833.0 + 343.0 * o) / 300.0 + var1 * ((-637.0 - 357.0 * o) / 90.0 + var1 * ((392.0 + 287.0 * o) / 60.0 + var1 * (-49.0 - 49.0 * o) / 25.0)));
         double var48 = var1
            * ((833.0 - 343.0 * o) / 300.0 + var1 * ((-637.0 + 357.0 * o) / 90.0 + var1 * ((392.0 - 287.0 * o) / 60.0 + var1 * (-49.0 + 49.0 * o) / 25.0)));
         double var49 = var1 * (0.3 + var1 * (-1.0 + var1 * 0.75));

         for (int var50 = 0; var50 < super.g.length; var50++) {
            double var51 = super.b[0][var50];
            double var52 = super.b[1][var50];
            double var53 = super.b[2][var50];
            double var54 = super.b[3][var50];
            double var55 = super.b[4][var50];
            double var56 = super.b[5][var50];
            double var57 = super.b[6][var50];
            super.g[var50] = super.a[var50]
               + var1 * super.d * (var44 * var51 + var52 * 0.0 + var45 * var53 + var46 * var54 + var47 * var55 + var48 * var56 + var49 * var57);
            super.h[var50] = var5 * var51 + var52 * 0.0 + var7 * var53 + var9 * var54 + var11 * var55 + var13 * var56 + var15 * var57;
         }
      } else {
         double var17 = -0.05 + var1 * (0.95 + var1 * (-4.45 + var1 * (7.55 + var1 * -21.0 / 5.0)));
         double var19 = -0.35555555555555557 + var1 * (-0.35555555555555557 + var1 * (-7.288888888888889 + var1 * (28.266666666666666 + var1 * -112.0 / 5.0)));
         double var21 = var1 * (var1 * (6.48 + var1 * (-25.92 + var1 * 567.0 / 25.0)));
         double var23 = -0.2722222222222222
            + var1 * (-0.2722222222222222 + var1 * ((2254.0 + 1029.0 * o) / 900.0 + var1 * ((-1372.0 - 847.0 * o) / 300.0 + var1 * (49.0 + 49.0 * o) / 25.0)));
         double var25 = -0.2722222222222222
            + var1 * (-0.2722222222222222 + var1 * ((2254.0 - 1029.0 * o) / 900.0 + var1 * ((-1372.0 + 847.0 * o) / 300.0 + var1 * (49.0 - 49.0 * o) / 25.0)));
         double var27 = -0.05 + var1 * (-0.05 + var1 * (0.25 + var1 * -0.75));

         for (int var29 = 0; var29 < super.g.length; var29++) {
            double var30 = super.b[0][var29];
            double var32 = super.b[1][var29];
            double var34 = super.b[2][var29];
            double var36 = super.b[3][var29];
            double var38 = super.b[4][var29];
            double var40 = super.b[5][var29];
            double var42 = super.b[6][var29];
            super.g[var29] = super.e[var29]
               + var3 * (var17 * var30 + var32 * 0.0 + var19 * var34 + var21 * var36 + var23 * var38 + var25 * var40 + var27 * var42);
            super.h[var29] = var5 * var30 + var32 * 0.0 + var7 * var34 + var9 * var36 + var11 * var38 + var13 * var40 + var15 * var42;
         }
      }
   }
}
