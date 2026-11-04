package org.valkyrienskies.mod.common.util

public data class SplittingDisablerAttachment(splitt: Boolean) {
   private final var splitt: Boolean

   init {
      this.splitt = splitt
   }

   public fun enableSplitting() {
      this.splitt = true
   }

   public fun disableSplitting() {
      this.splitt = false
   }

   public fun canSplit(): Boolean {
      return this.splitt
   }

   private operator fun component1(): Boolean {
      return this.splitt
   }

   public fun copy(splitt: Boolean = this.splitt): SplittingDisablerAttachment {
      return SplittingDisablerAttachment(splitt)
   }

   public override fun toString(): String {
      return "SplittingDisablerAttachment(splitt=${this.splitt})"
   }

   public override fun hashCode(): Int {
      return java.lang.Boolean.hashCode(this.splitt)
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is SplittingDisablerAttachment && this.splitt == (other as SplittingDisablerAttachment).splitt
      }
   }
}
