package dev.diorama.bake;

import dev.diorama.data.ChunkSnapshot;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public final class PinnedSource implements ChunkSource {
   private final ChunkSource live;
   private final Map<Long, @Nullable ChunkSnapshot> chunks = new HashMap<>();
   private final Map<PinnedSource.Top, Integer> tops = new HashMap<>();
   private final int topY;

   public PinnedSource(ChunkSource live) {
      this.live = live;
      this.topY = live.topY();
   }

   @Override
   public @Nullable ChunkSnapshot chunk(int chunkX, int chunkZ) {
      long key = (long)chunkX << 32 | chunkZ & 4294967295L;
      if (this.chunks.containsKey(key)) {
         return this.chunks.get(key);
      }

      ChunkSnapshot chunk = this.live.chunk(chunkX, chunkZ);
      this.chunks.put(key, chunk);
      return chunk;
   }

   @Override
   public int topBelow(int chunkX, int chunkZ, int ceiling) {
      return this.tops.computeIfAbsent(new PinnedSource.Top(chunkX, chunkZ, ceiling), top -> this.live.topBelow(chunkX, chunkZ, ceiling));
   }

   @Override
   public int minY() {
      return this.live.minY();
   }

   @Override
   public int maxY() {
      return this.live.maxY();
   }

   @Override
   public int topY() {
      return this.topY;
   }

   @Override
   public int roof() {
      return this.live.roof();
   }

   @Override
   public boolean depthCaves() {
      return this.live.depthCaves();
   }

   @Override
   public boolean isSeen(int x, int y, int z) {
      return this.live.isSeen(x, y, z);
   }

   private record Top(int chunkX, int chunkZ, int ceiling) {
   }
}
