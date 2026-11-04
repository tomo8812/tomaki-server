package kotlinx.coroutines.internal

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandlerKt
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@SourceDebugExtension(["SMAP\nLimitedDispatcher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LimitedDispatcher.kt\nkotlinx/coroutines/internal/LimitedDispatcher\n+ 2 Synchronized.common.kt\nkotlinx/coroutines/internal/Synchronized_commonKt\n+ 3 Synchronized.kt\nkotlinx/coroutines/internal/SynchronizedKt\n*L\n1#1,134:1\n66#1,8:135\n66#1,8:143\n28#2,4:151\n28#2,4:156\n20#3:155\n20#3:160\n*S KotlinDebug\n*F\n+ 1 LimitedDispatcher.kt\nkotlinx/coroutines/internal/LimitedDispatcher\n*L\n48#1:135,8\n55#1:143,8\n79#1:151,4\n92#1:156,4\n79#1:155\n92#1:160\n*E\n"])
internal class LimitedDispatcher(dispatcher: CoroutineDispatcher, parallelism: Int) : CoroutineDispatcher, Delay {
   private final val dispatcher: CoroutineDispatcher
   private final val parallelism: Int
   @JvmSynthetic
   private Delay $$delegate_0;
   @Volatile
   @Volatile
   private int runningWorkers;
   private final val queue: LockFreeTaskQueue<Runnable>
   private final val workerAllocationLock: Any
   @JvmStatic
   private AtomicIntegerFieldUpdater runningWorkers$FU = AtomicIntegerFieldUpdater.newUpdater(LimitedDispatcher.class, "runningWorkers");

   init {
      this.dispatcher = dispatcher
      this.parallelism = parallelism
      var var10001: Delay = dispatcher as? Delay
      if ((dispatcher as? Delay) == null) {
         var10001 = DefaultDelay
      }

      this.$$delegate_0 = var10001
      this.queue = LockFreeTaskQueue<>(false)
      this.workerAllocationLock = Any()
   }

   @Deprecated(message = "Deprecated without replacement as an internal method never intended for public use", level = DeprecationLevel.ERROR)
   public override suspend fun delay(time: Long) {
      return this.$$delegate_0.delay(time, `$completion`)
   }

   public override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
      return this.$$delegate_0.invokeOnTimeout(timeMillis, block, context)
   }

   public override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
      this.$$delegate_0.scheduleResumeAfterDelay(timeMillis, continuation)
   }

   @ExperimentalCoroutinesApi
   public override fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
      LimitedDispatcherKt.checkParallelism(parallelism)
      return if (parallelism >= this.parallelism) this else super.limitedParallelism(parallelism)
   }

   public override fun dispatch(context: CoroutineContext, block: Runnable) {
      this.queue.addLast(block)
      if (runningWorkers$FU.get(this) < this.parallelism && this.tryAllocateWorker()) {
         val var10000: Runnable = this.obtainTaskOrDeallocateWorker()
         if (var10000 != null) {
            this.dispatcher.dispatch(this as CoroutineContext, LimitedDispatcher.Worker(var10000))
         }
      }
   }

   @InternalCoroutinesApi
   public override fun dispatchYield(context: CoroutineContext, block: Runnable) {
      this.queue.addLast(block)
      if (runningWorkers$FU.get(this) < this.parallelism && this.tryAllocateWorker()) {
         val var10000: Runnable = this.obtainTaskOrDeallocateWorker()
         if (var10000 != null) {
            this.dispatcher.dispatchYield(this as CoroutineContext, LimitedDispatcher.Worker(var10000))
         }
      }
   }

   private inline fun dispatchInternal(block: Runnable, startWorker: (kotlinx.coroutines.internal.LimitedDispatcher.Worker) -> Unit) {
      this.queue.addLast(block)
      if (runningWorkers$FU.get(this) < this.parallelism) {
         if (this.tryAllocateWorker()) {
            val var10000: Runnable = this.obtainTaskOrDeallocateWorker()
            if (var10000 != null) {
               startWorker(LimitedDispatcher.Worker(var10000))
            }
         }
      }
   }

   private fun tryAllocateWorker(): Boolean {
      synchronized (this.workerAllocationLock) {
         if (runningWorkers$FU.get(this) >= this.parallelism) {
            return false
         } else {
            runningWorkers$FU.incrementAndGet(this)
            return true
         }
      }
   }

   private fun obtainTaskOrDeallocateWorker(): Runnable? {
      while (true) {
         val nextTask: Runnable = this.queue.removeFirstOrNull()
         if (nextTask != null) {
            return nextTask
         }

         synchronized (this.workerAllocationLock) {
            runningWorkers$FU.decrementAndGet(this)
            if (this.queue.size == 0) {
               return null
            }

            val var10: Int = runningWorkers$FU.incrementAndGet(this)
         }
      }
   }

   private inner class Worker(currentTask: Runnable) : Runnable {
      private final var currentTask: Runnable

      init {
         this.currentTask = currentTask
      }

      public override fun run() {
         val fairnessCounter: Int = 0

         do {
            try {
               this.currentTask.run()
            } catch (var3: java.lang.Throwable) {
               CoroutineExceptionHandlerKt.handleCoroutineException(EmptyCoroutineContext.INSTANCE as CoroutineContext, var3)
            }

            val var10001: Runnable = LimitedDispatcher.this.obtainTaskOrDeallocateWorker()
            if (var10001 == null) {
               return
            }

            this.currentTask = var10001
         } while (++fairnessCounter < 16 || !LimitedDispatcher.this.dispatcher.isDispatchNeeded(LimitedDispatcher.this as CoroutineContext))

         LimitedDispatcher.this.dispatcher.dispatch(LimitedDispatcher.this as CoroutineContext, this)
      }
   }
}
