package org.valkyrienskies.core.impl.program;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.ShipObjectClientWorldComponent;
import org.valkyrienskies.core.impl.shadow.ek;

public final class VSCoreClientImpl_Factory implements ek<VSCoreClientImpl> {
   private final Provider<VSCoreImpl> baseProvider;
   private final Provider<ShipObjectClientWorldComponent.Factory> shipWorldComponentFactoryProvider;

   public VSCoreClientImpl_Factory(Provider<VSCoreImpl> baseProvider, Provider<ShipObjectClientWorldComponent.Factory> shipWorldComponentFactoryProvider) {
      this.baseProvider = baseProvider;
      this.shipWorldComponentFactoryProvider = shipWorldComponentFactoryProvider;
   }

   public final VSCoreClientImpl get() {
      return newInstance(this.baseProvider.get(), this.shipWorldComponentFactoryProvider.get());
   }

   public static VSCoreClientImpl_Factory create(
      Provider<VSCoreImpl> baseProvider, Provider<ShipObjectClientWorldComponent.Factory> shipWorldComponentFactoryProvider
   ) {
      return new VSCoreClientImpl_Factory(baseProvider, shipWorldComponentFactoryProvider);
   }

   public static VSCoreClientImpl newInstance(VSCoreImpl base, ShipObjectClientWorldComponent.Factory shipWorldComponentFactory) {
      return new VSCoreClientImpl(base, shipWorldComponentFactory);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
