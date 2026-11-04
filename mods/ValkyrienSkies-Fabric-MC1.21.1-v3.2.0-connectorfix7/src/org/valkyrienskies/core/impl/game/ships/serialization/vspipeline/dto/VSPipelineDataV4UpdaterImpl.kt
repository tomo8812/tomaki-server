package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import javax.inject.Inject
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class VSPipelineDataV4UpdaterImpl @Inject  public constructor() : VSPipelineDataV4Updater {
   public open fun update(data: VSPipelineDataV4): VSPipelineDataV5 {
      return VSPipelineDataV5(data.chunkAllocator, data.ships, 0, CollectionsKt.emptyList())
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun update(data: Any) {
      this.update(data as VSPipelineDataV4)
   }
}
