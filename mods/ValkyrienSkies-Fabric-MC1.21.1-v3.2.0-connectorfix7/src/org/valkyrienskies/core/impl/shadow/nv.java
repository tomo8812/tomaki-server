package org.valkyrienskies.core.impl.shadow;

public final class nv extends nq {
   private static final double d = 1.0E-6;

   public nv() {
      this(1.0E-6);
   }

   private nv(double var1) {
      super(1.0E-6);
   }

   private nv(double var1, double var3) {
      super(var1, var3);
   }

   @Override
   protected final double j() {
      double var1 = this.d();
      double var3 = this.e();
      double var15 = var3;
      nP.b(var1, var15);
      double var5 = this.g();

      do {
         var15 = var3;
         double var7 = (var1 + var15) * 0.5;
         double var11 = this.b(var1);
         if (this.b(var7) * var11 > 0.0) {
            var1 = var7;
         } else {
            var3 = var7;
         }
      } while (!(BY.w(var3 - var1) <= var5));

      var15 = var3;
      return (var1 + var15) * 0.5;
   }
}
