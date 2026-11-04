package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import com.fasterxml.jackson.annotation.JsonTypeName
import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV2

@JsonTypeName("v2")
public data class VSPipelineDataV2(chunkAllocator: ChunkAllocator, ships: List<ServerShipDataV2>) : VSPipelineData {
   public final val chunkAllocator: ChunkAllocator
   public final val ships: List<ServerShipDataV2>

   init {
      this.chunkAllocator = chunkAllocator
      this.ships = ships
   }

   public operator fun component1(): ChunkAllocator {
      return this.chunkAllocator
   }

   public operator fun component2(): List<ServerShipDataV2> {
      return this.ships
   }

   public fun copy(chunkAllocator: ChunkAllocator = this.chunkAllocator, ships: List<ServerShipDataV2> = this.ships): VSPipelineDataV2 {
      return VSPipelineDataV2(chunkAllocator, ships)
   }

   public override fun toString(): String {
      return "VSPipelineDataV2(chunkAllocator=${this.chunkAllocator}, ships=${this.ships})"
   }

   public override fun hashCode(): Int {
      return this.chunkAllocator.hashCode() * 31 + this.ships.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VSPipelineDataV2
            && this.chunkAllocator == (other as VSPipelineDataV2).chunkAllocator
            && this.ships == (other as VSPipelineDataV2).ships
         }
   }
}
