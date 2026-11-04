package org.valkyrienskies.core.api.events

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.util.GameTickOnly

@VsBeta
@GameTickOnly
public interface ShipLoadEventClient {
   public val ship: ClientShip
}
