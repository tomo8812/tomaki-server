package dev.diorama.render;

import dev.diorama.Diorama;
import dev.diorama.Facing;
import dev.diorama.Projection;
import dev.diorama.bake.Mipmaps;
import dev.diorama.bake.TileKey;
import dev.diorama.data.TileLabel;
import dev.diorama.data.TileStore;
import dev.diorama.render.gpu.MapCanvas;
import dev.diorama.render.gpu.MapTexture;
import dev.diorama.render.gpu.Sampling;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public final class MapMemory {
   private static final String NOTE = "redraw-from";
   static final String LEFT = "left-at";
   private static final int EDGE = 512;
   private static final int KNOWN_EDGE = 128;
   private static final int SHRINK = 4;
   private static final int MAX_TILES = 64;
   private static final long IN_USE_MS = 1000L;
   private static final int UPLOADS_PER_FRAME = 4;
   private static final int MAX_LOADING = 8;
   private static final int MAX_KNOWN = 768;
   private static final int MAX_KNOWN_LOADING = 16;
   private static final int FIT_Y = 64;
   private static final ExecutorService LOADER = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "diorama-map-memory");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });
   private final TileStore store;
   private final double sin;
   private final double cos;
   private final Facing facing;
   private final int quarterTurns;
   private final LinkedHashMap<TileKey, MapMemory.Entry> tiles = new LinkedHashMap<>(16, 0.75F, true);
   private final Set<TileKey> loading = new HashSet<>();
   private final ConcurrentLinkedQueue<MapMemory.Loaded> loaded = new ConcurrentLinkedQueue<>();
   private final LinkedHashMap<TileKey, byte[]> knownTiles = new LinkedHashMap<>(16, 0.75F, true);
   private final Set<TileKey> knownLoading = new HashSet<>();
   private final ConcurrentLinkedQueue<MapMemory.Loaded> knownLoaded = new ConcurrentLinkedQueue<>();
   private final Map<Integer, Set<TileKey>> savedTiles = new HashMap<>();
   private final Set<Integer> listing = new HashSet<>();
   private final ConcurrentLinkedQueue<MapMemory.Listed> listed = new ConcurrentLinkedQueue<>();
   private final ConcurrentLinkedQueue<TileKey> made = new ConcurrentLinkedQueue<>();
   private @Nullable TileKey lastKnownKey;
   private byte @Nullable [] lastKnown;
   private long version;
   private long shown;
   private volatile boolean closed;

   private MapMemory(Path folder, TileLabel label) {
      this.store = new TileStore(folder);
      this.store.onOverviewChanged(this.made::add);
      this.sin = Math.sin(Math.toRadians(label.pitch()));
      this.cos = Math.cos(Math.toRadians(label.pitch()));
      this.facing = label.facing();
      this.quarterTurns = Math.floorMod(Projection.facing().turns - label.facing().turns, 4);
   }

   static @Nullable MapMemory open(Path folder, boolean keep) {
      String from = note(folder);
      if (from != null && !Files.isDirectory(folder.resolveSibling(from))) {
         from = null;
      }

      if (from == null && !hasTiles(folder)) {
         from = fullestBeside(folder);
         if (from != null && keep) {
            try {
               Files.createDirectories(folder);
               Files.writeString(folder.resolve("redraw-from"), from, StandardCharsets.UTF_8);
            } catch (IOException e) {
               Diorama.LOGGER.warn("Couldn't note what {} remembers", folder, e);
            }
         }
      }

      TileLabel label = from == null ? null : TileLabel.parse(from);
      return label != null && Files.isDirectory(folder.resolveSibling(from)) ? new MapMemory(folder.resolveSibling(from), label) : null;
   }

   static void noteLeft(Path folder) {
      try {
         if (Files.isDirectory(folder) && !Files.exists(folder.resolve("left-at"))) {
            Files.writeString(folder.resolve("left-at"), Long.toString(System.currentTimeMillis() / 1000L), StandardCharsets.UTF_8);
         }
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't note leaving {}", folder, e);
      }
   }

   private static @Nullable String fullestBeside(Path folder) {
      TileLabel label = TileLabel.parse(folder.getFileName().toString());
      Path parent = folder.getParent();
      if (label != null && parent != null && Files.isDirectory(parent)) {
         String best = null;
         long most = 0L;

         try (Stream<Path> folders = Files.list(parent)) {
            for (Path other : folders.toList()) {
               String name = other.getFileName().toString();
               TileLabel otherLabel = TileLabel.parse(name);
               long count = otherLabel != null && label.canShow(otherLabel) && !other.equals(folder) ? tileCount(other) : 0L;
               if (count > most) {
                  most = count;
                  best = name;
               }
            }
         } catch (IOException | UncheckedIOException e) {
            Diorama.LOGGER.warn("Couldn't look beside {}", folder, e);
         }

         return best;
      } else {
         return null;
      }
   }

   private static @Nullable String note(Path folder) {
      try {
         return Files.isRegularFile(folder.resolve("redraw-from")) ? Files.readString(folder.resolve("redraw-from"), StandardCharsets.UTF_8).trim() : null;
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't read what {} remembers", folder, e);
         return null;
      }
   }

   private static boolean hasTiles(Path folder) {
      if (!Files.isDirectory(folder)) {
         return false;
      }

      try (Stream<Path> files = Files.list(folder)) {
         return files.anyMatch(file -> file.getFileName().toString().endsWith(".omt"));
      } catch (IOException | UncheckedIOException e) {
         return true;
      }
   }

   private static long tileCount(Path folder) {
      if (!Files.isDirectory(folder)) {
         return 0L;
      }

      try (Stream<Path> files = Files.list(folder)) {
         return files.filter(file -> file.getFileName().toString().endsWith(".omt")).count();
      } catch (IOException | UncheckedIOException e) {
         return 0L;
      }
   }

   long version() {
      return this.version;
   }

   public long shownTiles() {
      return this.shown;
   }

   int lod(MapViewport.View view, int lod) {
      double maxU = view.minU() + view.width() / view.zoom();
      double maxV = view.minV() + view.height() / view.zoom();
      double[] a = this.toOld(view.minU(), view.minV());
      double[] b = this.toOld(maxU, maxV);

      while (lod < 11) {
         double units = 128.0 * (1 << lod);
         double tiles = (Math.abs(a[0] - b[0]) / units + 2.0) * (Math.abs(a[1] - b[1]) / units + 2.0);
         if (tiles <= 48.0) {
            break;
         }

         lod++;
      }

      return lod;
   }

   void draw(MapCanvas canvas, MapViewport.View view, int tint, int realLod, Sampling sampling) {
      long now = System.currentTimeMillis();
      this.takeLoaded(now);
      int lod = this.lod(view, realLod);
      double units = 128.0 * (1 << lod);
      double maxU = view.minU() + view.width() / view.zoom();
      double maxV = view.minV() + view.height() / view.zoom();
      double[] a = this.toOld(view.minU(), view.minV());
      double[] b = this.toOld(maxU, maxV);
      int tu0 = (int)Math.floor(Math.min(a[0], b[0]) / units);
      int tu1 = (int)Math.floor(Math.max(a[0], b[0]) / units);
      int tv0 = (int)Math.floor(Math.min(a[1], b[1]) / units);
      int tv1 = (int)Math.floor(Math.max(a[1], b[1]) / units);
      Circle circle = view.round() ? Circle.in(view) : null;

      for (int tu = tu0; tu <= tu1; tu++) {
         for (int tv = tv0; tv <= tv1; tv++) {
            TileKey key = new TileKey(Integer.MIN_VALUE, tu, tv, lod);
            MapMemory.Entry entry = this.tiles.get(key);
            if (entry == null) {
               this.load(key);
            }

            if (entry != null) {
               entry.drawnMs = now;
               if (entry.texture != null) {
                  double[] from = this.fromOld(tu * units, tv * units);
                  double[] to = this.fromOld((tu + 1) * units, (tv + 1) * units);
                  float x0 = view.screenX(Math.min(from[0], to[0]));
                  float x1 = view.screenX(Math.max(from[0], to[0]));
                  float y0 = view.screenY(Math.min(from[1], to[1]));
                  float y1 = view.screenY(Math.max(from[1], to[1]));
                  if (circle == null) {
                     canvas.rect(entry.texture, sampling, x0, y0, x1, y1, 0.0F, 0.0F, 1.0F, 1.0F, tint);
                  } else {
                     Circle.Quads inside = circle.clip(x0, y0, x1, y1, 0.0F, 0.0F, 1.0F, 1.0F);
                     if (inside != null) {
                        canvas.quad(entry.texture, sampling, inside.corners(), inside.uvs(), tint);
                     }
                  }
               }
            }
         }
      }

      this.evict(now);
   }

   float known(int lod, double u, double v) {
      double units = 128.0 * (1 << lod);
      double[] old = this.toOld(u, v);
      int tu = (int)Math.floor(old[0] / units);
      int tv = (int)Math.floor(old[1] / units);
      TileKey key = new TileKey(Integer.MIN_VALUE, tu, tv, lod);
      byte[] known;
      if (key.equals(this.lastKnownKey)) {
         known = this.lastKnown;
      } else {
         Set<TileKey> saved = this.savedTiles.get(lod);
         if (saved == null) {
            return Float.NaN;
         }

         if (!saved.contains(key)) {
            return 0.0F;
         }

         known = this.knownTiles.get(key);
         if (known == null) {
            return Float.NaN;
         }

         this.lastKnownKey = key;
         this.lastKnown = known;
      }

      int x = (int)Math.min(127L, (long)((old[0] - tu * units) / units * 128.0));
      int y = (int)Math.min(127L, (long)((old[1] - tv * units) / units * 128.0));
      return (known[y * 128 + x] & 0xFF) / 255.0F;
   }

   void ask(int lod, double minU, double minV, double maxU, double maxV) {
      Set<TileKey> saved = this.savedTiles.get(lod);
      if (saved == null) {
         if (this.listing.add(lod)) {
            LOADER.execute(() -> this.listed.add(new MapMemory.Listed(lod, this.closed ? Set.of() : this.store.saved(lod))));
         }
      } else {
         double units = 128.0 * (1 << lod);
         double[] a = this.toOld(minU, minV);
         double[] b = this.toOld(maxU, maxV);
         int tu0 = (int)Math.floor(Math.min(a[0], b[0]) / units);
         int tu1 = (int)Math.floor(Math.max(a[0], b[0]) / units);
         int tv0 = (int)Math.floor(Math.min(a[1], b[1]) / units);
         int tv1 = (int)Math.floor(Math.max(a[1], b[1]) / units);
         if ((long)(tu1 - tu0 + 1) * (tv1 - tv0 + 1) <= 384L) {
            double midU = (tu0 + tu1 + 1) * 0.5;
            double midV = (tv0 + tv1 + 1) * 0.5;
            List<TileKey> wanted = new ArrayList<>();

            for (int tu = tu0; tu <= tu1; tu++) {
               for (int tv = tv0; tv <= tv1; tv++) {
                  TileKey key = new TileKey(Integer.MIN_VALUE, tu, tv, lod);
                  if (saved.contains(key) && !this.knownTiles.containsKey(key) && !this.knownLoading.contains(key) && !this.loading.contains(key)) {
                     wanted.add(key);
                  }
               }
            }

            wanted.sort(Comparator.comparingDouble(keyx -> Math.hypot(keyx.tileU() + 0.5 - midU, keyx.tileV() + 0.5 - midV)));

            for (TileKey key : wanted) {
               if (this.knownLoading.size() >= 16) {
                  break;
               }

               this.knownLoading.add(key);
               LOADER.execute(() -> {
                  byte[] known = null;

                  try {
                     int[] pixels = this.closed ? null : this.store.read(key);
                     known = pixels == null ? new byte[16384] : known(pixels);
                  } catch (RuntimeException e) {
                     Diorama.LOGGER.warn("Couldn't load the remembered tile {}", key, e);
                  }

                  this.knownLoaded.add(new MapMemory.Loaded(key, null, known));
               });
            }
         }
      }
   }

   boolean showsChunk(int chunkX, int chunkZ, int top) {
      int x0 = chunkX << 4;
      int z0 = chunkZ << 4;

      for (int[] at : new int[][]{{8, 8}, {0, 0}, {15, 0}, {0, 15}, {15, 15}}) {
         double worldX = x0 + at[0] + 0.5;
         double worldZ = z0 + at[1] + 0.5;
         double u = this.facing.x(worldX, worldZ);
         double v = this.facing.z(worldX, worldZ) * this.sin - top * this.cos;
         TileKey key = new TileKey(Integer.MIN_VALUE, (int)Math.floor(u / 128.0), (int)Math.floor(v / 128.0));
         int[] pixels = this.store.read(key);
         if (pixels != null) {
            int size = 512;
            int x = (int)Math.min(size - 1L, (long)((u - key.minU()) * 4.0));
            int y = (int)Math.min(size - 1L, (long)((v - key.minV()) * 4.0));
            if (pixels[y * size + x] >>> 24 != 0) {
               return true;
            }
         }
      }

      return false;
   }

   private double[] toOld(double u, double v) {
      Facing now = Projection.facing();
      double z = Projection.zAt(v, 64.0);
      double worldX = now.worldX(u, z);
      double worldZ = now.worldZ(u, z);
      return new double[]{this.facing.x(worldX, worldZ), this.facing.z(worldX, worldZ) * this.sin - 64.0 * this.cos};
   }

   private double[] fromOld(double u, double v) {
      double z = (v + 64.0 * this.cos) / this.sin;
      double worldX = this.facing.worldX(u, z);
      double worldZ = this.facing.worldZ(u, z);
      return new double[]{Projection.u(worldX, worldZ), Projection.v(worldX, 64.0, worldZ)};
   }

   private void load(TileKey key) {
      if (this.loading.size() < 8 && this.loading.add(key)) {
         LOADER.execute(() -> {
            int[][] mips = null;
            byte[] known = null;

            try {
               int[] saved = this.closed ? null : this.store.read(key);
               if (saved != null) {
                  known = known(saved);
                  mips = Mipmaps.build(turned(saved, this.quarterTurns), 512);
               }
            } catch (RuntimeException e) {
               Diorama.LOGGER.warn("Couldn't load the remembered tile {}", key, e);
            }

            this.loaded.add(new MapMemory.Loaded(key, mips, known));
         });
      }
   }

   private static byte[] known(int[] tile) {
      byte[] known = new byte[16384];

      for (int y = 0; y < 128; y++) {
         for (int x = 0; x < 128; x++) {
            int a = 0;

            for (int dy = 0; dy < 4; dy++) {
               for (int dx = 0; dx < 4; dx++) {
                  a += tile[(y * 4 + dy) * 512 + x * 4 + dx] >>> 24;
               }
            }

            known[y * 128 + x] = (byte)(a / 16);
         }
      }

      return known;
   }

   private static int[] turned(int[] pixels, int quarterTurns) {
      if (quarterTurns == 0) {
         return (int[])pixels.clone();
      }

      int[] out = new int[pixels.length];
      int last = 511;

      for (int y = 0; y < 512; y++) {
         for (int x = 0; x < 512; x++) {
            int to = switch (quarterTurns) {
               case 1 -> (last - x) * 512 + y;
               case 2 -> (last - y) * 512 + last - x;
               default -> x * 512 + last - y;
            };
            out[to] = pixels[y * 512 + x];
         }
      }

      return out;
   }

   private void takeLoaded(long now) {
      TileKey fresh;
      while ((fresh = this.made.poll()) != null) {
         this.savedTiles.remove(fresh.lod());
         this.listing.remove(fresh.lod());
         MapMemory.Entry missing = this.tiles.get(fresh);
         if (missing != null && missing.texture == null) {
            this.tiles.remove(fresh);
         }

         this.version++;
      }

      MapMemory.Listed list;
      while ((list = this.listed.poll()) != null) {
         this.savedTiles.put(list.lod(), list.keys());
         this.version++;
      }

      MapMemory.Loaded done;
      while ((done = this.knownLoaded.poll()) != null) {
         this.knownLoading.remove(done.key());
         if (done.known() != null) {
            this.keepKnown(done.key(), done.known());
         }
      }

      for (int uploads = 0; uploads < 4 && (done = this.loaded.poll()) != null; this.version++) {
         this.loading.remove(done.key());
         MapTexture texture = null;
         if (done.mips() != null && !this.closed) {
            texture = MapTexture.create("diorama map memory", 512, 6);

            for (int level = 0; level < 6; level++) {
               texture.write(level, 0, 0, 512 >> level, done.mips()[level]);
            }

            this.shown++;
            uploads++;
         }

         if (done.known() != null) {
            this.keepKnown(done.key(), done.known());
         }

         MapMemory.Entry entry = new MapMemory.Entry(texture);
         entry.drawnMs = now;
         this.tiles.put(done.key(), entry);
      }
   }

   private void keepKnown(TileKey key, byte[] known) {
      this.knownTiles.put(key, known);
      this.lastKnownKey = null;
      this.version++;
      Iterator<TileKey> it = this.knownTiles.keySet().iterator();

      while (this.knownTiles.size() > 768 && it.hasNext()) {
         it.next();
         it.remove();
      }
   }

   private void evict(long now) {
      Iterator<Map.Entry<TileKey, MapMemory.Entry>> it = this.tiles.entrySet().iterator();

      while (this.tiles.size() > 64 && it.hasNext()) {
         MapMemory.Entry entry = it.next().getValue();
         if (now - entry.drawnMs >= 1000L) {
            if (entry.texture != null) {
               entry.texture.close();
            }

            it.remove();
            this.version++;
         }
      }
   }

   void close() {
      this.closed = true;
      this.store.close();

      for (MapMemory.Entry entry : this.tiles.values()) {
         if (entry.texture != null) {
            entry.texture.close();
         }
      }

      this.tiles.clear();
   }

   private static final class Entry {
      final @Nullable MapTexture texture;
      long drawnMs;

      Entry(@Nullable MapTexture texture) {
         this.texture = texture;
      }
   }

   private record Listed(int lod, Set<TileKey> keys) {
   }

   private record Loaded(TileKey key, int @Nullable [] @Nullable [] mips, byte @Nullable [] known) {
   }
}
