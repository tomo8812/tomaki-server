package org.valkyrienskies.core.impl.shadow;

public final class ub {
   private ub() {
   }

   private static ua a(zF var0, double var1, double var3) {
      return a(new oN(var0, var1, var3), a(new lE(0.0), 0.0, 0.0));
   }

   private static ua a(double var0, double var2) {
      return a(new oN(var0, var2), a(new lE(0.0), 0.0, 0.0));
   }

   private static <unrepresentable> a(final lE var0, double var1, double var3) {
      return new ua(0.0, 0.0) {
         private double a;
         // $VF: synthetic field
         private double b;
         // $VF: synthetic field
         private double d;

         {
            this.b = var1;
            this.d = var4;
            this.a = this.b;
         }

         @Override
         public final double a() {
            double var1x = var0.a(this.a);
            this.a = this.a + this.d;
            return var1x;
         }
      };
   }

   private static <unrepresentable> a(final oN var0, final ua var1) {
      return new ua() {
         @Override
         public final double a() {
            return var1.a() + var0.b();
         }
      };
   }
}
