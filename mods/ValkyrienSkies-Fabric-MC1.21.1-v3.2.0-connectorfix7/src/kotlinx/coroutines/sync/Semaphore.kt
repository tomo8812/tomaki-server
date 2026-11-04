package kotlinx.coroutines.sync

public interface Semaphore {
   public val availablePermits: Int

   public abstract suspend fun acquire() {
   }

   public abstract fun tryAcquire(): Boolean {
   }

   public abstract fun release() {
   }
}
