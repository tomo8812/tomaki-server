package dev.diorama.bake;

import dev.diorama.data.ChunkSnapshot;
import org.jspecify.annotations.Nullable;

public interface ChunkSource {
   int NO_ROOF = Integer.MIN_VALUE;

   @Nullable ChunkSnapshot chunk(int var1, int var2);

   default int topBelow(int chunkX, int chunkZ, int ceiling) {
      ChunkSnapshot chunk = this.chunk(chunkX, chunkZ);
      return chunk == null ? Integer.MIN_VALUE : chunk.topBelow(ceiling);
   }

   int minY();

   int maxY();

   int topY();

   default int roof() {
      return Integer.MIN_VALUE;
   }

   default boolean depthCaves() {
      return false;
   }

   boolean isSeen(int var1, int var2, int var3);
}
