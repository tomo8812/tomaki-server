package com.kishku7.chunksmith.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.List;

public final class HeapPressure {
   public static final int CONFIRM_SAMPLES = 3;
   public static final int RESUME_MARGIN_PERCENT = 15;
   public static final long NO_COLLECTION_RELEASE_MS = 10000L;
   static final int SIGNIFICANT_POOL_PERCENT = 5;
   private static int consecutiveHigh;
   private static volatile long holdStart;
   private static long clockStart;
   private static long[] snapAfterUsed;
   private static long[] snapAfterCommitted;
   private static long lastStaleBytes = -1L;
   private static boolean releasedBlind;

   private HeapPressure() {
   }

   public static double usedPercent() {
      Runtime runtime = Runtime.getRuntime();
      long max = runtime.maxMemory();
      if (max <= 0L) {
         return -1.0;
      }

      long used = runtime.totalMemory() - runtime.freeMemory();
      return 100.0 * used / max;
   }

   public static long usedMegabytes() {
      Runtime runtime = Runtime.getRuntime();
      return (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
   }

   public static long maxMegabytes() {
      return Runtime.getRuntime().maxMemory() / 1048576L;
   }

   static HeapPressure.Pools readPools() {
      long max = Runtime.getRuntime().maxMemory();
      if (max <= 0L) {
         return null;
      }

      try {
         List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();
         int n = 0;

         for (MemoryPoolMXBean pool : beans) {
            if (pool.getType() == MemoryType.HEAP && pool.isValid()) {
               n++;
            }
         }

         long[] afterUsed = new long[n];
         long[] afterCommitted = new long[n];
         long[] currentUsed = new long[n];
         int i = 0;

         for (MemoryPoolMXBean pool : beans) {
            if (pool.getType() == MemoryType.HEAP && pool.isValid() && i < n) {
               MemoryUsage after = pool.getCollectionUsage();
               MemoryUsage now = pool.getUsage();
               afterUsed[i] = after == null ? -1L : after.getUsed();
               afterCommitted[i] = after == null ? 0L : after.getCommitted();
               currentUsed[i] = now == null ? 0L : now.getUsed();
               i++;
            }
         }

         return n == 0 ? null : new HeapPressure.Pools(max, afterUsed, afterCommitted, currentUsed);
      } catch (RuntimeException | LinkageError e) {
         return null;
      }
   }

   public static double afterCollectionPercent() {
      return afterCollectionPercent(readPools());
   }

   static double afterCollectionPercent(HeapPressure.Pools pools) {
      if (pools != null && pools.max > 0L) {
         long total = 0L;
         boolean anyCollected = false;

         for (int i = 0; i < pools.currentUsed.length; i++) {
            if (pools.afterUsed[i] >= 0L && pools.afterCommitted[i] > 0L) {
               total += pools.afterUsed[i];
               anyCollected = true;
            } else {
               total += Math.max(0L, pools.currentUsed[i]);
            }
         }

         return anyCollected ? 100.0 * total / pools.max : -1.0;
      } else {
         return -1.0;
      }
   }

   public static double liveEstimatePercent() {
      return liveEstimate(usedPercent(), afterCollectionPercent());
   }

   static double liveEstimate(double raw, double afterCollection) {
      return !(raw < 0.0) && !(afterCollection < 0.0) ? Math.min(raw, afterCollection) : raw;
   }

   public static boolean shouldHold(boolean currentlyHeld, long thresholdPercent) {
      return shouldHold(currentlyHeld, thresholdPercent, usedPercent(), readPools(), System.currentTimeMillis());
   }

   static boolean shouldHold(boolean currentlyHeld, long thresholdPercent, double used) {
      return shouldHold(currentlyHeld, thresholdPercent, used, null, 0L);
   }

   static boolean shouldHold(boolean currentlyHeld, long thresholdPercent, double used, HeapPressure.Pools pools, long now) {
      if (thresholdPercent <= 0L || used < 0.0) {
         clear();
         return false;
      }

      if (currentlyHeld) {
         double resumeAt = Math.max(50.0, thresholdPercent - 15L);
         if (used <= resumeAt) {
            clear();
            return false;
         }

         if (pools != null && snapAfterUsed != null && snapAfterUsed.length == pools.afterUsed.length) {
            long stale = staleBytes(pools);
            lastStaleBytes = stale;
            if (stale == 0L) {
               snapshot(pools);
               clockStart = now;
               return true;
            } else if (now - clockStart >= 10000L) {
               clear();
               releasedBlind = true;
               return false;
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else if (used >= thresholdPercent) {
         consecutiveHigh++;
         if (consecutiveHigh >= 3) {
            holdStart = Math.max(1L, now);
            clockStart = holdStart;
            lastStaleBytes = -1L;
            if (pools != null) {
               snapshot(pools);
            } else {
               snapAfterUsed = null;
               snapAfterCommitted = null;
            }

            return true;
         } else {
            return false;
         }
      } else {
         consecutiveHigh = 0;
         return false;
      }
   }

   static long staleBytes(HeapPressure.Pools pools) {
      long significant = pools.max / 100L * 5L;
      long stale = 0L;

      for (int i = 0; i < pools.currentUsed.length; i++) {
         if (pools.currentUsed[i] >= significant && pools.afterUsed[i] == snapAfterUsed[i] && pools.afterCommitted[i] == snapAfterCommitted[i]) {
            stale += pools.currentUsed[i];
         }
      }

      return stale;
   }

   private static void snapshot(HeapPressure.Pools pools) {
      snapAfterUsed = (long[])pools.afterUsed.clone();
      snapAfterCommitted = (long[])pools.afterCommitted.clone();
   }

   public static boolean consumeReleasedBlind() {
      boolean was = releasedBlind;
      releasedBlind = false;
      return was;
   }

   public static boolean isHolding() {
      return holdStart != 0L;
   }

   public static long heldMillis(long now) {
      return holdStart == 0L ? 0L : Math.max(0L, now - holdStart);
   }

   public static long waitingMillis(long now) {
      return clockStart == 0L ? 0L : Math.max(0L, now - clockStart);
   }

   public static long staleMegabytes() {
      return lastStaleBytes < 0L ? -1L : lastStaleBytes / 1048576L;
   }

   private static void clear() {
      consecutiveHigh = 0;
      holdStart = 0L;
      clockStart = 0L;
      snapAfterUsed = null;
      snapAfterCommitted = null;
   }

   public static void reset() {
      clear();
      releasedBlind = false;
      lastStaleBytes = -1L;
   }

   static int consecutiveHigh() {
      return consecutiveHigh;
   }

   static final class Pools {
      final long max;
      final long[] afterUsed;
      final long[] afterCommitted;
      final long[] currentUsed;

      Pools(long max, long[] afterUsed, long[] afterCommitted, long[] currentUsed) {
         this.max = max;
         this.afterUsed = afterUsed;
         this.afterCommitted = afterCommitted;
         this.currentUsed = currentUsed;
      }
   }
}
