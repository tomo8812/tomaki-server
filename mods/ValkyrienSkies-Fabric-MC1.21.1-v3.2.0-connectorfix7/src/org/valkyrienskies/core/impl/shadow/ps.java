package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;

public final class ps {
   private ps() {
   }

   public static Object[] a(Object[] var0) {
      ArrayList var1 = new ArrayList();
      if (var0 != null) {
         Object[] var8;
         int var2 = (var8 = var0).length;

         for (int var3 = 0; var3 < var2; var3++) {
            Object var4;
            if ((var4 = var8[var3]) instanceof Object[]) {
               Object[] var9;
               int var5 = (var9 = a((Object[])var4)).length;

               for (int var6 = 0; var6 < var5; var6++) {
                  Object var7 = var9[var6];
                  var1.add(var7);
               }
            } else {
               var1.add(var4);
            }
         }
      }

      return var1.toArray();
   }
}
