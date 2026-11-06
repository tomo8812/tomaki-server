package maki.chunksmithcache;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_chunksmith_chunk_cache")
public final class MakiChunksmithChunkCache {
   private static final Logger LOG = LoggerFactory.getLogger("MakiChunkCache");

   public MakiChunksmithChunkCache() {
      LOG.info("Maki Chunksmith Chunk Cache initialized; cold-cache capture, guarded backfill and compatibility quarantine are enabled.");
      NeoForge.EVENT_BUS.addListener(event -> LOG.info("Maki Chunksmith Chunk Cache stopping; cache generations and backfill cursor remain durable."));
      NeoForge.EVENT_BUS.addListener(event -> CacheCommands.register(event.getDispatcher()));
      NeoForge.EVENT_BUS.addListener(event -> CacheManager.maintenanceTick(event.getServer()));
   }
}
