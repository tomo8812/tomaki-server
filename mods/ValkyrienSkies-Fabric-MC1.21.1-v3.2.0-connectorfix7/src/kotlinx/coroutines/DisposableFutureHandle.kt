package kotlinx.coroutines

import java.util.concurrent.Future

private class DisposableFutureHandle(future: Future<*>) : DisposableHandle {
   private final val future: Future<*>

   init {
      this.future = future
   }

   public override fun dispose() {
      this.future.cancel(false)
   }

   public override fun toString(): String {
      return "DisposableFutureHandle[${this.future}]"
   }
}
