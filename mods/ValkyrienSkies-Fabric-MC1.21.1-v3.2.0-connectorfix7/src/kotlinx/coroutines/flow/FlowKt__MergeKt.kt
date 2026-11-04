@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nMerge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Merge.kt\nkotlinx/coroutines/flow/FlowKt__MergeKt\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,218:1\n53#2:219\n55#2:223\n53#2:224\n55#2:228\n50#3:220\n55#3:222\n50#3:225\n55#3:227\n107#4:221\n107#4:226\n107#4:229\n1#5:230\n*S KotlinDebug\n*F\n+ 1 Merge.kt\nkotlinx/coroutines/flow/FlowKt__MergeKt\n*L\n47#1:219\n47#1:223\n74#1:224\n74#1:228\n47#1:220\n47#1:222\n74#1:225\n74#1:227\n47#1:221\n74#1:226\n82#1:229\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.internal.ChannelFlowMerge
import kotlinx.coroutines.flow.internal.ChannelFlowTransformLatest
import kotlinx.coroutines.flow.internal.ChannelLimitedFlowMerge
import kotlinx.coroutines.internal.SystemPropsKt

@FlowPreview
public final val DEFAULT_CONCURRENCY: Int = SystemPropsKt.systemProp("kotlinx.coroutines.flow.defaultConcurrency", 16, 1, Integer.MAX_VALUE)

@ExperimentalCoroutinesApi
public fun <T, R> Flow<Any>.flatMapConcat(transform: (Any, Continuation<Flow<Any>>) -> Any?): Flow<Any> {
   return FlowKt.flattenConcat(FlowKt__MergeKt$flatMapConcat$$inlined$map$1(`$this$flatMapConcat`, transform))
}

@ExperimentalCoroutinesApi
public fun <T, R> Flow<Any>.flatMapMerge(concurrency: Int = DEFAULT_CONCURRENCY, transform: (Any, Continuation<Flow<Any>>) -> Any?): Flow<Any> {
   return FlowKt.flattenMerge(FlowKt__MergeKt$flatMapMerge$$inlined$map$1(`$this$flatMapMerge`, transform), concurrency)
}

@ExperimentalCoroutinesApi
public fun <T> Flow<Flow<Any>>.flattenConcat(): Flow<Any> {
   return FlowKt__MergeKt$flattenConcat$$inlined$unsafeFlow$1(`$this$flattenConcat`)
}

public fun <T> Iterable<Flow<Any>>.merge(): Flow<Any> {
   return ChannelLimitedFlowMerge(`$this$merge`, null, 0, null, 14, null)
}

public fun <T> merge(vararg flows: Flow<Any>): Flow<Any> {
   return FlowKt.merge(ArraysKt.asIterable(flows))
}

@ExperimentalCoroutinesApi
public fun <T> Flow<Flow<Any>>.flattenMerge(concurrency: Int = DEFAULT_CONCURRENCY): Flow<Any> {
   if (concurrency <= 0) {
      throw IllegalArgumentException(("Expected positive concurrency level, but had $concurrency").toString())
   } else {
      return if (concurrency == 1) FlowKt.flattenConcat(`$this$flattenMerge`) else ChannelFlowMerge(`$this$flattenMerge`, concurrency, null, 0, null, 28, null)
   }
}

@ExperimentalCoroutinesApi
public fun <T, R> Flow<Any>.transformLatest(transform: (FlowCollector<Any>, Any, Continuation<Unit>) -> Any?): Flow<Any> {
   return ChannelFlowTransformLatest(transform, `$this$transformLatest`, null, 0, null, 28, null)
}

@ExperimentalCoroutinesApi
public inline fun <T, R> Flow<Any>.flatMapLatest(crossinline transform: (Any, Continuation<Flow<Any>>) -> Any?): Flow<Any> {
   return FlowKt.transformLatest(`$this$flatMapLatest`, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function3)
}

@ExperimentalCoroutinesApi
public fun <T, R> Flow<Any>.mapLatest(transform: (Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt.transformLatest(`$this$mapLatest`, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function3)
}
