package net.satisfy.herbalbrews.neoforge;

import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.herbalbrews.HerbalBrews;
import net.satisfy.herbalbrews.core.registry.CompostableRegistry;
import net.satisfy.herbalbrews.neoforge.config.HerbalBrewsNeoForgeConfig;

@Mod("herbalbrews")
public class HerbalBrewsNeoForge {
   public HerbalBrewsNeoForge(IEventBus modEventBus, ModContainer modContainer) {
      EventBusesHooks.whenAvailable("herbalbrews", IEventBus::start);
      HerbalBrews.init();
      modContainer.registerConfig(Type.COMMON, HerbalBrewsNeoForgeConfig.COMMON_CONFIG);
      modEventBus.addListener(this::commonSetup);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(CompostableRegistry::registerCompostable);
   }
}
