package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.lod.CsLodRegionStore;
import com.kishku7.chunksmith.lod.LodSupport;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
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
   private static final Set<CacheManager.CaptureKey> PREGEN_RETRY = ConcurrentHashMap.newKeySet();
   private static final Set<CacheManager.CaptureKey> PREGEN_LOD_RETRY = ConcurrentHashMap.newKeySet();
   private static final ConcurrentHashMap<CacheManager.CaptureKey, Long> PREGEN_NEW_TARGETS = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<CacheManager.CaptureKey, GenerationTask> PREGEN_REQUEST_TARGETS = new ConcurrentHashMap<>();
   private static final ThreadLocal<GenerationTask> CHUNKSMITH_REQUEST_TASK = new ThreadLocal<>();
   private static final ConcurrentHashMap<CacheManager.CaptureKey, AtomicInteger> LOD_PENDING = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<CacheManager.CaptureKey, Path> CONFIRMED_PREGEN = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<CacheManager.CaptureKey, CacheManager.FileStamp> CONFIRMED_PREGEN_STAMP = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<CacheManager.CaptureKey, CacheManager.LodStamp> CONFIRMED_LOD = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Path, ConcurrentHashMap<String, Long>> NEXT_LOCAL_RECOVERY_WARNING = new ConcurrentHashMap<>();
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
      return CHUNKSMITH_REQUEST_TASK.get();
   }

   public static <T> T withChunksmithRequestTask(GenerationTask sourceTask, Supplier<T> action) {
      GenerationTask previous = CHUNKSMITH_REQUEST_TASK.get();
      if (sourceTask == null) {
         CHUNKSMITH_REQUEST_TASK.remove();
      } else {
         CHUNKSMITH_REQUEST_TASK.set(sourceTask);
      }

      try {
         return action.get();
      } finally {
         if (previous == null) {
            CHUNKSMITH_REQUEST_TASK.remove();
         } else {
            CHUNKSMITH_REQUEST_TASK.set(previous);
         }
      }
   }

   public static void runWithChunksmithRequestTask(GenerationTask sourceTask, Runnable action) {
      withChunksmithRequestTask(sourceTask, () -> {
         action.run();
         return null;
      });
   }

   public static void captureFromChunksmith(ServerLevel level, LevelChunk chunk, CompletableFuture<Boolean> lodCommitted, GenerationTask sourceTask) {
      PipelineMetrics.count("direct-full-candidates");
      if (level != null && chunk != null) {
         ChunkPos pos = chunk.getPos();
         CacheManager.CaptureKey key = captureKey(level, pos);
         GenerationTask admitted = PREGEN_REQUEST_TARGETS.get(key);
         if (sourceTask != null && admitted == sourceTask) {
            LOD_PENDING.compute(key, (ignored, count) -> {
               if (count == null) {
                  count = new AtomicInteger();
               }

               count.incrementAndGet();
               return (AtomicInteger)count;
            });
            lodCommitted.whenComplete(
               (savedx, error) -> LOD_PENDING.computeIfPresent(key, (ignored, count) -> (AtomicInteger)(count.decrementAndGet() <= 0 ? null : count))
            );
            if (PregenBorrowLedger.kind(level, pos) == PregenBorrowLedger.Kind.BORROWED) {
               PregenBorrowLedger.attachLod(level, pos, lodCommitted);
               PREGEN_NEW_TARGETS.remove(key);
               lodCommitted.whenComplete((savedx, error) -> {
                  if (error == null && Boolean.TRUE.equals(savedx)) {
                     markLodCommitted(level, pos, key);
                     PipelineMetrics.count("pregen-lod-repair-complete");
                  } else {
                     addRetry(PREGEN_LOD_RETRY, key);
                     PipelineMetrics.count("pregen-lod-repair-failed");
                  }
               });
            } else {
               boolean generatedTarget = PREGEN_NEW_TARGETS.remove(key) != null;
               if (!generatedTarget) {
                  lodCommitted.whenComplete((lodSaved, lodError) -> {
                     if (lodError == null && Boolean.TRUE.equals(lodSaved)) {
                        markLodCommitted(level, pos, key);
                     } else {
                        addRetry(PREGEN_LOD_RETRY, key);
                     }
                  });
               } else if (level.getServer().isSameThread() && FollowCaptureOwnership.owns(level) && !BackfillGuard.suppress(level, pos)) {
                  if (PregenBorrowLedger.kind(level, pos) != PregenBorrowLedger.Kind.GENERATED) {
                     addRetry(PREGEN_RETRY, key);
                     PipelineMetrics.count("direct-persistence-guard-missing");
                  } else if (PregenBorrowLedger.isHandedOff(level, pos)) {
                     lodCommitted.whenComplete((lodSaved, lodError) -> {
                        if (lodError == null && Boolean.TRUE.equals(lodSaved)) {
                           markLodCommitted(level, pos, key);
                        } else {
                           addRetry(PREGEN_LOD_RETRY, key);
                        }
                     });
                     PipelineMetrics.count("direct-cache-skipped-after-player-handoff");
                  } else {
                     PregenBorrowLedger.attachLod(level, pos, lodCommitted);
                     lodCommitted.whenComplete((lodSaved, lodError) -> {
                        if (lodError == null && Boolean.TRUE.equals(lodSaved)) {
                           markLodCommitted(level, pos, key);
                        } else {
                           addRetry(PREGEN_LOD_RETRY, key);
                        }
                     });
                     CompletableFuture<Boolean> saved = capture(level, chunk, false, lodCommitted);
                     saved.whenComplete((ok, error) -> {
                        if (error == null && Boolean.TRUE.equals(ok)) {
                           Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
                           if (snapshot != null && !STOPPING.contains(level.getServer())) {
                              confirmPregenFull(key, path(level, pos, snapshot.fingerprint()));
                           } else {
                              addRetry(PREGEN_RETRY, key);
                           }

                           markLodCommitted(level, pos, key);
                        } else {
                           try {
                              NormalChunkStorage.inspectAsync(level, pos).whenComplete((normal, inspectError) -> {
                                 if (inspectError == null && normal == NormalChunkStorage.State.FULL) {
                                    PREGEN_RETRY.remove(key);
                                    lodCommitted.whenComplete((lodSaved, lodError) -> {
                                       if (lodError == null && Boolean.TRUE.equals(lodSaved)) {
                                          markLodCommitted(level, pos, key);
                                       } else {
                                          addRetry(PREGEN_LOD_RETRY, key);
                                       }
                                    });
                                 } else {
                                    addRetry(PREGEN_RETRY, key);
                                 }
                              });
                           } catch (Throwable inspectFailure) {
                              addRetry(PREGEN_RETRY, key);
                           }
                        }
                     });
                  }
               } else {
                  addRetry(PREGEN_RETRY, key);
                  PipelineMetrics.count("direct-ineligible");
               }
            }
         } else {
            PipelineMetrics.count("direct-external-observed");
         }
      }
   }

   public static boolean pregenRetryPending(MinecraftServer server, int centerX, int centerZ, int radiusBlocks) {
      if (server == null) {
         return false;
      } else {
         long radius = Math.max(0, radiusBlocks) + 16L;
         long radiusSquared = radius * radius;
         if (anyInRange(server, PREGEN_RETRY, centerX, centerZ, radiusSquared)) {
            return true;
         } else if (anyInRange(server, PREGEN_LOD_RETRY, centerX, centerZ, radiusSquared)) {
            return true;
         } else if (anyInRange(server, PREGEN_NEW_TARGETS.keySet(), centerX, centerZ, radiusSquared)) {
            return true;
         } else {
            return anyInRange(server, LOD_PENDING.keySet(), centerX, centerZ, radiusSquared)
               ? true
               : anyInRange(server, CAPTURE_PENDING.keySet(), centerX, centerZ, radiusSquared);
         }
      }
   }

   public static void noteChunksmithGeneratedResult(ServerLevel level, ChunkPos pos, GenerationTask sourceTask, boolean generated) {
      if (level != null && pos != null && sourceTask != null && !STOPPING.contains(level.getServer()) && FollowCaptureOwnership.active(level, sourceTask)) {
         CacheManager.CaptureKey key = captureKey(level, pos);
         if (generated) {
            PREGEN_NEW_TARGETS.remove(key);
         } else {
            PREGEN_NEW_TARGETS.put(key, System.currentTimeMillis());
         }
      }
   }

   public static void noteChunksmithGeneratedFailure(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
      if (level != null && pos != null && sourceTask != null && !STOPPING.contains(level.getServer()) && FollowCaptureOwnership.active(level, sourceTask)) {
         CacheManager.CaptureKey key = captureKey(level, pos);
         PREGEN_NEW_TARGETS.remove(key);
         addRetry(PREGEN_RETRY, key);
         PipelineMetrics.count("pregen-generated-state-failed");
      }
   }

   public static boolean preparePregenRequest(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
      if (level != null && pos != null) {
         if (STOPPING.contains(level.getServer())) {
            return false;
         }

         if (!FollowCaptureOwnership.owns(sourceTask)) {
            return true;
         }

         if (!FollowCaptureOwnership.active(level, sourceTask)) {
            PipelineMetrics.count("pregen-stale-follow-request-blocked");
            return false;
         }

         CacheManager.CaptureKey key = captureKey(level, pos);
         GenerationTask admitted = PREGEN_REQUEST_TARGETS.compute(
            key,
            (ignored, existing) -> (GenerationTask)(existing != null && existing != sourceTask && FollowCaptureOwnership.active(level, existing)
               ? existing
               : sourceTask)
         );
         if (admitted != sourceTask) {
            PipelineMetrics.count("pregen-request-owner-conflict");
            return false;
         }

         if (PREGEN_RETRY.contains(key) && !PREGEN_NEW_TARGETS.containsKey(key) && PregenBorrowLedger.kind(level, pos) == null) {
            PipelineMetrics.count("pregen-unproven-full-request-blocked");
            return false;
         }

         if (!PREGEN_NEW_TARGETS.containsKey(key)) {
            return true;
         }

         Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
         if (snapshot == null) {
            addRetry(PREGEN_RETRY, key);
            return false;
         }

         Path file = path(level, pos, snapshot.fingerprint());
         PregenBorrowLedger.Kind kind = PregenBorrowLedger.kind(level, pos);
         if (kind != null && kind != PregenBorrowLedger.Kind.GENERATED) {
            return false;
         }

         if (PregenBorrowLedger.beginGenerated(level, pos, file, sourceTask)) {
            return true;
         }

         addRetry(PREGEN_RETRY, key);
         return false;
      } else {
         return false;
      }
   }

   public static void notePregenFullLoaded(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
      if (level != null && pos != null && sourceTask != null) {
         if (PregenBorrowLedger.generatedFullLoaded(level, pos, sourceTask)) {
            PipelineMetrics.count("pregen-full-retained-after-failure");
         }
      }
   }

   public static boolean unifiedPregenRequestAdmitted(ServerLevel level, ChunkPos pos) {
      return level != null && pos != null && PREGEN_REQUEST_TARGETS.containsKey(captureKey(level, pos));
   }

   public static boolean unifiedPregenRequestAdmitted(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
      return level != null && pos != null && sourceTask != null && PREGEN_REQUEST_TARGETS.get(captureKey(level, pos)) == sourceTask;
   }

   public static boolean unifiedPregenRequestActive(ServerLevel level, ChunkPos pos) {
      if (level != null && pos != null) {
         GenerationTask sourceTask = PREGEN_REQUEST_TARGETS.get(captureKey(level, pos));
         return sourceTask != null && FollowCaptureOwnership.active(level, sourceTask);
      } else {
         return false;
      }
   }

   public static boolean unifiedPregenRequestActive(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
      return unifiedPregenRequestAdmitted(level, pos, sourceTask) && FollowCaptureOwnership.active(level, sourceTask);
   }

   public static void pregenRequestFinished(ServerLevel level, ChunkPos pos, GenerationTask sourceTask, boolean success) {
      if (level != null && pos != null && sourceTask != null) {
         CacheManager.CaptureKey key = captureKey(level, pos);
         if (!PREGEN_REQUEST_TARGETS.remove(key, sourceTask)) {
            if (!success) {
               PregenBorrowLedger.cancel(level, pos, sourceTask);
            }
         } else if (STOPPING.contains(level.getServer())) {
            PREGEN_NEW_TARGETS.remove(key);
         } else if (!success) {
            PREGEN_NEW_TARGETS.remove(key);
            addRetry(PREGEN_RETRY, key);
            PregenBorrowLedger.cancel(level, pos, sourceTask);
            addRetry(PREGEN_LOD_RETRY, key);
            invalidateChunksmithLodPresence(level, pos);
         }
      }
   }

   private static void invalidateChunksmithLodPresence(ServerLevel level, ChunkPos pos) {
      if (level != null && pos != null) {
         CONFIRMED_LOD.remove(captureKey(level, pos));

         try {
            CsLodPresenceIndex index = LodSupport.presenceIndexFor(level.getServer(), level.dimension().location().toString());
            if (index != null) {
               index.invalidate();
            }
         } catch (Throwable unavailable) {
            PipelineMetrics.count("pregen-lod-presence-invalidate-failed");
         }
      }
   }

   public static void pregenRequestFailed(ServerLevel level, ChunkPos pos, GenerationTask sourceTask) {
      pregenRequestFinished(level, pos, sourceTask, false);
   }

   public static boolean pregenReady(MinecraftServer server) {
      return server != null && Fingerprint.snapshotForLevel(server.overworld()) != null;
   }

   private static boolean anyInRange(MinecraftServer server, Iterable<CacheManager.CaptureKey> keys, int centerX, int centerZ, long radiusSquared) {
      for (CacheManager.CaptureKey key : keys) {
         if (key.level().getServer() == server) {
            ChunkPos pos = new ChunkPos(key.pos());
            long x = ((long)pos.x << 4) + 8L - centerX;
            long z = ((long)pos.z << 4) + 8L - centerZ;
            if (x * x + z * z <= radiusSquared) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean unifiedPregenOwns(ServerLevel level) {
      return level != null && FollowCaptureOwnership.owns(level);
   }

   public static boolean unifiedPairSkipHint(ServerLevel level, ChunkPos pos, CsLodPresenceIndex index) {
      if (level != null && pos != null && index != null && FollowCaptureOwnership.owns(level)) {
         CacheManager.CaptureKey key = captureKey(level, pos);
         Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
         if (snapshot == null) {
            return false;
         }

         Path expected = path(level, pos, snapshot.fingerprint());
         if (!confirmedPregenMatches(key, expected)) {
            return false;
         }

         if (!chunksmithLodSkipHint(level, pos, index)) {
            return false;
         }

         PREGEN_RETRY.remove(key);
         PREGEN_LOD_RETRY.remove(key);
         return true;
      } else {
         return false;
      }
   }

   public static CompletableFuture<Boolean> protectChunksmithGeneratedResult(
      ServerLevel level, ChunkPos pos, GenerationTask sourceTask, CompletableFuture<Boolean> nativeResult
   ) {
      if (level == null || pos == null || sourceTask == null || nativeResult == null) {
         return nativeResult;
      }

      if (!FollowCaptureOwnership.owns(sourceTask)) {
         return nativeResult;
      }

      CacheManager.CaptureKey key = captureKey(level, pos);
      return nativeResult.thenCompose(
         generated -> {
            if (!FollowCaptureOwnership.active(level, sourceTask)) {
               PipelineMetrics.count("pregen-stale-generated-result-blocked");
               return CompletableFuture.failedFuture(
                  new IllegalStateException("Unified pregen generated-state result belongs to a stopped or detached task at " + pos)
               );
            }

            if (Boolean.TRUE.equals(generated)) {
               return confirmNativeGenerated(level, pos, key);
            }

            CompletableFuture<Boolean> cacheDecision = chunksmithGeneratedDecision(level, pos);
            return cacheDecision == null ? CompletableFuture.completedFuture(false) : cacheDecision;
         }
      );
   }

   private static CompletableFuture<Boolean> confirmNativeGenerated(ServerLevel level, ChunkPos pos, CacheManager.CaptureKey key) {
      CompletableFuture<Boolean> cacheDecision = chunksmithGeneratedDecision(level, pos);
      return cacheDecision == null
         ? confirmNormalFull(level, pos, key)
         : cacheDecision.<Boolean>handle((accepted, error) -> (Boolean)(error == null ? accepted : null))
            .thenCompose(accepted -> Boolean.TRUE.equals(accepted) ? CompletableFuture.completedFuture(true) : confirmNormalFull(level, pos, key));
   }

   private static CompletableFuture<Boolean> confirmNormalFull(ServerLevel level, ChunkPos pos, CacheManager.CaptureKey key) {
      try {
         return NormalChunkStorage.inspectAsync(level, pos).handle((normal, error) -> {
            if (error == null && normal == NormalChunkStorage.State.FULL) {
               PregenBorrowLedger.releaseGeneratedToNormal(level, pos);
               PREGEN_RETRY.remove(key);
               PREGEN_NEW_TARGETS.remove(key);
               return true;
            }

            addRetry(PREGEN_RETRY, key);
            if (error == null && normal != null && normal != NormalChunkStorage.State.UNKNOWN) {
               return false;
            }

            PipelineMetrics.count("pregen-normal-full-inspection-unknown");
            throw new CompletionException(new IllegalStateException("Normal FULL ownership is unknown at " + pos, error));
         });
      } catch (Throwable inspectFailure) {
         addRetry(PREGEN_RETRY, key);
         PipelineMetrics.count("pregen-normal-full-inspection-unknown");
         return CompletableFuture.failedFuture(inspectFailure);
      }
   }

   public static boolean chunksmithWorldStateGenerated(ServerLevel level, ChunkPos pos, boolean nativeGenerated) {
      if (nativeGenerated && level != null && pos != null && FollowCaptureOwnership.owns(level)) {
         CacheManager.CaptureKey key = captureKey(level, pos);
         if (PREGEN_RETRY.contains(key)
            || PREGEN_NEW_TARGETS.containsKey(key)
            || CAPTURE_PENDING.containsKey(key)
            || PregenBorrowLedger.kind(level, pos) != null
            || PromotionLedger.owns(level, pos)) {
            return false;
         } else if (CONFIRMED_PREGEN.containsKey(key)) {
            Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
            Path expected = snapshot == null ? null : path(level, pos, snapshot.fingerprint());
            return confirmedPregenMatches(key, expected);
         } else {
            return true;
         }
      } else {
         return nativeGenerated;
      }
   }

   public static CompletableFuture<Boolean> chunksmithGeneratedDecision(ServerLevel level, ChunkPos pos) {
      if (level != null && pos != null) {
         if (STOPPING.contains(level.getServer())) {
            return CompletableFuture.completedFuture(true);
         }

         CacheManager.CaptureKey key = captureKey(level, pos);
         if (BackfillGuard.active(level, pos)) {
            return CompletableFuture.completedFuture(true);
         }

         CacheManager.CaptureFlight pending = CAPTURE_PENDING.get(key);
         if (pending != null) {
            return pending.result().thenApply(Boolean.TRUE::equals);
         }

         Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
         if (snapshot == null) {
            return null;
         }

         String fingerprint = snapshot.fingerprint();
         Path file = path(level, pos, fingerprint);
         if (confirmedPregenMatches(key, file)) {
            PREGEN_RETRY.remove(key);
            return CompletableFuture.completedFuture(true);
         }

         Path claim = PromotionClaim.claimedPath(file);

         try {
            if (RecoveryClaims.claimExists(claim)) {
               addRetry(PREGEN_RETRY, key);
               return CompletableFuture.completedFuture(true);
            }
         } catch (IOException ownershipUnknown) {
            addRetry(PREGEN_RETRY, key);
            PipelineMetrics.count("promotion-claim-inspection-failed");
            return CompletableFuture.completedFuture(true);
         }

         if (!Files.isRegularFile(file)) {
            return null;
         }

         try {
            return CompletableFuture.supplyAsync(() -> {
               try (CachePathLocks.Guard pathLock = CachePathLocks.lock(file)) {
                  try {
                     if (RecoveryClaims.claimExists(claim)) {
                        addRetry(PREGEN_RETRY, key);
                        return true;
                     }

                     CompoundTag envelope = NbtIo.readCompressed(file, NbtAccounter.create(67108864L));
                     if (validEnvelope(envelope, level, pos, fingerprint)) {
                        if (!STOPPING.contains(level.getServer())) {
                           confirmPregenFull(key, file);
                        } else {
                           addRetry(PREGEN_RETRY, key);
                        }

                        PipelineMetrics.count("pregen-full-validated");
                        return true;
                     } else {
                        clearConfirmedPregen(key);
                        quarantineRejected(file);
                        PipelineMetrics.count("pregen-full-invalid");
                        return false;
                     }
                  } catch (IOException ownershipOrReadFailure) {
                     clearConfirmedPregen(key);

                     try {
                        if (RecoveryClaims.claimExists(claim)) {
                           addRetry(PREGEN_RETRY, key);
                           return true;
                        }
                     } catch (IOException ownershipUnknown) {
                        addRetry(PREGEN_RETRY, key);
                        return true;
                     }

                     quarantineRejected(file);
                     PipelineMetrics.count("pregen-full-invalid");
                     return false;
                  } catch (Throwable invalid) {
                     clearConfirmedPregen(key);
                     quarantineRejected(file);
                     PipelineMetrics.count("pregen-full-invalid");
                     return false;
                  }
               }
            }, READ_IO);
         } catch (RejectedExecutionException full) {
            addRetry(PREGEN_RETRY, key);
            return CompletableFuture.completedFuture(true);
         }
      } else {
         return null;
      }
   }

   public static void noteObservedNormalFullLod(ServerLevel level, ChunkPos pos) {
      if (level != null
         && pos != null
         && !STOPPING.contains(level.getServer())
         && FollowCaptureOwnership.owns(level)
         && chunksmithWorldStateGenerated(level, pos, true)) {
         CacheManager.CaptureKey key = captureKey(level, pos);
         PREGEN_RETRY.remove(key);
         PREGEN_LOD_RETRY.remove(key);
         PREGEN_NEW_TARGETS.remove(key);
      }
   }

   public static void noteObservedLodPresent(ServerLevel level, ChunkPos pos) {
      if (level != null && pos != null && !STOPPING.contains(level.getServer()) && FollowCaptureOwnership.owns(level)) {
         PREGEN_LOD_RETRY.remove(captureKey(level, pos));
      }
   }

   public static boolean chunksmithGeneratedHint(ServerLevel level, ChunkPos pos) {
      if (level != null && pos != null) {
         CacheManager.CaptureKey key = captureKey(level, pos);
         if (!BackfillGuard.active(level, pos) && !CAPTURE_PENDING.containsKey(key)) {
            Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
            if (snapshot == null) {
               return false;
            }

            Path file = path(level, pos, snapshot.fingerprint());
            if (confirmedPregenMatches(key, file)) {
               return true;
            }

            try {
               return RecoveryClaims.claimExists(PromotionClaim.claimedPath(file));
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

   private static CacheManager.FileStamp fileStamp(Path file) {
      if (file == null) {
         return null;
      }

      try {
         BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
         return attrs.isRegularFile() ? new CacheManager.FileStamp(attrs.size(), attrs.lastModifiedTime()) : null;
      } catch (IOException unavailable) {
         return null;
      }
   }

   private static boolean confirmPregenFull(CacheManager.CaptureKey key, Path file) {
      CacheManager.FileStamp stamp = fileStamp(file);
      if (stamp == null) {
         clearConfirmedPregen(key);
         addRetry(PREGEN_RETRY, key);
         return false;
      } else {
         CONFIRMED_PREGEN.put(key, file);
         CONFIRMED_PREGEN_STAMP.put(key, stamp);
         PREGEN_RETRY.remove(key);
         return true;
      }
   }

   private static void clearConfirmedPregen(CacheManager.CaptureKey key) {
      if (key != null) {
         CONFIRMED_PREGEN.remove(key);
         CONFIRMED_PREGEN_STAMP.remove(key);
      }
   }

   private static boolean confirmedPregenMatches(CacheManager.CaptureKey key, Path expected) {
      Path confirmed = CONFIRMED_PREGEN.get(key);
      if (confirmed == null) {
         return false;
      }

      CacheManager.FileStamp recorded = CONFIRMED_PREGEN_STAMP.get(key);
      CacheManager.FileStamp current = expected == null ? null : fileStamp(expected);
      if (expected != null && expected.equals(confirmed) && recorded != null && recorded.equals(current)) {
         return true;
      }

      clearConfirmedPregen(key);
      addRetry(PREGEN_RETRY, key);
      PipelineMetrics.count("pregen-full-confirmation-invalidated");
      return false;
   }

   private static Path lodRegionPath(CsLodPresenceIndex index, ChunkPos pos) {
      int rx = Math.floorDiv(pos.x, 32);
      int rz = Math.floorDiv(pos.z, 32);
      return index.getRoot().resolve("r." + rx + "." + rz + ".cslod");
   }

   private static void markLodCommitted(ServerLevel level, ChunkPos pos, CacheManager.CaptureKey key) {
      try {
         CsLodPresenceIndex index = LodSupport.presenceIndexFor(level.getServer(), level.dimension().location().toString());
         if (index == null) {
            CONFIRMED_LOD.remove(key);
            addRetry(PREGEN_LOD_RETRY, key);
            return;
         }

         Path region = lodRegionPath(index, pos);
         CacheManager.FileStamp stamp = fileStamp(region);
         if (stamp == null) {
            CONFIRMED_LOD.remove(key);
            addRetry(PREGEN_LOD_RETRY, key);
            return;
         }

         CONFIRMED_LOD.put(key, new CacheManager.LodStamp(region, stamp));
         PREGEN_LOD_RETRY.remove(key);
      } catch (Throwable unavailable) {
         CONFIRMED_LOD.remove(key);
         addRetry(PREGEN_LOD_RETRY, key);
      }
   }

   public static boolean chunksmithLodSkipHint(ServerLevel level, ChunkPos pos, CsLodPresenceIndex index) {
      if (level != null && pos != null && index != null && FollowCaptureOwnership.owns(level)) {
         CacheManager.CaptureKey key = captureKey(level, pos);
         if (PREGEN_LOD_RETRY.contains(key)) {
            return false;
         }

         Path region = lodRegionPath(index, pos);
         CacheManager.FileStamp current = fileStamp(region);
         CacheManager.LodStamp confirmed = CONFIRMED_LOD.get(key);
         if (confirmed != null && confirmed.region().equals(region) && confirmed.stamp().equals(current)) {
            return true;
         }

         if (current == null) {
            return invalidateLodRecord(key, index);
         }

         CsLodRegionStore store = new CsLodRegionStore(index.getRoot());

         try {
            CsLodChunk record = store.read(pos.x, pos.z);
            if (record != null && record.getChunkX() == pos.x && record.getChunkZ() == pos.z) {
               CacheManager.FileStamp validated = fileStamp(region);
               if (validated == null) {
                  return invalidateLodRecord(key, index);
               }

               CONFIRMED_LOD.put(key, new CacheManager.LodStamp(region, validated));
               PREGEN_LOD_RETRY.remove(key);
               PipelineMetrics.count("pregen-lod-validated");
               return true;
            } else {
               return invalidateLodRecord(key, index);
            }
         } catch (Throwable invalid) {
            return invalidateLodRecord(key, index);
         } finally {
            try {
               store.close();
            } catch (IOException var21) {
            }
         }
      } else {
         return false;
      }
   }

   private static boolean invalidateLodRecord(CacheManager.CaptureKey key, CsLodPresenceIndex index) {
      CONFIRMED_LOD.remove(key);
      addRetry(PREGEN_LOD_RETRY, key);

      try {
         index.invalidate();
      } catch (Throwable var3) {
      }

      PipelineMetrics.count("pregen-lod-invalid");
      return false;
   }

   private static void addRetry(Set<CacheManager.CaptureKey> retrySet, CacheManager.CaptureKey key) {
      if (key != null && !STOPPING.contains(key.level().getServer())) {
         retrySet.add(key);
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

         inspected.whenComplete((normal, error) -> {
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

               if (normal == NormalChunkStorage.State.FULL) {
                  PregenBorrowLedger.releaseGeneratedToNormal(level, pos);
                  PREGEN_RETRY.remove(captureKey(level, pos));
                  PipelineMetrics.count("capture-normal-full-won");
                  work.result.complete(false);
               } else if (error == null && normal != NormalChunkStorage.State.UNKNOWN && !claimed && !STOPPING.contains(level.getServer())) {
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
         });
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
         PregenBorrowLedger.releaseGeneratedToNormal(level, pos);
         PromotionLedger.normalFullPresent(level, pos);
         return CompletableFuture.completedFuture(normal);
      } else {
         return pregenBorrowAllowed(level, pos)
            ? loadForPregenLodRepair(level, pos).thenApply(cached -> cached.isPresent() ? cached : normal)
            : loadIfPresent(level, pos).thenApply(cached -> cached.isPresent() ? cached : normal);
      }
   }

   private static boolean pregenBorrowAllowed(ServerLevel level, ChunkPos pos) {
      return FollowCaptureOwnership.owns(level) && PREGEN_REQUEST_TARGETS.containsKey(captureKey(level, pos));
   }

   private static CompletableFuture<Optional<CompoundTag>> loadForPregenLodRepair(ServerLevel level, ChunkPos pos) {
      if (PregenBorrowLedger.kind(level, pos) == PregenBorrowLedger.Kind.GENERATED) {
         return CompletableFuture.completedFuture(Optional.empty());
      }

      CompletableFuture<Boolean> active = PregenBorrowLedger.pending(level, pos);
      if (active != null) {
         return active.<Object>handle((ignored, error) -> null).thenCompose(ignored -> loadForPregenLodRepair(level, pos));
      }

      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(level);
      if (snapshot == null) {
         return CompletableFuture.completedFuture(Optional.empty());
      }

      String fingerprint = snapshot.fingerprint();
      rotateGenerations(level, snapshot, "pregen-lod-repair");
      Path file = path(level, pos, fingerprint);
      Path dimension = cacheRoot(level).resolve(dimensionKey(level));
      return ownershipForLoad(dimension, pos).whenComplete((claim, error) -> {
         if (error != null) {
            PromotionLedger.blockReplay(level, pos, RecoveryClaims.root(dimension));
            addRetry(PREGEN_RETRY, captureKey(level, pos));
         }
      }).thenCompose(ownership -> {
         if (ownership != null) {
            PromotionLedger.blockReplay(level, pos, ownership);
            addRetry(PREGEN_RETRY, captureKey(level, pos));
            return CompletableFuture.failedFuture(new IOException("Promotion ownership is active at " + ownership));
         }

         if (!Files.isRegularFile(file)) {
            return CompletableFuture.completedFuture(Optional.empty());
         }

         try {
            return CompletableFuture.supplyAsync(() -> {
               try (CachePathLocks.Guard pathLock = CachePathLocks.lock(file)) {
                  try {
                     Path currentClaim;
                     try {
                        currentClaim = RecoveryClaims.claimForCoordinate(dimension, pos.x, pos.z);
                     } catch (IOException ownershipFailure) {
                        PromotionLedger.blockReplay(level, pos, RecoveryClaims.root(dimension));
                        addRetry(PREGEN_RETRY, captureKey(level, pos));
                        throw new UncheckedIOException(ownershipFailure);
                     }

                     if (currentClaim != null) {
                        PromotionLedger.blockReplay(level, pos, currentClaim);
                        addRetry(PREGEN_RETRY, captureKey(level, pos));
                        throw new UncheckedIOException(new IOException("Promotion ownership changed at " + currentClaim));
                     } else {
                        CompoundTag envelope = NbtIo.readCompressed(file, NbtAccounter.create(67108864L));
                        if (!validEnvelope(envelope, level, pos, fingerprint)) {
                           quarantineRejected(file);
                           addRetry(PREGEN_RETRY, captureKey(level, pos));
                           return Optional.empty();
                        } else if (STOPPING.contains(level.getServer())) {
                           return Optional.empty();
                        } else if (!confirmPregenFull(captureKey(level, pos), file)) {
                           return Optional.empty();
                        } else if (!PregenBorrowLedger.beginBorrowed(level, pos, file)) {
                           addRetry(PREGEN_RETRY, captureKey(level, pos));
                           return Optional.empty();
                        } else {
                           PipelineMetrics.count("pregen-full-borrowed-for-lod");
                           return Optional.of(envelope.getCompound("chunk"));
                        }
                     }
                  } catch (UncheckedIOException ownershipFailure) {
                     throw ownershipFailure;
                  } catch (Throwable invalid) {
                     quarantineRejected(file);
                     addRetry(PREGEN_RETRY, captureKey(level, pos));
                     LOG.warn("Pregen FULL {} could not be validated for LOD repair; quarantined for coordinate-local regeneration", file, invalid);
                     return Optional.empty();
                  }
               }
            }, READ_IO);
         } catch (RejectedExecutionException full) {
            addRetry(PREGEN_RETRY, captureKey(level, pos));
            return CompletableFuture.failedFuture(full);
         }
      });
   }

   public static CompletableFuture<Optional<CompoundTag>> loadIfPresent(ServerLevel var0, ChunkPos var1) {
      Path dimension = cacheRoot(var0).resolve(dimensionKey(var0));
      return ownershipForLoad(dimension, var1).thenCompose(claim -> {
         if (claim != null) {
            PromotionLedger.blockReplay(var0, var1, claim);
            PipelineMetrics.count("promotion-replay-blocked");
            return CompletableFuture.completedFuture(Optional.empty());
         } else {
            return loadReadyCache(var0, var1);
         }
      }).whenComplete((cached, error) -> {
         if (error != null && !STOPPING.contains(var0.getServer())) {
            PromotionLedger.blockReplay(var0, var1, RecoveryClaims.root(dimension));
         }
      });
   }

   private static CompletableFuture<Path> ownershipForLoad(Path dimension, ChunkPos pos) {
      CompletableFuture<Throwable> prepared = RecoveryClaims.prepare(dimension, VALIDATE_IO)
         .copy()
         .orTimeout(30L, TimeUnit.SECONDS)
         .handle((ignored, error) -> error);
      return prepared.thenCompose(
         recoveryFailure -> {
            if (recoveryFailure == null) {
               try {
                  return CompletableFuture.completedFuture(RecoveryClaims.claimForCoordinate(dimension, pos.x, pos.z));
               } catch (IOException targetFailure) {
                  PipelineMetrics.count("promotion-claim-inspection-failed");
                  return CompletableFuture.failedFuture(targetFailure);
               }
            } else {
               try {
                  CompletableFuture<Path> probe = CompletableFuture.supplyAsync(
                     () -> {
                        try {
                           Path claim = RecoveryClaims.claimForCoordinate(dimension, pos.x, pos.z);
                           if (claim == null) {
                              PipelineMetrics.count("promotion-recovery-localized");
                              LOG.debug(
                                 "Chunk-cache recovery is unavailable for {} but coordinate {} has no ownership in canonical, legacy, quarantine, or conflict paths; normal cache/world loading may continue for this coordinate; recoveryFailure={}",
                                 new Object[]{dimension.getFileName(), pos, failureSummary(recoveryFailure)}
                              );
                           } else {
                              PipelineMetrics.count("promotion-recovery-local-claim");
                              if (allowLocalRecoveryWarning(dimension, "claim")) {
                                 LOG.warn(
                                    "Chunk-cache load remains blocked for {} {}: dimension recovery is unavailable and targeted ownership proof found {}; repair cache-directory access and retry recovery; recoveryFailure={}",
                                    new Object[]{dimension.getFileName(), pos, claim, failureSummary(recoveryFailure)}
                                 );
                              }
                           }

                           return claim;
                        } catch (IOException targetFailure) {
                           PipelineMetrics.count("promotion-recovery-local-proof-failed");
                           String category = "proof-" + unwrapFailure(targetFailure).getClass().getName();
                           if (allowLocalRecoveryWarning(dimension, category)) {
                              LOG.warn(
                                 "Chunk-cache load remains blocked for {} {}: dimension recovery failed and target ownership absence cannot be proven; repair cache-directory access before retrying this coordinate; recoveryFailure={}",
                                 new Object[]{dimension.getFileName(), pos, failureSummary(recoveryFailure), targetFailure}
                              );
                           }

                           throw new UncheckedIOException(
                              "Cannot prove chunk-cache promotion ownership absence for " + dimension.getFileName() + " " + pos, targetFailure
                           );
                        }
                     },
                     READ_IO
                  );
                  return boundedOwnershipProbe(probe, 10L, TimeUnit.SECONDS)
                     .whenComplete(
                        (claim, error) -> {
                           Throwable failure = unwrapFailure(error);
                           if (failure instanceof TimeoutException) {
                              PipelineMetrics.count("promotion-recovery-local-timeout");
                              if (allowLocalRecoveryWarning(dimension, "timeout")) {
                                 LOG.warn(
                                    "Chunk-cache load remains blocked for {} {}: coordinate-local ownership proof exceeded {}s after dimension recovery failed; ownership is unknown, so replacement generation remains disabled; repair cache-directory I/O and retry",
                                    new Object[]{dimension.getFileName(), pos, 10L}
                                 );
                              }
                           }
                        }
                     );
               } catch (RejectedExecutionException rejected) {
                  return CompletableFuture.failedFuture(
                     new IOException("Cannot queue coordinate-local chunk-cache ownership proof for " + dimension.getFileName() + " " + pos, rejected)
                  );
               }
            }
         }
      );
   }

   static <T> CompletableFuture<T> boundedOwnershipProbe(CompletableFuture<T> probe, long timeout, TimeUnit unit) {
      return probe.orTimeout(timeout, unit);
   }

   private static boolean allowLocalRecoveryWarning(Path dimension, String category) {
      Path key = dimension.toAbsolutePath().normalize();
      long now = System.nanoTime();
      AtomicBoolean allowed = new AtomicBoolean();
      NEXT_LOCAL_RECOVERY_WARNING.computeIfAbsent(key, ignored -> new ConcurrentHashMap<>()).compute(category, (ignored, next) -> {
         if (next != null && now < next) {
            return (Long)next;
         }

         allowed.set(true);
         return now + LOCAL_RECOVERY_WARNING_INTERVAL_NANOS;
      });
      return allowed.get();
   }

   private static Throwable unwrapFailure(Throwable error) {
      if (error == null) {
         return null;
      }

      Throwable value = error;

      while (value.getCause() != null && (value instanceof CompletionException || value instanceof ExecutionException)) {
         value = value.getCause();
      }

      return value;
   }

   private static String failureSummary(Throwable error) {
      Throwable value = unwrapFailure(error);
      String message = value == null ? null : value.getMessage();
      return value == null ? "none" : value.getClass().getSimpleName() + (message == null ? "" : ": " + message);
   }

   private static CompletableFuture<Optional<CompoundTag>> loadReadyCache(ServerLevel var0, ChunkPos var1) {
      CompletableFuture<Boolean> borrowed = PregenBorrowLedger.pending(var0, var1);
      if (borrowed != null) {
         return borrowed.<Object>handle((ignored, error) -> null).thenCompose(ignored -> loadReadyCache(var0, var1));
      }

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
                        LOG.warn("Ignoring corrupt chunk cache {}; quarantinedAs={}; vanilla worldgen will be used", new Object[]{var3, quarantined, var6x});
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
      Path handoff = PregenBorrowLedger.handoffCandidate(level, pos);
      if (handoff != null) {
         CacheManager.CaptureFlight pending = CAPTURE_PENDING.get(captureKey(level, pos));

         try {
            if (Files.isRegularFile(handoff) || pending != null) {
               if (!PromotionLedger.adoptLive(level, pos, handoff, pending == null ? null : pending.result())) {
                  throw new IOException("pregen payload is no longer claimable at " + handoff);
               }

               PromotionLedger.entitiesRestored(level, pos);
            }

            PregenBorrowLedger.handedOff(level, pos);
            PipelineMetrics.count("pregen-borrow-player-handoff");
            return false;
         } catch (IOException ownershipFailure) {
            PromotionLedger.blockReplay(level, pos, handoff);
            throw new IllegalStateException(
               "Cannot hand pregen FULL to normal persistence at "
                  + level.dimension().location()
                  + " "
                  + pos
                  + "; keeping normal writes blocked until cache ownership is unambiguous",
               ownershipFailure
            );
         }
      } else {
         return PregenBorrowLedger.suppressPersistence(level, pos);
      }
   }

   public static boolean suppressPregenChunkSave(ServerLevel level, ChunkAccess chunk) {
      return level != null && chunk != null && PregenBorrowLedger.suppressChunkSave(level, chunk);
   }

   public static boolean pregenPoiPersistenceSuppressed(Object poiManager, ChunkPos pos) {
      ServerLevel level = PregenBorrowLedger.levelForPoi(poiManager, pos);
      return level != null && pregenPersistenceSuppressed(level, pos);
   }

   public static boolean suppressPregenPoiPersistence(Object poiManager, ChunkPos pos) {
      ServerLevel level = PregenBorrowLedger.levelForPoi(poiManager, pos);
      return level != null && pregenPersistenceSuppressed(level, pos) ? PregenBorrowLedger.suppressPoiSave(level, pos, poiManager) : false;
   }

   public static void pregenBorrowUnloaded(ServerLevel level, ChunkPos pos) {
      PregenBorrowLedger.unloaded(level, pos);
   }

   public static void prepareNormalWrite(ServerLevel level, ChunkPos pos, CompoundTag tag) {
      if (NormalChunkStorage.fromTag(tag) == NormalChunkStorage.State.FULL && !BackfillGuard.suppress(level, pos) && !pregenPersistenceSuppressed(level, pos)) {
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
      if (NormalChunkStorage.fromTag(tag) == NormalChunkStorage.State.FULL && !BackfillGuard.suppress(level, pos) && !pregenPersistenceSuppressed(level, pos)) {
         if (!PromotionLedger.observeChunkWrite(level, pos, written)) {
            Fingerprint.Snapshot fingerprint = Fingerprint.snapshotForLevel(level);
            if (fingerprint != null) {
               Path file = path(level, pos, fingerprint.fingerprint());
               written.whenComplete((ignored, error) -> {
                  if (error == null) {
                     PregenBorrowLedger.releaseGeneratedToNormal(level, pos);
                     PREGEN_RETRY.remove(captureKey(level, pos));
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
      ArrayList<Path> stoppingDimensions = new ArrayList<>();

      for (ServerLevel level : server.getAllLevels()) {
         stoppingDimensions.add(cacheRoot(level).resolve(dimensionKey(level)).toAbsolutePath().normalize());
      }

      synchronized (STOPPING_DIMENSIONS) {
         STOPPING_DIMENSIONS.put(server, List.copyOf(stoppingDimensions));
      }

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
      synchronized (NEXT_MAINTENANCE) {
         NEXT_MAINTENANCE.remove(server);
      }

      synchronized (NEXT_DISK_REFRESH) {
         NEXT_DISK_REFRESH.remove(server);
      }
   }

   public static void stopped(MinecraftServer server) {
      STOPPING.add(server);
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
      PregenBorrowLedger.clear(server);
      PREGEN_RETRY.removeIf(key -> key.level().getServer() == server);
      PREGEN_LOD_RETRY.removeIf(key -> key.level().getServer() == server);
      PREGEN_NEW_TARGETS.keySet().removeIf(key -> key.level().getServer() == server);
      PREGEN_REQUEST_TARGETS.keySet().removeIf(key -> key.level().getServer() == server);
      LOD_PENDING.keySet().removeIf(key -> key.level().getServer() == server);
      CONFIRMED_PREGEN.keySet().removeIf(key -> key.level().getServer() == server);
      CONFIRMED_PREGEN_STAMP.keySet().removeIf(key -> key.level().getServer() == server);
      CONFIRMED_LOD.keySet().removeIf(key -> key.level().getServer() == server);
      List<Path> stoppingDimensions;
      synchronized (STOPPING_DIMENSIONS) {
         stoppingDimensions = STOPPING_DIMENSIONS.remove(server);
      }

      if (stoppingDimensions == null) {
         ArrayList<Path> fallback = new ArrayList<>();

         for (ServerLevel level : server.getAllLevels()) {
            fallback.add(cacheRoot(level).resolve(dimensionKey(level)).toAbsolutePath().normalize());
         }

         stoppingDimensions = List.copyOf(fallback);
      }

      for (Path dimension : stoppingDimensions) {
         RecoveryClaims.clear(dimension);
         NEXT_LOCAL_RECOVERY_WARNING.remove(dimension);
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

   private record FileStamp(long size, FileTime modified) {
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

   private record LodStamp(Path region, CacheManager.FileStamp stamp) {
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
