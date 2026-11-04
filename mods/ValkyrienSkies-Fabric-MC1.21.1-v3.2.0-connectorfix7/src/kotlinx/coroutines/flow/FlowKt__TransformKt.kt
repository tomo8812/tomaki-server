@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nTransform.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 2 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 3 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,136:1\n21#1:143\n23#1:147\n21#1:148\n23#1:152\n50#2:137\n55#2:139\n50#2:140\n55#2:142\n50#2:144\n55#2:146\n50#2:149\n55#2:151\n50#2:153\n55#2:155\n50#2:156\n55#2:158\n50#2:159\n55#2:161\n50#2:163\n55#2:165\n107#3:138\n107#3:141\n107#3:145\n107#3:150\n107#3:154\n107#3:157\n107#3:160\n107#3:162\n107#3:164\n107#3:166\n107#3:167\n*S KotlinDebug\n*F\n+ 1 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n*L\n36#1:143\n36#1:147\n41#1:148\n41#1:152\n21#1:137\n21#1:139\n28#1:140\n28#1:142\n36#1:144\n36#1:146\n41#1:149\n41#1:151\n46#1:153\n46#1:155\n53#1:156\n53#1:158\n60#1:159\n60#1:161\n78#1:163\n78#1:165\n21#1:138\n28#1:141\n36#1:145\n41#1:150\n46#1:154\n53#1:157\n60#1:160\n68#1:162\n78#1:164\n105#1:166\n125#1:167\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KClass

public inline fun <T> Flow<Any>.filter(crossinline predicate: (Any, Continuation<Boolean>) -> Any?): Flow<Any> {
   return FlowKt__TransformKt$filter$$inlined$unsafeTransform$1(`$this$filter`, predicate)
}

public inline fun <T> Flow<Any>.filterNot(crossinline predicate: (Any, Continuation<Boolean>) -> Any?): Flow<Any> {
   return FlowKt__TransformKt$filterNot$$inlined$unsafeTransform$1(`$this$filterNot`, predicate)
}

@JvmSynthetic
public inline fun <reified R> Flow<*>.filterIsInstance(): Flow<Any> {
   Intrinsics.needClassReification()
   return FlowKt__TransformKt$filterIsInstance$$inlined$filter$1(`$this$filterIsInstance`)
}

public fun <R : Any> Flow<*>.filterIsInstance(klass: KClass<Any>): Flow<Any> {
   return FlowKt__TransformKt$filterIsInstance$$inlined$filter$2(`$this$filterIsInstance`, klass)
}

public fun <T : Any> Flow<Any?>.filterNotNull(): Flow<Any> {
   return FlowKt__TransformKt$filterNotNull$$inlined$unsafeTransform$1(`$this$filterNotNull`)
}

public inline fun <T, R> Flow<Any>.map(crossinline transform: (Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt__TransformKt$map$$inlined$unsafeTransform$1(`$this$map`, transform)
}

public inline fun <T, R : Any> Flow<Any>.mapNotNull(crossinline transform: (Any, Continuation<Any?>) -> Any?): Flow<Any> {
   return FlowKt__TransformKt$mapNotNull$$inlined$unsafeTransform$1(`$this$mapNotNull`, transform)
}

public fun <T> Flow<Any>.withIndex(): Flow<IndexedValue<Any>> {
   return FlowKt__TransformKt$withIndex$$inlined$unsafeFlow$1(`$this$withIndex`)
}

public fun <T> Flow<Any>.onEach(action: (Any, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt__TransformKt$onEach$$inlined$unsafeTransform$1(`$this$onEach`, action)
}

public fun <T, R> Flow<Any>.scan(initial: Any, operation: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return (Flow<R>)FlowKt.runningFold(`$this$scan`, initial, operation)
}

public fun <T, R> Flow<Any>.runningFold(initial: Any, operation: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt__TransformKt$runningFold$$inlined$unsafeFlow$1(initial, `$this$runningFold`, operation)
}

public fun <T> Flow<Any>.runningReduce(operation: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt__TransformKt$runningReduce$$inlined$unsafeFlow$1(`$this$runningReduce`, operation)
}
