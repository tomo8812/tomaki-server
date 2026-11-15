package maki.chunksmithcontroller;

import java.util.concurrent.atomic.AtomicLong;

public final class WorkTelemetry {
   private static final long[] BOUNDS = new long[]{1L, 4L, 16L, 64L, 256L, 1000L, 4000L, 16000L, 60000L, Long.MAX_VALUE};
   private final long[] histogram = new long[BOUNDS.length];
   private long completions;
   private final AtomicLong observed = new AtomicLong();
   public volatile WorkTelemetry.NativeState state = new WorkTelemetry.NativeState(0L, 0, 0, -1L, false, false, false, -1L);

   public boolean due() {
      long now = System.nanoTime();
      long prev = this.observed.get();
      return (prev == 0L || now - prev >= 250000000L) && this.observed.compareAndSet(prev, now);
   }

   public void observe(int width, int inFlight, long writes, boolean writeHeld, boolean residencyHeld, boolean heapHeld, long added) {
      this.state = new WorkTelemetry.NativeState(System.nanoTime(), width, inFlight, writes, writeHeld, residencyHeld, heapHeld, added);
   }

   public synchronized void completed(long millis) {
      this.completions++;
      int i = 0;

      while (i < BOUNDS.length - 1 && millis > BOUNDS[i]) {
         i++;
      }

      this.histogram[i]++;
   }

   public synchronized WorkTelemetry.Batch drain() {
      long count = this.completions;
      this.completions = 0L;
      long need = (long)Math.ceil(count * 0.95);
      long sum = 0L;
      long p95 = 0L;

      for (int i = 0; i < this.histogram.length; i++) {
         sum += this.histogram[i];
         if (p95 == 0L && need > 0L && sum >= need) {
            p95 = BOUNDS[i];
         }

         this.histogram[i] = 0L;
      }

      return new WorkTelemetry.Batch(count, p95);
   }

   public record Batch(long completions, double latencyP95Ms) {
   }

   public record NativeState(long time, int width, int inFlight, long worldWrites, boolean writeHeld, boolean residencyHeld, boolean heapHeld, long addedChunks) {
      public boolean held() {
         return this.writeHeld || this.residencyHeld || this.heapHeld;
      }
   }
}
