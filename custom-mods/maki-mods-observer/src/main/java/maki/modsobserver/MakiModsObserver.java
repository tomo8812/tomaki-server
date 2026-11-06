package maki.modsobserver;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MakiModsObserver.MOD_ID)
public final class MakiModsObserver {
    public static final String MOD_ID = "maki_mods_observer";
    private static final Logger LOG = LoggerFactory.getLogger("MakiModsObserver");

    public MakiModsObserver() {
        LOG.info("Maki Mods Observer initialized; detailed watchdog and NBT diagnostic file output is disabled by default. Enable it explicitly in config/maki-mods-observer.properties when troubleshooting.");
    }
}
