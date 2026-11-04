package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.ServerShipDataConverter;
import org.valkyrienskies.core.impl.shadow.ek;

public final class VSPipelineDataConverterImpl_Factory implements ek<VSPipelineDataConverterImpl> {
   private final Provider<ServerShipDataConverter> converterProvider;

   public VSPipelineDataConverterImpl_Factory(Provider<ServerShipDataConverter> converterProvider) {
      this.converterProvider = converterProvider;
   }

   public final VSPipelineDataConverterImpl get() {
      return newInstance(this.converterProvider.get());
   }

   public static VSPipelineDataConverterImpl_Factory create(Provider<ServerShipDataConverter> converterProvider) {
      return new VSPipelineDataConverterImpl_Factory(converterProvider);
   }

   public static VSPipelineDataConverterImpl newInstance(ServerShipDataConverter converter) {
      return new VSPipelineDataConverterImpl(converter);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
