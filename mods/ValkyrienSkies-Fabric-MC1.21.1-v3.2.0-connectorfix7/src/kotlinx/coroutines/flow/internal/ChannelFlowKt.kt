@file:SourceDebugExtension(["SMAP\nChannelFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChannelFlow.kt\nkotlinx/coroutines/flow/internal/ChannelFlowKt\n+ 2 CoroutineContext.kt\nkotlinx/coroutines/CoroutineContextKt\n*L\n1#1,245:1\n95#2,5:246\n*S KotlinDebug\n*F\n+ 1 ChannelFlow.kt\nkotlinx/coroutines/flow/internal/ChannelFlowKt\n*L\n226#1:246,5\n*E\n"])

package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.internal.ThreadContextKt

internal fun <T> Flow<Any>.asChannelFlow(): ChannelFlow<Any> {
   var var10000: ChannelFlow = `$this$asChannelFlow` as? ChannelFlow
   if ((`$this$asChannelFlow` as? ChannelFlow) == null) {
      var10000 = ChannelFlowOperatorImpl(`$this$asChannelFlow`, null, 0, null, 14, null)
   }

   return var10000
}

private fun <T> FlowCollector<Any>.withUndispatchedContextCollector(emitContext: CoroutineContext): FlowCollector<Any> {
   return if (`$this$withUndispatchedContextCollector` is SendingCollector || `$this$withUndispatchedContextCollector` is NopCollector)
      `$this$withUndispatchedContextCollector`
      else
      UndispatchedContextCollector(`$this$withUndispatchedContextCollector`, emitContext)
   }

internal suspend fun <T, V> withContextUndispatched(
   newContext: CoroutineContext,
   value: Any,
   countOrElement: Any = ...,
   block: (Any, Continuation<Any>) -> Any?
): Any {
   val uCont: Continuation = `$completion`

   var var12: Any
   try {
      var12 = (TypeIntrinsics.beforeCheckcastToFunctionOfArity(block, 2) as Function2)(value, StackFrameContinuation(uCont, newContext))
   } finally {
      ThreadContextKt.restoreThreadContext(newContext, ThreadContextKt.updateThreadContext(newContext, countOrElement))
   }

   if (var12 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   return var12
}

@JvmSynthetic
fun `withContextUndispatched$default`(var0: CoroutineContext, var1: Any, var2: Any, var3: Function2, var4: Continuation, var5: Int, var6: Any) {
   if ((var5 and 4) != 0) {
      var2 = ThreadContextKt.threadContextElements(var0)
   }

   withContextUndispatched(var0, var1, var2, var3, var4)
}

@JvmSynthetic
fun `access$withUndispatchedContextCollector`(`$receiver`: FlowCollector, emitContext: CoroutineContext): FlowCollector {
   withUndispatchedContextCollector(`$receiver`, emitContext)
}
