package org.valkyrienskies.core.impl.game.ships

import org.joml.Vector3d
import org.joml.Vector3dc

public data class ShipPhysicsData(linearVelocity: Vector3dc, angularVelocity: Vector3dc) {
   public final var linearVelocity: Vector3dc
   public final var angularVelocity: Vector3dc

   init {
      this.linearVelocity = linearVelocity
      this.angularVelocity = angularVelocity
   }

   public operator fun component1(): Vector3dc {
      return this.linearVelocity
   }

   public operator fun component2(): Vector3dc {
      return this.angularVelocity
   }

   public fun copy(linearVelocity: Vector3dc = this.linearVelocity, angularVelocity: Vector3dc = this.angularVelocity): ShipPhysicsData {
      return ShipPhysicsData(linearVelocity, angularVelocity)
   }

   public override fun toString(): String {
      return "ShipPhysicsData(linearVelocity=${this.linearVelocity}, angularVelocity=${this.angularVelocity})"
   }

   public override fun hashCode(): Int {
      return this.linearVelocity.hashCode() * 31 + this.angularVelocity.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is ShipPhysicsData
            && this.linearVelocity == (other as ShipPhysicsData).linearVelocity
            && this.angularVelocity == (other as ShipPhysicsData).angularVelocity
         }
   }

   public companion object {
      public fun createEmpty(): ShipPhysicsData {
         return ShipPhysicsData(Vector3d(), Vector3d())
      }
   }
}
