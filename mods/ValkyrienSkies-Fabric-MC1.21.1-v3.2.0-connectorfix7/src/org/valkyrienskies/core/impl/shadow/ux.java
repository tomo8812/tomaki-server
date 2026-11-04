package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class ux implements ut {
   private final tW a;
   private final boolean b;

   private ux(boolean var1, tW var2) {
      this.b = var1;
      this.a = var2;
   }

   @Override
   public final double[][] a(uq var1, Iterable<double[]> var2) {
      int var3 = var1.b;
      int var4 = var1.c;
      ud var5 = var1.a;
      us var6 = new us(var1);
      int[][] var7 = new int[var3][var4];
      double[][] var8 = new double[var3][var4];
      Iterator var14 = var2.iterator();

      while (var14.hasNext()) {
         Co var9;
         ue var10 = (ue)(var9 = uc.b((double[])var14.next(), var1, this.a)).a;
         us.a var11;
         int var12 = (var11 = var6.a(var10)).a;
         int var18 = var11.b;
         var7[var12][var18]++;
         var10 = var10;
         if (!var5.a(var10, (Iterable<ue>)null).contains(var9.b)) {
            var8[var12][var18]++;
         }
      }

      if (this.b) {
         for (int var15 = 0; var15 < var3; var15++) {
            for (int var16 = 0; var16 < var4; var16++) {
               var8[var15][var16] = var8[var15][var16] / var7[var15][var16];
            }
         }
      }

      return var8;
   }
}
