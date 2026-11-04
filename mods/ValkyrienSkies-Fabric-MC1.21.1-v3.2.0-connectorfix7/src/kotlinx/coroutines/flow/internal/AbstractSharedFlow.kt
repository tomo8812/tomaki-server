package kotlinx.coroutines.flow.internal

import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.StateFlow

@SourceDebugExtension(["SMAP\nAbstractSharedFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractSharedFlow.kt\nkotlinx/coroutines/flow/internal/AbstractSharedFlow\n+ 2 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 3 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,132:1\n28#2,4:133\n28#2,4:138\n28#2,4:144\n20#3:137\n20#3:142\n20#3:148\n1#4:143\n13579#5,2:149\n*S KotlinDebug\n*F\n+ 1 AbstractSharedFlow.kt\nkotlinx/coroutines/flow/internal/AbstractSharedFlow\n*L\n30#1:133,4\n45#1:138,4\n76#1:144,4\n30#1:137\n45#1:142\n76#1:148\n95#1:149,2\n*E\n"])
internal abstract class AbstractSharedFlow<S extends AbstractSharedFlowSlot<?>> {
   protected final var slots: Array<Any?>?
      private set

   protected final var nCollectors: Int
      private set

   private final var nextIndex: Int
   private final var _subscriptionCount: SubscriptionCountStateFlow?

   public final val subscriptionCount: StateFlow<Int>
      public final get() {
         var var10: SubscriptionCountStateFlow
         synchronized (this) {
            var10 = this._subscriptionCount
            if (this._subscriptionCount == null) {
               val var5: SubscriptionCountStateFlow = SubscriptionCountStateFlow(this.nCollectors)
               this._subscriptionCount = var5
               var10 = var5
            }

            var10 = var10
         }

         return var10
      }


   protected abstract fun createSlot(): Any {
   }

   protected abstract fun createSlotArray(size: Int): Array<Any?> {
   }

   protected fun allocateSlot(): Any {
      val var16: SubscriptionCountStateFlow
      var var28: AbstractSharedFlowSlot
      synchronized (this) {
         val var10000: Array<AbstractSharedFlowSlot>
         if (this.slots == null) {
            val slot: Array<AbstractSharedFlowSlot> = this.createSlotArray(2)
            this.slots = (S[])slot
            var10000 = slot
         } else if (this.nCollectors >= this.slots.length) {
            val var26: Array<Any> = Arrays.copyOf(this.slots, 2 * this.slots.length)
            this.slots = (S[])var26
            var10000 = var26 as Array<AbstractSharedFlowSlot>
         } else {
            var10000 = this.slots
         }

         val slots: Array<AbstractSharedFlowSlot> = var10000
         var var18: Int = this.nextIndex

         do {
            var28 = slots[var18]
            if (slots[var18] == null) {
               val var25: AbstractSharedFlowSlot = this.createSlot()
               slots[var18] = var25
               var28 = var25
            }

            if (++var18 >= slots.length) {
               var18 = 0
            }
         } while (!var28.allocateLocked(this))

         this.nextIndex = var18
         val var23: Int = this.nCollectors++
         var16 = this._subscriptionCount
         var28 = var28
      }

      if (var16 != null) {
         var16.increment(1)
      }

      return (S)var28
   }

   protected fun freeSlot(slot: Any) {
      val var11: SubscriptionCountStateFlow
      val var10000: Array<Continuation>
      synchronized (this) {
         this.nCollectors += -1
         var11 = this._subscriptionCount
         if (this.nCollectors == 0) {
            this.nextIndex = 0
         }

         var10000 = slot.freeLocked(this)
      }

      for (var14 in var10000) {
         if (var14 != null) {
            var14.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
         }
      }

      if (var11 != null) {
         var11.increment(-1)
      }
   }

   protected inline fun forEachSlotLocked(block: (Any) -> Unit) {
      if (access$getNCollectors(this) != 0) {
         val var10000: Array<Any> = access$getSlots(this)
         if (var10000 != null) {
            for (`element$iv` in var10000) {
               if (`element$iv` != null) {
                  block(`element$iv`)
               }
            }
         }
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getNCollectors`(`$this`: AbstractSharedFlow): Int {
      `$this`.nCollectors
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getSlots`(`$this`: AbstractSharedFlow): Array<AbstractSharedFlowSlot> {
      `$this`.slots
   }
}
