package org.valkyrienskies.core.impl.shadow;

public final class nh implements ng {
   private static int b;
   private final ng c;
   final double a;
   private final int d;

   private nh(ng var1, double var2, int var4) {
      this.c = var1;
      this.a = var2;
      this.d = 5;
   }

   private nh(ng var1, double var2) {
      this(var1, var2, 5);
   }

   @Override
   public final lc b(double[] var1, double[] var2) {
      if (var1.length < this.d) {
         throw new pn(var1.length, this.d, true);
      }

      Ch.a(var1);
      final double var3 = var1[0];
      int var5;
      double[] var6 = new double[var5 = var1.length + (this.d << 1)];
      double[] var7 = new double[var5];

      for (int var8 = 0; var8 < var1.length; var8++) {
         int var9 = var8 + this.d;
         var6[var9] = Ci.a(var1[var8], this.a, var3);
         var7[var9] = var2[var8];
      }

      for (int var10 = 0; var10 < this.d; var10++) {
         int var12 = var1.length - this.d + var10;
         var6[var10] = Ci.a(var1[var12], this.a, var3) - this.a;
         var7[var10] = var2[var12];
         var12 = var5 - this.d + var10;
         var6[var12] = Ci.a(var1[var10], this.a, var3) + this.a;
         var7[var12] = var2[var10];
      }

      Ch.a(var6, new double[][]{var7});
      final lc var11 = this.c.b(var6, var7);
      return new lc() {
         @Override
         public final double a(double var1) {
            nh var3x = nh.this;
            return var11.a(Ci.a(var1, nh.this.a, var3));
         }
      };
   }

   // $VF: synthetic method
   static double a(nh var0) {
      return var0.a;
   }
}
