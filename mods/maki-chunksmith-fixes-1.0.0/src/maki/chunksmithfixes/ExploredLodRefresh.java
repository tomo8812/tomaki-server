package maki.chunksmithfixes;

import com.kishku7.chunksmith.lod.CsLodChunk;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.slf4j.LoggerFactory;

public final class ExploredLodRefresh {
   private static final Map<Path, ExploredLodRefresh.Journal> JOURNALS = new HashMap<>();
   private static final IdentityHashMap<CsLodChunk, ExploredLodRefresh.Request> RECORDS = new IdentityHashMap<>();
   private static final ThreadLocal<ExploredLodRefresh.Request> CAPTURE = new ThreadLocal<>();

   private ExploredLodRefresh() {
   }

   private static long key(int x, int z) {
      return (long)x << 32 | z & 4294967295L;
   }

   private static ExploredLodRefresh.Journal journal(Path root) {
      return JOURNALS.computeIfAbsent(root.toAbsolutePath().normalize(), ExploredLodRefresh.Journal::new);
   }

   public static synchronized CompletableFuture<Void> flush(Path root) {
      return journal(root).receipts.flush();
   }

   public static synchronized boolean needsRefresh(Path root, int x, int z) {
      return journal(root).receipts.contains(key(x, z));
   }

   public static synchronized boolean pending(Path root, int x, int z) {
      return journal(root).pending.containsKey(key(x, z));
   }

   public static CompletableFuture<Void> capture(Path root, int x, int z, Runnable offer) {
      ExploredLodRefresh.Request request;
      synchronized (ExploredLodRefresh.class) {
         ExploredLodRefresh.Journal journal = journal(root);
         long key = key(x, z);
         request = new ExploredLodRefresh.Request(journal, key);
         journal.receipts.change(key, true);
      }

      ExploredLodRefresh.Request previous = CAPTURE.get();
      CAPTURE.set(request);

      try {
         offer.run();
      } catch (RuntimeException error) {
         request.saved.completeExceptionally(error);
      } finally {
         if (previous == null) {
            CAPTURE.remove();
         } else {
            CAPTURE.set(previous);
         }

         if (!request.submitted) {
            request.saved.completeExceptionally(new IllegalStateException("LOD snapshot was not submitted"));
         }
      }

      return request.saved;
   }

   public static synchronized void request(Path root, int x, int z) {
      journal(root).receipts.change(key(x, z), true);
   }

   public static synchronized void resolved(Path root, int x, int z) {
      journal(root).receipts.change(key(x, z), false);
   }

   public static synchronized Set<Long> targets(Path root) {
      return journal(root).receipts.keys();
   }

   public static synchronized CompletableFuture<Void> submitted(Path root, CsLodChunk record) {
      ExploredLodRefresh.Journal journal = journal(root);
      long key = key(record.getChunkX(), record.getChunkZ());
      ExploredLodRefresh.Request request = CAPTURE.get();
      if (request != null && request.journal == journal && request.key == key) {
         request.submitted = true;
      } else {
         request = new ExploredLodRefresh.Request(journal, key);
      }

      journal.pending.put(key, request);
      RECORDS.put(record, request);
      return request.saved;
   }

   public static void completed(CsLodChunk record, LodWriteEvents.Outcome outcome) {
      ExploredLodRefresh.Request request;
      synchronized (ExploredLodRefresh.class) {
         request = RECORDS.remove(record);
         if (request == null) {
            return;
         }

         request.journal.pending.remove(request.key, request);
      }

      if (outcome == LodWriteEvents.Outcome.FAILED) {
         request.saved.completeExceptionally(new IllegalStateException("LOD write failed"));
      } else {
         request.saved.complete(null);
      }
   }

   public static void clear() {
      List<ExploredLodRefresh.Journal> journals;
      synchronized (ExploredLodRefresh.class) {
         journals = new ArrayList<>(JOURNALS.values());
         JOURNALS.clear();
         RECORDS.clear();
         CAPTURE.remove();
      }

      for (ExploredLodRefresh.Journal journal : journals) {
         try {
            journal.receipts.close();
         } catch (CompletionException error) {
            LoggerFactory.getLogger("MakiChunksmithFixes").error("Could not flush LOD refresh receipts", error);
         }
      }
   }

   private static final class Journal {
      final AsyncLodJournal receipts;
      final Map<Long, ExploredLodRefresh.Request> pending = new HashMap<>();

      Journal(Path root) {
         this.receipts = new AsyncLodJournal(root);
      }
   }

   private static final class Request {
      final ExploredLodRefresh.Journal journal;
      final long key;
      boolean submitted;
      final CompletableFuture<Void> saved = new CompletableFuture<>();

      Request(ExploredLodRefresh.Journal journal, long key) {
         this.journal = journal;
         this.key = key;
      }
   }
}
