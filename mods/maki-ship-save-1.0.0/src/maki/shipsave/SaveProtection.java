package maki.shipsave;

import com.kishku7.chunksmith.lod.LodHorizon.Existing;
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
