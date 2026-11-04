package org.valkyrienskies.core.impl.shadow

import org.joml.Vector3dc
import org.valkyrienskies.core.api.physics.RayCastResult
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.util.AerodynamicUtils
import org.valkyrienskies.core.api.world.PhysLevel

public object FS : PhysLevel {
   public open val b: String
      public open get() {
         return "minecraft:dimension:minecraft:overworld"
      }


   public open val a: AerodynamicUtils
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public override fun getShipById(p0: Long): PhysShip? {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun physTick(p0: Double) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun rayCast(p0: Vector3dc, p1: Vector3dc, p2: Double, p3: LongArray): RayCastResult? {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun enableCollisionBetween(p0: Long, p1: Long): Boolean {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun disableCollisionBetween(p0: Long, p1: Long): Boolean {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }
}
