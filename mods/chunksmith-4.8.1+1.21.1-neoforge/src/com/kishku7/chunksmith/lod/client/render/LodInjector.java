package com.kishku7.chunksmith.lod.client.render;

import com.kishku7.chunksmith.lod.CsLodRegionStore;
import com.kishku7.chunksmith.lod.client.CsLodClientConfig;
import com.kishku7.chunksmith.lod.client.CsLodDimension;
import com.kishku7.chunksmith.lod.client.CsLodStore;
import com.kishku7.chunksmith.lod.client.InjectedIndex;
import com.kishku7.chunksmith.lod.client.InjectedRegions;
import com.kishku7.chunksmith.lod.client.Renderers;
import com.kishku7.chunksmith.lod.net.CsLodMessages;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LodInjector {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final long READY_TIMEOUT_MILLIS = 60000L;
   private static final long READY_POLL_MILLIS = 250L;
   private static final AtomicLong chunks = new AtomicLong();
   private static final AtomicLong voxySections = new AtomicLong();
   private static final AtomicLong dhChunks = new AtomicLong();
   private static final AtomicInteger SESSION = new AtomicInteger();
   private static final InjectedRegions INJECTED = new InjectedRegions();
   private static final Map<String, InjectedIndex> PERSISTED = new ConcurrentHashMap<>();

   public static void stop() {
      SESSION.incrementAndGet();
   }

   private LodInjector() {
   }

   public static void injectRegions(Path storeRoot, String dimension, List<CsLodMessages.RegionEntry> regions, Consumer<String> progress) {
      int session = SESSION.get();
      Minecraft client = Minecraft.getInstance();
      Level level = client.level;
      if (level == null) {
         LOGGER.info("Chunksmith: no world loaded; nothing to inject");
      } else {
         String levelDimension = CsLodDimension.of(level);
         if (!levelDimension.equals(dimension)) {
            LOGGER.info(
               "Chunksmith: not injecting {} LOD data. The player is now in {}. (Terrain from another dimension is not a substitute for this one's; the data for {} will be fetched for the level the player is actually in.)",
               new Object[]{dimension, levelDimension, levelDimension}
            );
         } else {
            boolean voxyInstalled = Renderers.hasVoxy();
            boolean dhInstalled = Renderers.hasDh();
            InjectedIndex index = PERSISTED.computeIfAbsent(
               dimension,
               dim -> {
                  InjectedIndex opened = InjectedIndex.open(
                     storeRoot, dim, InjectedIndex.epochFor(voxyInstalled, dhInstalled, 1), CsLodClientConfig.reinjectOnJoin()
                  );
                  if (opened != null && opened.size() > 0) {
                     for (long[] entry : opened.entries()) {
                        INJECTED.seed(dim, (int)entry[0], (int)entry[1], entry[2]);
                     }

                     LOGGER.info(
                        "Chunksmith: {} region(s) of {} were already given to this renderer in an earlier session; only what has changed will be injected",
                        opened.size(),
                        dim
                     );
                  }

                  return opened;
               }
            );
            List<CsLodMessages.RegionEntry> fresh = new ArrayList<>();

            for (CsLodMessages.RegionEntry region : regions) {
               if (INJECTED.claim(dimension, region.regionX(), region.regionZ(), region.hash())) {
                  fresh.add(region);
               }
            }

            if (!fresh.isEmpty()) {
               if (!awaitRenderer(level)) {
                  for (CsLodMessages.RegionEntry region : fresh) {
                     INJECTED.release(dimension, region.regionX(), region.regionZ());
                     forget(index, region);
                  }

                  LOGGER.info(
                     "Chunksmith: no renderer became ready within {}s (voxy={} dh={}); downloaded LODs are cached and will be injected on the next join",
                     new Object[]{60L, Renderers.hasVoxy(), Renderers.hasDh()}
                  );
               } else {
                  boolean voxy = voxyInstalled && VoxyTarget.available();
                  boolean dh = dhInstalled && DhTarget.available(level);
                  boolean persist = index != null && voxy == voxyInstalled && dh == dhInstalled;
                  progress.accept(
                     "injecting " + fresh.size() + " new region(s) of " + dimension + " into " + (voxy ? "voxy " : "") + (dh ? "distant-horizons" : "")
                  );
                  Path dir = CsLodStore.dimensionDir(storeRoot, dimension);
                  if (dir == null) {
                     LOGGER.warn("Chunksmith: refusing to inject a malformed dimension id");
                  } else {
                     for (int i = 0; i < fresh.size(); i++) {
                        CsLodMessages.RegionEntry region = fresh.get(i);
                        boolean sessionEnded = SESSION.get() != session;
                        if (sessionEnded || Minecraft.getInstance().level != level) {
                           for (int j = i; j < fresh.size(); j++) {
                              INJECTED.release(dimension, fresh.get(j).regionX(), fresh.get(j).regionZ());
                              forget(index, fresh.get(j));
                           }

                           flush(index, dimension);
                           LOGGER.info(
                              "Chunksmith: stopping the {} LOD injection ({}). {} region(s) were not injected and will be re-fetched {}.",
                              new Object[]{
                                 dimension,
                                 sessionEnded ? "the session ended" : "the player is no longer in this world",
                                 fresh.size() - i,
                                 sessionEnded ? "on the next join" : "if the player returns"
                              }
                           );
                           return;
                        }

                        try {
                           CsLodRegionStore.forEachChunkInRegion(dir, region.regionX(), region.regionZ(), record -> {
                              if (voxy) {
                                 voxySections.addAndGet(VoxyTarget.inject(level, record));
                              }

                              if (dh && DhTarget.inject(level, record)) {
                                 dhChunks.incrementAndGet();
                              }

                              long done = chunks.incrementAndGet();
                              if (done % 500L == 0L) {
                                 progress.accept("injected " + done + " chunks");
                              }
                           });
                           if (persist) {
                              index.put(region.regionX(), region.regionZ(), region.hash());
                           }
                        } catch (IOException e) {
                           INJECTED.release(dimension, region.regionX(), region.regionZ());
                           forget(index, region);
                           LOGGER.warn("Chunksmith: failed to read region {}.{}: {}", new Object[]{region.regionX(), region.regionZ(), e.toString()});
                        }
                     }

                     flush(index, dimension);
                     progress.accept(
                        "done, "
                           + chunks.get()
                           + " chunks"
                           + (voxy ? ", " + voxySections.get() + " voxy sections" : "")
                           + (dh ? ", " + dhChunks.get() + " to distant-horizons (" + DhTarget.describe() + ")" : "")
                     );
                     reportDhGate(dh);
                  }
               }
            }
         }
      }
   }

   private static void reportDhGate(boolean dh) {
      if (dh && dhChunks.get() != 0L && DhPushGuard.forcedCount() <= 0L) {
         LOGGER.info(
            "Chunksmith: pushed {} chunks to Distant Horizons and never had to force its dedupe gate. On a singleplayer world or a server WITHOUT DH that is normal and expected. If this IS a DH-enabled server, our mixin did not fire and DH may be silently discarding terrain it has seen in the last 10 minutes. Please report it with your DH version ({}).",
            dhChunks.get(),
            DhTarget.version()
         );
      }
   }

   private static void forget(InjectedIndex index, CsLodMessages.RegionEntry region) {
      if (index != null) {
         index.remove(region.regionX(), region.regionZ());
      }
   }

   private static void flush(InjectedIndex index, String dimension) {
      if (index != null) {
         try {
            index.save();
         } catch (IOException e) {
            LOGGER.warn(
               "Chunksmith: could not record which {} LOD regions were injected ({}); they will be injected again on the next join", dimension, e.toString()
            );
         }
      }
   }

   public static void reset() {
      INJECTED.clear();
      PERSISTED.clear();
   }

   private static boolean awaitRenderer(Level level) {
      long deadline = System.currentTimeMillis() + 60000L;

      while (System.currentTimeMillis() < deadline) {
         if (Renderers.hasVoxy() && VoxyTarget.available() || Renderers.hasDh() && DhTarget.available(level)) {
            return true;
         }

         try {
            Thread.sleep(250L);
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
         }
      }

      return false;
   }

   public static String describe() {
      return chunks.get() + " chunks injected (" + voxySections.get() + " voxy sections, " + dhChunks.get() + " dh chunks)";
   }
}
