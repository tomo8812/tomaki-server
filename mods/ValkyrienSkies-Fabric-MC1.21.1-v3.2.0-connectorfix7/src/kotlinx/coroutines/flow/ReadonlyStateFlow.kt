package kotlinx.coroutines.flow

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.internal.FusibleFlow

private class ReadonlyStateFlow<T>(flow: StateFlow<Any>, job: Job?) : StateFlow<T>, CancellableFlow<T>, FusibleFlow<T> {
   private final val job: Job?
   @JvmSynthetic
   private StateFlow<T> $$delegate_0;

   init {
      this.job = job
      this.$$delegate_0 = flow
   }

   public open val replayCache: List<Any>

   public open val value: Any

   public override suspend fun collect(collector: FlowCollector<Any>): Nothing {
      return this.$$delegate_0.collect(collector, `$completion`)
   }

   public override fun fuse(context: CoroutineContext, capacity: Int, onBufferOverflow: BufferOverflow): Flow<Any> {
      return StateFlowKt.fuseStateFlow(this, context, capacity, onBufferOverflow)
   }
}
