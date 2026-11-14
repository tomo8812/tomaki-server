package com.kishku7.chunksmith.lod.net;

public final class CsLodGrowth {
   public static final long PUSH_INTERVAL_MILLIS = 60000L;
   private static volatile long lastWriteMillis;
   private static long lastPushMillis;

   private CsLodGrowth() {
   }

   public static void noteWrite(long nowMillis) {
      lastWriteMillis = nowMillis;
   }

   public static synchronized boolean pushDue(long nowMillis) {
      long written = lastWriteMillis;
      if (written == 0L || nowMillis - lastPushMillis < 60000L) {
         return false;
      }

      if (lastPushMillis != 0L && written <= lastPushMillis - 10000L) {
         return false;
      }

      lastPushMillis = nowMillis;
      return true;
   }

   public static synchronized void reset() {
      lastWriteMillis = 0L;
      lastPushMillis = 0L;
   }
}
