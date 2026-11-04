package org.valkyrienskies.core.internal

import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.ships.properties.ShipTransform

public interface ShipTeleportData {
   public val newPos: Vector3dc

   public val newPosInShip: Vector3dc?

   public val newRot: Quaterniondc

   public val newVel: Vector3dc

   public val newOmega: Vector3dc

   public val newDimension: String?

   public val newScale: Double?

   public abstract fun createNewShipTransform(oldShipTransform: ShipTransform): BodyKinematics {
   }
}
