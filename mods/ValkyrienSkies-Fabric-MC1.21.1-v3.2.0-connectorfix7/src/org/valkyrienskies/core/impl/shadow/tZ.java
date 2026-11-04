package org.valkyrienskies.core.impl.shadow;

public final class tZ implements tW {
   private static final long a = -9108154600539125566L;

   @Override
   public final double a(double[] var1, double[] var2) {
      var2 = var2;
      double[] var7 = var1;
      double[] var4 = var2;
      Ch.a(var1, var4, true);
      double var5 = 0.0;

      for (int var3 = 0; var3 < var7.length; var3++) {
         var5 += BY.w(var7[var3] - var2[var3]);
      }

      return var5;
   }
}
