package org.valkyrienskies.core.impl.shadow;

public final class mA extends mv {
   private mA(double[] var1, double[] var2) {
      super(var1, var2);
   }

   public mA(Co<double[], double[]> var1) {
      this((double[])var1.a, (double[])var1.b);
   }

   @Override
   public final double a(<unrepresentable> var1) {
      int var2;
      if ((var2 = this.a()) == 1) {
         return this.b(0) * var1.a(0.0);
      }

      int var3 = var2 / 2;
      double var4 = 0.0;
      double var6 = 0.0;

      for (int var8 = 0; var8 < var3; var8++) {
         double var9 = this.a(var8);
         double var11 = this.b(var8);
         double var13 = var1.a(var9);
         double var15 = var1.a(-var9);
         double var17 = var11 * (var13 + var15) - var6;
         double var19;
         var6 = (var19 = var4 + var17) - var4 - var17;
         var4 = var19;
      }

      if (var2 % 2 != 0) {
         double var10 = this.b(var3) * var1.a(0.0) - var6;
         double var12;
         var4 = var12 = var4 + var10;
      }

      return var4;
   }
}
