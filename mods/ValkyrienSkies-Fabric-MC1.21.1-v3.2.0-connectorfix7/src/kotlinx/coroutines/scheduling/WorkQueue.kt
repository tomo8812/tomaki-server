package kotlinx.coroutines.scheduling

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef

@SourceDebugExtension(["SMAP\nWorkQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WorkQueue.kt\nkotlinx/coroutines/scheduling/WorkQueue\n+ 2 Tasks.kt\nkotlinx/coroutines/scheduling/TasksKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 WorkQueue.kt\nkotlinx/coroutines/scheduling/WorkQueueKt\n*L\n1#1,255:1\n93#2:256\n93#2:257\n93#2:258\n93#2:261\n93#2:262\n1#3:259\n25#4:260\n*S KotlinDebug\n*F\n+ 1 WorkQueue.kt\nkotlinx/coroutines/scheduling/WorkQueue\n*L\n95#1:256\n162#1:257\n185#1:258\n205#1:261\n249#1:262\n205#1:260\n*E\n"])
internal class WorkQueue {
   private final val buffer: AtomicReferenceArray<Task?> = AtomicReferenceArray(128)
   @Volatile
   @Volatile
   private Object lastScheduledTask;
   @Volatile
   @Volatile
   private int producerIndex;
   @Volatile
   @Volatile
   private int consumerIndex;
   @Volatile
   @Volatile
   private int blockingTasksInBuffer;
   @JvmStatic
   private AtomicReferenceFieldUpdater lastScheduledTask$FU = AtomicReferenceFieldUpdater.newUpdater(WorkQueue.class, Object.class, "lastScheduledTask");
   @JvmStatic
   private AtomicIntegerFieldUpdater producerIndex$FU = AtomicIntegerFieldUpdater.newUpdater(WorkQueue.class, "producerIndex");
   @JvmStatic
   private AtomicIntegerFieldUpdater consumerIndex$FU = AtomicIntegerFieldUpdater.newUpdater(WorkQueue.class, "consumerIndex");
   @JvmStatic
   private AtomicIntegerFieldUpdater blockingTasksInBuffer$FU = AtomicIntegerFieldUpdater.newUpdater(WorkQueue.class, "blockingTasksInBuffer");

   private final val bufferSize: Int
      private final get() {
         return producerIndex$FU.get(this) - consumerIndex$FU.get(this)
      }


   internal final val size: Int
      internal final get() {
         return if (lastScheduledTask$FU.get(this) != null) this.bufferSize + 1 else this.bufferSize
      }


   public fun poll(): Task? {
      var var10000: Task = lastScheduledTask$FU.getAndSet(this, null) as Task
      if (var10000 == null) {
         var10000 = this.pollBuffer()
      }

      return var10000
   }

   public fun add(task: Task, fair: Boolean = false): Task? {
      label16@
      if (fair) {
         return this.addLast(task)
      } else {
         val var10000: Task = lastScheduledTask$FU.getAndSet(this, task)
         return if (var10000 == null) null else this.addLast(var10000)
      }
   }

   private fun addLast(task: Task): Task? {
      if (this.bufferSize == 127) {
         return task
      } else {
         if (task.taskContext.taskMode == 1) {
            blockingTasksInBuffer$FU.incrementAndGet(this)
         }

         val var4: Int = producerIndex$FU.get(this) and 127

         while (this.buffer.get(var4) != null) {
            Thread.yield()
         }

         this.buffer.lazySet(var4, task)
         producerIndex$FU.incrementAndGet(this)
         return null
      }
   }

   public fun trySteal(stealingMode: Int, stolenTaskRef: ObjectRef<Task?>): Long {
      val task: Task = if (stealingMode == 3) this.pollBuffer() else this.stealWithExclusiveMode(stealingMode)
      if (task != null) {
         stolenTaskRef.element = task
         return -1L
      } else {
         return this.tryStealLastScheduled(stealingMode, stolenTaskRef)
      }
   }

   private fun stealWithExclusiveMode(stealingMode: Int): Task? {
      var start: Int = consumerIndex$FU.get(this)
      val end: Int = producerIndex$FU.get(this)
      val onlyBlocking: Boolean = stealingMode == 1

      while (start != end) {
         if (onlyBlocking && blockingTasksInBuffer$FU.get(this) == 0) {
            return null
         }

         val var10000: Task = this.tryExtractFromTheMiddle(start++, onlyBlocking)
         if (var10000 != null) {
            return var10000
         }
      }

      return null
   }

   public fun pollBlocking(): Task? {
      return this.pollWithExclusiveMode(true)
   }

   public fun pollCpu(): Task? {
      return this.pollWithExclusiveMode(false)
   }

   private fun pollWithExclusiveMode(onlyBlocking: Boolean): Task? {
      while (true) {
         val var10000: Task = lastScheduledTask$FU.get(this) as Task
         if (var10000 != null && var10000.taskContext.taskMode == 1 == onlyBlocking) {
            if (!lastScheduledTask$FU.compareAndSet(this, var10000, null)) {
               continue
            }

            return var10000
         }

         val var5: Int = consumerIndex$FU.get(this)
         var var6: Int = producerIndex$FU.get(this)

         while (var5 != var6) {
            if (onlyBlocking && blockingTasksInBuffer$FU.get(this) == 0) {
               return null
            }

            val var7: Task = this.tryExtractFromTheMiddle(--var6, onlyBlocking)
            if (var7 != null) {
               return var7
            }
         }

         return null
      }
   }

   private fun tryExtractFromTheMiddle(index: Int, onlyBlocking: Boolean): Task? {
      val arrayIndex: Int = index and 127
      val value: Task = this.buffer.get(index and 127)
      if (value != null && value.taskContext.taskMode == 1 == onlyBlocking && this.buffer.compareAndSet(arrayIndex, value, null)) {
         if (onlyBlocking) {
            blockingTasksInBuffer$FU.decrementAndGet(this)
         }

         return value
      } else {
         return null
      }
   }

   public fun offloadAllWorkTo(globalQueue: GlobalQueue) {
      val var10000: Task = lastScheduledTask$FU.getAndSet(this, null) as Task
      if (var10000 != null) {
         globalQueue.addLast(var10000)
      }

      while (this.pollTo(globalQueue)) {
      }
   }

   private fun tryStealLastScheduled(stealingMode: Int, stolenTaskRef: ObjectRef<Task?>): Long {
      val var10000: Task
      do {
         var10000 = lastScheduledTask$FU.get(this) as Task
         if (var10000 == null) {
            return -2L
         }

         if (((if (var10000.taskContext.taskMode == 1) 1 else 2) and stealingMode) == 0) {
            return -2L
         }

         val var9: Long = TasksKt.schedulerTimeSource.nanoTime() - var10000.submissionTime
         if (var9 < TasksKt.WORK_STEALING_TIME_RESOLUTION_NS) {
            return TasksKt.WORK_STEALING_TIME_RESOLUTION_NS - var9
         }
      } while (!lastScheduledTask$FU.compareAndSet(this, var10000, null))

      stolenTaskRef.element = var10000
      return -1L
   }

   private fun pollTo(queue: GlobalQueue): Boolean {
      val var10000: Task = this.pollBuffer()
      if (var10000 == null) {
         return false
      } else {
         queue.addLast(var10000)
         return true
      }
   }

   private fun pollBuffer(): Task? {
      while (true) {
         val tailLocal: Int = consumerIndex$FU.get(this)
         if (tailLocal - producerIndex$FU.get(this) == 0) {
            return null
         }

         val index: Int = tailLocal and 127
         if (consumerIndex$FU.compareAndSet(this, tailLocal, tailLocal + 1)) {
            val var10000: Task = this.buffer.getAndSet(index, null)
            if (var10000 != null) {
               this.decrementIfBlocking(var10000)
               return var10000
            }
         }
      }
   }

   private fun Task?.decrementIfBlocking() {
      if (`$this$decrementIfBlocking` != null
         && `$this$decrementIfBlocking`.taskContext.taskMode == 1
         && ASSERTIONS_ENABLED
         && blockingTasksInBuffer$FU.decrementAndGet(this) < 0) {
         throw AssertionError()
      }
   }
}
