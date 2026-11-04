@file:SourceDebugExtension(["SMAP\nDispatchedTask.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DispatchedTask.kt\nkotlinx/coroutines/DispatchedTaskKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 DispatchedContinuation.kt\nkotlinx/coroutines/internal/DispatchedContinuation\n+ 4 CoroutineContext.kt\nkotlinx/coroutines/CoroutineContextKt\n+ 5 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n*L\n1#1,222:1\n200#1,17:240\n1#2:223\n255#3:224\n256#3,2:235\n258#3:239\n107#4,10:225\n118#4,2:237\n61#5,2:257\n*S KotlinDebug\n*F\n+ 1 DispatchedTask.kt\nkotlinx/coroutines/DispatchedTaskKt\n*L\n190#1:240,17\n178#1:224\n178#1:235,2\n178#1:239\n178#1:225,10\n178#1:237,2\n220#1:257,2\n*E\n"])

package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.DispatchedContinuation
import kotlinx.coroutines.internal.StackTraceRecoveryKt
import kotlinx.coroutines.internal.ThreadContextKt

internal const val MODE_ATOMIC: Int = 0

@PublishedApi
internal const val MODE_CANCELLABLE: Int = 1

internal const val MODE_CANCELLABLE_REUSABLE: Int = 2
internal const val MODE_UNDISPATCHED: Int = 4
internal const val MODE_UNINITIALIZED: Int = -1

internal final val isCancellableMode: Boolean
   internal final get() {
      return `$this$isCancellableMode` == 1 || `$this$isCancellableMode` == 2
   }


internal final val isReusableMode: Boolean
   internal final get() {
      return `$this$isReusableMode` == 2
   }


internal fun <T> DispatchedTask<Any>.dispatch(mode: Int) {
   if (ASSERTIONS_ENABLED && mode == -1) {
      throw AssertionError()
   } else {
      val var6: Continuation = `$this$dispatch`.delegate
      val undispatched: Boolean = mode == 4
      if (mode != 4 && var6 is DispatchedContinuation && isCancellableMode == isCancellableMode) {
         val dispatcher: CoroutineDispatcher = (var6 as DispatchedContinuation).dispatcher
         val context: CoroutineContext = var6.getContext()
         if (dispatcher.isDispatchNeeded(context)) {
            dispatcher.dispatch(context, `$this$dispatch`)
         } else {
            resumeUnconfined(`$this$dispatch`)
         }
      } else {
         resume(`$this$dispatch`, var6, undispatched)
      }
   }
}

internal fun <T> DispatchedTask<Any>.resume(delegate: Continuation<Any>, undispatched: Boolean) {
   val state: Any = `$this$resume`.takeState$kotlinx_coroutines_core()
   val exception: java.lang.Throwable = `$this$resume`.getExceptionalResult$kotlinx_coroutines_core(state)
   val result: Any = if (exception != null)
      Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(exception))
      else
      Result.constructor_impl/* $VF was: constructor-impl */(`$this$resume`.getSuccessfulResult$kotlinx_coroutines_core(state))
      if (undispatched) {
      val `this_$iv`: DispatchedContinuation = delegate as DispatchedContinuation
      val `continuation$iv$iv`: Continuation = (delegate as DispatchedContinuation).continuation
      val `countOrElement$iv$iv`: Any = (delegate as DispatchedContinuation).countOrElement
      val `context$iv$iv`: CoroutineContext = (delegate as DispatchedContinuation).continuation.getContext()
      val `oldValue$iv$iv`: Any = ThreadContextKt.updateThreadContext(`context$iv$iv`, `countOrElement$iv$iv`)
      val `undispatchedCompletion$iv$iv`: UndispatchedCoroutine = if (`oldValue$iv$iv` != ThreadContextKt.NO_THREAD_ELEMENTS)
         CoroutineContextKt.updateUndispatchedCompletion(`continuation$iv$iv`, `context$iv$iv`, `oldValue$iv$iv`)
         else
         null

      try {
         `this_$iv`.continuation.resumeWith(result)
      } finally {
         if (`undispatchedCompletion$iv$iv` == null || `undispatchedCompletion$iv$iv`.clearThreadContext()) {
            ThreadContextKt.restoreThreadContext(`context$iv$iv`, `oldValue$iv$iv`)
         }
      }
   } else {
      delegate.resumeWith(result)
   }
}

private fun DispatchedTask<*>.resumeUnconfined() {
   val eventLoop: EventLoop = ThreadLocalEventLoop.INSTANCE.eventLoop
   if (eventLoop.isUnconfinedLoopActive) {
      eventLoop.dispatchUnconfined(`$this$resumeUnconfined`)
   } else {
      val `$this$runUnconfinedEventLoop$iv`: DispatchedTask = `$this$resumeUnconfined`
      eventLoop.incrementUseCount(true)

      try {
         resume(`$this$resumeUnconfined`, `$this$resumeUnconfined`.delegate, true)

         while (eventLoop.processUnconfinedEvent()) {
         }
      } catch (var8: java.lang.Throwable) {
         `$this$runUnconfinedEventLoop$iv`.handleFatalException$kotlinx_coroutines_core(var8, null)
      } finally {
         eventLoop.decrementUseCount(true)
      }
   }
}

internal inline fun DispatchedTask<*>.runUnconfinedEventLoop(eventLoop: EventLoop, block: () -> Unit) {
   eventLoop.incrementUseCount(true)

   try {
      block()

      while (eventLoop.processUnconfinedEvent()) {
      }
   } catch (var7: java.lang.Throwable) {
      `$this$runUnconfinedEventLoop`.handleFatalException$kotlinx_coroutines_core(var7, null)
   } finally {
      InlineMarker.finallyStart(1)
      eventLoop.decrementUseCount(true)
      InlineMarker.finallyEnd(1)
   }
}

internal inline fun Continuation<*>.resumeWithStackTrace(exception: Throwable) {
   `$this$resumeWithStackTrace`.resumeWith(
      Result.constructor_impl/* $VF was: constructor-impl */(
         ResultKt.createFailure(
            if (RECOVER_STACK_TRACES && `$this$resumeWithStackTrace` is CoroutineStackFrame)
               StackTraceRecoveryKt.access$recoverFromStackFrame(exception, `$this$resumeWithStackTrace` as CoroutineStackFrame)
               else
               exception
         )
      )
   )
}
