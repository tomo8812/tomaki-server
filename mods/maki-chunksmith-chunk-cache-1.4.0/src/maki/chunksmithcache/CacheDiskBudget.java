package maki.chunksmithcache;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

final class CacheDiskBudget {
   private static final long DEFAULT_MAX = 21474836480L;
   private static final long DEFAULT_RESERVE = 2147483648L;
   private static final Map<Path, CacheDiskBudget> ROOTS = new ConcurrentHashMap<>();
   private final Path root;
   private long observedAt;
   private long inventoriedAt;
   private long activeBytes;
   private long usableBytes;
   private long reservedBytes;
   private long additions;
   private long inventoryEpoch;
   private long maxBytes = 21474836480L;
   private long minimumFree = 2147483648L;
   private String observationFailure = "cache-disk-warming-up";

   private CacheDiskBudget(Path root) {
      this.root = root;
   }

   static CacheDiskBudget at(Path root) {
      return ROOTS.computeIfAbsent(root.toAbsolutePath().normalize(), CacheDiskBudget::new);
   }

   void refresh(Path config) {
      try {
         Properties properties = new Properties();
         if (Files.isRegularFile(config)) {
            try (InputStream input = Files.newInputStream(config)) {
               properties.load(input);
            }
         }

         long max = positive(properties, "cache.max-active-bytes", 21474836480L);
         long reserve = positive(properties, "cache.minimum-free-bytes", 2147483648L);
         Files.createDirectories(this.root);
         long free = Files.getFileStore(this.root).getUsableSpace();
         long now = System.nanoTime();
         boolean inventory;
         long additionsBefore;
         long epoch;
         synchronized (this) {
            inventory = this.inventoriedAt == 0L || now - this.inventoriedAt > 900000000000L;
            additionsBefore = this.additions;
            epoch = this.inventoryEpoch;
         }

         long bytes = 0L;
         if (inventory) {
            try (Stream<Path> walk = Files.walk(this.root)) {
               Iterator<Path> files = walk.filter(x$0 -> Files.isRegularFile(x$0)).iterator();

               while (files.hasNext()) {
                  Path file = files.next();
                  boolean quarantine = false;

                  for (Path segment : this.root.relativize(file)) {
                     if (segment.toString().equals(".quarantine") || segment.toString().equals(".corrupt")) {
                        quarantine = true;
                        break;
                     }
                  }

                  if (!quarantine) {
                     bytes = Math.addExact(bytes, Files.size(file));
                  }
               }
            }
         }

         synchronized (this) {
            this.maxBytes = max;
            this.minimumFree = reserve;
            this.usableBytes = free;
            if (inventory) {
               this.activeBytes = Math.addExact(bytes, this.additions - additionsBefore);
               this.inventoriedAt = this.inventoryEpoch == epoch ? now : 0L;
            }

            this.observedAt = now;
            this.observationFailure = null;
         }
      } catch (Exception error) {
         synchronized (this) {
            this.observationFailure = "cache-disk-unavailable";
         }
      }
   }

   private static long positive(Properties properties, String key, long fallback) {
      String value = properties.getProperty(key);
      if (value == null) {
         return fallback;
      } else {
         long parsed = Long.parseLong(value.trim());
         if (parsed <= 0L) {
            throw new IllegalArgumentException(key + " must be positive");
         } else {
            return parsed;
         }
      }
   }

   synchronized String pressure(long additional) {
      if (this.observationFailure != null) {
         return this.observationFailure;
      } else if (System.nanoTime() - this.observedAt > 60000000000L) {
         return "cache-disk-measurement-stale";
      } else if (additional > this.maxBytes - this.activeBytes - this.reservedBytes) {
         return "cache-active-capacity";
      } else {
         return additional > this.usableBytes - this.minimumFree - this.reservedBytes ? "cache-disk-reserve" : null;
      }
   }

   synchronized CacheDiskBudget.Lease tryAcquire(long estimate) {
      if (estimate > 0L && this.pressure(estimate) == null) {
         this.reservedBytes += estimate;
         return new CacheDiskBudget.Lease(estimate);
      } else {
         return null;
      }
   }

   static String pressure(Collection<Path> roots) {
      if (roots.isEmpty()) {
         return "cache-disk-warming-up";
      }

      for (Path root : roots) {
         CacheDiskBudget budget = at(root);
         String reason = budget.pressure(1048576L);
         if (reason != null) {
            return reason;
         }
      }

      return null;
   }

   synchronized void deleted(long bytes) {
      this.activeBytes = Math.max(0L, this.activeBytes - bytes);
   }

   synchronized void invalidateInventory() {
      this.inventoryEpoch++;
      this.inventoriedAt = 0L;
   }

   synchronized long activeBytes() {
      return this.activeBytes;
   }

   synchronized long usableBytes() {
      return this.usableBytes;
   }

   final class Lease implements AutoCloseable {
      private long bytes;
      private boolean closed;

      private Lease(long bytes) {
         this.bytes = bytes;
      }

      boolean resize(long next) {
         synchronized (CacheDiskBudget.this) {
            if (!this.closed && next > 0L && (next <= this.bytes || CacheDiskBudget.this.pressure(next - this.bytes) == null)) {
               CacheDiskBudget.this.reservedBytes = CacheDiskBudget.this.reservedBytes + (next - this.bytes);
               this.bytes = next;
               return true;
            } else {
               return false;
            }
         }
      }

      void published(long addedBytes) {
         synchronized (CacheDiskBudget.this) {
            CacheDiskBudget.this.activeBytes = Math.max(0L, CacheDiskBudget.this.activeBytes + addedBytes);
            CacheDiskBudget.this.additions = CacheDiskBudget.this.additions + Math.max(0L, addedBytes);
            CacheDiskBudget.this.usableBytes = Math.max(0L, CacheDiskBudget.this.usableBytes - Math.max(0L, addedBytes));
         }
      }

      @Override
      public void close() {
         synchronized (CacheDiskBudget.this) {
            if (!this.closed) {
               this.closed = true;
               CacheDiskBudget.this.reservedBytes = CacheDiskBudget.this.reservedBytes - this.bytes;
            }
         }
      }
   }
}
