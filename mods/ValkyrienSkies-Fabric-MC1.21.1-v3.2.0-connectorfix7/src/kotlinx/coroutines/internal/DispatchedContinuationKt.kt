@file:SourceDebugExtension(["SMAP\nDispatchedContinuation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DispatchedContinuation.kt\nkotlinx/coroutines/internal/DispatchedContinuationKt\n+ 2 DispatchedContinuation.kt\nkotlinx/coroutines/internal/DispatchedContinuation\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 DispatchedTask.kt\nkotlinx/coroutines/DispatchedTaskKt\n+ 5 CoroutineContext.kt\nkotlinx/coroutines/CoroutineContextKt\n*L\n1#1,317:1\n297#1,5:325\n302#1,12:331\n314#1:387\n301#1:389\n302#1,12:391\n314#1:420\n217#2,7:318\n224#2:346\n243#2,8:347\n225#2:355\n255#2:356\n256#2,2:367\n258#2:371\n227#2:372\n229#2:388\n1#3:330\n1#3:390\n1#3:421\n200#4,3:343\n203#4,14:373\n200#4,17:403\n200#4,17:422\n107#5,10:357\n118#5,2:369\n*S KotlinDebug\n*F\n+ 1 DispatchedContinuation.kt\nkotlinx/coroutines/internal/DispatchedContinuationKt\n*L\n282#1:325,5\n282#1:331,12\n282#1:387\n287#1:389\n287#1:391,12\n287#1:420\n282#1:318,7\n282#1:346\n282#1:347,8\n282#1:355\n282#1:356\n282#1:367,2\n282#1:371\n282#1:372\n282#1:388\n282#1:330\n287#1:390\n282#1:343,3\n282#1:373,14\n287#1:403,17\n313#1:422,17\n282#1:357,10\n282#1:369,2\n*E\n"])

package kotlinx.coroutines.internal

import java.util.concurrent.CancellationException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CompletionStateKt
import kotlinx.coroutines.CoroutineContextKt
import kotlinx.coroutines.DispatchedTask
import kotlinx.coroutines.EventLoop
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ThreadLocalEventLoop
import kotlinx.coroutines.UndispatchedCoroutine
import org.jetbrains.annotations.NotNull

private final val UNDEFINED: Symbol = Symbol("UNDEFINED")

@JvmField
@NotNull
internal final val REUSABLE_CLAIMED: Symbol = Symbol("REUSABLE_CLAIMED")

@InternalCoroutinesApi
public fun <T> Continuation<Any>.resumeCancellableWith(result: Result<Any>, onCancellation: ((Throwable) -> Unit)? = ...) {
   if (`$this$resumeCancellableWith` is DispatchedContinuation) {
      val `this_$iv`: DispatchedContinuation = `$this$resumeCancellableWith` as DispatchedContinuation
      val `state$iv`: Any = CompletionStateKt.toState(result, onCancellation)
      if (`this_$iv`.dispatcher.isDispatchNeeded(`this_$iv`.context)) {
         `this_$iv`._state = `state$iv`
         `this_$iv`.resumeMode = 1
         `this_$iv`.dispatcher.dispatch(`this_$iv`.context, `this_$iv`)
      } else {
         if (ASSERTIONS_ENABLED && false) {
            throw AssertionError()
         }

         val var33: EventLoop = ThreadLocalEventLoop.INSTANCE.eventLoop
         if (var33.isUnconfinedLoopActive) {
            `this_$iv`._state = `state$iv`
            `this_$iv`.resumeMode = 1
            var33.dispatchUnconfined(`this_$iv`)
         } else {
            val `$this$runUnconfinedEventLoop$iv$iv$iv`: DispatchedTask = `this_$iv`
            var33.incrementUseCount(true)

            try {
               val `continuation$iv$iv$iv`: Job = `this_$iv`.context.get(Job.Key) as Job
               val var10000: Boolean
               if (`continuation$iv$iv$iv` != null && !`continuation$iv$iv$iv`.isActive) {
                  val `countOrElement$iv$iv$iv`: CancellationException = `continuation$iv$iv$iv`.getCancellationException()
                  `this_$iv`.cancelCompletedResult$kotlinx_coroutines_core(`state$iv`, `countOrElement$iv$iv$iv`)
                  `this_$iv`.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(`countOrElement$iv$iv$iv`)))
                  var10000 = true
               } else {
                  var10000 = false
               }

               if (!var10000) {
                  val var34: DispatchedContinuation = `this_$iv`
                  val var36: Continuation = `this_$iv`.continuation
                  val var37: Any = `this_$iv`.countOrElement
                  val `context$iv$iv$iv`: CoroutineContext = `this_$iv`.continuation.getContext()
                  val `oldValue$iv$iv$iv`: Any = ThreadContextKt.updateThreadContext(`context$iv$iv$iv`, var37)
                  val `undispatchedCompletion$iv$iv$iv`: UndispatchedCoroutine = if (`oldValue$iv$iv$iv` != ThreadContextKt.NO_THREAD_ELEMENTS)
                     CoroutineContextKt.updateUndispatchedCompletion(var36, `context$iv$iv$iv`, `oldValue$iv$iv$iv`)
                     else
                     null

                  try {
                     var34.continuation.resumeWith(result)
                  } finally {
                     if (`undispatchedCompletion$iv$iv$iv` == null || `undispatchedCompletion$iv$iv$iv`.clearThreadContext()) {
                        ThreadContextKt.restoreThreadContext(`context$iv$iv$iv`, `oldValue$iv$iv$iv`)
                     }
                  }
               }

               while (var33.processUnconfinedEvent()) {
               }
            } catch (var31: java.lang.Throwable) {
               `$this$runUnconfinedEventLoop$iv$iv$iv`.handleFatalException$kotlinx_coroutines_core(var31, null)
            } finally {
               var33.decrementUseCount(true)
            }
         }
      }
   } else {
      `$this$resumeCancellableWith`.resumeWith(result)
   }
}

@JvmSynthetic
fun `resumeCancellableWith$default`(var0: Continuation, var1: Any, var2: Function1, var3: Int, var4: Any) {
   if ((var3 and 2) != 0) {
      var2 = null
   }

   resumeCancellableWith(var0, var1, var2)
}

internal fun DispatchedContinuation<Unit>.yieldUndispatched(): Boolean {
   val `contState$iv`: Unit = Unit.INSTANCE
   val `doYield$iv`: Boolean = true
   if (ASSERTIONS_ENABLED && false) {
      throw AssertionError()
   } else {
      val var15: EventLoop = ThreadLocalEventLoop.INSTANCE.eventLoop
      val var10000: Boolean
      if (var15.isUnconfinedQueueEmpty) {
         var10000 = false
      } else if (var15.isUnconfinedLoopActive) {
         `$this$yieldUndispatched`._state = `contState$iv`
         `$this$yieldUndispatched`.resumeMode = 1
         var15.dispatchUnconfined(`$this$yieldUndispatched`)
         var10000 = true
      } else {
         val `$this$runUnconfinedEventLoop$iv$iv`: DispatchedTask = `$this$yieldUndispatched`
         var15.incrementUseCount(true)

         try {
            `$this$yieldUndispatched`.run()

            while (var15.processUnconfinedEvent()) {
            }
         } catch (var13: java.lang.Throwable) {
            `$this$runUnconfinedEventLoop$iv$iv`.handleFatalException$kotlinx_coroutines_core(var13, null)
         } finally {
            var15.decrementUseCount(true)
         }

         var10000 = false
      }

      return var10000
   }
}

private inline fun DispatchedContinuation<*>.executeUnconfined(contState: Any?, mode: Int, doYield: Boolean = false, block: () -> Unit): Boolean {
   if (ASSERTIONS_ENABLED && mode == -1) {
      throw AssertionError()
   } else {
      val var14: EventLoop = ThreadLocalEventLoop.INSTANCE.eventLoop
      if (doYield && var14.isUnconfinedQueueEmpty) {
         return false
      } else {
         val var10000: Boolean
         if (var14.isUnconfinedLoopActive) {
            `$this$executeUnconfined`._state = contState
            `$this$executeUnconfined`.resumeMode = mode
            var14.dispatchUnconfined(`$this$executeUnconfined`)
            var10000 = true
         } else {
            val `$this$runUnconfinedEventLoop$iv`: DispatchedTask = `$this$executeUnconfined`
            var14.incrementUseCount(true)

            try {
               block()

               while (var14.processUnconfinedEvent()) {
               }
            } catch (var12: java.lang.Throwable) {
               `$this$runUnconfinedEventLoop$iv`.handleFatalException$kotlinx_coroutines_core(var12, null)
            } finally {
               InlineMarker.finallyStart(1)
               var14.decrementUseCount(true)
               InlineMarker.finallyEnd(1)
            }

            var10000 = false
         }

         return var10000
      }
   }
}

@JvmSynthetic
fun `access$getUNDEFINED$p`(): Symbol {
   UNDEFINED
}
