package kotlinx.coroutines

import kotlin.coroutines.Continuation

private class ResumeOnCompletion(continuation: Continuation<Unit>) : JobNode {
   private final val continuation: Continuation<Unit>

   init {
      this.continuation = continuation
   }

   public override operator fun invoke(cause: Throwable?) {
      this.continuation.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(Unit.INSTANCE))
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
