package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.List;

public abstract class tS<T extends tL> {
   private final tW a;

   public tS() {
      this(new tY());
   }

   public tS(tW var1) {
      this.a = var1;
   }

   public abstract double a(List<? extends tK<T>> var1);

   public static boolean a(double var0, double var2) {
      return var0 < var2;
   }

   protected final double a(tL var1, tL var2) {
      return this.a.a(var1.a(), var2.a());
   }

   protected static tL a(tK<T> var0) {
      List var1 = var0.b;
      if (var0.b.isEmpty()) {
         return null;
      }

      if (var0 instanceof tJ) {
         tJ var6;
         return (var6 = (tJ)var0).a;
      }

      double[] var5 = new double[((tL)var1.get(0)).a().length];
      Iterator var2 = var1.iterator();

      while (var2.hasNext()) {
         double[] var3 = ((tL)var2.next()).a();

         for (int var4 = 0; var4 < var5.length; var4++) {
            var5[var4] += var3[var4];
         }
      }

      for (int var7 = 0; var7 < var5.length; var7++) {
         var5[var7] /= var1.size();
      }

      return new tO(var5);
   }
}
