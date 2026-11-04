package org.valkyrienskies.core.impl.game.ships

import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.bodies.properties.BodyTransformFactory
import org.valkyrienskies.core.impl.shadow.Ew

public class ShipTransformImpl {
   public companion object {
      public final val UNIT_SCALING: Vector3dc
      public final val ZERO: Vector3dc
      public final val ZERO_ROTATION: Quaterniondc

      public fun createEmpty(): ShipTransform {
         return this.create(this.ZERO, this.ZERO)
      }

      public fun create(centerCoordinateInWorld: Vector3dc, centerCoordinateInShip: Vector3dc): ShipTransform {
         return this.create(centerCoordinateInWorld, centerCoordinateInShip, this.ZERO_ROTATION)
      }

      public fun create(centerCoordinateInWorld: Vector3dc, centerCoordinateInShip: Vector3dc, shipRotation: Quaterniondc): ShipTransform {
         return Ew.a(centerCoordinateInWorld, centerCoordinateInShip, shipRotation, this.UNIT_SCALING)
      }

      public fun create(centerCoordinateInWorld: Vector3dc, centerCoordinateInShip: Vector3dc, shipRotation: Quaterniondc, shipScaling: Vector3dc): ShipTransform {
         return Ew.a(centerCoordinateInWorld, centerCoordinateInShip, shipRotation, shipScaling)
      }

      public fun createFromSlerp(prevTransform: ShipTransform, curTransform: ShipTransform, alpha: Double): ShipTransform {
         return BodyTransformFactory.INSTANCE.createFromSlerp(prevTransform, curTransform, alpha)
      }
   }
}
