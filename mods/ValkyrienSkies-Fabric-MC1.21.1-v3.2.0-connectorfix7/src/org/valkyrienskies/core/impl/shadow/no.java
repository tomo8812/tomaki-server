package org.valkyrienskies.core.impl.shadow;

public abstract class no extends ns<ni> implements nJ {
   private ni d;

   protected no(double var1) {
      super(var1);
   }

   protected no(double var1, double var3) {
      super(var1, var3);
   }

   protected no(double var1, double var3, double var5) {
      super(var1, var3, var5);
   }

   protected final void a(int var1, ni var2, double var3, double var5, double var7) {
      super.a(var1, var2, var3, var5, var7);
      this.d = var2;
   }

   protected final double[] a() {
      return this.d.b();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected final void a(int var1, lc var2, double var3, double var5, double var7) {
      this.a(var1, (ni)var2, var3, var5, var7);
   }
}
