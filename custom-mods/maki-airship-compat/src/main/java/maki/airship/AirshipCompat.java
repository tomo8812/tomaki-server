package maki.airship;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_airship_compat")
public final class AirshipCompat {
    private static final Logger LOG = LoggerFactory.getLogger("MakiAirshipCompat");

    public AirshipCompat() {
        LOG.info("Maki Airship Compatibility initialized; ship sleeping, passenger recovery, camera/render/light and lifecycle compatibility are enabled.");
    }
}
