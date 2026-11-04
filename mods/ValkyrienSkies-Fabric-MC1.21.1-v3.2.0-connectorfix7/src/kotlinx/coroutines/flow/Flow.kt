package kotlinx.coroutines.flow

public interface Flow<T> {
   public abstract suspend fun collect(collector: FlowCollector<Any>) {
   }
}
