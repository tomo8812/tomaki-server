package maki.lodcpu;

import java.util.BitSet;

final class CpuTopology {
   private CpuTopology() {
   }

   static int count(String var0) {
      BitSet var1 = new BitSet();
      String[] var2 = var0.trim().split(",", -1);
      int var3 = var2.length;
      int var4 = 0;

      while (var4 < var3) {
         String var5 = var2[var4];
         String[] var6 = var5.trim().split("-", -1);
         if (var6.length >= 1 && var6.length <= 2) {
            int var7 = Integer.parseInt(var6[0]);
            int var8 = var6.length == 1 ? var7 : Integer.parseInt(var6[1]);
            if (var7 >= 0 && var8 >= var7 && var8 <= 65535) {
               var1.set(var7, var8 + 1);
               var4++;
               continue;
            }

            throw new IllegalArgumentException("CPU range");
         }

         throw new IllegalArgumentException("CPU list");
      }

      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Empty CPU list");
      } else {
         return var1.cardinality();
      }
   }

   static double restrict(double var0, String var2) {
      try {
         return Math.min(var0, count(var2));
      } catch (IllegalArgumentException var4) {
         return var0;
      }
   }
}
