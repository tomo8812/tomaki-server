package org.valkyrienskies.core.api.events

import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.util.GameTickOnly

@VsBeta
@GameTickOnly
public interface ShipLoadEvent {
   public val ship: LoadedServerShip
}
