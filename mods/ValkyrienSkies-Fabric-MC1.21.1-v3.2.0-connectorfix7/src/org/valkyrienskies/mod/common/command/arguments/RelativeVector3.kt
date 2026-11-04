package org.valkyrienskies.mod.common.command.arguments

import org.joml.Quaterniond
import org.joml.Vector3d

public data class RelativeVector3(x: RelativeValue, y: RelativeValue, z: RelativeValue) {
   public final val x: RelativeValue
   public final val y: RelativeValue
   public final val z: RelativeValue

   init {
      this.x = x
      this.y = y
      this.z = z
   }

   public fun toEulerRotation(sourcePitchDegrees: Double, sourceYawDegrees: Double, sourceRollDegrees: Double): Quaterniond {
      val var10000: Quaterniond = Quaterniond()
         .rotateZYX(
            Math.toRadians(this.z.getRelativeValue(sourceRollDegrees)),
            Math.toRadians(this.y.getRelativeValue(sourceYawDegrees)),
            Math.toRadians(this.x.getRelativeValue(sourcePitchDegrees))
         )
         return var10000
   }

   public fun toEulerRotationFromMCEntity(mcEntityPitch: Double, mcEntityYaw: Double): Quaterniond {
      return this.toEulerRotation(mcEntityPitch, -mcEntityYaw, 0.0)
   }

   public fun toVector3d(sourceX: Double, sourceY: Double, sourceZ: Double): Vector3d {
      return Vector3d(this.x.getRelativeValue(sourceX), this.y.getRelativeValue(sourceY), this.z.getRelativeValue(sourceZ))
   }

   public operator fun component1(): RelativeValue {
      return this.x
   }

   public operator fun component2(): RelativeValue {
      return this.y
   }

   public operator fun component3(): RelativeValue {
      return this.z
   }

   public fun copy(x: RelativeValue = this.x, y: RelativeValue = this.y, z: RelativeValue = this.z): RelativeVector3 {
      return RelativeVector3(x, y, z)
   }

   public override fun toString(): String {
      return "RelativeVector3(x=${this.x}, y=${this.y}, z=${this.z})"
   }

   public override fun hashCode(): Int {
      return (this.x.hashCode() * 31 + this.y.hashCode()) * 31 + this.z.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is RelativeVector3
            && this.x == (other as RelativeVector3).x
            && this.y == (other as RelativeVector3).y
            && this.z == (other as RelativeVector3).z
         }
   }
}
