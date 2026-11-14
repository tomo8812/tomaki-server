package com.kishku7.chunksmith.lod.net;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CsLodBackfill {
   private final Map<UUID, Integer> radii = new ConcurrentHashMap<>();

   public int radius(UUID player, int baseRadius) {
      return Math.max(baseRadius, this.radii.getOrDefault(player, 0));
   }

   public int advance(UUID player, int baseRadius, int cap) {
      return this.radii.merge(player, next(baseRadius, cap), (old, fresh) -> next(Math.max(old, baseRadius), cap));
   }

   static int next(int current, int cap) {
      if (current >= cap) {
         return cap;
      }

      long doubled = current * 2L;
      return (int)Math.min(doubled, cap);
   }

   public void forget(UUID player) {
      this.radii.remove(player);
   }

   public void clear() {
      this.radii.clear();
   }
}
