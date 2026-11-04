package org.valkyrienskies.core.impl.shadow;

public final class mW implements mI {
   private static mV b(double[] var0, double[] var1, double[][] var2) {
      if (var0 == null || var1 == null || var2 == null || var2[0] == null) {
         throw new pl();
      } else if (var0.length != 0 && var1.length != 0 && var2.length != 0) {
         Ch.a(var0);
         Ch.a(var1);
         return new mV(var0, var1, var2);
      } else {
         throw new pf();
      }
   }

   // $VF: synthetic method
   @Override
   public final kP a(double[] var1, double[] var2, double[][] var3) {
      var3 = var3;
      var2 = var2;
      var1 = var1;
      if (var1 == null || var2 == null || var3 == null || var3[0] == null) {
         throw new pl();
      } else if (var1.length != 0 && var2.length != 0 && var3.length != 0) {
         Ch.a(var1);
         Ch.a(var2);
         return new mV(var1, var2, var3);
      } else {
         throw new pf();
      }
   }
}
