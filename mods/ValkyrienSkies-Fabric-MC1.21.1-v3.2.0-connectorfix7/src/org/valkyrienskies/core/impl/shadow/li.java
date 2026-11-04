package org.valkyrienskies.core.impl.shadow;

public final class li implements kY {
   private final lk a;

   public li(lk var1) {
      this.a = var1;
   }

   @Override
   public final double[] a(double[] var1) {
      lg[] var2 = new lg[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = new lg(var1.length, 1, var3, var1[var3]);
      }

      lg var7 = this.a.a(var2);
      double[] var6 = new double[var1.length];
      int[] var4 = new int[var1.length];

      for (int var5 = 0; var5 < var1.length; var5++) {
         var4[var5] = 1;
         var6[var5] = var7.a(var4);
         var4[var5] = 0;
      }

      return var6;
   }
}
