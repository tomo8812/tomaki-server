package org.valkyrienskies.core.impl.shadow;

public final class lj implements kX {
   private final ll a;

   private lj(ll var1) {
      this.a = var1;
   }

   @Override
   public final double[][] a(double[] var1) {
      lg[] var2 = new lg[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = new lg(var1.length, 1, var3, var1[var3]);
      }

      lg[] var8;
      double[][] var7 = new double[(var8 = this.a.a(var2)).length][var1.length];
      int[] var4 = new int[var1.length];

      for (int var5 = 0; var5 < var8.length; var5++) {
         for (int var6 = 0; var6 < var1.length; var6++) {
            var4[var6] = 1;
            var7[var5][var6] = var8[var5].a(var4);
            var4[var6] = 0;
         }
      }

      return var7;
   }
}
