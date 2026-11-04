package kotlinx.coroutines

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import org.jetbrains.annotations.NotNull

internal open class CompletedExceptionally(cause: Throwable, handled: Boolean = false) {
   @JvmField
   @NotNull
   public final val cause: Throwable

   @Volatile
   @Volatile
   private int _handled;
   @JvmStatic
   private AtomicIntegerFieldUpdater _handled$FU = AtomicIntegerFieldUpdater.newUpdater(CompletedExceptionally.class, "_handled");

   init {
      this.cause = cause
      this._handled = handled
   }

   public final val handled: Boolean
      public final get() {
         return _handled$FU.get(this) != 0
      }


   public fun makeHandled(): Boolean {
      return _handled$FU.compareAndSet(this, 0, 1)
   }

   public override fun toString(): String {
      return "${classSimpleName}[${this.cause}]"
   }
}
