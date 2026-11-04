package kotlinx.coroutines

import java.util.concurrent.locks.LockSupport
import kotlinx.coroutines.EventLoopImplBase.DelayedTask

internal abstract class EventLoopImplPlatform : EventLoop {
   protected abstract val thread: Thread

   protected fun unpark() {
      val thread: Thread = this.thread
      if (Thread.currentThread() != thread) {
         val var10000: AbstractTimeSource = timeSource
         val var2: Unit
         if (var10000 != null) {
            var10000.unpark(thread)
            var2 = Unit.INSTANCE
         } else {
            var2 = null
         }

         if (var2 == null) {
            LockSupport.unpark(thread)
         }
      }
   }

   protected open fun reschedule(now: Long, delayedTask: DelayedTask) {
      DefaultExecutor.INSTANCE.schedule(now, delayedTask)
   }
}
