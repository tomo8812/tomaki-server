package org.valkyrienskies.core.impl.shadow;

final class eV {
   private static int a;

   static boolean a(Object[] var0, Object var1) {
      var1 = var1;
      boolean var3 = false;
      var1 = var1;
      var0 = var0;
      if (var0 != null) {
         if (var1 == null) {
            for (int var2 = 0; var2 < var0.length; var2++) {
               if (var0[var2] == null) {
                  return var2 != -1;
               }
            }
         } else {
            for (int var7 = 0; var7 < var0.length; var7++) {
               if (var1.equals(var0[var7])) {
                  return var7 != -1;
               }
            }
         }
      }

      return -1 != -1;
   }

   private static <T> int b(T[] var0, Object var1) {
      return a(var0, var1, 0);
   }

   private static int a(Object[] var0, Object var1, int var2) {
      if (var0 == null) {
         return -1;
      }

      if (var1 == null) {
         for (int var3 = 0; var3 < var0.length; var3++) {
            if (var0[var3] == null) {
               return var3;
            }
         }
      } else {
         for (int var4 = 0; var4 < var0.length; var4++) {
            if (var1.equals(var0[var4])) {
               return var4;
            }
         }
      }

      return -1;
   }
}
