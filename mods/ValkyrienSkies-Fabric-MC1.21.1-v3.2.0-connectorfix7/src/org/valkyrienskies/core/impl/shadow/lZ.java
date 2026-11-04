package org.valkyrienskies.core.impl.shadow;

public final class lZ implements kS, ln {
   private final double a;

   private lZ(double var1) {
      this.a = var1;
   }

   @Override
   public final double a(double var1) {
      return BY.a(var1, this.a);
   }

   @Deprecated
   @Override
   public final lc a() {
      return kV.a(this).a();
   }

   @Override
   public final lg a(lg var1) {
      return var1.k(this.a);
   }
}
