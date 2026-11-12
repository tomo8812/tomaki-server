package maki.millenairelod;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_millenaire_lod")
public final class MillenaireLodCompat {
    public MillenaireLodCompat() {
        LoggerFactory.getLogger("MakiMillenaireLOD").info(
            "Maki Millenaire Chunksmith Compatibility initialized; deferred village generation during Chunksmith pregen is enabled.");
    }
}
