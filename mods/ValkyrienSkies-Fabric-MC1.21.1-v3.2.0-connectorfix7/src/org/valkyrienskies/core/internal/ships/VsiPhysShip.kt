package org.valkyrienskies.core.internal.ships

import org.valkyrienskies.core.api.ships.DragController
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.properties.PhysPose
import org.valkyrienskies.core.api.util.PhysTickOnly

@PhysTickOnly
public interface VsiPhysShip : PhysShip, VsiShip {
   public var kinematicTarget: PhysPose?

   public val dragController: DragController?
}
