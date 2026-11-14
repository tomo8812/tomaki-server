package dev.diorama.data;

import dev.diorama.Diorama;
import dev.diorama.bake.BlockLight;
import dev.diorama.bake.Mipmaps;
import dev.diorama.bake.TileChunks;
import dev.diorama.bake.TileImage;
import dev.diorama.bake.TileKey;
import dev.diorama.bake.TileSpots;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.jspecify.annotations.Nullable;

public final class TileStore {
   private static final int MAGIC = 1330467889;
   private static final int SPOTS_MAGIC = 1397773361;
   private static final int CHUNKS_MAGIC = 1128811313;
   private static final int LIGHT_MAGIC = 1279874097;
   private static final int CACHED_TILES = 24;
   private static final long WRITE_DELAY_SECONDS = 3L;
   private static final long RETRY_DELAY_SECONDS = 30L;
   private static final long OVERVIEW_DELAY_SECONDS = 2L;
   private static final int OLD_MAX_LOD = 5;
   private static final ScheduledThreadPoolExecutor WRITER = new ScheduledThreadPoolExecutor(1, task -> {
      Thread thread = new Thread(task, "diorama-tile-writer");
      thread.setDaemon(true);
      thread.setPriority(2);
      return thread;
   });
   private final Path folder;
   private volatile boolean closed;
   private final Object io = new Object();
   private final Map<TileKey, ArrayDeque<TileImage>> handedOver = new HashMap<>();
   private final Set<TileKey> mergeQueued = new HashSet<>();
   private final LinkedHashMap<TileKey, TileStore.Tile> cache = new LinkedHashMap<TileKey, TileStore.Tile>(16, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<TileKey, TileStore.Tile> eldest) {
         return this.size() > 24 && !TileStore.this.unwritten.containsKey(eldest.getKey());
      }
   };
   private final Map<TileKey, TileStore.Tile> unwritten = new HashMap<>();
   private final Set<TileKey> staleOverviews = new HashSet<>();
   private final Set<TileKey> chunksAsked = new HashSet<>();
   private boolean rebuildScheduled;
   private volatile Consumer<TileKey> overviewChanged = key -> {};
   private static final AtomicInteger CHANGED = new AtomicInteger();
   private static final AtomicInteger UNCHANGED = new AtomicInteger();
   private static final int IO_BUFFER = 65536;

   public TileStore(Path folder) {
      this.folder = folder;
      WRITER.execute(() -> this.extendOverviews(5, null));
   }

   public Path folder() {
      return this.folder;
   }

   public void onOverviewChanged(Consumer<TileKey> listener) {
      this.overviewChanged = listener;
   }

   public synchronized int unwrittenTiles() {
      return this.unwritten.size() + this.mergeQueued.size();
   }

   private TileStore.@Nullable Saved shrinkChildren(TileKey key) {
      int size = 512;
      int half = size / 2;
      int[] out = null;
      byte[][] childLight = new byte[4][];
      int[][] childPixels = new int[4][];

      for (int dy = 0; dy < 2; dy++) {
         for (int dx = 0; dx < 2; dx++) {
            TileKey child = new TileKey(key.layer(), key.tileU() * 2 + dx, key.tileV() * 2 + dy, key.lod() - 1);
            TileStore.Saved saved = this.readSaved(child);
            if (saved != null) {
               int[] c = saved.pixels();
               childPixels[dy * 2 + dx] = c;
               childLight[dy * 2 + dx] = saved.blockLight();
               if (out == null) {
                  out = new int[size * size];
               }

               for (int y = 0; y < half; y++) {
                  int row = 2 * y * size;

                  for (int x = 0; x < half; x++) {
                     int i = row + 2 * x;
                     out[(dy * half + y) * size + dx * half + x] = Mipmaps.average(c[i], c[i + 1], c[i + size], c[i + size + 1]);
                  }
               }
            }
         }
      }

      return out == null ? null : new TileStore.Saved(out, BlockLight.overview(childLight, childPixels), null);
   }

   private synchronized void overviewsStale(TileKey key) {
      if (key.lod() < 11 && !key.isCave()) {
         this.staleOverviews.add(key.parent());
         if (!this.rebuildScheduled && !this.closed) {
            this.rebuildScheduled = true;
            WRITER.schedule(this::rebuildOverviews, 2L, TimeUnit.SECONDS);
         }
      }
   }

   private void rebuildOverviews() {
      TreeMap<Integer, Set<TileKey>> levels = new TreeMap<>();
      synchronized (this) {
         for (TileKey key : this.staleOverviews) {
            levels.computeIfAbsent(key.lod(), lod -> new HashSet<>()).add(key);
         }

         this.staleOverviews.clear();
         this.rebuildScheduled = false;
      }

      while (!levels.isEmpty()) {
         Set<TileKey> level = levels.pollFirstEntry().getValue();
         Set<TileKey> above = new HashSet<>();

         for (TileKey key : level) {
            if (this.rebuild(key, true) && key.lod() < 11) {
               above.add(key.parent());
            }
         }

         if (!above.isEmpty()) {
            levels.computeIfAbsent(above.iterator().next().lod(), lod -> new HashSet<>()).addAll(above);
         }
      }
   }

   private boolean rebuild(TileKey key, boolean repair) {
      TileStore.Saved shrunk = this.shrinkChildren(key);
      if (shrunk == null) {
         return false;
      }

      TileStore.Tile tile = new TileStore.Tile(shrunk.pixels(), null, this.shrinkChunks(key, repair), shrunk.blockLight());
      synchronized (this.io) {
         synchronized (this) {
            this.cache.put(key, tile);
            this.unwritten.put(key, tile);
         }
      }

      boolean saved = this.store(key, tile);
      synchronized (this) {
         if (this.unwritten.get(key) == tile) {
            if (saved) {
               this.unwritten.remove(key);
            } else if (!this.closed) {
               WRITER.schedule(() -> this.write(key), 30L, TimeUnit.SECONDS);
            }
         }
      }

      this.overviewChanged.accept(key);
      synchronized (this) {
         this.trimCache();
         return true;
      }
   }

   private void extendOverviews(int lod, @Nullable ArrayDeque<TileKey> stale) {
      while (lod < 11 && !this.closed) {
         if (stale == null) {
            stale = this.staleAbove(lod);
         }

         TileKey key = stale.poll();
         if (key != null) {
            this.rebuild(key, false);
            int level = lod;
            ArrayDeque<TileKey> rest = stale;
            WRITER.execute(() -> this.extendOverviews(level, rest));
            return;
         }

         lod++;
         stale = null;
      }
   }

   private ArrayDeque<TileKey> staleAbove(int lod) {
      Set<TileKey> stale = new HashSet<>();

      for (TileKey key : this.saved(lod)) {
         Path child = this.file(key);
         Path parent = this.file(key.parent());

         try {
            if (!Files.isRegularFile(parent) || Files.isRegularFile(child) && Files.getLastModifiedTime(parent).compareTo(Files.getLastModifiedTime(child)) < 0
               )
             {
               stale.add(key.parent());
            }
         } catch (IOException var8) {
         }
      }

      return new ArrayDeque<>(stale);
   }

   public int @Nullable [] read(TileKey key) {
      synchronized (this.io) {
         this.merge(key);
         TileStore.Tile tile = this.saved(key);
         return tile == null ? null : tile.pixels;
      }
   }

   public TileStore.@Nullable Saved readSaved(TileKey key) {
      synchronized (this.io) {
         this.merge(key);
         TileStore.Tile tile = this.saved(key);
         return tile == null ? null : new TileStore.Saved(tile.pixels, tile.blockLight, tile.spots);
      }
   }

   private byte @Nullable [] shrinkChunks(TileKey key, boolean repair) {
      byte[][] children = new byte[4][];
      boolean complete = true;
      boolean anyTile = false;

      for (int dy = 0; dy < 2; dy++) {
         for (int dx = 0; dx < 2; dx++) {
            TileKey child = new TileKey(key.layer(), key.tileU() * 2 + dx, key.tileV() * 2 + dy, key.lod() - 1);
            TileStore.Tile tile;
            synchronized (this.io) {
               this.merge(child);
               tile = this.saved(child);
            }

            if (tile != null) {
               anyTile = true;
               if (child.lod() == 0) {
                  children[dy * 2 + dx] = TileChunks.fromSpots(child, tile.spots);
               } else if (tile.chunks == null) {
                  complete = false;
                  if (repair) {
                     this.askChunks(child);
                  }
               } else {
                  children[dy * 2 + dx] = tile.chunks;
               }
            }
         }
      }

      if (!complete) {
         return null;
      }

      byte[] chunks = TileChunks.shrink(key, children);
      if (chunks == null && anyTile) {
         chunks = new byte[262144];
         Arrays.fill(chunks, (byte)-128);
      }

      return chunks;
   }

   public byte @Nullable [] chunks(TileKey key) {
      TileStore.Tile tile;
      synchronized (this.io) {
         this.merge(key);
         tile = this.saved(key);
      }

      if (tile == null) {
         return null;
      }

      if (key.lod() == 0) {
         return TileChunks.fromSpots(key, tile.spots);
      }

      if (tile.chunks == null) {
         this.askChunks(key);
      }

      return tile.chunks;
   }

   private void askChunks(TileKey key) {
      synchronized (this) {
         if (this.closed || !this.chunksAsked.add(key)) {
            return;
         }
      }

      int shift = key.lod() - 1;
      Set<TileKey> under = new HashSet<>();

      for (TileKey one : this.saved(1)) {
         if (one.layer() == key.layer() && one.tileU() >> shift == key.tileU() && one.tileV() >> shift == key.tileV()) {
            under.add(one);
         }
      }

      synchronized (this) {
         if (!this.closed && !under.isEmpty()) {
            this.staleOverviews.addAll(under);
            if (!this.rebuildScheduled) {
               this.rebuildScheduled = true;
               WRITER.schedule(this::rebuildOverviews, 2L, TimeUnit.SECONDS);
            }
         }
      }
   }

   public int @Nullable [] spots(TileKey key) {
      synchronized (this.io) {
         this.merge(key);
         TileStore.Tile tile = this.saved(key);
         return tile == null ? null : tile.spots;
      }
   }

   private TileStore.@Nullable Tile saved(TileKey key) {
      synchronized (this) {
         TileStore.Tile pending = this.unwritten.get(key);
         if (pending != null) {
            return pending;
         }

         if (this.cache.containsKey(key)) {
            return this.cache.get(key);
         }
      }

      TileStore.Tile tile = this.load(key);
      synchronized (this) {
         this.cache.put(key, tile);
         return tile;
      }
   }

   public void baked(TileImage image) {
      TileKey key = image.key();
      synchronized (this) {
         this.handedOver.computeIfAbsent(key, k -> new ArrayDeque<>()).add(image);
         if (!this.mergeQueued.add(key)) {
            return;
         }
      }

      WRITER.execute(() -> {
         synchronized (this) {
            this.mergeQueued.remove(key);
         }

         this.merge(key);
      });
   }

   private void merge(TileKey key) {
      synchronized (this.io) {
         List<TileImage> images;
         synchronized (this) {
            ArrayDeque<TileImage> queue = this.handedOver.get(key);
            if (queue == null) {
               return;
            }

            images = List.copyOf(queue);
         }

         int size = 512;
         TileStore.Tile saved = this.saved(key);
         boolean changed = false;

         for (TileImage image : images) {
            int[] pixels;
            byte[] light;
            if (image.isFull()) {
               pixels = image.mips()[0];
               light = image.blockLight();
            } else {
               pixels = saved == null ? new int[size * size] : (int[])saved.pixels.clone();
               light = saved != null && saved.blockLight != null ? (byte[])saved.blockLight.clone() : null;

               for (TileImage.Patch patch : image.patches()) {
                  int[] block = patch.mips()[0];
                  int edge = 32;

                  for (int y = 0; y < edge; y++) {
                     System.arraycopy(block, y * edge, pixels, (patch.blockY() * edge + y) * size + patch.blockX() * edge, edge);
                  }

                  byte[] patchLight = patch.blockLight();
                  if (patchLight != null) {
                     if (light == null) {
                        light = new byte[16384];
                     }

                     int lightEdge = edge / 4;

                     for (int y = 0; y < lightEdge; y++) {
                        System.arraycopy(patchLight, y * lightEdge, light, (patch.blockY() * lightEdge + y) * 128 + patch.blockX() * lightEdge, lightEdge);
                     }
                  }
               }
            }

            TileStore.Tile merged = new TileStore.Tile(pixels, mergeSpots(saved == null ? null : saved.spots, image), null, light);
            if (saved != null ? !saved.same(merged) : !nothingKnown(pixels)) {
               count(false);
               saved = merged;
               changed = true;
            } else {
               count(true);
            }
         }

         synchronized (this) {
            ArrayDeque<TileImage> queue = this.handedOver.get(key);

            for (int i = 0; i < images.size(); i++) {
               queue.poll();
            }

            if (queue.isEmpty()) {
               this.handedOver.remove(key);
            }

            if (changed) {
               this.cache.put(key, saved);
               boolean scheduled = this.unwritten.containsKey(key);
               this.unwritten.put(key, saved);
               if (!scheduled) {
                  WRITER.schedule(() -> this.write(key), 3L, TimeUnit.SECONDS);
               }
            }
         }
      }
   }

   private static int @Nullable [] mergeSpots(int @Nullable [] saved, TileImage image) {
      int[] fresh = image.spots();
      if (fresh == null) {
         return saved;
      }

      if (image.isFull()) {
         return fresh;
      }

      int[] spots = saved == null ? new int[fresh.length] : (int[])saved.clone();

      for (TileImage.Patch patch : image.patches()) {
         for (int row = patch.blockY() * 32; row < (patch.blockY() + 1) * 32; row++) {
            int i = row * 128 + TileSpots.patchColumn(patch);
            System.arraycopy(fresh, i, spots, i, 8);
         }
      }

      return spots;
   }

   public void forgetHighOverviews(boolean chunks) throws InterruptedException {
      if (!chunks) {
         for (TileKey key : this.saved(5)) {
            this.forgetChunks(key);
         }
      }

      Future<?> done = WRITER.submit(() -> {
         for (int lod = 6; lod <= 11; lod++) {
            for (TileKey key : this.saved(lod)) {
               synchronized (this.io) {
                  synchronized (this) {
                     this.cache.remove(key);
                     this.unwritten.remove(key);
                  }

                  try {
                     Files.deleteIfExists(this.file(key));
                  } catch (IOException e) {
                     throw new UncheckedIOException(ex);
                  }
               }
            }
         }

         if (!this.saved(6).isEmpty()) {
            throw new IllegalStateException("Overview tiles above level 5 are still there");
         }
      });

      try {
         done.get();
      } catch (ExecutionException e) {
         throw new IllegalStateException(e.getCause());
      }
   }

   public void forgetChunks(TileKey key) {
      TileStore.Tile old;
      synchronized (this.io) {
         this.merge(key);
         TileStore.Tile tile = this.saved(key);
         if (tile == null || key.lod() == 0) {
            return;
         }

         old = new TileStore.Tile(tile.pixels, null, null, tile.blockLight);
         synchronized (this) {
            this.cache.put(key, old);
            this.chunksAsked.remove(key);
         }
      }

      this.store(key, old);
   }

   public void forgetBlockLight(TileKey key) {
      TileStore.Tile old;
      synchronized (this.io) {
         this.merge(key);
         TileStore.Tile tile = this.saved(key);
         if (tile == null || key.lod() != 0) {
            return;
         }

         old = new TileStore.Tile(tile.pixels, tile.spots, null, null);
         synchronized (this) {
            this.cache.put(key, old);
            this.unwritten.replace(key, old);
         }
      }

      this.store(key, old);
   }

   public void forgetSpots(TileKey key) {
      TileStore.Tile old;
      synchronized (this.io) {
         this.merge(key);
         TileStore.Tile tile = this.saved(key);
         if (tile == null || key.lod() != 0) {
            return;
         }

         old = new TileStore.Tile(tile.pixels, null, null, tile.blockLight);
         synchronized (this) {
            this.cache.put(key, old);
            this.unwritten.replace(key, old);
         }
      }

      this.store(key, old);
   }

   public void catchUp() throws InterruptedException {
      Future<?> done = WRITER.submit(() -> {
         Set<TileKey> handed;
         synchronized (this) {
            handed = new HashSet<>(this.handedOver.keySet());
         }

         for (TileKey key : handed) {
            this.merge(key);
         }

         synchronized (this) {
            for (TileKey key : this.unwritten.keySet()) {
               if (key.lod() < 11) {
                  this.staleOverviews.add(key.parent());
               }
            }
         }

         this.findStaleOverviews();
         this.rebuildOverviews();
      });

      try {
         done.get();
      } catch (ExecutionException e) {
         Diorama.LOGGER.error("Couldn't bring the saved map up to date", e);
      }
   }

   private void findStaleOverviews() {
      for (int lod = 0; lod < 11; lod++) {
         for (TileKey key : this.saved(lod)) {
            Path child = this.file(key);
            Path parent = this.file(key.parent());

            try {
               if (Files.isRegularFile(child)
                  && (!Files.isRegularFile(parent) || Files.getLastModifiedTime(parent).compareTo(Files.getLastModifiedTime(child)) < 0)) {
                  synchronized (this) {
                     this.staleOverviews.add(key.parent());
                  }
               }
            } catch (IOException var9) {
            }
         }
      }
   }

   public Set<TileKey> saved(int lod) {
      Set<TileKey> keys = new HashSet<>();
      Path dir = lod == 0 ? this.folder : this.folder.resolve("lod" + lod);
      if (Files.isDirectory(dir)) {
         try (Stream<Path> files = Files.list(dir)) {
            files.forEach(file -> {
               String name = file.getFileName().toString();
               int split = name.indexOf(95);
               if (name.endsWith(".omt") && split >= 0) {
                  try {
                     int u = Integer.parseInt(name.substring(0, split));
                     int v = Integer.parseInt(name.substring(split + 1, name.length() - 4));
                     keys.add(new TileKey(Integer.MIN_VALUE, u, v, lod));
                  } catch (NumberFormatException var7) {
                  }
               }
            });
         } catch (IOException e) {
            Diorama.LOGGER.warn("Couldn't list saved tiles in {}", dir, e);
         }
      }

      synchronized (this) {
         for (TileKey key : this.unwritten.keySet()) {
            if (key.lod() == lod) {
               keys.add(key);
            }
         }

         return keys;
      }
   }

   public Map<TileKey, Long> savedTimes(int lod) {
      Map<TileKey, Long> times = new HashMap<>();
      Path dir = lod == 0 ? this.folder : this.folder.resolve("lod" + lod);
      if (Files.isDirectory(dir)) {
         try (Stream<Path> files = Files.find(dir, 1, (file, attributes) -> {
               String name = file.getFileName().toString();
               int split = name.indexOf(95);
               if (attributes.isRegularFile() && name.endsWith(".omt") && split >= 0) {
                  try {
                     int u = Integer.parseInt(name.substring(0, split));
                     int v = Integer.parseInt(name.substring(split + 1, name.length() - 4));
                     times.put(new TileKey(Integer.MIN_VALUE, u, v, lod), attributes.lastModifiedTime().toMillis());
                  } catch (NumberFormatException var8x) {
                  }

                  return false;
               } else {
                  return false;
               }
            })) {
            files.forEach(file -> {});
         } catch (IOException | UncheckedIOException e) {
            Diorama.LOGGER.warn("Couldn't list saved tiles in {}", dir, e);
         }
      }

      long now = System.currentTimeMillis();
      synchronized (this) {
         for (TileKey key : this.unwritten.keySet()) {
            if (key.lod() == lod) {
               times.put(key, now);
            }
         }

         return times;
      }
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         Future<?> flush = WRITER.submit(() -> {
            Set<TileKey> handed;
            synchronized (this) {
               handed = new HashSet<>(this.handedOver.keySet());
            }

            for (TileKey key : handed) {
               this.merge(key);
            }

            Map<TileKey, TileStore.Tile> pending;
            synchronized (this) {
               pending = new HashMap<>(this.unwritten);
            }

            for (Entry<TileKey, TileStore.Tile> entry : pending.entrySet()) {
               if (this.store(entry.getKey(), entry.getValue()) && entry.getKey().lod() == 0 && !entry.getKey().isCave()) {
                  synchronized (this) {
                     this.staleOverviews.add(entry.getKey().parent());
                  }
               }
            }

            this.rebuildOverviews();
            synchronized (this) {
               this.unwritten.clear();
               this.cache.clear();
            }
         });

         try {
            flush.get();
         } catch (ExecutionException e) {
            Diorama.LOGGER.error("Couldn't save map tiles", e);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }
   }

   private void write(TileKey key) {
      if (!this.closed) {
         TileStore.Tile tile;
         synchronized (this) {
            tile = this.unwritten.get(key);
         }

         if (tile != null) {
            boolean saved = this.store(key, tile);
            if (saved && key.lod() == 0) {
               this.overviewsStale(key);
            }

            synchronized (this) {
               if (this.unwritten.get(key) != tile) {
                  WRITER.schedule(() -> this.write(key), 3L, TimeUnit.SECONDS);
               } else if (saved) {
                  this.unwritten.remove(key);
               } else {
                  WRITER.schedule(() -> this.write(key), 30L, TimeUnit.SECONDS);
               }

               this.trimCache();
            }
         }
      }
   }

   private static void count(boolean unchanged) {
      if (Diorama.DEBUG) {
         int changed = unchanged ? CHANGED.get() : CHANGED.incrementAndGet();
         int same = unchanged ? UNCHANGED.incrementAndGet() : UNCHANGED.get();
         if ((changed + same) % 100 == 0) {
            Diorama.LOGGER.info("Bakes saved: {} changed, {} unchanged and skipped", changed, same);
         }
      }
   }

   private static boolean nothingKnown(int[] pixels) {
      for (int pixel : pixels) {
         if (pixel >>> 24 != 0) {
            return false;
         }
      }

      return true;
   }

   private void trimCache() {
      Iterator<TileKey> it = this.cache.keySet().iterator();

      while (this.cache.size() > 24 && it.hasNext()) {
         if (!this.unwritten.containsKey(it.next())) {
            it.remove();
         }
      }
   }

   private Path file(TileKey key) {
      if (key.isCave()) {
         return this.folder.resolve(Integer.toString(key.layer())).resolve(key.tileU() + "_" + key.tileV() + ".omt");
      }

      Path dir = key.lod() == 0 ? this.folder : this.folder.resolve("lod" + key.lod());
      return dir.resolve(key.tileU() + "_" + key.tileV() + ".omt");
   }

   private TileStore.@Nullable Tile load(TileKey key) {
      Path file = this.file(key);
      if (!Files.isRegularFile(file)) {
         return null;
      }

      try (
         InputStream in = Files.newInputStream(file);
         DataInputStream data = new DataInputStream(in);
      ) {
         if (data.readInt() != 1330467889 || data.readInt() != 512) {
            return null;
         }

         int size = 512;
         InflaterInputStream inflated = new InflaterInputStream(in);
         ByteBuffer buffer = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);
         int[] pixels = new int[size * size];
         if (!readInts(inflated, buffer, pixels)) {
            return null;
         }

         int name = readInt(inflated);
         byte[] chunks = null;
         int[] spots = null;
         if (key.lod() > 0 && name == 1128811313) {
            chunks = new byte[size * size];
            if (inflated.readNBytes(chunks, 0, chunks.length) != chunks.length) {
               chunks = null;
            }

            name = chunks == null ? 0 : readInt(inflated);
         } else if (key.lod() == 0 && name == 1397773361) {
            spots = new int[65536];
            if (!readInts(inflated, buffer, spots)) {
               spots = null;
            }

            name = spots == null ? 0 : readInt(inflated);
         }

         byte[] light = null;
         if (name == 1279874097) {
            light = new byte[16384];
            if (inflated.readNBytes(light, 0, light.length) != light.length) {
               light = null;
            }
         }

         inflated.transferTo(OutputStream.nullOutputStream());
         return new TileStore.Tile(pixels, spots, chunks, light);
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't read saved tile {}", file, e);
         return null;
      }
   }

   private static boolean readInts(InputStream in, ByteBuffer buffer, int[] out) throws IOException {
      int perRead = buffer.capacity() / 4;

      for (int at = 0; at < out.length; at += perRead) {
         int n = Math.min(perRead, out.length - at);
         if (in.readNBytes(buffer.array(), 0, n * 4) != n * 4) {
            return false;
         }

         buffer.asIntBuffer().get(out, at, n);
      }

      return true;
   }

   private static int readInt(InputStream in) throws IOException {
      byte[] bytes = in.readNBytes(4);
      return bytes.length == 4 ? ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt() : 0;
   }

   private static void writeInts(OutputStream out, ByteBuffer buffer, int[] ints) throws IOException {
      int perWrite = buffer.capacity() / 4;

      for (int at = 0; at < ints.length; at += perWrite) {
         int n = Math.min(perWrite, ints.length - at);
         buffer.clear();
         buffer.asIntBuffer().put(ints, at, n);
         out.write(buffer.array(), 0, n * 4);
      }
   }

   private boolean store(TileKey key, TileStore.Tile tile) {
      Path file = this.file(key);
      int[] pixels = tile.pixels;
      int[] spots = tile.spots;
      byte[] chunks = tile.chunks;
      byte[] light = tile.blockLight;

      try {
         Files.createDirectories(file.getParent());
         Path temp = file.resolveSibling(file.getFileName() + ".tmp");
         ByteBuffer buffer = ByteBuffer.allocate(65536).order(ByteOrder.LITTLE_ENDIAN);

         try (DataOutputStream data = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(temp), 65536))) {
            data.writeInt(1330467889);
            data.writeInt(512);

            try (OutputStream deflate = new DeflaterOutputStream(data)) {
               writeInts(deflate, buffer, pixels);
               if (spots != null) {
                  writeInts(deflate, buffer, new int[]{1397773361});
                  writeInts(deflate, buffer, spots);
               } else if (chunks != null) {
                  writeInts(deflate, buffer, new int[]{1128811313});
                  deflate.write(chunks);
               }

               if (light != null) {
                  writeInts(deflate, buffer, new int[]{1279874097});
                  deflate.write(light);
               }
            }
         }

         Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         return true;
      } catch (IOException | RuntimeException e) {
         Diorama.LOGGER.error("Couldn't save tile {}", file, e);
         return false;
      }
   }

   public record Saved(int[] pixels, byte @Nullable [] blockLight, int @Nullable [] spots) {
   }

   private record Tile(int[] pixels, int @Nullable [] spots, byte @Nullable [] chunks, byte @Nullable [] blockLight) {
      boolean same(TileStore.Tile other) {
         return Arrays.equals(this.pixels, other.pixels)
            && Arrays.equals(this.spots, other.spots)
            && Arrays.equals(this.chunks, other.chunks)
            && Arrays.equals(this.blockLight, other.blockLight);
      }
   }
}
