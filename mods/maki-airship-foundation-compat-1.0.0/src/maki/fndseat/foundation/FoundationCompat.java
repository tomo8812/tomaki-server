package maki.fndseat.foundation;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_airship_foundation_compat")
public final class FoundationCompat {
   public FoundationCompat() {
      LoggerFactory.getLogger("MakiFoundationCompat").info("Maki Airship Foundation Compatibility initialized; ship-safe Foundation seating is enabled.");
   }
}
