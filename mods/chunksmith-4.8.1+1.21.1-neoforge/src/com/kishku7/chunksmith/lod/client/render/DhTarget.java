package com.kishku7.chunksmith.lod.client.render;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodSectionBuilder;
import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.DhApi.Delayed;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiLevelLoadEvent.EventParam;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DhTarget {
   private static final long MIN_PUSH_INTERVAL_NANOS = 10000000L;
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static long lastPushNanos;
   private static final Map<Object, IDhApiLevelWrapper> WRAPPERS = new ConcurrentHashMap<>();
   private static final AtomicLong pushed = new AtomicLong();
   private static final AtomicLong failed = new AtomicLong();
   private static volatile boolean bound;
   private static volatile boolean disabled;

   private DhTarget() {
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
            "Chunksmith: this Distant Horizons is not compatible with the API we build against, so we are not feeding it this session ({}). Everything else (including voxy) keeps working. Please report this with the DH version above.",
            cause.toString()
         );
      }
   }

   public static void bind() {
      if (!bound) {
         bound = true;
         DhApi.events.bind(DhApiLevelLoadEvent.class, new DhApiLevelLoadEvent() {
            public void onLevelLoad(DhApiEventParam<EventParam> event) {
               IDhApiLevelWrapper wrapper = ((EventParam)event.value).levelWrapper;
               Object raw = wrapper.getWrappedMcObject();
               if (raw != null) {
                  DhTarget.WRAPPERS.put(raw, wrapper);
               }
            }
         });
      }
   }

   public static boolean available(Level level) {
      return !disabled && WRAPPERS.containsKey(level);
   }

   public static boolean inject(Level level, CsLodChunk record) {
      if (disabled) {
         return false;
      }

      IDhApiLevelWrapper wrapper = WRAPPERS.get(level);
      if (wrapper == null) {
         failed.incrementAndGet();
         return false;
      }

      LevelChunk chunk = new LevelChunk(level, new ChunkPos(record.getChunkX(), record.getChunkZ()));
      LevelChunkSection[] sections = chunk.getSections();
      int count = Math.min(sections.length, record.getSections().size());

      for (int i = 0; i < count; i++) {
         sections[i] = CsLodSectionBuilder.rebuild(level, record, record.getSections().get(i));
      }

      pace();

      DhApiResult<Void> result;
      try {
         result = DhPushGuard.pushing(() -> Delayed.terrainRepo.overwriteChunkDataAsync(wrapper, new Object[]{chunk, level}));
      } catch (LinkageError e) {
         disable(e);
         failed.incrementAndGet();
         return false;
      }

      if (result.success) {
         pushed.incrementAndGet();
         return true;
      } else {
         failed.incrementAndGet();
         return false;
      }
   }

   private static void pace() {
      long now = System.nanoTime();
      long wait = 10000000L - (now - lastPushNanos);
      if (wait > 0L) {
         try {
            Thread.sleep(wait / 1000000L, (int)(wait % 1000000L));
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }

      lastPushNanos = System.nanoTime();
   }

   public static String describe() {
      return "dh pushed " + pushed.get() + ", failed " + failed.get() + ", forced past DH's dedupe gate " + DhPushGuard.forcedCount();
   }
}
