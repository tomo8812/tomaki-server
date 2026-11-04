@file:JvmMultifileClass
@file:JvmName("FlowKt")
@file:SourceDebugExtension(["SMAP\nBuilders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n+ 2 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,354:1\n107#2:355\n107#2:356\n107#2:357\n107#2:358\n107#2:359\n107#2:360\n107#2:361\n107#2:362\n107#2:363\n107#2:364\n107#2:365\n107#2:366\n*S KotlinDebug\n*F\n+ 1 Builders.kt\nkotlinx/coroutines/flow/FlowKt__BuildersKt\n*L\n68#1:355\n82#1:356\n89#1:357\n98#1:358\n107#1:359\n122#1:360\n131#1:361\n153#1:362\n164#1:363\n175#1:364\n184#1:365\n193#1:366\n*E\n"])

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.channels.ProducerScope

public fun <T> flow(block: (FlowCollector<Any>, Continuation<Unit>) -> Any?): Flow<Any> {
   return SafeFlow(block)
}

public fun <T> (() -> Any).asFlow(): Flow<Any> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$1(`$this$asFlow`)
}

public fun <T> ((Continuation<Any>) -> Any?).asFlow(): Flow<Any> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$2(`$this$asFlow`)
}

public fun <T> Iterable<Any>.asFlow(): Flow<Any> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$3(`$this$asFlow`)
}

public fun <T> Iterator<Any>.asFlow(): Flow<Any> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$4(`$this$asFlow`)
}

public fun <T> Sequence<Any>.asFlow(): Flow<Any> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$5(`$this$asFlow`)
}

public fun <T> flowOf(vararg elements: Any): Flow<Any> {
   return FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$1(elements)
}

public fun <T> flowOf(value: Any): Flow<Any> {
   return FlowKt__BuildersKt$flowOf$$inlined$unsafeFlow$2(value)
}

public fun <T> emptyFlow(): Flow<Any> {
   return EmptyFlow.INSTANCE
}

public fun <T> Array<Any>.asFlow(): Flow<Any> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$6(`$this$asFlow`)
}

public fun IntArray.asFlow(): Flow<Int> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$7(`$this$asFlow`)
}

public fun LongArray.asFlow(): Flow<Long> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$8(`$this$asFlow`)
}

public fun IntRange.asFlow(): Flow<Int> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$9(`$this$asFlow`)
}

public fun LongRange.asFlow(): Flow<Long> {
   return FlowKt__BuildersKt$asFlow$$inlined$unsafeFlow$10(`$this$asFlow`)
}

public fun <T> channelFlow(block: (ProducerScope<Any>, Continuation<Unit>) -> Any?): Flow<Any> {
   return ChannelFlowBuilder(block, null, 0, null, 14, null)
}

public fun <T> callbackFlow(block: (ProducerScope<Any>, Continuation<Unit>) -> Any?): Flow<Any> {
   return CallbackFlowBuilder(block, null, 0, null, 14, null)
}
