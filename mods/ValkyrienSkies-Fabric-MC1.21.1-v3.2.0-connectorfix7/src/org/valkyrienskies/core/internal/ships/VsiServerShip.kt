package org.valkyrienskies.core.internal.ships

import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.PhysPose

public interface VsiServerShip : ServerShip, VsiShip {
   public var kinematicTarget: PhysPose?
}
