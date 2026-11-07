package maki.millenairelod;

final class SnapshotBudget {
   private final int maxCount;
   private final long maxBytes;
   private int count;
   private long bytes;

   SnapshotBudget(int maxCount, long maxBytes) {
      this.maxCount = maxCount;
      this.maxBytes = maxBytes;
   }

   synchronized SnapshotBudget.Lease tryAcquire(long estimate) {
      if (estimate >= 0L && this.count < this.maxCount && estimate <= this.maxBytes - this.bytes) {
         this.count++;
         this.bytes += estimate;
         return new SnapshotBudget.Lease(estimate);
      } else {
         return null;
      }
   }

   synchronized boolean pressured() {
      return this.count >= this.maxCount * 3 / 4 || this.bytes >= this.maxBytes * 3L / 4L;
   }

   synchronized int count() {
      return this.count;
   }

   synchronized long bytes() {
      return this.bytes;
   }

   final class Lease implements AutoCloseable {
      private long size;
      private boolean closed;

      Lease(long size) {
         this.size = size;
      }

      boolean resize(long actual) {
         synchronized (SnapshotBudget.this) {
            if (!this.closed && actual >= 0L && actual - this.size <= SnapshotBudget.this.maxBytes - SnapshotBudget.this.bytes) {
               SnapshotBudget.this.bytes = SnapshotBudget.this.bytes + (actual - this.size);
               this.size = actual;
               return true;
            } else {
               return false;
            }
         }
      }

      @Override
      public void close() {
         synchronized (SnapshotBudget.this) {
            if (!this.closed) {
               this.closed = true;
               SnapshotBudget.this.bytes = SnapshotBudget.this.bytes - this.size;
               SnapshotBudget.this.count--;
            }
         }
      }
   }
}
