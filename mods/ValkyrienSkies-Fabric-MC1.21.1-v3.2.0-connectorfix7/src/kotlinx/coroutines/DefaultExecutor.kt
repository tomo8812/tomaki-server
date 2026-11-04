package kotlinx.coroutines

import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.EventLoopImplBase.DelayedTask

@SourceDebugExtension(["SMAP\nDefaultExecutor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DefaultExecutor.kt\nkotlinx/coroutines/DefaultExecutor\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,187:1\n1#2:188\n*E\n"])
internal object DefaultExecutor : EventLoopImplBase, Runnable {
   public const val THREAD_NAME: String = "kotlinx.coroutines.DefaultExecutor"
   private const val DEFAULT_KEEP_ALIVE_MS: Long = 1000L
   private final val KEEP_ALIVE_NANOS: Long
   private final var _thread: Thread?
   private const val FRESH: Int = 0
   private const val ACTIVE: Int = 1
   private const val SHUTDOWN_REQ: Int = 2
   private const val SHUTDOWN_ACK: Int = 3
   private const val SHUTDOWN: Int = 4
   private final var debugStatus: Int

   protected open val thread: Thread
      protected open get() {
         var var10000: Thread = _thread
         if (_thread == null) {
            var10000 = this.createThreadSync()
         }

         return var10000
      }


   private final val isShutDown: Boolean
      private final get() {
         return debugStatus == 4
      }


   private final val isShutdownRequested: Boolean
      private final get() {
         return debugStatus == 2 || debugStatus == 3
      }


   public override fun enqueue(task: Runnable) {
      if (this.isShutDown) {
         this.shutdownError()
      }

      super.enqueue(task)
   }

   protected override fun reschedule(now: Long, delayedTask: DelayedTask) {
      this.shutdownError()
   }

   private fun shutdownError() {
      throw RejectedExecutionException(
         "DefaultExecutor was shut down. This error indicates that Dispatchers.shutdown() was invoked prior to completion of exiting coroutines, leaving coroutines in incomplete state. Please refer to Dispatchers.shutdown documentation for more details"
      )
   }

   public override fun shutdown() {
      debugStatus = 4
      super.shutdown()
   }

   public override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
      return this.scheduleInvokeOnTimeout(timeMillis, block)
   }

   public override fun run() {
      ThreadLocalEventLoop.INSTANCE.setEventLoop$kotlinx_coroutines_core(this)
      var var10000: AbstractTimeSource = timeSource
      if (var10000 != null) {
         var10000.registerTimeLoopThread()
      }

      try {
         var shutdownNanos: Long = java.lang.Long.MAX_VALUE
         if (this.notifyStartup()) {
            while (true) {
               Thread.interrupted()
               var parkNanos: Long = this.processNextEvent()
               if (parkNanos == java.lang.Long.MAX_VALUE) {
                  var10000 = timeSource
                  val now: Long = if (var10000 != null) var10000.nanoTime() else System.nanoTime()
                  if (shutdownNanos == java.lang.Long.MAX_VALUE) {
                     shutdownNanos = now + KEEP_ALIVE_NANOS
                  }

                  val tillShutdown: Long = shutdownNanos - now
                  if (shutdownNanos - now <= 0L) {
                     return
                  }

                  parkNanos = RangesKt.coerceAtMost(parkNanos, tillShutdown)
               } else {
                  shutdownNanos = java.lang.Long.MAX_VALUE
               }

               if (parkNanos > 0L) {
                  if (this.isShutdownRequested) {
                     return
                  }

                  var10000 = timeSource
                  val var14: Unit
                  if (var10000 != null) {
                     var10000.parkNanos(this, parkNanos)
                     var14 = Unit.INSTANCE
                  } else {
                     var14 = null
                  }

                  if (var14 == null) {
                     LockSupport.parkNanos(this, parkNanos)
                  }
               }
            }
         }
      } finally {
         _thread = null
         this.acknowledgeShutdownIfNeeded()
         var10000 = timeSource
         if (var10000 != null) {
            var10000.unregisterTimeLoopThread()
         }

         if (!this.isEmpty()) {
            this.thread
         }
      }
   }

   @Synchronized
   private fun createThreadSync(): Thread {
      var var10000: Thread = _thread
      if (_thread == null) {
         val var1: Thread = Thread(this, "kotlinx.coroutines.DefaultExecutor")
         _thread = var1
         var1.setDaemon(true)
         var1.start()
         var10000 = var1
      }

      return var10000
   }

   @Synchronized
   internal fun ensureStarted() {
      if (ASSERTIONS_ENABLED && _thread != null) {
         throw AssertionError()
      } else if (ASSERTIONS_ENABLED && debugStatus != 0 && debugStatus != 3) {
         throw AssertionError()
      } else {
         debugStatus = 0
         this.createThreadSync()

         while (debugStatus == 0) {
            this.wait()
         }
      }
   }

   @Synchronized
   private fun notifyStartup(): Boolean {
      if (this.isShutdownRequested) {
         return false
      } else {
         debugStatus = 1
         this.notifyAll()
         return true
      }
   }

   @Synchronized
   public fun shutdownForTests(timeout: Long) {
      val deadline: Long = System.currentTimeMillis() + timeout
      if (!this.isShutdownRequested) {
         debugStatus = 2
      }

      while (debugStatus != 3 && _thread != null) {
         if (_thread != null) {
            val it: Thread = _thread
            val var10000: AbstractTimeSource = timeSource
            val var9: Unit
            if (var10000 != null) {
               var10000.unpark(it)
               var9 = Unit.INSTANCE
            } else {
               var9 = null
            }

            if (var9 == null) {
               LockSupport.unpark(it)
            }
         }

         if (deadline - System.currentTimeMillis() <= 0L) {
            break
         }

         this.wait(timeout)
      }

      debugStatus = 0
   }

   @Synchronized
   private fun acknowledgeShutdownIfNeeded() {
      if (this.isShutdownRequested) {
         debugStatus = 3
         this.resetAll()
         this.notifyAll()
      }
   }

   internal final val isThreadPresent: Boolean
      internal final get() {
         return _thread != null
      }


   @JvmStatic
   fun {
      EventLoop.incrementUseCount$default(INSTANCE, false, 1, null)
      val var2: TimeUnit = TimeUnit.MILLISECONDS

      var var0: java.lang.Long
      var var10000: TimeUnit
      try {
         var10000 = var2
         var0 = java.lang.Long.getLong("kotlinx.coroutines.DefaultExecutor.keepAlive", 1000L)
      } catch (var3: SecurityException) {
         var10000 = TimeUnit.MILLISECONDS
         var0 = 1000L
      }

      KEEP_ALIVE_NANOS = var10000.toNanos(var0)
   }
}
