package kotlinx.coroutines

import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.coroutines.Continuation

internal class CancelledContinuation(continuation: Continuation<*>, cause: Throwable?, handled: Boolean) : CompletedExceptionally {
   @Volatile
   @Volatile
   private int _resumed;
   @JvmStatic
   private AtomicIntegerFieldUpdater _resumed$FU = AtomicIntegerFieldUpdater.newUpdater(CancelledContinuation.class, "_resumed");

   init {
      var var10001: java.lang.Throwable = cause
      if (cause == null) {
         var10001 = CancellationException("Continuation $continuation was cancelled normally")
      }

      super(var10001, handled)
      this._resumed = 0
   }

   public fun makeResumed(): Boolean {
      return _resumed$FU.compareAndSet(this, 0, 1)
   }
}
