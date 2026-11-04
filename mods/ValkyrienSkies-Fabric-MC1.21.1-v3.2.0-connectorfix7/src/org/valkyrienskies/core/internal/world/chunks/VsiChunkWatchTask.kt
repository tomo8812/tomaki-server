package org.valkyrienskies.core.internal.world.chunks

import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.internal.world.VsiPlayer

public interface VsiChunkWatchTask : java.lang.Comparable<VsiChunkWatchTask> {
   public val chunkPos: Long

   public val dimensionId: String

   public val playersNeedWatching: Iterable<VsiPlayer>

   public val ship: ServerShip

   public val chunkX: Int

   public val chunkZ: Int
}
