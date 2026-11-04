package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import javax.inject.Inject
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class ServerShipDataV3UpdaterImpl @Inject  public constructor() : ServerShipDataV3Updater {
   public open fun update(data: ServerShipDataV3): ServerShipDataV4 {
      return ServerShipDataV4(
         data.id,
         data.name,
         data.chunkClaim,
         data.chunkClaimDimension,
         data.velocity,
         data.omega,
         data.inertiaData,
         data.transform,
         data.prevTickTransform,
         data.worldAABB,
         data.shipAABB,
         data.activeChunks,
         data.isStatic,
         data.persistentAttachedData,
         0
      )
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun update(data: Any) {
      this.update(data as ServerShipDataV3)
   }
}
