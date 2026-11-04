package org.valkyrienskies.mod.api

import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.util.PhysTickOnly
import org.valkyrienskies.core.api.world.PhysLevel

@PhysTickOnly
public interface BlockEntityPhysicsListener {
   public abstract fun physTick(physShip: PhysShip?, physLevel: PhysLevel) {
   }

   public var dimension: String
}
