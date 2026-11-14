package com.kishku7.chunksmith.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChunkResidency {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   public static final long FRESH_MILLIS = 60000L;
   private static final long DRAIN_MARGIN = 512L;
   private static final long DRAIN_STALL_MILLIS = 30000L;
   private static final long DRAIN_MAX_MILLIS = 600000L;
   private static volatile long loadedChunks = -1L;
   private static volatile long reportedAtMillis;
   private static volatile long baseline = -1L;
   private static volatile boolean draining;
   private static volatile long drainStartedMillis;
   private static volatile long drainBestSeen = Long.MAX_VALUE;
   private static volatile long drainProgressMillis;
   private static volatile long drainStartedAt = -1L;
   private static volatile boolean drainOnFullBudget;
   private static volatile boolean generationHeld;
   private static volatile String lastDrainOutcome = "none yet";

   private ChunkResidency() {
   }

   public static void report(long loaded) {
      report(loaded, System.currentTimeMillis());
   }

   static void report(long loaded, long now) {
      if (loaded >= 0L) {
         loadedChunks = loaded;
         reportedAtMillis = now;
         if (draining) {
            evaluateDrain(loaded, now);
         }
      }
   }

   public static long loadedChunks() {
      return loadedChunksAt(System.currentTimeMillis());
   }

   static long loadedChunksAt(long now) {
      long value = loadedChunks;
      if (value < 0L) {
         return -1L;
      } else {
         return now - reportedAtMillis > 60000L ? -1L : value;
      }
   }

   public static boolean isSupported() {
      return loadedChunks() >= 0L;
   }

   public static long baseline() {
      return baseline;
   }

   public static long addedChunks() {
      long now = loadedChunks();
      long base = baseline;
      return now >= 0L && base >= 0L ? Math.max(0L, now - base) : -1L;
   }

   public static void noteTaskStart() {
      noteTaskStart(System.currentTimeMillis());
   }

   static void noteTaskStart(long now) {
      baseline = loadedChunksAt(now);
      draining = false;
      drainBestSeen = Long.MAX_VALUE;
   }

   public static void noteTaskEnd() {
      noteTaskEnd(System.currentTimeMillis());
   }

   static void noteTaskEnd(long now) {
      draining = true;
      drainStartedMillis = now;
      drainProgressMillis = now;
      long current = loadedChunksAt(now);
      drainBestSeen = current < 0L ? Long.MAX_VALUE : current;
      drainStartedAt = current;
      LOGGER.info(
         String.format(
            "Chunksmith: pregen finished; draining its chunks. %d resident, %d of them added by this run. %s",
            current,
            Math.max(0L, current - baseline),
            UnloadDiagnostics.describe() + " | our tickets: " + TicketLedger.describe()
         )
      );
   }

   public static boolean isDraining() {
      return draining;
   }

   public static void noteDrainBudget(boolean fullBudget) {
      drainOnFullBudget = fullBudget;
   }

   public static void reconsiderDrain() {
      reconsiderDrain(System.currentTimeMillis());
   }

   static void reconsiderDrain(long now) {
      if (!draining) {
         long base = baseline;
         long loaded = loadedChunksAt(now);
         if (base >= 0L && loaded >= 0L && loaded > base + 512L) {
            draining = true;
            drainStartedMillis = now;
            drainProgressMillis = now;
            drainBestSeen = loaded;
            drainStartedAt = loaded;
            LOGGER.info(
               String.format("Chunksmith: resuming the drain now that conditions allow it. %d resident, %d above where the run started.", loaded, loaded - base)
            );
         }
      }
   }

   public static void noteGenerationHeld(boolean held) {
      generationHeld = held;
   }

   public static boolean isGenerationHeld() {
      return generationHeld;
   }

   public static void clear() {
      loadedChunks = -1L;
      reportedAtMillis = 0L;
      baseline = -1L;
      draining = false;
      drainBestSeen = Long.MAX_VALUE;
      drainStartedMillis = 0L;
      drainProgressMillis = 0L;
      drainStartedAt = -1L;
      drainOnFullBudget = false;
      generationHeld = false;
      UnloadDiagnostics.clear();
      lastDrainOutcome = "none yet";
   }

   private static void evaluateDrain(long loaded, long now) {
      long base = baseline;
      if (base >= 0L && loaded <= base + 512L) {
         finishDrain(loaded, now, "back to where the run started");
      } else {
         if (loaded < drainBestSeen) {
            drainBestSeen = loaded;
            drainProgressMillis = now;
         } else if (!drainOnFullBudget) {
            drainProgressMillis = now;
         } else if (now - drainProgressMillis >= 30000L) {
            finishDrain(loaded, now, "stopped falling on a full budget; the rest is pinned by something that is not ours");
            return;
         }

         if (now - drainStartedMillis >= 600000L) {
            finishDrain(loaded, now, "hit the ten-minute ceiling");
         }
      }
   }

   private static void finishDrain(long loaded, long now, String reason) {
      draining = false;
      long seconds = Math.max(0L, (now - drainStartedMillis) / 1000L);
      long freed = drainStartedAt < 0L ? -1L : Math.max(0L, drainStartedAt - loaded);
      lastDrainOutcome = String.format("%s (%d resident, %d freed, %ds)", reason, loaded, freed, seconds);
      String message = String.format(
         "Chunksmith: drain finished (%s). %d chunks resident, %d freed, %d above where the run started, took %ds. %s",
         reason,
         loaded,
         freed,
         baseline < 0L ? -1L : Math.max(0L, loaded - baseline),
         seconds,
         UnloadDiagnostics.describe() + " | our tickets: " + TicketLedger.describe()
      );
      if (baseline >= 0L && loaded > baseline + 512L) {
         LOGGER.warn(message);
      } else {
         LOGGER.info(message);
      }
   }

   public static String describe() {
      long now = loadedChunks();
      long base = baseline;
      long added = addedChunks();
      return String.format(
         "resident=%s baseline=%s added=%s draining=%s heap=%dMB of %dMB (%.0f pct) lastDrain=[%s]",
         now < 0L ? "unknown" : Long.toString(now),
         base < 0L ? "unset" : Long.toString(base),
         added < 0L ? "unknown" : Long.toString(added),
         draining,
         HeapPressure.usedMegabytes(),
         HeapPressure.maxMegabytes(),
         HeapPressure.usedPercent(),
         lastDrainOutcome
      );
   }
}
