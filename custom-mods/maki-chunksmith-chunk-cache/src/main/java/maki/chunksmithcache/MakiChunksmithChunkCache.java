package maki.chunksmithcache;

import maki.statusio.StatusIo;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod("maki_chunksmith_chunk_cache")
public final class MakiChunksmithChunkCache {
    public MakiChunksmithChunkCache() {
        NeoForge.EVENT_BUS.addListener(this::stopping);
    }

    private void stopping(ServerStoppingEvent event) {
        StatusIo.flush(2_000L);
    }
}
