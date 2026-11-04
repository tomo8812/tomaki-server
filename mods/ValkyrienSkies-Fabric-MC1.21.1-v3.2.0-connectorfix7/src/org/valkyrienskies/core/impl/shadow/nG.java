package org.valkyrienskies.core.impl.shadow;

public final class nG extends np {
   private static final double d = 1.0E-6;

   public nG() {
      this(1.0E-6);
   }

   private nG(double var1) {
      super(1.0E-6);
   }

   private double a(int var1, ln var2, double var3, double var5) {
      double var9 = var5;
      return super.a(var1, var2, (var3 + var9) * 0.5);
   }

   @Override
   protected final double j() {
      double var1 = this.f();
      double var3 = this.g();
      double var5 = var1;

      while (true) {
         lg var9 = this.a(var5);
         double var7;
         if (BY.w((var7 = var5 - var9.b[0] / var9.a(1)) - var5) <= var3) {
            return var7;
         }

         var5 = var7;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final double a(int var1, lc var2, double var3, double var5) {
      ln var10002 = (ln)var2;
      double var12 = var5;
      double var10 = var3;
      ln var19 = var10002;
      int var18 = var1;
      double var16 = var12;
      return super.a(var18, var19, (var10 + var16) * 0.5);
   }
}
