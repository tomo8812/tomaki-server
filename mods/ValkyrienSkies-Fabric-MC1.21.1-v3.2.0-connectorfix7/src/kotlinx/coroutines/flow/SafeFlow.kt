package kotlinx.coroutines.flow

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt

private class SafeFlow<T>(block: (FlowCollector<Any>, Continuation<Unit>) -> Any?) : AbstractFlow<T> {
   private final val block: (FlowCollector<Any>, Continuation<Unit>) -> Any?

   init {
      this.block = block
   }

   public override suspend fun collectSafely(collector: FlowCollector<Any>) {
      val var10000: Any = this.block(collector, `$completion`)
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE
   }
}
