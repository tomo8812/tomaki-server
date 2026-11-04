package org.valkyrienskies.core.api.physics

import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.util.PhysTickOnly

@PhysTickOnly
public data class RayCastResult(hitBody: PhysShip, distance: Double, hitNormal: Vector3dc, velocity: Vector3dc, angularVelocity: Vector3dc) {
   public final val hitBody: PhysShip
   public final val distance: Double
   public final val hitNormal: Vector3dc
   public final val velocity: Vector3dc
   public final val angularVelocity: Vector3dc

   init {
      this.hitBody = hitBody
      this.distance = distance
      this.hitNormal = hitNormal
      this.velocity = velocity
      this.angularVelocity = angularVelocity
   }

   public operator fun component1(): PhysShip {
      return this.hitBody
   }

   public operator fun component2(): Double {
      return this.distance
   }

   public operator fun component3(): Vector3dc {
      return this.hitNormal
   }

   public operator fun component4(): Vector3dc {
      return this.velocity
   }

   public operator fun component5(): Vector3dc {
      return this.angularVelocity
   }

   public fun copy(
      hitBody: PhysShip = this.hitBody,
      distance: Double = this.distance,
      hitNormal: Vector3dc = this.hitNormal,
      velocity: Vector3dc = this.velocity,
      angularVelocity: Vector3dc = this.angularVelocity
   ): RayCastResult {
      return RayCastResult(hitBody, distance, hitNormal, velocity, angularVelocity)
   }

   public override fun toString(): String {
      return "RayCastResult(hitBody=${this.hitBody}, distance=${this.distance}, hitNormal=${this.hitNormal}, velocity=${this.velocity}, angularVelocity=${this.angularVelocity})"
   }

   public override fun hashCode(): Int {
      return (((this.hitBody.hashCode() * 31 + java.lang.Double.hashCode(this.distance)) * 31 + this.hitNormal.hashCode()) * 31 + this.velocity.hashCode())
            * 31
         + this.angularVelocity.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is RayCastResult
            && this.hitBody == (other as RayCastResult).hitBody
            && java.lang.Double.compare(this.distance, (other as RayCastResult).distance) == 0
            && this.hitNormal == (other as RayCastResult).hitNormal
            && this.velocity == (other as RayCastResult).velocity
            && this.angularVelocity == (other as RayCastResult).angularVelocity
         }
   }
}
