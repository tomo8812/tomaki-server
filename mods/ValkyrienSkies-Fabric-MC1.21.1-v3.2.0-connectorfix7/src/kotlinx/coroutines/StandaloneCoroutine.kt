package kotlinx.coroutines

import kotlin.coroutines.CoroutineContext

private open class StandaloneCoroutine(parentContext: CoroutineContext, active: Boolean) : AbstractCoroutine(parentContext, true, active) {
   protected override fun handleJobException(exception: Throwable): Boolean {
      CoroutineExceptionHandlerKt.handleCoroutineException(this.getContext(), exception)
      return true
   }
}
