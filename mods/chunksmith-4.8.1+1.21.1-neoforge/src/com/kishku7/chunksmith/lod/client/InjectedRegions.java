package com.kishku7.chunksmith.lod.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InjectedRegions {
   private final Map<String, Long> injected = new ConcurrentHashMap<>();

   public boolean claim(String dimension, int regionX, int regionZ, long hash) {
      String key = key(dimension, regionX, regionZ);
      Long previous = this.injected.put(key, hash);
      return previous == null ? true : previous != hash;
   }

   public void seed(String dimension, int regionX, int regionZ, long hash) {
      this.injected.put(key(dimension, regionX, regionZ), hash);
   }

   public void release(String dimension, int regionX, int regionZ) {
      this.injected.remove(key(dimension, regionX, regionZ));
   }

   public boolean contains(String dimension, int regionX, int regionZ) {
      return this.injected.containsKey(key(dimension, regionX, regionZ));
   }

   public Long injectedHash(String dimension, int regionX, int regionZ) {
      return this.injected.get(key(dimension, regionX, regionZ));
   }

   public int size() {
      return this.injected.size();
   }

   public void clear() {
      this.injected.clear();
   }

   static String key(String dimension, int regionX, int regionZ) {
      return dimension + "/" + regionX + "," + regionZ;
   }
}
