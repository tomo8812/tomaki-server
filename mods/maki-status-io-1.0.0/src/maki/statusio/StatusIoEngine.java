package maki.statusio;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Map.Entry;

final class StatusIoEngine {
   private final Object lock = new Object();
   private final LinkedHashMap<Path, StatusIoEngine.Pending> pending = new LinkedHashMap<>();
   private final int maxDestinations;
   private final StatusIoEngine.Sink sink;
   private final Logger log;
   private final long slowNanos;
   private long sequence;
   private boolean active;

   StatusIoEngine(String threadName, int maxDestinations, long slowMillis, StatusIoEngine.Sink sink, Logger log) {
      if (maxDestinations < 1) {
         throw new IllegalArgumentException("maxDestinations must be positive");
      }

      this.maxDestinations = maxDestinations;
      this.sink = Objects.requireNonNull(sink, "sink");
      this.log = Objects.requireNonNull(log, "log");
      this.slowNanos = Math.max(0L, slowMillis) * 1000000L;
      Thread worker = new Thread(this::run, threadName);
      worker.setDaemon(true);
      worker.setPriority(1);
      worker.start();
   }

   boolean publish(Path destination, String contents) {
      Path normalized = Objects.requireNonNull(destination, "destination").toAbsolutePath().normalize();
      Objects.requireNonNull(contents, "contents");
      synchronized (this.lock) {
         if (!this.pending.containsKey(normalized) && this.pending.size() >= this.maxDestinations) {
            return false;
         }

         this.pending.put(normalized, new StatusIoEngine.Pending(++this.sequence, contents));
         this.lock.notifyAll();
         return true;
      }
   }

   boolean flush(long timeoutMillis) {
      long deadline = System.nanoTime() + Math.max(0L, timeoutMillis) * 1000000L;
      synchronized (this.lock) {
         while (this.active || !this.pending.isEmpty()) {
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0L) {
               return false;
            }

            try {
               this.lock.wait(remaining / 1000000L, (int)(remaining % 1000000L));
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
         StatusIoEngine.Pending snapshot;
         synchronized (this.lock) {
            while (this.pending.isEmpty()) {
               try {
                  this.lock.wait();
               } catch (InterruptedException var23) {
               }
            }

            Iterator<Entry<Path, StatusIoEngine.Pending>> iterator = this.pending.entrySet().iterator();
            Entry<Path, StatusIoEngine.Pending> entry = iterator.next();
            destination = entry.getKey();
            snapshot = entry.getValue();
            iterator.remove();
            this.active = true;
         }

         long started = System.nanoTime();

         try {
            StatusIoEngine.WriteStats stats = this.sink.write(destination, snapshot.contents());
            long total = System.nanoTime() - started;
            if (total >= this.slowNanos) {
               this.log
                  .log(
                     Level.WARNING,
                     "Slow diagnostic status I/O for "
                        + destination
                        + ": total="
                        + millis(total)
                        + "ms, write="
                        + millis(stats.writeNanos())
                        + "ms, move="
                        + millis(stats.moveNanos())
                        + "ms, sequence="
                        + snapshot.sequence()
                  );
            }
         } catch (Throwable error) {
            this.log.log(Level.WARNING, "Could not publish diagnostic status snapshot " + destination + " (sequence " + snapshot.sequence() + ")", error);
         } finally {
            synchronized (this.lock) {
               this.active = false;
               this.lock.notifyAll();
            }
         }
      }
   }

   private static String millis(long nanos) {
      return String.format(Locale.ROOT, "%.1f", nanos / 1000000.0);
   }

   private record Pending(long sequence, String contents) {
   }

   interface Sink {
      StatusIoEngine.WriteStats write(Path var1, String var2) throws Exception;
   }

   record WriteStats(long writeNanos, long moveNanos) {
   }
}
