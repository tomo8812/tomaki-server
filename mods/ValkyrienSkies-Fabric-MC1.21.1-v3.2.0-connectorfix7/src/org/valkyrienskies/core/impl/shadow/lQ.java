package org.valkyrienskies.core.impl.shadow;

public final class lQ implements kS, ln {
   @Override
   public final double a(double var1) {
      return BY.m(var1);
   }

   @Deprecated
   @Override
   public final lc a() {
      return kV.a(this).a();
   }

   @Override
   public final lg a(lg var1) {
      var1 = var1;
      lg var2 = new lg(var1.a);
      boolean var15 = false;
      double[] var4 = var2.b;
      boolean var16 = false;
      double[] var3 = var1.b;
      lf var17 = var1.a;
      double[] var5;
      (var5 = new double[1 + var17.b])[0] = BY.m(var3[0]);
      if (var17.b > 0) {
         double var10;
         double var12 = (var10 = 1.0 / var3[0]) / BY.k(10.0);

         for (int var6 = 1; var6 <= var17.b; var6++) {
            var5[var6] = var12;
            var12 *= -var6 * var10;
         }
      }

      var17.b(var3, 0, var5, var4, 0);
      return var2;
   }
}
