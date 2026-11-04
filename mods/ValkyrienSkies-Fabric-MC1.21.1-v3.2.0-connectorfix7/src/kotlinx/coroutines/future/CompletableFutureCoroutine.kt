package kotlinx.coroutines.future

import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.function.BiFunction
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.AbstractCoroutine
import kotlinx.coroutines.Job

private class CompletableFutureCoroutine<T>(context: CoroutineContext, future: CompletableFuture<Any>) : AbstractCoroutine(context, true, true),
   BiFunction<T, java.lang.Throwable, Unit> {
   private final val future: CompletableFuture<Any>

   init {
      this.future = future
   }

   public open fun apply(value: Any?, exception: Throwable?) {
      Job.DefaultImpls.cancel$default(this, (CancellationException)null, 1, null)
   }

   protected override fun onCompleted(value: Any) {
      this.future.complete((T)value)
   }

   protected override fun onCancelled(cause: Throwable, handled: Boolean) {
      this.future.completeExceptionally(cause)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun apply(p0: Any, p1: Any) {
      this.apply((T)p0, p1 as java.lang.Throwable)
      Unit.INSTANCE
   }
}
