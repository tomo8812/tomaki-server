package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;

/**
 * Coordination boundary between follow-owned Chunksmith dispatch and the
 * downstream pregen pipeline. No method here adopts or mutates an external task.
 */
public final class ResourceBudgetApi {
    private ResourceBudgetApi() {}

    /** Controller owns only its Chunksmith task admission. */
    public static boolean blockFollowAdmission(GenerationTask task) {
        return !PlayerCenter.admissionOpen(task) || MakiChunksmithController.activeWork()>=CpuGovernor.cap() || PregenFlowController.admissionBlocked();
    }

    /**
     * Current server completion events:
     * Chunksmith supplies native FULL acquisition and LOD writer outcomes.
     */

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
}
