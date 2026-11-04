package com.simonshiki.worldgenfeaturefix;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("worldgenfeaturefix")
public class WorldgenFeatureFix {
   public WorldgenFeatureFix(IEventBus eventBus) {
      Constants.LOG.info("Hello NeoForge!");
      CommonClass.init();
   }
}
