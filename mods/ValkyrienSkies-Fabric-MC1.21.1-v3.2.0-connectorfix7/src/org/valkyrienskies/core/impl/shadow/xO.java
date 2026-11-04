package org.valkyrienskies.core.impl.shadow;

public final class xO implements wO {
   final double a;
   final double b;
   final double c;

   public xO(double var1, double var3, double var5) {
      if (var1 >= var3) {
         throw new pm(var1, var3, false);
      }

      if (!(var5 < var1) && !(var5 > var3)) {
         this.a = var1;
         this.b = var3;
         this.c = var5;
      } else {
         throw new po(var5, var1, var3);
      }
   }

   private xO(double var1, double var3) {
      this(var1, var3, 0.5 * (var1 + var3));
   }

   public final double a() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   public final double c() {
      return this.c;
   }
}
