package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class Cr implements Serializable, Cp {
   private static final long a = 20140713L;
   private final zF b;

   private Cr(zF var1) {
      this.b = var1;
   }

   @Override
   public final int a(double[] var1, int var2, int var3) {
      int var5 = var3 - var2;
      int var4 = var2;
      Ch.a(var1, var4, var5, false);
      return var2 + this.b.nextInt(var3 - var2 - 1);
   }
}
