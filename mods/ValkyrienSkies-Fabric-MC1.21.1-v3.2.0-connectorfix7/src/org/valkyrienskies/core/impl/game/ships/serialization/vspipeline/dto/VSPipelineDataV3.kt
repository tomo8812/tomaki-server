package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV3

public data class VSPipelineDataV3(chunkAllocator: ChunkAllocator, ships: List<ServerShipDataV3>) : VSPipelineData {
   public final val chunkAllocator: ChunkAllocator
   public final val ships: List<ServerShipDataV3>

   init {
      this.chunkAllocator = chunkAllocator
      this.ships = ships
   }

   public operator fun component1(): ChunkAllocator {
      return this.chunkAllocator
   }

   public operator fun component2(): List<ServerShipDataV3> {
      return this.ships
   }

   public fun copy(chunkAllocator: ChunkAllocator = this.chunkAllocator, ships: List<ServerShipDataV3> = this.ships): VSPipelineDataV3 {
      return VSPipelineDataV3(chunkAllocator, ships)
   }

   public override fun toString(): String {
      return "VSPipelineDataV3(chunkAllocator=${this.chunkAllocator}, ships=${this.ships})"
   }

   public override fun hashCode(): Int {
      return this.chunkAllocator.hashCode() * 31 + this.ships.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VSPipelineDataV3
            && this.chunkAllocator == (other as VSPipelineDataV3).chunkAllocator
            && this.ships == (other as VSPipelineDataV3).ships
         }
   }
}
