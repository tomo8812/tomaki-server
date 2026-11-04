package kotlinx.coroutines.scheduling

import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DefaultExecutor
import kotlinx.coroutines.ExecutorCoroutineDispatcher

@PublishedApi
@SourceDebugExtension(["SMAP\nDeprecated.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Deprecated.kt\nkotlinx/coroutines/scheduling/ExperimentalCoroutineDispatcher\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,213:1\n1#2:214\n*E\n"])
internal open class ExperimentalCoroutineDispatcher(corePoolSize: Int,
      maxPoolSize: Int,
      idleWorkerKeepAliveNs: Long,
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

   public constructor(corePoolSize: Int = TasksKt.CORE_POOL_SIZE,
      maxPoolSize: Int = TasksKt.MAX_POOL_SIZE,
      schedulerName: String = TasksKt.DEFAULT_SCHEDULER_NAME
   ) : this(corePoolSize, maxPoolSize, TasksKt.IDLE_WORKER_KEEP_ALIVE_NS, schedulerName)
   @Deprecated(message = "Binary compatibility for Ktor 1.0-beta", level = DeprecationLevel.HIDDEN)
   @JvmSynthetic
   public constructor(corePoolSize: Int = TasksKt.CORE_POOL_SIZE, maxPoolSize: Int = TasksKt.MAX_POOL_SIZE) : this(
         corePoolSize, maxPoolSize, TasksKt.IDLE_WORKER_KEEP_ALIVE_NS, null, 8, null
      )
   public open val executor: Executor
      public open get() {
         return this.coroutineScheduler
      }


   public override fun dispatch(context: CoroutineContext, block: Runnable) {
      try {
         CoroutineScheduler.dispatch$default(this.coroutineScheduler, block, null, false, 6, null)
      } catch (var4: RejectedExecutionException) {
         DefaultExecutor.INSTANCE.dispatch(context, block)
      }
   }

   public override fun dispatchYield(context: CoroutineContext, block: Runnable) {
      try {
         CoroutineScheduler.dispatch$default(this.coroutineScheduler, block, null, true, 2, null)
      } catch (var4: RejectedExecutionException) {
         DefaultExecutor.INSTANCE.dispatchYield(context, block)
      }
   }

   public override fun close() {
      this.coroutineScheduler.close()
   }

   public override fun toString(): String {
      return "${super.toString()}[scheduler = ${this.coroutineScheduler}]"
   }

   public fun blocking(parallelism: Int = 16): CoroutineDispatcher {
      if (parallelism <= 0) {
         throw IllegalArgumentException(("Expected positive parallelism level, but have $parallelism").toString())
      } else {
         return LimitingDispatcher(this, parallelism, null, 1)
      }
   }

   public fun limited(parallelism: Int): CoroutineDispatcher {
      if (parallelism <= 0) {
         throw IllegalArgumentException(("Expected positive parallelism level, but have $parallelism").toString())
      } else if (parallelism > this.corePoolSize) {
         throw IllegalArgumentException(("Expected parallelism level lesser than core pool size (${this.corePoolSize}), but have $parallelism").toString())
      } else {
         return LimitingDispatcher(this, parallelism, null, 0)
      }
   }

   internal fun dispatchWithContext(block: Runnable, context: TaskContext, tailDispatch: Boolean) {
      try {
         this.coroutineScheduler.dispatch(block, context, tailDispatch)
      } catch (var5: RejectedExecutionException) {
         DefaultExecutor.INSTANCE.enqueue(this.coroutineScheduler.createTask(block, context))
      }
   }

   private fun createScheduler(): CoroutineScheduler {
      return CoroutineScheduler(this.corePoolSize, this.maxPoolSize, this.idleWorkerKeepAliveNs, this.schedulerName)
   }
}
