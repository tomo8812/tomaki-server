package org.valkyrienskies.core.internal.world.chunks

import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.internal.world.VsiPlayer

public interface VsiChunkUnwatchTask : java.lang.Comparable<VsiChunkUnwatchTask> {
   public val chunkPos: Long

   public val dimensionId: String

   public val playersNeedUnwatching: Iterable<VsiPlayer>

   public val shouldUnload: Boolean

   public val ship: ServerShip

   public val chunkX: Int

   public val chunkZ: Int
}
