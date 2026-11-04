package org.valkyrienskies.core.impl.shadow;

public final class mq extends mm {
   private static int a;

   private mq(double var1, double var3, int var5, int var6) {
      super(var1, var3, var5, var6);
      if (var6 > 32) {
         throw new pm(var6, 32, false);
      }
   }

   private mq(int var1, int var2) {
      super(var1, var2);
      if (var2 > 32) {
         throw new pm(var2, 32, false);
      }
   }

   public mq() {
      super(3, 32);
   }

   @Override
   protected final double j() {
      int var1;
      double[] var2 = new double[var1 = this.d() + 1];
      double[] var15 = new double[var1];
      ms var3 = new ms();
      var15[0] = var3.a(this, 0);
      this.g();
      double var5 = var15[0];

      while (true) {
         int var4 = this.f();
         double[] var7 = var2;
         var2 = var15;
         var15 = var7;
         var7[0] = var3.a(this, var4);
         this.g();

         for (int var9 = 1; var9 <= var4; var9++) {
            double var10 = (1L << 2 * var9) - 1L;
            double var12 = var15[var9 - 1];
            var15[var9] = var12 + (var12 - var2[var9 - 1]) / var10;
         }

         double var16 = var15[var4];
         if (var4 >= this.c()) {
            double var11 = BY.w(var16 - var5);
            double var13 = this.a() * (BY.w(var5) + BY.w(var16)) * 0.5;
            if (var11 <= var13 || var11 <= this.b()) {
               return var16;
            }
         }

         var5 = var16;
      }
   }
}
