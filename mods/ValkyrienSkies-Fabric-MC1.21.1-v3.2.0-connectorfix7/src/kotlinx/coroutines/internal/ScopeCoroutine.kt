package kotlinx.coroutines.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.CoroutineStackFrame
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.CompletionStateKt
import org.jetbrains.annotations.NotNull

internal open class ScopeCoroutine<T>(context: CoroutineContext, uCont: Continuation<Any>) : AbstractCoroutine(context, true, true), CoroutineStackFrame {
   @JvmField
   @NotNull
   public final val uCont: Continuation<Any>

   init {
      this.uCont = uCont
   }

   public final val callerFrame: CoroutineStackFrame?
      public final get() {
         return this.uCont as? CoroutineStackFrame
      }


   public fun getStackTraceElement(): StackTraceElement? {
      return null
   }

   protected final val isScopedCoroutine: Boolean
      protected final get() {
         return true
      }


   protected override fun afterCompletion(state: Any?) {
      DispatchedContinuationKt.resumeCancellableWith$default(
         IntrinsicsKt.intercepted(this.uCont), CompletionStateKt.recoverResult(state, this.uCont), null, 2, null
      )
   }

   protected override fun afterResume(state: Any?) {
      this.uCont.resumeWith(CompletionStateKt.recoverResult(state, this.uCont))
   }
}
