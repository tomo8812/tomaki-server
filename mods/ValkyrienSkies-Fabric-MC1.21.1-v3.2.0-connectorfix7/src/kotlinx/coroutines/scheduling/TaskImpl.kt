package kotlinx.coroutines.scheduling

import org.jetbrains.annotations.NotNull

internal class TaskImpl(block: Runnable, submissionTime: Long, taskContext: TaskContext) : Task(submissionTime, taskContext) {
   @JvmField
   @NotNull
   public final val block: Runnable

   init {
      this.block = block
   }

   public override fun run() {
      try {
         this.block.run()
      } finally {
         this.taskContext.afterTask()
      }
   }

   public override fun toString(): String {
      return "Task[${classSimpleName}@${hexAddress}, ${this.submissionTime}, ${this.taskContext}]"
   }
}
