package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.serialization.ChainUpdater;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.ServerShipDataConverter;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV0Updater;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV5;
import org.valkyrienskies.core.impl.shadow.ek;

public final class VSPipelineSerializer_Factory implements ek<VSPipelineSerializer> {
   private final Provider<ObjectMapper> objectMapperProvider;
   private final Provider<ServerShipDataConverter> shipDataMapperProvider;
   private final Provider<ChainUpdater<VSPipelineDataV5>> updaterProvider;
   private final Provider<ServerShipDataV0Updater> v0UpdaterProvider;
   private final Provider<VSPipelineDataConverter> pipelineDataConverterProvider;

   public VSPipelineSerializer_Factory(
      Provider<ObjectMapper> objectMapperProvider,
      Provider<ServerShipDataConverter> shipDataMapperProvider,
      Provider<ChainUpdater<VSPipelineDataV5>> updaterProvider,
      Provider<ServerShipDataV0Updater> v0UpdaterProvider,
      Provider<VSPipelineDataConverter> pipelineDataConverterProvider
   ) {
      this.objectMapperProvider = objectMapperProvider;
      this.shipDataMapperProvider = shipDataMapperProvider;
      this.updaterProvider = updaterProvider;
      this.v0UpdaterProvider = v0UpdaterProvider;
      this.pipelineDataConverterProvider = pipelineDataConverterProvider;
   }

   public final VSPipelineSerializer get() {
      return newInstance(
         this.objectMapperProvider.get(),
         this.shipDataMapperProvider.get(),
         this.updaterProvider.get(),
         this.v0UpdaterProvider.get(),
         this.pipelineDataConverterProvider.get()
      );
   }

   public static VSPipelineSerializer_Factory create(
      Provider<ObjectMapper> objectMapperProvider,
      Provider<ServerShipDataConverter> shipDataMapperProvider,
      Provider<ChainUpdater<VSPipelineDataV5>> updaterProvider,
      Provider<ServerShipDataV0Updater> v0UpdaterProvider,
      Provider<VSPipelineDataConverter> pipelineDataConverterProvider
   ) {
      return new VSPipelineSerializer_Factory(objectMapperProvider, shipDataMapperProvider, updaterProvider, v0UpdaterProvider, pipelineDataConverterProvider);
   }

   public static VSPipelineSerializer newInstance(
      ObjectMapper objectMapper,
      ServerShipDataConverter shipDataMapper,
      ChainUpdater<VSPipelineDataV5> updater,
      ServerShipDataV0Updater v0Updater,
      VSPipelineDataConverter pipelineDataConverter
   ) {
      return new VSPipelineSerializer(objectMapper, shipDataMapper, updater, v0Updater, pipelineDataConverter);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
