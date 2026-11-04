package org.valkyrienskies.core.api.ships

public data class PositionedWing(posX: Int, posY: Int, posZ: Int, wing: Wing?) {
   public final val posX: Int
   public final val posY: Int
   public final val posZ: Int
   public final val wing: Wing?

   init {
      this.posX = posX
      this.posY = posY
      this.posZ = posZ
      this.wing = wing
   }

   public operator fun component1(): Int {
      return this.posX
   }

   public operator fun component2(): Int {
      return this.posY
   }

   public operator fun component3(): Int {
      return this.posZ
   }

   public operator fun component4(): Wing? {
      return this.wing
   }

   public fun copy(posX: Int = this.posX, posY: Int = this.posY, posZ: Int = this.posZ, wing: Wing? = this.wing): PositionedWing {
      return PositionedWing(posX, posY, posZ, wing)
   }

   public override fun toString(): String {
      return "PositionedWing(posX=${this.posX}, posY=${this.posY}, posZ=${this.posZ}, wing=${this.wing})"
   }

   public override fun hashCode(): Int {
      return ((Integer.hashCode(this.posX) * 31 + Integer.hashCode(this.posY)) * 31 + Integer.hashCode(this.posZ)) * 31
         + (if (this.wing == null) 0 else this.wing.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label40@
      if (this === other) {
         return true
      } else {
         return other is PositionedWing
            && this.posX == (other as PositionedWing).posX
            && this.posY == (other as PositionedWing).posY
            && this.posZ == (other as PositionedWing).posZ
            && this.wing == (other as PositionedWing).wing
         }
   }
}
