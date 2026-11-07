package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.lod.HorizonGuard;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
   private static final int SCHEMA = 1;
   private static final AtomicLong LAST_QUEUE_WARNING = new AtomicLong();
   private static final Set<String> ROTATED = ConcurrentHashMap.newKeySet();
   private static final Map<CaptureKey, CompletableFuture<Boolean>> CAPTURE_PENDING = new ConcurrentHashMap<>();
   private static final Map<MinecraftServer, Long> NEXT_MAINTENANCE = new WeakHashMap<>();
   private static final long MAINTENANCE_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15L);
   private static final long MAINTENANCE_RETRY_MS = TimeUnit.MINUTES.toMillis(1L);
   private static final ThreadPoolExecutor WRITE_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128), daemonFactory("maki-chunk-cache-write"), new AbortPolicy()
   );
   private static final ThreadPoolExecutor READ_IO = new ThreadPoolExecutor(
      1, 1, 30L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(32), daemonFactory("maki-chunk-cache-read"), new AbortPolicy()
   );

   private CacheManager() {
   }

   public static void captureFromChunksmith(ServerLevel level, LevelChunk chunk) {
      PipelineMetrics.count("direct-full-candidates");
      // A generic LodSupport.offer can also come from save/unload refreshes or
      // another owner's task. Only the world-adapter call site invokes us now.
      if (level == null || chunk == null || !level.getServer().isSameThread()
         || level.getServer().getPlayerCount() != 0 || !FollowCaptureOwnership.owns(level)
         || BackfillGuard.suppress(level, chunk.getPos()) || !HorizonGuard.drop(level, chunk.getPos())) {
         PipelineMetrics.count("direct-ineligible");
         return;
      }
      capture(level, chunk, false);
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
      return snapshot!=null&&Files.isRegularFile(path(level,pos,snapshot.fingerprint()));
   }

   private record CaptureKey(ServerLevel level, long pos) {}

   private static CaptureKey captureKey(ServerLevel level,ChunkPos pos) {
      return new CaptureKey(level, pos.toLong());
   }

   static CompletableFuture<Boolean> capture(ServerLevel var0, LevelChunk var1, boolean var2) {
      if (!var0.getServer().isSameThread()) throw new IllegalStateException("Chunk snapshot requires server thread");
      String source = var2 ? "backfill" : "direct";
      CaptureKey key = captureKey(var0, var1.getPos());
      CompletableFuture<Boolean> result = new CompletableFuture<>();
      CompletableFuture<Boolean> pending = CAPTURE_PENDING.putIfAbsent(key, result);
      if (pending != null) {
         PipelineMetrics.count("capture-duplicate-avoided");
         return pending;
      }
      result.whenComplete((ok, error) -> {
         PipelineMetrics.count(source + (Boolean.TRUE.equals(ok) ? "-saved" : "-failed"));
         CAPTURE_PENDING.remove(key, result);
      });
      PipelineMetrics.count(source + "-capture-started");
      try {
         captureSnapshot(var0, var1, result);
      } catch (Throwable error) {
         LOG.warn("Could not capture chunk {}; regeneration remains available", var1.getPos(), error);
         result.complete(false);
      }
      return result;
   }

   private static void captureSnapshot(ServerLevel var0, LevelChunk var1, CompletableFuture<Boolean> var7) {
      // Bound admission before the expensive server-thread NBT snapshot.
      if (WRITE_IO.getQueue().remainingCapacity() == 0) {
         PipelineMetrics.count("capture-queue-rejected");
         var7.complete(false);
         return;
      }
      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
      if (snapshot == null) {
         var7.complete(false);
         return;
      }
      String var3 = snapshot.fingerprint();

      CompoundTag var4;
      try {
         long started = System.nanoTime();
         var4 = ChunkSerializer.write(var0, var1);
         PipelineMetrics.time("cache-serialize", System.nanoTime() - started);
      } catch (Throwable var14) {
         LOG.warn("Could not serialize chunk {} for cache; normal regeneration remains the fallback", new Object[]{var1.getPos(), var14});
         var7.complete(false);
         return;
      }

      CompoundTag var5 = new CompoundTag();
      var5.putInt("schema", SCHEMA);
      var5.putString("fingerprint", var3);
      var5.putString("dimension", var0.dimension().location().toString());
      var5.putInt("x", var1.getPos().x);
      var5.putInt("z", var1.getPos().z);
      CompoundTagBridge.put(var5, "chunk", var4);
      rotateGenerations(var0, snapshot, "capture");
      Path var6 = path(var0, var1.getPos(), var3);
      ChunkPos capturedPos = var1.getPos();
      long queuedAt = System.nanoTime();
      Runnable var8 = () -> {
         PipelineMetrics.time("cache-queue", System.nanoTime() - queuedAt);
         long writeStarted = System.nanoTime();
         try {
            // Horizon eligibility is necessary, but not proof that normal FULL
            // storage is absent (e.g. an older saved chunk not in task state).
            // Classify on the writer, never join disk I/O on the server thread.
            NormalChunkStorage.State normal = NormalChunkStorage.inspect(var0, capturedPos);
            if (normal == NormalChunkStorage.State.FULL || normal == NormalChunkStorage.State.UNKNOWN) {
               PipelineMetrics.count("capture-normal-storage-rejected");
               var7.complete(false);
               return;
            }
            Files.createDirectories(var6.getParent());
            Path var3x = var6.resolveSibling(var6.getFileName().toString() + ".tmp-" + Long.toUnsignedString(System.nanoTime()));
            NbtIo.writeCompressed(var5, var3x);
            moveAtomically(var3x, var6);
            PipelineMetrics.time("cache-write", System.nanoTime() - writeStarted);
            var7.complete(true);
         } catch (Throwable var4x) {
            LOG.warn("Could not write chunk cache {}", new Object[]{var6, var4x});
            var7.complete(false);
         }
      };

      try {
         WRITE_IO.execute(var8);
      } catch (RejectedExecutionException var15) {
         long var10 = System.currentTimeMillis();
         long var12 = LAST_QUEUE_WARNING.get();
         if (var10 - var12 > 30000L && LAST_QUEUE_WARNING.compareAndSet(var12, var10)) {
            LOG.warn("Chunk cache write queue is full; skipping cache entries until it drains", new Object[0]);
         }
         var7.complete(false);
      }
   }

   public static CompletableFuture<Optional<CompoundTag>> preferCacheOverIncompleteNormal(
      ServerLevel level, ChunkPos pos, Optional<CompoundTag> normal
   ) {
      if (normal.isPresent() && NormalChunkStorage.fromTag(normal.get()) != NormalChunkStorage.State.PROTO) {
         return CompletableFuture.completedFuture(normal);
      }

      return loadIfPresent(level, pos).thenApply(cached -> cached.isPresent() ? cached : normal);
   }

   public static CompletableFuture<Optional<CompoundTag>> loadIfPresent(ServerLevel var0, ChunkPos var1) {
      if (!BackfillGuard.suppress(var0, var1) && promotionAllowed(var0, var1)) {
         Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
         if (snapshot == null) {
            return CompletableFuture.completedFuture(Optional.empty());
         }
         String var2 = snapshot.fingerprint();

         rotateGenerations(var0, snapshot, "load");
         Path var3 = path(var0, var1, var2);
         if (!Files.isRegularFile(var3)) {
            return CompletableFuture.completedFuture(Optional.empty());
         }

         try {
            return CompletableFuture.supplyAsync(
               () -> {
                  try {
                     CompoundTag var4 = NbtIo.readCompressed(var3, NbtAccounter.unlimitedHeap());
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
                        PromotionLedger.mark(var0, var1);
                        return Optional.of(var5x);
                     } else {
                        return Optional.empty();
                     }
                  } catch (Throwable var6) {
                     Path quarantined = GenerationStore.quarantineCorrupt(var3);
                     LOG.warn("Ignoring corrupt chunk cache {}; quarantinedAs={}; vanilla worldgen will be used", new Object[]{var3, quarantined, var6});
                     return Optional.empty();
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

   public static void onNormalSave(ServerLevel var0, ChunkAccess var1, boolean var2) {
      if (var2 && var0 != null && var1 != null && !BackfillGuard.suppress(var0, var1.getPos())) {
         String var3 = Fingerprint.forLevel(var0);
         if (var3 != null) {
            Path var4 = path(var0, var1.getPos(), var3);
            try {
               WRITE_IO.execute(() -> {
                  try {
                     Files.deleteIfExists(var4);
                  } catch (IOException ignored) {
                  }
               });
            } catch (RejectedExecutionException ignored) {
            }
         }
      }
   }

   static boolean cacheExists(ServerLevel var0, ChunkPos var1) {
      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
      return snapshot != null && cacheExists(var0, var1, snapshot.fingerprint());
   }

   static boolean cacheExists(ServerLevel var0, ChunkPos var1, String var2) {
      Fingerprint.Snapshot snapshot = Fingerprint.snapshotForLevel(var0);
      if (snapshot != null && snapshot.fingerprint().equals(var2)) {
         rotateGenerations(var0, snapshot, "scan");
      }
      Path file = path(var0, var1, var2);
      if (!Files.isRegularFile(file)) {
         return false;
      }

      try {
         CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
         if (validEnvelope(root, var0, var1, var2)) {
            return true;
         }
         GenerationStore.quarantineCorrupt(file);
         return false;
      } catch (Throwable error) {
         GenerationStore.quarantineCorrupt(file);
         LOG.warn("Ignoring invalid chunk cache {}; it remains eligible for backfill", new Object[]{file, error});
         return false;
      }
   }

   private static boolean validEnvelope(CompoundTag root, ServerLevel level, ChunkPos pos, String fingerprint) {
      return root != null
         && root.getInt("schema") == 1
         && GenerationStore.acceptsStoredFingerprint(path(level,pos,fingerprint).getParent().getParent(), fingerprint, root.getString("fingerprint"))
         && level.dimension().location().toString().equals(root.getString("dimension"))
         && root.getInt("x") == pos.x
         && root.getInt("z") == pos.z
         && root.contains("chunk")
         && NormalChunkStorage.fromTag(root.getCompound("chunk")) == NormalChunkStorage.State.FULL;
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

   private static void rotateGenerations(ServerLevel var0, Fingerprint.Snapshot snapshot, String trigger) {
      String var2 = dimensionKey(var0) + "|" + snapshot.fingerprint();
      if (ROTATED.add(var2)) {
         try {
            WRITE_IO.execute(() -> GenerationStore.reconcile(var0, snapshot, trigger));
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
