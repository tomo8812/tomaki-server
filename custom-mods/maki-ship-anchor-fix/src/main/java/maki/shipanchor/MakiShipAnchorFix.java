package maki.shipanchor;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_ship_anchor_fix")
public final class MakiShipAnchorFix {
    public MakiShipAnchorFix() {
        LoggerFactory.getLogger("MakiShipAnchor").info("Maki Ship Anchor Fix initialized; ship passenger and sleeping physical anchor ownership is enabled.");
    }
}
