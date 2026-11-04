package kotlinx.coroutines

import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.internal.DispatchedContinuation
import kotlinx.coroutines.internal.Segment
import kotlinx.coroutines.internal.StackTraceRecoveryKt
import kotlinx.coroutines.internal.Symbol

@PublishedApi
@SourceDebugExtension(["SMAP\nCancellableContinuationImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CancellableContinuationImpl.kt\nkotlinx/coroutines/CancellableContinuationImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CancellableContinuationImpl.kt\nkotlinx/coroutines/CancellableContinuationImplKt\n+ 4 CompletionHandler.kt\nkotlinx/coroutines/CompletionHandlerKt\n+ 5 StackTraceRecovery.kt\nkotlinx/coroutines/internal/StackTraceRecoveryKt\n*L\n1#1,662:1\n230#1,2:666\n232#1,8:669\n230#1,10:677\n230#1,10:688\n1#2:663\n24#3:664\n24#3:665\n22#3:687\n21#3:698\n22#3,3:699\n21#3:702\n22#3,3:703\n22#3:711\n21#3,4:712\n22#4:668\n13#4:710\n61#5,2:706\n61#5,2:708\n61#5,2:716\n*S KotlinDebug\n*F\n+ 1 CancellableContinuationImpl.kt\nkotlinx/coroutines/CancellableContinuationImpl\n*L\n246#1:666,2\n246#1:669,8\n249#1:677,10\n254#1:688,10\n72#1:664\n158#1:665\n252#1:687\n277#1:698\n278#1:699,3\n287#1:702\n288#1:703,3\n389#1:711\n392#1:712,4\n246#1:668\n350#1:710\n329#1:706,2\n339#1:708,2\n613#1:716,2\n*E\n"])
internal open class CancellableContinuationImpl<T>(delegate: Continuation<Any>, resumeMode: Int) : DispatchedTask(resumeMode),
   CancellableContinuation<T>,
   CoroutineStackFrame,
   Waiter {
   internal final val delegate: Continuation<Any>
   public open val context: CoroutineContext
   @Volatile
   @Volatile
   private int _decisionAndIndex;
   @Volatile
   @Volatile
   private Object _state;
   @Volatile
   @Volatile
   private Object _parentHandle;
   @JvmStatic
   private AtomicIntegerFieldUpdater _decisionAndIndex$FU = AtomicIntegerFieldUpdater.newUpdater(CancellableContinuationImpl.class, "_decisionAndIndex");
   @JvmStatic
   private AtomicReferenceFieldUpdater _state$FU = AtomicReferenceFieldUpdater.newUpdater(CancellableContinuationImpl.class, Object.class, "_state");
   @JvmStatic
   private AtomicReferenceFieldUpdater _parentHandle$FU = AtomicReferenceFieldUpdater.newUpdater(
      CancellableContinuationImpl.class, Object.class, "_parentHandle"
   );

   init {
      this.delegate = delegate
      if (ASSERTIONS_ENABLED && resumeMode == -1) {
         throw AssertionError()
      } else {
         this.context = this.delegate.getContext()
         this._decisionAndIndex = (0 shl 29) + 536870911
         this._state = Active.INSTANCE
      }
   }

   private final val parentHandle: DisposableHandle?
      private final get() {
         return _parentHandle$FU.get(this) as DisposableHandle
      }


   internal final val state: Any?
      internal final get() {
         return _state$FU.get(this)
      }


   public open val isActive: Boolean
      public open get() {
         return this.state is NotCompleted
      }


   public open val isCompleted: Boolean
      public open get() {
         return this.state !is NotCompleted
      }


   public open val isCancelled: Boolean
      public open get() {
         return this.state is CancelledContinuation
      }


   private final val stateDebugRepresentation: String
      private final get() {
         val var1: Any = this.state
         return if (var1 is NotCompleted) "Active" else (if (var1 is CancelledContinuation) "Cancelled" else "Completed")
      }


   public override fun initCancellability() {
      val var10000: DisposableHandle = this.installParentHandle()
      if (var10000 != null) {
         if (this.isCompleted) {
            var10000.dispose()
            _parentHandle$FU.set(this, NonDisposableHandle.INSTANCE)
         }
      }
   }

   private fun isReusable(): Boolean {
      if (isReusableMode) {
         val var10000: Continuation = this.delegate
         if ((var10000 as DispatchedContinuation).isReusable$kotlinx_coroutines_core()) {
            return true
         }
      }

      return false
   }

   @JvmName(name = "resetStateReusable")
   internal fun resetStateReusable(): Boolean {
      if (ASSERTIONS_ENABLED && this.resumeMode != 2) {
         throw AssertionError()
      } else if (ASSERTIONS_ENABLED && this.parentHandle === NonDisposableHandle.INSTANCE) {
         throw AssertionError()
      } else {
         val var6: Any = _state$FU.get(this)
         if (ASSERTIONS_ENABLED && var6 is NotCompleted) {
            throw AssertionError()
         } else if (var6 is CompletedContinuation && (var6 as CompletedContinuation).idempotentResume != null) {
            this.detachChild$kotlinx_coroutines_core()
            return false
         } else {
            _decisionAndIndex$FU.set(this, (0 shl 29) + 536870911)
            _state$FU.set(this, Active.INSTANCE)
            return true
         }
      }
   }

   public open val callerFrame: CoroutineStackFrame?
      public open get() {
         return this.delegate as? CoroutineStackFrame
      }


   public open fun getStackTraceElement(): StackTraceElement? {
      return null
   }

   internal override fun takeState(): Any? {
      return this.state
   }

   internal override fun cancelCompletedResult(takenState: Any?, cause: Throwable) {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _state$FU

      while (true) {
         val state: Any = `atomicfu$handler$iv`.get(this)
         if (state is NotCompleted) {
            throw IllegalStateException("Not completed".toString())
         }

         if (state is CompletedExceptionally) {
            return
         }

         if (state is CompletedContinuation) {
            if ((state as CompletedContinuation).cancelled) {
               throw IllegalStateException("Must be called at most once".toString())
            }

            if (_state$FU.compareAndSet(
               this, state, CompletedContinuation.copy$default(state as CompletedContinuation, null, null, null, null, cause, 15, null)
            )) {
               (state as CompletedContinuation).invokeHandlers(this, cause)
               return
            }
         } else if (_state$FU.compareAndSet(this, state, CompletedContinuation(state, null, null, null, cause, 14, null))) {
            return
         }
      }
   }

   private fun cancelLater(cause: Throwable): Boolean {
      if (!this.isReusable()) {
         return false
      } else {
         val var10000: Continuation = this.delegate
         return (var10000 as DispatchedContinuation).postponeCancellation$kotlinx_coroutines_core(cause)
      }
   }

   public override fun cancel(cause: Throwable?): Boolean {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _state$FU

      val state: Any
      do {
         state = `atomicfu$handler$iv`.get(this)
         if (state !is NotCompleted) {
            return false
         }
      } while (!_state$FU.compareAndSet(this, state, CancelledContinuation(this, cause, state is CancelHandler || state is Segment)))

      val var8: NotCompleted = state as NotCompleted
      if (state as NotCompleted is CancelHandler) {
         this.callCancelHandler(state as CancelHandler, cause)
      } else if (var8 is Segment) {
         this.callSegmentOnCancellation(state as Segment<*>, cause)
      }

      this.detachChildIfNonResuable()
      this.dispatchResume(this.resumeMode)
      return true
   }

   internal fun parentCancelled(cause: Throwable) {
      if (!this.cancelLater(cause)) {
         this.cancel(cause)
         this.detachChildIfNonResuable()
      }
   }

   private inline fun callCancelHandlerSafely(block: () -> Unit) {
      try {
         block()
      } catch (var4: java.lang.Throwable) {
         CoroutineExceptionHandlerKt.handleCoroutineException(
            this.context, CompletionHandlerException("Exception in invokeOnCancellation handler for $this", var4)
         )
      }
   }

   private fun callCancelHandler(handler: (Throwable?) -> Unit, cause: Throwable?) {
      try {
         handler(cause)
      } catch (var9: java.lang.Throwable) {
         CoroutineExceptionHandlerKt.handleCoroutineException(
            this.context, CompletionHandlerException("Exception in invokeOnCancellation handler for $this", var9)
         )
      }
   }

   public fun callCancelHandler(handler: CancelHandler, cause: Throwable?) {
      try {
         handler.invoke(cause)
      } catch (var7: java.lang.Throwable) {
         CoroutineExceptionHandlerKt.handleCoroutineException(
            this.context, CompletionHandlerException("Exception in invokeOnCancellation handler for $this", var7)
         )
      }
   }

   private fun callSegmentOnCancellation(segment: Segment<*>, cause: Throwable?) {
      val index: Int = _decisionAndIndex$FU.get(this) and 536870911
      if (index == 536870911) {
         throw IllegalStateException("The index for Segment.onCancellation(..) is broken".toString())
      } else {
         try {
            segment.onCancellation(index, cause, this.context)
         } catch (var8: java.lang.Throwable) {
            CoroutineExceptionHandlerKt.handleCoroutineException(
               this.context, CompletionHandlerException("Exception in invokeOnCancellation handler for $this", var8)
            )
         }
      }
   }

   public fun callOnCancellation(onCancellation: (Throwable) -> Unit, cause: Throwable) {
      try {
         onCancellation(cause)
      } catch (var4: java.lang.Throwable) {
         CoroutineExceptionHandlerKt.handleCoroutineException(
            this.context, CompletionHandlerException("Exception in resume onCancellation handler for $this", var4)
         )
      }
   }

   public open fun getContinuationCancellationCause(parent: Job): Throwable {
      return parent.getCancellationException()
   }

   private fun trySuspend(): Boolean {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = _decisionAndIndex$FU

      label24@ while (true) {
         val cur: Int = `atomicfu$handler$iv`.get(this)
         when (cur shr 29) {
            0 -> {
               if (_decisionAndIndex$FU.compareAndSet(this, cur, (1 shl 29) + (cur and 536870911))) {
                  break@label24
               }
               continue
            }
            1 -> throw IllegalStateException("Already suspended".toString())
            2 -> return false
            else -> throw IllegalStateException("Already suspended".toString())
         }
      }

      return true
   }

   private fun tryResume(): Boolean {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = _decisionAndIndex$FU

      label24@ while (true) {
         val cur: Int = `atomicfu$handler$iv`.get(this)
         when (cur shr 29) {
            0 -> {
               if (_decisionAndIndex$FU.compareAndSet(this, cur, (2 shl 29) + (cur and 536870911))) {
                  break@label24
               }
               continue
            }
            1 -> return false
            else -> throw IllegalStateException("Already resumed".toString())
         }
      }

      return true
   }

   @PublishedApi
   internal fun getResult(): Any? {
      val isReusable: Boolean = this.isReusable()
      if (this.trySuspend()) {
         if (this.parentHandle == null) {
            this.installParentHandle()
         }

         if (isReusable) {
            this.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
         }

         return IntrinsicsKt.getCOROUTINE_SUSPENDED()
      } else {
         if (isReusable) {
            this.releaseClaimedReusableContinuation$kotlinx_coroutines_core()
         }

         val state: Any = this.state
         if (state is CompletedExceptionally) {
            val var6: java.lang.Throwable = (state as CompletedExceptionally).cause
            throw if (RECOVER_STACK_TRACES && this as Continuation is CoroutineStackFrame)
               StackTraceRecoveryKt.access$recoverFromStackFrame(var6, this)
               else
               var6
            } else {
            if (isCancellableMode) {
               val job: Job = this.context.get(Job.Key) as Job
               if (job != null && !job.isActive) {
                  val cause: CancellationException = job.getCancellationException()
                  this.cancelCompletedResult$kotlinx_coroutines_core(state, cause)
                  throw if (RECOVER_STACK_TRACES && this as Continuation is CoroutineStackFrame)
                     StackTraceRecoveryKt.access$recoverFromStackFrame(cause, this)
                     else
                     cause as java.lang.Throwable
                  }
            }

            return this.getSuccessfulResult$kotlinx_coroutines_core(state)
         }
      }
   }

   private fun installParentHandle(): DisposableHandle? {
      val var10000: Job = this.context.get(Job.Key) as Job
      if (var10000 == null) {
         return null
      } else {
         val handle: DisposableHandle = Job.DefaultImpls.invokeOnCompletion$default(var10000, true, false, ChildContinuation(this), 2, null)
         _parentHandle$FU.compareAndSet(this, null, handle)
         return handle
      }
   }

   internal fun releaseClaimedReusableContinuation() {
      val var2: Continuation = this.delegate
      val var10000: DispatchedContinuation = this.delegate as? DispatchedContinuation
      if ((this.delegate as? DispatchedContinuation) != null) {
         val var3: java.lang.Throwable = var10000.tryReleaseClaimedContinuation$kotlinx_coroutines_core(this)
         if (var3 != null) {
            this.detachChild$kotlinx_coroutines_core()
            this.cancel(var3)
            return
         }
      }
   }

   public open fun resumeWith(result: Result<Any>) {
      resumeImpl$default(this, CompletionStateKt.toState(result, this), this.resumeMode, null, 4, null)
   }

   public override fun resume(value: Any, onCancellation: ((Throwable) -> Unit)?) {
      this.resumeImpl(value, this.resumeMode, onCancellation)
   }

   public override fun invokeOnCancellation(segment: Segment<*>, index: Int) {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = _decisionAndIndex$FU

      val var6: Int
      do {
         var6 = `atomicfu$handler$iv`.get(this)
         if ((var6 and 536870911) != 536870911) {
            throw IllegalStateException("invokeOnCancellation should be called at most once".toString())
         }
      } while (!`atomicfu$handler$iv`.compareAndSet(this, var6, ((var6 shr 29) shl 29) + index))

      this.invokeOnCancellationImpl(segment)
   }

   public override fun invokeOnCancellation(handler: (Throwable?) -> Unit) {
      this.invokeOnCancellationImpl(this.makeCancelHandler(handler))
   }

   private fun invokeOnCancellationImpl(handler: Any) {
      if (ASSERTIONS_ENABLED && handler !is CancelHandler && handler !is Segment) {
         throw AssertionError()
      } else {
         val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _state$FU

         while (true) {
            val state: Any = `atomicfu$handler$iv`.get(this)
            if (state is Active) {
               if (_state$FU.compareAndSet(this, state, handler)) {
                  return
               }
            } else if (state is CancelHandler || state is Segment) {
               this.multipleHandlersError(handler, state)
            } else {
               if (state is CompletedExceptionally) {
                  if (!(state as CompletedExceptionally).makeHandled()) {
                     this.multipleHandlersError(handler, state)
                  }

                  if (state is CancelledContinuation) {
                     val var11: java.lang.Throwable = if ((state as? CompletedExceptionally) != null) (state as? CompletedExceptionally).cause else null
                     if (handler is CancelHandler) {
                        this.callCancelHandler(handler as CancelHandler, var11)
                     } else {
                        this.callSegmentOnCancellation(handler as Segment<*>, var11)
                     }
                  }

                  return
               }

               if (state is CompletedContinuation) {
                  if ((state as CompletedContinuation).cancelHandler != null) {
                     this.multipleHandlersError(handler, state)
                  }

                  if (handler is Segment) {
                     return
                  }

                  if ((state as CompletedContinuation).cancelled) {
                     this.callCancelHandler(handler as CancelHandler, (state as CompletedContinuation).cancelCause)
                     return
                  }

                  if (_state$FU.compareAndSet(
                     this,
                     state,
                     CompletedContinuation.copy$default(state as CompletedContinuation, null, handler as CancelHandler, null, null, null, 29, null)
                  )) {
                     return
                  }
               } else {
                  if (handler is Segment) {
                     return
                  }

                  if (_state$FU.compareAndSet(this, state, CompletedContinuation(state, handler as CancelHandler, null, null, null, 28, null))) {
                     return
                  }
               }
            }
         }
      }
   }

   private fun multipleHandlersError(handler: Any, state: Any?) {
      throw IllegalStateException(("It's prohibited to register multiple handlers, tried to register $handler, already has $state").toString())
   }

   private fun makeCancelHandler(handler: (Throwable?) -> Unit): CancelHandler {
      return if (handler is CancelHandler) handler as CancelHandler else InvokeOnCancel(handler)
   }

   private fun dispatchResume(mode: Int) {
      if (!this.tryResume()) {
         DispatchedTaskKt.dispatch(this, mode)
      }
   }

   private fun resumedState(state: NotCompleted, proposedUpdate: Any?, resumeMode: Int, onCancellation: ((Throwable) -> Unit)?, idempotent: Any?): Any? {
      val var10000: Any
      if (proposedUpdate is CompletedExceptionally) {
         if (ASSERTIONS_ENABLED && idempotent != null) {
            throw AssertionError()
         }

         if (ASSERTIONS_ENABLED && onCancellation != null) {
            throw AssertionError()
         }

         var10000 = proposedUpdate
      } else {
         var10000 = if (!isCancellableMode && idempotent == null)
            proposedUpdate
            else
            (
               if (onCancellation == null && state !is CancelHandler && idempotent == null)
                  proposedUpdate
                  else
                  CompletedContinuation(proposedUpdate, state as? CancelHandler, onCancellation, idempotent, null, 16, null)
            )
         }

      return var10000
   }

   private fun resumeImpl(proposedUpdate: Any?, resumeMode: Int, onCancellation: ((Throwable) -> Unit)? = null) {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _state$FU

      val state: Any
      do {
         state = `atomicfu$handler$iv`.get(this)
         if (state !is NotCompleted) {
            if (state is CancelledContinuation && (state as CancelledContinuation).makeResumed()) {
               if (onCancellation != null) {
                  this.callOnCancellation(onCancellation, (state as CancelledContinuation).cause)
               }

               return
            }

            this.alreadyResumedError(proposedUpdate)
            throw KotlinNothingValueException()
         }
      } while (!_state$FU.compareAndSet(this, state, this.resumedState(state as NotCompleted, proposedUpdate, resumeMode, onCancellation, null)))

      this.detachChildIfNonResuable()
      this.dispatchResume(resumeMode)
   }

   private fun tryResumeImpl(proposedUpdate: Any?, idempotent: Any?, onCancellation: ((Throwable) -> Unit)?): Symbol? {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _state$FU

      val state: Any
      do {
         state = `atomicfu$handler$iv`.get(this)
         if (state !is NotCompleted) {
            if (state !is CompletedContinuation) {
               return null
            } else {
               val var10000: Symbol
               if (idempotent != null && (state as CompletedContinuation).idempotentResume === idempotent) {
                  if (ASSERTIONS_ENABLED && !((state as CompletedContinuation).result == proposedUpdate)) {
                     throw AssertionError()
                  }

                  var10000 = CancellableContinuationImplKt.RESUME_TOKEN
               } else {
                  var10000 = null
               }

               return var10000
            }
         }
      } while (!_state$FU.compareAndSet(this, state, this.resumedState(state as NotCompleted, proposedUpdate, this.resumeMode, onCancellation, idempotent)))

      this.detachChildIfNonResuable()
      return CancellableContinuationImplKt.RESUME_TOKEN
   }

   private fun alreadyResumedError(proposedUpdate: Any?): Nothing {
      throw IllegalStateException(("Already resumed, but proposed with update $proposedUpdate").toString())
   }

   private fun detachChildIfNonResuable() {
      if (!this.isReusable()) {
         this.detachChild$kotlinx_coroutines_core()
      }
   }

   internal fun detachChild() {
      val var10000: DisposableHandle = this.parentHandle
      if (var10000 != null) {
         var10000.dispose()
         _parentHandle$FU.set(this, NonDisposableHandle.INSTANCE)
      }
   }

   public override fun tryResume(value: Any, idempotent: Any?): Any? {
      return this.tryResumeImpl(value, idempotent, null)
   }

   public override fun tryResume(value: Any, idempotent: Any?, onCancellation: ((Throwable) -> Unit)?): Any? {
      return this.tryResumeImpl(value, idempotent, onCancellation)
   }

   public override fun tryResumeWithException(exception: Throwable): Any? {
      return this.tryResumeImpl(CompletedExceptionally(exception, false, 2, null), null, null)
   }

   public override fun completeResume(token: Any) {
      if (ASSERTIONS_ENABLED && token != CancellableContinuationImplKt.RESUME_TOKEN) {
         throw AssertionError()
      } else {
         this.dispatchResume(this.resumeMode)
      }
   }

   public override fun CoroutineDispatcher.resumeUndispatched(value: Any) {
      resumeImpl$default(
         this,
         value,
         if ((if ((this.delegate as? DispatchedContinuation) != null) (this.delegate as? DispatchedContinuation).dispatcher else null) === `$this$resumeUndispatched`)
            4
            else
            this.resumeMode,
         null,
         4,
         null
      )
   }

   public override fun CoroutineDispatcher.resumeUndispatchedWithException(exception: Throwable) {
      val dc: DispatchedContinuation = this.delegate as? DispatchedContinuation
      resumeImpl$default(
         this,
         CompletedExceptionally(exception, false, 2, null),
         if ((if (dc != null) dc.dispatcher else null) === `$this$resumeUndispatchedWithException`) 4 else this.resumeMode,
         null,
         4,
         null
      )
   }

   internal override fun <T> getSuccessfulResult(state: Any?): Any {
      return (T)(if (state is CompletedContinuation) (state as CompletedContinuation).result else state)
   }

   internal override fun getExceptionalResult(state: Any?): Throwable? {
      var var10000: java.lang.Throwable = super.getExceptionalResult$kotlinx_coroutines_core(state)
      if (var10000 != null) {
         val `continuation$iv`: Continuation = this.delegate
         var10000 = if (RECOVER_STACK_TRACES && `continuation$iv` is CoroutineStackFrame)
            StackTraceRecoveryKt.access$recoverFromStackFrame(var10000, `continuation$iv` as CoroutineStackFrame)
            else
            var10000
         } else {
         var10000 = null
      }

      return var10000
   }

   public override fun toString(): String {
      return "${this.nameString()}(${DebugStringsKt.toDebugString(this.delegate)}){${this.stateDebugRepresentation}}@${hexAddress}"
   }

   protected open fun nameString(): String {
      return "CancellableContinuation"
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicIntegerFieldUpdater, `atomicfu$action`: (Int?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   fun `update$atomicfu`(`atomicfu$handler`: AtomicIntegerFieldUpdater, `atomicfu$action`: (Int?) -> Int, `atomicfu$dispatchReceiver`: Any) {
      val var5: Int
      do {
         var5 = `atomicfu$handler`.get(`atomicfu$dispatchReceiver`)
      } while (!`atomicfu$handler`.compareAndSet(`atomicfu$dispatchReceiver`, var5, (`atomicfu$action`(var5) as java.lang.Number).intValue()))
   }
}
