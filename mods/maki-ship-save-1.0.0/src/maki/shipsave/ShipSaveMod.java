package maki.shipsave;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_ship_save")
public final class ShipSaveMod {
   public ShipSaveMod() {
      LoggerFactory.getLogger("MakiShipSave").info("Ship save protection 1.0.0 loaded: shipyard persistence and task-scoped LOD discard");
   }
}
