package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import com.fasterxml.jackson.databind.node.ObjectNode
import javax.inject.Inject
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.dto.ShipInertiaDataV0
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto.ShipTransformDataV0
import org.valkyrienskies.core.impl.shadow.dY

@dY
public class ServerShipDataV0UpdaterImpl @Inject  public constructor() : ServerShipDataV0Updater {
   public open fun update(data: ServerShipDataV0): ServerShipDataV1 {
      val var10002: Long = data.id
      val var10003: java.lang.String = data.name
      val var10004: ChunkClaim = data.chunkClaim
      val var10005: java.lang.String = data.chunkClaimDimension
      val var10006: ShipPhysicsData = data.physicsData
      val var10007: ShipInertiaDataV0 = data.inertiaData
      val var10008: ShipTransformDataV0 = data.shipTransform
      val var10009: ShipTransformDataV0 = data.shipTransform
      val var10010: AABBdc = data.shipAABB
      val var10011: AABBic = data.shipVoxelAABB
      val var10012: IShipActiveChunksSet = data.shipActiveChunksSet
      val var10013: Boolean = data.isStatic
      val var10014: ObjectNode = data.persistentAttachedData
      return ServerShipDataV1(var10002, var10003, var10004, var10005, var10006, var10007, var10008, var10009, var10010, var10011, var10012, var10013, var10014)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun update(data: Any) {
      this.update(data as ServerShipDataV0)
   }
}
