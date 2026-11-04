package org.valkyrienskies.core.api.util

public data class DoublePair(first: Double, second: Double) {
   public final val first: Double
   public final val second: Double

   init {
      this.first = first
      this.second = second
   }

   public operator fun component1(): Double {
      return this.first
   }

   public operator fun component2(): Double {
      return this.second
   }

   public fun copy(first: Double = this.first, second: Double = this.second): DoublePair {
      return DoublePair(first, second)
   }

   public override fun toString(): String {
      return "DoublePair(first=${this.first}, second=${this.second})"
   }

   public override fun hashCode(): Int {
      return java.lang.Double.hashCode(this.first) * 31 + java.lang.Double.hashCode(this.second)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is DoublePair
            && java.lang.Double.compare(this.first, (other as DoublePair).first) == 0
            && java.lang.Double.compare(this.second, (other as DoublePair).second) == 0
         }
   }
}
