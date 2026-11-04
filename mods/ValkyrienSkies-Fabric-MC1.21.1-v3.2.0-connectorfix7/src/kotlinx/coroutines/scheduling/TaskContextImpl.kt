package kotlinx.coroutines.scheduling

private class TaskContextImpl(taskMode: Int) : TaskContext {
   public open val taskMode: Int

   init {
      this.taskMode = taskMode
   }

   public override fun afterTask() {
   }
}
