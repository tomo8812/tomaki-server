package kotlinx.coroutines

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.LimitedDispatcherKt

@SourceDebugExtension(["SMAP\nEventLoop.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventLoop.common.kt\nkotlinx/coroutines/EventLoop\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,543:1\n1#2:544\n*E\n"])
internal abstract class EventLoop : CoroutineDispatcher {
   private final var useCount: Long
   private final var shared: Boolean
   private final var unconfinedQueue: ArrayDeque<DispatchedTask<*>>?

   public open fun processNextEvent(): Long {
      return if (!this.processUnconfinedEvent()) java.lang.Long.MAX_VALUE else 0L
   }

   protected open val isEmpty: Boolean
      protected open get() {
         return this.isUnconfinedQueueEmpty
      }


   protected open val nextTime: Long
      protected open get() {
         if (this.unconfinedQueue == null) {
            return java.lang.Long.MAX_VALUE
         } else {
            return if (this.unconfinedQueue.isEmpty()) java.lang.Long.MAX_VALUE else 0L
         }
      }


   public fun processUnconfinedEvent(): Boolean {
      if (this.unconfinedQueue == null) {
         return false
      } else {
         val var10000: DispatchedTask = this.unconfinedQueue.removeFirstOrNull() as DispatchedTask
         if (var10000 == null) {
            return false
         } else {
            var10000.run()
            return true
         }
      }
   }

   public open fun shouldBeProcessedFromContext(): Boolean {
      return false
   }

   public fun dispatchUnconfined(task: DispatchedTask<*>) {
      var var10000: ArrayDeque = this.unconfinedQueue
      if (this.unconfinedQueue == null) {
         val var3: ArrayDeque = ArrayDeque()
         this.unconfinedQueue = var3
         var10000 = var3
      }

      var10000.addLast(task)
   }

   public final val isActive: Boolean
      public final get() {
         return this.useCount > 0L
      }


   public final val isUnconfinedLoopActive: Boolean
      public final get() {
         return this.useCount >= this.delta(true)
      }


   public final val isUnconfinedQueueEmpty: Boolean
      public final get() {
         return this.unconfinedQueue == null || this.unconfinedQueue.isEmpty()
      }


   private fun delta(unconfined: Boolean): Long {
      return if (unconfined) 4294967296L else 1L
   }

   public fun incrementUseCount(unconfined: Boolean = false) {
      this.useCount = this.useCount + this.delta(unconfined)
      if (!unconfined) {
         this.shared = true
      }
   }

   public fun decrementUseCount(unconfined: Boolean = false) {
      this.useCount = this.useCount - this.delta(unconfined)
      if (this.useCount <= 0L) {
         if (ASSERTIONS_ENABLED && this.useCount != 0L) {
            throw AssertionError()
         } else {
            if (this.shared) {
               this.shutdown()
            }
         }
      }
   }

   public override fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
      LimitedDispatcherKt.checkParallelism(parallelism)
      return this
   }

   public open fun shutdown() {
   }
}
