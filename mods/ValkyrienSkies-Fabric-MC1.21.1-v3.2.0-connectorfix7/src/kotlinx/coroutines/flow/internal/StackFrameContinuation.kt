package kotlinx.coroutines.flow.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.jvm.internal.CoroutineStackFrame

private class StackFrameContinuation<T>(uCont: Continuation<Any>, context: CoroutineContext) : Continuation<T>, CoroutineStackFrame {
   private final val uCont: Continuation<Any>
   public open val context: CoroutineContext

   init {
      this.uCont = uCont
      this.context = context
   }

   public open val callerFrame: CoroutineStackFrame?
      public open get() {
         return this.uCont as? CoroutineStackFrame
      }


   public open fun resumeWith(result: Result<Any>) {
      this.uCont.resumeWith(result)
   }

   public open fun getStackTraceElement(): StackTraceElement? {
      return null
   }
}
