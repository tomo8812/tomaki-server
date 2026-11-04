package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import javax.inject.Inject
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class ServerShipDataV1UpdaterImpl @Inject  public constructor() : ServerShipDataV1Updater {
   public open fun update(data: ServerShipDataV1): ServerShipDataV2 {
      return ServerShipDataV2(
         data.id,
         data.name,
         data.chunkClaim,
         data.chunkClaimDimension,
         data.physicsData,
         data.inertiaData,
         data.shipTransform,
         data.prevTickShipTransform,
         data.shipAABB,
         data.shipVoxelAABB,
         data.shipActiveChunksSet,
         data.isStatic,
         data.persistentAttachedData
      )
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun update(data: Any) {
      this.update(data as ServerShipDataV1)
   }
}
