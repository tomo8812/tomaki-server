package org.valkyrienskies.core.impl.shadow;

public final class wU extends wF<wQ> {
   private static final int a = -1;
   private final int b;

   public wU(double var1, double var3) {
      super(var1, var3);
      this.b = -1;
   }

   private wU(double var1, double var3, int var5) {
      super(var1, var3);
      if (var5 <= 0) {
         throw new pk(var5);
      }

      this.b = var5;
   }

   private boolean a(int var1, wQ var2, wQ var3) {
      if (this.b != -1 && var1 >= this.b) {
         return true;
      }

      double var4 = (Double)var2.c();
      double var6 = (Double)var3.c();
      double var8 = BY.w(var4 - var6);
      double var10 = BY.e(BY.w(var4), BY.w(var6));
      return var8 <= var10 * this.a() || var8 <= this.b();
   }

   // $VF: synthetic method
   @Override
   public final boolean a(int var1, Object var2, Object var3) {
      wQ var10002 = (wQ)var2;
      wQ var4 = var3;
      var3 = var10002;
      int var17 = var1;
      wU var16 = this;
      if (this.b != -1 && var17 >= var16.b) {
         return true;
      }

      double var8 = (Double)var3.c();
      double var10 = (Double)var4.c();
      double var12 = BY.w(var8 - var10);
      double var14 = BY.e(BY.w(var8), BY.w(var10));
      return var12 <= var14 * var16.a() || var12 <= var16.b();
   }
}
