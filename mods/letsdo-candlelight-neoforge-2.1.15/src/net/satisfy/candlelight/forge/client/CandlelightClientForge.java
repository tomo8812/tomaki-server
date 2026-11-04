package net.satisfy.candlelight.forge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.candlelight.client.CandlelightClient;
import net.satisfy.candlelight.client.gui.LetterGui;
import net.satisfy.candlelight.client.renderer.block.SideTableBookModels;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.candlelight.core.registry.ScreenHandlerTypeRegistry;
import net.satisfy.candlelight.forge.client.compat.CuriosClientCompat;
import net.satisfy.foundation.neoforge.client.FoundationArmorExtensions;

@EventBusSubscriber(modid = "candlelight", value = Dist.CLIENT, bus = Bus.MOD)
public class CandlelightClientForge {
   @SubscribeEvent
   public static void beforeClientSetup(RegisterEvent event) {
      CandlelightClient.preInitClient();
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      CandlelightClient.initClient();
      if (ModList.get().isLoaded("curios")) {
         event.enqueueWork(CuriosClientCompat::init);
      }
   }

   @SubscribeEvent
   public static void clientSetup(RegisterMenuScreensEvent event) {
      event.register((MenuType)ScreenHandlerTypeRegistry.LETTER_SCREEN_HANDLER.get(), LetterGui::new);
   }

   @SubscribeEvent
   public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(
         FoundationArmorExtensions.INSTANCE,
         new Item[]{
            (Item)ObjectRegistry.FLOWER_CROWN.get(),
            (Item)ObjectRegistry.COOKING_HAT.get(),
            (Item)ObjectRegistry.NECKTIE.get(),
            (Item)ObjectRegistry.CHEFS_JACKET.get(),
            (Item)ObjectRegistry.SHIRT.get(),
            (Item)ObjectRegistry.FORMAL_SHIRT.get(),
            (Item)ObjectRegistry.DRESS.get(),
            (Item)ObjectRegistry.CHEFS_PANTS.get(),
            (Item)ObjectRegistry.TROUSERS_AND_VEST.get(),
            (Item)ObjectRegistry.CHEFS_BOOTS.get()
         }
      );
   }

   @SubscribeEvent
   public static void registerAdditionalModels(RegisterAdditional event) {
      SideTableBookModels.IDS.forEach(id -> event.register(ModelResourceLocation.standalone(id)));
      SideTableBookModels.setLookup(id -> Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(id)));
   }
}
