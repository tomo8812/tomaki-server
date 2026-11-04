package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3Updater;
import org.valkyrienskies.core.impl.shadow.ek;

public final class VSPipelineDataV3UpdaterImpl_Factory implements ek<VSPipelineDataV3UpdaterImpl> {
   private final Provider<ServerShipDataV3Updater> updaterProvider;

   public VSPipelineDataV3UpdaterImpl_Factory(Provider<ServerShipDataV3Updater> updaterProvider) {
      this.updaterProvider = updaterProvider;
   }

   public final VSPipelineDataV3UpdaterImpl get() {
      return newInstance(this.updaterProvider.get());
   }

   public static VSPipelineDataV3UpdaterImpl_Factory create(Provider<ServerShipDataV3Updater> updaterProvider) {
      return new VSPipelineDataV3UpdaterImpl_Factory(updaterProvider);
   }

   public static VSPipelineDataV3UpdaterImpl newInstance(ServerShipDataV3Updater updater) {
      return new VSPipelineDataV3UpdaterImpl(updater);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
