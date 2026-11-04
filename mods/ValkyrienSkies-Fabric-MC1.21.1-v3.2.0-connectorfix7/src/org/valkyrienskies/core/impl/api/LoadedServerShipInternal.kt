package org.valkyrienskies.core.impl.api

import org.valkyrienskies.core.internal.ShipTeleportData
import org.valkyrienskies.core.internal.ships.VsiLoadedServerShip

public interface LoadedServerShipInternal : LoadedShipInternal, ServerShipInternal, VsiLoadedServerShip {
   public val shipTeleportId: Int

   public abstract fun teleportShip(teleportData: ShipTeleportData) {
   }
}
