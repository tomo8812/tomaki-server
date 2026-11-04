package org.valkyrienskies.core.internal.world

import java.net.SocketAddress
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.core.api.world.ClientShipWorld
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData

@GameTickOnly
public interface VsiClientShipWorld : VsiShipWorld, ClientShipWorld {
   public val allShips: VsiQueryableShipData<ClientShip>

   public val loadedShips: VsiQueryableShipData<ClientShip>

   public abstract fun getPhysEntityClientRenderTransform(id: Long): ShipTransform? {
   }

   public abstract fun tickNetworking(server: SocketAddress) {
   }

   public abstract fun postTick() {
   }

   public abstract fun updateRenderTransforms(partialTicks: Double) {
   }

   public abstract fun destroyWorld() {
   }

   public val isSyncedWithServer: Boolean
}
