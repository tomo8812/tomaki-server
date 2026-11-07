package maki.millenairelod;

/** Bounds both in-build and queued detached snapshots. Leases are idempotent. */
final class SnapshotBudget {
    private final int maxCount;
    private final long maxBytes;
    private int count;
    private long bytes;
    SnapshotBudget(int maxCount,long maxBytes) { this.maxCount=maxCount; this.maxBytes=maxBytes; }
    synchronized Lease tryAcquire(long estimate) {
        if (estimate<0 || count>=maxCount || estimate>maxBytes-bytes) return null;
        count++; bytes+=estimate; return new Lease(estimate);
    }
    synchronized boolean pressured() { return count>=maxCount*3/4 || bytes>=maxBytes*3/4; }
    synchronized int count() { return count; }
    synchronized long bytes() { return bytes; }
    final class Lease implements AutoCloseable {
        private long size;
        private boolean closed;
        Lease(long size) { this.size=size; }
        boolean resize(long actual) {
            synchronized (SnapshotBudget.this) {
                if (closed || actual<0 || actual-size>maxBytes-bytes) return false;
                bytes+=actual-size; size=actual; return true;
            }
        }
        @Override public void close() {
            synchronized (SnapshotBudget.this) {
                if (!closed) { closed=true; bytes-=size; count--; }
            }
        }
    }
}
