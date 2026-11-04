package kotlinx.coroutines.flow.internal

import kotlinx.coroutines.flow.FlowCollector

internal object NopCollector : FlowCollector<Object> {
   public override suspend fun emit(value: Any?) {
      return Unit.INSTANCE
   }
}
