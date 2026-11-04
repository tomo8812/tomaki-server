package org.valkyrienskies.core.impl.shadow

import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import org.valkyrienskies.core.internal.ShipTeleportData
import org.valkyrienskies.core.internal.VsiCoreCommands
import org.valkyrienskies.core.internal.world.VsiServerShipWorld

public object DS : VsiCoreCommands {
   public override fun deleteShips(p0: ServerShipWorld, p1: List<ServerShip>) {
      for (var3 in var4 = ships) {
         (world as VsiServerShipWorld).deleteShip(var3)
      }
   }

   public override fun renameShip(p0: ServerShip, p1: String) {
      ship.slug = newName
   }

   public override fun scaleShip(p0: ServerShipWorld, p1: ServerShip, p2: Double) {
      val var5: ShipTransform = ship.getTransform()
      this.teleportShip(
         world, ship, ShipTeleportDataImpl(var5.positionInWorld, var5.shipToWorldRotation, Vector3d(), Vector3d(), null, newScale, null, 80, null)
      )
   }

   public override fun teleportShip(p0: ServerShipWorld, p1: ServerShip, p2: ShipTeleportData) {
      (world as VsiServerShipWorld).teleportShip(ship, teleportData)
   }
}
