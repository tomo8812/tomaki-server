package dev.diorama.render;

import dev.diorama.Facing;
import dev.diorama.MapStyle;
import dev.diorama.Projection;
import dev.diorama.bake.ChunkSource;
import dev.diorama.bake.Looks;
import dev.diorama.bake.Mipmaps;
import dev.diorama.bake.TileBaker;
import dev.diorama.bake.TileKey;
import dev.diorama.data.ChunkSnapshot;
import dev.diorama.data.ImageExport;
import dev.diorama.data.SeenBlocks;
import dev.diorama.data.TileStore;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.DoubleConsumer;
import org.jspecify.annotations.Nullable;

public final class CaveSliceTiles implements ImageExport.Tiles {
   private static final int[] EMPTY = new int[0];
   private final ChunkSource source;
   private final int layer;
   private final Looks looks;
   private final boolean fogOfWar;
   private final Facing facing = Projection.facing();
   private final MapStyle style = MapStyle.get();
   private final double @Nullable [] bounds;
   private final Set<Long> withChunks = new HashSet<>();
   private final ConcurrentHashMap<Long, SoftReference<int[]>> drawn = new ConcurrentHashMap<>();

   public CaveSliceTiles(MapLevel level, int layer, Looks looks) {
      this.layer = layer;
      this.looks = looks;
      this.fogOfWar = level.fogOfWar();
      this.bounds = level.sliceBounds(layer);
      final Map<Long, ChunkSnapshot> chunks = level.chunksNow();

      for (ChunkSnapshot chunk : chunks.values()) {
         double[] box = MapLevel.sliceBox(chunk, layer);

         for (int tv = (int)Math.floor((box[1] - 1.0) / 128.0); tv <= (int)Math.floor((box[3] + 1.0) / 128.0); tv++) {
            for (int tu = (int)Math.floor((box[0] - 1.0) / 128.0); tu <= (int)Math.floor((box[2] + 1.0) / 128.0); tu++) {
               this.withChunks.add(tileKey(tu, tv));
            }
         }
      }

      final Facing facing = this.facing;
      final int minY = level.minY();
      final int maxY = level.maxY();
      final int topY = level.topY();
      final int roof = level.roof();
      final boolean depthCaves = level.depthCaves();
      final SeenBlocks seen = level.seen();
      this.source = new ChunkSource() {
         @Override
         public @Nullable ChunkSnapshot chunk(int chunkX, int chunkZ) {
            ChunkSnapshot chunk = chunks.get(ChunkSnapshot.key(facing.worldBlockX(chunkX, chunkZ), facing.worldBlockZ(chunkX, chunkZ)));
            return chunk == null ? null : chunk.turned(facing);
         }

         @Override
         public int minY() {
            return minY;
         }

         @Override
         public int maxY() {
            return maxY;
         }

         @Override
         public int topY() {
            return topY;
         }

         @Override
         public int roof() {
            return roof;
         }

         @Override
         public boolean depthCaves() {
            return depthCaves;
         }

         @Override
         public boolean isSeen(int x, int y, int z) {
            return seen.isSeen(facing.worldBlockX(x, z), y, facing.worldBlockZ(x, z));
         }
      };
   }

   @Override
   public TileStore.@Nullable Saved tile(int tileU, int tileV, int lod) {
      int[] pixels = this.pixels(tileU, tileV, lod);
      return pixels == null ? null : new TileStore.Saved(pixels, null, null);
   }

   private int @Nullable [] pixels(int tileU, int tileV, int lod) {
      if (!this.inBounds(tileU, tileV, lod)) {
         return null;
      }

      if (lod > 0) {
         return this.shrunk(tileU, tileV, lod);
      }

      long key = tileKey(tileU, tileV);
      SoftReference<int[]> held = this.drawn.get(key);
      int[] pixels = held == null ? null : held.get();
      if (pixels == null) {
         pixels = TileBaker.reference(new TileKey(this.layer, tileU, tileV), this.source, this.looks, this.fogOfWar, null, null);
         if (Projection.facing() != this.facing || !MapStyle.get().equals(this.style)) {
            throw new IllegalStateException("the map was turned or its look changed while the image was being saved");
         }

         boolean empty = true;

         for (int i = 0; i < pixels.length && empty; i++) {
            empty = pixels[i] >>> 24 == 0;
         }

         if (empty) {
            pixels = EMPTY;
         }

         this.drawn.put(key, new SoftReference<>(pixels));
      }

      return pixels == EMPTY ? null : pixels;
   }

   private int @Nullable [] shrunk(int tileU, int tileV, int lod) {
      int size = 512;
      int half = size / 2;
      int[] out = null;

      for (int dy = 0; dy < 2; dy++) {
         for (int dx = 0; dx < 2; dx++) {
            int[] c = this.pixels(tileU * 2 + dx, tileV * 2 + dy, lod - 1);
            if (c != null) {
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

      return out;
   }

   private static long tileKey(int tileU, int tileV) {
      return (long)tileU << 32 | tileV & 4294967295L;
   }

   private boolean inBounds(int tileU, int tileV, int lod) {
      if (this.bounds == null) {
         return false;
      }

      if (lod == 0) {
         return this.withChunks.contains(tileKey(tileU, tileV));
      }

      double units = 128.0 * (1 << lod);
      return (tileU + 1) * units > this.bounds[0] && tileU * units < this.bounds[2] && (tileV + 1) * units > this.bounds[1] && tileV * units < this.bounds[3];
   }

   @Override
   public void prepare(ImageExport.Area area, DoubleConsumer progress) {
      double[] units = area.units();
      double margin = ExportFog.margin(area.lod());
      int size = 128;
      List<int[]> tiles = new ArrayList<>();

      for (int tv = (int)Math.floor((units[1] - margin) / size); tv <= (int)Math.floor((units[3] + margin) / size); tv++) {
         for (int tu = (int)Math.floor((units[0] - margin) / size); tu <= (int)Math.floor((units[2] + margin) / size); tu++) {
            if (this.inBounds(tu, tv, 0)) {
               tiles.add(new int[]{tu, tv});
            }
         }
      }

      if (!tiles.isEmpty()) {
         int threads = Math.max(1, Math.min(tiles.size(), Runtime.getRuntime().availableProcessors() / 2));
         ExecutorService pool = Executors.newFixedThreadPool(threads, task -> {
            Thread thread = new Thread(task, "diorama-export-cave");
            thread.setDaemon(true);
            thread.setPriority(2);
            return thread;
         });

         try {
            List<Future<?>> waiting = new ArrayList<>();

            for (int[] tile : tiles) {
               Runnable draw = () -> this.pixels(tile[0], tile[1], 0);
               waiting.add(pool.submit(draw));
            }

            for (int i = 0; i < waiting.size(); i++) {
               waiting.get(i).get();
               progress.accept((i + 1.0) / waiting.size());
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while drawing the cave", e);
         } catch (ExecutionException e) {
            throw e.getCause() instanceof RuntimeException failed ? failed : new IllegalStateException(e.getCause());
         } finally {
            pool.shutdownNow();
         }
      }
   }

   @Override
   public double prepareShare() {
      return 0.9;
   }
}
