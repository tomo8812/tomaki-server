@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nZip.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Zip.kt\nkotlinx/coroutines/flow/FlowKt__ZipKt\n+ 2 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,332:1\n272#1,3:334\n272#1,3:337\n261#1:340\n263#1:342\n272#1,3:343\n261#1:346\n263#1:348\n272#1,3:349\n261#1:352\n263#1:354\n272#1,3:355\n107#2:333\n107#2:341\n107#2:347\n107#2:353\n107#2:358\n107#2:359\n107#2:362\n37#3,2:360\n37#3,2:363\n*S KotlinDebug\n*F\n+ 1 Zip.kt\nkotlinx/coroutines/flow/FlowKt__ZipKt\n*L\n75#1:334,3\n103#1:337,3\n119#1:340\n119#1:342\n138#1:343,3\n156#1:346\n156#1:348\n177#1:349,3\n197#1:352\n197#1:354\n220#1:355,3\n32#1:333\n119#1:341\n156#1:347\n197#1:353\n237#1:358\n261#1:359\n288#1:362\n287#1:360,2\n306#1:363,2\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.internal.CombineKt

@JvmName(name = "flowCombine")
public fun <T1, T2, R> Flow<Any>.combine(flow: Flow<Any>, transform: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt__ZipKt$combine$$inlined$unsafeFlow$1(`$this$combine`, flow, transform)
}

public fun <T1, T2, R> combine(flow: Flow<Any>, flow2: Flow<Any>, transform: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt.flowCombine(flow, flow2, transform)
}

@JvmName(name = "flowCombineTransform")
public fun <T1, T2, R> Flow<Any>.combineTransform(flow: Flow<Any>, transform: (FlowCollector<Any>, Any, Any, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt.flow(FlowKt__ZipKt$combineTransform$$inlined$combineTransformUnsafe$FlowKt__ZipKt$1(arrayOf(`$this$combineTransform`, flow), null, transform))
}

public fun <T1, T2, R> combineTransform(flow: Flow<Any>, flow2: Flow<Any>, transform: (FlowCollector<Any>, Any, Any, Continuation<Unit>) -> Any?): Flow<Any> {
   return FlowKt.flow(FlowKt__ZipKt$combineTransform$$inlined$combineTransformUnsafe$FlowKt__ZipKt$2(arrayOf(flow, flow2), null, transform))
}

public fun <T1, T2, T3, R> combine(flow: Flow<Any>, flow2: Flow<Any>, flow3: Flow<Any>, transform: (Any, Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return FlowKt__ZipKt$combine$$inlined$combineUnsafe$FlowKt__ZipKt$1(arrayOf(flow, flow2, flow3), transform)
}

public fun <T1, T2, T3, R> combineTransform(
   flow: Flow<Any>,
   flow2: Flow<Any>,
   flow3: Flow<Any>,
   transform: (FlowCollector<Any>, Any, Any, Any, Continuation<Unit>) -> Any?
): Flow<Any> {
   return FlowKt.flow(FlowKt__ZipKt$combineTransform$$inlined$combineTransformUnsafe$FlowKt__ZipKt$3(arrayOf(flow, flow2, flow3), null, transform))
}

public fun <T1, T2, T3, T4, R> combine(
   flow: Flow<Any>,
   flow2: Flow<Any>,
   flow3: Flow<Any>,
   flow4: Flow<Any>,
   transform: (Any, Any, Any, Any, Continuation<Any>) -> Any?
): Flow<Any> {
   return FlowKt__ZipKt$combine$$inlined$combineUnsafe$FlowKt__ZipKt$2(arrayOf(flow, flow2, flow3, flow4), transform)
}

public fun <T1, T2, T3, T4, R> combineTransform(
   flow: Flow<Any>,
   flow2: Flow<Any>,
   flow3: Flow<Any>,
   flow4: Flow<Any>,
   transform: (FlowCollector<Any>, Any, Any, Any, Any, Continuation<Unit>) -> Any?
): Flow<Any> {
   return FlowKt.flow(FlowKt__ZipKt$combineTransform$$inlined$combineTransformUnsafe$FlowKt__ZipKt$4(arrayOf(flow, flow2, flow3, flow4), null, transform))
}

public fun <T1, T2, T3, T4, T5, R> combine(
   flow: Flow<Any>,
   flow2: Flow<Any>,
   flow3: Flow<Any>,
   flow4: Flow<Any>,
   flow5: Flow<Any>,
   transform: (Any, Any, Any, Any, Any, Continuation<Any>) -> Any?
): Flow<Any> {
   return FlowKt__ZipKt$combine$$inlined$combineUnsafe$FlowKt__ZipKt$3(arrayOf(flow, flow2, flow3, flow4, flow5), transform)
}

public fun <T1, T2, T3, T4, T5, R> combineTransform(
   flow: Flow<Any>,
   flow2: Flow<Any>,
   flow3: Flow<Any>,
   flow4: Flow<Any>,
   flow5: Flow<Any>,
   transform: (FlowCollector<Any>, Any, Any, Any, Any, Any, Continuation<Unit>) -> Any?
): Flow<Any> {
   return FlowKt.flow(
      FlowKt__ZipKt$combineTransform$$inlined$combineTransformUnsafe$FlowKt__ZipKt$5(arrayOf(flow, flow2, flow3, flow4, flow5), null, transform)
   )
}

@JvmSynthetic
public inline fun <reified T, R> combine(vararg flows: Flow<Any>, crossinline transform: (Array<Any>, Continuation<Any>) -> Any?): Flow<Any> {
   Intrinsics.needClassReification()
   return FlowKt__ZipKt$combine$$inlined$unsafeFlow$2(flows, transform)
}

@JvmSynthetic
public inline fun <reified T, R> combineTransform(vararg flows: Flow<Any>, crossinline transform: (FlowCollector<Any>, Array<Any>, Continuation<Unit>) -> Any?): Flow<
      Any
   > {
   Intrinsics.needClassReification()
   return FlowKt.flow({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2)
}

@JvmSynthetic
private inline fun <reified T, R> combineUnsafe(vararg flows: Flow<Any>, crossinline transform: (Array<Any>, Continuation<Any>) -> Any?): Flow<Any> {
   Intrinsics.needClassReification()
   return FlowKt__ZipKt$combineUnsafe$$inlined$unsafeFlow$1(flows, transform)
}

@JvmSynthetic
private inline fun <reified T, R> combineTransformUnsafe(
   vararg flows: Flow<Any>,
   crossinline transform: (FlowCollector<Any>, Array<Any>, Continuation<Unit>) -> Any?
): Flow<Any> {
   Intrinsics.needClassReification()
   return FlowKt.flow({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2)
}

private fun <T> nullArrayFactory(): () -> Array<Any>? {
   return {
      null
   }
}

@JvmSynthetic
public inline fun <reified T, R> combine(flows: Iterable<Flow<Any>>, crossinline transform: (Array<Any>, Continuation<Any>) -> Any?): Flow<Any> {
   val flowArray: Array<Flow> = CollectionsKt.toList(flows).toArray(arrayOfNulls(0))
   Intrinsics.needClassReification()
   return FlowKt__ZipKt$combine$$inlined$unsafeFlow$3(flowArray, transform)
}

@JvmSynthetic
public inline fun <reified T, R> combineTransform(
   flows: Iterable<Flow<Any>>,
   crossinline transform: (FlowCollector<Any>, Array<Any>, Continuation<Unit>) -> Any?
): Flow<Any> {
   val flowArray: Array<Flow> = CollectionsKt.toList(flows).toArray(arrayOfNulls(0))
   Intrinsics.needClassReification()
   return FlowKt.flow({
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2)
}

public fun <T1, T2, R> Flow<Any>.zip(other: Flow<Any>, transform: (Any, Any, Continuation<Any>) -> Any?): Flow<Any> {
   return CombineKt.zipImpl(`$this$zip`, other, transform)
}
