package kotlinx.coroutines.scheduling

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.internal.SystemPropsKt
import org.jetbrains.annotations.NotNull

@JvmField
@NotNull
internal final val DEFAULT_SCHEDULER_NAME: String = SystemPropsKt.systemProp("kotlinx.coroutines.scheduler.default.name", "DefaultDispatcher")

@JvmField
internal final val WORK_STEALING_TIME_RESOLUTION_NS: Long =
   SystemPropsKt.systemProp$default("kotlinx.coroutines.scheduler.resolution.ns", 100000L, 0L, 0L, 12, null)

@JvmField
internal final val CORE_POOL_SIZE: Int =
   SystemPropsKt.systemProp$default(
      "kotlinx.coroutines.scheduler.core.pool.size", RangesKt.coerceAtLeast(SystemPropsKt.getAVAILABLE_PROCESSORS(), 2), 1, 0, 8, null
   )

@JvmField
internal final val MAX_POOL_SIZE: Int = SystemPropsKt.systemProp$default("kotlinx.coroutines.scheduler.max.pool.size", 2097150, 0, 2097150, 4, null)

@JvmField
internal final val IDLE_WORKER_KEEP_ALIVE_NS: Long =
   TimeUnit.SECONDS.toNanos(SystemPropsKt.systemProp$default("kotlinx.coroutines.scheduler.keep.alive.sec", 60L, 0L, 0L, 12, null))

@JvmField
@NotNull
internal final var schedulerTimeSource: SchedulerTimeSource = NanoTimeSource.INSTANCE as SchedulerTimeSource
   private set

internal const val TASK_NON_BLOCKING: Int = 0
internal const val TASK_PROBABLY_BLOCKING: Int = 1

@JvmField
@NotNull
internal final val NonBlockingContext: TaskContext = TaskContextImpl(0) as TaskContext

@JvmField
@NotNull
internal final val BlockingContext: TaskContext = TaskContextImpl(1) as TaskContext

internal final val isBlocking: Boolean
   internal final inline get() {
      return `$this$isBlocking`.taskContext.taskMode == 1
   }

