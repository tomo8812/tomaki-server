package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class nH extends nn {
   private static final double d = 1.0E-6;

   public nH() {
      this(1.0E-6);
   }

   private nH(double var1) {
      super(1.0E-6);
   }

   private double a(int var1, kS var2, double var3, double var5) {
      double var9 = var5;
      return super.a(var1, var2, (var3 + var9) * 0.5);
   }

   @Override
   protected final double j() {
      double var1 = this.f();
      double var3 = this.g();
      double var5 = var1;

      double var7;
      while (!(BY.w((var7 = var5 - this.b(var5) / this.a(var5)) - var5) <= var3)) {
         var5 = var7;
      }

      return var7;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final double a(int var1, lc var2, double var3, double var5) {
      kS var10002 = (kS)var2;
      double var12 = var5;
      double var10 = var3;
      kS var19 = var10002;
      int var18 = var1;
      double var16 = var12;
      return super.a(var18, var19, (var10 + var16) * 0.5);
   }
}
