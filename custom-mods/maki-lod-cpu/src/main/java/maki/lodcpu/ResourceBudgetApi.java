package maki.lodcpu;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;

/**
 * Coordination boundary between follow-owned Chunksmith dispatch and the
 * downstream pregen pipeline. No method here adopts or mutates an external task.
 */
public final class ResourceBudgetApi {
    private ResourceBudgetApi() {}

    public static String reserveBackfill() {
        CpuGovernor.refreshPipelinePressure();
        SharedWorkBudget.Decision decision=CpuGovernor.reserveBackfill(activeTaskMode());
        return decision.reserved()&&decision.ready()?"granted":decision.reason();
    }

    public static boolean backfillReservationHeld(){return CpuGovernor.backfillReservationHeld();}
    public static boolean blockFollowDispatch(){return CpuGovernor.blockFollowDispatch();}
    public static void noteFollowInFlight(int count){CpuGovernor.noteFollowInFlight(Math.max(0,count));}
    public static void backfillStarted(){CpuGovernor.backfillStarted();}
    public static void releaseBackfill(String outcome){CpuGovernor.releaseBackfill(outcome==null?"released":outcome);}
    public static String status(){return CpuGovernor.sharedBudgetStatus();}

    /** E-lite owns all generation admission. Cache-side safety remains independent. */
    public static boolean blockFollowAdmission(GenerationTask task) {
        return !PlayerCenter.admissionOpen(task) || PregenFlowController.admissionBlocked();
    }

    /**
     * E_LITE_PAIR_V1 producer contract:
     * Chunksmith supplies LOD outcomes; chunk-cache supplies FULL outcomes
     * and its own validity decisions. Neither delegate controls E-lite's window.
     */
    public static boolean connectPairTelemetry(String protocol) {
        return PregenFlowController.connectProofFeed(protocol);
    }
    /** Read-only token lookup for a FULL result belonging to this native attempt. */
    public static String attemptToken(String dimension,int x,int z,GenerationTask owner) {
        return PregenFlowController.token(dimension,x,z,owner);
    }

    public static void captureFailed(String token) {PregenFlowController.logicalCaptureFailed(token);}
    public static void stageUnavailable(String token,String stage) {
        PregenFlowController.stageUnavailable(token,PregenFlowController.Stage.valueOf(stage));
    }
    public static void stageStarted(String token,String stage) {
        PregenFlowController.physicalStart(token,PregenFlowController.Stage.valueOf(stage));
    }
    public static void stageFinished(String token,String stage,long serviceNanos,boolean success) {
        PregenFlowController.physicalTerminal(token,PregenFlowController.Stage.valueOf(stage),serviceNanos,success);
    }
    /** Cache-owned FULL successfully published or validated; no LOD decision here. */
    public static void fullReady(String token){PregenFlowController.fullReady(token);}
    public static void pairUncertain(String token,String reason){PregenFlowController.uncertain(token,reason);}

    public static long followAdmissionEpoch(GenerationTask task){return PlayerCenter.admissionEpoch(task);}
    public static boolean followAdmissionOpen(GenerationTask task){return PlayerCenter.admissionOpen(task);}

    private static String activeTaskMode() {
        try {
            if(!ChunksmithProvider.isLoaded()||ChunksmithProvider.get()==null
                ||ChunksmithProvider.get().getGenerationTasks().isEmpty())return "none";
            for(GenerationTask task:ChunksmithProvider.get().getGenerationTasks().values())
                if(!TaskOwnership.isOwned(task))return "external";
            return "follow";
        } catch(Throwable error) {
            return "external";
        }
    }
}
