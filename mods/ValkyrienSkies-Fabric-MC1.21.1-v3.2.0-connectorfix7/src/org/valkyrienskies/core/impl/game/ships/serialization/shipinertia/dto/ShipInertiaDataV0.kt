package org.valkyrienskies.core.impl.game.ships.serialization.shipinertia.dto

import org.joml.Matrix3d
import org.joml.Vector3d

public data class ShipInertiaDataV0(centerOfMassInShipSpace: Vector3d, shipMass: Double, momentOfInertiaTensor: Matrix3d) {
   public final val centerOfMassInShipSpace: Vector3d
   public final var shipMass: Double
   public final val momentOfInertiaTensor: Matrix3d

   init {
      this.centerOfMassInShipSpace = centerOfMassInShipSpace
      this.shipMass = shipMass
      this.momentOfInertiaTensor = momentOfInertiaTensor
   }

   public operator fun component1(): Vector3d {
      return this.centerOfMassInShipSpace
   }

   public operator fun component2(): Double {
      return this.shipMass
   }

   public operator fun component3(): Matrix3d {
      return this.momentOfInertiaTensor
   }

   public fun copy(
      centerOfMassInShipSpace: Vector3d = this.centerOfMassInShipSpace,
      shipMass: Double = this.shipMass,
      momentOfInertiaTensor: Matrix3d = this.momentOfInertiaTensor
   ): ShipInertiaDataV0 {
      return ShipInertiaDataV0(centerOfMassInShipSpace, shipMass, momentOfInertiaTensor)
   }

   public override fun toString(): String {
      return "ShipInertiaDataV0(centerOfMassInShipSpace=${this.centerOfMassInShipSpace}, shipMass=${this.shipMass}, momentOfInertiaTensor=${this.momentOfInertiaTensor})"
   }

   public override fun hashCode(): Int {
      return (this.centerOfMassInShipSpace.hashCode() * 31 + java.lang.Double.hashCode(this.shipMass)) * 31 + this.momentOfInertiaTensor.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is ShipInertiaDataV0
            && this.centerOfMassInShipSpace == (other as ShipInertiaDataV0).centerOfMassInShipSpace
            && java.lang.Double.compare(this.shipMass, (other as ShipInertiaDataV0).shipMass) == 0
            && this.momentOfInertiaTensor == (other as ShipInertiaDataV0).momentOfInertiaTensor
         }
   }
}
