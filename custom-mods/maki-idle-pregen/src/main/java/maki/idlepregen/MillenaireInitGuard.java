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
import org.millenaire.village.VillageWaypointGraph;

public final class MillenaireInitGuard {
    private static final Logger LOG = System.getLogger("MakiIdlePregen");
    private static final int MAX_HEAVY_INITIALIZATIONS_PER_TICK = 1;
    private static final int MISSING_CHUNK_RETRY_TICKS = 20;
    private static final int GRAPH_PREFLIGHT_RADIUS_BLOCKS =
            (int)VillageWaypointGraph.MAX_EDGE_DISTANCE + 16;
    private static final long SUMMARY_INTERVAL_TICKS = 1200L;

    private static MinecraftServer owner;
    private static int permits;
    private static long serverTicks;
    private static final Map<String, Long> RETRY_AFTER = new HashMap<>();

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
            resetCounters();
        }
        serverTicks++;
        permits = MAX_HEAVY_INITIALIZATIONS_PER_TICK;
        if (serverTicks % SUMMARY_INTERVAL_TICKS == 0L) {
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
            resetCounters();
        }
    }

    public static boolean allowWaypointGraph(ServerLevel level, Village village) {
        MinecraftServer server = level.getServer();
        if (!server.isDedicatedServer()) {
            return true;
        }
        if (server.getPlayerCount() == 0) {
            zeroPlayerGraphDeferrals++;
            if (zeroPlayerGraphDeferrals == 1L) {
                LOG.log(Level.INFO,
                        "Deferring Millenaire waypoint graph initialization while the dedicated server has no players");
            }
            return false;
        }

        String key = operationKey("graph", level, village.getId());
        if (coolingDown(key)) {
            return false;
        }

        int missing = countMissingGraphChunks(level, village);
        if (missing > 0) {
            missingGraphDeferrals++;
            graphMissingChunksObserved += missing;
            deferRetry(key);
            if (missingGraphDeferrals == 1L || missingGraphDeferrals % 200L == 0L) {
                LOG.log(Level.INFO, "Deferred Millenaire waypoint graph for village "
                        + village.getId() + ": " + missing
                        + " required chunks are not loaded; no synchronous load was requested");
            }
            return false;
        }

        RETRY_AFTER.remove(key);
        return hasPermit("waypoint graph", String.valueOf(village.getId()));
    }

    public static void consumeWaypointGraphPermit(ServerLevel level) {
        if (!level.getServer().isDedicatedServer()) {
            return;
        }

        // This is called immediately before Village.rebuildWaypointGraph, after
        // WaypointRebuildThrottle.tryAcquire has returned true. Server ticks are
        // single-threaded, so the permit observed by allowWaypointGraph cannot be
        // consumed between the preflight and this point.
        if (permits > 0) {
            permits--;
        }
    }

    public static boolean allowBedBackfill(ServerLevel level, BuildingInstance building, BuildingPlan plan) {
        MinecraftServer server = level.getServer();
        if (!server.isDedicatedServer()) {
            return true;
        }
        if (server.getPlayerCount() == 0) {
            zeroPlayerBedDeferrals++;
            if (zeroPlayerBedDeferrals == 1L) {
                LOG.log(Level.INFO,
                        "Deferring Millenaire bed-position backfill while the dedicated server has no players");
            }
            return false;
        }

        String key = operationKey("bed", level, building.getId());
        if (coolingDown(key)) {
            return false;
        }

        int missing = countMissingBedChunks(level, building, plan);
        if (missing > 0) {
            missingBedDeferrals++;
            bedMissingChunksObserved += missing;
            deferRetry(key);
            if (missingBedDeferrals == 1L || missingBedDeferrals % 200L == 0L) {
                LOG.log(Level.INFO, "Deferred Millenaire bed backfill for building "
                        + building.getId() + ": " + missing
                        + " required chunks are not loaded; no synchronous load was requested");
            }
            return false;
        }

        RETRY_AFTER.remove(key);
        return acquirePermit("bed backfill", String.valueOf(building.getId()));
    }

    private static boolean hasPermit(String operation, String id) {
        if (permits > 0) {
            return true;
        }

        budgetDeferrals++;
        if (budgetDeferrals == 1L || budgetDeferrals % 500L == 0L) {
            LOG.log(Level.INFO, "Deferred Millenaire " + operation + " " + id
                    + " because the per-server-tick initialization budget is exhausted");
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
        RETRY_AFTER.put(key, serverTicks + MISSING_CHUNK_RETRY_TICKS);
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
        int minChunkX = SectionPos.blockToSectionCoord(anchor.getX() - GRAPH_PREFLIGHT_RADIUS_BLOCKS);
        int maxChunkX = SectionPos.blockToSectionCoord(anchor.getX() + GRAPH_PREFLIGHT_RADIUS_BLOCKS);
        int minChunkZ = SectionPos.blockToSectionCoord(anchor.getZ() - GRAPH_PREFLIGHT_RADIUS_BLOCKS);
        int maxChunkZ = SectionPos.blockToSectionCoord(anchor.getZ() + GRAPH_PREFLIGHT_RADIUS_BLOCKS);
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

        StructurePlaceSettings settings =
                new StructurePlaceSettings().setRotation(building.getRotation());
        BlockPos relativeA = StructureTemplate.calculateRelativePosition(settings, BlockPos.ZERO);
        BlockPos relativeB = StructureTemplate.calculateRelativePosition(
                settings, new BlockPos(plan.width() - 1, plan.height() - 1, plan.depth() - 1));

        int minChunkX = SectionPos.blockToSectionCoord(
                origin.getX() + Math.min(relativeA.getX(), relativeB.getX()));
        int maxChunkX = SectionPos.blockToSectionCoord(
                origin.getX() + Math.max(relativeA.getX(), relativeB.getX()));
        int minChunkZ = SectionPos.blockToSectionCoord(
                origin.getZ() + Math.min(relativeA.getZ(), relativeB.getZ()));
        int maxChunkZ = SectionPos.blockToSectionCoord(
                origin.getZ() + Math.max(relativeA.getZ(), relativeB.getZ()));

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
        return kind + ':' + level.dimension().location() + ':' + id;
    }

    private static void logSummary() {
        if (zeroPlayerGraphDeferrals == 0L
                && zeroPlayerBedDeferrals == 0L
                && budgetDeferrals == 0L
                && missingGraphDeferrals == 0L
                && missingBedDeferrals == 0L) {
            return;
        }

        LOG.log(Level.INFO,
                "Millenaire init guard summary: zero-player graph deferrals="
                        + zeroPlayerGraphDeferrals
                        + ", zero-player bed deferrals=" + zeroPlayerBedDeferrals
                        + ", budget deferrals=" + budgetDeferrals
                        + ", missing-chunk graph deferrals=" + missingGraphDeferrals
                        + " (missing chunks observed=" + graphMissingChunksObserved + ')'
                        + ", missing-chunk bed deferrals=" + missingBedDeferrals
                        + " (missing chunks observed=" + bedMissingChunksObserved + ')');
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
}
