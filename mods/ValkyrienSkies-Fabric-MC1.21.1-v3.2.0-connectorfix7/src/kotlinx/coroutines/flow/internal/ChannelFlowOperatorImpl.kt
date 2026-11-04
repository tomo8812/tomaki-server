package kotlinx.coroutines.flow.internal

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

internal class ChannelFlowOperatorImpl<T>(flow: Flow<Any>,
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = -3,
   onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) : ChannelFlowOperator(flow, context, capacity, onBufferOverflow) {
   protected override fun create(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): ChannelFlow<Any> {
      return ChannelFlowOperatorImpl<>(this.flow, context, capacity, onBufferOverflow)
   }

   public override fun dropChannelOperators(): Flow<Any> {
      return this.flow
   }

   protected override suspend fun flowCollect(collector: FlowCollector<Any>) {
      val var10000: Any = this.flow.collect(collector, `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
