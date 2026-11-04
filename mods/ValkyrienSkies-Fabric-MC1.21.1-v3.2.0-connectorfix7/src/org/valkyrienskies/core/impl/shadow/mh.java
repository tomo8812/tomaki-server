package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class mh implements lc {
   private final double[] a;
   private final double[] b;

   private mh(double[] var1, double[] var2) {
      if (var1 == null || var2 == null) {
         throw new pl();
      }

      if (var1.length != 0 && var2.length != 0) {
         if (var2.length != var1.length) {
            throw new oS(var2.length, var1.length);
         }

         Ch.a(var1);
         double[] var3;
         this.a = Ch.a(var3 = var1, var3.length);
         var1 = var2;
         this.b = Ch.a(var2, var1.length);
      } else {
         throw new pf();
      }
   }

   @Override
   public final double a(double var1) {
      double var4;
      int var6;
      if ((var6 = Arrays.binarySearch(this.a, var1)) < -1) {
         var4 = this.b[-var6 - 2];
      } else if (var6 >= 0) {
         var4 = this.b[var6];
      } else {
         var4 = this.b[0];
      }

      return var4;
   }
}
