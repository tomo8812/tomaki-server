package kotlinx.coroutines.flow

public interface StateFlow<T> : SharedFlow<T> {
   public val value: Any
}
