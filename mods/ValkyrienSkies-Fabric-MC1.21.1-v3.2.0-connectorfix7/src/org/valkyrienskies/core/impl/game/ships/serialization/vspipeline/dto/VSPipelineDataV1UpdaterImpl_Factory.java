package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1Updater;
import org.valkyrienskies.core.impl.shadow.ek;

public final class VSPipelineDataV1UpdaterImpl_Factory implements ek<VSPipelineDataV1UpdaterImpl> {
   private final Provider<ServerShipDataV1Updater> updaterProvider;

   public VSPipelineDataV1UpdaterImpl_Factory(Provider<ServerShipDataV1Updater> updaterProvider) {
      this.updaterProvider = updaterProvider;
   }

   public final VSPipelineDataV1UpdaterImpl get() {
      return newInstance(this.updaterProvider.get());
   }

   public static VSPipelineDataV1UpdaterImpl_Factory create(Provider<ServerShipDataV1Updater> updaterProvider) {
      return new VSPipelineDataV1UpdaterImpl_Factory(updaterProvider);
   }

   public static VSPipelineDataV1UpdaterImpl newInstance(ServerShipDataV1Updater updater) {
      return new VSPipelineDataV1UpdaterImpl(updater);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
