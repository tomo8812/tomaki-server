package kotlinx.coroutines.internal

import java.util.concurrent.atomic.AtomicReferenceArray

internal class ResizableAtomicArray<T>(initialLength: Int) {
   private final var array: AtomicReferenceArray<Any>

   init {
      this.array = AtomicReferenceArray<>(initialLength)
   }

   public fun currentLength(): Int {
      return this.array.length()
   }

   public operator fun get(index: Int): Any? {
      val array: AtomicReferenceArray = this.array
      return (T)(if (index < this.array.length()) array.get(index) else null)
   }

   public fun setSynchronized(index: Int, value: Any?) {
      val curArray: AtomicReferenceArray = this.array
      val curLen: Int = this.array.length()
      if (index < curLen) {
         curArray.set(index, value)
      } else {
         val newArray: AtomicReferenceArray = AtomicReferenceArray(RangesKt.coerceAtLeast(index + 1, 2 * curLen))

         repeat(curLen) { i ->
            newArray.set(i, curArray.get(i))
         }

         newArray.set(index, value)
         this.array = newArray
      }
   }
}
