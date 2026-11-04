package net.satisfy.farm_and_charm.neoforge.client;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.farm_and_charm.client.FarmAndCharmClient;
import net.satisfy.farm_and_charm.client.gui.CookingPotGui;
import net.satisfy.farm_and_charm.client.gui.RoasterGui;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.foundation.neoforge.client.FoundationArmorExtensions;

@EventBusSubscriber(modid = "farm_and_charm", value = Dist.CLIENT, bus = Bus.MOD)
public class FarmAndCharmClientForge {
   @SubscribeEvent
   public static void beforeClientSetup(RegisterEvent event) {
      FarmAndCharmClient.preInitClient();
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      FarmAndCharmClient.onInitializeClient();
   }

   @SubscribeEvent
   public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
   }

   @SubscribeEvent
   public static void clientSetup(RegisterMenuScreensEvent event) {
      event.register((MenuType)ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), StoveGui::new);
      event.register((MenuType)ScreenhandlerTypeRegistry.COOKING_POT_SCREEN_HANDLER.get(), CookingPotGui::new);
      event.register((MenuType)ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), RoasterGui::new);
   }

   @SubscribeEvent
   public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(FoundationArmorExtensions.INSTANCE, new Item[]{(Item)ObjectRegistry.DUNGAREES.get()});
   }
}
