package kotlinx.coroutines

import org.jetbrains.annotations.NotNull

private class IncompleteStateBox(state: Incomplete) {
   @JvmField
   @NotNull
   public final val state: Incomplete

   init {
      this.state = state
   }
}
