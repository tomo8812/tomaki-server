package org.valkyrienskies.core.impl.shadow;

public abstract class np extends ns<ln> implements nN {
   private ln d;

   protected np(double var1) {
      super(var1);
   }

   private np(double var1, double var3, double var5) {
      super(var1, var3, var5);
   }

   protected final lg a(double var1) {
      this.k();
      return this.d.a(new lg(1, 1, 0, var1));
   }

   private void a(int var1, ln var2, double var3, double var5, double var7) {
      super.a(var1, var2, var3, var5, var7);
      this.d = var2;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final void a(int var1, lc var2, double var3, double var5, double var7) {
      ln var10002 = (ln)var2;
      double var16 = var7;
      double var14 = var5;
      double var12 = var3;
      ln var20 = var10002;
      int var19 = var1;
      np var18 = this;
      super.a(var19, var20, var12, var14, var16);
      var18.d = var20;
   }
}
