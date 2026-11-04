package org.valkyrienskies.core.api.ships.properties

import org.joml.Matrix3dc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.bodies.properties.BodyInertia

/** @deprecated */
@Deprecated(message = "Deprecated for removal; use BodyInertia instead.", replaceWith = @ReplaceWith(expression = "BodyInertia", imports = ["org.valkyrienskies.core.api.bodies.properties.BodyInertia"]))
public interface ShipInertiaData : BodyInertia {
   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "inertiaTensor", imports = {}))
   public open val momentOfInertiaTensor: Matrix3dc
      public open get() {
         return this.getInertiaTensor()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "centerOfMass", imports = {}))
   public open val centerOfMassInShip: Vector3dc
      public open get() {
         return this.getCenterOfMass()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "mass", imports = {}))
   public open val shipMass: Double
      public open get() {
         return this.getMass()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "centerOfMass", imports = {}))
   public open val centerOfMassInShipSpace: Vector3dc
      public open get() {
         return this.getCenterOfMass()
      }

}
