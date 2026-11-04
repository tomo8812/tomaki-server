package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@SourceDebugExtension(["SMAP\nMerge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Merge.kt\nkotlinx/coroutines/flow/internal/ChannelFlowTransformLatest\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,100:1\n1#2:101\n*E\n"])
internal class ChannelFlowTransformLatest<T, R>(transform: (FlowCollector<Any>, Any, Continuation<Unit>) -> Any?,
   flow: Flow<Any>,
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = -2,
   onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) : ChannelFlowOperator(flow, context, capacity, onBufferOverflow) {
   private final val transform: (FlowCollector<Any>, Any, Continuation<Unit>) -> Any?

   init {
      this.transform = transform
   }

   protected override fun create(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): ChannelFlow<Any> {
      return ChannelFlowTransformLatest<>(this.transform, this.flow, context, capacity, onBufferOverflow) as ChannelFlow<R>
   }

   protected override suspend fun flowCollect(collector: FlowCollector<Any>) {
      if (ASSERTIONS_ENABLED && collector !is SendingCollector) {
         throw AssertionError()
      } else {
         val var10000: Any = CoroutineScopeKt.coroutineScope({
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as (CoroutineScope?, Continuation<in R>?) -> Any, `$completion`)
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }
}
