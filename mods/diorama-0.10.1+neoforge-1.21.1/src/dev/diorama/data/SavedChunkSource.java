package dev.diorama.data;

import dev.diorama.bake.ChunkSource;
import dev.diorama.waypoint.Portals;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

public final class SavedChunkSource implements ChunkSource {
   private static final int CACHED_SNAPSHOTS = Runtime.getRuntime().maxMemory() > 3221225472L ? 1536 : 512;
   private final ChunkSource live;
   private final ChunkSave save;
   private volatile SavedChunkSource.Allowed allowed = (chunkX, chunkZ, top) -> true;
   private final Map<Long, @Nullable ChunkSnapshot> snapshots = new LinkedHashMap<Long, ChunkSnapshot>(CACHED_SNAPSHOTS, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, @Nullable ChunkSnapshot> eldest) {
         return this.size() > SavedChunkSource.CACHED_SNAPSHOTS;
      }
   };
   private final @Nullable Consumer<List<Portals.Spot>> portals;
   private final int blendRadius;

   public SavedChunkSource(ChunkSource live, ChunkSave save, Consumer<List<Portals.Spot>> portals) {
      this(live, save, portals, -1);
   }

   public SavedChunkSource(ChunkSource live, ChunkSave save, int blendRadius) {
      this(live, save, null, blendRadius);
   }

   private SavedChunkSource(ChunkSource live, ChunkSave save, @Nullable Consumer<List<Portals.Spot>> portals, int blendRadius) {
      this.live = live;
      this.save = save;
      this.portals = portals;
      this.blendRadius = blendRadius;
   }

   @Override
   public @Nullable ChunkSnapshot chunk(int chunkX, int chunkZ) {
      ChunkSnapshot live = this.live.chunk(chunkX, chunkZ);
      return live != null ? live : this.saved(chunkX, chunkZ);
   }

   public synchronized void allow(SavedChunkSource.Allowed allowed) {
      this.allowed = allowed;
      this.snapshots.clear();
   }

   private synchronized @Nullable ChunkSnapshot saved(int chunkX, int chunkZ) {
      long key = ChunkSnapshot.key(chunkX, chunkZ);
      if (this.snapshots.containsKey(key)) {
         return this.snapshots.get(key);
      }

      SavedChunk chunk = this.save.read(chunkX, chunkZ);
      ChunkSnapshot snapshot = null;
      if (chunk != null && chunk.finished() && this.allowed.test(chunkX, chunkZ, this.save.top(chunkX, chunkZ))) {
         SavedChunk[] around = new SavedChunk[9];

         for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
               around[(dx + 1) * 3 + dz + 1] = dx == 0 && dz == 0 ? chunk : this.save.read(chunkX + dx, chunkZ + dz);
            }
         }

         snapshot = this.blendRadius < 0
            ? ChunkSnapshot.fromSave(chunk, around, this.save.level())
            : ChunkSnapshot.fromSave(chunk, around, this.save.level(), this.blendRadius);
         if (this.portals != null) {
            List<Portals.Spot> spots = Portals.find(snapshot, this.roof());
            if (!spots.isEmpty()) {
               this.portals.accept(spots);
            }
         }
      }

      this.snapshots.put(key, snapshot);
      return snapshot;
   }

   @Override
   public int topBelow(int chunkX, int chunkZ, int ceiling) {
      ChunkSnapshot live = this.live.chunk(chunkX, chunkZ);
      if (live != null) {
         return live.topBelow(ceiling);
      }

      int top;
      synchronized (this) {
         top = this.save.top(chunkX, chunkZ);
      }

      if (top != Integer.MIN_VALUE && top >= ceiling) {
         ChunkSnapshot chunk = this.chunk(chunkX, chunkZ);
         return chunk == null ? Integer.MIN_VALUE : chunk.topBelow(ceiling);
      } else {
         return top == Integer.MIN_VALUE ? top : top + 1;
      }
   }

   public synchronized void forgetTops(long region) {
      this.save.forgetTops(region);
   }

   public synchronized void clear() {
      this.snapshots.clear();
      this.save.clear();
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
      return this.live.maxY();
   }

   @Override
   public int roof() {
      return this.live.roof();
   }

   @Override
   public boolean isSeen(int x, int y, int z) {
      return this.live.isSeen(x, y, z);
   }

   public interface Allowed {
      boolean test(int var1, int var2, int var3);
   }
}
