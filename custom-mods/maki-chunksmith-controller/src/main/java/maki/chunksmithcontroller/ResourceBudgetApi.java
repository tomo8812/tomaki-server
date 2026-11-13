package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;

/**
 * Coordination boundary between follow-owned Chunksmith dispatch and the
 * downstream pregen pipeline. No method here adopts or mutates an external task.
 */
public final class ResourceBudgetApi {
    private ResourceBudgetApi() {}

    /** E-lite owns all generation admission. Cache-side safety remains independent. */
    public static boolean blockFollowAdmission(GenerationTask task) {
        return !PlayerCenter.admissionOpen(task) || PregenFlowController.admissionBlocked();
    }

    /**
     * Current server completion events:
     * Chunksmith supplies LOD outcomes; chunk-cache supplies FULL outcomes
     * and its own validity decisions. Neither delegate controls E-lite's window.
     */

    /** Read-only token lookup for a FULL result belonging to this native attempt. */
    public static String attemptToken(String dimension,int x,int z,GenerationTask owner) {
        return PregenFlowController.token(dimension,x,z,owner);
    }

    public static void fullPending(String token){PregenFlowController.fullPending(token);}
    public static void fullSettled(String token){PregenFlowController.fullSettled(token);}
    public static void fullSaveDeferred(String token){PregenFlowController.fullSaveDeferred(token);}
    public static void fullSaveResolved(String token){PregenFlowController.fullSaveResolved(token);}
    public static boolean reserveFullRetry(String token){return PregenFlowController.reserveFullRetry(token);}
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
    public static void fullPublished(String token){PregenFlowController.fullPublished(token);}
    public static void fullReused(String token){PregenFlowController.fullReused(token);}
    public static boolean fullCaptureExpected(String token){return PregenFlowController.fullCaptureExpected(token);}

}
