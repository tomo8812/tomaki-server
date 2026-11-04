package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class nn extends ns<kS> implements nA {
   private lc d;

   protected nn(double var1) {
      super(var1);
   }

   private nn(double var1, double var3, double var5) {
      super(var1, var3, var5);
   }

   protected final double a(double var1) {
      this.k();
      return this.d.a(var1);
   }

   private void a(int var1, kS var2, double var3, double var5, double var7) {
      super.a(var1, var2, var3, var5, var7);
      this.d = var2.a();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final void a(int var1, lc var2, double var3, double var5, double var7) {
      kS var10002 = (kS)var2;
      double var16 = var7;
      double var14 = var5;
      double var12 = var3;
      kS var20 = var10002;
      int var19 = var1;
      nn var18 = this;
      super.a(var19, var20, var12, var14, var16);
      var18.d = var20.a();
   }
}
