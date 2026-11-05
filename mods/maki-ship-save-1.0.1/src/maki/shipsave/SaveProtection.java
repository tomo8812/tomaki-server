package maki.shipsave;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodHorizon.Existing;
import java.util.Map;
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

   public static void arm(GenerationTask var0, String var1, int var2, int var3, double var4, boolean var6, Existing var7) {
      try {
         VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(var2, var3);
      } catch (RuntimeException var9) {
         stopForLookupFailure(var0, var1, var2, var3, var9, false, true);
         return;
      } catch (LinkageError var10) {
         stopForLookupFailure(var0, var1, var2, var3, var10, true, true);
         return;
      }

      ACTIVE_TASKS.put(var1, var0);
      LodHorizon.arm(var1, var2, var3, var4, var6, var7);
   }

   public static boolean protectShipyard(String var0, int var1, int var2) {
      try {
         return VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(var1, var2);
      } catch (RuntimeException var4) {
         stopForLookupFailure(ACTIVE_TASKS.get(var0), var0, var1, var2, var4, false, false);
         return true;
      } catch (LinkageError var5) {
         stopForLookupFailure(ACTIVE_TASKS.get(var0), var0, var1, var2, var5, true, false);
         return true;
      }
   }

   public static void taskFinished(String var0, GenerationTask var1) {
      ACTIVE_TASKS.remove(var0, var1);
      REPORTED_FAILURES.remove(var1);
   }

   private static void stopForLookupFailure(GenerationTask var0, String var1, int var2, int var3, Throwable var4, boolean var5, boolean var6) {
      if (var0 != null) {
         var0.stop(false);
      }

      if (var5) {
         String var7 = var4.getMessage();
         if (var7 == null || var7.isBlank()) {
            var7 = var4.getClass().getSimpleName();
         }

         LodHorizon.setUnavailableReason("Valkyrien Skies shipyard lookup linkage failure: " + var7 + "; restart after fixing mod compatibility");
      }

      boolean var8 = var0 == null || REPORTED_FAILURES.putIfAbsent(var0, Boolean.TRUE) == null;
      if (var8) {
         if (var6) {
            LOG.error(
               "Valkyrien Skies shipyard lookup probe failed for {} at chunk {},{}; refusing to arm this Chunksmith LOD horizon and stopping the task. {}",
               new Object[]{
                  var1,
                  var2,
                  var3,
                  var5
                     ? "The horizon is marked unavailable until restart because this is a linkage failure."
                     : "A later task may retry after the transient failure clears.",
                  var4
               }
            );
         } else {
            LOG.error(
               "Valkyrien Skies shipyard lookup failed for {} at chunk {},{}; stopping the active Chunksmith LOD task. Only this ambiguous write is preserved; subsequent coordinates are checked independently. {}",
               new Object[]{
                  var1,
                  var2,
                  var3,
                  var5
                     ? "Further LOD horizon starts are disabled until restart because this is a linkage failure."
                     : "A later task may retry after the transient failure clears.",
                  var4
               }
            );
         }
      }
   }

   public static Existing scopedExisting(Existing var0, int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0) {
         long var5 = (long)var1 - var3;
         long var7 = (long)var1 + var3;
         long var9 = (long)var2 - var4;
         long var11 = (long)var2 + var4;
         LOG.info("Scoped LOD discard bounds: chunks [{},{}]..[{},{}]; shipyard writes always preserved", new Object[]{var5, var9, var7, var11});
         return (var9x, var10) -> var9x < var5 || var9x > var7 || var10 < var9 || var10 > var11 || var0 != null && var0.isGenerated(var9x, var10);
      } else {
         throw new IllegalArgumentException("Negative selection radius");
      }
   }
}
