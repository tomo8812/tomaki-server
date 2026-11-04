package org.valkyrienskies.core.impl.shadow;

public final class qw implements qJ {
   private int a = 0;
   private final int b;

   private qw(int var1) {
      if (var1 <= 0) {
         throw new pn(var1, 1, true);
      }

      this.b = var1;
   }

   @Override
   public final boolean a() {
      if (this.a < this.b) {
         this.a++;
         return false;
      } else {
         return true;
      }
   }

   private int b() {
      return this.a;
   }
}
