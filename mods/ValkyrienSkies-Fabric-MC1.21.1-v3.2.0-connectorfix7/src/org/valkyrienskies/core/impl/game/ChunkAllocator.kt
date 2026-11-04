package org.valkyrienskies.core.impl.game

import com.fasterxml.jackson.annotation.JsonAutoDetect
import org.joml.Vector2i
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.properties.ChunkClaim

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public data class ChunkAllocator(nextClaimX: Int, nextClaimZ: Int, nextShipId: Long) {
   private final var nextClaimX: Int
   private final var nextClaimZ: Int
   private final var nextShipId: Long

   init {
      this.nextClaimX = nextClaimX
      this.nextClaimZ = nextClaimZ
      this.nextShipId = nextShipId
   }

   public fun isChunkInShipyard(chunkX: Int, chunkZ: Int): Boolean {
      chunkX = ChunkClaim.Companion.getClaimXIndex(chunkX)
      chunkZ = ChunkClaim.Companion.getClaimZIndex(chunkZ)
      return (-7000 <= chunkX && chunkX < 7001) and (3000 <= chunkZ && chunkZ < 7001)
   }

   public fun isChunkInShipyard(chunkPos: Vector2i): Boolean {
      return this.isChunkInShipyard(chunkPos.x, chunkPos.y)
   }

   public fun isBlockInShipyard(posX: Int, posY: Int, posZ: Int): Boolean {
      return this.isChunkInShipyard(posX shr 4, posZ shr 4)
   }

   public fun isBlockInShipyard(posX: Double, posY: Double, posZ: Double): Boolean {
      return this.isChunkInShipyard((int)posX shr 4, (int)posZ shr 4)
   }

   public fun isBlockInShipyard(blockPos: Vector3ic): Boolean {
      return this.isBlockInShipyard(blockPos.x(), blockPos.y(), blockPos.z())
   }

   public fun isBlockInShipyard(pos: Vector3dc): Boolean {
      return this.isBlockInShipyard(pos.x(), pos.y(), pos.z())
   }

   public fun allocateShipId(): Long {
      return (long)(this.nextShipId++)
   }

   public fun allocateNewChunkClaim(): ChunkClaim {
      val var1: ChunkClaimImpl = ChunkClaimImpl(this.nextClaimX, this.nextClaimZ)
      var var2: Int = this.nextClaimX++
      if (this.nextClaimX > 7000) {
         this.nextClaimX = -7000
         var2 = this.nextClaimZ++
      }

      if (3000 > this.nextClaimZ || this.nextClaimZ >= 7001) {
         throw IllegalStateException("We ran out of chunk claims to allocate!")
      } else {
         return var1
      }
   }

   private operator fun component1(): Int {
      return this.nextClaimX
   }

   private operator fun component2(): Int {
      return this.nextClaimZ
   }

   private operator fun component3(): Long {
      return this.nextShipId
   }

   public fun copy(nextClaimX: Int = this.nextClaimX, nextClaimZ: Int = this.nextClaimZ, nextShipId: Long = this.nextShipId): ChunkAllocator {
      return ChunkAllocator(nextClaimX, nextClaimZ, nextShipId)
   }

   public override fun toString(): String {
      return "ChunkAllocator(nextClaimX=${this.nextClaimX}, nextClaimZ=${this.nextClaimZ}, nextShipId=${this.nextShipId})"
   }

   public override fun hashCode(): Int {
      return (Integer.hashCode(this.nextClaimX) * 31 + Integer.hashCode(this.nextClaimZ)) * 31 + java.lang.Long.hashCode(this.nextShipId)
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is ChunkAllocator
            && this.nextClaimX == (other as ChunkAllocator).nextClaimX
            && this.nextClaimZ == (other as ChunkAllocator).nextClaimZ
            && this.nextShipId == (other as ChunkAllocator).nextShipId
         }
   }

   public companion object {
      public const val SHIP_ID_START: Long = 0L
      public const val X_INDEX_END: Int = 7000
      public const val X_INDEX_START: Int = -7000
      public const val Z_INDEX_END: Int = 7000
      public const val Z_INDEX_START: Int = 3000

      public fun create(): ChunkAllocator {
         return ChunkAllocator(-7000, 3000, 0L)
      }
   }
}
