package kotlinx.coroutines.internal

internal abstract class CtorCache {
   public abstract fun get(key: Class<out Throwable>): (Throwable) -> Throwable? {
   }
}
