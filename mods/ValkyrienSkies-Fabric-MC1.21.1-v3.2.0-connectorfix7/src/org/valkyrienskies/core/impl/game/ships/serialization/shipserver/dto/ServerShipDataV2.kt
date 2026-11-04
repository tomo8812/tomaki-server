package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import com.fasterxml.jackson.databind.JsonNode
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.dto.ShipInertiaDataV0
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto.ShipTransformDataV0

public class ServerShipDataV2(id: Long,
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
   isStatic: Boolean,
   persistentAttachedData: JsonNode
) {
   public final val id: Long
   public final val name: String
   public final val chunkClaim: ChunkClaim
   public final val chunkClaimDimension: String
   public final val physicsData: ShipPhysicsData
   public final val inertiaData: ShipInertiaDataV0
   public final val shipTransform: ShipTransformDataV0
   public final val prevTickShipTransform: ShipTransformDataV0
   public final val shipAABB: AABBdc
   public final val shipVoxelAABB: AABBic?
   public final val shipActiveChunksSet: IShipActiveChunksSet
   public final val isStatic: Boolean
   public final val persistentAttachedData: JsonNode

   init {
      this.id = id
      this.name = name
      this.chunkClaim = chunkClaim
      this.chunkClaimDimension = chunkClaimDimension
      this.physicsData = physicsData
      this.inertiaData = inertiaData
      this.shipTransform = shipTransform
      this.prevTickShipTransform = prevTickShipTransform
      this.shipAABB = shipAABB
      this.shipVoxelAABB = shipVoxelAABB
      this.shipActiveChunksSet = shipActiveChunksSet
      this.isStatic = isStatic
      this.persistentAttachedData = persistentAttachedData
   }
}
