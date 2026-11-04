package org.valkyrienskies.core.util.datastructures

import kotlin.jvm.internal.SourceDebugExtension
import org.joml.Vector3ic
import org.valkyrienskies.core.util.UtilKt

@SourceDebugExtension(["SMAP\nSingleChunkDenseBlockPosSet.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SingleChunkDenseBlockPosSet.kt\norg/valkyrienskies/core/util/datastructures/SingleChunkDenseBlockPosSet\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 Util.kt\norg/valkyrienskies/core/util/UtilKt\n*L\n1#1,79:1\n13477#2,2:80\n13479#2:91\n10#3,3:82\n66#3,5:85\n14#3:90\n*S KotlinDebug\n*F\n+ 1 SingleChunkDenseBlockPosSet.kt\norg/valkyrienskies/core/util/datastructures/SingleChunkDenseBlockPosSet\n*L\n24#1:80,2\n24#1:91\n25#1:82,3\n27#1:85,5\n25#1:90\n*E\n"])
public class SingleChunkDenseBlockPosSet {
   public final val data: ByteArray = ByteArray(512)

   public inline fun forEach(fn: (Int, Int, Int) -> Unit) {
      val `$this$forEachIndexed$iv`: ByteArray = this.data
      var `index$iv`: Int = 0

      for (`item$iv` in `$this$forEachIndexed$iv`) {
         val index: Int = `index$iv`++
         val `$this$iterateBits$iv`: Byte = `item$iv`

         for (`i$iv` in 7 downTo 0) {
            if ((`$this$iterateBits$iv` and 1 shl `i$iv`) != 0) {
               val `index$ivx`: Int = index * 8 + `i$iv`
               val `dimensions$iv`: Vector3ic = Companion.dimensions
               val `z$iv`: Int = `index$ivx` / (`dimensions$iv`.x() * `dimensions$iv`.y())
               fn(
                  (`index$ivx` - `z$iv` * `dimensions$iv`.x() * `dimensions$iv`.y()) % `dimensions$iv`.x(),
                  (`index$ivx` - `z$iv` * `dimensions$iv`.x() * `dimensions$iv`.y()) / `dimensions$iv`.x(),
                  `z$iv`
               )
            }
         }
      }
   }

   public fun remove(x: Int, y: Int, z: Int): Boolean {
      this.requireInBounds(x, y, z)
      val index: Int = UtilKt.wrapIndex(x, y, z, dimensions)
      val realIndex: Int = index / 8
      val offset: Int = index % 8
      val prev: Byte = this.data[index / 8]
      this.data[realIndex] = (byte)(this.data[realIndex] and (byte)((1 shl index % 8).inv()))
      return Companion.isBitSet(prev, offset)
   }

   public fun add(x: Int, y: Int, z: Int): Boolean {
      this.requireInBounds(x, y, z)
      val index: Int = UtilKt.wrapIndex(x, y, z, dimensions)
      val realIndex: Int = index / 8
      val offset: Int = index % 8
      val prev: Byte = this.data[index / 8]
      this.data[realIndex] = (byte)(this.data[realIndex] or (byte)(1 shl index % 8))
      return !Companion.isBitSet(prev, offset)
   }

   public fun contains(x: Int, y: Int, z: Int): Boolean {
      if (!this.isInBounds(x, y, z)) {
         return false
      } else {
         val index: Int = UtilKt.wrapIndex(x, y, z, dimensions)
         return Companion.isBitSet(this.data[index / 8], index % 8)
      }
   }

   private fun requireInBounds(x: Int, y: Int, z: Int) {
      if (!this.isInBounds(x, y, z)) {
         throw IllegalArgumentException(("Block coordinates ($x, $y, $z) must be within the bounds of the chunk (0 <= x, y, z <= 15)").toString())
      }
   }

   private fun isInBounds(x: Int, y: Int, z: Int): Boolean {
      return x < dimensions.x() && x >= 0 && y < dimensions.y() && y >= 0 && z < dimensions.z() && z >= 0
   }

   public companion object {
      public final val dimensions: Vector3ic

      private fun Byte.isBitSet(bitIndex: Int): Boolean {
         return (byte)(`$this$isBitSet` and (byte)(1 shl bitIndex)) != 0
      }
   }
}
