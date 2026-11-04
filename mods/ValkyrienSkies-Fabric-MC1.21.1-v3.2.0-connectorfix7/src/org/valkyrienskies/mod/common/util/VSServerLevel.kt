package org.valkyrienskies.mod.common.util

import it.unimi.dsi.fastutil.longs.LongOpenHashSet

public interface VSServerLevel {
   public abstract fun removeChunk(chunkX: Int, chunkZ: Int) {
   }

   public abstract fun addPendingForcedChunk(chunkX: Int, chunkZ: Int) {
   }

   public abstract fun getPendingForcedChunks(): LongOpenHashSet {
   }
}
