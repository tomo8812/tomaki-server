package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class xB extends xF {
   private kX a;

   public xB(wK<wR> var1) {
      super(var1);
   }

   protected final double[][] a(double[] var1) {
      return this.a.a(var1);
   }

   @Override
   public wR c(wO... var1) {
      return super.c(var1);
   }

   @Override
   public void b(wO... var1) {
      super.b(var1);
      wO[] var5;
      int var2 = (var5 = var1).length;

      for (int var3 = 0; var3 < var2; var3++) {
         wO var4;
         if ((var4 = var5[var3]) instanceof xD) {
            xD var6;
            this.a = (var6 = (xD)var4).a;
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
