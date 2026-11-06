package maki.chunksmithcache;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod("maki_chunksmith_chunk_cache")
public final class MakiChunksmithChunkCache {
    public MakiChunksmithChunkCache() {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> CacheCommands.register(event.getDispatcher()));
    }
}
