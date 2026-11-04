package org.valkyrienskies.core.api.ships

import org.joml.Vector3dc

public data class Wing(wingNormal: Vector3dc, wingLiftPower: Double, wingDragPower: Double, wingBreakingForce: Double?, wingCamberAttackAngleBias: Double) {
   public final val wingNormal: Vector3dc
   public final val wingLiftPower: Double
   public final val wingDragPower: Double
   public final val wingBreakingForce: Double?
   public final val wingCamberAttackAngleBias: Double

   init {
      this.wingNormal = wingNormal
      this.wingLiftPower = wingLiftPower
      this.wingDragPower = wingDragPower
      this.wingBreakingForce = wingBreakingForce
      this.wingCamberAttackAngleBias = wingCamberAttackAngleBias
   }

   public operator fun component1(): Vector3dc {
      return this.wingNormal
   }

   public operator fun component2(): Double {
      return this.wingLiftPower
   }

   public operator fun component3(): Double {
      return this.wingDragPower
   }

   public operator fun component4(): Double? {
      return this.wingBreakingForce
   }

   public operator fun component5(): Double {
      return this.wingCamberAttackAngleBias
   }

   public fun copy(
      wingNormal: Vector3dc = this.wingNormal,
      wingLiftPower: Double = this.wingLiftPower,
      wingDragPower: Double = this.wingDragPower,
      wingBreakingForce: Double? = this.wingBreakingForce,
      wingCamberAttackAngleBias: Double = this.wingCamberAttackAngleBias
   ): Wing {
      return Wing(wingNormal, wingLiftPower, wingDragPower, wingBreakingForce, wingCamberAttackAngleBias)
   }

   public override fun toString(): String {
      return "Wing(wingNormal=${this.wingNormal}, wingLiftPower=${this.wingLiftPower}, wingDragPower=${this.wingDragPower}, wingBreakingForce=${this.wingBreakingForce}, wingCamberAttackAngleBias=${this.wingCamberAttackAngleBias})"
   }

   public override fun hashCode(): Int {
      return (
               ((this.wingNormal.hashCode() * 31 + java.lang.Double.hashCode(this.wingLiftPower)) * 31 + java.lang.Double.hashCode(this.wingDragPower)) * 31
                  + (if (this.wingBreakingForce == null) 0 else this.wingBreakingForce.hashCode())
            )
            * 31
         + java.lang.Double.hashCode(this.wingCamberAttackAngleBias)
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is Wing
            && this.wingNormal == (other as Wing).wingNormal
            && java.lang.Double.compare(this.wingLiftPower, (other as Wing).wingLiftPower) == 0
            && java.lang.Double.compare(this.wingDragPower, (other as Wing).wingDragPower) == 0
            && this.wingBreakingForce == (other as Wing).wingBreakingForce
            && java.lang.Double.compare(this.wingCamberAttackAngleBias, (other as Wing).wingCamberAttackAngleBias) == 0
         }
   }
}
