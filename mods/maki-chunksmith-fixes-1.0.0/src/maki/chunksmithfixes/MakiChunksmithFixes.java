package maki.chunksmithfixes;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("maki_chunksmith_fixes")
public final class MakiChunksmithFixes {
   public MakiChunksmithFixes() {
      NeoForge.EVENT_BUS.addListener(e -> LodUpdateQueue.start(e.getServer()));
      NeoForge.EVENT_BUS.addListener(e -> LodUpdateQueue.tick(e.getServer()));
      NeoForge.EVENT_BUS.addListener(e -> LodUpdateQueue.stop());
   }
}
