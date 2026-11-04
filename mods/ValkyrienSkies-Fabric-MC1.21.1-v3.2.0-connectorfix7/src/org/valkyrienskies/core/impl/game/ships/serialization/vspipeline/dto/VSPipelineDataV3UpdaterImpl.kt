package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import java.util.ArrayList
import javax.inject.Inject
import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3Updater
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV4
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class VSPipelineDataV3UpdaterImpl @Inject  public constructor(updater: ServerShipDataV3Updater) : VSPipelineDataV3Updater {
   private final val updater: ServerShipDataV3Updater

   init {
      this.updater = updater
   }

   public open fun update(data: VSPipelineDataV3): VSPipelineDataV4 {
      val var6: java.lang.Iterable = data.ships
      val var4: ChunkAllocator = data.chunkAllocator
      val var7: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(var6, 10))

      for (var3 in var6) {
         var7.add(this.updater.update(var3 as ServerShipDataV3))
      }

      return VSPipelineDataV4(var4, var7 as MutableList<ServerShipDataV4>)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun update(data: Any) {
      this.update(data as VSPipelineDataV3)
   }
}
