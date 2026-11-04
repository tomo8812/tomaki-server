package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;

public final class wS implements wO {
   private final double[] a;
   private final double[] b;

   private wS(double[] var1, double[] var2) {
      this.a = (double[])var1.clone();
      this.b = (double[])var2.clone();
   }

   public final double[] a() {
      return (double[])this.a.clone();
   }

   public final double[] b() {
      return (double[])this.b.clone();
   }

   private static wS a(int var0) {
      double[] var1;
      Arrays.fill(var1 = new double[var0], Double.NEGATIVE_INFINITY);
      double[] var2;
      Arrays.fill(var2 = new double[var0], Double.POSITIVE_INFINITY);
      return new wS(var1, var2);
   }
}
