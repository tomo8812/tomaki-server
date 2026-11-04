@file:JvmMultifileClass
@file:JvmName("FlowKt")

package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function3
import kotlin.jvm.internal.InlineMarker
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.internal.NopCollector

public suspend fun Flow<*>.collect() {
   val var10000: Any = `$this$collect`.collect(NopCollector.INSTANCE, `$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

public fun <T> Flow<Any>.launchIn(scope: CoroutineScope): Job {
   return BuildersKt.launch$default(scope, null, null, {
      // $VF: Could not decompile lambda - root function was not found. Is this a suspend lambda?
   } as Function2, 3, null)
}

public suspend inline fun <T> Flow<Any>.collectIndexed(crossinline action: (Int, Any, Continuation<Unit>) -> Any?) {
   val var10000: Any = `$this$collectIndexed`.collect(object : FlowCollector<Any> {
      private final var index: Int

      public override suspend fun emit(value: Any) {
         val var10000: Function3 = action
         val var5: Int = this.index++
         if (var5 < 0) {
            throw ArithmeticException("Index overflow has happened")
         } else {
            val var6: Any = var10000(Boxing.boxInt(var5), value, `$completion`)
            return if (var6 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var6 else Unit.INSTANCE
         }
      }
   }, `$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

fun <T> Flow<out T>.`collectIndexed$$forInline`(action: (Int?, T?, Continuation<in Unit>?) -> Any, `$completion`: Continuation<in Unit>) {
   val var10001: FlowCollector = object : FlowCollector<Any> {
      private final var index: Int

      public override suspend fun emit(value: Any) {
         val var10000: Function3 = action
         val var5: Int = this.index++
         if (var5 < 0) {
            throw ArithmeticException("Index overflow has happened")
         } else {
            val var6: Any = var10000(Boxing.boxInt(var5), value, `$completion`)
            if (var6 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var6 else Unit.INSTANCE
         }
      }
   }
   InlineMarker.mark(0)
   `$this$collectIndexed`.collect(var10001, `$completion`)
   InlineMarker.mark(1)
   Unit.INSTANCE
}

public suspend fun <T> Flow<Any>.collectLatest(action: (Any, Continuation<Unit>) -> Any?) {
   val var10000: Any = FlowKt.collect(FlowKt.buffer$default(FlowKt.mapLatest(`$this$collectLatest`, action), 0, null, 2, null), `$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

public suspend fun <T> FlowCollector<Any>.emitAll(flow: Flow<Any>) {
   FlowKt.ensureActive(`$this$emitAll`)
   val var10000: Any = flow.collect(`$this$emitAll`, `$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

@Deprecated(message = "Backwards compatibility with JS and K/N", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
public suspend inline fun <T> Flow<Any>.collect(crossinline action: (Any, Continuation<Unit>) -> Any?) {
   val var10000: Any = `$this$collect`.collect(object : FlowCollector<Any> {
      public override suspend fun emit(value: Any) {
         val var10000: Any = action(value, `$completion`)
         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }, `$completion`)
   return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
}

/** @deprecated */
@Deprecated(message = "Backwards compatibility with JS and K/N", level = DeprecationLevel.HIDDEN)
@JvmSynthetic
fun <T> Flow<out T>.`collect$$forInline`(action: (T?, Continuation<in Unit>?) -> Any, `$completion`: Continuation<in Unit>) {
   val var10001: FlowCollector = object : FlowCollector<Any> {
      public override suspend fun emit(value: Any) {
         val var10000: Any = action(value, `$completion`)
         if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }
   InlineMarker.mark(0)
   `$this$collect`.collect(var10001, `$completion`)
   InlineMarker.mark(1)
   Unit.INSTANCE
}
