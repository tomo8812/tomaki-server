package kotlinx.coroutines

@ExperimentalCoroutinesApi
public interface CopyableThrowable<T extends java.lang.Throwable & CopyableThrowable<T>> {
   public abstract fun createCopy(): Any? {
   }
}
