package org.valkyrienskies.core.impl.shadow;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.config.VSCoreConfig;

public final class Fz implements ek<Fy> {
   private final Provider<VSCoreConfig.Server> a;

   private Fz(Provider<VSCoreConfig.Server> var1) {
      this.a = var1;
   }

   private Fy a() {
      VSCoreConfig.Server var1 = this.a.get();
      return new Fy(var1);
   }

   public static Fz a(Provider<VSCoreConfig.Server> var0) {
      return new Fz(var0);
   }

   private static Fy a(VSCoreConfig.Server var0) {
      return new Fy(var0);
   }

   // $VF: synthetic method
   @Override
   public final Object get() {
      VSCoreConfig.Server var1 = this.a.get();
      return new Fy(var1);
   }
}
