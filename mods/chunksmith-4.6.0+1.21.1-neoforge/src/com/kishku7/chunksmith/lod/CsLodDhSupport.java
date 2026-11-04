package com.kishku7.chunksmith.lod;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.platform.Config;
import com.kishku7.chunksmith.platform.impl.GsonConfig;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent.EventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CsLodDhSupport {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static volatile MinecraftServer server;
   private static volatile CsLodDhGenerator lastGenerator;
   private static volatile IDhApiLevelWrapper lastWrapper;
   private static volatile boolean bound;
   private static volatile boolean disabled;
   private static final Map<Object, IDhApiLevelWrapper> WRAPPERS = new ConcurrentHashMap<>();

   private CsLodDhSupport() {
   }

   public static void register() {
      if (!bound && !disabled && dhPresent()) {
         bound = true;
         LOGGER.info("Chunksmith: {}", version());

         try {
            bind();
         } catch (LinkageError e) {
            disable(e);
         }
      }
   }

   public static String version() {
      try {
         return "Distant Horizons "
            + DhApi.getModVersion()
            + " (API "
            + DhApi.getApiMajorVersion()
            + "."
            + DhApi.getApiMinorVersion()
            + "."
            + DhApi.getApiPatchVersion()
            + ")";
      } catch (RuntimeException | LinkageError e) {
         return "Distant Horizons (version unreadable: " + e + ")";
      }
   }

   public static boolean isDisabled() {
      return disabled;
   }

   static void disable(Throwable cause) {
      if (!disabled) {
         disabled = true;
         LOGGER.warn(
            "Chunksmith: this Distant Horizons is not compatible with the DH API Chunksmith was built against, so LOD will not serve DH this session -- {}. Everything else keeps working. Please report this along with the DH version logged above.",
            cause.toString()
         );
      }
   }

   private static void bind() {
      final boolean override = enabled();
      DhApi.events.bind(DhApiLevelLoadEvent.class, new DhApiLevelLoadEvent() {
         public void onLevelLoad(DhApiEventParam<EventParam> event) {
            IDhApiLevelWrapper level = ((EventParam)event.value).levelWrapper;
            CsLodDhSupport.lastWrapper = level;
            Object raw = level.getWrappedMcObject();
            if (raw != null) {
               CsLodDhSupport.WRAPPERS.put(raw, level);
            }

            Path store = CsLodDhSupport.storeFor(level);
            if (override) {
               if (store != null && Files.isDirectory(store)) {
                  CsLodDhGenerator generator = new CsLodDhGenerator(level, store);
                  CsLodDhSupport.lastGenerator = generator;
                  DhApi.worldGenOverrides.registerWorldGeneratorOverride(level, generator);
                  CsLodDhSupport.LOGGER.info("Chunksmith: serving Distant Horizons from the CSLOD store -> {}", store);
               } else {
                  CsLodDhSupport.LOGGER.info("Chunksmith: DH loaded a level with no CSLOD store; not overriding its generator");
               }
            }
         }
      });
      LOGGER.info("Chunksmith: Distant Horizons detected, CSLOD level events bound");
   }

   public static void setServer(MinecraftServer current) {
      server = current;
   }

   private static Path storeFor(IDhApiLevelWrapper level) {
      MinecraftServer current = server;
      if (current == null) {
         return null;
      }

      Object raw = level.getWrappedMcObject();

      for (ServerLevel candidate : current.getAllLevels()) {
         if (candidate == raw) {
            return LodSupport.storeRoot(candidate);
         }
      }

      return null;
   }

   private static boolean dhPresent() {
      return LodPlatform.isModLoaded("distanthorizons");
   }

   private static boolean enabled() {
      if (!dhPresent()) {
         return false;
      }

      Config config = config();
      return config != null && LodSupport.decide(config, server) && config.isLodDhOverrideEnabled();
   }

   private static Config config() {
      if (ChunksmithProvider.isLoaded()) {
         return ChunksmithProvider.get().getConfig();
      }

      Path configDir = LodPlatform.configDir();
      Path path = configDir.resolve("chunksmith").resolve("config.json");
      if (!Files.isRegularFile(path)) {
         path = configDir.resolve("chunky").resolve("config.json");
      }

      return !Files.isRegularFile(path) ? null : new GsonConfig(path);
   }

   public static IDhApiLevelWrapper wrapperFor(ServerLevel level) {
      return WRAPPERS.get(level);
   }

   public static IDhApiLevelWrapper wrapper() {
      return lastWrapper;
   }

   public static int knownLevelCount() {
      return WRAPPERS.size();
   }

   public static String describe() {
      CsLodDhGenerator generator = lastGenerator;
      return generator == null
         ? "not serving DH (levels known: " + WRAPPERS.size() + ")"
         : "serving DH: " + generator.getServedCount() + " chunks from the store, " + generator.getMissedCount() + " not pregenerated";
   }
}
