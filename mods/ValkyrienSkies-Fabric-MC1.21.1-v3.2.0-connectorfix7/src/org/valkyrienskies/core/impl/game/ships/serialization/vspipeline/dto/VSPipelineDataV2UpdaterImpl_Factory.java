package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2Updater;
import org.valkyrienskies.core.impl.shadow.ek;

public final class VSPipelineDataV2UpdaterImpl_Factory implements ek<VSPipelineDataV2UpdaterImpl> {
   private final Provider<ServerShipDataV2Updater> updaterProvider;

   public VSPipelineDataV2UpdaterImpl_Factory(Provider<ServerShipDataV2Updater> updaterProvider) {
      this.updaterProvider = updaterProvider;
   }

   public final VSPipelineDataV2UpdaterImpl get() {
      return newInstance(this.updaterProvider.get());
   }

   public static VSPipelineDataV2UpdaterImpl_Factory create(Provider<ServerShipDataV2Updater> updaterProvider) {
      return new VSPipelineDataV2UpdaterImpl_Factory(updaterProvider);
   }

   public static VSPipelineDataV2UpdaterImpl newInstance(ServerShipDataV2Updater updater) {
      return new VSPipelineDataV2UpdaterImpl(updater);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
