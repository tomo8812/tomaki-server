package org.valkyrienskies.core.impl.shadow;

public final class zM implements zA {
   private static final double a = BY.a(3.0);
   private final zF b;

   private zM(zF var1) {
      this.b = var1;
   }

   @Override
   public final double a() {
      return a * (2.0 * this.b.nextDouble() - 1.0);
   }
}
