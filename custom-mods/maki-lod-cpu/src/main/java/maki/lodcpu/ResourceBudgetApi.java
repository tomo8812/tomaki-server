package maki.lodcpu;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;

/**
 * Stable server-side coordination boundary between the follow governor and
 * low-priority real-chunk backfill. Only follow-owned work may be paused.
 */
public final class ResourceBudgetApi {
    private ResourceBudgetApi() {}

    public static String reserveBackfill() {
        String mode=activeTaskMode();
        SharedWorkBudget.Decision decision=CpuGovernor.reserveBackfill(mode);
        if(!decision.reserved()) {
            PlayerCenter.releaseBackfillExclusivePause();
            return decision.reason();
        }

        String pause=PlayerCenter.requestBackfillExclusivePause();
        if(!"paused".equals(pause)&&!"draining-follow".equals(pause)) {
            CpuGovernor.releaseBackfill(pause,false);
            PlayerCenter.releaseBackfillExclusivePause();
            return pause;
        }

        // stop(false) is asynchronous: the Chunksmith task remains registered
        // until its in-flight requests drain and its persisted progress is saved.
        mode=activeTaskMode();
        decision=CpuGovernor.reserveBackfill(mode);
        if(decision.reserved()&&decision.ready()&&"none".equals(mode))return "granted";
        return "draining-follow".equals(pause)&&"pausing-follow".equals(decision.reason())
            ?"draining-follow":decision.reason();
    }

    public static boolean backfillReservationHeld() {
        return CpuGovernor.backfillReservationHeld();
    }

    public static boolean completeBackfill(String outcome) {
        boolean keep=CpuGovernor.completeBackfill(outcome==null?"completed":outcome);
        if(!keep)PlayerCenter.releaseBackfillExclusivePause();
        return keep;
    }

    public static void releaseBackfill(String outcome) {
        CpuGovernor.releaseBackfill(outcome==null?"released":outcome,true);
        PlayerCenter.releaseBackfillExclusivePause();
    }

    public static String status() {
        return CpuGovernor.sharedBudgetStatus()+"; followPause="+PlayerCenter.backfillExclusivePauseActive();
    }

    private static String activeTaskMode() {
        try {
            if(!ChunksmithProvider.isLoaded()||ChunksmithProvider.get()==null
                ||ChunksmithProvider.get().getGenerationTasks().isEmpty())return "none";
            for(GenerationTask task:ChunksmithProvider.get().getGenerationTasks().values()) {
                if(!TaskOwnership.isOwned(task))return "external";
            }
            return "follow";
        } catch(Throwable error) {
            return "external";
        }
    }
}
