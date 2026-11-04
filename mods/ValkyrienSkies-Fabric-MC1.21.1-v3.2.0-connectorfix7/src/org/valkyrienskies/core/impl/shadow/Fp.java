package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.internal.hooks.VsiCoreHooks;

public final class Fp implements ek<Fb> {
   private final Provider<VsiCoreHooks> a;

   private Fp(Provider<VsiCoreHooks> var1) {
      this.a = var1;
   }

   private Fb a() {
      Provider var1 = this.a;
      return ev.b(Fl.a.a.b(var1));
   }

   public static Fp a(Provider<VsiCoreHooks> var0) {
      return new Fp(var0);
   }

   private static Fb a(Provider<VsiCoreHooks> var0) {
      return ev.b(Fl.a.a.b(var0));
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      Provider var1 = this.a;
      return ev.b(Fl.a.a.b(var1));
   }
}
