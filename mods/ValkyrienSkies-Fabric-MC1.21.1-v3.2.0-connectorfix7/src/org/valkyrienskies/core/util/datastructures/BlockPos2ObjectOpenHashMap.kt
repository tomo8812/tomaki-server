package org.valkyrienskies.core.util.datastructures

import it.unimi.dsi.fastutil.HashCommon
import kotlin.jvm.internal.SourceDebugExtension
import org.joml.Vector3ic

@SourceDebugExtension(["SMAP\nBlockPos2ObjectOpenHashMap.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BlockPos2ObjectOpenHashMap.kt\norg/valkyrienskies/core/util/datastructures/BlockPos2ObjectOpenHashMap\n*L\n1#1,273:1\n68#1,10:274\n*S KotlinDebug\n*F\n+ 1 BlockPos2ObjectOpenHashMap.kt\norg/valkyrienskies/core/util/datastructures/BlockPos2ObjectOpenHashMap\n*L\n80#1:274,10\n*E\n"])
public class BlockPos2ObjectOpenHashMap<T>(expected: Int = 10, loadFactor: Float = 0.75F) {
   public final var n: Int
      private set

   private final var size: Int

   public final var keys: IntArray
      private set

   public final var values: Array<Any?>
      private set

   public final var containsNullKey: Boolean
      private set

   private final var maxFill: Int
   public final var defRetValue: Any?
   private final val minN: Int
   private final val f: Float

   init {
      this.f = loadFactor
      this.n = HashCommon.arraySize(expected, this.f)
      this.minN = this.n
      this.maxFill = HashCommon.maxFill(this.n, loadFactor)
      this.keys = IntArray((this.n + 1) * 3)
      this.values = (T[])arrayOfNulls(this.n + 1)
   }

   private final val mask: Int
      private final get() {
         return this.n - 1
      }


   private final val realSize: Int
      private final get() {
         return if (this.containsNullKey) this.size - 1 else this.size
      }


   public fun get(x: Int, y: Int, z: Int): Any? {
      val pos: Int = this.find(x, y, z)
      return if (pos < 0) this.defRetValue else this.values[pos]
   }

   public fun clear() {
      if (this.size != 0) {
         this.size = 0
         this.containsNullKey = false
         ArraysKt.fill$default(this.keys, 0, 0, 0, 6, null)
         ArraysKt.fill$default(this.values, null, 0, 0, 6, null)
      }
   }

   public fun getOrPut(x: Int, y: Int, z: Int, default: () -> Any): Any {
      val var10000: Any
      if (this.contains(x, y, z)) {
         var10000 = this.get(x, y, z)
      } else {
         val newValue: Any = default()
         this.put(x, y, z, (T)newValue)
         var10000 = newValue
      }

      return (T)var10000
   }

   public fun getOrPut(pos: Vector3ic, default: () -> Any): Any {
      return this.getOrPut(pos.x(), pos.y(), pos.z(), default)
   }

   public inline fun forEach(fn: (Int, Int, Int, Any) -> Unit) {
      if (this.containsNullKey) {
         fn(this.keys[this.n * 3], this.keys[this.n * 3 + 1], this.keys[this.n * 3 + 2], this.values[this.n])
      }

      for (pos in this.n downTo 0) {
         if (this.keys[pos * 3] != 0 || this.keys[pos * 3 + 1] != 0 || this.keys[pos * 3 + 2] != 0) {
            fn(this.keys[pos * 3], this.keys[pos * 3 + 1], this.keys[pos * 3 + 2], this.values[pos])
         }
      }
   }

   public fun forEach(fn: org.valkyrienskies.core.util.datastructures.BlockPos2ObjectOpenHashMap.EntryConsumer<Any>) {
      val `this_$iv`: BlockPos2ObjectOpenHashMap = this
      if (this.containsNullKey) {
         fn.accept(this.keys[this.n * 3], this.keys[this.n * 3 + 1], this.keys[this.n * 3 + 2], this.values[this.n])
      }

      for (`pos$iv` in this.n downTo 0) {
         if (`this_$iv`.keys[`pos$iv` * 3] != 0 || `this_$iv`.keys[`pos$iv` * 3 + 1] != 0 || `this_$iv`.keys[`pos$iv` * 3 + 2] != 0) {
            fn.accept(`this_$iv`.keys[`pos$iv` * 3], `this_$iv`.keys[`pos$iv` * 3 + 1], `this_$iv`.keys[`pos$iv` * 3 + 2], `this_$iv`.values[`pos$iv`])
         }
      }
   }

   public fun put(x: Int, y: Int, z: Int, v: Any?): Any? {
      val pos: Int = this.find(x, y, z)
      if (pos < 0) {
         this.insert(-pos - 1, x, y, z, (T)v)
         return this.defRetValue
      } else {
         val oldValue: Any = this.values[pos]
         this.values[pos] = (T)v
         return (T)oldValue
      }
   }

   public fun remove(x: Int, y: Int, z: Int): Any? {
      if (x == 0 && y == 0 && z == 0) {
         return if (this.containsNullKey) this.removeNullEntry() else this.defRetValue
      } else {
         val pos: Int = this.find(x, y, z)
         return if (pos < 0) this.defRetValue else this.removeEntry(pos)
      }
   }

   public fun contains(x: Int, y: Int, z: Int): Boolean {
      return this.find(x, y, z) >= 0
   }

   private fun removeNullEntry(): Any? {
      this.containsNullKey = false
      val oldValue: Any = this.values[this.n]
      this.size += -1
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2)
      }

      return (T)oldValue
   }

   private fun removeEntry(pos: Int): Any? {
      val oldValue: Any = this.values[pos]
      this.size += -1
      this.shiftKeys(pos)
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2)
      }

      return (T)oldValue
   }

   private fun shiftKeys(pos: Int) {
      var posx: Int = pos
      val key: IntArray = this.keys

      label56@ while (true) {
         val var9: Int = posx
         posx = posx + 1 and this.mask

         while (true) {
            val var11: Int = key[posx * 3]
            val var12: Int = key[posx * 3 + 1]
            val var13: Int = key[posx * 3 + 2]
            if (var11 == 0 && var12 == 0 && key[posx * 3 + 2] == 0) {
               key[var9 * 3] = 0
               key[var9 * 3 + 1] = 0
               key[var9 * 3 + 2] = 0
               return
            }

            val var10: Int = this.hash(var11, var12, var13) and this.mask
            if (if (var9 <= posx) var9 >= var10 || var10 > posx else var9 >= var10 && var10 > posx) {
               key[var9 * 3] = var11
               key[var9 * 3 + 1] = var12
               key[var9 * 3 + 2] = var13
               this.values[var9] = this.values[posx]
               continue@label56
            }

            posx = posx + 1 and this.mask
         }
      }
   }

   private fun hash(x: Int, y: Int, z: Int): Int {
      return MurmurHash3.INSTANCE.fmix32(MurmurHash3.INSTANCE.mix32(z, MurmurHash3.INSTANCE.mix32(y, MurmurHash3.INSTANCE.mix32(x, 0))) xor 12)
   }

   private fun insert(pos: Int, x: Int, y: Int, z: Int, v: Any?) {
      if (pos == this.n) {
         this.containsNullKey = true
      }

      val keyPos: Int = pos * 3
      this.keys[pos * 3] = x
      this.keys[keyPos + 1] = y
      this.keys[keyPos + 2] = z
      this.values[pos] = (T)v
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f))
      }
   }

   private fun rehash(newN: Int) {
      val keys: IntArray = this.keys
      val values: Array<Any> = this.values
      val newMask: Int = newN - 1
      val newKey: IntArray = IntArray((newN + 1) * 3)
      val newValue: Array<Any> = arrayOfNulls(newN + 1)
      val j: Int = this.realSize
      var i: Int = this.n * 3

      while (j-- != 0) {
         do {
            i -= 3
         } while (keys[i] == 0 && keys[i + 1] == 0 && keys[i + 2] == 0)

         val oldPos: Int = i / 3
         var var12: Int = this.hash(keys[i], keys[i + 1], keys[i + 2]) and newMask

         // $VF: Unable to resugar Kotlin loop from Java for loop
         keyPos = var12 * 3
         while (true) {
            if (newKey[keyPos] != 0 || newKey[keyPos + 1] != 0 || newKey[keyPos + 2] != 0) break
            var12 = var12 + 1 and newMask

            keyPos = var12 * 3
         }

         newKey[keyPos] = keys[i]
         newKey[keyPos + 1] = keys[i + 1]
         newKey[keyPos + 2] = keys[i + 2]
         newValue[var12] = values[oldPos]
      }

      newValue[newN] = values[this.n]
      this.n = newN
      this.maxFill = HashCommon.maxFill(this.n, this.f)
      this.keys = newKey
      this.values = (T[])newValue
   }

   private fun find(x: Int, y: Int, z: Int): Int {
      if (x == 0 && y == 0 && z == 0) {
         return if (this.containsNullKey) this.n else -(this.n + 1)
      } else {
         val keys: IntArray = this.keys
         val mask: Int = this.mask
         var pos: Int = this.hash(x, y, z) and mask

         while (true) {
            val keyPos: Int = pos * 3
            val curX: Int = keys[pos * 3]
            val curY: Int = keys[pos * 3 + 1]
            val curZ: Int = keys[keyPos + 2]
            if (curX == 0 && curY == 0 && keys[keyPos + 2] == 0) {
               return -(pos + 1)
            }

            if (x == curX && y == curY && z == curZ) {
               return pos
            }

            pos = pos + 1 and mask
         }
      }
   }

   public operator fun set(pos: Vector3ic, value: Any): Any? {
      return this.put(pos.x(), pos.y(), pos.z(), (T)value)
   }

   fun BlockPos2ObjectOpenHashMap() {
      this(0, 0.0F, 3, null)
   }

   public companion object {
      public const val NUM_KEYS: Int = 3
   }

   public fun interface EntryConsumer<T> {
      public abstract fun accept(x: Int, y: Int, z: Int, v: Any) {
      }
   }
}
