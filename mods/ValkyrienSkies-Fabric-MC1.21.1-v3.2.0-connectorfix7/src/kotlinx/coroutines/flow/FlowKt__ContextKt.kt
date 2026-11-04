@file:JvmMultifileClass
@file:JvmName("FlowKt")

package kotlinx.coroutines.flow

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.internal.ChannelFlowOperatorImpl
import kotlinx.coroutines.flow.internal.FusibleFlow

public fun <T> Flow<Any>.buffer(capacity: Int = -2, onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND): Flow<Any> {
   if (capacity < 0 && capacity != -2 && capacity != -1) {
      throw IllegalArgumentException(("Buffer size should be non-negative, BUFFERED, or CONFLATED, but was $capacity").toString())
   } else if (capacity == -1 && onBufferOverflow != BufferOverflow.SUSPEND) {
      throw IllegalArgumentException("CONFLATED capacity cannot be used with non-default onBufferOverflow".toString())
   } else {
      var capacityx: Int = capacity
      var onBufferOverflowx: BufferOverflow = onBufferOverflow
      if (capacity == -1) {
         capacityx = 0
         onBufferOverflowx = BufferOverflow.DROP_OLDEST
      }

      return if (`$this$buffer` is FusibleFlow)
         FusibleFlow.DefaultImpls.fuse$default(`$this$buffer` as FusibleFlow, null, capacityx, onBufferOverflowx, 1, null)
         else
         ChannelFlowOperatorImpl(`$this$buffer`, null, capacityx, onBufferOverflowx, 2, null)
      }
}

@Deprecated(message = "Since 1.4.0, binary compatibility with earlier versions", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public fun <T> Flow<Any>.buffer(capacity: Int = -2): Flow<Any> {
   return FlowKt.buffer$default(`$this$buffer`, capacity, null, 2, null)
}

public fun <T> Flow<Any>.conflate(): Flow<Any> {
   return FlowKt.buffer$default(`$this$conflate`, -1, null, 2, null)
}

public fun <T> Flow<Any>.flowOn(context: CoroutineContext): Flow<Any> {
   checkFlowContext$FlowKt__ContextKt(context)
   return if (context == EmptyCoroutineContext.INSTANCE)
      `$this$flowOn`
      else
      (
         if (`$this$flowOn` is FusibleFlow)
            FusibleFlow.DefaultImpls.fuse$default(`$this$flowOn` as FusibleFlow, context, 0, null, 6, null)
            else
            ChannelFlowOperatorImpl(`$this$flowOn`, context, 0, null, 12, null)
      )
   }

public fun <T> Flow<Any>.cancellable(): Flow<Any> {
   return if (`$this$cancellable` is CancellableFlow) `$this$cancellable` else CancellableFlowImpl(`$this$cancellable`)
}

private fun checkFlowContext(context: CoroutineContext) {
   if (context.get(Job.Key) != null) {
      throw IllegalArgumentException(("Flow context cannot contain job in it. Had $context").toString())
   }
}
