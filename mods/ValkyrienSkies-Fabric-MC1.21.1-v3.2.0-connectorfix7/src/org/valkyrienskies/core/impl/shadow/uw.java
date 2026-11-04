package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collections;

public final class uw implements ut {
   private final int a;
   private final tW b;
   private final double c;

   private uw(int var1, tW var2) {
      this.a = var1;
      this.b = var2;
      double var3 = 0.0;

      for (int var5 = 0; var5 < var1; var5++) {
         var3 += var1 - var5;
      }

      this.c = 1.0 / var3;
   }

   @Override
   public final double[][] a(uq var1, Iterable<double[]> var2) {
      int var3 = var1.b;
      int var4 = var1.c;
      int var5;
      if ((var5 = var3 * var4) < this.a) {
         throw new pn(var5, this.a, true);
      }

      us var27 = new us(var1);
      double[][] var24 = new double[var3][var4];

      for (double[] var10000 : var2) {
         tW var7 = this.b;
         ud var6 = var1.a;
         double[] var25 = var10000;
         ArrayList var8 = new ArrayList();

         for (ue var9 : var6) {
            double var21 = var7.a(var9.c(), var25);
            var8.add(new uc.a(var9, var21));
         }

         Collections.sort(var8, uc.a.a);
         int var29;
         ue[] var34 = new ue[var29 = var8.size()];

         for (int var35 = 0; var35 < var29; var35++) {
            var34[var35] = ((uc.a)var8.get(var35)).b;
         }

         ue[] var26 = var34;

         for (int var30 = 0; var30 < this.a; var30++) {
            us.a var31;
            int var33 = (var31 = var27.a(var26[var30])).a;
            int var32 = var31.b;
            var24[var33][var32] = var24[var33][var32] + (this.a - var30) * this.c;
         }
      }

      return var24;
   }
}
