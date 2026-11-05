package maki.shipsave;

import com.kishku7.chunksmith.lod.LodHorizon.Existing;
import com.kishku7.chunksmith.shape.Shape;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.mod.common.VS2ChunkAllocator;

public final class SaveProtection {
   private static final Logger LOG = LoggerFactory.getLogger("MakiShipSave");
   private static final AtomicBoolean failed = new AtomicBoolean();

   private SaveProtection() {
   }

   public static boolean protectShipyard(int var0, int var1) {
      if (failed.get()) {
         return true;
      }

      try {
         return VS2ChunkAllocator.INSTANCE.isChunkInShipyardCompanion(var0, var1);
      } catch (RuntimeException | LinkageError var3) {
         if (failed.compareAndSet(false, true)) {
            LOG.error("Shipyard lookup failed; preserving ALL horizon writes for this JVM. Restart after fixing the compatibility error.", var3);
         }

         return true;
      }
   }

   public static Existing scopedExisting(Existing var0, Shape var1) {
      Objects.requireNonNull(var1, "shape");
      LOG.info("Scoped LOD discard to Chunksmith task shape {}; shipyard writes always preserved", new Object[]{var1.name()});
      return (var2, var3) -> !var1.isBounding((var2 << 4) + 8, (var3 << 4) + 8) || var0 != null && var0.isGenerated(var2, var3);
   }
}
