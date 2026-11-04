package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class BP implements Serializable, Cp {
   private static final long a = 20140713L;

   @Override
   public final int a(double[] var1, int var2, int var3) {
      int var5 = var3 - var2;
      int var4 = var2;
      Ch.a(var1, var4, var5, false);
      return var2 + (var3 - var2) / 2;
   }
}
