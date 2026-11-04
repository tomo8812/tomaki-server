package org.valkyrienskies.core.impl.shadow;

public final class te extends pn {
   private static final long c = 1641613838113738061L;
   private final int d;
   private final double e;

   public te(double var1, int var3, double var4) {
      super(var1, var4, false);
      this.d = var3;
      this.e = var4;
      pu var6;
      (var6 = this.a()).a(px.NOT_POSITIVE_DEFINITE_MATRIX);
      var6.a(px.ARRAY_ELEMENT, var1, var3);
   }

   private int b() {
      return this.d;
   }

   private int c() {
      return this.d;
   }

   private double d() {
      return this.e;
   }
}
