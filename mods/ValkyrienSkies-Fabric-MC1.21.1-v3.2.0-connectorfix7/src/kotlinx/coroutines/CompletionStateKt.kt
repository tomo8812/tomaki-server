@file:SourceDebugExtension(["SMAP\nCompletionState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CompletionState.kt\nkotlinx/coroutines/CompletionStateKt\n+ 2 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n*L\n1#1,67:1\n61#2,2:68\n61#2,2:70\n*S KotlinDebug\n*F\n+ 1 CompletionState.kt\nkotlinx/coroutines/CompletionStateKt\n*L\n21#1:68,2\n27#1:70,2\n*E\n"])

package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.StackTraceRecoveryKt

internal fun <T> Result<Any>.toState(onCancellation: ((Throwable) -> Unit)? = ...): Any? {
   val var10000: java.lang.Throwable = Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$toState`)
   return if (var10000 == null)
      (if (onCancellation != null) CompletedWithCancellation(`$this$toState`, onCancellation) else `$this$toState`)
      else
      CompletedExceptionally(var10000, false, 2, null)
   }

@JvmSynthetic
fun `toState$default`(var0: Any, var1: Function1, var2: Int, var3: Any) {
   if ((var2 and 1) != 0) {
      var1 = null
   }

   toState(var0, var1)
}

internal fun <T> Result<Any>.toState(caller: CancellableContinuation<*>): Any? {
   val var10000: java.lang.Throwable = Result.exceptionOrNull_impl/* $VF was: exceptionOrNull-impl */(`$this$toState`)
   return if (var10000 == null)
      `$this$toState`
      else
      CompletedExceptionally(
         if (RECOVER_STACK_TRACES && caller as Continuation is CoroutineStackFrame)
            StackTraceRecoveryKt.access$recoverFromStackFrame(var10000, caller as CoroutineStackFrame)
            else
            var10000,
         false,
         2,
         null
      )
   }

internal fun <T> recoverResult(state: Any?, uCont: Continuation<Any>): Result<Any> {
   val var10000: Any
   if (state is CompletedExceptionally) {
      val `exception$iv`: java.lang.Throwable = (state as CompletedExceptionally).cause
      var10000 = Result.constructor_impl/* $VF was: constructor-impl */(
         ResultKt.createFailure(
            if (RECOVER_STACK_TRACES && uCont is CoroutineStackFrame)
               StackTraceRecoveryKt.access$recoverFromStackFrame(`exception$iv`, uCont as CoroutineStackFrame)
               else
               `exception$iv`
         )
      )
   } else {
      var10000 = Result.constructor_impl/* $VF was: constructor-impl */(state)
   }

   return var10000
}
