package org.valkyrienskies.core.impl.shadow;

public final class xk implements kW {
   private final kY a;
   private final double[] b;
   private final double[] c;
   private final tq d;

   private xk(kY var1, double[] var2) {
      this.a = var1;
      this.b = (double[])var2.clone();
      this.c = null;
      this.d = null;
   }

   private xk(kY var1, double[] var2, double[] var3) {
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      this.a = var1;
      this.b = (double[])var2.clone();
      this.c = (double[])var3.clone();
      this.d = null;
   }

   private xk(kY var1, double[] var2, tq var3) {
      if (var2.length != var3.g()) {
         throw new oS(var2.length, var3.g());
      }

      this.a = var1;
      this.b = (double[])var2.clone();
      this.c = null;
      this.d = var3.i();
   }

   @Override
   public final double a(double[] var1) {
      if ((var1 = this.a.a(var1)).length != this.b.length) {
         throw new oS(var1.length, this.b.length);
      }

      for (int var3 = 0; var3 < var1.length; var3++) {
         var1[var3] -= this.b[var3];
      }

      double var13 = 0.0;
      if (this.c != null) {
         for (int var2 = 0; var2 < var1.length; var2++) {
            double var6 = var1[var2];
            var13 += this.c[var2] * var6 * var6;
         }
      } else if (this.d != null) {
         double[] var11;
         int var14 = (var11 = this.d.a(var1)).length;

         for (int var7 = 0; var7 < var14; var7++) {
            double var8 = var11[var7];
            var13 += var8 * var8;
         }
      } else {
         double[] var12 = var1;
         int var15 = var1.length;

         for (int var16 = 0; var16 < var15; var16++) {
            double var17 = var12[var16];
            var13 += var17 * var17;
         }
      }

      return var13;
   }
}
