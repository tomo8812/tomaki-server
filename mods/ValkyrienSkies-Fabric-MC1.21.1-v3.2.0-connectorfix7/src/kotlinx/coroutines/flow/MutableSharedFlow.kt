package kotlinx.coroutines.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi

public interface MutableSharedFlow<T> : SharedFlow<T>, FlowCollector<T> {
   public abstract override suspend fun emit(value: Any) {
   }

   public abstract fun tryEmit(value: Any): Boolean {
   }

   public val subscriptionCount: StateFlow<Int>

   @ExperimentalCoroutinesApi
   public abstract fun resetReplayCache() {
   }
}
