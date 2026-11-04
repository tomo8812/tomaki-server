package kotlinx.coroutines.scheduling

import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.ExecutorCoroutineDispatcher

internal open class SchedulerCoroutineDispatcher(corePoolSize: Int = TasksKt.CORE_POOL_SIZE,
      maxPoolSize: Int = TasksKt.MAX_POOL_SIZE,
      idleWorkerKeepAliveNs: Long = TasksKt.IDLE_WORKER_KEEP_ALIVE_NS,
      schedulerName: String = "CoroutineScheduler"
   )
   : ExecutorCoroutineDispatcher {
   private final val corePoolSize: Int
   private final val maxPoolSize: Int
   private final val idleWorkerKeepAliveNs: Long
   private final val schedulerName: String
   private final var coroutineScheduler: CoroutineScheduler

   init {
      this.corePoolSize = corePoolSize
      this.maxPoolSize = maxPoolSize
      this.idleWorkerKeepAliveNs = idleWorkerKeepAliveNs
      this.schedulerName = schedulerName
      this.coroutineScheduler = this.createScheduler()
   }

   public open val executor: Executor
      public open get() {
         return this.coroutineScheduler
      }


   private fun createScheduler(): CoroutineScheduler {
      return CoroutineScheduler(this.corePoolSize, this.maxPoolSize, this.idleWorkerKeepAliveNs, this.schedulerName)
   }

   public override fun dispatch(context: CoroutineContext, block: Runnable) {
      CoroutineScheduler.dispatch$default(this.coroutineScheduler, block, null, false, 6, null)
   }

   public override fun dispatchYield(context: CoroutineContext, block: Runnable) {
      CoroutineScheduler.dispatch$default(this.coroutineScheduler, block, null, true, 2, null)
   }

   internal fun dispatchWithContext(block: Runnable, context: TaskContext, tailDispatch: Boolean) {
      this.coroutineScheduler.dispatch(block, context, tailDispatch)
   }

   public override fun close() {
      this.coroutineScheduler.close()
   }

   @Synchronized
   internal fun usePrivateScheduler() {
      this.coroutineScheduler.shutdown(1000L)
      this.coroutineScheduler = this.createScheduler()
   }

   @Synchronized
   internal fun shutdown(timeout: Long) {
      this.coroutineScheduler.shutdown(timeout)
   }

   internal fun restore() {
      this.usePrivateScheduler$kotlinx_coroutines_core()
   }

   open fun SchedulerCoroutineDispatcher() {
      this(0, 0, 0L, null, 15, null)
   }
}
