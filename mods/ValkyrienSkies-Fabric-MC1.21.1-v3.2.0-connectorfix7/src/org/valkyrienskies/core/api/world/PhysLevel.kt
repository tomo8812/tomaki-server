package org.valkyrienskies.core.api.world

import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.physics.RayCastResult
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.util.AerodynamicUtils
import org.valkyrienskies.core.api.util.PhysTickOnly

@VsBeta
@PhysTickOnly
public interface PhysLevel {
   public val dimension: String

   public val aerodynamicUtils: AerodynamicUtils

   public abstract fun getShipById(shipId: Long): PhysShip? {
   }

   public abstract fun physTick(delta: Double) {
   }

   public abstract fun rayCast(start: Vector3dc, direction: Vector3dc, length: Double, ignoredShips: LongArray): RayCastResult? {
   }

   public abstract fun enableCollisionBetween(shipId0: Long, shipId1: Long): Boolean {
   }

   public abstract fun disableCollisionBetween(shipId0: Long, shipId1: Long): Boolean {
   }
}
