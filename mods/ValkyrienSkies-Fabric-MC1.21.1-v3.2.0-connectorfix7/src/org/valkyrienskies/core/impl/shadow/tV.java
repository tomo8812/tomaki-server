package org.valkyrienskies.core.impl.shadow;

public final class tV implements tW {
   private static final long a = -4694868171115238296L;

   @Override
   public final double a(double[] var1, double[] var2) {
      var2 = var2;
      double[] var7 = var1;
      double[] var4 = var2;
      Ch.a(var1, var4, true);
      double var5 = 0.0;

      for (int var3 = 0; var3 < var7.length; var3++) {
         var5 = BY.e(var5, BY.w(var7[var3] - var2[var3]));
      }

      return var5;
   }
}
