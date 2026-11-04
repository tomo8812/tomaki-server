package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.CoroutineContext.Key
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.CoroutineContextKt
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import org.jetbrains.annotations.NotNull

internal abstract class ChannelFlowOperator<S, T> : ChannelFlow<T> {
   @JvmField
   @NotNull
   protected final val flow: Flow<Any>

   open fun ChannelFlowOperator(flow: Flow<out S>, context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow) {
      super(context, capacity, onBufferOverflow)
      this.flow = flow
   }

   protected abstract suspend fun flowCollect(collector: FlowCollector<Any>) {
   }

   private suspend fun collectWithContextUndispatched(collector: FlowCollector<Any>, newContext: CoroutineContext) {
      val var10000: Any = ChannelFlowKt.withContextUndispatched$default(
         newContext, ChannelFlowKt.access$withUndispatchedContextCollector(collector, `$completion`.getContext()), null, {
            // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
         } as Function2, `$completion`, 4, null
      )
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   protected override suspend fun collectTo(scope: ProducerScope<Any>) {
      return collectTo$suspendImpl(this, scope, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <S, T> `collectTo$suspendImpl`(`$this`: ChannelFlowOperator<S, T>, scope: ProducerScope<in T>, `$completion`: Continuation<in Unit>) {
      val var10000: Any = `$this`.flowCollect(SendingCollector<>(scope), `$completion`)
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   public override suspend fun collect(collector: FlowCollector<Any>) {
      return collect$suspendImpl(this, collector, `$completion`)
   }

   @JvmStatic
   @JvmSynthetic
   fun <S, T> `collect$suspendImpl`(`$this`: ChannelFlowOperator<S, T>, collector: FlowCollector<in T>, `$completion`: Continuation<in Unit>) {
      if (`$this`.capacity == -3) {
         val collectContext: CoroutineContext = `$completion`.getContext()
         val newContext: CoroutineContext = CoroutineContextKt.newCoroutineContext(collectContext, `$this`.context)
         if (newContext == collectContext) {
            val var6: Any = `$this`.flowCollect(collector, `$completion`)
            if (var6 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
               var6
            }

            Unit.INSTANCE
         }

         if (newContext.get(ContinuationInterceptor.Key as Key) == collectContext.get(ContinuationInterceptor.Key as Key)) {
            val var5: Any = `$this`.collectWithContextUndispatched(collector, newContext, `$completion`)
            if (var5 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
               var5
            }

            Unit.INSTANCE
         }
      }

      val var10000: Any = `$this`.collect(collector, `$completion`)
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }

   public override fun toString(): String {
      return "${this.flow} -> ${super.toString()}"
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$collectWithContextUndispatched`(
      `$this`: ChannelFlowOperator, collector: FlowCollector, newContext: CoroutineContext, `$completion`: Continuation
   ) {
      `$this`.collectWithContextUndispatched(collector, newContext, `$completion`)
   }
}
