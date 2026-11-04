package kotlinx.coroutines.scheduling

import org.jetbrains.annotations.NotNull

@PublishedApi
internal abstract class Task : Runnable {
   @JvmField
   public final var submissionTime: Long
      private set

   @JvmField
   @NotNull
   internal final var taskContext: TaskContext
      private set

   open fun Task(submissionTime: Long, taskContext: TaskContext) {
      this.submissionTime = submissionTime
      this.taskContext = taskContext
   }

   open fun Task() {
      this(0L, TasksKt.NonBlockingContext)
   }

   internal final val mode: Int
      internal final inline get() {
         return this.taskContext.taskMode
      }

}
