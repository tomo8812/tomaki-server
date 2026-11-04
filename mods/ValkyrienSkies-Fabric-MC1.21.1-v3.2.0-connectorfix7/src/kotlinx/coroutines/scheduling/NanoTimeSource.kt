package kotlinx.coroutines.scheduling

internal object NanoTimeSource : SchedulerTimeSource {
   public override fun nanoTime(): Long {
      return System.nanoTime()
   }
}
