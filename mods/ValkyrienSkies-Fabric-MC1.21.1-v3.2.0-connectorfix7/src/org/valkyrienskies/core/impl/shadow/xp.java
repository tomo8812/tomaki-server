package org.valkyrienskies.core.impl.shadow;

public abstract class xp extends wI<wQ> {
   private kW b;
   xi a;

   public xp(wK<wQ> var1) {
      super(var1);
   }

   public wQ c(wO... var1) {
      return (wQ)super.a(var1);
   }

   @Override
   public void b(wO... var1) {
      super.b(var1);
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof xi) {
            this.a = (xi)var4;
         } else if (var4 instanceof xq) {
            xq var6;
            this.b = (var6 = (xq)var4).a;
         }
      }
   }

   public final xi n() {
      return this.a;
   }

   public final double b(double[] var1) {
      super.k();
      return this.b.a(var1);
   }

   // $VF: synthetic method
   @Override
   public Object a(wO[] var1) {
      return this.c(var1);
   }
}
