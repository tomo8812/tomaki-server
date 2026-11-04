package com.kishku7.chunksmith.lod;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class LodHorizon {
   public static final int SEAL_CHUNKS = 2;
   static final long GRACE_MS = 300000L;
   private static final Map<String, LodHorizon.Zone> ZONES = new ConcurrentHashMap<>();
   private static volatile boolean anyArmed;
   private static volatile String unavailableReason = "this Minecraft version or loader has no LOD support";

   private LodHorizon() {
   }

   public static void setUnavailableReason(String reason) {
      unavailableReason = reason;
   }

   public static String unavailableReason() {
      return unavailableReason;
   }

   public static long horizonChunks(double horizonBlocks) {
      return (long)Math.ceil(horizonBlocks / 16.0);
   }

   public static boolean isBeyond(int centerChunkX, int centerChunkZ, double horizonBlocks, boolean round, int chunkX, int chunkZ) {
      return new LodHorizon.Zone(centerChunkX, centerChunkZ, horizonChunks(horizonBlocks) + 2L, round, null).beyond(chunkX, chunkZ);
   }

   public static void arm(String world, int centerChunkX, int centerChunkZ, double horizonBlocks, boolean round, LodHorizon.Existing existing) {
      ZONES.put(world, new LodHorizon.Zone(centerChunkX, centerChunkZ, horizonChunks(horizonBlocks) + 2L, round, existing));
      anyArmed = true;
   }

   public static void release(String world) {
      LodHorizon.Zone zone = ZONES.get(world);
      if (zone != null) {
         zone.disarmAt = System.currentTimeMillis() + 300000L;
      }
   }

   public static boolean anyArmed() {
      return anyArmed;
   }

   private static LodHorizon.Zone live(String world) {
      LodHorizon.Zone zone = ZONES.get(world);
      if (zone == null) {
         return null;
      } else if (System.currentTimeMillis() >= zone.disarmAt) {
         ZONES.remove(world, zone);
         anyArmed = !ZONES.isEmpty();
         return null;
      } else {
         return zone;
      }
   }

   public static boolean isArmed(String world) {
      return live(world) != null;
   }

   public static boolean inDiscardZone(String world, int chunkX, int chunkZ) {
      LodHorizon.Zone zone = live(world);
      if (zone != null && zone.beyond(chunkX, chunkZ)) {
         return zone.kept.contains(pack(chunkX, chunkZ)) ? false : zone.existing == null || !zone.existing.isGenerated(chunkX, chunkZ);
      } else {
         return false;
      }
   }

   public static void keepAround(String world, int chunkX, int chunkZ, int radius) {
      LodHorizon.Zone zone = live(world);
      if (zone != null) {
         for (int x = chunkX - radius; x <= chunkX + radius; x++) {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
               if (zone.beyond(x, z)) {
                  zone.kept.add(pack(x, z));
               }
            }
         }
      }
   }

   public static void noteDiscarded(String world) {
      LodHorizon.Zone zone = ZONES.get(world);
      if (zone != null) {
         zone.discarded.incrementAndGet();
      }
   }

   public static long discarded(String world) {
      LodHorizon.Zone zone = ZONES.get(world);
      return zone == null ? 0L : zone.discarded.get();
   }

   public static Set<String> armedWorlds() {
      ZONES.keySet().forEach(LodHorizon::live);
      return Set.copyOf(ZONES.keySet());
   }

   private static long pack(int chunkX, int chunkZ) {
      return chunkX & 4294967295L | (chunkZ & 4294967295L) << 32;
   }

   public interface Existing {
      boolean isGenerated(int var1, int var2);
   }

   private static final class Zone {
      final int centerChunkX;
      final int centerChunkZ;
      final long limitChunks;
      final boolean round;
      final LodHorizon.Existing existing;
      final Set<Long> kept = ConcurrentHashMap.newKeySet();
      final AtomicLong discarded = new AtomicLong();
      volatile long disarmAt = Long.MAX_VALUE;

      Zone(int centerChunkX, int centerChunkZ, long limitChunks, boolean round, LodHorizon.Existing existing) {
         this.centerChunkX = centerChunkX;
         this.centerChunkZ = centerChunkZ;
         this.limitChunks = limitChunks;
         this.round = round;
         this.existing = existing;
      }

      boolean beyond(int chunkX, int chunkZ) {
         long dx = (long)chunkX - this.centerChunkX;
         long dz = (long)chunkZ - this.centerChunkZ;
         return this.round ? dx * dx + dz * dz > this.limitChunks * this.limitChunks : Math.max(Math.abs(dx), Math.abs(dz)) > this.limitChunks;
      }
   }
}
