package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import com.fasterxml.jackson.databind.JsonNode
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.dto.ShipInertiaDataV0
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto.ShipTransformDataV0

public data class ServerShipDataV1(id: Long,
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

   public operator fun component1(): Long {
      return this.id
   }

   public operator fun component2(): String {
      return this.name
   }

   public operator fun component3(): ChunkClaim {
      return this.chunkClaim
   }

   public operator fun component4(): String {
      return this.chunkClaimDimension
   }

   public operator fun component5(): ShipPhysicsData {
      return this.physicsData
   }

   public operator fun component6(): ShipInertiaDataV0 {
      return this.inertiaData
   }

   public operator fun component7(): ShipTransformDataV0 {
      return this.shipTransform
   }

   public operator fun component8(): ShipTransformDataV0 {
      return this.prevTickShipTransform
   }

   public operator fun component9(): AABBdc {
      return this.shipAABB
   }

   public operator fun component10(): AABBic? {
      return this.shipVoxelAABB
   }

   public operator fun component11(): IShipActiveChunksSet {
      return this.shipActiveChunksSet
   }

   public operator fun component12(): Boolean {
      return this.isStatic
   }

   public operator fun component13(): JsonNode {
      return this.persistentAttachedData
   }

   public fun copy(
      id: Long = this.id,
      name: String = this.name,
      chunkClaim: ChunkClaim = this.chunkClaim,
      chunkClaimDimension: String = this.chunkClaimDimension,
      physicsData: ShipPhysicsData = this.physicsData,
      inertiaData: ShipInertiaDataV0 = this.inertiaData,
      shipTransform: ShipTransformDataV0 = this.shipTransform,
      prevTickShipTransform: ShipTransformDataV0 = this.prevTickShipTransform,
      shipAABB: AABBdc = this.shipAABB,
      shipVoxelAABB: AABBic? = this.shipVoxelAABB,
      shipActiveChunksSet: IShipActiveChunksSet = this.shipActiveChunksSet,
      isStatic: Boolean = this.isStatic,
      persistentAttachedData: JsonNode = this.persistentAttachedData
   ): ServerShipDataV1 {
      return ServerShipDataV1(
         id,
         name,
         chunkClaim,
         chunkClaimDimension,
         physicsData,
         inertiaData,
         shipTransform,
         prevTickShipTransform,
         shipAABB,
         shipVoxelAABB,
         shipActiveChunksSet,
         isStatic,
         persistentAttachedData
      )
   }

   public override fun toString(): String {
      return "ServerShipDataV1(id=${this.id}, name=${this.name}, chunkClaim=${this.chunkClaim}, chunkClaimDimension=${this.chunkClaimDimension}, physicsData=${this.physicsData}, inertiaData=${this.inertiaData}, shipTransform=${this.shipTransform}, prevTickShipTransform=${this.prevTickShipTransform}, shipAABB=${this.shipAABB}, shipVoxelAABB=${this.shipVoxelAABB}, shipActiveChunksSet=${this.shipActiveChunksSet}, isStatic=${this.isStatic}, persistentAttachedData=${this.persistentAttachedData})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (
                                                                                       (
                                                                                                (java.lang.Long.hashCode(this.id) * 31 + this.name.hashCode())
                                                                                                      * 31
                                                                                                   + this.chunkClaim.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + this.chunkClaimDimension.hashCode()
                                                                                    )
                                                                                    * 31
                                                                                 + this.physicsData.hashCode()
                                                                           )
                                                                           * 31
                                                                        + this.inertiaData.hashCode()
                                                                  )
                                                                  * 31
                                                               + this.shipTransform.hashCode()
                                                         )
                                                         * 31
                                                      + this.prevTickShipTransform.hashCode()
                                                )
                                                * 31
                                             + this.shipAABB.hashCode()
                                       )
                                       * 31
                                    + (if (this.shipVoxelAABB == null) 0 else this.shipVoxelAABB.hashCode())
                              )
                              * 31
                           + this.shipActiveChunksSet.hashCode()
                     )
                     * 31
                  + java.lang.Boolean.hashCode(this.isStatic)
            )
            * 31
         + this.persistentAttachedData.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label94@
      if (this === other) {
         return true
      } else {
         return other is ServerShipDataV1
            && this.id == (other as ServerShipDataV1).id
            && this.name == (other as ServerShipDataV1).name
            && this.chunkClaim == (other as ServerShipDataV1).chunkClaim
            && this.chunkClaimDimension == (other as ServerShipDataV1).chunkClaimDimension
            && this.physicsData == (other as ServerShipDataV1).physicsData
            && this.inertiaData == (other as ServerShipDataV1).inertiaData
            && this.shipTransform == (other as ServerShipDataV1).shipTransform
            && this.prevTickShipTransform == (other as ServerShipDataV1).prevTickShipTransform
            && this.shipAABB == (other as ServerShipDataV1).shipAABB
            && this.shipVoxelAABB == (other as ServerShipDataV1).shipVoxelAABB
            && this.shipActiveChunksSet == (other as ServerShipDataV1).shipActiveChunksSet
            && this.isStatic == (other as ServerShipDataV1).isStatic
            && this.persistentAttachedData == (other as ServerShipDataV1).persistentAttachedData
         }
   }
}
