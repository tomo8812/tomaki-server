package com.kishku7.chunksmith.lod.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = "chunksmith", dist = Dist.CLIENT)
public class LodClientInit {
   public LodClientInit(ModContainer mod, IEventBus bus, Dist dist) {
      ClientPlatform.bootstrap(bus);
      CsLodClientBoot.init();
   }
}
