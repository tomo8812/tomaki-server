package org.valkyrienskies.core.impl.shadow;

public final class xo implements kW {
   private final kW a;
   private final double[] b;
   private final double[] c;
   private final double d;
   private final double[] e;

   private xo(kW var1, double[] var2, double[] var3, double var4, double[] var6) {
      Ci.a(var2);
      Ci.a(var3);
      Ci.a(var6);
      if (var2.length != var3.length) {
         throw new oS(var2.length, var3.length);
      }

      if (var2.length != var6.length) {
         throw new oS(var2.length, var6.length);
      }

      for (int var7 = 0; var7 < var2.length; var7++) {
         if (!(var3[var7] >= var2[var7])) {
            throw new pn(var3[var7], var2[var7], true);
         }
      }

      this.a = var1;
      this.b = (double[])var2.clone();
      this.c = (double[])var3.clone();
      this.d = var4;
      this.e = (double[])var6.clone();
   }

   @Override
   public final double a(double[] var1) {
      for (int var2 = 0; var2 < this.e.length; var2++) {
         if (var1[var2] < this.b[var2] || var1[var2] > this.c[var2]) {
            double var3 = 0.0;

            for (int var8 = var2; var8 < this.e.length; var8++) {
               double var6;
               if (var1[var8] < this.b[var8]) {
                  var6 = this.e[var8] * (this.b[var8] - var1[var8]);
               } else if (var1[var8] > this.c[var8]) {
                  var6 = this.e[var8] * (var1[var8] - this.c[var8]);
               } else {
                  var6 = 0.0;
               }

               var3 += BY.a(var6);
            }

            return this.d + var3;
         }
      }

      return this.a.a(var1);
   }
}
