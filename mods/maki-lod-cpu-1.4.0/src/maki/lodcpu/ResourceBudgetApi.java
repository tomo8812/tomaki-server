package maki.lodcpu;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;

public final class ResourceBudgetApi {
   private ResourceBudgetApi() {
   }

   public static String reserveBackfill() {
      SharedWorkBudget.Decision decision = CpuGovernor.reserveBackfill(activeTaskMode());
      return decision.reserved() && decision.ready() ? "granted" : decision.reason();
   }

   public static boolean backfillReservationHeld() {
      return CpuGovernor.backfillReservationHeld();
   }

   public static boolean blockFollowDispatch() {
      return CpuGovernor.blockFollowDispatch();
   }

   public static void noteFollowInFlight(int count) {
      CpuGovernor.noteFollowInFlight(Math.max(0, count));
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
         if (ChunksmithProvider.isLoaded() && ChunksmithProvider.get() != null && !ChunksmithProvider.get().getGenerationTasks().isEmpty()) {
            for (GenerationTask task : ChunksmithProvider.get().getGenerationTasks().values()) {
               if (!TaskOwnership.isOwned(task)) {
                  return "external";
               }
            }

            return "follow";
         } else {
            return "none";
         }
      } catch (Throwable error) {
         return "external";
      }
   }
}
