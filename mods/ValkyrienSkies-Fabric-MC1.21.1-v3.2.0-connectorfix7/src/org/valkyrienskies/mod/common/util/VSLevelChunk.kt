package org.valkyrienskies.mod.common.util

public interface VSLevelChunk {
   public abstract fun clearChunk() {
   }

   public abstract fun copyChunkFromOtherDimension(srcChunk: VSLevelChunk) {
   }
}
