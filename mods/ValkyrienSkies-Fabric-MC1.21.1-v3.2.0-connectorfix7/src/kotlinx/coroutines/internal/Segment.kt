package kotlinx.coroutines.internal

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.NotCompleted

@SourceDebugExtension(["SMAP\nConcurrentLinkedList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/Segment\n+ 2 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/ConcurrentLinkedListKt\n*L\n1#1,269:1\n252#2,4:270\n*S KotlinDebug\n*F\n+ 1 ConcurrentLinkedList.kt\nkotlinx/coroutines/internal/Segment\n*L\n225#1:270,4\n*E\n"])
internal abstract class Segment<S extends Segment<S>> : ConcurrentLinkedListNode<S>, NotCompleted {
   @JvmField
   public final val id: Long

   @Volatile
   @Volatile
   private int cleanedAndPointers;
   @JvmStatic
   private AtomicIntegerFieldUpdater cleanedAndPointers$FU = AtomicIntegerFieldUpdater.newUpdater(Segment.class, "cleanedAndPointers");

   open fun Segment(id: Long, prev: S?, pointers: Int) {
      super((S)prev)
      this.id = id
      this.cleanedAndPointers = pointers shl 16
   }

   public abstract val numberOfSlots: Int

   public open val isRemoved: Boolean
      public open get() {
         return cleanedAndPointers$FU.get(this) == this.numberOfSlots && !this.isTail()
      }


   internal fun tryIncPointers(): Boolean {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = cleanedAndPointers$FU
      val `delta$iv`: Int = 65536

      var var10000: Boolean
      while (true) {
         val `cur$iv`: Int = `atomicfu$handler$iv`.get(this)
         if (`cur$iv` == this.numberOfSlots && !this.isTail()) {
            var10000 = false
            break
         }

         if (`atomicfu$handler$iv`.compareAndSet(this, `cur$iv`, `cur$iv` + `delta$iv`)) {
            var10000 = true
            break
         }
      }

      return var10000
   }

   internal fun decPointers(): Boolean {
      return cleanedAndPointers$FU.addAndGet(this, -65536) == this.numberOfSlots && !this.isTail()
   }

   public abstract fun onCancellation(index: Int, cause: Throwable?, context: CoroutineContext) {
   }

   public fun onSlotCleaned() {
      if (cleanedAndPointers$FU.incrementAndGet(this) == this.numberOfSlots) {
         this.remove()
      }
   }
}
