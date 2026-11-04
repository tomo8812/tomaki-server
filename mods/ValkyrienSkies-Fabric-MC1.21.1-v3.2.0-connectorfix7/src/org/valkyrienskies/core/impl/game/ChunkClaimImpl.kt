package org.valkyrienskies.core.impl.game

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIncludeProperties
import org.joml.Vector3i
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.world.LevelYRange

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIncludeProperties(["xIndex", "zIndex"])
public data class ChunkClaimImpl(xIndex: Int, zIndex: Int) : ChunkClaim {
   public open val xIndex: Int
   public open val zIndex: Int
   public open val xStart: Int
   public open val xEnd: Int
   public open val zStart: Int
   public open val zEnd: Int
   public open val xMiddle: Int
   public open val zMiddle: Int
   public open val size: Int

   init {
      this.xIndex = xIndex
      this.zIndex = zIndex
      this.xStart = this.xIndex shl 8
      this.xEnd = (this.xIndex shl 8) + 256 - 1
      this.zStart = this.zIndex shl 8
      this.zEnd = (this.zIndex shl 8) + 256 - 1
      this.xMiddle = this.xStart + 128
      this.zMiddle = this.zStart + 128
      this.size = (this.xEnd - this.xStart + 1) * (this.zEnd - this.zStart + 1)
   }

   public override fun toLong(): Long {
      return ChunkClaim.Companion.claimToLong(this.xIndex, this.zIndex)
   }

   public override fun contains(x: Int, z: Int): Boolean {
      return (x <= this.xEnd && this.xStart <= x) and (z <= this.zEnd && this.zStart <= z)
   }

   public override fun getCenterBlockCoordinates(yRange: LevelYRange, destination: Vector3i): Vector3i {
      val var10000: Vector3i = destination.set(
         ((this.xStart shl 4) + (this.xEnd shl 4) + 15) / 2, yRange.center, ((this.zStart shl 4) + (this.zEnd shl 4) + 15) / 2
      )
      return var10000
   }

   public override fun getBlockSize(yRange: LevelYRange, destination: Vector3i): Vector3i {
      val var10000: Vector3i = destination.set(this.xEnd - this.xStart + 1 shl 4, 256, this.zEnd - this.zStart + 1 shl 4)
      return var10000
   }

   public override fun getTotalVoxelRegion(yRange: LevelYRange, destination: AABBi): AABBi {
      destination.minX = this.xStart shl 4
      destination.minY = yRange.minY
      destination.minZ = this.zStart shl 4
      destination.maxX = (this.xEnd shl 4) + 15
      destination.maxY = yRange.maxY
      destination.maxZ = (this.zEnd shl 4) + 15
      return destination
   }

   public operator fun component1(): Int {
      return this.xIndex
   }

   public operator fun component2(): Int {
      return this.zIndex
   }

   public fun copy(xIndex: Int = this.xIndex, zIndex: Int = this.zIndex): ChunkClaimImpl {
      return ChunkClaimImpl(xIndex, zIndex)
   }

   public override fun toString(): String {
      return "ChunkClaimImpl(xIndex=${this.xIndex}, zIndex=${this.zIndex})"
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.xIndex) * 31 + Integer.hashCode(this.zIndex)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is ChunkClaimImpl && this.xIndex == (other as ChunkClaimImpl).xIndex && this.zIndex == (other as ChunkClaimImpl).zIndex
      }
   }

   public companion object {
      public fun getClaim(chunkX: Int, chunkZ: Int): ChunkClaim {
         return ChunkClaimImpl(ChunkClaim.Companion.getClaimXIndex(chunkX), ChunkClaim.Companion.getClaimZIndex(chunkZ))
      }
   }
}
