package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;

public final class ResourceBudgetApi {
   private ResourceBudgetApi() {
   }

   public static boolean blockFollowAdmission(GenerationTask task) {
      return !PlayerCenter.admissionOpen(task) || MakiChunksmithController.activeWork() >= CpuGovernor.cap() || PregenFlowController.admissionBlocked();
   }

   public static String attemptToken(String dimension, int x, int z, GenerationTask owner) {
      return PregenFlowController.token(dimension, x, z, owner);
   }

   public static void captureFailed(String token) {
      PregenFlowController.logicalCaptureFailed(token);
   }

   public static void stageUnavailable(String token, String stage) {
      PregenFlowController.stageUnavailable(token, PregenFlowController.Stage.valueOf(stage));
   }

   public static void stageStarted(String token, String stage) {
      PregenFlowController.physicalStart(token, PregenFlowController.Stage.valueOf(stage));
   }

   public static void stageFinished(String token, String stage, long serviceNanos, boolean success) {
      PregenFlowController.physicalTerminal(token, PregenFlowController.Stage.valueOf(stage), serviceNanos, success);
   }
}
