package dev.diorama.render;

import dev.diorama.Diorama;
import dev.diorama.Facing;
import dev.diorama.MapScreen;
import dev.diorama.MapSession;
import dev.diorama.Projection;
import dev.diorama.bake.BlockLooks;
import dev.diorama.bake.Looks;
import dev.diorama.bake.TileBaker;
import dev.diorama.bake.TileKey;
import dev.diorama.data.ChunkSave;
import dev.diorama.data.ChunkSnapshot;
import dev.diorama.data.ChunkTags;
import dev.diorama.data.SavedChunkSource;
import dev.diorama.data.SavedChunks;
import dev.diorama.share.SharedChunks;
import dev.diorama.waypoint.Portals;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public final class SavedChunkImport {
   private static final long START_DELAY_MS = 5000L;
   private static final long RESCAN_MS = 60000L;
   private static final long SAVE_PROGRESS_MS = 10000L;
   private static final long RETRY_MS = 30000L;
   private static final long BUSY_WAIT_MS = 250L;
   private static final double MAX_HEAP_SHARE = 0.9;
   private static final long MAX_HEAP_WAIT_MS = 2000L;
   private static final int MAX_UNWRITTEN = 32;
   private static final int TYPICAL_Y = 64;
   private static final int WORKERS = Math.max(1, MapLevel.BAKER_COUNT - 1);
   private static final boolean PACED = MapLevel.BAKER_COUNT == 1;
   private static final Map<Path, ReentrantLock> FOLDER_LOCKS = new ConcurrentHashMap<>();
   private static final long FOLDER_WAIT_WARN_MS = 30000L;
   private final MapLevel map;
   private final ChunkSave save;
   private final SavedChunkSource source;
   private final Thread thread;
   private final ExecutorService workers = Executors.newFixedThreadPool(WORKERS, task -> {
      Thread thread = new Thread(task, "diorama-saved-chunks-worker");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });
   private final CountDownLatch stop = new CountDownLatch(1);
   private volatile boolean idle;
   private volatile boolean rescanAsked;
   private volatile boolean onlyRemembered;
   private final Map<Long, Boolean> remembered = new ConcurrentHashMap<>();
   private @Nullable Path folder;
   private @Nullable ReentrantLock folderLock;
   private volatile SavedChunkImport.@Nullable Progress progress;
   private Map<Long, int[]> regions = Map.of();
   private long lastScan;
   private long heapFullSince;
   private final Set<TileKey> viewChecked = new HashSet<>();
   private int scannedAt;
   private long lastSave;
   private final Set<Long> pending = new HashSet<>();
   private volatile int chunksToDo;
   private volatile int chunksInSave;
   private volatile int chunksDone;
   private final Map<Long, SavedChunkImport.Plan> plans = new HashMap<>();
   private final Map<Long, Long> heldUntil = new HashMap<>();
   private final Map<TileKey, Integer> drawn = new ConcurrentHashMap<>();
   private int drawnCount;
   private long drawNanos;

   private SavedChunkImport(MapLevel map, ChunkSave save, Consumer<List<Portals.Spot>> portals) {
      this.map = map;
      this.save = save;
      this.source = new SavedChunkSource(map.world(), save, portals);
      this.thread = new Thread(this::run, "diorama-saved-chunks");
      this.thread.setDaemon(true);
      this.thread.setPriority(1);
   }

   public static boolean needsRelight(MapLevel map, ClientLevel level) {
      Path folder = map.importFolder();
      if (folder != null && !map.roofless() && !level.dimensionType().hasFixedTime()) {
         try (DataInputStream in = new DataInputStream(Files.newInputStream(folder.resolve("saved-chunks.bin")))) {
            int magic = in.readInt();
            if (magic == 1146307377) {
               return true;
            }

            if (magic != 1146307378) {
               return false;
            }

            DataInputStream data = new DataInputStream(new InflaterInputStream(in));
            int count = data.readInt();

            for (int r = 0; r < count; r++) {
               data.readInt();
               data.readInt();

               for (int i = 0; i < 1024; i++) {
                  if (data.readInt() == 2) {
                     return true;
                  }
               }
            }

            return false;
         } catch (IOException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static @Nullable SavedChunkImport start(MapLevel map, ClientLevel level, Consumer<List<Portals.Spot>> portals) {
      SavedChunks save = SavedChunks.open(level, map.roofless());
      return save == null ? null : start(map, save, portals);
   }

   public static @Nullable SavedChunkImport startShared(MapLevel map, ClientLevel level, Consumer<List<Portals.Spot>> portals) {
      if (map.roofless()) {
         return null;
      }

      SharedChunks save = SharedChunks.open(level);
      if (save == null) {
         return null;
      }

      SavedChunkImport job = start(map, save, portals);
      save.onChange(job::rescanSoon);
      return job;
   }

   private static SavedChunkImport start(MapLevel map, ChunkSave save, Consumer<List<Portals.Spot>> portals) {
      SavedChunkImport job = new SavedChunkImport(map, save, portals);
      map.watchLiveChunks(true);
      job.thread.start();
      return job;
   }

   public void stop() {
      this.map.watchLiveChunks(false);
      this.save.close();
      this.stop.countDown();
   }

   public void onlyRemembered(boolean only) {
      if (this.onlyRemembered != only) {
         this.onlyRemembered = only;
         this.rescanAsked = true;
         this.remembered.clear();
         this.drawn.clear();
         this.source.allow(only ? this::remembered : (chunkX, chunkZ, top) -> true);
      }
   }

   private boolean remembered(int chunkX, int chunkZ, int top) {
      SavedChunkImport.Progress progress = this.progress;
      if (progress != null && progress.relight.contains(ChunkSnapshot.key(chunkX, chunkZ))) {
         return true;
      }

      MapMemory memory = this.map.memory();
      return memory != null && this.remembered.computeIfAbsent(ChunkSnapshot.key(chunkX, chunkZ), k -> memory.showsChunk(chunkX, chunkZ, top));
   }

   public int chunksLeft() {
      return this.idle ? 0 : Math.max(0, this.chunksToDo - this.chunksDone);
   }

   public int percentDone() {
      int all = this.chunksInSave;
      return all == 0 ? 0 : Math.min(99, 100 * (all - this.chunksLeft()) / all);
   }

   public boolean idle() {
      return this.idle && !this.rescanAsked;
   }

   public void refresh() {
      this.save.refresh();
   }

   public void rescanSoon() {
      this.rescanAsked = true;
   }

   public int drawnCount() {
      return this.drawnCount;
   }

   public boolean drew(TileKey key) {
      return this.drawn.containsKey(key);
   }

   public boolean drewChunk(int chunkX, int chunkZ) {
      SavedChunkImport.Progress progress = this.progress;
      return progress != null && progress.time(chunkX >> 5, chunkZ >> 5, index(chunkX, chunkZ)) > 0;
   }

   public boolean finished() {
      return !this.thread.isAlive() && this.workers.isTerminated();
   }

   private boolean stopped() {
      return this.stop.getCount() == 0L;
   }

   private boolean pause(long ms) {
      try {
         return this.stop.await(ms, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         return true;
      }
   }

   private void run() {
      for (boolean stopped = this.pause(5000L); !stopped; stopped = this.stopped()) {
         try {
            this.step();
         } catch (Looks.StatesRenumbered var8) {
         } catch (Throwable t) {
            if (!this.stopped()) {
               Diorama.LOGGER.error("Drawing saved chunks failed", t);
               this.pause(60000L);
            }
         }
      }

      try {
         this.workers.shutdown();
         if (this.progress != null) {
            this.progress.saveIfChanged();
         }
      } finally {
         this.leaveFolder();
      }
   }

   private boolean takeFolder(Path folder) {
      ReentrantLock lock = FOLDER_LOCKS.computeIfAbsent(folder, key -> new ReentrantLock());
      long warnAt = System.currentTimeMillis() + 30000L;

      while (!lock.tryLock()) {
         if (this.pause(100L)) {
            return false;
         }

         if (warnAt != 0L && System.currentTimeMillis() > warnAt) {
            Diorama.LOGGER.warn("Drawing saved chunks into {} waits for an earlier fill there to finish", folder);
            warnAt = 0L;
         }
      }

      this.folderLock = lock;
      return true;
   }

   private void leaveFolder() {
      ReentrantLock lock = this.folderLock;
      this.folderLock = null;
      if (lock != null && lock.isHeldByCurrentThread()) {
         lock.unlock();
      }
   }

   private void step() {
      Path folder = this.map.importFolder();
      BlockLooks looks = BlockLooks.get();
      if (folder != null && looks != null && this.save.isOpen()) {
         if (!folder.equals(this.folder)) {
            if (this.progress != null) {
               this.progress.saveIfChanged();
            }

            this.progress = null;
            this.folder = null;
            this.leaveFolder();
            if (!this.takeFolder(folder)) {
               return;
            }

            this.folder = folder;
            boolean night = !this.map.roofless() && !this.save.level().dimensionType().hasFixedTime();
            this.progress = SavedChunkImport.Progress.load(folder.resolve("saved-chunks.bin"), night);
            this.plans.clear();
            this.heldUntil.clear();
            this.drawn.clear();
            this.viewChecked.clear();
            this.remembered.clear();
            this.lastScan = 0L;
         }

         SavedChunkImport.Progress progress = this.progress;
         this.map.takeLiveChunks(key -> {
            int cx = (int)(key >> 32);
            int cz = (int)key;
            progress.mark(cx >> 5, cz >> 5, index(cx, cz), -1);
         });
         long now = System.currentTimeMillis();
         if (now - this.lastScan >= 60000L || this.rescanAsked) {
            this.idle = false;
            this.rescanAsked = false;
            this.lastScan = now;
            this.scannedAt = this.save.now();
            Map<Long, int[]> before = this.regions;
            this.regions = this.save.scan();
            if (progress.back(this.regions)) {
               progress.saveIfChanged();
            }

            for (Entry<Long, int[]> region : this.regions.entrySet()) {
               if (!Arrays.equals(region.getValue(), before.get(region.getKey()))) {
                  this.source.forgetTops(region.getKey());
               }
            }

            this.pending.clear();
            int toDo = 0;
            int inSave = 0;

            for (Entry<Long, int[]> region : this.regions.entrySet()) {
               if (progress.hasWork(region.getKey(), region.getValue(), this.onlyRemembered)) {
                  this.pending.add(region.getKey());
               }

               int regionX = (int)(region.getKey() >> 32);
               int regionZ = (int)region.getKey().longValue();

               for (int i = 0; i < 1024; i++) {
                  inSave += ((int[])region.getValue())[i] == 0 ? 0 : 1;
                  toDo += progress.isWork(regionX, regionZ, i, region.getValue()[i], this.onlyRemembered) ? 1 : 0;
               }
            }

            this.chunksToDo = toDo;
            this.chunksInSave = inSave;
            this.chunksDone = 0;
            if (Diorama.DEBUG) {
               Diorama.LOGGER
                  .info("Saved chunks: listed {} regions, {} with work, {} chunks to draw", new Object[]{this.regions.size(), this.pending.size(), toDo});
            }
         }

         Long next = this.nearestPending();
         if (next == null) {
            progress.saveIfChanged();
            this.source.clear();
            if (this.pending.isEmpty() && !this.idle) {
               this.idle = true;
               if (this.drawnCount > 0) {
                  Diorama.LOGGER.info("Saved chunks: drew {} tiles, {} ms each", this.drawnCount, this.drawNanos / this.drawnCount / 1000000L);
               }
            }

            this.pause(100L);
         } else {
            this.draw(next, looks, folder, progress);
         }
      } else {
         this.pause(1000L);
      }
   }

   private @Nullable Long nearestPending() {
      double[] focus = this.map.importFocus();
      double x;
      double z;
      if (focus != null) {
         Facing facing = Projection.facing();
         double turnedZ = Projection.zAt(focus[1], 64.0);
         x = facing.worldX(focus[0], turnedZ);
         z = facing.worldZ(focus[0], turnedZ);
      } else {
         Entity viewer = MapSession.viewer();
         x = viewer == null ? 0.0 : viewer.getX();
         z = viewer == null ? 0.0 : viewer.getZ();
      }

      Long best = null;
      double bestDistance = Double.MAX_VALUE;
      long now = System.currentTimeMillis();

      for (long region : this.pending) {
         if (this.heldUntil.getOrDefault(region, 0L) <= now) {
            double dx = (region >> 32) * 512.0 + 256.0 - x;
            double dz = (int)region * 512.0 + 256.0 - z;
            double distance = dx * dx + dz * dz;
            if (distance < bestDistance) {
               bestDistance = distance;
               best = region;
            }
         }
      }

      return best;
   }

   private @Nullable TileKey nextViewTile(Set<TileKey> inProgress) {
      if (this.onlyRemembered) {
         return null;
      }

      for (TileKey key : this.map.viewTiles()) {
         if (!this.drawn.containsKey(key) && !inProgress.contains(key)) {
            Facing facing = Projection.facing();
            double u = key.minU() + key.units() / 2.0;
            double z = Projection.zAt(key.minV() + key.units() / 2.0, 64.0);
            int regionX = (int)Math.floor(facing.worldX(u, z)) >> 9;
            int regionZ = (int)Math.floor(facing.worldZ(u, z)) >> 9;
            if (this.pending.contains(SavedChunks.regionKey(regionX, regionZ)) && this.viewChecked.add(key) && !this.map.pictureWhole(key)) {
               if (this.save.prefetches()) {
                  this.save.prefetch(this.chunksUnder(Set.of(key), regionX, regionZ, facing), true);
               }

               return key;
            }
         }
      }

      return null;
   }

   private void draw(long region, BlockLooks looks, Path folder, SavedChunkImport.Progress progress) {
      this.source.clear();
      int readSince = Math.min(this.scannedAt, this.save.now());
      SavedChunkImport.Plan plan = this.plans.get(region);
      if (plan == null || plan.onlyRemembered != this.onlyRemembered || plan.facing != Projection.facing()) {
         plan = this.plan(region, progress);
         if (plan == null) {
            return;
         }

         this.plans.put(region, plan);
      }

      List<TileKey> retry = new ArrayList<>();
      CompletionService<SavedChunkImport.Drawn> drawing = new ExecutorCompletionService<>(this.workers);
      Set<TileKey> inProgress = new HashSet<>();
      Set<TileKey> forViews = new HashSet<>();
      int running = 0;
      boolean leaving = false;
      RuntimeException failure = null;

      while (running > 0 || !leaving && !plan.order.isEmpty()) {
         leaving |= this.stopped()
            || !folder.equals(this.map.importFolder())
            || plan.onlyRemembered != this.onlyRemembered
            || plan.facing != Projection.facing();

         while (true) {
            if (!leaving && running < this.workersNow() && !this.save.serverBehind() && this.map.unwrittenTiles() < 32 && !this.memoryTight()) {
               TileKey viewTile = this.nextViewTile(inProgress);
               if (viewTile != null) {
                  inProgress.add(viewTile);
                  forViews.add(viewTile);
                  this.submit(drawing, viewTile, looks, plan.facing);
                  running++;
                  continue;
               }

               if (!plan.order.isEmpty()) {
                  Long nearest = this.nearestPending();
                  if (nearest != null && nearest != region) {
                     leaving = true;
                  } else {
                     TileKey key = plan.order.get(plan.order.size() - 1);
                     if (!inProgress.contains(key)) {
                        plan.order.remove(plan.order.size() - 1);
                        if (this.needsDrawing(key, plan.tiles.get(key), plan, progress)) {
                           inProgress.add(key);
                           this.submit(drawing, key, looks, plan.facing);
                           running++;
                        } else {
                           this.tileDone(key, plan, progress);
                        }
                        continue;
                     }
                  }
               }
            }

            if (running == 0) {
               if (!leaving && !plan.order.isEmpty()) {
                  if (heapNearlyFull()) {
                     this.source.clear();
                  }

                  this.pause(250L);
               }
            } else {
               SavedChunkImport.Drawn done;
               try {
                  done = drawing.take().get();
               } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  return;
               } catch (ExecutionException e) {
                  throw new CompletionException(e.getCause());
               }

               running--;
               inProgress.remove(done.key());
               if (done.failure() != null && failure == null) {
                  failure = done.failure();
                  leaving = true;
               }

               if (!this.stopped()) {
                  boolean planned = !forViews.remove(done.key());
                  if (!done.taken()) {
                     if (planned) {
                        retry.add(done.key());
                     }
                  } else {
                     this.drawNanos = this.drawNanos + done.nanos();
                     this.drawnCount++;
                     this.drawn.put(done.key(), readSince);
                     if (planned) {
                        this.tileDone(done.key(), plan, progress);
                     }
                  }
               }
            }
            break;
         }
      }

      if (leaving) {
         plan.order.addAll(retry);
         if (failure != null) {
            throw failure;
         }
      } else if (!retry.isEmpty()) {
         plan.order.addAll(retry);
         this.heldUntil.put(region, System.currentTimeMillis() + 30000L);
      } else {
         this.plans.remove(region);
         this.pending.remove(region);
         this.heldUntil.remove(region);
         progress.saveIfChanged();
      }
   }

   private static boolean heapNearlyFull() {
      Runtime runtime = Runtime.getRuntime();
      return runtime.totalMemory() - runtime.freeMemory() > runtime.maxMemory() * 0.9;
   }

   private boolean memoryTight() {
      if (!heapNearlyFull()) {
         this.heapFullSince = 0L;
         return false;
      }

      long now = System.currentTimeMillis();
      if (this.heapFullSince == 0L) {
         this.heapFullSince = now;
      }

      if (now - this.heapFullSince > 2000L) {
         this.heapFullSince = now;
         return false;
      } else {
         return true;
      }
   }

   private void submit(CompletionService<SavedChunkImport.Drawn> drawing, TileKey key, BlockLooks looks, Facing facing) {
      drawing.submit(() -> {
         long start = System.nanoTime();

         try {
            boolean taken = this.map.bakeFromSave(key, this.source, looks, facing);
            long nanos = System.nanoTime() - start;
            if (PACED && playing()) {
               this.pause(3L * nanos / 1000000L);
            }

            return new SavedChunkImport.Drawn(key, taken, nanos, null);
         } catch (ChunkSave.Late e) {
            return new SavedChunkImport.Drawn(key, false, 0L, null);
         } catch (RuntimeException e) {
            return new SavedChunkImport.Drawn(key, false, 0L, e);
         }
      });
   }

   private int workersNow() {
      return playing() ? 1 : WORKERS;
   }

   public static boolean playing() {
      Minecraft mc = Minecraft.getInstance();
      return !mc.isPaused() && !(mc.screen instanceof MapScreen);
   }

   private void tileDone(TileKey key, SavedChunkImport.Plan plan, SavedChunkImport.Progress progress) {
      for (long chunk : plan.tiles.get(key)) {
         if (plan.waiting.merge(chunk, -1, Integer::sum) == 0) {
            this.chunksDone++;
            int cx = (int)(chunk >> 32);
            int cz = (int)chunk;
            progress.mark(cx >> 5, cz >> 5, index(cx, cz), plan.times.get(chunk));
         }
      }

      if (System.currentTimeMillis() - this.lastSave > 10000L) {
         progress.saveIfChanged();
         this.lastSave = System.currentTimeMillis();
      }
   }

   private boolean needsDrawing(TileKey key, Set<Long> chunks, SavedChunkImport.Plan plan, SavedChunkImport.Progress progress) {
      Integer drawnAt = this.drawn.get(key);
      boolean first = true;
      boolean changedSince = false;

      for (long chunk : chunks) {
         int cx = (int)(chunk >> 32);
         int cz = (int)chunk;
         first &= progress.time(cx >> 5, cz >> 5, index(cx, cz)) == 0;
         changedSince |= drawnAt == null || plan.times.get(chunk) >= drawnAt;
      }

      return changedSince && (!first || !this.map.pictureWhole(key));
   }

   private SavedChunkImport.@Nullable Plan plan(long region, SavedChunkImport.Progress progress) {
      int regionX = (int)(region >> 32);
      int regionZ = (int)region;
      int[] times = this.regions.get(region);
      SavedChunkImport.Plan plan = new SavedChunkImport.Plan(this.onlyRemembered, Projection.facing());
      MapMemory memory = plan.onlyRemembered ? this.map.memory() : null;

      for (int i = 0; i < 1024; i++) {
         int time = times[i];
         if (progress.isWork(regionX, regionZ, i, time, plan.onlyRemembered)) {
            while (!this.stopped() && this.save.serverBehind()) {
               this.pause(250L);
            }

            if (this.stopped()) {
               return null;
            }

            int cx = regionX << 5 | i & 31;
            int cz = regionZ << 5 | i >> 5;
            ChunkTags.Summary summary = this.save.summary(cx, cz);
            if (this.stopped()) {
               return null;
            }

            if (summary != null && summary.finished()) {
               if (!plan.onlyRemembered || progress.time(regionX, regionZ, i) == 2 || memory != null && memory.showsChunk(cx, cz, summary.maxY())) {
                  long chunk = ChunkSnapshot.key(cx, cz);
                  plan.times.put(chunk, time);

                  for (TileKey key : this.tilesShowing(cx, cz, summary, plan.facing)) {
                     plan.tiles.computeIfAbsent(key, k -> new HashSet<>()).add(chunk);
                     plan.waiting.merge(chunk, 1, Integer::sum);
                  }
               } else {
                  progress.mark(regionX, regionZ, i, -2);
                  this.chunksDone++;
               }
            } else {
               progress.mark(regionX, regionZ, i, time);
               this.chunksDone++;
            }
         }
      }

      plan.order.addAll(plan.tiles.keySet());
      plan.order.sort(Comparator.comparingInt(TileKey::tileU).thenComparingInt(TileKey::tileV).reversed());
      if (!plan.tiles.isEmpty() && this.save.prefetches()) {
         this.save.prefetch(this.chunksUnder(plan.tiles.keySet(), regionX, regionZ, plan.facing), false);
      }

      return plan;
   }

   private List<Long> chunksUnder(Set<TileKey> tiles, int regionX, int regionZ, Facing facing) {
      Set<Long> under = new HashSet<>();

      for (int rx = regionX - 1; rx <= regionX + 1; rx++) {
         for (int rz = regionZ - 1; rz <= regionZ + 1; rz++) {
            for (int i = 0; i < 1024; i++) {
               int cx = rx << 5 | i & 31;
               int cz = rz << 5 | i >> 5;
               ChunkTags.Summary summary = this.save.summary(cx, cz);
               if (summary != null && summary.finished()) {
                  for (TileKey key : this.tilesShowing(cx, cz, summary, facing)) {
                     if (tiles.contains(key)) {
                        for (int dx = -1; dx <= 1; dx++) {
                           for (int dz = -1; dz <= 1; dz++) {
                              under.add(ChunkSnapshot.key(cx + dx, cz + dz));
                           }
                        }
                        break;
                     }
                  }
               }
            }
         }
      }

      List<Long> ordered = new ArrayList<>(under);
      ordered.sort(Comparator.<Long>comparingInt(keyx -> (int)(keyx >> 32)).thenComparingInt(keyx -> (int)keyx.longValue()));
      return ordered;
   }

   private List<TileKey> tilesShowing(int cx, int cz, ChunkTags.Summary summary, Facing facing) {
      int x0 = facing.blockX(cx, cz) << 4;
      int z0 = facing.blockZ(cx, cz) << 4;
      int x1 = x0 + 15;
      int z1 = z0 + 15;
      double top = summary.maxY() + 2;
      double bottom = summary.floorY() - 1 - TileBaker.SHADOW_REACH_Y - 12;
      if (this.map.roofless()) {
         top = Math.min(top, this.map.roof());
         bottom = this.map.minY() - 1;
      }

      double u0 = x0 - 1 - 12;
      double u1 = x1 + 2 + Math.max(TileBaker.SHADOW_REACH_X, 12);
      double v0 = Projection.v(top, z0 - 1 - Math.max(TileBaker.SHADOW_REACH_Z, 12));
      double v1 = Projection.v(bottom, z1 + 2 + 12);
      double units = 128.0;
      List<TileKey> keys = new ArrayList<>();

      for (int tu = (int)Math.floor(u0 / units); tu <= (int)Math.floor(u1 / units); tu++) {
         for (int tv = (int)Math.floor(v0 / units); tv <= (int)Math.floor(v1 / units); tv++) {
            keys.add(new TileKey(Integer.MIN_VALUE, tu, tv));
         }
      }

      return keys;
   }

   private static int index(int cx, int cz) {
      return (cz & 31) << 5 | cx & 31;
   }

   private record Drawn(TileKey key, boolean taken, long nanos, @Nullable RuntimeException failure) {
   }

   private static final class Plan {
      final boolean onlyRemembered;
      final Facing facing;
      final List<TileKey> order = new ArrayList<>();
      final Map<TileKey, Set<Long>> tiles = new HashMap<>();
      final Map<Long, Integer> waiting = new HashMap<>();
      final Map<Long, Integer> times = new HashMap<>();

      Plan(boolean onlyRemembered, Facing facing) {
         this.onlyRemembered = onlyRemembered;
         this.facing = facing;
      }
   }

   private static final class Progress {
      private static final int MAGIC = 1146307378;
      private static final int BEFORE_LIGHT = 1146307377;
      static final int LIVE = -1;
      static final int NOT_REMEMBERED = -2;
      static final int CHANGED = 1;
      static final int RELIGHT = 2;
      private final Path file;
      private final Map<Long, int[]> times = new HashMap<>();
      final LongOpenHashSet relight = new LongOpenHashSet();
      private boolean changed;
      private int leftAt;
      private boolean leftChecked;

      private Progress(Path file) {
         this.file = file;
      }

      static SavedChunkImport.Progress load(Path file, boolean night) {
         SavedChunkImport.Progress progress = new SavedChunkImport.Progress(file);
         Path left = file.resolveSibling("left-at");

         try {
            if (Files.isRegularFile(left)) {
               progress.leftAt = Integer.parseInt(Files.readString(left).trim());
            }
         } catch (IOException | NumberFormatException e) {
            Diorama.LOGGER.warn("Couldn't read when the map left {}", left, e);
         }

         if (!Files.exists(file)) {
            return progress;
         }

         try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
            int magic = in.readInt();
            if (magic == 1146307378 || magic == 1146307377) {
               boolean relight = magic == 1146307377 && night;
               DataInputStream data = new DataInputStream(new InflaterInputStream(in));
               int count = data.readInt();

               for (int r = 0; r < count; r++) {
                  int x = data.readInt();
                  int z = data.readInt();
                  int[] times = new int[1024];

                  for (int i = 0; i < 1024; i++) {
                     times[i] = data.readInt();
                     if (relight && (times[i] > 2 || times[i] == -1 || times[i] == 1)) {
                        times[i] = 2;
                        progress.changed = true;
                     }

                     if (times[i] == 2) {
                        progress.relight.add(ChunkSnapshot.key(x << 5 | i & 31, z << 5 | i >> 5));
                     }
                  }

                  progress.times.put(SavedChunks.regionKey(x, z), times);
               }

               return progress;
            } else {
               return progress;
            }
         } catch (IOException e) {
            Diorama.LOGGER.warn("Couldn't read which saved chunks are drawn from {}; drawing them again", file, e);
            progress.times.clear();
            return progress;
         }
      }

      int time(int regionX, int regionZ, int index) {
         int[] times = this.times.get(SavedChunks.regionKey(regionX, regionZ));
         return times == null ? 0 : times[index];
      }

      boolean isWork(int regionX, int regionZ, int index, int savedTime, boolean onlyRemembered) {
         if (savedTime == 0) {
            return false;
         }

         int time = this.time(regionX, regionZ, index);
         return time != -1 && time != savedTime && (!onlyRemembered || time != -2);
      }

      boolean hasWork(long region, int[] savedTimes, boolean onlyRemembered) {
         int regionX = (int)(region >> 32);
         int regionZ = (int)region;

         for (int i = 0; i < 1024; i++) {
            if (this.isWork(regionX, regionZ, i, savedTimes[i], onlyRemembered)) {
               return true;
            }
         }

         return false;
      }

      boolean back(Map<Long, int[]> regions) {
         if (this.leftAt == 0) {
            return false;
         }

         for (Entry<Long, int[]> region : regions.entrySet()) {
            int[] times = this.times.get(region.getKey());

            for (int i = 0; times != null && i < 1024; i++) {
               if (times[i] == -1 && region.getValue()[i] >= this.leftAt) {
                  times[i] = 1;
               }
            }
         }

         this.leftAt = 0;
         this.leftChecked = true;
         this.changed = true;
         return true;
      }

      void mark(int regionX, int regionZ, int index, int time) {
         int[] times = this.times.computeIfAbsent(SavedChunks.regionKey(regionX, regionZ), k -> new int[1024]);
         if (times[index] != time && times[index] != -1) {
            times[index] = time;
            this.changed = true;
         }
      }

      void saveIfChanged() {
         if (this.changed) {
            try {
               Files.createDirectories(this.file.getParent());
               Path temp = this.file.resolveSibling(this.file.getFileName() + ".tmp");
               DataOutputStream out = new DataOutputStream(Files.newOutputStream(temp));

               try {
                  out.writeInt(1146307378);
                  DeflaterOutputStream deflated = new DeflaterOutputStream(out);
                  DataOutputStream data = new DataOutputStream(deflated);
                  data.writeInt(this.times.size());

                  for (Entry<Long, int[]> e : this.times.entrySet()) {
                     data.writeInt((int)(e.getKey() >> 32));
                     data.writeInt((int)e.getKey().longValue());

                     for (int time : e.getValue()) {
                        data.writeInt(time);
                     }
                  }

                  data.flush();
                  deflated.finish();
               } catch (Throwable var12) {
                  try {
                     out.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }

                  throw var12;
               }

               out.close();
               Files.move(temp, this.file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
               this.changed = false;
               if (this.leftChecked) {
                  Files.deleteIfExists(this.file.resolveSibling("left-at"));
                  this.leftChecked = false;
               }
            } catch (IOException e) {
               Diorama.LOGGER.warn("Couldn't save which saved chunks are drawn to {}", this.file, e);
            }
         }
      }
   }
}
