package com.kishku7.chunksmith.lod;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.platform.Config;
import com.kishku7.chunksmith.platform.LodMode;
import com.kishku7.chunksmith.util.RendererNames;
import com.kishku7.chunksmith.util.WorldEnterFreeze;
import com.kishku7.chunksmith.util.WorldEnterLod;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LodSupport {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final int WRITE_QUEUE_CAPACITY = 2048;
   private static final Map<String, LodSink> SINKS = new ConcurrentHashMap<>();
   private static final Map<String, CsLodPresenceIndex> PRESENCE = new ConcurrentHashMap<>();
   private static final List<String> RENDERER_IDS = RendererNames.ids();
   private static volatile String renderer;
   private static volatile boolean rendererResolved;
   private static final AtomicBoolean ANNOUNCED = new AtomicBoolean();

   private LodSupport() {
   }

   public static void install(MinecraftServer server) {
      LodPresence.setProvider(worldName -> presenceIndexFor(server, worldName));
      HorizonGuard.install(server);
   }

   public static CsLodPresenceIndex presenceIndexFor(MinecraftServer server, String worldName) {
      if (server != null && lodEnabled(server)) {
         ServerLevel level = levelByName(server, worldName);
         return level == null ? null : PRESENCE.computeIfAbsent(worldName, ignored -> new CsLodPresenceIndex(storeRoot(level)));
      } else {
         return null;
      }
   }

   private static ServerLevel levelByName(MinecraftServer server, String worldName) {
      for (ServerLevel level : server.getAllLevels()) {
         if (dimensionId(level).equals(worldName)) {
            return level;
         }
      }

      return null;
   }

   public static void offer(ServerLevel level, LevelChunk chunk) {
      if (lodEnabled(level.getServer())) {
         LodSink sink = sinkFor(level);
         if (sink != LodSink.NOOP) {
            sink.offer(chunk);
            if (hasStore(sink)) {
               CsLodChunk record = CsLodExtractor.extract(chunk);
               if (record != null) {
                  sink.offer(record);
               }
            }
         }
      }
   }

   public static LodSink sinkFor(ServerLevel level) {
      String key = dimensionId(level);
      return SINKS.computeIfAbsent(key, ignored -> create(level));
   }

   public static void shutdown() {
      for (LodSink sink : SINKS.values()) {
         for (LodSink leaf : leaves(sink)) {
            if (leaf instanceof CsLodStoreSink store) {
               LOGGER.info(
                  String.format(
                     "Chunksmith: LOD store: %d chunks, %d bytes (%.1f KB/chunk), %d synchronous writes",
                     store.getWrittenCount(),
                     store.getWrittenBytes(),
                     store.getWrittenCount() == 0L ? 0.0 : store.getWrittenBytes() / 1024.0 / store.getWrittenCount(),
                     store.getSynchronousWrites()
                  )
               );
               store.shutdown();
            }
         }
      }

      LiveFeedLedger.finishAll();
      ExploredLod.reset();
      SINKS.clear();
      LodSinks.set(LodSink.NOOP);
      LodPresence.setProvider(null);
      PRESENCE.clear();
   }

   private static LodSink create(ServerLevel level) {
      List<LodSink> sinks = new ArrayList<>(2);
      Path root = storeRoot(level);
      sinks.add(new CsLodStoreSink(root, 2048));
      LOGGER.info("Chunksmith: LOD store enabled -> {}", root);
      if (LodPlatform.isModLoaded("voxy")) {
         try {
            sinks.add(new VoxyLodSink(root));
            LOGGER.info("Chunksmith: voxy detected -- feeding LODs to voxy as well");
         } catch (LinkageError error) {
            LOGGER.warn("Chunksmith: voxy present but incompatible, skipping voxy sink: {}", error.toString());
         }
      }

      LodSink sink = sinks.size() == 1 ? sinks.get(0) : new CompositeLodSink(sinks);
      LodSinks.set(sink);
      return sink;
   }

   public static Path storeRootBase(MinecraftServer server) {
      return server.getWorldPath(LevelResource.ROOT).resolve("chunksmith").resolve("lod").normalize();
   }

   public static String worldId(MinecraftServer server) {
      return CsLodWorldId.forStore(storeRootBase(server));
   }

   public static Path storeRoot(ServerLevel level) {
      Path worldRoot = level.getServer().getWorldPath(LevelResource.ROOT);
      return worldRoot.resolve("chunksmith").resolve("lod").resolve(dimensionKey(level)).normalize();
   }

   public static String dimensionKey(ServerLevel level) {
      return dimensionId(level).replace(':', '_').replace('/', '_');
   }

   public static String dimensionId(ServerLevel level) {
      return level.dimension().location().toString();
   }

   private static boolean hasStore(LodSink sink) {
      for (LodSink leaf : leaves(sink)) {
         if (leaf instanceof CsLodStoreSink) {
            return true;
         }
      }

      return false;
   }

   private static List<LodSink> leaves(LodSink sink) {
      return sink instanceof CompositeLodSink composite ? composite.getSinks() : List.of(sink);
   }

   private static String detectRenderer() {
      if (!rendererResolved) {
         synchronized (LodSupport.class) {
            if (!rendererResolved) {
               String found = null;

               for (String id : RENDERER_IDS) {
                  if (LodPlatform.isModLoaded(id)) {
                     found = id;
                     break;
                  }
               }

               renderer = found;
               rendererResolved = true;
            }
         }
      }

      return renderer;
   }

   public static boolean decide(Config config, MinecraftServer server) {
      if (config == null) {
         return false;
      }

      switch (config.getLodMode()) {
         case ON:
            return true;
         case OFF:
            return false;
         default:
            return detectRenderer() != null || server != null && server.isDedicatedServer();
      }
   }

   public static boolean lodEnabled(MinecraftServer server) {
      if (!ChunksmithProvider.isLoaded()) {
         return false;
      }

      Config config = ChunksmithProvider.get().getConfig();
      return WorldEnterFreeze.isFrozen() && !WorldEnterLod.resolve(config.getWorldEnterLodMode(), detectRenderer() != null) ? false : decide(config, server);
   }

   public static void announce(MinecraftServer server) {
      if (ANNOUNCED.compareAndSet(false, true)) {
         if (ChunksmithProvider.isLoaded()) {
            Config config = ChunksmithProvider.get().getConfig();
            LodMode mode = config.getLodMode();
            String found = detectRenderer();
            boolean on = decide(config, server);
            if (mode != LodMode.AUTO) {
               LOGGER.info(
                  "Chunksmith: LOD generation {} (lodEnabled={} set explicitly in the config{})",
                  new Object[]{
                     on ? "ON" : "off", mode == LodMode.ON ? "true" : "false", found == null ? "" : "; " + RendererNames.display(found) + " is installed"
                  }
               );
            } else {
               if (found != null) {
                  LOGGER.info(
                     "Chunksmith: detected {}, so LOD generation is auto-enabled. Pregen will build the CSLOD store (~5 KB/chunk, measured 2026-10-12; it roughly HALVES pregen throughput, measured 2026-09-19). Set lodEnabled=false in config/chunksmith/config.json, or run /cs set lodEnabled false, to turn it off.",
                     RendererNames.display(found)
                  );
               } else if (server != null && server.isDedicatedServer()) {
                  LOGGER.info(
                     "Chunksmith: LOD generation auto-enabled (dedicated server). No renderer runs here, but the CSLOD store is what connecting clients download, so the store is built (~5 KB/chunk, measured 2026-10-12; it roughly HALVES pregen throughput, measured 2026-09-19). Set lodEnabled=false in config/chunksmith/config.json, or run /cs set lodEnabled false, to turn it off."
                  );
               } else {
                  LOGGER.info(
                     "Chunksmith: no LOD renderer detected (looked for {}); LOD generation off. Install Distant Horizons or Voxy, or set lodEnabled=true to force it on.",
                     String.join(", ", RendererNames.displayNames())
                  );
               }

               announceWorldEnter(config, server, found, on);
            }
         }
      }
   }

   private static void announceWorldEnter(Config config, MinecraftServer server, String found, boolean lodOn) {
      if (server != null && !server.isDedicatedServer()) {
         LodMode mode = config.getWorldEnterLodMode();
         boolean build = WorldEnterLod.resolve(mode, found != null);
         if (mode == LodMode.AUTO && found == null) {
            LOGGER.info(
               "Chunksmith: no LOD renderer is installed, so the world-enter pre-gen builds no LOD data (worldEnterLod=auto -> off). Set worldEnterLod=on to build it anyway."
            );
         } else if (!lodOn) {
            LOGGER.info(
               "Chunksmith: LOD generation is off, so the world-enter pre-gen builds no LOD data (worldEnterLod={} has nothing to act on).",
               mode.name().toLowerCase(Locale.ROOT)
            );
         } else if (mode != LodMode.AUTO) {
            LOGGER.info(
               "Chunksmith: the world-enter pre-gen {} LOD data (worldEnterLod={} set explicitly; auto would {}).",
               new Object[]{
                  build ? "builds" : "does NOT build",
                  mode.name().toLowerCase(Locale.ROOT),
                  WorldEnterLod.resolve(LodMode.AUTO, found != null) ? "build it" : "not build it"
               }
            );
         } else if (build) {
            LOGGER.info(
               "Chunksmith: {} is installed, so the world-enter pre-gen builds LOD data as it goes (worldEnterLod=auto -> on; measured faster than leaving it to the renderer). Set worldEnterLod=off to skip it.",
               RendererNames.display(found)
            );
         }
      }
   }

   public static String describeDecision(MinecraftServer server) {
      if (!ChunksmithProvider.isLoaded()) {
         return "lod: unknown (chunksmith not loaded)";
      }

      Config config = ChunksmithProvider.get().getConfig();
      String found = detectRenderer();

      return "lod: "
         + (decide(config, server) ? "ON" : "off")
         + " ("
         + switch (config.getLodMode()) {
            case ON -> "forced on";
            case OFF -> "forced off";
            default -> found != null
               ? "auto: " + found + " detected"
               : (server != null && server.isDedicatedServer() ? "auto: dedicated server" : "auto: no renderer");
         }
         + ")";
   }
}
