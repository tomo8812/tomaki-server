package org.valkyrienskies.core.api.ships.properties

import org.joml.Vector3i
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.world.LevelYRange

public interface ChunkClaim {
   @JvmStatic
   ChunkClaim.Companion Companion = ChunkClaim.Companion.$$INSTANCE;

   public val xIndex: Int

   public val zIndex: Int

   public val xStart: Int

   public val xEnd: Int

   public val zStart: Int

   public val zEnd: Int

   public val xMiddle: Int

   public val zMiddle: Int

   public val size: Int

   public abstract fun toLong(): Long {
   }

   public abstract fun contains(x: Int, z: Int): Boolean {
   }

   public abstract fun getCenterBlockCoordinates(yRange: LevelYRange, destination: Vector3i = Vector3i()): Vector3i {
   }

   public abstract fun getBlockSize(yRange: LevelYRange, destination: Vector3i = Vector3i()): Vector3i {
   }

   public abstract fun getTotalVoxelRegion(yRange: LevelYRange, destination: AABBi = AABBi()): AABBi {
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      ChunkClaim.Companion $$INSTANCE = ChunkClaim.Companion();
      public const val DIAMETER: Int = 256
      private const val BOTTOM_32_BITS_MASK: Long = 4294967295L

      public fun getClaimXIndex(chunkX: Int): Int {
         return Math.floorDiv(chunkX, 256)
      }

      public fun getClaimZIndex(chunkZ: Int): Int {
         return Math.floorDiv(chunkZ, 256)
      }

      public fun claimToLong(claimXIndex: Int, claimZIndex: Int): Long {
         return (long)claimXIndex shl 32 or claimZIndex and 4294967295L
      }

      public fun getClaimThenToLong(chunkX: Int, chunkZ: Int): Long {
         return this.claimToLong(this.getClaimXIndex(chunkX), this.getClaimZIndex(chunkZ))
      }
   }
}
