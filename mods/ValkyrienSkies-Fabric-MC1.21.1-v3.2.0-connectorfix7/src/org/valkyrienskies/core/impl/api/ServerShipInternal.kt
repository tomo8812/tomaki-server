package org.valkyrienskies.core.impl.api

import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.internal.ships.VsiServerShip

public interface ServerShipInternal : ShipInternal, VsiServerShip {
   public abstract fun asShipDataCommon(): ShipDataCommon {
   }

   public abstract fun updateShipAABBGenerator(posX: Int, posY: Int, posZ: Int, set: Boolean) {
   }

   public abstract fun onLoadChunk(chunkX: Int, chunkZ: Int) {
   }

   public abstract fun onUnloadChunk(chunkX: Int, chunkZ: Int) {
   }

   public abstract fun areVoxelsFullyLoaded(): Boolean {
   }
}
