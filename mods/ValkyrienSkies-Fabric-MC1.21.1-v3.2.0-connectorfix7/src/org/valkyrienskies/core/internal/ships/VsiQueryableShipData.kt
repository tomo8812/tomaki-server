package org.valkyrienskies.core.internal.ships

import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.Ship

public interface VsiQueryableShipData<S extends Ship> : QueryableShipData<S> {
   public abstract fun getByChunkPos(chunkX: Int, chunkZ: Int): Any? {
   }
}
