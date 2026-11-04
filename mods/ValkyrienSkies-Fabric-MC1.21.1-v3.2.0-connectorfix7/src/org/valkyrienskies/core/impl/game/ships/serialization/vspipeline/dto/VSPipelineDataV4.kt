package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV4

public data class VSPipelineDataV4(chunkAllocator: ChunkAllocator, ships: List<ServerShipDataV4>) : VSPipelineData {
   public final val chunkAllocator: ChunkAllocator
   public final val ships: List<ServerShipDataV4>

   init {
      this.chunkAllocator = chunkAllocator
      this.ships = ships
   }

   public operator fun component1(): ChunkAllocator {
      return this.chunkAllocator
   }

   public operator fun component2(): List<ServerShipDataV4> {
      return this.ships
   }

   public fun copy(chunkAllocator: ChunkAllocator = this.chunkAllocator, ships: List<ServerShipDataV4> = this.ships): VSPipelineDataV4 {
      return VSPipelineDataV4(chunkAllocator, ships)
   }

   public override fun toString(): String {
      return "VSPipelineDataV4(chunkAllocator=${this.chunkAllocator}, ships=${this.ships})"
   }

   public override fun hashCode(): Int {
      return this.chunkAllocator.hashCode() * 31 + this.ships.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VSPipelineDataV4
            && this.chunkAllocator == (other as VSPipelineDataV4).chunkAllocator
            && this.ships == (other as VSPipelineDataV4).ships
         }
   }
}
