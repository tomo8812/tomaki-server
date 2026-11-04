package org.valkyrienskies.core.impl.game.ships.serialization.vspipeline.dto

import com.fasterxml.jackson.annotation.JsonTypeName
import org.valkyrienskies.core.impl.game.ChunkAllocator
import org.valkyrienskies.core.impl.game.ships.serialization.shipserver.dto.ServerShipDataV1

@JsonTypeName("v1")
public data class VSPipelineDataV1(chunkAllocator: ChunkAllocator, ships: List<ServerShipDataV1>) : VSPipelineData {
   public final val chunkAllocator: ChunkAllocator
   public final val ships: List<ServerShipDataV1>

   init {
      this.chunkAllocator = chunkAllocator
      this.ships = ships
   }

   public operator fun component1(): ChunkAllocator {
      return this.chunkAllocator
   }

   public operator fun component2(): List<ServerShipDataV1> {
      return this.ships
   }

   public fun copy(chunkAllocator: ChunkAllocator = this.chunkAllocator, ships: List<ServerShipDataV1> = this.ships): VSPipelineDataV1 {
      return VSPipelineDataV1(chunkAllocator, ships)
   }

   public override fun toString(): String {
      return "VSPipelineDataV1(chunkAllocator=${this.chunkAllocator}, ships=${this.ships})"
   }

   public override fun hashCode(): Int {
      return this.chunkAllocator.hashCode() * 31 + this.ships.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is VSPipelineDataV1
            && this.chunkAllocator == (other as VSPipelineDataV1).chunkAllocator
            && this.ships == (other as VSPipelineDataV1).ships
         }
   }
}
