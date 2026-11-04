@file:SourceDebugExtension(["SMAP\nCancellable.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Cancellable.kt\nkotlinx/coroutines/intrinsics/CancellableKt\n*L\n1#1,68:1\n49#1,6:69\n49#1,6:75\n49#1,6:81\n*S KotlinDebug\n*F\n+ 1 Cancellable.kt\nkotlinx/coroutines/intrinsics/CancellableKt\n*L\n17#1:69,6\n29#1:75,6\n38#1:81,6\n*E\n"])

package kotlinx.coroutines.intrinsics

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.DispatchedContinuationKt

@InternalCoroutinesApi
public fun <T> ((Continuation<Any>) -> Any?).startCoroutineCancellable(completion: Continuation<Any>) {
   try {
      DispatchedContinuationKt.resumeCancellableWith$default(
         IntrinsicsKt.intercepted(IntrinsicsKt.createCoroutineUnintercepted(`$this$startCoroutineCancellable`, completion)),
         Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE),
         null,
         2,
         null
      )
   } catch (var5: java.lang.Throwable) {
      dispatcherFailure(completion, var5)
   }
}

internal fun <R, T> ((Any, Continuation<Any>) -> Any?).startCoroutineCancellable(
   receiver: Any,
   completion: Continuation<Any>,
   onCancellation: ((Throwable) -> Unit)? = null
) {
   try {
      DispatchedContinuationKt.resumeCancellableWith(
         IntrinsicsKt.intercepted(IntrinsicsKt.createCoroutineUnintercepted(`$this$startCoroutineCancellable`, receiver, completion)),
         Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE),
         onCancellation
      )
   } catch (var7: java.lang.Throwable) {
      dispatcherFailure(completion, var7)
   }
}

internal fun Continuation<Unit>.startCoroutineCancellable(fatalCompletion: Continuation<*>) {
   try {
      DispatchedContinuationKt.resumeCancellableWith$default(
         IntrinsicsKt.intercepted(`$this$startCoroutineCancellable`), Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE), null, 2, null
      )
   } catch (var5: java.lang.Throwable) {
      dispatcherFailure(fatalCompletion, var5)
   }
}

private inline fun runSafely(completion: Continuation<*>, block: () -> Unit) {
   try {
      block()
   } catch (var4: java.lang.Throwable) {
      dispatcherFailure(completion, var4)
   }
}

private fun dispatcherFailure(completion: Continuation<*>, e: Throwable) {
   completion.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(e)))
   throw e
}
