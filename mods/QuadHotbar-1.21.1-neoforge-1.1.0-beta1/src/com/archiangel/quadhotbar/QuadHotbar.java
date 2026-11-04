package com.archiangel.quadhotbar;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;

@Mod("quadhotbar")
public class QuadHotbar {
   public static final String MODID = "quadhotbar";

   public QuadHotbar(IEventBus modEventBus, ModContainer modContainer) {
      QuadHotbarOverviewMenu.MENUS.register(modEventBus);
      modEventBus.addListener(QuadHotbarNetwork::registerPayloads);
      modEventBus.addListener(QuadHotbarConfig::onLoad);
      modContainer.registerConfig(Type.CLIENT, QuadHotbarConfig.SPEC);
      modContainer.registerConfig(Type.SERVER, QuadHotbarServerConfig.SPEC);
   }
}
