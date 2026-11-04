package org.valkyrienskies.core.impl.api

import org.valkyrienskies.core.api.ships.ServerShip

@Deprecated(message = "sus")
public interface ServerShipUser : ServerShipProvider {
   public var ship: ServerShip?
}
