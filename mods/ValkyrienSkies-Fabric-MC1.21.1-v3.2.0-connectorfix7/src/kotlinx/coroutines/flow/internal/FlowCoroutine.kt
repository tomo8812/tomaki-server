package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.internal.ScopeCoroutine

private class FlowCoroutine<T>(context: CoroutineContext, uCont: Continuation<Any>) : ScopeCoroutine(context, uCont) {
   public override fun childCancelled(cause: Throwable): Boolean {
      return cause is ChildCancelledException || this.cancelImpl$kotlinx_coroutines_core(cause)
   }
}
