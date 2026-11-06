package maki.statusio;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

final class StatusIoEngine {
    interface Sink {
        WriteStats write(Path destination, String contents) throws Exception;
    }
    record WriteStats(long writeNanos, long moveNanos) {}
    private record Pending(long sequence, String contents) {}

    private final Object lock = new Object();
    private final LinkedHashMap<Path, Pending> pending = new LinkedHashMap<>();
    private final int maxDestinations;
    private final Sink sink;
    private final System.Logger log;
    private final long slowNanos;
    private long sequence;
    private boolean active;

    StatusIoEngine(String threadName, int maxDestinations, long slowMillis, Sink sink, System.Logger log) {
        if (maxDestinations < 1) throw new IllegalArgumentException("maxDestinations must be positive");
        this.maxDestinations = maxDestinations;
        this.sink = Objects.requireNonNull(sink, "sink");
        this.log = Objects.requireNonNull(log, "log");
        this.slowNanos = Math.max(0L, slowMillis) * 1_000_000L;
        Thread worker = new Thread(this::run, threadName);
        worker.setDaemon(true);
        worker.setPriority(Thread.MIN_PRIORITY);
        worker.start();
    }

    boolean publish(Path destination, String contents) {
        Path normalized = Objects.requireNonNull(destination, "destination").toAbsolutePath().normalize();
        Objects.requireNonNull(contents, "contents");
        synchronized (lock) {
            if (!pending.containsKey(normalized) && pending.size() >= maxDestinations) return false;
            pending.put(normalized, new Pending(++sequence, contents));
            lock.notifyAll();
            return true;
        }
    }

    boolean flush(long timeoutMillis) {
        long deadline = System.nanoTime() + Math.max(0L, timeoutMillis) * 1_000_000L;
        synchronized (lock) {
            while (active || !pending.isEmpty()) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0L) return false;
                try {
                    lock.wait(remaining / 1_000_000L, (int)(remaining % 1_000_000L));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return true;
        }
    }

    private void run() {
        while (true) {
            Path destination;
            Pending snapshot;
            synchronized (lock) {
                while (pending.isEmpty()) {
                    try { lock.wait(); } catch (InterruptedException ignored) {}
                }
                Iterator<Map.Entry<Path, Pending>> iterator = pending.entrySet().iterator();
                Map.Entry<Path, Pending> entry = iterator.next();
                destination = entry.getKey();
                snapshot = entry.getValue();
                iterator.remove();
                active = true;
            }
            long started = System.nanoTime();
            try {
                WriteStats stats = sink.write(destination, snapshot.contents());
                long total = System.nanoTime() - started;
                if (total >= slowNanos) {
                    log.log(System.Logger.Level.WARNING,
                        "Slow diagnostic status I/O for " + destination
                            + ": total=" + millis(total) + "ms"
                            + ", write=" + millis(stats.writeNanos()) + "ms"
                            + ", move=" + millis(stats.moveNanos()) + "ms"
                            + ", sequence=" + snapshot.sequence());
                }
            } catch (Throwable error) {
                log.log(System.Logger.Level.WARNING,
                    "Could not publish diagnostic status snapshot " + destination
                        + " (sequence " + snapshot.sequence() + ")", error);
            } finally {
                synchronized (lock) {
                    active = false;
                    lock.notifyAll();
                }
            }
        }
    }

    private static String millis(long nanos) {
        return String.format(java.util.Locale.ROOT, "%.1f", nanos / 1_000_000.0);
    }
}
