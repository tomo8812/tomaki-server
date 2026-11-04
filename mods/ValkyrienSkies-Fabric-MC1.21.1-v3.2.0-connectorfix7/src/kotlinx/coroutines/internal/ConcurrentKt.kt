package kotlinx.coroutines.internal

import java.lang.reflect.Method
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.jvm.internal.InlineMarker

private final val REMOVE_FUTURE_ON_CANCEL: Method?

internal inline fun <T> ReentrantLock.withLock(action: () -> Any): Any {
   val var3: Lock = `$this$withLock`
   `$this$withLock`.lock()

   try {
      return (T)action()
   } finally {
      InlineMarker.finallyStart(1)
      var3.unlock()
      InlineMarker.finallyEnd(1)
   }
}

internal inline fun <E> identitySet(expectedSize: Int): MutableSet<Any> {
   return Collections.newSetFromMap(IdentityHashMap(expectedSize))
}

internal fun removeFutureOnCancel(executor: Executor): Boolean {
   try {
      val executorx: ScheduledThreadPoolExecutor = executor as? ScheduledThreadPoolExecutor
      if ((executor as? ScheduledThreadPoolExecutor) == null) {
         return false
      } else if (REMOVE_FUTURE_ON_CANCEL == null) {
         return false
      } else {
         REMOVE_FUTURE_ON_CANCEL.invoke(executorx, true)
         return true
      }
   } catch (var3: java.lang.Throwable) {
      return false
   }
}

/** @deprecated */
@JvmSynthetic
fun `ReentrantLock$annotations`() {
}

fun {
   var var0: Method
   try {
      var0 = ScheduledThreadPoolExecutor.class.getMethod("setRemoveOnCancelPolicy", boolean.class)
   } catch (var2: java.lang.Throwable) {
      var0 = null
   }

   REMOVE_FUTURE_ON_CANCEL = var0
}
