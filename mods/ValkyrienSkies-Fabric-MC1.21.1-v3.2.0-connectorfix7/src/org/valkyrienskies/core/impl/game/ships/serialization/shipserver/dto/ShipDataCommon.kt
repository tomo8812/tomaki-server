package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto.ShipTransformDataV0
import org.valkyrienskies.core.impl.shadow.GB
import org.valkyrienskies.core.impl.shadow.GJ
import org.valkyrienskies.core.util.AABBdUtilKt

public open class ShipDataCommon(id: Long,
   name: String,
   chunkClaim: ChunkClaim,
   chunkClaimDimension: String,
   physicsData: ShipPhysicsData,
   shipTransform: ShipTransformDataV0,
   prevTickShipTransform: ShipTransformDataV0 = shipTransform,
   shipAABB: AABBdc = shipTransform.createEmptyAABB(),
   shipVoxelAABB: AABBic?,
   shipActiveChunksSet: IShipActiveChunksSet
) {
   public final val id: Long
   public final var name: String
   public final val chunkClaim: ChunkClaim
   public final val chunkClaimDimension: String

   @GB
   public final val physicsData: ShipPhysicsData

   public final var shipVoxelAABB: AABBic?
   public final val shipActiveChunksSet: IShipActiveChunksSet

   @GB
   public final var shipTransform: ShipTransformDataV0
      public final set(shipTransform) {
         var var3: AABBd
         run label23@{
            this.shipTransform = shipTransform
            if (this.shipVoxelAABB != null) {
               var3 = AABBdUtilKt.toAABBd(this.shipVoxelAABB, AABBd())
               if (var3 != null) {
                  var3 = var3.transform(shipTransform.shipToWorldMatrix, AABBd())
                  return@label23
               }
            }

            var3 = null
         }

         this.shipAABB = if (var3 == null) shipTransform.createEmptyAABB() else var3
      }


   @GJ
   public final var prevTickShipTransform: ShipTransformDataV0
      private set

   @GB
   public final var shipAABB: AABBdc
      private set

   init {
      this.id = id
      this.name = name
      this.chunkClaim = chunkClaim
      this.chunkClaimDimension = chunkClaimDimension
      this.physicsData = physicsData
      this.shipVoxelAABB = shipVoxelAABB
      this.shipActiveChunksSet = shipActiveChunksSet
      this.shipTransform = shipTransform
      this.prevTickShipTransform = prevTickShipTransform
      this.shipAABB = shipAABB
   }

   public final val velocity: Vector3dc
      public final get() {
         return this.physicsData.linearVelocity
      }


   public final val omega: Vector3dc
      public final get() {
         return this.physicsData.angularVelocity
      }

}
