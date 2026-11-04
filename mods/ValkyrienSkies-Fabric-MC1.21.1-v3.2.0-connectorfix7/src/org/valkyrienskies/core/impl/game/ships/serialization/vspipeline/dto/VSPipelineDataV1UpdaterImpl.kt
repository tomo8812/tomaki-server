package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import java.util.ArrayList
import javax.inject.Inject
import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1Updater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class VSPipelineDataV1UpdaterImpl @Inject  public constructor(updater: ServerShipDataV1Updater) : VSPipelineDataV1Updater {
   private final val updater: ServerShipDataV1Updater

   init {
      this.updater = updater
   }

   public open fun update(data: VSPipelineDataV1): VSPipelineDataV2 {
      val var6: java.lang.Iterable = data.ships
      val var4: ChunkAllocator = data.chunkAllocator
      val var7: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var6, 10))

      for (var3 in var6) {
         var7.add(this.updater.update(var3 as ServerShipDataV1))
      }

      return VSPipelineDataV2(var4, var7 as MutableList<ServerShipDataV2>)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun update(data: Any) {
      this.update(data as VSPipelineDataV1)
   }
}
