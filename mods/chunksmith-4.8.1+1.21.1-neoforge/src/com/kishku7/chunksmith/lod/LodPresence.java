package com.kishku7.chunksmith.lod;

public final class LodPresence {
   private static volatile LodPresence.Provider provider;
   private static volatile LodPresence.Provider horizonProvider;

   private LodPresence() {
   }

   public static void setProvider(LodPresence.Provider value) {
      provider = value;
   }

   public static CsLodPresenceIndex indexFor(String worldName) {
      LodPresence.Provider current = provider;
      return current == null ? null : current.indexFor(worldName);
   }

   public static void setHorizonProvider(LodPresence.Provider value) {
      horizonProvider = value;
   }

   public static CsLodPresenceIndex horizonIndexFor(String worldName) {
      CsLodPresenceIndex index = indexFor(worldName);
      if (index != null) {
         return index;
      }

      LodPresence.Provider current = horizonProvider;
      return current == null ? null : current.indexFor(worldName);
   }

   @FunctionalInterface
   public interface Provider {
      CsLodPresenceIndex indexFor(String var1);
   }
}
