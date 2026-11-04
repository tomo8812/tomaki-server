package org.valkyrienskies.core.impl.shadow;

public class mv {
   private final double[] a;
   private final double[] b;

   public mv(double[] var1, double[] var2) {
      if (var1.length != var2.length) {
         throw new oS(var1.length, var2.length);
      }

      Ch.a(var1, Ch.b.INCREASING$37efe540, true, true);
      this.a = (double[])var1.clone();
      this.b = (double[])var2.clone();
   }

   public mv(Co<double[], double[]> var1) {
      this((double[])var1.a, (double[])var1.b);
   }

   public double a(<unrepresentable> var1) {
      double var2 = 0.0;
      double var4 = 0.0;

      for (int var6 = 0; var6 < this.a.length; var6++) {
         double var7 = this.a[var6];
         double var9;
         double var11 = (var9 = this.b[var6]) * var1.a(var7) - var4;
         double var13;
         var4 = (var13 = var2 + var11) - var2 - var11;
         var2 = var13;
      }

      return var2;
   }

   public final int a() {
      return this.a.length;
   }

   public final double a(int var1) {
      return this.a[var1];
   }

   public final double b(int var1) {
      return this.b[var1];
   }
}
