package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.dto.ShipInertiaDataV0
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto.ShipTransformDataV0
import org.valkyrienskies.core.impl.shadow.GJ

public class ServerShipDataV0(id: Long,
   name: String,
   chunkClaim: ChunkClaim,
   chunkClaimDimension: String,
   physicsData: ShipPhysicsData,
   inertiaData: ShipInertiaDataV0,
   shipTransform: ShipTransformDataV0,
   prevTickShipTransform: ShipTransformDataV0,
   shipAABB: AABBdc,
   shipVoxelAABB: AABBic?,
   shipActiveChunksSet: IShipActiveChunksSet,
   isStatic: Boolean = false
) : ShipDataCommon(id, name, chunkClaim, chunkClaimDimension, physicsData, shipTransform, prevTickShipTransform, shipAABB, shipVoxelAABB, shipActiveChunksSet) {
   @GJ
   public final val inertiaData: ShipInertiaDataV0

   public final var isStatic: Boolean
   public final val persistentAttachedData: ObjectNode

   init {
      this.inertiaData = inertiaData
      this.isStatic = isStatic
      this.persistentAttachedData = JsonNodeFactory.instance.objectNode()
   }
}
