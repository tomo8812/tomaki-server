package org.valkyrienskies.core.impl.program;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.shadow.ek;

public final class VSCoreServerImpl_Factory implements ek<VSCoreServerImpl> {
   private final Provider<VSCoreImpl> baseProvider;

   public VSCoreServerImpl_Factory(Provider<VSCoreImpl> baseProvider) {
      this.baseProvider = baseProvider;
   }

   public final VSCoreServerImpl get() {
      return newInstance(this.baseProvider.get());
   }

   public static VSCoreServerImpl_Factory create(Provider<VSCoreImpl> baseProvider) {
      return new VSCoreServerImpl_Factory(baseProvider);
   }

   public static VSCoreServerImpl newInstance(VSCoreImpl base) {
      return new VSCoreServerImpl(base);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
