package kotlinx.coroutines.debug.internal

import org.jetbrains.annotations.Nullable

private class Marked(ref: Any?) {
   @JvmField
   @Nullable
   public final val ref: Any?

   init {
      this.ref = ref
   }
}
