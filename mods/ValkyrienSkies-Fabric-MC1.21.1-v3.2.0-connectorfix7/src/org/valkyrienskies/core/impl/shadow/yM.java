package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class yM extends yJ<ni.a> {
   @Deprecated
   private final int b;

   @Deprecated
   private yM(int var1, ye var2) {
      super(var2);
      this.b = var1;
   }

   private yM(ye var1) {
      super(var1);
      this.b = -1;
   }

   @Deprecated
   private double[] b() {
      return this.a(new ni.a(), new double[this.b + 1]);
   }

   private double[] a(int var1, double[] var2) {
      return this.a(var1, new ni.a(), var2);
   }

   private double[] a(double[] var1) {
      return this.a(new ni.a(), var1);
   }
}
