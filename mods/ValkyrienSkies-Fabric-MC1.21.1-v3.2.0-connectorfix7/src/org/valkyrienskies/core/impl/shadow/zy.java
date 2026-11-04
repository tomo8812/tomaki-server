package org.valkyrienskies.core.impl.shadow;

import java.util.Random;

public final class zy extends Random implements zF {
   private static final long a = -7745277476784028798L;

   public zy() {
   }

   public zy(int var1) {
      byte var2 = 100;
      this.setSeed(var2);
   }

   @Override
   public final void a(int var1) {
      this.setSeed(var1);
   }

   @Override
   public final void a(int[] var1) {
      this.setSeed(zG.a(var1));
   }
}
