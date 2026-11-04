package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import java.util.ArrayList
import javax.inject.Inject
import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2Updater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class VSPipelineDataV2UpdaterImpl @Inject  public constructor(updater: ServerShipDataV2Updater) : VSPipelineDataV2Updater {
   private final val updater: ServerShipDataV2Updater

   init {
      this.updater = updater
   }

   public open fun update(data: VSPipelineDataV2): VSPipelineDataV3 {
      val var6: java.lang.Iterable = data.ships
      val var4: ChunkAllocator = data.chunkAllocator
      val var7: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var6, 10))

      for (var3 in var6) {
         var7.add(this.updater.update(var3 as ServerShipDataV2))
      }

      return VSPipelineDataV3(var4, var7 as MutableList<ServerShipDataV3>)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun update(data: Any) {
      this.update(data as VSPipelineDataV2)
   }
}
