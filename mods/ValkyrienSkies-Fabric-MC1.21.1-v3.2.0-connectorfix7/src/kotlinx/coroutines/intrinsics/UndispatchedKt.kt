@file:SourceDebugExtension(["SMAP\nUndispatched.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Undispatched.kt\nkotlinx/coroutines/intrinsics/UndispatchedKt\n+ 2 ProbesSupport.kt\nkotlinx/coroutines/internal/ProbesSupportKt\n+ 3 CoroutineContext.kt\nkotlinx/coroutines/CoroutineContextKt\n+ 4 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n*L\n1#1,127:1\n55#1:128\n56#1,11:130\n55#1:141\n56#1,2:143\n58#1,9:150\n55#1:159\n56#1,2:161\n58#1,9:168\n97#1,4:178\n114#1,6:182\n120#1,5:190\n97#1,4:195\n114#1,6:199\n120#1,5:207\n11#2:129\n11#2:142\n11#2:160\n11#2:177\n95#3,5:145\n95#3,5:163\n61#4,2:188\n61#4,2:205\n61#4,2:212\n61#4,2:214\n*S KotlinDebug\n*F\n+ 1 Undispatched.kt\nkotlinx/coroutines/intrinsics/UndispatchedKt\n*L\n18#1:128\n18#1:130,11\n29#1:141\n29#1:143,2\n29#1:150,9\n42#1:159\n42#1:161,2\n42#1:168,9\n77#1:178,4\n77#1:182,6\n77#1:190,5\n88#1:195,4\n88#1:199,6\n88#1:207,5\n18#1:129\n29#1:142\n42#1:160\n55#1:177\n30#1:145,5\n43#1:163,5\n77#1:188,2\n88#1:205,2\n119#1:212,2\n120#1:214,2\n*E\n"])

package kotlinx.coroutines.intrinsics

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import kotlinx.coroutines.CompletedExceptionally
import kotlinx.coroutines.JobSupportKt
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.internal.ScopeCoroutine
import kotlinx.coroutines.internal.StackTraceRecoveryKt
import kotlinx.coroutines.internal.ThreadContextKt

internal fun <T> ((Continuation<Any>) -> Any?).startCoroutineUnintercepted(completion: Continuation<Any>) {
   val `actualCompletion$iv`: Continuation = DebugProbesKt.probeCoroutineCreated(completion)

   var var7: Any
   try {
      var7 = (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$startCoroutineUnintercepted`, 1) as Function1)(`actualCompletion$iv`)
   } catch (var8: java.lang.Throwable) {
      `actualCompletion$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var8)))
      return
   }

   if (var7 != IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      `actualCompletion$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(var7))
   }
}

internal fun <T> ((Continuation<Any>) -> Any?).startCoroutineUndispatched(completion: Continuation<Any>) {
   val `actualCompletion$iv`: Continuation = DebugProbesKt.probeCoroutineCreated(completion)

   var var13: Any
   try {
      val actualCompletion: Continuation = `actualCompletion$iv`
      val `context$iv`: CoroutineContext = completion.getContext()

      var var12: Any
      try {
         var12 = (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$startCoroutineUndispatched`, 1) as Function1)(actualCompletion)
      } finally {
         ThreadContextKt.restoreThreadContext(`context$iv`, ThreadContextKt.updateThreadContext(`context$iv`, null))
      }

      var13 = var12
   } catch (var18: java.lang.Throwable) {
      `actualCompletion$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var18)))
      return
   }

   if (var13 != IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      `actualCompletion$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(var13))
   }
}

internal fun <R, T> ((Any, Continuation<Any>) -> Any?).startCoroutineUndispatched(receiver: Any, completion: Continuation<Any>) {
   val `actualCompletion$iv`: Continuation = DebugProbesKt.probeCoroutineCreated(completion)

   var var14: Any
   try {
      val actualCompletion: Continuation = `actualCompletion$iv`
      val `context$iv`: CoroutineContext = completion.getContext()

      var var13: Any
      try {
         var13 = (TypeIntrinsics.beforeCheckcastToFunctionOfArity(`$this$startCoroutineUndispatched`, 2) as Function2)(receiver, actualCompletion)
      } finally {
         ThreadContextKt.restoreThreadContext(`context$iv`, ThreadContextKt.updateThreadContext(`context$iv`, null))
      }

      var14 = var13
   } catch (var19: java.lang.Throwable) {
      `actualCompletion$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var19)))
      return
   }

   if (var14 != IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      `actualCompletion$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(var14))
   }
}

private inline fun <T> startDirect(completion: Continuation<Any>, block: (Continuation<Any>) -> Any?) {
   val actualCompletion: Continuation = DebugProbesKt.probeCoroutineCreated(completion)

   var var5: Any
   try {
      var5 = block(actualCompletion)
   } catch (var7: java.lang.Throwable) {
      actualCompletion.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(var7)))
      return
   }

   if (var5 != IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      actualCompletion.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(var5))
   }
}

internal fun <T, R> ScopeCoroutine<Any>.startUndispatchedOrReturn(receiver: Any, block: (Any, Continuation<Any>) -> Any?): Any? {
   var `state$iv`: Any
   try {
      `state$iv` = (TypeIntrinsics.beforeCheckcastToFunctionOfArity(block, 2) as Function2)(receiver, `$this$startUndispatchedOrReturn`)
   } catch (var12: java.lang.Throwable) {
      `state$iv` = CompletedExceptionally(var12, false, 2, null)
   }

   val var10000: Any
   if (`state$iv` === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      var10000 = IntrinsicsKt.getCOROUTINE_SUSPENDED()
   } else {
      `state$iv` = `$this$startUndispatchedOrReturn`.makeCompletingOnce$kotlinx_coroutines_core(`state$iv`)
      if (`state$iv` === JobSupportKt.COMPLETING_WAITING_CHILDREN) {
         var10000 = IntrinsicsKt.getCOROUTINE_SUSPENDED()
      } else if (`state$iv` is CompletedExceptionally) {
         val var13: java.lang.Throwable = (`state$iv` as CompletedExceptionally).cause
         if (true) {
            val var15: java.lang.Throwable = (`state$iv` as CompletedExceptionally).cause
            val var16: Continuation = `$this$startUndispatchedOrReturn`.uCont
            throw if (RECOVER_STACK_TRACES && var16 is CoroutineStackFrame)
               StackTraceRecoveryKt.access$recoverFromStackFrame(var15, var16 as CoroutineStackFrame)
               else
               var15
            }

         if (`state$iv` is CompletedExceptionally) {
            val `exception$iv$iv`: java.lang.Throwable = (`state$iv` as CompletedExceptionally).cause
            val `continuation$iv$iv`: Continuation = `$this$startUndispatchedOrReturn`.uCont
            throw if (RECOVER_STACK_TRACES && `continuation$iv$iv` is CoroutineStackFrame)
               StackTraceRecoveryKt.access$recoverFromStackFrame(`exception$iv$iv`, `continuation$iv$iv` as CoroutineStackFrame)
               else
               `exception$iv$iv`
            }

         var10000 = `state$iv`
      } else {
         var10000 = JobSupportKt.unboxState(`state$iv`)
      }
   }

   return var10000
}

internal fun <T, R> ScopeCoroutine<Any>.startUndispatchedOrReturnIgnoreTimeout(receiver: Any, block: (Any, Continuation<Any>) -> Any?): Any? {
   var `state$iv`: Any
   try {
      `state$iv` = (TypeIntrinsics.beforeCheckcastToFunctionOfArity(block, 2) as Function2)(receiver, `$this$startUndispatchedOrReturnIgnoreTimeout`)
   } catch (var12: java.lang.Throwable) {
      `state$iv` = CompletedExceptionally(var12, false, 2, null)
   }

   val var10000: Any
   if (`state$iv` === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      var10000 = IntrinsicsKt.getCOROUTINE_SUSPENDED()
   } else {
      `state$iv` = `$this$startUndispatchedOrReturnIgnoreTimeout`.makeCompletingOnce$kotlinx_coroutines_core(`state$iv`)
      if (`state$iv` === JobSupportKt.COMPLETING_WAITING_CHILDREN) {
         var10000 = IntrinsicsKt.getCOROUTINE_SUSPENDED()
      } else if (`state$iv` !is CompletedExceptionally) {
         var10000 = JobSupportKt.unboxState(`state$iv`)
      } else {
         if ((`state$iv` as CompletedExceptionally).cause !is TimeoutCancellationException
            || ((`state$iv` as CompletedExceptionally).cause as TimeoutCancellationException).coroutine != `$this$startUndispatchedOrReturnIgnoreTimeout`) {
            val var15: java.lang.Throwable = (`state$iv` as CompletedExceptionally).cause
            val var16: Continuation = `$this$startUndispatchedOrReturnIgnoreTimeout`.uCont
            throw if (RECOVER_STACK_TRACES && var16 is CoroutineStackFrame)
               StackTraceRecoveryKt.access$recoverFromStackFrame(var15, var16 as CoroutineStackFrame)
               else
               var15
            }

         if (`state$iv` is CompletedExceptionally) {
            val `exception$iv$iv`: java.lang.Throwable = (`state$iv` as CompletedExceptionally).cause
            val `continuation$iv$iv`: Continuation = `$this$startUndispatchedOrReturnIgnoreTimeout`.uCont
            throw if (RECOVER_STACK_TRACES && `continuation$iv$iv` is CoroutineStackFrame)
               StackTraceRecoveryKt.access$recoverFromStackFrame(`exception$iv$iv`, `continuation$iv$iv` as CoroutineStackFrame)
               else
               `exception$iv$iv`
            }

         var10000 = `state$iv`
      }
   }

   return var10000
}

private inline fun <T> ScopeCoroutine<Any>.undispatchedResult(shouldThrow: (Throwable) -> Boolean, startBlock: () -> Any?): Any? {
   var state: Any
   try {
      state = startBlock()
   } catch (var9: java.lang.Throwable) {
      state = CompletedExceptionally(var9, false, 2, null)
   }

   if (state === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
      return IntrinsicsKt.getCOROUTINE_SUSPENDED()
   } else {
      state = `$this$undispatchedResult`.makeCompletingOnce$kotlinx_coroutines_core(state)
      if (state === JobSupportKt.COMPLETING_WAITING_CHILDREN) {
         return IntrinsicsKt.getCOROUTINE_SUSPENDED()
      } else {
         val var10000: Any
         if (state is CompletedExceptionally) {
            if (shouldThrow((state as CompletedExceptionally).cause) as java.lang.Boolean) {
               val var11: java.lang.Throwable = (state as CompletedExceptionally).cause
               val var12: Continuation = `$this$undispatchedResult`.uCont
               throw if (RECOVER_STACK_TRACES && var12 is CoroutineStackFrame)
                  StackTraceRecoveryKt.access$recoverFromStackFrame(var11, var12 as CoroutineStackFrame)
                  else
                  var11
               }

            if (state is CompletedExceptionally) {
               val `exception$iv`: java.lang.Throwable = (state as CompletedExceptionally).cause
               val `continuation$iv`: Continuation = `$this$undispatchedResult`.uCont
               throw if (RECOVER_STACK_TRACES && `continuation$iv` is CoroutineStackFrame)
                  StackTraceRecoveryKt.access$recoverFromStackFrame(`exception$iv`, `continuation$iv` as CoroutineStackFrame)
                  else
                  `exception$iv`
               }

            var10000 = state
         } else {
            var10000 = JobSupportKt.unboxState(state)
         }

         return var10000
      }
   }
}
