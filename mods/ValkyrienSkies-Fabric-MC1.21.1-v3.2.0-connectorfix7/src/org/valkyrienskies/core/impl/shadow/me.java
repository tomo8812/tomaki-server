package org.valkyrienskies.core.impl.shadow;

public final class me implements kS, ln {
   private static final double a = 0.006;
   private final boolean b = false;

   public me() {
      this(false);
   }

   private me(boolean var1) {
   }

   @Override
   public final double a(double var1) {
      double var3;
      double var5;
      return BY.w(var3 = this.b ? var1 * Math.PI : var1) <= 0.006 ? (((var5 = var3 * var3) - 20.0) * var5 + 120.0) / 120.0 : BY.n(var3) / var3;
   }

   @Deprecated
   @Override
   public final lc a() {
      return kV.a(this).a();
   }

   @Override
   public final lg a(lg var1) {
      double var2;
      double var4 = (var2 = (this.b ? Math.PI : 1.0) * var1.b[0]) * var2;
      double[] var6 = new double[var1.D() + 1];
      if (BY.w(var2) <= 0.006) {
         for (int var7 = 0; var7 < var6.length; var7++) {
            int var8 = var7 / 2;
            if ((var7 & 1) == 0) {
               var6[var7] = ((var8 & 1) == 0 ? 1 : -1) * (1.0 / (var7 + 1) - var4 * (1.0 / (2 * var7 + 6) - var4 / (var7 * 24 + 120)));
            } else {
               var6[var7] = ((var8 & 1) == 0 ? -var2 : var2) * (1.0 / (var7 + 2) - var4 * (1.0 / (var7 * 6 + 24) - var4 / (var7 * 120 + 720)));
            }
         }
      } else {
         double var22 = 1.0 / var2;
         double var9 = BY.o(var2);
         double var11 = BY.n(var2);
         var6[0] = var22 * var11;
         double[] var13;
         (var13 = new double[var6.length])[0] = 1.0;
         double var14 = var22;

         for (int var16 = 1; var16 < var6.length; var16++) {
            double var17 = 0.0;
            double var19 = 0.0;
            int var21;
            if ((var16 & 1) == 0) {
               var13[var16] = 0.0;
               var21 = var16;
            } else {
               var13[var16] = var13[var16 - 1];
               var19 = var13[var16];
               var21 = var16 - 1;
            }

            for (int var26 = var21; var26 > 1; var26 -= 2) {
               var13[var26] = (var26 - var16) * var13[var26] - var13[var26 - 1];
               var17 = var17 * var4 + var13[var26];
               var13[var26 - 1] = (var26 - 1 - var16) * var13[var26 - 1] + var13[var26 - 2];
               var19 = var19 * var4 + var13[var26 - 1];
            }

            var13[0] *= -var16;
            var17 = var17 * var4 + var13[0];
            var14 *= var22;
            var6[var16] = var14 * (var17 * var11 + var19 * var2 * var9);
         }
      }

      if (this.b) {
         double var23 = Math.PI;

         for (int var24 = 1; var24 < var6.length; var24++) {
            var6[var24] *= var23;
            var23 *= Math.PI;
         }
      }

      return var1.a(var6);
   }
}
