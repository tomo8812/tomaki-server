package maki.shipedge;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_ship_edge")
public final class MakiShipEdge {
    private static final Logger LOG = LoggerFactory.getLogger("MakiShipEdge");
    public MakiShipEdge() {
        LOG.info("Maki Ship Edge initialized; one-tick Valkyrien Skies deck contact correction is enabled.");
    }
}
