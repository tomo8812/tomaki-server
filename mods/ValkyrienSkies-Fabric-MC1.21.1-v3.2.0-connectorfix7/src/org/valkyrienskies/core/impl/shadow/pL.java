package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

public final class pL extends pD {
   private final kZ a;
   private final double[] b;
   private final int c;

   private pL(kZ var1, double[] var2, int var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private static pL a(kZ var0, double[] var1) {
      return new pL(var0, var1, Integer.MAX_VALUE);
   }

   private pL a(double[] var1) {
      return new pL(this.a, (double[])var1.clone(), this.c);
   }

   private pL a(int var1) {
      return new pL(this.a, this.b, var1);
   }

   @Override
   protected final pW a(Collection<pM> var1) {
      int var2;
      double[] var3 = new double[var2 = var1.size()];
      double[] var11 = new double[var2];
      int var4 = 0;

      for (pM var6 : var1) {
         var3[var4] = var6.c;
         var11[var4] = var6.a;
         var4++;
      }

      pD.a var12 = new pD.a(this.a, var1);
      pT var10000 = new pT();
      int var8 = Integer.MAX_VALUE;
      pT var7 = var10000;
      var10000.a = Integer.MAX_VALUE;
      int var9 = this.c;
      pT var13 = var7;
      var7.b = var9;
      var10000 = var13.b(this.b).a(var3);
      sM var10 = new sM(var11);
      var7 = var10000;
      var10000.c = var10;
      return var7.a(var12.a(), var12.b()).a();
   }
}
