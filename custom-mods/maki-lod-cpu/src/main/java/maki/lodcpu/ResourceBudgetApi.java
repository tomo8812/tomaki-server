package maki.lodcpu;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;

/**
 * Stable server-side coordination boundary between the follow governor and
 * low-priority real-chunk backfill. It never stops, resumes, or adopts a
 * Chunksmith task.
 */
public final class ResourceBudgetApi {
    private ResourceBudgetApi() {}

    public static String reserveBackfill() {
        SharedWorkBudget.Decision decision = CpuGovernor.reserveBackfill(activeTaskMode());
        if (decision.reserved() && decision.ready()) {
            return "granted";
        }
        return decision.reason();
    }

    public static boolean backfillReservationHeld() {
        return CpuGovernor.backfillReservationHeld();
    }

    public static void backfillStarted() {
        CpuGovernor.backfillStarted();
    }

    public static void releaseBackfill(String outcome) {
        CpuGovernor.releaseBackfill(outcome == null ? "released" : outcome);
    }

    public static String status() {
        return CpuGovernor.sharedBudgetStatus();
    }

    private static String activeTaskMode() {
        try {
            if (!ChunksmithProvider.isLoaded() || ChunksmithProvider.get() == null
                || ChunksmithProvider.get().getGenerationTasks().isEmpty()) {
                return "none";
            }
            for (GenerationTask task : ChunksmithProvider.get().getGenerationTasks().values()) {
                if (!TaskOwnership.isOwned(task)) {
                    return "external";
                }
            }
            return "follow";
        } catch (Throwable error) {
            return "external";
        }
    }
}
