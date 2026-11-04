package kotlinx.coroutines.internal

import org.jetbrains.annotations.NotNull

private class Removed(ref: LockFreeLinkedListNode) {
   @JvmField
   @NotNull
   public final val ref: LockFreeLinkedListNode

   init {
      this.ref = ref
   }

   public override fun toString(): String {
      return "Removed[${this.ref}]"
   }
}
