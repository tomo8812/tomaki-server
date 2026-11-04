package org.valkyrienskies.core.impl.shadow;

final class wq extends wm {
   private static final long n = 20111120L;

   public wq() {
   }

   private wq(wq var1) {
      super(var1);
   }

   @Override
   public final ws a() {
      return new wq(this);
   }

   @Override
   public final void a(double var1, double var3) {
      double var5;
      double var7 = (var5 = var1 * 0.75) * (var1 * 4.0 - 5.0) + 1.0;
      double var9 = var5 * (5.0 - var1 * 6.0);
      double var11 = var5 * (var1 * 2.0 - 1.0);
      if (super.a != null && var1 <= 0.5) {
         double var36 = var1 * super.d / 8.0;
         double var37 = var1 * 4.0 * var1;
         double var38 = var36 * (8.0 - var1 * 15.0 + var37 * 2.0);
         double var39 = var36 * 3.0 * (var1 * 5.0 - var37);
         double var40 = var36 * 3.0 * var1;
         double var41 = var36 * (var1 * -3.0 + var37);

         for (int var35 = 0; var35 < super.g.length; var35++) {
            double var42 = super.b[0][var35];
            double var43 = super.b[1][var35];
            double var44 = super.b[2][var35];
            double var45 = super.b[3][var35];
            super.g[var35] = super.a[var35] + var38 * var42 + var39 * var43 + var40 * var44 + var41 * var45;
            super.h[var35] = var7 * var42 + var9 * var43 + var5 * var44 + var11 * var45;
         }
      } else {
         double var13 = var3 / 8.0;
         double var15 = var1 * 4.0 * var1;
         double var17 = var13 * (1.0 - var1 * 7.0 + var15 * 2.0);
         double var19 = var13 * 3.0 * (var1 + 1.0 - var15);
         double var21 = var13 * 3.0 * (var1 + 1.0);
         double var23 = var13 * (var1 + 1.0 + var15);

         for (int var34 = 0; var34 < super.g.length; var34++) {
            double var26 = super.b[0][var34];
            double var28 = super.b[1][var34];
            double var30 = super.b[2][var34];
            double var32 = super.b[3][var34];
            super.g[var34] = super.e[var34] - var17 * var26 - var19 * var28 - var21 * var30 - var23 * var32;
            super.h[var34] = var7 * var26 + var9 * var28 + var5 * var30 + var11 * var32;
         }
      }
   }
}
