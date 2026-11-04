package org.valkyrienskies.core.impl.shadow;

public final class tX implements tW {
   private static final long a = -5406732779747414922L;

   @Override
   public final double a(double[] var1, double[] var2) {
      double[] var4 = var2;
      Ch.a(var1, var4, true);
      double var3 = 0.0;
      double var5 = 0.0;

      for (int var7 = 0; var7 < var1.length; var7++) {
         double var8 = var1[var7] + var3 - var2[var7];
         var5 += BY.w(var8);
         var3 = var8;
      }

      return var5;
   }
}
