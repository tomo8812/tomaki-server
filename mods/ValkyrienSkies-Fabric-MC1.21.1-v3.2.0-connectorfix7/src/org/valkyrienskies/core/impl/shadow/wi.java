package org.valkyrienskies.core.impl.shadow;

final class wi extends wm {
   private static final long n = 20111120L;

   public wi() {
   }

   private wi(wi var1) {
      super(var1);
   }

   @Override
   public final ws a() {
      return new wi(this);
   }

   @Override
   public final void a(double var1, double var3) {
      double var5 = var1 * 2.0;
      double var7 = 1.0 - var5;
      if (super.a != null && var1 <= 0.5) {
         double var20 = var1 * var3;
         double var21 = var1 * var1 * super.d;

         for (int var19 = 0; var19 < super.g.length; var19++) {
            double var22 = super.b[0][var19];
            double var23 = super.b[1][var19];
            super.g[var19] = super.a[var19] + var20 * var22 + var21 * var23;
            super.h[var19] = var7 * var22 + var5 * var23;
         }
      } else {
         double var9 = var3 * var1;
         double var11 = var3 * (var1 + 1.0);

         for (int var18 = 0; var18 < super.g.length; var18++) {
            double var14 = super.b[0][var18];
            double var16 = super.b[1][var18];
            super.g[var18] = super.e[var18] + var9 * var14 - var11 * var16;
            super.h[var18] = var7 * var14 + var5 * var16;
         }
      }
   }
}
