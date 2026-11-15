package maki.chunksmithfixes;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.lod.LodSupport;
import com.kishku7.chunksmith.mixin.ServerChunkCacheMixin;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.common.NeoForge;

public final class LodUpdateQueue {
   private static final Map<LodUpdateQueue.Key, LodUpdateQueue.Entry> TARGETS = new HashMap<>();
   private static final TicketType<Unit> UPDATE = TicketType.create("maki_lod_update", (a, b) -> 0);
   private static MinecraftServer owner;
   private static int threshold = 32;
   private static long quietNanos = 60000000000L;
   private static long ticks;

   private LodUpdateQueue() {
   }

   public static synchronized void start(MinecraftServer server) {
      owner = server;
      TARGETS.clear();
      ticks = 0L;
      threshold = 32;
      quietNanos = 60000000000L;
      Path config = Path.of("config/maki-chunksmith-fixes.properties");
      if (Files.exists(config)) {
         try (Reader reader = Files.newBufferedReader(config)) {
            Properties properties = new Properties();
            properties.load(reader);
            threshold = Integer.parseInt(properties.getProperty("lod-update-block-changes", "32"));
            if (threshold < 1) {
               throw new IllegalArgumentException("lod-update-block-changes must be positive");
            }

            int quietSeconds = Integer.parseInt(properties.getProperty("lod-update-quiet-seconds", "60"));
            if (quietSeconds < 1) {
               throw new IllegalArgumentException("lod-update-quiet-seconds must be positive");
            }

            quietNanos = TimeUnit.SECONDS.toNanos(quietSeconds);
         } catch (Exception error) {
            throw new IllegalStateException("Invalid Chunksmith Fixes settings", error);
         }
      }
   }

   public static synchronized void stop() {
      owner = null;
      TARGETS.clear();
   }

   public static synchronized void changed(LevelChunk chunk) {
      if (chunk.getLevel() instanceof ServerLevel level && level.getServer() == owner) {
         CsLodPresenceIndex index = LodSupport.presenceIndexFor(owner, LodSupport.dimensionId(level));
         if (index != null) {
            boolean stored = index.hasLod(chunk.getPos().x, chunk.getPos().z);
            if (stored || ExploredLodRefresh.pending(index.getRoot(), chunk.getPos().x, chunk.getPos().z)) {
               LodUpdateQueue.Key key = new LodUpdateQueue.Key(LodSupport.dimensionId(level), chunk.getPos().x, chunk.getPos().z);
               LodUpdateQueue.Entry entry = TARGETS.computeIfAbsent(key, ignored -> new LodUpdateQueue.Entry());
               entry.changes.changed(System.nanoTime());
               if (stored && entry.changes.due(threshold)) {
                  ExploredLodRefresh.request(index.getRoot(), key.x, key.z);
               }
            }
         }
      }
   }

   public static synchronized Runnable snapshotAcknowledgement(CsLodChunk record) {
      LodUpdateQueue.Key key = new LodUpdateQueue.Key(record.getDimension(), record.getChunkX(), record.getChunkZ());
      LodUpdateQueue.Entry entry = TARGETS.get(key);
      if (entry == null) {
         return null;
      }

      MinecraftServer server = owner;
      long snapshot = entry.changes.snapshot();
      return () -> acknowledged(server, key, entry, snapshot);
   }

   public static void tick(MinecraftServer server) {
      List<GenerationWorkEvent> work = new ArrayList<>();
      synchronized (LodUpdateQueue.class) {
         if (server != owner || ++ticks % 20L != 0L) {
            return;
         }

         if (!ChunksmithProvider.get().getConfig().isLodFromExploredChunksEnabled() || !LodSupport.lodEnabled(server)) {
            return;
         }

         for (ServerLevel level : server.getAllLevels()) {
            CsLodPresenceIndex index = LodSupport.presenceIndexFor(server, LodSupport.dimensionId(level));
            if (index != null) {
               for (long pos : ExploredLodRefresh.targets(index.getRoot())) {
                  int x = (int)(pos >> 32);
                  int z = (int)pos;
                  if (!index.hasLod(x, z)) {
                     ExploredLodRefresh.resolved(index.getRoot(), x, z);
                  } else {
                     LodUpdateQueue.Key key = new LodUpdateQueue.Key(LodSupport.dimensionId(level), x, z);
                     LodUpdateQueue.Entry entry = TARGETS.computeIfAbsent(key, ignored -> new LodUpdateQueue.Entry());
                     if (!entry.changes.due(threshold)) {
                        entry.changes.restore(threshold, System.nanoTime());
                     }
                  }
               }
            }
         }

         Iterator<Map.Entry<LodUpdateQueue.Key, LodUpdateQueue.Entry>> iterator = TARGETS.entrySet().iterator();

         while (iterator.hasNext()) {
            Map.Entry<LodUpdateQueue.Key, LodUpdateQueue.Entry> item = iterator.next();
            LodUpdateQueue.Key key = item.getKey();
            LodUpdateQueue.Entry entry = item.getValue();
            ServerLevel world = null;
            Iterator index = server.getAllLevels().iterator();

            while (true) {
               if (index.hasNext()) {
                  ServerLevel level = (ServerLevel)index.next();
                  if (!LodSupport.dimensionId(level).equals(key.dimension)) {
                     continue;
                  }

                  world = level;
               }

               if (world != null) {
                  CsLodPresenceIndex indexx = LodSupport.presenceIndexFor(server, key.dimension);
                  if (indexx != null) {
                     if (!indexx.hasLod(key.x, key.z)) {
                        if (!ExploredLodRefresh.pending(indexx.getRoot(), key.x, key.z)) {
                           iterator.remove();
                        }
                     } else {
                        if (entry.changes.due(threshold)) {
                           ExploredLodRefresh.request(indexx.getRoot(), key.x, key.z);
                        }

                        if (!entry.queued && entry.retryAt <= ticks && entry.changes.ready(threshold, System.nanoTime(), quietNanos)) {
                           entry.queued = true;
                           work.add(new GenerationWorkEvent(() -> run(server, key, entry)));
                        }
                     }
                  }
               }
               break;
            }
         }
      }

      work.forEach(NeoForge.EVENT_BUS::post);
   }

   private static CompletableFuture<Void> run(MinecraftServer server, LodUpdateQueue.Key key, LodUpdateQueue.Entry entry) {
      synchronized (LodUpdateQueue.class) {
         if (server != owner || !entry.changes.ready(threshold, System.nanoTime(), quietNanos)) {
            entry.queued = false;
            return CompletableFuture.completedFuture(null);
         }
      }

      ServerLevel level = null;

      for (ServerLevel candidate : server.getAllLevels()) {
         if (LodSupport.dimensionId(candidate).equals(key.dimension)) {
            level = candidate;
            break;
         }
      }

      if (level == null) {
         synchronized (LodUpdateQueue.class) {
            entry.queued = false;
            entry.retryAt = ticks + 100L;
         }

         return CompletableFuture.failedFuture(new IllegalStateException("Update dimension is not available"));
      } else {
         ServerLevel world = level;
         ServerChunkCache cache = world.getChunkSource();
         ChunkPos pos = new ChunkPos(key.x, key.z);
         cache.addRegionTicket(UPDATE, pos, 0, Unit.INSTANCE);

         CompletableFuture<Void> finished;
         try {
            ((ServerChunkCacheMixin)cache).invokeRunDistanceManagerUpdates();
            finished = ((ServerChunkCacheMixin)cache)
               .invokeGetChunkFutureMainThread(key.x, key.z, ChunkStatus.FULL, false)
               .thenComposeAsync(
                  result -> {
                     if (result.isSuccess() && result.orElse(null) instanceof LevelChunk chunk) {
                        long snapshot;
                        synchronized (LodUpdateQueue.class) {
                           if (server != owner || !entry.changes.ready(threshold, System.nanoTime(), quietNanos)) {
                              return CompletableFuture.completedFuture(null);
                           }

                           snapshot = entry.changes.snapshot();
                        }

                        return ExploredLodRefresh.capture(LodSupport.storeRoot(world), key.x, key.z, () -> LodSupport.offer(world, chunk))
                           .thenRun(() -> acknowledged(server, key, entry, snapshot));
                     } else {
                        return CompletableFuture.failedFuture(new IllegalStateException("Existing FULL chunk is not available"));
                     }
                  },
                  server
               );
         } catch (RuntimeException error) {
            finished = CompletableFuture.failedFuture(error);
         }

         return finished.whenComplete((unused, error) -> {
            server.execute(() -> cache.removeRegionTicket(UPDATE, pos, 0, Unit.INSTANCE));
            synchronized (LodUpdateQueue.class) {
               if (server == owner) {
                  entry.queued = false;
                  if (error != null) {
                     entry.retryAt = ticks + 100L;
                  }
               }
            }
         });
      }
   }

   private static synchronized void acknowledged(MinecraftServer server, LodUpdateQueue.Key key, LodUpdateQueue.Entry entry, long snapshot) {
      if (owner == server && TARGETS.get(key) == entry) {
         entry.changes.saved(snapshot);
         if (!entry.changes.due(threshold)) {
            ServerLevel level = null;

            for (ServerLevel candidate : server.getAllLevels()) {
               if (LodSupport.dimensionId(candidate).equals(key.dimension)) {
                  level = candidate;
                  break;
               }
            }

            if (level != null) {
               ExploredLodRefresh.resolved(LodSupport.storeRoot(level), key.x, key.z);
            }
         }

         if (entry.changes.clean()) {
            TARGETS.remove(key, entry);
         }
      }
   }

   private static final class Entry {
      final LodChangeCounter changes = new LodChangeCounter();
      boolean queued;
      long retryAt;
   }

   private record Key(String dimension, int x, int z) {
   }
}
