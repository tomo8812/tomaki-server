package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
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
   private static final ConcurrentHashMap<String, CompletableFuture<Boolean>> ROTATION_RESULTS = new ConcurrentHashMap<>();
   private static final Map<String,Long> ROTATION_RETRY = new ConcurrentHashMap<>();
   private static final Map<CaptureKey, CaptureFlight> CAPTURE_PENDING = new ConcurrentHashMap<>();
   // Retry state covers FULL storage ownership only.
   private static final Set<CaptureKey> PREGEN_RETRY = ConcurrentHashMap.newKeySet();
   private static final ConcurrentHashMap<CaptureKey,Long> PREGEN_NEW_TARGETS = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<CaptureKey,GenerationTask> PREGEN_REQUEST_TARGETS = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<CaptureKey,Path> CONFIRMED_PREGEN = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<CaptureKey,FileStamp> CONFIRMED_PREGEN_STAMP = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Path,ConcurrentHashMap<String,Long>> NEXT_LOCAL_RECOVERY_WARNING = new ConcurrentHashMap<>();
   private static final Map<MinecraftServer, Long> NEXT_MAINTENANCE = new WeakHashMap<>();
   private static final Map<MinecraftServer, List<Path>> STOPPING_DIMENSIONS = new WeakHashMap<>();
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

   public static GenerationTask currentChunksmithRequestTask() {
      return FullTelemetryBridge.currentTask();
   }

   /** Snapshot only this request's temporary FULL; LOD is entirely native. */
   public static void captureFromChunksmith(ServerLevel level, LevelChunk chunk, GenerationTask sourceTask) {
      if(level==null||chunk==null||sourceTask==null)return;
      ChunkPos pos=chunk.getPos();
      CaptureKey key=captureKey(level,pos);
      String token=FullTelemetryBridge.token(level,pos,sourceTask);
      if(PREGEN_REQUEST_TARGETS.get(key)!=sourceTask) { FullTelemetryBridge.failed(token); return; }
      boolean generatedTarget=PREGEN_NEW_TARGETS.remove(key)!=null;
      if(PregenBorrowLedger.kind(level,pos)==PregenBorrowLedger.Kind.BORROWED || !generatedTarget) {
         FullTelemetryBridge.ready(token);
         return;
      }
      if(!level.getServer().isSameThread() || !FollowCaptureOwnership.owns(level)
         || PregenBorrowLedger.kind(level,pos)!=PregenBorrowLedger.Kind.GENERATED
         || PregenBorrowLedger.isHandedOff(level,pos)) {
         FullTelemetryBridge.failed(token);
         return;
      }
      CompletableFuture<Boolean> saved=capture(level,chunk,token);
      PregenBorrowLedger.attachCapture(level,pos,saved);
      saved.whenComplete((ok,error)->{
         if(error==null&&Boolean.TRUE.equals(ok)) {
            Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
            if(snapshot!=null&&!STOPPING.contains(level.getServer()))
               confirmPregenFull(key,path(level,pos,snapshot.fingerprint()));
         } else addRetry(PREGEN_RETRY,key);
      });
   }

   /** Storage integration called by the sole Controller generation hook. */
   public static CompletableFuture<Boolean> observeGenerated(com.kishku7.chunksmith.platform.World world,
      int x,int z,GenerationTask task,CompletableFuture<Boolean> nativeResult) {
      if(!(world instanceof com.kishku7.chunksmith.platform.NeoForgeWorld neoforge))return nativeResult;
      ServerLevel level=neoforge.getWorld();
      ChunkPos pos=new ChunkPos(x,z);
      return protectChunksmithGeneratedResult(level,pos,task,nativeResult).whenComplete((generated,error)->{
         if(error==null)noteChunksmithGeneratedResult(level,pos,task,Boolean.TRUE.equals(generated));
         else noteChunksmithGeneratedFailure(level,pos,task);
      });
   }

   public static CompletableFuture<Void> request(com.kishku7.chunksmith.platform.World world,
      int x,int z,GenerationTask task,java.util.function.Supplier<CompletableFuture<Void>> nativeRequest) {
      if(!(world instanceof com.kishku7.chunksmith.platform.NeoForgeWorld neoforge))return nativeRequest.get();
      ServerLevel level=neoforge.getWorld();
      ChunkPos pos=new ChunkPos(x,z);
      CompletableFuture<Boolean> classified=PREGEN_NEW_TARGETS.containsKey(captureKey(level,pos))
         ? CompletableFuture.completedFuture(false):confirmNativeGenerated(level,pos,captureKey(level,pos));
      return classified.thenCompose(generated->{
         noteChunksmithGeneratedResult(level,pos,task,generated);
         // FULL ownership is storage policy, independent of resource admission.
         if(!preparePregenRequest(level,pos,task)) {
            pregenRequestFailed(level,pos,task);
            return CompletableFuture.failedFuture(new IllegalStateException("FULL cache ownership unavailable at "+pos));
         }
         try {
            return nativeRequest.get().whenComplete((value,error)->pregenRequestFinished(level,pos,task,error==null));
         } catch(Throwable error) { pregenRequestFailed(level,pos,task); throw error; }
      });
   }

   public static boolean pregenRetryPending(MinecraftServer server, int centerX, int centerZ, int radiusBlocks) {
      if(server==null)return false;
      long radius=(long)Math.max(0,radiusBlocks)+16L;
      long radiusSquared=radius*radius;
      if(anyInRange(server,PREGEN_RETRY,centerX,centerZ,radiusSquared))return true;
      if(anyInRange(server,PREGEN_NEW_TARGETS.keySet(),centerX,centerZ,radiusSquared))return true;
      return anyInRange(server,CAPTURE_PENDING.keySet(),centerX,centerZ,radiusSquared);
   }

   public static void noteChunksmithGeneratedResult(ServerLevel level,ChunkPos pos,GenerationTask sourceTask,boolean generated) {
      if(level==null||pos==null||sourceTask==null||STOPPING.contains(level.getServer())
         || !FollowCaptureOwnership.active(level,sourceTask))return;
      CaptureKey key=captureKey(level,pos);
      if(generated) PREGEN_NEW_TARGETS.remove(key);
      else PREGEN_NEW_TARGETS.put(key,System.currentTimeMillis());
   }

   public static void noteChunksmithGeneratedFailure(ServerLevel level,ChunkPos pos,GenerationTask sourceTask) {
      if(level==null||pos==null||sourceTask==null||STOPPING.contains(level.getServer())
         || !FollowCaptureOwnership.active(level,sourceTask))return;
      CaptureKey key=captureKey(level,pos);
      PREGEN_NEW_TARGETS.remove(key);
      addRetry(PREGEN_RETRY,key);
      PipelineMetrics.count("pregen-generated-state-failed");
   }

   public static boolean preparePregenRequest(ServerLevel level,ChunkPos pos,GenerationTask sourceTask) {
      if(level==null||pos==null)return false;
      if(STOPPING.contains(level.getServer()))return false;
      // Classify the actual task that owns this asynchronous request. Looking up
      // only the current world task loses identity after GenerationTask's 60s drain
      // timeout and can let a stopped follow task generate without persistence guards.
      if(!FollowCaptureOwnership.owns(sourceTask))return true;
      if(!FollowCaptureOwnership.active(level,sourceTask)) {
         PipelineMetrics.count("pregen-stale-follow-request-blocked");
         return false;
      }
      CaptureKey key=captureKey(level,pos);
      GenerationTask admitted=PREGEN_REQUEST_TARGETS.compute(key,(ignored,existing) ->
         existing==null || existing==sourceTask || !FollowCaptureOwnership.active(level,existing)
            ? sourceTask : existing);
      if(admitted!=sourceTask) {
         PipelineMetrics.count("pregen-request-owner-conflict");
         return false;
      }
      // A retry debt without an admitted NEW target means ownership is still
      // unproven (normal storage UNKNOWN, an active claim, or an I/O failure).
      // Chunksmith may request a missing LOD even after generated=true, so do
      // not let that request create an unguarded FULL chunk.
      if(PREGEN_RETRY.contains(key) && !PREGEN_NEW_TARGETS.containsKey(key)
         && PregenBorrowLedger.kind(level,pos)==null) {
         PipelineMetrics.count("pregen-unproven-full-request-blocked");
         return false;
      }
      if(!PREGEN_NEW_TARGETS.containsKey(key))return true;
      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if(snapshot==null) {
         addRetry(PREGEN_RETRY,key);
         return false;
      }
      Path file=path(level,pos,snapshot.fingerprint());
      PregenBorrowLedger.Kind kind=PregenBorrowLedger.kind(level,pos);
      if(kind!=null && kind!=PregenBorrowLedger.Kind.GENERATED)return false;
      // A guard from a stopped task cannot be reused by this request.
      if(PregenBorrowLedger.beginGenerated(level,pos,file,sourceTask))return true;
      addRetry(PREGEN_RETRY,key);
      return false;
   }

   /** Track FULL availability before a detached task's result is failed closed. */
   public static void notePregenFullLoaded(ServerLevel level,ChunkPos pos,GenerationTask sourceTask) {
      if(level==null||pos==null||sourceTask==null)return;
      if(PregenBorrowLedger.generatedFullLoaded(level,pos,sourceTask))
         PipelineMetrics.count("pregen-full-retained-after-failure");
   }

   public static boolean unifiedPregenRequestAdmitted(ServerLevel level,ChunkPos pos) {
      return level!=null&&pos!=null&&PREGEN_REQUEST_TARGETS.containsKey(captureKey(level,pos));
   }

   public static boolean unifiedPregenRequestAdmitted(ServerLevel level,ChunkPos pos,GenerationTask sourceTask) {
      return level!=null&&pos!=null&&sourceTask!=null
         && PREGEN_REQUEST_TARGETS.get(captureKey(level,pos))==sourceTask;
   }

   public static boolean unifiedPregenRequestActive(ServerLevel level,ChunkPos pos) {
      if(level==null||pos==null)return false;
      GenerationTask sourceTask=PREGEN_REQUEST_TARGETS.get(captureKey(level,pos));
      return sourceTask!=null&&FollowCaptureOwnership.active(level,sourceTask);
   }

   public static boolean unifiedPregenRequestActive(ServerLevel level,ChunkPos pos,GenerationTask sourceTask) {
      return unifiedPregenRequestAdmitted(level,pos,sourceTask)
         && FollowCaptureOwnership.active(level,sourceTask);
   }

   public static void pregenRequestFinished(ServerLevel level,ChunkPos pos,GenerationTask sourceTask,boolean success) {
      if(level==null||pos==null||sourceTask==null)return;
      CaptureKey key=captureKey(level,pos);
      // Only the exact follow task admitted for this coordinate may mutate unified
      // request state. A detached task's late completion must not erase a newer
      // task's marker or persistence guard for the same coordinate.
      if(!PREGEN_REQUEST_TARGETS.remove(key,sourceTask)) {
         // The task may have been replaced while its request was still in flight.
         // Only its own failed guard may be cancelled; never mutate the new task's state.
         if(!success)PregenBorrowLedger.cancel(level,pos,sourceTask);
         return;
      }
      if(STOPPING.contains(level.getServer())) {
         PREGEN_NEW_TARGETS.remove(key);
         return;
      }
      if(success)return;
      PREGEN_NEW_TARGETS.remove(key);
      addRetry(PREGEN_RETRY,key);
      // Cancel only when no FULL was observed. A stopped task may already have
      // a live FULL in vanilla's chunk manager, which still needs the save veto.
      PregenBorrowLedger.requestFailed(level,pos,sourceTask);
      PregenBorrowLedger.cancel(level,pos,sourceTask);
   }

   public static void pregenRequestFailed(ServerLevel level,ChunkPos pos,GenerationTask sourceTask) {
      pregenRequestFinished(level,pos,sourceTask,false);
   }

   public static boolean pregenReady(MinecraftServer server) {
      return server!=null && Fingerprint.snapshotForLevel(server.overworld())!=null;
   }

   private static boolean anyInRange(MinecraftServer server,Iterable<CaptureKey> keys,int centerX,int centerZ,long radiusSquared) {
      for(CaptureKey key:keys) {
         if(key.level().getServer()!=server)continue;
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

   public static CompletableFuture<Boolean> protectChunksmithGeneratedResult(
      ServerLevel level, ChunkPos pos, GenerationTask sourceTask, CompletableFuture<Boolean> nativeResult
   ) {
      if(level==null||pos==null||sourceTask==null||nativeResult==null)return nativeResult;
      if(!FollowCaptureOwnership.owns(sourceTask))return nativeResult;
      CaptureKey key=captureKey(level,pos);
      return nativeResult.thenCompose(generated -> {
         if(!FollowCaptureOwnership.active(level,sourceTask)) {
            PipelineMetrics.count("pregen-stale-generated-result-blocked");
            return CompletableFuture.failedFuture(
               new IllegalStateException("Unified pregen generated-state result belongs to a stopped or detached task at "+pos));
         }
         if(Boolean.TRUE.equals(generated))return confirmNativeGenerated(level,pos,key);
         CompletableFuture<Boolean> cacheDecision=chunksmithGeneratedDecision(level,pos);
         return cacheDecision==null?CompletableFuture.completedFuture(false):cacheDecision;
      });
   }

   private static CompletableFuture<Boolean> confirmNativeGenerated(ServerLevel level,ChunkPos pos,CaptureKey key) {
      CompletableFuture<Boolean> cacheDecision=chunksmithGeneratedDecision(level,pos);
      if(cacheDecision==null)return confirmNormalFull(level,pos,key);
      return cacheDecision.handle((accepted,error) -> error==null?accepted:null)
         .thenCompose(accepted -> Boolean.TRUE.equals(accepted)
            ? CompletableFuture.completedFuture(true)
            : confirmNormalFull(level,pos,key));
   }

   private static CompletableFuture<Boolean> confirmNormalFull(ServerLevel level,ChunkPos pos,CaptureKey key) {
      try {
         return NormalChunkStorage.inspectAsync(level,pos).handle((normal,error) -> {
            if(error==null && normal==NormalChunkStorage.State.FULL) {
               PregenBorrowLedger.releaseGeneratedToNormal(level,pos);
               PREGEN_RETRY.remove(key);
               PREGEN_NEW_TARGETS.remove(key);
               return true;
            }
            addRetry(PREGEN_RETRY,key);
            if(error!=null || normal==null || normal==NormalChunkStorage.State.UNKNOWN) {
               PipelineMetrics.count("pregen-normal-full-inspection-unknown");
               // generated=true is not a safe way to stop: Chunksmith will
               // still request a FULL chunk if its LOD is missing. Complete
               // exceptionally so neither generation nor LOD repair starts.
               throw new java.util.concurrent.CompletionException(
                  new IllegalStateException("Normal FULL ownership is unknown at "+pos,error));
            }
            return false;
         });
      } catch(Throwable inspectFailure) {
         addRetry(PREGEN_RETRY,key);
         PipelineMetrics.count("pregen-normal-full-inspection-unknown");
         return CompletableFuture.failedFuture(inspectFailure);
      }
   }

   public static CompletableFuture<Boolean> chunksmithGeneratedDecision(ServerLevel level, ChunkPos pos) {
      if(level==null||pos==null)return null;
      if(STOPPING.contains(level.getServer()))return CompletableFuture.completedFuture(true);
      CaptureKey key=captureKey(level,pos);
      CaptureFlight pending=CAPTURE_PENDING.get(key);
      if(pending!=null)return pending.result().thenApply(Boolean.TRUE::equals);

      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if(snapshot==null)return null;
      String fingerprint=snapshot.fingerprint();
      Path file=path(level,pos,fingerprint);
      if(confirmedPregenMatches(key,file)) {
         PREGEN_RETRY.remove(key);
         return CompletableFuture.completedFuture(true);
      }

      Path claim=PromotionClaim.claimedPath(file);
      try {
         if(RecoveryClaims.claimExists(claim)) {
            addRetry(PREGEN_RETRY,key);
            return CompletableFuture.completedFuture(true);
         }
      } catch(IOException ownershipUnknown) {
         addRetry(PREGEN_RETRY,key);
         PipelineMetrics.count("promotion-claim-inspection-failed");
         return CompletableFuture.completedFuture(true);
      }
      if(!Files.isRegularFile(file))return null;

      try {
         return CompletableFuture.supplyAsync(() -> {
            try(var pathLock=CachePathLocks.lock(file)) {
               try {
                  if(RecoveryClaims.claimExists(claim)) {
                     addRetry(PREGEN_RETRY,key);
                     return true;
                  }
                  CompoundTag envelope=NbtIo.readCompressed(file,NbtAccounter.create(64L<<20));
                  if(validEnvelope(envelope,level,pos,fingerprint)) {
                     if(!STOPPING.contains(level.getServer()))confirmPregenFull(key,file);
                     else addRetry(PREGEN_RETRY,key);
                     PipelineMetrics.count("pregen-full-validated");
                     return true;
                  }
                  clearConfirmedPregen(key);
                  quarantineRejected(file);
                  PipelineMetrics.count("pregen-full-invalid");
                  return false;
               } catch(IOException ownershipOrReadFailure) {
                  clearConfirmedPregen(key);
                  try {
                     if(RecoveryClaims.claimExists(claim)) {
                        addRetry(PREGEN_RETRY,key);
                        return true;
                     }
                  } catch(IOException ownershipUnknown) {
                     addRetry(PREGEN_RETRY,key);
                     return true;
                  }
                  quarantineRejected(file);
                  PipelineMetrics.count("pregen-full-invalid");
                  return false;
               } catch(Throwable invalid) {
                  clearConfirmedPregen(key);
                  quarantineRejected(file);
                  PipelineMetrics.count("pregen-full-invalid");
                  return false;
               }
            }
         },READ_IO);
      } catch(RejectedExecutionException full) {
         addRetry(PREGEN_RETRY,key);
         return CompletableFuture.completedFuture(true);
      }
   }

   private record FileStamp(long size, FileTime modified) {}

   private static FileStamp fileStamp(Path file) {
      if(file==null)return null;
      try {
         BasicFileAttributes attrs=Files.readAttributes(file,BasicFileAttributes.class);
         return attrs.isRegularFile()?new FileStamp(attrs.size(),attrs.lastModifiedTime()):null;
      } catch(IOException unavailable) {
         return null;
      }
   }

   private static boolean confirmPregenFull(CaptureKey key,Path file) {
      FileStamp stamp=fileStamp(file);
      if(stamp==null) {
         clearConfirmedPregen(key);
         addRetry(PREGEN_RETRY,key);
         return false;
      }
      CONFIRMED_PREGEN.put(key,file);
      CONFIRMED_PREGEN_STAMP.put(key,stamp);
      PREGEN_RETRY.remove(key);
      return true;
   }

   private static void clearConfirmedPregen(CaptureKey key) {
      if(key==null)return;
      CONFIRMED_PREGEN.remove(key);
      CONFIRMED_PREGEN_STAMP.remove(key);
   }

   private static boolean confirmedPregenMatches(CaptureKey key,Path expected) {
      Path confirmed=CONFIRMED_PREGEN.get(key);
      if(confirmed==null)return false;
      FileStamp recorded=CONFIRMED_PREGEN_STAMP.get(key);
      FileStamp current=expected==null?null:fileStamp(expected);
      if(expected!=null&&expected.equals(confirmed)&&recorded!=null&&recorded.equals(current))return true;
      clearConfirmedPregen(key);
      addRetry(PREGEN_RETRY,key);
      PipelineMetrics.count("pregen-full-confirmation-invalidated");
      return false;
   }

   private record CaptureKey(ServerLevel level, long pos) {}

   private static void addRetry(Set<CaptureKey> retrySet,CaptureKey key) {
      if(key!=null&&!STOPPING.contains(key.level().getServer())) retrySet.add(key);
   }

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

   static CompletableFuture<Boolean> capture(ServerLevel var0, LevelChunk var1, String token) {
      if (!var0.getServer().isSameThread()) throw new IllegalStateException("Chunk snapshot requires server thread");
      String source = "direct";
      CaptureKey key = captureKey(var0, var1.getPos());
      CompletableFuture<Boolean> result = new CompletableFuture<>();
      CaptureTelemetry telemetry=new CaptureTelemetry(result,new CaptureTelemetry.Events(){
         public void started(String stage){FullTelemetryBridge.started(token,stage);}
         public void finished(String stage,long nanos,boolean success){FullTelemetryBridge.finished(token,stage,nanos,success);}
         public void failed(){FullTelemetryBridge.failed(token);}
      });
      CaptureFlight flight = new CaptureFlight(result, System.nanoTime());
      CaptureFlight pending = CAPTURE_PENDING.putIfAbsent(key, flight);
      if (pending != null) {
         PipelineMetrics.count("capture-duplicate-avoided");
         FullTelemetryBridge.failed(token);
         return pending.result();
      }
      result.whenComplete((ok, error) -> {
         PipelineMetrics.count(source + (Boolean.TRUE.equals(ok) ? "-saved" : "-failed"));
         PipelineMetrics.time("capture-total", System.nanoTime() - flight.startedNanos());
         CAPTURE_PENDING.remove(key, flight);
      });
      PipelineMetrics.count(source + "-capture-started");
      try {
         captureSnapshot(var0, var1, result, token, telemetry);
      } catch (Throwable error) {
         LOG.warn("Could not capture chunk {}; regeneration remains available", var1.getPos(), error);
         result.complete(false);
      }
      return result;
   }

   /** Takes the complete snapshot synchronously, while the caller still owns its native ticket. */
   private static void captureSnapshot(ServerLevel level, LevelChunk chunk, CompletableFuture<Boolean> result, String token, CaptureTelemetry telemetry) {
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
      CaptureWork work = new CaptureWork(memory, storage, result, inspection, token, telemetry);
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
         envelope.putBoolean("fullCommitted", true);
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
      if (!work.validationStarted()) { work.inspection.close(); return; }
      long started = System.nanoTime();
      CompletableFuture<NormalChunkStorage.State> inspected;
      try {
         RecoveryClaims.initialize(cacheRoot(level).resolve(dimensionKey(level)));
         inspected = NormalChunkStorage.inspectAsync(level, pos);
      }
      catch (Throwable error) { work.validationFinished(false); work.inspection.close(); work.result.complete(false); return; }
      inspected.whenComplete((normal, error) -> {
         work.validationFinished(error==null && normal!=NormalChunkStorage.State.UNKNOWN);
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
         if (normal == NormalChunkStorage.State.FULL) {
            PregenBorrowLedger.releaseGeneratedToNormal(level,pos);
            PREGEN_RETRY.remove(captureKey(level,pos));
            PipelineMetrics.count("capture-normal-full-won");
            work.result.complete(false);
            return;
         }
         if (error != null || normal == NormalChunkStorage.State.UNKNOWN
            || claimed || STOPPING.contains(level.getServer())) {
            PipelineMetrics.count("capture-normal-storage-rejected"); work.result.complete(false); return;
         }
         long writeQueuedAt=System.nanoTime();
         try { WRITE_IO.execute(() -> publishCapture(level.getServer(), file, work, writeQueuedAt)); }
         catch (RejectedExecutionException rejected) { PipelineMetrics.count("capture-queue-rejected"); work.result.complete(false); }

      });
   }

   private static void publishCapture(MinecraftServer server, Path file, CaptureWork work, long queuedAt) {
      CompoundTag envelope = work.beginWrite();
      if (envelope == null) return;
      PipelineMetrics.time("cache-queue", System.nanoTime()-queuedAt);
      boolean published=false;
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
         published=true;
         FullTelemetryBridge.ready(work.token);
         work.result.complete(true);
      } catch (Throwable error) {
         LOG.warn("Could not write chunk cache {}", file, error);
         work.result.complete(false);
      } finally {
         try { Files.deleteIfExists(temporary); } catch (IOException ignored) {}
         work.endWrite(published);
      }
   }

   private static final class CaptureWork {
      final AtomicReference<CompoundTag> snapshot = new AtomicReference<>();
      final CaptureBudget.Lease memory;
      final CacheDiskBudget.Lease storage;
      final CompletableFuture<Boolean> result;
      final InspectionPermit inspection;
      final String token;
      final CaptureTelemetry telemetry;
      long validationStart,writeStart;
      private boolean writing;
      CaptureWork(CaptureBudget.Lease memory, CacheDiskBudget.Lease storage, CompletableFuture<Boolean> result, InspectionPermit inspection, String token, CaptureTelemetry telemetry) {
         this.memory=memory; this.storage=storage; this.result=result; this.inspection=inspection;
         this.token=token;this.telemetry=telemetry;
      }
      synchronized CompoundTag beginWrite() {
         if(snapshot.get()==null || !telemetry.start("FULL"))return null;
         CompoundTag tag=snapshot.getAndSet(null);
         writing=tag!=null;
         if(writing) { writeStart=System.nanoTime();  }
         return tag;
      }
      synchronized boolean validationStarted() {
         if(!telemetry.start("VALIDATE"))return false;
         validationStart=System.nanoTime();return true;
      }
      void validationFinished(boolean success) {
         telemetry.finish("VALIDATE",System.nanoTime()-validationStart,success);
      }
      synchronized void terminal() { snapshot.set(null); if (!writing) { memory.close(); storage.close(); } }
      synchronized void endWrite(boolean success) {
         telemetry.finish("FULL",System.nanoTime()-writeStart,success);
         writing=false; memory.close(); storage.close();
      }
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
         PregenBorrowLedger.releaseGeneratedToNormal(level,pos);
         PromotionLedger.normalFullPresent(level,pos);
         return CompletableFuture.completedFuture(normal);
      }

      if (pregenBorrowAllowed(level,pos)) {
         return loadForPregen(level,pos).thenApply(cached -> cached.isPresent() ? cached : normal);
      }
      return loadIfPresent(level, pos).thenApply(cached -> cached.isPresent() ? cached : normal);
   }

   private static boolean pregenBorrowAllowed(ServerLevel level, ChunkPos pos) {
      // Only the coordinate selected by GenerationTask may borrow pregen FULL.
      // Dependency reads keep normal-world semantics; borrow only the requested FULL.
      return FollowCaptureOwnership.owns(level)
         && PREGEN_REQUEST_TARGETS.containsKey(captureKey(level,pos));
   }

   private static CompletableFuture<Optional<CompoundTag>> loadForPregen(ServerLevel level, ChunkPos pos) {
      // A GENERATED guard is armed before getChunkAtAsync precisely so the target
      // can reach FULL without entering normal persistence. Do not wait on its own
      // future FULL publication or try to load a cache that does not exist yet.
      if(PregenBorrowLedger.kind(level,pos)==PregenBorrowLedger.Kind.GENERATED)
         return CompletableFuture.completedFuture(Optional.empty());

      CompletableFuture<Boolean> active=PregenBorrowLedger.pending(level,pos);
      if(active!=null) return active.handle((ignored,error)->null).thenCompose(ignored -> loadForPregen(level,pos));

      Fingerprint.Snapshot snapshot=Fingerprint.snapshotForLevel(level);
      if(snapshot==null)return CompletableFuture.completedFuture(Optional.empty());
      String fingerprint=snapshot.fingerprint();
      CompletableFuture<Boolean> rotation=rotateGenerations(level,snapshot,"pregen-read");
      if(!rotation.isDone()) {
         return rotation.thenCompose(ok -> Boolean.TRUE.equals(ok)
            ? loadForPregen(level,pos)
            : reconciliationUnavailable(level,pos,"pregen-read"));
      }
      if(!Boolean.TRUE.equals(rotation.getNow(false)))
         return reconciliationUnavailable(level,pos,"pregen-read");
      Path file=path(level,pos,fingerprint);
      Path dimension=cacheRoot(level).resolve(dimensionKey(level));
      // Repair is a cache load too: prove absence of coordinate ownership in
      // canonical, historical, quarantine, and conflicting promotion paths.
      return ownershipForLoad(dimension,pos)
         .whenComplete((claim,error) -> {
            if(error!=null) {
               PromotionLedger.blockReplay(level,pos,RecoveryClaims.root(dimension));
               addRetry(PREGEN_RETRY,captureKey(level,pos));
            }
         })
         .thenCompose(ownership -> {
            if(ownership!=null) {
               PromotionLedger.blockReplay(level,pos,ownership);
               addRetry(PREGEN_RETRY,captureKey(level,pos));
               return CompletableFuture.failedFuture(new IOException("Promotion ownership is active at "+ownership));
            }
            if(!Files.isRegularFile(file))return CompletableFuture.completedFuture(Optional.empty());

            try {
               return CompletableFuture.supplyAsync(() -> {
            try(var pathLock=CachePathLocks.lock(file)) {
               try {
                  // Ownership can change while validation is queued; re-prove it
                  // immediately before borrowing, without quarantining a valid FULL.
                  Path currentClaim;
                  try {
                     currentClaim=RecoveryClaims.claimForCoordinate(dimension,pos.x,pos.z);
                  } catch(IOException ownershipFailure) {
                     PromotionLedger.blockReplay(level,pos,RecoveryClaims.root(dimension));
                     addRetry(PREGEN_RETRY,captureKey(level,pos));
                     throw new java.io.UncheckedIOException(ownershipFailure);
                  }
                  if(currentClaim!=null) {
                     PromotionLedger.blockReplay(level,pos,currentClaim);
                     addRetry(PREGEN_RETRY,captureKey(level,pos));
                     throw new java.io.UncheckedIOException(new IOException("Promotion ownership changed at "+currentClaim));
                  }
                  CompoundTag envelope=NbtIo.readCompressed(file,NbtAccounter.create(64L<<20));
                  if(!validEnvelope(envelope,level,pos,fingerprint)) {
                     quarantineRejected(file);
                     addRetry(PREGEN_RETRY,captureKey(level,pos));
                     return Optional.<CompoundTag>empty();
                  }
                  if(STOPPING.contains(level.getServer()))return Optional.<CompoundTag>empty();
                  if(!confirmPregenFull(captureKey(level,pos),file))return Optional.<CompoundTag>empty();
                  if(!PregenBorrowLedger.beginBorrowed(level,pos,file)) {
                     addRetry(PREGEN_RETRY,captureKey(level,pos));
                     return Optional.<CompoundTag>empty();
                  }
                  PipelineMetrics.count("pregen-full-borrowed");
                  return Optional.of(envelope.getCompound("chunk"));
               } catch(java.io.UncheckedIOException ownershipFailure) {
                  throw ownershipFailure;
               } catch(Throwable invalid) {
                  quarantineRejected(file);
                  addRetry(PREGEN_RETRY,captureKey(level,pos));
                  LOG.warn("Pregen FULL {} could not be validated for a Chunksmith load; quarantined for coordinate-local regeneration",file,invalid);
                  return Optional.<CompoundTag>empty();
               }
            }
         },READ_IO);
            } catch(RejectedExecutionException full) {
               addRetry(PREGEN_RETRY,captureKey(level,pos));
               return CompletableFuture.failedFuture(full);
            }
         });
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

   private static <T> CompletableFuture<T> reconciliationUnavailable(
      ServerLevel level, ChunkPos pos, String trigger
   ) {
      Path dimension=cacheRoot(level).resolve(dimensionKey(level));
      PromotionLedger.blockReplay(level,pos,RecoveryClaims.root(dimension));
      PipelineMetrics.count("cache-generation-reconcile-blocked");
      return CompletableFuture.failedFuture(new IOException(
         "Chunk-cache generation reconciliation is unavailable for "
            +dimensionKey(level)+" "+pos+" trigger="+trigger
            +"; replacement generation remains blocked until reconciliation succeeds"));
   }

   private static CompletableFuture<Optional<CompoundTag>> loadReadyCache(ServerLevel var0, ChunkPos var1) {
      CompletableFuture<Boolean> borrowed=PregenBorrowLedger.pending(var0,var1);
      if(borrowed!=null) {
         return borrowed.handle((ignored,error)->null).thenCompose(ignored -> loadReadyCache(var0,var1));
      }
      if (promotionAllowed(var0, var1)) {
         Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
         if (snapshot == null) {
            return CompletableFuture.completedFuture(Optional.empty());
         }
         String var2 = snapshot.fingerprint();

         CompletableFuture<Boolean> rotation=rotateGenerations(var0, snapshot, "load");
         // Wait asynchronously for an approved historical FULL to be adopted.
         // Otherwise the first player request can miss the old cache and generate again.
         if(!rotation.isDone()) {
            return rotation.thenCompose(ok -> Boolean.TRUE.equals(ok)
               ? loadReadyCache(var0,var1)
               : reconciliationUnavailable(var0,var1,"load"));
         }
         if(!Boolean.TRUE.equals(rotation.getNow(false)))
            return reconciliationUnavailable(var0,var1,"load");
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

   public static boolean playerLoadReady(ServerLevel level, ChunkPos pos) {
      return level!=null&&pos!=null&&!PregenBorrowLedger.suppressPersistence(level,pos)
         &&!PromotionLedger.replayBlocked(level,pos);
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

   public static boolean suppressPregenChunkSave(ServerLevel level,ChunkAccess chunk) {
      return level!=null&&chunk!=null&&PregenBorrowLedger.suppressChunkSave(level,chunk);
   }

   public static boolean pregenPoiPersistenceSuppressed(Object poiManager, ChunkPos pos) {
      ServerLevel level=PregenBorrowLedger.levelForPoi(poiManager,pos);
      return level!=null&&pregenPersistenceSuppressed(level,pos);
   }

   public static boolean suppressPregenPoiPersistence(Object poiManager, ChunkPos pos) {
      ServerLevel level=PregenBorrowLedger.levelForPoi(poiManager,pos);
      if(level==null||!pregenPersistenceSuppressed(level,pos))return false;
      return PregenBorrowLedger.suppressPoiSave(level,pos,poiManager);
   }

   public static void pregenBorrowUnloaded(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.unloaded(level,pos);
   }

   /** IOWorker.store's Future completes after RegionFileStorage.write, not just enqueue. */
   public static void prepareNormalWrite(ServerLevel level, ChunkPos pos, CompoundTag tag) {
      if(NormalChunkStorage.fromTag(tag)!=NormalChunkStorage.State.FULL
         || pregenPersistenceSuppressed(level,pos)) return;
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
         || pregenPersistenceSuppressed(level,pos)) return;
      if (PromotionLedger.observeChunkWrite(level,pos,written)) return;
      Fingerprint.Snapshot fingerprint=Fingerprint.snapshotForLevel(level);
      if (fingerprint==null) return;
      Path file=path(level,pos,fingerprint.fingerprint());
      written.whenComplete((ignored,error) -> {
         if(error==null) {
            // This write may have started just before beginGenerated armed the
            // pregen guard. Once durable normal FULL wins, normal persistence
            // must no longer be vetoed for this coordinate.
            PregenBorrowLedger.releaseGeneratedToNormal(level,pos);
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
         CacheRetentionPolicy policy = CacheRetentionPolicy.load(server.getServerDirectory());
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

   /**
    * ServerStoppingEvent fires before MinecraftServer.stopServer() performs the final
    * world save. Close new/detached work here, but keep coordinate persistence guards
    * and promotion ownership alive until ServerStoppedEvent.
    */
   public static void stop(MinecraftServer server) {
      STOPPING.add(server);
      ArrayList<Path> stoppingDimensions=new ArrayList<>();
      for(ServerLevel level:server.getAllLevels())
         stoppingDimensions.add(cacheRoot(level).resolve(dimensionKey(level)).toAbsolutePath().normalize());
      synchronized(STOPPING_DIMENSIONS) { STOPPING_DIMENSIONS.put(server,List.copyOf(stoppingDimensions)); }
      if(currentServer==server) { currentServer=null; activeRoots=List.of(); }
      // No join on server callbacks/native I/O during shutdown. Detached queued captures are
      // invalidated; a running writer checks this flag again before atomic publication.
      CAPTURE_PENDING.forEach((key,flight) -> { if(key.level().getServer()==server) flight.result().complete(false); });
      LIVE_INSPECTIONS.forEach(permit -> { if(permit.server==server) permit.close(); });
      synchronized(NEXT_MAINTENANCE) { NEXT_MAINTENANCE.remove(server); }
      synchronized(NEXT_DISK_REFRESH) { NEXT_DISK_REFRESH.remove(server); }
   }

   /** Final cleanup only after vanilla/NeoForge have finished the shutdown save/close path. */
   public static void stopped(MinecraftServer server) {
      // Crash shutdown can reach ServerStoppedEvent without ServerStoppingEvent.
      // Keep this weak stop marker after cleanup so detached callbacks cannot
      // resurrect retry state or publish new cache data for a dead server.
      STOPPING.add(server);
      CAPTURE_PENDING.forEach((key,flight) -> { if(key.level().getServer()==server) flight.result().complete(false); });
      LIVE_INSPECTIONS.forEach(permit -> { if(permit.server==server) permit.close(); });
      PromotionLedger.clear(server);
      PregenBorrowLedger.clear(server);
      PREGEN_RETRY.removeIf(key -> key.level().getServer()==server);
      PREGEN_NEW_TARGETS.keySet().removeIf(key -> key.level().getServer()==server);
      PREGEN_REQUEST_TARGETS.keySet().removeIf(key -> key.level().getServer()==server);
      CONFIRMED_PREGEN.keySet().removeIf(key -> key.level().getServer()==server);
      CONFIRMED_PREGEN_STAMP.keySet().removeIf(key -> key.level().getServer()==server);
      List<Path> stoppingDimensions;
      synchronized(STOPPING_DIMENSIONS) { stoppingDimensions=STOPPING_DIMENSIONS.remove(server); }
      if(stoppingDimensions==null) {
         ArrayList<Path> fallback=new ArrayList<>();
         for(ServerLevel level:server.getAllLevels())
            fallback.add(cacheRoot(level).resolve(dimensionKey(level)).toAbsolutePath().normalize());
         stoppingDimensions=List.copyOf(fallback);
      }
      for(Path dimension:stoppingDimensions) {
         RecoveryClaims.clear(dimension);
         NEXT_LOCAL_RECOVERY_WARNING.remove(dimension);
      }
   }

   private static CompletableFuture<Boolean> rotateGenerations(
      ServerLevel level, Fingerprint.Snapshot snapshot, String trigger
   ) {
      String key=cacheRoot(level).toAbsolutePath().normalize() + "|"
         + dimensionKey(level) + "|" + snapshot.fingerprint();
      CompletableFuture<Boolean> current=ROTATION_RESULTS.get(key);
      if(current!=null)return current;
      if(System.nanoTime()<ROTATION_RETRY.getOrDefault(key,0L))
         return CompletableFuture.completedFuture(false);

      CompletableFuture<Boolean> started=new CompletableFuture<>();
      current=ROTATION_RESULTS.putIfAbsent(key,started);
      if(current!=null)return current;
      try {
         WRITE_IO.execute(() -> {
            boolean completed=false;
            try {
               completed=GenerationStore.reconcile(level,snapshot,trigger);
            } catch(Throwable failure) {
               LOG.warn("Chunk cache generation reconciliation failed for {}",
                  dimensionKey(level),failure);
            } finally {
               try { CacheDiskBudget.at(cacheRoot(level)).invalidateInventory(); }
               catch(Throwable ignored) {}
               if(completed) {
                  ROTATION_RETRY.remove(key);
               } else {
                  ROTATION_RETRY.put(key,System.nanoTime()+60_000_000_000L);
                  ROTATION_RESULTS.remove(key,started);
               }
               started.complete(completed);
            }
         });
      } catch(RejectedExecutionException rejected) {
         ROTATION_RESULTS.remove(key,started);
         started.complete(false);
      }
      return started;
   }

   private static ThreadFactory daemonFactory(String var0) {
      return var1 -> {
         Thread var2 = new Thread(var1, var0);
         var2.setDaemon(true);
         return var2;
      };
   }
}
