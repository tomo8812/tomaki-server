package kotlinx.coroutines

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJobSupport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JobSupport.kt\nkotlinx/coroutines/ResumeAwaitOnCompletion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1454:1\n1#2:1455\n*E\n"])
private class ResumeAwaitOnCompletion<T>(continuation: CancellableContinuationImpl<Any>) : JobNode {
   private final val continuation: CancellableContinuationImpl<Any>

   init {
      this.continuation = continuation
   }

   public override operator fun invoke(cause: Throwable?) {
      val state: Any = this.getJob().state
      if (ASSERTIONS_ENABLED && state is Incomplete) {
         throw AssertionError()
      } else {
         if (state is CompletedExceptionally) {
            this.continuation
               .resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(ResultKt.createFailure((state as CompletedExceptionally).cause)))
            } else {
            this.continuation.resumeWith(Result.constructor_impl/* $VF was: constructor-impl */(JobSupportKt.unboxState(state)))
         }
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
