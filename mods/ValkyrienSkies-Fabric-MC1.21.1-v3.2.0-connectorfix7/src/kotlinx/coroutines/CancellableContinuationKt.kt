@file:SourceDebugExtension(["SMAP\nCancellableContinuation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CompletionHandler.kt\nkotlinx/coroutines/CompletionHandlerKt\n*L\n1#1,386:1\n1#2:387\n19#3:388\n*S KotlinDebug\n*F\n+ 1 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n380#1:388\n*E\n"])

package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.DispatchedContinuation

public suspend inline fun <T> suspendCancellableCoroutine(crossinline block: (CancellableContinuation<Any>) -> Unit): Any {
   val cancellable: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
   cancellable.initCancellability()
   block(cancellable)
   val var10000: Any = cancellable.getResult()
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   return var10000
}

fun <T> `suspendCancellableCoroutine$$forInline`(block: (CancellableContinuation<in T>?) -> Unit, `$completion`: Continuation<in T>) {
   InlineMarker.mark(0)
   val cancellable: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
   cancellable.initCancellability()
   block(cancellable)
   val var10000: Any = cancellable.getResult()
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   InlineMarker.mark(1)
   var10000
}

internal suspend inline fun <T> suspendCancellableCoroutineReusable(crossinline block: (CancellableContinuationImpl<Any>) -> Unit): Any {
   val cancellable: CancellableContinuationImpl = getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(`$completion`))

   try {
      block(cancellable)
   } catch (var7: java.lang.Throwable) {
      cancellable.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
      throw var7
   }

   val var10000: Any = cancellable.getResult()
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   return var10000
}

fun <T> `suspendCancellableCoroutineReusable$$forInline`(block: (CancellableContinuationImpl<in T>?) -> Unit, `$completion`: Continuation<in T>) {
   InlineMarker.mark(0)
   val cancellable: CancellableContinuationImpl = getOrCreateCancellableContinuation(IntrinsicsKt.intercepted(`$completion`))

   try {
      block(cancellable)
   } catch (var7: java.lang.Throwable) {
      cancellable.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
      throw var7
   }

   val var10000: Any = cancellable.getResult()
   if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      DebugProbesKt.probeCoroutineSuspended(`$completion`)
   }

   InlineMarker.mark(1)
   var10000
}

internal fun <T> getOrCreateCancellableContinuation(delegate: Continuation<Any>): CancellableContinuationImpl<Any> {
   if (delegate !is DispatchedContinuation) {
      return CancellableContinuationImpl(delegate, 1)
   } else {
      var var10000: CancellableContinuationImpl = (delegate as DispatchedContinuation).claimReusableCancellableContinuation$kotlinx_coroutines_core()
      if (var10000 != null) {
         var10000 = if (var10000.resetStateReusable()) var10000 else null
         if (var10000 != null) {
            return var10000
         }
      }

      return CancellableContinuationImpl(delegate, 2)
   }
}

@InternalCoroutinesApi
public fun CancellableContinuation<*>.disposeOnCancellation(handle: DisposableHandle) {
   `$this$disposeOnCancellation`.invokeOnCancellation(DisposeOnCancel(handle))
}
