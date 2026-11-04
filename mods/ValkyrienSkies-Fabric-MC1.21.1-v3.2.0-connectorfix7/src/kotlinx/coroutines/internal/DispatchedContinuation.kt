package kotlinx.coroutines.internal

import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.CompletedWithCancellation
import kotlinx.coroutines.CompletionStateKt
import kotlinx.coroutines.CoroutineContextKt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DebugStringsKt
import kotlinx.coroutines.DispatchedTask
import kotlinx.coroutines.EventLoop
import kotlinx.coroutines.Job
import kotlinx.coroutines.ThreadLocalEventLoop
import kotlinx.coroutines.UndispatchedCoroutine
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@PublishedApi
@SourceDebugExtension(["SMAP\nDispatchedContinuation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DispatchedContinuation.kt\nkotlinx/coroutines/internal/DispatchedContinuation\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 DispatchedContinuation.kt\nkotlinx/coroutines/internal/DispatchedContinuationKt\n+ 4 DispatchedTask.kt\nkotlinx/coroutines/DispatchedTaskKt\n+ 5 CoroutineContext.kt\nkotlinx/coroutines/CoroutineContextKt\n*L\n1#1,317:1\n243#1,8:381\n255#1:389\n256#1,2:400\n258#1:404\n1#2:318\n1#2:324\n1#2:365\n297#3,5:319\n302#3,12:325\n314#3:359\n297#3,5:360\n302#3,12:366\n314#3:419\n200#4,3:337\n203#4,14:345\n200#4,3:378\n203#4,14:405\n95#5,5:340\n107#5,10:390\n118#5,2:402\n107#5,13:420\n*S KotlinDebug\n*F\n+ 1 DispatchedContinuation.kt\nkotlinx/coroutines/internal/DispatchedContinuation\n*L\n224#1:381,8\n225#1:389\n225#1:400,2\n225#1:404\n202#1:324\n223#1:365\n202#1:319,5\n202#1:325,12\n202#1:359\n223#1:360,5\n223#1:366,12\n223#1:419\n202#1:337,3\n202#1:345,14\n223#1:378,3\n223#1:405,14\n203#1:340,5\n225#1:390,10\n225#1:402,2\n255#1:420,13\n*E\n"])
internal class DispatchedContinuation<T>(dispatcher: CoroutineDispatcher, continuation: Continuation<Any>) : DispatchedTask(-1),
   CoroutineStackFrame,
   Continuation<T> {
   @JvmField
   @NotNull
   internal final val dispatcher: CoroutineDispatcher

   @JvmField
   @NotNull
   public final val continuation: Continuation<Any>

   @JvmField
   @Nullable
   internal final var _state: Any?
      private set

   @JvmField
   @NotNull
   internal final val countOrElement: Any

   @Volatile
   @Volatile
   private Object _reusableCancellableContinuation;
   @JvmStatic
   private AtomicReferenceFieldUpdater _reusableCancellableContinuation$FU = AtomicReferenceFieldUpdater.newUpdater(
      DispatchedContinuation.class, Object.class, "_reusableCancellableContinuation"
   );

   init {
      this.dispatcher = dispatcher
      this.continuation = continuation
      this._state = DispatchedContinuationKt.access$getUNDEFINED$p()
      this.countOrElement = ThreadContextKt.threadContextElements(this.context)
   }

   public open val context: CoroutineContext

   public open val callerFrame: CoroutineStackFrame?
      public open get() {
         return this.continuation as? CoroutineStackFrame
      }


   public open fun getStackTraceElement(): StackTraceElement? {
      return null
   }

   private final val reusableCancellableContinuation: CancellableContinuationImpl<*>?
      private final get() {
         val var1: Any = _reusableCancellableContinuation$FU.get(this)
         return var1 as? CancellableContinuationImpl
      }


   internal fun isReusable(): Boolean {
      return _reusableCancellableContinuation$FU.get(this) != null
   }

   internal fun awaitReusability() {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _reusableCancellableContinuation$FU

      do {
      } while (`atomicfu$handler$iv`.get(this) === DispatchedContinuationKt.REUSABLE_CLAIMED)
   }

   internal fun release() {
      this.awaitReusability$kotlinx_coroutines_core()
      val var10000: CancellableContinuationImpl = this.reusableCancellableContinuation
      if (var10000 != null) {
         var10000.detachChild$kotlinx_coroutines_core()
      }
   }

   internal fun claimReusableCancellableContinuation(): CancellableContinuationImpl<Any>? {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _reusableCancellableContinuation$FU

      while (true) {
         val state: Any = `atomicfu$handler$iv`.get(this)
         if (state == null) {
            _reusableCancellableContinuation$FU.set(this, DispatchedContinuationKt.REUSABLE_CLAIMED)
            return null
         }

         if (state is CancellableContinuationImpl) {
            if (_reusableCancellableContinuation$FU.compareAndSet(this, state, DispatchedContinuationKt.REUSABLE_CLAIMED)) {
               return state as CancellableContinuationImpl<T>
            }
         } else if (state != DispatchedContinuationKt.REUSABLE_CLAIMED && state !is java.lang.Throwable) {
            throw IllegalStateException(("Inconsistent state $state").toString())
         }
      }
   }

   internal fun tryReleaseClaimedContinuation(continuation: CancellableContinuation<*>): Throwable? {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _reusableCancellableContinuation$FU

      do {
         val state: Any = `atomicfu$handler$iv`.get(this)
         if (state != DispatchedContinuationKt.REUSABLE_CLAIMED) {
            if (state is java.lang.Throwable) {
               if (!_reusableCancellableContinuation$FU.compareAndSet(this, state, null)) {
                  throw IllegalArgumentException("Failed requirement.".toString())
               }

               return state as java.lang.Throwable
            }

            throw IllegalStateException(("Inconsistent state $state").toString())
         }
      } while (!_reusableCancellableContinuation$FU.compareAndSet(this, DispatchedContinuationKt.REUSABLE_CLAIMED, continuation))

      return null
   }

   internal fun postponeCancellation(cause: Throwable): Boolean {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _reusableCancellableContinuation$FU

      while (true) {
         val state: Any = `atomicfu$handler$iv`.get(this)
         if (state == DispatchedContinuationKt.REUSABLE_CLAIMED) {
            if (_reusableCancellableContinuation$FU.compareAndSet(this, DispatchedContinuationKt.REUSABLE_CLAIMED, cause)) {
               return true
            }
         } else {
            if (state is java.lang.Throwable) {
               return true
            }

            if (_reusableCancellableContinuation$FU.compareAndSet(this, state, null)) {
               return false
            }
         }
      }
   }

   internal override fun takeState(): Any? {
      val state: Any = this._state
      if (ASSERTIONS_ENABLED && state === DispatchedContinuationKt.access$getUNDEFINED$p()) {
         throw AssertionError()
      } else {
         this._state = DispatchedContinuationKt.access$getUNDEFINED$p()
         return state
      }
   }

   internal open val delegate: Continuation<Any>
      internal open get() {
         return this
      }


   public open fun resumeWith(result: Result<Any>) {
      val context: CoroutineContext = this.continuation.getContext()
      val state: Any = CompletionStateKt.toState$default(result, null, 1, null)
      if (this.dispatcher.isDispatchNeeded(context)) {
         this._state = state
         this.resumeMode = 0
         this.dispatcher.dispatch(context, this)
      } else {
         if (ASSERTIONS_ENABLED && false) {
            throw AssertionError()
         }

         val var28: EventLoop = ThreadLocalEventLoop.INSTANCE.eventLoop
         if (var28.isUnconfinedLoopActive) {
            this._state = state
            this.resumeMode = 0
            var28.dispatchUnconfined(this)
         } else {
            val `$this$runUnconfinedEventLoop$iv$iv`: DispatchedTask = this
            var28.incrementUseCount(true)

            try {
               val `context$iv`: CoroutineContext = this.context

               try {
                  this.continuation.resumeWith(result)
               } finally {
                  ThreadContextKt.restoreThreadContext(`context$iv`, ThreadContextKt.updateThreadContext(`context$iv`, this.countOrElement))
               }

               while (var28.processUnconfinedEvent()) {
               }
            } catch (var26: java.lang.Throwable) {
               `$this$runUnconfinedEventLoop$iv$iv`.handleFatalException$kotlinx_coroutines_core(var26, null)
            } finally {
               var28.decrementUseCount(true)
            }
         }
      }
   }

   internal inline fun resumeCancellableWith(result: Result<Any>, noinline onCancellation: ((Throwable) -> Unit)?) {
      val state: Any = CompletionStateKt.toState(result, onCancellation)
      if (this.dispatcher.isDispatchNeeded(this.context)) {
         this._state = state
         this.resumeMode = 1
         this.dispatcher.dispatch(this.context, this)
      } else {
         if (ASSERTIONS_ENABLED && false) {
            throw AssertionError()
         }

         val var32: EventLoop = ThreadLocalEventLoop.INSTANCE.eventLoop
         if (var32.isUnconfinedLoopActive) {
            this._state = state
            this.resumeMode = 1
            var32.dispatchUnconfined(this)
         } else {
            val `$this$runUnconfinedEventLoop$iv$iv`: DispatchedTask = this
            var32.incrementUseCount(true)

            try {
               val `continuation$iv$iv`: Job = this.context.get(Job.Key) as Job
               val var10000: Boolean
               if (`continuation$iv$iv` != null && !`continuation$iv$iv`.isActive) {
                  val `countOrElement$iv$iv`: CancellationException = `continuation$iv$iv`.getCancellationException()
                  this.cancelCompletedResult$kotlinx_coroutines_core(state, `countOrElement$iv$iv`)
                  this.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(`countOrElement$iv$iv`)))
                  var10000 = true
               } else {
                  var10000 = false
               }

               if (!var10000) {
                  val var33: DispatchedContinuation = this
                  val var35: Continuation = this.continuation
                  val var36: Any = this.countOrElement
                  val `context$iv$iv`: CoroutineContext = this.continuation.getContext()
                  val `oldValue$iv$iv`: Any = ThreadContextKt.updateThreadContext(`context$iv$iv`, var36)
                  val `undispatchedCompletion$iv$iv`: UndispatchedCoroutine = if (`oldValue$iv$iv` != ThreadContextKt.NO_THREAD_ELEMENTS)
                     CoroutineContextKt.updateUndispatchedCompletion(var35, `context$iv$iv`, `oldValue$iv$iv`)
                     else
                     null

                  try {
                     var33.continuation.resumeWith(result)
                  } finally {
                     InlineMarker.finallyStart(1)
                     if (`undispatchedCompletion$iv$iv` == null || `undispatchedCompletion$iv$iv`.clearThreadContext()) {
                        ThreadContextKt.restoreThreadContext(`context$iv$iv`, `oldValue$iv$iv`)
                     }

                     InlineMarker.finallyEnd(1)
                  }
               }

               while (var32.processUnconfinedEvent()) {
               }
            } catch (var30: java.lang.Throwable) {
               `$this$runUnconfinedEventLoop$iv$iv`.handleFatalException$kotlinx_coroutines_core(var30, null)
            } finally {
               InlineMarker.finallyStart(1)
               var32.decrementUseCount(true)
               InlineMarker.finallyEnd(1)
            }
         }
      }
   }

   internal override fun cancelCompletedResult(takenState: Any?, cause: Throwable) {
      if (takenState is CompletedWithCancellation) {
         (takenState as CompletedWithCancellation).onCancellation(cause)
      }
   }

   internal inline fun resumeCancelled(state: Any?): Boolean {
      val job: Job = this.context.get(Job.Key) as Job
      if (job != null && !job.isActive) {
         val cause: CancellationException = job.getCancellationException()
         this.cancelCompletedResult$kotlinx_coroutines_core(state, cause)
         this.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure(cause)))
         return true
      } else {
         return false
      }
   }

   internal inline fun resumeUndispatchedWith(result: Result<Any>) {
      val `continuation$iv`: Continuation = this.continuation
      val `countOrElement$iv`: Any = this.countOrElement
      val `context$iv`: CoroutineContext = this.continuation.getContext()
      val `oldValue$iv`: Any = ThreadContextKt.updateThreadContext(`context$iv`, `countOrElement$iv`)
      val `undispatchedCompletion$iv`: UndispatchedCoroutine = if (`oldValue$iv` != ThreadContextKt.NO_THREAD_ELEMENTS)
         CoroutineContextKt.updateUndispatchedCompletion(`continuation$iv`, `context$iv`, `oldValue$iv`)
         else
         null

      try {
         this.continuation.resumeWith(result)
      } finally {
         InlineMarker.finallyStart(1)
         if (`undispatchedCompletion$iv` == null || `undispatchedCompletion$iv`.clearThreadContext()) {
            ThreadContextKt.restoreThreadContext(`context$iv`, `oldValue$iv`)
         }

         InlineMarker.finallyEnd(1)
      }
   }

   internal fun dispatchYield(context: CoroutineContext, value: Any) {
      this._state = value
      this.resumeMode = 1
      this.dispatcher.dispatchYield(context, this)
   }

   public override fun toString(): String {
      return "DispatchedContinuation[${this.dispatcher}, ${DebugStringsKt.toDebugString(this.continuation)}]"
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }
}
