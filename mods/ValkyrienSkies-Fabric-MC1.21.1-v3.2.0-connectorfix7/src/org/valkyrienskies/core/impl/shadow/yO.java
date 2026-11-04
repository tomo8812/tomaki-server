package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class yO extends yz<lk> {
   private li b;

   private yO(ya<yp> var1) {
      super(var1);
   }

   private double[] b(double[] var1) {
      return this.b.a(var1);
   }

   @Deprecated
   private yp a(int var1, lk var2, yf var3, double[] var4) {
      return this.a(var1, var2, var3, new yg(var4));
   }

   private yp a(int var1, lk var2, yf var3, yo... var4) {
      this.b = new li(var2);
      return super.a(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final yp a(int var1, kW var2, yf var3, yo[] var4) {
      return this.a(var1, (lk)var2, var3, var4);
   }

   // $VF: synthetic method
   @Deprecated
   @Override
   public final yp a(int var1, kQ var2, yf var3, double[] var4) {
      lk var10002 = (lk)var2;
      double[] var5 = var4;
      yf var8 = var3;
      lk var7 = var10002;
      int var6 = var1;
      return this.a(var6, var7, var8, new yg(var5));
   }
}
