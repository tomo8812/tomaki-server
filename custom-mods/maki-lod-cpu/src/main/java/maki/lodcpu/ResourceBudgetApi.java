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

    /** Soft stage-WIP pressure waits before the iterator consumes another coordinate. */
    public static boolean blockFollowAdmission() {
        try {
            return Boolean.TRUE.equals(Class.forName("maki.chunksmithcache.CacheManager")
                .getMethod("admissionBlocked").invoke(null));
        } catch(ClassNotFoundException absent) {
            return false;
        } catch(ReflectiveOperationException|LinkageError|RuntimeException unavailable) {
            return true;
        }
    }

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
