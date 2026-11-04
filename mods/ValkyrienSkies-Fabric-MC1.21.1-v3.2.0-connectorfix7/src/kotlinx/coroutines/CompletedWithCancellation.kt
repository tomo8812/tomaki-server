package kotlinx.coroutines

import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

internal data class CompletedWithCancellation(result: Any?, onCancellation: (Throwable) -> Unit) {
   @JvmField
   @Nullable
   public final val result: Any?

   @JvmField
   @NotNull
   public final val onCancellation: (Throwable) -> Unit

   init {
      this.result = result
      this.onCancellation = onCancellation
   }

   public operator fun component1(): Any? {
      return this.result
   }

   public operator fun component2(): (Throwable) -> Unit {
      return this.onCancellation
   }

   public fun copy(result: Any? = this.result, onCancellation: (Throwable) -> Unit = this.onCancellation): CompletedWithCancellation {
      return CompletedWithCancellation(result, onCancellation)
   }

   public override fun toString(): String {
      return "CompletedWithCancellation(result=${this.result}, onCancellation=${this.onCancellation})"
   }

   public override fun hashCode(): Int {
      return (if (this.result == null) 0 else this.result.hashCode()) * 31 + this.onCancellation.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is CompletedWithCancellation
            && this.result == (other as CompletedWithCancellation).result
            && this.onCancellation == (other as CompletedWithCancellation).onCancellation
         }
   }
}
