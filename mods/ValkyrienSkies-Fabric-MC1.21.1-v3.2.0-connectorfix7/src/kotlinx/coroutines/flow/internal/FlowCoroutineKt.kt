@file:SourceDebugExtension(["SMAP\nFlowCoroutine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FlowCoroutine.kt\nkotlinx/coroutines/flow/internal/FlowCoroutineKt\n+ 2 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,63:1\n107#2:64\n*S KotlinDebug\n*F\n+ 1 FlowCoroutine.kt\nkotlinx/coroutines/flow/internal/FlowCoroutineKt\n*L\n50#1:64\n*E\n"])

package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.intrinsics.UndispatchedKt

internal suspend fun <R> flowScope(block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   val coroutine: FlowCoroutine = FlowCoroutine(`$completion`.getContext(), `$completion`)
   val var10000: Any = UndispatchedKt.startUndispatchedOrReturn(coroutine, coroutine, block)
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   return var10000
}

internal fun <R> scopedFlow(block: (CoroutineScope, FlowCollector<Any>, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowCoroutineKt$scopedFlow$$inlined$unsafeFlow$1(block)
}
