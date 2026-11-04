package org.valkyrienskies.core.impl.shadow;

final class vU extends wm {
   private static final double n = 1.0 - BY.a(0.5);
   private static final double o = 1.0 + BY.a(0.5);
   private static final long p = 20111120L;

   public vU() {
   }

   private vU(vU var1) {
      super(var1);
   }

   @Override
   public final ws a() {
      return new vU(this);
   }

   @Override
   public final void a(double var1, double var3) {
      double var5;
      double var7 = (var5 = var1 * 2.0) * var5;
      double var9 = var1 * (var5 - 3.0) + 1.0;
      double var11;
      double var13 = (var11 = var5 * (1.0 - var1)) * n;
      double var15 = var11 * o;
      double var17 = var1 * (var5 - 1.0);
      if (super.a != null && var1 <= 0.5) {
         double var42;
         double var43 = (var42 = var1 * super.d / 6.0) * (var1 * 6.0 - var7);
         double var44 = var42 * (6.0 - var1 * 9.0 + var7);
         double var45 = var43 * n;
         double var46 = var43 * o;
         double var47 = var42 * (var1 * -3.0 + var7);

         for (int var41 = 0; var41 < super.g.length; var41++) {
            double var48 = super.b[0][var41];
            double var49 = super.b[1][var41];
            double var50 = super.b[2][var41];
            double var51 = super.b[3][var41];
            super.g[var41] = super.a[var41] + var44 * var48 + var45 * var49 + var46 * var50 + var47 * var51;
            super.h[var41] = var9 * var48 + var13 * var49 + var15 * var50 + var17 * var51;
         }
      } else {
         double var19;
         double var21 = (var19 = var3 / 6.0) * (var5 + 2.0 - var7);
         double var23 = var19 * (1.0 - var1 * 5.0 + var7);
         double var25 = var21 * n;
         double var27 = var21 * o;
         double var29 = var19 * (var1 + 1.0 + var7);

         for (int var40 = 0; var40 < super.g.length; var40++) {
            double var32 = super.b[0][var40];
            double var34 = super.b[1][var40];
            double var36 = super.b[2][var40];
            double var38 = super.b[3][var40];
            super.g[var40] = super.e[var40] - var23 * var32 - var25 * var34 - var27 * var36 - var29 * var38;
            super.h[var40] = var9 * var32 + var13 * var34 + var15 * var36 + var17 * var38;
         }
      }
   }
}
