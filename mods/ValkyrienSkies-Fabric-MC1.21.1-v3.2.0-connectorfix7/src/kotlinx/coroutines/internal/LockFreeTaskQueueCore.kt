package kotlinx.coroutines.internal

import java.util.ArrayList
import java.util.concurrent.atomic.AtomicLongFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nLockFreeTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LockFreeTaskQueue.kt\nkotlinx/coroutines/internal/LockFreeTaskQueueCore\n+ 2 LockFreeTaskQueue.kt\nkotlinx/coroutines/internal/LockFreeTaskQueueCore$Companion\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,308:1\n299#2,3:309\n299#2,3:312\n299#2,3:315\n299#2,3:318\n299#2,3:321\n299#2,3:325\n299#2,3:328\n1#3:324\n*S KotlinDebug\n*F\n+ 1 LockFreeTaskQueue.kt\nkotlinx/coroutines/internal/LockFreeTaskQueueCore\n*L\n91#1:309,3\n92#1:312,3\n107#1:315,3\n167#1:318,3\n200#1:321,3\n231#1:325,3\n247#1:328,3\n*E\n"])
internal class LockFreeTaskQueueCore<E>(capacity: Int, singleConsumer: Boolean) {
   private final val capacity: Int
   private final val singleConsumer: Boolean
   private final val mask: Int
   @Volatile
   @Volatile
   private Object _next;
   @Volatile
   @Volatile
   private long _state;
   private AtomicReferenceArray array;
   @JvmStatic
   private AtomicReferenceFieldUpdater _next$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeTaskQueueCore.class, Object.class, "_next");
   @JvmStatic
   private AtomicLongFieldUpdater _state$FU = AtomicLongFieldUpdater.newUpdater(LockFreeTaskQueueCore.class, "_state");

   init {
      this.capacity = capacity
      this.singleConsumer = singleConsumer
      this.mask = this.capacity - 1
      this.array = AtomicReferenceArray(this.capacity)
      if (this.mask > 1073741823) {
         throw IllegalStateException("Check failed.".toString())
      } else if ((this.capacity and this.mask) != 0) {
         throw IllegalStateException("Check failed.".toString())
      }
   }

   public final val isEmpty: Boolean
      public final get() {
         val `this_$iv`: LockFreeTaskQueueCore.Companion = Companion
         val `$this$withState$iv`: Long = _state$FU.get(this)
         return (int)((`$this$withState$iv` and 1073741823L) shr 0) == (int)((`$this$withState$iv` and 1152921503533105152L) shr 30)
      }


   public final val size: Int
      public final get() {
         val `this_$iv`: LockFreeTaskQueueCore.Companion = Companion
         val `$this$withState$iv`: Long = _state$FU.get(this)
         return (int)((`$this$withState$iv` and 1152921503533105152L) shr 30) - (int)((`$this$withState$iv` and 1073741823L) shr 0) and 1073741823
      }


   public fun close(): Boolean {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = _state$FU

      val var4: Long
      do {
         var4 = `atomicfu$handler$iv`.get(this)
         if ((var4 and 2305843009213693952L) != 0L) {
            return true
         }

         if ((var4 and 1152921504606846976L) != 0L) {
            return false
         }
      } while (!`atomicfu$handler$iv`.compareAndSet(this, var4, var4 or 2305843009213693952L))

      return true
   }

   public fun addLast(element: Any): Int {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = _state$FU

      while (true) {
         val state: Long = `atomicfu$handler$iv`.get(this)
         if ((state and 3458764513820540928L) != 0L) {
            return Companion.addFailReason(state)
         }

         val `this_$iv`: LockFreeTaskQueueCore.Companion = Companion
         val `head$iv`: Int = (int)((state and 1073741823L) shr 0)
         val tail: Int = (int)((state and 1152921503533105152L) shr 30)
         val mask: Int = this.mask
         if ((tail + 2 and this.mask) == (`head$iv` and this.mask)) {
            return 1
         }

         if (!this.singleConsumer && this.array.get(tail and this.mask) != null) {
            if (this.capacity < 1024 || (tail - `head$iv` and 1073741823) > this.capacity shr 1) {
               return 1
            }
         } else if (_state$FU.compareAndSet(this, state, Companion.updateTail(state, tail + 1 and 1073741823))) {
            this.array.set(tail and mask, element)
            var cur: LockFreeTaskQueueCore = this

            while ((_state$FU.get(cur) and 1152921504606846976L) != 0L) {
               val var10000: LockFreeTaskQueueCore = cur.next().fillPlaceholder(tail, element)
               if (var10000 == null) {
                  break
               }

               cur = var10000
            }

            return 0
         }
      }
   }

   private fun fillPlaceholder(index: Int, element: Any): LockFreeTaskQueueCore<Any>? {
      val old: Any = this.array.get(index and this.mask)
      if (old is LockFreeTaskQueueCore.Placeholder && (old as LockFreeTaskQueueCore.Placeholder).index == index) {
         this.array.set(index and this.mask, element)
         return this
      } else {
         return null
      }
   }

   public fun removeFirstOrNull(): Any? {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = _state$FU

      while (true) {
         val state: Long = `atomicfu$handler$iv`.get(this)
         if ((state and 1152921504606846976L) != 0L) {
            return REMOVE_FROZEN
         }

         val `this_$iv`: LockFreeTaskQueueCore.Companion = Companion
         val `head$iv`: Int = (int)((state and 1073741823L) shr 0)
         val tail: Int = (int)((state and 1152921503533105152L) shr 30)
         val head: Int = `head$iv`
         if ((tail and this.mask) == (`head$iv` and this.mask)) {
            return null
         }

         val element: Any = this.array.get(`head$iv` and this.mask)
         if (element == null) {
            if (this.singleConsumer) {
               return null
            }
         } else {
            if (element is LockFreeTaskQueueCore.Placeholder) {
               return null
            }

            val newHead: Int = `head$iv` + 1 and 1073741823
            if (_state$FU.compareAndSet(this, state, Companion.updateHead(state, `head$iv` + 1 and 1073741823))) {
               this.array.set(`head$iv` and this.mask, null)
               return element
            }

            if (this.singleConsumer) {
               var cur: LockFreeTaskQueueCore = this

               while (true) {
                  val var10000: LockFreeTaskQueueCore = cur.removeSlowPath(head, newHead)
                  if (var10000 == null) {
                     return element
                  }

                  cur = var10000
               }
            }
         }
      }
   }

   private fun removeSlowPath(oldHead: Int, newHead: Int): LockFreeTaskQueueCore<Any>? {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = _state$FU

      val state: Long
      val `head$iv`: Int
      do {
         state = `atomicfu$handler$iv`.get(this)
         val `this_$iv`: LockFreeTaskQueueCore.Companion = Companion
         `head$iv` = (int)((state and 1073741823L) shr 0)
         val `tail$iv`: Int = (int)((state and 1152921503533105152L) shr 30)
         if (ASSERTIONS_ENABLED && `head$iv` != oldHead) {
            throw AssertionError()
         }

         if ((state and 1152921504606846976L) != 0L) {
            return this.next()
         }
      } while (!_state$FU.compareAndSet(this, state, Companion.updateHead(state, newHead)))

      this.array.set(`head$iv` and this.mask, null)
      return null
   }

   public fun next(): LockFreeTaskQueueCore<Any> {
      return this.allocateOrGetNextCopy(this.markFrozen())
   }

   private fun markFrozen(): Long {
      val `atomicfu$handler$iv`: AtomicLongFieldUpdater = _state$FU

      val var4: Long
      do {
         var4 = `atomicfu$handler$iv`.get(this)
         if ((var4 and 1152921504606846976L) != 0L) {
            return var4
         }
      } while (!`atomicfu$handler$iv`.compareAndSet(this, var4, var4 or 1152921504606846976L))

      return var4 or 1152921504606846976L
   }

   private fun allocateOrGetNextCopy(state: Long): LockFreeTaskQueueCore<Any> {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _next$FU

      while (true) {
         val next: LockFreeTaskQueueCore = `atomicfu$handler$iv`.get(this) as LockFreeTaskQueueCore
         if (next != null) {
            return next
         }

         _next$FU.compareAndSet(this, null, this.allocateNextCopy(state))
      }
   }

   private fun allocateNextCopy(state: Long): LockFreeTaskQueueCore<Any> {
      val next: LockFreeTaskQueueCore = LockFreeTaskQueueCore(this.capacity * 2, this.singleConsumer)
      val `this_$iv`: LockFreeTaskQueueCore.Companion = Companion
      val `head$iv`: Int = (int)((state and 1073741823L) shr 0)
      val tail: Int = (int)((state and 1152921503533105152L) shr 30)

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var index: Int = `head$iv`
      while (true) {
         if ((index and this.mask) != (tail and this.mask)) break
         var var10000: Any = this.array.get(index and this.mask)
         if (var10000 == null) {
            var10000 = LockFreeTaskQueueCore.Placeholder(index)
         }

         next.array.set(index and next.mask, var10000)

         index++
      }

      _state$FU.set(next, state wo 1152921504606846976L)
      return next
   }

   public fun <R> map(transform: (Any) -> Any): List<Any> {
      val res: ArrayList = ArrayList(this.capacity)
      val `this_$iv`: LockFreeTaskQueueCore.Companion = Companion
      val `$this$withState$iv`: Long = _state$FU.get(this)
      val `head$iv`: Int = (int)((`$this$withState$iv` and 1073741823L) shr 0)
      val tail: Int = (int)((`$this$withState$iv` and 1152921503533105152L) shr 30)

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var index: Int = `head$iv`
      while (true) {
         if ((index and this.mask) != (tail and this.mask)) break
         val element: Any = this.array.get(index and this.mask)
         if (element != null && element !is LockFreeTaskQueueCore.Placeholder) {
            res.add(transform(element))
         }

         index++
      }

      return res
   }

   public fun isClosed(): Boolean {
      return (_state$FU.get(this) and 2305843009213693952L) != 0L
   }

   fun `update$atomicfu`(`atomicfu$handler`: AtomicLongFieldUpdater, `atomicfu$action`: (java.lang.Long?) -> java.lang.Long, `atomicfu$dispatchReceiver`: Any) {
      val var5: Long
      do {
         var5 = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
      } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, var5, (`atomicfu$action`(var5) as java.lang.Number).longValue()))
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicLongFieldUpdater, `atomicfu$action`: (java.lang.Long?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   fun `updateAndGet$atomicfu`(
      `atomicfu$handler`: AtomicLongFieldUpdater, `atomicfu$action`: (java.lang.Long?) -> java.lang.Long, `atomicfu$dispatchReceiver`: Any
   ): Long {
      val var5: Long
      val var7: Any
      do {
         var5 = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
         var7 = `atomicfu$action`(var5)
      } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, var5, (var7 as java.lang.Number).longValue()))

      (var7 as java.lang.Number).longValue()
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   internal companion object {
      public const val ADD_CLOSED: Int = 2
      public const val ADD_FROZEN: Int = 1
      public const val ADD_SUCCESS: Int = 0
      public const val CAPACITY_BITS: Int = 30
      public const val CLOSED_MASK: Long = 2305843009213693952L
      public const val CLOSED_SHIFT: Int = 61
      public const val FROZEN_MASK: Long = 1152921504606846976L
      public const val FROZEN_SHIFT: Int = 60
      public const val HEAD_MASK: Long = 1073741823L
      public const val HEAD_SHIFT: Int = 0
      public const val INITIAL_CAPACITY: Int = 8
      public const val MAX_CAPACITY_MASK: Int = 1073741823
      public const val MIN_ADD_SPIN_CAPACITY: Int = 1024

      @JvmField
      @NotNull
      public final val REMOVE_FROZEN: Symbol

      public const val TAIL_MASK: Long = 1152921503533105152L
      public const val TAIL_SHIFT: Int = 30

      public infix fun Long.wo(other: Long): Long {
         return `$this$wo` and other.inv()
      }

      public fun Long.updateHead(newHead: Int): Long {
         return (`$this$updateHead` wo 1073741823L) or (long)newHead shl 0
      }

      public fun Long.updateTail(newTail: Int): Long {
         return (`$this$updateTail` wo 1152921503533105152L) or (long)newTail shl 30
      }

      public inline fun <T> Long.withState(block: (Int, Int) -> Any): Any {
         return (T)block((int)((`$this$withState` and 1073741823L) shr 0), (int)((`$this$withState` and 1152921503533105152L) shr 30))
      }

      public fun Long.addFailReason(): Int {
         return if ((`$this$addFailReason` and 2305843009213693952L) != 0L) 2 else 1
      }
   }

   internal class Placeholder(index: Int) {
      @JvmField
      public final val index: Int

      init {
         this.index = index
      }
   }
}
