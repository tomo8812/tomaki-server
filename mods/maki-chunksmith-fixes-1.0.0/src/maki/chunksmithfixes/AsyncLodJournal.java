package maki.chunksmithfixes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class AsyncLodJournal {
   private final Path file;
   private final ExecutorService io = Executors.newSingleThreadExecutor(action -> {
      Thread thread = new Thread(action, "maki-lod-receipts");
      thread.setDaemon(true);
      return thread;
   });
   private final Set<Long> debt = new HashSet<>();
   private final Set<Long> diskDebt = new HashSet<>();
   private final Map<Long, Boolean> overrides = new HashMap<>();
   private final Map<Long, CompletableFuture<Void>> markers = new HashMap<>();
   private boolean loaded;
   private boolean ioInitialized;
   private boolean repair;
   private int entries;
   private CompletableFuture<Void> tail;

   AsyncLodJournal(Path root) {
      this.file = root.resolve("maki-lod-refresh.pending");
      this.tail = CompletableFuture.runAsync(this::load, this.io);
   }

   private void load() {
      try {
         this.diskDebt.clear();
         this.entries = 0;
         if (Files.exists(this.file)) {
            try (DataInputStream in = new DataInputStream(Files.newInputStream(this.file))) {
               while (true) {
                  try {
                     boolean add = in.readBoolean();
                     long key = in.readLong();
                     if (add) {
                        this.diskDebt.add(key);
                     } else {
                        this.diskDebt.remove(key);
                     }

                     this.entries++;
                  } catch (EOFException end) {
                     break;
                  }
               }
            }

            this.compact();
         }

         Files.deleteIfExists(this.file.resolveSibling(this.file.getFileName() + ".tmp"));
         this.ioInitialized = true;
         synchronized (this) {
            this.debt.addAll(this.diskDebt);
            this.overrides.forEach((keyx, add) -> {
               if (add) {
                  this.debt.add(keyx);
               } else {
                  this.debt.remove(keyx);
               }
            });
            this.overrides.clear();
            this.loaded = true;
         }
      } catch (IOException error) {
         throw new CompletionException(error);
      }
   }

   synchronized boolean contains(long key) {
      return this.loaded && this.debt.contains(key);
   }

   synchronized Set<Long> keys() {
      return this.loaded ? Set.copyOf(this.debt) : Set.of();
   }

   synchronized void change(long key, boolean add) {
      if (!this.loaded || this.debt.contains(key) != add || this.markers.getOrDefault(key, CompletableFuture.completedFuture(null)).isCompletedExceptionally()) {
         if (add) {
            this.debt.add(key);
         } else {
            this.debt.remove(key);
         }

         if (!this.loaded) {
            this.overrides.put(key, add);
         }

         CompletableFuture<Void> marker = this.tail.handle((unused, error) -> null).thenRunAsync(() -> {
            try {
               if (!this.ioInitialized) {
                  this.load();
               }

               if (this.repair) {
                  this.compact();
                  this.repair = false;
               }

               this.append(key, add);
            } catch (IOException | CompletionException failure) {
               this.repair = this.ioInitialized;
               throw new CompletionException(failure);
            }
         }, this.io);
         this.tail = marker;
         this.markers.put(key, marker);
         marker.whenComplete((unused, error) -> {
            if (error == null) {
               synchronized (this) {
                  this.markers.remove(key, marker);
               }
            }
         });
      }
   }

   synchronized CompletableFuture<Void> flush() {
      return this.tail;
   }

   private void append(long key, boolean add) {
      try {
         if (this.diskDebt.contains(key) != add) {
            Files.createDirectories(this.file.getParent());
            DataOutputStream out = new DataOutputStream(Files.newOutputStream(this.file, StandardOpenOption.CREATE, StandardOpenOption.APPEND));

            try {
               out.writeBoolean(add);
               out.writeLong(key);
            } catch (Throwable var8) {
               try {
                  out.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }

               throw var8;
            }

            out.close();
            if (add) {
               this.diskDebt.add(key);
            } else {
               this.diskDebt.remove(key);
            }

            this.entries++;
            if (this.diskDebt.isEmpty() || this.entries > Math.max(4096L, this.diskDebt.size() * 4L)) {
               this.compact();
            }
         }
      } catch (IOException error) {
         throw new CompletionException(error);
      }
   }

   private void compact() throws IOException {
      if (this.diskDebt.isEmpty()) {
         Files.deleteIfExists(this.file);
         this.entries = 0;
      } else {
         Path tmp = this.file.resolveSibling(this.file.getFileName() + ".tmp");

         try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(tmp))) {
            for (long key : this.diskDebt) {
               out.writeBoolean(true);
               out.writeLong(key);
            }
         }

         Files.move(tmp, this.file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         this.entries = this.diskDebt.size();
      }
   }

   void close() {
      try {
         this.flush().join();
      } finally {
         this.io.shutdown();
      }
   }
}
