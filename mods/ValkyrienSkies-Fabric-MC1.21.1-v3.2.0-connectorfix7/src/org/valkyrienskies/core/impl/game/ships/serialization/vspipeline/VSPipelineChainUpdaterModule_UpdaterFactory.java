package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline;

import javax.inject.Provider;
import org.valkyrienskies.core.impl.game.ships.serialization.ChainUpdater;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV1Updater;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV2Updater;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV3Updater;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV4Updater;
import org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto.VSPipelineDataV5;
import org.valkyrienskies.core.impl.shadow.ek;
import org.valkyrienskies.core.impl.shadow.ev;

public final class VSPipelineChainUpdaterModule_UpdaterFactory implements ek<ChainUpdater<VSPipelineDataV5>> {
   private final VSPipelineChainUpdaterModule module;
   private final Provider<VSPipelineDataV1Updater> v1Provider;
   private final Provider<VSPipelineDataV2Updater> v2Provider;
   private final Provider<VSPipelineDataV3Updater> v3Provider;
   private final Provider<VSPipelineDataV4Updater> v4Provider;

   public VSPipelineChainUpdaterModule_UpdaterFactory(
      VSPipelineChainUpdaterModule module,
      Provider<VSPipelineDataV1Updater> v1Provider,
      Provider<VSPipelineDataV2Updater> v2Provider,
      Provider<VSPipelineDataV3Updater> v3Provider,
      Provider<VSPipelineDataV4Updater> v4Provider
   ) {
      this.module = module;
      this.v1Provider = v1Provider;
      this.v2Provider = v2Provider;
      this.v3Provider = v3Provider;
      this.v4Provider = v4Provider;
   }

   public final ChainUpdater<VSPipelineDataV5> get() {
      return updater(this.module, this.v1Provider.get(), this.v2Provider.get(), this.v3Provider.get(), this.v4Provider.get());
   }

   public static VSPipelineChainUpdaterModule_UpdaterFactory create(
      VSPipelineChainUpdaterModule module,
      Provider<VSPipelineDataV1Updater> v1Provider,
      Provider<VSPipelineDataV2Updater> v2Provider,
      Provider<VSPipelineDataV3Updater> v3Provider,
      Provider<VSPipelineDataV4Updater> v4Provider
   ) {
      return new VSPipelineChainUpdaterModule_UpdaterFactory(module, v1Provider, v2Provider, v3Provider, v4Provider);
   }

   public static ChainUpdater<VSPipelineDataV5> updater(
      VSPipelineChainUpdaterModule instance, VSPipelineDataV1Updater v1, VSPipelineDataV2Updater v2, VSPipelineDataV3Updater v3, VSPipelineDataV4Updater v4
   ) {
      return ev.b(instance.updater(v1, v2, v3, v4));
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object get() {
      return this.get();
   }
}
