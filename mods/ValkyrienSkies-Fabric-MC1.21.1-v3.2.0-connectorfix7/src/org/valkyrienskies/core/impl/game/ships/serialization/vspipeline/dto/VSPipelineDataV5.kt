package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV4
import org.valkyrienskies.core.internal.joints.VSJointAndId

public data class VSPipelineDataV5(chunkAllocator: ChunkAllocator, ships: List<ServerShipDataV4>, nextJointId: Int, joints: List<VSJointAndId>) : VSPipelineData {
   public final val chunkAllocator: ChunkAllocator
   public final val ships: List<ServerShipDataV4>
   public final val nextJointId: Int
   public final val joints: List<VSJointAndId>

   init {
      this.chunkAllocator = chunkAllocator
      this.ships = ships
      this.nextJointId = nextJointId
      this.joints = joints
   }

   public operator fun component1(): ChunkAllocator {
      return this.chunkAllocator
   }

   public operator fun component2(): List<ServerShipDataV4> {
      return this.ships
   }

   public operator fun component3(): Int {
      return this.nextJointId
   }

   public operator fun component4(): List<VSJointAndId> {
      return this.joints
   }

   public fun copy(
      chunkAllocator: ChunkAllocator = this.chunkAllocator,
      ships: List<ServerShipDataV4> = this.ships,
      nextJointId: Int = this.nextJointId,
      joints: List<VSJointAndId> = this.joints
   ): VSPipelineDataV5 {
      return VSPipelineDataV5(chunkAllocator, ships, nextJointId, joints)
   }

   public override fun toString(): String {
      return "VSPipelineDataV5(chunkAllocator=${this.chunkAllocator}, ships=${this.ships}, nextJointId=${this.nextJointId}, joints=${this.joints})"
   }

   public override fun hashCode(): Int {
      return ((this.chunkAllocator.hashCode() * 31 + this.ships.hashCode()) * 31 + Integer.hashCode(this.nextJointId)) * 31 + this.joints.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is VSPipelineDataV5
            && this.chunkAllocator == (other as VSPipelineDataV5).chunkAllocator
            && this.ships == (other as VSPipelineDataV5).ships
            && this.nextJointId == (other as VSPipelineDataV5).nextJointId
            && this.joints == (other as VSPipelineDataV5).joints
         }
   }
}
