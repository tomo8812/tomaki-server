package org.valkyrienskies.core.api.ships

import org.valkyrienskies.core.api.world.PhysLevel

public interface ShipPhysicsListener {
   public abstract fun physTick(physShip: PhysShip, physLevel: PhysLevel) {
   }

   public open fun physTick(physShip: PhysShip, physLevel: PhysLevel, delta: Double) {
      this.physTick(physShip, physLevel)
   }
}
