package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;

abstract class rD implements rG {
   private static final double a = 1.0E-10;
   private final double b;
   private final boolean c;

   protected rD(boolean var1) {
      this(var1, 1.0E-10);
   }

   protected rD(boolean var1, double var2) {
      this.c = var1;
      this.b = var2;
   }

   public double a() {
      return this.b;
   }

   public boolean b() {
      return this.c;
   }

   @Override
   public rF a(Collection<rB> var1) {
      Ci.a(var1);
      if (var1.size() < 2) {
         var1 = var1;
      } else {
         var1 = this.b(var1);
      }

      try {
         return new rF(var1.toArray(new rB[var1.size()]), this.b);
      } catch (oV var2) {
         throw new oR();
      }
   }

   protected abstract Collection<rB> b(Collection<rB> var1);

   // $VF: synthetic method
   @Override
   public final rF c(Collection var1) {
      return this.a(var1);
   }
}
