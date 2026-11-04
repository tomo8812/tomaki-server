package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.net.CsLodMessages;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class CsLodDownloader {
   private static final int WORKERS = 4;
   Duration requestTimeout = Duration.ofSeconds(60L);
   private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).build();
   private final Path storeRoot;
   private final AtomicBoolean cancelled = new AtomicBoolean();
   private final AtomicLong downloaded = new AtomicLong();
   private final AtomicLong bytes = new AtomicLong();
   private final AtomicLong skipped = new AtomicLong();
   private final AtomicLong failed = new AtomicLong();
   private final AtomicReference<String> firstFailure = new AtomicReference<>();
   private final AtomicLong deadFailures = new AtomicLong();
   private final AtomicBoolean gaveUp = new AtomicBoolean();
   private volatile ExecutorService pool;
   private volatile CsLodManifest manifest;

   public CsLodDownloader(Path storeRoot) {
      this.storeRoot = storeRoot;
   }

   public void download(String host, int port, String token, CsLodMessages.RegionIndex index, Consumer<String> progress) {
      this.cancelled.set(false);
      Path dimDir = CsLodStore.dimensionDir(this.storeRoot, index.dimension());
      if (dimDir == null) {
         progress.accept("LOD: refusing a malformed dimension id from the server");
      } else {
         this.manifest = CsLodManifest.open(this.storeRoot, index.dimension());
         List<CsLodMessages.RegionEntry> wanted = index.regions().stream().filter(entryx -> {
            if (this.haveAlready(dimDir, entryx)) {
               this.skipped.incrementAndGet();
               return false;
            } else {
               return true;
            }
         }).toList();
         progress.accept(
            "LOD: " + index.regions().size() + " regions within my radius, " + this.skipped.get() + " already cached, " + wanted.size() + " to fetch"
         );
         if (!wanted.isEmpty()) {
            String effectiveHost = resolvableHost(host, port);
            if (effectiveHost == null) {
               this.failed.addAndGet(wanted.size());
               progress.accept(
                  "LOD: cannot use the backchannel host \""
                     + host
                     + "\" -- it is not a usable URL host and did not resolve. Falling back to the in-band channel (slower)."
               );
            } else {
               if (!effectiveHost.equals(host)) {
                  progress.accept(
                     "LOD: the backchannel host \""
                        + host
                        + "\" cannot go in a URL directly (an underscore is not legal in a hostname there), so it resolved to "
                        + effectiveHost
                        + "."
                  );
               }

               this.pool = Executors.newFixedThreadPool(4, runnable -> {
                  Thread thread = new Thread(runnable, "chunksmith-lod-download");
                  thread.setDaemon(true);
                  return thread;
               });

               for (CsLodMessages.RegionEntry entry : wanted) {
                  if (this.gaveUp.get() || this.cancelled.get()) {
                     break;
                  }

                  try {
                     this.pool.submit(() -> {
                        if (!this.cancelled.get() && !this.gaveUp.get()) {
                           try {
                              this.fetch(effectiveHost, port, token, index.dimension(), entry);
                              long done = this.downloaded.incrementAndGet();
                              if (done % 25L == 0L) {
                                 progress.accept("LOD: fetched " + done + "/" + wanted.size() + " regions (" + this.bytes.get() / 1024L / 1024L + " MB)");
                              }
                           } catch (InterruptedException e) {
                              this.failed.incrementAndGet();
                              Thread.currentThread().interrupt();
                           } catch (IOException e) {
                              this.failed.incrementAndGet();
                              this.noteFailure(entry, e);
                              if (!(e instanceof CsLodDownloader.ServerBusy)) {
                                 this.countDeadFailure(progress);
                              }
                           } catch (RuntimeException e) {
                              this.failed.incrementAndGet();
                              this.noteFailure(entry, e);
                              this.countDeadFailure(progress);
                           }
                        }
                     });
                  } catch (RejectedExecutionException e) {
                     break;
                  }
               }

               this.pool.shutdown();

               try {
                  if (!this.pool.awaitTermination(30L, TimeUnit.MINUTES)) {
                     progress.accept("LOD: download timed out");
                  }
               } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
               }

               try {
                  this.manifest.save();
               } catch (IOException e) {
                  progress.accept("LOD: could not write the region manifest (" + e + "); these regions will be re-fetched next session");
               }

               progress.accept(
                  "LOD: done. "
                     + this.downloaded.get()
                     + " fetched, "
                     + this.skipped.get()
                     + " cached, "
                     + this.failed.get()
                     + " failed, "
                     + this.bytes.get() / 1024L / 1024L
                     + " MB"
               );
               String why = this.firstFailure.get();
               if (why != null) {
                  progress.accept("LOD: first download failure from " + host + ":" + port + " -- " + why);
               }
            }
         }
      }
   }

   private void countDeadFailure(Consumer<String> progress) {
      if (!this.cancelled.get()) {
         long dead = this.deadFailures.incrementAndGet();
         if (shouldGiveUp(dead, this.downloaded.get()) && this.gaveUp.compareAndSet(false, true)) {
            progress.accept("LOD: the first " + dead + " backchannel downloads all failed and none succeeded; skipping the rest of this pass");
            ExecutorService current = this.pool;
            if (current != null) {
               current.shutdownNow();
            }
         }
      }
   }

   static boolean shouldGiveUp(long deadFailures, long fetched) {
      return fetched == 0L && deadFailures >= 4L;
   }

   public boolean gaveUp() {
      return this.gaveUp.get();
   }

   public boolean wasCancelled() {
      return this.cancelled.get();
   }

   private void noteFailure(CsLodMessages.RegionEntry entry, Exception e) {
      this.firstFailure
         .compareAndSet(
            null, "r." + entry.regionX() + "." + entry.regionZ() + ": " + e.getClass().getName() + (e.getMessage() == null ? "" : ": " + e.getMessage())
         );
   }

   static boolean needsAddressResolution(String host, int port) {
      try {
         return URI.create("http://" + host + ":" + port + "/").getHost() == null;
      } catch (IllegalArgumentException notEvenAUri) {
         return true;
      }
   }

   static String resolvableHost(String host, int port) {
      if (!needsAddressResolution(host, port)) {
         return host;
      }

      try {
         String literal = InetAddress.getByName(host).getHostAddress();
         return literal.indexOf(58) >= 0 ? "[" + literal + "]" : literal;
      } catch (UnknownHostException | SecurityException e) {
         return null;
      }
   }

   public long fetched() {
      return this.downloaded.get();
   }

   public long failed() {
      return this.failed.get();
   }

   public void cancel() {
      this.cancelled.set(true);
      ExecutorService current = this.pool;
      if (current != null) {
         current.shutdownNow();
      }
   }

   public String describe() {
      String why = this.firstFailure.get();
      return "fetched "
         + this.downloaded.get()
         + ", cached "
         + this.skipped.get()
         + ", failed "
         + this.failed.get()
         + ", "
         + this.bytes.get() / 1024L / 1024L
         + " MB"
         + (why == null ? "" : " (first failure: " + why + ")");
   }

   private void fetch(String host, int port, String token, String dimension, CsLodMessages.RegionEntry entry) throws IOException, InterruptedException {
      Path dimDir = CsLodStore.dimensionDir(this.storeRoot, dimension);
      if (dimDir == null) {
         throw new IOException("refusing a malformed dimension id: " + dimension);
      }

      String name = "r." + entry.regionX() + "." + entry.regionZ() + ".cslod";
      URI uri = URI.create("http://" + host + ":" + port + "/lod/" + dimension + "/" + name);
      HttpRequest request = HttpRequest.newBuilder(uri).header("X-Chunksmith-Token", token).timeout(this.requestTimeout).GET().build();
      HttpResponse<InputStream> response = this.http.send(request, BodyHandlers.ofInputStream());
      if (response.statusCode() == 429) {
         response.body().close();
         throw new CsLodDownloader.ServerBusy("HTTP 429 for " + name);
      }

      if (response.statusCode() != 200) {
         response.body().close();
         throw new IOException("HTTP " + response.statusCode() + " for " + name);
      }

      Path target = dimDir.resolve(name);
      Files.createDirectories(target.getParent());
      Path temp = target.resolveSibling(name + ".part");

      try (InputStream in = response.body()) {
         Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
      }

      Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
      long stored = Files.size(target);
      this.bytes.addAndGet(stored);
      this.manifest.put(entry.regionX(), entry.regionZ(), entry.hash(), stored);
   }

   private boolean haveAlready(Path dimDir, CsLodMessages.RegionEntry entry) {
      CsLodManifest current = this.manifest;
      return current != null && current.holds(dimDir, entry);
   }

   static final class ServerBusy extends IOException {
      private static final long serialVersionUID = 1L;

      ServerBusy(String message) {
         super(message);
      }
   }
}
