package kotlinx.coroutines.flow.internal

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProduceKt
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow

@SourceDebugExtension(["SMAP\nMerge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Merge.kt\nkotlinx/coroutines/flow/internal/ChannelLimitedFlowMerge\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,100:1\n1855#2,2:101\n*S KotlinDebug\n*F\n+ 1 Merge.kt\nkotlinx/coroutines/flow/internal/ChannelLimitedFlowMerge\n*L\n95#1:101,2\n*E\n"])
internal class ChannelLimitedFlowMerge<T>(flows: Iterable<Flow<Any>>,
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   capacity: Int = -2,
   onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) : ChannelFlow(context, capacity, onBufferOverflow) {
   private final val flows: Iterable<Flow<Any>>

   init {
      this.flows = flows
   }

   protected override fun create(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): ChannelFlow<Any> {
      return ChannelLimitedFlowMerge<>(this.flows, context, capacity, onBufferOverflow)
   }

   public override fun produceImpl(scope: CoroutineScope): ReceiveChannel<Any> {
      return ProduceKt.produce(scope, this.context, this.capacity, this.getCollectToFun$kotlinx_coroutines_core())
   }

   protected override suspend fun collectTo(scope: ProducerScope<Any>) {
      val collector: SendingCollector = SendingCollector(scope)

      for (`element$iv` in this.flows) {
         val flow: Flow = `element$iv` as Flow
         BuildersKt.launch$default(scope, null, null, {
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as Function2, 3, null)
      }

      return Unit.INSTANCE
   }
}
