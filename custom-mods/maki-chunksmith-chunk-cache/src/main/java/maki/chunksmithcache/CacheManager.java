package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.lod.HorizonGuard;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheManager {
   private static final Logger LOG = LoggerFactory.getLogger("MakiChunkCache");
   private static final int SCHEMA = CacheEnvelope.SCHEMA;
   private static final long SNAPSHOT_ESTIMATE = 1L << 20;
   private static final long MAX_ENTRY_BYTES = 16L << 20;
   private static final CaptureBudget CAPTURE_BUDGET = new CaptureBudget(64, 64L << 20);
   private static final Semaphore INSPECTIONS = new Semaphore(32);
   private static final Set<InspectionPermit> LIVE_INSPECTIONS = ConcurrentHashMap.newKeySet();
   private static final Set<MinecraftServer> STOPPING = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
   private static volatile MinecraftServer currentServer;
   private static volatile List<Path> activeRoots = List.of();
   private static final Map<MinecraftServer, Long> NEXT_DISK_REFRESH = new WeakHashMap<>();
   private static final Set<MinecraftServer> DISK_REFRESHING = ConcurrentHashMap.newKeySet();
   private static final AtomicLong LAST_QUEUE_WARNING = new AtomicLong();
   private static final Set<String> ROTATED = ConcurrentHashMap.newKeySet();
   private static final Map<String,Long> ROTATION_RETRY = new ConcurrentHashMap<>();
   private static final Map<String,CompletableFuture<Boolean>> ROTATION_IN_FLIGHT = new ConcurrentHashMap<>();
   private static final Map<CaptureKey, CaptureFlight> CAPTURE_PENDING = new ConcurrentHashMap<>();
   private static final Map<MinecraftServer, Long> NEXT_MAINTENANCE = new WeakHashMap<>();
   private static final long MAINTENANCE_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15L);
   private static final long MAINTENANCE_RETRY_MS = TimeUnit.MINUTES.toMillis(1L);
   private static final ThreadPoolExecutor WRITE_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128), daemonFactory("maki-chunk-cache-write"), new AbortPolicy()
   );
   private static final ThreadPoolExecutor MIGRATION_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(8), daemonFactory("maki-chunk-cache-migration"), new AbortPolicy()
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
      // A generic LodSupport.offer can also come from save/unload refreshes or
      // another owner's task. Only the world-adapter call site invokes us now.
      if (level == null || chunk == null || !level.getServer().isSameThread()
         || level.getServer().getPlayerCount() != 0 || !FollowCaptureOwnership.owns(level)
         || BackfillGuard.suppress(level, chunk.getPos()) || !HorizonGuard.drop(level, chunk.getPos())) {
         PipelineMetrics.count("direct-ineligible");
         return;
      }
      ChunkPos pos=chunk.getPos();
      lodCommitted.whenComplete((saved,error) -> {
         if(error!=null || !Boolean.TRUE.equals(saved)) Fingerprint.whenReady(level).thenAccept(fingerprint -> {
            if(fingerprint!=null) BackfillController.retryDirectLod(level,pos,fingerprint.fingerprint());
         });
      });
      capture(level, chunk, false, lodCommitted);
   }

   /**
    * Fast, non-blocking indication used after the follow dispatch barrier and
    * by the Chunksmith world adapter. Cache files are atomically published, so
    * a visible path represents a completed write; CAPTURE_PENDING closes the
    * small race before that atomic move.
    */
   public static boolean chunksmithGeneratedHint(ServerLevel level, ChunkPos pos) {
      if(level==null||pos==null)return false;
      if(BackfillGuard.active(level,pos)||CAPTURE_PENDING.containsKey(captureKey(level,pos)))return true;
      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if (snapshot == null) return false;
      String fingerprint = snapshot.fingerprint();
      Path file = path(level, pos, fingerprint);
      Path claim = PromotionClaim.claimedPath(file);

      // Promotion moves the payload to the durable claim path. Observe claim
      // ownership and payload visibility under the same coordinate lock so a move
      // cannot fall between two independent existence checks.
      try (var ignored = CachePathLocks.lock(claim)) {
         try {
            if (RecoveryClaims.claimExists(claim)) return true;
         } catch(IOException error) {
            // Unknown ownership is generated/pending for admission purposes: never generate over it.
            PipelineMetrics.count("promotion-claim-inspection-failed");
            return true;
         }
         if (!Files.isRegularFile(file)) return false;
         if (Files.isRegularFile(generatedCompleteMarker(file))) return true;

         // Crash recovery for the narrow publication window between the atomic NBT
         // move and sidecar creation. The normal fast path remains marker-only; a
         // compressed read occurs only when a visible cache is unexpectedly missing
         // its marker.
         try {
            CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.create(64L << 20));
            boolean validIdentity = validEnvelopeIdentity(root, level, pos, fingerprint);
            CacheEnvelope.Reuse reuse = CacheEnvelope.reuse(root);
            if (reuse == CacheEnvelope.Reuse.GENERATED_COMPLETE) {
               return ensureGeneratedCompleteMarker(file, root, validIdentity);
            }
            if (reuse == CacheEnvelope.Reuse.PROMOTION_ONLY && validIdentity) {
               // A verified FULL payload already exists. Its missing LOD receipt is
               // retry debt for Backfill, not permission for Chunksmith to regenerate FULL.
               PipelineMetrics.count("cached-full-pending-lod");
               return true;
            }
            return false;
         } catch(Throwable invalid) {
            PipelineMetrics.count("generated-complete-marker-repair-failed");
            return false;
         }
      }
   }

   /**
    * Preserve Chunksmith's native generated-state result, but do not allow a false
    * result to race retained FULL migration. NeoForgeWorld performs its native
    * status probe on/through the server thread; any retained-generation scan is
    * deferred to READ_IO after the dimension reconciliation settles.
    */
   public static CompletableFuture<Boolean> protectChunksmithGeneratedResult(
      ServerLevel level,
      ChunkPos pos,
      CompletableFuture<Boolean> nativeResult
   ) {
      if (level == null || pos == null || nativeResult == null) return nativeResult;
      if (nativeResult.isDone() && !nativeResult.isCompletedExceptionally()
         && Boolean.TRUE.equals(nativeResult.getNow(false))) {
         return nativeResult;
      }

      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
      if (snapshot == null) return nativeResult;
      CompletableFuture<Boolean> rotation = rotateGenerations(level, snapshot, "chunksmith-generated");

      return nativeResult.thenCompose(generated -> {
         if (Boolean.TRUE.equals(generated) || chunksmithGeneratedHint(level, pos)) {
            return CompletableFuture.completedFuture(true);
         }
         if (reconciliationAllowsMiss(rotation)) {
            return CompletableFuture.completedFuture(false);
         }

         CompletableFuture<?> settled = rotation.isDone()
            ? CompletableFuture.completedFuture(null)
            : rotation.handle((complete, error) -> null);
         return settled.thenCompose(ignored ->
            chunksmithRetainedGeneratedHint(level, pos, snapshot, rotation)
         );
      });
   }

   private static CompletableFuture<Boolean> chunksmithRetainedGeneratedHint(
      ServerLevel level,
      ChunkPos pos,
      Fingerprint.Snapshot snapshot,
      CompletableFuture<Boolean> rotation
   ) {
      if (chunksmithGeneratedHint(level, pos)) {
         return CompletableFuture.completedFuture(true);
      }
      if (reconciliationAllowsMiss(rotation)) {
         return CompletableFuture.completedFuture(false);
      }

      try {
         return CompletableFuture.supplyAsync(() -> {
            // Re-check after reconciliation because the payload may have been
            // published between the native status probe and this retained scan.
            if (chunksmithGeneratedHint(level, pos)) return true;

            GenerationStore.RetainedCoordinateState retained = GenerationStore.retainedCoordinateState(
               cacheRoot(level).resolve(dimensionKey(level)),
               snapshot.manifest(Set.of(snapshot.fingerprint())),
               snapshot.fingerprint(),
               snapshot.legacyFingerprint(),
               snapshot.policy(),
               level.dimension().location().toString(),
               pos.x,
               pos.z
            );
            if (!retainedBlocksChunksmithGeneration(retained)) {
               PipelineMetrics.count("chunksmith-reconciliation-localized-miss");
               return false;
            }
            PipelineMetrics.count(retained == GenerationStore.RetainedCoordinateState.REUSABLE
               ? "chunksmith-reconciliation-coordinate-pending"
               : "chunksmith-reconciliation-coordinate-unknown");
            return true;
         }, READ_IO);
      } catch (RejectedExecutionException rejected) {
         // Admission uncertainty must fail closed: Chunksmith may retry the probe,
         // but must not generate over retained source that we could not inspect.
         PipelineMetrics.count("chunksmith-reconciliation-inspection-rejected");
         return CompletableFuture.completedFuture(true);
      }
   }

   static boolean retainedBlocksChunksmithGeneration(GenerationStore.RetainedCoordinateState retained) {
      return retained != GenerationStore.RetainedCoordinateState.ABSENT;
   }

   private record CaptureKey(ServerLevel level, long pos) {}
   private record CaptureFlight(CompletableFuture<Boolean> result, long startedNanos) {}

   public record ResourceStatus(int pendingCaptures, long pendingBytes, long oldestPendingMillis,
                                int writerQueue, int validationInFlight, boolean accepting, String pressureReason,
                                long activeCacheBytes, long usableDiskBytes) {}

   public static ResourceStatus resourceStatus() {
      long now = System.nanoTime(), oldest = 0L;
      List<Path> roots=activeRoots;
      for (CaptureFlight flight : CAPTURE_PENDING.values()) oldest = Math.max(oldest, Math.max(0L, now-flight.startedNanos()));
      boolean accepting = currentServer != null && !STOPPING.contains(currentServer);
      String pressure = !accepting ? "cache-not-accepting" : CAPTURE_BUDGET.full(SNAPSHOT_ESTIMATE) ? "cache-snapshot-capacity"
         : INSPECTIONS.availablePermits() == 0 ? "cache-inspection-capacity" : CacheDiskBudget.pressure(roots);
      return new ResourceStatus(CAPTURE_PENDING.size(), CAPTURE_BUDGET.bytes(), oldest/1_000_000L,
         WRITE_IO.getQueue().size(), 32-INSPECTIONS.availablePermits(), accepting, pressure,
         roots.isEmpty()?0L:CacheDiskBudget.at(roots.getFirst()).activeBytes(),
         roots.isEmpty()?-1L:CacheDiskBudget.at(roots.getFirst()).usableBytes());
   }

   public static String resourcePressure() { return resourceStatus().pressureReason(); }

   static String ioSummary(long nowNanos) {
      ResourceStatus status = resourceStatus();
      return "pending=" + status.pendingCaptures() + ",pendingBytes=" + status.pendingBytes()
         + ",oldestPendingMs=" + status.oldestPendingMillis() + ",writerQueue=" + status.writerQueue()
         + ",writerActive=" + WRITE_IO.getActiveCount() + ",validating=" + status.validationInFlight()
         + ",activeCacheBytes=" + status.activeCacheBytes() + ",usableDiskBytes=" + status.usableDiskBytes()
         + ",pressure=" + status.pressureReason();
   }

   private static CaptureKey captureKey(ServerLevel level,ChunkPos pos) {
      return new CaptureKey(level, pos.toLong());
   }

   static CompletableFuture<Boolean> capture(ServerLevel var0, LevelChunk var1, boolean var2, CompletableFuture<Boolean> lodCommitted) {
      if (!var0.getServer().isSameThread()) throw new IllegalStateException("Chunk snapshot requires server thread");
      String source = var2 ? "backfill" : "direct";
      CaptureKey key = captureKey(var0, var1.getPos());
      CompletableFuture<Boolean> result = new CompletableFuture<>();
      CaptureFlight flight = new CaptureFlight(result, System.nanoTime());
      CaptureFlight pending = CAPTURE_PENDING.putIfAbsent(key, flight);
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

   /** Takes the complete snapshot synchronously, while the caller still owns its native ticket. */
   private static void captureSnapshot(ServerLevel level, LevelChunk chunk, CompletableFuture<Boolean> result, CompletableFuture<Boolean> lodCommitted) {
      if (STOPPING.contains(level.getServer())) { result.complete(false); return; }
      CaptureBudget.Lease memory = CAPTURE_BUDGET.tryAcquire(SNAPSHOT_ESTIMATE);
      if (memory == null) { PipelineMetrics.count("capture-memory-rejected"); result.complete(false); return; }
      CacheDiskBudget disk = CacheDiskBudget.at(cacheRoot(level));
      CacheDiskBudget.Lease storage = disk.tryAcquire(SNAPSHOT_ESTIMATE);
      if (storage == null) {
         memory.close(); PipelineMetrics.count("capture-disk-rejected"); result.complete(false); return;
      }
      if (!INSPECTIONS.tryAcquire()) {
         memory.close(); storage.close(); PipelineMetrics.count("capture-inspection-rejected"); result.complete(false); return;
      }
      InspectionPermit inspection=new InspectionPermit(level.getServer());
      LIVE_INSPECTIONS.add(inspection);
      CaptureWork work = new CaptureWork(memory, storage, result, inspection, lodCommitted);
      result.whenComplete((ok, error) -> work.terminal());
      boolean submitted = false;
      try {
         Fingerprint.Snapshot fingerprint = Fingerprint.snapshotForLevel(level);
         if (fingerprint == null) { result.complete(false); return; }
         ChunkPos pos = chunk.getPos();
         long started = System.nanoTime();
         CompoundTag chunkTag = ChunkSerializer.write(level, chunk);
         // FULL serialization omits entities. Vanilla FULL read/post-load supports this legacy
         // entities field and inserts roots/passengers through the persistent entity manager.
         CompoundTagBridge.put(chunkTag, "entities", EntitySnapshot.capture(level, pos));
         PipelineMetrics.time("cache-serialize", System.nanoTime()-started);
         CompoundTag envelope = new CompoundTag();
         envelope.putInt("schema", SCHEMA);
         envelope.putBoolean("entitiesComplete", true);
         envelope.putBoolean("lodCommitted", false); // Account for the publication receipt before reserving measured bytes.
         envelope.putString("fingerprint", fingerprint.fingerprint());
         envelope.putString("dimension", level.dimension().location().toString());
         envelope.putInt("x", pos.x); envelope.putInt("z", pos.z);
         CompoundTagBridge.put(envelope, "chunk", chunkTag);
         long bytes = envelope.sizeInBytes();
         if (bytes <= 0 || bytes > MAX_ENTRY_BYTES || !memory.resize(bytes) || !storage.resize(bytes+65_536L)) {
            PipelineMetrics.count("capture-size-rejected"); result.complete(false); return;
         }
         work.snapshot.set(envelope);
         rotateGenerations(level, fingerprint, "capture");
         Path file = path(level, pos, fingerprint.fingerprint());
         long queuedAt = System.nanoTime();
         VALIDATE_IO.execute(() -> validateCapture(level, pos, file, work, queuedAt));
         submitted = true;
         // The validation operation may remain queued in native I/O. Expiring our detached work
         // drops its NBT, but its inspection permit remains held until the actual native completion.
         result.completeOnTimeout(false, 30L, TimeUnit.SECONDS);
      } finally {
         if (!submitted) inspection.close();
      }
   }

   private static void validateCapture(ServerLevel level, ChunkPos pos, Path file, CaptureWork work, long queuedAt) {
      PipelineMetrics.time("cache-validation-queue",System.nanoTime()-queuedAt);
      if (work.result.isDone()) { work.inspection.close(); return; }
      long started = System.nanoTime();
      CompletableFuture<NormalChunkStorage.State> inspected;
      try {
         RecoveryClaims.initialize(cacheRoot(level).resolve(dimensionKey(level)));
         inspected = NormalChunkStorage.inspectAsync(level, pos);
      }
      catch (Throwable error) { work.inspection.close(); work.result.complete(false); return; }
      inspected.whenComplete((normal, error) -> {
         work.inspection.close();
         PipelineMetrics.time("cache-inspect", System.nanoTime()-started);
         if (work.result.isDone()) return;
         boolean claimed;
         try { claimed=RecoveryClaims.claimExists(PromotionClaim.claimedPath(file)); }
         catch(IOException claimError) {
            PipelineMetrics.count("promotion-claim-inspection-failed");
            work.result.complete(false);
            return;
         }
         if (error != null || normal == NormalChunkStorage.State.FULL || normal == NormalChunkStorage.State.UNKNOWN
            || claimed || STOPPING.contains(level.getServer())) {
            PipelineMetrics.count("capture-normal-storage-rejected"); work.result.complete(false); return;
         }
         // Snapshotting and native inspection overlap the LOD writer, but a visible cache
         // entry must also prove its LOD obligation was met. Otherwise discovery would
         // skip the coordinate and erase a failed LOD write's retry debt.
         PublicationGate.afterLod(work.lodCommitted, work.result, () -> {
            long writeQueuedAt=System.nanoTime();
            try { WRITE_IO.execute(() -> publishCapture(level.getServer(), file, work, writeQueuedAt)); }
            catch (RejectedExecutionException rejected) { PipelineMetrics.count("capture-queue-rejected"); work.result.complete(false); }
         });
      });
   }

   private static void publishCapture(MinecraftServer server, Path file, CaptureWork work, long queuedAt) {
      CompoundTag envelope = work.beginWrite();
      if (envelope == null) return;
      envelope.putBoolean("lodCommitted", true);
      PipelineMetrics.time("cache-queue", System.nanoTime()-queuedAt);
      Path temporary = file.resolveSibling(file.getFileName()+".tmp-"+Long.toUnsignedString(System.nanoTime()));
      try {
         if (STOPPING.contains(server) || RecoveryClaims.claimExists(PromotionClaim.claimedPath(file))) { work.result.complete(false); return; }
         long started = System.nanoTime();
         Files.createDirectories(file.getParent());
         long previous = Files.isRegularFile(file) ? Files.size(file) : 0L;
         NbtIo.writeCompressed(envelope, temporary);
         if (work.result.isDone() || STOPPING.contains(server)) return;
         if (!PromotionClaim.publishGenerated(temporary,file)) { work.result.complete(false); return; }
         work.storage.published(Files.size(file)-previous);
         PipelineMetrics.time("cache-write", System.nanoTime()-started);
         work.result.complete(true);
      } catch (Throwable error) {
         LOG.warn("Could not write chunk cache {}", file, error);
         work.result.complete(false);
      } finally {
         try { Files.deleteIfExists(temporary); } catch (IOException ignored) {}
         work.endWrite();
      }
   }

   private static final class CaptureWork {
      final AtomicReference<CompoundTag> snapshot = new AtomicReference<>();
      final CaptureBudget.Lease memory;
      final CacheDiskBudget.Lease storage;
      final CompletableFuture<Boolean> result;
      final InspectionPermit inspection;
      final CompletableFuture<Boolean> lodCommitted;
      private boolean writing;
      CaptureWork(CaptureBudget.Lease memory, CacheDiskBudget.Lease storage, CompletableFuture<Boolean> result, InspectionPermit inspection, CompletableFuture<Boolean> lodCommitted) {
         this.memory=memory; this.storage=storage; this.result=result; this.inspection=inspection;
         this.lodCommitted=lodCommitted;
      }
      synchronized CompoundTag beginWrite() {
         if (result.isDone()) return null;
         CompoundTag tag=snapshot.getAndSet(null);
         writing=tag!=null;
         return tag;
      }
      synchronized void terminal() { snapshot.set(null); if (!writing) { memory.close(); storage.close(); } }
      synchronized void endWrite() { writing=false; memory.close(); storage.close(); }
   }

   private static final class InspectionPermit {
      final MinecraftServer server;
      final AtomicBoolean closed=new AtomicBoolean();
      InspectionPermit(MinecraftServer server) { this.server=server; }
      void close() {
         if(closed.compareAndSet(false,true)) { LIVE_INSPECTIONS.remove(this); INSPECTIONS.release(); }
      }
   }

   public static CompletableFuture<Optional<CompoundTag>> preferCacheOverIncompleteNormal(
      ServerLevel level, ChunkPos pos, Optional<CompoundTag> normal
   ) {
      if (CacheLoadPolicy.keepNormal(normal.orElse(null))) {
         PromotionLedger.normalFullPresent(level,pos);
         return CompletableFuture.completedFuture(normal);
      }

      return loadIfPresent(level, pos).thenApply(cached -> cached.isPresent() ? cached : normal);
   }

   public static CompletableFuture<Optional<CompoundTag>> loadIfPresent(ServerLevel var0, ChunkPos var1) {
      Path dimension=cacheRoot(var0).resolve(dimensionKey(var0));
      // Ownership is independent of fingerprint readiness and admission vetoes.
      // Concurrent loads share off-thread startup inventory, never a server-thread scan.
      return RecoveryClaims.prepare(dimension,VALIDATE_IO).copy().orTimeout(30,TimeUnit.SECONDS).thenCompose(ignored -> {
         Path claim=RecoveryClaims.root(dimension).resolve("r."+Math.floorDiv(var1.x,32)+"."+Math.floorDiv(var1.z,32))
            .resolve("c."+var1.x+"."+var1.z+".nbt.gz.in-use");
         try {
            if(RecoveryClaims.claimExists(claim)) {
               PromotionLedger.blockReplay(var0,var1,claim);
               PipelineMetrics.count("promotion-replay-blocked");
               return CompletableFuture.completedFuture(Optional.empty());
            }
         } catch(IOException claimError) {
            PipelineMetrics.count("promotion-claim-inspection-failed");
            return CompletableFuture.failedFuture(claimError);
         }
         return loadReadyCache(var0,var1);
      }).whenComplete((cached,error) -> {
         // Vanilla catches read failures and can substitute an empty ProtoChunk. Keep the
         // ownership failure until the outer scheduleChunkLoad boundary rejects that fallback.
         if(error!=null && !STOPPING.contains(var0.getServer())) PromotionLedger.blockReplay(var0,var1,RecoveryClaims.root(dimension));
      });
   }

   static boolean cacheReadAdmission(boolean suppressed, boolean backfillTarget, boolean generalPromotionAllowed) {
      return (!suppressed || backfillTarget) && (backfillTarget || generalPromotionAllowed);
   }

   static boolean claimOnCacheRead(CacheEnvelope.Reuse reuse, boolean backfillTarget) {
      return (reuse == CacheEnvelope.Reuse.PROMOTION_ONLY || reuse == CacheEnvelope.Reuse.GENERATED_COMPLETE)
         && !(backfillTarget && reuse == CacheEnvelope.Reuse.PROMOTION_ONLY);
   }

   private static CompletableFuture<Optional<CompoundTag>> loadReadyCache(ServerLevel var0, ChunkPos var1) {
      boolean backfillTarget = BackfillGuard.active(var0, var1);
      if (cacheReadAdmission(BackfillGuard.suppress(var0, var1), backfillTarget, promotionAllowed(var0, var1))) {
         Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
         if (snapshot == null) {
            return CompletableFuture.completedFuture(Optional.empty());
         }
         String var2 = snapshot.fingerprint();

         CompletableFuture<Boolean> rotation = rotateGenerations(var0, snapshot, "load");
         Path var3 = path(var0, var1, var2);
         Path claim=PromotionClaim.claimedPath(var3);
         try {
            if (RecoveryClaims.claimExists(claim)) {
               PromotionLedger.blockReplay(var0,var1,claim);
               PipelineMetrics.count("promotion-replay-blocked");
               return CompletableFuture.completedFuture(Optional.empty());
            }
         } catch(IOException claimError) {
            PipelineMetrics.count("promotion-claim-inspection-failed");
            return CompletableFuture.failedFuture(claimError);
         }
         if (!Files.isRegularFile(var3)) {
            if (!rotation.isDone()) {
               // Do not race a retained candidate that is actively being copied. A load
               // deadline alone does not prove that the migration task stopped mutating
               // retained paths, so timeout stays fail-closed. Coordinate localization is
               // only safe once the underlying reconciliation has actually terminated.
               return rotation.copy().orTimeout(30, TimeUnit.SECONDS)
                  .handle((complete, error) -> {
                     if (error == null && Boolean.TRUE.equals(complete)) {
                        return loadReadyCache(var0, var1);
                     }
                     if (!rotation.isDone()) {
                        return CompletableFuture.<Optional<CompoundTag>>failedFuture(new IOException(
                           "Chunk-cache generation reconciliation is still in progress for " + var1
                              + " in " + var0.dimension().location(),
                           error
                        ));
                     }
                     return resolveIncompleteReconciliation(var0, var1, snapshot);
                  })
                  .thenCompose(future -> future);
            }
            if (!reconciliationAllowsMiss(rotation)) {
               return resolveIncompleteReconciliation(var0, var1, snapshot);
            }
            return CompletableFuture.completedFuture(Optional.empty());
         }

         try {
            return CompletableFuture.supplyAsync(
               () -> {
                  try (var pathLock=CachePathLocks.lock(var3)) {
                  try {
                     try {
                        if (RecoveryClaims.claimExists(claim)) {
                           PromotionLedger.blockReplay(var0,var1,claim);
                           return Optional.empty();
                        }
                     } catch(IOException claimError) {
                        PipelineMetrics.count("promotion-claim-inspection-failed");
                        throw new java.io.UncheckedIOException("Cannot inspect promotion ownership at "+claim,claimError);
                     }
                     CompoundTag var4 = NbtIo.readCompressed(var3, NbtAccounter.create(64L << 20));
                     String storedFingerprint = var4.getString("fingerprint");
                     Path generationDirectory = var3.getParent().getParent();
                     CacheEnvelope.Reuse reuse = CacheEnvelope.reuse(var4);
                     boolean identityValid = GenerationStore.acceptsStoredFingerprint(generationDirectory, var2, storedFingerprint)
                        && var0.dimension().location().toString().equals(var4.getString("dimension"))
                        && var4.getInt("x") == var1.x
                        && var4.getInt("z") == var1.z
                        && var4.contains("chunk")
                        && NormalChunkStorage.fromTag(var4.getCompound("chunk")) == NormalChunkStorage.State.FULL;
                     if ((reuse == CacheEnvelope.Reuse.PROMOTION_ONLY || reuse == CacheEnvelope.Reuse.GENERATED_COMPLETE)
                        && identityValid) {
                        CompoundTag var5x = var4.getCompound("chunk");
                        if (claimOnCacheRead(reuse, backfillTarget)) {
                           try {
                              if (!PromotionLedger.claim(var0, var1, var3, !var5x.getList("entities", 10).isEmpty())) {
                                 PipelineMetrics.count("promotion-claim-race-blocked");
                                 throw new java.io.UncheckedIOException(
                                    "Promotion ownership changed before final acquisition at "+claim,
                                    new IOException("cache was claimed or disappeared during promotion"));
                              }
                           } catch(IOException ownershipFailure) {
                              PipelineMetrics.count("promotion-claim-acquire-failed");
                              throw new java.io.UncheckedIOException("Cannot acquire promotion ownership at "+claim,ownershipFailure);
                           }
                        } else {
                           // Backfill owns the load and suppresses normal persistence. Keep a
                           // promotion-only payload in place so the post-LOD capture can atomically
                           // replace it with a generated-complete schema-3 record.
                           PipelineMetrics.count("backfill-promotion-only-reused");
                        }
                        return Optional.of(var5x);
                     }
                     if (reuse == CacheEnvelope.Reuse.LEGACY_UNPROVEN) {
                        PipelineMetrics.count("legacy-cache-retained-unproven");
                        return Optional.empty();
                     }
                     quarantineRejected(var3);
                     return Optional.empty();
                  } catch (java.io.UncheckedIOException ownershipFailure) {
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

   private static CompletableFuture<Optional<CompoundTag>> resolveIncompleteReconciliation(
      ServerLevel level, ChunkPos pos, Fingerprint.Snapshot snapshot
   ) {
      Path active = path(level, pos, snapshot.fingerprint());
      if (Files.isRegularFile(active)) return loadReadyCache(level, pos);

      try {
         return CompletableFuture.supplyAsync(() -> {
            GenerationStore.RetainedCoordinateState retained = GenerationStore.retainedCoordinateState(
               cacheRoot(level).resolve(dimensionKey(level)),
               snapshot.manifest(Set.of(snapshot.fingerprint())),
               snapshot.fingerprint(),
               snapshot.legacyFingerprint(),
               snapshot.policy(),
               level.dimension().location().toString(),
               pos.x,
               pos.z
            );
            if (retained == GenerationStore.RetainedCoordinateState.ABSENT) {
               PipelineMetrics.count("generation-reconciliation-localized-miss");
               return Optional.<CompoundTag>empty();
            }

            String reason;
            if (retained == GenerationStore.RetainedCoordinateState.REUSABLE) {
               PipelineMetrics.count("generation-reconciliation-coordinate-pending");
               reason = "a compatible retained cache candidate is still pending migration";
            } else {
               PipelineMetrics.count("generation-reconciliation-coordinate-unknown");
               reason = "retained cache ownership cannot be proven absent";
            }
            throw new java.util.concurrent.CompletionException(new IOException(
               "Chunk-cache generation reconciliation remains required for " + pos + " in "
                  + level.dimension().location() + ": " + reason
            ));
         }, READ_IO);
      } catch (RejectedExecutionException rejected) {
         return CompletableFuture.failedFuture(new IOException(
            "Cannot queue coordinate-local retained-cache inspection for " + pos + " in "
               + level.dimension().location(),
            rejected
         ));
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

   /** IOWorker.store's Future completes after RegionFileStorage.write, not just enqueue. */
   public static void prepareNormalWrite(ServerLevel level, ChunkPos pos, CompoundTag tag) {
      if(NormalChunkStorage.fromTag(tag)!=NormalChunkStorage.State.FULL || BackfillGuard.suppress(level,pos)) return;
      Fingerprint.Snapshot fingerprint=Fingerprint.snapshotForLevel(level);
      if(fingerprint==null) return;
      Path file=path(level,pos,fingerprint.fingerprint());
      CaptureFlight pending=CAPTURE_PENDING.get(captureKey(level,pos));
      if(pending==null && !Files.isRegularFile(file)) return;
      try {
         // A generated chunk can pass directly to a player without a cache read. Close that
         // rollback route too. If a detached capture is already in flight, keep the .in-use
         // ownership barrier until that exact Future is terminal so its delayed LOD ACK cannot
         // reopen pre-save state after the native FULL write has committed.
         PromotionLedger.adoptLive(level,pos,file,pending==null?null:pending.result());
      } catch(IOException error) {
         throw new IllegalStateException("Cannot claim chunk-cache snapshot before normal save at "+file+"; payload retained; repair cache-directory access before retrying save",error);
      }
   }

   public static void onNormalWrite(ServerLevel level, ChunkPos pos, CompoundTag tag, CompletableFuture<Void> written) {
      if (NormalChunkStorage.fromTag(tag) != NormalChunkStorage.State.FULL || BackfillGuard.suppress(level,pos)) return;
      if (PromotionLedger.observeChunkWrite(level,pos,written)) return;
      Fingerprint.Snapshot fingerprint=Fingerprint.snapshotForLevel(level);
      if (fingerprint==null) return;
      Path file=path(level,pos,fingerprint.fingerprint());
      written.whenComplete((ignored,error) -> { if(error==null) retireClaim(file, () -> {}); });
   }

   static boolean retireClaim(Path file, Runnable completed) {
      return retireClaim(file,completed,() -> {});
   }

   static boolean retireClaim(Path file, Runnable completed, Runnable retryableFailure) {
      return submitMaintenance(() -> {
         try {
            long bytes=Files.isRegularFile(file)?Files.size(file):0L;
            Files.deleteIfExists(file);
            clearGeneratedComplete(file);
            CacheDiskBudget.at(file.getParent().getParent().getParent().getParent()).deleted(bytes);
            completed.run();
         } catch(IOException error) {
            retryableFailure.run();
            LOG.warn("Could not retire saved chunk cache {}; keeping unavailable payload for maintenance retry",file,error);
         }
      });
   }

   static boolean classifyLiveClaim(PromotionClaim claim,Runnable classified) {
      try {
         VALIDATE_IO.execute(() -> {
            boolean empty=false;
            try {
               CompoundTag envelope=NbtIo.readCompressed(claim.path(),NbtAccounter.create(64L<<20));
               empty=CacheEnvelope.completeEntities(envelope) && envelope.getCompound("chunk").getList("entities",10).isEmpty();
            } catch(Exception unavailable) {
               // Known-empty native bookkeeping is not proof: its write may have failed.
               // Preserve unreadable/old payloads rather than manufacture an entity ACK.
               LOG.debug("Keeping unclassified promotion payload {} until native entity ACK",claim.path(),unavailable);
            }
            claim.classified(empty);
            classified.run();
         });
         return true;
      } catch(RejectedExecutionException full) { return false; }
   }

   enum CacheState { ABSENT, COMPLETE, FULL_PENDING_LOD, CLAIMED, UNKNOWN }

   static CacheState cacheState(ServerLevel var0, ChunkPos var1, String var2) {
      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
      CompletableFuture<Boolean> rotation = null;
      if (snapshot != null && snapshot.fingerprint().equals(var2)) {
         rotation = rotateGenerations(var0, snapshot, "scan");
      }
      Path file = path(var0, var1, var2);
      Path claim = PromotionClaim.claimedPath(file);

      // Scan classification, marker repair, and promotion all share the same
      // coordinate-claim lock. This prevents a player load from moving a verified
      // payload to .promotions after validation but before marker repair, which
      // would otherwise recreate an orphaned marker at the old available path.
      try (var ignored = CachePathLocks.lock(claim)) {
         // A claim blocks generation but does not by itself prove the LOD retry obligation
         // is complete: adoptLive can create a payload-free marker while capture is pending.
         try {
            if (RecoveryClaims.claimExists(claim)) return CacheState.CLAIMED;
         } catch(IOException claimError) {
            PipelineMetrics.count("promotion-claim-inspection-failed");
            return CacheState.UNKNOWN;
         }
         if (!Files.isRegularFile(file)) {
            // Reconciliation is dimension-scoped, but a failed/deferred entry must only
            // block the coordinate that can actually be re-adopted. Scan runs off-thread,
            // so a targeted retained-entry inspection is safe here.
            if (rotation != null && !reconciliationAllowsMiss(rotation)) {
               GenerationStore.RetainedCoordinateState retained = GenerationStore.retainedCoordinateState(
                  cacheRoot(var0).resolve(dimensionKey(var0)),
                  snapshot.manifest(Set.of(snapshot.fingerprint())),
                  snapshot.fingerprint(),
                  snapshot.legacyFingerprint(),
                  snapshot.policy(),
                  var0.dimension().location().toString(),
                  var1.x,
                  var1.z
               );
               return retained == GenerationStore.RetainedCoordinateState.ABSENT
                  ? CacheState.ABSENT
                  : CacheState.UNKNOWN;
            }
            return CacheState.ABSENT;
         }

         try {
            CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.create(64L << 20));
            CacheEnvelope.Reuse reuse = CacheEnvelope.reuse(root);
            if (reuse == CacheEnvelope.Reuse.GENERATED_COMPLETE
               && ensureGeneratedCompleteMarker(file, root, validEnvelopeIdentity(root, var0, var1, var2))) {
               return CacheState.COMPLETE;
            }
            if (reuse == CacheEnvelope.Reuse.PROMOTION_ONLY && validEnvelopeIdentity(root, var0, var1, var2)) {
               // The FULL payload is promotion-safe, while only the LOD receipt remains
               // unproven. Backfill may consume this state for LOD work without regenerating FULL.
               return CacheState.FULL_PENDING_LOD;
            }
            if (reuse == CacheEnvelope.Reuse.LEGACY_UNPROVEN) {
               PipelineMetrics.count("legacy-cache-retained-unproven");
               return CacheState.ABSENT;
            }
            quarantineRejected(file);
            return CacheState.ABSENT;
         } catch (Throwable error) {
            quarantineRejected(file);
            LOG.warn("Ignoring invalid chunk cache {}; it remains eligible for backfill", new Object[]{file, error});
            return CacheState.ABSENT;
         }
      }
   }

   private static boolean validEnvelopeIdentity(CompoundTag root, ServerLevel level, ChunkPos pos, String fingerprint) {
      return root != null
         && GenerationStore.acceptsStoredFingerprint(path(level,pos,fingerprint).getParent().getParent(), fingerprint, root.getString("fingerprint"))
         && level.dimension().location().toString().equals(root.getString("dimension"))
         && root.getInt("x") == pos.x
         && root.getInt("z") == pos.z
         && root.contains("chunk")
         && NormalChunkStorage.fromTag(root.getCompound("chunk")) == NormalChunkStorage.State.FULL;
   }

   private static Path cacheRoot(ServerLevel level) {
      return level.getServer().getWorldPath(LevelResource.ROOT).resolve("chunksmith_chunk_cache");
   }

   static Path generatedCompleteMarker(Path file) {
      return file.resolveSibling(file.getFileName() + ".lod-complete");
   }

   static void markGeneratedComplete(Path file) throws IOException {
      Path marker = generatedCompleteMarker(file);
      Files.createDirectories(marker.getParent());
      Files.writeString(marker, "schema=" + SCHEMA + "\n");
   }

   static void clearGeneratedComplete(Path file) throws IOException {
      Files.deleteIfExists(generatedCompleteMarker(file));
   }

   static boolean ensureGeneratedCompleteMarker(Path file, CompoundTag root, boolean validIdentity) {
      if (!validIdentity || CacheEnvelope.reuse(root) != CacheEnvelope.Reuse.GENERATED_COMPLETE) return false;
      if (Files.isRegularFile(generatedCompleteMarker(file))) return true;
      try {
         markGeneratedComplete(file);
         PipelineMetrics.count("generated-complete-marker-repaired");
      } catch (IOException markerError) {
         // The validated schema-3 payload is still authoritative for this admission
         // decision. Retry marker persistence on the next observation rather than
         // allowing duplicate FULL generation.
         PipelineMetrics.count("generated-complete-marker-repair-failed");
      }
      return true;
   }

   private static Path quarantineRejected(Path file) {
      try { clearGeneratedComplete(file); } catch (IOException ignored) {}
      Path quarantined=GenerationStore.quarantineCorrupt(file);
      if(quarantined!=null) CacheDiskBudget.at(file.getParent().getParent().getParent().getParent()).invalidateInventory();
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

   static boolean submitMigration(Runnable task) {
      try {
         MIGRATION_IO.execute(task);
         return true;
      } catch (RejectedExecutionException ignored) {
         return false;
      }
   }

   static boolean submitGenerationMaintenance(Runnable task) {
      return submitMigration(task);
   }

   static boolean submitRetainedInspection(Runnable task) {
      return submitGenerationMaintenance(task);
   }

   static void maintenanceTick(MinecraftServer server) {
      long now = System.currentTimeMillis();
      if (STOPPING.contains(server)) return;
      // Reconciliation must not depend on capture admission. Otherwise an old
      // full generation can consume the quota needed to trigger its own quarantine.
      for (ServerLevel level:server.getAllLevels()) {
         Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
         if(snapshot!=null) rotateGenerations(level,snapshot,"maintenance");
      }
      synchronized (NEXT_DISK_REFRESH) {
         if (now >= NEXT_DISK_REFRESH.getOrDefault(server,0L) && DISK_REFRESHING.add(server)) {
            NEXT_DISK_REFRESH.put(server,now+10_000L);
            ArrayList<Path> roots=new ArrayList<>();
            roots.add(server.getWorldPath(LevelResource.ROOT).resolve("chunksmith_chunk_cache"));
            currentServer=server;
            activeRoots=List.copyOf(roots);
            try {
               VALIDATE_IO.execute(() -> {
                  try {
                     PromotionLedger.classifyPending(server);
                     for(Path root:roots) CacheDiskBudget.at(root).refresh(server.getServerDirectory().resolve("config/maki-chunksmith-chunk-cache.properties"));
                  }
                  finally { DISK_REFRESHING.remove(server); }
               });
            } catch(RejectedExecutionException rejected) { DISK_REFRESHING.remove(server); }
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
      boolean queued = submitRetainedInspection(() -> {
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

   public static void stop(MinecraftServer server) {
      STOPPING.add(server);
      if(currentServer==server) { currentServer=null; activeRoots=List.of(); }
      // No join on server callbacks/native I/O during shutdown. Detached queued captures are
      // invalidated; a running writer checks this flag again before atomic publication.
      CAPTURE_PENDING.forEach((key,flight) -> { if(key.level().getServer()==server) flight.result().complete(false); });
      LIVE_INSPECTIONS.forEach(permit -> { if(permit.server==server) permit.close(); });
      PromotionLedger.clear(server);
      for(ServerLevel level:server.getAllLevels()) RecoveryClaims.clear(cacheRoot(level).resolve(dimensionKey(level)));
      synchronized(NEXT_MAINTENANCE) { NEXT_MAINTENANCE.remove(server); }
      synchronized(NEXT_DISK_REFRESH) { NEXT_DISK_REFRESH.remove(server); }
   }

   static boolean reconciliationAllowsMiss(CompletableFuture<Boolean> rotation) {
      return rotation != null
         && rotation.isDone()
         && !rotation.isCompletedExceptionally()
         && Boolean.TRUE.equals(rotation.getNow(false));
   }

   private static CompletableFuture<Boolean> rotateGenerations(ServerLevel var0, Fingerprint.Snapshot snapshot, String trigger) {
      String var2 = cacheRoot(var0).toAbsolutePath().normalize() + "|" + dimensionKey(var0) + "|" + snapshot.fingerprint();
      CompletableFuture<Boolean> inFlight = ROTATION_IN_FLIGHT.get(var2);
      if (inFlight != null) return inFlight;
      if (System.nanoTime() < ROTATION_RETRY.getOrDefault(var2, 0L)) {
         return CompletableFuture.completedFuture(false);
      }
      if (ROTATED.contains(var2)) return CompletableFuture.completedFuture(true);

      CompletableFuture<Boolean> created = new CompletableFuture<>();
      inFlight = ROTATION_IN_FLIGHT.putIfAbsent(var2, created);
      if (inFlight != null) return inFlight;
      if (!ROTATED.add(var2)) {
         ROTATION_IN_FLIGHT.remove(var2, created);
         created.complete(true);
         return created;
      }

      boolean queued = submitGenerationMaintenance(() -> {
         try {
            boolean complete = GenerationStore.reconcile(var0, snapshot, trigger);
            if (!complete) {
               ROTATION_RETRY.put(var2, System.nanoTime() + 60_000_000_000L);
               ROTATED.remove(var2);
            } else {
               ROTATION_RETRY.remove(var2);
            }
            created.complete(complete);
         } catch (Throwable error) {
            ROTATED.remove(var2);
            created.completeExceptionally(error);
         } finally {
            ROTATION_IN_FLIGHT.remove(var2, created);
            CacheDiskBudget.at(cacheRoot(var0)).invalidateInventory();
         }
      });
      if (!queued) {
         ROTATED.remove(var2);
         ROTATION_IN_FLIGHT.remove(var2, created);
         created.completeExceptionally(new RejectedExecutionException("generation maintenance queue full"));
      }
      return created;
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
}
