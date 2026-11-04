package kotlinx.coroutines

import kotlinx.coroutines.internal.Symbol

private final val DISPOSED_TASK: Symbol = Symbol("REMOVED_TASK")
private const val SCHEDULE_OK: Int = 0
private const val SCHEDULE_COMPLETED: Int = 1
private const val SCHEDULE_DISPOSED: Int = 2
private const val MS_TO_NS: Long = 1000000L
private const val MAX_MS: Long = 9223372036854L
private const val MAX_DELAY_NS: Long = 4611686018427387903L
private final val CLOSED_EMPTY: Symbol = Symbol("CLOSED_EMPTY")

internal fun delayToNanos(timeMillis: Long): Long {
   return if (timeMillis <= 0L) 0L else (if (timeMillis >= 9223372036854L) java.lang.Long.MAX_VALUE else timeMillis * 1000000L)
}

internal fun delayNanosToMillis(timeNanos: Long): Long {
   return timeNanos / 1000000L
}

@JvmSynthetic
fun `access$getCLOSED_EMPTY$p`(): Symbol {
   CLOSED_EMPTY
}

@JvmSynthetic
fun `access$getDISPOSED_TASK$p`(): Symbol {
   DISPOSED_TASK
}
