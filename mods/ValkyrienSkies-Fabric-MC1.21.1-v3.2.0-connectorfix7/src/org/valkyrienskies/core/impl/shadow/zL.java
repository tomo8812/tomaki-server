package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class zL implements zH {
   private final zA a;
   private final double[] b;
   private final double[] c;

   private zL(double[] var1, double[] var2, zA var3) {
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      this.b = (double[])var1.clone();
      this.c = (double[])var2.clone();
      this.a = var3;
   }

   private zL(int var1, zA var2) {
      this.b = new double[var1];
      this.c = new double[var1];
      Arrays.fill(this.c, 1.0);
      this.a = var2;
   }

   @Override
   public final double[] a() {
      double[] var1 = new double[this.b.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = this.b[var2] + this.c[var2] * this.a.a();
      }

      return var1;
   }
}
