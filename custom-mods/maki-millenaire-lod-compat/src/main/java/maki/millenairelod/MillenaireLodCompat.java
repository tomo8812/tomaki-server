package maki.millenairelod;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_millenaire_lod")
public final class MillenaireLodCompat {
    public MillenaireLodCompat() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(
            (net.neoforged.neoforge.event.server.ServerStartedEvent event)->{
                for(var level:event.getServer().getAllLevels())DeferredData.get(level);
            });
        LoggerFactory.getLogger("MakiMillenaireLOD").info(
            "Maki Millenaire Chunksmith Compatibility initialized; deferred village generation during Chunksmith pregen is enabled.");
    }
}
