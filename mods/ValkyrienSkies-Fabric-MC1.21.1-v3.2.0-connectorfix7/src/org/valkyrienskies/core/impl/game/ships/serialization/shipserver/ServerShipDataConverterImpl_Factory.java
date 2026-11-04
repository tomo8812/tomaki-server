package org.valkyrienskies.core.impl.game.ships.serialization.shipserver;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.ShipInertiaConverter;
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.ShipTransformConverter;
import org.valkyrienskies.core.impl.shadow.Cy;
import org.valkyrienskies.core.impl.shadow.ek;

public final class ServerShipDataConverterImpl_Factory implements ek<ServerShipDataConverterImpl> {
   private final Provider<ShipInertiaConverter> inertiaConverterProvider;
   private final Provider<ShipTransformConverter> transformConverterProvider;
   private final Provider<Cy> attachmentHolderFactoryProvider;

   public ServerShipDataConverterImpl_Factory(
      Provider<ShipInertiaConverter> inertiaConverterProvider,
      Provider<ShipTransformConverter> transformConverterProvider,
      Provider<Cy> attachmentHolderFactoryProvider
   ) {
      this.inertiaConverterProvider = inertiaConverterProvider;
      this.transformConverterProvider = transformConverterProvider;
      this.attachmentHolderFactoryProvider = attachmentHolderFactoryProvider;
   }

   public final ServerShipDataConverterImpl get() {
      return newInstance(this.inertiaConverterProvider.get(), this.transformConverterProvider.get(), this.attachmentHolderFactoryProvider.get());
   }

   public static ServerShipDataConverterImpl_Factory create(
      Provider<ShipInertiaConverter> inertiaConverterProvider,
      Provider<ShipTransformConverter> transformConverterProvider,
      Provider<Cy> attachmentHolderFactoryProvider
   ) {
      return new ServerShipDataConverterImpl_Factory(inertiaConverterProvider, transformConverterProvider, attachmentHolderFactoryProvider);
   }

   public static ServerShipDataConverterImpl newInstance(
      ShipInertiaConverter inertiaConverter, ShipTransformConverter transformConverter, Cy attachmentHolderFactory
   ) {
      return new ServerShipDataConverterImpl(inertiaConverter, transformConverter, attachmentHolderFactory);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
