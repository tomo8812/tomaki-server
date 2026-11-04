package org.valkyrienskies.core.api.ships

import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.util.GameTickOnly

@GameTickOnly
public interface ClientShip : LoadedShip {
   public val renderTransform: ShipTransform

   public val renderAABB: AABBdc

   public var transformProvider: ClientShipTransformProvider?
}
