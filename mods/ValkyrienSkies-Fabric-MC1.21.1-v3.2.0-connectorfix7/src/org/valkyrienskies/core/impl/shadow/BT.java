package org.valkyrienskies.core.impl.shadow;

public abstract class BT {
   private static final double a = 1.0E-8;

   protected abstract double b(int var1, double var2);

   protected abstract double a(int var1, double var2);

   private double a(double var1) {
      return this.a(var1, 1.0E-8, Integer.MAX_VALUE);
   }

   private double a(double var1, double var3) {
      return this.a(var1, var3, Integer.MAX_VALUE);
   }

   private double a(double var1, int var3) {
      return this.a(var1, 1.0E-8, var3);
   }

   public final double a(double var1, double var3, int var5) {
      double var6;
      if (Cq.b(var6 = this.b(0, var1), 0.0, 1.0E-50)) {
         var6 = 1.0E-50;
      }

      int var8 = 1;
      double var9 = 0.0;
      double var11 = var6;
      double var13 = var6;

      while (var8 < var5) {
         double var15 = this.b(var8, var1);
         double var17 = this.a(var8, var1);
         double var19;
         if (Cq.b(var19 = var15 + var17 * var9, 0.0, 1.0E-50)) {
            var19 = 1.0E-50;
         }

         double var21;
         if (Cq.b(var21 = var15 + var17 / var11, 0.0, 1.0E-50)) {
            var21 = 1.0E-50;
         }

         var19 = 1.0 / var19;
         double var23 = var21 * var19;
         if (Double.isInfinite(var13 = var6 * var23)) {
            throw new oR(px.CONTINUED_FRACTION_INFINITY_DIVERGENCE, var1);
         }

         if (Double.isNaN(var13)) {
            throw new oR(px.CONTINUED_FRACTION_NAN_DIVERGENCE, var1);
         }

         if (BY.w(var23 - 1.0) < var3) {
            break;
         }

         var9 = var19;
         var11 = var21;
         var6 = var13;
         var8++;
      }

      if (var8 >= var5) {
         throw new pc(px.NON_CONVERGENT_CONTINUED_FRACTION, var5, var1);
      } else {
         return var13;
      }
   }
}
