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
import java.util.concurrent.atomic.AtomicInteger;
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
   private static final Map<CaptureKey, CaptureFlight> CAPTURE_PENDING = new ConcurrentHashMap<>();
   // FULL/ownership and LOD persistence debt are independent: a normal FULL save
   // must never erase a failed/missing LOD obligation.
   private static final Set<CaptureKey> PREGEN_RETRY = ConcurrentHashMap.newKeySet();
   private static final Set<CaptureKey> PREGEN_LOD_RETRY = ConcurrentHashMap.newKeySet();
   private static final ConcurrentHashMap<CaptureKey,Long> PREGEN_NEW_TARGETS = new ConcurrentHashMap<>();
   private static final Set<CaptureKey> PREGEN_REQUEST_TARGETS = ConcurrentHashMap.newKeySet();
   private static final ConcurrentHashMap<CaptureKey,AtomicInteger> LOD_PENDING = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<CaptureKey,Path> CONFIRMED_PREGEN = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Path,ConcurrentHashMap<String,Long>> NEXT_LOCAL_RECOVERY_WARNING = new ConcurrentHashMap<>();
   private static final Map<MinecraftServer, Long> NEXT_MAINTENANCE = new WeakHashMap<>();
   private static final long MAINTENANCE_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15L);
   private static final long MAINTENANCE_RETRY_MS = TimeUnit.MINUTES.toMillis(1L);
   private static final long LOCAL_RECOVERY_WARNING_INTERVAL_NANOS = TimeUnit.MINUTES.toNanos(1L);
   private static final long LOCAL_OWNERSHIP_TIMEOUT_SECONDS = 10L;
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
      if (level == null || chunk == null) return;
      ChunkPos pos=chunk.getPos();
      CaptureKey key=captureKey(level,pos);
      LOD_PENDING.compute(key,(ignored,count) -> {
         if(count==null)count=new AtomicInteger();
         count.incrementAndGet();
         return count;
      });
      lodCommitted.whenComplete((saved,error) -> LOD_PENDING.computeIfPresent(key,(ignored,count) ->
         count.decrementAndGet()<=0?null:count));

      // A confirmed cache FULL borrowed to repair LOD already owns its read-only
      // persistence guard. Attach the native writer ACK and keep the guard until unload.
      if (PregenBorrowLedger.kind(level,pos)==PregenBorrowLedger.Kind.BORROWED) {
         PregenBorrowLedger.attachLod(level,pos,lodCommitted);
         PREGEN_NEW_TARGETS.remove(key);
         lodCommitted.whenComplete((saved,error) -> {
            if(error==null && Boolean.TRUE.equals(saved)) {
               PREGEN_LOD_RETRY.remove(key);
               PipelineMetrics.count("pregen-lod-repair-complete");
            } else {
               PREGEN_LOD_RETRY.add(key);
               PipelineMetrics.count("pregen-lod-repair-failed");
            }
         });
         return;
      }

      // No NEW marker means Chunksmith loaded an already-authoritative normal FULL
      // only to repair LOD. Do not copy it into the pregen cache or suppress its saves.
      boolean generatedTarget=PREGEN_NEW_TARGETS.remove(key)!=null;
      if(!generatedTarget) {
         lodCommitted.whenComplete((lodSaved,lodError) -> {
            if(lodError==null && Boolean.TRUE.equals(lodSaved)) PREGEN_LOD_RETRY.remove(key);
            else PREGEN_LOD_RETRY.add(key);
         });
         return;
      }

      // A generic LodSupport.offer can also come from another owner's task.
      // Only a target proven NEW by this follow-owned Chunksmith task is cached.
      // Player arrival does not cancel already-owned work; handoff is claim-before-write.
      if (!level.getServer().isSameThread() || !FollowCaptureOwnership.owns(level)
         || BackfillGuard.suppress(level, pos)) {
         PREGEN_RETRY.add(key);
         PipelineMetrics.count("direct-ineligible");
         return;
      }

      if(PregenBorrowLedger.kind(level,pos)!=PregenBorrowLedger.Kind.GENERATED) {
         PREGEN_RETRY.add(key);
         PipelineMetrics.count("direct-persistence-guard-missing");
         return;
      }
      if(PregenBorrowLedger.isHandedOff(level,pos)) {
         lodCommitted.whenComplete((lodSaved,lodError) -> {
            if(lodError==null&&Boolean.TRUE.equals(lodSaved)) PREGEN_LOD_RETRY.remove(key);
            else PREGEN_LOD_RETRY.add(key);
         });
         PipelineMetrics.count("direct-cache-skipped-after-player-handoff");
         return;
      }
      PregenBorrowLedger.attachLod(level,pos,lodCommitted);
      lodCommitted.whenComplete((lodSaved,lodError) -> {
         if(lodError==null && Boolean.TRUE.equals(lodSaved)) PREGEN_LOD_RETRY.remove(key);
         else PREGEN_LOD_RETRY.add(key);
      });
      CompletableFuture<Boolean> saved=capture(level, chunk, false, lodCommitted);
      saved.whenComplete((ok,error) -> {
         if(error==null && Boolean.TRUE.equals(ok)) {
            Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
            if(snapshot!=null)CONFIRMED_PREGEN.put(key,path(level,pos,snapshot.fingerprint()));
            PREGEN_RETRY.remove(key);
            PREGEN_LOD_RETRY.remove(key);
            return;
         }
         try {
            NormalChunkStorage.inspectAsync(level,pos).whenComplete((normal,inspectError) -> {
               if(inspectError!=null || normal!=NormalChunkStorage.State.FULL) {
                  PREGEN_RETRY.add(key);
                  return;
               }
               PREGEN_RETRY.remove(key);
               lodCommitted.whenComplete((lodSaved,lodError) -> {
                  if(lodError==null && Boolean.TRUE.equals(lodSaved)) PREGEN_LOD_RETRY.remove(key);
                  else PREGEN_LOD_RETRY.add(key);
               });
            });
         } catch(Throwable inspectFailure) {
            PREGEN_RETRY.add(key);
         }
      });
   }

   public static boolean pregenRetryPending(int centerX, int centerZ, int radiusBlocks) {
      long radius=(long)Math.max(0,radiusBlocks)+16L;
      long radiusSquared=radius*radius;
      if(anyInRange(PREGEN_RETRY,centerX,centerZ,radiusSquared))return true;
      if(anyInRange(PREGEN_LOD_RETRY,centerX,centerZ,radiusSquared))return true;
      if(anyInRange(PREGEN_NEW_TARGETS.keySet(),centerX,centerZ,radiusSquared))return true;
      if(anyInRange(LOD_PENDING.keySet(),centerX,centerZ,radiusSquared))return true;
      return anyInRange(CAPTURE_PENDING.keySet(),centerX,centerZ,radiusSquared);
   }

   public static void noteChunksmithGeneratedResult(ServerLevel level,ChunkPos pos,boolean generated) {
      if(level==null||pos==null||!FollowCaptureOwnership.owns(level))return;
      CaptureKey key=captureKey(level,pos);
      if(generated) PREGEN_NEW_TARGETS.remove(key);
      else PREGEN_NEW_TARGETS.put(key,System.currentTimeMillis());
   }

   public static void noteChunksmithGeneratedFailure(ServerLevel level,ChunkPos pos) {
      if(level==null||pos==null||!FollowCaptureOwnership.owns(level))return;
      CaptureKey key=captureKey(level,pos);
      PREGEN_NEW_TARGETS.remove(key);
      PREGEN_RETRY.add(key);
      PipelineMetrics.count("pregen-generated-state-failed");
   }

   public static boolean preparePregenRequest(ServerLevel level,ChunkPos pos) {
      if(level==null||pos==null)return false;
      if(!FollowCaptureOwnership.owns(level))return true;
      CaptureKey key=captureKey(level,pos);
      PREGEN_REQUEST_TARGETS.add(key);
      if(!PREGEN_NEW_TARGETS.containsKey(key))return true;
      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if(snapshot==null) {
         PREGEN_RETRY.add(key);
         return false;
      }
      Path file=path(level,pos,snapshot.fingerprint());
      PregenBorrowLedger.Kind kind=PregenBorrowLedger.kind(level,pos);
      if(kind==PregenBorrowLedger.Kind.GENERATED)return true;
      if(kind!=null)return false;
      if(PregenBorrowLedger.beginGenerated(level,pos,file))return true;
      PREGEN_RETRY.add(key);
      return false;
   }

   public static void pregenRequestFinished(ServerLevel level,ChunkPos pos,boolean success) {
      if(level==null||pos==null)return;
      CaptureKey key=captureKey(level,pos);
      // Only requests admitted as follow-owned targets may mutate unified-pregen debt.
      // External Chunksmith tasks pass through this Mixin but never enter this set.
      if(!PREGEN_REQUEST_TARGETS.remove(key))return;
      if(success)return;
      PREGEN_NEW_TARGETS.remove(key);
      PREGEN_RETRY.add(key);
   }

   public static void pregenRequestFailed(ServerLevel level,ChunkPos pos) {
      pregenRequestFinished(level,pos,false);
   }

   public static boolean pregenReady(MinecraftServer server) {
      return server!=null && Fingerprint.snapshotForLevel(server.overworld())!=null;
   }

   private static boolean anyInRange(Iterable<CaptureKey> keys,int centerX,int centerZ,long radiusSquared) {
      for(CaptureKey key:keys) {
         ChunkPos pos=new ChunkPos(key.pos());
         long x=((long)pos.x<<4)+8L-centerX;
         long z=((long)pos.z<<4)+8L-centerZ;
         if(x*x+z*z<=radiusSquared)return true;
      }
      return false;
   }

   /**
    * Fast, non-blocking indication used after the follow dispatch barrier and
    * by the Chunksmith world adapter. Cache files are atomically published, so
    * a visible path represents a completed write; CAPTURE_PENDING closes the
    * small race before that atomic move.
    */
   public static boolean unifiedPairSkipHint(ServerLevel level, ChunkPos pos) {
      if(level==null||pos==null||!FollowCaptureOwnership.owns(level))return false;
      CaptureKey key=captureKey(level,pos);
      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if(snapshot==null)return false;
      Path expected=path(level,pos,snapshot.fingerprint());
      Path confirmed=CONFIRMED_PREGEN.get(key);
      if(expected.equals(confirmed)&&Files.isRegularFile(expected)) {
         PREGEN_RETRY.remove(key);
         PREGEN_LOD_RETRY.remove(key);
         return true;
      }
      if(confirmed!=null)CONFIRMED_PREGEN.remove(key,confirmed);
      // Do not let the horizon's cheap LOD-only test skip an unvalidated cache file.
      // The later isChunkGenerated Future can validate it off-thread.
      return false;
   }

   public static CompletableFuture<Boolean> protectChunksmithGeneratedResult(
      ServerLevel level, ChunkPos pos, CompletableFuture<Boolean> nativeResult
   ) {
      if(level==null||pos==null||nativeResult==null)return nativeResult;
      CaptureKey key=captureKey(level,pos);
      return nativeResult.thenCompose(generated -> {
         if(Boolean.TRUE.equals(generated)) {
            // Native FULL is authoritative over every pregen/cache observation.
            PREGEN_RETRY.remove(key);
            PREGEN_NEW_TARGETS.remove(key);
            return CompletableFuture.completedFuture(true);
         }
         // The unified cache is an implementation detail of the follow-owned
         // pipeline. External Chunksmith tasks retain native generated-state
         // semantics and are never adopted or redirected through this cache.
         if(!FollowCaptureOwnership.owns(level))return CompletableFuture.completedFuture(false);
         CompletableFuture<Boolean> cacheDecision=chunksmithGeneratedDecision(level,pos);
         return cacheDecision==null?CompletableFuture.completedFuture(false):cacheDecision;
      });
   }

   public static CompletableFuture<Boolean> chunksmithGeneratedDecision(ServerLevel level, ChunkPos pos) {
      if(level==null||pos==null)return null;
      CaptureKey key=captureKey(level,pos);
      if(BackfillGuard.active(level,pos))return CompletableFuture.completedFuture(true);
      CaptureFlight pending=CAPTURE_PENDING.get(key);
      if(pending!=null)return pending.result().thenApply(Boolean.TRUE::equals);

      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if(snapshot==null)return null;
      String fingerprint=snapshot.fingerprint();
      Path file=path(level,pos,fingerprint);
      Path confirmed=CONFIRMED_PREGEN.get(key);
      if(file.equals(confirmed)&&Files.isRegularFile(file)) {
         PREGEN_RETRY.remove(key);
         return CompletableFuture.completedFuture(true);
      }
      if(confirmed!=null)CONFIRMED_PREGEN.remove(key,confirmed);

      Path claim=PromotionClaim.claimedPath(file);
      try {
         if(RecoveryClaims.claimExists(claim)) {
            PREGEN_RETRY.add(key);
            return CompletableFuture.completedFuture(true);
         }
      } catch(IOException ownershipUnknown) {
         PREGEN_RETRY.add(key);
         PipelineMetrics.count("promotion-claim-inspection-failed");
         return CompletableFuture.completedFuture(true);
      }
      if(!Files.isRegularFile(file))return null;

      try {
         return CompletableFuture.supplyAsync(() -> {
            try(var pathLock=CachePathLocks.lock(file)) {
               try {
                  if(RecoveryClaims.claimExists(claim)) {
                     PREGEN_RETRY.add(key);
                     return true;
                  }
                  CompoundTag envelope=NbtIo.readCompressed(file,NbtAccounter.create(64L<<20));
                  if(validEnvelope(envelope,level,pos,fingerprint)) {
                     CONFIRMED_PREGEN.put(key,file);
                     PREGEN_RETRY.remove(key);
                     PipelineMetrics.count("pregen-full-validated");
                     return true;
                  }
                  CONFIRMED_PREGEN.remove(key);
                  quarantineRejected(file);
                  PipelineMetrics.count("pregen-full-invalid");
                  return false;
               } catch(IOException ownershipOrReadFailure) {
                  CONFIRMED_PREGEN.remove(key);
                  try {
                     if(RecoveryClaims.claimExists(claim)) {
                        PREGEN_RETRY.add(key);
                        return true;
                     }
                  } catch(IOException ownershipUnknown) {
                     PREGEN_RETRY.add(key);
                     return true;
                  }
                  quarantineRejected(file);
                  PipelineMetrics.count("pregen-full-invalid");
                  return false;
               } catch(Throwable invalid) {
                  CONFIRMED_PREGEN.remove(key);
                  quarantineRejected(file);
                  PipelineMetrics.count("pregen-full-invalid");
                  return false;
               }
            }
         },READ_IO);
      } catch(RejectedExecutionException full) {
         PREGEN_RETRY.add(key);
         return CompletableFuture.completedFuture(true);
      }
   }

   public static void noteObservedNormalFullLod(ServerLevel level,ChunkPos pos) {
      if(level==null||pos==null)return;
      CaptureKey key=captureKey(level,pos);
      PREGEN_RETRY.remove(key);
      PREGEN_LOD_RETRY.remove(key);
      PREGEN_NEW_TARGETS.remove(key);
   }

   public static void noteObservedLodPresent(ServerLevel level,ChunkPos pos) {
      if(level==null||pos==null)return;
      PREGEN_LOD_RETRY.remove(captureKey(level,pos));
   }

   public static boolean chunksmithGeneratedHint(ServerLevel level, ChunkPos pos) {
      if(level==null||pos==null)return false;
      CaptureKey key=captureKey(level,pos);
      if(BackfillGuard.active(level,pos)||CAPTURE_PENDING.containsKey(key))return true;
      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if(snapshot==null)return false;
      Path file=path(level,pos,snapshot.fingerprint());
      Path confirmed=CONFIRMED_PREGEN.get(key);
      if(file.equals(confirmed)&&Files.isRegularFile(file))return true;
      if(confirmed!=null)CONFIRMED_PREGEN.remove(key,confirmed);
      try {
         return RecoveryClaims.claimExists(PromotionClaim.claimedPath(file));
      } catch(IOException error) {
         PipelineMetrics.count("promotion-claim-inspection-failed");
         return true;
      }
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
         if (!PromotionClaim.publish(temporary,file)) { work.result.complete(false); return; }
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

      if (pregenBorrowAllowed(level,pos)) {
         return loadForPregenLodRepair(level,pos).thenApply(cached -> cached.isPresent() ? cached : normal);
      }
      return loadIfPresent(level, pos).thenApply(cached -> cached.isPresent() ? cached : normal);
   }

   private static boolean pregenBorrowAllowed(ServerLevel level, ChunkPos pos) {
      // Only the coordinate selected by GenerationTask may borrow pregen FULL.
      // Dependency reads must keep normal-world semantics and must not acquire a
      // LOD-repair guard for which no target LodSupport.offer will ever arrive.
      return FollowCaptureOwnership.owns(level)
         && PREGEN_REQUEST_TARGETS.contains(captureKey(level,pos));
   }

   private static CompletableFuture<Optional<CompoundTag>> loadForPregenLodRepair(ServerLevel level, ChunkPos pos) {
      // A GENERATED guard is armed before getChunkAtAsync precisely so the target
      // can reach FULL without entering normal persistence. Do not wait on its own
      // future LOD ACK or try to load a cache that does not exist yet.
      if(PregenBorrowLedger.kind(level,pos)==PregenBorrowLedger.Kind.GENERATED)
         return CompletableFuture.completedFuture(Optional.empty());

      CompletableFuture<Boolean> active=PregenBorrowLedger.pending(level,pos);
      if(active!=null) return active.handle((ignored,error)->null).thenCompose(ignored -> loadForPregenLodRepair(level,pos));

      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if(snapshot==null)return CompletableFuture.completedFuture(Optional.empty());
      String fingerprint=snapshot.fingerprint();
      rotateGenerations(level,snapshot,"pregen-lod-repair");
      Path file=path(level,pos,fingerprint);
      Path claim=PromotionClaim.claimedPath(file);
      try {
         if(RecoveryClaims.claimExists(claim)) {
            PromotionLedger.blockReplay(level,pos,claim);
            PREGEN_RETRY.add(captureKey(level,pos));
            return CompletableFuture.failedFuture(new IOException("Promotion ownership is active at "+claim));
         }
      } catch(IOException ownershipFailure) {
         PromotionLedger.blockReplay(level,pos,claim);
         PREGEN_RETRY.add(captureKey(level,pos));
         return CompletableFuture.failedFuture(ownershipFailure);
      }
      if(!Files.isRegularFile(file))return CompletableFuture.completedFuture(Optional.empty());

      try {
         return CompletableFuture.supplyAsync(() -> {
            try(var pathLock=CachePathLocks.lock(file)) {
               try {
                  if(RecoveryClaims.claimExists(claim)) {
                     PromotionLedger.blockReplay(level,pos,claim);
                     PREGEN_RETRY.add(captureKey(level,pos));
                     throw new java.io.UncheckedIOException(new IOException("Promotion ownership changed at "+claim));
                  }
                  CompoundTag envelope=NbtIo.readCompressed(file,NbtAccounter.create(64L<<20));
                  if(!validEnvelope(envelope,level,pos,fingerprint)) {
                     quarantineRejected(file);
                     PREGEN_RETRY.add(captureKey(level,pos));
                     return Optional.<CompoundTag>empty();
                  }
                  CONFIRMED_PREGEN.put(captureKey(level,pos),file);
                  if(!PregenBorrowLedger.beginBorrowed(level,pos,file)) {
                     PREGEN_RETRY.add(captureKey(level,pos));
                     return Optional.<CompoundTag>empty();
                  }
                  PipelineMetrics.count("pregen-full-borrowed-for-lod");
                  return Optional.of(envelope.getCompound("chunk"));
               } catch(java.io.UncheckedIOException ownershipFailure) {
                  throw ownershipFailure;
               } catch(Throwable invalid) {
                  quarantineRejected(file);
                  PREGEN_RETRY.add(captureKey(level,pos));
                  LOG.warn("Pregen FULL {} could not be validated for LOD repair; quarantined for coordinate-local regeneration",file,invalid);
                  return Optional.<CompoundTag>empty();
               }
            }
         },READ_IO);
      } catch(RejectedExecutionException full) {
         PREGEN_RETRY.add(captureKey(level,pos));
         return CompletableFuture.failedFuture(full);
      }
   }

   public static CompletableFuture<Optional<CompoundTag>> loadIfPresent(ServerLevel var0, ChunkPos var1) {
      Path dimension=cacheRoot(var0).resolve(dimensionKey(var0));
      // Ownership is independent of fingerprint readiness and admission vetoes.
      // Dimension-wide recovery remains the fast path. If it fails or exceeds the load deadline,
      // inspect only ownership paths that can belong to this coordinate before allowing fallback.
      return ownershipForLoad(dimension,var1).thenCompose(claim -> {
         if(claim!=null) {
            PromotionLedger.blockReplay(var0,var1,claim);
            PipelineMetrics.count("promotion-replay-blocked");
            return CompletableFuture.completedFuture(Optional.empty());
         }
         return loadReadyCache(var0,var1);
      }).whenComplete((cached,error) -> {
         // Vanilla catches read failures and can substitute an empty ProtoChunk. Keep target
         // ownership uncertainty until the outer scheduleChunkLoad boundary rejects that fallback.
         if(error!=null && !STOPPING.contains(var0.getServer())) PromotionLedger.blockReplay(var0,var1,RecoveryClaims.root(dimension));
      });
   }

   private static CompletableFuture<Path> ownershipForLoad(Path dimension,ChunkPos pos) {
      CompletableFuture<Throwable> prepared=RecoveryClaims.prepare(dimension,VALIDATE_IO)
         .copy().orTimeout(30,TimeUnit.SECONDS).handle((ignored,error) -> error);
      return prepared.thenCompose(recoveryFailure -> {
         if(recoveryFailure==null) {
            try {
               return CompletableFuture.completedFuture(RecoveryClaims.claimForCoordinate(dimension,pos.x,pos.z));
            } catch(IOException targetFailure) {
               PipelineMetrics.count("promotion-claim-inspection-failed");
               return CompletableFuture.failedFuture(targetFailure);
            }
         }

         try {
            CompletableFuture<Path> probe=CompletableFuture.supplyAsync(() -> {
               try {
                  Path claim=RecoveryClaims.claimForCoordinate(dimension,pos.x,pos.z);
                  if(claim==null) {
                     PipelineMetrics.count("promotion-recovery-localized");
                     LOG.debug(
                        "Chunk-cache recovery is unavailable for {} but coordinate {} has no ownership in canonical, legacy, quarantine, or conflict paths; normal cache/world loading may continue for this coordinate; recoveryFailure={}",
                        dimension.getFileName(),pos,failureSummary(recoveryFailure)
                     );
                  } else {
                     PipelineMetrics.count("promotion-recovery-local-claim");
                     if(allowLocalRecoveryWarning(dimension,"claim")) LOG.warn(
                        "Chunk-cache load remains blocked for {} {}: dimension recovery is unavailable and targeted ownership proof found {}; repair cache-directory access and retry recovery; recoveryFailure={}",
                        dimension.getFileName(),pos,claim,failureSummary(recoveryFailure)
                     );
                  }
                  return claim;
               } catch(IOException targetFailure) {
                  PipelineMetrics.count("promotion-recovery-local-proof-failed");
                  String category="proof-"+unwrapFailure(targetFailure).getClass().getName();
                  if(allowLocalRecoveryWarning(dimension,category)) LOG.warn(
                     "Chunk-cache load remains blocked for {} {}: dimension recovery failed and target ownership absence cannot be proven; repair cache-directory access before retrying this coordinate; recoveryFailure={}",
                     dimension.getFileName(),pos,failureSummary(recoveryFailure),targetFailure
                  );
                  throw new java.io.UncheckedIOException(
                     "Cannot prove chunk-cache promotion ownership absence for "+dimension.getFileName()+" "+pos,
                     targetFailure
                  );
               }
            },READ_IO);
            return boundedOwnershipProbe(probe,LOCAL_OWNERSHIP_TIMEOUT_SECONDS,TimeUnit.SECONDS)
               .whenComplete((claim,error) -> {
                  Throwable failure=unwrapFailure(error);
                  if(failure instanceof java.util.concurrent.TimeoutException) {
                     PipelineMetrics.count("promotion-recovery-local-timeout");
                     if(allowLocalRecoveryWarning(dimension,"timeout")) LOG.warn(
                        "Chunk-cache load remains blocked for {} {}: coordinate-local ownership proof exceeded {}s after dimension recovery failed; ownership is unknown, so replacement generation remains disabled; repair cache-directory I/O and retry",
                        dimension.getFileName(),pos,LOCAL_OWNERSHIP_TIMEOUT_SECONDS
                     );
                  }
               });
         } catch(RejectedExecutionException rejected) {
            return CompletableFuture.failedFuture(new IOException(
               "Cannot queue coordinate-local chunk-cache ownership proof for "+dimension.getFileName()+" "+pos,
               rejected
            ));
         }
      });
   }

   static <T> CompletableFuture<T> boundedOwnershipProbe(CompletableFuture<T> probe,long timeout,TimeUnit unit) {
      return probe.orTimeout(timeout,unit);
   }

   private static boolean allowLocalRecoveryWarning(Path dimension,String category) {
      Path key=dimension.toAbsolutePath().normalize();
      long now=System.nanoTime();
      AtomicBoolean allowed=new AtomicBoolean();
      NEXT_LOCAL_RECOVERY_WARNING.computeIfAbsent(key,ignored -> new ConcurrentHashMap<>()).compute(category,(ignored,next) -> {
         if(next==null || now>=next) {
            allowed.set(true);
            return now+LOCAL_RECOVERY_WARNING_INTERVAL_NANOS;
         }
         return next;
      });
      return allowed.get();
   }

   private static Throwable unwrapFailure(Throwable error) {
      if(error==null) return null;
      Throwable value=error;
      while(value.getCause()!=null && (value instanceof java.util.concurrent.CompletionException
         || value instanceof java.util.concurrent.ExecutionException)) value=value.getCause();
      return value;
   }

   private static String failureSummary(Throwable error) {
      Throwable value=unwrapFailure(error);
      String message=value==null?null:value.getMessage();
      return value==null?"none":value.getClass().getSimpleName()+(message==null?"":": "+message);
   }

   private static CompletableFuture<Optional<CompoundTag>> loadReadyCache(ServerLevel var0, ChunkPos var1) {
      CompletableFuture<Boolean> borrowed=PregenBorrowLedger.pending(var0,var1);
      if(borrowed!=null) {
         return borrowed.handle((ignored,error)->null).thenCompose(ignored -> loadReadyCache(var0,var1));
      }
      if (!BackfillGuard.suppress(var0, var1) && promotionAllowed(var0, var1)) {
         Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
         if (snapshot == null) {
            return CompletableFuture.completedFuture(Optional.empty());
         }
         String var2 = snapshot.fingerprint();

         rotateGenerations(var0, snapshot, "load");
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
                     if (var4.getInt("schema") == SCHEMA
                        && GenerationStore.acceptsStoredFingerprint(generationDirectory, var2, storedFingerprint)
                        && var0.dimension().location().toString().equals(var4.getString("dimension"))
                        && var4.getInt("x") == var1.x
                        && var4.getInt("z") == var1.z
                        && var4.contains("chunk")
                        && NormalChunkStorage.fromTag(var4.getCompound("chunk")) == NormalChunkStorage.State.FULL) {
                        CompoundTag var5x = var4.getCompound("chunk");
                        if (!CacheEnvelope.completeEntities(var4)) {
                           quarantineRejected(var3);
                           return Optional.empty();
                        }
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
                        return Optional.of(var5x);
                     } else {
                        quarantineRejected(var3);
                        return Optional.empty();
                     }
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

   public static boolean pregenPersistenceSuppressed(ServerLevel level, ChunkPos pos) {
      Path handoff=PregenBorrowLedger.handoffCandidate(level,pos);
      if(handoff!=null) {
         CaptureFlight pending=CAPTURE_PENDING.get(captureKey(level,pos));
         try {
            // Existing cache repair claims the durable payload. A newly generated
            // target may still be publishing; in that case adoptLive creates the
            // restart-safe marker that prevents a delayed cache publication.
            if(Files.isRegularFile(handoff) || pending!=null) {
               if(!PromotionLedger.adoptLive(level,pos,handoff,pending==null?null:pending.result())) {
                  throw new IOException("pregen payload is no longer claimable at "+handoff);
               }
               PromotionLedger.entitiesRestored(level,pos);
            }
            // If capture already failed and no payload exists, there is no delayed
            // pregen state left to own; normal player persistence may become authoritative.
            PregenBorrowLedger.handedOff(level,pos);
            PipelineMetrics.count("pregen-borrow-player-handoff");
            return false;
         } catch(IOException ownershipFailure) {
            PromotionLedger.blockReplay(level,pos,handoff);
            throw new IllegalStateException(
               "Cannot hand pregen FULL to normal persistence at "+level.dimension().location()+" "+pos
                  +"; keeping normal writes blocked until cache ownership is unambiguous",
               ownershipFailure
            );
         }
      }
      return PregenBorrowLedger.suppressPersistence(level,pos);
   }

   public static boolean pregenPoiPersistenceSuppressed(Object poiManager, ChunkPos pos) {
      ServerLevel level=PregenBorrowLedger.levelForPoi(poiManager,pos);
      return level!=null&&pregenPersistenceSuppressed(level,pos);
   }

   public static void pregenBorrowUnloaded(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.unloaded(level,pos);
   }

   /** IOWorker.store's Future completes after RegionFileStorage.write, not just enqueue. */
   public static void prepareNormalWrite(ServerLevel level, ChunkPos pos, CompoundTag tag) {
      if(NormalChunkStorage.fromTag(tag)!=NormalChunkStorage.State.FULL
         || BackfillGuard.suppress(level,pos) || pregenPersistenceSuppressed(level,pos)) return;
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
      if (NormalChunkStorage.fromTag(tag) != NormalChunkStorage.State.FULL
         || BackfillGuard.suppress(level,pos) || pregenPersistenceSuppressed(level,pos)) return;
      if (PromotionLedger.observeChunkWrite(level,pos,written)) return;
      Fingerprint.Snapshot fingerprint=Fingerprint.snapshotForLevel(level);
      if (fingerprint==null) return;
      Path file=path(level,pos,fingerprint.fingerprint());
      written.whenComplete((ignored,error) -> {
         if(error==null) {
            PREGEN_RETRY.remove(captureKey(level,pos));
            retireClaim(file, () -> {});
         }
      });
   }

   static boolean retireClaim(Path file, Runnable completed) {
      return retireClaim(file,completed,() -> {});
   }

   static boolean retireClaim(Path file, Runnable completed, Runnable retryableFailure) {
      return submitMaintenance(() -> {
         try {
            long bytes=Files.isRegularFile(file)?Files.size(file):0L;
            Files.deleteIfExists(file);
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

   enum CacheState { ABSENT, COMPLETE, CLAIMED, UNKNOWN }

   static CacheState cacheState(ServerLevel var0, ChunkPos var1, String var2) {
      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
      if (snapshot != null && snapshot.fingerprint().equals(var2)) {
         rotateGenerations(var0, snapshot, "scan");
      }
      Path file = path(var0, var1, var2);
      // A claim blocks generation but does not by itself prove the LOD retry obligation
      // is complete: adoptLive can create a payload-free marker while capture is pending.
      try {
         if (RecoveryClaims.claimExists(PromotionClaim.claimedPath(file))) return CacheState.CLAIMED;
      } catch(IOException claimError) {
         PipelineMetrics.count("promotion-claim-inspection-failed");
         return CacheState.UNKNOWN;
      }
      if (!Files.isRegularFile(file)) {
         return CacheState.ABSENT;
      }

      try {
         CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.create(64L << 20));
         if (validEnvelope(root, var0, var1, var2)) {
            return CacheState.COMPLETE;
         }
         quarantineRejected(file);
         return CacheState.ABSENT;
      } catch (Throwable error) {
         quarantineRejected(file);
         LOG.warn("Ignoring invalid chunk cache {}; it remains eligible for backfill", new Object[]{file, error});
         return CacheState.ABSENT;
      }
   }

   private static boolean validEnvelope(CompoundTag root, ServerLevel level, ChunkPos pos, String fingerprint) {
      return root != null
         && root.getInt("schema") == SCHEMA
         && CacheEnvelope.completeEntities(root)
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

   private static Path quarantineRejected(Path file) {
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

   public static void stop(MinecraftServer server) {
      STOPPING.add(server);
      if(currentServer==server) { currentServer=null; activeRoots=List.of(); }
      // No join on server callbacks/native I/O during shutdown. Detached queued captures are
      // invalidated; a running writer checks this flag again before atomic publication.
      CAPTURE_PENDING.forEach((key,flight) -> { if(key.level().getServer()==server) flight.result().complete(false); });
      LIVE_INSPECTIONS.forEach(permit -> { if(permit.server==server) permit.close(); });
      PromotionLedger.clear(server);
      PregenBorrowLedger.clear(server);
      PREGEN_RETRY.removeIf(key -> key.level().getServer()==server);
      PREGEN_LOD_RETRY.removeIf(key -> key.level().getServer()==server);
      PREGEN_NEW_TARGETS.keySet().removeIf(key -> key.level().getServer()==server);
      PREGEN_REQUEST_TARGETS.removeIf(key -> key.level().getServer()==server);
      LOD_PENDING.keySet().removeIf(key -> key.level().getServer()==server);
      CONFIRMED_PREGEN.keySet().removeIf(key -> key.level().getServer()==server);
      for(ServerLevel level:server.getAllLevels()) {
         Path dimension=cacheRoot(level).resolve(dimensionKey(level));
         RecoveryClaims.clear(dimension);
         NEXT_LOCAL_RECOVERY_WARNING.remove(dimension.toAbsolutePath().normalize());
      }
      synchronized(NEXT_MAINTENANCE) { NEXT_MAINTENANCE.remove(server); }
      synchronized(NEXT_DISK_REFRESH) { NEXT_DISK_REFRESH.remove(server); }
   }

   private static void rotateGenerations(ServerLevel var0, Fingerprint.Snapshot snapshot, String trigger) {
      String var2 = cacheRoot(var0).toAbsolutePath().normalize() + "|" + dimensionKey(var0) + "|" + snapshot.fingerprint();
      if (System.nanoTime()<ROTATION_RETRY.getOrDefault(var2,0L)) return;
      if (ROTATED.add(var2)) {
         try {
            WRITE_IO.execute(() -> {
               try {
                  if (!GenerationStore.reconcile(var0, snapshot, trigger)) {
                     ROTATION_RETRY.put(var2,System.nanoTime()+60_000_000_000L);
                     ROTATED.remove(var2);
                  } else ROTATION_RETRY.remove(var2);
               }
               finally { CacheDiskBudget.at(cacheRoot(var0)).invalidateInventory(); }
            });
         } catch (RejectedExecutionException var5) {
            ROTATED.remove(var2);
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
}
