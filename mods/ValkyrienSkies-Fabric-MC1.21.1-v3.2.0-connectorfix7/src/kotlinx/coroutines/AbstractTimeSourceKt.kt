package kotlinx.coroutines

import java.util.concurrent.locks.LockSupport
import kotlin.internal.InlineOnly

internal final var timeSource: AbstractTimeSource?

@InlineOnly
internal inline fun currentTimeMillis(): Long {
   val var10000: AbstractTimeSource = timeSource
   return if (var10000 != null) var10000.currentTimeMillis() else System.currentTimeMillis()
}

@InlineOnly
internal inline fun nanoTime(): Long {
   val var10000: AbstractTimeSource = timeSource
   return if (var10000 != null) var10000.nanoTime() else System.nanoTime()
}

@InlineOnly
internal inline fun wrapTask(block: Runnable): Runnable {
   val var10000: AbstractTimeSource = timeSource
   if (var10000 != null) {
      val var1: Runnable = var10000.wrapTask(block)
      if (var1 != null) {
         return var1
      }
   }

   return block
}

@InlineOnly
internal inline fun trackTask() {
   val var10000: AbstractTimeSource = timeSource
   if (var10000 != null) {
      var10000.trackTask()
   }
}

@InlineOnly
internal inline fun unTrackTask() {
   val var10000: AbstractTimeSource = timeSource
   if (var10000 != null) {
      var10000.unTrackTask()
   }
}

@InlineOnly
internal inline fun registerTimeLoopThread() {
   val var10000: AbstractTimeSource = timeSource
   if (var10000 != null) {
      var10000.registerTimeLoopThread()
   }
}

@InlineOnly
internal inline fun unregisterTimeLoopThread() {
   val var10000: AbstractTimeSource = timeSource
   if (var10000 != null) {
      var10000.unregisterTimeLoopThread()
   }
}

@InlineOnly
internal inline fun parkNanos(blocker: Any, nanos: Long) {
   val var10000: AbstractTimeSource = timeSource
   val var3: Unit
   if (var10000 != null) {
      var10000.parkNanos(blocker, nanos)
      var3 = Unit.INSTANCE
   } else {
      var3 = null
   }

   if (var3 == null) {
      LockSupport.parkNanos(blocker, nanos)
   }
}

@InlineOnly
internal inline fun unpark(thread: Thread) {
   val var10000: AbstractTimeSource = timeSource
   val var1: Unit
   if (var10000 != null) {
      var10000.unpark(thread)
      var1 = Unit.INSTANCE
   } else {
      var1 = null
   }

   if (var1 == null) {
      LockSupport.unpark(thread)
   }
}
