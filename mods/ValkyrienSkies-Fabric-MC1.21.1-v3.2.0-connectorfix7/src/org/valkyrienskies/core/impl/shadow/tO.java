package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;

public final class tO implements Serializable, tL {
   private static final long a = 3946024775784901369L;
   private final double[] b;

   public tO(double[] var1) {
      this.b = var1;
   }

   private tO(int[] var1) {
      this.b = new double[var1.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         this.b[var2] = var1[var2];
      }
   }

   @Override
   public final double[] a() {
      return this.b;
   }

   @Override
   public final boolean equals(Object other) {
      return !(other instanceof tO) ? false : Arrays.equals(this.b, ((tO)other).b);
   }

   @Override
   public final int hashCode() {
      return Arrays.hashCode(this.b);
   }

   @Override
   public final String toString() {
      return Arrays.toString(this.b);
   }
}
