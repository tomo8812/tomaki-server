package kotlinx.coroutines.scheduling

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.LimitedDispatcherKt

private object UnlimitedIoScheduler : CoroutineDispatcher {
   @InternalCoroutinesApi
   public override fun dispatchYield(context: CoroutineContext, block: Runnable) {
      DefaultScheduler.INSTANCE.dispatchWithContext$kotlinx_coroutines_core(block, TasksKt.BlockingContext, true)
   }

   public override fun dispatch(context: CoroutineContext, block: Runnable) {
      DefaultScheduler.INSTANCE.dispatchWithContext$kotlinx_coroutines_core(block, TasksKt.BlockingContext, false)
   }

   @ExperimentalCoroutinesApi
   public override fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
      LimitedDispatcherKt.checkParallelism(parallelism)
      return if (parallelism >= TasksKt.MAX_POOL_SIZE) this else super.limitedParallelism(parallelism)
   }
}
