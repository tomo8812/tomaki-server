package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.internal.VsiCore;

public final class Dp implements ek<Do> {
   private final Provider<VsiCore> a;

   private Dp(Provider<VsiCore> var1) {
      this.a = var1;
   }

   private Do a() {
      dT var1 = ej.a(this.a);
      return new Do(var1);
   }

   public static Dp a(Provider<VsiCore> var0) {
      return new Dp(var0);
   }

   private static Do a(dT<VsiCore> var0) {
      return new Do(var0);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      dT var1 = ej.a(this.a);
      return new Do(var1);
   }
}
