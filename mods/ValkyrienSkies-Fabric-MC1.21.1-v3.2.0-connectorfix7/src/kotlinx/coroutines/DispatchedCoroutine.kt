package kotlinx.coroutines

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlinx.coroutines.internal.DispatchedContinuationKt
import kotlinx.coroutines.internal.ScopeCoroutine

@PublishedApi
internal class DispatchedCoroutine<T> internal constructor(context: CoroutineContext, uCont: Continuation<Any>) : ScopeCoroutine(context, uCont) {
   @Volatile
   @JvmField
   @Volatile
   private int _decision;
   @JvmStatic
   private AtomicIntegerFieldUpdater _decision$FU = AtomicIntegerFieldUpdater.newUpdater(DispatchedCoroutine.class, "_decision");

   private fun trySuspend(): Boolean {
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = _decision$FU

      label24@ while (true) {
         when (`atomicfu$handler$iv`.get(this)) {
            0 -> {
               if (_decision$FU.compareAndSet(this, 0, 1)) {
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
      val `atomicfu$handler$iv`: AtomicIntegerFieldUpdater = _decision$FU

      label24@ while (true) {
         when (`atomicfu$handler$iv`.get(this)) {
            0 -> {
               if (_decision$FU.compareAndSet(this, 0, 2)) {
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

   protected override fun afterCompletion(state: Any?) {
      this.afterResume(state)
   }

   protected override fun afterResume(state: Any?) {
      if (!this.tryResume()) {
         DispatchedContinuationKt.resumeCancellableWith$default(
            IntrinsicsKt.intercepted(this.uCont), CompletionStateKt.recoverResult(state, this.uCont), null, 2, null
         )
      }
   }

   internal fun getResult(): Any? {
      if (this.trySuspend()) {
         return IntrinsicsKt.getCOROUTINE_SUSPENDED()
      } else {
         val state: Any = JobSupportKt.unboxState(this.getState$kotlinx_coroutines_core())
         if (state is CompletedExceptionally) {
            throw (state as CompletedExceptionally).cause
         } else {
            return state
         }
      }
   }

   @JvmStatic
   fun `get_decision$FU`(): AtomicIntegerFieldUpdater {
      _decision$FU
   }

   fun `loop$atomicfu`(`atomicfu$handler`: AtomicIntegerFieldUpdater, `atomicfu$action`: (Int?) -> Unit, `atomicfu$dispatchReceiver`: Any) {
      while (true) {
         `atomicfu$action`(`atomicfu$handler`.get(`atomicfu$dispatchReceiver`))
      }
   }
}
