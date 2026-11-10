package maki.chunksmithcache;

final class LatencyHistogram {
   private static final long[] BOUNDS = new long[]{1L, 4L, 16L, 64L, 256L, 1000L, 4000L, 16000L, 60000L, Long.MAX_VALUE};
   private final long[] buckets = new long[BOUNDS.length];
   private long count;
   private long maxNanos;
   private long overSecond;

   void record(long nanos) {
      nanos = Math.max(0L, nanos);
      int i = 0;

      while (i < BOUNDS.length - 1 && nanos > BOUNDS[i] * 1000000L) {
         i++;
      }

      this.buckets[i]++;
      this.count++;
      this.maxNanos = Math.max(this.maxNanos, nanos);
      if (nanos > 1000000000L) {
         this.overSecond++;
      }
   }

   long percentile(double fraction) {
      if (this.count == 0L) {
         return 0L;
      }

      long need = (long)Math.ceil(this.count * fraction);
      long sum = 0L;

      for (int i = 0; i < this.buckets.length; i++) {
         if ((sum += this.buckets[i]) >= need) {
            return BOUNDS[i];
         }
      }

      return Long.MAX_VALUE;
   }

   @Override
   public String toString() {
      return "n="
         + this.count
         + ",p50Upper="
         + this.percentile(0.5)
         + ",p95Upper="
         + this.percentile(0.95)
         + ",max="
         + this.maxNanos / 1000000.0
         + ",over1000="
         + this.overSecond;
   }
}
