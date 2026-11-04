package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;

public final class Fv implements ek<Fu> {
   private final Provider<Fb> a;

   private Fv(Provider<Fb> var1) {
      this.a = var1;
   }

   private Fu a() {
      dT var1 = ej.a(this.a);
      return new Fu(var1);
   }

   public static Fv a(Provider<Fb> var0) {
      return new Fv(var0);
   }

   private static Fu a(dT<Fb> var0) {
      return new Fu(var0);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      dT var1 = ej.a(this.a);
      return new Fu(var1);
   }
}
