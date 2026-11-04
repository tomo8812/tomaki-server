@file:JvmMultifileClass
@file:JvmName("BuildersKt")
@file:SourceDebugExtension(["SMAP\nBuilders.common.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Builders.common.kt\nkotlinx/coroutines/BuildersKt__Builders_commonKt\n+ 2 CoroutineContext.kt\nkotlinx/coroutines/CoroutineContextKt\n*L\n1#1,273:1\n95#2,5:274\n*S KotlinDebug\n*F\n+ 1 Builders.common.kt\nkotlinx/coroutines/BuildersKt__Builders_commonKt\n*L\n166#1:274,5\n*E\n"])

package kotlinx.coroutines

import kotlin.contracts.InvocationKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.CoroutineContext.Key
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.ScopeCoroutine
import kotlinx.coroutines.internal.ThreadContextKt
import kotlinx.coroutines.intrinsics.CancellableKt
import kotlinx.coroutines.intrinsics.UndispatchedKt

private const val UNDECIDED: Int = 0
private const val SUSPENDED: Int = 1
private const val RESUMED: Int = 2

public fun CoroutineScope.launch(
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   start: CoroutineStart = CoroutineStart.DEFAULT,
   block: (CoroutineScope, Continuation<Unit>) -> Any?
): Job {
   val newContext: CoroutineContext = CoroutineContextKt.newCoroutineContext(`$this$launch`, context)
   val coroutine: StandaloneCoroutine = if (start.isLazy) LazyStandaloneCoroutine(newContext, block) else StandaloneCoroutine(newContext, true)
   coroutine.start(start, coroutine, block)
   return coroutine
}

public fun <T> CoroutineScope.async(
   context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext,
   start: CoroutineStart = CoroutineStart.DEFAULT,
   block: (CoroutineScope, Continuation<Any>) -> Any?
): Deferred<Any> {
   val newContext: CoroutineContext = CoroutineContextKt.newCoroutineContext(`$this$async`, context)
   val coroutine: DeferredCoroutine = if (start.isLazy) LazyDeferredCoroutine(newContext, block) else DeferredCoroutine(newContext, true)
   coroutine.start(start, coroutine, block)
   return coroutine
}

public suspend fun <T> withContext(context: CoroutineContext, block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   val oldContext: CoroutineContext = `$completion`.getContext()
   val newContext: CoroutineContext = CoroutineContextKt.newCoroutineContext(oldContext, context)
   JobKt.ensureActive(newContext)
   val var10000: Any
   if (newContext === oldContext) {
      val coroutine: ScopeCoroutine = ScopeCoroutine(newContext, `$completion`)
      var10000 = UndispatchedKt.startUndispatchedOrReturn(coroutine, coroutine, block)
   } else if (newContext.get(ContinuationInterceptor.Key as Key) == oldContext.get(ContinuationInterceptor.Key as Key)) {
      val var17: UndispatchedCoroutine = UndispatchedCoroutine(newContext, `$completion`)
      val `context$iv`: CoroutineContext = var17.getContext()

      var var13: Any
      try {
         var13 = UndispatchedKt.startUndispatchedOrReturn(var17, var17, block)
      } finally {
         ThreadContextKt.restoreThreadContext(`context$iv`, ThreadContextKt.updateThreadContext(`context$iv`, null))
      }

      var10000 = var13
   } else {
      val var18: DispatchedCoroutine = DispatchedCoroutine(newContext, `$completion`)
      CancellableKt.startCoroutineCancellable$default(block, var18, var18, null, 4, null)
      var10000 = var18.getResult$kotlinx_coroutines_core()
   }

   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   return var10000
}

public suspend inline operator fun <T> CoroutineDispatcher.invoke(noinline block: (CoroutineScope, Continuation<Any>) -> Any?): Any {
   return BuildersKt.withContext(`$this$invoke` as CoroutineContext, block, `$completion`)
}

fun <T> CoroutineDispatcher.`invoke$$forInline`(block: (CoroutineScope?, Continuation<in T>?) -> Any, `$completion`: Continuation<in T>) {
   val var10000: CoroutineContext = `$this$invoke` as CoroutineContext
   InlineMarker.mark(0)
   val var4: Any = BuildersKt.withContext(var10000, block, `$completion`)
   InlineMarker.mark(1)
   var4
}
