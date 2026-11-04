package kotlinx.coroutines.scheduling

internal abstract class SchedulerTimeSource {
   public abstract fun nanoTime(): Long {
   }
}
