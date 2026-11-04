package kotlinx.coroutines

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.internal.ConcurrentKt

internal class ExecutorCoroutineDispatcherImpl(executor: Executor) : ExecutorCoroutineDispatcher, Delay {
   public open val executor: Executor

   init {
      this.executor = executor
      ConcurrentKt.removeFutureOnCancel(this.executor)
   }

   public override fun dispatch(context: CoroutineContext, block: Runnable) {
      try {
         var var5: Executor
         var var6: Runnable
         run label30@{
            var5 = this.executor
            val var10001: AbstractTimeSource = timeSource
            if (var10001 != null) {
               var6 = var10001.wrapTask(block)
               if (var6 != null) {
                  return@label30
               }
            }

            var6 = block
         }

         var5.execute(var6)
      } catch (var4: RejectedExecutionException) {
         val var10000: AbstractTimeSource = timeSource
         if (var10000 != null) {
            var10000.unTrackTask()
         }

         this.cancelJobOnRejection(context, var4)
         IO.dispatch(context, block)
      }
   }

   public override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
      val var5: Executor = this.executor
      val future: ScheduledFuture = if ((var5 as? ScheduledExecutorService) != null)
         this.scheduleBlock(var5 as? ScheduledExecutorService, ResumeUndispatchedRunnable(this, continuation), continuation.getContext(), timeMillis)
         else
         null
         if (future != null) {
         JobKt.cancelFutureOnCancellation(continuation, future)
      } else {
         DefaultExecutor.INSTANCE.scheduleResumeAfterDelay(timeMillis, continuation)
      }
   }

   public override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
      val var6: Executor = this.executor
      val future: ScheduledFuture = if ((var6 as? ScheduledExecutorService) != null)
         this.scheduleBlock(var6 as? ScheduledExecutorService, block, context, timeMillis)
         else
         null
         return if (future != null) DisposableFutureHandle(future) else DefaultExecutor.INSTANCE.invokeOnTimeout(timeMillis, block, context)
   }

   private fun ScheduledExecutorService.scheduleBlock(block: Runnable, context: CoroutineContext, timeMillis: Long): ScheduledFuture<*>? {
      var var6: ScheduledFuture
      try {
         var6 = `$this$scheduleBlock`.schedule(block, timeMillis, TimeUnit.MILLISECONDS)
      } catch (var8: RejectedExecutionException) {
         this.cancelJobOnRejection(context, var8)
         var6 = null
      }

      return var6
   }

   private fun cancelJobOnRejection(context: CoroutineContext, exception: RejectedExecutionException) {
      JobKt.cancel(context, ExceptionsKt.CancellationException("The task was rejected", exception))
   }

   public override fun close() {
      val var1: Executor = this.executor
      val var10000: ExecutorService = var1 as? ExecutorService
      if ((var1 as? ExecutorService) != null) {
         var10000.shutdown()
      }
   }

   public override fun toString(): String {
      return this.executor.toString()
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ExecutorCoroutineDispatcherImpl && (other as ExecutorCoroutineDispatcherImpl).executor === this.executor
   }

   public override fun hashCode(): Int {
      return System.identityHashCode(this.executor)
   }

   /** @deprecated */
   @Deprecated(message = "Deprecated without replacement as an internal method never intended for public use", level = DeprecationLevel.ERROR)
   override fun delay(time: Long, `$completion`: Continuation<in Unit>) {
      Delay.DefaultImpls.delay(this, time, `$completion`)
   }
}
