package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import javax.inject.Inject
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class ServerShipDataV2UpdaterImpl @Inject  public constructor() : ServerShipDataV2Updater {
   public open fun update(data: ServerShipDataV2): ServerShipDataV3 {
      return ServerShipDataV3(
         data.id,
         data.name,
         data.chunkClaim,
         data.chunkClaimDimension,
         data.physicsData.linearVelocity,
         data.physicsData.angularVelocity,
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
      this.update(data as ServerShipDataV2)
   }
}
