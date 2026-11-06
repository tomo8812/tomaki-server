package maki.idlepregen;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Locale;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.BuildingPlan;
import org.millenaire.village.Village;

public final class MillenaireInitTelemetry {
    private static final Logger LOG = System.getLogger("MakiIdlePregen");
    private static final ThreadLocal<Context> CURRENT = new ThreadLocal<>();

    private MillenaireInitTelemetry() {
    }

    public static void beginGraph(Village village) {
        CURRENT.set(new Context("waypoint-graph", String.valueOf(village.getId()), 0L));
    }

    public static void endGraph() {
        finish();
    }

    public static void beginBedBackfill(BuildingInstance building, BuildingPlan plan) {
        long blocks = (long)plan.width() * (long)plan.height() * (long)plan.depth();
        CURRENT.set(new Context("bed-backfill", String.valueOf(building.getId()), blocks));
    }

    public static void endBedBackfill() {
        finish();
    }

    public static void recordTraversalSearch() {
        Context context = CURRENT.get();
        if (context != null && "waypoint-graph".equals(context.operation)) {
            context.traversalSearches++;
        }
    }

    public static void recordPathTypeLookup() {
        Context context = CURRENT.get();
        if (context != null && "waypoint-graph".equals(context.operation)) {
            context.pathTypeLookups++;
        }
    }

    public static void recordEmptyChunkAccess() {
        Context context = CURRENT.get();
        if (context != null && "waypoint-graph".equals(context.operation)) {
            context.emptyChunkAccesses++;
        }
    }

    public static void recordEmptyChunkCreation() {
        Context context = CURRENT.get();
        if (context != null && "waypoint-graph".equals(context.operation)) {
            context.emptyChunkCreations++;
        }
    }

    public static void recordChunkGet(boolean loadRequested, boolean unloadedAtEntry) {
        Context context = CURRENT.get();
        if (context == null) {
            return;
        }
        context.chunkGetCalls++;
        if (loadRequested) {
            context.chunkGetLoadRequested++;
        }
        if (loadRequested && unloadedAtEntry) {
            context.synchronousLoadMisses++;
        }
    }

    private static void finish() {
        Context context = CURRENT.get();
        if (context == null) {
            return;
        }

        CURRENT.remove();
        double elapsedMs = (System.nanoTime() - context.startedNanos) / 1_000_000.0;
        String detail = "Millenaire init " + context.operation
                + " id=" + context.id
                + " durationMs=" + String.format(Locale.ROOT, "%.1f", elapsedMs)
                + (context.plannedBlocks > 0L ? " plannedBlocks=" + context.plannedBlocks : "")
                + " traversalSearches=" + context.traversalSearches
                + " pathTypeLookups=" + context.pathTypeLookups
                + " emptyChunkAccesses=" + context.emptyChunkAccesses
                + " emptyChunkCreations=" + context.emptyChunkCreations
                + " chunkGetCalls=" + context.chunkGetCalls
                + " getChunkLoadRequested=" + context.chunkGetLoadRequested
                + " unloadedLoadRequests=" + context.synchronousLoadMisses;

        LOG.log(elapsedMs >= 200.0 ? Level.WARNING : Level.INFO, detail);
    }

    private static final class Context {
        final String operation;
        final String id;
        final long plannedBlocks;
        final long startedNanos = System.nanoTime();
        long traversalSearches;
        long pathTypeLookups;
        long emptyChunkAccesses;
        long emptyChunkCreations;
        long chunkGetCalls;
        long chunkGetLoadRequested;
        long synchronousLoadMisses;

        Context(String operation, String id, long plannedBlocks) {
            this.operation = operation;
            this.id = id;
            this.plannedBlocks = plannedBlocks;
        }
    }
}
