package org.valkyrienskies.core.impl.shadow;

import java.util.List;

public final class zo {
   private zo() {
   }

   private static boolean a(int var0) {
      if (var0 < 2) {
         return false;
      }

      int[] var1 = zp.a;
      int var2 = zp.a.length;

      for (int var3 = 0; var3 < var2; var3++) {
         int var4 = var1[var3];
         if (0 == var0 % var4) {
            if (var0 == var4) {
               return true;
            }

            return false;
         }
      }

      return zp.b(var0);
   }

   private static int b(int var0) {
      if (var0 < 0) {
         throw new oV(px.NUMBER_TOO_SMALL, var0, 0);
      }

      if (var0 == 2) {
         return 2;
      }

      if ((var0 = var0 | 1) == 1) {
         return 2;
      }

      if (a(var0)) {
         return var0;
      }

      int var1 = var0 % 3;
      if (var1 == 0) {
         var0 += 2;
      } else if (1 == var1) {
         var0 += 4;
      }

      while (!a(var0)) {
         var0 += 2;
         if (a(var0)) {
            return var0;
         }

         var0 += 4;
      }

      return var0;
   }

   private static List<Integer> c(int var0) {
      if (var0 < 2) {
         throw new oV(px.NUMBER_TOO_SMALL, var0, 2);
      } else {
         return zp.a(var0);
      }
   }
}
