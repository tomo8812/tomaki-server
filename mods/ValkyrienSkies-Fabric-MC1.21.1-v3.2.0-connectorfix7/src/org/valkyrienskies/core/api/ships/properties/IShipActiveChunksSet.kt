package org.valkyrienskies.core.api.ships.properties

import kotlin.jvm.internal.Ref.IntRef
import org.joml.Vector3i
import org.valkyrienskies.core.api.util.functions.IntBinaryConsumer
import org.valkyrienskies.core.api.world.LevelYRange

public interface IShipActiveChunksSet {
   @JvmStatic
   IShipActiveChunksSet.Companion Companion = IShipActiveChunksSet.Companion.$$INSTANCE;

   public val size: Int

   public abstract fun add(chunkX: Int, chunkZ: Int): Boolean {
   }

   public abstract fun remove(chunkX: Int, chunkZ: Int): Boolean {
   }

   public abstract fun contains(chunkX: Int, chunkZ: Int): Boolean {
   }

   public abstract fun forEach(func: IntBinaryConsumer) {
   }

   public open fun getMinMaxWorldPos(minWorldPos: Vector3i, maxWorldPos: Vector3i, yRange: LevelYRange) {
      if (this.size == 0) {
         minWorldPos.set(Integer.MAX_VALUE)
         maxWorldPos.set(Integer.MIN_VALUE)
      }

      val minChunkX: IntRef = IntRef()
      minChunkX.element = Integer.MAX_VALUE
      val minChunkZ: IntRef = IntRef()
      minChunkZ.element = Integer.MAX_VALUE
      val maxChunkX: IntRef = IntRef()
      maxChunkX.element = Integer.MIN_VALUE
      val maxChunkZ: IntRef = IntRef()
      maxChunkZ.element = Integer.MIN_VALUE
      this.forEach({ chunkX: Int, chunkZ: Int ->
         `$minChunkX`.element = Math.min(`$minChunkX`.element, chunkX)
         `$minChunkZ`.element = Math.min(`$minChunkZ`.element, chunkZ)
         `$maxChunkX`.element = Math.max(`$maxChunkX`.element, chunkX)
         `$maxChunkZ`.element = Math.max(`$maxChunkZ`.element, chunkZ)
      })
      minWorldPos.set(minChunkX.element shl 4, yRange.minY, minChunkZ.element shl 4)
      maxWorldPos.set((maxChunkX.element shl 4) + 15, yRange.maxY, (maxChunkZ.element shl 4) + 15)
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "remove(chunkX, chunkZ)", imports = []))
   public open fun removeChunkPos(chunkX: Int, chunkZ: Int): Boolean {
      return this.remove(chunkX, chunkZ)
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "size", imports = []))
   public open fun getTotalChunks(): Int {
      return this.size
   }

   @Deprecated(message = "Uses boxed integers as parameters and requires Unit return type, bad for performance and Java interop", replaceWith = @ReplaceWith(expression = "forEach(func)", imports = []))
   public abstract fun iterateChunkPos(func: (Int, Int) -> Unit) {
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "contains(chunkX, chunkZ)", imports = []))
   public open fun containsChunkPos(chunkX: Int, chunkZ: Int): Boolean {
      return this.contains(chunkX, chunkZ)
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "add(chunkX, chunkZ)", imports = []))
   public open fun addChunkPos(chunkX: Int, chunkZ: Int): Boolean {
      return this.add(chunkX, chunkZ)
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      IShipActiveChunksSet.Companion $$INSTANCE = IShipActiveChunksSet.Companion();

      public fun chunkPosToLong(chunkX: Int, chunkZ: Int): Long {
         return (long)chunkX shl 32 or chunkZ
      }

      public fun longToChunkX(chunkLong: Long): Int {
         return (int)(chunkLong shr 32)
      }

      public fun longToChunkZ(chunkLong: Long): Int {
         return (int)(chunkLong and 4294967295L)
      }
   }
}
