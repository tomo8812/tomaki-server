package com.kishku7.chunksmith.util;

public final class UnloadDiagnostics {
   private static volatile boolean supported;
   private static volatile long visible;
   private static volatile long toDrop;
   private static volatile long unloadQueue;
   private static volatile long pendingUnloads;
   private static volatile boolean hasTickets;
   private static volatile long levelTicking;
   private static volatile long levelLoaded;
   private static volatile long levelDroppable;
   private static volatile long levelSampledAt;
   private static volatile String ticketSample = "not sampled yet";
   private static volatile String ticketTally = "not sampled yet";
   private static volatile Runnable levelSampler;

   private UnloadDiagnostics() {
   }

   public static void report(long visible, long toDrop, long unloadQueue, long pendingUnloads, boolean hasTickets) {
      UnloadDiagnostics.visible = visible;
      UnloadDiagnostics.toDrop = toDrop;
      UnloadDiagnostics.unloadQueue = unloadQueue;
      UnloadDiagnostics.pendingUnloads = pendingUnloads;
      UnloadDiagnostics.hasTickets = hasTickets;
      supported = true;
   }

   public static boolean isSupported() {
      return supported;
   }

   public static long toDrop() {
      return toDrop;
   }

   public static long unloadQueue() {
      return unloadQueue;
   }

   public static void offerLevelSampler(Runnable sampler) {
      levelSampler = sampler;
   }

   public static void sampleLevelsNow() {
      Runnable sampler = levelSampler;
      if (sampler != null) {
         sampler.run();
      }
   }

   public static void reportLevels(long ticking, long loaded, long droppable, long sampledAtMillis) {
      levelTicking = ticking;
      levelLoaded = loaded;
      levelDroppable = droppable;
      levelSampledAt = sampledAtMillis;
   }

   public static void reportTicketSample(String sample) {
      ticketSample = sample;
   }

   public static void reportTicketTally(String tally) {
      ticketTally = tally;
   }

   public static String describeTicketTally() {
      return ticketTally;
   }

   public static String describeTicketSample() {
      return ticketSample;
   }

   public static String describeLevels() {
      if (levelSampledAt <= 0L) {
         return levelSampler == null ? "unavailable on this platform" : "not sampled yet";
      }

      long total = levelTicking + levelLoaded + levelDroppable;
      String verdict;
      if (total <= 0L) {
         verdict = "nothing resident";
      } else if (levelDroppable > total / 2L) {
         verdict = "MOST ARE PAST MAX_LEVEL: nothing holds them and vanilla is not dropping them";
      } else {
         verdict = "mostly worldgen context around the frontier; judge by 'ticking', not this";
      }

      return String.format(
         "ticking=%d loaded=%d droppable=%d age=%ds (%s)",
         levelTicking,
         levelLoaded,
         levelDroppable,
         Math.max(0L, (System.currentTimeMillis() - levelSampledAt) / 1000L),
         verdict
      );
   }

   public static void clear() {
      supported = false;
      visible = 0L;
      toDrop = 0L;
      unloadQueue = 0L;
      pendingUnloads = 0L;
      hasTickets = false;
      levelTicking = 0L;
      levelLoaded = 0L;
      levelDroppable = 0L;
      levelSampledAt = 0L;
      ticketSample = "not sampled yet";
      ticketTally = "not sampled yet";
      levelSampler = null;
   }

   public static String describe() {
      if (!supported) {
         return "unavailable on this platform";
      }

      String verdict;
      if (visible <= 0L) {
         verdict = "nothing resident";
      } else if (unloadQueue > 2000L) {
         verdict = "eligible work is backing up, a throughput problem";
      } else {
         verdict = "note: toDrop and unloadQueue are drained every tick, so 0 here means nothing";
      }

      return String.format(
         "visible=%d toDrop=%d unloadQueue=%d pendingUnloads=%d hasTickets=%s (%s)", visible, toDrop, unloadQueue, pendingUnloads, hasTickets, verdict
      );
   }
}
