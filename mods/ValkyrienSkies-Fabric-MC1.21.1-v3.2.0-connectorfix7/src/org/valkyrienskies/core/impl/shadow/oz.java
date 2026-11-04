package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class oz extends oA<oB> {
   public oz(double[] var1, double[][] var2, double[][][] var3) {
      super(a(var1, var2, var3));
   }

   public oz(List<Co<Double, oB>> var1) {
      super(var1);
   }

   private oz(zF var1, List<Co<Double, oB>> var2) {
      super(var1, var2);
   }

   private static List<Co<Double, oB>> a(double[] var0, double[][] var1, double[][][] var2) {
      ArrayList var3 = new ArrayList(var0.length);

      for (int var4 = 0; var4 < var0.length; var4++) {
         oB var5 = new oB(var1[var4], var2[var4]);
         var3.add(new Co<>(var0[var4], var5));
      }

      return var3;
   }
}
