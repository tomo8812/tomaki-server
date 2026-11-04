package kotlinx.coroutines.channels

import kotlin.jvm.functions.Function1
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction2
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.internal.Symbol
import kotlinx.coroutines.internal.SystemPropsKt
import org.jetbrains.annotations.NotNull

private final val NULL_SEGMENT: ChannelSegment<Any?> = ChannelSegment(-1L, null, null, 0)

@JvmField
internal final val SEGMENT_SIZE: Int = SystemPropsKt.systemProp$default("kotlinx.coroutines.bufferedChannel.segmentSize", 32, 0, 0, 12, null)

private final val EXPAND_BUFFER_COMPLETION_WAIT_ITERATIONS: Int =
   SystemPropsKt.systemProp$default("kotlinx.coroutines.bufferedChannel.expandBufferCompletionWaitIterations", 10000, 0, 0, 12, null)
   private const val BUFFER_END_RENDEZVOUS: Long = 0L
private const val BUFFER_END_UNLIMITED: Long = java.lang.Long.MAX_VALUE

@JvmField
@NotNull
internal final val BUFFERED: Symbol = Symbol("BUFFERED")

private final val IN_BUFFER: Symbol = Symbol("SHOULD_BUFFER")
private final val RESUMING_BY_RCV: Symbol = Symbol("S_RESUMING_BY_RCV")
private final val RESUMING_BY_EB: Symbol = Symbol("RESUMING_BY_EB")
private final val POISONED: Symbol = Symbol("POISONED")
private final val DONE_RCV: Symbol = Symbol("DONE_RCV")
private final val INTERRUPTED_SEND: Symbol = Symbol("INTERRUPTED_SEND")
private final val INTERRUPTED_RCV: Symbol = Symbol("INTERRUPTED_RCV")
internal final val CHANNEL_CLOSED: Symbol = Symbol("CHANNEL_CLOSED")
private final val SUSPEND: Symbol = Symbol("SUSPEND")
private final val SUSPEND_NO_WAITER: Symbol = Symbol("SUSPEND_NO_WAITER")
private final val FAILED: Symbol = Symbol("FAILED")
private const val RESULT_RENDEZVOUS: Int = 0
private const val RESULT_BUFFERED: Int = 1
private const val RESULT_SUSPEND: Int = 2
private const val RESULT_SUSPEND_NO_WAITER: Int = 3
private const val RESULT_CLOSED: Int = 4
private const val RESULT_FAILED: Int = 5
private final val NO_RECEIVE_RESULT: Symbol = Symbol("NO_RECEIVE_RESULT")
private final val CLOSE_HANDLER_CLOSED: Symbol = Symbol("CLOSE_HANDLER_CLOSED")
private final val CLOSE_HANDLER_INVOKED: Symbol = Symbol("CLOSE_HANDLER_INVOKED")
private final val NO_CLOSE_CAUSE: Symbol = Symbol("NO_CLOSE_CAUSE")
private const val CLOSE_STATUS_ACTIVE: Int = 0
private const val CLOSE_STATUS_CANCELLATION_STARTED: Int = 1
private const val CLOSE_STATUS_CLOSED: Int = 2
private const val CLOSE_STATUS_CANCELLED: Int = 3
private const val SENDERS_CLOSE_STATUS_SHIFT: Int = 60
private const val SENDERS_COUNTER_MASK: Long = 1152921504606846975L
private const val EB_COMPLETED_PAUSE_EXPAND_BUFFERS_BIT: Long = 4611686018427387904L
private const val EB_COMPLETED_COUNTER_MASK: Long = 4611686018427387903L

internal fun <E> createSegmentFunction(): KFunction2<Long, ChannelSegment<Any>, ChannelSegment<Any>> {
   return <unrepresentable>.INSTANCE as KFunction<ChannelSegment<E>>
}

private fun <E> createSegment(id: Long, prev: ChannelSegment<Any>): ChannelSegment<Any> {
   return ChannelSegment(id, prev, prev.channel, 0)
}

private fun <T> CancellableContinuation<Any>.tryResume0(value: Any, onCancellation: ((Throwable) -> Unit)? = null): Boolean {
   val token: Any = `$this$tryResume0`.tryResume(value, null, onCancellation)
   val var10000: Boolean
   if (token != null) {
      `$this$tryResume0`.completeResume(token)
      var10000 = true
   } else {
      var10000 = false
   }

   return var10000
}

private fun initialBufferEnd(capacity: Int): Long {
   var var10000: Long
   when (capacity) {
      0 -> var10000 = 0L
      Integer.MAX_VALUE -> var10000 = java.lang.Long.MAX_VALUE
      else -> var10000 = capacity
   }

   return var10000
}

private final val sendersCounter: Long
   private final inline get() {
      return `$this$sendersCounter` and 1152921504606846975L
   }


private final val sendersCloseStatus: Int
   private final inline get() {
      return (int)(`$this$sendersCloseStatus` shr 60)
   }


private fun constructSendersAndCloseStatus(counter: Long, closeStatus: Int): Long {
   return ((long)closeStatus shl 60) + counter
}

private final val ebCompletedCounter: Long
   private final inline get() {
      return `$this$ebCompletedCounter` and 4611686018427387903L
   }


private final val ebPauseExpandBuffers: Boolean
   private final inline get() {
      return (`$this$ebPauseExpandBuffers` and 4611686018427387904L) != 0L
   }


private fun constructEBCompletedAndPauseFlag(counter: Long, pauseEB: Boolean): Long {
   return (if (pauseEB) 4611686018427387904L else 0L) + counter
}

@JvmSynthetic
fun `access$initialBufferEnd`(capacity: Int): Long {
   initialBufferEnd(capacity)
}

@JvmSynthetic
fun `access$getNULL_SEGMENT$p`(): ChannelSegment {
   NULL_SEGMENT
}

@JvmSynthetic
fun `access$getNO_CLOSE_CAUSE$p`(): Symbol {
   NO_CLOSE_CAUSE
}

@JvmSynthetic
fun `access$getINTERRUPTED_SEND$p`(): Symbol {
   INTERRUPTED_SEND
}

@JvmSynthetic
fun `access$getDONE_RCV$p`(): Symbol {
   DONE_RCV
}

@JvmSynthetic
fun `access$getINTERRUPTED_RCV$p`(): Symbol {
   INTERRUPTED_RCV
}

@JvmSynthetic
fun `access$getIN_BUFFER$p`(): Symbol {
   IN_BUFFER
}

@JvmSynthetic
fun `access$getPOISONED$p`(): Symbol {
   POISONED
}

@JvmSynthetic
fun `access$tryResume0`(`$receiver`: CancellableContinuation, value: Any, onCancellation: Function1): Boolean {
   tryResume0(`$receiver`, value, onCancellation)
}

@JvmSynthetic
fun `access$getFAILED$p`(): Symbol {
   FAILED
}

@JvmSynthetic
fun `access$getSUSPEND$p`(): Symbol {
   SUSPEND
}

@JvmSynthetic
fun `access$getSUSPEND_NO_WAITER$p`(): Symbol {
   SUSPEND_NO_WAITER
}

@JvmSynthetic
fun `access$getRESUMING_BY_EB$p`(): Symbol {
   RESUMING_BY_EB
}

@JvmSynthetic
fun `access$getRESUMING_BY_RCV$p`(): Symbol {
   RESUMING_BY_RCV
}

@JvmSynthetic
fun `access$getEXPAND_BUFFER_COMPLETION_WAIT_ITERATIONS$p`(): Int {
   EXPAND_BUFFER_COMPLETION_WAIT_ITERATIONS
}

@JvmSynthetic
fun `access$constructEBCompletedAndPauseFlag`(counter: Long, pauseEB: Boolean): Long {
   constructEBCompletedAndPauseFlag(counter, pauseEB)
}

@JvmSynthetic
fun `access$getNO_RECEIVE_RESULT$p`(): Symbol {
   NO_RECEIVE_RESULT
}

@JvmSynthetic
fun `access$getCLOSE_HANDLER_CLOSED$p`(): Symbol {
   CLOSE_HANDLER_CLOSED
}

@JvmSynthetic
fun `access$getCLOSE_HANDLER_INVOKED$p`(): Symbol {
   CLOSE_HANDLER_INVOKED
}

@JvmSynthetic
fun `access$constructSendersAndCloseStatus`(counter: Long, closeStatus: Int): Long {
   constructSendersAndCloseStatus(counter, closeStatus)
}
