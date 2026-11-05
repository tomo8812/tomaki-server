package maki.lodcpu;

import java.util.BitSet;

final class CpuTopology {
   private CpuTopology() {
   }

   static int count(String text) {
      BitSet cpus = new BitSet();
      String[] var2 = text.trim().split(",", -1);
      int var3 = var2.length;
      int var4 = 0;

      while (var4 < var3) {
         String part = var2[var4];
         String[] span = part.trim().split("-", -1);
         if (span.length >= 1 && span.length <= 2) {
            int first = Integer.parseInt(span[0]);
            int last = span.length == 1 ? first : Integer.parseInt(span[1]);
            if (first >= 0 && last >= first && last <= 65535) {
               cpus.set(first, last + 1);
               var4++;
               continue;
            }

            throw new IllegalArgumentException("CPU range");
         }

         throw new IllegalArgumentException("CPU list");
      }

      if (cpus.isEmpty()) {
         throw new IllegalArgumentException("Empty CPU list");
      } else {
         return cpus.cardinality();
      }
   }

   static double restrict(double current, String cpus) {
      try {
         return Math.min(current, count(cpus));
      } catch (IllegalArgumentException ignored) {
         return current;
      }
   }
}
