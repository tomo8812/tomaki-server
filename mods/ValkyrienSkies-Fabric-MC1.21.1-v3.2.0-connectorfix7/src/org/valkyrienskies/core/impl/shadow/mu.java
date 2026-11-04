package org.valkyrienskies.core.impl.shadow;

import java.util.Map;
import java.util.TreeMap;

public abstract class mu<T extends Number> {
   private final Map<Integer, Co<T[], T[]>> a = new TreeMap<>();
   private final Map<Integer, Co<double[], double[]>> b = new TreeMap<>();

   public final Co<double[], double[]> a(int var1) {
      if (var1 <= 0) {
         throw new pk(px.NUMBER_OF_POINTS, var1);
      }

      Co var2;
      if ((var2 = this.b.get(var1)) == null) {
         Number[] var3 = (Number[])(var2 = this.b(var1)).a;
         Number[] var9 = (Number[])var2.b;
         int var4;
         double[] var5 = new double[var4 = var3.length];
         double[] var6 = new double[var4];

         for (int var7 = 0; var7 < var4; var7++) {
            var5[var7] = var3[var7].doubleValue();
            var6[var7] = var9[var7].doubleValue();
         }

         var2 = new Co<>(var5, var6);
         this.b.put(var1, var2);
      }

      return new Co<>((double[])((double[])var2.a).clone(), (double[])((double[])var2.b).clone());
   }

   protected final synchronized Co<T[], T[]> b(int var1) {
      Co var2;
      if ((var2 = this.a.get(var1)) == null) {
         Co var3 = this.c(var1);
         mu var5 = this;
         if (((Number[])var3.a).length != ((Number[])var3.b).length) {
            throw new oS(((Number[])var3.a).length, ((Number[])var3.b).length);
         }

         var5.a.put(((Number[])var3.a).length, var3);
         return this.b(var1);
      } else {
         return var2;
      }
   }

   private void a(Co<T[], T[]> var1) {
      if (((Number[])var1.a).length != ((Number[])var1.b).length) {
         throw new oS(((Number[])var1.a).length, ((Number[])var1.b).length);
      }

      this.a.put(((Number[])var1.a).length, var1);
   }

   protected abstract Co<T[], T[]> c(int var1);

   private static <T extends Number> Co<double[], double[]> b(Co<T[], T[]> var0) {
      Number[] var1 = (Number[])var0.a;
      Number[] var6 = (Number[])var0.b;
      int var2;
      double[] var3 = new double[var2 = var1.length];
      double[] var4 = new double[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var3[var5] = var1[var5].doubleValue();
         var4[var5] = var6[var5].doubleValue();
      }

      return new Co<>(var3, var4);
   }
}
