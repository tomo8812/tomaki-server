package org.valkyrienskies.core.impl.shadow;

public final class Bl {
   private double a;
   private double b;
   private double c;

   public Bl(double var1, double var3, double var5) {
      double var11 = var5;
      double var9 = var3;
      double var7 = var1;
      if (var1 >= var9) {
         throw new oV(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var7, var9);
      }

      if (!(var11 <= 0.0) && !(var11 >= 1.0)) {
         this.a = var1;
         this.b = var3;
         this.c = var5;
      } else {
         throw new oV(px.OUT_OF_BOUNDS_CONFIDENCE_LEVEL, var11, 0, 1);
      }
   }

   private double a() {
      return this.a;
   }

   private double b() {
      return this.b;
   }

   private double c() {
      return this.c;
   }

   @Override
   public final String toString() {
      return "[" + this.a + ";" + this.b + "] (confidence level:" + this.c + ")";
   }

   private static void a(double var0, double var2, double var4) {
      if (var0 >= var2) {
         throw new oV(px.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, var0, var2);
      } else if (var4 <= 0.0 || var4 >= 1.0) {
         throw new oV(px.OUT_OF_BOUNDS_CONFIDENCE_LEVEL, var4, 0, 1);
      }
   }
}
