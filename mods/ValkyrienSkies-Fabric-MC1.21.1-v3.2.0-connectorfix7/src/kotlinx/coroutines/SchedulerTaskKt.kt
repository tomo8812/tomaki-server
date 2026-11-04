package kotlinx.coroutines

import kotlinx.coroutines.scheduling.TaskContext

internal final val taskContext: TaskContext
   internal final get() {
      return `$this$taskContext`.taskContext
   }


internal inline fun TaskContext.afterTask() {
   `$this$afterTask`.afterTask()
}
