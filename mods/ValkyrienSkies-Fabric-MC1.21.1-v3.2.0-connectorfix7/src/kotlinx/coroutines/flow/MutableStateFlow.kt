package kotlinx.coroutines.flow

public interface MutableStateFlow<T> : StateFlow<T>, MutableSharedFlow<T> {
   public var value: Any

   public abstract fun compareAndSet(expect: Any, update: Any): Boolean {
   }
}
