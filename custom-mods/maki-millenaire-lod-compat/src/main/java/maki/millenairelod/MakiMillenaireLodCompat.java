package maki.millenairelod;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_millenaire_lod")
public final class MakiMillenaireLodCompat {
    public MakiMillenaireLodCompat() {
        LoggerFactory.getLogger("MakiMillenaireLOD").info("Maki Millenaire Chunksmith Compatibility initialized; deferred village generation, protection reservations and LOD refresh are enabled.");
    }
}
