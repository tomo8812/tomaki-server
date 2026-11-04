package org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto

import com.fasterxml.jackson.databind.JsonNode
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.dto.ShipInertiaDataV0
import org.valkyrienskies.core.impl.game.ships.serialization.shiptransform.dto.ShipTransformDataV0

public data class ServerShipDataV4(id: Long,
   name: String,
   chunkClaim: ChunkClaim,
   chunkClaimDimension: String,
   velocity: Vector3dc,
   omega: Vector3dc,
   inertiaData: ShipInertiaDataV0,
   transform: ShipTransformDataV0,
   prevTickTransform: ShipTransformDataV0,
   worldAABB: AABBdc,
   shipAABB: AABBic?,
   activeChunks: IShipActiveChunksSet,
   isStatic: Boolean,
   persistentAttachedData: JsonNode,
   portalCoolDown: Int
) {
   public final val id: Long
   public final val name: String
   public final val chunkClaim: ChunkClaim
   public final val chunkClaimDimension: String
   public final val velocity: Vector3dc
   public final val omega: Vector3dc
   public final val inertiaData: ShipInertiaDataV0
   public final val transform: ShipTransformDataV0
   public final val prevTickTransform: ShipTransformDataV0
   public final val worldAABB: AABBdc
   public final val shipAABB: AABBic?
   public final val activeChunks: IShipActiveChunksSet
   public final val isStatic: Boolean
   public final val persistentAttachedData: JsonNode
   public final val portalCoolDown: Int

   init {
      this.id = id
      this.name = name
      this.chunkClaim = chunkClaim
      this.chunkClaimDimension = chunkClaimDimension
      this.velocity = velocity
      this.omega = omega
      this.inertiaData = inertiaData
      this.transform = transform
      this.prevTickTransform = prevTickTransform
      this.worldAABB = worldAABB
      this.shipAABB = shipAABB
      this.activeChunks = activeChunks
      this.isStatic = isStatic
      this.persistentAttachedData = persistentAttachedData
      this.portalCoolDown = portalCoolDown
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

   public operator fun component5(): Vector3dc {
      return this.velocity
   }

   public operator fun component6(): Vector3dc {
      return this.omega
   }

   public operator fun component7(): ShipInertiaDataV0 {
      return this.inertiaData
   }

   public operator fun component8(): ShipTransformDataV0 {
      return this.transform
   }

   public operator fun component9(): ShipTransformDataV0 {
      return this.prevTickTransform
   }

   public operator fun component10(): AABBdc {
      return this.worldAABB
   }

   public operator fun component11(): AABBic? {
      return this.shipAABB
   }

   public operator fun component12(): IShipActiveChunksSet {
      return this.activeChunks
   }

   public operator fun component13(): Boolean {
      return this.isStatic
   }

   public operator fun component14(): JsonNode {
      return this.persistentAttachedData
   }

   public operator fun component15(): Int {
      return this.portalCoolDown
   }

   public fun copy(
      id: Long = this.id,
      name: String = this.name,
      chunkClaim: ChunkClaim = this.chunkClaim,
      chunkClaimDimension: String = this.chunkClaimDimension,
      velocity: Vector3dc = this.velocity,
      omega: Vector3dc = this.omega,
      inertiaData: ShipInertiaDataV0 = this.inertiaData,
      transform: ShipTransformDataV0 = this.transform,
      prevTickTransform: ShipTransformDataV0 = this.prevTickTransform,
      worldAABB: AABBdc = this.worldAABB,
      shipAABB: AABBic? = this.shipAABB,
      activeChunks: IShipActiveChunksSet = this.activeChunks,
      isStatic: Boolean = this.isStatic,
      persistentAttachedData: JsonNode = this.persistentAttachedData,
      portalCoolDown: Int = this.portalCoolDown
   ): ServerShipDataV4 {
      return ServerShipDataV4(
         id,
         name,
         chunkClaim,
         chunkClaimDimension,
         velocity,
         omega,
         inertiaData,
         transform,
         prevTickTransform,
         worldAABB,
         shipAABB,
         activeChunks,
         isStatic,
         persistentAttachedData,
         portalCoolDown
      )
   }

   public override fun toString(): String {
      return "ServerShipDataV4(id=${this.id}, name=${this.name}, chunkClaim=${this.chunkClaim}, chunkClaimDimension=${this.chunkClaimDimension}, velocity=${this.velocity}, omega=${this.omega}, inertiaData=${this.inertiaData}, transform=${this.transform}, prevTickTransform=${this.prevTickTransform}, worldAABB=${this.worldAABB}, shipAABB=${this.shipAABB}, activeChunks=${this.activeChunks}, isStatic=${this.isStatic}, persistentAttachedData=${this.persistentAttachedData}, portalCoolDown=${this.portalCoolDown})"
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
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           java.lang.Long.hashCode(this.id)
                                                                                                                                 * 31
                                                                                                                              + this.name.hashCode()
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + this.chunkClaim.hashCode()
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.chunkClaimDimension.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + this.velocity.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + this.omega.hashCode()
                                                                                    )
                                                                                    * 31
                                                                                 + this.inertiaData.hashCode()
                                                                           )
                                                                           * 31
                                                                        + this.transform.hashCode()
                                                                  )
                                                                  * 31
                                                               + this.prevTickTransform.hashCode()
                                                         )
                                                         * 31
                                                      + this.worldAABB.hashCode()
                                                )
                                                * 31
                                             + (if (this.shipAABB == null) 0 else this.shipAABB.hashCode())
                                       )
                                       * 31
                                    + this.activeChunks.hashCode()
                              )
                              * 31
                           + java.lang.Boolean.hashCode(this.isStatic)
                     )
                     * 31
                  + this.persistentAttachedData.hashCode()
            )
            * 31
         + Integer.hashCode(this.portalCoolDown)
      }

   public override operator fun equals(other: Any?): Boolean {
      label106@
      if (this === other) {
         return true
      } else {
         return other is ServerShipDataV4
            && this.id == (other as ServerShipDataV4).id
            && this.name == (other as ServerShipDataV4).name
            && this.chunkClaim == (other as ServerShipDataV4).chunkClaim
            && this.chunkClaimDimension == (other as ServerShipDataV4).chunkClaimDimension
            && this.velocity == (other as ServerShipDataV4).velocity
            && this.omega == (other as ServerShipDataV4).omega
            && this.inertiaData == (other as ServerShipDataV4).inertiaData
            && this.transform == (other as ServerShipDataV4).transform
            && this.prevTickTransform == (other as ServerShipDataV4).prevTickTransform
            && this.worldAABB == (other as ServerShipDataV4).worldAABB
            && this.shipAABB == (other as ServerShipDataV4).shipAABB
            && this.activeChunks == (other as ServerShipDataV4).activeChunks
            && this.isStatic == (other as ServerShipDataV4).isStatic
            && this.persistentAttachedData == (other as ServerShipDataV4).persistentAttachedData
            && this.portalCoolDown == (other as ServerShipDataV4).portalCoolDown
         }
   }
}
