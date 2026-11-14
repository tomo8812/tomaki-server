package maki.idlepregen;

import net.neoforged.fml.common.Mod;
import org.slf4j.LoggerFactory;

@Mod("maki_idle_pregen")
public final class MakiIdlePregen {
   public static final String MOD_ID = "maki_idle_pregen";

   public MakiIdlePregen() {
      LoggerFactory.getLogger("MakiIdlePregen")
         .info("Maki Idle Pregen initialized; idle-only generation, physics pause coordination and server-thread guards are enabled.");
   }
}
