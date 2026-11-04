package org.valkyrienskies.core.util.datastructures

import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import org.joml.Vector3i
import org.joml.Vector3ic

@SourceDebugExtension(["SMAP\nDenseBlockPosSet.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DenseBlockPosSet.kt\norg/valkyrienskies/core/util/datastructures/DenseBlockPosSet\n+ 2 BlockPos2ObjectOpenHashMap.kt\norg/valkyrienskies/core/util/datastructures/BlockPos2ObjectOpenHashMap\n+ 3 SingleChunkDenseBlockPosSet.kt\norg/valkyrienskies/core/util/datastructures/SingleChunkDenseBlockPosSet\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 Util.kt\norg/valkyrienskies/core/util/UtilKt\n*L\n1#1,90:1\n68#2,10:91\n68#2,2:101\n72#2,3:125\n77#2:143\n24#3:103\n25#3:106\n26#3,2:110\n28#3,4:117\n32#3:122\n33#3:124\n25#3,3:128\n28#3,4:136\n32#3,2:141\n13477#4,2:104\n13479#4:123\n10#5,3:107\n66#5,5:112\n14#5:121\n66#5,5:131\n14#5:140\n*S KotlinDebug\n*F\n+ 1 DenseBlockPosSet.kt\norg/valkyrienskies/core/util/datastructures/DenseBlockPosSet\n*L\n78#1:91,10\n84#1:101,2\n84#1:125,3\n84#1:143\n85#1:103\n85#1:106\n85#1:110,2\n85#1:117,4\n85#1:122\n85#1:124\n85#1:128,3\n85#1:136,4\n85#1:141,2\n85#1:104,2\n85#1:123\n85#1:107,3\n85#1:112,5\n85#1:121\n85#1:131,5\n85#1:140\n*E\n"])
public class DenseBlockPosSet : IBlockPosSet {
   public final val chunks: BlockPos2ObjectOpenHashMap<SingleChunkDenseBlockPosSet> = BlockPos2ObjectOpenHashMap(0, 0.0F, 3, null)

   public open var size: Int
      private set

   public fun getChunk(x: Int, y: Int, z: Int): SingleChunkDenseBlockPosSet? {
      return this.chunks.get(x shr 4, y shr 4, z shr 4)
   }

   public override fun add(x: Int, y: Int, z: Int): Boolean {
      val prev: Boolean = this.chunks.getOrPut(x shr 4, y shr 4, z shr 4, {
         SingleChunkDenseBlockPosSet()
      }).add(x and 15, y and 15, z and 15)
      if (prev) {
         this.size = this.size() + 1
      }

      return prev
   }

   public override fun clear() {
      this.chunks.clear()
      this.size = 0
   }

   public override operator fun iterator(): MutableIterator<Vector3ic> {
      return object : MutableIterator<Vector3ic> {
         private final var lastReturnedVector: Vector3i?

         private final val blockPosIterator: Iterator<Vector3ic> = SequencesKt.sequence({
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as Function2).iterator()

         public override operator fun hasNext(): Boolean {
            return this.blockPosIterator.hasNext()
         }

         public open operator fun next(): Vector3ic {
            return this.blockPosIterator.next()
         }

         public override fun remove() {
            if (this.lastReturnedVector != null) {
               val var10000: DenseBlockPosSet = DenseBlockPosSet.this
               val var10001: Vector3i = this.lastReturnedVector
               val var1: Int = var10001.x
               val var10002: Vector3i = this.lastReturnedVector
               val var2: Int = var10002.y
               val var10003: Vector3i = this.lastReturnedVector
               var10000.remove(var1, var2, var10003.z)
               this.lastReturnedVector = null
            } else {
               throw IllegalStateException("Remove is not ready!")
            }
         }

         // $VF: bridge methodSupplier
         @JvmSynthetic
         override fun next() {
            this.next()
         }

         @JvmStatic
         @JvmSynthetic
         fun `access$setLastReturnedVector$p`(`$this`: <unrepresentable>, `<set-?>`: Vector3i) {
            `$this`.lastReturnedVector = `<set-?>`
         }

         @JvmStatic
         @JvmSynthetic
         fun `access$getLastReturnedVector$p`(`$this`: <unrepresentable>): Vector3i {
            `$this`.lastReturnedVector
         }
      }
   }

   public override fun remove(x: Int, y: Int, z: Int): Boolean {
      val var10000: SingleChunkDenseBlockPosSet = this.chunks.get(x shr 4, y shr 4, z shr 4)
      val prev: Boolean = var10000 != null && var10000.remove(x and 15, y and 15, z and 15)
      if (prev) {
         this.size = this.size() + -1
      }

      return prev
   }

   public override fun contains(x: Int, y: Int, z: Int): Boolean {
      val var10000: SingleChunkDenseBlockPosSet = this.chunks.get(x shr 4, y shr 4, z shr 4)
      return var10000 != null && var10000.contains(x and 15, y and 15, z and 15)
   }

   public override fun canStore(x: Int, y: Int, z: Int): Boolean {
      return true
   }

   public inline fun forEachChunk(fn: (Int, Int, Int, SingleChunkDenseBlockPosSet) -> Unit) {
      val `this_$iv`: BlockPos2ObjectOpenHashMap = this.chunks
      if (`this_$iv`.containsNullKey) {
         fn(
            `this_$iv`.keys[`this_$iv`.n * 3],
            `this_$iv`.keys[`this_$iv`.n * 3 + 1],
            `this_$iv`.keys[`this_$iv`.n * 3 + 2],
            `this_$iv`.values[`this_$iv`.n] as SingleChunkDenseBlockPosSet
         )
      }

      for (`pos$iv` in `this_$iv`.n downTo 0) {
         if (`this_$iv`.keys[`pos$iv` * 3] != 0 || `this_$iv`.keys[`pos$iv` * 3 + 1] != 0 || `this_$iv`.keys[`pos$iv` * 3 + 2] != 0) {
            fn(
               `this_$iv`.keys[`pos$iv` * 3],
               `this_$iv`.keys[`pos$iv` * 3 + 1],
               `this_$iv`.keys[`pos$iv` * 3 + 2],
               `this_$iv`.values[`pos$iv`] as SingleChunkDenseBlockPosSet
            )
         }
      }
   }

   public inline fun forEach(fn: (Int, Int, Int) -> Unit) {
      val `this_$iv`: BlockPos2ObjectOpenHashMap = this.chunks
      if (`this_$iv`.containsNullKey) {
         val var10000: Int = `this_$iv`.keys[`this_$iv`.n * 3]
         val var10001: Int = `this_$iv`.keys[`this_$iv`.n * 3 + 1]
         val var10002: Int = `this_$iv`.keys[`this_$iv`.n * 3 + 2]
         val `this_$ivx`: SingleChunkDenseBlockPosSet = `this_$iv`.values[`this_$iv`.n] as SingleChunkDenseBlockPosSet
         val chunkZ: Int = var10002
         val chunkY: Int = var10001
         val chunkX: Int = var10000
         val `$this$forEachIndexed$iv$iv`: ByteArray = `this_$ivx`.data
         var `index$iv$iv`: Int = 0

         for (`item$iv$iv` in `$this$forEachIndexed$iv$iv`) {
            val `index$iv`: Int = `index$iv$iv`++
            val `$this$iterateBits$iv$iv`: Byte = `item$iv$iv`

            for (`i$iv$iv` in 7 downTo 0) {
               if ((`$this$iterateBits$iv$iv` and 1 shl `i$iv$iv`) != 0) {
                  val `index$iv$ivx`: Int = `index$iv` * 8 + `i$iv$iv`
                  val `dimensions$iv$iv`: Vector3ic = SingleChunkDenseBlockPosSet.Companion.dimensions
                  val `z$iv$iv`: Int = `index$iv$ivx` / (`dimensions$iv$iv`.x() * `dimensions$iv$iv`.y())
                  fn(
                     (`index$iv$ivx` - `z$iv$iv` * `dimensions$iv$iv`.x() * `dimensions$iv$iv`.y()) % `dimensions$iv$iv`.x() + (chunkX shl 4),
                     (`index$iv$ivx` - `z$iv$iv` * `dimensions$iv$iv`.x() * `dimensions$iv$iv`.y()) / `dimensions$iv$iv`.x() + (chunkY shl 4),
                     `z$iv$iv` + (chunkZ shl 4)
                  )
               }
            }
         }
      }

      for (`pos$iv` in `this_$iv`.n downTo 0) {
         if (`this_$iv`.keys[`pos$iv` * 3] != 0 || `this_$iv`.keys[`pos$iv` * 3 + 1] != 0 || `this_$iv`.keys[`pos$iv` * 3 + 2] != 0) {
            val var82: Int = `this_$iv`.keys[`pos$iv` * 3]
            val var85: Int = `this_$iv`.keys[`pos$iv` * 3 + 1]
            val var86: Int = `this_$iv`.keys[`pos$iv` * 3 + 2]
            val `this_$ivx`: SingleChunkDenseBlockPosSet = `this_$iv`.values[`pos$iv`] as SingleChunkDenseBlockPosSet
            val var44: Int = var86
            val var45: Int = var85
            val var46: Int = var82
            val var50: ByteArray = `this_$ivx`.data
            var var52: Int = 0

            for (var55 in var50) {
               val var57: Int = var52++
               val var59: Byte = var55

               for (var61 in 7 downTo 0) {
                  if ((var59 and 1 shl var61) != 0) {
                     val var66: Int = var57 * 8 + var61
                     val var67: Vector3ic = SingleChunkDenseBlockPosSet.Companion.dimensions
                     val var69: Int = var66 / (var67.x() * var67.y())
                     fn(
                        (var66 - var69 * var67.x() * var67.y()) % var67.x() + (var46 shl 4),
                        (var66 - var69 * var67.x() * var67.y()) / var67.x() + (var45 shl 4),
                        var69 + (var44 shl 4)
                     )
                  }
               }
            }
         }
      }
   }

   // $VF: bridge methodSupplier
   override final fun size(): Int {
      this.size
   }
}
