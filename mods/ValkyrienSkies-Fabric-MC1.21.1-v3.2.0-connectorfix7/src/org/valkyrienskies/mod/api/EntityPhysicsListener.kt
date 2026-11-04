package org.valkyrienskies.mod.api

import org.valkyrienskies.core.api.world.PhysLevel

public interface EntityPhysicsListener {
   public abstract fun physTick(physLevel: PhysLevel) {
   }

   public var dimension: String
}
