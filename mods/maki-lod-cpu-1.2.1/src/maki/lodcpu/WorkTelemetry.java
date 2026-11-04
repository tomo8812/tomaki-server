package maki.lodcpu;

import java.util.concurrent.atomic.AtomicLong;

public final class WorkTelemetry {
   private static final long[] BOUNDS = new long[]{1L, 4L, 16L, 64L, 256L, 1000L, 4000L, 16000L, 60000L, Long.MAX_VALUE};
   private final long[] histogram = new long[BOUNDS.length];
   private long completions;
   private final AtomicLong observed = new AtomicLong();
   public volatile WorkTelemetry.NativeState state = new WorkTelemetry.NativeState(0L, 0, 0, -1L, false, false, false, -1L);

   public boolean due() {
      long var1 = System.nanoTime();
      long var3 = this.observed.get();
      return (var3 == 0L || var1 - var3 >= 250000000L) && this.observed.compareAndSet(var3, var1);
   }

   public void observe(int var1, int var2, long var3, boolean var5, boolean var6, boolean var7, long var8) {
      this.state = new WorkTelemetry.NativeState(System.nanoTime(), var1, var2, var3, var5, var6, var7, var8);
   }

   public synchronized void completed(long var1) {
      this.completions++;
      int var3 = 0;

      while (var3 < BOUNDS.length - 1 && var1 > BOUNDS[var3]) {
         var3++;
      }

      this.histogram[var3]++;
   }

   public synchronized WorkTelemetry.Batch drain() {
      long var1 = this.completions;
      this.completions = 0L;
      long var3 = (long)Math.ceil(var1 * 0.95);
      long var5 = 0L;
      long var7 = 0L;

      for (int var9 = 0; var9 < this.histogram.length; var9++) {
         var5 += this.histogram[var9];
         if (var7 == 0L && var3 > 0L && var5 >= var3) {
            var7 = BOUNDS[var9];
         }

         this.histogram[var9] = 0L;
      }

      return new WorkTelemetry.Batch(var1, var7);
   }

   public record Batch(long completions, double latencyP95Ms) {
   }

   public record NativeState(long time, int width, int inFlight, long worldWrites, boolean writeHeld, boolean residencyHeld, boolean heapHeld, long addedChunks) {
      public boolean held() {
         return this.writeHeld || this.residencyHeld || this.heapHeld;
      }
   }
}
