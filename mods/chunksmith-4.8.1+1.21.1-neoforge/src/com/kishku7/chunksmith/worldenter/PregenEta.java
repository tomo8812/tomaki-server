package com.kishku7.chunksmith.worldenter;

import java.util.ArrayDeque;
import java.util.Deque;

public final class PregenEta {
   public static final int WINDOW = 8;
   public static final long MIN_SAMPLE_GAP_MILLIS = 1000L;
   private static final double STALLED_RATE = 0.05;
   private final Deque<long[]> samples = new ArrayDeque<>();

   public void sample(long nowMillis, long chunksDone) {
      long[] last = this.samples.peekLast();
      if (last == null || nowMillis - last[0] >= 1000L) {
         this.samples.addLast(new long[]{nowMillis, chunksDone});

         while (this.samples.size() > 8) {
            this.samples.removeFirst();
         }
      }
   }

   public double ratePerSecond() {
      if (this.samples.size() < 2) {
         return 0.0;
      }

      long[] first = this.samples.peekFirst();
      long[] last = this.samples.peekLast();
      long millis = last[0] - first[0];
      long chunks = last[1] - first[1];
      return millis > 0L && chunks > 0L ? chunks * 1000.0 / millis : 0.0;
   }

   public long secondsRemaining(long chunksDone, long chunksTotal) {
      if (chunksTotal > 0L && chunksDone < chunksTotal) {
         double rate = this.ratePerSecond();
         return rate < 0.05 ? -1L : (long)Math.ceil((chunksTotal - chunksDone) / rate);
      } else {
         return -1L;
      }
   }

   public String describe(long chunksDone, long chunksTotal) {
      long seconds = this.secondsRemaining(chunksDone, chunksTotal);
      return seconds < 0L ? "estimating..." : "about " + humanTime(seconds) + " remaining";
   }

   public static double fraction(long chunksDone, long chunksTotal) {
      if (chunksTotal <= 0L) {
         return 0.0;
      } else {
         return chunksDone >= chunksTotal ? 1.0 : (double)chunksDone / chunksTotal;
      }
   }

   public static String humanTime(long seconds) {
      if (seconds < 0L) {
         return "unknown";
      }

      if (seconds < 60L) {
         return "less than a minute";
      }

      long minutes = seconds / 60L;
      if (minutes < 60L) {
         return minutes + (minutes == 1L ? " minute" : " minutes");
      }

      long hours = minutes / 60L;
      long restMinutes = minutes % 60L;
      String h = hours + (hours == 1L ? " hour" : " hours");
      return restMinutes == 0L ? h : h + " " + restMinutes + (restMinutes == 1L ? " minute" : " minutes");
   }

   public void reset() {
      this.samples.clear();
   }
}
