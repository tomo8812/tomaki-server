package org.valkyrienskies.core.impl.shadow;

public final class wM implements wO {
   final int a;

   public wM(int var1) {
      if (var1 <= 0) {
         throw new pk(var1);
      }

      this.a = var1;
   }

   public final int a() {
      return this.a;
   }

   private static wM b() {
      return new wM(Integer.MAX_VALUE);
   }
}
