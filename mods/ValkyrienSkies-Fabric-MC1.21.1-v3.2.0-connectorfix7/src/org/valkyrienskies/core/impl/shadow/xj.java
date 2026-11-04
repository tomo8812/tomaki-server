package org.valkyrienskies.core.impl.shadow;

public abstract class xj extends xp {
   private kY b;

   public xj(wK<wQ> var1) {
      super(var1);
   }

   protected final double[] a(double[] var1) {
      return this.b.a(var1);
   }

   @Override
   public wQ c(wO... var1) {
      return super.c(var1);
   }

   @Override
   public void b(wO... var1) {
      super.b(var1);
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof xr) {
            xr var6;
            this.b = (var6 = (xr)var4).a;
            return;
         }
      }
   }

   // $VF: synthetic method
   @Override
   public Object a(wO[] var1) {
      return this.c(var1);
   }
}
