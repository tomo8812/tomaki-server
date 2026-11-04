package org.valkyrienskies.mod.common.command.arguments

public data class RelativeValue(angleDegrees: Double, isRelative: Boolean) {
   private final val angleDegrees: Double
   private final val isRelative: Boolean

   init {
      this.angleDegrees = angleDegrees
      this.isRelative = isRelative
   }

   public fun getRelativeValue(sourceAngleDegrees: Double): Double {
      return if (this.isRelative) sourceAngleDegrees + this.angleDegrees else this.angleDegrees
   }

   private operator fun component1(): Double {
      return this.angleDegrees
   }

   private operator fun component2(): Boolean {
      return this.isRelative
   }

   public fun copy(angleDegrees: Double = this.angleDegrees, isRelative: Boolean = this.isRelative): RelativeValue {
      return RelativeValue(angleDegrees, isRelative)
   }

   public override fun toString(): String {
      return "RelativeValue(angleDegrees=${this.angleDegrees}, isRelative=${this.isRelative})"
   }

   public override fun hashCode(): Int {
      return java.lang.Double.hashCode(this.angleDegrees) * 31 + java.lang.Boolean.hashCode(this.isRelative)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is RelativeValue
            && java.lang.Double.compare(this.angleDegrees, (other as RelativeValue).angleDegrees) == 0
            && this.isRelative == (other as RelativeValue).isRelative
         }
   }
}
