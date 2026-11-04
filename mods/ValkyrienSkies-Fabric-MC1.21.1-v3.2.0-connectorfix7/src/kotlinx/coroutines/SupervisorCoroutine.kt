package kotlinx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.internal.ScopeCoroutine

private class SupervisorCoroutine<T>(context: CoroutineContext, uCont: Continuation<Any>) : ScopeCoroutine(context, uCont) {
   public override fun childCancelled(cause: Throwable): Boolean {
      return false
   }
}
