package org.valkyrienskies.mod.common.entity

import org.joml.Vector3d

public interface ShipyardPosSavable {
   public abstract fun `valkyrienskies$getUnloadedShipyardPos`(): Vector3d? {
   }

   public abstract fun `valkyrienskies$setUnloadedShipyardPos`(vector3d: Vector3d?) {
   }
}
