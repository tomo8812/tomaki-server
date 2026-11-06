package maki.chunksmithcache;

import maki.statusio.StatusIo;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod("maki_chunksmith_chunk_cache")
public final class MakiChunksmithChunkCache {
    public MakiChunksmithChunkCache() {
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> StatusIo.flush(2_000L));
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> CacheCommands.register(event.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> CacheManager.maintenanceTick(event.getServer()));
    }
}
