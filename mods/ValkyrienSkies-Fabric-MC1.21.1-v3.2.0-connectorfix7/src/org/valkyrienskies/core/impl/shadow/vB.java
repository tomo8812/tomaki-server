package org.valkyrienskies.core.impl.shadow;

final class vB extends wm {
   private static final long n = 20111120L;

   public vB() {
   }

   private vB(vB var1) {
      super(var1);
   }

   @Override
   public final ws a() {
      return new vB(this);
   }

   @Override
   public final void a(double var1, double var3) {
      double var5 = 1.0 - var1;
      double var7 = 1.0 - var1 * 2.0;
      double var9 = var5 * var7;
      double var11 = var1 * 2.0 * var5;
      double var13 = -var1 * var7;
      if (super.a != null && var1 <= 0.5) {
         double var34 = var1 * 4.0 * var1;
         double var35;
         double var36 = (var35 = var1 * super.d / 6.0) * (6.0 - var1 * 9.0 + var34);
         double var37 = var35 * (var1 * 6.0 - var34);
         double var38 = var35 * (var1 * -3.0 + var34);

         for (int var33 = 0; var33 < super.g.length; var33++) {
            double var39 = super.b[0][var33];
            double var40 = super.b[1][var33] + super.b[2][var33];
            double var41 = super.b[3][var33];
            super.g[var33] = super.a[var33] + var36 * var39 + var37 * var40 + var38 * var41;
            super.h[var33] = var9 * var39 + var11 * var40 + var13 * var41;
         }
      } else {
         double var15 = var1 * 4.0;
         double var17;
         double var19 = (var17 = var3 / 6.0) * ((-var15 + 5.0) * var1 - 1.0);
         double var21 = var17 * ((var15 - 2.0) * var1 - 2.0);
         double var23 = var17 * ((-var15 - 1.0) * var1 - 1.0);

         for (int var32 = 0; var32 < super.g.length; var32++) {
            double var26 = super.b[0][var32];
            double var28 = super.b[1][var32] + super.b[2][var32];
            double var30 = super.b[3][var32];
            super.g[var32] = super.e[var32] + var19 * var26 + var21 * var28 + var23 * var30;
            super.h[var32] = var9 * var26 + var11 * var28 + var13 * var30;
         }
      }
   }
}
