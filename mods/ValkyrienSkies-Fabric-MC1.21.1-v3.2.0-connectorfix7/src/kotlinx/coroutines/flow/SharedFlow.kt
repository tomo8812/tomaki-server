package kotlinx.coroutines.flow

public interface SharedFlow<T> : Flow<T> {
   public val replayCache: List<Any>

   public abstract override suspend fun collect(collector: FlowCollector<Any>): Nothing {
   }
}
