package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class mR implements mT {
   private static int a;
   private static int b;
   private final int c = 2000;
   private final int d = 2;

   public mR() {
      this(2000, 2);
   }

   private mR(int var1, int var2) {
   }

   @Override
   public final kW a(double[][] var1, double[] var2) {
      zN var3 = new zN(var1[0].length);
      return new mQ(var1, var2, this.d, this.c, var3);
   }
}
