package com.kishku7.chunksmith.util;

import com.kishku7.chunksmith.platform.ServerEnvironment;

public final class DispatchStats {
   private static volatile int inFlight = -1;
   private static volatile int limit = -1;
   private static volatile int goodWidth = -1;
   private static volatile int ceiling = -1;
   private static volatile int sinkQueue = -1;

   private DispatchStats() {
   }

   public static void publish(int chunksInFlight, int currentLimit, int knownGoodWidth, int configuredMax, int rendererQueue) {
      inFlight = chunksInFlight;
      limit = currentLimit;
      goodWidth = knownGoodWidth;
      ceiling = configuredMax;
      sinkQueue = rendererQueue;
   }

   public static void clear() {
      inFlight = -1;
      limit = -1;
      goodWidth = -1;
      ceiling = -1;
      sinkQueue = -1;
   }

   public static String describe() {
      if (limit < 0) {
         return "no generation task running";
      }

      String known = goodWidth >= ceiling ? "none yet" : Integer.toString(goodWidth);
      return String.format(
         "inFlight=%d width=%d of %d (ceiling) knownGood=%s rendererQueue=%s on=%s",
         inFlight,
         limit,
         ceiling,
         known,
         sinkQueue < 0 ? "n/a" : Integer.toString(sinkQueue),
         ServerEnvironment.isDedicated() ? "dedicated" : "client"
      );
   }
}
