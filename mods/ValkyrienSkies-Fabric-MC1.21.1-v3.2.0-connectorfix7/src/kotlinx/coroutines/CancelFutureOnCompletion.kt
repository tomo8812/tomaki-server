package kotlinx.coroutines

import java.util.concurrent.Future

private class CancelFutureOnCompletion(future: Future<*>) : JobNode {
   private final val future: Future<*>

   init {
      this.future = future
   }

   public override operator fun invoke(cause: Throwable?) {
      if (cause != null) {
         this.future.cancel(false)
      }
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
