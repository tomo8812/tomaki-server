package fuzs.goldenagecombat.neoforge;

import fuzs.goldenagecombat.GoldenAgeCombat;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod("goldenagecombat")
public class GoldenAgeCombatNeoForge {
   public GoldenAgeCombatNeoForge() {
      ModConstructor.construct("goldenagecombat", GoldenAgeCombat::new);
   }
}
