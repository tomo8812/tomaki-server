package kotlinx.coroutines

import java.util.concurrent.Future

private class CancelFutureOnCancel(future: Future<*>) : CancelHandler {
   private final val future: Future<*>

   init {
      this.future = future
   }

   public override operator fun invoke(cause: Throwable?) {
      if (cause != null) {
         this.future.cancel(false)
      }
   }

   public override fun toString(): String {
      return "CancelFutureOnCancel[${this.future}]"
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun invoke(p1: Any) {
      this.invoke(p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
