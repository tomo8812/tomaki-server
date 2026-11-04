package kotlinx.coroutines.scheduling

internal interface TaskContext {
   public val taskMode: Int

   public abstract fun afterTask() {
   }
}
