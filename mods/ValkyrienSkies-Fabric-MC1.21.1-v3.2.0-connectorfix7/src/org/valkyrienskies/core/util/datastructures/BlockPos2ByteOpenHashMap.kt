package org.valkyrienskies.core.util.datastructures

import it.unimi.dsi.fastutil.HashCommon

public class BlockPos2ByteOpenHashMap(expected: Int = 10, loadFactor: Float = 0.75F) {
   private final var n: Int
   private final var size: Int
   private final var keys: IntArray
   private final var values: ByteArray
   private final var containsNullKey: Boolean
   private final var maxFill: Int
   public final var defRetValue: Byte
   private final val minN: Int
   private final val f: Float

   init {
      this.f = loadFactor
      this.n = HashCommon.arraySize(expected, this.f)
      this.minN = this.n
      this.maxFill = HashCommon.maxFill(this.n, loadFactor)
      this.keys = IntArray((this.n + 1) * 3)
      this.values = ByteArray(this.n + 1)
   }

   private final val mask: Int
      private final get() {
         return this.n - 1
      }


   private final val realSize: Int
      private final get() {
         return if (this.containsNullKey) this.size - 1 else this.size
      }


   public fun get(x: Int, y: Int, z: Int): Byte {
      val pos: Int = this.find(x, y, z)
      return if (pos < 0) this.defRetValue else this.values[pos]
   }

   public fun put(x: Int, y: Int, z: Int, v: Byte): Byte {
      val pos: Int = this.find(x, y, z)
      if (pos < 0) {
         this.insert(-pos - 1, x, y, z, v)
         return this.defRetValue
      } else {
         val oldValue: Byte = this.values[pos]
         this.values[pos] = v
         return oldValue
      }
   }

   public fun remove(x: Int, y: Int, z: Int): Byte {
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

   private fun removeNullEntry(): Byte {
      this.containsNullKey = false
      val oldValue: Byte = this.values[this.n]
      this.size += -1
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2)
      }

      return oldValue
   }

   private fun removeEntry(pos: Int): Byte {
      val oldValue: Byte = this.values[pos]
      this.size += -1
      this.shiftKeys(pos)
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2)
      }

      return oldValue
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

   private fun insert(pos: Int, x: Int, y: Int, z: Int, v: Byte) {
      if (pos == this.n) {
         this.containsNullKey = true
      }

      val keyPos: Int = pos * 3
      this.keys[pos * 3] = x
      this.keys[keyPos + 1] = y
      this.keys[keyPos + 2] = z
      this.values[pos] = v
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f))
      }
   }

   private fun rehash(newN: Int) {
      val keys: IntArray = this.keys
      val values: ByteArray = this.values
      val newMask: Int = newN - 1
      val newKey: IntArray = IntArray((newN + 1) * 3)
      val newValue: ByteArray = ByteArray(newN + 1)
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
      this.values = newValue
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

   fun BlockPos2ByteOpenHashMap() {
      this(0, 0.0F, 3, null)
   }

   public companion object {
      private const val NUM_KEYS: Int = 3
   }
}
