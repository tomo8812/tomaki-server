package org.valkyrienskies.core.impl.shadow;

public final class zN implements zH {
   private final zF a;
   private final int b;

   private zN(int var1, zz var2) {
      this.b = var1;
      this.a = var2;
   }

   public zN(int var1) {
      this(var1, new zz());
   }

   @Override
   public final double[] a() {
      double[] var1 = new double[this.b];
      double var2 = 0.0;

      for (int var4 = 0; var4 < this.b; var4++) {
         double var5 = this.a.nextGaussian();
         var1[var4] = var5;
         var2 += var5 * var5;
      }

      double var7 = 1.0 / BY.a(var2);

      for (int var6 = 0; var6 < this.b; var6++) {
         var1[var6] *= var7;
      }

      return var1;
   }
}
