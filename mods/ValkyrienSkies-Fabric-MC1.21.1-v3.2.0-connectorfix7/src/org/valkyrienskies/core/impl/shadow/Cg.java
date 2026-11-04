package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class Cg implements Serializable {
   private static final long b = 20140713L;
   private static final int c = 15;
   public final Cp a;

   public Cg() {
      this.a = new Cj();
   }

   public Cg(Cj var1) {
      Ci.a(var1);
      this.a = var1;
   }

   public final Cp a() {
      return this.a;
   }

   public final double a(double[] var1, int[] var2, int var3) {
      int var4 = 0;
      int var5 = var1.length;
      int var6 = 0;
      boolean var7 = var2 != null;

      while (var5 - var4 > 15) {
         int var8;
         if (var7 && var6 < var2.length && var2[var6] >= 0) {
            var8 = var2[var6];
         } else {
            var8 = a(var1, var4, var5, this.a.a(var1, var4, var5));
            if (var7 && var6 < var2.length) {
               var2[var6] = var8;
            }
         }

         if (var3 == var8) {
            return var1[var3];
         }

         if (var3 < var8) {
            var5 = var8;
            var6 = BY.a(2 * var6 + 1, var7 ? var2.length : var5);
         } else {
            var4 = var8 + 1;
            var6 = BY.a(2 * var6 + 2, var7 ? var2.length : var5);
         }
      }

      Arrays.sort(var1, var4, var5);
      return var1[var3];
   }

   private static int a(double[] var0, int var1, int var2, int var3) {
      double var4 = var0[var3];
      var0[var3] = var0[var1];
      var3 = var1 + 1;
      int var6 = var2 - 1;

      while (var3 < var6) {
         while (var3 < var6 && var0[var6] > var4) {
            var6--;
         }

         while (var3 < var6 && var0[var3] < var4) {
            var3++;
         }

         if (var3 < var6) {
            double var8 = var0[var3];
            var0[var3++] = var0[var6];
            var0[var6--] = var8;
         }
      }

      if (var3 >= var2 || var0[var3] > var4) {
         var3--;
      }

      var0[var1] = var0[var3];
      var0[var3] = var4;
      return var3;
   }
}
