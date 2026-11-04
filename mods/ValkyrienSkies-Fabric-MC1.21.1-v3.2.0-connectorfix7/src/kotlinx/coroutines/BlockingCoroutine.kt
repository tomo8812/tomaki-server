package kotlinx.coroutines

import java.util.concurrent.locks.LockSupport
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nBuilders.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Builders.kt\nkotlinx/coroutines/BlockingCoroutine\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,102:1\n1#2:103\n*E\n"])
private class BlockingCoroutine<T>(parentContext: CoroutineContext, blockedThread: Thread, eventLoop: EventLoop?) : AbstractCoroutine(parentContext, true, true) {
   private final val blockedThread: Thread
   private final val eventLoop: EventLoop?

   init {
      this.blockedThread = blockedThread
      this.eventLoop = eventLoop
   }

   protected open val isScopedCoroutine: Boolean
      protected open get() {
         return true
      }


   protected override fun afterCompletion(state: Any?) {
      if (!(Thread.currentThread() == this.blockedThread)) {
         val var2: Thread = this.blockedThread
         val var10000: AbstractTimeSource = timeSource
         val var3: Unit
         if (var10000 != null) {
            var10000.unpark(var2)
            var3 = Unit.INSTANCE
         } else {
            var3 = null
         }

         if (var3 == null) {
            LockSupport.unpark(var2)
         }
      }
   }

   public fun joinBlocking(): Any {
      var var10000: AbstractTimeSource = timeSource
      if (var10000 != null) {
         var10000.registerTimeLoopThread()
      }

      try {
         if (this.eventLoop != null) {
            EventLoop.incrementUseCount$default(this.eventLoop, false, 1, null)
         }

         try {
            while (true) {
               if (Thread.interrupted()) {
                  val var13: InterruptedException = InterruptedException()
                  this.cancelCoroutine(var13)
                  throw var13 as java.lang.Throwable
               }

               val state: Long = if (this.eventLoop != null) this.eventLoop.processNextEvent() else java.lang.Long.MAX_VALUE
               if (this.isCompleted()) {
                  break
               }

               var10000 = timeSource
               val var17: Unit
               if (var10000 != null) {
                  var10000.parkNanos(this, state)
                  var17 = Unit.INSTANCE
               } else {
                  var17 = null
               }

               if (var17 == null) {
                  LockSupport.parkNanos(this, state)
               }
            }
         } finally {
            if (this.eventLoop != null) {
               EventLoop.decrementUseCount$default(this.eventLoop, false, 1, null)
            }
         }
      } finally {
         var10000 = timeSource
         if (var10000 != null) {
            var10000.unregisterTimeLoopThread()
         }
      }

      val var12: Any = JobSupportKt.unboxState(this.getState$kotlinx_coroutines_core())
      val it: CompletedExceptionally = var12 as? CompletedExceptionally
      if ((var12 as? CompletedExceptionally) != null) {
         throw it.cause
      } else {
         return (T)var12
      }
   }
}
