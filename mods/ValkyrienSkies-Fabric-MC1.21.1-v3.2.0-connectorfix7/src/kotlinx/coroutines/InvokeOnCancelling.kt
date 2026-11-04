package kotlinx.coroutines

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater

private class InvokeOnCancelling(handler: (Throwable?) -> Unit) : JobCancellingNode {
   private final val handler: (Throwable?) -> Unit
   @Volatile
   @Volatile
   private int _invoked;
   @JvmStatic
   private AtomicIntegerFieldUpdater _invoked$FU = AtomicIntegerFieldUpdater.newUpdater(InvokeOnCancelling.class, "_invoked");

   init {
      this.handler = handler
   }

   public override operator fun invoke(cause: Throwable?) {
      if (_invoked$FU.compareAndSet(this, 0, 1)) {
         this.handler(cause)
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
