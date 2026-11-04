package org.valkyrienskies.core.internal

import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.world.ServerShipWorld

public interface VsiCoreCommands {
   public abstract fun deleteShips(world: ServerShipWorld, ships: List<ServerShip>) {
   }

   public abstract fun renameShip(ship: ServerShip, newName: String) {
   }

   public abstract fun scaleShip(world: ServerShipWorld, ship: ServerShip, newScale: Double) {
   }

   public abstract fun teleportShip(world: ServerShipWorld, ship: ServerShip, teleportData: ShipTeleportData) {
   }
}
