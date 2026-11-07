package maki.chunksmithcache;

final class CaptureBudget {
   private final int maxCount;
   private final long maxBytes;
   private int count;
   private long bytes;

   CaptureBudget(int maxCount, long maxBytes) {
      this.maxCount = maxCount;
      this.maxBytes = maxBytes;
   }

   synchronized CaptureBudget.Lease tryAcquire(long estimate) {
      if (estimate > 0L && this.count < this.maxCount && estimate <= this.maxBytes - this.bytes) {
         this.count++;
         this.bytes += estimate;
         return new CaptureBudget.Lease(estimate);
      } else {
         return null;
      }
   }

   synchronized int count() {
      return this.count;
   }

   synchronized long bytes() {
      return this.bytes;
   }

   synchronized boolean full(long estimate) {
      return this.count >= this.maxCount || estimate > this.maxBytes - this.bytes;
   }

   final class Lease implements AutoCloseable {
      private long reserved;
      private boolean closed;

      private Lease(long reserved) {
         this.reserved = reserved;
      }

      boolean resize(long measured) {
         synchronized (CaptureBudget.this) {
            if (!this.closed && measured > 0L && measured - this.reserved <= CaptureBudget.this.maxBytes - CaptureBudget.this.bytes) {
               CaptureBudget.this.bytes = CaptureBudget.this.bytes + (measured - this.reserved);
               this.reserved = measured;
               return true;
            } else {
               return false;
            }
         }
      }

      @Override
      public void close() {
         synchronized (CaptureBudget.this) {
            if (!this.closed) {
               this.closed = true;
               CaptureBudget.this.count--;
               CaptureBudget.this.bytes = CaptureBudget.this.bytes - this.reserved;
            }
         }
      }
   }
}
