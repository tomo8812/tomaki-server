package kotlinx.coroutines

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nExecutors.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Executors.kt\nkotlinx/coroutines/ResumeUndispatchedRunnable\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,207:1\n1#2:208\n*E\n"])
private class ResumeUndispatchedRunnable(dispatcher: CoroutineDispatcher, continuation: CancellableContinuation<Unit>) : Runnable {
   private final val dispatcher: CoroutineDispatcher
   private final val continuation: CancellableContinuation<Unit>

   init {
      this.dispatcher = dispatcher
      this.continuation = continuation
   }

   public override fun run() {
      this.continuation.resumeUndispatched(this.dispatcher, Unit.INSTANCE)
   }
}
