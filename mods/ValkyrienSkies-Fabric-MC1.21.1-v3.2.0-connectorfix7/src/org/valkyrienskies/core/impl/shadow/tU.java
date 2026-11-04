package org.valkyrienskies.core.impl.shadow;

public final class tU implements tW {
   private static final long a = -6972277381587032228L;

   @Override
   public final double a(double[] var1, double[] var2) {
      double[] var4 = var2;
      Ch.a(var1, var4, true);
      double var3 = 0.0;

      for (int var5 = 0; var5 < var1.length; var5++) {
         double var6 = BY.w(var1[var5] - var2[var5]);
         double var8 = BY.w(var1[var5]) + BY.w(var2[var5]);
         var3 += var6 == 0.0 && var8 == 0.0 ? 0.0 : var6 / var8;
      }

      return var3;
   }
}
