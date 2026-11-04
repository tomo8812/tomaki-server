package org.valkyrienskies.core.internal

import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet

public interface VsiGameServer {
   public abstract fun moveTerrainAcrossDimensions(shipChunks: IShipActiveChunksSet, srcDimension: String, destDimension: String) {
   }
}
