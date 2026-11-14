package maki.shipsave;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodHorizon.Existing;
import com.kishku7.chunksmith.shape.Shape;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

public final class SaveProtection {
   private static final Logger LOG = LoggerFactory.getLogger("MakiShipSave");
   private static final Map<String, GenerationTask> ACTIVE_TASKS = new ConcurrentHashMap<>();
   private static final Map<GenerationTask, Boolean> REPORTED_FAILURES = new ConcurrentHashMap<>();

   private SaveProtection() {
   }

   public static void arm(GenerationTask task, String world, int centerChunkX, int centerChunkZ, double radius, boolean discard, Existing existing) {
      try {
         VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(centerChunkX, centerChunkZ);
      } catch (RuntimeException error) {
         stopForLookupFailure(task, world, centerChunkX, centerChunkZ, error, false, true);
         return;
      } catch (LinkageError error) {
         stopForLookupFailure(task, world, centerChunkX, centerChunkZ, error, true, true);
         return;
      }

      ACTIVE_TASKS.put(world, task);
      LodHorizon.arm(world, centerChunkX, centerChunkZ, radius, discard, existing);
   }

   public static boolean protectShipyard(String world, int chunkX, int chunkZ) {
      try {
         return VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(chunkX, chunkZ);
      } catch (RuntimeException error) {
         stopForLookupFailure(ACTIVE_TASKS.get(world), world, chunkX, chunkZ, error, false, false);
         return true;
      } catch (LinkageError error) {
         stopForLookupFailure(ACTIVE_TASKS.get(world), world, chunkX, chunkZ, error, true, false);
         return true;
      }
   }

   public static void taskFinished(String world, GenerationTask task) {
      ACTIVE_TASKS.remove(world, task);
      REPORTED_FAILURES.remove(task);
   }

   private static void stopForLookupFailure(GenerationTask task, String world, int chunkX, int chunkZ, Throwable error, boolean linkageFailure, boolean probe) {
      if (task != null) {
         task.stop(false);
      }

      if (linkageFailure) {
         String message = error.getMessage();
         if (message == null || message.isBlank()) {
            message = error.getClass().getSimpleName();
         }

         LodHorizon.setUnavailableReason("Valkyrien Skies shipyard lookup linkage failure: " + message + "; restart after fixing mod compatibility");
      }

      boolean report = task == null || REPORTED_FAILURES.putIfAbsent(task, Boolean.TRUE) == null;
      if (report) {
         if (probe) {
            LOG.error(
               "Valkyrien Skies shipyard lookup probe failed for {} at chunk {},{}; refusing to arm this Chunksmith LOD horizon and stopping the task. {}",
               new Object[]{
                  world,
                  chunkX,
                  chunkZ,
                  linkageFailure
                     ? "The horizon is marked unavailable until restart because this is a linkage failure."
                     : "A later task may retry after the transient failure clears.",
                  error
               }
            );
         } else {
            LOG.error(
               "Valkyrien Skies shipyard lookup failed for {} at chunk {},{}; stopping the active Chunksmith LOD task. Only this ambiguous write is preserved; subsequent coordinates are checked independently. {}",
               new Object[]{
                  world,
                  chunkX,
                  chunkZ,
                  linkageFailure
                     ? "Further LOD horizon starts are disabled until restart because this is a linkage failure."
                     : "A later task may retry after the transient failure clears.",
                  error
               }
            );
         }
      }
   }

   public static Existing scopedExisting(Existing existing, Shape shape) {
      Objects.requireNonNull(shape, "shape");
      LOG.info("Scoped LOD discard to Chunksmith task shape {}; shipyard writes always preserved", shape.name());
      return (chunkX, chunkZ) -> !shape.isBounding((chunkX << 4) + 8, (chunkZ << 4) + 8) || existing != null && existing.isGenerated(chunkX, chunkZ);
   }
}
