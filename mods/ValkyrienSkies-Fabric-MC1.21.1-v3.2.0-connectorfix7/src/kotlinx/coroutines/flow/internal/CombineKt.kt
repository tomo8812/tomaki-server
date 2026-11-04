@file:SourceDebugExtension(["SMAP\nCombine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Combine.kt\nkotlinx/coroutines/flow/internal/CombineKt\n+ 2 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,143:1\n107#2:144\n*S KotlinDebug\n*F\n+ 1 Combine.kt\nkotlinx/coroutines/flow/internal/CombineKt\n*L\n86#1:144\n*E\n"])

package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@PublishedApi
internal suspend fun <R, T> FlowCollector<Any>.combineInternal(
   flows: Array<out Flow<Any>>,
   arrayFactory: () -> Array<Any?>?,
   transform: (FlowCollector<Any>, Array<Any>, Continuation<Unit>) -> Any?
) {
   val var10000: Any = FlowCoroutineKt.flowScope({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, `$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

internal fun <T1, T2, R> zipImpl(flow: Flow<Any>, flow2: Flow<Any>, transform: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return CombineKt$zipImpl$$inlined$unsafeFlow$1(flow2, flow, transform)
}
