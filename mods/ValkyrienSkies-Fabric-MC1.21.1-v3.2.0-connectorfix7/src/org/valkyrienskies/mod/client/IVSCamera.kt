package org.valkyrienskies.mod.client

import net.minecraft.class_1297
import net.minecraft.class_1922
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.properties.ShipTransform

public interface IVSCamera {
   public abstract fun setupWithShipMounted(
      level: class_1922,
      renderViewEntity: class_1297,
      thirdPerson: Boolean,
      thirdPersonReverse: Boolean,
      partialTicks: Float,
      shipMountedTo: ClientShip,
      inShipPlayerPosition: Vector3dc
   ) {
   }

   public val shipMountedRenderTransform: ShipTransform?

   public abstract fun resetShipMountedRenderTransform() {
   }
}
