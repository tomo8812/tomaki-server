package com.kishku7.chunksmith.util;

import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegionCache {
   private final Map<String, RegionCache.WorldState> cache = new ConcurrentHashMap<>();

   public RegionCache.WorldState getWorld(String world) {
      return this.cache.computeIfAbsent(world, x -> new RegionCache.WorldState());
   }

   public void clear(String world) {
      this.cache.remove(world);
   }

   public static final class WorldState {
      private final Map<Long, BitSet> regions = new ConcurrentHashMap<>();

      public void setGenerated(int x, int z) {
         int regionX = x >> 5;
         int regionZ = z >> 5;
         long regionKey = ChunkMath.pack(regionX, regionZ);
         BitSet region = this.regions.computeIfAbsent(regionKey, v -> new BitSet());
         int chunkIndex = ChunkMath.regionIndex(x, z);
         synchronized (region) {
            region.set(chunkIndex);
         }
      }

      public boolean isGenerated(int x, int z) {
         int regionX = x >> 5;
         int regionZ = z >> 5;
         long regionKey = ChunkMath.pack(regionX, regionZ);
         if (!this.regions.containsKey(regionKey)) {
            return false;
         }

         BitSet region = this.regions.get(regionKey);
         int chunkIndex = ChunkMath.regionIndex(x, z);
         synchronized (region) {
            return region.get(chunkIndex);
         }
      }
   }
}
