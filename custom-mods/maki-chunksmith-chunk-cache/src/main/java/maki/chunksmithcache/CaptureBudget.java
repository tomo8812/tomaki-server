package maki.chunksmithcache;

/** Non-blocking admission. A rejected snapshot remains eligible for a later scan. */
final class CaptureBudget {
    private final int maxCount;
    private final long maxBytes;
    private int count;
    private long bytes;

    CaptureBudget(int maxCount, long maxBytes) {
        this.maxCount = maxCount;
        this.maxBytes = maxBytes;
    }

    synchronized Lease tryAcquire(long estimate) {
        if (estimate <= 0 || count >= maxCount || estimate > maxBytes - bytes) return null;
        count++;
        bytes += estimate;
        return new Lease(estimate);
    }

    synchronized int count() { return count; }
    synchronized long bytes() { return bytes; }
    synchronized boolean full(long estimate) { return count >= maxCount || estimate > maxBytes - bytes; }

    final class Lease implements AutoCloseable {
        private long reserved;
        private boolean closed;
        private Lease(long reserved) { this.reserved = reserved; }

        boolean resize(long measured) {
            synchronized (CaptureBudget.this) {
                if (closed || measured <= 0 || measured - reserved > maxBytes - bytes) return false;
                bytes += measured - reserved;
                reserved = measured;
                return true;
            }
        }

        public void close() {
            synchronized (CaptureBudget.this) {
                if (closed) return;
                closed = true;
                count--;
                bytes -= reserved;
            }
        }
    }
}
