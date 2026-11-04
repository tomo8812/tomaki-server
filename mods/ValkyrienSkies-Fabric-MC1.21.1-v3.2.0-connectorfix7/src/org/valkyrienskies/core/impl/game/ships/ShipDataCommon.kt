package org.valkyrienskies.core.impl.game.ships

import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.api.ShipInternal
import org.valkyrienskies.core.impl.shadow.GB
import org.valkyrienskies.core.impl.shadow.GJ
import org.valkyrienskies.core.util.AABBdUtilKt

public open class ShipDataCommon(id: Long,
      slug: String?,
      chunkClaim: ChunkClaim,
      chunkClaimDimension: String,
      kinematics: BodyKinematics,
      worldAABB: AABBdc = kinematics.transform,
      shipAABB: AABBic?,
      activeChunksSet: IShipActiveChunksSet
   ) :
   ShipInternal {
   public open val id: Long
   public open var slug: String?
   public open val chunkClaim: ChunkClaim
   public open var chunkClaimDimension: String
   public open var shipAABB: AABBic?
   public open val activeChunksSet: IShipActiveChunksSet

   @GB
   public open var kinematics: BodyKinematics
      public open set(kinematics) {
         var var4: AABBd
         run label23@{
            this.kinematics = kinematics
            val var10001: AABBic = this.shipAABB
            if (var10001 != null) {
               var4 = AABBdUtilKt.toAABBd(var10001, AABBd())
               if (var4 != null) {
                  var4 = var4.transform(kinematics.toWorld, AABBd())
                  return@label23
               }
            }

            var4 = null
         }

         this.worldAABB = if (var4 == null) AABBd(kinematics.position, kinematics.position) else var4
      }


   @GJ
   public final var prevTickTransform: ShipTransform
      private set

   @GB
   public final var worldAABB: AABBdc
      private set

   init {
      this.id = id
      this.slug = slug
      this.chunkClaim = chunkClaim
      this.chunkClaimDimension = chunkClaimDimension
      this.shipAABB = shipAABB
      this.activeChunksSet = activeChunksSet
      this.kinematics = kinematics
      val var10001: BodyTransform = kinematics.transform
      this.prevTickTransform = var10001 as ShipTransform
      this.worldAABB = worldAABB
   }

   public override fun setFromTransform(transform: BodyTransform) {
      this.kinematics = this.kinematics.withTransform(transform)
   }

   public override fun unsafeSetKinematics(kinematics: BodyKinematics) {
      this.kinematics = kinematics
   }

   public fun updatePrevTickShipTransform() {
      this.prevTickTransform = this.getTransform()
   }

   public override fun onSetBlock(
      posX: Int,
      posY: Int,
      posZ: Int,
      newBlockIsAir: Boolean,
      oldBlockMass: Double,
      newBlockMass: Double,
      isRunningOnServer: Boolean
   ) {
      if (!this.chunkClaim.contains(posX shr 4, posZ shr 4)) {
         throw IllegalArgumentException(("Block at <$posX, $posY, $posZ> is not in the chunk claim belonging to $this").toString())
      } else {
         if (!newBlockIsAir) {
            this.activeChunksSet.add(posX shr 4, posZ shr 4)
            this.activeChunksSet.add((posX shr 4) - 1, posZ shr 4)
            this.activeChunksSet.add((posX shr 4) + 1, posZ shr 4)
            this.activeChunksSet.add(posX shr 4, (posZ shr 4) - 1)
            this.activeChunksSet.add(posX shr 4, (posZ shr 4) + 1)
         }
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true
      } else if (!(this.getClass() == (if (other != null) other.getClass() else null))) {
         return false
      } else {
         return this.id == (other as ShipDataCommon).id
      }
   }

   public override fun hashCode(): Int {
      return java.lang.Long.hashCode(this.id)
   }
}
