package net.satisfy.brewery.forge.client;

import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.brewery.client.BreweryClient;
import net.satisfy.brewery.core.item.BreathalyzerItem;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.foundation.neoforge.client.FoundationArmorExtensions;

@EventBusSubscriber(modid = "brewery", value = Dist.CLIENT)
public class BreweryClientNeoForge {
   @SubscribeEvent
   public static void beforeClientSetup(RegisterEvent event) {
      BreweryClient.preInitClient();
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         BreathalyzerItem.init();
         BreweryClient.onInitializeClient();
      });
   }

   @SubscribeEvent
   public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(
         FoundationArmorExtensions.INSTANCE,
         new Item[]{
            (Item)ObjectRegistry.BREWFEST_HAT.get(),
            (Item)ObjectRegistry.BREWFEST_HAT_RED.get(),
            (Item)ObjectRegistry.BREWFEST_REGALIA.get(),
            (Item)ObjectRegistry.BREWFEST_BLOUSE.get(),
            (Item)ObjectRegistry.BREWFEST_TROUSERS.get(),
            (Item)ObjectRegistry.BREWFEST_DRESS.get(),
            (Item)ObjectRegistry.BREWFEST_BOOTS.get(),
            (Item)ObjectRegistry.BREWFEST_SHOES.get()
         }
      );
   }
}
