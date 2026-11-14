package dev.diorama.web;

import dev.diorama.Diorama;
import dev.diorama.Projection;
import dev.diorama.bake.ChunkSource;
import dev.diorama.bake.JarModels;
import dev.diorama.bake.TableLooks;
import dev.diorama.bake.TileBaker;
import dev.diorama.bake.TileKey;
import dev.diorama.bake.VanillaTable;
import dev.diorama.data.ChunkSnapshot;
import dev.diorama.data.ChunkTags;
import dev.diorama.data.SavedChunkSource;
import dev.diorama.share.ServerLocks;
import dev.diorama.share.ShareServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.storage.LevelResource;
import org.jspecify.annotations.Nullable;

public final class WebMap {
   private static final long CHECK_MS = 30000L;
   private static final long PASS_GAP_MS = 300000L;
   private static final long SERVER_BEHIND_NANOS = 40000000L;
   private static final int BLEND_RADIUS = 2;
   private static final int MAX_ZOOM = 10;
   private static final Pattern TILE_NAME = Pattern.compile("-?[0-9]+_-?[0-9]+[.]jpg");
   private static @Nullable WebMap running;
   private final MinecraftServer server;
   private final WebSettings settings;
   private final VanillaTable table;
   private final Path modsFolder;
   final Path folder;
   private final Map<String, WebMap.Dimension> dimensions = new ConcurrentHashMap<>();
   private final Set<String> closed = ConcurrentHashMap.newKeySet();
   private final Set<String> warnedKept = new HashSet<>();
   private final Object wake = new Object();
   private final Thread thread;
   private volatile @Nullable WebServer http;
   private volatile boolean stopping;
   private final AtomicBoolean redrawAll = new AtomicBoolean();
   private boolean redrawOwed;
   private volatile String status = "starting";
   private volatile int passes;

   private WebMap(MinecraftServer server, WebSettings settings, VanillaTable table, Path modsFolder) {
      this.server = server;
      this.settings = settings;
      this.table = table;
      this.modsFolder = modsFolder;
      this.folder = settings.folder() != null ? settings.folder() : server.getWorldPath(LevelResource.ROOT).resolve("diorama-web");
      this.thread = new Thread(this::run, "Diorama web map");
      this.thread.setDaemon(true);
      this.thread.setPriority(1);
   }

   public static synchronized void started(MinecraftServer server, Path configFolder, Path modsFolder) {
      if (server.isDedicatedServer()) {
         WebSettings settings = WebSettings.load(configFolder);
         if (!settings.enabled()) {
            Diorama.LOGGER.info("Diorama's web map is off (web-map in config/{})", "diorama-server.properties");
         } else {
            VanillaTable table = VanillaTable.shipped();
            if (table == null) {
               Diorama.LOGGER.warn("Diorama's web map can't start: this jar has no vanilla colour table");
            } else {
               WebMap map = new WebMap(server, settings, table, modsFolder);
               if (map.start()) {
                  running = map;
               }
            }
         }
      }
   }

   public static synchronized void stopped() {
      WebMap map = running;
      running = null;
      if (map != null) {
         map.stop();
      }
   }

   public static @Nullable WebMap get() {
      return running;
   }

   private boolean start() {
      try {
         Files.createDirectories(this.folder);

         try (InputStream page = WebMap.class.getResourceAsStream("/diorama/web/index.html")) {
            if (page != null) {
               Files.copy(page, this.folder.resolve("index.html"), StandardCopyOption.REPLACE_EXISTING);
            }
         }
      } catch (IOException e) {
         Diorama.LOGGER.warn("Diorama's web map can't write to {}, so it doesn't start", this.folder, e);
         return false;
      }

      this.thread.start();
      Diorama.LOGGER.info("Diorama's web map is drawing into {}", this.folder);
      return true;
   }

   private void stop() {
      this.stopping = true;
      synchronized (this.wake) {
         this.wake.notifyAll();
      }

      try {
         this.thread.join(10000L);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }

      WebServer http = this.http;
      if (http != null) {
         http.stop();
      }
   }

   public void redrawAll() {
      this.redrawAll.set(true);
      synchronized (this.wake) {
         this.wake.notifyAll();
      }
   }

   public int passes() {
      return this.passes;
   }

   public String status() {
      return this.status;
   }

   private boolean leftOut(ServerLevel level) {
      ServerLocks locks = ShareServer.locks(this.server);
      return locks.mapOffDimensions().contains(level.dimension().location().toString())
         || level.dimensionType().hasCeiling() && (!locks.caveMapsAllowed() || locks.caveFogOfWar());
   }

   private void remove(String id, Properties progress) {
      this.dimensions.remove(id);
      if (progress.remove(id) != null) {
         this.saveProgress(progress);
      }

      String name = folderName(id);
      Path dimension = this.folder.resolve(name);
      if (this.folder.equals(dimension.getParent()) && name.equals(String.valueOf(dimension.getFileName()))) {
         this.closed.add(name.toLowerCase(Locale.ROOT));
         if (Files.isDirectory(dimension, LinkOption.NOFOLLOW_LINKS)) {
            int deleted = 0;
            int kept = 0;

            for (int zoom = 0; zoom <= 10; zoom++) {
               Path level = dimension.resolve(Integer.toString(zoom));
               if (Files.isDirectory(level, LinkOption.NOFOLLOW_LINKS)) {
                  try (DirectoryStream<Path> files = Files.newDirectoryStream(level, "*.jpg")) {
                     for (Path file : files) {
                        if (TILE_NAME.matcher(file.getFileName().toString()).matches()) {
                           try {
                              Files.delete(file);
                              deleted++;
                           } catch (IOException e) {
                              kept++;
                           }
                        }
                     }

                     deleteIfEmpty(level);
                  } catch (IOException e) {
                     kept++;
                  }
               }
            }

            try {
               deleteIfEmpty(dimension);
            } catch (IOException e) {
               kept++;
            }

            if (deleted > 0) {
               Diorama.LOGGER.info("Web map: deleted the {} pictures of {}, which this server's settings keep off it", deleted, id);
            }

            if (kept > 0 && this.warnedKept.add(name)) {
               Diorama.LOGGER
                  .warn(
                     "Web map: couldn't delete {} of the pictures or folders of {} in {}, which this server's settings keep off the map. Diorama's own web server doesn't serve them; a web server of your own still would, so delete that folder yourself",
                     new Object[]{kept, id, dimension}
                  );
            }
         }
      }
   }

   private static void deleteIfEmpty(Path folder) throws IOException {
      try (DirectoryStream<Path> files = Files.newDirectoryStream(folder)) {
         if (files.iterator().hasNext()) {
            return;
         }
      }

      Files.delete(folder);
   }

   private static String folderName(String id) {
      return id.replace(':', '_').replace('/', '_');
   }

   private void run() {
      try {
         Properties progress = this.loadProgress();
         List<String> out = new ArrayList<>();

         for (ServerLevel level : this.server.getAllLevels()) {
            String id = level.dimension().location().toString();
            if (this.leftOut(level)) {
               out.add(id);
               this.remove(id, progress);
            } else {
               this.dimensions.computeIfAbsent(id, key -> new WebMap.Dimension(this, level, key));
            }
         }

         for (String id : ShareServer.locks(this.server).mapOffDimensions()) {
            if (!out.contains(id)) {
               this.remove(id, progress);
            }
         }

         if (!out.isEmpty()) {
            Diorama.LOGGER
               .info(
                  "Web map: not drawing {}, which this server's settings keep off the map (map-off-dimensions, or cave maps locked for a dimension under a roof)",
                  String.join(", ", out)
               );
         }

         this.writeMapJson();
         if (this.settings.port() > 0 && !this.stopping) {
            this.http = WebServer.start(this.folder, this.settings.address(), this.settings.port(), this.closed::contains);
            if (this.stopping) {
               WebServer late = this.http;
               if (late != null) {
                  late.stop();
               }

               return;
            }

            if (this.http != null) {
               Diorama.LOGGER
                  .info(
                     "Diorama's web map is served at http://{}:{}/",
                     this.settings.address().isEmpty() ? "<this server>" : this.settings.address(),
                     this.settings.port()
                  );
            }
         }

         this.colourMaps();
         JarModels jars = JarModels.of(this.modsFolder);
         TableLooks looks = new TableLooks(this.table, jars);
         Diorama.LOGGER.info("Web map colours: vanilla table of {}; block models from {} mod jars", this.table.describe(), jars.jarCount());
         String land = this.settings.everyChunk() ? "generated" : "visited";
         if (!land.equals(progress.getProperty("share-land", land))) {
            Diorama.LOGGER.info("share-land changed to {}: drawing the whole web map again", land);
            this.redrawAll.set(true);
         } else {
            progress.setProperty("share-land", land);
         }

         while (!this.stopping) {
            boolean all = this.redrawAll.getAndSet(false) || this.redrawOwed;
            this.redrawOwed = false;
            long passStarted = System.currentTimeMillis();
            boolean complete = true;

            for (ServerLevel level : this.server.getAllLevels()) {
               if (this.stopping) {
                  break;
               }

               String id = level.dimension().location().toString();
               if (this.leftOut(level)) {
                  this.remove(id, progress);
               } else {
                  WebMap.Dimension dimension = this.dimensions.computeIfAbsent(id, key -> new WebMap.Dimension(this, level, key));
                  long last = all ? -1L : Long.parseLong(progress.getProperty(id, "-1"));
                  long started = dimension.pass(looks, last);
                  if (started >= 0L) {
                     progress.setProperty(id, Long.toString(started));
                     this.saveProgress(progress);
                  } else if (all) {
                     complete = false;
                  }
               }
            }

            if (all && complete && !this.stopping) {
               progress.setProperty("share-land", land);
               this.saveProgress(progress);
            } else if (all && !this.stopping) {
               this.redrawOwed = true;
            }

            this.writeMapJson();
            this.passes++;
            this.status = "up to date; draws what was saved since at most every 5 minutes";
            this.waitForChanges(progress, passStarted + 300000L);
         }

         jars.close();
      } catch (RuntimeException | Error e) {
         this.status = "stopped by an error: " + e;
         Diorama.LOGGER.error("Diorama's web map stopped", e);
      }
   }

   private void waitForChanges(Properties progress, long notBefore) {
      label53:
      while (!this.stopping && !this.redrawAll.get()) {
         long wait = Math.max(30000L, notBefore - System.currentTimeMillis());
         synchronized (this.wake) {
            try {
               this.wake.wait(wait);
            } catch (InterruptedException e) {
               return;
            }
         }

         if (!this.stopping && !this.redrawAll.get() && System.currentTimeMillis() >= notBefore) {
            if (this.redrawOwed) {
               return;
            }

            Iterator var11 = this.dimensions.values().iterator();

            WebMap.Dimension dimension;
            do {
               if (!var11.hasNext()) {
                  continue label53;
               }

               dimension = (WebMap.Dimension)var11.next();
            } while (!dimension.savedSince(Long.parseLong(progress.getProperty(dimension.id, "-1"))));

            return;
         }
      }
   }

   private void colourMaps() {
      if (GrassColor.get(0.5, 1.0) == 0) {
         int[] grass = this.table.colourMap(0);
         if (grass != null) {
            GrassColor.init(grass);
         }
      }

      if (FoliageColor.get(0.5, 1.0) == 0) {
         int[] foliage = this.table.colourMap(1);
         if (foliage != null) {
            FoliageColor.init(foliage);
         }
      }
   }

   boolean serverBehind() {
      return this.server.getAverageTickTimeNanos() > 40000000L;
   }

   double averageTickMs() {
      return this.server.getAverageTickTimeNanos() / 1000000.0;
   }

   boolean stopping() {
      return this.stopping || this.redrawAll.get();
   }

   boolean shuttingDown() {
      return this.stopping;
   }

   void setStatus(String status) {
      this.status = status;
   }

   private Properties loadProgress() {
      Properties progress = new Properties();
      Path file = this.folder.resolve("progress.properties");
      if (Files.exists(file)) {
         try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            progress.load(reader);
         } catch (IOException | IllegalArgumentException e) {
            Diorama.LOGGER.warn("Couldn't read the web map's progress in {}; drawing everything again", file, e);
            progress.clear();
         }
      }

      return progress;
   }

   private void saveProgress(Properties progress) {
      Path file = this.folder.resolve("progress.properties");
      Path part = this.folder.resolve("progress.properties.part");

      try (Writer writer = Files.newBufferedWriter(part, StandardCharsets.UTF_8)) {
         progress.store(writer, "When each dimension's last web map pass started, in the region files' seconds");
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't save the web map's progress in {}", file, e);
         return;
      }

      try {
         Files.move(part, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't save the web map's progress in {}", file, e);
      }
   }

   void writeMapJson() {
      StringBuilder json = new StringBuilder();
      json.append("{\"tileUnits\":")
         .append(128)
         .append(",\"tilePixels\":")
         .append(512)
         .append(",\"sin\":")
         .append(String.format(Locale.ROOT, "%.6f", Projection.sin()))
         .append(",\"cos\":")
         .append(String.format(Locale.ROOT, "%.6f", Projection.cos()))
         .append(",\"background\":\"#")
         .append(String.format(Locale.ROOT, "%06x", 1382172))
         .append('"')
         .append(",\"updated\":")
         .append(System.currentTimeMillis())
         .append(",\"dimensions\":[");
      List<WebMap.Dimension> list = new ArrayList<>(this.dimensions.values());
      list.sort(Comparator.comparing(d -> d.order() + d.id));
      boolean first = true;

      for (WebMap.Dimension dimension : list) {
         if (!first) {
            json.append(',');
         }

         first = false;
         dimension.json(json);
      }

      json.append("]}");
      Path file = this.folder.resolve("map.json");
      Path part = this.folder.resolve("map.json.part");

      try {
         Files.writeString(part, json, StandardCharsets.UTF_8);
         Files.move(part, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException e) {
         Diorama.LOGGER.warn("Couldn't write the web map's map.json", e);
      }
   }

   private static void sleep(long ms) {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }

   static final class Dimension {
      private final WebMap map;
      private final ServerLevel level;
      final String id;
      private final Path folder;
      private final ServerSave save;
      private final SavedChunkSource source;
      private final Map<Long, long[]> shown = new HashMap<>();
      private final Map<Long, WebMap.Dimension.Drawn> drawn = new HashMap<>();
      private final Set<Long>[] tiles = new Set[11];
      private int zoomLevels;

      Dimension(WebMap map, ServerLevel level, String id) {
         this.map = map;
         this.level = level;
         this.id = id;
         this.folder = map.folder.resolve(WebMap.folderName(id));
         this.save = ServerSave.of(level);
         final int roof = level.dimensionType().hasCeiling() ? level.getMinBuildHeight() + level.dimensionType().logicalHeight() : Integer.MIN_VALUE;
         final int minY = level.getMinBuildHeight();
         final int maxY = level.getMaxBuildHeight() - 1 + 1;
         ChunkSource bounds = new ChunkSource() {
            @Override
            public @Nullable ChunkSnapshot chunk(int chunkX, int chunkZ) {
               return null;
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
               return maxY;
            }

            @Override
            public int roof() {
               return roof;
            }

            @Override
            public boolean isSeen(int x, int y, int z) {
               return true;
            }
         };
         this.source = new SavedChunkSource(bounds, this.save, 2);
         boolean everyChunk = map.settings.everyChunk();
         this.source.allow((chunkX, chunkZ, top) -> everyChunk || this.shows(chunkX, chunkZ));

         for (int z = 0; z <= 10; z++) {
            this.tiles[z] = new HashSet<>();
            Path zoom = this.folder.resolve(Integer.toString(z));
            if (Files.isDirectory(zoom)) {
               try (DirectoryStream<Path> files = Files.newDirectoryStream(zoom, "*.jpg")) {
                  for (Path file : files) {
                     String name = file.getFileName().toString();
                     int sep = name.indexOf(95);

                     try {
                        if (sep > 0) {
                           this.tiles[z].add(key(Integer.parseInt(name.substring(0, sep)), Integer.parseInt(name.substring(sep + 1, name.length() - 4))));
                        }
                     } catch (NumberFormatException var17) {
                     }
                  }
               } catch (IOException e) {
                  Diorama.LOGGER.warn("Couldn't list the web map's tiles in {}", zoom, e);
               }

               if (!this.tiles[z].isEmpty()) {
                  this.zoomLevels = z + 1;
               }
            }
         }
      }

      String order() {
         return this.id.equals("minecraft:overworld")
            ? "0"
            : (this.id.equals("minecraft:the_nether") ? "1" : (this.id.equals("minecraft:the_end") ? "2" : "3"));
      }

      static long key(int u, int v) {
         return (long)u << 32 | v & 4294967295L;
      }

      private boolean shows(int chunkX, int chunkZ) {
         long[] bits = this.shown.computeIfAbsent(ServerSave.regionKey(chunkX >> 5, chunkZ >> 5), k -> new long[32]);
         int i = (chunkZ & 31) << 5 | chunkX & 31;
         long looked = 1L << (i & 31) << 32;
         long yes = 1L << (i & 31);
         if ((bits[i >> 5] & looked) == 0L) {
            ServerSave.Info info = this.save.info(chunkX, chunkZ);
            boolean visited = info != null && info.summary().finished() && info.inhabited() > 0L;
            bits[i >> 5] = bits[i >> 5] | looked | (visited ? yes : 0L);
         }

         return (bits[i >> 5] & yes) != 0L;
      }

      private void forget(int chunkX, int chunkZ) {
         long[] bits = this.shown.get(ServerSave.regionKey(chunkX >> 5, chunkZ >> 5));
         if (bits != null) {
            int i = (chunkZ & 31) << 5 | chunkX & 31;
            bits[i >> 5] = bits[i >> 5] & ~(1L << (i & 31) << 32 | 1L << (i & 31));
         }
      }

      boolean savedSince(long since) {
         for (int[] times : this.save.scan().values()) {
            for (int time : times) {
               if (time != 0 && time >= since) {
                  return true;
               }
            }
         }

         return false;
      }

      long pass(TableLooks looks, long last) {
         long started = System.currentTimeMillis() / 1000L;
         long wallStart = System.nanoTime();
         int failedReads = this.save.failedReads();
         Map<Long, int[]> regions = this.save.scan();
         this.source.clear();
         Set<Long> dirty = new HashSet<>();
         if (last < 0L) {
            dirty.addAll(this.tiles[0]);
         }

         boolean failed = false;
         int changed = 0;
         int counted = 0;

         for (Entry<Long, int[]> region : regions.entrySet()) {
            if (this.map.shuttingDown()) {
               return -1L;
            }

            int regionX = (int)(region.getKey() >> 32);
            int regionZ = (int)region.getKey().longValue();
            int[] times = region.getValue();
            boolean any = false;

            for (int i = 0; i < 1024; i++) {
               if (times[i] != 0 && (last < 0L || times[i] >= last)) {
                  any = true;
                  int chunkX = regionX * 32 + (i & 31);
                  int chunkZ = regionZ * 32 + (i >> 5);
                  boolean wasShown = last >= 0L && (this.map.settings.everyChunk() || this.shows(chunkX, chunkZ));
                  this.forget(chunkX, chunkZ);
                  changed++;
                  ServerSave.Info info = this.save.info(chunkX, chunkZ);
                  if (info != null && info.summary().finished() && (this.map.settings.everyChunk() || info.inhabited() > 0L)) {
                     ChunkTags.Summary summary = info.summary();
                     long chunk = ChunkSnapshot.key(chunkX, chunkZ);
                     WebMap.Dimension.Drawn before = this.drawn.get(chunk);
                     int hash = info.blocks();
                     this.drawn.put(chunk, new WebMap.Dimension.Drawn(hash, summary.maxY(), summary.floorY()));
                     if (last < 0L || !wasShown || before == null || before.blocks() != hash) {
                        counted++;
                        int top = before != null ? Math.max(before.top(), summary.maxY()) : (last >= 0L ? this.level.getMaxBuildHeight() - 1 : summary.maxY());
                        int floor = before != null
                           ? Math.min(before.floor(), summary.floorY())
                           : (last >= 0L ? this.level.getMinBuildHeight() : summary.floorY());
                        this.markTiles(chunkX, chunkZ, top, floor, dirty);
                     }
                  } else {
                     WebMap.Dimension.Drawn gone = this.drawn.remove(ChunkSnapshot.key(chunkX, chunkZ));
                     if (gone != null) {
                        this.markTiles(chunkX, chunkZ, gone.top(), gone.floor(), dirty);
                     } else if (wasShown) {
                        this.markTiles(chunkX, chunkZ, this.level.getMaxBuildHeight() - 1, this.level.getMinBuildHeight(), dirty);
                     }
                  }
               }
            }

            if (any) {
               this.source.forgetTops(region.getKey());
            }
         }

         if (!dirty.isEmpty()) {
            List<Long> order = new ArrayList<>(dirty);
            order.sort(Comparator.comparingLong(k -> {
               long ux = k >> 32;
               long v = (int)k.longValue();
               return ux * ux + v * v;
            }));
            ThreadMXBean threads = ManagementFactory.getThreadMXBean();
            long cpuStart = threads.isCurrentThreadCpuTimeSupported() ? threads.getCurrentThreadCpuTime() : -1L;
            long bytes = 0L;
            int drawn = 0;
            int empty = 0;
            double tickSum = 0.0;
            double tickMax = 0.0;

            for (long key : order) {
               if (this.map.stopping()) {
                  Diorama.LOGGER.info("Web map pass of {} cut short after {} of {} tiles", new Object[]{this.id, drawn + empty, order.size()});
                  return -1L;
               }

               while ((this.map.serverBehind() || this.map.server.isCurrentlySaving()) && !this.map.stopping()) {
                  this.map.setStatus("waiting: the server is behind or saving");
                  WebMap.sleep(1000L);
               }

               this.map.setStatus(String.format(Locale.ROOT, "drawing %s: tile %d of %d", this.id, drawn + empty + 1, order.size()));
               int u = (int)(key >> 32);
               int v = (int)key;

               int[] pixels;
               try {
                  pixels = TileBaker.reference(new TileKey(Integer.MIN_VALUE, u, v), this.source, looks, false, null, null);
               } catch (RuntimeException | LinkageError | StackOverflowError e) {
                  Diorama.LOGGER.warn("Couldn't draw web map tile {} {},{}", new Object[]{this.id, u, v, e});
                  continue;
               }

               try {
                  long size = WebTiles.write(WebTiles.file(this.folder, 0, u, v), pixels);
                  bytes += size;
                  if (size > 0L) {
                     drawn++;
                     this.tiles[0].add(key);
                  } else {
                     empty++;
                     this.tiles[0].remove(key);
                  }
               } catch (IOException e) {
                  failed = true;
                  Diorama.LOGGER.warn("Couldn't write web map tile {} {},{}", new Object[]{this.id, u, v, e});
               }

               double tick = this.map.averageTickMs();
               tickSum += tick;
               tickMax = Math.max(tickMax, tick);
               if ((drawn + empty) % 50 == 0) {
                  this.map.writeMapJson();
               }
            }

            long overviewBytes = this.zoomOut(dirty);
            failed |= overviewBytes < 0L;
            overviewBytes = Math.max(0L, overviewBytes);
            long cpu = cpuStart < 0L ? -1L : threads.getCurrentThreadCpuTime() - cpuStart;
            double seconds = (System.nanoTime() - wallStart) / 1.0E9;
            Diorama.LOGGER
               .info(
                  String.format(
                     Locale.ROOT,
                     "Web map pass of %s: %d chunks saved since the last, %d with new blocks on the map; %d tiles drawn, %d empty, %d zoom levels; %.1f s (%.2f s of CPU a tile); %.1f MB written (%.0f KB a tile, %.1f MB zoomed out); server tick %.1f ms average, %.1f ms worst while drawing",
                     this.id,
                     changed,
                     counted,
                     drawn,
                     empty,
                     this.zoomLevels,
                     seconds,
                     cpu < 0L ? -1.0 : cpu / 1.0E9 / Math.max(1, drawn + empty),
                     (bytes + overviewBytes) / 1000000.0,
                     drawn == 0 ? 0.0 : bytes / 1000.0 / drawn,
                     overviewBytes / 1000000.0,
                     tickSum / Math.max(1, drawn + empty),
                     tickMax
                  )
               );
            this.source.clear();
            failed |= this.save.failedReads() != failedReads;
            if (failed) {
               this.drawn.clear();
               return -1L;
            } else {
               return started;
            }
         } else if (this.save.failedReads() != failedReads) {
            this.drawn.clear();
            return -1L;
         } else {
            return last >= 0L && changed <= 0 ? -1L : started;
         }
      }

      private void markTiles(int chunkX, int chunkZ, int maxY, int floorY, Set<Long> dirty) {
         double sin = Projection.sin();
         double cos = Projection.cos();
         int x0 = chunkX * 16 - Math.abs(TileBaker.SHADOW_REACH_X) - 1;
         int x1 = chunkX * 16 + 16 + Math.abs(TileBaker.SHADOW_REACH_X) + 1;
         int z0 = chunkZ * 16 - Math.abs(TileBaker.SHADOW_REACH_Z) - 1;
         int z1 = chunkZ * 16 + 16 + Math.abs(TileBaker.SHADOW_REACH_Z) + 1;
         int top = maxY + 1;
         int bottom = this.level.dimensionType().hasCeiling() ? this.level.getMinBuildHeight() : Math.max(floorY, this.level.getMinBuildHeight());
         double v0 = z0 * sin - (top + TileBaker.SHADOW_REACH_Y) * cos;
         double v1 = z1 * sin - bottom * cos;
         int u0 = Math.floorDiv(x0, 128);
         int u1 = Math.floorDiv(x1, 128);
         int tv0 = (int)Math.floor(v0 / 128.0);
         int tv1 = (int)Math.floor(v1 / 128.0);

         for (int u = u0; u <= u1; u++) {
            for (int v = tv0; v <= tv1; v++) {
               dirty.add(key(u, v));
            }
         }
      }

      private long zoomOut(Set<Long> drawn) {
         int levels = 1;
         int minU = Integer.MAX_VALUE;
         int maxU = Integer.MIN_VALUE;
         int minV = Integer.MAX_VALUE;
         int maxV = Integer.MIN_VALUE;

         for (long key : this.tiles[0]) {
            int u = (int)(key >> 32);
            int v = (int)key;
            minU = Math.min(minU, u);
            maxU = Math.max(maxU, u);
            minV = Math.min(minV, v);
            maxV = Math.max(maxV, v);
         }

         int span = this.tiles[0].isEmpty() ? 1 : Math.max(maxU - minU + 1, maxV - minV + 1);

         while (levels <= 10 && span >> levels - 1 > 3) {
            levels++;
         }

         long bytes = 0L;
         boolean failed = false;
         Set<Long> below = drawn;

         for (int zoom = 1; zoom < levels; zoom++) {
            Set<Long> here = new HashSet<>();

            for (long key : zoom >= this.zoomLevels ? this.tiles[zoom - 1] : below) {
               here.add(key(Math.floorDiv((int)(key >> 32), 2), Math.floorDiv((int)key, 2)));
            }

            for (long key : here) {
               int u = (int)(key >> 32);
               int v = (int)key;
               Path[] children = new Path[4];

               for (int i = 0; i < 4; i++) {
                  children[i] = WebTiles.file(this.folder, zoom - 1, u * 2 + (i & 1), v * 2 + (i >> 1));
               }

               try {
                  long size = WebTiles.shrink(WebTiles.file(this.folder, zoom, u, v), children);
                  bytes += size;
                  if (size > 0L) {
                     this.tiles[zoom].add(key);
                  } else {
                     this.tiles[zoom].remove(key);
                  }
               } catch (IOException e) {
                  failed = true;
                  Diorama.LOGGER.warn("Couldn't write web map tile {} zoom {} {},{}", new Object[]{this.id, zoom, u, v, e});
               }
            }

            below = here;
         }

         int kept = levels;

         for (int zoom = levels; zoom < this.zoomLevels && zoom <= 10; zoom++) {
            Iterator<Long> it = this.tiles[zoom].iterator();

            while (it.hasNext()) {
               long key = it.next();

               try {
                  Files.deleteIfExists(WebTiles.file(this.folder, zoom, (int)(key >> 32), (int)key));
                  it.remove();
               } catch (IOException e) {
                  failed = true;
               }
            }

            if (!this.tiles[zoom].isEmpty()) {
               kept = zoom + 1;
            }
         }

         this.zoomLevels = kept;
         return failed ? -1L : bytes;
      }

      void json(StringBuilder json) {
         json.append("{\"id\":\"")
            .append(this.id)
            .append("\",\"folder\":\"")
            .append(this.folder.getFileName())
            .append("\",\"zoomLevels\":")
            .append(Math.max(1, this.zoomLevels))
            .append(",\"versions\":true,\"tiles\":[");

         for (int z = 0; z < Math.max(1, this.zoomLevels); z++) {
            if (z > 0) {
               json.append(',');
            }

            json.append('[');
            boolean first = true;

            for (long key : this.tiles[z]) {
               if (!first) {
                  json.append(',');
               }

               first = false;
               int u = (int)(key >> 32);
               int v = (int)key;

               long version;
               try {
                  version = Files.getLastModifiedTime(WebTiles.file(this.folder, z, u, v)).toMillis() / 1000L;
               } catch (IOException e) {
                  version = 0L;
               }

               json.append(u).append(',').append(v).append(',').append(version);
            }

            json.append(']');
         }

         json.append("]}");
      }

      private record Drawn(int blocks, int top, int floor) {
      }
   }
}
