package maki.chunksmithcache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

final class BackfillRetries {
   private static final int LIMIT = 4096;
   private final Map<BackfillRetries.Key, BackfillRetries.Entry> entries = new LinkedHashMap<>();
   private final Map<BackfillRetries.Key, BackfillRetries.Entry> pendingBeforeLoad = new LinkedHashMap<>();
   private boolean loaded;

   synchronized boolean defer(String dimension, long pos, long now, String reason) {
      BackfillRetries.Key key = new BackfillRetries.Key(dimension, pos);
      BackfillRetries.Entry previous = this.pendingBeforeLoad.get(key);
      if (previous != null) {
         this.pendingBeforeLoad.put(key, next(previous, now, reason));
         return true;
      }

      previous = this.entries.get(key);
      if (previous != null) {
         this.entries.put(key, next(previous, now, reason));
         return true;
      }

      Map<BackfillRetries.Key, BackfillRetries.Entry> target = this.loaded ? this.entries : this.pendingBeforeLoad;
      if (target.size() >= 4096) {
         return false;
      }

      target.put(key, next(null, now, reason));
      return true;
   }

   private static BackfillRetries.Entry next(BackfillRetries.Entry previous, long now, String reason) {
      int attempts = previous == null ? 1 : Math.min(16, previous.attempts + 1);
      long delay = Math.min(300000L, 5000L << Math.min(6, attempts - 1));
      return new BackfillRetries.Entry(attempts, now + delay, reason);
   }

   synchronized boolean waiting(String dimension, long pos, long now) {
      BackfillRetries.Key key = new BackfillRetries.Key(dimension, pos);
      BackfillRetries.Entry entry = this.pendingBeforeLoad.get(key);
      if (entry == null) {
         entry = this.entries.get(key);
      }

      return entry != null && entry.retryAt > now;
   }

   synchronized List<BackfillRetries.Key> due(String dimension, long now) {
      LinkedHashMap<BackfillRetries.Key, BackfillRetries.Entry> combined = new LinkedHashMap<>(this.entries);
      this.pendingBeforeLoad.forEach(combined::put);
      return combined.entrySet()
         .stream()
         .filter(e -> e.getKey().dimension.equals(dimension) && e.getValue().retryAt <= now)
         .sorted(Comparator.comparingLong(e -> e.getValue().retryAt))
         .limit(4L)
         .map(Map.Entry::getKey)
         .toList();
   }

   synchronized void complete(String dimension, long pos) {
      BackfillRetries.Key key = new BackfillRetries.Key(dimension, pos);
      this.entries.remove(key);
      this.pendingBeforeLoad.remove(key);
   }

   synchronized boolean load(Path path) {
      if (this.loaded) {
         return true;
      }

      Properties source = new Properties();

      try (InputStream input = Files.newInputStream(path)) {
         source.load(input);
      } catch (NoSuchFileException missing) {
         this.loaded = true;
         this.mergePending();
         return true;
      } catch (IOException | RuntimeException unreadable) {
         return false;
      }

      if (source.size() > 4096) {
         return false;
      }

      LinkedHashMap<BackfillRetries.Key, BackfillRetries.Entry> disk = new LinkedHashMap<>();

      for (String key : source.stringPropertyNames()) {
         try {
            int slash = key.lastIndexOf(47);
            if (slash <= 0 || slash == key.length() - 1) {
               return false;
            }

            String[] value = source.getProperty(key).split(",", 3);
            if (value.length != 3) {
               return false;
            }

            disk.put(
               new BackfillRetries.Key(key.substring(0, slash), Long.parseLong(key.substring(slash + 1))),
               new BackfillRetries.Entry(
                  Math.max(1, Math.min(16, Integer.parseInt(value[0]))), Math.min(System.currentTimeMillis() + 300000L, Long.parseLong(value[1])), value[2]
               )
            );
         } catch (RuntimeException malformed) {
            return false;
         }
      }

      this.entries.clear();
      this.entries.putAll(disk);
      this.loaded = true;
      this.mergePending();
      return true;
   }

   private void mergePending() {
      Iterator<Map.Entry<BackfillRetries.Key, BackfillRetries.Entry>> iterator = this.pendingBeforeLoad.entrySet().iterator();

      while (iterator.hasNext()) {
         Map.Entry<BackfillRetries.Key, BackfillRetries.Entry> pending = iterator.next();
         BackfillRetries.Entry persisted = this.entries.get(pending.getKey());
         if (persisted != null) {
            BackfillRetries.Entry current = pending.getValue();
            this.entries
               .put(
                  pending.getKey(),
                  new BackfillRetries.Entry(Math.max(persisted.attempts, current.attempts), Math.max(persisted.retryAt, current.retryAt), current.reason)
               );
            iterator.remove();
         } else if (this.entries.size() < 4096) {
            this.entries.put(pending.getKey(), pending.getValue());
            iterator.remove();
         }
      }
   }

   synchronized void save(Path path) throws IOException {
      if (!this.loaded) {
         throw new IOException("Retry ledger has not been read successfully; refusing to replace unresolved persisted debt");
      }

      this.mergePending();
      if (!this.pendingBeforeLoad.isEmpty()) {
         throw new IOException(
            "Retry ledger capacity is full; refusing a partial save while " + this.pendingBeforeLoad.size() + " pre-load debt entries remain pending"
         );
      }

      Properties output = new Properties();
      this.entries.forEach((key, value) -> output.setProperty(key.dimension + "/" + key.pos, value.attempts + "," + value.retryAt + "," + value.reason));
      Files.createDirectories(path.getParent());
      Path temporary = path.resolveSibling(path.getFileName() + ".tmp");

      try (OutputStream stream = Files.newOutputStream(temporary)) {
         output.store(stream, "Backfill retries");
      }

      try {
         Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException unsupported) {
         Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   record Entry(int attempts, long retryAt, String reason) {
   }

   record Key(String dimension, long pos) {
   }
}
