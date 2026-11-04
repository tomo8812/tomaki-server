package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class ur implements ut {
   private final tW a;
   private final boolean b;

   private ur(boolean var1, tW var2) {
      this.b = var1;
      this.a = var2;
   }

   @Override
   public final double[][] a(uq var1, Iterable<double[]> var2) {
      int var3 = var1.b;
      int var4 = var1.c;
      us var5 = new us(var1);
      int var6 = 0;
      double[][] var7 = new double[var3][var4];

      for (Iterator var11 = var2.iterator(); var11.hasNext(); var6++) {
         ue var8 = uc.a((double[])var11.next(), var1, this.a);
         us.a var13;
         int var9 = (var13 = var5.a(var8)).a;
         int var14 = var13.b;
         var7[var9][var14]++;
      }

      if (this.b) {
         for (int var12 = 0; var12 < var3; var12++) {
            for (int var15 = 0; var15 < var4; var15++) {
               var7[var12][var15] = var7[var12][var15] / var6;
            }
         }
      }

      return var7;
   }
}
