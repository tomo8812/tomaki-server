package org.valkyrienskies.core.impl.shadow;

public final class wV extends wF<wR> {
   private static final int a = -1;
   private final int b;

   public wV(double var1, double var3) {
      super(var1, var3);
      this.b = -1;
   }

   private wV(double var1, double var3, int var5) {
      super(var1, var3);
      if (var5 <= 0) {
         throw new pk(var5);
      }

      this.b = var5;
   }

   private boolean a(int var1, wR var2, wR var3) {
      if (this.b != -1 && var1 >= this.b) {
         return true;
      }

      double[] var15 = var2.b();
      double[] var16 = var3.b();

      for (int var17 = 0; var17 < var15.length; var17++) {
         double var7 = var15[var17];
         double var9 = var16[var17];
         double var11 = BY.w(var7 - var9);
         double var13 = BY.e(BY.w(var7), BY.w(var9));
         if (var11 > var13 * this.a() && var11 > this.b()) {
            return false;
         }
      }

      return true;
   }

   // $VF: synthetic method
   @Override
   public final boolean a(int var1, Object var2, Object var3) {
      wR var10002 = (wR)var2;
      wR var4 = var3;
      var3 = var10002;
      int var20 = var1;
      wV var19 = this;
      if (this.b == -1 || var20 < var19.b) {
         double[] var21 = var3.b();
         double[] var23 = var4.b();

         for (int var24 = 0; var24 < var21.length; var24++) {
            double var11 = var21[var24];
            double var13 = var23[var24];
            double var15 = BY.w(var11 - var13);
            double var17 = BY.e(BY.w(var11), BY.w(var13));
            if (var15 > var17 * var19.a() && var15 > var19.b()) {
               return false;
            }
         }
      }

      return true;
   }
}
