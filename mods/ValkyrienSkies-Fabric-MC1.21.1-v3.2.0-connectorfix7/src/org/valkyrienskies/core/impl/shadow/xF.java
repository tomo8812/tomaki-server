package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class xF extends wI<wR> {
   private double[] a;
   private tq b;
   private kY c;

   protected xF(wK<wR> var1) {
      super(var1);
   }

   protected final double[] b(double[] var1) {
      super.k();
      return this.c.a(var1);
   }

   public wR c(wO... var1) {
      return (wR)super.a(var1);
   }

   public final tq b() {
      return this.b.i();
   }

   public final double[] d() {
      return (double[])this.a.clone();
   }

   public final int m() {
      return this.a.length;
   }

   @Override
   public void b(wO... var1) {
      super.b(var1);
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof xC) {
            xC var6;
            this.c = (var6 = (xC)var4).a;
         } else if (var4 instanceof xG) {
            this.a = ((xG)var4).a();
         } else if (var4 instanceof xH) {
            this.b = ((xH)var4).a();
         }
      }

      xF var7 = this;
      if (this.a.length != var7.b.g()) {
         throw new oS(var7.a.length, var7.b.g());
      }
   }

   private void n() {
      if (this.a.length != this.b.g()) {
         throw new oS(this.a.length, this.b.g());
      }
   }

   // $VF: synthetic method
   @Override
   public Object a(wO[] var1) {
      return this.c(var1);
   }
}
