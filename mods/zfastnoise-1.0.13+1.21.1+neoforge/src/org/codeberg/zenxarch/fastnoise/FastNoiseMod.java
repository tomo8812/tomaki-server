package org.codeberg.zenxarch.fastnoise;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("zfastnoise")
public class FastNoiseMod {
   public FastNoiseMod(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      FastNoiseConstants.LOGGER.info("Hello Fabric world!");
   }

   public static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("zfastnoise", path);
   }
}
