package org.valkyrienskies.core.impl.shadow;

public final class qY extends rL<qU, qU> {
   public qY(rV<qU> var1, rY<qU> var2) {
      super(var1, var2);
   }

   @Override
   public final double a() {
      return 0.0;
   }

   @Override
   public final boolean b() {
      return false;
   }

   @Override
   public final rL<qU, qU> a(rV<qU> var1, rY<qU> var2) {
      return new qY(var1, var2);
   }

   @Override
   public final sb.a<qU> a(rV<qU> var1) {
      double var2;
      if ((var2 = var1.a(((qX)this.c()).a)) < -1.0E-10) {
         return new sb.a<>(null, this);
      } else {
         return var2 > 1.0E-10 ? new sb.a<>(this, null) : new sb.a<>(null, null);
      }
   }
}
