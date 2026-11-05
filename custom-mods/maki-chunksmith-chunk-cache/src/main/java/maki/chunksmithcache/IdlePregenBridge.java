package maki.chunksmithcache;

import maki.idlepregen.IdlePregenApi;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class IdlePregenBridge {
   private static final Logger LOG=LoggerFactory.getLogger("MakiChunkCache");
   private static final String OWNER="maki-chunksmith-chunk-cache";
   private static volatile boolean claimed;
   private static volatile boolean warned;

   private IdlePregenBridge(){}

   static void setBackfillActive(boolean active) {
      try {
         if(!ModList.get().isLoaded("maki_idle_pregen")) {
            claimed=false;
            return;
         }
         IdlePregenApi.setExternalIdleWorkActive(OWNER,active);
         claimed=active;
      } catch(LinkageError|RuntimeException e) {
         claimed=false;
         if(!warned) {
            warned=true;
            LOG.warn("Could not coordinate chunk-cache backfill with maki-idle-pregen; guarded backfill continues without claiming simulation freeze",e);
         }
      }
   }

   static boolean isClaimed(){return claimed;}
}
