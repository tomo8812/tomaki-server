package org.valkyrienskies.core.impl.shadow;

public final class lE implements kS, ln {
   private final double a;

   public lE(double var1) {
      this.a = var1;
   }

   @Override
   public final double a(double var1) {
      return this.a;
   }

   @Deprecated
   private static lE b() {
      return new lE(0.0);
   }

   @Override
   public final lg a(lg var1) {
      return new lg(var1.C(), var1.D(), this.a);
   }

   // $VF: synthetic method
   @Deprecated
   @Override
   public final lc a() {
      return new lE(0.0);
   }
}
