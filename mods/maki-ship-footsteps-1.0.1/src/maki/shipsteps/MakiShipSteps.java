package maki.shipsteps;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_ship_steps")
public final class MakiShipSteps {
   public MakiShipSteps() {
      LoggerFactory.getLogger("MakiShipSteps").info("Maki Ship Footsteps initialized; deck-relative player movement drives step sounds.");
   }
}
