package maki.statusio;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public final class StatusIo {
   private static final Logger LOG = System.getLogger("MakiStatusIo");
   private static final StatusIoEngine ENGINE = new StatusIoEngine("maki-status-io", 16, 100L, StatusIo::writeSnapshot, LOG);

   private StatusIo() {
   }

   public static boolean publish(Path destination, String contents) {
      boolean accepted = ENGINE.publish(destination, contents);
      if (!accepted) {
         LOG.log(Level.WARNING, "Diagnostic status queue is full; dropping snapshot for " + destination);
      }

      return accepted;
   }

   public static boolean flush(long timeoutMillis) {
      boolean flushed = ENGINE.flush(timeoutMillis);
      if (!flushed) {
         LOG.log(
            Level.WARNING,
            "Timed out flushing diagnostic status snapshots after " + timeoutMillis + "ms; the last successfully published files remain authoritative."
         );
      }

      return flushed;
   }

   static StatusIoEngine.WriteStats writeSnapshot(Path destination, String contents) throws Exception {
      Path parent = destination.getParent();
      if (parent != null) {
         Files.createDirectories(parent);
      }

      Path tmp = destination.resolveSibling(destination.getFileName().toString() + ".tmp");
      long writeStarted = System.nanoTime();
      Files.writeString(tmp, contents, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
      long writeNanos = System.nanoTime() - writeStarted;
      long moveStarted = System.nanoTime();

      try {
         Files.move(tmp, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException e) {
         Files.move(tmp, destination, StandardCopyOption.REPLACE_EXISTING);
      }

      long moveNanos = System.nanoTime() - moveStarted;
      return new StatusIoEngine.WriteStats(writeNanos, moveNanos);
   }
}
