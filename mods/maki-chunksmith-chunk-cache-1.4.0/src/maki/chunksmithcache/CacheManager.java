package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.lod.HorizonGuard;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheManager {
   private static final Logger LOG = LoggerFactory.getLogger("MakiChunkCache");
   private static final int SCHEMA = 3;
   private static final long SNAPSHOT_ESTIMATE = 1048576L;
   private static final long MAX_ENTRY_BYTES = 16777216L;
   private static final CaptureBudget CAPTURE_BUDGET = new CaptureBudget(64, 67108864L);
   private static final Semaphore INSPECTIONS = new Semaphore(32);
   private static final Set<CacheManager.InspectionPermit> LIVE_INSPECTIONS = ConcurrentHashMap.newKeySet();
   private static final Set<MinecraftServer> STOPPING = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
   private static volatile MinecraftServer currentServer;
   private static volatile List<Path> activeRoots = List.of();
   private static final Map<MinecraftServer, Long> NEXT_DISK_REFRESH = new WeakHashMap<>();
   private static final Set<MinecraftServer> DISK_REFRESHING = ConcurrentHashMap.newKeySet();
   private static final AtomicLong LAST_QUEUE_WARNING = new AtomicLong();
   private static final Set<String> ROTATED = ConcurrentHashMap.newKeySet();
   private static final Map<String, Long> ROTATION_RETRY = new ConcurrentHashMap<>();
   private static final Map<CacheManager.CaptureKey, CacheManager.CaptureFlight> CAPTURE_PENDING = new ConcurrentHashMap<>();
   private static final Map<MinecraftServer, Long> NEXT_MAINTENANCE = new WeakHashMap<>();
   private static final long MAINTENANCE_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15L);
   private static final long MAINTENANCE_RETRY_MS = TimeUnit.MINUTES.toMillis(1L);
   private static final ThreadPoolExecutor WRITE_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128), daemonFactory("maki-chunk-cache-write"), new AbortPolicy()
   );
   private static final ThreadPoolExecutor VALIDATE_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64), daemonFactory("maki-chunk-cache-validate"), new AbortPolicy()
   );
   private static final ThreadPoolExecutor READ_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(32), daemonFactory("maki-chunk-cache-read"), new AbortPolicy()
   );

   private CacheManager() {
   }

   public static void captureFromChunksmith(ServerLevel level, LevelChunk chunk, CompletableFuture<Boolean> lodCommitted) {
      PipelineMetrics.count("direct-full-candidates");
      if (level != null
         && chunk != null
         && level.getServer().isSameThread()
         && level.getServer().getPlayerCount() == 0
         && FollowCaptureOwnership.owns(level)
         && !BackfillGuard.suppress(level, chunk.getPos())
         && HorizonGuard.drop(level, chunk.getPos())) {
         ChunkPos pos = chunk.getPos();
         lodCommitted.whenComplete((saved, error) -> {
            if (error != null || !Boolean.TRUE.equals(saved)) {
               Fingerprint.whenReady(level).thenAccept(fingerprint -> {
                  if (fingerprint != null) {
                     BackfillController.retryDirectLod(level, pos, fingerprint.fingerprint());
                  }
               });
            }
         });
         capture(level, chunk, false, lodCommitted);
      } else {
         PipelineMetrics.count("direct-ineligible");
      }
   }

   public static boolean chunksmithGeneratedHint(ServerLevel level, ChunkPos pos) {
      if (level != null && pos != null) {
         if (!BackfillGuard.active(level, pos) && !CAPTURE_PENDING.containsKey(captureKey(level, pos))) {
            Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
            if (snapshot == null) {
               return false;
            }

            Path file = path(level, pos, snapshot.fingerprint());

            try {
               return Files.isRegularFile(file) || RecoveryClaims.claimExists(PromotionClaim.claimedPath(file));
            } catch (IOException error) {
               PipelineMetrics.count("promotion-claim-inspection-failed");
               return true;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static CacheManager.ResourceStatus resourceStatus() {
      long now = System.nanoTime();
      long oldest = 0L;
      List<Path> roots = activeRoots;

      for (CacheManager.CaptureFlight flight : CAPTURE_PENDING.values()) {
         oldest = Math.max(oldest, Math.max(0L, now - flight.startedNanos()));
      }

      boolean accepting = currentServer != null && !STOPPING.contains(currentServer);
      String pressure = !accepting
         ? "cache-not-accepting"
         : (
            CAPTURE_BUDGET.full(1048576L)
               ? "cache-snapshot-capacity"
               : (INSPECTIONS.availablePermits() == 0 ? "cache-inspection-capacity" : CacheDiskBudget.pressure(roots))
         );
      return new CacheManager.ResourceStatus(
         CAPTURE_PENDING.size(),
         CAPTURE_BUDGET.bytes(),
         oldest / 1000000L,
         WRITE_IO.getQueue().size(),
         32 - INSPECTIONS.availablePermits(),
         accepting,
         pressure,
         roots.isEmpty() ? 0L : CacheDiskBudget.at(roots.getFirst()).activeBytes(),
         roots.isEmpty() ? -1L : CacheDiskBudget.at(roots.getFirst()).usableBytes()
      );
   }

   public static String resourcePressure() {
      return resourceStatus().pressureReason();
   }

   static String ioSummary(long nowNanos) {
      CacheManager.ResourceStatus status = resourceStatus();
      return "pending="
         + status.pendingCaptures()
         + ",pendingBytes="
         + status.pendingBytes()
         + ",oldestPendingMs="
         + status.oldestPendingMillis()
         + ",writerQueue="
         + status.writerQueue()
         + ",writerActive="
         + WRITE_IO.getActiveCount()
         + ",validating="
         + status.validationInFlight()
         + ",activeCacheBytes="
         + status.activeCacheBytes()
         + ",usableDiskBytes="
         + status.usableDiskBytes()
         + ",pressure="
         + status.pressureReason();
   }

   private static CacheManager.CaptureKey captureKey(ServerLevel level, ChunkPos pos) {
      return new CacheManager.CaptureKey(level, pos.toLong());
   }

   static CompletableFuture<Boolean> capture(ServerLevel var0, LevelChunk var1, boolean var2, CompletableFuture<Boolean> lodCommitted) {
      if (!var0.getServer().isSameThread()) {
         throw new IllegalStateException("Chunk snapshot requires server thread");
      }

      String source = var2 ? "backfill" : "direct";
      CacheManager.CaptureKey key = captureKey(var0, var1.getPos());
      CompletableFuture<Boolean> result = new CompletableFuture<>();
      CacheManager.CaptureFlight flight = new CacheManager.CaptureFlight(result, System.nanoTime());
      CacheManager.CaptureFlight pending = CAPTURE_PENDING.putIfAbsent(key, flight);
      if (pending != null) {
         PipelineMetrics.count("capture-duplicate-avoided");
         return pending.result();
      }

      result.whenComplete((ok, error) -> {
         PipelineMetrics.count(source + (Boolean.TRUE.equals(ok) ? "-saved" : "-failed"));
         PipelineMetrics.time("capture-total", System.nanoTime() - flight.startedNanos());
         CAPTURE_PENDING.remove(key, flight);
      });
      PipelineMetrics.count(source + "-capture-started");

      try {
         captureSnapshot(var0, var1, result, lodCommitted);
      } catch (Throwable error) {
         LOG.warn("Could not capture chunk {}; regeneration remains available", var1.getPos(), error);
         result.complete(false);
      }

      return result;
   }

   private static void captureSnapshot(ServerLevel level, LevelChunk chunk, CompletableFuture<Boolean> result, CompletableFuture<Boolean> lodCommitted) {
      if (STOPPING.contains(level.getServer())) {
         result.complete(false);
      } else {
         CaptureBudget.Lease memory = CAPTURE_BUDGET.tryAcquire(1048576L);
         if (memory == null) {
            PipelineMetrics.count("capture-memory-rejected");
            result.complete(false);
         } else {
            CacheDiskBudget disk = CacheDiskBudget.at(cacheRoot(level));
            CacheDiskBudget.Lease storage = disk.tryAcquire(1048576L);
            if (storage == null) {
               memory.close();
               PipelineMetrics.count("capture-disk-rejected");
               result.complete(false);
            } else if (!INSPECTIONS.tryAcquire()) {
               memory.close();
               storage.close();
               PipelineMetrics.count("capture-inspection-rejected");
               result.complete(false);
            } else {
               CacheManager.InspectionPermit inspection = new CacheManager.InspectionPermit(level.getServer());
               LIVE_INSPECTIONS.add(inspection);
               CacheManager.CaptureWork work = new CacheManager.CaptureWork(memory, storage, result, inspection, lodCommitted);
               result.whenComplete((ok, error) -> work.terminal());
               boolean submitted = false;

               try {
                  Fingerprint.Snapshot fingerprint = Fingerprint.snapshotForLevel(level);
                  if (fingerprint == null) {
                     result.complete(false);
                     return;
                  }

                  ChunkPos pos = chunk.getPos();
                  long started = System.nanoTime();
                  CompoundTag chunkTag = ChunkSerializer.write(level, chunk);
                  CompoundTagBridge.put(chunkTag, "entities", EntitySnapshot.capture(level, pos));
                  PipelineMetrics.time("cache-serialize", System.nanoTime() - started);
                  CompoundTag envelope = new CompoundTag();
                  envelope.putInt("schema", 3);
                  envelope.putBoolean("entitiesComplete", true);
                  envelope.putBoolean("lodCommitted", false);
                  envelope.putString("fingerprint", fingerprint.fingerprint());
                  envelope.putString("dimension", level.dimension().location().toString());
                  envelope.putInt("x", pos.x);
                  envelope.putInt("z", pos.z);
                  CompoundTagBridge.put(envelope, "chunk", chunkTag);
                  long bytes = envelope.sizeInBytes();
                  if (bytes <= 0L || bytes > 16777216L || !memory.resize(bytes) || !storage.resize(bytes + 65536L)) {
                     PipelineMetrics.count("capture-size-rejected");
                     result.complete(false);
                     return;
                  }

                  work.snapshot.set(envelope);
                  rotateGenerations(level, fingerprint, "capture");
                  Path file = path(level, pos, fingerprint.fingerprint());
                  long queuedAt = System.nanoTime();
                  VALIDATE_IO.execute(() -> validateCapture(level, pos, file, work, queuedAt));
                  submitted = true;
                  result.completeOnTimeout(false, 30L, TimeUnit.SECONDS);
               } finally {
                  if (!submitted) {
                     inspection.close();
                  }
               }
            }
         }
      }
   }

   private static void validateCapture(ServerLevel level, ChunkPos pos, Path file, CacheManager.CaptureWork work, long queuedAt) {
      PipelineMetrics.time("cache-validation-queue", System.nanoTime() - queuedAt);
      if (work.result.isDone()) {
         work.inspection.close();
      } else {
         long started = System.nanoTime();

         CompletableFuture<NormalChunkStorage.State> inspected;
         try {
            RecoveryClaims.initialize(cacheRoot(level).resolve(dimensionKey(level)));
            inspected = NormalChunkStorage.inspectAsync(level, pos);
         } catch (Throwable error) {
            work.inspection.close();
            work.result.complete(false);
            return;
         }

         inspected.whenComplete(
            (normal, error) -> {
               work.inspection.close();
               PipelineMetrics.time("cache-inspect", System.nanoTime() - started);
               if (!work.result.isDone()) {
                  boolean claimed;
                  try {
                     claimed = RecoveryClaims.claimExists(PromotionClaim.claimedPath(file));
                  } catch (IOException claimError) {
                     PipelineMetrics.count("promotion-claim-inspection-failed");
                     work.result.complete(false);
                     return;
                  }

                  if (error == null
                     && normal != NormalChunkStorage.State.FULL
                     && normal != NormalChunkStorage.State.UNKNOWN
                     && !claimed
                     && !STOPPING.contains(level.getServer())) {
                     PublicationGate.afterLod(work.lodCommitted, work.result, () -> {
                        long writeQueuedAt = System.nanoTime();

                        try {
                           WRITE_IO.execute(() -> publishCapture(level.getServer(), file, work, writeQueuedAt));
                        } catch (RejectedExecutionException rejected) {
                           PipelineMetrics.count("capture-queue-rejected");
                           work.result.complete(false);
                        }
                     });
                  } else {
                     PipelineMetrics.count("capture-normal-storage-rejected");
                     work.result.complete(false);
                  }
               }
            }
         );
      }
   }

   private static void publishCapture(MinecraftServer server, Path file, CacheManager.CaptureWork work, long queuedAt) {
      CompoundTag envelope = work.beginWrite();
      if (envelope != null) {
         envelope.putBoolean("lodCommitted", true);
         PipelineMetrics.time("cache-queue", System.nanoTime() - queuedAt);
         Path temporary = file.resolveSibling(file.getFileName() + ".tmp-" + Long.toUnsignedString(System.nanoTime()));

         try {
            if (STOPPING.contains(server) || RecoveryClaims.claimExists(PromotionClaim.claimedPath(file))) {
               work.result.complete(false);
               return;
            }

            long started = System.nanoTime();
            Files.createDirectories(file.getParent());
            long previous = Files.isRegularFile(file) ? Files.size(file) : 0L;
            NbtIo.writeCompressed(envelope, temporary);
            if (work.result.isDone() || STOPPING.contains(server)) {
               return;
            }

            if (PromotionClaim.publish(temporary, file)) {
               work.storage.published(Files.size(file) - previous);
               PipelineMetrics.time("cache-write", System.nanoTime() - started);
               work.result.complete(true);
               return;
            }

            work.result.complete(false);
         } catch (Throwable error) {
            LOG.warn("Could not write chunk cache {}", file, error);
            work.result.complete(false);
            return;
         } finally {
            try {
               Files.deleteIfExists(temporary);
            } catch (IOException var22) {
            }

            work.endWrite();
         }
      }
   }

   public static CompletableFuture<Optional<CompoundTag>> preferCacheOverIncompleteNormal(ServerLevel level, ChunkPos pos, Optional<CompoundTag> normal) {
      if (CacheLoadPolicy.keepNormal(normal.orElse(null))) {
         PromotionLedger.normalFullPresent(level, pos);
         return CompletableFuture.completedFuture(normal);
      } else {
         return loadIfPresent(level, pos).thenApply(cached -> cached.isPresent() ? cached : normal);
      }
   }

   public static CompletableFuture<Optional<CompoundTag>> loadIfPresent(ServerLevel var0, ChunkPos var1) {
      Path dimension = cacheRoot(var0).resolve(dimensionKey(var0));
      return RecoveryClaims.prepare(dimension, VALIDATE_IO)
         .copy()
         .orTimeout(30L, TimeUnit.SECONDS)
         .thenCompose(
            ignored -> {
               Path claim = RecoveryClaims.root(dimension)
                  .resolve("r." + Math.floorDiv(var1.x, 32) + "." + Math.floorDiv(var1.z, 32))
                  .resolve("c." + var1.x + "." + var1.z + ".nbt.gz.in-use");

               try {
                  if (RecoveryClaims.claimExists(claim)) {
                     PromotionLedger.blockReplay(var0, var1, claim);
                     PipelineMetrics.count("promotion-replay-blocked");
                     return CompletableFuture.completedFuture(Optional.empty());
                  }
               } catch (IOException claimError) {
                  PipelineMetrics.count("promotion-claim-inspection-failed");
                  return CompletableFuture.failedFuture(claimError);
               }

               return loadReadyCache(var0, var1);
            }
         )
         .whenComplete((cached, error) -> {
            if (error != null && !STOPPING.contains(var0.getServer())) {
               PromotionLedger.blockReplay(var0, var1, RecoveryClaims.root(dimension));
            }
         });
   }

   private static CompletableFuture<Optional<CompoundTag>> loadReadyCache(ServerLevel var0, ChunkPos var1) {
      if (!BackfillGuard.suppress(var0, var1) && promotionAllowed(var0, var1)) {
         Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
         if (snapshot == null) {
            return CompletableFuture.completedFuture(Optional.empty());
         }

         String var2 = snapshot.fingerprint();
         rotateGenerations(var0, snapshot, "load");
         Path var3 = path(var0, var1, var2);
         Path claim = PromotionClaim.claimedPath(var3);

         try {
            if (RecoveryClaims.claimExists(claim)) {
               PromotionLedger.blockReplay(var0, var1, claim);
               PipelineMetrics.count("promotion-replay-blocked");
               return CompletableFuture.completedFuture(Optional.empty());
            }
         } catch (IOException claimError) {
            PipelineMetrics.count("promotion-claim-inspection-failed");
            return CompletableFuture.failedFuture(claimError);
         }

         if (!Files.isRegularFile(var3)) {
            return CompletableFuture.completedFuture(Optional.empty());
         }

         try {
            return CompletableFuture.supplyAsync(
               () -> {
                  try (CachePathLocks.Guard pathLock = CachePathLocks.lock(var3)) {
                     try {
                        try {
                           if (RecoveryClaims.claimExists(claim)) {
                              PromotionLedger.blockReplay(var0, var1, claim);
                              return Optional.empty();
                           }
                        } catch (IOException claimError) {
                           PipelineMetrics.count("promotion-claim-inspection-failed");
                           throw new UncheckedIOException("Cannot inspect promotion ownership at " + claim, claimError);
                        }

                        CompoundTag var4x = NbtIo.readCompressed(var3, NbtAccounter.create(67108864L));
                        String storedFingerprint = var4x.getString("fingerprint");
                        Path generationDirectory = var3.getParent().getParent();
                        if (var4x.getInt("schema") == 3
                           && GenerationStore.acceptsStoredFingerprint(generationDirectory, var2, storedFingerprint)
                           && var0.dimension().location().toString().equals(var4x.getString("dimension"))
                           && var4x.getInt("x") == var1.x
                           && var4x.getInt("z") == var1.z
                           && var4x.contains("chunk")
                           && NormalChunkStorage.fromTag(var4x.getCompound("chunk")) == NormalChunkStorage.State.FULL) {
                           CompoundTag var5x = var4x.getCompound("chunk");
                           if (!CacheEnvelope.completeEntities(var4x)) {
                              quarantineRejected(var3);
                              return Optional.empty();
                           }

                           try {
                              if (!PromotionLedger.claim(var0, var1, var3, !var5x.getList("entities", 10).isEmpty())) {
                                 PipelineMetrics.count("promotion-claim-race-blocked");
                                 throw new UncheckedIOException(
                                    "Promotion ownership changed before final acquisition at " + claim,
                                    new IOException("cache was claimed or disappeared during promotion")
                                 );
                              }
                           } catch (IOException ownershipFailure) {
                              PipelineMetrics.count("promotion-claim-acquire-failed");
                              throw new UncheckedIOException("Cannot acquire promotion ownership at " + claim, ownershipFailure);
                           }

                           return Optional.of(var5x);
                        } else {
                           quarantineRejected(var3);
                           return Optional.empty();
                        }
                     } catch (UncheckedIOException ownershipFailure) {
                        throw ownershipFailure;
                     } catch (Throwable var6) {
                        Path quarantined = quarantineRejected(var3);
                        LOG.warn("Ignoring corrupt chunk cache {}; quarantinedAs={}; vanilla worldgen will be used", new Object[]{var3, quarantined, var6});
                        return Optional.empty();
                     }
                  }
               },
               READ_IO
            );
         } catch (RejectedExecutionException var5) {
            return CompletableFuture.completedFuture(Optional.empty());
         }
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   private static boolean promotionAllowed(ServerLevel var0, ChunkPos var1) {
      try {
         if (ChunksmithProvider.isLoaded() && !ChunksmithProvider.get().getGenerationTasks().isEmpty()) {
            int var2 = var0.getServer().getPlayerList().getViewDistance() + 1;

            for (ServerPlayer var4 : var0.players()) {
               ChunkPos var5 = var4.chunkPosition();
               if (Math.abs(var5.x - var1.x) <= var2 && Math.abs(var5.z - var1.z) <= var2) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      } catch (Throwable var6) {
         return false;
      }
   }

   public static void prepareNormalWrite(ServerLevel level, ChunkPos pos, CompoundTag tag) {
      if (NormalChunkStorage.fromTag(tag) == NormalChunkStorage.State.FULL && !BackfillGuard.suppress(level, pos)) {
         Fingerprint.Snapshot fingerprint = Fingerprint.snapshotForLevel(level);
         if (fingerprint != null) {
            Path file = path(level, pos, fingerprint.fingerprint());
            CacheManager.CaptureFlight pending = CAPTURE_PENDING.get(captureKey(level, pos));
            if (pending != null || Files.isRegularFile(file)) {
               try {
                  PromotionLedger.adoptLive(level, pos, file, pending == null ? null : pending.result());
               } catch (IOException error) {
                  throw new IllegalStateException(
                     "Cannot claim chunk-cache snapshot before normal save at "
                        + file
                        + "; payload retained; repair cache-directory access before retrying save",
                     error
                  );
               }
            }
         }
      }
   }

   public static void onNormalWrite(ServerLevel level, ChunkPos pos, CompoundTag tag, CompletableFuture<Void> written) {
      if (NormalChunkStorage.fromTag(tag) == NormalChunkStorage.State.FULL && !BackfillGuard.suppress(level, pos)) {
         if (!PromotionLedger.observeChunkWrite(level, pos, written)) {
            Fingerprint.Snapshot fingerprint = Fingerprint.snapshotForLevel(level);
            if (fingerprint != null) {
               Path file = path(level, pos, fingerprint.fingerprint());
               written.whenComplete((ignored, error) -> {
                  if (error == null) {
                     retireClaim(file, () -> {});
                  }
               });
            }
         }
      }
   }

   static boolean retireClaim(Path file, Runnable completed) {
      return retireClaim(file, completed, () -> {});
   }

   static boolean retireClaim(Path file, Runnable completed, Runnable retryableFailure) {
      return submitMaintenance(() -> {
         try {
            long bytes = Files.isRegularFile(file) ? Files.size(file) : 0L;
            Files.deleteIfExists(file);
            CacheDiskBudget.at(file.getParent().getParent().getParent().getParent()).deleted(bytes);
            completed.run();
         } catch (IOException error) {
            retryableFailure.run();
            LOG.warn("Could not retire saved chunk cache {}; keeping unavailable payload for maintenance retry", file, error);
         }
      });
   }

   static boolean classifyLiveClaim(PromotionClaim claim, Runnable classified) {
      try {
         VALIDATE_IO.execute(() -> {
            boolean empty = false;

            try {
               CompoundTag envelope = NbtIo.readCompressed(claim.path(), NbtAccounter.create(67108864L));
               empty = CacheEnvelope.completeEntities(envelope) && envelope.getCompound("chunk").getList("entities", 10).isEmpty();
            } catch (Exception unavailable) {
               LOG.debug("Keeping unclassified promotion payload {} until native entity ACK", claim.path(), unavailable);
            }

            claim.classified(empty);
            classified.run();
         });
         return true;
      } catch (RejectedExecutionException full) {
         return false;
      }
   }

   static CacheManager.CacheState cacheState(ServerLevel var0, ChunkPos var1, String var2) {
      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
      if (snapshot != null && snapshot.fingerprint().equals(var2)) {
         rotateGenerations(var0, snapshot, "scan");
      }

      Path file = path(var0, var1, var2);

      try {
         if (RecoveryClaims.claimExists(PromotionClaim.claimedPath(file))) {
            return CacheManager.CacheState.CLAIMED;
         }
      } catch (IOException claimError) {
         PipelineMetrics.count("promotion-claim-inspection-failed");
         return CacheManager.CacheState.UNKNOWN;
      }

      if (!Files.isRegularFile(file)) {
         return CacheManager.CacheState.ABSENT;
      }

      try {
         CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.create(67108864L));
         if (validEnvelope(root, var0, var1, var2)) {
            return CacheManager.CacheState.COMPLETE;
         }

         quarantineRejected(file);
         return CacheManager.CacheState.ABSENT;
      } catch (Throwable error) {
         quarantineRejected(file);
         LOG.warn("Ignoring invalid chunk cache {}; it remains eligible for backfill", new Object[]{file, error});
         return CacheManager.CacheState.ABSENT;
      }
   }

   private static boolean validEnvelope(CompoundTag root, ServerLevel level, ChunkPos pos, String fingerprint) {
      return root != null
         && root.getInt("schema") == 3
         && CacheEnvelope.completeEntities(root)
         && GenerationStore.acceptsStoredFingerprint(path(level, pos, fingerprint).getParent().getParent(), fingerprint, root.getString("fingerprint"))
         && level.dimension().location().toString().equals(root.getString("dimension"))
         && root.getInt("x") == pos.x
         && root.getInt("z") == pos.z
         && root.contains("chunk")
         && NormalChunkStorage.fromTag(root.getCompound("chunk")) == NormalChunkStorage.State.FULL;
   }

   private static Path cacheRoot(ServerLevel level) {
      return level.getServer().getWorldPath(LevelResource.ROOT).resolve("chunksmith_chunk_cache");
   }

   private static Path quarantineRejected(Path file) {
      Path quarantined = GenerationStore.quarantineCorrupt(file);
      if (quarantined != null) {
         CacheDiskBudget.at(file.getParent().getParent().getParent().getParent()).invalidateInventory();
      }

      return quarantined;
   }

   static Path lodRoot(ServerLevel var0) {
      return var0.getServer().getWorldPath(LevelResource.ROOT).resolve("chunksmith").resolve("lod").resolve(dimensionKey(var0));
   }

   static Path worldRegionRoot(ServerLevel var0) {
      Path var1 = var0.getServer().getWorldPath(LevelResource.ROOT);
      String var2 = var0.dimension().location().toString();
      if ("minecraft:overworld".equals(var2)) {
         return var1.resolve("region");
      }

      if (var2.startsWith("minecraft:")) {
         String var3 = var2.substring("minecraft:".length());
         if ("the_nether".equals(var3)) {
            return var1.resolve("DIM-1").resolve("region");
         }

         if ("the_end".equals(var3)) {
            return var1.resolve("DIM1").resolve("region");
         }
      }

      String[] var4 = var2.split(":", 2);
      return var4.length == 2 ? var1.resolve("dimensions").resolve(var4[0]).resolve(var4[1]).resolve("region") : var1.resolve("region");
   }

   static Path path(ServerLevel var0, ChunkPos var1, String var2) {
      int var3 = Math.floorDiv(var1.x, 32);
      int var4 = Math.floorDiv(var1.z, 32);
      return var0.getServer()
         .getWorldPath(LevelResource.ROOT)
         .resolve("chunksmith_chunk_cache")
         .resolve(dimensionKey(var0))
         .resolve(var2)
         .resolve("r." + var3 + "." + var4)
         .resolve("c." + var1.x + "." + var1.z + ".nbt.gz");
   }

   static String dimensionKey(ServerLevel var0) {
      return var0.dimension().location().toString().replace(':', '_').replace('/', '_');
   }

   static boolean submitMaintenance(Runnable task) {
      try {
         WRITE_IO.execute(task);
         return true;
      } catch (RejectedExecutionException ignored) {
         return false;
      }
   }

   static void maintenanceTick(MinecraftServer server) {
      long now = System.currentTimeMillis();
      if (!STOPPING.contains(server)) {
         for (ServerLevel level : server.getAllLevels()) {
            Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
            if (snapshot != null) {
               rotateGenerations(level, snapshot, "maintenance");
            }
         }

         synchronized (NEXT_DISK_REFRESH) {
            if (now >= NEXT_DISK_REFRESH.getOrDefault(server, 0L) && DISK_REFRESHING.add(server)) {
               NEXT_DISK_REFRESH.put(server, now + 10000L);
               ArrayList<Path> roots = new ArrayList<>();
               roots.add(server.getWorldPath(LevelResource.ROOT).resolve("chunksmith_chunk_cache"));
               currentServer = server;
               activeRoots = List.copyOf(roots);

               try {
                  VALIDATE_IO.execute(() -> {
                     try {
                        PromotionLedger.classifyPending(server);

                        for (Path root : roots) {
                           CacheDiskBudget.at(root).refresh(server.getServerDirectory().resolve("config/maki-chunksmith-chunk-cache.properties"));
                        }
                     } finally {
                        DISK_REFRESHING.remove(server);
                     }
                  });
               } catch (RejectedExecutionException rejected) {
                  DISK_REFRESHING.remove(server);
               }
            }
         }

         synchronized (NEXT_MAINTENANCE) {
            long next = NEXT_MAINTENANCE.getOrDefault(server, 0L);
            if (now < next) {
               return;
            }

            NEXT_MAINTENANCE.put(server, now + MAINTENANCE_INTERVAL_MS);
         }

         ArrayList<ServerLevel> levels = new ArrayList<>();
         server.getAllLevels().forEach(levels::add);
         boolean queued = submitMaintenance(() -> {
            FingerprintPolicy policy = FingerprintPolicy.load(server.getServerDirectory());

            for (ServerLevel level : levels) {
               GenerationStore.prune(level, policy);
            }
         });
         if (!queued) {
            synchronized (NEXT_MAINTENANCE) {
               NEXT_MAINTENANCE.put(server, now + MAINTENANCE_RETRY_MS);
            }
         }
      }
   }

   public static void stop(MinecraftServer server) {
      STOPPING.add(server);
      if (currentServer == server) {
         currentServer = null;
         activeRoots = List.of();
      }

      CAPTURE_PENDING.forEach((key, flight) -> {
         if (key.level().getServer() == server) {
            flight.result().complete(false);
         }
      });
      LIVE_INSPECTIONS.forEach(permit -> {
         if (permit.server == server) {
            permit.close();
         }
      });
      PromotionLedger.clear(server);

      for (ServerLevel level : server.getAllLevels()) {
         RecoveryClaims.clear(cacheRoot(level).resolve(dimensionKey(level)));
      }

      synchronized (NEXT_MAINTENANCE) {
         NEXT_MAINTENANCE.remove(server);
      }

      synchronized (NEXT_DISK_REFRESH) {
         NEXT_DISK_REFRESH.remove(server);
      }
   }

   private static void rotateGenerations(ServerLevel var0, Fingerprint.Snapshot snapshot, String trigger) {
      String var2x = cacheRoot(var0).toAbsolutePath().normalize() + "|" + dimensionKey(var0) + "|" + snapshot.fingerprint();
      if (System.nanoTime() >= ROTATION_RETRY.getOrDefault(var2x, 0L)) {
         if (ROTATED.add(var2x)) {
            try {
               WRITE_IO.execute(() -> {
                  try {
                     if (!GenerationStore.reconcile(var0, snapshot, trigger)) {
                        ROTATION_RETRY.put(var2, System.nanoTime() + 60000000000L);
                        ROTATED.remove(var2);
                     } else {
                        ROTATION_RETRY.remove(var2);
                     }
                  } finally {
                     CacheDiskBudget.at(cacheRoot(var0)).invalidateInventory();
                  }
               });
            } catch (RejectedExecutionException var5) {
               ROTATED.remove(var2x);
            }
         }
      }
   }

   private static void moveAtomically(Path var0, Path var1) throws IOException {
      try {
         Files.move(var0, var1, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException var3) {
         Files.move(var0, var1, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private static ThreadFactory daemonFactory(String var0) {
      return var1 -> {
         Thread var2 = new Thread(var1, var0);
         var2.setDaemon(true);
         return var2;
      };
   }

   enum CacheState {
      ABSENT,
      COMPLETE,
      CLAIMED,
      UNKNOWN;

      // $VF: synthetic method
      private static CacheManager.CacheState[] $values() {
         return new CacheManager.CacheState[]{ABSENT, COMPLETE, CLAIMED, UNKNOWN};
      }
   }

   private record CaptureFlight(CompletableFuture<Boolean> result, long startedNanos) {
   }

   private record CaptureKey(ServerLevel level, long pos) {
   }

   private static final class CaptureWork {
      final AtomicReference<CompoundTag> snapshot = new AtomicReference<>();
      final CaptureBudget.Lease memory;
      final CacheDiskBudget.Lease storage;
      final CompletableFuture<Boolean> result;
      final CacheManager.InspectionPermit inspection;
      final CompletableFuture<Boolean> lodCommitted;
      private boolean writing;

      CaptureWork(
         CaptureBudget.Lease memory,
         CacheDiskBudget.Lease storage,
         CompletableFuture<Boolean> result,
         CacheManager.InspectionPermit inspection,
         CompletableFuture<Boolean> lodCommitted
      ) {
         this.memory = memory;
         this.storage = storage;
         this.result = result;
         this.inspection = inspection;
         this.lodCommitted = lodCommitted;
      }

      synchronized CompoundTag beginWrite() {
         if (this.result.isDone()) {
            return null;
         }

         CompoundTag tag = this.snapshot.getAndSet(null);
         this.writing = tag != null;
         return tag;
      }

      synchronized void terminal() {
         this.snapshot.set(null);
         if (!this.writing) {
            this.memory.close();
            this.storage.close();
         }
      }

      synchronized void endWrite() {
         this.writing = false;
         this.memory.close();
         this.storage.close();
      }
   }

   private static final class InspectionPermit {
      final MinecraftServer server;
      final AtomicBoolean closed = new AtomicBoolean();

      InspectionPermit(MinecraftServer server) {
         this.server = server;
      }

      void close() {
         if (this.closed.compareAndSet(false, true)) {
            CacheManager.LIVE_INSPECTIONS.remove(this);
            CacheManager.INSPECTIONS.release();
         }
      }
   }

   public record ResourceStatus(
      int pendingCaptures,
      long pendingBytes,
      long oldestPendingMillis,
      int writerQueue,
      int validationInFlight,
      boolean accepting,
      String pressureReason,
      long activeCacheBytes,
      long usableDiskBytes
   ) {
   }
}
