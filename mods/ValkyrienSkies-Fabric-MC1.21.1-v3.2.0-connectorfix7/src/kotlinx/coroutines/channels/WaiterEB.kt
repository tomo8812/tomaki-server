package kotlinx.coroutines.channels

import kotlinx.coroutines.Waiter
import org.jetbrains.annotations.NotNull

private class WaiterEB(waiter: Waiter) {
   @JvmField
   @NotNull
   public final val waiter: Waiter

   init {
      this.waiter = waiter
   }

   public override fun toString(): String {
      return "WaiterEB(${this.waiter})"
   }
}
