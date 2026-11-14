package com.kishku7.chunksmith.lod.net;

public final class CsLodRetry {
   public static final long FIRST_DELAY_MILLIS = 15000L;
   public static final long MAX_DELAY_MILLIS = 120000L;
   private final long firstDelayMillis;
   private final long maxDelayMillis;
   private long delayMillis;
   private long lastAttemptMillis;
   private int attempts;

   public CsLodRetry() {
      this(15000L, 120000L);
   }

   public CsLodRetry(long firstDelayMillis, long maxDelayMillis) {
      if (firstDelayMillis > 0L && maxDelayMillis >= firstDelayMillis) {
         this.firstDelayMillis = firstDelayMillis;
         this.maxDelayMillis = maxDelayMillis;
         this.delayMillis = firstDelayMillis;
      } else {
         throw new IllegalArgumentException("a retry delay must be positive and may not exceed its own ceiling");
      }
   }

   public synchronized void started(long nowMillis) {
      this.lastAttemptMillis = nowMillis;
   }

   public synchronized boolean due(long nowMillis) {
      return nowMillis - this.lastAttemptMillis >= this.delayMillis;
   }

   public synchronized void attempted(long nowMillis) {
      this.lastAttemptMillis = nowMillis;
      this.attempts++;
      this.delayMillis = Math.min(this.maxDelayMillis, this.delayMillis * 2L);
   }

   public synchronized void reset() {
      this.delayMillis = this.firstDelayMillis;
      this.lastAttemptMillis = 0L;
      this.attempts = 0;
   }

   public synchronized int attempts() {
      return this.attempts;
   }

   public synchronized long delayMillis() {
      return this.delayMillis;
   }
}
