package org.valkyrienskies.core.impl.shadow;

public final class mN extends mM {
   private static final int h = 2;

   private mN(int var1, double var2, double var4, double var6) {
      super(2, var1, var2, var4, var6);

      for (int var11 = 0; var11 < var1; var11++) {
         double var9 = var11 * (Math.PI * 2) / var1;
         this.a(new double[]{BY.o(var9), BY.n(var9)}, false);
      }
   }

   private mN(mN var1) {
      super(var1);
   }

   private mN c() {
      return new mN(this);
   }

   // $VF: synthetic method
   @Override
   public final mM a() {
      mN var1 = this;
      return new mN(var1);
   }
}
