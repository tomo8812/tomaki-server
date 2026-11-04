package kotlinx.coroutines.flow

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellableContinuationImpl
import kotlinx.coroutines.flow.internal.AbstractSharedFlowKt
import kotlinx.coroutines.flow.internal.AbstractSharedFlowSlot

@SourceDebugExtension(["SMAP\nStateFlow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowSlot\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n1#1,428:1\n1#2:429\n314#3,11:430\n*S KotlinDebug\n*F\n+ 1 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowSlot\n*L\n298#1:430,11\n*E\n"])
private class StateFlowSlot : AbstractSharedFlowSlot<StateFlowImpl<?>> {
   @Volatile
   @Volatile
   private Object _state;
   @JvmStatic
   private AtomicReferenceFieldUpdater _state$FU = AtomicReferenceFieldUpdater.newUpdater(StateFlowSlot.class, Object.class, "_state");

   public open fun allocateLocked(flow: StateFlowImpl<*>): Boolean {
      if (_state$FU.get(this) != null) {
         return false
      } else {
         _state$FU.set(this, StateFlowKt.access$getNONE$p())
         return true
      }
   }

   public open fun freeLocked(flow: StateFlowImpl<*>): Array<Continuation<Unit>?> {
      _state$FU.set(this, null)
      return AbstractSharedFlowKt.EMPTY_RESUMES
   }

   public fun makePending() {
      val `atomicfu$handler$iv`: AtomicReferenceFieldUpdater = _state$FU

      while (true) {
         val state: Any = `atomicfu$handler$iv`.get(this)
         if (state == null) {
            return
         }

         if (state === StateFlowKt.access$getPENDING$p()) {
            return
         }

         if (state === StateFlowKt.access$getNONE$p()) {
            if (_state$FU.compareAndSet(this, state, StateFlowKt.access$getPENDING$p())) {
               return
            }
         } else if (_state$FU.compareAndSet(this, state, StateFlowKt.access$getNONE$p())) {
            (state as CancellableContinuationImpl).resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
            return
         }
      }
   }

   public fun takePending(): Boolean {
      val var10000: Any = _state$FU.getAndSet(this, StateFlowKt.access$getNONE$p())
      if (ASSERTIONS_ENABLED && var10000 is CancellableContinuationImpl) {
         throw AssertionError()
      } else {
         return var10000 === StateFlowKt.access$getPENDING$p()
      }
   }

   public suspend fun awaitPending() {
      val `cancellable$iv`: CancellableContinuationImpl = CancellableContinuationImpl(IntrinsicsKt.intercepted(`$completion`), 1)
      `cancellable$iv`.initCancellability()
      val cont: CancellableContinuation = `cancellable$iv`
      if (ASSERTIONS_ENABLED && access$get_state$FU$p().get(this) is CancellableContinuationImpl) {
         throw AssertionError()
      } else {
         if (!access$get_state$FU$p().compareAndSet(this, StateFlowKt.access$getNONE$p(), cont)) {
            if (ASSERTIONS_ENABLED && access$get_state$FU$p().get(this) != StateFlowKt.access$getPENDING$p()) {
               throw AssertionError()
            }

            cont.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
         }

         val var10000: Any = `cancellable$iv`.getResult()
         if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(`$completion`)
         }

         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
      }
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicReferenceFieldUpdater, `atomicfu$action`: (Any?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun allocateLocked(flow: Any): Boolean {
      this.allocateLocked(flow as StateFlowImpl<*>)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun freeLocked(flow: Any): Array<Continuation> {
      this.freeLocked(flow as StateFlowImpl<*>)
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$get_state$FU$p`(): AtomicReferenceFieldUpdater {
      _state$FU
   }
}
