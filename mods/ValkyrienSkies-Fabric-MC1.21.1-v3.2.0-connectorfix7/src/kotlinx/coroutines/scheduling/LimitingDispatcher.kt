package kotlinx.coroutines.scheduling

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.ExecutorCoroutineDispatcher

private class LimitingDispatcher(dispatcher: ExperimentalCoroutineDispatcher, parallelism: Int, name: String?, taskMode: Int)
   : ExecutorCoroutineDispatcher,
   TaskContext,
   Executor {
   private final val dispatcher: ExperimentalCoroutineDispatcher
   private final val parallelism: Int
   private final val name: String?
   public open val taskMode: Int
   private final val queue: ConcurrentLinkedQueue<Runnable>
   @Volatile
   @Volatile
   private int inFlightTasks;
   @JvmStatic
   private AtomicIntegerFieldUpdater inFlightTasks$FU = AtomicIntegerFieldUpdater.newUpdater(LimitingDispatcher.class, "inFlightTasks");

   init {
      this.dispatcher = dispatcher
      this.parallelism = parallelism
      this.name = name
      this.taskMode = taskMode
      this.queue = ConcurrentLinkedQueue<>()
   }

   public open val executor: Executor
      public open get() {
         return this
      }


   public override fun execute(command: Runnable) {
      this.dispatch(command, false)
   }

   public override fun close() {
      throw IllegalStateException("Close cannot be invoked on LimitingBlockingDispatcher".toString())
   }

   public override fun dispatch(context: CoroutineContext, block: Runnable) {
      this.dispatch(block, false)
   }

   private fun dispatch(block: Runnable, tailDispatch: Boolean) {
      var taskToSchedule: Runnable = block

      while (true) {
         if (inFlightTasks$FU.incrementAndGet(this) <= this.parallelism) {
            this.dispatcher.dispatchWithContext$kotlinx_coroutines_core(taskToSchedule, this, tailDispatch)
            return
         }

         this.queue.add(taskToSchedule)
         if (inFlightTasks$FU.decrementAndGet(this) >= this.parallelism) {
            return
         }

         val var10000: Runnable = this.queue.poll()
         if (var10000 == null) {
            return
         }

         taskToSchedule = var10000
      }
   }

   public override fun dispatchYield(context: CoroutineContext, block: Runnable) {
      this.dispatch(block, true)
   }

   public override fun toString(): String {
      var var10000: java.lang.String = this.name
      if (this.name == null) {
         var10000 = "${super.toString()}[dispatcher = ${this.dispatcher}]"
      }

      return var10000
   }

   public override fun afterTask() {
      val next: Runnable = this.queue.poll()
      if (next != null) {
         this.dispatcher.dispatchWithContext$kotlinx_coroutines_core(next, this, true)
      } else {
         inFlightTasks$FU.decrementAndGet(this)
         val var10000: Runnable = this.queue.poll()
         if (var10000 != null) {
            this.dispatch(var10000, true)
         }
      }
   }
}
