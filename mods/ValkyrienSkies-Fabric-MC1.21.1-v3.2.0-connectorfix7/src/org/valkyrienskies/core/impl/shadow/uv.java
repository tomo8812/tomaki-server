package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class uv implements ut {
   private final tW a;

   private uv(tW var1) {
      this.a = var1;
   }

   @Override
   public final double[][] a(uq var1, Iterable<double[]> var2) {
      int var3 = var1.b;
      int var4 = var1.c;
      us var5 = new us(var1);
      int[][] var6 = new int[var3][var4];
      double[][] var7 = new double[var3][var4];
      Iterator var13 = var2.iterator();

      while (var13.hasNext()) {
         double[] var8;
         ue var9 = uc.a(var8 = (double[])var13.next(), var1, this.a);
         us.a var10;
         int var11 = (var10 = var5.a(var9)).a;
         int var17 = var10.b;
         var6[var11][var17]++;
         var7[var11][var17] = var7[var11][var17] + this.a.a(var8, var9.c());
      }

      for (int var14 = 0; var14 < var3; var14++) {
         for (int var15 = 0; var15 < var4; var15++) {
            int var16;
            if ((var16 = var6[var14][var15]) != 0) {
               var7[var14][var15] = var7[var14][var15] / var16;
            }
         }
      }

      return var7;
   }
}
