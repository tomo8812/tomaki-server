package maki.idlepregen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.millenaire.village.Village;

public final class MillenaireInitGuard {
   private static final Logger LOG = System.getLogger("MakiIdlePregen");
   private static final int MAX_HEAVY_INITIALIZATIONS_PER_TICK = 1;
   private static final int MISSING_CHUNK_RETRY_TICKS = 20;
   private static final int GRAPH_PREFLIGHT_RADIUS_BLOCKS = 80;
   private static final long SUMMARY_INTERVAL_TICKS = 1200L;
   private static MinecraftServer owner;
   private static int permits;
   private static long serverTicks;
   private static final Map<String, Long> RETRY_AFTER = new HashMap<>();
   private static final ThreadLocal<MillenaireInitGuard.WaypointAttempt> WAYPOINT_ATTEMPT = new ThreadLocal<>();
   private static long zeroPlayerGraphDeferrals;
   private static long zeroPlayerBedDeferrals;
   private static long budgetDeferrals;
   private static long missingGraphDeferrals;
   private static long missingBedDeferrals;
   private static long graphMissingChunksObserved;
   private static long bedMissingChunksObserved;

   private MillenaireInitGuard() {
   }

   public static void beginServerTick(MinecraftServer server) {
      if (owner != server) {
         owner = server;
         permits = 0;
         serverTicks = 0L;
         RETRY_AFTER.clear();
         WAYPOINT_ATTEMPT.remove();
         resetCounters();
      }

      serverTicks++;
      permits = 1;
      if (serverTicks % 1200L == 0L) {
         logSummary();
      }
   }

   public static void shutdown(MinecraftServer server) {
      if (owner == server) {
         logSummary();
         owner = null;
         permits = 0;
         serverTicks = 0L;
         RETRY_AFTER.clear();
         WAYPOINT_ATTEMPT.remove();
         resetCounters();
      }
   }

   public static boolean beginWaypointGraphAttempt(ServerLevel level, Village village) {
      WAYPOINT_ATTEMPT.remove();
      MinecraftServer server = level.getServer();
      if (!server.isDedicatedServer()) {
         return true;
      }

      if (server.getPlayerCount() == 0) {
         zeroPlayerGraphDeferrals++;
         if (zeroPlayerGraphDeferrals == 1L) {
            LOG.log(Level.INFO, "Deferring Millenaire waypoint graph initialization while the dedicated server has no players");
         }

         return false;
      } else {
         String key = operationKey("graph", level, village.getId());
         if (coolingDown(key)) {
            return false;
         }

         if (!hasPermit("waypoint graph", String.valueOf(village.getId()))) {
            return false;
         }

         WAYPOINT_ATTEMPT.set(new MillenaireInitGuard.WaypointAttempt(level, village, key));
         return true;
      }
   }

   public static boolean hasWaypointGraphAttempt() {
      return WAYPOINT_ATTEMPT.get() != null;
   }

   public static boolean admitWaypointGraphAfterThrottle() {
      MillenaireInitGuard.WaypointAttempt attempt = WAYPOINT_ATTEMPT.get();
      if (attempt == null) {
         return true;
      }

      try {
         if (permits <= 0) {
            budgetDeferrals++;
            return false;
         }

         int missing = countMissingGraphChunks(attempt.level, attempt.village);
         if (missing <= 0) {
            RETRY_AFTER.remove(attempt.key);
            permits--;
            return true;
         }

         missingGraphDeferrals++;
         graphMissingChunksObserved += missing;
         deferRetry(attempt.key);
         if (missingGraphDeferrals == 1L || missingGraphDeferrals % 200L == 0L) {
            LOG.log(
               Level.INFO,
               "Deferred Millenaire waypoint graph for village "
                  + attempt.village.getId()
                  + ": "
                  + missing
                  + " required chunks are not loaded; no synchronous load was requested"
            );
         }

         return false;
      } finally {
         WAYPOINT_ATTEMPT.remove();
      }
   }

   public static void finishWaypointGraphAttempt() {
      WAYPOINT_ATTEMPT.remove();
   }

   public static boolean allowBedBackfill(ServerLevel level, BuildingInstance building, BuildingPlan plan) {
      MinecraftServer server = level.getServer();
      if (!server.isDedicatedServer()) {
         return true;
      }

      if (server.getPlayerCount() == 0) {
         zeroPlayerBedDeferrals++;
         if (zeroPlayerBedDeferrals == 1L) {
            LOG.log(Level.INFO, "Deferring Millenaire bed-position backfill while the dedicated server has no players");
         }

         return false;
      } else {
         String key = operationKey("bed", level, building.getId());
         if (coolingDown(key)) {
            return false;
         }

         if (!hasPermit("bed backfill", String.valueOf(building.getId()))) {
            return false;
         }

         int missing = countMissingBedChunks(level, building, plan);
         if (missing <= 0) {
            RETRY_AFTER.remove(key);
            return acquirePermit("bed backfill", String.valueOf(building.getId()));
         }

         missingBedDeferrals++;
         bedMissingChunksObserved += missing;
         deferRetry(key);
         if (missingBedDeferrals == 1L || missingBedDeferrals % 200L == 0L) {
            LOG.log(
               Level.INFO,
               "Deferred Millenaire bed backfill for building "
                  + building.getId()
                  + ": "
                  + missing
                  + " required chunks are not loaded; no synchronous load was requested"
            );
         }

         return false;
      }
   }

   private static boolean hasPermit(String operation, String id) {
      if (permits > 0) {
         return true;
      }

      budgetDeferrals++;
      if (budgetDeferrals == 1L || budgetDeferrals % 500L == 0L) {
         LOG.log(Level.INFO, "Deferred Millenaire " + operation + " " + id + " because the per-server-tick initialization budget is exhausted");
      }

      return false;
   }

   private static boolean acquirePermit(String operation, String id) {
      if (!hasPermit(operation, id)) {
         return false;
      }

      permits--;
      return true;
   }

   private static boolean coolingDown(String key) {
      Long retryAt = RETRY_AFTER.get(key);
      return retryAt != null && serverTicks < retryAt;
   }

   private static void deferRetry(String key) {
      RETRY_AFTER.put(key, serverTicks + 20L);
   }

   private static int countMissingGraphChunks(ServerLevel level, Village village) {
      Set<Long> checked = new HashSet<>();
      int missing = countMissingAround(level, village.getCenter(), checked);

      for (BuildingInstance building : village.getBuildings()) {
         BlockPos anchor = building.resolvePathAnchor();
         if (anchor != null) {
            missing += countMissingAround(level, anchor, checked);
         }
      }

      return missing;
   }

   private static int countMissingAround(ServerLevel level, BlockPos anchor, Set<Long> checked) {
      int minChunkX = SectionPos.blockToSectionCoord(anchor.getX() - 80);
      int maxChunkX = SectionPos.blockToSectionCoord(anchor.getX() + 80);
      int minChunkZ = SectionPos.blockToSectionCoord(anchor.getZ() - 80);
      int maxChunkZ = SectionPos.blockToSectionCoord(anchor.getZ() + 80);
      int missing = 0;

      for (int x = minChunkX; x <= maxChunkX; x++) {
         for (int z = minChunkZ; z <= maxChunkZ; z++) {
            long key = ChunkPos.asLong(x, z);
            if (checked.add(key) && !level.getChunkSource().hasChunk(x, z)) {
               missing++;
            }
         }
      }

      return missing;
   }

   private static int countMissingBedChunks(ServerLevel level, BuildingInstance building, BuildingPlan plan) {
      BlockPos origin = building.getOrigin();
      if (origin == null) {
         return 0;
      }

      StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(building.getRotation());
      BlockPos relativeA = StructureTemplate.calculateRelativePosition(settings, BlockPos.ZERO);
      BlockPos relativeB = StructureTemplate.calculateRelativePosition(settings, new BlockPos(plan.width() - 1, plan.height() - 1, plan.depth() - 1));
      int minChunkX = SectionPos.blockToSectionCoord(origin.getX() + Math.min(relativeA.getX(), relativeB.getX()));
      int maxChunkX = SectionPos.blockToSectionCoord(origin.getX() + Math.max(relativeA.getX(), relativeB.getX()));
      int minChunkZ = SectionPos.blockToSectionCoord(origin.getZ() + Math.min(relativeA.getZ(), relativeB.getZ()));
      int maxChunkZ = SectionPos.blockToSectionCoord(origin.getZ() + Math.max(relativeA.getZ(), relativeB.getZ()));
      int missing = 0;

      for (int x = minChunkX; x <= maxChunkX; x++) {
         for (int z = minChunkZ; z <= maxChunkZ; z++) {
            if (!level.getChunkSource().hasChunk(x, z)) {
               missing++;
            }
         }
      }

      return missing;
   }

   private static String operationKey(String kind, ServerLevel level, Object id) {
      return kind + ":" + level.dimension().location() + ":" + id;
   }

   private static void logSummary() {
      if (zeroPlayerGraphDeferrals != 0L || zeroPlayerBedDeferrals != 0L || budgetDeferrals != 0L || missingGraphDeferrals != 0L || missingBedDeferrals != 0L) {
         LOG.log(
            Level.INFO,
            "Millenaire init guard summary: zero-player graph deferrals="
               + zeroPlayerGraphDeferrals
               + ", zero-player bed deferrals="
               + zeroPlayerBedDeferrals
               + ", budget deferrals="
               + budgetDeferrals
               + ", missing-chunk graph deferrals="
               + missingGraphDeferrals
               + " (missing chunks observed="
               + graphMissingChunksObserved
               + "), missing-chunk bed deferrals="
               + missingBedDeferrals
               + " (missing chunks observed="
               + bedMissingChunksObserved
               + ")"
         );
      }
   }

   private static void resetCounters() {
      zeroPlayerGraphDeferrals = 0L;
      zeroPlayerBedDeferrals = 0L;
      budgetDeferrals = 0L;
      missingGraphDeferrals = 0L;
      missingBedDeferrals = 0L;
      graphMissingChunksObserved = 0L;
      bedMissingChunksObserved = 0L;
   }

   private record WaypointAttempt(ServerLevel level, Village village, String key) {
   }
}
