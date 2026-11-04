package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private object NoOpContinuation : Continuation<Object> {
   public open val context: CoroutineContext = EmptyCoroutineContext.INSTANCE as CoroutineContext

   public open fun resumeWith(result: Result<Any?>) {
   }
}
