package maki.chunksmithcache;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

final class CachePathLocks {
   private static final ConcurrentHashMap<Path, CachePathLocks.Cell> LOCKS = new ConcurrentHashMap<>();

   private CachePathLocks() {
   }

   static CachePathLocks.Guard lock(Path file) {
      Path key = file.toAbsolutePath().normalize();
      CachePathLocks.Cell cell = LOCKS.compute(key, (ignored, current) -> {
         CachePathLocks.Cell chosen = current == null ? new CachePathLocks.Cell() : current;
         chosen.users++;
         return chosen;
      });
      cell.lock.lock();
      return new CachePathLocks.Guard(key, cell);
   }

   private static final class Cell {
      final ReentrantLock lock = new ReentrantLock();
      int users;
   }

   static final class Guard implements AutoCloseable {
      private final Path key;
      private final CachePathLocks.Cell cell;
      private boolean closed;

      private Guard(Path key, CachePathLocks.Cell cell) {
         this.key = key;
         this.cell = cell;
      }

      @Override
      public void close() {
         if (!this.closed) {
            this.closed = true;
            this.cell.lock.unlock();
            CachePathLocks.LOCKS.compute(this.key, (ignored, current) -> --this.cell.users == 0 ? null : this.cell);
         }
      }
   }
}
