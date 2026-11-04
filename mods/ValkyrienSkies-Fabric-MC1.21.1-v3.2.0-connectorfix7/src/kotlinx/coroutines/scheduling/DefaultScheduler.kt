package kotlinx.coroutines.scheduling

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.internal.LimitedDispatcherKt

internal object DefaultScheduler : SchedulerCoroutineDispatcher(
      TasksKt.CORE_POOL_SIZE, TasksKt.MAX_POOL_SIZE, TasksKt.IDLE_WORKER_KEEP_ALIVE_NS, TasksKt.DEFAULT_SCHEDULER_NAME
   ) {
   @ExperimentalCoroutinesApi
   public override fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
      LimitedDispatcherKt.checkParallelism(parallelism)
      return if (parallelism >= TasksKt.CORE_POOL_SIZE) this else super.limitedParallelism(parallelism)
   }

   internal fun shutdown() {
      super.close()
   }

   public override fun close() {
      throw UnsupportedOperationException("Dispatchers.Default cannot be closed")
   }

   public override fun toString(): String {
      return "Dispatchers.Default"
   }
}
