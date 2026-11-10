package maki.chunksmithcache;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_chunksmith_chunk_cache")
public final class MakiChunksmithChunkCache {
    private static final Logger LOG = LoggerFactory.getLogger("MakiChunkCache");
    public MakiChunksmithChunkCache() {
        LOG.info("Maki Chunksmith Chunk Cache initialized; unified FULL+LOD capture, read-only LOD repair, safe handoff and compatibility quarantine are enabled.");
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> {
            CacheManager.stop(event.getServer());
            LOG.info("Chunk cache stopping: new work closed; incomplete captures remain retryable, in-use entries remain isolated.");
        });
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppedEvent event) -> {
            CacheManager.stopped(event.getServer());
        });
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> CacheCommands.register(event.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> CacheManager.maintenanceTick(event.getServer()));
    }
}
