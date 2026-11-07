package maki.millenairelod;

import com.kishku7.chunksmith.lod.CompositeLodSink;
import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodStoreSink;
import com.kishku7.chunksmith.lod.LiveFeedLedger;
import com.kishku7.chunksmith.lod.LodSink;
import com.kishku7.chunksmith.lod.LodSupport;
import com.kishku7.chunksmith.lod.CsLodChunk.Section;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LodRefreshQueue {
   private static final Logger LOG = LoggerFactory.getLogger("MakiMillenaireLOD");
   private static final String VOXY_SINK = "com.kishku7.chunksmith.lod.VoxyLodSink";
   private static final Object LIFECYCLE_LOCK = new Object();
   private static final ConcurrentHashMap<LodRefreshQueue.WorkKey, LodRefreshQueue.Slot> SLOTS = new ConcurrentHashMap<>();
   private static volatile ExecutorService worker;
   private static volatile MinecraftServer currentServer;
   private static volatile boolean accepting;
   private static volatile boolean stopping;
   private static volatile long epoch;
   private static final SnapshotBudget MEMORY = new SnapshotBudget(32, 67108864L);
   private static final AtomicInteger ACTIVE = new AtomicInteger();
   private static final AtomicInteger PENDING = new AtomicInteger();
   private static final AtomicLong CAPTURED = new AtomicLong();
   private static final AtomicLong INITIAL_CAPTURED = new AtomicLong();
   private static final AtomicLong EXISTING_CAPTURED = new AtomicLong();
   private static final AtomicLong COALESCED = new AtomicLong();
   private static final AtomicLong COMPLETED = new AtomicLong();
   private static final AtomicLong STALE_SKIPPED = new AtomicLong();
   private static final AtomicLong FAILED = new AtomicLong();
   private static final AtomicLong BLOCK_SAMPLES = new AtomicLong();
   private static final AtomicLong UNIQUE_BLOCK_STATES = new AtomicLong();
   private static final AtomicLong SNAPSHOT_NANOS = new AtomicLong();
   private static final AtomicLong SNAPSHOT_MAX_NANOS = new AtomicLong();
   private static final AtomicLong BUILD_NANOS = new AtomicLong();
   private static final AtomicLong STORE_OFFER_NANOS = new AtomicLong();
   private static final AtomicLong STORE_SYNC_FALLBACKS = new AtomicLong();
   private static final AtomicLong MAX_STORE_QUEUE = new AtomicLong();
   private static final AtomicLong RENDERER_OFFER_NANOS = new AtomicLong();
   private static final AtomicLong LAST_REPORT_NANOS = new AtomicLong();

   public static String resourcePressure() {
      return MEMORY.pressured() ? "millenaire-snapshot-backlog" : null;
   }

   private LodRefreshQueue() {
   }

   public static void start(MinecraftServer server) {
      synchronized (LIFECYCLE_LOCK) {
         if (!accepting || currentServer != server || worker == null || worker.isShutdown()) {
            if (worker != null && !worker.isTerminated()) {
               LOG.warn("LOD refresh worker was still alive while a new server was installed; stopping the old worker");
               worker.shutdownNow();
            }

            for (LodRefreshQueue.Slot old : SLOTS.values()) {
               LodRefreshQueue.Snapshot abandoned = old.latest.getAndSet(null);
               if (abandoned != null && abandoned.lease != null) {
                  abandoned.lease.close();
               }
            }

            epoch++;
            SLOTS.clear();
            PENDING.set(0);
            currentServer = server;
            worker = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(32), task -> {
               Thread thread = new Thread(task, "maki-millenaire-lod-refresh");
               thread.setDaemon(true);
               thread.setPriority(Math.max(1, 4));
               return thread;
            }, new AbortPolicy());
            stopping = false;
            accepting = true;
         }
      }
   }

   public static void shutdown() {
      ExecutorService executor;
      synchronized (LIFECYCLE_LOCK) {
         stopping = true;
         executor = worker;
         if (executor == null) {
            accepting = false;
            return;
         }
      }

      boolean interrupted = false;
      long nextWarning = System.nanoTime() + TimeUnit.SECONDS.toNanos(15L);

      while (PENDING.get() > 0) {
         try {
            Thread.sleep(5L);
         } catch (InterruptedException ignored) {
            interrupted = true;
         }

         long now = System.nanoTime();
         if (now >= nextWarning) {
            LOG.warn("Still draining {} pending LOD refresh slots before Chunksmith store shutdown", PENDING.get());
            nextWarning = now + TimeUnit.SECONDS.toNanos(15L);
         }
      }

      synchronized (LIFECYCLE_LOCK) {
         accepting = false;
         executor.shutdown();
      }

      boolean terminated = false;

      while (!terminated) {
         try {
            terminated = executor.awaitTermination(5L, TimeUnit.SECONDS);
            if (!terminated) {
               LOG.warn("Still waiting for Millenaire LOD refresh worker termination before Chunksmith store shutdown");
            }
         } catch (InterruptedException ignored) {
            interrupted = true;
         }
      }

      if (interrupted) {
         Thread.currentThread().interrupt();
      }

      synchronized (LIFECYCLE_LOCK) {
         worker = null;
         currentServer = null;
         SLOTS.clear();
         PENDING.set(0);
      }
   }

   public static boolean captureInitial(ServerLevel level, LevelChunk chunk) {
      return capture(level, chunk, false);
   }

   public static boolean captureExisting(ServerLevel level, LevelChunk chunk) {
      return capture(level, chunk, true);
   }

   private static boolean capture(ServerLevel level, LevelChunk chunk, boolean existing) {
      if (!ensureStarted(level.getServer())) {
         return false;
      }

      long reservation = Math.max(65536L, chunk.getSections().length * 64 * 1024L);
      SnapshotBudget.Lease lease = MEMORY.tryAcquire(reservation);
      if (lease == null) {
         return false;
      }

      long started = System.nanoTime();

      LodRefreshQueue.Snapshot snapshot;
      LodRefreshQueue.SinkPlan plan;
      try {
         plan = LodRefreshQueue.SinkPlan.plan(level);
         if (plan.stores.isEmpty()) {
            lease.close();
            plan.dispatchRenderers(level, chunk);
            return true;
         }

         snapshot = LodRefreshQueue.Snapshot.capture(level, chunk, plan);
         if (!lease.resize(snapshot.estimatedBytes())) {
            lease.close();
            return false;
         }

         snapshot.lease = lease;
      } catch (Throwable error) {
         lease.close();
         throw error;
      }

      long elapsed = System.nanoTime() - started;
      synchronized (LIFECYCLE_LOCK) {
         label123: {
            if (stopping || !accepting || currentServer != level.getServer() || worker == null || worker.isShutdown()) {
               snapshot.lease.close();
               return false;
            }

            LodRefreshQueue.WorkKey key = new LodRefreshQueue.WorkKey(snapshot.dimension, snapshot.chunkX, snapshot.chunkZ);
            LodRefreshQueue.Slot slot = SLOTS.computeIfAbsent(key, ignored -> new LodRefreshQueue.Slot());
            boolean var10000;
            synchronized (slot) {
               LodRefreshQueue.Snapshot previous = slot.latest.getAndSet(snapshot);
               if (previous != null) {
                  previous.lease.close();
               }

               if (previous != null || slot.scheduled.get()) {
                  COALESCED.incrementAndGet();
               }

               if (slot.scheduled.get()) {
                  break label123;
               }

               slot.scheduled.set(true);
               PENDING.incrementAndGet();

               try {
                  worker.execute(() -> drain(key, slot));
                  break label123;
               } catch (RejectedExecutionException rejected) {
                  slot.scheduled.set(false);
                  PENDING.decrementAndGet();
                  if (slot.latest.compareAndSet(snapshot, null)) {
                     snapshot.lease.close();
                  }

                  FAILED.incrementAndGet();
                  LOG.warn("LOD refresh worker rejected {} during lifecycle transition", key);
                  var10000 = false;
               }
            }

            return var10000;
         }
      }

      snapshot.installRenderers(plan.dispatchRenderers(level, chunk));
      CAPTURED.incrementAndGet();
      if (existing) {
         EXISTING_CAPTURED.incrementAndGet();
      } else {
         INITIAL_CAPTURED.incrementAndGet();
      }

      BLOCK_SAMPLES.addAndGet(snapshot.blockSamples);
      UNIQUE_BLOCK_STATES.addAndGet(snapshot.uniqueBlockStates);
      SNAPSHOT_NANOS.addAndGet(elapsed);
      SNAPSHOT_MAX_NANOS.accumulateAndGet(elapsed, Math::max);
      return true;
   }

   private static boolean ensureStarted(MinecraftServer server) {
      if (stopping) {
         return false;
      }

      if (!accepting || currentServer != server || worker == null || worker.isShutdown()) {
         start(server);
      }

      return accepting && !stopping && currentServer == server && worker != null && !worker.isShutdown();
   }

   private static void drain(LodRefreshQueue.WorkKey key, LodRefreshQueue.Slot slot) {
      ACTIVE.incrementAndGet();

      try {
         while (true) {
            LodRefreshQueue.Snapshot snapshot;
            synchronized (slot) {
               snapshot = slot.latest.getAndSet(null);
            }

            if (snapshot == null) {
               synchronized (LIFECYCLE_LOCK) {
                  synchronized (slot) {
                     snapshot = slot.latest.getAndSet(null);
                     if (snapshot == null) {
                        slot.scheduled.set(false);
                        if (slot.epoch == epoch) {
                           PENDING.decrementAndGet();
                        }

                        SLOTS.remove(key, slot);
                        return;
                     }
                  }
               }
            }

            try {
               snapshot.awaitRenderers();
               if (snapshot.server == currentServer) {
                  long buildStart = System.nanoTime();
                  CsLodChunk record = build(snapshot);
                  BUILD_NANOS.addAndGet(System.nanoTime() - buildStart);
                  if (slot.latest.get() != null) {
                     STALE_SKIPPED.incrementAndGet();
                  } else {
                     submit(snapshot, record);
                     COMPLETED.incrementAndGet();
                     reportIfDue();
                  }
               }
            } catch (Throwable error) {
               FAILED.incrementAndGet();
               LOG.error("Could not asynchronously refresh village LOD for {}", key, error);
            } finally {
               snapshot.lease.close();
            }
         }
      } finally {
         ACTIVE.decrementAndGet();
      }
   }

   private static void submit(LodRefreshQueue.Snapshot snapshot, CsLodChunk record) {
      long started = System.nanoTime();

      for (CsLodStoreSink store : snapshot.stores) {
         int beforeDepth = store.queueDepth();
         long beforeSync = store.getSynchronousWrites();
         MAX_STORE_QUEUE.accumulateAndGet(beforeDepth, Math::max);
         store.offer(record);
         long syncDelta = Math.max(0L, store.getSynchronousWrites() - beforeSync);
         if (syncDelta != 0L) {
            STORE_SYNC_FALLBACKS.addAndGet(syncDelta);
         }

         MAX_STORE_QUEUE.accumulateAndGet(store.queueDepth(), Math::max);
      }

      STORE_OFFER_NANOS.addAndGet(System.nanoTime() - started);

      for (LodRefreshQueue.RendererReceipt receipt : snapshot.renderers) {
         if (receipt.voxy) {
            LiveFeedLedger.touch(snapshot.storeRoot, snapshot.chunkX, snapshot.chunkZ, receipt.directFed);
         } else {
            MinecraftServer server = snapshot.server;
            if (server != null) {
               server.execute(
                  () -> {
                     try {
                        receipt.sink.offer(record);
                     } catch (Throwable error) {
                        LOG.warn(
                           "Could not finish non-store LOD sink refresh for {} {},{}",
                           new Object[]{snapshot.dimension, snapshot.chunkX, snapshot.chunkZ, error}
                        );
                     }
                  }
               );
            }
         }
      }
   }

   private static CsLodChunk build(LodRefreshQueue.Snapshot snapshot) {
      LinkedHashMap<String, Integer> blockIds = new LinkedHashMap<>();
      LinkedHashMap<String, Integer> biomeIds = new LinkedHashMap<>();
      List<Section> out = new ArrayList<>(snapshot.sections.length);

      for (LodRefreshQueue.SectionSnapshot section : snapshot.sections) {
         int[] localToGlobal = new int[section.blockPalette.length];

         for (int i = 0; i < localToGlobal.length; i++) {
            localToGlobal[i] = id(blockIds, section.blockPalette[i]);
         }

         int uniformBlock = -1;
         int[] blocks = null;
         if (section.blockIds.length == 0) {
            uniformBlock = localToGlobal[0];
         } else {
            int firstBlock = localToGlobal[Short.toUnsignedInt(section.blockIds[0])];
            boolean uniform = true;

            for (int i = 1; i < section.blockIds.length; i++) {
               if (localToGlobal[Short.toUnsignedInt(section.blockIds[i])] != firstBlock) {
                  uniform = false;
                  break;
               }
            }

            if (uniform) {
               uniformBlock = firstBlock;
            } else {
               blocks = new int[section.blockIds.length];

               for (int i = 0; i < blocks.length; i++) {
                  blocks[i] = localToGlobal[Short.toUnsignedInt(section.blockIds[i])];
               }
            }
         }

         int[] localBiomeToGlobal = new int[section.biomePalette.length];

         for (int i = 0; i < localBiomeToGlobal.length; i++) {
            localBiomeToGlobal[i] = id(biomeIds, section.biomePalette[i]);
         }

         int uniformBiome = -1;
         int[] biomes = null;
         if (section.uniformBiomes) {
            uniformBiome = localBiomeToGlobal[0];
         } else {
            biomes = new int[section.biomeIds.length];

            for (int i = 0; i < biomes.length; i++) {
               biomes[i] = localBiomeToGlobal[Short.toUnsignedInt(section.biomeIds[i])];
            }
         }

         out.add(new Section(blocks, uniformBlock, biomes, uniformBiome, section.skyLight, section.uniformSky, section.blockLight, section.uniformBlockLight));
      }

      return new CsLodChunk(
         snapshot.dimension,
         snapshot.chunkX,
         snapshot.chunkZ,
         snapshot.minSectionY,
         new ArrayList<>(blockIds.keySet()),
         new ArrayList<>(biomeIds.keySet()),
         out
      );
   }

   private static int id(LinkedHashMap<String, Integer> ids, String value) {
      Integer existing = ids.get(value);
      if (existing != null) {
         return existing;
      }

      int id = ids.size();
      ids.put(value, id);
      return id;
   }

   private static List<LodSink> leaves(LodSink sink) {
      return sink instanceof CompositeLodSink composite ? composite.getSinks() : List.of(sink);
   }

   private static void reportIfDue() {
      long now = System.nanoTime();
      long previous = LAST_REPORT_NANOS.get();
      if (previous == 0L || now - previous >= TimeUnit.SECONDS.toNanos(30L)) {
         if (LAST_REPORT_NANOS.compareAndSet(previous, now)) {
            LOG.info(
               "Millenaire protected explored-LOD save/unload (non-task): pending {}, active {}, captured {} (initial {}, existing {}), coalesced {}, completed {}, stale {}, failed {}; server snapshot max {} ms, worker build {} ms total, worker store offer {} ms total, store sync fallbacks {}, max store queue {}",
               new Object[]{
                  PENDING.get(),
                  ACTIVE.get(),
                  CAPTURED.get(),
                  INITIAL_CAPTURED.get(),
                  EXISTING_CAPTURED.get(),
                  COALESCED.get(),
                  COMPLETED.get(),
                  STALE_SKIPPED.get(),
                  FAILED.get(),
                  String.format(Locale.ROOT, "%.1f", SNAPSHOT_MAX_NANOS.get() / 1000000.0),
                  String.format(Locale.ROOT, "%.1f", BUILD_NANOS.get() / 1000000.0),
                  String.format(Locale.ROOT, "%.1f", STORE_OFFER_NANOS.get() / 1000000.0),
                  STORE_SYNC_FALLBACKS.get(),
                  MAX_STORE_QUEUE.get()
               }
            );
         }
      }
   }

   public static Map<String, Object> status() {
      Map<String, Object> out = new LinkedHashMap<>();
      out.put("source", "millenaire-protected-explored-lod");
      out.put("pending", PENDING.get());
      out.put("snapshotBytes", MEMORY.bytes());
      out.put("snapshotCount", MEMORY.count());
      out.put("active", ACTIVE.get());
      out.put("captured", CAPTURED.get());
      out.put("initialCaptured", INITIAL_CAPTURED.get());
      out.put("existingCaptured", EXISTING_CAPTURED.get());
      out.put("coalesced", COALESCED.get());
      out.put("completed", COMPLETED.get());
      out.put("staleSkipped", STALE_SKIPPED.get());
      out.put("failed", FAILED.get());
      out.put("blockSamples", BLOCK_SAMPLES.get());
      out.put("uniqueBlockStates", UNIQUE_BLOCK_STATES.get());
      out.put("snapshotMillisTotal", SNAPSHOT_NANOS.get() / 1000000.0);
      out.put("snapshotMillisMax", SNAPSHOT_MAX_NANOS.get() / 1000000.0);
      out.put("asyncBuildMillisTotal", BUILD_NANOS.get() / 1000000.0);
      out.put("storeOfferMillisTotal", STORE_OFFER_NANOS.get() / 1000000.0);
      out.put("storeSynchronousFallbacks", STORE_SYNC_FALLBACKS.get());
      out.put("maxObservedStoreQueue", MAX_STORE_QUEUE.get());
      out.put("rendererOfferMillisTotal", RENDERER_OFFER_NANOS.get() / 1000000.0);
      return out;
   }

   private static final class LightSnapshot {
      final byte[] packed;
      final int uniform;

      LightSnapshot(byte[] packed, int uniform) {
         this.packed = packed;
         this.uniform = uniform;
      }

      static LodRefreshQueue.LightSnapshot capture(LevelLightEngine engine, LightLayer layer, SectionPos pos) {
         DataLayer data = engine.getLayerListener(layer).getDataLayerData(pos);
         if (data != null && !data.isEmpty()) {
            byte[] packed = new byte[2048];
            int first = -1;
            boolean uniform = true;
            int n = 0;

            for (int y = 0; y < 16; y++) {
               for (int z = 0; z < 16; z++) {
                  for (int x = 0; x < 16; x++) {
                     int value = clampLight(data.get(x, y, z));
                     if (first < 0) {
                        first = value;
                     } else if (value != first) {
                        uniform = false;
                     }

                     if ((n & 1) == 0) {
                        packed[n >> 1] = (byte)value;
                     } else {
                        packed[n >> 1] = (byte)(packed[n >> 1] | value << 4);
                     }

                     n++;
                  }
               }
            }

            return uniform ? new LodRefreshQueue.LightSnapshot(null, first) : new LodRefreshQueue.LightSnapshot(packed, -1);
         } else {
            int value = clampLight(engine.getLayerListener(layer).getLightValue(pos.origin()));
            return new LodRefreshQueue.LightSnapshot(null, value);
         }
      }

      private static int clampLight(int value) {
         return Math.max(0, Math.min(15, value));
      }
   }

   private record RendererReceipt(LodSink sink, boolean directFed, boolean voxy) {
   }

   private static final class SectionSnapshot {
      final String[] blockPalette;
      final short[] blockIds;
      final boolean uniformBlocks;
      final String[] biomePalette;
      final short[] biomeIds;
      final boolean uniformBiomes;
      final byte[] skyLight;
      final int uniformSky;
      final byte[] blockLight;
      final int uniformBlockLight;

      SectionSnapshot(
         String[] blockPalette,
         short[] blockIds,
         boolean uniformBlocks,
         String[] biomePalette,
         short[] biomeIds,
         boolean uniformBiomes,
         LodRefreshQueue.LightSnapshot sky,
         LodRefreshQueue.LightSnapshot block
      ) {
         this.blockPalette = blockPalette;
         this.blockIds = blockIds;
         this.uniformBlocks = uniformBlocks;
         this.biomePalette = biomePalette;
         this.biomeIds = biomeIds;
         this.uniformBiomes = uniformBiomes;
         this.skyLight = sky.packed;
         this.uniformSky = sky.uniform;
         this.blockLight = block.packed;
         this.uniformBlockLight = block.uniform;
      }

      static LodRefreshQueue.SectionSnapshot capture(
         LevelChunkSection section, SectionPos pos, LevelLightEngine light, IdentityHashMap<BlockState, String> serializedStates
      ) {
         IdentityHashMap<BlockState, Integer> localBlocks = new IdentityHashMap<>();
         List<String> blockPalette = new ArrayList<>();
         short[] blockIds;
         if (section.hasOnlyAir()) {
            BlockState state = section.getBlockState(0, 0, 0);
            String serialized = serializedStates.get(state);
            if (serialized == null) {
               serialized = BlockStateParser.serialize(state);
               serializedStates.put(state, serialized);
            }

            localBlocks.put(state, 0);
            blockPalette.add(serialized);
            blockIds = new short[0];
         } else {
            blockIds = new short[4096];
            int n = 0;

            for (int y = 0; y < 16; y++) {
               for (int z = 0; z < 16; z++) {
                  for (int x = 0; x < 16; x++) {
                     BlockState state = section.getBlockState(x, y, z);
                     Integer id = localBlocks.get(state);
                     if (id == null) {
                        id = blockPalette.size();
                        localBlocks.put(state, id);
                        String serialized = serializedStates.get(state);
                        if (serialized == null) {
                           serialized = BlockStateParser.serialize(state);
                           serializedStates.put(state, serialized);
                        }

                        blockPalette.add(serialized);
                     }

                     blockIds[n++] = (short)id.intValue();
                  }
               }
            }
         }

         LinkedHashMap<String, Integer> localBiomes = new LinkedHashMap<>();
         short[] biomeIds = new short[64];
         int b = 0;

         for (int y = 0; y < 4; y++) {
            for (int z = 0; z < 4; z++) {
               for (int x = 0; x < 4; x++) {
                  Holder<Biome> holder = section.getNoiseBiome(x, y, z);
                  String biome = holder.unwrapKey().map(key -> key.location().toString()).orElse("minecraft:plains");
                  Integer id = localBiomes.get(biome);
                  if (id == null) {
                     id = localBiomes.size();
                     localBiomes.put(biome, id);
                  }

                  biomeIds[b++] = (short)id.intValue();
               }
            }
         }

         LodRefreshQueue.LightSnapshot sky = LodRefreshQueue.LightSnapshot.capture(light, LightLayer.SKY, pos);
         LodRefreshQueue.LightSnapshot block = LodRefreshQueue.LightSnapshot.capture(light, LightLayer.BLOCK, pos);
         return new LodRefreshQueue.SectionSnapshot(
            blockPalette.toArray(String[]::new),
            blockIds,
            blockPalette.size() == 1,
            localBiomes.keySet().toArray(String[]::new),
            biomeIds,
            localBiomes.size() == 1,
            sky,
            block
         );
      }
   }

   private static final class SinkPlan {
      final List<CsLodStoreSink> stores;
      final List<LodSink> rendererSinks;
      final Path storeRoot;
      final MinecraftServer server;

      SinkPlan(List<CsLodStoreSink> stores, List<LodSink> rendererSinks, Path storeRoot, MinecraftServer server) {
         this.stores = stores;
         this.rendererSinks = rendererSinks;
         this.storeRoot = storeRoot;
         this.server = server;
      }

      static LodRefreshQueue.SinkPlan plan(ServerLevel level) {
         LodSink root = LodSupport.sinkFor(level);
         List<CsLodStoreSink> stores = new ArrayList<>();
         List<LodSink> renderers = new ArrayList<>();

         for (LodSink leaf : LodRefreshQueue.leaves(root)) {
            if (leaf instanceof CsLodStoreSink store) {
               stores.add(store);
            } else {
               renderers.add(leaf);
            }
         }

         return new LodRefreshQueue.SinkPlan(List.copyOf(stores), List.copyOf(renderers), LodSupport.storeRoot(level), level.getServer());
      }

      List<LodRefreshQueue.RendererReceipt> dispatchRenderers(ServerLevel level, LevelChunk chunk) {
         List<LodRefreshQueue.RendererReceipt> receipts = new ArrayList<>(this.rendererSinks.size());

         for (LodSink leaf : this.rendererSinks) {
            long started = System.nanoTime();
            boolean voxy = "com.kishku7.chunksmith.lod.VoxyLodSink".equals(leaf.getClass().getName());
            boolean directFed = false;

            try {
               boolean accepted = leaf.offer(chunk);
               directFed = voxy && accepted && leaf.feedsRendererDirectly();
            } catch (RuntimeException | LinkageError error) {
               LodRefreshQueue.LOG.warn("Could not offer live protected chunk to non-store LOD sink {}", leaf.getClass().getName(), error);
            } finally {
               LodRefreshQueue.RENDERER_OFFER_NANOS.addAndGet(System.nanoTime() - started);
            }

            receipts.add(new LodRefreshQueue.RendererReceipt(leaf, directFed, voxy));
         }

         return List.copyOf(receipts);
      }
   }

   private static final class Slot {
      final long epoch;
      final AtomicReference<LodRefreshQueue.Snapshot> latest;
      final AtomicBoolean scheduled;

      private Slot() {
         this.epoch = LodRefreshQueue.epoch;
         this.latest = new AtomicReference<>();
         this.scheduled = new AtomicBoolean();
      }
   }

   private static final class Snapshot {
      SnapshotBudget.Lease lease;
      final MinecraftServer server;
      final String dimension;
      final int chunkX;
      final int chunkZ;
      final int minSectionY;
      final LodRefreshQueue.SectionSnapshot[] sections;
      final List<CsLodStoreSink> stores;
      volatile List<LodRefreshQueue.RendererReceipt> renderers = List.of();
      final CompletableFuture<Void> renderersReady = new CompletableFuture<>();
      final Path storeRoot;
      final long blockSamples;
      final long uniqueBlockStates;

      long estimatedBytes() {
         long bytes = 512L;

         for (LodRefreshQueue.SectionSnapshot section : this.sections) {
            bytes += 256L + section.blockIds.length * 2L + section.biomeIds.length * 2L;

            for (String state : section.blockPalette) {
               bytes += 48L + state.length() * 2L;
            }

            for (String biome : section.biomePalette) {
               bytes += 48L + biome.length() * 2L;
            }

            if (section.skyLight != null) {
               bytes += section.skyLight.length;
            }

            if (section.blockLight != null) {
               bytes += section.blockLight.length;
            }
         }

         return bytes;
      }

      Snapshot(
         MinecraftServer server,
         String dimension,
         int chunkX,
         int chunkZ,
         int minSectionY,
         LodRefreshQueue.SectionSnapshot[] sections,
         LodRefreshQueue.SinkPlan plan,
         long blockSamples,
         long uniqueBlockStates
      ) {
         this.server = server;
         this.dimension = dimension;
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
         this.minSectionY = minSectionY;
         this.sections = sections;
         this.stores = plan.stores;
         this.storeRoot = plan.storeRoot;
         this.blockSamples = blockSamples;
         this.uniqueBlockStates = uniqueBlockStates;
      }

      void installRenderers(List<LodRefreshQueue.RendererReceipt> receipts) {
         this.renderers = receipts;
         this.renderersReady.complete(null);
      }

      void awaitRenderers() {
         this.renderersReady.join();
      }

      static LodRefreshQueue.Snapshot capture(ServerLevel level, LevelChunk chunk, LodRefreshQueue.SinkPlan plan) {
         LevelChunkSection[] source = chunk.getSections();
         LodRefreshQueue.SectionSnapshot[] sections = new LodRefreshQueue.SectionSnapshot[source.length];
         IdentityHashMap<BlockState, String> serializedStates = new IdentityHashMap<>();
         LevelLightEngine light = level.getLightEngine();
         int minSectionY = chunk.getMinSection();
         int chunkX = chunk.getPos().x;
         int chunkZ = chunk.getPos().z;
         long samples = 0L;

         for (int i = 0; i < source.length; i++) {
            LevelChunkSection section = source[i];
            SectionPos sectionPos = SectionPos.of(chunkX, minSectionY + i, chunkZ);
            sections[i] = LodRefreshQueue.SectionSnapshot.capture(section, sectionPos, light, serializedStates);
            samples += Math.max(1, sections[i].blockIds.length);
         }

         return new LodRefreshQueue.Snapshot(
            level.getServer(), level.dimension().location().toString(), chunkX, chunkZ, minSectionY, sections, plan, samples, serializedStates.size()
         );
      }
   }

   private record WorkKey(String dimension, int chunkX, int chunkZ) {
   }
}
