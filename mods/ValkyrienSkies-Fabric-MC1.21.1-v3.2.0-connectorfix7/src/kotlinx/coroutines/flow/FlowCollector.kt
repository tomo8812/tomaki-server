package kotlinx.coroutines.flow

public fun interface FlowCollector<T> {
   public abstract suspend fun emit(value: Any) {
   }
}
