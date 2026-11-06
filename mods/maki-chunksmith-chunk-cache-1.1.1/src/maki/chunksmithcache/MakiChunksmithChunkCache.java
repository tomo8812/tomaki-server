package maki.chunksmithcache;

import maki.statusio.StatusIo;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("maki_chunksmith_chunk_cache")
public final class MakiChunksmithChunkCache {
   public MakiChunksmithChunkCache() {
      NeoForge.EVENT_BUS.addListener(event -> StatusIo.flush(2000L));
      NeoForge.EVENT_BUS.addListener(event -> CacheCommands.register(event.getDispatcher()));
      NeoForge.EVENT_BUS.addListener(event -> CacheManager.maintenanceTick(event.getServer()));
   }
}
