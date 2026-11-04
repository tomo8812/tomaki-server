package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class Cj implements Serializable, Cp {
   private static final long a = 20140713L;

   @Override
   public final int a(double[] var1, int var2, int var3) {
      int var6 = var3 - var2;
      int var5 = var2;
      Ch.a(var1, var5, var6, false);
      var3--;
      int var4 = var2 + (var3 - var2) / 2;
      double var13 = var1[var2];
      double var8 = var1[var4];
      double var10 = var1[var3];
      if (var13 < var8) {
         if (var8 < var10) {
            return var4;
         } else {
            return var13 < var10 ? var3 : var2;
         }
      } else if (var13 < var10) {
         return var2;
      } else {
         return var8 < var10 ? var3 : var4;
      }
   }
}
