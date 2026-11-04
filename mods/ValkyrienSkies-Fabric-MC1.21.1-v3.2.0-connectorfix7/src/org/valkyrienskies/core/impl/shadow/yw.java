package org.valkyrienskies.core.impl.shadow;

@Deprecated
public final class yw implements yo {
   private final tq a;

   public yw(double[] var1) {
      this.a = new sM(var1);
   }

   private yw(tq var1) {
      if (var1.g() != var1.f()) {
         throw new th(var1.g(), var1.f());
      }

      this.a = var1.i();
   }

   public final tq a() {
      return this.a.i();
   }
}
