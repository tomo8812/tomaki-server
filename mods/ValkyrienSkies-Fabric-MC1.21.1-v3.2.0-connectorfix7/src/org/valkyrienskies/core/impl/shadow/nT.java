package org.valkyrienskies.core.impl.shadow;

public final class nT {
   private nT() {
   }

   private static nQ a(double var0, double var2) {
      if (var0 < 0.0) {
         throw new oV(px.NEGATIVE_COMPLEX_MODULE, var0);
      } else {
         return new nQ(var0 * BY.o(var2), var0 * BY.n(var2));
      }
   }

   public static nQ[] a(double[] var0) {
      nQ[] var1 = new nQ[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = new nQ(var0[var2], 0.0);
      }

      return var1;
   }
}
