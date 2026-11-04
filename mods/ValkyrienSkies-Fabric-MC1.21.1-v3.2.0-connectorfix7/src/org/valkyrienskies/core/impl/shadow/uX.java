package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class uX implements uW {
   private final uM a;
   private final uZ b;
   private final Map<String, Double> c;

   uX(uR var1, uZ var2, uV[] var3) {
      this.a = var1;
      this.b = var2;
      this.c = new HashMap<>();
      uV[] var7 = var3;
      int var8 = var3.length;

      for (int var4 = 0; var4 < var8; var4++) {
         uV var5;
         String var6 = (var5 = var7[var4]).a;
         if (var2.a(var6)) {
            this.c.put(var6, var5.b);
         }
      }
   }

   @Override
   public final Collection<String> a() {
      return this.b.a();
   }

   @Override
   public final boolean a(String var1) {
      return this.b.a(var1);
   }

   @Override
   public final void a(double var1, double[] var3, double[] var4, String var5, double[] var6) {
      int var7 = this.a.a();
      if (!this.b.a(var5)) {
         Arrays.fill(var6, 0, var7, 0.0);
      } else {
         double[] var8 = new double[var7];
         this.b.b(var5);
         double var9 = this.c.get(var5);
         this.a.a(var1, var3, var8);

         for (int var11 = 0; var11 < var7; var11++) {
            var6[var11] = (var8[var11] - var4[var11]) / var9;
         }
      }
   }
}
